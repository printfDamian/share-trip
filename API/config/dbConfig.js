const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '.env') });

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USERNAME,
    password: process.env.DB_PASSWORD,
    database: process.env.DB,
    port: process.env.DB_PORT ?? 3306,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
};

module.exports = dbConfig;