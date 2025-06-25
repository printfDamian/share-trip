const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Like {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT l.*, u.name as user_name,
                   CASE 
                       WHEN l.post_id IS NOT NULL THEN 'post'
                       WHEN l.comment_id IS NOT NULL THEN 'comment'
                       WHEN l.poi_id IS NOT NULL THEN 'poi'
                   END as entity_type
            FROM likes l 
            JOIN users u ON l.user_id = u.id 
            ORDER BY l.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, u.name as user_name 
            FROM likes l 
            JOIN users u ON l.user_id = u.id 
            WHERE l.id = ?
        `, [id]);
        return rows[0];
    }

    async findByPostId(postId) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, u.name as user_name 
            FROM likes l 
            JOIN users u ON l.user_id = u.id 
            WHERE l.post_id = ? 
            ORDER BY l.created_at DESC
        `, [postId]);
        return rows;
    }

    async findByCommentId(commentId) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, u.name as user_name 
            FROM likes l 
            JOIN users u ON l.user_id = u.id 
            WHERE l.comment_id = ? 
            ORDER BY l.created_at DESC
        `, [commentId]);
        return rows;
    }

    async findByPoiId(poiId) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, u.name as user_name 
            FROM likes l 
            JOIN users u ON l.user_id = u.id 
            WHERE l.poi_id = ? 
            ORDER BY l.created_at DESC
        `, [poiId]);
        return rows;
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, 
                   CASE 
                       WHEN l.post_id IS NOT NULL THEN 'post'
                       WHEN l.comment_id IS NOT NULL THEN 'comment'
                       WHEN l.poi_id IS NOT NULL THEN 'poi'
                   END as entity_type
            FROM likes l 
            WHERE l.user_id = ? 
            ORDER BY l.created_at DESC
        `, [userId]);
        return rows;
    }

    async checkLike(userId, postId = null, commentId = null, poiId = null) {
        const [rows] = await this.connection.execute(
            'SELECT id FROM likes WHERE user_id = ? AND post_id = ? AND comment_id = ? AND poi_id = ?',
            [userId, postId, commentId, poiId]
        );
        return rows[0];
    }

    async create(likeData) {
        const { post_id, comment_id, poi_id, user_id } = likeData;
        const [result] = await this.connection.execute(
            'INSERT INTO likes (post_id, comment_id, poi_id, user_id) VALUES (?, ?, ?, ?)',
            [post_id, comment_id, poi_id, user_id]
        );
        return result.insertId;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM likes WHERE id = ?', [id]);
        return result.affectedRows;
    }

    async deleteByEntity(userId, postId = null, commentId = null, poiId = null) {
        const [result] = await this.connection.execute(
            'DELETE FROM likes WHERE user_id = ? AND post_id = ? AND comment_id = ? AND poi_id = ?',
            [userId, postId, commentId, poiId]
        );
        return result.affectedRows;
    }

    async countByPostId(postId) {
        const [rows] = await this.connection.execute(
            'SELECT COUNT(*) as count FROM likes WHERE post_id = ?',
            [postId]
        );
        return rows[0].count;
    }

    async countByCommentId(commentId) {
        const [rows] = await this.connection.execute(
            'SELECT COUNT(*) as count FROM likes WHERE comment_id = ?',
            [commentId]
        );
        return rows[0].count;
    }

    async countByPoiId(poiId) {
        const [rows] = await this.connection.execute(
            'SELECT COUNT(*) as count FROM likes WHERE poi_id = ?',
            [poiId]
        );
        return rows[0].count;
    }
}

module.exports = new Like();