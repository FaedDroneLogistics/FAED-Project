/*
 * Author: Julio Bondia, Marc Gonzàlez
 * E-mail: {julio.bondia13, marcgc21}@gmail.com
 * 
 * FAED Project - Google Summer of Code 2015
 */
package model;

public class ScreenOverlay {
	
	public class OverlayLocation {
	
		public static final String TOP_LEFT = 
				"\t\t<overlayXY x=\"0\" y=\"1\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
	    		+ "\t\t<screenXY x=\"0\" y=\"1\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
	    		+ "\t\t<rotationXY x=\"0\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
	    		+ "\t\t<size x=\"0\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>\n";
		
		public static final String BOTTOM_RIGHT = 
				"\t\t<overlayXY x=\"1\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
	    		+ "\t\t<screenXY x=\"1\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
	    		+ "\t\t<rotationXY x=\"0\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
	    		+ "\t\t<size x=\"0\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>\n";
	}

	private String name;
	private String imagePath;
	private String overlayLocation;
	
	/**
	 * Screen overlay constructor
	 * 
	 * @param name - name of the overlay
	 * @param imagePath - path to the image to be shown
	 * @param overlayLocation - OverlayLocation, where to put the information on the screen
	 */
	public ScreenOverlay(String name, String imagePath, String overlayLocation) {
		super();
		this.name = name;
		this.imagePath = imagePath;
		this.overlayLocation = overlayLocation;
	}

	/*** Getters and Setters ***/
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getOverlayLocation() {
		return overlayLocation;
	}

	public void setOverlayLocation(String overlayLocation) {
		this.overlayLocation = overlayLocation;
	}
}
