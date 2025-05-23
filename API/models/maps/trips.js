const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

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
        const { name, description } = tripData;
        const [result] = await this.connection.execute(
            'INSERT INTO trips (name, description) VALUES (?, ?)',
            [name, description]
        );
        return result.insertId;
    }

    async update(id, tripData) {
        const { name, description } = tripData;
        const [result] = await this.connection.execute(
            'UPDATE trips SET name = ?, description = ? WHERE id = ?',
            [name, description, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM trips WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Trip();