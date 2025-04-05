CREATE SCHEMA core;

CREATE TABLE core.USER (
	ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(50) NOT NULL,
    PASSWORD VARCHAR(50) NOT NULL,
    CREATED DATETIME DEFAULT current_timestamp,
    UPDATED DATETIME DEFAULT current_timestamp
);

CREATE TABLE core.ROLE (
	ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    ROLE_NAME VARCHAR(50) NOT NULL,
    CREATED DATETIME DEFAULT current_timestamp,
    UPDATED DATETIME DEFAULT current_timestamp
);


CREATE TABLE core.PERMISSION (
	ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	USER_ID INT NOT NULL,
    ROLE_NAME VARCHAR(50) NOT NULL,
    CREATED DATETIME DEFAULT current_timestamp,
    UPDATED DATETIME DEFAULT current_timestamp,
    FOREIGN KEY(USER_ID) REFERENCES core.USER(ID) 
);


CREATE TABLE core.GEOLOCATION (
	ID INT NOT NULL PRIMARY KEY auto_increment,
	LOCATION VARCHAR(50),
    CREATED DATETIME DEFAULT current_timestamp
);


CREATE TABLE core.CONFIGURATION (
	ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    TABLE_NAME VARCHAR(30) NOT NULL,
    CONFIGURATION_TYPE VARCHAR(30) NOT NULL,
    YEARS INT UNSIGNED,
    MONTHS INT UNSIGNED,
    WEEKS INT UNSIGNED,
    DAYS INT UNSIGNED,
    HOURS INT UNSIGNED,
    MINUTES INT UNSIGNED,
    CREATED DATETIME DEFAULT current_timestamp,
    UPDATED DATETIME DEFAULT current_timestamp
);