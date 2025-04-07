package com.algaworks.algasensors.temperature.processing;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class UUIDv7Utils {

  private UUIDv7Utils() {
    // Private constructor to prevent instantiation
  }

  public static OffsetDateTime extractOffsetDateTime(UUID uuid) {
    if (uuid == null) {
      return null;
    }
    if (uuid.version() != 7) {
      throw new IllegalArgumentException("UUID is not version 7");
    }

    // UUIDv7 is a time-based UUID, so we can extract the timestamp from it
    long timestamp = uuid.getMostSignificantBits() >>> 16;
    return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
  }
}
