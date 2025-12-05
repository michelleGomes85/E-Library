--
-- PostgreSQL database dump
--

\restrict ALxVUTCRw5PW4ScK8ifs6iPOGkP0Sf95nRmpytw2scERqd4kVrL44c5f82aabau

-- Dumped from database version 14.19 (Ubuntu 14.19-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.19 (Ubuntu 14.19-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY public.loans DROP CONSTRAINT fklinseuu52xb6wknrxp9ae0uwu;
ALTER TABLE ONLY public.book_category DROP CONSTRAINT fkiwvwb2bwuvg0017hh8kg5e8g1;
ALTER TABLE ONLY public.copies DROP CONSTRAINT fkic2xn1usf0mywohvy5thj1m3l;
ALTER TABLE ONLY public.book_category DROP CONSTRAINT fk7k0c5mr0rx89i8jy5ges23jpe;
ALTER TABLE ONLY public.loans DROP CONSTRAINT fk6xxlcjc0rqtn5nq28vjnx5t9d;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.loans DROP CONSTRAINT loans_pkey;
ALTER TABLE ONLY public.copies DROP CONSTRAINT copies_pkey;
ALTER TABLE ONLY public.copies DROP CONSTRAINT copies_internalcode_key;
ALTER TABLE ONLY public.categories DROP CONSTRAINT categories_pkey;
ALTER TABLE ONLY public.categories DROP CONSTRAINT categories_name_key;
ALTER TABLE ONLY public.books DROP CONSTRAINT books_pkey;
ALTER TABLE ONLY public.books DROP CONSTRAINT books_isbn_key;
DROP TABLE public.users;
DROP SEQUENCE public.user_seq;
DROP TABLE public.loans;
DROP SEQUENCE public.loan_seq;
DROP SEQUENCE public.copy_seq;
DROP TABLE public.copies;
DROP SEQUENCE public.category_seq;
DROP TABLE public.categories;
DROP TABLE public.books;
DROP SEQUENCE public.book_seq;
DROP TABLE public.book_category;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: book_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.book_category (
    book_id bigint NOT NULL,
    category_id bigint NOT NULL
);


--
-- Name: book_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.book_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: books; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.books (
    year integer NOT NULL,
    id bigint NOT NULL,
    author character varying(255) NOT NULL,
    isbn character varying(255),
    publisher character varying(255) NOT NULL,
    title character varying(255) NOT NULL
);


--
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categories (
    id bigint NOT NULL,
    name character varying(50) NOT NULL
);


--
-- Name: category_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.category_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: copies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.copies (
    book_id bigint NOT NULL,
    id bigint NOT NULL,
    internalcode character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT copies_status_check CHECK (((status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'BORROWED'::character varying, 'RESERVED'::character varying])::text[])))
);


--
-- Name: copy_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.copy_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: loan_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.loan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: loans; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loans (
    due_date date NOT NULL,
    issue_date date NOT NULL,
    return_date date,
    copy_id bigint NOT NULL,
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT loans_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'RETURNED'::character varying, 'OVERDUE'::character varying])::text[])))
);


--
-- Name: user_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    passwordhash character varying(255) NOT NULL,
    registration character varying(255) NOT NULL,
    rules character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    CONSTRAINT users_rules_check CHECK (((rules)::text = ANY ((ARRAY['COMMON_USER'::character varying, 'ADMIN'::character varying])::text[]))),
    CONSTRAINT users_type_check CHECK (((type)::text = ANY ((ARRAY['STUDENT'::character varying, 'TEACHER'::character varying])::text[])))
);


--
-- Data for Name: book_category; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.book_category (book_id, category_id) VALUES (1, 1);
INSERT INTO public.book_category (book_id, category_id) VALUES (1, 2);
INSERT INTO public.book_category (book_id, category_id) VALUES (4, 1);
INSERT INTO public.book_category (book_id, category_id) VALUES (4, 2);
INSERT INTO public.book_category (book_id, category_id) VALUES (5, 1);
INSERT INTO public.book_category (book_id, category_id) VALUES (5, 2);
INSERT INTO public.book_category (book_id, category_id) VALUES (15, 1);
INSERT INTO public.book_category (book_id, category_id) VALUES (15, 2);
INSERT INTO public.book_category (book_id, category_id) VALUES (6, 4);
INSERT INTO public.book_category (book_id, category_id) VALUES (8, 4);
INSERT INTO public.book_category (book_id, category_id) VALUES (16, 4);
INSERT INTO public.book_category (book_id, category_id) VALUES (7, 3);
INSERT INTO public.book_category (book_id, category_id) VALUES (12, 3);
INSERT INTO public.book_category (book_id, category_id) VALUES (19, 3);
INSERT INTO public.book_category (book_id, category_id) VALUES (9, 3);
INSERT INTO public.book_category (book_id, category_id) VALUES (11, 3);
INSERT INTO public.book_category (book_id, category_id) VALUES (18, 3);
INSERT INTO public.book_category (book_id, category_id) VALUES (14, 6);
INSERT INTO public.book_category (book_id, category_id) VALUES (17, 8);
INSERT INTO public.book_category (book_id, category_id) VALUES (13, 5);
INSERT INTO public.book_category (book_id, category_id) VALUES (20, 10);
INSERT INTO public.book_category (book_id, category_id) VALUES (3, 2);
INSERT INTO public.book_category (book_id, category_id) VALUES (3, 1);
INSERT INTO public.book_category (book_id, category_id) VALUES (3, 8);
INSERT INTO public.book_category (book_id, category_id) VALUES (2, 2);
INSERT INTO public.book_category (book_id, category_id) VALUES (2, 1);
INSERT INTO public.book_category (book_id, category_id) VALUES (2, 8);


