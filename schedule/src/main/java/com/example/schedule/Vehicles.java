package com.example.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.common.Vehicle;

public class Vehicles implements Iterable<Vehicle> {

	List<Vehicle> list = new ArrayList<>();

	public Vehicles() {
	}

	public List<Vehicle> getList() {
		return list;
	}

	public void setList(List<Vehicle> list) {
		this.list = list;
	}

	public void add(Vehicle vehicle) {

		if (vehicle.getVIN() == null)
			throw new IllegalArgumentException("Invalid VIN - GIVING UP: " + vehicle);

		list.add(vehicle);

		System.out.println("Key = " + vehicle.getStartTime().name() + " Count = " + list.size());
	}

	@Override
	public Iterator<Vehicle> iterator() {
		return list.iterator();
	}
}
