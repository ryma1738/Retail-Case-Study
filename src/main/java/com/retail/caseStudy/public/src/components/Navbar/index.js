import React, {useState} from 'react';
import {Container, Row, Col} from 'react-bootstrap';
function Navigator() {

    return (
        <Container fluid className="nav-bar">
            <Row className="mx-2 d-flex justify-content-center align-content-center">
                
                <Col xs={7} className="">
                    <a href="/" className="no-link">
                    <h1 className="vermin fs-100 gradient-text-shadow m-0">CaseStudy. Store</h1>
                    </a>
                </Col>
                
                <Col xs={5}>

                </Col>
            </Row>
        </Container>
    )
}

export default Navigator;