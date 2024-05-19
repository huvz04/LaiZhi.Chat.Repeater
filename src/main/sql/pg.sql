
CREATE TABLE image_file (
    id BIGSERIAL PRIMARY KEY,
    md5 VARCHAR(255),
    qq VARCHAR(255),
    count BIGINT,
    about VARCHAR(255),
    type VARCHAR(255),
    url VARCHAR(255)
);

-- GroupDetail 表
CREATE TABLE group_detail (
    id VARCHAR(255) PRIMARY KEY,
    avatar BYTEA,
    name VARCHAR(255),
    total INTEGER,
    gallery_number INTEGER
);

