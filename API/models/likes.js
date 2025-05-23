const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class Likes {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM likes');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM likes WHERE id = ?', [id]);
        return rows[0];
    }

    async create(likeData) {
        const { createdAt } = likeData;
        const [result] = await this.connection.execute(
            'INSERT INTO likes (createdAt) VALUES (?)',
            [createdAt]
        );
        return result.insertId;
    }

    async update(id, likeData) {
        const { createdAt } = likeData;
        const [result] = await this.connection.execute(
            'UPDATE likes SET createdAt = ? WHERE id = ?',
            [createdAt, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM likes WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Likes();