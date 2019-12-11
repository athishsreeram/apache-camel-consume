package com.camelexample.main.route;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.cxf.message.MessageContentsList;

import javax.ws.rs.core.MediaType;

public class SimpleRouteBuilder2 extends RouteBuilder {


    @Override
    public void configure() throws Exception {


        restConfiguration().component("netty-http").bindingMode(RestBindingMode.json)
                // and output using pretty print
                .dataFormatProperty("prettyPrint", "true")
                // setup context path and port number that netty will use
                .contextPath("/camel").port(8080)
                // add swagger api-doc out of the box
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "User API").apiProperty("api.version", "1.2.3")
                // and enable CORS
                .apiProperty("cors", "true");

        rest("/said")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .get("/employees").outType(Object.class).to("direct:employees")
                .get("/employees/{id}").outType(Object.class).to("direct:employee1")
                .post("/add").type(JsonNode.class).outType(JsonNode.class).to("direct:add");

        // Rest Endpoint
        from("direct:employees")
                .removeHeader(Exchange.HTTP_URI)
                .removeHeader(Exchange.HTTP_PATH)
                .process(exchange -> {
                    String ar = "[{\"id\":\"1\",\"name\":\"Employee 1\"},{\"id\":\"2\",\"name\":\"Employee 2\"},{\"id\":\"3\",\"name\":\"Employee 3\"},{\"id\":\"4\",\"name\":\"Employee 4\"},{\"id\":\"5\",\"name\":\"Employee 5\"},{\"id\":\"6\",\"name\":\"Employee 6\"},{\"id\":\"7\",\"name\":\"Employee 7\"},{\"id\":\"8\",\"name\":\"Employee 8\"},{\"id\":\"9\",\"name\":\"Employee 9\"},{\"id\":\"10\",\"name\":\"Employee 10\"}]";

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(ar);
                    exchange.getOut().setBody(actualObj);

                });

        // Rest Endpoint
        from("direct:employee1")
                .log(LoggingLevel.INFO, " Header : ${header.id}")
                .removeHeader(Exchange.HTTP_URI)
                .removeHeader(Exchange.HTTP_PATH)
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .streamCaching()
                .to("http://localhost:8080/camel/said/employees")
                .log(LoggingLevel.INFO, "${body}")
                .process(
                        exchange -> {
                            String pathInput = (String)exchange.getIn().getHeader("id");
                            String jsonGotFromCall = exchange.getIn().getBody(String.class);
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode actualObjs = mapper.readTree(jsonGotFromCall);
                            JsonNode actualObj = null;
                            if (actualObjs.isArray()) {
                                for (JsonNode node : actualObjs) {
                                    String id = node.path("id").asText();

                                    if(pathInput.equals(id)) {
                                        actualObj = node;
                                        break;
                                    }
                                }
                            }
                            exchange.getOut().setBody(actualObj);
                 });


        // Rest -> SOAP Route
        from("direct:add")
                .log(LoggingLevel.INFO, " Body : ${body}")
                .process(exchange -> {
                    Object jsonGotFromCall = exchange.getIn().getBody();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    JsonNode actualObj = mapper.readTree(String.valueOf(jsonGotFromCall));

                    exchange.getOut().setBody(new Object[]{new Integer(actualObj.path("a").asInt()), new Integer(actualObj.path("b").asInt())});

                })
                .setHeader("operationName", constant("Add"))
                .to("cxf://http://www.dneonline.com/calculator.asmx?serviceClass=org.tempuri.CalculatorSoap&wsdlURL=src/main/resources/service.wsdl&loggingFeatureEnabled=true")
                .log(LoggingLevel.INFO, "The response was ${body}").setHeader(Exchange.CONTENT_TYPE, constant("application/json")).process(
                exchange -> {
                    MessageContentsList soapMessage = (MessageContentsList) exchange.getIn().getBody();
                    if (soapMessage == null) {
                        System.out.println("Incoming null message detected...");

                    }
                    Integer sum = (Integer) soapMessage.get(0);

                    String jsonStr = "{\"sum\" :"+ sum +"}";
                    ObjectMapper mapper = new ObjectMapper();

                    JsonNode actualObj = mapper.readTree(jsonStr);
                    exchange.getOut().setBody(actualObj);
                });

    }


}
