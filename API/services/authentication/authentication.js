require('dotenv').config({ path: 'config/.env' });
const bcrypt = require('bcryptjs');
const User = require('../../models/user/userModel');

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
    // Recolha dos dados

    // Análise e formatação dos dados

    // Verificações Base de Dados

    // Resposta

    // Enviar para a BD

    // Verificar sucesso
}

module.exports = { register, login }
