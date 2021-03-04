package cn.com.infostrategy.bs.sysapp;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.WLTAppException;

/**
 * 服务器端文件系统处理对象!!!
 * 即我们后来增加了一个监控工具,从客户端
 * @author xch
 *
 */
public class ServerFileSystemBSO {

	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getFileListByDirectory(HashMap<String, Object> param) throws Exception {
		HashMap<String, Object> rtMap = new HashMap<String, Object>();
		List<HashMap<String, Object>> date = new ArrayList<HashMap<String, Object>>();
		int level = 1;
		boolean rootable = true;
		String directory = null;
		String fileType = "folder";
		if (param != null) {
			if (param.containsKey("directory")) {
				directory = (String) param.get("directory");
			}
			if (param.containsKey("level")) {
				level = Integer.parseInt(param.get("level").toString());
			}
			if (param.containsKey("rootable")) {
				rootable = Boolean.parseBoolean(param.get("rootable").toString());
			}
			if (param.containsKey("filetype")) {
				fileType = param.get("filetype").toString();
			}
		}
		File[] rootfiles = null;
		if (directory != null && !"".equals(directory)) {
			File rfile = new File(directory);
			if (rfile.exists()) {
				rootfiles = new File[] { rfile };
			} else {
				File sfile = getSystemFile(directory);
				if (sfile != null && sfile.exists()) {
					rootfiles = new File[] { sfile };
				} else {
					rtMap.put("state", "404");
					return rtMap;
				}
			}
		} else {
			rootfiles = FileSystemView.getFileSystemView().getRoots();
		}
		if (rootfiles != null && rootfiles.length > 0) {
			File rootFile = null;
			rtMap.put("state", "100");
			for (int i = 0; i < rootfiles.length; i++) {
				rootFile = rootfiles[i];
				if (rootFile.exists()) {
					if (level > 0) {
						if (rootable) {
							date.add(getFilePropertyMap(rootFile, level, 0, directory == null?true:false));
						}
						getFileByLevel(rootFile, date, level, 1, fileType);
						rtMap.put("data", date);
					}
				}
			}
		} else {
			rtMap.put("state", "404");
		}
		return rtMap;
	}

	/**
	 * 没找到啥好办法 
	 * 来得到我的电脑与网上邻居
	 * @return
	 */
	public File getSystemFile(String filename) {
		File rtn = null;
		File[] rootfiles = FileSystemView.getFileSystemView().getRoots();
		if (rootfiles != null) {
			File rootFile = null;
			for (int i = 0; i < rootfiles.length; i++) {
				rootFile = rootfiles[i];
				File[] listfiles = FileSystemView.getFileSystemView().getFiles(rootFile, false);
				if (listfiles != null && listfiles.length > 0) {
					File a = null;
					for (int i1 = 0; i1 < listfiles.length; i1++) {
						a = listfiles[i1];
						if (filename.equals(a.getPath())) {
							rtn = a;
							break;
						}
					}
				}
			}
		}
		return rtn;
	}

	public boolean isTraversable(File f) {
		Boolean traversable = null;
		if (f != null) {
			if (FileSystemView.getFileSystemView() != null) {
				traversable = FileSystemView.getFileSystemView().isTraversable(f);
			}
		}
		return (traversable != null && traversable.booleanValue());
	}

	/**
	 * @param root
	 * @param list
	 * @param rlevel
	 * @param clevel
	 * @param fileType
	 */
	private void getFileByLevel(File root, List<HashMap<String, Object>> list, int rlevel, int clevel, String fileType) {
		if (clevel <= rlevel) {
			File[] listfiles = FileSystemView.getFileSystemView().getFiles(root, false);
			if (listfiles != null && listfiles.length > 0) {
				File a = null;
				for (int i = 0; i < listfiles.length; i++) {
					a = listfiles[i];
					if ("folder".equals(fileType)) {
						if (FileSystemView.getFileSystemView().isFileSystemRoot(a) || a.isDirectory())
							list.add(getFilePropertyMap(a, rlevel, clevel));
					} else if ("singlefile".equals(fileType)) {
						if (!a.isDirectory()) {
							list.add(getFilePropertyMap(a, rlevel, clevel));
						}
					} else {
						list.add(getFilePropertyMap(a, rlevel, clevel));
					}
					if (a.isDirectory()) {
						getFileByLevel(a, list, rlevel, clevel + 1, fileType);
					}
				}
			}
		}
	}

