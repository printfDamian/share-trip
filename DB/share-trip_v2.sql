DROP DATABASE IF EXISTS shareTrip;
CREATE DATABASE shareTrip;
USE shareTrip;

CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_users_email (email),
    INDEX idx_users_active (active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS sessions (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    dailyLimit INT NOT NULL DEFAULT 25,
    refreshLimit DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 7 DAY),
    PRIMARY KEY (id),
    INDEX idx_sessions_user (user_id),
    INDEX idx_sessions_expires (expires_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS chats (
    id INT NOT NULL AUTO_INCREMENT,
    session_id INT NOT NULL,
    sender_id INT NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_chats_session (session_id),
    INDEX idx_chats_sender (sender_id),
    INDEX idx_chats_created (created_at),
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS memory (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    notes TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_memory_user (user_id),
    INDEX idx_memory_created (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS trips (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    location VARCHAR(100) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_trips_user (user_id),
    INDEX idx_trips_dates (start_date, end_date),
    INDEX idx_trips_location (location),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_trip_dates CHECK (
        start_date IS NULL
        OR end_date IS NULL
        OR start_date <= end_date
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS types (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    PRIMARY KEY (id),
    INDEX idx_types_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS points_of_interest (
    id INT NOT NULL AUTO_INCREMENT,
    trip_id INT NOT NULL,
    type_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_poi_trip (trip_id),
    INDEX idx_poi_type (type_id),
    INDEX idx_poi_name (name),
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES types(id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS location (
    id INT NOT NULL AUTO_INCREMENT,
    poi_id INT NOT NULL UNIQUE,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    address VARCHAR(255) NULL,
    PRIMARY KEY (id),
    INDEX idx_location_coordinates (latitude, longitude),
    FOREIGN KEY (poi_id) REFERENCES points_of_interest(id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS posts (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    trip_id INT NULL,
    location_id INT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_posts_user (user_id),
    INDEX idx_posts_trip (trip_id),
    INDEX idx_posts_location (location_id),
    INDEX idx_posts_created (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE
    SET NULL,
        FOREIGN KEY (location_id) REFERENCES location(id) ON DELETE
    SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS images (
    id INT NOT NULL AUTO_INCREMENT,
    post_id INT NULL,
    user_id INT NULL,
    poi_id INT NULL,
    url VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_images_post (post_id),
    INDEX idx_images_user (user_id),
    INDEX idx_images_poi (poi_id),
    INDEX idx_images_created (created_at),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (poi_id) REFERENCES points_of_interest(id) ON DELETE CASCADE,
    -- Ensure image belongs to only one entity type
    CONSTRAINT chk_image_entity CHECK (
        (
            post_id IS NOT NULL
            AND user_id IS NULL
            AND poi_id IS NULL
        )
        OR (
            post_id IS NULL
            AND user_id IS NOT NULL
            AND poi_id IS NULL
        )
        OR (
            post_id IS NULL
            AND user_id IS NULL
            AND poi_id IS NOT NULL
        )
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS comments (
    id INT NOT NULL AUTO_INCREMENT,
    post_id INT NULL,
    poi_id INT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_comments_post (post_id),
    INDEX idx_comments_poi (poi_id),
    INDEX idx_comments_user (user_id),
    INDEX idx_comments_created (created_at),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (poi_id) REFERENCES points_of_interest(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- Ensure comment belongs to either a post OR a POI
    CONSTRAINT chk_comment_entity CHECK (
        (
            post_id IS NOT NULL
            AND poi_id IS NULL
        )
        OR (
            post_id IS NULL
            AND poi_id IS NOT NULL
        )
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS likes (
    id INT NOT NULL AUTO_INCREMENT,
    post_id INT NULL,
    comment_id INT NULL,
    poi_id INT NULL,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY ux_likes_post (post_id, user_id),
    UNIQUE KEY ux_likes_comment (comment_id, user_id),
    UNIQUE KEY ux_likes_poi (poi_id, user_id),
    INDEX idx_likes_post (post_id),
    INDEX idx_likes_comment (comment_id),
    INDEX idx_likes_poi (poi_id),
    INDEX idx_likes_user (user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    FOREIGN KEY (poi_id) REFERENCES points_of_interest(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- Ensure like belongs to only one entity type
    CONSTRAINT chk_like_entity CHECK (
        (
            post_id IS NOT NULL
            AND comment_id IS NULL
            AND poi_id IS NULL
        )
        OR (
            post_id IS NULL
            AND comment_id IS NOT NULL
            AND poi_id IS NULL
        )
        OR (
            post_id IS NULL
            AND comment_id IS NULL
            AND poi_id IS NOT NULL
        )
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS poi_contacts (
    poi_id INT NOT NULL,
    contact_poi_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (poi_id, contact_poi_id),
    FOREIGN KEY (poi_id) REFERENCES points_of_interest(id) ON DELETE CASCADE,
    FOREIGN KEY (contact_poi_id) REFERENCES points_of_interest(id) ON DELETE CASCADE,
    -- Prevent self-referencing contacts
    CONSTRAINT chk_poi_contact_different CHECK (poi_id != contact_poi_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Insert default POI types
INSERT IGNORE INTO types (name, description)
VALUES (
        'Restaurant',
        'Dining establishments and food venues'
    ),
    ('Hotel', 'Accommodation and lodging facilities'),
    (
        'Attraction',
        'Tourist attractions and landmarks'
    ),
    ('Transport', 'Transportation hubs and services'),
    (
        'Shopping',
        'Shopping centers and retail locations'
    ),
    (
        'Entertainment',
        'Entertainment venues and activities'
    ),
    (
        'Nature',
        'Parks, beaches, and natural attractions'
    ),
    (
        'Culture',
        'Museums, galleries, and cultural sites'
    );

-- Insert ShareTripBot user
INSERT IGNORE INTO users (name, email, password, active)
VALUES (
        'ShareTripBot',
        'bot@sharetrip.local',
        '$2b$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX',
        -- replace with actual bcrypt hash
        1
    );

-- Create event to clean up expired sessions
/*DELIMITER $$ CREATE EVENT IF NOT EXISTS cleanup_expired_sessions ON SCHEDULE EVERY 1 DAY STARTS CURRENT_TIMESTAMP DO BEGIN
DELETE FROM sessions
WHERE expires_at < CURRENT_TIMESTAMP;
END $$ DELIMITER;*/