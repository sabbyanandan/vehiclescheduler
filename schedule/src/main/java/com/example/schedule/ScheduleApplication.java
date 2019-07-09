package com.example.schedule;

import com.example.common.Vehicle;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
@EnableBinding(ScheduleApplication.VehicleScheduleProcessor.class)
public class ScheduleApplication {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}

	/*@StreamListener("input")
	public void getOne(KStream<String, Vehicle> input) {

		input
				.map((k, v) -> new KeyValue<>(((Vehicle) v).getStartTime(), ((Vehicle) v)))
				.groupByKey(Serialized
						.with(new JsonSerde<>(Vehicle.ScheduleStartTime.class), new JsonSerde<>(Vehicle.class)))
				.windowedBy(TimeWindows.of(10000))
				.count(Materialized.as("foo-u5"))
				.toStream()
				.map((k, v) -> {
					System.out.println("In the last " + 30 + " secs, " + v
							+ " new Vehicles were added to the " + k
							+ " Start-Time bucket.");
					return new KeyValue(k, v);

		});
	}*/

	/*@StreamListener("input")
	public void getTwo(KStream<String, Vehicle> input) {

		input
				.map((k, v) -> new KeyValue<>(((Vehicle) v).getStartTime(), ((Vehicle) v)))
				.groupByKey(Serialized
						.with(new JsonSerde<>(Vehicle.ScheduleStartTime.class), new JsonSerde<>(Vehicle.class)))
				.windowedBy(TimeWindows.of(10000))
				.aggregate(Vehicle::new, (scheduleStartTime, vehicle, vehicle2) -> {
					return vehicle2;
				})
				.toStream()
				.map((k, v) -> {
					System.out.println("In the last " + 30 + " secs, " + v
							+ " new Vehicles were added to the " + k
							+ " Start-Time bucket.");
					return new KeyValue(k, v);
				});
	}*/

	@StreamListener("input")
	public void getThree(KStream<String, Vehicle> input) {

		input
				.map((k, v) -> new KeyValue<>(((Vehicle) v).getStartTime(), ((Vehicle) v)))
				.groupByKey(Serialized
						.with(new JsonSerde<>(Vehicle.ScheduleStartTime.class), new JsonSerde<>(Vehicle.class)))
				.windowedBy(TimeWindows.of(10000))
				.reduce((vehicle, v1) -> v1)
				.toStream()
				.map((k, v) -> {
					System.out.println("In the last " + 30 + " secs, " + v
							+ " new Vehicles were added to the " + k
							+ " Start-Time bucket.");
					return new KeyValue(k, v);
				});
	}

	interface VehicleScheduleProcessor {
		@Input("input")
		KStream<String, Vehicle> input();
	}

	@RestController
	class InteractiveQueryController {

		@RequestMapping("/windows")
		public List<Vehicle> windowedData() {

			ReadOnlyWindowStore<String, Vehicle> queryableStore = interactiveQueryService
					.getQueryableStore("foo-v5",
							QueryableStoreTypes.windowStore());

			List<Vehicle> vehicles = new ArrayList<>();

			if (queryableStore != null) {

				KeyValueIterator<Windowed<String>, Vehicle> vehicleCountIterator = queryableStore
						.all();

				Set<KeyValue<Windowed<String>, Vehicle>> windowedSet = new LinkedHashSet<>();
				vehicleCountIterator.forEachRemaining(windowedSet::add);
				vehicleCountIterator.close();

				// Transform windows to a list of domain objects
				windowedSet.forEach(value -> vehicles.add((Vehicle) value.value));

				vehicles.sort(Comparator.comparing(o -> o.getStartTime()));
			}
			return vehicles;
		}
	}
}
