const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Types {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM types');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM types WHERE id = ?', [id]);
        return rows[0];
    }

    async create(typeData) {
        const { type, description } = typeData;
        const [result] = await this.connection.execute(
            'INSERT INTO types (type, description) VALUES (?, ?)',
            [type, description]
        );
        return result.insertId;
    }

    async update(id, typeData) {
        const { type, description } = typeData;
        const [result] = await this.connection.execute(
            'UPDATE types SET type = ?, description = ? WHERE id = ?',
            [type, description, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM types WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Types();