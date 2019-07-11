package com.example.schedule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import com.example.common.Vehicle;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.streams.serde.CollectionSerde;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

@SpringBootApplication
public class ScheduleApplication {

	static String VEHICLE_SCH_VIEW = "vehicle-aggregates-4";

	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}

	@Bean
	public Consumer<KStream<String, Vehicle>> schedule() {
		// find and aggregate the cars and its details based on schedule time (time at which car needs started)
		return input -> input
				.map((k, v) -> new KeyValue<>(v.getStartTime(), v))
				.groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Vehicle.class)))
				.windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
				.aggregate(ArrayList::new,
						(k, v, vehicles) -> {
							System.out.println("Key: " + k + ", count so far: " + (vehicles.size() + 1));
							vehicles.add(v);
							return vehicles;
						},
						Materialized.<String, Collection<Vehicle>, WindowStore<Bytes, byte[]>>as(
								VEHICLE_SCH_VIEW)
								.withKeySerde(Serdes.String())
								.withValueSerde(new CollectionSerde<>(Vehicle.class, ArrayList.class)))
				.toStream()
				.map((k, v) -> {
					System.out.println("In the last " + 30 + " secs, " + v.size()
							+ " new Vehicles were added to the " + k
							+ " Start-Time bucket.");
					return new KeyValue<>(k, v);
				});
	}
}
