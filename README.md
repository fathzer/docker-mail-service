# docker-mail-service

A web service to send emails from a fixed address using an external SMTP server.

The docker image is available on [Docker Hub](https://hub.docker.com/r/fathzer/mail-service). Source code is available on [GitHub](https://github.com/fathzer/docker-mail-service).

It is based on the [com.fathzer:mail-sender](https://github.com/fathzer/docker-mail-service/mail-sender) library available on Maven Central ![Maven Central](https://img.shields.io/maven-central/v/com.fathzer/mail-sender).

# How to use the image

docker run -d --rm -e HOST_USER="me@gmail.com" -e HOST_PWD="myapppwd" -p 8080:8080 fathzer/mail-service

## Environment variables
All environment variables are optional. Nevertheless, some smtp server's will require some (typically USER and PWD).

- HOST (optional): The STMP server to use (default is *smtp.gmail.com*, which corresponds to GMail).
- HOST_USER (optional): The user used to authenticate on server (No authentication if missing - This should not be supported by reliable SMTP server).  
Please note that GMail requires an [application password](https://support.google.com/accounts/answer/185833) that is different from the GMail user' password. 
- HOST_PWD (optional): The password used to authenticate on server (Default is empty)
- FROM (optional if USER is provided): The email address of the sender. if missing USER environment variable is used instead.
- ENCRYPTION (optional): The following values are allowed:  
    - NONE to have no encryption (This should not be supported anymore by reliable SMTP server).
    - TLS to use TLS encryption.
    - SSL (This is the default) to use SSL encryption.
- PORT (optional): The port to use. By default, it depends on the encryption (25, 587 or 465).
- AUTHORIZED_DEST (optional): A comma separated list of recipient emails that will be accepted be the service.
This allows you to prevent an attacker from using your service to send junk emails to anybody.
If this variable is not defined, all recipients are authorized.
- JAVA_OPTS: command line options passed to the java virtual machine. It is typically used to set the memory size (example -e "JAVA_OPTS=-Xmx128m").  

## Open API documentation
After the server is launched, open api documentation is available at [http://127.0.0.1:8080/v3/api-docs](http://127.0.0.1:8080/v3/api-docs).  
You can view it with a human friendle interface at [http://127.0.0.1:8080/swagger-ui/index.html](http://127.0.0.1:8080/swagger-ui/index.html).

# Developer notes
## How to run the service without docker?
Run the class *com.fathzer.mailservice.MailApplication*.
## My developement environment reports compilation errors on the project.
First check your environment is compatible with java 17.

If it is, maybe you should [install lombok](https://projectlombok.org/) in your environment.
## How to build the docker image.
You can build it with Maven: ```mvn package``` or with the *docker build* command: ```docker build -t fathzer/mail-service:latest .```

