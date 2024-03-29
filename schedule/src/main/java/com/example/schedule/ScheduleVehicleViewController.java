package com.example.schedule;

import com.example.common.Vehicle;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
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
	public Map<Vehicle.ScheduleStartTime, List<Vehicle>> windowedData() {

		Map<Vehicle.ScheduleStartTime, List<Vehicle>> vehicles = new HashMap<>();

		ReadOnlyWindowStore<Vehicle.ScheduleStartTime, Vehicles> queryableStore = interactiveQueryService
				.getQueryableStore(ScheduleApplication.VEHICLE_SCH_VIEW_DETAILS,
						QueryableStoreTypes.windowStore());

		if (queryableStore != null) {
			KeyValueIterator<Windowed<Vehicle.ScheduleStartTime>, Vehicles> vehicleCountIterator = queryableStore
					.all();
			Set<KeyValue<Windowed<Vehicle.ScheduleStartTime>, Vehicles>> windowedSet = new LinkedHashSet<>();
			vehicleCountIterator.forEachRemaining(windowedSet::add);
			vehicleCountIterator.close();
			windowedSet.forEach(value -> vehicles.put(value.key.key(), value.value.vehicleList));
		}
		return vehicles;
	}

	@RequestMapping("/count")
	public Map<Vehicle.ScheduleStartTime, Long> windowedCount() {

		AtomicLong total = new AtomicLong(0);
		Map<Vehicle.ScheduleStartTime, Long> map = new HashMap<Vehicle.ScheduleStartTime, Long>();

		ReadOnlyWindowStore<Vehicle.ScheduleStartTime, Long> queryableStore = interactiveQueryService
				.getQueryableStore(ScheduleApplication.VEHICLE_SCH_VIEW_COUNT,
						QueryableStoreTypes.windowStore());

		if (queryableStore != null) {
			KeyValueIterator<Windowed<Vehicle.ScheduleStartTime>, Long> vehicleCountIterator = queryableStore
					.all();

			Set<KeyValue<Windowed<Vehicle.ScheduleStartTime>, Long>> windowedSet = new LinkedHashSet<>();
			vehicleCountIterator.forEachRemaining(windowedSet::add);
			vehicleCountIterator.close();
			windowedSet.forEach(value -> map.put(value.key.key(), value.value.longValue()));
		}

		return map;
	}
}
