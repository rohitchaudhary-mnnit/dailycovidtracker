package io.javaweb.dailycovidtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DailycovidtrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailycovidtrackerApplication.class, args);
	}

}
