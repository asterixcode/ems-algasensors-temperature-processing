package com.algaworks.algasensors.temperature.processing.api.controller;

import com.algaworks.algasensors.temperature.processing.api.model.TemperatureLogOutput;
import com.algaworks.algasensors.temperature.processing.common.IdGenerator;
import com.algaworks.algasensors.temperature.processing.infrastructure.rabbitmq.RabbitMQConfig;
import io.hypersistence.tsid.TSID;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors/{sensorId}/temperatures/data")
@Slf4j
public class TemperatureProcessingController {

  private final RabbitTemplate rabbitTemplate;

  public TemperatureProcessingController(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
  public void data(@PathVariable TSID sensorId, @RequestBody String input) {
    if (input == null || input.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    Double temperature;

    try {
      temperature = Double.parseDouble(input);
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid temperature value");
    }

    TemperatureLogOutput logOutput =
        TemperatureLogOutput.builder()
            .id(IdGenerator.generateTimeBasedUUID())
            .sensorId(sensorId)
            .registeredAt(OffsetDateTime.now())
            .value(temperature)
            .build();

    log.info(logOutput.toString());

    String exchange = RabbitMQConfig.FANOUT_EXCHANGE_NAME;
    String routingKey = "";
    Object payload = temperature;

    MessagePostProcessor messagePostProcessor =
        message -> {
          message.getMessageProperties().setHeader("sensorId", logOutput.sensorId().toString());
          return message;
        };

    rabbitTemplate.convertAndSend(exchange, routingKey, payload, messagePostProcessor);
  }
}
