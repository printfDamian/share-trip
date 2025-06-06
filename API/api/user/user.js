const express = require('express');
const router = express.Router();
const { register, login } = require('../../services/authentication/authentication');

// api/user/register - acesso livre
router.post('/register', (req, res) => {
    register(req, res);
});

// api/user/login - acesso livre
router.post('/login', (req, res) => {
    login(req, res);
});

module.exports = router;
