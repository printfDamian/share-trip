const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Memory {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT m.*, u.name as user_name 
            FROM memory m 
            JOIN users u ON m.user_id = u.id 
            ORDER BY m.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT m.*, u.name as user_name 
            FROM memory m 
            JOIN users u ON m.user_id = u.id 
            WHERE m.id = ?
        `, [id]);
        return rows[0];
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM memory WHERE user_id = ? ORDER BY created_at DESC',
            [userId]
        );
        return rows;
    }

    async create(memoryData) {
        const { user_id, notes } = memoryData;
        const [result] = await this.connection.execute(
            'INSERT INTO memory (user_id, notes) VALUES (?, ?)',
            [user_id, notes]
        );
        return result.insertId;
    }

    async update(id, memoryData) {
        const { notes } = memoryData;
        const [result] = await this.connection.execute(
            'UPDATE memory SET notes = ? WHERE id = ?',
            [notes, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM memory WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Memory();