const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Comment {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as user_name,
                   CASE 
                       WHEN c.post_id IS NOT NULL THEN 'post'
                       WHEN c.poi_id IS NOT NULL THEN 'poi'
                   END as entity_type
            FROM comments c 
            JOIN users u ON c.user_id = u.id 
            ORDER BY c.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as user_name 
            FROM comments c 
            JOIN users u ON c.user_id = u.id 
            WHERE c.id = ?
        `, [id]);
        return rows[0];
    }

    async findByPostId(postId) {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as user_name 
            FROM comments c 
            JOIN users u ON c.user_id = u.id 
            WHERE c.post_id = ? 
            ORDER BY c.created_at ASC
        `, [postId]);
        return rows;
    }

    async findByPoiId(poiId) {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as user_name 
            FROM comments c 
            JOIN users u ON c.user_id = u.id 
            WHERE c.poi_id = ? 
            ORDER BY c.created_at ASC
        `, [poiId]);
        return rows;
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(`
            SELECT c.*, 
                   CASE 
                       WHEN c.post_id IS NOT NULL THEN 'post'
                       WHEN c.poi_id IS NOT NULL THEN 'poi'
                   END as entity_type
            FROM comments c 
            WHERE c.user_id = ? 
            ORDER BY c.created_at DESC
        `, [userId]);
        return rows;
    }

    async create(commentData) {
        const { post_id, poi_id, user_id, content } = commentData;
        const [result] = await this.connection.execute(
            'INSERT INTO comments (post_id, poi_id, user_id, content) VALUES (?, ?, ?, ?)',
            [post_id, poi_id, user_id, content]
        );
        return result.insertId;
    }

    async update(id, commentData) {
        const { content } = commentData;
        const [result] = await this.connection.execute(
            'UPDATE comments SET content = ? WHERE id = ?',
            [content, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM comments WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Comment();