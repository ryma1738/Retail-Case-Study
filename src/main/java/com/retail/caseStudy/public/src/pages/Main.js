import React, { useState, useEffect} from 'react';
import {getAllProducts} from "../utils/api";
import {Container, Row, Col} from 'react-bootstrap';

function Main() {
    const [products, setProducts] = useState();

    async function initialLoad() {
        const response = await getAllProducts();
        if (response.ok) {
            const productData = await response.json();
            setProducts(productData);
            console.log(productData);

        } else alert("An unknown error has occurred, please try again later!")  
    }

    useEffect(() => {
       initialLoad();
    }, []);

    return (
        <Container>
            <Row className="d-flex justify-content-center align-content-top mt-5">

            </Row>
        </Container>
    );
}

export default Main;