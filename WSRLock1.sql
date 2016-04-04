﻿-- Role: wsrlock
CREATE ROLE wsrlock LOGIN
  ENCRYPTED PASSWORD 'md541b9881054873bc1a5f91316490d1c2d'
  NOSUPERUSER INHERIT CREATEDB NOCREATEROLE NOREPLICATION;


-- Database: wsrlock
CREATE DATABASE wsrlock
  WITH OWNER = wsrlock
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;