/*
 * Author: Julio Bondia, Marc Gonzàlez
 * E-mail: {julio.bondia13, marcgc21}@gmail.com
 * 
 * FAED Project - Google Summer of Code 2015
 */
package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kml.KMLCircle;
import kml.KMLPlacemark;
import kml.KMLScreenOverlay;
import model.Circle;
import model.Placemark;
import model.ScreenOverlay;
import model.ScreenOverlay.OverlayLocation;
import util.TextImage;

public class Main {

	public static void main(String[] args) throws IOException {
		
		List<Circle> circles = new ArrayList<Circle>();
		Circle c1 = new Circle(750, 41.618085, 0.626876, 190d, "ff0000ff");
		Circle c2 = new Circle(750, 41.616443, 0.613583, 190d, "ffffffff");
		circles.add(c1);
		circles.add(c2);
		
		
		List<Placemark> hangars = new ArrayList<Placemark>();
		Placemark h1 = new Placemark("H1", "Hangar 1", 41.618085, 0.626876, 175d);	
		Placemark h2 = new Placemark("H2", "Hangar 2", 41.616443, 0.613583, 175d);
		hangars.add(h1);
		hangars.add(h2);
		
		
		List<Placemark> meteoStations = new ArrayList<Placemark>();
		Placemark m1 = new Placemark("MS1", "Meteo Station 1", 41.605469, 0.606588, 75d);
		Placemark m2 = new Placemark("MS2", "Meteo Station 2", 41.607202, 0.624097, 75d);
		meteoStations.add(m1);
		meteoStations.add(m2);
		
		
		List<ScreenOverlay> screenOverlays = new ArrayList<ScreenOverlay>();
		ScreenOverlay s1 = new ScreenOverlay("FAED Project Icon",
				"/Users/jbondia/Documents/workspace/KMLGenerator/images/faed_logo_small.png",
				OverlayLocation.TOP_LEFT);
		ScreenOverlay s2 = new ScreenOverlay("FAED Information",
				"/Users/jbondia/Documents/workspace/KMLGenerator/images/faed_info.png",
				OverlayLocation.BOTTOM_RIGHT);
		screenOverlays.add(s1);
		screenOverlays.add(s2);
		
		
		KMLCircle kmlCircle = new KMLCircle();
		kmlCircle.genCircle(circles, "kml/hangar_influence.kml");
		
		KMLPlacemark kmlPlacemark = new KMLPlacemark();
		kmlPlacemark.genPlacemark("kml/drone_hangars.kml",
				"/Users/jbondia/Documents/workspace/KMLGenerator/images/hangar_icon.png",
				hangars);
		kmlPlacemark.genPlacemark("kml/meteo_stations.kml",
				"/Users/jbondia/Documents/workspace/KMLGenerator/images/meteo_icon.png",
				meteoStations);
		
		KMLScreenOverlay kmlScreenOverlay = new KMLScreenOverlay();
		kmlScreenOverlay.genKMLInormationOverlay("kml/faed_data.kml", screenOverlays);
		
		int[] values = {7,3,70,13,21,24};
		TextImage.insertText(values);
		
		/*
		GeoTool geoTool = new GeoTool();
		boolean b1 = geoTool.isInCircle(41.618085, 0.626876, 41.618085, 0.626876, 125);
		boolean b2 = geoTool.isInCircle(41.618085, 0.626876, 41.616443, 0.613583, 125);		
		System.out.println("1st is in? => " + b1 + "\n" + "2nd is in? => " + b2);
		*/
		
	}
}
