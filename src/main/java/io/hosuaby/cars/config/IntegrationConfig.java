package io.hosuaby.cars.config;

import io.hosuaby.cars.domain.NormalizedWeatherMeasures;
import io.hosuaby.cars.dto.MeteoPlusInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.mongodb.outbound.MongoDbStoringMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration of Spring Integration.
 */
@Configuration
@EnableIntegration
public class IntegrationConfig {

  @Autowired
  private MongoDbFactory mongoDbFactory;

  @Bean
  public HttpRequestHandlingMessagingGateway MeteoPlusInboundGateway() {
    HttpRequestHandlingMessagingGateway gateway =
        new HttpRequestHandlingMessagingGateway();

    RequestMapping mapping = new RequestMapping();
    mapping.setMethods(HttpMethod.POST);
    mapping.setPathPatterns("/meteoplus");

    gateway.setRequestMapping(mapping);
    gateway.setRequestChannel(meteoPlusInboundChannel());
    gateway.setRequestPayloadType(MeteoPlusInput.class);

    return gateway;
  }

  @Bean
  public MessageChannel meteoPlusInboundChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  public IntegrationFlow meteoPlusFlow() {
    return IntegrationFlows
        .from(meteoPlusInboundChannel())
        .transform((MeteoPlusInput input) -> {
          NormalizedWeatherMeasures nwm = new NormalizedWeatherMeasures();
          nwm.setTemperature(input.getTemperature());
          return nwm;
        })
        .channel(normalizedMeasuresChannel())
        .get();
  }

  @Bean
  public MessageChannel normalizedMeasuresChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  public IntegrationFlow persistanceFlow() {
    return IntegrationFlows
        .from(normalizedMeasuresChannel())
        .handle(new MongoDbStoringMessageHandler(mongoDbFactory), c -> {
          c.get().getT2().setCollectionNameExpression(
                  new LiteralExpression("nwm"));
        })
        .get();
  }

//  @Transformer(inputChannel = "requestChannel", outputChannel = "logChannel")
//  public NormalizedWeatherMeasures normalize(Message message) {
//    NormalizedWeatherMeasures measures = new NormalizedWeatherMeasures();
//    measures.setTemperature(45);
//    return measures;
//  }


//  @Bean
//  @Autowired
//  public MongoDbStoringMessageHandler mongoDbWriter(
//        final MongoDbFactory mongoDbFactory) {
//    MongoDbStoringMessageHandler writer =
//        new MongoDbStoringMessageHandler(mongoDbFactory);
////    writer.
//    return writer;
//  }

//  @Bean
//  public MessageChannel requestChannel() {
//    return new DirectChannel();
//  }

  @Bean
  public MessageChannel logChannel() {
    return new PublishSubscribeChannel();
  }

//  @Bean
//  public MessageChannel persisterChannel() {
//    return new DirectChannel();
//  }

  @Bean
  @ServiceActivator(inputChannel = "logChannel")
  public MessageHandler logger() {
    LoggingHandler logger = new LoggingHandler(
        LoggingHandler.Level.INFO.name());
    logger.setShouldLogFullMessage(true);
    return logger;
  }

}
