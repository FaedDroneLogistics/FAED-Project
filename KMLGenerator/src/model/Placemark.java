/*
 * Author: Julio Bondia, Marc Gonzàlez
 * E-mail: {julio.bondia13, marcgc21}@gmail.com
 * 
 * FAED Project - Google Summer of Code 2015
 */
package model;

public class Placemark {
	
	private String name, description, geolocation;
	private double lat, lon, altitude;	

	/**
	 * Point of interest constructor
	 * 
	 * @param name - name of the point of interest
	 * @param description - description of the point of interest
	 * @param lat - latitude coordinate
	 * @param lon - longitude coordinate
	 */
	public Placemark(String name, String description, double lat, double lon) {
		super();
		this.name = name;
		this.description = description;
		this.lat = lat;
		this.lon = lon;
		this.geolocation = lon + "," + lat + ",0";
	}
	
	/**
	 * Point of interest constructor
	 * 
	 * @param name - name of the point of interest
	 * @param description - description of the point of interest
	 * @param lat - latitude coordinate
	 * @param lon - longitude coordinate
	 * @param altitude - altitude of the point of interest
	 */
	public Placemark(String name, String description, double lat, double lon, 
			double altitude) {
		super();
		this.name = name;
		this.description = description;
		this.lat = lat;
		this.lon = lon;
		this.geolocation = lon + "," + lat + "," + altitude;
	}

	/*** Getters and Setters ***/
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public String getGeolocation() {
		return this.geolocation;
	}
	
	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
}
