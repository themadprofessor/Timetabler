DROP DATABASE IF EXISTS school;
CREATE DATABASE school;
USE school;

CREATE TABLE subject
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	subjectName VARCHAR(20) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE staff
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	staffName VARCHAR(20) NOT NULL,
	subjectId INT NOT NULL,
	hoursPerWeek INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (subjectId) REFERENCES subject(id));

CREATE TABLE dayOfWeek
       (id INT UNSIGNED NOT NULL,
	dayOfWeek CHAR(9) NOT NULL,
	PRIMARY KEY (id));

CREATE TABLE building
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    buildingName VARCHAR(10) NOT NULL,
    PRIMARY KEY (id));

CREATE TABLE classroom
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	roomName VARCHAR(10) NOT NULL,
	buildingId INT NULL,
	subjectId INT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (buildingId) REFERENCES building(id),
	FOREIGN KEY (subjectId) REFERENCES subject(id));

CREATE TABLE period
       (id INT UNSIGNED UNSIGNED NOT NULL AUTO_INCREMENT,
        dayId INT NOT NULL,
	startTime TIME NOT NULL,
	endTime TIME NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (dayId) REFERENCES dayOfWeek(id));

CREATE TABLE learningSet
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    setName VARCHAR(10) NOT NULL,
    PRIMARY KEY (id));

CREATE TABLE schoolYear
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    schoolYearName VARCHAR(8) NOT NULL,
    PRIMARY KEY (id));

CREATE TABLE subjectSet
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    subjectId INT NOT NULL,
    setId INT NOT NULL,
    schoolYearId INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (subjectId) REFERENCES subject(id),
    FOREIGN KEY (setId) REFERENCES learningSet(id),
    FOREIGN KEY (schoolYearId) REFERENCES schoolYear(id));

CREATE TABLE distance
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    startRoomId INT NOT NULL,
    endRoomId INT NOT NULL,
    distance INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (startRoomId) REFERENCES classroom(id),
    FOREIGN KEY (endRoomId) REFERENCES classroom(id));

CREATE TABLE lessonPlan
       (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	staffId INT NULL,
	classroomId INT NULL,
	periodId INT NOT NULL,
	subjectSetId INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (staffId) REFERENCES staff(id),
	FOREIGN KEY (classroomId) REFERENCES classroom(id),
	FOREIGN KEY (subjectSetId) REFERENCES subjectSet(id),
	FOREIGN KEY (periodId) REFERENCES period(id));

INSERT INTO dayOfWeek (id,dayOfWeek) VALUES (1,'Monday'),(2,'Tuesday'),(3,'Wednesday'),(4,'Thursday'),(5,'Friday');
INSERT INTO period (id,dayId,startTime,endTime) VALUES
    (1,1,'9:10:00','10:10:00'),(2,1,'10:10:00','11:10:00'),(3,1,'11:30:00','12:30:00'),(4,1,'1:30 PM','2:30 PM'),(5,1,'2:30 PM','3:30 PM'),(6,1,'3:30 PM','4:30PM'),
    (7,2,'9:10:00','10:10:00'),(8,2,'10:10:00','11:10:00'),(9,2,'11:30:00','12:30:00'),(10,2,'1:30 PM','2:30 PM'),(11,2,'2:30 PM','3:30 PM'),(12,2,'3:30 PM','4:30PM'),
    (13,3,'9:10:00','10:10:00'),(14,3,'10:10:00','11:10:00'),(15,3,'11:30:00','12:30:00'),(16,3,'1:30 PM','2:30 PM'),(17,3,'2:30 PM','3:30 PM'),(18,3,'3:30 PM','4:30PM'),
    (19,4,'9:10:00','10:10:00'),(20,4,'10:10:00','11:10:00'),(21,4,'11:30:00','12:30:00'),(22,4,'1:30 PM','2:30 PM'),(23,4,'2:30 PM','3:30 PM'),(24,4,'3:30 PM','4:30PM'),
    (25,5,'9:10:00','10:10:00'),(26,5,'10:10:00','11:10:00'),(27,5,'11:30:00','12:30:00'),(28,5,'1:30 PM','2:30 PM'),(29,5,'2:30 PM','3:30 PM'),(30,5,'3:30 PM','4:30PM');

