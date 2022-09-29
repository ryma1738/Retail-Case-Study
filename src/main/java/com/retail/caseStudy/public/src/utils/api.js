export const getAllProducts = () => {
    return fetch('/api/v1/product', { 
        method: 'GET',
        headers: {
            'content-type': 'application/json'
        }
    });
}

export const login = (email, password) => {
    return fetch('/api/v1/user/login', {
        method: 'POST',
        headers: {
            'content-type': 'application/json'
        }, 
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
}

export const signup = (email, password, phoneNumber) => {
    return fetch('/api/v1/user/create', {
        method: 'POST',
        headers: {
            'content-type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
            password: password,
            phoneNumber: phoneNumber
        })
    })
}

export const forgot = (email, phoneNumber) => {
    return fetch('/api/v1/user/forgot/' + email + "/" + phoneNumber, {
        method: "GET",
        headers: {
            'content-type': 'application/json'
        }
    })
}