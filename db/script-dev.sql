-- This file contains the SQL code to create the tables for the library database and the relationships between them.
-- The following file has the "dev" suffix because it is used for development purposes only
-- and is not used in the production environment.
-- the production environment uses oracle database and the sql code is written in a different way.
-- this code runs on postgresql database. That's the reason why it is written in this way.

CREATE TABLE author
(
    id          serial UNIQUE NOT NULL PRIMARY KEY,
    name        varchar(255)  NOT NULL,
    nationality varchar(255)  NOT NULL
);

CREATE TABLE book
(
    id               serial UNIQUE NOT NULL PRIMARY KEY,
    title            varchar(255)  NOT NULL,
    isbn             varchar(13)   NOT NULL UNIQUE,
    publication_date date          NOT NULL,
    pages            integer       NOT NULL,
    author_id        bigint        NOT NULL,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES author (id) ON DELETE CASCADE
);

CREATE TABLE patron
(
    id   serial UNIQUE NOT NULL PRIMARY KEY,
    name varchar(50)   NOT NULL,
    type varchar(50)   NOT NULL
);

CREATE TABLE instance
(
    id        serial UNIQUE NOT NULL PRIMARY KEY,
    book_isbn varchar(13)   NOT NULL,
    status    varchar       NOT NULL,
    type      VARCHAR       NOT NULL,
    CONSTRAINT fk_book_isbn FOREIGN KEY (book_isbn) REFERENCES book (isbn) ON DELETE CASCADE
);

CREATE TABLE hold
(
    id             serial UNIQUE NOT NULL PRIMARY KEY,
    patron_id      bigint        NOT NULL,
    instance_id    bigint        NOT NULL,
    date_placed    date          NOT NULL,
    days_to_expire int,
    hold_fee       real          NOT NULL,
    CONSTRAINT fk_patron_id FOREIGN KEY (patron_id) REFERENCES patron (id) ON DELETE CASCADE,
    CONSTRAINT fk_instance_id FOREIGN KEY (instance_id) REFERENCES instance (id) ON DELETE CASCADE
);

CREATE TABLE loan
(
    id          serial UNIQUE NOT NULL PRIMARY KEY,
    hold_id     bigint        NOT NULL UNIQUE,
    time        int           NOT NULL,
    loan_date   date          NOT NULL,
    due_date    date          NOT NULL,
    overdue_fee real          NOT NULL,
    CONSTRAINT fk_hold_id FOREIGN KEY (hold_id) REFERENCES hold (id) ON DELETE CASCADE
);