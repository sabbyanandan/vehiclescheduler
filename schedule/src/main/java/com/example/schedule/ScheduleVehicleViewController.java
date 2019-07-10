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
	public Map<Vehicle.ScheduleStartTime, List<VehicleSchedule>> windowedData() {

		Map<Vehicle.ScheduleStartTime, List<VehicleSchedule>> vehicles = new HashMap<>();

		ReadOnlyWindowStore<String, VehicleSchedule> queryableStore = interactiveQueryService
				.getQueryableStore(ScheduleApplication.VEHICLE_SCH_VIEW,
						QueryableStoreTypes.windowStore());

		if (queryableStore != null) {
			KeyValueIterator<Windowed<String>, VehicleSchedule> vehicleCountIterator = queryableStore
					.all();
			Set<KeyValue<Windowed<String>, VehicleSchedule>> windowedSet = new LinkedHashSet<>();
			vehicleCountIterator.forEachRemaining(windowedSet::add);
			vehicleCountIterator.close();
			windowedSet.forEach(value -> vehicles.putAll(((VehicleSchedule) value.value).vehicleScheduleMap));
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
