import pandas
import numpy

def deleteLastComma(insertion):
    comma = len(insertion)-3
    is1 = insertion[:comma] 
    is2 = insertion[comma+1:]
    insertion = is1 + is2
    return insertion

data = pandas.read_csv('shmeeplesoft.raw.txt')
data.drop([0], inplace=True)
attributeTuples = [('Student', ['studentID', 'studentName', 'class', 'gpa']),
               ('Dept', ['deptID', 'deptName', 'building']),
               ('Major', ['studentID', 'major']),
               ('Course', ['CourseNum', 'deptID', 'CourseName', 'Location', 'meetDay', 'meetTime']),
               ('Enroll', ['CourseNum', 'deptID', 'studentID'])]

data.replace(to_replace='SR', value='Senior', inplace=True)
data.replace(to_replace='JR', value='Junior', inplace=True)
data.replace(to_replace='SO', value='Sophomore', inplace=True)
data.replace(to_replace='FR', value='Freshman', inplace=True)

itemNum = len(data.index)

for i in range(0, itemNum):
    for (relation, attributes) in attributeTuples:
        attributesFilled = True
        insertion = '('
        for attribute in attributes:       
            if pandas.isnull(data.iloc[i][attribute]):
                attributesFilled = False

        if attributesFilled:
            if relation is 'Major':
                    majors = str(data.iloc[i]['major']).split(';')
                    for major in majors:
                        insertion = '('
                        insertion = insertion + str(int(data.iloc[i]['studentID'])) + ',' 
                        insertion = insertion + '\'' + major + '\''
                        insertion = insertion + '\');'
                        insertion = deleteLastComma(insertion)
                        print('INSERT INTO ' + relation + ' VALUES ' + insertion)
            else:
                for attribute in attributes:
                    if attribute is 'gpa':
                        addition = str(data.iloc[i][attribute])
                    elif attribute is 'studentID'or attribute is 'CourseNum':
                        addition = str(int(data.iloc[i][attribute]))
                    else:
                        addition = '\'' + str(data.iloc[i][attribute]) + '\''
                    insertion = insertion + addition + ',' 
                insertion = insertion + ');'
                insertion = deleteLastComma(insertion)
                print('INSERT INTO ' + relation + ' VALUES ' + insertion)
