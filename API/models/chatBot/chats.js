const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class chats {
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
        const { user_id, trip_id, message } = chatData;
        const [result] = await this.connection.execute(
            'INSERT INTO chats (user_id, message) VALUES (?, ?, ?)',
            [user_id, trip_id, message]
        );
        return result.insertId;
    }
    
}