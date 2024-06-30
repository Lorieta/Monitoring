CREATE TABLE Student_info(
  lrn varchar(255),
  Firstname varchar(255),
  Lastname varchar(255),
  Gender varchar(255),
  age varchar(255),
 adviserID varchar(255) NOT NULL REFERENCES teacher_info(employeeID),
 UNIQUE(adviserID),
  PRIMARY KEY (LRN)
);

SELECT student_info.lrn, student_info.firstname, student_info.lastname, student_info.gender, student_info.age, teacher_info.grade_section
FROM student_info
JOIN teacher_info ON teacher_info.employeeID = student_info.adviserID;


CREATE TABLE SectionClassList (
  sectionID BIGSERIAL NOT NULL,
  LRN varchar(255) NOT NULL REFERENCES student_info(lrn),
  adviserID varchar(255) NOT NULL REFERENCES teacher_info(employeeID),
  PRIMARY KEY (`sectionID`)
);



CREATE TABLE teacher_Info(
  employeeID VARCHAR(225),
  teacherFname VARCHAR(225),
  teacherLname VARCHAR(225),
  grade_section VARCHAR(225),
  Password VARCHAR(225),
  PRIMARY KEY (EmployeeID)
);

CREATE TABLE Languagetype (
  LanguageID SERIAL,
  LanguageType VARCHAR(225),
  PRIMARY KEY (LanguageID)
);

CREATE TABLE Resources (
  ResourceID BIGSERIAL,
  LanguageID BIGINT NOT NULL REFERENCES languagetype (LanguageID),
  ResourceTitle VARCHAR(255) NOT NULL,
  URL VARCHAR(255),
  "Author/Publisher" VARCHAR(255) NOT NULL,
   "Date Published" date,
  ResourceType VARCHAR(255) NOT NULL,
  PRIMARY KEY (ResourceID)

);

SELECT * FROM resources
JOIN languagetype on resources.LanguageID = languagetype.LanguageID
