const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Contacts {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM contacts');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM contacts WHERE id = ?', [id]);
        return rows[0];
    }

    async create(contactData) {
        const { type, description } = contactData;
        const [result] = await this.connection.execute(
            'INSERT INTO contacts (type, description) VALUES (?, ?)',
            [type, description]
        );
        return result.insertId;
    }

    async update(id, contactData) {
        const { type, description } = contactData;
        const [result] = await this.connection.execute(
            'UPDATE contacts SET type = ?, description = ? WHERE id = ?',
            [type, description, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM contacts WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Contacts();