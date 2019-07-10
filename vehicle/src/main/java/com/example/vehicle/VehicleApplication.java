package com.example.vehicle;

import com.example.common.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableScheduling
@EnableBinding(Source.class)
public class VehicleApplication {

	static List<Vehicle.VehicleType> vehicles = Arrays
			.asList(Vehicle.VehicleType.CAR, Vehicle.VehicleType.SUV, Vehicle.VehicleType.TRUCK);

	static List<Vehicle.Manufacturer> manufacturers = Arrays
			.asList(Vehicle.Manufacturer.LANDROVER, Vehicle.Manufacturer.BMW, Vehicle.Manufacturer.TESLA);

	static List<Vehicle.ScheduleStartTime> startTimes = Arrays
			.asList(Vehicle.ScheduleStartTime.T_0600, Vehicle.ScheduleStartTime.T_0605,
					Vehicle.ScheduleStartTime.T_0610,
					Vehicle.ScheduleStartTime.T_0710, Vehicle.ScheduleStartTime.T_0705,
					Vehicle.ScheduleStartTime.T_0630,
					Vehicle.ScheduleStartTime.T_0720, Vehicle.ScheduleStartTime.T_0730,
					Vehicle.ScheduleStartTime.T_0739,
					Vehicle.ScheduleStartTime.T_0800, Vehicle.ScheduleStartTime.T_0735,
					Vehicle.ScheduleStartTime.T_0605, Vehicle.ScheduleStartTime.T_0630,
					Vehicle.ScheduleStartTime.T_0739);

	@Autowired
	private Source source;

	public static void main(String[] args) {
		SpringApplication.run(VehicleApplication.class, args);
	}

	@Scheduled(fixedRate = 1000L)
	public void generateAndSendNewVehicle() {
		Vehicle vehicle = new Vehicle(Long.toHexString(Double.doubleToLongBits(Math.random())),
				manufacturers.get(new Random().nextInt(manufacturers.size())),
				vehicles.get(new Random().nextInt(vehicles.size())),
				startTimes.get(new Random().nextInt(startTimes.size())), Calendar.getInstance().getTime());

		source.output().send(MessageBuilder.withPayload(vehicle).build());
		System.out.println("Generated Vehicle: " + vehicle);
	}
}
