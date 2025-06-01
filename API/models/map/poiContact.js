const mysql = require('mysql2');
const dbConfig = require('../../config/dbConfig');

class PoiContact {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT pc.*, 
                   poi1.name as poi_name,
                   poi2.name as contact_poi_name
            FROM poi_contacts pc 
            JOIN points_of_interest poi1 ON pc.poi_id = poi1.id 
            JOIN points_of_interest poi2 ON pc.contact_poi_id = poi2.id 
            ORDER BY pc.created_at DESC
        `);
        return rows;
    }

    async findByPoiId(poiId) {
        const [rows] = await this.connection.execute(`
            SELECT pc.*, poi.name as contact_poi_name 
            FROM poi_contacts pc 
            JOIN points_of_interest poi ON pc.contact_poi_id = poi.id 
            WHERE pc.poi_id = ? 
            ORDER BY pc.created_at DESC
        `, [poiId]);
        return rows;
    }

    async checkContact(poiId, contactPoiId) {
        const [rows] = await this.connection.execute(
            'SELECT * FROM poi_contacts WHERE poi_id = ? AND contact_poi_id = ?',
            [poiId, contactPoiId]
        );
        return rows[0];
    }

    async create(contactData) {
        const { poi_id, contact_poi_id } = contactData;
        const [result] = await this.connection.execute(
            'INSERT INTO poi_contacts (poi_id, contact_poi_id) VALUES (?, ?)',
            [poi_id, contact_poi_id]
        );
        return result.affectedRows;
    }

    async delete(poiId, contactPoiId) {
        const [result] = await this.connection.execute(
            'DELETE FROM poi_contacts WHERE poi_id = ? AND contact_poi_id = ?',
            [poiId, contactPoiId]
        );
        return result.affectedRows;
    }

    async deleteAllByPoiId(poiId) {
        const [result] = await this.connection.execute(
            'DELETE FROM poi_contacts WHERE poi_id = ? OR contact_poi_id = ?',
            [poiId, poiId]
        );
        return result.affectedRows;
    }
}

module.exports = new PoiContact();