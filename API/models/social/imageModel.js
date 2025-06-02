const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Image {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT i.*, 
                   CASE 
                       WHEN i.post_id IS NOT NULL THEN 'post'
                       WHEN i.user_id IS NOT NULL THEN 'user'
                       WHEN i.poi_id IS NOT NULL THEN 'poi'
                   END as entity_type
            FROM images i 
            ORDER BY i.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM images WHERE id = ?', [id]);
        return rows[0];
    }

    async findByPostId(postId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM images WHERE post_id = ? ORDER BY created_at DESC',
            [postId]
        );
        return rows;
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM images WHERE user_id = ? ORDER BY created_at DESC',
            [userId]
        );
        return rows;
    }

    async findByPoiId(poiId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM images WHERE poi_id = ? ORDER BY created_at DESC',
            [poiId]
        );
        return rows;
    }

    async create(imageData) {
        const { post_id, user_id, poi_id, url } = imageData;
        const [result] = await this.connection.execute(
            'INSERT INTO images (post_id, user_id, poi_id, url) VALUES (?, ?, ?, ?)',
            [post_id, user_id, poi_id, url]
        );
        return result.insertId;
    }

    async update(id, imageData) {
        const { url } = imageData;
        const [result] = await this.connection.execute(
            'UPDATE images SET url = ? WHERE id = ?',
            [url, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM images WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Image();