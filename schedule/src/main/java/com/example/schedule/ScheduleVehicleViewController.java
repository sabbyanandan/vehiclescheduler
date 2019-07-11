package com.example.schedule;

import com.example.common.Vehicle;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ScheduleVehicleViewController {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	@RequestMapping("/aggregate")
	public Map<String, List<Vehicle>> windowedData() {

		Map<String, List<Vehicle>> vehicles = new HashMap<>();

		ReadOnlyWindowStore<String, List<Vehicle>> queryableStore = interactiveQueryService
				.getQueryableStore(ScheduleApplication.VEHICLE_SCH_VIEW,
						QueryableStoreTypes.windowStore());

		if (queryableStore != null) {
			KeyValueIterator<Windowed<String>, List<Vehicle>> vehicleCountIterator = queryableStore
					.all();
			Set<KeyValue<Windowed<String>, List<Vehicle>>> windowedSet = new LinkedHashSet<>();
			vehicleCountIterator.forEachRemaining(windowedSet::add);
			vehicleCountIterator.close();
			windowedSet.forEach(value -> vehicles.put(value.key.key(), value.value));
		}
		return vehicles;
	}

	@RequestMapping("/count")
	public Map<Vehicle.ScheduleStartTime, Long> windowedCount() {

		AtomicLong total = new AtomicLong(0);
		Map<Vehicle.ScheduleStartTime, Long> map = null;

		ReadOnlyWindowStore<Vehicle.ScheduleStartTime, Long> queryableStore = interactiveQueryService
				.getQueryableStore("test",
						QueryableStoreTypes.windowStore());

		if (queryableStore != null) {
			long now = System.currentTimeMillis();
			map = new HashMap<Vehicle.ScheduleStartTime, Long>();
			for (Vehicle.ScheduleStartTime startTime : Vehicle.ScheduleStartTime.values()) {
				WindowStoreIterator<Long> startTimeItr = queryableStore
						.fetch(startTime, now - (60 * 1000 * 60), now);
				startTimeItr.forEachRemaining(value -> total.addAndGet(value.value));
				map.put(startTime, total.longValue());
			}
		}
		return map;
	}
}
