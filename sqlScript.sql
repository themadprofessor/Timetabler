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
CREATE TABLE lesson 
       (id INT NOT NULL,
	classID INT NOT NULL,
	staffID INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (classID) REFERENCES class(id),
	FOREIGN KEY (staffID) REFERENCES staff(id));
