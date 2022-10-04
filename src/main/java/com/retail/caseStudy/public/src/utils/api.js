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
    return fetch('/api/v1/user/forgot/' + email + '/' + phoneNumber, {
        method: "GET",
        headers: {
            'content-type': 'application/json'
        }
    })
}

export const resetPassword = (userId, key, password) => {
    return fetch('/api/v1/user/forgot/', {
        method: "POST",
        headers: {
            'content-type': 'application/json'
        },
        body: JSON.stringify({
            userId: userId,
            key: key,
            password: password
        })
    });
}

export const getProduct = (productId) => {
    return fetch('/api/v1/product/' + productId, {
        method: "GET",
        headers: {
            'content-type': 'application/json'
        }
    });
}

export const addToCart = (quantity, product, jwt) => {
    const headers = new Headers({ 
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    console.log(jwt);
    return fetch('/api/v1/user/cart/' + quantity, {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(product)
    });
}

export const getCart = (jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/user/cart', {
        method: "GET",
        headers: headers
    })
}

export const createOrder = (jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/order', {
        method: "POST",
        headers: headers
    })
}

export const getOrders = (jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/order', {
        method: "GET",
        headers: headers
    })
}

export const getOrder = (id, jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/order/' + id, {
        method: "GET",
        headers: headers
    })
}

export const updateOrder = (id, status, jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/order/' + id + "/" + status, {
        method: "PUT",
        headers: headers
    })
}

