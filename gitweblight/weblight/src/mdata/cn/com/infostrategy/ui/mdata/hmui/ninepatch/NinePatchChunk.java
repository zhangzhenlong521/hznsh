package cn.com.infostrategy.ui.mdata.hmui.ninepatch;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatchChunk 
 * @Description: 把.9.png图片放大类。核心
 * @author haoming
 * @date Mar 19, 2013 4:37:36 PM
 *  
*/ 
public class NinePatchChunk implements Serializable {
	private static final long serialVersionUID = -7353439224505296217L;
	private static final int[] sPaddingRect = new int[4];
	private boolean mVerticalStartWithPatch;
	private boolean mHorizontalStartWithPatch;
	private List<Rectangle> mFixed;
	private List<Rectangle> mPatches;
	private List<Rectangle> mHorizontalPatches;
	private List<Rectangle> mVerticalPatches;
	private Pair<Integer> mHorizontalPadding;
	private Pair<Integer> mVerticalPadding;

	public static NinePatchChunk create(BufferedImage image) {
		NinePatchChunk chunk = new NinePatchChunk();
		chunk.findPatches(image);
		return chunk;
	}

	public void draw(BufferedImage image, Graphics2D graphics2D, int x, int y, int scaledWidth, int scaledHeight, int destDensity, int srcDensity) {
		boolean scaling = (destDensity != srcDensity) && (destDensity != 0) && (srcDensity != 0);

		if (scaling) {
			try {
				graphics2D = (Graphics2D) graphics2D.create();

				float densityScale = destDensity / srcDensity;

				graphics2D.translate(x, y);
				graphics2D.scale(densityScale, densityScale);

				scaledWidth = (int) (scaledWidth / densityScale);
				scaledHeight = (int) (scaledHeight / densityScale);
				x = y = 0;

				draw(image, graphics2D, x, y, scaledWidth, scaledHeight);
			} finally {
				graphics2D.dispose();
			}
		} else
			draw(image, graphics2D, x, y, scaledWidth, scaledHeight);
	}

