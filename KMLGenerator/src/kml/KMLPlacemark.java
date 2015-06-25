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

import model.Placemark;

public class KMLPlacemark {

	/**
	 * Generate a KML file specifying several points of interest 
	 * 
	 * @param outputFile - path to store the generated KML
	 * @param iconPath - path to the icon representing the points of interest
	 * @param placemarks - list of points of interest
	 * @throws IOException
	 */
	public void genPlacemark(String outputFile, String iconPath, 
			List<Placemark> placemarks) throws IOException {
		
		outputFile = outputFile.endsWith(".kml") ? outputFile : outputFile + ".kml";
		
		File file = new File(outputFile); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
				+ "\t<Document>\n"
				+ "\t\t<Style id=\"custom_icon\">\n"
				+ "\t\t\t<IconStyle>\n"
				+ "\t\t\t\t<Icon>\n"
				+ "\t\t\t\t\t<href>" + iconPath + "</href>\n"
				+ "\t\t\t\t\t<scale>1.0</scale>\n"
				+ "\t\t\t\t</Icon>\n"
				+ "\t\t\t</IconStyle>\n"
				+ "\t\t</Style>\n");
		
		for(Placemark placemark : placemarks) {
			writer.write("\t\t<Placemark>\n" 
					+ "\t\t\t<name>" + placemark.getName() + "</name>\n"
					+ "\t\t\t<description>" + placemark.getDescription() + "</description>\n"
					+ "\t\t\t<styleUrl>custom_icon</styleUrl>\n"
					+ "\t\t\t<Point>\n"
					+ "\t\t\t\t<altitudeMode>absolute</altitudeMode>\n"
					+ "\t\t\t\t<coordinates>" + placemark.getGeolocation() + "</coordinates>\n"
					+ "\t\t\t</Point>\n"
					+ "\t\t</Placemark>\n");
		}

		writer.write("t</Document>\n"
				+ "</kml>");
		writer.close();
	}
}
