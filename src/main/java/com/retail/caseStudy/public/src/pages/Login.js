import React, { useState, useEffect, } from 'react';
import { Col, Container, Row } from "react-bootstrap";
import {login} from "../utils/api";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    async function attemptLogin(e) {
        e.preventDefault();
        setErrorMessage("");
        const response = await login(email, password);
        console.log(response);
        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwtCaseStudy', data.jwtToken);
            window.location = "/";
        } else if (response.status === 400) {
            const error = await response.json();
            console.log(error);
            setErrorMessage(error.message);
        } else if (response.status === 403) {
            const banReason = await response.json();
            setErrorMessage(banReason.message + " Ban Reason: " + banReason.banReason);
            window.location = "/"
        } else {
            alert("An unknown error has occurred, please try again later!")
        }
    }
 

    useEffect(() => {
        
    }, []);
    return (
        <Container fluid>
            <Row className="d-flex justify-content-center align-items-center mt-5">
                <Col lg={4} md={6} xs={12} className="loginDiv">
                    <h2 className="text-center mt-2 vermin fs-1" >Login</h2>
                    <form onSubmit={(e) => attemptLogin(e)}>
                        <div className="my-3">
                            <label htmlFor="email" >Email:</label>
                            <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)}
                                className="text-center" maxLength={40} required placeholder="yourEmail@email.com"></input>
                        </div>
                        <div className="my-3">
                            <label htmlFor="password" >Password:</label>
                            <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)}
                                className="text-center" maxLength={25} required placeholder="Password"></input>
                        </div>
                        <p className="text-danger text-center">{errorMessage}</p>
                        <div className="d-flex justify-content-center align-items-center my-3">
                            <button type="submit" className="button">Submit</button>
                        </div>
                    </form>
                </Col>
            </Row>
            <Row className="mt-5 d-flex justify-content-center align-items-center" style={{ borderBottom: "5px solid var(--cyan)" }}>
                <p className="text-center">Forgot your Password? <a href="/forgot" className="text-link">Recover Password</a></p>
            </Row>
            <Row className="d-flex justify-content-center align-items-center" style={{ borderBottom: "5px solid var(--cyan)" }}>
                <p className="text-center">Don't have an account? <a href="/signup" className="text-link">Sign Up</a></p>
            </Row>
        </Container>
    );
}

export default Login;