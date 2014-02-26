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



CREATE TABLE limit_data (
    hostname character varying(255) DEFAULT NULL::character varying,
    realm character varying(255) DEFAULT NULL::character varying,
    id character varying(255) DEFAULT NULL::character varying,
    uuid character varying(255) DEFAULT NULL::character varying
);



CREATE TABLE sip_authentication (
    nonce character varying(255),
    expires integer,
    profile_name character varying(255),
    hostname character varying(255)
);

CREATE INDEX sa_hostname ON sip_authentication USING btree (hostname);
CREATE INDEX sa_nonce ON sip_authentication USING btree (nonce);



CREATE TABLE sip_dialogs (
    call_id character varying(255),
    uuid character varying(255),
    sip_to_user character varying(255),
    sip_to_host character varying(255),
    sip_from_user character varying(255),
    sip_from_host character varying(255),
    contact_user character varying(255),
    contact_host character varying(255),
    state character varying(255),
    direction character varying(255),
    user_agent character varying(255),
    profile_name character varying(255),
    hostname character varying(255)
);

CREATE INDEX sd_hostname ON sip_dialogs USING btree (hostname);
CREATE INDEX sd_uuid ON sip_dialogs USING btree (uuid);



CREATE TABLE sip_presence (
    sip_user character varying(255),
    sip_host character varying(255),
    status character varying(255),
    rpid character varying(255),
    expires integer,
    user_agent character varying(255),
    profile_name character varying(255),
    hostname character varying(255),
    network_ip character varying(255),
    network_port character varying(6)
);

CREATE INDEX sp_hostname ON sip_presence USING btree (hostname);



CREATE TABLE sip_registrations (
    call_id character varying(255),
    sip_user character varying(255),
    sip_host character varying(255),
    presence_hosts character varying(255),
    contact character varying(1024),
    status character varying(255),
    rpid character varying(255),
    expires integer,
    user_agent character varying(255),
    server_user character varying(255),
    server_host character varying(255),
    profile_name character varying(255),
    hostname character varying(255),
    network_ip character varying(255),
    network_port character varying(6),
    sip_username character varying(255),
    sip_realm character varying(255),
    mwi_user character varying(255),
    mwi_host character varying(255)
);

CREATE INDEX sr_call_id ON sip_registrations USING btree (call_id);
CREATE INDEX sr_contact ON sip_registrations USING btree (contact);
CREATE INDEX sr_expires ON sip_registrations USING btree (expires);
CREATE INDEX sr_hostname ON sip_registrations USING btree (hostname);
CREATE INDEX sr_network_ip ON sip_registrations USING btree (network_ip);
CREATE INDEX sr_network_port ON sip_registrations USING btree (network_port);
CREATE INDEX sr_presence_hosts ON sip_registrations USING btree (presence_hosts);
CREATE INDEX sr_profile_name ON sip_registrations USING btree (profile_name);
CREATE INDEX sr_sip_host ON sip_registrations USING btree (sip_host);
CREATE INDEX sr_sip_realm ON sip_registrations USING btree (sip_realm);
CREATE INDEX sr_sip_user ON sip_registrations USING btree (sip_user);
CREATE INDEX sr_sip_username ON sip_registrations USING btree (sip_username);
CREATE INDEX sr_status ON sip_registrations USING btree (status);



CREATE TABLE sip_shared_appearance_dialogs (
    profile_name character varying(255),
    hostname character varying(255),
    contact_str character varying(255),
    call_id character varying(255),
    network_ip character varying(255),
    expires integer
);

CREATE INDEX ssd_call_id ON sip_shared_appearance_dialogs USING btree (call_id);
CREATE INDEX ssd_contact_str ON sip_shared_appearance_dialogs USING btree (contact_str);
CREATE INDEX ssd_expires ON sip_shared_appearance_dialogs USING btree (expires);
CREATE INDEX ssd_hostname ON sip_shared_appearance_dialogs USING btree (hostname);
CREATE INDEX ssd_profile_name ON sip_shared_appearance_dialogs USING btree (profile_name);



CREATE TABLE sip_shared_appearance_subscriptions (
    subscriber character varying(255),
    call_id character varying(255),
    aor character varying(255),
    profile_name character varying(255),
    hostname character varying(255),
    contact_str character varying(255),
    network_ip character varying(255)
);

CREATE INDEX ssa_aor ON sip_shared_appearance_subscriptions USING btree (aor);
CREATE INDEX ssa_hostname ON sip_shared_appearance_subscriptions USING btree (hostname);
CREATE INDEX ssa_profile_name ON sip_shared_appearance_subscriptions USING btree (profile_name);
CREATE INDEX ssa_subscriber ON sip_shared_appearance_subscriptions USING btree (subscriber);



CREATE TABLE sip_subscriptions (
    proto character varying(255),
    sip_user character varying(255),
    sip_host character varying(255),
    sub_to_user character varying(255),
    sub_to_host character varying(255),
    presence_hosts character varying(255),
    event character varying(255),
    contact character varying(1024),
    call_id character varying(255),
    full_from character varying(255),
    full_via character varying(255),
    expires integer,
    user_agent character varying(255),
    accept character varying(255),
    profile_name character varying(255),
    hostname character varying(255),
    network_port character varying(6),
    network_ip character varying(255)
);



CREATE INDEX ss_call_id ON sip_subscriptions USING btree (call_id);
CREATE INDEX ss_event ON sip_subscriptions USING btree (event);
CREATE INDEX ss_hostname ON sip_subscriptions USING btree (hostname);
CREATE INDEX ss_presence_hosts ON sip_subscriptions USING btree (presence_hosts);
CREATE INDEX ss_proto ON sip_subscriptions USING btree (proto);
CREATE INDEX ss_sip_host ON sip_subscriptions USING btree (sip_host);
CREATE INDEX ss_sip_user ON sip_subscriptions USING btree (sip_user);
CREATE INDEX ss_sub_to_host ON sip_subscriptions USING btree (sub_to_host);
CREATE INDEX ss_sub_to_user ON sip_subscriptions USING btree (sub_to_user);
