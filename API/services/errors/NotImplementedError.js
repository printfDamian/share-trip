class NotImplementedError extends Error {
    constructor(message = 'This feature is not yet implemented.') {
        super(message);
        this.name = 'NotImplementedError';
        this.statusCode = 501;
    }
}

module.exports = NotImplementedError;
