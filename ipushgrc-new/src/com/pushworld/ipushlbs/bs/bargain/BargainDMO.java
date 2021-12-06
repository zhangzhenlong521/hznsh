package com.pushworld.ipushlbs.bs.bargain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TBUtil;


/**
 * 合同模块DMO
 * 
 * @author hm
 * 
 */
public class BargainDMO extends AbstractDMO {
	private TBUtil tbutil = new TBUtil();
	private CommDMO commDmo = new CommDMO();
	/*
	 * 把office和附件一起复制
	 */
	public HashMap bargainCopyFile(HashMap fileMap) {// key=office,files
		HashMap returnFile = new HashMap();
		if (fileMap != null) {
			Iterator it = fileMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String value = (String) entry.getValue();
				if (entry.getKey().equals("office")) {
					try {
						String name = bargainCopyFile(value);
						returnFile.put(entry.getKey(), name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					String name = "";
					String names[] = value.split(";");
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < names.length; i++) {
						// /20110202/N2672_D7C9D1AFB7FECEF1BACFCDAC.doc
						try {
							name = bargainCopyFile(names[i].substring(1, 9), names[i].substring(names[i].lastIndexOf("/")+1, names[i].length()));
							if (i != names.length - 1) {
								sb.append(name + ";");
							}else{
								sb.append(name);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					returnFile.put(entry.getKey(), sb.toString());
				}
			}
		}
		return returnFile;
	}
	/*
	 * 用来赋值上传的附件
	 */
	public String bargainCopyFile(String filePath, String _fileName) throws Exception {
		String wfpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + File.separator + "upload";// 创建临时目录C:/WebPushTemp/officecompfile/word
		File oldfile = new File(wfpath + File.separator + filePath + File.separator + _fileName);
		if (oldfile.exists()) {
			String newName = _fileName.substring(_fileName.indexOf("_") + 1, _fileName.lastIndexOf("."));
			String str_extName = _fileName.substring(_fileName.lastIndexOf(".") + 1, _fileName.length()); // 扩展名
			String no = "N" + commDmo.getSequenceNextValByDS(null, "s_pub_fileupload") + "_";
			String lastName = no + newName + "." + str_extName;
			FileInputStream input = new FileInputStream(oldfile);
			byte[] bs = tbutil.readFromInputStreamToBytes(input);
			String newPath = tbutil.getCurrDate(false, true);
			File f = new File(wfpath+File.separator+newPath);  //看看有没有目录
			if(!f.exists()){
				f.mkdir();
			}
			File newFile = new File(wfpath + File.separator + newPath + File.separator + lastName);
			FileOutputStream output = new FileOutputStream(newFile);
			tbutil.writeBytesToOutputStream(output, bs);
			return "/" + newPath + "/" + lastName;
		} else {
			String str_masterName = _fileName.substring(0, _fileName.lastIndexOf(".")); // 主名
			String str_extName = _fileName.substring(_fileName.lastIndexOf(".") + 1, _fileName.length()); // 扩展名
			if (str_masterName.indexOf("_") > 0) { // 如果有序号!
				str_masterName = _fileName.substring(_fileName.indexOf("_") + 1, _fileName.lastIndexOf(".")); //
			}
			str_masterName = new TBUtil().convertHexStringToStr(str_masterName); // 将16进制转成看得懂的中文
			throw new Exception("服务器端没有[" + _fileName + "]文件,转码前[" + str_masterName + "." + str_extName + "]");
		}
	}
	/*
	 * 用来复制office控件文件
	 */
	public String bargainCopyFile(String _fileName) throws Exception {
		String wfpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + File.separator + "officecompfile";// 创建临时目录C:/WebPushTemp/officecompfile/word
		File oldfile = new File(wfpath + File.separator + _fileName);
		if (oldfile.exists()) {
			String newName = _fileName.substring(_fileName.indexOf("_") + 1, _fileName.lastIndexOf("."));
			String str_extName = _fileName.substring(_fileName.lastIndexOf(".") + 1, _fileName.length()); // 扩展名
			String no = "N" + commDmo.getSequenceNextValByDS(null, "s_pub_fileupload") + "_";
			String lastName = no + newName + "." + str_extName;
			FileInputStream input = new FileInputStream(oldfile);
			byte[] bs = tbutil.readFromInputStreamToBytes(input);
			File newFile = new File(wfpath + File.separator + lastName);
			FileOutputStream output = new FileOutputStream(newFile);
			tbutil.writeBytesToOutputStream(output, bs);
			return lastName;
		} else {
			String str_masterName = _fileName.substring(0, _fileName.lastIndexOf(".")); // 主名
			String str_extName = _fileName.substring(_fileName.lastIndexOf(".") + 1, _fileName.length()); // 扩展名
			if (str_masterName.indexOf("_") > 0) { // 如果有序号!
				str_masterName = _fileName.substring(_fileName.indexOf("_") + 1, _fileName.lastIndexOf(".")); //
			}
			str_masterName = new TBUtil().convertHexStringToStr(str_masterName); // 将16进制转成看得懂的中文
			throw new Exception("服务器端没有[" + _fileName + "]文件,转码前[" + str_masterName + "." + str_extName + "]");
		}
	}
	public void deleteBargainCopyFile(){
		
	}
}
