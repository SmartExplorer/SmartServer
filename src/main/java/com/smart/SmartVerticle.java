package com.smart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smart.Main.SmartMain;

import io.vertx.core.AbstractVerticle;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class SmartVerticle extends AbstractVerticle {
	private static final Logger logger = LogManager.getLogger(SmartMain.class);
	@Override
	public void start(Future<Void> fut) {
		Router router = Router.router(vertx);

		// Bind "/" to our hello message - so we are still compatible.
		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Hello from my first Vert.x 3 application</h1>");
		});
		router.route("/assets/*").handler(StaticHandler.create("assets"));
		vertx.createHttpServer().requestHandler(router::accept)
			.listen(8080, result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}
}
