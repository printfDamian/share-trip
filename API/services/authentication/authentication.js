require('dotenv').config({ path: 'config/.env' });
const bcrypt = require('bcryptjs');
const User = require('../../models/user/userModel');
const jwt = require('jsonwebtoken');

async function register(req, res) {
    try {
        let body;

        // Recolha dos dados
        if (req.body) {
            try {
                body = typeof req.body === 'string' ? JSON.parse(req.body) : req.body;
            } catch (error) {
                return res.status(406).json({
                    success: false,
                    message: "Only 'application/json' content type supported." +
                        (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
                });
            }
        } else {
            return res.status(400).json({
                success: false,
                message: "Request body is required"
            });
        }

        const { name, email, password } = body;

        // Análise e formatação dos dados
        if (!name || !email || !password) {
            return res.status(400).json({
                success: false,
                message: "Name, email, and password are required fields"
            });
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return res.status(400).json({
                success: false,
                message: "Invalid email format"
            });
        }

        if (password.length < 8) { // Alterar caso seja necessário acrescentar mais validações
            return res.status(400).json({
                success: false,
                message: "Password must be at least 8 characters long"
            });
        }

        const formattedName = name.trim();
        const formattedEmail = email.trim().toLowerCase();

        // Verificações Base de Dados
        const existingUser = await User.findByEmail(formattedEmail);
        if (existingUser) {
            return res.status(409).json({
                success: false,
                message: "User with this email already exists"
            });
        }

        // Encriptar conteudo sensivel Bcrypt
        const saltRounds = parseInt(process.env.BCRYPT_SALT) ?? 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);

        // Enviar para a BD
        const userData = {
            name: formattedName,
            email: formattedEmail,
            password: hashedPassword,
        };

        const userId = await User.create(userData);

        // Verificar sucesso
        if (userId) {
            // Get the created user (without password)
            const newUser = await User.findById(userId);
            if (newUser) {
                delete newUser.password; // Remove password from response
            }

            return res.status(201).json({
                success: true,
                message: "User registered successfully",
                data: {
                    user: newUser
                }
            });
        } else {
            return res.status(500).json({
                success: false,
                message: "Failed to create user"
            });
        }

    } catch (error) {
        console.error('Register error:', error);
        return res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ': ' + error.message : '')
        });
    }
}

async function login(req, res) {
    try {
        let body;

        // Recolha dos dados
        if (req.body) {
            try {
                body = typeof req.body === 'string' ? JSON.parse(req.body) : req.body;
            } catch (error) {
                return res.status(406).json({
                    success: false,
                    message: "Only 'application/json' content type supported." +
                        (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
                });
            }
        } else {
            return res.status(400).json({
                success: false,
                message: "Request body is required"
            });
        }

        const { email, password } = body;

        // Análise e formatação dos dados
        if ( !email || !password) {
            return res.status(400).json({
                success: false,
                message: "Email and password are required fields"
            });
        }

        const formattedEmail = email.trim().toLowerCase();

        // Verificações Base de Dados
        const existingUser = await User.findByEmail(formattedEmail);
        if (!existingUser) {
            return res.status(404).json({
                success: false,
                message: "User not found"
            });
        }

        // Verificar sucesso
        if (await bcrypt.compare(password, existingUser.password)) {
            const token = jwt.sign({
                // Math.floor(Date.now() / 1000) > Seconds from 1970
                // + (60 * 60 * 24 * 7) > Quantity of seconds in 1 week
                exp: Math.floor(Date.now() / 1000) + (60 * 60 * 24 * 7),
                data: {
                    id: existingUser.id,
                    email: email,
                    name: existingUser.name
                }
            }, process.env.SECRET_KEY)

            return res.status(202).json({
                success: true,
                message: "User logged in successfully",
                data: {
                    token: token
                }
            });
        } else {
            return res.status(404).json({
                success: false,
                message: "Invalid credentials"
            });
        }

    } catch (error) {
        console.error('Login error:', error);
        return res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ': ' + error.message : '')
        });
    }
}

module.exports = { register, login }
