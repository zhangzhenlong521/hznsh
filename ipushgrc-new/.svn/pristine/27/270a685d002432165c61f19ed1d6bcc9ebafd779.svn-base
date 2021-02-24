package com.pushworld.ipushgrc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩,解压缩工具
 * @author hm
 *
 */
public class ZipFileUtil {
	public boolean zip(String zipFileName, String inputFile) {
		File f = new File(inputFile);
		boolean flag = false;
		try {
			zip(zipFileName, new File(inputFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (f.exists() && (f.length() > 0)) {
			return true;
		}
		return flag;
	}

	private static void zip(String zipFileName, File inputFile) throws Exception {

		File file_crete = new File(zipFileName);
		if (file_crete.exists()) {
			file_crete.delete();
		} else {
			try {
				// 创建打包目录
				file_crete.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFile, "");// 递归压缩方法
		out.close();
	}

	/**
	 * 递归压缩方法
	 * 
	 * @param out
	 *            压缩包输出流
	 * @param f
	 *            需要压缩的文件
	 * @param base
	 *            压缩的路径
	 * @throws Exception
	 */
	private static void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) { // 如果是文件夹，则获取下面的所有文件
			File[] fl = f.listFiles();
			//原有的压缩方法老把/压缩到包中。
			base = base.length() == 0 ? "" : base + File.separator;
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else { // 如果是文件，则压缩
			if (base != null && !"".equals(base)) {
				out.putNextEntry(new ZipEntry(base)); // 生成下一个压缩节点
				FileInputStream in = new FileInputStream(f); // 读取文件内容
				int b;
				while ((b = in.read()) != -1)
					out.write(b); // 写入到压缩包
				in.close();
			}
		}
	}

	/** ************************************ */
	public static boolean unzip(String outputFileName) {
		int index = outputFileName.lastIndexOf(".");
		boolean flag = true;
		String inputFileName = outputFileName.substring(0, index);
		try {
			unzip(outputFileName, inputFileName);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	public static void unzip(String zipFilename, String outputDirectory) throws Exception {
		File outFile = new File(outputDirectory);
		if (!outFile.exists()) {
			outFile.mkdirs();
		}
		ZipFile zipFile = new ZipFile(zipFilename);
		Enumeration en = zipFile.entries();
		ZipEntry zipEntry = null;
		while (en.hasMoreElements()) {
			zipEntry = (ZipEntry) en.nextElement();
			if (File.separator.equals(zipEntry.getName())) {
				continue;
			}
			if (zipEntry.isDirectory()) {
				// mkdir directory
				String dirName = zipEntry.getName();
				dirName = dirName.substring(0, dirName.length() - 1);
				File f = new File(outFile.getPath() + File.separator + dirName);
				f.mkdirs();
			} else {
				// unzip file
				//以前没有考虑解压文件中有文件夹的情况，现已解决【李春娟/2016-09-08】
				if (zipEntry.getName().contains(File.separator)) {
					//outFile.getPath() 路径如  C:\Users\Administrator\WEBLIGHT_CODECACHE_DEBUG\importDB\0\1473302895312
					//zipEntry.getName() 路径如 icheck\20160907\N3142_BBFDB7D6D6B8B1EABFE2.xls
					String currpath = outFile.getPath() + File.separator + zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf(File.separator));
					File df = new File(currpath);
					if (!df.exists()) {
						df.mkdirs();
					}
				}
				File f = new File(outFile.getPath() + File.separator + zipEntry.getName());
				f.createNewFile();
				InputStream in = zipFile.getInputStream(zipEntry);
				FileOutputStream out = new FileOutputStream(f);
				int c;
				byte[] by = new byte[1024];
				while ((c = in.read(by)) != -1) {
					out.write(by, 0, c);
				}
				out.close();
				in.close();
			}
		}
	}

}
