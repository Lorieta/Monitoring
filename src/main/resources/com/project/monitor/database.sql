CREATE TABLE Student_info(
  lrn varchar(255),
  Firstname varchar(255),
  Lastname varchar(255),
  Gender varchar(255),
  age varchar(255),
 adviserID varchar(255) NOT NULL REFERENCES teacher_info(employeeID),

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

CREATE TABLE Materials (
  `MaterialsId` int BIGSERIAL,
  `TypeID` BIGINT  NOT NULL REFERENCES languagetype(Language),
  ` ResourceTitle` <type>,
  `URL` VARCHAR(255),
  `Author/Publisher` VARCHAR(255),
  ` Date_Published` date,
  `ResourceID` BIGINT  NOT NULL REFERENCES Resourcetype(resourceid),
  PRIMARY KEY (`MaterialsId`)
);


CREATE TABLE Resourcetype (
  `resourceid` Type,
  `ResourceType` Type,
  PRIMARY KEY (`resourceid`)
);



SELECT * FROM resources
JOIN languagetype on resources.LanguageID = languagetype.LanguageID

-- Insert data into Materials table
INSERT INTO Materials (TypeID, ResourceTitle, URL, AuthorPublisher, Date_Published, ResourceID)
VALUES
(1, 'Effective Java', 'http://example.com/effectivejava', 'Joshua Bloch', '2018-01-01', 1),
(2, 'Spanish Grammar', 'http://example.com/spanishgrammar', 'John Doe', '2019-06-15', 2),
(3, 'French for Beginners', 'http://example.com/frenchbeginners', 'Jane Smith', '2020-09-10', 3);
