const express = require('express');
const app = express();
const path = require('path');
require('dotenv').config({ path: 'config/.env' });
const morgan = require('morgan');

// === App Config ===

const port = process.env.API_PORT ?? 8800;

// Set EJS as the view engine
// app.set('view engine', 'ejs');
// app.set('views', path.join(__dirname, 'views'));
app.use('/images', express.static('images'))


// Middleware to parse JSON and URL-encoded data
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Middleware to log requests
app.use(morgan('dev'));

// Routes & EndPoints
app.use('/api', require('./api/routes'));

app.listen(port);
console.log('Server running on http://localhost:' + port);
