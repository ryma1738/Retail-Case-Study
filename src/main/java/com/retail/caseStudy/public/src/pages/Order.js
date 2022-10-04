import React, { useState, useEffect } from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { getOrder, updateOrder } from "../utils/api";
import { loading, formatDate } from "../utils/helpers"
import { useParams } from 'react-router-dom';

function Order() {
    const params = useParams();
    const [orderInfo, setOrderInfo] = useState(null);
    const [orderHTML, setOrderHTML] = useState(loading(100, 100));

    async function cancelOrder(e, orderId, orderStatus) {
        e.preventDefault();
        if (window.confirm('Are you sure you want to cancel this order?')) {
            const response = await updateOrder(orderId, orderStatus, localStorage.getItem("jwtCaseStudy"));
            if (response.ok) {
                alert("Your order has been canceled.");
                initialLoad();
            } else if (response.status === 400) {
                const errorMessage = await response.json()
                alert(errorMessage.message);
            } else if (response.status === 401) {
                localStorage.removeItem("jwtCaseStudy");
                window.location = "/login";
            } else alert("An unknown error has occurred, please try again later!");
        } else alert("Order was not canceled.");
        
    }

    async function initialLoad() {
        const response = await getOrder(params.orderId, localStorage.getItem("jwtCaseStudy"));
        if (response.ok) {
            const data = await response.json();
            setOrderInfo(data);
            setOrderHTML((
                <Container fluid>
                    <Row className="pt-2 pb-2 d-flex justify-content-center gradient-text-underline">
                        <h2 className="vermin gradient-text-shadow fs-50">Order Items:</h2>
                        {data.products.map(productInfo => (
                            <Col xs={4} key={productInfo.product.id} className="mb-2">
                                <div className="loginDiv me-2 p-2">
                                    <a href={"/product/" + productInfo.product.id} className="no-link">
                                        <div>
                                            <img src={process.env.PUBLIC_URL + '/images/' + productInfo.product.image}
                                                alt={productInfo.product.name} className="productCardImage my-2" />
                                        </div>
                                        <div>
                                            <h3 className="text-center m-0">{productInfo.product.name}</h3>
                                        </div>
                                        <div className="d-flex justify-content-start">
                                            <p className="fs-2 m-0 text-center">Quantity: {productInfo.quantity}</p>
                                            <p className="text-bolder fs-2 mb-0 ms-auto vermin">${productInfo.product.price}</p>
                                        </div>
                                    </a>
                                </div>
                            </Col>
                        ))}
                    </Row>
                    <Row>
                        <Col xs={6}>
                            <div className="m-3 p-3 loginDiv">
                                <p className="fs-2 m-0">Order ID: {data.id}</p>
                                <p className="fs-2 m-0">Order Total: <span className="vermin fs-3" >${data.total}</span></p>
                                <p className="fs-2 m-0">Order Status: {data.status}</p>
                                <p className="fs-2 m-0">Order Created: {formatDate(data.createdAt)}</p>
                            </div>
                        </Col>
                        {data.status === 'ORDER_PLACED' ? 
                        <Col xs={6}>
                            <div className="m-3 p-3 loginDiv">
                                <div style={{ width: "100%" }}>
                                    <p className='vermin fs-2 text-center'>Would you like to Cancel your Order?</p>
                                </div>
                                <div>
                                    <button type="button" className="button mx-auto" style={{ width: "100%" }}
                                        onClick={(e) => cancelOrder(e, data.id, "CANCELED")} >Cancel Order</button>
                                </div>
                            </div>
                        </Col> :
                        <Col xs={6}>
                            <div className="m-3 p-3 loginDiv">
                                <div style={{ width: "100%" }}>
                                    <p className='vermin fs-2 text-center'>If you have any further questions, contact us at: 801-666-9999</p>
                                </div>
                            </div>
                        </Col>}
                    </Row>
                </Container>
            ));
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
        <>
            {orderHTML}
        </>
        
    );
}

export default Order;