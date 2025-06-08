const express = require('express');
const router = express.Router();
const { register, login } = require('../../services/authentication/authentication');
const NotImplementedError = require('../../services/errors/NotImplementedError');

// POST - api/users/register - unrestricted
router.post('/register', (req, res) => {
    register(req, res);
});

// POST - api/users/login - unrestricted
router.post('/login', (req, res) => {
    login(req, res);
});

// GET - api/users/:id -  - accont restricted
router.get('/:id', (req, res) => {
    throw new NotImplementedError();
});

// GET - api/users/:id/all - * - owner restricted
router.get('/:id/all', (req, res) => {
    throw new NotImplementedError();
});


module.exports = router;
