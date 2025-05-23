const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class posts {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }
    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM posts');
        return rows;
    }
    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM posts WHERE id = ?', [id]);
        return rows[0];
    }
    async create(postData) {
        const { user_id, trip_id, message } = postData;
        const [result] = await this.connection.execute(
            'INSERT INTO posts (user_id, trip_id, message) VALUES (?, ?, ?)',
            [user_id, trip_id, message]
        );
        return result.insertId;
    }

    
    
}