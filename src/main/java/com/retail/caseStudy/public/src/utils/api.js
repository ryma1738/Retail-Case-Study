export const getAllProducts = () => {
    return fetch('/api/v1/product', { 
        method: 'GET',
        headers: {
            'content-type': 'application/json'
        }
    });
}