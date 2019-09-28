FROM arm32v7/openjdk:11.0.1
MAINTAINER Bard Lind <bard.lind@gmail.com>
MAINTAINER Bard Lind <bard.lind@gmail.com>
#RUN yum -y install yum-cron
#RUN yum -y update
#RUN yum -y install curl

# Install Application
RUN adduser bacnetiot
ADD target/bacnetiot*.jar /home/bacnetiot/bacnetiot.jar
#ADD docker/bacnetiot_override.properties /home/bacnetiot/bacnetiot-override.properties
#RUN chown bacnetiot:bacnetiot /home/bacnetiot/bacnetiot.properties

#EXPOSE 21500:21599

WORKDIR "/home/bacnetiot"
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
    "bacnetiot.jar" \
]


