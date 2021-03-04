/*
 * @(#)CachedImageBevelBorder14.java  1.1.1  2007-01-11
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package cn.com.infostrategy.ui.mdata.hmui.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.border.Border;

/**
 * Draws a filled bevel border using an image and insets.
 * The image must consist of a bevel and a fill area.
 * Volatile images of the border are cached to speed up drawing.
 * <p>
 * The insets and the size of the image are
 * used do determine which parts of the image shall be
 * used to draw the corners and edges of the bevel as
 * well the fill area.
 *
 * <p>For example, if you provide an image of size 10,10
 * and a insets of size 2, 2, 4, 4, then the corners of
 * the border are made up of top left: 2,2, top right: 2,4,
 * bottom left: 2,4, bottom right: 4,4 rectangle of the image.
 * The inner area of the image is used to fill the inner area.
 *
 * @author  Werner Randelshofer
 * @version 1.1.1 2007-01-11 Don't create subimages with widths or
 * heights smaller than 1 pixel. 
 * <br>1.1 2005-11-30 Convert image to buffered image lazily.
 * <br>1.0 September 7, 2005 Created.
 */
public class CachedImageBevelBorder14 extends CachedPainter14 implements Border {
	private final static boolean VERBOSE = false;
	/**
	 * The image to be used for drawing.
	 */
	private Image image;

	/**
	 * The border insets
	 */
	private Insets borderInsets;
	/**
	 * The insets of the image.
	 */
	private Insets imageInsets;

	/**
	 * This attribute is set to true, when the image
	 * is used to fill the content area too.
	 */
	private boolean fillContentArea;

	private Rectangle clip; //���ж��١�
	private CropImageFilter filter;

	/**
	 * We don't need arguments. Instead of creating an array all the time,
	 * we cache one.
	 */
	private Object[] args = { this };

	/**
	 * Creates a new instance with the given image and insets.
	 * The image has the same insets as the border.
	 */
	public CachedImageBevelBorder14(Image img, Insets borderInsets) {
		this(img, borderInsets, borderInsets, true);
	}

	/**
	 * Creates a new instance with the given image and insets.
	 * The image has different insets than the border.
	 */
	public CachedImageBevelBorder14(Image img, Insets imageInsets, Insets borderInsets) {
		this(img, imageInsets, borderInsets, true);
	}

	/**
	 * Creates a new instance with the given image and insets.
	 * The image has different insets than the border.
	 */
	public CachedImageBevelBorder14(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea) {
		this(img, imageInsets, borderInsets, fillContentArea, null);
	}
	public CachedImageBevelBorder14(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea, Rectangle _clip) {
		super(32);
		this.image = img;
		this.imageInsets = imageInsets;
		this.borderInsets = borderInsets;
		this.fillContentArea = fillContentArea;
		clip = _clip;
		if (clip != null) {
			filter = new CropImageFilter(0, 0, (int) _clip.getWidth(), (int) _clip.getHeight());
			image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
		}
	}

	/**
	 * Returns true if the border is opaque.
	 * This implementation always returns false.
	 */
	public boolean isBorderOpaque() {
		return false;
	}

