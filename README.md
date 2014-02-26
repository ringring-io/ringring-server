[![Stories in Ready](https://badge.waffle.io/zirgoo/zirgoo-server.png?label=ready&title=Ready)](https://waffle.io/zirgoo/zirgoo-server)
[![Build Status](https://travis-ci.org/zirgoo/zirgoo-server.png)](https://travis-ci.org/zirgoo/zirgoo-server)
zirgoo-server
=============

RESTful backend

To run app:

    mvn clean compile exec:java -Dexec.mainClass=com.zirgoo.server.Main

The application will automaticly set up and migrate the database to the latest version if required.    

To test that it's working go to:

    http://localhost:8080/foo

Do not forget to set everything in the **server.properties** before run.

