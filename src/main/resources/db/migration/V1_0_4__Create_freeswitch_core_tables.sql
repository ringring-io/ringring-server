CREATE TABLE registrations (
   reg_user           CHARACTER VARYING(256)
  ,realm              CHARACTER VARYING(256)
  ,token              CHARACTER VARYING(256)
  ,url                TEXT
  ,expires            INTEGER
  ,network_ip         CHARACTER VARYING(256)
  ,network_port       CHARACTER VARYING(256)
  ,network_proto      CHARACTER VARYING(256)
  ,hostname           CHARACTER VARYING(256)
  ,metadata           CHARACTER VARYING(256)
);

CREATE INDEX regindex1 ON registrations USING btree (reg_user, realm, hostname);