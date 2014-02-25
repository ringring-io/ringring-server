--
-- Users table
--
CREATE TABLE zirgoo_users (
   id                 SERIAL
  ,email              VARCHAR(255) NOT NULL
  ,activation_code    VARCHAR(255) NOT NULL
  ,is_activated       BOOLEAN DEFAULT NULL
  ,created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  ,updated_at         TIMESTAMP NOT NULL DEFAULT '1900-01-01 00:00:00'
  ,PRIMARY KEY (id)
  ,UNIQUE (email)
);


-- 
-- Trigger to automatically update UPDATED_AD column
--
CREATE FUNCTION update_zirgoo_users_updated_at() RETURNS trigger AS '
BEGIN
  new.updated_at = NOW();
  RETURN new;
END
' LANGUAGE plpgsql;

CREATE TRIGGER zirgoo_user_update
BEFORE UPDATE ON zirgoo_users
FOR EACH ROW
EXECUTE PROCEDURE update_zirgoo_users_updated_at();