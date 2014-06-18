-- 
-- Freeswitch tables for MOD_XML_CURL (directory and dialplan only)
--
CREATE TABLE dialplan (
   dialplan_id        SERIAL
  ,domain             VARCHAR(128) NOT NULL
  ,ip_address         VARCHAR(15) NOT NULL
  ,PRIMARY KEY (dialplan_id)
);

CREATE TABLE dialplan_condition (
   condition_id       SERIAL
  ,extension_id       INTEGER NOT NULL
  ,field              VARCHAR(1238) NOT NULL
  ,expression         VARCHAR(512) DEFAULT NULL
  ,weight             INTEGER NOT NULL
  ,PRIMARY KEY (condition_id)
);

CREATE TABLE dialplan_context (
   context_id         SERIAL
  ,dialplan_id        INTEGER NOT NULL
  ,context            VARCHAR(64) NOT NULL
  ,weight             INTEGER NOT NULL
  ,PRIMARY KEY (context_id)
);

CREATE TABLE dialplan_extension (
   extension_id       SERIAL
  ,context_id         INTEGER NOT NULL
  ,name               VARCHAR(512) DEFAULT NULL
  ,continue           VARCHAR(32) NOT NULL
  ,weight             INTEGER NOT NULL
  ,PRIMARY KEY (extension_id)
);
CREATE INDEX dialplan_extension_name ON dialplan_extension (name);

CREATE TABLE dialplan_special (
   id                 SERIAL
  ,context            VARCHAR(255) NOT NULL
  ,class_file         VARCHAR(255) NOT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE (context)
);


CREATE TABLE dialplan_actions (
   action_id          SERIAL
  ,condition_id       INTEGER NOT NULL
  ,application        VARCHAR(256) NOT NULL
  ,data               VARCHAR(512) DEFAULT NULL
  ,type               VARCHAR(32) NOT NULL
  ,weight             INTEGER NOT NULL
  ,PRIMARY KEY (action_id)
);

CREATE TABLE directory (
   id                 SERIAL
  ,ringring_user_id   INTEGER DEFAULT NULL
  ,username           VARCHAR(255) NOT NULL
  ,domain             VARCHAR(255) NOT NULL
  ,domain_id          INTEGER DEFAULT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE (username)
);

CREATE TABLE directory_domains (
   id                 SERIAL
  ,domain_name        VARCHAR(128) NOT NULL
  ,PRIMARY KEY (id)
);

CREATE TABLE directory_gateway_params (
   id                 SERIAL
  ,d_gw_id            INTEGER NOT NULL
  ,param_name         VARCHAR(64) NOT NULL
  ,param_value        VARCHAR(64) NOT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE (d_gw_id, param_name)
);

CREATE TABLE directory_gateways (
   id                 SERIAL
  ,directory_id       INTEGER NOT NULL
  ,gateway_name       VARCHAR(128) NOT NULL
  ,PRIMARY KEY (id)
);

CREATE TABLE directory_global_params (
   id                 SERIAL
  ,param_name         VARCHAR(64) NOT NULL
  ,param_value        VARCHAR(128) NOT NULL
  ,domain_id          INTEGER
  ,PRIMARY KEY (id)
);

CREATE TABLE directory_global_vars (
   id                 SERIAL
  ,var_name           VARCHAR(64) NOT NULL
  ,var_value          VARCHAR(128) NOT NULL
  ,domain_id          INTEGER
  ,PRIMARY KEY (id)
);

CREATE TABLE directory_params (
   id                 SERIAL
  ,directory_id       INTEGER DEFAULT NULL
  ,param_name         VARCHAR(255) DEFAULT NULL
  ,param_value        VARCHAR(255) DEFAULT NULL
  ,PRIMARY KEY (id)
);

CREATE TABLE directory_vars (
   id                 SERIAL
  ,directory_id       INTEGER
  ,var_name           VARCHAR(255) DEFAULT NULL
  ,var_value          VARCHAR(255) DEFAULT NULL
  ,PRIMARY KEY (id)
);
