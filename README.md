# APACHE CAMEL Example

# Expose REST by Consuming  SOAP

REST Req -> cxf SOAP Client Call -> REST Resp

http://localhost:8080/camel/say/add

{
  "intA": 1,
  "intB": 2
}


Domain SOAP Service src/main/resources/service.wsdl


# Expose REST by Consuming  REST

REST Req -> HTTP Client Call -> REST Resp

http://localhost:8080/camel/say/emp/1

Domain REST Service http://localhost:8082/employees


# Camel K Deployment Steps

cp ~/Downloads/camel-k-client-1.0.0-M2-mac-64bit/kamel ~/bin

kamel version

kamel install

Publish you jar into maven repo public or private
Configure you In camel-k-maven-settings.xml the public/private repo

kamel run -d mvn:com.github.athishsreeram:apache-camel-consume:0.0.1-SNAPSHOT SimpleRouteBuilder.java --dev