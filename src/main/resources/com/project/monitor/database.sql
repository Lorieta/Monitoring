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

CREATE TABLE reading_log (
  LogID BIGSERIAL PRIMARY KEY,
  MaterialID BIGINT NOT NULL REFERENCES materials(MaterialsId),
  LRN VARCHAR(255) NOT NULL REFERENCES student_info(LRN),
  Duration VARCHAR(50),
  DateStarted DATE,
  DateFinished DATE,
  Comment VARCHAR(255)
);


SELECT
    rl.LogID,
    si.LRN,
    si.Firstname,
    si.Lastname,
    m.ResourceTitle,
    m.URL,
    lt.LanguageType,
    rt.ResourceType,
    rl.Duration,
    rl.DateStarted,
    rl.DateFinished,
    rl.Comment
FROM
    reading_log rl
JOIN
    Student_info si ON rl.LRN = si.LRN
JOIN
    Materials m ON rl.MaterialID = m.MaterialsId
JOIN
    Languagetype lt ON m.TypeID = lt.LanguageID
JOIN
    Resourcetype rt ON m.ResourceID = rt.ResourceID
ORDER BY
    rl.DateStarted DESC;


SELECT * FROM resources
JOIN languagetype on resources.LanguageID = languagetype.LanguageID

-- Insert data into Materials table
INSERT INTO Materials (TypeID, ResourceTitle, URL, AuthorPublisher, Date_Published, ResourceID)
VALUES
(1, 'Effective Java', 'http://example.com/effectivejava', 'Joshua Bloch', '2018-01-01', 1),
(2, 'Spanish Grammar', 'http://example.com/spanishgrammar', 'John Doe', '2019-06-15', 2),
(3, 'French for Beginners', 'http://example.com/frenchbeginners', 'Jane Smith', '2020-09-10', 3);

CREATE TABLE IF NOT EXISTS Result (
  ResultID BIGSERIAL PRIMARY KEY,
  LRN VARCHAR(255) NOT NULL REFERENCES Student_info(LRN),
  TypeID INT NOT NULL REFERENCES Languagetype(LanguageID),
  OralResult VARCHAR(255) NOT NULL,
  SilentResult VARCHAR(255) NOT NULL,
  DateRecorded DATE NOT NULL,
  Remarks VARCHAR(255)
);

SELECT
    lt.LanguageID,
    lt.LanguageType,
    o.orallID,
    o.oralresult
FROM
    Languagetype lt
JOIN
    Result r ON lt.LanguageID = r.TypeID
JOIN
    oral o ON r.oralID = o.orallID
WHERE
    r.ResultID = (
        SELECT MAX(ResultID)
        FROM Result
        WHERE TypeID = lt.LanguageID
    )
ORDER BY
    lt.LanguageType;

SELECT
    r.ResultID,
    r.LRN,
    si.LastName,
    o.oralresult AS OralResult,
    s.silentresult AS SilentResult,
    lt.Languagetype AS LanguageType,
    r.DateRecorded,
    r.Remarks
FROM
    Result r
JOIN
    oral o ON r.oralID = o.orallID
JOIN
    silent s ON r.silentID = s.SilentID
JOIN
    LanguageType lt ON r.LanguageID = lt.LanguageID
JOIN
    Student_info si ON r.LRN = si.LRN
WHERE
    lt.Languagetype IN ('English', 'Tagalog')
ORDER BY
    r.LRN, lt.Languagetype, r.DateRecorded;
