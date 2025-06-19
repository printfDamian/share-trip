require('dotenv').config({ path: 'config/.env' });
const jwt = require('jsonwebtoken');

function checkToken(token) {
    return jwt.verify(token, process.env.SECRET_KEY, (err, decoded) => {
        if(err) {
            console.error('Token check error: ' + err);
            return null;
        }
        return decoded;
    });
}

function unauthorized(res) {
    return res.status(401).json({
        success: false,
        message: "Unauthorized"
    });
}

function requiresToken(req, res, next) {
    const authHeader = req.headers["authorization"];
	if(!authHeader) return unauthorized(res);

	const token = authHeader.split(" ")[1] || authHeader;
  	if(!token) return unauthorized(res);

    const userData = checkToken(token);
    
	if(userData) {
		req.userData = userData;
		next();
	} else {
        return unauthorized(res);
    };
}

module.exports = { requiresToken, unauthorized }
