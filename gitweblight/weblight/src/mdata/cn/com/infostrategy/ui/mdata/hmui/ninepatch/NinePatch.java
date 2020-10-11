package cn.com.infostrategy.ui.mdata.hmui.ninepatch;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatch 
 * @Description: .9.png图片的NinePatch对象.先调用load方法，然后调用draw方法。需要重新面板的paintComponent方法。
 * @author haoming
 * @date Mar 19, 2013 4:35:34 PM
 *  
*/ 
public class NinePatch {
	public static final String EXTENSION_9PATCH = ".9.png";
	private BufferedImage mImage;
	private NinePatchChunk mChunk;

	public BufferedImage getImage() {
		return this.mImage;
	}

	public NinePatchChunk getChunk() {
		return this.mChunk;
	}

	public static NinePatch load(URL fileUrl, boolean convert) throws IOException {
		BufferedImage image = null;
		try {
			image = GraphicsUtilities.loadCompatibleImage(fileUrl);
		} catch (MalformedURLException e) {
			return null;
		}

		boolean is9Patch = fileUrl.getPath().toLowerCase().endsWith(".9.png");

		return load(image, is9Patch, convert);
	}

	public static NinePatch load(InputStream stream, boolean is9Patch, boolean convert) throws IOException {
		BufferedImage image = null;
		try {
			image = GraphicsUtilities.loadCompatibleImage(stream);
		} catch (MalformedURLException e) {
			return null;
		}

		return load(image, is9Patch, convert);
	}

	public static NinePatch load(BufferedImage image, boolean is9Patch, boolean convert) {
		if (!is9Patch) {
			if (convert)
				image = convertTo9Patch(image);
			else
				return null;
		} else {
			ensure9Patch(image);
		}

		return new NinePatch(image);
	}

	public int getWidth() {
		return this.mImage.getWidth();
	}

	public int getHeight() {
		return this.mImage.getHeight();
	}

	public boolean getPadding(int[] padding) {
		this.mChunk.getPadding(padding);
		return true;
	}
	public void draw(Graphics graphics2D, int x, int y, int scaledWidth, int scaledHeight) {
		this.mChunk.draw(this.mImage,(Graphics2D) graphics2D, x, y, scaledWidth, scaledHeight, 0, 0);
	}
	public void draw(Graphics2D graphics2D, int x, int y, int scaledWidth, int scaledHeight) {
		this.mChunk.draw(this.mImage, graphics2D, x, y, scaledWidth, scaledHeight, 0, 0);
	}

	private NinePatch(BufferedImage image) {
		this.mChunk = NinePatchChunk.create(image);
		this.mImage = extractBitmapContent(image);
	}

	private static void ensure9Patch(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		for (int i = 0; i < width; i++) {
			int pixel = image.getRGB(i, 0);
			if ((pixel != 0) && (pixel != -16777216)) {
				image.setRGB(i, 0, 0);
			}
			pixel = image.getRGB(i, height - 1);
			if ((pixel != 0) && (pixel != -16777216)) {
				image.setRGB(i, height - 1, 0);
			}
		}
		for (int i = 0; i < height; i++) {
			int pixel = image.getRGB(0, i);
			if ((pixel != 0) && (pixel != -16777216)) {
				image.setRGB(0, i, 0);
			}
			pixel = image.getRGB(width - 1, i);
			if ((pixel != 0) && (pixel != -16777216))
				image.setRGB(width - 1, i, 0);
		}
	}

	private static BufferedImage convertTo9Patch(BufferedImage image) {
		BufferedImage buffer = GraphicsUtilities.createTranslucentCompatibleImage(image.getWidth() + 2, image.getHeight() + 2);

		Graphics2D g2 = buffer.createGraphics();
		g2.drawImage(image, 1, 1, null);
		g2.dispose();

		return buffer;
	}

	private BufferedImage extractBitmapContent(BufferedImage image) {
		return image.getSubimage(1, 1, image.getWidth() - 2, image.getHeight() - 2);
	}
}