package util;

import java.io.Serializable;

public class WeatherDay implements Serializable {

	private String day;
	private String date;
	private String largeTemp;
	private String smallTemp;
	private String conditions;
	
	
	public WeatherDay() {
		
	}
	
	public WeatherDay(String day, String date, String largeTemp, String smallTemp, String conditions) {
		this.day = day;
		this.date = date;
		this.largeTemp = largeTemp;
		this.smallTemp = smallTemp;
		this.conditions = conditions;
	}

	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLargeTemp() {
		return largeTemp;
	}
	public void setLargeTemp(String largeTemp) {
		this.largeTemp = largeTemp;
	}
	public String getSmallTemp() {
		return smallTemp;
	}
	public void setSmallTemp(String smallTemp) {
		this.smallTemp = smallTemp;
	}
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
}
