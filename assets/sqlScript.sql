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
	subjectID INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectID) REFERENCES subject(id));

CREATE TABLE class
       (id INT NOT NULL AUTO_INCREMENT,
	className VARCHAR(20) NOT NULL,
	subjectID INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectID) REFERENCES subject(id));

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

CREATE TABLE set
       (id INT NOT NULL AUTO_INCREMENT,
    setName VARCHAR(10),
    PRIMARY KEY (id));

CREATE TABLE subjectSet
       (id INT NOT NULL AUTO_INCREMENT,
    subjectId INT NOT NULL,
    setId INT NOT NULL,
    yearGroupId INT NOT NULL,
    FOREIGN KEY (subjectId) REFERENCES subject(id),
    FOREIGN KEY (setId) REFERENCES set(id));

CREATE TABLE timetable 
       (id INT NOT NULL AUTO_INCREMENT,
	classID INT NOT NULL,
	staffID INT NOT NULL,
	classroomID INT NOT NULL,
	periodID INT NOT NULL,
	subjectSetId INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (classID) REFERENCES class(id),
	FOREIGN KEY (staffID) REFERENCES staff(id),
	FOREIGN KEY (classroomID) REFERENCES classroom(id),
	FOREIGN KEY (subjectSetId) REFERENCES subjectSet(id),
	FOREIGN KEY (periodID) REFERENCES period(id));

