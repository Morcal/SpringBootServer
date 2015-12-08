
CREATE SCHEMA PWS;

CREATE TABLE PWS.Session(
    id VARCHAR(64) PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(64) NOT NULL,
    nas_id VARCHAR(32) NOT NULL,
    ip VARCHAR(64) NOT NULL,
    mac VARCHAR(32),
    start_date DATE NOT NULL);
