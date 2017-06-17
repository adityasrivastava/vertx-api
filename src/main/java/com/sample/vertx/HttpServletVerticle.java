package com.sample.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

/**
 * Created by adi on 15/06/17.
 */
public class HttpServletVerticle extends AbstractVerticle {

    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";  // <1>
    public static final String CONFIG_WIKIDB_QUEUE = "wikidb.queue";

    private WikiDatabaseService dbService;



    @Override
    public void start(Future<Void> startFuture) throws Exception {

        String wikiDbQueue = config().getString(CONFIG_WIKIDB_QUEUE, "wikidb.queue");
//        dbService = WikiDatabaseService.cre

        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        // Set up routers



    }

    private void apiCreatePage(RoutingContext context)
    {
        JsonObject object = context.getBodyAsJson();

        if (!validateJsonPageDocument(context,object,"name", "markdown"))
        {
            return;
        }




    }

    private boolean validateJsonPageDocument(RoutingContext context, JsonObject page, String... expectedKeys)
    {
        if(!Arrays.stream(expectedKeys).allMatch(page::containsKey))
        {
            context.response().setStatusCode(400);
            context.response().putHeader("Content-Type", "application/json");
            context.response().end(new JsonObject()
                .put("success", false)
                .put("error", "Bad request payload")
                .encode()
            );
            return false;
        }

        return true;
    }


}