--
-- Data for Name: books; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2018, 1, 'Joshua Bloch', '9780134685991', 'Addison-Wesley', 'Effective Java');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2005, 2, 'Kathy Sierra', '9780596009205', 'O''Reilly', 'Head First Java');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2008, 3, 'Robert C. Martin', '9780132350884', 'Prentice Hall', 'Clean Code');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2018, 4, 'Craig Walls', '9781617294945', 'Manning', 'Spring in Action');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2013, 5, 'Mark Lutz', '9781491950357', 'O''Reilly', 'Learning Python');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1937, 6, 'J.R.R. Tolkien', '9780345391803', 'HarperCollins', 'O Hobbit');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2011, 7, 'Ernest Cline', '9780307887443', 'Crown', 'Ready Player One');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1996, 8, 'George R.R. Martin', '9780553573428', 'Bantam', 'A Guerra dos Tronos');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1949, 9, 'George Orwell', '9788535914849', 'Companhia das Letras', '1984');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1985, 11, 'Margaret Atwood', '9780062315007', 'Houghton Mifflin', 'O Conto da Aia');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1984, 12, 'William Gibson', '9788595081512', 'Aleph', 'Neuromancer');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2011, 13, 'Yuval Noah Harari', '9788544104521', 'L&PM', 'Sapiens');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1945, 14, 'George Orwell', '9788537814765', 'Companhia das Letras', 'A Revolução dos Bichos');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2017, 15, 'Robert C. Martin', '9780134494166', 'Prentice Hall', 'Clean Architecture');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2007, 16, 'Patrick Rothfuss', '9788535930917', 'Arqueiro', 'O Nome do Vento');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1977, 17, 'Stephen King', '9780307474278', 'Doubleday', 'O Iluminado');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1953, 18, 'Ray Bradbury', '9788535913958', 'Companhia das Letras', 'Fahrenheit 451');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (2011, 19, 'Andy Weir', '9780804139021', 'Crown', 'The Martian');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (-380, 20, 'Platão', '9780140449266', 'Penguin', 'A República');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1997, 21, 'J. K. Rowling', '123456', 'J.K Ed', 'Harry Potter - e a Pedra Filosofal');
INSERT INTO public.books (year, id, author, isbn, publisher, title) VALUES (1900, 22, 'Teste', 'Livro-001', 'Teste - ED', 'Testando');


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.categories (id, name) VALUES (1, 'Tecnologia');
INSERT INTO public.categories (id, name) VALUES (2, 'Programação');
INSERT INTO public.categories (id, name) VALUES (3, 'Ficção');
INSERT INTO public.categories (id, name) VALUES (4, 'Fantasia');
INSERT INTO public.categories (id, name) VALUES (6, 'História');
INSERT INTO public.categories (id, name) VALUES (7, 'Romance');
INSERT INTO public.categories (id, name) VALUES (8, 'Terror');
INSERT INTO public.categories (id, name) VALUES (9, 'Suspense');
INSERT INTO public.categories (id, name) VALUES (10, 'Educação');
INSERT INTO public.categories (id, name) VALUES (5, 'Ciências');


