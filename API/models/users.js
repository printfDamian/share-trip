const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class User {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM users');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM users WHERE id = ?', [id]);
        return rows[0];
    }

    async create(userData) {
        const { name, email, password, phone } = userData;
        const [result] = await this.connection.execute(
            'INSERT INTO users (name, email, password, phone) VALUES (?, ?, ?, ?)',
            [name, email, password, phone]
        );
        return result.insertId;
    }

    async update(id, userData) {
        const { name, email, password, phone } = userData;
        const [result] = await this.connection.execute(
            'UPDATE users SET name = ?, email = ?, password = ?, phone = ? WHERE id = ?',
            [name, email, password, phone, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM users WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new User();
