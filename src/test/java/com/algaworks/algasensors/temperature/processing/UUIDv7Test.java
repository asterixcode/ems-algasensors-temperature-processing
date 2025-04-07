package com.algaworks.algasensors.temperature.processing;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UUIDv7Test {

  @Test
  void shouldGenerateUUIDv7() {
    UUID uuid = IdGenerator.generateTimeBasedUUID();

    OffsetDateTime uuidDateTime =
        UUIDv7Utils.extractOffsetDateTime(uuid).truncatedTo(ChronoUnit.MINUTES);
    OffsetDateTime currentDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    Assertions.assertThat(uuidDateTime).isEqualTo(currentDateTime);
  }
}
