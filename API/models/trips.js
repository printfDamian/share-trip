const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class Trip {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM trips');
        return rows;
    }
    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM trips WHERE id = ?', [id]);
        return rows[0];
    }
    async create(tripData) {
        const { user_id, trip_id, message } = tripData;
        const [result] = await this.connection.execute(
            'INSERT INTO trips (user_id, trip_id, message) VALUES (?, ?, ?)',
            [user_id, trip_id, message]
        );
        return result.insertId;
    }
    
}