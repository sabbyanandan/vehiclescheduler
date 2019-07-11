package com.example.common;

import java.util.Date;

/**
 * Created by sanandan on 7/3/19.
 */
public class Vehicle {

	String VIN;

	Manufacturer manufacturer;

	VehicleType type;

	ScheduleStartTime startTime;

	Date scheduledTime;

	public enum VehicleType {
		CAR, SUV, TRUCK;
	}

	public enum Manufacturer {
		TESLA, LANDROVER, BMW;
	}

	public enum ScheduleStartTime {
		T_0600, T_0605, T_0610, T_0630, T_0705, T_0710, T_0720, T_0730, T_0735, T_0739, T_0800;
	}

	public Vehicle() {
	}

	public Vehicle(String VIN, Manufacturer manufacturer, VehicleType type,
			ScheduleStartTime startTime, Date scheduledTime) {
		this.VIN = VIN;
		this.manufacturer = manufacturer;
		this.type = type;
		this.startTime = startTime;
		this.scheduledTime = scheduledTime;
	}

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String VIN) {
		this.VIN = VIN;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public ScheduleStartTime getStartTime() {
		return startTime;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public void setStartTime(ScheduleStartTime startTime) {
		this.startTime = startTime;
	}

	@Override public String toString() {
		return "Vehicle{" +
				"VIN='" + VIN + '\'' +
				", manufacturer=" + manufacturer +
				", type=" + type +
				", startTime=" + startTime +
				", scheduledTime=" + scheduledTime +
				'}';
	}
}
