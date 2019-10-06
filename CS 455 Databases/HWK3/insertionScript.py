import pandas
import numpy

data = pandas.read_csv('shmeeplesoft.raw.txt')
data.drop([0], inplace=True)
primaryKeys = [('Student', ['studentID', 'studentName', 'class', 'gpa']),
               ('Major', ['studentID', 'major']),
               ('Course', ['CourseNum', 'deptID', 'CourseName', 'Location', 'meetDay', 'meetTime']),
               ('Dept', ['deptID', 'deptName', 'building']),
               ('Enroll', ['CourseNum', 'deptID', 'studentID'])]

itemNum = len(data.index)

for i in range(0, itemNum):
    for (relation, attributes) in primaryKeys:
        attributesFilled = True
        insertion = '('
        for attribute in attributes:       
            if pandas.isnull(data.iloc[i][attribute]):
                attributesFilled = False

        if attributesFilled:
            for attribute in attributes:
                insertion = insertion + str(data.iloc[i][attribute]) + ',' 
            insertion = insertion + ');'
            comma = len(insertion)-3
            is1 = insertion[:comma] 
            is2 = insertion[comma+1:]
            insertion = is1 + is2
            print('INSERT INTO ' + relation + ' VALUES ' + insertion)