--
-- Data for Name: copies; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (1, 2, 'CP-0002', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (2, 3, 'CP-0003', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (2, 4, 'CP-0004', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (4, 7, 'CP-0007', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (4, 8, 'CP-0008', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (5, 9, 'CP-0009', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (5, 10, 'CP-0010', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (6, 11, 'CP-0011', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (6, 12, 'CP-0012', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (6, 13, 'CP-0013', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (7, 14, 'CP-0014', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (11, 22, 'CP-0022', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (12, 24, 'CP-0024', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (12, 25, 'CP-0025', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (13, 26, 'CP-0026', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (13, 27, 'CP-0027', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (14, 28, 'CP-0028', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (15, 29, 'CP-0029', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (15, 30, 'CP-0030', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (16, 31, 'CP-0031', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (16, 32, 'CP-0032', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (17, 33, 'CP-0033', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (18, 35, 'CP-0035', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (18, 36, 'CP-0036', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (6, 42, 'CP-0042', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (12, 45, 'CP-0045', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (13, 46, 'CP-0046', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (14, 47, 'CP-0047', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (1, 48, 'CP-0048', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (2, 49, 'CP-0049', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (3, 5, 'CP-0005', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (9, 18, 'CP-0018', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (19, 38, 'CP-0038', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (20, 40, 'CP-0040', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (20, 41, 'CP-0041', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (3, 50, 'CP-0050', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (3, 6, 'CP-0006', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (7, 15, 'CP-0015', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (11, 23, 'CP-0023', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (17, 34, 'CP-0034', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (8, 17, 'CP-0017', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (8, 43, 'CP-0043', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (8, 16, 'CP-0016', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (22, 57, 'CP-004', 'BORROWED');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (9, 19, 'CP-0019', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (9, 51, 'CP-002', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (19, 37, 'CP-0037', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (19, 39, 'CP-0039', 'AVAILABLE');
INSERT INTO public.copies (book_id, id, internalcode, status) VALUES (9, 58, '"CP-0024"', 'AVAILABLE');


--
-- Data for Name: loans; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 5, 1, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 39, 49, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 37, 3, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 4, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 39, 5, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 16, 6, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 17, 7, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 43, 8, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 37, 9, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 10, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 39, 11, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 37, 12, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 13, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 18, 14, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 19, 15, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 39, 16, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 16, 17, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 17, 18, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 43, 19, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 37, 20, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 21, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 39, 22, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 16, 23, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 37, 24, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 25, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 39, 26, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 37, 27, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 28, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 39, 29, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 38, 30, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 50, 31, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 18, 32, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 19, 33, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 18, 34, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-13', '2025-11-29', '2025-11-29', 19, 35, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 40, 36, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 41, 37, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 40, 38, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 41, 39, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 40, 40, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 17, 41, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 43, 42, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 16, 43, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 41, 44, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-14', '2025-11-30', '2025-11-30', 40, 45, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 18, 46, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 19, 47, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 51, 48, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 38, 50, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 37, 51, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 39, 52, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 53, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 54, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 55, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 56, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 38, 57, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 58, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 59, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 60, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 61, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 62, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 63, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 64, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 65, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 66, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 67, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 68, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 69, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 70, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 38, 71, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 72, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 73, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 74, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 38, 75, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 76, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 77, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 78, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 58, 79, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 18, 80, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 18, 81, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 18, 82, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 38, 83, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 40, 84, 1, 'RETURNED');
INSERT INTO public.loans (due_date, issue_date, return_date, copy_id, id, user_id, status) VALUES ('2025-12-19', '2025-12-05', '2025-12-05', 41, 85, 1, 'RETURNED');


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.users (id, email, name, passwordhash, registration, rules, type) VALUES (1, 'admin@gmail.com', 'admin', '$2a$10$PU6kcAuWL8TLBr3nR5zoeuorzlJAjYOMDvgekqpOnUlSwQ6kXozii', 'admin', 'ADMIN', 'TEACHER');
INSERT INTO public.users (id, email, name, passwordhash, registration, rules, type) VALUES (3, 'michelle@gmail.com', 'michelle', '$2a$10$F2VaP2dGxDsVQsQbQqqEuOYDk3wcj.kMsFWbUtXJhEPDJIfppl7uu', 'michelle', 'COMMON_USER', 'STUDENT');


--
-- Name: book_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.book_seq', 22, true);


--
-- Name: category_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.category_seq', 11, true);


--
-- Name: copy_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.copy_seq', 58, true);


--
-- Name: loan_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.loan_seq', 85, true);


--
-- Name: user_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.user_seq', 3, true);


--
-- Name: books books_isbn_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_isbn_key UNIQUE (isbn);


--
-- Name: books books_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_pkey PRIMARY KEY (id);


--
-- Name: categories categories_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_name_key UNIQUE (name);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: copies copies_internalcode_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies
    ADD CONSTRAINT copies_internalcode_key UNIQUE (internalcode);


--
-- Name: copies copies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies
    ADD CONSTRAINT copies_pkey PRIMARY KEY (id);


--
-- Name: loans loans_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT loans_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: loans fk6xxlcjc0rqtn5nq28vjnx5t9d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT fk6xxlcjc0rqtn5nq28vjnx5t9d FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: book_category fk7k0c5mr0rx89i8jy5ges23jpe; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_category
    ADD CONSTRAINT fk7k0c5mr0rx89i8jy5ges23jpe FOREIGN KEY (book_id) REFERENCES public.books(id);


--
-- Name: copies fkic2xn1usf0mywohvy5thj1m3l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies
    ADD CONSTRAINT fkic2xn1usf0mywohvy5thj1m3l FOREIGN KEY (book_id) REFERENCES public.books(id);


--
-- Name: book_category fkiwvwb2bwuvg0017hh8kg5e8g1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_category
    ADD CONSTRAINT fkiwvwb2bwuvg0017hh8kg5e8g1 FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: loans fklinseuu52xb6wknrxp9ae0uwu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT fklinseuu52xb6wknrxp9ae0uwu FOREIGN KEY (copy_id) REFERENCES public.copies(id);


--
-- PostgreSQL database dump complete
--

\unrestrict ALxVUTCRw5PW4ScK8ifs6iPOGkP0Sf95nRmpytw2scERqd4kVrL44c5f82aabau

