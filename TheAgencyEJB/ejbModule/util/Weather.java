package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Weather implements Serializable{

	private List<WeatherDay> weatherDays;
	private String cityName;
	
	public Weather() { 
		this.weatherDays = new ArrayList<>();
	}

	public List<WeatherDay> getWeatherDays() {
		return weatherDays;
	}

	public void setWeatherDays(List<WeatherDay> weatherDays) {
		this.weatherDays = weatherDays;
	}
	
	public void addWeatherDay(WeatherDay day) {
		this.weatherDays.add(day);
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	
}
