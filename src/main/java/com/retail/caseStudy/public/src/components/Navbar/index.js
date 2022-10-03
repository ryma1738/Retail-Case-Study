import React, {useState, useEffect} from 'react';
import {Container, Row, Col} from 'react-bootstrap';
function Navigator() {

    const [jwt, setJwt] = useState(null);
    const [navbar, setNavbar] = useState(<></>);

    function getJWT() {
        setJwt(localStorage.getItem("jwtCaseStudy"))
        console.log(localStorage.getItem("jwtCaseStudy") === null);
        console.log(localStorage.getItem("jwtCaseStudy"))
        if (localStorage.getItem("jwtCaseStudy") === null) 
            setNavbar((<p className="my-0"><a href="/login" className="text-decoration-none text-link">Login or Signup</a></p>));
        else setNavbar((
        <div className="d-flex">
            <p className="my-0"><a href="/cart" className="text-link text-decoration-none">Cart</a></p>
            <p className="my-0 mx-3 gradient-text fw-bolder">|</p>
            <p className="my-0 text-link" onClick={(e) => logout(e)}>Logout</p>
        </div>));
    }

    function logout(e) {
        e.preventDefault();
        localStorage.removeItem("jwtCaseStudy");
        getJWT();
        window.location = "/";
    }

    useEffect(() => {
        getJWT();
    } ,[])

    return (
        <Container fluid className="nav-bar">
            <Row className="mx-2 d-flex justify-content-center align-content-center">
                
                <Col xs={7} className="">
                    <a href="/" className="no-link">
                    <h1 className="vermin fs-100 gradient-text-shadow-button m-0">CaseStudy. Store</h1>
                    </a>
                </Col>
                
                <Col xs={5} className="d-flex justify-content-end align-content-center my-auto navbarText">
                    {navbar}
                </Col>
            </Row>
        </Container>
    )
}

export default Navigator;