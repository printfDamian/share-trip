const express = require('express');
const router = express.Router();

try {
    // chatBot
    router.use('/chatbot', require('./chatBot/chats'));
    router.use('/chatbot', require('./chatBot/memorys'));
    router.use('/chatbot', require('./chatBot/sessions'));

    // map
    router.use('/map', require('./map/locations'));
    router.use('/map', require('./map/poiContacts'));
    router.use('/map', require('./map/pointOfInterests'));
    router.use('/map', require('./map/trips'));
    router.use('/map', require('./map/types'));

    // social
    router.use('/social', require('./social/comments'));
    router.use('/social', require('./social/images'));
    router.use('/social', require('./social/likes'));

    // user
    router.use('/users', require('./user/posts'));
    router.use('/users', require('./user/users'));

} catch (error) {
    
}

module.exports = router;
