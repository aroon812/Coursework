-- Q0: This is just an example showing the format I expect.
-- This query retrieves every student in the database. It projects
-- only their ID and name.
SELECT studentID,name
FROM student;

-- Q1: Get all courses being taught by the MATH department that start in the afternoon.
-- This query projects CourseNum, deptID, CourseName, Location, meetDay, meetTime
select * from Course where deptID = 'MATH' and meetTime >= '12:00';

--attributes out of order
-- Q2: Return David's course schedule.
-- projects all attributes from course
select * from Course natural join 
(select deptID, CourseNum from (select studentID from Student where studentName = 'David')
natural join Enroll);

-- Q3: Finds the average GPA for each of the class ranks (freshman, sophomore, junior, senior).
-- It projects each class and the average gpa of the associated class.
select class, avg(gpa) as 'ClassGPA'
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

-- Q6: Lists all departments in the college and their respective student enrollments.
-- porjects deptName, enrolled
select deptName, count(deptID) as 'enrolled'
from Enroll natural join (select deptID, deptName from Dept)
group by deptID
order by enrolled desc;

-- Q7: Lists all of the valedictorians in each major
-- It projects studentID, studentName, class, major, gpa
select studentID, studentName, class, major, gpa 
from Student natural join Major natural join (select major, max(gpa) as 'gpa' from Student natural join Major group by major) 
order by major;

-- Q8: Returns the students with the second highest GPA in each major
-- It projects studentID, studentName, class, major, gpa
select studentID, studentName, class, major, max(gpa) as 'gpa' from Student natural join Major where (studentID, major) not in
(select studentID, major from Student natural join Major natural join (select major, max(gpa) as 'gpa' from Student natural join Major group by major) 
order by major)
group by major
order by major;

-- Q9 finds the name, ID, and number of courses that students are taking for the students who are taking the most courses
-- this query projects studentID, studentName, and numCourses
select studentID, studentName, NumCourses from Student natural join (select StudentID, count(studentID) as NumCourses 
from Enroll 
group by studentID)
natural join (select max(NumCourses) as NumCourses 
from (select count(studentID) as NumCourses 
from Enroll 
group by studentID));

-- Q10 give every CSCI student a 1.0 bump in gpa
-- projects studentID, studentName, class, gpa, major
select * from student natural join major where major='CSCI';

begin transaction;
update Student
set gpa = 4.0
where exists (select * from Major where major = 'CSCI' and Student.studentID = Major.StudentID and Student.gpa > 3.0);
update Student
set gpa = 1.0+gpa
where exists (select * from Major where major = 'CSCI' and Student.studentID = Major.StudentID and Student.gpa <= 3.0);
commit transaction;

select * from student natural join major where major='CSCI';

-- Q11 Create philisophy department, add phil 101: ethics to courses, and enroll all current CSCI majors in the course
begin transaction;
insert into Dept values ('PHIL', 'Philosophy', 'Plato''s Cave');
insert into Course values (101, 'PHIL', 'Ethics', 'CAVE', 'TR', '16:00');
insert into Enroll values (101, 'PHIL', 1381), (101, 'PHIL', 1709),
(101, 'PHIL', 1316), (101, 'PHIL', 1911), (101, 'PHIL', 1501), (101, 'PHIL', 1661);
commit transaction;


