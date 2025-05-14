const express = require("express");
const app = express();
const path = require("path");
require("dotenv").config();
const morgan = require('morgan');

// === App Config ===

const port = process.env.PORT ?? 8800;

// Set EJS as the view engine
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Middleware to parse JSON and URL-encoded data
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Middleware to log requests
app.use(morgan('dev'));

// Routes & EndPoints
app.use(require('./routes/routes'));

app.listen(port);
console.log("Server running on http://localhost:" + port);