	private void draw(BufferedImage image, Graphics2D graphics2D, int x, int y, int scaledWidth, int scaledHeight) {
		if ((scaledWidth <= 1) || (scaledHeight <= 1)) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics2D.create();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		try {
			if (this.mPatches.size() == 0) {
				g.drawImage(image, x, y, scaledWidth, scaledHeight, null);
				return;
			}
			g.translate(x, y);
			x = y = 0;

			DrawingData data = computePatches(scaledWidth, scaledHeight);

			int fixedIndex = 0;
			int horizontalIndex = 0;
			int verticalIndex = 0;
			int patchIndex = 0;

			float vWeightSum = 1.0F;
			float vRemainder = data.mRemainderVertical;

			boolean vStretch = this.mVerticalStartWithPatch;
			while (y < scaledHeight - 1) {
				boolean hStretch = this.mHorizontalStartWithPatch;

				int height = 0;
				float vExtra = 0.0F;

				float hWeightSum = 1.0F;
				float hRemainder = data.mRemainderHorizontal;

				while (x < scaledWidth - 1) {
					if (!vStretch) {
						Rectangle r;
						if (hStretch) {
							r = (Rectangle) this.mHorizontalPatches.get(horizontalIndex++);
							float extra = r.width / data.mHorizontalPatchesSum;
							int width = (int) (extra * hRemainder / hWeightSum);
							hWeightSum -= extra;
							hRemainder -= width;
							g.drawImage(image, x, y, x + width, y + r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
							x += width;
						} else {
							r = (Rectangle) this.mFixed.get(fixedIndex++);
							g.drawImage(image, x, y, x + r.width, y + r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
							x += r.width;
						}
						height = r.height;
					} else if (hStretch) {
						Rectangle r = (Rectangle) this.mPatches.get(patchIndex++);
						vExtra = r.height / data.mVerticalPatchesSum;
						height = (int) (vExtra * vRemainder / vWeightSum);
						float extra = r.width / data.mHorizontalPatchesSum;
						int width = (int) (extra * hRemainder / hWeightSum);
						hWeightSum -= extra;
						hRemainder -= width;
						g.drawImage(image, x, y, x + width, y + height, r.x, r.y, r.x + r.width, r.y + r.height, null);
						x += width;
					} else {
						Rectangle r = (Rectangle) this.mVerticalPatches.get(verticalIndex++);
						vExtra = r.height / data.mVerticalPatchesSum;
						height = (int) (vExtra * vRemainder / vWeightSum);
						g.drawImage(image, x, y, x + r.width, y + height, r.x, r.y, r.x + r.width, r.y + r.height, null);
						x += r.width;
					}

					hStretch = !hStretch;
				}
				x = 0;
				y += height;
				if (vStretch) {
					vWeightSum -= vExtra;
					vRemainder -= height;
				}
				vStretch = !vStretch;
			}
		} finally {
			g.dispose();
		}
		g.dispose();
	}

	public void getPadding(int[] padding) {
		padding[0] = ((Integer) this.mHorizontalPadding.mFirst).intValue();
		padding[2] = ((Integer) this.mHorizontalPadding.mSecond).intValue();
		padding[1] = ((Integer) this.mVerticalPadding.mFirst).intValue();
		padding[3] = ((Integer) this.mVerticalPadding.mSecond).intValue();
	}

	public int[] getPadding() {
		getPadding(sPaddingRect);
		return sPaddingRect;
	}

	private DrawingData computePatches(int scaledWidth, int scaledHeight) {
		DrawingData data = new DrawingData();
		boolean measuredWidth = false;
		boolean endRow = true;

		int remainderHorizontal = 0;
		int remainderVertical = 0;

		if (this.mFixed.size() > 0) {
			int start = ((Rectangle) this.mFixed.get(0)).y;
			for (Rectangle rect : this.mFixed) {
				if (rect.y > start) {
					endRow = true;
					measuredWidth = true;
				}
				if (!measuredWidth) {
					remainderHorizontal += rect.width;
				}
				if (endRow) {
					remainderVertical += rect.height;
					endRow = false;
					start = rect.y;
				}
			}
		}

		data.mRemainderHorizontal = (scaledWidth - remainderHorizontal);
		data.mRemainderVertical = (scaledHeight - remainderVertical);

		data.mHorizontalPatchesSum = 0.0F;
		if (this.mHorizontalPatches.size() > 0) {
			int start = -1;
			for (Rectangle rect : this.mHorizontalPatches)
				if (rect.x > start) {
					data.mHorizontalPatchesSum += rect.width;
					start = rect.x;
				}
		} else {
			int start = -1;
			for (Rectangle rect : this.mPatches) {
				if (rect.x > start) {
					data.mHorizontalPatchesSum += rect.width;
					start = rect.x;
				}
			}
		}

		data.mVerticalPatchesSum = 0.0F;
		if (this.mVerticalPatches.size() > 0) {
			int start = -1;
			for (Rectangle rect : this.mVerticalPatches)
				if (rect.y > start) {
					data.mVerticalPatchesSum += rect.height;
					start = rect.y;
				}
		} else {
			int start = -1;
			for (Rectangle rect : this.mPatches) {
				if (rect.y > start) {
					data.mVerticalPatchesSum += rect.height;
					start = rect.y;
				}
			}
		}

		return data;
	}

	private void findPatches(BufferedImage image) {
		int width = image.getWidth() - 2;
		int height = image.getHeight() - 2;

		int[] row = (int[]) null;
		int[] column = (int[]) null;

		row = GraphicsUtilities.getPixels(image, 1, 0, width, 1, row);
		column = GraphicsUtilities.getPixels(image, 0, 1, 1, height, column);

		boolean[] result = new boolean[1];
		Pair left = getPatches(column, result);
		this.mVerticalStartWithPatch = result[0];

		result = new boolean[1];
		Pair top = getPatches(row, result);
		this.mHorizontalStartWithPatch = result[0];

		this.mFixed = getRectangles((List) left.mFirst, (List) top.mFirst);
		this.mPatches = getRectangles((List) left.mSecond, (List) top.mSecond);

		if (this.mFixed.size() > 0) {
			this.mHorizontalPatches = getRectangles((List) left.mFirst, (List) top.mSecond);
			this.mVerticalPatches = getRectangles((List) left.mSecond, (List) top.mFirst);
		} else if (((List) top.mFirst).size() > 0) {
			this.mHorizontalPatches = new ArrayList(0);
			this.mVerticalPatches = getVerticalRectangles(height, (List) top.mFirst);
		} else if (((List) left.mFirst).size() > 0) {
			this.mHorizontalPatches = getHorizontalRectangles(width, (List) left.mFirst);
			this.mVerticalPatches = new ArrayList(0);
		} else {
			this.mHorizontalPatches = (this.mVerticalPatches = new ArrayList(0));
		}

		row = GraphicsUtilities.getPixels(image, 1, height + 1, width, 1, row);
		column = GraphicsUtilities.getPixels(image, width + 1, 1, 1, height, column);

		top = getPatches(row, result);
		this.mHorizontalPadding = getPadding((List) top.mFirst);

		left = getPatches(column, result);
		this.mVerticalPadding = getPadding((List) left.mFirst);
	}

	private List<Rectangle> getVerticalRectangles(int imageHeight, List<Pair<Integer>> topPairs) {
		List rectangles = new ArrayList();
		for (Pair top : topPairs) {
			int x = ((Integer) top.mFirst).intValue();
			int width = ((Integer) top.mSecond).intValue() - ((Integer) top.mFirst).intValue();

			rectangles.add(new Rectangle(x, 0, width, imageHeight));
		}
		return rectangles;
	}

	private List<Rectangle> getHorizontalRectangles(int imageWidth, List<Pair<Integer>> leftPairs) {
		List rectangles = new ArrayList();
		for (Pair left : leftPairs) {
			int y = ((Integer) left.mFirst).intValue();
			int height = ((Integer) left.mSecond).intValue() - ((Integer) left.mFirst).intValue();

			rectangles.add(new Rectangle(0, y, imageWidth, height));
		}
		return rectangles;
	}

	private Pair<Integer> getPadding(List<Pair<Integer>> pairs) {
		if (pairs.size() == 0)
			return new Pair(Integer.valueOf(0), Integer.valueOf(0));
		if (pairs.size() == 1) {
			if (((Integer) ((Pair) pairs.get(0)).mFirst).intValue() == 0) {
				return new Pair(Integer.valueOf(((Integer) ((Pair) pairs.get(0)).mSecond).intValue() - ((Integer) ((Pair) pairs.get(0)).mFirst).intValue()), Integer.valueOf(0));
			}
			return new Pair(Integer.valueOf(0), Integer.valueOf(((Integer) ((Pair) pairs.get(0)).mSecond).intValue() - ((Integer) ((Pair) pairs.get(0)).mFirst).intValue()));
		}

		int index = pairs.size() - 1;
		return new Pair(Integer.valueOf(((Integer) ((Pair) pairs.get(0)).mSecond).intValue() - ((Integer) ((Pair) pairs.get(0)).mFirst).intValue()), Integer.valueOf(((Integer) ((Pair) pairs.get(index)).mSecond).intValue() - ((Integer) ((Pair) pairs.get(index)).mFirst).intValue()));
	}

	private List<Rectangle> getRectangles(List<Pair<Integer>> leftPairs, List<Pair<Integer>> topPairs) {
		List rectangles = new ArrayList();
		for (Pair left : leftPairs) {
			int y = ((Integer) left.mFirst).intValue();
			int height = ((Integer) left.mSecond).intValue() - ((Integer) left.mFirst).intValue();
			for (Pair top : topPairs) {
				int x = ((Integer) top.mFirst).intValue();
				int width = ((Integer) top.mSecond).intValue() - ((Integer) top.mFirst).intValue();

				rectangles.add(new Rectangle(x, y, width, height));
			}
		}
		return rectangles;
	}

	private Pair<List<Pair<Integer>>> getPatches(int[] pixels, boolean[] startWithPatch) {
		int lastIndex = 0;
		int lastPixel = pixels[0];
		boolean first = true;

		List fixed = new ArrayList();
		List patches = new ArrayList();

		for (int i = 0; i < pixels.length; i++) {
			int pixel = pixels[i];
			if (pixel != lastPixel) {
				if (lastPixel == -16777216) {
					if (first)
						startWithPatch[0] = true;
					patches.add(new Pair(Integer.valueOf(lastIndex), Integer.valueOf(i)));
				} else {
					fixed.add(new Pair(Integer.valueOf(lastIndex), Integer.valueOf(i)));
				}
				first = false;

				lastIndex = i;
				lastPixel = pixel;
			}
		}
		if (lastPixel == -16777216) {
			if (first)
				startWithPatch[0] = true;
			patches.add(new Pair(Integer.valueOf(lastIndex), Integer.valueOf(pixels.length)));
		} else {
			fixed.add(new Pair(Integer.valueOf(lastIndex), Integer.valueOf(pixels.length)));
		}

		if (patches.size() == 0) {
			patches.add(new Pair(Integer.valueOf(1), Integer.valueOf(pixels.length)));
			startWithPatch[0] = true;
			fixed.clear();
		}

		return new Pair(fixed, patches);
	}

	static final class DrawingData {
		private int mRemainderHorizontal;
		private int mRemainderVertical;
		private float mHorizontalPatchesSum;
		private float mVerticalPatchesSum;
	}

	static class Pair<E> implements Serializable {
		private static final long serialVersionUID = -2204108979541762418L;
		E mFirst;
		E mSecond;

		Pair(E first, E second) {
			this.mFirst = first;
			this.mSecond = second;
		}

		public String toString() {
			return "Pair[" + this.mFirst + ", " + this.mSecond + "]";
		}
	}
}