const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class Chat {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM chats');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM chats WHERE id = ?', [id]);
        return rows[0];
    }

    async create(chatData) {
        const { owner, message, createdAt, modifiedAt } = chatData;
        const [result] = await this.connection.execute(
            'INSERT INTO chats (owner, message, createdAt, modifiedAt) VALUES (?, ?, ?, ?)',
            [owner, message, createdAt, modifiedAt]
        );
        return result.insertId;
    }

    async update(id, chatData) {
        const { owner, message, createdAt, modifiedAt } = chatData;
        const [result] = await this.connection.execute(
            'UPDATE chats SET owner = ?, message = ?, createdAt = ?, modifiedAt = ? WHERE id = ?',
            [owner, message, createdAt, modifiedAt, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM chats WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Chat();