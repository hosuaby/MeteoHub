package io.hosuaby.cars.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.hosuaby.cars.endpoints.WheelGateway;

//@Component
public class WheelFactory {

    @Autowired
//    @Qualifier("wheelGateway")
    private WheelGateway gateway;

    @Scheduled(fixedRate = 1000)
    public void produce() {
        gateway.publish("Hello!");
    }

}
