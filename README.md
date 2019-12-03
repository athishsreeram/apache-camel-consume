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

# Pre-Requisit k8s setup
https://github.com/athishsreeram/installsteps/blob/master/minikubeinstall.md

# Camel K Deployment Steps

## Install Kamel CLI
cp ~/Downloads/camel-k-client-1.0.0-M2-mac-64bit/kamel ~/bin

kamel version

kamel install

## Run the Route

kamel run -d mvn:com.github.athishsreeram:apache-camel-consume:0.0.1-SNAPSHOT SimpleRouteBuilder.java --dev

# Demo Output

![](https://github.com/athishsreeram/apache-camel-consume/img/1.png)

![](https://github.com/athishsreeram/apache-camel-consume/img/2.png)

![](https://github.com/athishsreeram/apache-camel-consume/img/3.png)

![](https://github.com/athishsreeram/apache-camel-consume/img/4.png)

![](https://github.com/athishsreeram/apache-camel-consume/img/5.png)

![](https://github.com/athishsreeram/apache-camel-consume/img/6.png)

![](https://github.com/athishsreeram/apache-camel-consume/img/7.png)