	public HashMap upLoadFile(HashMap param) {
		String path = param.get("path").toString();
		String name = param.get("name").toString();
		int off = Integer.parseInt(param.get("off").toString());
		byte[] buf = (byte[]) param.get("date");
		return upLoadFile(path, name, buf, off);
	}

	public HashMap upLoadFile(String path, String name, byte[] buf, int off) {
		RandomAccessFile c = null;
		try {
			c = new RandomAccessFile(path + File.separator + name, "rw");
			c.seek(off);
			c.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new WLTAppException(e.getMessage());
				}
			}
		}
		return null;
	}

	public HashMap downLoadFile(HashMap param) {
		String path = param.get("path").toString();
		int len = Integer.parseInt(param.get("len").toString());
		int off = Integer.parseInt(param.get("off").toString());
		return downLoadFile(path, len, off);
	}
	
	public HashMap deleteFile(HashMap param) {
		try{
			String path = param.get("path").toString();
			File def = new File(path);
			def.delete();
		}catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		}
		return null;
	}

	public HashMap downLoadFile(String path, int len, int off) {
		RandomAccessFile c = null;
		byte[] buf = new byte[len];
		try {
			c = new RandomAccessFile(path, "rw");
			c.seek(off);
			System.out.println("off:" + off);
			System.out.println("len:" + len);
			System.out.println(off + len + "__" + c.length());
			if (off < c.length() && (off + len) > c.length()) {
				buf = new byte[Integer.parseInt(c.length() + "") - off];
			} // 就是说取最后一次的时候如果不够了 只取剩下的，由于此处的问题 导致下载的jar在mainfest.mf里多了很多空行
			int rlen = c.read(buf);
			System.out.println("rlen:" + rlen);
			HashMap rtnm = new HashMap();
			rtnm.put("rlen", rlen);
			rtnm.put("byte", buf);
			return rtnm;
		} catch (IOException e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new WLTAppException(e.getMessage());
				}
			}
		}
	}

	public HashMap readLineFromFile(HashMap param) {
		BufferedReader br = null;
		try {
			String path = param.get("path").toString();
			int len = Integer.parseInt(param.get("len").toString());
			int off = Integer.parseInt(param.get("off").toString());
			int rlen = 0;
			HashMap rtn = new HashMap();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GBK"));
			String line = null;
			List sb = new ArrayList();
			int i = 0;
			int j = 0;
			while ((line = br.readLine()) != null) {
				if (i >= len) {
					break;
				}
				if (j >= off) {
					sb.add(line);
					i++;
					off++;
				}
				j++;
			}
			rtn.put("string", (String[]) sb.toArray(new String[0]));
			rtn.put("rlen", i);
			rtn.put("off", off);
			return rtn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		}
	}

	public HashMap readAllLineFromFile(HashMap param) {
		BufferedReader br = null;
		try {
			String path = param.get("path").toString();
			int length = Integer.parseInt(param.get("length").toString());
			HashMap rtn = new HashMap();
			File logf = new File(path);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(logf), "GBK"));
			String line = null;
			List sb = new ArrayList();
			int i = 0;
			if (length < logf.length()) {
				while ((line = br.readLine()) != null) {
					sb.add(line);
					i++;
				}
			}
			rtn.put("string", (String[]) sb.toArray(new String[0]));
			rtn.put("rlen", i);
			rtn.put("length", logf.length());
			return rtn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		}
	}
	
	private HashMap<String, Object> getFilePropertyMap(File file, int rlevel, int clevel){
		return getFilePropertyMap(file, rlevel, clevel, false);
	}

	private HashMap<String, Object> getFilePropertyMap(File file, int rlevel, int clevel, boolean directory) {
		HashMap<String, Object> fileMap = new HashMap<String, Object>();
		fileMap.put("id", file.getPath());
		fileMap.put("name", FileSystemView.getFileSystemView().getSystemDisplayName(file));
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date modifyDate = new Date(file.lastModified());
		fileMap.put("lastmodify", sf.format(modifyDate));
		if (file.length() >= 1024) {
			fileMap.put("length", file.length() / 1024l + "KB");
		} else {
			fileMap.put("length", file.length() + "B");
		}
		fileMap.put("reallength", file.length() + "");
		if(directory) {
			fileMap.put("parentid", null);
		} else {
			fileMap.put("parentid", file.getParent());//win7这里好像有问题，就是跟目录的父亲是他的子目录导致前台出问题，所以这里特殊处理
		}
		fileMap.put("isdirectory", file.isDirectory() ? "Y" : "N");
		fileMap.put("level", clevel + "");
		fileMap.put("filetype", FileSystemView.getFileSystemView().getSystemTypeDescription(file));
		fileMap.put("ifhasExpand", clevel < rlevel ? "true" : "false");
		if (File.separatorChar == '/'){
			fileMap.put("icon", null);
		}else{
			fileMap.put("icon", imageToBytes((FileSystemView.getFileSystemView().getSystemIcon(file)), "jpg"));
		}
		//可以每个类型传一个优化
		return fileMap;
	}

	//这个方法在tbutil里有copy过来，开始是直接传icon有点问题好像是版本号的问题
	public byte[] imageToBytes(Icon icon, String format) {
		BufferedImage bImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bImage.getGraphics();
		icon.paintIcon(null, bg, 0, 0);
		bg.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(bImage, format, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public HashMap runFile(HashMap param) {
		String path = param.get("path").toString();
		return runFile(path);
	}

	public HashMap runFile(final String path) {
		Timer runtimer = new Timer();
		String dir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + File.separator + "runlog";
		File dirf = new File(dir);
		if (!dirf.exists()) {
			dirf.mkdirs();
		}
		final String logpath = dir + File.separator + System.currentTimeMillis() + ".txt";
		File log = new File(logpath);
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new WLTAppException(e.getMessage());
			}
		}
		runtimer.schedule(new TimerTask() {
			public void run() {
				realRun(path, logpath);
			}
		}, 0);
		HashMap rtn = new HashMap();
		rtn.put("logpath", logpath);
		return rtn;
	}

	public void realRun(final String path, final String logpath) {
		try {
			Timer logtimer = new Timer();
			logtimer.schedule(new TimerTask() {
				public void run() {
					new NoteLogThread(path, logpath).run();
				}
			}, 0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		}
	}

	class NoteLogThread {
		private String cmdpath = null;
		private String logpath = null;
		private Thread time1 = null;
		private Thread time2 = null;
		private FileOutputStream fo = null;
		private FileWriter writer = null;
		private PrintWriter pw = null;

		public NoteLogThread(String cmdpath_, String logpath_) {
			this.cmdpath = cmdpath_;
			this.logpath = logpath_;
		}

		public void run() {
			String cmdstr = null;
			if (File.separatorChar == '/') {//linux
				cmdstr = "/bin/sh " + cmdpath;
			} else {//window
				cmdstr = cmdpath;
			}
			try {
				final File logf = new File(logpath);
				if (!logf.exists()) {
					logf.createNewFile();
				}
				fo = new FileOutputStream(logf);
				writer = new FileWriter(logf, true);
				pw = new PrintWriter(writer);
				final Process p = Runtime.getRuntime().exec(cmdstr);
				time1 = new Thread(new Runnable() {
					public void run() {
						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
							String line = null;
							while ((line = br.readLine()) != null) {
								pw.println(line);
								pw.flush();
							}
							time1.interrupt();
							time1 = null;
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (time2 == null) {
								try {
									pw.println("-------------结束-----------");
									pw.close();
									writer.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});
				time1.start();

				time2 = new Thread(new Runnable() {
					public void run() {
						try {
							BufferedReader br2 = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
							String line2 = null;
							while ((line2 = br2.readLine()) != null) {
								pw.println(line2);
								pw.flush();
							}
							time2.interrupt();
							time2 = null;
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (time1 == null) {
								try {
									pw.println("-------------结束-----------");
									pw.close();
									writer.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});
				time2.start();
			} catch (Exception e) {
				e.printStackTrace();
				if (time1 == null && time2 == null) {
					try {
						if(pw != null) {
							pw.println(e.getMessage());
							pw.println("-------------结束-----------");
							pw.close();
						}
						if(writer != null){
							writer.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		File[] a = FileSystemView.getFileSystemView().getRoots();
		File aa = new File(a[0].getPath(), "我的电脑");
		System.out.println(aa.exists());
		File bbb = FileSystemView.getFileSystemView().createFileObject("::{208D2C60-3AEA-1069-A2D7-08002B30309D}");
		System.out.println(bbb.exists());
		File[] b = a[0].listFiles();
		for (int i = 0; i < b.length; i++) {
			System.out.println(b[i] + ":" + b[i].getPath() + b[i]);
		}
	}
}
