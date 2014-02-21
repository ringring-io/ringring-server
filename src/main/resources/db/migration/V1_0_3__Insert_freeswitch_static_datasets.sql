-- 
-- Insert static datasets
-- 

LOCK TABLES dialplan WRITE;
INSERT INTO dialplan VALUES (1,'sip.zirgoo.com','127.0.0.1');
UNLOCK TABLES;

LOCK TABLES dialplan_context WRITE;
INSERT INTO dialplan_context VALUES (1,1,'context_zirgoo',10);
UNLOCK TABLES;

LOCK TABLES directory_domains WRITE;
INSERT INTO directory_domains VALUES (1,'sip.zirgoo.com');
UNLOCK TABLES;
