import React, { useState, useEffect} from 'react';
import { Container, Row, Col} from 'react-bootstrap';
import {getOrders} from "../utils/api";
import {loading, formatDate} from "../utils/helpers"

function Orders() {
    const [orders, setOrders] = useState(null);
    const [ordersHTML, setOrdersHTML] = useState(loading(100, 100));

    function viewIndividualOrder(e, orderId) {
        window.location ="/order/" + orderId;
    }

    async function initialLoad(){
        const response = await getOrders(localStorage.getItem("jwtCaseStudy"));
        if (response.ok) {
            const data = await response.json();
            if(data.length === 0) {
                setOrdersHTML(
                <Col xs={12}>
                    <div className="loginDiv mt-3 d-flex justify-content-center align-items-center">
                        <p className="vermin gradient-text fs-50 m-0">You have no Orders Yet... Go buy stuff!</p>
                    </div>
                </Col>
                );
                return;
            }
            setOrders(data);
            setOrdersHTML(data.map(order => (
                <Col xs={12} key={order.id}>
                    <div className="loginDiv mt-3 d-flex justify-content-start align-items-center">
                        <div className="d-flex justify-content-start align-items-center" style={{ objectFit: "contain" }}>
                            <p className="fs-4">Products: </p>
                            {order.products.map(product => (
                                <a href={"/product/" + product.product.id} key={product.product.image} >
                                    <img src={process.env.PUBLIC_URL + '/images/' + product.product.image} alt={product.product.name}
                                        className="ordersCardImage my-2 p-0 mx-2" />
                                </a>))}
                        </div>
                        <div className="ms-auto  px-2 ordersImagesBorder" style={{minWidth: "27vw"}}>
                            <p className="fs-4 m-0">Order ID: {order.id}</p>
                            <p className="fs-4 m-0">Order Total: <span className="vermin fs-3" >${order.total}</span></p>
                            <p className="fs-4 m-0">Order Status: {order.status}</p>
                            <p className="fs-4 m-0">Order Created: {formatDate(order.createdAt)}</p>
                            <button type="button" className="button" onClick={(e) => viewIndividualOrder(e, order.id)}
                                style={{width: '100%'}}>View Order</button>
                        </div>
                    </div>
                </Col>
            )));
        } else if (response.status === 401) {
            localStorage.removeItem("jwtCaseStudy");
            window.location = "/login";
        } else alert("An unknown error has occurred, please try again later!");
    }

    useEffect(() => {
        if (localStorage.getItem("jwtCaseStudy") === null) {
            window.location = "/login";
            return;
        }
        initialLoad();
    }, []);

    return (
        <Container fluid>
            <Row className="d-flex">
                <div className="d-flex">
                    <p className="fs-50 vermin m-0 gradient-text-shadow">Your Orders:</p>
                </div>
                
            </Row>
            <Row>
                {ordersHTML}
            </Row>
        </Container>
    );
}

export default Orders;