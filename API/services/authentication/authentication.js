require('dotenv').config({ path: 'API/config' });
const bcrypt = require('bcryptjs');
const User = require('../../models/users/users');

async function register(req, res, next) {
    try {
        let body;

        // Recolha dos dados
        if (req.body) {
            try {
                // If body is string, parse it; if already object, use directly
                body = typeof req.body === 'string' ? JSON.parse(req.body) : req.body;
            } catch (e) {
                return res.status(406).json({
                    success: false,
                    message: "Only 'application/json' content type supported." +
                        (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + e.message : '')
                });
            }
        } else {
            return res.status(400).json({
                success: false,
                message: "Request body is required"
            });
        }

        const { name, email, password, phone } = body;

        // Análise e formatação dos dados
        if (!name || !email || !password) {
            return res.status(400).json({
                success: false,
                message: "Name, email, and password are required fields"
            });
        }

        // Validate email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return res.status(400).json({
                success: false,
                message: "Invalid email format"
            });
        }

        // Validate password strength
        if (password.length < 8) {
            return res.status(400).json({
                success: false,
                message: "Password must be at least 8 characters long"
            });
        }

        // Format data
        const formattedName = name.trim();
        const formattedEmail = email.trim().toLowerCase();
        const formattedPhone = phone ? phone.trim() : null;

        // Verificações Base de Dados
        const existingUser = await User.findByEmail(formattedEmail);
        if (existingUser) {
            return res.status(409).json({
                success: false,
                message: "User with this email already exists"
            });
        }

        // Encriptar conteudo sensivel Bcrypt
        const saltRounds = 12;
        const hashedPassword = await bcrypt.hash(password, saltRounds);

        // Enviar para a BD
        const userData = {
            name: formattedName,
            email: formattedEmail,
            password: hashedPassword,
            phone: formattedPhone
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

async function login(req, res, next) {
    // Recolha dos dados

    // Análise e formatação dos dados

    // Verificações Base de Dados

    // Resposta

    // Enviar para a BD

    // Verificar sucesso
}

module.exports = { register, login }
