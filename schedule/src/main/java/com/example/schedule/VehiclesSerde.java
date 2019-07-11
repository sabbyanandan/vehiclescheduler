package com.example.schedule;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class VehiclesSerde implements Serde<Vehicles> {
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {}

	@Override
	public void close() {}

	@Override
	public Serializer<Vehicles> serializer() {
		return new Serializer<Vehicles>() {
			@Override
			public void configure(final Map<String, ?> map, final boolean b) {
			}

			@Override
			public byte[] serialize(final String s, final Vehicles vehicles) {
				JsonSerializer<Vehicles> jsonSerializer = new JsonSerializer<>();
				return jsonSerializer.serialize(s, vehicles);
			}

			@Override
			public void close() {

			}
		};
	}

	@Override
	public Deserializer<Vehicles> deserializer() {
		return new Deserializer<Vehicles>() {
			@Override
			public void configure(final Map<String, ?> map, final boolean b) {}

			@Override
			public Vehicles deserialize(final String s, final byte[] bytes) {
				if (bytes.length == 0) {
					return null;
				}
				JsonDeserializer<Vehicles> jsonDeserializer = new JsonDeserializer<>(Vehicles.class);
				return jsonDeserializer.deserialize(s, bytes);
			}

			@Override
			public void close() {}
		};
	}
}
