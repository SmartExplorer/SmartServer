package com.smart.main;

import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.smart.vertx.SmartVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
public class SmartMain {
	
	private static final Logger logger = LogManager.getLogger(SmartMain.class);
	public static void main(String[] args) {
		vertxRunner();
	}
	public static void vertxRunner() {
		JsonObject config = null;
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:*/bean.xml")) {
				config = (JsonObject)context.getBean("serverConfig");
			}
		Objects.requireNonNull(config);
		DeploymentOptions options = new DeploymentOptions()
			    .setConfig(new JsonObject().put("http.port", 6553)
			);
		
		Vertx.vertx().deployVerticle(SmartVerticle.class.getName(), options, 
				res -> {
	                if (res.succeeded()) {
	                	logger.info("Verticle deployed.");                
	                    logger.info("Verticle id:" +  res.result());      
	                }

	                else{
	                    logger.info("Verticle deployment faild.");         
	                }
	            });
	}
}
