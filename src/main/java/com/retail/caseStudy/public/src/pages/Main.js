import React, { useState, useEffect} from 'react';
import {getAllProducts, getProduct, addToCart} from "../utils/api";
import {Container, Row, Col} from 'react-bootstrap';
import { loading } from '../utils/helpers';

function Main() {
    const [products, setProducts] = useState(<></>);
    const [productsHTML, setProductsHTML] = useState(loading(150, 150));

    async function initialLoad() {
        const response = await getAllProducts();
        if (response.ok) {
            const productData = await response.json();
            setProducts(productData);
            setProductsHTML(productData.map(product => (
                <Col xs={5} className="loginDiv mx-2" key={product.id}>
                    <a href={"/product/" + product.id} className="no-link">
                        <div>
                            <img src={process.env.PUBLIC_URL + '/images/' + product.image} 
                                alt={product.name} className="productCardImage my-2" />
                        </div>
                        <div>
                            <h3 className="text-center m-0">{product.name}</h3>
                        </div>
                    </a>
                    <Row>
                        <Col xs={5}>
                            <div className="d-flex justify-content-start">
                                <p className="text-bolder fs-50 vermin m-0">${product.price}</p>
                            </div>
                            <div className="d-flex justify-content-start">
                                <p className="text-bold fs-4 mb-1">{product.quantity <= 0 ? "Out of Stock" : product.quantity + " Available"}</p>
                            </div>
                        </Col>
                        <Col xs={7}>
                            <form className="d-flex justify-content-end align-items-center" 
                                style={{ height: "100%" }} onSubmit={(e) => attemptAddToCart(e)}>
                                <div className="me-1" style={{width: "25%"}}>
                                    <input type="number" className="ps-1" min="1"
                                        max={product.quantity} defaultValue="1" required />
                                </div>
                                {product.quantity === 0 ? 
                                <button type="button" className="button fs-5 disabled"
                                    value={product.id} disabled={true}>Add to Cart</button> : 
                                <button type="submit" className="button fs-5"
                                    value={product.id}>Add to Cart</button>}

                            </form>
                        </Col>
                    </Row>
                </Col>
            )));

        } else alert("An unknown error has occurred, please try again later!")  
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
       initialLoad();
    }, []);

    return (
        <Container>
            <Row className="d-flex justify-content-center align-content-top mt-5">
                {productsHTML}
            </Row>
        </Container>
    );
}

export default Main;