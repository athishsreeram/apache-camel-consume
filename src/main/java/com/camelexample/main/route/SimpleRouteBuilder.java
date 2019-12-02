package com.camelexample.main.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.cxf.message.MessageContentsList;
import org.tempuri.Add;
import org.tempuri.AddResponse;

import javax.ws.rs.core.MediaType;

public class SimpleRouteBuilder extends RouteBuilder {


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

        rest("/say")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .get("/employees").outType(Object.class).to("direct:employees")
                .get("/employees/1").outType(Object.class).to("direct:employee1")
                .post("/add").type(Add.class).outType(AddResponse.class).to("direct:add")
                .get("/emp/{id}").outType(EmployeeDTO.class).to("direct:getEmployee");

        // Rest Endpoint
        from("direct:employees")
                .process(exchange -> {
                    String ar = "[{\"id\":\"1\",\"name\":\"Employee 1\"},{\"id\":\"2\",\"name\":\"Employee 2\"},{\"id\":\"3\",\"name\":\"Employee 3\"},{\"id\":\"4\",\"name\":\"Employee 4\"},{\"id\":\"5\",\"name\":\"Employee 5\"},{\"id\":\"6\",\"name\":\"Employee 6\"},{\"id\":\"7\",\"name\":\"Employee 7\"},{\"id\":\"8\",\"name\":\"Employee 8\"},{\"id\":\"9\",\"name\":\"Employee 9\"},{\"id\":\"10\",\"name\":\"Employee 10\"}]";

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(ar);
                    exchange.getOut().setBody(actualObj);

                });

        // Rest Endpoint
        from("direct:employee1")
                .process(exchange -> {
                    String ar = "{\"id\":\"1\",\"name\":\"Employee 27\"}";
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(ar);
                    exchange.getOut().setBody(actualObj);

                });

        // Rest -> SOAP Route
        from("direct:add")
                .log(LoggingLevel.INFO, " Body : ${body}")
                .process(exchange -> {
                    Add a = exchange.getIn().getBody(Add.class);
                    exchange.getOut().setBody(new Object[]{new Integer(a.getIntA()), new Integer(a.getIntB())});

                })
                .setHeader("operationName", constant("Add"))
                .to("cxf://http://www.dneonline.com/calculator.asmx?serviceClass=org.tempuri.CalculatorSoap&wsdlURL=src/main/resources/service.wsdl&loggingFeatureEnabled=true")
                .log(LoggingLevel.INFO, "The response was ${body}").setHeader(Exchange.CONTENT_TYPE, constant("application/json")).process(
                exchange -> {
                    MessageContentsList soapMessage = (MessageContentsList) exchange.getIn().getBody();
                    if (soapMessage == null) {
                        System.out.println("Incoming null message detected...");

                    }
                    Integer test = (Integer) soapMessage.get(0);
                    System.out.println(test);
                    AddResponse ar = new AddResponse();
                    ar.setAddResult(test);
                    exchange.getOut().setBody(ar);
                });

        // Rest ->  Rest Route
        from("direct:getEmployee")
                .log(LoggingLevel.INFO, " Header : ${header.id}")
                .removeHeader(Exchange.HTTP_URI)
                .removeHeader(Exchange.HTTP_PATH)
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader(Exchange.HTTP_PATH, simple("${header.id}"))
                .streamCaching()
                .to("http://localhost:8080/camel/say/employees")
                .log(LoggingLevel.INFO, "${body}")
                .process(
                        exchange -> {
                            String jsonGotFromCall = exchange.getIn().getBody(String.class);
                            //Unmarshall the JSON to Pojo
                            ObjectMapper mapper = new ObjectMapper();
                            EmployeeDTO emp = mapper.readValue(jsonGotFromCall, EmployeeDTO.class);
                            emp.setOrg("CS");
                            // Do Any Convertion
                            exchange.getOut().setBody(emp);
                        });

    }


}
