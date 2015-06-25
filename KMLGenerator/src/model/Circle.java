/*
 * Author: Julio Bondia, Marc Gonzàlez
 * E-mail: {julio.bondia13, marcgc21}@gmail.com
 * 
 * FAED Project - Google Summer of Code 2015
 */
package model;

public class Circle {

	private double radius;
	private String color;	
	private double lat, lon, altitude;
	
	public Circle(double radius, double lat, double lon, double altitude, String color) {
		super();
		this.radius = radius;
		this.lat = lat;
		this.lon = lon;
		this.altitude = altitude;
		this.color = color;
	}
	
	/*** Getters and Setters ***/
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
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
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
