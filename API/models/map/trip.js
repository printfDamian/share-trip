const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Trip {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT t.*, u.name as user_name 
            FROM trips t 
            JOIN users u ON t.user_id = u.id 
            ORDER BY t.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT t.*, u.name as user_name 
            FROM trips t 
            JOIN users u ON t.user_id = u.id 
            WHERE t.id = ?
        `, [id]);
        return rows[0];
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM trips WHERE user_id = ? ORDER BY start_date DESC',
            [userId]
        );
        return rows;
    }

    async create(tripData) {
        const { user_id, title, description, start_date, end_date, location } = tripData;
        const [result] = await this.connection.execute(
            'INSERT INTO trips (user_id, title, description, start_date, end_date, location) VALUES (?, ?, ?, ?, ?, ?)',
            [user_id, title, description, start_date, end_date, location]
        );
        return result.insertId;
    }

    async update(id, tripData) {
        const { title, description, start_date, end_date, location } = tripData;
        const [result] = await this.connection.execute(
            'UPDATE trips SET title = ?, description = ?, start_date = ?, end_date = ?, location = ? WHERE id = ?',
            [title, description, start_date, end_date, location, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM trips WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Trip();