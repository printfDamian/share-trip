# How to setup the server?

## DataBase

### Create
Run the [`share-trip_v2.sql`](DB/share-trip_v2.sql) script in some mySQL service.
For development we used [**XAMP**](https://www.apachefriends.org/), a local mySQL and others program to launch local SQL servers.

### Add Data
Run the JavaScript file named [`loadDataDB.js`](DB/loadDataDB.js) using `node .\DB\loadDataDB` and wait until its finished.

*Done!*

## API Server

Run this commands inside [`\API`](API/):
- `npm install`
- `node app`

## Android APP

*W.I.P.*

# API Folder Structure

[`\api`](API/api/) > API routes
[`\config`](API/config/) > API and DataBase configurations
[`\models`](API/models/) > DataBase Model
[`\services`](API/services/) > Business Logic