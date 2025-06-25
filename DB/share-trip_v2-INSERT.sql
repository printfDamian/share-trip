USE shareTrip;

-- Insert Users (3 users + existing ShareTripBot)
INSERT INTO users (name, email, password, active) VALUES
('John Traveler', 'john.traveler@email.com', '$2b$10$hashedpassword1', 1),
('Sarah Explorer', 'sarah.explorer@email.com', '$2b$10$hashedpassword2', 1),
('Mike Adventurer', 'mike.adventurer@email.com', '$2b$10$hashedpassword3', 1);

-- Insert Trips (4 trips)
INSERT INTO trips (user_id, title, description, start_date, end_date, location) VALUES
(2, 'European Adventure', 'Exploring the historic cities of Europe', '2024-06-15', '2024-06-30', 'Europe'),
(3, 'Tokyo Discovery', 'Modern culture meets ancient traditions', '2024-08-10', '2024-08-20', 'Tokyo, Japan'),
(4, 'California Road Trip', 'From San Francisco to Los Angeles', '2024-09-05', '2024-09-15', 'California, USA'),
(2, 'Mediterranean Cruise', 'Island hopping in the Mediterranean Sea', '2024-10-01', '2024-10-14', 'Mediterranean');

-- Insert Points of Interest (using existing types)
INSERT INTO points_of_interest (trip_id, type_id, name, description) VALUES
(1, 1, 'Cafe de Flore', 'Historic Parisian cafe famous for its literary clientele'),
(1, 3, 'Eiffel Tower', 'Iconic iron tower and symbol of Paris'),
(2, 1, 'Tsukiji Outer Market', 'Fresh sushi and traditional Japanese breakfast'),
(2, 8, 'Senso-ji Temple', 'Ancient Buddhist temple in Asakusa district'),
(3, 3, 'Golden Gate Bridge', 'Famous suspension bridge in San Francisco'),
(4, 7, 'Santorini Beaches', 'Beautiful volcanic beaches with crystal clear waters');

-- Insert Locations for POIs
INSERT INTO location (poi_id, latitude, longitude, address) VALUES
(1, 48.8566, 2.3522, '172 Boulevard Saint-Germain, 75006 Paris, France'),
(2, 48.8584, 2.2945, 'Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France'),
(3, 35.6762, 139.7702, '5 Chome-2-1 Tsukiji, Chuo City, Tokyo 104-0045, Japan'),
(4, 35.7148, 139.7967, '2 Chome-3-1 Asakusa, Taito City, Tokyo 111-0032, Japan'),
(5, 37.8199, -122.4783, 'Golden Gate Bridge, San Francisco, CA 94129, USA'),
(6, 36.4618, 25.3753, 'Santorini, Greece');

-- Insert Posts (10 posts)
INSERT INTO posts (user_id, trip_id, location_id, title, content) VALUES
(2, 1, 1, 'Amazing Parisian Morning', 'Started my day at the famous Cafe de Flore. The atmosphere is incredible and the coffee is perfect!'),
(2, 1, 2, 'Eiffel Tower at Sunset', 'Nothing beats the view of the Eiffel Tower during golden hour. Absolutely magical experience!'),
(3, 2, 3, 'Sushi Breakfast in Tokyo', 'Had the most authentic sushi breakfast at Tsukiji Market. Fresh fish and amazing flavors!'),
(3, 2, 4, 'Spiritual Moment at Senso-ji', 'The ancient Senso-ji Temple is a peaceful oasis in the bustling city of Tokyo.'),
(4, 3, 5, 'Golden Gate Bridge Hike', 'Hiked across the Golden Gate Bridge today. The views of San Francisco Bay are breathtaking!'),
(2, 4, 6, 'Santorini Paradise', 'The beaches here are absolutely stunning. Crystal clear water and volcanic sand!'),
(3, NULL, NULL, 'Travel Planning Tips', 'Here are my top 5 tips for planning your next international trip. Always research local customs first!'),
(4, NULL, NULL, 'Best Travel Apps', 'Sharing my favorite travel apps that have made my journeys so much easier and more organized.'),
(2, 1, NULL, 'European Train Travel', 'The train system in Europe is amazing! So convenient to hop between cities.'),
(4, 3, NULL, 'California Coast Drive', 'Driving along the Pacific Coast Highway is one of the most scenic routes in America.');

