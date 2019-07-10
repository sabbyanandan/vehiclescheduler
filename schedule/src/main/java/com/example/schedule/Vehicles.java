package com.example.schedule;

import com.example.common.Vehicle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vehicles implements Iterable<Vehicle> {

	List<Vehicle> list = new ArrayList<>();

	public void add(Vehicle vehicle) {
		if (vehicle.getVIN() == null)
			throw new IllegalArgumentException("Invalid VIN - GIVING UP: " + vehicle);

		list.add(vehicle);
	}

	@Override
	public Iterator<Vehicle> iterator() {
		return list.iterator();
	}
}
