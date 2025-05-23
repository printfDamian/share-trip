const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Post {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute('SELECT * FROM posts');
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute('SELECT * FROM posts WHERE id = ?', [id]);
        return rows[0];
    }

    async create(postData) {
        const { userId, state, title, visibility, views } = postData;
        const [result] = await this.connection.execute(
            'INSERT INTO posts (userId, state, title, visibility, views) VALUES (?, ?, ?, ?, ?)',
            [userId, state, title, visibility, views]
        );
        return result.insertId;
    }

    async update(id, postData) {
        const { state, title, visibility, views } = postData;
        const [result] = await this.connection.execute(
            'UPDATE posts SET state = ?, title = ?, visibility = ?, views = ? WHERE id = ?',
            [state, title, visibility, views, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM posts WHERE id = ?', [id]);
        return result.affectedRows;
    }
}

module.exports = new Post();