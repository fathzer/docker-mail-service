# docker-mail-service

Work in progress

A web service to send emails from a fixed address using an external SMTP server.

The docker image is available on [Docker Hub](https://hub.docker.com/r/fathzer/mail-service)

## Environment variables
- HOST (optional): The STMP server to use (default is *smtp.gmail.com*, which corresponds to GMail).
- USER (optional): The user used to authenticate on server (No authentication if missing - This should not be supported by reliable SMTP server).  
Please note that GMail requires an [application password](https://support.google.com/accounts/answer/185833) that is different from the GMail user' password. 
- PWD (optional): The password used to authenticate on server (Default is empty)
- FROM (optional if USER is provided): The email address of the sender. if missing USER environment variable is used instead.
- ENCRYPTION (optional): The following values are allowed:  
    - NONE to have no encryption (This should not be supported anymore by reliable SMTP server).
    - TLS to use TLS encryption.
    - SSL (This is the default) to use SSL encryption.
- PORT (optional): The port to use. By default, it depends on the encryption (25, 587 or 465).
- AUTHORIZED_DEST (optional): A comma separated list of recipient emails that will be accepted be the service. This allows you to prevent an attacker from using your service to send junk emails to anybody.

## Open API documentation
After the server is launched, open api documentation is available at [http://127.0.0.1:8080/v3/api-docs](http://127.0.0.1:8080/v3/api-docs).  
You can view it with a human friendle interface at [http://127.0.0.1:8080/swagger-ui/index.html](http://127.0.0.1:8080/swagger-ui/index.html).