	/**
	 * Returns the insets of the border.
	 * @param c the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c) {
		return (Insets) borderInsets.clone();
	}

	/**
	 * Paints the bevel image for the specified component with the
	 * specified position and size.
	 * @param c the component for which this border is being painted
	 * @param gr the paint graphics
	 * @param x the x position of the painted border
	 * @param y the y position of the painted border
	 * @param width the width of the painted border
	 * @param height the height of the painted border
	 */
	public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
		if (image == null)
			return;
		if (!gr.getClipBounds().intersects(x, y, width, height)) {
			return;
		}
		paint(c, gr, x, y, width, height, args);

	}

	/**
	 * Creates the image to cache.  This returns a translucent image.
	 *
	 * @param c Component painting to
	 * @param w Width of image to create
	 * @param h Height to image to create
	 * @param config GraphicsConfiguration that will be
	 *        rendered to, this may be null.
	 */
	protected Image createImage(Component c, int w, int h, GraphicsConfiguration config) {
		if (config == null) {
			return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
		}
		return config.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
	}

	protected void paintToImage(Component c, Graphics gr, int width, int height, Object[] args) {
		// Cast Graphics to Graphics2D
		// Workaround for Java 1.4 and 1.4 on Mac OS X 10.4. We create a new
		// Graphics object instead of just casting the provided one. This is
		// because drawing texture paints appears to confuse the Graphics object.
		Graphics2D g = (Graphics2D) gr.create();

		// Convert image to buffered image (and keep the buffered image).
		image = Images.toBufferedImage(image);
		BufferedImage img = (BufferedImage) image;

		// Set some variables for easy access of insets and image size
		int top = imageInsets.top;
		int left = imageInsets.left;
		int bottom = imageInsets.bottom;
		int right = imageInsets.right;
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();

		// Optimisation: Draw image directly if it fits into the component
		if (fillContentArea) {
			if (width == imgWidth && height == imgHeight) {
				g.drawImage(img, 0, 0, c);
				g.dispose();
				return;
			}
		}

		// Optimisation: Remove insets, if image width or image height fits
		if (width == imgWidth) {
			left = imgWidth;
			right = 0;
		}
		if (height == imgHeight) {
			top = imgHeight;
			bottom = 0;
		}

		// Adjust insets if component is too small
		if (width < left + right) {
			left = Math.min(left, width / 2); //Math.max(0, left + (width - left - right) / 2);
			right = width - left;
		}
		if (height < top + bottom) {
			top = Math.min(top, height / 2); //Math.max(0, top + (height - top - bottom) / 2);
			bottom = height - top;
		}

		// Draw the Corners
		if (top > 0 && left > 0) {
			g.drawImage(img, 0, 0, left, top, 0, 0, left, top, c);
		}
		if (top > 0 && right > 0) {
			//g.fillRect(x+width-right, y, x+width, y+top);
			g.drawImage(img, width - right, 0, width, top, imgWidth - right, 0, imgWidth, top, c);
		}
		if (bottom > 0 && left > 0) {
			g.drawImage(img, 0, height - bottom, left, height, 0, imgHeight - bottom, left, imgHeight, c);
		}
		if (bottom > 0 && right > 0) {
			g.drawImage(img, width - right, height - bottom, width, height, imgWidth - right, imgHeight - bottom, imgWidth, imgHeight, c);
		}

		// Draw the edges
		BufferedImage subImg = null;
		TexturePaint paint;

		// North
		if (top > 0 && left + right < width) {
			if (imgWidth > right + left) {
				subImg = img.getSubimage(left, 0, imgWidth - right - left, top);
				paint = new TexturePaint(subImg, new Rectangle(left, 0, imgWidth - left - right, top));
				g.setPaint(paint);
				g.fillRect(left, 0, width - left - right, top);
			}
		}
		// South
		if (bottom > 0 && left + right < width) {
			if (imgHeight > bottom && imgWidth > right + left) {
				subImg = img.getSubimage(left, imgHeight - bottom, imgWidth - right - left, bottom);
				paint = new TexturePaint(subImg, new Rectangle(left, height - bottom, imgWidth - left - right, bottom));
				g.setPaint(paint);
				g.fillRect(left, height - bottom, width - left - right, bottom);
			}
		}
		// West
		if (left > 0 && top + bottom < height) {
			if (imgHeight > top + bottom) {
				subImg = img.getSubimage(0, top, left, imgHeight - top - bottom);
				paint = new TexturePaint(subImg, new Rectangle(0, top, left, imgHeight - top - bottom));
				g.setPaint(paint);
				g.fillRect(0, top, left, height - top - bottom);
			}
		}
		// East
		if (right > 0 && top + bottom < height) {
			if (imgWidth > right && imgHeight > top + bottom) {
				subImg = img.getSubimage(imgWidth - right, top, right, imgHeight - top - bottom);
				paint = new TexturePaint(subImg, new Rectangle(width - right, top, right, imgHeight - top - bottom));
				g.setPaint(paint);
				g.fillRect(width - right, top, right, height - top - bottom);
			}
		}

		// Fill the center
		if (fillContentArea) {
			if (left + right < imgWidth && top + bottom < imgHeight) {
				subImg = img.getSubimage(left, top, imgWidth - right - left, imgHeight - top - bottom);
				paint = new TexturePaint(subImg, new Rectangle(left, top, imgWidth - right - left, imgHeight - top - bottom));
				g.setPaint(paint);
				g.fillRect(left, top, width - right - left, height - top - bottom);
			}
		}
		g.dispose();
	}
}