INSERT INTO C##LABDATABASE.AUTHOR (NAME, NATIONALITY)
VALUES ('J.K. Rowling', 'British');

INSERT INTO C##LABDATABASE.AUTHOR (NAME, NATIONALITY)
VALUES ('J.R.R. Tolkien', 'British');

INSERT INTO C##LABDATABASE.AUTHOR (NAME, NATIONALITY)
VALUES ('George R.R. Martin', 'American');

INSERT INTO C##LABDATABASE.AUTHOR (NAME, NATIONALITY)
VALUES ('Stephen King', 'American');

INSERT INTO C##LABDATABASE.AUTHOR (NAME, NATIONALITY)
VALUES ('Machado de Assis', 'Brazilian');

INSERT INTO C##LABDATABASE.PATRON (NAME, TYPE)
VALUES ('John', 'STUDENT');
INSERT INTO C##LABDATABASE.PATRON (NAME, TYPE)
VALUES ('Mary', 'STUDENT');
INSERT INTO C##LABDATABASE.PATRON (NAME, TYPE)
VALUES ('Peter', 'RESEARCHER');
INSERT INTO C##LABDATABASE.PATRON (NAME, TYPE)
VALUES ('Paul', 'RESEARCHER');
INSERT INTO C##LABDATABASE.PATRON (NAME, TYPE)
VALUES ('George', 'REGULAR');
INSERT INTO C##LABDATABASE.PATRON (NAME, TYPE)
VALUES ('Ringo', 'REGULAR');

COMMIT;

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Philosopher''s Stone', '9780747532743', TO_DATE('26-JUN-1997', 'DD-MON-YYYY'), 223, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Chamber of Secrets', '9780747538493', TO_DATE('2-JUL-1998', 'DD-MON-YYYY'), 251, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Prisoner of Azkaban', '9780747542155', TO_DATE('8-JUL-1999', 'DD-MON-YYYY'), 317, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Goblet of Fire', '9780747546245', TO_DATE('8-JUL-2000', 'DD-MON-YYYY'), 636, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Order of the Phoenix', '9780747551003', TO_DATE('21-JUN-2003', 'DD-MON-YYYY'), 766, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Half-Blood Prince', '9780747581086', TO_DATE('16-JUL-2005', 'DD-MON-YYYY'), 607, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('Harry Potter and the Deathly Hallows', '9780545010221', TO_DATE('21-JUL-2007', 'DD-MON-YYYY'), 607, 1);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('The Hobbit', '9780547928227', TO_DATE('21-SEP-1937', 'DD-MON-YYYY'), 310, 2);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('The Lord of the Rings', '9780544003415', TO_DATE('29-JUL-1954', 'DD-MON-YYYY'), 1178, 2);

INSERT INTO C##LABDATABASE.BOOK (TITLE, ISBN, PUBLICATION_DATE, PAGES, AUTHOR_ID)
VALUES ('A Game of Thrones', '9780553103540', TO_DATE('6-AUG-1996', 'DD-MON-YYYY'), 694, 2);

COMMIT;

