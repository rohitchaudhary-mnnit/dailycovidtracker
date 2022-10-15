package io.javaweb.dailycovidtracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.javaweb.dailycovidtracker.models.LocationStats;

@Service
public class CoronaVirusDataService {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(VIRUS_DATA_URL))
				.build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationstat = new LocationStats();
			
			String state = record.get("Province/State");
			if(Objects.isNull(state) || state.length()==0) {
				locationstat.setState("NA");
			} else {
				locationstat.setState(state);
			}
			
			locationstat.setCountry(record.get("Country/Region"));
			
			int latestTotalCases = Integer.parseInt(record.get(record.size()-1));
			int prevDayTotalCases = Integer.parseInt(record.get(record.size()-2));
		    locationstat.setLatestTotalCases(latestTotalCases);
		    locationstat.setDiffFromPrevDay(latestTotalCases - prevDayTotalCases);
		    System.out.println(locationstat);
		    
		    newStats.add(locationstat);
		}
		this.allStats = newStats;
	}
}
