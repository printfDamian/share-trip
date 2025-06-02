const express = require('express');
const router = express.Router();

// chatBot
router.use('/chatbot', require('./chatBot/chat'));
router.use('/chatbot', require('./chatBot/memory'));
router.use('/chatbot', require('./chatBot/session'));

// map
router.use('/map', require('./map/location'));
router.use('/map', require('./map/poiContact'));
router.use('/map', require('./map/pointOfInterest'));
router.use('/map', require('./map/trip'));
router.use('/map', require('./map/type'));

// social
router.use('/social', require('./social/comment'));
router.use('/social', require('./social/image'));
router.use('/social', require('./social/like'));

// user
router.use('/users', require('./user/post'));
router.use('/users', require('./user/user'));

module.exports = router;
