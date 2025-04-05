CREATE DATABASE main;

CREATE TABLE main.STUDENT (
	ID INT NOT NULL PRIMARY KEY auto_increment,
    NAME VARCHAR(50) NOT NULL,
    DOB date,
    GENDER char NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);

CREATE TABLE main.ATTENDANCE (
	ATTENDENCE_DATE DATE DEFAULT (current_date()),
    STUDENT_ID int,
    ATTENDANCE char NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);


CREATE TABLE main.SUBJECT (
	ID INT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(50) NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);


CREATE TABLE main.EXAM (
	ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    SESSION_YEAR YEAR NOT NULL,
    EXAM_NAME VARCHAR(50),
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);

CREATE TABLE main.GRADES (
	ID INT PRIMARY KEY AUTO_INCREMENT,
    EXAM_ID INT NOT NULL,
    STUDENT_ID INT NOT NULL,
    SUBJECT_ID INT NOT NULL,
    MARKS INT NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);


