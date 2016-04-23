package io.hosuaby.meteohub.config;

import io.hosuaby.meteohub.domain.NormalizedWeatherMeasures;
import io.hosuaby.meteohub.dto.MeteoPlusInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.interceptor.WireTap;
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
    return MessageChannels
        .publishSubscribe()
        .interceptor(new WireTap(logChannel()))
        .get();
  }

  @Bean
  public IntegrationFlow meteoPlusFlow() {
    return IntegrationFlows
        .from(meteoPlusInboundChannel())
        .<MeteoPlusInput, NormalizedWeatherMeasures>transform(input -> {
          NormalizedWeatherMeasures nwm = new NormalizedWeatherMeasures();
          nwm.setTemperature(input.getTemperature());
          nwm.setHumidity(input.getHumidity());
          nwm.setMesureMoment(input.getMesureDateTime());
          nwm.setCoordinates(input.getCoordinates().toArray(new Double[2]));
          nwm.setSource(input.getDeviceId().toString());
          return nwm;
        })
        .channel(normalizedMeasuresChannel())
        .get();
  }

  @Bean
  public IntegrationFlow meteoPlusRawInputPersistenceFlow() {
    return IntegrationFlows
        .from(meteoPlusInboundChannel())
        .handle(new MongoDbStoringMessageHandler(mongoDbFactory), c -> {
          c.get().getT2().setCollectionNameExpression(
              new LiteralExpression("meteoPlusRawInput"));
        })
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

  /**
   * Direct channel to print logged messages immediately after sent to
   * logChannel.
   */
  @Bean
  public MessageChannel logChannel() {
    return MessageChannels.direct("logChannel").get();
  }

  @Bean
  @ServiceActivator(inputChannel = "logChannel")
  public MessageHandler logger() {
    LoggingHandler logger = new LoggingHandler(
        LoggingHandler.Level.INFO.name());
    logger.setShouldLogFullMessage(true);
    return logger;
  }

}
