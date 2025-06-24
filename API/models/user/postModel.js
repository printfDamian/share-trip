const mysql = require('mysql2/promise');
const dbConfig = require('../../config/dbConfig');

class Post {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT p.*, 
                   u.id as user_id, u.name as user_name, u.email as user_email,
                   t.title as trip_title 
            FROM posts p 
            JOIN users u ON p.user_id = u.id 
            LEFT JOIN trips t ON p.trip_id = t.id 
            ORDER BY p.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT p.title, p.content, p.trip_id, p.location_id, p.created_at, p.updated_at 
            FROM posts p 
            WHERE p.id = ?
        `, [id]);
        return rows[0];
    }

    async findAllById(id) {
        const [rows] = await this.connection.execute(`
            SELECT p.*, u.name as user_name, t.title as trip_title 
            FROM posts p 
            JOIN users u ON p.user_id = u.id 
            LEFT JOIN trips t ON p.trip_id = t.id 
            WHERE p.id = ?
        `, [id]);
        return rows[0];
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(`
            SELECT p.title, p.content, p.trip_id, p.location_id, p.created_at, p.updated_at 
            FROM posts p 
            WHERE p.user_id = ? 
            ORDER BY p.created_at DESC
        `, [userId]);
        return rows;
    }

    async findByTripId(tripId) {
        const [rows] = await this.connection.execute(`
            SELECT p.title, p.content, p.location_id, p.created_at, p.updated_at, p.user_id
            FROM posts p 
            WHERE p.trip_id = ? 
            ORDER BY p.created_at DESC
        `, [tripId]);
        return rows;
    }

    async findByUserIdWithUser(userId) {
        const [rows] = await this.connection.execute(`
            SELECT p.*, u.name as user_name, t.title as trip_title 
            FROM posts p 
            JOIN users u ON p.user_id = u.id 
            LEFT JOIN trips t ON p.trip_id = t.id 
            WHERE p.user_id = ? 
            ORDER BY p.created_at DESC
        `, [userId]);
        return rows;
    }

    async findByTripIdWithUser(tripId) {
        const [rows] = await this.connection.execute(`
            SELECT p.*, u.name as user_name, t.title as trip_title 
            FROM posts p 
            JOIN users u ON p.user_id = u.id 
            LEFT JOIN trips t ON p.trip_id = t.id 
            WHERE p.trip_id = ? 
            ORDER BY p.created_at DESC
        `, [tripId]);
        return rows;
    }

    async create(postData) {
        const { user_id, trip_id, location_id, title, content } = postData;
        const [result] = await this.connection.execute(
            'INSERT INTO posts (user_id, trip_id, location_id, title, content) VALUES (?, ?, ?, ?, ?)',
            [user_id, trip_id, location_id, title, content]
        );
        return result.insertId;
    }

    async update(id, postData) {
        const { trip_id, location_id, title, content } = postData;
        const [result] = await this.connection.execute(
            'UPDATE posts SET trip_id = ?, location_id = ?, title = ?, content = ? WHERE id = ?',
            [trip_id, location_id, title, content, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM posts WHERE id = ?', [id]);
        return result.affectedRows;
    }

    async findAllWithImages() {
        const [rows] = await this.connection.execute(`
            SELECT 
                p.id,
                p.user_id,
                p.trip_id,
                p.location_id,
                p.title,
                p.content,
                p.created_at,
                p.updated_at,
                u.name as user_name,
                u.email as user_email,
                (SELECT i.url 
                 FROM images i 
                 WHERE i.post_id = p.id 
                 ORDER BY i.id ASC 
                 LIMIT 1) as imageUrl
            FROM posts p
            LEFT JOIN users u ON p.user_id = u.id
            ORDER BY p.created_at DESC
        `);
        return rows;
    }
}

module.exports = new Post();
