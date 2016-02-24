DROP DATABASE IF EXISTS school;
CREATE DATABASE school;
USE school;

CREATE TABLE subject
       (id INT NOT NULL,
	subjectName VARCHAR(20) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE staff
       (id INT NOT NULL,
	staffName VARCHAR(20) NOT NULL,
	subjectID INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectID) REFERENCES subject(id));

CREATE TABLE class
       (id INT NOT NULL,
	className VARCHAR(20) NOT NULL,
	subjectID INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectID) REFERENCES subject(id));

CREATE TABLE day
       (id INT NOT NULL,
	dayName CHAR(9) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE classroom
       (id INT NOT NULL,
	roomName VARCHAR(10) NOT NULL,
	buildingName VARCHAR(10) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE period
       (id INT NOT NULL,
        day INT NOT NULL,
	startTime TIME NOT NULL,
	endTime TIME NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (day) REFERENCES day(id));

CREATE TABLE timetable 
       (id INT NOT NULL,
	classID INT NOT NULL,
	staffID INT NOT NULL,
	classroomID INT NOT NULL,
	periodID INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (classID) REFERENCES class(id),
	FOREIGN KEY (staffID) REFERENCES staff(id),
	FOREIGN KEY (classroomID) REFERENCES classroom(id),
	FOREIGN KEY (periodID) REFERENCES period(id));
