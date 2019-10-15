-- Q0: This is just an example showing the format I expect.
-- This query retrieves every student in the database. It projects
-- only their ID and name.
SELECT studentID,name
FROM student;


-- Q1: Get all courses being taught by the MATH department that start in the afternoon.
select * from Course where deptID = 'MATH' AND meetTime >= '12:00';

--attributes out of order
-- Q2: Return David's course schedule.
SELECT * from Course NATURAL JOIN 
(SELECT deptID, CourseNum from (SELECT studentID from Student where studentName = 'David')
NATURAL JOIN Enroll);

-- Q3: Finds the average GPA for each of the class ranks (freshman, sophomore, junior, senior).
-- It projects each class and the average gpa of the associated class.
select class, avg(gpa) as 'ClassGPA'Get a list of all students who are still undeclared
from Student 
group by class;

-- Q4: Identifies all students who have a lower GPA than the average of his/her respective class rank.
select * from Student natural join (select class, avg(gpa) as 'ClassGPA'
from Student 
group by class) where gpa < ClassGPA
order by class, studentName;

-- Q5: Gets a list of all students who are still undeclared. 
-- This query projects the student IDs along with their names.
select studentID, studentName 
from Student where studentID not in (select studentID from Major)
order by studentID;
