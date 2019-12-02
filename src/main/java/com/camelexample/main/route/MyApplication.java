package com.camelexample.main.route;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class MyApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApplication.class);

    private MyApplication() {
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.addRouteBuilder(new SimpleRouteBuilder());
        main.run(args);
    }

}

