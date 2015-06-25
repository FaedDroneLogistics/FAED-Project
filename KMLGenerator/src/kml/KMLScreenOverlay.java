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

import model.ScreenOverlay;

public class KMLScreenOverlay {
	
	public void genKMLInormationOverlay(String outputFile, List<ScreenOverlay> screenOverlays) 
			throws IOException {
		
		outputFile = outputFile.endsWith(".kml") ? outputFile : outputFile + ".kml";
		
		File file = new File(outputFile); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
				+ "\t<Document>\n");
		
		for(ScreenOverlay screenOverlay : screenOverlays) {
			
			writer.write("\t\t<ScreenOverlay>\n"
					+ "\t\t\t<name>" + screenOverlay.getName() + "</name>\n"
					+ "\t\t\t<Icon>\n"
					+ "\t\t\t\t<href>" + screenOverlay.getImagePath() + "</href>\n"
					+ "\t\t\t</Icon>\n"
					+ screenOverlay.getOverlayLocation()
					+ "\t\t</ScreenOverlay>\n");
		}
		
		writer.write("\t</Document>\n"
				+ "</kml>");
		writer.close();
	}

}
