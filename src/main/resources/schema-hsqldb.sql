DROP TABLE user
IF EXISTS;
CREATE TABLE user (
  id               VARCHAR(255),
  photo_uri        VARCHAR(255),
  login            VARCHAR(255),
  email            VARCHAR(255),
  status           VARCHAR(255),
  status_timestamp TIMESTAMP
);