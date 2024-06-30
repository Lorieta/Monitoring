CREATE TABLE Student_info(
  LRN varchar(255),
  Firstname varchar(255),
  Lastname varchar(255),
  Gender varchar(255),
  birthDay Date(255),

  PRIMARY KEY (LRN)
);



CREATE TABLE teacher_Info(
  EmployeeID VARCHAR(225),
  TeacherFname VARCHAR(225),
  TeacherLname VARCHAR(225),
  Grade_Section VARCHAR(225),
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
