-- Turn on foreign keys
PRAGMA foreign_keys = ON;

-- Delete the tables if they already exist
drop table if exists Student;
drop table if exists Course;
drop table if exists Enroll;
drop table if exists Dept;
drop table if exists Major;

-- Create the schema for your tables below
CREATE TABLE Student(
    studentID INTEGER UNIQUE,
    studentName TEXT NOT NULL,
    class TEXT CHECK(class = 'Freshman' OR class = 'Sophomore' OR class = 'Junior' OR class = 'Senior'),
    gpa REAL CHECK(gpa IS NULL OR (gpa >= 0.0 AND gpa <= 4.0)),
    PRIMARY KEY (studentID)
    );

CREATE TABLE Major(
    studentID INTEGER,
    major TEXT,
    PRIMARY KEY(studentID, major)
    FOREIGN KEY (major) REFERENCES Dept(deptID)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE Course(
    CourseNum INTEGER,
    deptID TEXT,
    CourseName TEXT,
    Location TEXT,
    meetDay TEXT,
    meetTime TEXT CHECK(meetTime >= '07:00' AND meetTime <= '17:00'),
    PRIMARY KEY (deptID, CourseNum),
    FOREIGN KEY (deptID) REFERENCES Dept(deptID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Dept(
    deptID TEXT CHECK(length(deptID) <= 4),
    deptName TEXT UNIQUE CHECK(NOT NULL),
    building TEXT,
    PRIMARY KEY (deptID)
);

CREATE TABLE Enroll(
    CourseNum INTEGER,
    deptID TEXT,
    studentID INTEGER,
    PRIMARY KEY (CourseNum, deptID, studentID),
    FOREIGN KEY (deptID) REFERENCES Dept(deptID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
