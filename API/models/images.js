const mysql = require('mysql2');
const dbConfig = require('../config/dbConfig');

class Images {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM images');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM images WHERE id = ?', [id]);
        return rows[0];
    }

    async create(imageData) {
        const { source, createdAt } = imageData;
        const [result] = await this.connection.execute(
            'INSERT INTO images (source, createdAt) VALUES (?, ?)',
            [source, createdAt]
        );
        return result.insertId;
    }

    async update(id, imageData) {
        const { source, createdAt } = imageData;
        const [result] = await this.connection.execute(
            'UPDATE images SET source = ?, createdAt = ? WHERE id = ?',
            [source, createdAt, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM images WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Images();