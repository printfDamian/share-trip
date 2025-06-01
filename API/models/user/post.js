const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Post {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT p.*, u.name as user_name, t.title as trip_title 
            FROM posts p 
            JOIN users u ON p.user_id = u.id 
            LEFT JOIN trips t ON p.trip_id = t.id 
            ORDER BY p.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
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
            SELECT p.*, t.title as trip_title 
            FROM posts p 
            LEFT JOIN trips t ON p.trip_id = t.id 
            WHERE p.user_id = ? 
            ORDER BY p.created_at DESC
        `, [userId]);
        return rows;
    }

    async findByTripId(tripId) {
        const [rows] = await this.connection.execute(`
            SELECT p.*, u.name as user_name 
            FROM posts p 
            JOIN users u ON p.user_id = u.id 
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
}

module.exports = new Post();