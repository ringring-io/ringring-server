-- 
-- Insert static datasets
-- 

INSERT INTO dialplan VALUES (1,'sip.zirgoo.com','127.0.0.1');

INSERT INTO dialplan_context VALUES (1,1,'context_zirgoo',10);

INSERT INTO directory_domains VALUES (1,'sip.zirgoo.com');

COMMIT;