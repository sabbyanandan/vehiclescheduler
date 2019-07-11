package com.example.schedule;

import com.example.common.Vehicle;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class VehiclesSerde implements Serde<Vehicles> {
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	@Override
	public void close() {

	}

	@Override
	public Serializer<Vehicles> serializer() {
		return new Serializer<Vehicles>() {
			@Override
			public void configure(final Map<String, ?> map, final boolean b) {
			}

			@Override
			public byte[] serialize(final String s, final Vehicles vehicles) {

				JsonSerializer<Vehicle> jsonSerializer = new JsonSerializer<>();

				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final DataOutputStream
						dataOutputStream =
						new DataOutputStream(out);
				try {

					for (Vehicle vehicle : vehicles) {

						byte[] serialized = jsonSerializer.serialize(s, vehicle);
						dataOutputStream.write(serialized);
					}
					dataOutputStream.flush();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				return out.toByteArray();
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
			public void configure(final Map<String, ?> map, final boolean b) {

			}

			@Override
			public Vehicles deserialize(final String s, final byte[] bytes) {
				if (bytes.length == 0) {
					return null;
				}
				String stringBytes = new String(bytes, StandardCharsets.UTF_8);
				String[] splits = stringBytes.split("(?<=})");
				final Vehicles result = new Vehicles();

				JsonDeserializer<Vehicle> jsonDeserializer = new JsonDeserializer<>(Vehicle.class);
				for (String split : splits) {
					Vehicle deserialized = jsonDeserializer.deserialize(s, split.getBytes());
					result.list.add(deserialized);
				}
				return result;
			}

			@Override
			public void close() {

			}
		};
	}
}
