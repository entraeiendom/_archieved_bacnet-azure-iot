FROM arm32v7/openjdk:11.0.1
MAINTAINER Bard Lind <bard.lind@gmail.com>
#RUN yum -y install yum-cron
#RUN yum -y update
#RUN yum -y install curl

# Install Application
#RUN adduser bacnetagent
# Create a group and user
#RUN addgroup -S bacnet && adduser -S bacnetagent -G bacnet
ADD target/bacnetagent*.jar /home/bacnetagent/bacnetagent.jar
#ADD docker/bacnetagent_override.properties /home/bacnetagent/bacnetagent-override.properties
#RUN chown bacnetagent:bacnetagent /home/bacnetagent/bacnetagent.properties

#EXPOSE 21500:21599

WORKDIR "/home/bacnetagent"
CMD [ \
    "java", \
    "-Xdebug", \
#    "-Xrunjdwp:transport=dt_socket,address=21515,server=y,suspend=n", \
#    "-Dcom.sun.management.jmxremote.port=21516", \
#    "-Dcom.sun.management.jmxremote.rmi.port=21516", \
#    "-Dcom.sun.management.jmxremote.ssl=false", \
#    "-Dcom.sun.management.jmxremote.local.only=false", \
#    "-Dcom.sun.management.jmxremote.authenticate=false", \
#    "-Djava.rmi.server.hostname=localhost", \
    "-jar", \
    "bacnetagent.jar" \
]