-- Insert Images for Posts (5 out of 10 posts have images)
INSERT INTO images (post_id, url) VALUES
(1, 'image1.jpg'),
(2, 'image2.jpg'),
(3, 'image3.jpg'),
(5, 'image4.jpg'),
(6, 'image5.jpg');

-- Insert Comments (10 comments)
INSERT INTO comments (post_id, user_id, content) VALUES
(1, 3, 'I love that cafe! Did you try their croissants?'),
(1, 4, 'Cafe de Flore is on my bucket list for my next Paris trip!'),
(2, 3, 'Your photo timing was perfect! The lighting is amazing.'),
(3, 2, 'Tokyo sushi is unmatched. Which was your favorite roll?'),
(4, 4, 'Temples in Japan are so peaceful. Did you participate in any ceremonies?'),
(5, 2, 'That hike looks challenging but so worth it!'),
(6, 3, 'Santorini is my dream destination. How long did you stay?'),
(7, 4, 'Great tips! I especially agree about researching local customs.'),
(8, 2, 'Which travel app do you recommend most for international trips?'),
(9, 4, 'European trains are so efficient compared to other places.');

-- Insert Likes (10 likes)
INSERT INTO likes (post_id, user_id) VALUES
(1, 3),
(1, 4),
(2, 3),
(3, 2),
(3, 4),
(4, 2),
(5, 2),
(5, 3),
(6, 3),
(7, 4);

-- Insert Chat Sessions (for chatbot functionality)
INSERT INTO sessions (user_id, dailyLimit, refreshLimit) VALUES
(2, 25, '2024-12-19 10:00:00'),
(3, 25, '2024-12-19 14:30:00'),
(4, 25, '2024-12-19 16:45:00');

-- Insert Memory entries (chatbot memory)
INSERT INTO memory (user_id, notes) VALUES
(2, 'User prefers European destinations, loves historical sites and cafes. Interested in cultural experiences.'),
(3, 'User enjoys Asian cuisine and spiritual/cultural sites. Prefers authentic local experiences over tourist traps.'),
(4, 'User loves outdoor activities and scenic drives. Prefers road trips and nature-based adventures.');

-- Insert Chat messages (chatbot conversations)
INSERT INTO chats (session_id, sender_id, message) VALUES
(1, 2, 'Hi! Can you help me plan my next trip to Italy?'),
(1, 1, 'Of course! Based on your travel history, I see you love European culture and historical sites. What cities in Italy interest you most?'),
(1, 2, 'I was thinking Rome and Florence. What are the must-see places?'),
(1, 1, 'Great choices! For Rome, I recommend the Colosseum, Vatican City, and Trevi Fountain. Florence is perfect for art lovers - the Uffizi Gallery and Ponte Vecchio are essential visits.'),
(2, 3, 'What are some good vegetarian restaurants in Tokyo?'),
(2, 1, 'Tokyo has amazing vegetarian options! I recommend Ain Soph for vegan ramen, T''s for healthy plates, and Morpho Cafe for organic dishes. Would you like specific locations?'),
(2, 3, 'Yes, please! Especially near Shibuya area.'),
(2, 1, 'Near Shibuya, try Ain Soph Ginza (short train ride) or look for Buddhist temple restaurants which often serve excellent vegetarian meals.'),
(3, 4, 'I want to plan a road trip on the West Coast. Any suggestions?'),
(3, 1, 'Perfect! Based on your California experience, I recommend extending to Oregon and Washington. The Pacific Coast Highway continues north with stunning views, redwood forests, and charming coastal towns.');
