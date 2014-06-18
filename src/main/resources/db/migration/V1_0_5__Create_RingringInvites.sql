--
-- Invites table
--
CREATE TABLE ringring_invites (
   id                 SERIAL
  ,invite_from        VARCHAR(255) NOT NULL
  ,invite_to          VARCHAR(255) NOT NULL
  ,created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  ,updated_at         TIMESTAMP NOT NULL DEFAULT '1900-01-01 00:00:00'
  ,PRIMARY KEY (id)
);


--
-- Trigger to automatically update UPDATED_AD column
--
CREATE FUNCTION update_ringring_invites_updated_at() RETURNS trigger AS '
BEGIN
  new.updated_at = NOW();
  RETURN new;
END
' LANGUAGE plpgsql;

CREATE TRIGGER ringring_invite_update
BEFORE UPDATE ON ringring_invites
FOR EACH ROW
EXECUTE PROCEDURE update_ringring_invites_updated_at();