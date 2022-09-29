import React, { useState, useEffect } from "react"
import { Col, Container, Row } from "react-bootstrap";
import { forgot, resetPassword } from "../utils/api";
import { createPhoneNumber, convertPhoneNumber } from '../utils/helpers';

function Forgot() {

    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [userId, setUserId] = useState(null);
    const [key, setKey] = useState(null);
    const [confirmDisplay, setConfirmDisplay] = useState("");
    const [resetDisplay, setResetDisplay] = useState("d-none");

    function determineRequest(e) {
        e.preventDefault();
        if (key === null) attemptConfirmation();
        else attemptResetPassword();
    }

    async function attemptConfirmation() {
        setErrorMessage("");
        const phoneNum = convertPhoneNumber(phoneNumber);
        const response = await forgot(email, phoneNumber);
        if (response.ok) {
            const data = await response.json();
            setUserId(data.userId);
            setKey(data.key);


        } else if (response.status === 400) {
            const err = await response.json();
            console.log('/api/v1/user/forgot/' + email + '/' + phoneNumber)
            setErrorMessage(err.message);
        } else if (response.status === 500) {
            alert("An Unknown Error has occurred, please try again.")
        }
    }

    async function attemptResetPassword() {
        setErrorMessage("");
        if (password !== confirmPassword) {
            return setErrorMessage("Passwords do not match");
        }
        const response = await resetPassword(userId, key, password);
        if (response.ok) {
            alert("Your password has been reset.");
            setKey(null);
            setUserId(null);
            window.location = "/login";
        } else if (response.status === 400) {
            const err = await response.json();
            setErrorMessage(err.message);
        } else if (response.status === 500) {
            alert("An Unknown Error has occurred, please try again.")
        }
    }

    useEffect(() => {
        setConfirmDisplay("");
        setResetDisplay("d-none");

    }, []);
    return (
        <Container fluid="xxl">
            <Row className="d-flex justify-content-center align-items-center mt-5">
                <Col xl={4} md={6} sm={10} xs={11} className="loginDiv">
                    <h2 className="text-center mt-2 vermin fs-1">Password Recovery</h2>
                    <form onSubmit={(e) => determineRequest(e)}>
                        <div className={confirmDisplay}>
                            <div className="my-3">
                                <label htmlFor="phoneNumber" >Phone Number:</label>
                                <input type="tel" id="phoneNumber" className="text-center" value={phoneNumber} placeholder="8887776666"
                                    minLength={10} maxLength={10} onChange={(e) => setPhoneNumber(e.target.value)}></input>
                            </div>
                            <div className="my-3">
                                <label htmlFor="email" >Email:</label>
                                <input type="email" id="email" className="text-center" minLength={10} maxLength={40}
                                    required placeholder="yourEmail@email.com" onChange={(e) => setEmail(e.target.value)}></input>
                            </div>
                        </div>
                        <div className={resetDisplay}>
                            <div className="my-3">
                                <label htmlFor="password" >New Password:</label>
                                <input type="password" id="password" className="text-center" minLength={6} maxLength={25}
                                    required placeholder="Password" onChange={(e) => setPassword(e.target.value)}></input>
                            </div>
                            <div className="my-3">
                                <label htmlFor="passwordConfirm" >Confirm New Password:</label>
                                <input type="password" id="passwordConfirm" className="text-center" minLength={6} maxLength={25}
                                    required placeholder="Confirm Password" onChange={(e) => setConfirmPassword(e.target.value)}></input>
                            </div>
                        </div >
                        <p className="text-center text-danger">{errorMessage}</p>
                        <div className="d-flex justify-content-center align-items-center my-3">
                            <button type="submit" className="button">Submit</button>
                        </div>
                    </form>
                </Col>
            </Row>
            <Row className="mt-5 d-flex justify-content-center align-items-center" style={{ borderBottom: "5px solid var(--cyan)" }}>
                <p className="text-center">Remember you Password? <a href="/login" className="text-link">Login</a></p>
            </Row>
        </Container>
    );
}

export default Forgot;