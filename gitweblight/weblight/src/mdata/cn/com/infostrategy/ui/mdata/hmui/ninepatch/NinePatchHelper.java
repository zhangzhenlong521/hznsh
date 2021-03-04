package cn.com.infostrategy.ui.mdata.hmui.ninepatch;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NinePatchHelper {
	public static NinePatch createNinePatch(URL fileUrl, boolean convert) {
		try {
			return NinePatch.load(fileUrl, convert);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static NinePatch createNinePatch(InputStream stream, boolean is9Patch, boolean convert) throws IOException {
		return NinePatch.load(stream, is9Patch, convert);
	}

	public static NinePatch createNinePatch(BufferedImage image, boolean is9Patch, boolean convert) {
		return NinePatch.load(image, is9Patch, convert);
	}
}