.mode columns
.headers on
.nullvalue NULL
PRAGMA foreign_keys = ON;

-- Delete the tables if they already exist
drop table if exists Enroll;
drop table if exists Major;
drop table if exists Course;
drop table if exists Student;
drop table if exists Dept;

-- Create the schema for our tables
create TABLE Dept(
        deptID TEXT CHECK(length(deptID) < 5) PRIMARY KEY,
        deptName TEXT NOT NULL UNIQUE,
        building TEXT
);
create TABLE Student(
        studentID INTEGER PRIMARY KEY,
        studentName TEXT NOT NULL,
        class TEXT CHECK(class = 'Freshman' or class = 'Sophomore' or class = 'Junior' or class = 'Senior'),
        gpa REAL CHECK(0 <= gpa AND gpa <= 4)
);
create TABLE Major(
        StudentID INTEGER,
        major TEXT,
        PRIMARY KEY(studentID, major),
        FOREIGN KEY(studentID) REFERENCES student(studentID)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
        FOREIGN KEY(major) REFERENCES Dept(deptID)
                ON UPDATE CASCADE
                ON DELETE SET NULL
);
create TABLE Course(
        CourseNum INTEGER,
        deptID TEXT CHECK(length(deptID) < 5),
        CourseName TEXT,
        Location TEXT,
        meetDay TEXT,
        meetTime TEXT CHECK(meetTime >= '07:00' and meetTime < '17:00'),
        PRIMARY KEY(CourseNum,deptID),
        FOREIGN KEY(deptID) REFERENCES Dept(deptID)
                ON UPDATE CASCADE
                ON DELETE CASCADE
);
create TABLE Enroll(
        CourseNum INTEGER,
        deptID TEXT CHECK(length(deptID) < 5),
        StudentID INTEGER,
        PRIMARY KEY(CourseNum,deptID,StudentID),
        FOREIGN KEY(deptID,CourseNum) REFERENCES Course(deptID,CourseNum)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
        FOREIGN KEY(studentID) REFERENCES Student(studentID)
                ON UPDATE CASCADE
                ON DELETE CASCADE
);
