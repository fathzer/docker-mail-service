# docker-mail-service

Work in progress

A web service to send emails from a fixed address using an external SMTP server.

The docker image is available on [Docker Hub](https://hub.docker.com/r/fathzer/mail-service)

## Environment variables
- HOST (Optional): The STMP server to use (default is *smtp.gmail.com*, which corresponds to GMail).
- USER (Optional): The user used to authenticate on server (No authentication if missing - This should not be supported by reliable SMTP server).  
Please note that GMail requires an [application password](https://support.google.com/accounts/answer/185833) that is different from the GMail user' password. 
- PWD (Optional): The password used to authenticate on server (Default is empty)
- FROM (Optional if USER is provided): The email address of the sender. if missing USER environment variable is used instead.
- ENCRYPTION (Optional): The following values are allowed:  
    - NONE to have no encryption (This should not be supported anymore by reliable SMTP server).
    - TLS to use TLS encryption.
    - SSL (This is the default) to use SSL encryption.
- PORT (Optional): The port to use. By default, it depends on the encryption (25, 587 or 465).