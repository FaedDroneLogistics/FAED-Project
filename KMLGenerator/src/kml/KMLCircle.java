/*
 * Author: Julio Bondia, Marc Gonzàlez
 * E-mail: {julio.bondia13, marcgc21}@gmail.com
 * 
 * FAED Project - Google Summer of Code 2015
 */
package kml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.Circle;
import util.GeoTool;

public class KMLCircle {

	/**
	 * Generate a KML file of a circle with center (centerLat, centerLon) of radius 'radius'
	 * 
	 * @param circles - list of circles to draw
	 * @param outputFile - path to store the generated KML
	 * @throws IOException
	 */
	public void genCircle(List<Circle> circles, String outputFile) throws IOException {
		
		outputFile = outputFile.endsWith(".kml") ? outputFile : outputFile + ".kml";
		
		String zoneAndLetter;
		double utmX, utmY;
		double utmCenterX, utmCenterY;
		
		File file = new File(outputFile); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
				+ "\t<Document>\n"
				+ "\t\t<name>Influence Radius</name>\n"
				+ "\t\t<visibility>1</visibility>\n");		
			
		String utmStr;
		String[] latLonStr;
		double[] projectedPoint;
		GeoTool geoTool = new GeoTool();
		
		for(Circle circle : circles) {	
			
			utmStr = geoTool.latLon2UTM(circle.getLat(), circle.getLon());
			latLonStr = utmStr.split("\\s+");
			
			zoneAndLetter = latLonStr[0] + " " + latLonStr[1];
			utmCenterX = Double.parseDouble(latLonStr[2]);
			utmCenterY = Double.parseDouble(latLonStr[3]);
			
			writer.write("\t\t<Placemark>\n"
					+ "\t\t\t<name>Hangar</name>\n"
					+ "\t\t\t<visibility>1</visibility>\n"
					+ "\t\t\t<Style>\n"
					+ "\t\t\t\t<LineStyle>\n"
					+ "\t\t\t\t\t<color>" + circle.getColor() + "</color>\n"
					+ "\t\t\t\t\t<scale>1</scale>\n"
					+ "\t\t\t\t\t<width>10</width>\n"
					+ "\t\t\t\t</LineStyle>\n"
					+ "\t\t\t</Style>\n"					
					+ "\t\t\t<LineString>\n"
					+ "\t\t\t\t<altitudeMode>absolute</altitudeMode>\n"
					+ "\t\t\t\t<coordinates>\n");
	
			for(int i = 0; i <= 360; i++) {		
				
				utmX = circle.getRadius() * Math.cos(Math.toRadians(i)) + utmCenterX;
				utmY = circle.getRadius() * Math.sin(Math.toRadians(i)) + utmCenterY;
				
				projectedPoint = geoTool.utm2LatLon(zoneAndLetter + " " + utmX + " " + utmY);

				writer.write("\t\t\t\t" + projectedPoint[1] + "," + projectedPoint[0] + "," + circle.getAltitude());
				writer.write("\n");				
			}
			
			writer.write("\t\t\t\t</coordinates>\n"
					+ "\t\t\t</LineString>\n"
					+ "\t\t</Placemark>\n");
		}

		writer.write("\t</Document>\n"
				+ "</kml>");
		
		writer.close();
	}
}
