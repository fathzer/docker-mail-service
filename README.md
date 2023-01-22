# docker-mail-service

A web service to send emails from a fixed address using an external SMTP server.

The docker image is available on [Docker Hub](https://hub.docker.com/r/fathzer/mail-service). Source code is available on [GitHub](https://github.com/fathzer/docker-mail-service).

It is based on the [com.fathzer:mail-sender](https://github.com/fathzer/mail-sender) java library available on [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fathzer/mail-sender/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fathzer/mail-sender).

# How to use it
Please note that all examples are based on GMail smtp host.  
It requires an [application password](https://support.google.com/accounts/answer/185833) that is different from the GMail user's password.  

**Please note that this server is not secured. Any one with a network access to the server can send emails!**  
It was designed to be used as an internal service in a docker stack, not to be exposed outside of its stack.  
Even when used if not exposed, it is safer to limit the accepted recipients using the AUTHORIZED_RECIPIENTS environment variable (see below).

## With docker

Replace, in the following command, the HOST_USER and HOST_PWD with the credentials of a GMail account you own.  
Then run the following command:  
```docker run -d --rm -e HOST_USER="me@gmail.com" -e HOST_PWD="myapppwd" -p 8080:8080 fathzer/mail-service```

## With 17+ java VM

Compile the project with Maven:  
```mvn clean package```
Define the HOST_USER and HOST_PWD with the credentials of a GMail account.
Then run the following command:    
```java -jar mail-service.jar```

Here is a cUrl command to send a basic message:
```
curl --location --request POST '127.0.0.1:8080/v1/mails' --header 'Content-Type: application/json' \
--data-raw '{
    "to":["someone@domain.com"],
    "content":"A basic message"
}
'
```

## Open API documentation
After the server is launched, open api documentation is available at [http://127.0.0.1:8080/v3/api-docs](http://127.0.0.1:8080/v3/api-docs).  
You can view it with a human friendly interface at [http://127.0.0.1:8080/swagger-ui/index.html](http://127.0.0.1:8080/swagger-ui/index.html).

## Environment variables
All environment variables are optional. Nevertheless, some smtp server's will require some (typically HOST_USER and HOST_PWD).

- HOST (optional): The STMP server to use (default is *smtp.gmail.com*, which corresponds to GMail).
- HOST_USER (optional): The user used to authenticate on server (No authentication if missing - This should not be supported by reliable SMTP server).
- HOST_PWD (optional): The password used to authenticate on server (Default is empty).  
Please remember GMail requires an [application password](https://support.google.com/accounts/answer/185833) that is different from the GMail user' password. 
- FROM (optional if USER is provided): The email address of the sender. if missing USER environment variable is used instead.
- ENCRYPTION (optional): The following values are allowed:  
    - NONE to have no encryption (This should not be supported anymore by reliable SMTP server).
    - TLS (This is the default) to use TLS encryption.
    - SSL to use SSL encryption.
- PORT (optional): The port to use. By default, it depends on the encryption (25, 587 or 465).
- AUTHORIZED_RECIPIENTS (optional): A comma separated list of recipient emails that will be accepted by the service.
This allows you to prevent an attacker from using your service to send junk emails to anybody.
If this variable is not defined, all recipients are authorized.
- JAVA_OPTS: command line options passed to the java virtual machine. It is typically used to set the memory size (example -e "JAVA_OPTS=-Xmx128m").  


# Developer notes
## How to run the service from my development environment?
Run the class *com.fathzer.mailservice.MailApplication*.
## My developement environment reports compilation errors on the project.
First check your environment is compatible with java 17.

If it is, maybe you should [install lombok](https://projectlombok.org/) in your environment.
## How to build the docker image.
You can build it with Maven: ```mvn package -P docker``` or with the *docker build* command: ```docker build -t fathzer/mail-service:latest .```
