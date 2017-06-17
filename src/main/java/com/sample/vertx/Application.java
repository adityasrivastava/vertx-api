package com.sample.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by adi on 14/06/17.
 */
public class Application {

    public static void main(String[] args){

        VertxOptions options = new VertxOptions();
        options.setBlockedThreadCheckInterval(200000000);

        Vertx v = Vertx.vertx(options);

    }

}
