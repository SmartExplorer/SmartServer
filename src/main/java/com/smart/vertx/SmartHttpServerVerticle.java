package com.smart.vertx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smart.handlers.DeviceRequestHandler;
import com.smart.services.VertxRunner;

import io.vertx.core.AbstractVerticle;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class SmartHttpServerVerticle extends AbstractVerticle {
	private static final Logger logger = LogManager.getLogger(SmartHttpServerVerticle.class);
	@Override
	public void start(Future<Void> fut) {
		Router router = Router.router(vertx);

		// Bind "/" to our hello message - so we are still compatible.
		router.route("/debug/").handler(DeviceRequestHandler::handleTestRequest);
		router.route("/docs/*").handler(StaticHandler.create("raml"));
		router.route("/device/:deviceType").method(HttpMethod.GET)
			.handler(DeviceRequestHandler::handleDeviceGETRequest);
		router.route("/device/:deviceType").method(HttpMethod.POST)
			.handler(DeviceRequestHandler::handleDevicePOSTRequest);
		vertx.createHttpServer().requestHandler(router::accept)
			.listen(8080, 
					result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}
}
