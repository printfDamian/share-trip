const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Chat {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as sender_name 
            FROM chats c 
            JOIN users u ON c.sender_id = u.id 
            ORDER BY c.created_at DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as sender_name 
            FROM chats c 
            JOIN users u ON c.sender_id = u.id 
            WHERE c.id = ?
        `, [id]);
        return rows[0];
    }

    async findBySessionId(sessionId) {
        const [rows] = await this.connection.execute(`
            SELECT c.*, u.name as sender_name 
            FROM chats c 
            JOIN users u ON c.sender_id = u.id 
            WHERE c.session_id = ? 
            ORDER BY c.created_at ASC
        `, [sessionId]);
        return rows;
    }

    async create(chatData) {
        const { session_id, sender_id, message } = chatData;
        const [result] = await this.connection.execute(
            'INSERT INTO chats (session_id, sender_id, message) VALUES (?, ?, ?)',
            [session_id, sender_id, message]
        );
        return result.insertId;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM chats WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Chat();