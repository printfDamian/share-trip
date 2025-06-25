const express = require('express');
const router = express.Router();

const { requiresToken } = require('../../services/authentication/token');
const isOwnerId = require('../../services/access/ownerChecker');

const Post = require('../../models/user/postModel');

// GET - /api/posts - account restricted - Manter enquanto não houver algum tipo de algoritmo de gerir posts no feed
router.get('/', requiresToken, async (req, res) => {
    try {
        const posts = await Post.findAllWithImages();

        res.json({
            success: true,
            data: posts
        });

    } catch (error) {
        console.error('Error fetching all posts:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// GET - /api/posts/:id - title, content, trip_id, location_id, created_at, updated_at - account restricted
router.get('/:id', requiresToken, async (req, res) => {
    try {
        const postId = req.params.id;
        const post = await Post.findAllById(postId);

        if (!post) {
            return res.status(404).json({
                success: false,
                message: "Post not found"
            });
        }

        res.json({
            success: true,
            data: post
        });

    } catch (error) {
        console.error('Error fetching post:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// GET - /api/posts/user/:userId - title, content, trip_id, location_id, created_at, updated_at - account restricted
router.get('/user/:userId', requiresToken, async (req, res) => {
    try {
        const userId = req.params.userId;
        const posts = await Post.findByUserIdWithUser(userId);

        res.json({
            success: true,
            data: posts
        });

    } catch (error) {
        console.error('Error fetching user posts:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// GET - /api/posts/trip/:tripId - title, content, location_id, created_at, updated_at - account restricted
router.get('/trip/:tripId', requiresToken, async (req, res) => {
    try {
        const tripId = req.params.tripId;
        const posts = await Post.findByTripIdWithUser(tripId);

        res.json({
            success: true,
            data: posts
        });

    } catch (error) {
        console.error('Error fetching trip posts:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

// POST - /api/posts - account restricted
router.post('/', requiresToken, async (req, res) => {
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

        const { trip_id, location_id, title, content } = body;

        if (!title || !content) {
            return res.status(400).json({
                success: false,
                message: "Title and content are required"
            });
        }

        console.log(req.userData.data.id) // 5

        const postData = {
            user_id: req.userData.data.id,
            trip_id: trip_id || null,
            location_id: location_id || null,
            title: title.trim(),
            content: content.trim()
        };

        const postId = await Post.create(postData);

        const newPost = await Post.findById(postId);

        res.status(201).json({
            success: true,
            message: "Post created successfully",
            data: newPost
        });

    } catch (error) {
        console.error('Create post error:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
        });
    }
});

// PUT - /api/posts/:id - owner restricted
router.put('/:id', requiresToken, isOwnerId, async (req, res) => {
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

        const postId = req.params.id;
        const { trip_id, location_id, title, content } = body;

        if (!title && !content && trip_id === undefined && location_id === undefined) {
            return res.status(400).json({
                success: false,
                message: "At least one field (title, content, trip_id, location_id) is required for update"
            });
        }

        const existingPost = await Post.findAllById(postId);
        if (!existingPost) {
            return res.status(404).json({
                success: false,
                message: "Post not found"
            });
        }


        if (existingPost.user_id !== req.userData.data.id) {
            return res.status(403).json({
                success: false,
                message: "Access denied"
            });
        }

        const updateData = {
            trip_id: trip_id !== undefined ? trip_id : existingPost.trip_id,
            location_id: location_id !== undefined ? location_id : existingPost.location_id,
            title: title ? title.trim() : existingPost.title,
            content: content ? content.trim() : existingPost.content
        };

        const affectedRows = await Post.update(postId, updateData);

        if (affectedRows > 0) {
            const updatedPost = await Post.findById(postId);

            return res.json({
                success: true,
                message: "Post updated successfully",
                data: updatedPost
            });
        } else {
            return res.status(500).json({
                success: false,
                message: "Failed to update post"
            });
        }

    } catch (error) {
        console.error('Update post error:', error);
        return res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
        });
    }
});

// DELETE - /api/posts/:id - owner restricted
router.delete('/:id', requiresToken, isOwnerId, async (req, res) => {
    try {
        const postId = req.params.id;

        const existingPost = await Post.findAllById(postId);
        if (!existingPost) {
            return res.status(404).json({
                success: false,
                message: "Post not found"
            });
        }


        if (existingPost.user_id !== req.userData.data.id) {
            return res.status(403).json({
                success: false,
                message: "Access denied"
            });
        }

        const affectedRows = await Post.delete(postId);

        if (affectedRows > 0) {
            return res.json({
                success: true,
                message: "Post deleted successfully"
            });
        } else {
            return res.status(500).json({
                success: false,
                message: "Failed to delete post"
            });
        }

    } catch (error) {
        console.error('Delete post error:', error);
        return res.status(500).json({
            success: false,
            message: "Internal server error" +
                (process.env.DEV_MODE === 'TRUE' ? ' Dev Error: ' + error.message : '')
        });
    }
});

module.exports = router;
