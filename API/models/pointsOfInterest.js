const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class PointOfInterest {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM points_of_interest');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM points_of_interest WHERE id = ?', [id]);
        return rows[0];
    }

    async create(poiData) {
        const { name, rating, price, wifi, accessibility, description } = poiData;
        const [result] = await this.connection.execute(
            'INSERT INTO points_of_interest (name, rating, price, wifi, accessibility, description) VALUES (?, ?, ?, ?, ?, ?)',
            [name, rating, price, wifi, accessibility, description]
        );
        return result.insertId;
    }

    async update(id, poiData) {
        const { name, rating, price, wifi, accessibility, description } = poiData;
        const [result] = await this.connection.execute(
            'UPDATE points_of_interest SET name = ?, rating = ?, price = ?, wifi = ?, accessibility = ?, description = ? WHERE id = ?',
            [name, rating, price, wifi, accessibility, description, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM points_of_interest WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new PointOfInterest();