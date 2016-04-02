-- Users: wsrlock
insert into users (userid, username, userpass, enabled) values(nextval('hibernate_sequence'),'Bob','$2a$04$/xTLObe.npII5l74n0b3bOhbpl7CbfjNupSMztAvhZ/Kv/miR4upG','1');
insert into users (userid, username, userpass, enabled) values(nextval('hibernate_sequence'),'Alice','$2a$04$/xTLObe.npII5l74n0b3bOhbpl7CbfjNupSMztAvhZ/Kv/miR4upG','1');

-- Userroles: wsrlock
insert into userroles (userroleid,role, userid_userid) values(nextval('hibernate_sequence'),'ROLE_USER',(select userid from users where username='Bob'));
insert into userroles (userroleid,role, userid_userid) values(nextval('hibernate_sequence'),'ROLE_USER',(select userid from users where username='Alice'));