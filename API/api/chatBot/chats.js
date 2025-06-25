const express = require('express');
const router = express.Router();
const { askChatbot } = require('../../services/chatbot/promptChatbot');

// GET - /api/chatbot/:sessionId - message, created_at, sender_id - account restricted


// POST - /api/chatbot - account restricted
router.post('/', async (req, res) => {
    if (!req.body) {
        return res.status(400).json({
            success: false,
            message: "Prompt is required"
        });
    }

    const { prompt, context } = req.body;
    const response = await askChatbot(prompt, context);

    if(response) {
        res.json({
            success: true,
            response: response
        });
    } else {
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// DELETE - /api/chatbot/:id - owner restricted

module.exports = router;
