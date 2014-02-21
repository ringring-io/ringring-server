-- 
-- Freeswitch tables
--
CREATE TABLE acl_lists (
   id                 INT(10) unsigned NOT NULL AUTO_INCREMENT
  ,acl_name           VARCHAR(128) NOT NULL
  ,default_policy     VARCHAR(45) NOT NULL
  ,PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE acl_nodes (
   id                 INT(10) unsigned NOT NULL AUTO_INCREMENT
  ,cidr               VARCHAR(45) NOT NULL
  ,type               VARCHAR(16) NOT NULL
  ,list_id            INT(10) unsigned NOT NULL
  ,PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE cdr (
   id                 INT(11) NOT NULL AUTO_INCREMENT
  ,caller_id_name     VARCHAR(255) NOT NULL DEFAULT ''
  ,caller_id_number   VARCHAR(255) NOT NULL DEFAULT ''
  ,destination_number VARCHAR(255) NOT NULL DEFAULT ''
  ,context            VARCHAR(255) NOT NULL DEFAULT ''
  ,start_stamp        VARCHAR(255) NOT NULL DEFAULT ''
  ,answer_stamp       VARCHAR(255) NOT NULL DEFAULT ''
  ,end_stamp          VARCHAR(255) NOT NULL DEFAULT ''
  ,duration           VARCHAR(255) NOT NULL DEFAULT ''
  ,billsec            VARCHAR(255) NOT NULL DEFAULT ''
  ,hangup_cause       VARCHAR(255) NOT NULL DEFAULT ''
  ,uuid               VARCHAR(255) NOT NULL DEFAULT ''
  ,bleg_uuid          VARCHAR(255) NOT NULL DEFAULT ''
  ,accountcode        VARCHAR(255) NOT NULL DEFAULT ''
  ,read_codec         VARCHAR(255) NOT NULL DEFAULT ''
  ,write_codec        VARCHAR(255) NOT NULL DEFAULT ''
  ,PRIMARY KEY (id)
  ,UNIQUE KEY uuid (uuid)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE conference_advertise (
   id                 INT(10) unsigned NOT NULL AUTO_INCREMENT
  ,room               VARCHAR(64) NOT NULL
  ,status             VARCHAR(128) NOT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE KEY unique_room (room)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE conference_controls (
   id                 INT(10) unsigned NOT NULL AUTO_INCREMENT
  ,conf_group         VARCHAR(64) NOT NULL
  ,action             VARCHAR(64) NOT NULL
  ,digits             VARCHAR(16) NOT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE KEY nique_group_action (conf_group,action) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE conference_profiles (
   id                 INT(10) unsigned NOT NULL AUTO_INCREMENT
  ,profile_name       VARCHAR(64) NOT NULL
  ,param_name         VARCHAR(64) NOT NULL
  ,param_value        VARCHAR(64) NOT NULL
  ,PRIMARY KEY (id)
  ,KEY unique_profile_param (profile_name,param_name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dialplan (
   dialplan_id        INT(11) NOT NULL AUTO_INCREMENT
  ,domain             VARCHAR(128) NOT NULL
  ,ip_address         VARCHAR(15) NOT NULL
  ,PRIMARY KEY (dialplan_id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dialplan_condition (
   condition_id       INT(11) NOT NULL AUTO_INCREMENT
  ,extension_id       INT(11) NOT NULL
  ,field              VARCHAR(1238) NOT NULL
  ,expression         VARCHAR(512) DEFAULT NULL
  ,weight             INT(11) NOT NULL
  ,PRIMARY KEY (condition_id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dialplan_context (
   context_id         INT(11) NOT NULL AUTO_INCREMENT
  ,dialplan_id        INT(11) NOT NULL
  ,context            VARCHAR(64) NOT NULL
  ,weight             INT(11) NOT NULL
  ,PRIMARY KEY (context_id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dialplan_extension (
   extension_id       INT(11) NOT NULL AUTO_INCREMENT
  ,context_id         INT(11) NOT NULL
  ,name               VARCHAR(512) DEFAULT NULL
  ,`continue`         VARCHAR(32) NOT NULL
  ,weight             INT(11) NOT NULL
  ,PRIMARY KEY (extension_id)
  ,KEY name (name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dialplan_special (
   id                 INT(11) NOT NULL AUTO_INCREMENT
  ,context            VARCHAR(255) NOT NULL
  ,class_file         VARCHAR(255) NOT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE KEY unique_context (context)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dingaling_profile_params (
   id                 INT(10) unsigned NOT NULL AUTO_INCREMENT
  ,dingaling_id       INT(10) unsigned NOT NULL
  ,param_name         VARCHAR(64) NOT NULL
  ,param_value        VARCHAR(64) NOT NULL
  ,PRIMARY KEY (id)
  ,UNIQUE KEY unique_type_name (dingaling_id,param_name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dingaling_profiles (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  profile_name VARCHAR(64) NOT NULL,
  type VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_name (profile_name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE dingaling_settings (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  param_name VARCHAR(64) NOT NULL,
  param_value VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_param (param_name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


CREATE TABLE dialplan_actions (
  action_id INT(11) NOT NULL AUTO_INCREMENT,
  condition_id INT(11) NOT NULL,
  application VARCHAR(256) NOT NULL,
  data VARCHAR(512) DEFAULT NULL,
  type VARCHAR(32) NOT NULL,
  weight INT(11) NOT NULL,
  PRIMARY KEY (action_id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE directory (
  id INT(11) NOT NULL AUTO_INCREMENT,
  zirgoo_user_id INT(11) DEFAULT NULL,
  username VARCHAR(255) NOT NULL,
  domain VARCHAR(255) NOT NULL,
  domain_id INT(10) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY username (username)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE directory_domains (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  domain_name VARCHAR(128) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE directory_gateway_params (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  d_gw_id INT(10) unsigned NOT NULL,
  param_name VARCHAR(64) NOT NULL,
  param_value VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_gw_param (d_gw_id,param_name)
) ENGINE=MyISAM CHARSET=latin1;

CREATE TABLE directory_gateways (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  directory_id INT(10) unsigned NOT NULL,
  gateway_name VARCHAR(128) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE directory_global_params (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  param_name VARCHAR(64) NOT NULL,
  param_value VARCHAR(128) NOT NULL,
  domain_id INT(10) unsigned NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE directory_global_vars (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  var_name VARCHAR(64) NOT NULL,
  var_value VARCHAR(128) NOT NULL,
  domain_id INT(10) unsigned NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE directory_params (
  id INT(11) NOT NULL AUTO_INCREMENT,
  directory_id INT(11) DEFAULT NULL,
  param_name VARCHAR(255) DEFAULT NULL,
  param_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE directory_vars (
  id INT(11) NOT NULL AUTO_INCREMENT,
  directory_id INT(11) DEFAULT NULL,
  var_name VARCHAR(255) DEFAULT NULL,
  var_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE iax_conf (
  id INT(11) NOT NULL AUTO_INCREMENT,
  profile_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE iax_settings (
  id INT(11) NOT NULL AUTO_INCREMENT,
  iax_id INT(11) DEFAULT NULL,
  param_name VARCHAR(255) DEFAULT NULL,
  param_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE ivr_conf (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  greet_long VARCHAR(255) NOT NULL,
  greet_short VARCHAR(255) NOT NULL,
  invalid_sound VARCHAR(255) NOT NULL,
  exit_sound VARCHAR(255) NOT NULL,
  max_failures INT(10) unsigned NOT NULL DEFAULT '3',
  timeout INT(11) NOT NULL DEFAULT '5',
  tts_engine VARCHAR(64) DEFAULT NULL,
  tts_voice VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_name (name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE ivr_entries (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  ivr_id INT(10) unsigned NOT NULL,
  action VARCHAR(64) NOT NULL,
  digits VARCHAR(16) NOT NULL,
  params VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_ivr_digits (ivr_id,digits) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE limit_conf (
  id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE limit_data (
  hostname VARCHAR(255) DEFAULT NULL,
  realm VARCHAR(255) DEFAULT NULL,
  id VARCHAR(255) DEFAULT NULL,
  uuid VARCHAR(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE local_stream_conf (
  id INT(11) NOT NULL AUTO_INCREMENT,
  directory_name VARCHAR(255) DEFAULT NULL,
  directory_path text,
  param_name VARCHAR(255) DEFAULT NULL,
  param_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE modless_conf (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  conf_name VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

CREATE TABLE post_load_modules_conf (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  module_name VARCHAR(64) NOT NULL,
  load_module tinyINT(1) NOT NULL DEFAULT '1',
  priority INT(10) unsigned NOT NULL DEFAULT '1000',
  PRIMARY KEY (id),
  UNIQUE KEY unique_mod (module_name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE rss_conf (
  id INT(11) NOT NULL AUTO_INCREMENT,
  directory_id INT(11) NOT NULL,
  feed text NOT NULL,
  local_file text NOT NULL,
  description text,
  priority INT(11) NOT NULL DEFAULT '1000',
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE sip_authentication (
  nonce VARCHAR(255) DEFAULT NULL,
  expires INT(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sip_dialogs (
  call_id VARCHAR(255) DEFAULT NULL,
  uuid VARCHAR(255) DEFAULT NULL,
  sip_to_user VARCHAR(255) DEFAULT NULL,
  sip_to_host VARCHAR(255) DEFAULT NULL,
  sip_from_user VARCHAR(255) DEFAULT NULL,
  sip_from_host VARCHAR(255) DEFAULT NULL,
  contact_user VARCHAR(255) DEFAULT NULL,
  contact_host VARCHAR(255) DEFAULT NULL,
  state VARCHAR(255) DEFAULT NULL,
  direction VARCHAR(255) DEFAULT NULL,
  user_agent VARCHAR(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sip_registrations (
  call_id VARCHAR(255) DEFAULT NULL,
  sip_user VARCHAR(255) DEFAULT NULL,
  sip_host VARCHAR(255) DEFAULT NULL,
  contact VARCHAR(1024) DEFAULT NULL,
  status VARCHAR(255) DEFAULT NULL,
  rpid VARCHAR(255) DEFAULT NULL,
  expires INT(11) DEFAULT NULL,
  user_agent VARCHAR(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sip_subscriptions (
  proto VARCHAR(255) DEFAULT NULL,
  sip_user VARCHAR(255) DEFAULT NULL,
  sip_host VARCHAR(255) DEFAULT NULL,
  sub_to_user VARCHAR(255) DEFAULT NULL,
  sub_to_host VARCHAR(255) DEFAULT NULL,
  event VARCHAR(255) DEFAULT NULL,
  contact VARCHAR(1024) DEFAULT NULL,
  call_id VARCHAR(255) DEFAULT NULL,
  full_from VARCHAR(255) DEFAULT NULL,
  full_via VARCHAR(255) DEFAULT NULL,
  expires INT(11) DEFAULT NULL,
  user_agent VARCHAR(255) DEFAULT NULL,
  accept VARCHAR(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sofia_aliases (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  sofia_id INT(10) unsigned NOT NULL,
  alias_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sofia_conf (
  id INT(11) NOT NULL AUTO_INCREMENT,
  profile_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sofia_domains (
  id INT(11) NOT NULL AUTO_INCREMENT,
  sofia_id INT(11) DEFAULT NULL,
  domain_name VARCHAR(255) DEFAULT NULL,
  parse tinyINT(1) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sofia_gateways (
  id INT(11) NOT NULL AUTO_INCREMENT,
  sofia_id INT(11) DEFAULT NULL,
  gateway_name VARCHAR(255) DEFAULT NULL,
  gateway_param VARCHAR(255) DEFAULT NULL,
  gateway_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE sofia_settings (
  id INT(11) NOT NULL AUTO_INCREMENT,
  sofia_id INT(11) DEFAULT NULL,
  param_name VARCHAR(255) DEFAULT NULL,
  param_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE voicemail_conf (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  vm_profile VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_profile (vm_profile)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE voicemail_email (
  id INT(10) unsigned NOT NULL AUTO_INCREMENT,
  voicemail_id INT(10) unsigned NOT NULL,
  param_name VARCHAR(64) NOT NULL,
  param_value VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_profile_param (param_name,voicemail_id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE voicemail_settings (
  id INT(11) NOT NULL AUTO_INCREMENT,
  voicemail_id INT(11) DEFAULT NULL,
  param_name VARCHAR(255) DEFAULT NULL,
  param_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

