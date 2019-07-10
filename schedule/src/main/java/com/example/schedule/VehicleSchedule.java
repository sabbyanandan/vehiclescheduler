package com.example.schedule;

import com.example.common.Vehicle;

import java.util.*;

public class VehicleSchedule {

	String VIN;

	Vehicle.Manufacturer manufacturer;

	Vehicle.VehicleType type;

	Vehicle.ScheduleStartTime startTime;

	Date scheduledTime;

	Date addedToBucketTime;

	final Map<Vehicle.ScheduleStartTime, List<VehicleSchedule>> vehicleScheduleMap = new HashMap<>();

	public VehicleSchedule() {
	}

	public VehicleSchedule(String VIN, Vehicle.Manufacturer manufacturer, Vehicle.VehicleType type,
			Vehicle.ScheduleStartTime startTime, Date scheduledTime, Date addedToBucketTime) {
		this.VIN = VIN;
		this.manufacturer = manufacturer;
		this.type = type;
		this.startTime = startTime;
		this.scheduledTime = scheduledTime;
		this.addedToBucketTime = addedToBucketTime;
	}

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String VIN) {
		this.VIN = VIN;
	}

	public Vehicle.Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Vehicle.Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Vehicle.VehicleType getType() {
		return type;
	}

	public void setType(Vehicle.VehicleType type) {
		this.type = type;
	}

	public Vehicle.ScheduleStartTime getStartTime() {
		return startTime;
	}

	public void setStartTime(Vehicle.ScheduleStartTime startTime) {
		this.startTime = startTime;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Date getAddedToBucketTime() {
		return addedToBucketTime;
	}

	public void setAddedToBucketTime(Date addedToBucketTime) {
		this.addedToBucketTime = addedToBucketTime;
	}

	public VehicleSchedule add(Vehicle vehicle) {

		if (vehicle.getVIN() == null || vehicle.getVIN() == null)
			throw new IllegalArgumentException("Invalid VIN - GIVING UP: " + vehicle);

		if (this.VIN == null)
			this.VIN = vehicle.getVIN();
		if (this.type == null)
			this.type = vehicle.getType();
		if (this.manufacturer == null)
			this.manufacturer = vehicle.getManufacturer();
		if (this.startTime == null)
			this.startTime = vehicle.getStartTime();
		if (this.scheduledTime == null)
			this.scheduledTime = vehicle.getScheduledTime();
		addedToBucketTime = Calendar.getInstance().getTime();

		return this;
	}

	public VehicleSchedule addToList(Vehicle vehicle) {

		if (vehicle.getVIN() == null || vehicle.getVIN() == null)
			throw new IllegalArgumentException("Invalid VIN - GIVING UP: " + vehicle);

		if (this.VIN == null)
			this.VIN = vehicle.getVIN();
		if (this.type == null)
			this.type = vehicle.getType();
		if (this.manufacturer == null)
			this.manufacturer = vehicle.getManufacturer();
		if (this.startTime == null)
			this.startTime = vehicle.getStartTime();
		if (this.scheduledTime == null)
			this.scheduledTime = vehicle.getScheduledTime();
		addedToBucketTime = Calendar.getInstance().getTime();

		if (vehicleScheduleMap.get(vehicle.getStartTime()) == null) {
			vehicleScheduleMap.put(vehicle.getStartTime(), new ArrayList<>(Arrays.asList(this)));
		}
		else {
			vehicleScheduleMap.get(vehicle.getStartTime()).add(this);
		}

		System.out.println("Key = " + vehicle.getStartTime().name() + " Count = " + vehicleScheduleMap.size());

		return this;
	}

	@Override public String toString() {
		return "VehicleSchedule{" +
				"VIN='" + VIN + '\'' +
				", manufacturer=" + manufacturer +
				", type=" + type +
				", startTime=" + startTime +
				", scheduledTime=" + scheduledTime +
				", addedToBucketTime=" + addedToBucketTime +
				'}';
	}

	class VehicleScheduleCount {
		Vehicle.ScheduleStartTime startTime;

		long count;

		public Vehicle.ScheduleStartTime getStartTime() {
			return startTime;
		}

		public void setStartTime(Vehicle.ScheduleStartTime startTime) {
			this.startTime = startTime;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}
	}
}
