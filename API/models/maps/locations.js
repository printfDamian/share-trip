const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Location {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM location');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM location WHERE id = ?', [id]);
        return rows[0];
    }

    async create(locationData) {
        const { latitude, longitude, address } = locationData;
        const [result] = await this.connection.execute(
            'INSERT INTO location (latitude, longitude, address) VALUES (?, ?, ?)',
            [latitude, longitude, address]
        );
        return result.insertId;
    }

    async update(id, locationData) {
        const { latitude, longitude, address } = locationData;
        const [result] = await this.connection.execute(
            'UPDATE location SET latitude = ?, longitude = ?, address = ? WHERE id = ?',
            [latitude, longitude, address, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM location WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Location();