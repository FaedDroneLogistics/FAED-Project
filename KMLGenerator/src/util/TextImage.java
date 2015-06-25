package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextImage {

	private static final String PNG_FORMAT = "png";
	private static final String IMAGE_PATH = "images/faed_info.png";
	private static final String BASE_IMAGE_PATH = "images/faed_info_base.png";
	private static final int SIZE_VALUES = 6;
	private static final int X_COORDINATE = 255;
	private static final int[] Y_COORDINATES = {90, 127, 163, 200, 240, 275};
	private static final Color[] VALUE_COLORS = {Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.BLUE, Color.BLUE};
	
	public static void insertText(int[] values) throws IOException {
		
		assert(values.length == SIZE_VALUES);
		
		new File(IMAGE_PATH).delete();
		final BufferedImage image = ImageIO.read(new File(BASE_IMAGE_PATH));
		
		Graphics graphics;
		
		int i = 0;
		for(int value : values) {	
			graphics = image.getGraphics();
			graphics.setFont(graphics.getFont().deriveFont(24f));
			graphics.setColor(VALUE_COLORS[i]);
			graphics.drawString(Integer.toString(value), X_COORDINATE, Y_COORDINATES[i]);
			graphics.dispose();
			graphics = null;
			i++;			
		}
		
		
		ImageIO.write(image, PNG_FORMAT, new File(IMAGE_PATH));
	}
}
