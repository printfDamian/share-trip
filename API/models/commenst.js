const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class Comments {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM comments');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM comments WHERE id = ?', [id]);
        return rows[0];
    }

    async create(commentData) {
        const { comment, state, createdAt, modifiedAt } = commentData;
        const [result] = await this.connection.execute(
            'INSERT INTO comments (comment, state, createdAt, modifiedAt) VALUES (?, ?, ?, ?)',
            [comment, state, createdAt, modifiedAt]
        );
        return result.insertId;
    }

    async update(id, commentData) {
        const { comment, state, createdAt, modifiedAt } = commentData;
        const [result] = await this.connection.execute(
            'UPDATE comments SET comment = ?, state = ?, createdAt = ?, modifiedAt = ? WHERE id = ?',
            [comment, state, createdAt, modifiedAt, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM comments WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Comments();