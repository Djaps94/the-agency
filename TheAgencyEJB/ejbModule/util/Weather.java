package util;

import java.util.ArrayList;
import java.util.List;

public class Weather {

	private List<WeatherDay> weatherDays;
	
	public Weather() { 
		this.weatherDays = new ArrayList<>();
	}

	public List<WeatherDay> getWeatherDays() {
		return weatherDays;
	}

	public void setWeatherDays(List<WeatherDay> weatherDays) {
		this.weatherDays = weatherDays;
	}
	
	
}
