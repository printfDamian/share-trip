const mysql = require('mysql2/promise');
const dbConfig = require('../../config/dbConfig');

class PointOfInterest {
    constructor() {
        this.connection = mysql.createPool(dbConfig);
    }

    async findAll() {
        const [rows] = await this.connection.execute(`
            SELECT poi.*, t.name as type_name, tr.title as trip_title 
            FROM points_of_interest poi 
            JOIN types t ON poi.type_id = t.id 
            JOIN trips tr ON poi.trip_id = tr.id 
            ORDER BY poi.created_at DESC
        `);
        return rows;
    }

    async findAllWithLocation() {
        const [rows] = await this.connection.execute(`
            SELECT 
                poi.id,
                poi.name,
                poi.description,
                poi.created_at,
                t.name as type_name,
                tr.title as trip_title,
                l.latitude,
                l.longitude,
                l.address
            FROM points_of_interest poi 
            JOIN types t ON poi.type_id = t.id 
            JOIN trips tr ON poi.trip_id = tr.id 
            JOIN location l ON poi.id = l.poi_id
            ORDER BY poi.created_at DESC
        `);
        return rows;
    }

    async findAllWithLocationLimited(limit = 1000) {
        const [rows] = await this.connection.execute(`
            SELECT 
                poi.id,
                poi.name,
                poi.description,
                poi.created_at,
                t.name as type_name,
                tr.title as trip_title,
                l.latitude,
                l.longitude,
                l.address
            FROM points_of_interest poi 
            JOIN types t ON poi.type_id = t.id 
            JOIN trips tr ON poi.trip_id = tr.id 
            JOIN location l ON poi.id = l.poi_id
            ORDER BY poi.created_at DESC
            LIMIT ?
        `, [limit]);
        return rows;
    }

    async findById(id) {
        const [rows] = await this.connection.execute(`
            SELECT poi.*, t.name as type_name, tr.title as trip_title 
            FROM points_of_interest poi 
            JOIN types t ON poi.type_id = t.id 
            JOIN trips tr ON poi.trip_id = tr.id 
            WHERE poi.id = ?
        `, [id]);
        return rows[0];
    }

    async findByTripId(tripId) {
        const [rows] = await this.connection.execute(`
            SELECT poi.*, t.name as type_name 
            FROM points_of_interest poi 
            JOIN types t ON poi.type_id = t.id 
            WHERE poi.trip_id = ? 
            ORDER BY poi.created_at DESC
        `, [tripId]);
        return rows;
    }

    async findByTypeId(typeId) {
        const [rows] = await this.connection.execute(`
            SELECT poi.*, tr.title as trip_title 
            FROM points_of_interest poi 
            JOIN trips tr ON poi.trip_id = tr.id 
            WHERE poi.type_id = ? 
            ORDER BY poi.created_at DESC
        `, [typeId]);
        return rows;
    }

    async create(poiData) {
        const { trip_id, type_id, name, description } = poiData;
        const [result] = await this.connection.execute(
            'INSERT INTO points_of_interest (trip_id, type_id, name, description) VALUES (?, ?, ?, ?)',
            [trip_id, type_id, name, description]
        );
        return result.insertId;
    }

    async update(id, poiData) {
        const { type_id, name, description } = poiData;
        const [result] = await this.connection.execute(
            'UPDATE points_of_interest SET type_id = ?, name = ?, description = ? WHERE id = ?',
            [type_id, name, description, id]
        );
        return result.affectedRows;
    }

    async delete(id) {
        const [result] = await this.connection.execute('DELETE FROM points_of_interest WHERE id = ?', [id]);
        return result.affectedRows;
    }

    async findWithinBoundingBox(north, south, east, west, limit = 1000) {
        const [rows] = await this.connection.execute(`
            SELECT 
                poi.id,
                poi.name,
                poi.description,
                poi.created_at,
                t.name as type_name,
                tr.title as trip_title,
                l.latitude,
                l.longitude,
                l.address
            FROM points_of_interest poi 
            JOIN types t ON poi.type_id = t.id 
            JOIN trips tr ON poi.trip_id = tr.id 
            JOIN location l ON poi.id = l.poi_id
            WHERE l.latitude BETWEEN ? AND ? 
            AND l.longitude BETWEEN ? AND ?
            ORDER BY poi.created_at DESC
            LIMIT ?
        `, [south, north, west, east, limit]);
        return rows;
    }
}

module.exports = new PointOfInterest();