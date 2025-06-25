const express = require('express');
const router = express.Router();

const { requiresToken } = require('../../services/authentication/token');
const PointOfInterest = require('../../models/map/pointOfInterestModel');

// GET - /api/map/markers - Returns all POI markers in GeoJSON format
router.get('/markers', requiresToken, async (req, res) => {
    try {
        const limit = parseInt(req.query.limit) || 1000;


        const { north, south, east, west } = req.query;

        let pois;
        if (north && south && east && west) {

            pois = await PointOfInterest.findWithinBoundingBox(
                parseFloat(north),
                parseFloat(south),
                parseFloat(east),
                parseFloat(west),
                limit
            );
        } else {

            pois = await PointOfInterest.findAllWithLocationLimited(limit);
        }


        const validPois = pois.filter(poi =>
            poi.latitude && poi.longitude &&
            !isNaN(parseFloat(poi.latitude)) &&
            !isNaN(parseFloat(poi.longitude))
        );

        console.log(`Found ${pois.length} POIs, ${validPois.length} with valid coordinates`);

        // Convert to GeoJSON format
        const geoJson = {
            type: "FeatureCollection",
            features: validPois.map(poi => ({
                type: "Feature",
                properties: {
                    id: poi.id,
                    title: poi.name || "",
                    description: poi.description ? poi.description.replace(/"/g, '\\"').replace(/\n/g, ' ').replace(/\r/g, '') : "",
                    type: poi.type_name || "",
                    trip_title: poi.trip_title || "",
                    address: poi.address || ""
                },
                geometry: {
                    type: "Point",
                    coordinates: [parseFloat(poi.longitude), parseFloat(poi.latitude)]
                }
            }))
        };

        res.json({
            success: true,
            data: geoJson
        });

    } catch (error) {
        console.error('Error fetching POI markers:', error);
        res.status(500).json({
            success: false,
            message: "Internal server error"
        });
    }
});

module.exports = router;
