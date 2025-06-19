require('dotenv').config({ path: 'config/.env' });
const { unauthorized } = require('../authentication/token');

function isOwnerId(req, res, next) {
    const paramId = req.params.id;
    const loginId = req.userData?.data.id;

    if (!paramId) {
        return res.status(400).json({
            success: false,
            message: "Missing required parameter: id"
        });
    }

    if (!loginId) {
        return unauthorized(res);
    }

    if(paramId == loginId) {
        next();
    } else {
        return unauthorized(res);
    }
}

module.exports = isOwnerId;
