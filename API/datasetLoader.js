const fs = require('fs');
const csv = require('csv-parser');
const mysql = require('mysql2/promise');
const dbConfig = require('./config/dbConfig');


const typeMapping = {
    'restaurant': 1,
    'hotel': 2,
    'attraction': 3,
    'transport': 4,
    'shopping': 5,
    'entertainment': 6,
    'nature': 7,
    'culture': 8,
    'museum': 8,
    'park': 7,
    'beach': 7,
    'monument': 3,
    'gallery': 8,
    'theater': 6,
    'stadium': 6,
    'church': 8,
    'default': 3
};

async function processBatch(connection, batchData) {
    try {
        await connection.beginTransaction();

        for (const poi of batchData) {
            try {

                const [result] = await connection.execute(
                    `INSERT INTO points_of_interest (trip_id, type_id, name, description) 
                     VALUES (?, ?, ?, ?)`,
                    [poi.trip_id, poi.type_id, poi.name, poi.description]
                );

                const poiId = result.insertId;

                await connection.execute(
                    `INSERT INTO location (poi_id, latitude, longitude, address) 
                     VALUES (?, ?, ?, ?)`,
                    [poiId, poi.latitude, poi.longitude, poi.address]
                );

                if (poi.url && (poi.url.includes('image') || poi.url.includes('.jpg') || poi.url.includes('.png'))) {
                    await connection.execute(
                        `INSERT INTO images (poi_id, url) VALUES (?, ?)`,
                        [poiId, poi.url]
                    );
                }

            } catch (error) {
                console.error(`Error inserting POI "${poi.name}": ${error.message}`);
                throw error;
            }
        }

        await connection.commit();
        return batchData.length;

    } catch (error) {
        await connection.rollback();
        console.error('Batch processing error:', error);
        throw error;
    }
}

async function importPOIData() {
    let connection;

    try {

        connection = await mysql.createConnection(dbConfig);
        console.log('Connected to database');

        const [trips] = await connection.execute('SELECT id FROM trips');
        const tripIds = trips.map(trip => trip.id);

        if (tripIds.length === 0) {
            console.error('No trips found in database. Please add some trips first.');
            return;
        }

        let processedCount = 0;
        let errorCount = 0;
        const batchSize = 100;
        let batch = [];
        let allData = [];

        console.log('Reading CSV file...');

    
        await new Promise((resolve, reject) => {
            fs.createReadStream('../DB/pointsOfInterestDataSet.csv')
                .pipe(csv())
                .on('data', (row) => {
                    try {
                  
                        if (!row.title || !row.latitude || !row.longitude) {
                            return;
                        }

                        const lat = parseFloat(row.latitude);
                        const lng = parseFloat(row.longitude);

          
                        if (isNaN(lat) || isNaN(lng) || lat < -90 || lat > 90 || lng < -180 || lng > 180) {
                            return;
                        }

                        
                        const typeKey = row.type ? row.type.toLowerCase() : 'default';
                        const typeId = typeMapping[typeKey] || typeMapping['default'];

                        
                        const tripId = tripIds[Math.floor(Math.random() * tripIds.length)];

                        const poiData = {
                            trip_id: tripId,
                            type_id: typeId,
                            name: row.title.substring(0, 150),
                            description: row.description || row.alt || null,
                            latitude: lat,
                            longitude: lng,
                            address: row.address || null,
                            phone: row.phone || null,
                            email: row.email || null,
                            url: row.url || null,
                            hours: row.hours || null,
                            price: row.price || null,
                            wifi: row.wifi === 'true' || row.wifi === '1' ? 1 : 0,
                            accessibility: row.accessibility === 'true' || row.accessibility === '1' ? 1 : 0
                        };

                        allData.push(poiData);

                    } catch (error) {
                        errorCount++;
                        console.error(`Error processing row: ${error.message}`);
                    }
                })
                .on('end', () => {
                    console.log(`Finished reading CSV. Found ${allData.length} valid records.`);
                    resolve();
                })
                .on('error', (error) => {
                    console.error('Error reading CSV file:', error);
                    reject(error);
                });
        });

        
        console.log('Starting database insertion...');

        for (let i = 0; i < allData.length; i += batchSize) {
            const batch = allData.slice(i, i + batchSize);

            try {
                const batchProcessed = await processBatch(connection, batch);
                processedCount += batchProcessed;

                if (processedCount % 1000 === 0) {
                    console.log(`Processed ${processedCount} POIs...`);
                }

            } catch (error) {
                errorCount += batch.length;
                console.error(`Failed to process batch starting at index ${i}`);
            }
        }

        console.log(`\nImport completed!`);
        console.log(`Total processed: ${processedCount}`);
        console.log(`Total errors: ${errorCount}`);

    } catch (error) {
        console.error('Import error:', error);
    } finally {
        if (connection) {
            await connection.end();
            console.log('Database connection closed.');
        }
    }
}


async function addAdditionalTypes() {
    let connection;

    try {
        connection = await mysql.createConnection(dbConfig);

        const additionalTypes = [
            ['Museum', 'Museums and historical sites'],
            ['Park', 'Parks and recreational areas'],
            ['Beach', 'Beaches and waterfront areas'],
            ['Monument', 'Monuments and landmarks'],
            ['Gallery', 'Art galleries and exhibitions'],
            ['Theater', 'Theaters and performance venues'],
            ['Stadium', 'Sports stadiums and arenas'],
            ['Hospital', 'Medical facilities'],
            ['School', 'Educational institutions'],
            ['Church', 'Religious buildings and sites']
        ];

        for (const [name, description] of additionalTypes) {
            try {
                await connection.execute(
                    'INSERT IGNORE INTO types (name, description) VALUES (?, ?)',
                    [name, description]
                );
            } catch (error) {
                console.error(`Error adding type ${name}:`, error.message);
            }
        }

        console.log('Additional types added successfully.');

    } catch (error) {
        console.error('Error adding additional types:', error);
    } finally {
        if (connection) {
            await connection.end();
        }
    }
}


async function main() {
    console.log('Starting POI data import...');
    console.log('Adding additional types first...');

    await addAdditionalTypes();

    console.log('Starting main import...');
    await importPOIData();
}

main().catch(console.error);
