# Build with username and password to download from BSCW
# docker build --build-arg username=user --build-arg password=pw .

# Mountpoint for configuration file
# /usr/local/tomcat/webapps/scral/WEB-INF/classes/applicationContext-pwal.xml

FROM tomcat:jre8

ARG username=none
ARG password=none

workdir /usr/local/tomcat/webapps

RUN apt-get update && apt-get install -y \
    vim \
    curl
# Downloads, extracts and removes

ADD it.ismb.pert.pwal.rest/target/connectors.rest.war .
RUN mv connectors.rest.war scral.war
#RUN curl -o scral.war -u $username:$password https://fit-bscw.fit.fraunhofer.de/bscw/bscw.cgi/d44292218/scral.war
RUN unzip scral.war -d scral
RUN rm scral.war
RUN mkdir /config
RUN mv /usr/local/tomcat/webapps/scral/WEB-INF/classes/applicationContext.xml /config
RUN ln -s /config/applicationContext.xml /usr/local/tomcat/webapps/scral/WEB-INF/classes/applicationContext.xml

RUN echo '<Context> <Resources allowLinking="true" /> </Context>' >> /usr/local/tomcat/webapps/scral/META-INF/context.xml

EXPOSE 8080
VOLUME /config
