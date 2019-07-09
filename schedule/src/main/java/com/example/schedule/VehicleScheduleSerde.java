package com.example.schedule;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public final class VehicleScheduleSerde extends Serdes.WrapperSerde<VehicleSchedule> {
	public VehicleScheduleSerde() {
		super(new JsonSerializer<>(), new JsonDeserializer<>(VehicleSchedule.class));
	}
}
