--
-- Users table
--
CREATE TABLE zirgoo_users (
   id                 INT(11) NOT NULL AUTO_INCREMENT
  ,email              VARCHAR(255) NOT NULL
  ,activation_code    VARCHAR(255) NOT NULL
  ,is_activated       TINYINT(1) DEFAULT NULL
  ,created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  ,updated_at         TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'
  ,PRIMARY KEY (id)
  ,UNIQUE KEY email (email)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


-- 
-- Trigger to automatically update UPDATED_AD column
-- 
CREATE TRIGGER zirgoo_user_update
BEFORE UPDATE ON zirgoo_users
FOR EACH ROW SET  NEW.updated_at = NOW()
                 ,NEW.created_at = OLD.created_at;
