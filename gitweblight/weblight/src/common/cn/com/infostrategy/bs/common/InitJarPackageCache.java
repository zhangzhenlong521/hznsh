/**************************************************************************
 * $RCSfile: InitJarPackageCache.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.wlappletloader.bs.DownLoadTool;
import cn.com.infostrategy.wlappletloader.to.ClassFileVO;

public class InitJarPackageCache {

	private final static String str_image_path = "gxlu/nova/framework/images";

	private Vector vec_images = new Vector();

	private String[] str_jars;

	public InitJarPackageCache(String _realpath, String[] _initjars) {
		this.str_jars = _initjars;

		long ll_begin = System.currentTimeMillis();
		//WLTLogger.getLogger(this).debug("��ʼ��������JAR��·��...");

		// ��WEB-INF/lib�µ�����jar�ļ��İ�ע��һ��
		_realpath = _realpath.replace('\\', '/');
		_realpath = _realpath + "/WEB-INF/lib/";
		_realpath = _realpath.replaceAll("//", "/");
		WLTLogger.getLogger(this).info("[Ҫ�����JAR��·��]"+ _realpath);

		File lib_dir = new File(_realpath);
		File[] jar_files = lib_dir.listFiles(new FileFilter() {
			public boolean accept(File fi) {
				return fi.getPath().toLowerCase().endsWith(".jar");
			}
		});

		long ll_1 = 0;
		long ll_2 = 0;
		for (int i = 0; i < jar_files.length; i++) {
			try {
				String str_filename = _realpath + jar_files[i].getName();
				JarInputStream myJarInputStream = new JarInputStream(new FileInputStream(str_filename));

				int li_count = 0;
				boolean li_flag = false;

				if (isInitJar(jar_files[i].getName())) {
					li_flag = true;
					ll_1 = System.currentTimeMillis();
				}
				while (true) { // ѭ���ͷ�jar�µ��ļ���������һ��jarEntry����(jarEntry�������ǿɽ������ɽ�ѹjar���ÿһ��Ԫ��)
					JarEntry myJarEntry = myJarInputStream.getNextJarEntry();
					if (myJarEntry == null) { // ���jarEntryΪ�����ж�ѭ��
						break;
					} else if (myJarEntry.getName().toLowerCase().endsWith(".java")) {// �����java�ļ��򲻴���
						continue;
					}
					String str_entry = myJarEntry.getName();

					String str_key = null;
					if (myJarEntry.isDirectory()) { // ���ֱ���жϳ���Ŀ¼
						str_key = str_entry;
						str_key = str_key.replace('/', '.'); //
						str_key = str_key.substring(0, str_key.length() - 1);
					} else {
						str_key = str_entry;
						int li_pos = str_key.lastIndexOf("/");

						if (li_pos >= 0) {
							str_key = str_key.substring(0, li_pos);
							str_key = str_key.replace('/', '.'); //
							str_key = str_key.substring(0, str_key.length());
						}
					}

					if (str_key != null) {
						Object obj = DownLoadTool.ht_package_jar.get(str_key); // �Ƿ�����
						if (obj == null) { // ���û��,���½�һ��
							Vector v_jars = new Vector();
							v_jars.add(str_filename);
							DownLoadTool.ht_package_jar.put(str_key, v_jars);
							li_count++;
						} else {
							Vector v_jars = (Vector) obj; // �����,����ԭ���Ļ����ϼ�һ��
							if (!v_jars.contains(str_filename)) {
								v_jars.add(str_filename);
								DownLoadTool.ht_package_jar.put(str_key, v_jars);
								li_count++;
							}
						}
					}
					if (myJarEntry.isDirectory() || myJarEntry.getName().toLowerCase().endsWith(".java")) {
						continue;
					}

					ByteArrayOutputStream baos = null;
					byte[] byteCodes = null;
					if (li_flag) {
						baos = new ByteArrayOutputStream(); // ����������
						byte[] buf = new byte[1024];
						int len;
						while ((len = myJarInputStream.read(buf, 0, 1024)) > 0) {
							baos.write(buf, 0, len);
						}
						byteCodes = baos.toByteArray();
						baos.close();
						baos = null;

						String str_f = str_entry;
						String str_package = str_entry;
						int li_pos = str_entry.lastIndexOf("/");
						if (li_pos <= 0 || str_entry.length() <= li_pos + 1) {
						} else {
							str_package = str_entry.substring(0, li_pos);
							str_f = str_entry.substring(li_pos + 1);
						}
						Vector vec_files = (Vector) DownLoadTool.ht_pluto.get(str_package);
						if (vec_files == null) {
							vec_files = new Vector();
						}
						if (str_package.startsWith(str_image_path)) {
							String str_temp = str_f.toLowerCase();  //
							if (str_temp.endsWith(".jpg") || str_temp.endsWith(".gif") || str_temp.endsWith(".jpeg") || str_temp.endsWith(".ico") || str_temp.endsWith(".png")) {
								vec_images.add(str_f);
							}
						}
						ClassFileVO cfVO = new ClassFileVO();
						cfVO.setClassFileName(str_f);
						cfVO.setByteCodes(byteCodes);
						vec_files.add(cfVO);

						DownLoadTool.ht_pluto.put(str_package, vec_files);
					}
				}
				if (li_flag) {
					li_flag = false;
					ll_2 = System.currentTimeMillis(); //
					WLTLogger.getLogger(this).info("����[" + jar_files[i].getName() + "]���������,��ʱ[" + (ll_2 - ll_1) + "]!");
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		long ll_end = System.currentTimeMillis();
		WLTLogger.getLogger(this).info("��������JAR��·������,������[" + jar_files.length + "]��JAR�ļ�,��ʱ[" + (ll_end - ll_begin) + "]");
	}

	private boolean isInitJar(String _jar) {
		if (str_jars == null || str_jars.length == 0) {
			return false;
		}
		for (int i = 0; i < str_jars.length; i++) {
			if (str_jars[i] != null && str_jars[i].trim().equals(_jar)) {
				return true;
			}
		}
		return false;
	}

	public Vector getImagesVec() {
		return vec_images;
	}
}
