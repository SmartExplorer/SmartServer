package com.smart.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class RequestHandler {
	
	private static final Logger logger = LogManager.getLogger(RequestHandler.class);
	
	public static void handleDeviceRequest(RoutingContext routingContext) {
		String deviceId = routingContext.request().getParam("deviceID");
		logger.info(deviceId);
		String Dtype = routingContext.request().getParam("deviceType");
		byte[] encoded = null;
		String json = null;
		 try {
			encoded = Files.readAllBytes(Paths.get("src/main/resources/test.json"));
			json = new String(encoded,"UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject jsonObject = new JsonObject(json);
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/json")
			.end(jsonObject.encode());
	}
	public static void handleTestRequest(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/json")
			.end("a\na\na\na\n");
	}
}
