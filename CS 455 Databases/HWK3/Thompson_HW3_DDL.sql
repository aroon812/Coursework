-- Turn on foreign keys
PRAGMA foreign_keys = ON;

-- Delete the tables if they already exist
drop table if exists Student;
drop table if exists Course;
drop table if exists Enroll;
drop table if exists Dept;

-- Create the schema for your tables below
CREATE TABLE Student(
    studentID INTEGER,
    studentName TEXT

    );