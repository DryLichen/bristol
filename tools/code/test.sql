CREATE DATABASE IF NOT EXISTS data;

DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS committee;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    student_number VARCHAR(50),
    skill_level INT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    CHECK (skill_level >= 0)
);


CREATE TABLE event (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    event_date DATE NOT NULL,
    name VARCHAR(50) NOT NULL,
    location VARCHAR(50) NOT NULL,
    description TEXT,
    organiser_id INT NOT NULL,
    UNIQUE (event_date, location),
    FOREIGN KEY (organiser_id) REFERENCES member(member_id)
);

CREATE TABLE committee (
    committee_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL UNIQUE,
    committee_role VARCHAR(50) NOT NULL UNIQUE,
    UNIQUE (member_id, committee_role),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

