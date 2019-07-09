package com.example.schedule;

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

@RestController
public class ScheduleVehicleViewController {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	@RequestMapping("/windows")
	public List<VehicleSchedule> windowedData() {

		ReadOnlyWindowStore<String, VehicleSchedule> queryableStore = interactiveQueryService
				.getQueryableStore(ScheduleApplication.VEHICLE_SCH_VIEW,
						QueryableStoreTypes.windowStore());

		List<VehicleSchedule> vehicles = new ArrayList<>();

		if (queryableStore != null) {

			KeyValueIterator<Windowed<String>, VehicleSchedule> vehicleCountIterator = queryableStore
					.all();

			Set<KeyValue<Windowed<String>, VehicleSchedule>> windowedSet = new LinkedHashSet<>();
			vehicleCountIterator.forEachRemaining(windowedSet::add);
			vehicleCountIterator.close();

			// Transform windows to a list of domain objects
			windowedSet.forEach(value -> vehicles.add((VehicleSchedule) value.value));

			vehicles.sort(Comparator.comparing(o -> o.getStartTime()));
		}
		return vehicles;
	}
}
