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

	static String VEHICLE_SCH_VIEW = "vehicle-aggregates-4";

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
				.windowedBy(TimeWindows.of(30000))
				.aggregate(Vehicles::new,
						(k, v, vehicles) -> {
							vehicles.add(v);
							return vehicles;
						},
						Materialized.<Vehicle.ScheduleStartTime, Vehicles, WindowStore<Bytes, byte[]>>as(
								VEHICLE_SCH_VIEW)
								.withKeySerde(new JsonSerde<>(Vehicle.ScheduleStartTime.class))
								.withValueSerde(new VehiclesSerde()))
				.toStream()
				.map((k, v) -> {
					System.out.println("In the last " + 30 + " secs, " + v.list.size()
							+ " new Vehicles were added to the " + k
							+ " Start-Time bucket.");
					return new KeyValue(k, v);
				});

		// find count of total number of cars by schedule time (time at which car needs started)

//		input
//				.map((k, v) -> new KeyValue<>(((Vehicle) v).getStartTime(), ((Vehicle) v)))
//				.groupByKey(Serialized
//						.with(new JsonSerde<>(Vehicle.ScheduleStartTime.class), new JsonSerde<>(Vehicle.class)))
//				.windowedBy(TimeWindows.of(5000))
//				.count(Materialized.as("test"))
//				.toStream()
//				.map((k, v) -> {
//					System.out.println("In the last " + 5 + " secs, " + v
//							+ " new Vehicles were added to the " + k
//							+ " Start-Time bucket.");
//					return new KeyValue(k, v);
//				});
	}

	interface VehicleScheduleProcessor {
		@Input("input")
		KStream<String, Vehicle> input();
	}
}
