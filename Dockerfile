FROM openjdk:11-jre

ENV RUN_USER mailservice
ENV RUN_GROUP mailservice
RUN groupadd -r ${RUN_GROUP} && useradd -g ${RUN_GROUP} -m -s /bin/bash ${RUN_USER}

WORKDIR /home/${RUN_USER}

COPY target/mail-service.jar .

RUN chown -R ${RUN_USER}:${RUN_USER} .
USER ${RUN_USER}

EXPOSE 8080

CMD ["java", "-Xmx128m", "-jar", "mail-service.jar"]
