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
 * ѹ��,��ѹ������
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
				// �������Ŀ¼
				file_crete.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFile, "");// �ݹ�ѹ������
		out.close();
	}

	/**
	 * �ݹ�ѹ������
	 * 
	 * @param out
	 *            ѹ���������
	 * @param f
	 *            ��Ҫѹ�����ļ�
	 * @param base
	 *            ѹ����·��
	 * @throws Exception
	 */
	private static void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) { // ������ļ��У����ȡ����������ļ�
			File[] fl = f.listFiles();
			//ԭ�е�ѹ�������ϰ�/ѹ�������С�
			base = base.length() == 0 ? "" : base + File.separator;
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else { // ������ļ�����ѹ��
			if (base != null && !"".equals(base)) {
				out.putNextEntry(new ZipEntry(base)); // ������һ��ѹ���ڵ�
				FileInputStream in = new FileInputStream(f); // ��ȡ�ļ�����
				int b;
				while ((b = in.read()) != -1)
					out.write(b); // д�뵽ѹ����
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
				//��ǰû�п��ǽ�ѹ�ļ������ļ��е���������ѽ�������/2016-09-08��
				if (zipEntry.getName().contains(File.separator)) {
					//outFile.getPath() ·����  C:\Users\Administrator\WEBLIGHT_CODECACHE_DEBUG\importDB\0\1473302895312
					//zipEntry.getName() ·���� icheck\20160907\N3142_BBFDB7D6D6B8B1EABFE2.xls
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
