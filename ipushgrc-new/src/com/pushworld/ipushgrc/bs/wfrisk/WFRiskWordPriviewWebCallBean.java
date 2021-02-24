package com.pushworld.ipushgrc.bs.wfrisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

import com.pushworld.ipushgrc.to.WordTBUtil;

/**
 * 流程文件维护  word预览
 * 如果服务器上面的操作系统是linux，无法安装office等办公软件，无法利用jcob生成word，那么就需要在另外一台windows的电脑上，部署同样一套系统
 * ，这台电脑就专门负责生成word，然后将生成好的word发送给原来的linux服务器
 * 功能还不完善，因为这需要上传的流程文件与这个Servlet都在同一台电脑上，以后可以改进成只在另一台电脑上部署这个Servlet就行
 * 
 * 要实现这种转发的功能需要在“系统参数”菜单里面设定参数“生成word需要访问的Servlet”的值
 * 
 * @author p17
 *
 */
public class WFRiskWordPriviewWebCallBean implements WebDispatchIfc {

	private static final long serialVersionUID = -8830330389851867437L;

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap _wfMap) throws Exception {
		String cmpfileid = null;
		if (_wfMap == null) {
			System.out.println("传入的参数为空!");
			return;
		}
		cmpfileid = (String) _wfMap.get("comfileid");
		_wfMap.remove("comfileid");//除掉这一项，因为这一项是另外添加的，不能用来生城word

		HashMap<String, Object> map = createWord(cmpfileid, _wfMap);//在本地生成好word，map里面有word文件的内容(字节数组)，以及文件名字
		sendDocToClient(_request, _response, map);//将这个包含生成的word文件信息的map发送给客户端，让它重新建立一个文件就可以了
	}

	/**
	 * 将生成的word的字节数组发送给客户端
	 * @param request
	 * @param response
	 * @param map
	 * @throws IOException
	 */
	private void sendDocToClient(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> map) throws IOException {
		System.out.println("《》《》《》《》《》cn.com.infostrategy.bs.mdata.WFRiskWordPriviewServlet：发送给客户端word字节数组····" + map.get("size"));
		ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
		oos.writeObject(map);
		oos.flush();
		oos.close();
		System.out.println("《》《》《》《》《》cn.com.infostrategy.bs.mdata.WFRiskWordPriviewServlet：发送给客户端word字节数组成功");
	}

	/**
	 * 接受序列化过来的Map对象
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private HashMap getMapFromClient(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("《》《》《》《》《》cn.com.infostrategy.bs.mdata.WFRiskWordPriviewServlet：获取客户端传来的Map····");
		HashMap map = null;
		ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
		Object object = ois.readObject();
		if (object instanceof HashMap) {
			map = (HashMap) object;
		}
		ois.close();
		System.out.println("《》《》《》《》《》cn.com.infostrategy.bs.mdata.WFRiskWordPriviewServlet：获取Map成功");
		return map;
	}

	/**
	 * 在本地创建一个word对象
	 * @param _cmpfileid
	 * @param _wfmap
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> createWord(String _cmpfileid, HashMap _wfmap) throws Exception {
		System.out.println("《》《》《》《》《》cn.com.infostrategy.bs.mdata.WFRiskWordPriviewServlet：创建word····");
		CommDMO dmo = new CommDMO();
		TBUtil tbUtil = new TBUtil();
		WFRiskWordBuilder wordBuilder = new WFRiskWordBuilder(_cmpfileid, _wfmap); //		
		byte[] wfBytes = wordBuilder.getDocContextBytes(true); // 从李春娟提供的生成Word的方法中取得Word格式的二进制流,只有流程说明部分
		String wfpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word";//创建临时目录C:/WebPushTemp/officecompfile/word
		File file = new File(wfpath);

		if (!file.exists()) {//如果服务器端没有该文件夹，则创建之
			file.mkdir();
		}

		wfpath = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// 流程说明word的完整路径，如C:/WebPushTemp/officecompfile/word/258_1.2.doc
		File wffile = new File(wfpath);
		if (!wffile.exists())
			wffile.createNewFile();
		FileOutputStream output = new FileOutputStream(wffile);
		output.write(wfBytes);
		output.close();

		HashVO[] reffileVO = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
		String reffilepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + "/" + reffileVO[0].getStringValue("reffile");//正文的完整路径
		reffilepath = copyFile(reffilepath, reffileVO[0].getStringValue("cmpfilename", "cmpfile") + ".doc"); // 复制正文，返回正文副本的完整路径
		WordTBUtil wordutil = new WordTBUtil();
		HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
		textmap.put("$文件名称$", reffileVO[0].getStringValue("cmpfilename", ""));
		textmap.put("$编码$", reffileVO[0].getStringValue("cmpfilecode", ""));
		textmap.put("$编制单位$", convertStr(reffileVO[0].getStringValue("blcorpname", "")));
		textmap.put("$发布日期$", tbUtil.getCurrDate() + "    ");
		textmap.put("$相关文件$", reffileVO[0].getStringValue("item_addenda", ""));//新增了几个替换【李春娟/2014-09-22】
		textmap.put("$相关表单$", reffileVO[0].getStringValue("item_formids", ""));
		wordutil.mergeOrReplaceFile(wfpath, reffilepath, "$一图两表$", textmap, _cmpfileid);//合并文件和替换文本
		//reffilepath  这个文件是存在的 
		//最后将流程说明word和正文副本word删除
		if (wffile.exists()) {
			wffile.delete();
			System.out.println("删除文件：" + wffile.getAbsolutePath());
		}
		File[] allfile = file.listFiles();
		for (int i = 0; i < allfile.length; i++) {//删除不是今天的冗余文件
			String filename = allfile[i].getName();
			if (filename != null && !filename.startsWith(tbUtil.getCurrDate(false, false)) && !filename.startsWith("wf_")) {
				allfile[i].delete();
			}
		}

		InputStream is = new FileInputStream(reffilepath);
		int size = is.available();
		byte[] bytes = new byte[size];
		is.read(bytes, 0, size);
		is.close();

		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("size", bytes.length);
		returnMap.put("bytes", bytes);
		returnMap.put("name", reffilepath);
		System.out.println("《》《》《》《》《》cn.com.infostrategy.bs.mdata.WFRiskWordPriviewServlet：创建word成功");
		return returnMap;
	}

	/**
	 * 复制文件
	 * @param _oldFilePath
	 * @param _newFilename
	 * @return
	 * @throws Exception
	 */
	private String copyFile(String _oldFilePath, String _newFilename) throws Exception {
		TBUtil tbUtil = new TBUtil();
		File file = new File(_oldFilePath);
		InputStream input = new FileInputStream(file);
		byte[] by = tbUtil.readFromInputStreamToBytes(input);
		if (_newFilename == null || _newFilename.trim().equals("")) {
			_newFilename = _oldFilePath.substring(_oldFilePath.lastIndexOf("/") + 1);
		}
		//需要处理一下，因为文件名中不能包括以下特殊符号：\/:*?"<>|
		_newFilename = tbUtil.replaceAll(_newFilename, "\\", "＼");
		_newFilename = tbUtil.replaceAll(_newFilename, "/", "／");
		_newFilename = tbUtil.replaceAll(_newFilename, ":", "：");
		_newFilename = tbUtil.replaceAll(_newFilename, "*", "×");
		_newFilename = tbUtil.replaceAll(_newFilename, "?", "？");
		_newFilename = tbUtil.replaceAll(_newFilename, "\"", "“");
		_newFilename = tbUtil.replaceAll(_newFilename, "<", "＜");
		_newFilename = tbUtil.replaceAll(_newFilename, ">", "＞");
		_newFilename = tbUtil.replaceAll(_newFilename, "|", "│");//注意这两个竖线是不一样的哦
		_newFilename = tbUtil.replaceAll(_newFilename, " ", "");
		_newFilename = new TBUtil().getCurrTime(false, false) + "_" + _newFilename;
		String newFilePath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word/" + _newFilename;
		FileOutputStream output = new FileOutputStream(newFilePath);
		output.write(by);
		input.close();
		output.close();
		return newFilePath;
	}

	/**
	 * 将短的字符串后面加空格，变成不小于某长度的字符串
	 * @param _oldstr
	 * @return
	 */
	private String convertStr(String _oldstr) {
		if (_oldstr == null || "".equals(_oldstr)) {
			return "              ";
		}
		int i = _oldstr.getBytes().length;
		StringBuffer sb_str = new StringBuffer(_oldstr);
		for (; i < 14; i++) {
			sb_str.append(" ");
		}
		return sb_str.toString();
	}

}
