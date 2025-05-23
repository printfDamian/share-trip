const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

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

    async create(sessionData) {
        const { state, createdAt, modifiedAt } = sessionData;
        const [result] = await this.connection.execute(
            'INSERT INTO sessions (state, createdAt, modifiedAt) VALUES (?, ?, ?)',
            [state, createdAt, modifiedAt]
        );
        return result.insertId;
    }

    async update(id, sessionData) {
        const { state, createdAt, modifiedAt } = sessionData;
        const [result] = await this.connection.execute(
            'UPDATE sessions SET state = ?, createdAt = ?, modifiedAt = ? WHERE id = ?',
            [state, createdAt, modifiedAt, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM sessions WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Session();