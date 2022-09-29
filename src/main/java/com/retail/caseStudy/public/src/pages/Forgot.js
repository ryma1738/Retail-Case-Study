import React, {useState, useEffect} from "react"
import { Col, Container, Row } from "react-bootstrap";
import { forgot, resetPassword } from "../utils/api"; 
import { createPhoneNumber } from '../utils/helpers';

function Forgot() {

    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [userId, setUserId] = useState(null);
    const [key, setKey] = useState(null);
    const [resetDisplay, setResetDisplay] = useState("d-none");
    const [confirmDisplay, setConfirmDisplay] = useState("");

    async function attemptConfirmation(e) {
        setErrorMessage("");
        e.preventDefault();
        console.log('/api/v1/user/forgot/' + email + '/' + phoneNumber)
        const response = await forgot(email, phoneNumber);
        console.log(response)
        if (response.ok) {
            const data = await response.json();
            setUserId(data.userId);
            setKey(data.key);
            setResetDisplay("");
            setConfirmDisplay("d-none");
        } else if (response.status === 400) {
            const err = await response.json();
            setErrorMessage(err.message);
        } else if (response.status === 500) {
            alert("An Unknown Error has occurred, please try again.")
        }
    }

    async function attemptResetPassword(e) {
        setErrorMessage("");
        e.preventDefault();
        if (password !== confirmPassword) {
            return setErrorMessage("Passwords do not match");
        } 
        const response = await resetPassword(userId, key, password);
        if (response.ok) {
            alert("Your password has been reset.");
            window.location = "/login";
        } else if (response.status === 400) {
            const err = await response.json();
            setErrorMessage(err.message);
        } else if (response.status === 500) {
            alert("An Unknown Error has occurred, please try again.")
        }
    }

    useEffect(() => {
       setResetDisplay("d-none");
       setConfirmDisplay("");
    }, []);
    return (
        <Container fluid="xxl">
            <Row className="d-flex justify-content-center align-items-center mt-5">
                <Col xl={4} md={6} sm={10} xs={11} className="loginDiv">
                    <h2 className="text-center mt-2 vermin fs-1">Password Recovery</h2>
                    <form className={confirmDisplay} onSubmit={(e) => attemptConfirmation(e)}>
                        <div className="my-3">
                            <label htmlFor="phoneNumber" >Phone Number:</label>
                            <input type="tel" id="phoneNumber" className="text-center" value={phoneNumber} placeholder="888-888-8888"
                                minLength={12} maxLength={12} onChange={(e) => setPhoneNumber(createPhoneNumber(e.target.value, phoneNumber))}></input>
                        </div>
                        <div className="my-3">
                            <label htmlFor="email" >Email:</label>
                            <input type="email" id="email" className="text-center" minLength={10} maxLength={40}
                                required placeholder="yourEmail@email.com" onChange={(e) => setEmail(e.target.value)}></input>
                        </div>

                        <p className="text-center text-danger">{errorMessage}</p>
                        <div className="d-flex justify-content-center align-items-center my-3">
                            <button type="submit" className="button">Submit</button>
                        </div>
                    </form>
                    <form className={resetDisplay} onSubmit={(e) => attemptResetPassword(e)}>
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
                        <p className="text-center text-danger">{errorMessage}</p>
                        <div className="d-flex justify-content-center align-items-center my-3">
                            <button type="submit" className="button">Submit</button>
                        </div>
                    </form >
                </Col>
            </Row>
            <Row className="mt-5 d-flex justify-content-center align-items-center" style={{ borderBottom: "5px solid var(--cyan)" }}>
                <p className="text-center">Remember you Password? <a href="/login" className="text-link">Login</a></p>
            </Row>
        </Container>
    );
}

export default Forgot;