const express = require('express');
const router = express.Router();

// chatBot
router.use(require('./chatBot/chat'));
router.use(require('./chatBot/memory'));
router.use(require('./chatBot/session'));

// map
router.use(require('./map/location'));
router.use(require('./map/poiContact'));
router.use(require('./map/pointOfInterest'));
router.use(require('./map/trip'));
router.use(require('./map/type'));

// social
router.use(require('./social/comment'));
router.use(require('./social/image'));
router.use(require('./social/like'));

// user
router.use(require('./user/post'));
router.use(require('./user/user'));

module.exports = report;
