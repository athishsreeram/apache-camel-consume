package com.camelexample.main.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

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

        rest("/say")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .get("/employees").outType(Object.class).to("direct:employees")
                .get("/employees/1").outType(Object.class).to("direct:employee1");

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


    }


}
