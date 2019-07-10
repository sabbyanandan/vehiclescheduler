package com.example.schedule;

import com.example.common.Vehicle;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.*;
import java.util.Date;
import java.util.Map;

public class VehiclesSerde implements Serde<Vehicles> {
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	@Override
	public void close() {

	}

	@Override
	public Serializer<Vehicles> serializer() {
		return new Serializer<Vehicles>() {
			@Override
			public void configure(final Map<String, ?> map, final boolean b) {
			}

			@Override
			public byte[] serialize(final String s, final Vehicles vehicles) {

				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final DataOutputStream
						dataOutputStream =
						new DataOutputStream(out);
				try {

					for (Vehicle vehicle : vehicles) {

						dataOutputStream.writeUTF(vehicle.getVIN());
						dataOutputStream.writeUTF(vehicle.getManufacturer().name());
						dataOutputStream.writeUTF(vehicle.getType().name());
						dataOutputStream.writeUTF(vehicle.getStartTime().name());
						dataOutputStream.writeLong(vehicle.getScheduledTime().getTime());
					}
					dataOutputStream.flush();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				return out.toByteArray();
			}

			@Override
			public void close() {

			}
		};
	}

	@Override
	public Deserializer<Vehicles> deserializer() {
		return new Deserializer<Vehicles>() {
			@Override
			public void configure(final Map<String, ?> map, final boolean b) {

			}

			@Override
			public Vehicles deserialize(final String s, final byte[] bytes) {
				if (bytes == null || bytes.length == 0) {
					return null;
				}
				final Vehicles result = new Vehicles();

				final DataInputStream
						dataInputStream =
						new DataInputStream(new ByteArrayInputStream(bytes));

				try {
					while (dataInputStream.available() > 0) {
						String vin = dataInputStream.readUTF();

						String manufacturer = dataInputStream.readUTF();
						Vehicle.Manufacturer manufacturerType = null;
						switch (manufacturer) {
						case "TESLA":
							manufacturerType = Vehicle.Manufacturer.TESLA;
							break;
						case "BMW":
							manufacturerType = Vehicle.Manufacturer.BMW;
							break;
						case "LANDROVER":
							manufacturerType = Vehicle.Manufacturer.LANDROVER;
							break;
						}

						String vehicle = dataInputStream.readUTF();
						Vehicle.VehicleType vehicleType = null;
						switch (vehicle) {
						case "CAR":
							vehicleType = Vehicle.VehicleType.CAR;
							break;
						case "SUV":
							vehicleType = Vehicle.VehicleType.SUV;
							break;
						case "TRUCK":
							vehicleType = Vehicle.VehicleType.TRUCK;
							break;
						}

						String startTime = dataInputStream.readUTF();
						Vehicle.ScheduleStartTime scheduleStartTime = null;
						switch (startTime) {
						case "T_0600":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0600;
							break;
						case "T_0605":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0605;
							break;
						case "T_0610":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0610;
							break;
						case "T_0630":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0630;
							break;
						case "T_0705":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0705;
							break;
						case "T_0710":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0710;
							break;
						case "T_0720":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0720;
							break;
						case "T_0730":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0730;
							break;
						case "T_0735":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0735;
							break;
						case "T_0739":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0739;
							break;
						case "T_0800":
							scheduleStartTime = Vehicle.ScheduleStartTime.T_0800;
							break;
						}

						Date scheduledTime = new Date(dataInputStream.readLong());

						result.add(new Vehicle(vin, manufacturerType, vehicleType, scheduleStartTime, scheduledTime));
					}
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				return result;
			}

			@Override
			public void close() {

			}
		};
	}
}
