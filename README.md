[![Build Status](https://travis-ci.org/ringring-io/ringring-server.png)](https://travis-ci.org/ringring-io/ringring-server)

# Ringring.io RESTful backend #

Ringring.io (http://www.ringring.io) is a Phone and Messaging application built for privacy with open architectures. You can originate calls and send messages in an easy and secured way with no data-collecting man or company in the middle. The technology is freely available for public.

To run app:

    mvn clean compile exec:java -Dexec.mainClass=io.ringring.server.Main

The application will automatically set up and migrate the database to the latest version if required.

To test that it's working go to:

    http://localhost:8080/foo

Do not forget to set everything in the **server.properties** before run.

