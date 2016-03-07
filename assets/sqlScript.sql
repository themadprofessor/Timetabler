DROP DATABASE IF EXISTS school;
CREATE DATABASE school;
USE school;

CREATE TABLE subject
       (id INT NOT NULL AUTO_INCREMENT,
	subjectName VARCHAR(20) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE staff
       (id INT NOT NULL AUTO_INCREMENT,
	staffName VARCHAR(20) NOT NULL,
	subjectId INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectId) REFERENCES subject(id));

CREATE TABLE class
       (id INT NOT NULL AUTO_INCREMENT,
	className VARCHAR(20) NOT NULL,
	subjectId INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectId) REFERENCES subject(id));

CREATE TABLE dayOfWeek
       (id INT NOT NULL AUTO_INCREMENT,
	dayOfWeek CHAR(9) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE classroom
       (id INT NOT NULL AUTO_INCREMENT,
	roomName VARCHAR(10) NOT NULL,
	buildingName VARCHAR(10) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE period
       (id INT NOT NULL AUTO_INCREMENT,
        dayId INT NOT NULL,
	startTime TIME NOT NULL,
	endTime TIME NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (dayId) REFERENCES dayOfWeek(id));

CREATE TABLE learningSet
       (id INT NOT NULL AUTO_INCREMENT,
    setName VARCHAR(10),
    PRIMARY KEY (id));

CREATE TABLE subjectSet
       (id INT NOT NULL AUTO_INCREMENT,
    subjectId INT NOT NULL,
    setId INT NOT NULL,
    yearGroupId INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (subjectId) REFERENCES subject(id),
    FOREIGN KEY (setId) REFERENCES learningSet(id));

CREATE TABLE timetable 
       (id INT NOT NULL AUTO_INCREMENT,
	classId INT NOT NULL,
	staffId INT,
	classroomId INT NOT NULL,
	periodId INT NOT NULL,
	subjectSetId INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (classId) REFERENCES class(id),
	FOREIGN KEY (staffId) REFERENCES staff(id),
	FOREIGN KEY (classroomId) REFERENCES classroom(id),
	FOREIGN KEY (subjectSetId) REFERENCES subjectSet(id),
	FOREIGN KEY (periodId) REFERENCES period(id));

INSERT INTO dayOfWeek (dayOfWeek) VALUES ('Monday'),('Tuesday'),('Wednesday'),('Thursday'),('Friday');

