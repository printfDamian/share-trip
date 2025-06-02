const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class Location {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT l.*, poi.name as poi_name 
            FROM location l 
            JOIN points_of_interest poi ON l.poi_id = poi.id 
            ORDER BY l.id DESC
        `);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, poi.name as poi_name 
            FROM location l 
            JOIN points_of_interest poi ON l.poi_id = poi.id 
            WHERE l.id = ?
        `, [id]);
        return rows[0];
    }

    async findByPoiId(poiId) {
        const [rows] = await this.connection.execute('SELECT * FROM location WHERE poi_id = ?', [poiId]);
        return rows[0];
    }

    async findNearby(latitude, longitude, radiusKm = 10) {
        const [rows] = await this.connection.execute(`
            SELECT l.*, poi.name as poi_name,
                   (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * 
                   cos(radians(longitude) - radians(?)) + sin(radians(?)) * 
                   sin(radians(latitude)))) AS distance
            FROM location l 
            JOIN points_of_interest poi ON l.poi_id = poi.id 
            HAVING distance < ? 
            ORDER BY distance
        `, [latitude, longitude, latitude, radiusKm]);
        return rows;
    }

    async create(locationData) {
        const { poi_id, latitude, longitude, address } = locationData;
        const [result] = await this.connection.execute(
            'INSERT INTO location (poi_id, latitude, longitude, address) VALUES (?, ?, ?, ?)',
            [poi_id, latitude, longitude, address]
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