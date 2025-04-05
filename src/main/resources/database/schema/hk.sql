CREATE DATABASE hk;

CREATE TABLE hk.STUDENT (
	ID INT NOT NULL PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL,
    DOB date,
    GENDER char NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp,
    ARCHIVED datetime DEFAULT current_timestamp
);

CREATE TABLE hk.ATTENDANCE (
	ATTENDENCE_DATE DATE DEFAULT (current_date()),
    STUDENT_ID int,
    ATTENDANCE char NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp,
    ARCHIVED datetime DEFAULT current_timestamp
);


CREATE TABLE hk.SUBJECT (
	ID INT PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);


CREATE TABLE hk.EXAM (
	ID INT NOT NULL PRIMARY KEY,
    SESSION_YEAR YEAR NOT NULL,
    EXAM_NAME VARCHAR(50),
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp
);

CREATE TABLE hk.GRADES (
	ID INT PRIMARY KEY,
    EXAM_ID INT NOT NULL,
    STUDENT_ID INT NOT NULL,
    SUBJECT_ID INT NOT NULL,
    MARKS INT NOT NULL,
    CREATED datetime DEFAULT current_timestamp,
    UPDATED datetime DEFAULT current_timestamp,
    ARCHIVED datetime DEFAULT current_timestamp
);