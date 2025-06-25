const mysql = require('mysql2/promise');
const dbConfig = require('../../config/dbConfig');

class User {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM users');
        return rows;
    }

    async findAllById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM users WHERE id = ?', [id]);
        return rows[0];
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT name, active, created_at, updated_at FROM users WHERE id = ?', [id]);
        return rows[0];
    }

    async findByEmail(email) {
        const [rows] = await this.connection.execute('SELECT * FROM users WHERE email = ?', [email]);
        return rows[0];
    }

    async create(userData) {
        const { name, email, password } = userData;
        const [result] = await this.connection.execute(
            'INSERT INTO users (name, email, password) VALUES (?, ?, ?)',
            [name, email, password]
        );
        return result.insertId;
    }

    async update(id, userData) {
        const { name, password, active } = userData;

        if (active !== undefined) {
            const [result] = await this.connection.execute(
                'UPDATE users SET name = ?, password = ?, active = ? WHERE id = ?',
                [name, password, active, id]
            );
            return result.affectedRows;
        } else {
            const [result] = await this.connection.execute(
                'UPDATE users SET name = ?, password = ? WHERE id = ?',
                [name, password, id]
            );
            return result.affectedRows;
        }
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM users WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new User();
