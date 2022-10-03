import React, {useState, useEffect} from "react";
import {Container, Row, Col} from "react-bootstrap";
import {getCart, getProduct, addToCart} from "../utils/api";

function Cart() {
    const [cart, setCart] = useState();
    const [productCards, setProductCards] = useState();
    const [subtotalHTML, setSubtotalHTML] = useState();
    const [disableOrder, setDisableOrder] = useState(false);

   async function initialLoad() {
        const response = await getCart(localStorage.getItem("jwtCaseStudy"));
        if (response.ok) {
            const data = await response.json();
            setCart(data);
            if(data.products.length == 0) {
                setProductCards((
                    <Col xs={12}>
                        <h1 className="text-center">Your cart is Empty</h1>
                    </Col>
                ));
                setSubtotalHTML((
                    <Row className="gradient-text-underline pb-1">
                        <Col xs={12}>
                            <h2 className="text-center">Your cart is Empty</h2>
                        </Col>
                    </Row>
                ));
                setDisableOrder(true);
            } else {
                setProductCards(data.products.map(productInfo => (
                    <Col xs={6} key={productInfo.product.id}>
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
                                    <p className="text-bolder fs-50 vermin m-0">${productInfo.product.price}</p>
                                </div>
                            </a>
                            <Row>
                                <form as="col" className="d-flex justify-content-center align-items-center mb-3"
                                    style={{ height: "100%" }} onSubmit={(e) => attemptUpdateCart(e)}>
                                    <div className="me-1" style={{ width: "25%" }}>
                                        <input type="number" className="ps-1" min="1"
                                            max={productInfo.product.quantity} defaultValue={productInfo.quantity} required />
                                    </div>
                                    <button type="submit" className="button fs-5"
                                        value={productInfo.product.id}>Update Quantity</button>
                                </form>
                            </Row>
                        </div>
                    </Col>
                )));
                setSubtotalHTML(data.products.map(productInfo => (
                    <Row className="gradient-text-underline pb-1">
                        <Col xs={8} className="mt-2">
                            <p className="m-0">{productInfo.product.name} x {productInfo.quantity}: </p>
                        </Col>
                        <Col xs={4} className="d-flex align-items-center">
                            <p className="vermin fs-4 m-0"> ${Math.round((productInfo.product.price * productInfo.quantity) * 100) / 100}
                            </p>
                        </Col>
                    </Row>
                )));
            }
            
        } else console.log(response)
    }

    async function attemptUpdateCart(e) {
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
            if (product.quantity < quantity) {
                const updateCartQuantityResponse = await addToCart(product.quantity, product, localStorage.getItem("jwtCaseStudy"));
                if (updateCartQuantityResponse.ok) {
                    if (product.quantity === 0) alert("An item in your cart is out of stock and has been removed.");
                    else alert("An item in your cart has a limited supply left and the quantity available for purchase has changed.");
                } else alert("An unknown error has occurred, please try again later!");
                window.location.reload();
                return;
            }
            const response2 = await addToCart(quantity, product, localStorage.getItem("jwtCaseStudy"));
            if (response2.ok) {
                alert("You now have " + quantity + " of these items in your cart.");
                initialLoad();
            } else if (response2.status === 400) {
                const errorMessage = await response2.json()
                alert(errorMessage.message)
            } else if (response2.status === 401) {
                localStorage.removeItem("jwtCaseStudy");
                window.location = "/login";
            } else alert("An unknown error has occurred, please try again later!");
        } else alert("An unknown error has occurred, please try again later!");
    }

    function attemptPlaceOrder(e) {
        e.preventDefault();
    }


    useEffect(() => {
        if(localStorage.getItem("jwtCaseStudy") === null) {
            window.location = "/login";
            return;
        }
        initialLoad();
    }, []);

    return (
        <Container style={{height: "80vh"}}>
            <Row>
                <Col xs={8} className="my-3">
                    <Row>
                        {productCards}
                    </Row>
                </Col>
                <Col xs={4} className="loginDiv my-3 d-flex justify-content-center" style={{ position: "relative", minHeight: "25vh" }}>
                    <Container fluid className="" >
                            {subtotalHTML}
                        <Row className="d-flex align-items-bottom p-2" style={{position: "absolute", bottom: "0", left: "0"}}>
                            <Col xs={12} className="">
                                <p className="fs-2 mb-0 mx-auto text-center">Subtotal: 
                                    <span className="vermin fs-1" > ${cart != null ? cart.subTotal : 0}</span>
                                </p>
                                <p className="fs-2 mb-0 mx-auto text-center">Total: <span className="vermin fs-1" > 
                                        ${cart != null ? Math.round((cart.subTotal + (cart.subTotal * .10)) * 100) / 100 : 0}
                                    </span>
                                </p>
                            </Col>
                            <Col xs={12} className="mt-3 mb-3" style={{ width: "100%" }}>
                                <button className="button" style={{ width: "100%" }} onClick={(e) => attemptPlaceOrder(e)}
                                    disabled={disableOrder}>Place Order</button>
                            </Col>
                        </Row>
                    </Container>
                </Col>
            </Row>
        </Container>
    );
}

export default Cart;