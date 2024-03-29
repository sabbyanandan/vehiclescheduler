package com.example.schedule;

import com.example.common.Vehicle;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;

@SpringBootApplication
@EnableBinding(ScheduleApplication.VehicleScheduleProcessor.class)
public class ScheduleApplication {

	static String VEHICLE_SCH_VIEW_DETAILS = "vehicle-aggregates-19";

	static String VEHICLE_SCH_VIEW_COUNT = "vehicle-sch-count-19";

	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}

	@StreamListener("input")
	public void get(KStream<String, Vehicle> input) {

		// find and aggregate the cars and its details based on schedule time (time at which car needs started)

		input
				.map((k, v) -> new KeyValue<>(v.getStartTime(), v))
				.groupByKey(Serialized
						.with(new JsonSerde<>(Vehicle.ScheduleStartTime.class), new JsonSerde<>(Vehicle.class)))
				.windowedBy(TimeWindows.of(60 * 1000 * 60 * 60 * 24))
				.aggregate(Vehicles::new,
						(k, v, vehicles) -> {
							vehicles.add(v);
							return vehicles;
						}, Materialized.<Vehicle.ScheduleStartTime, Vehicles, WindowStore<Bytes, byte[]>>as(
								VEHICLE_SCH_VIEW_DETAILS).withKeySerde(new JsonSerde<>(Vehicle.ScheduleStartTime.class))
								.withValueSerde(new VehiclesSerde()))
				.toStream()
				.map((k, v) -> {
					System.out.println(
							v.vehicleList.size() + " new Vehicles were added to the Aggregated List under the " + k
									.key() + " Start-Time bucket.");
					return new KeyValue(k, v);
				});

		// find count of total number of cars by schedule time (time at which car needs started)

		input
				.map((k, v) -> new KeyValue<>(((Vehicle) v).getStartTime(), ((Vehicle) v)))
				.groupByKey(Serialized
						.with(new JsonSerde<>(Vehicle.ScheduleStartTime.class), new JsonSerde<>(Vehicle.class)))
				.windowedBy(TimeWindows.of(60 * 1000 * 60 * 60 * 24))
				.count(Materialized.as(VEHICLE_SCH_VIEW_COUNT))
				.toStream()
				.map((k, v) -> {
					System.out.println(v + " new Vehicles were added to the " + k.key() + " Start-Time bucket.");
					return new KeyValue(k, v);
				});
	}

	interface VehicleScheduleProcessor {
		@Input("input")
		KStream<String, Vehicle> input();
	}
}
