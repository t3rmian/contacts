CREATE TABLE IF NOT EXISTS CUSTOMERS (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME varchar(1000) not null,
    SURNAME varchar(1000) not null,
    AGE INT
);

CREATE TABLE IF NOT EXISTS CONTACTS (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_CUSTOMERS INT NOT NULL,
    CONTACT varchar(1000) not null,
    TYPE INT not null,
    FOREIGN KEY(ID_CUSTOMERS) REFERENCES CUSTOMERS(ID)
);
