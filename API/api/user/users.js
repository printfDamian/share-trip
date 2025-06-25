const express = require('express');
const router = express.Router();

const { register, login } = require('../../services/authentication/authentication');
const { requiresToken, unauthorized, verifyToken } = require('../../services/authentication/token');
const isOwnerId = require('../../services/access/ownerChecker');
const NotImplementedError = require('../../services/errors/NotImplementedError');

const User = require('../../models/user/userModel');

// POST - /api/users/register - unrestricted
router.post('/register', register);

// POST - /api/users/login - unrestricted
router.post('/login', login);

// POST - /api/users/login/check - unrestricted 
router.post('/login/check', verifyToken);

// GET - /api/users/:id - name, active, created_at, updated_at - account restricted
router.get('/:id', requiresToken, async (req, res) => {
    try {
        const userId = req.params.id;
        const user = await User.findById(userId);

        if (!user) {
            return res.status(404).json({
                success: false,
                message: "User not found"
            });
        }

        res.json({
            success: true,
            data: user
        });

    } catch (error) {
        console.error('Error fetching user:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// GET - /api/users/:id/all - * - owner restricted
router.get('/:id/all', requiresToken, isOwnerId, async (req, res) => {
    try {
        const userId = req.params.id;
        const user = await User.findAllById(userId);

        if (!user) {
            return res.status(404).json({
                success: false,
                message: "User not found"
            });
        }

        res.json({
            success: true,
            data: user
        });

    } catch (error) {
        console.error('Error fetching user:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// PUT - /api/users - owner restricted
router.put('/', requiresToken, async (req, res) => {
    try {
        let body;

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

        const userId = req.userData.data.id;
        const { name, password } = body;

        if (!name && !password) {
            return res.status(400).json({
                success: false,
                message: "At least one field (name, password) is required for update"
            });
        }

        const existingUser = await User.findAllById(userId);
        if (!existingUser) {
            return res.status(404).json({
                success: false,
                message: "User not found"
            });
        }

        const updateData = {
            name: name ? name.trim() : existingUser.name,
            password: existingUser.password // manter password existente por defeito
        };

        if (password) {
            if (password.length < 8) {
                return res.status(400).json({
                    success: false,
                    message: "Password must be at least 8 characters long"
                });
            }

            const bcrypt = require('bcryptjs');
            const saltRounds = parseInt(process.env.BCRYPT_SALT) ?? 10;
            updateData.password = await bcrypt.hash(password, saltRounds);
        }

        const affectedRows = await User.update(userId, updateData);

        if (affectedRows > 0) {
            const updatedUser = await User.findById(userId);

            return res.json({
                success: true,
                message: "User updated successfully",
                data: updatedUser
            });
        } else {
            return res.status(500).json({
                success: false,
                message: "Failed to update user"
            });
        }

    } catch (error) {
        console.error('Update user error:', error);
        return res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
        });
    }
});

// DELETE - /api/users- owner restricted
router.delete('/', requiresToken, async (req, res) => {
    try {
        const userId = req.userData.data.id;

        const affectedRows = await User.delete(userId);

        if (affectedRows > 0) {
            return res.json({
                success: true,
                message: "User deactivated successfully"
            });
        } else {
            return res.status(500).json({
                success: false,
                message: "Failed to remove user"
            });
        }

    } catch (error) {
        console.error('Delete user error:', error);
        return res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
        });
    }
});

module.exports = router;
