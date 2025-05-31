const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Session {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM sessions');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM sessions WHERE id = ?', [id]);
        return rows[0];
    }

    async findByUserId(userId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM sessions WHERE user_id = ? AND expires_at > NOW() ORDER BY created_at DESC',
            [userId]
        );
        return rows;
    }

    async create(sessionData) {
        const { user_id, dailyLimit = 25, refreshLimit } = sessionData;
        const [result] = await this.connection.execute(
            'INSERT INTO sessions (user_id, dailyLimit, refreshLimit) VALUES (?, ?, ?)',
            [user_id, dailyLimit, refreshLimit]
        );
        return result.insertId;
    }

    async update(id, sessionData) {
        const { dailyLimit, refreshLimit } = sessionData;
        const [result] = await this.connection.execute(
            'UPDATE sessions SET dailyLimit = ?, refreshLimit = ? WHERE id = ?',
            [dailyLimit, refreshLimit, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM sessions WHERE id = ?', [id]);
        return result.affectedRows;
    }

    async deleteExpired() {
        const [result] = await this.connection.execute('DELETE FROM sessions WHERE expires_at < NOW()');
        return result.affectedRows;
    }
}

module.exports = new Session();