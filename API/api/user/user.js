const express = require('express');
const router = express.Router();
const { register, login } = require('../../services/authentication/authentication');

// api/user/register - acesso livre
router.post('/register', (req, res) => {
    register(req, res);
});

module.exports = router;
