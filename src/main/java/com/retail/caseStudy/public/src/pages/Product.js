import React, { useState, useEffect} from 'react';
import { Container, Row, Col, Spinner} from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import {getProduct, addToCart} from '../utils/api';
import {loading} from '../utils/helpers';


function Product() {
    const params = useParams();
    const [product, setProduct] = useState(null);
    const [productHTML, setProductHTML] = useState(loading(100, 100));

    async function initialLoading() {
        if (!params.productId) {
            alert("Unable to get product information. Returning to home page.");
            //window.location.replace("/");
            return;
        }
        const response = await getProduct(params.productId);
        if (response.ok) {
            const productInfo = await response.json();
            setProduct(productInfo);
            setProductHTML((
                <Container fluid className="p-0">
                    <Row className="backgroundGradient m-0">
                        <Col xs={12} className="d-flex justify-content-center my-2" key={productInfo.id}>
                            <div>
                                <img src={process.env.PUBLIC_URL + '/images/' + productInfo.image}
                                    alt={productInfo.name} className="productImage my-2" />
                            </div>
                        </Col>
                    </Row>
                    <Row className="px-5 my-3" style={{width: "100%"}}>
                        <Col className="p-0 m-0 d-flex">
                        <div className="gradient-text-underline">
                            <p className="gradient-text vermin m-0 fs-100">{productInfo.name}</p>
                        </div>
                        
                        </Col>
                    </Row>
                    <Row className="px-5 my-3" style={{ width: "100%" }} >
                        <Col xs={12} className="d-flex justify-content-center loginDiv">
                            <p className="fs-3">{productInfo.description}</p>
                        </Col>
                    </Row>
                    <Row className="myt-3 px-5" style={{ width: "100%" }}>
                        <Col xs={8} className="loginDiv me-auto">
                            <div className="d-flex justify-content-start align-items-center">
                                <p className="text-bolder fs-50 vermin m-0">${productInfo.price}</p>
                                <p className="text-bold fs-4 mb-1 ms-auto">{productInfo.quantity <= 0 ? "Out of Stock" : 
                                    productInfo.quantity + " Available"}</p>
                            </div>
                        </Col>
                        <Col xs={3} className="loginDiv d-flex justify-content-center ms-auto p-0">
                            <form className="d-flex justify-content-center align-items-center"
                                style={{ height: "100%", width: "100%" }} onSubmit={(e) => attemptAddToCart(e)}>
                                <div className="me-1" style={{ width: "25%" }}>
                                    <input type="number" className="ps-1" min="1"
                                        max={productInfo.quantity} defaultValue="1" required />
                                </div>
                                {productInfo.quantity === 0 ?
                                    <button type="button" className="button fs-5 disabled"
                                        value={productInfo.id} disabled={true}>Add to Cart</button> :
                                    <button type="submit" className="button fs-5"
                                        value={productInfo.id}>Add to Cart</button>}
                            </form>
                        </Col>
                    </Row>
                </Container>

            ))
        } else if (response.status === 404) {
            alert("Unable to get product information. Returning to home page.");
            window.location.replace("/");
            return;
        }
    }

    async function attemptAddToCart(e) {
        e.preventDefault();
        if (localStorage.getItem("jwtCaseStudy") === null) {
            window.location = "/login";
            return;
        }
        const quantity = e.target[0].value;
        const productId = e.target[1].attributes[2].value;
        const response = await getProduct(productId);
        if (response.ok) {
            const product = await response.json();
            console.log(product);
            const response2 = await addToCart(quantity, product, localStorage.getItem("jwtCaseStudy"));
            console.log(response2.status)
            if (response2.ok) {
                alert("You now have " + quantity + " of these items in your cart.");
            } else if (response2.status === 400) {
                const errorMessage = await response2.json()
                alert(errorMessage.message)
            } else if (response2.status === 401) {
                localStorage.removeItem("jwtCaseStudy");
                window.location = "/login";
            } else alert("An unknown error has occurred, please try again later!");
        } else alert("An unknown error has occurred, please try again later!");
    }

    useEffect(() => {
        initialLoading();
    }, [])

    return (
        <>
            {productHTML}
        </>
    );
}

export default Product;