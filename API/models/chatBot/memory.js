const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class Memory {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM memory');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM memory WHERE id = ?', [id]);
        return rows[0];
    }

    async create(memoryData) {
        const { description, createdAt, modifiedAt } = memoryData;
        const [result] = await this.connection.execute(
            'INSERT INTO memory (description, createdAt, modifiedAt) VALUES (?, ?, ?)',
            [description, createdAt, modifiedAt]
        );
        return result.insertId;
    }

    async update(id, memoryData) {
        const { description, createdAt, modifiedAt } = memoryData;
        const [result] = await this.connection.execute(
            'UPDATE memory SET description = ?, createdAt = ?, modifiedAt = ? WHERE id = ?',
            [description, createdAt, modifiedAt, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM memory WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Memory();