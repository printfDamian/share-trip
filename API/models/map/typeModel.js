const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Type {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM types ORDER BY name ASC');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM types WHERE id = ?', [id]);
        return rows[0];
    }

    async findByName(name) {
        const [rows] = await this.connection.execute('SELECT * FROM types WHERE name = ?', [name]);
        return rows[0];
    }

    async create(typeData) {
        const { name, description } = typeData;
        const [result] = await this.connection.execute(
            'INSERT INTO types (name, description) VALUES (?, ?)',
            [name, description]
        );
        return result.insertId;
    }

    async update(id, typeData) {
        const { name, description } = typeData;
        const [result] = await this.connection.execute(
            'UPDATE types SET name = ?, description = ? WHERE id = ?',
            [name, description, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM types WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Type();