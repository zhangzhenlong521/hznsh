package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JWindow;

import org.jgraph.JGraph;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

import com.pushworld.ipushgrc.to.WordTBUtil;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 流程与风险模块的客户端工具类
 * @author xch
 *
 */
public class WFRiskUIUtil {
	private boolean process_showSeq = new TBUtil().getSysOptionBooleanValue("流程文件word预览时流程图是否显示排序", false);

	/**
	 * 根据一个流程文件的内容,输出成一个Html格式的临时版本!!!
	 * Html是一个非常有用的输出格式,在Word与Wps都搞不定的情况下,Html将是最终"搞定"客户的终极手段!!!
	 * 所以必须将Html搞得非常强大!!! 包括热点提示,包括超链接跳转!! 包括分组展开与收缩!!!
	 * 另外以后考虑可能将体系文件直接以表单的方式提交!! 所以更需要Html文件样式!!!
	 * @param _parent 父面板
	 * @param _cmpfileID 流程文件id
	 */
	public void openOneFileAsHTML(Container _parent, String _cmpfileID, int _htmlStyle) throws Exception {
		HashMap webCallParMap = new HashMap(); //

		HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileID);//耗时的地方,一般需要1-2秒!!!
		if (wfBase64CodesMap.size() > 0) {//如果有流程图就生成流程图并注册缓存
			HashMap returnMap = UIUtil.commMethod("com.pushworld.ipushgrc.bs.wfrisk.WFRiskHtmlWebCallBean", "registCacheCode", wfBase64CodesMap);
			String str_regid = (String) returnMap.get("regcacheid"); //注册的唯一码!!!
			webCallParMap.put("regcacheid", str_regid); //注册的缓存ID
		}
		webCallParMap.put("cmpfileid", _cmpfileID); //
		webCallParMap.put("htmlStyle", _htmlStyle); //
		//远程调用,使用浏览器打开Html!!!
		UIUtil.openRemoteServerHtml("com.pushworld.ipushgrc.bs.wfrisk.WFRiskHtmlWebCallBean", webCallParMap); //打开!!!!!
	}

	/**
	 * 根据一个流程文件的正文内容或子项内容，输出成一个Word格式的临时版本!!!
	 * @param _parent 父面板
	 * @param _cmpfileID 流程文件id
	 */
	public void openOneFileAsWord(Container _parent, String _cmpfileid) throws Exception {
		TBUtil tbutil = new TBUtil();
		boolean showreffile = tbutil.getSysOptionBooleanValue("流程文件是否由正文生成word", true);//默认有正文，由正文生成word
		if (showreffile) {//如果由正文生成word，就得考虑合并正文和流程图的问题
			//第一次远程调用，查询文件信息
			HashVO[] reffileVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
			if (reffileVO == null || reffileVO.length == 0) {
				MessageBox.show(_parent, "该文件已被删除,不能预览!"); //
				return;
			} else {
				String reffile = reffileVO[0].getStringValue("reffile");
				if (reffile == null || reffile.trim().equals("")) {
					MessageBox.show(_parent, "该文件的正文未编写,不能预览!"); //
					return;
				}
			}
			String jacobtype = tbutil.getSysOptionStringValue("JACOB工作方式", "2");//JACOB工作方式:[查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端 , 默认为2
			if ("0".equals(jacobtype)) {//只存储正文内容,直接打开服务器端正文即可
				String reffile = UIUtil.getStringValueByDS(null, "select reffile from cmp_cmpfile where id=" + _cmpfileid);
				UIUtil.openRemoteServerFile("office", reffile);
			} else if ("1".equals(jacobtype)) {//从服务器端下载正文到客户端，并在客户端生成流程说明部分，然后合并，转换成二进制流传到后台保存

				String str_ClientCodeCache = System.getProperty("ClientCodeCache");
				System.out.println("!!!!!!!!!!!!!!!!!!!"+str_ClientCodeCache);
				if (str_ClientCodeCache.indexOf("\\") >= 0) {// 变换客户端的\\为/
					str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
				}
				if (str_ClientCodeCache.endsWith("/")) {// 如果客户端路径最后一位为/则去掉
					str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
				}
				String wfpath = str_ClientCodeCache + "/word";//创建临时目录 C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\word
				File wffile = new File(wfpath);
				if (!wffile.exists()) {//如果客户端没有该文件夹，则创建之
					wffile.mkdir();
				}

				//第二次远程调用，下载流程文件正文到客户端，进行文档相关内容的替换。
				String reffilepath = null;
				try {
					String cmpfilename = reffileVO[0].getStringValue("cmpfilename");//需要处理一下，因为文件名中不能包括一下特殊符号：\/:*?"<>|
					cmpfilename = tbutil.replaceAll(cmpfilename, "\\", "＼");
					cmpfilename = tbutil.replaceAll(cmpfilename, "/", "／");
					cmpfilename = tbutil.replaceAll(cmpfilename, ":", "：");
					cmpfilename = tbutil.replaceAll(cmpfilename, "*", "×");
					cmpfilename = tbutil.replaceAll(cmpfilename, "?", "？");
					cmpfilename = tbutil.replaceAll(cmpfilename, "\"", "“");
					cmpfilename = tbutil.replaceAll(cmpfilename, "<", "＜");
					cmpfilename = tbutil.replaceAll(cmpfilename, ">", "＞");
					cmpfilename = tbutil.replaceAll(cmpfilename, "|", "│");//注意这两个竖线是不一样的哦
					cmpfilename = tbutil.replaceAll(cmpfilename, " ", "");
					// cmpfilename + "_" + tbutil.getCurrDate(false, true) + ".doc" 这里创建的临时文件名称不要跟发布时创建的临时文件重名，否则预览的同时，进行发布创建文件就会报错！导致发布流程结束了，但文件发布失败了！
					reffilepath = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile", reffileVO[0].getStringValue("reffile"), true, wfpath, cmpfilename + "_" + tbutil.getCurrDate(false, true) + "_" + new Random().nextInt(100) + ".doc", true);//这里增加一个随机数，否则在客户端word预览多次打开同一个流程文件会因进程占用而报错【李春娟/2012-11-07】
					reffilepath = UIUtil.replaceAll(reffilepath, "\\", "/"); //
					if (reffilepath.contains("/")) {
						reffilepath = wfpath + reffilepath.substring(reffilepath.lastIndexOf("/"), reffilepath.length());
						System.out.println(">>>>>>>" + reffilepath);
					}
				} catch (Exception e) {
					MessageBox.show(_parent, "无法找到该文件对应的正文,不能预览!");
					e.printStackTrace();
					return;
				}

				//第三次远程调用，获得流程说明内容的二进制流
				IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				byte[] wfbytes = service.getDocContextBytes(_cmpfileid, getWfBase64CodesMap(_cmpfileid), true);
				String wffilename = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// 流程说明word的完整路径，如C:/WebPushTemp/word/258_1.2.doc
				wffile = new File(wffilename);//在客户端创建流程说明文档
				FileOutputStream output = new FileOutputStream(wffile);
				output.write(wfbytes);
				output.close();

				WordTBUtil wordutil = new WordTBUtil();
				HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
				textmap.put("$文件名称$", reffileVO[0].getStringValue("cmpfilename", ""));
				textmap.put("$编码$", reffileVO[0].getStringValue("cmpfilecode", ""));
				textmap.put("$编制单位$", tbutil.changeStrToLonger(reffileVO[0].getStringValue("blcorpname", ""), 14, true));
				textmap.put("$发布日期$", tbutil.getCurrDate() + "    ");
				textmap.put("$相关文件$", reffileVO[0].getStringValue("item_addenda", ""));//新增了几个替换【李春娟/2014-09-22】
				textmap.put("$相关表单$", reffileVO[0].getStringValue("item_formids", ""));
				wordutil.mergeOrReplaceFile(wffilename, reffilepath, "$一图两表$", textmap, _cmpfileid);//合并文件和替换文本
				String str_url = "file://" + reffilepath;
				Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");//打开文件
				File reffile = new File(reffilepath);
				if (reffile.exists()) {
					reffile.deleteOnExit();//java虚拟机退出时删除
				}
				if (wffile.exists()) {
					wffile.deleteOnExit();//java虚拟机退出时删除
				}
			} else {//在服务器端生成临时合并文件
				//第一次远程调用，查询文件信息
				IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				try {
					String filename = service.getServerCmpfilePath(_cmpfileid, getWfBase64CodesMap(_cmpfileid)); //远程调用,临时合并文件   C:\WebPushTemp\officecompfile文件下的路径 ，如“word/cmpfile.doc”
					UIUtil.openRemoteServerFile("office", filename);
					//加上退出系统自动删除临时生成的WORD[YangQing/2013-09-09]
					String filepath = System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + filename;
					File wordfile = new File(filepath);
					if (wordfile.exists()) {
						wordfile.deleteOnExit();//java虚拟机退出时删除
					}
				} catch (Exception e) {
					MessageBox.show(_parent, "无法找到该文件对应的正文,不能预览!"); //
				}
			}
		} else {//如果不由正文生成word，则直接用iText生成即可
			openOneFileAsWord2(_parent, _cmpfileid);
		}
	}

	/**
	 * 根据一个流程文件的子项内容，如目的、适用范围等输出成一个Word格式的临时版本!!!
	 * @param _parent 父面板
	 * @param _cmpfileID 流程文件id
	 */
	private void openOneFileAsWord2(Container _parent, String _cmpfileID) throws Exception {
		HashMap webCallParMap = new HashMap(); //

		HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileID);//耗时的地方,一般需要1-2秒!!!
		if (wfBase64CodesMap.size() > 0) {
			webCallParMap.put("ImgCode", wfBase64CodesMap); //图片的码!!!
		}
		webCallParMap.put("cmpfileid", _cmpfileID); //体系文件ID
		UIUtil.loadHtml("com.pushworld.ipushgrc.bs.wfrisk.WFRiskDocViewWebDisPatch", webCallParMap, true); ////
	}

	/**
	 * 根据一个流程文件的id,输出最新版本的html浏览,从数据库中取出发布的最新版本的html内容!!!
	 * @param _parent 父面板
	 * @param _cmpfileID 流程文件id
	 */
	public void openOneFileAsHTMLByHist(Container _parent, String _cmpfileID) throws Exception {
		//取得历史版本的主键
		String str_cmpfile_histid = UIUtil.getStringValueByDS(null, "select max(id) from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileID + " group by cmpfile_id");
		if (str_cmpfile_histid == null || "".equals(str_cmpfile_histid)) {
			MessageBox.show(_parent, "该文件未发布过，不能html浏览");
			return;
		}
		String str_webean = "com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistHtmlViewWebCallBean"; //
		HashMap parMap = new HashMap(); //
		parMap.put("cmpfilehistid", str_cmpfile_histid); //
		UIUtil.openRemoteServerHtml(str_webean, parMap); //打开!

	}

	/**
	 * 根据一个流程文件的id,输出最新版本的word浏览,从数据库中取出发布的最新版本的word内容!!!
	 * @param _parent 父面板
	 * @param _cmpfileID 流程文件id
	 */
	public void openOneFileAsWordLByHist(Container _parent, String _cmpfileID, boolean _bigVersion) throws Exception {
		//取得历史版本的主键
		String str_cmpfile_histid = null;
		if (_bigVersion) {//如果需要显示大版本号
			str_cmpfile_histid = UIUtil.getStringValueByDS(null, "select max(id) from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileID + "  and cmpfile_versionno not like '%._1' group by cmpfile_id");
		} else {
			str_cmpfile_histid = UIUtil.getStringValueByDS(null, "select max(id) from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileID + " group by cmpfile_id");
		}

		if (str_cmpfile_histid == null || "".equals(str_cmpfile_histid)) {
			MessageBox.show(_parent, "该文件未发布过，不能Word浏览");
			return;
		}
		String str_webean = "com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistDocViewWebCallBean"; //
		HashMap parMap = new HashMap(); //
		parMap.put("cmpfilehistid", str_cmpfile_histid); //
		UIUtil.openRemoteServerHtml(str_webean, parMap, true); //打开!!
	}

	public void ExportHandBook(Container _parent, String _cmpfileID) throws Exception {
		HashMap webCallParMap = new HashMap(); //

		HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileID);//耗时的地方,一般需要1-2秒!!!
		if (wfBase64CodesMap.size() > 0) {
			webCallParMap.put("ImgCode", wfBase64CodesMap); //图片的码!!!
		} else {
			MessageBox.show(_parent, "该文件没有流程,不能查看!");
			return;
		}
		webCallParMap.put("cmpfileid", _cmpfileID); //体系文件ID
		webCallParMap.put("ishandbook", "Y");
		UIUtil.loadHtml("com.pushworld.ipushgrc.bs.wfrisk.WFRiskDocViewWebDisPatch", webCallParMap, true); ////
	}

	/**
	 * 发布所有状态为【有效】的流程文件为1.0版本，并且清空历史记录。以后会优化！！！
	 * @param _wfmap
	 * @throws Exception
	 */
	public HashMap publishAllCmpFile() throws Exception {
		HashMap hashmap = new HashMap();
		String[] cmpfileids = UIUtil.getStringArrayFirstColByDS(null, "select id from cmp_cmpfile where filestate='3'");//将【有效】状态的流程文件全部查出来
		for (int i = 0; i < cmpfileids.length; i++) {
			hashmap.put(cmpfileids[i], getWfBase64CodesMap(cmpfileids[i]));
		}
		IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		return service.publishAllCmpFile(hashmap); //远程调用,发布新版本，有正文
	}

	/**
	 * 实际发布的逻辑，保存word和html格式的历史记录
	 * @param _parent     父亲面板
	 * @param _cmpfileid     文件id
	 * @param _cmpfilename   文件名称
	 * @param _showreffile   是否使用正文
	 * @param _newversionno  新版本号
	 */
	public void dealpublish(Container _parent, String _cmpfileid, String _cmpfilename, boolean _showreffile, String _newversionno) {
		this.dealpublish(_parent, _cmpfileid, _cmpfilename, _showreffile, _newversionno, false);
	}

	/**
	 * 实际发布的逻辑，保存word和html格式的历史记录
	 * @param _parent     父亲面板
	 * @param _cmpfileid     文件id
	 * @param _cmpfilename   文件名称
	 * @param _showreffile   是否使用正文
	 * @param _newversionno  新版本号
	 * @param _overwrite     如果数据库中已有该新版本号，是否要覆盖。覆盖则删除同版本号的所有历史记录
	 */
	public void dealpublish(Container _parent, String _cmpfileid, String _cmpfilename, boolean _showreffile, String _newversionno, boolean _overwrite) {
		String str_cmpfile_histid = null;
		try {
			IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			if (_showreffile) {//如果由正文生成word，就得考虑合并正文和流程图的问题
				//第一次远程调用，查询文件信息
				HashVO[] reffileVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
				if (reffileVO == null || reffileVO.length == 0) {
					MessageBox.show(_parent, "该文件已被删除,不能发布!"); //
					return;
				} else {
					String reffile = reffileVO[0].getStringValue("reffile");
					if (reffile == null || reffile.trim().equals("")) {
						MessageBox.show(_parent, "该文件的正文未编写,不能发布!"); //
						return;
					}
				}
				TBUtil tbutil = new TBUtil();
				String jacobtype = tbutil.getSysOptionStringValue("JACOB工作方式", "2");//JACOB工作方式:[查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端 , 默认为2
				if ("0".equals(jacobtype)) {//只存储正文内容
					service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, getWfBase64CodesMap(_cmpfileid), _overwrite);//只保存正文
				} else if ("1".equals(jacobtype)) {//从服务器端下载正文到客户端，并在客户端生成流程说明部分，然后合并，转换成二进制流传到后台保存
					String str_ClientCodeCache = System.getProperty("ClientCodeCache");
					if (str_ClientCodeCache.indexOf("\\") >= 0) {// 变换客户端的\\为/
						str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
					}
					if (str_ClientCodeCache.endsWith("/")) {// 如果客户端路径最后一位为/则去掉
						str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
					}
					String wfpath = str_ClientCodeCache + "/word";//C:/WebPushTemp/word
					File wffile = new File(wfpath);
					if (!wffile.exists()) {//创建临时目录 C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\word
						wffile.mkdir();
					}

					//第二次远程调用，下载流程文件正文到客户端，进行文档相关内容的替换。
					String reffilepath = null;
					try {
						String cmpfilename = reffileVO[0].getStringValue("cmpfilename");//需要处理一下，因为文件名中不能包括一下特殊符号：\/:*?"<>|
						cmpfilename = tbutil.replaceAll(cmpfilename, "\\", "＼");
						cmpfilename = tbutil.replaceAll(cmpfilename, "/", "／");
						cmpfilename = tbutil.replaceAll(cmpfilename, ":", "：");
						cmpfilename = tbutil.replaceAll(cmpfilename, "*", "×");
						cmpfilename = tbutil.replaceAll(cmpfilename, "?", "？");
						cmpfilename = tbutil.replaceAll(cmpfilename, "\"", "“");
						cmpfilename = tbutil.replaceAll(cmpfilename, "<", "＜");
						cmpfilename = tbutil.replaceAll(cmpfilename, ">", "＞");
						cmpfilename = tbutil.replaceAll(cmpfilename, "|", "│");//注意这两个竖线是不一样的哦
						cmpfilename = tbutil.replaceAll(cmpfilename, " ", "");
						// tbutil.getCurrTime(false, false) + "_" + cmpfilename + ".doc" 发布时创建的临时文件名称不要跟预览时创建的临时文件重名，否则预览的同时，发布文件就会报错！导致发布流程结束了，但文件发布失败了！
						reffilepath = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile", reffileVO[0].getStringValue("reffile"), true, wfpath, tbutil.getCurrTime(false, false) + "_" + cmpfilename + "_" + new Random().nextInt(100) + ".doc", true);//这里增加一个随机数，否则在客户端word预览多次打开同一个流程文件会因进程占用而报错【李春娟/2012-11-07】
					} catch (Exception e) {
						MessageBox.show(_parent, "无法找到该文件对应的正文,不能发布!");
						e.printStackTrace();
						return;
					}

					//第三次远程调用，获得流程说明内容的二进制流
					HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileid);//耗时的地方,一般需要1-2秒!!!
					byte[] wfbytes = service.getDocContextBytes(_cmpfileid, wfBase64CodesMap, true);
					String wffilename = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// 流程说明word的完整路径，如C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\word\258_1.2.doc
					wffile = new File(wffilename);//在客户端创建流程说明文档
					FileOutputStream output = new FileOutputStream(wffile);
					output.write(wfbytes);
					output.close();

					//第四次远程调用，先在数据库中增加一个历史版本记录，为了在客户端生成文档时修改记录里有当前版本！这里已经设置了_overwrite，在后面的发布就不要判断了，否则将自己也删掉了
					str_cmpfile_histid = service.addCmpfileHist(_cmpfileid, _cmpfilename, _newversionno, _overwrite);

					WordTBUtil wordutil = new WordTBUtil();
					HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
					textmap.put("$文件名称$", reffileVO[0].getStringValue("cmpfilename", ""));
					textmap.put("$编码$", reffileVO[0].getStringValue("cmpfilecode", ""));
					textmap.put("$编制单位$", tbutil.changeStrToLonger(reffileVO[0].getStringValue("blcorpname", ""), 14, true));
					textmap.put("$发布日期$", tbutil.getCurrDate() + "    ");
					textmap.put("$相关文件$", reffileVO[0].getStringValue("item_addenda", ""));//新增了几个替换【李春娟/2014-09-22】
					textmap.put("$相关表单$", reffileVO[0].getStringValue("item_formids", ""));
					wordutil.mergeOrReplaceFile(wffilename, reffilepath, "$一图两表$", textmap, _cmpfileid);//合并文件和替换文本
					InputStream input = new FileInputStream(reffilepath);
					byte readDocType[] = tbutil.readFromInputStreamToBytes(input);
					byte[] zipDocBytes = tbutil.compressBytes(readDocType);
					String str_doc64code = tbutil.convertBytesTo64Code(zipDocBytes); // 转成64位编码!!!

					//第五次远程调用，传正文word合并后的二进制流到后台，直接保存到数据库，然后调用生成html的逻辑，再历史记录表中新增一条数据，
					//最后将流程说明word和正文副本word删除
					//远程调用,发布新版本，因为前面在增加新版本的时候已经判断了是否要覆盖相同版本的历史记录，所以这里就不用判断了。这是唯一一个不用判断是否覆盖的发布方法publishCmpFile
					service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, str_cmpfile_histid, wfBase64CodesMap, str_doc64code);

					File reffile = new File(reffilepath);
					if (wffile.exists()) {
						wffile.delete();
					}
					if (reffile.exists()) {
						reffile.delete();
					}
				} else {//由服务器端合并word
					try {
						service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, getWfBase64CodesMap(_cmpfileid), true, _overwrite); //远程调用,发布新版本，有正文
					} catch (Exception e) {
						e.printStackTrace();
						MessageBox.show(_parent, "无法找到该文件对应的正文,不能发布!");
						return;
					}
				}
			} else {//如果不由正文生成word，则直接用iText生成即可
				service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, getWfBase64CodesMap(_cmpfileid), false, _overwrite); //远程调用,发布新版本，无正文
			}
			UIUtil.executeUpdateByDS(null, "update cmp_risk_editlog set filestate='3' where cmpfile_id =" + _cmpfileid);//将风险点日志表更新一下状态，以便统计
			MessageBox.show(_parent, "发布新版本成功,新版本是[" + _newversionno + "]!"); //
		} catch (Throwable e) {
			if (str_cmpfile_histid != null) {//如果历史记录表中增加了记录，但后面执行报错了，要删除新增的记录
				try {
					UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_hist where id=" + str_cmpfile_histid);
				} catch (Exception e1) {
					MessageBox.showException(_parent, e1);
				}
			}
			MessageBox.showException(_parent, e);
		}
	}

	public HashMap getWfBase64CodesMap(String _cmpfileID) throws Exception {
		HashMap wfBase64CodesMap = new LinkedHashMap();
		//第一步，远程访问查出流程文件所有的流程的id
		String[] str_wfs = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_wf_process where cmpfileid=" + _cmpfileID + " order by userdef04,id");
		if (str_wfs == null || str_wfs.length < 1) {//如果该流程文件没有流程则直接返回
			return wfBase64CodesMap;
		}
		//第二步，远程访问一下子查出所有风险点
		HashVO[] hvs_risk = UIUtil.getHashVoArrayByDS(null, "select wfprocess_id,wfactivity_id,rank from cmp_risk where cmpfile_id='" + _cmpfileID + "'"); //

		//第三步，创建流程图,然后生成64位码!!!
		//支持排序,即每次输出的顺序都一样!!!
		for (int i = 0; i < str_wfs.length; i++) { //遍历各个流程
			WorkFlowDesignWPanel wfPanel = new WorkFlowDesignWPanel(false); //创建一个工作流!不显示工具箱【李春娟/2012-03-08】
			wfPanel.loadGraphByID(str_wfs[i]);//一定要用id加载，因为流程编码有重复的
			wfPanel.setGridVisible(false);//设置不显示背景点点【李春娟/2012-11-16】
			wfPanel.showStaff(false);//设置不显示标尺
			boolean isExportTitle = TBUtil.getTBUtil().getSysOptionBooleanValue("工作流导出是否有标题", true); //内控系统遇到一些客户要求导出时不要标题!
			if (!isExportTitle) {
				wfPanel.setTitleCellForeground(Color.WHITE);//设置标题颜色为白色，即不显示
			}
			wfPanel.reSetAllLayer(false);//必须设置一下,否则阶段左边向下的箭头有可能不显示[李春娟/2012-11-19]
			//加风险点
			HashMap riskMap = new HashMap(); //
			for (int j = 0; j < hvs_risk.length; j++) {
				if (hvs_risk[j].getStringValue("wfprocess_id") != null && hvs_risk[j].getStringValue("wfprocess_id").equals(str_wfs[i])) { //属于本流程的
					String str_activity_id = hvs_risk[j].getStringValue("wfactivity_id"); //
					if (str_activity_id != null) {
						int li_1 = 0, li_2 = 0, li_3 = 0;
						if (riskMap.containsKey(str_activity_id)) { //如果已经有了风险点
							RiskVO rvo = (RiskVO) riskMap.get(str_activity_id); ////
							li_1 = rvo.getLevel1RiskCount();
							li_2 = rvo.getLevel2RiskCount();
							li_3 = rvo.getLevel3RiskCount();
						}
						String str_rank = hvs_risk[j].getStringValue("rank"); //风险等级
						if (str_rank != null) {
							if (str_rank.equals("高风险") || str_rank.equals("极大风险")) {
								li_1++;
							} else if (str_rank.equals("低风险") || str_rank.equals("极小风险")) {
								li_3++;
							} else {
								li_2++; //中等风险
							}
						} else {
							li_2++; //中等风险
						}
						RiskVO rsvo = new RiskVO(li_1, li_2, li_3); //
						riskMap.put(str_activity_id, rsvo); //重新置入!!!
					}
				}
			}
			String[] str_keys = (String[]) riskMap.keySet().toArray(new String[0]); //
			for (int k = 0; k < str_keys.length; k++) {
				RiskVO rvo = (RiskVO) riskMap.get(str_keys[k]); ////
				if (rvo != null) {
					wfPanel.setCellAddRisk(str_keys[k], rvo); ////
				}
			}
			if (process_showSeq) {//导出word里，流程图显示排序
				wfPanel.doShowOrder(true);
			}
			JGraph graph = wfPanel.getGraph(); ////
			int li_width = (int) graph.getPreferredSize().getWidth(); //
			int li_height = (int) graph.getPreferredSize().getHeight(); //

			JWindow win = new JWindow(); //创建一个窗口,不知道为什么一定要弄一个窗口显示出来,才能把图画上去!!!
			win.setSize(0, 0); //
			win.getContentPane().add(wfPanel); // 
			win.toBack(); //
			win.setVisible(true); //
			if (li_width == 0 || li_height == 0) {//如果流程没有环节
				li_width = 1;
				li_height = 1;
			}
			BufferedImage image = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_RGB); //创建一个空白的图片!!
			Graphics g = image.createGraphics(); //为图片创建一个新的画笔!!
			graph.paint(g); //将控件的图画写入到这个新的画板中
			g.dispose();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "JPEG", out);
			byte[] imgBytes = out.toByteArray(); //生成二进制内容!!!
			byte[] zipedImgBytes = new TBUtil().compressBytes(imgBytes); //压缩一下!!
			wfBase64CodesMap.put(str_wfs[i], zipedImgBytes); //送入哈希表!!注册!!
			win.dispose();
			win = null; //内存释放
		}
		return wfBase64CodesMap; //返回!!
	}

	public void insertAddRiskLog(BillVO _billvo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("cmp_risk_editlog"); //
		String str_newId = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISK_EDITLOG"); //主键
		isql.putFieldValue("id", str_newId); //
		isql.putFieldValue("edittype", "新增风险点");//编辑类型
		isql.putFieldValue("edittime", new TBUtil().getCurrTime());//编辑的时间
		isql.putFieldValue("cmpfile_id", _billvo.getStringValue("cmpfile_id"));//文件id
		isql.putFieldValue("cmpfile_name", _billvo.getStringValue("cmpfile_name"));//文件名称
		isql.putFieldValue("filestate", "1"); //文件状态,默认为编辑中，如果文件发布或废止了，应该改变该状态
		isql.putFieldValue("blcorpid", _billvo.getStringValue("blcorpid"));//所属部门id
		isql.putFieldValue("blcorpname", _billvo.getStringValue("blcorpname"));//所属部门名称
		isql.putFieldValue("bsactid", _billvo.getStringValue("bsactid"));//业务活动id
		isql.putFieldValue("bsactname", _billvo.getStringValue("bsactname"));//业务活动名称
		isql.putFieldValue("possible", _billvo.getStringValue("possible"));//可能性
		isql.putFieldValue("serious", _billvo.getStringValue("serious"));//严重程度
		isql.putFieldValue("rank", _billvo.getStringValue("rank"));//风险等级 
		isql.putFieldValue("ctrlfneffect", _billvo.getStringValue("ctrlfneffect"));//控制措施有效性
		isql.putFieldValue("resdpossible", _billvo.getStringValue("resdpossible"));//剩余风险的可能性
		isql.putFieldValue("resdserious", _billvo.getStringValue("resdserious"));//剩余风险的严重程度 
		isql.putFieldValue("resdrank", _billvo.getStringValue("resdrank"));//剩余风险的风险等级
		isql.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getName());//新增人姓名
		UIUtil.executeUpdateByDS(null, isql);
	}

	public void insertEditRiskLog(BillVO _oldbillvo, BillVO _newbillvo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("cmp_risk_editlog"); //
		String str_newId = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISK_EDITLOG"); //主键
		isql.putFieldValue("id", str_newId); //
		isql.putFieldValue("edittype", "修改风险点");//编辑类型
		isql.putFieldValue("edittime", new TBUtil().getCurrTime());//编辑的时间
		isql.putFieldValue("cmpfile_id", _newbillvo.getStringValue("cmpfile_id"));//文件id
		isql.putFieldValue("cmpfile_name", _newbillvo.getStringValue("cmpfile_name"));//文件名称
		isql.putFieldValue("filestate", "1"); //文件状态,默认为编辑中，如果文件发布或废止了，应该改变该状态
		isql.putFieldValue("blcorpid", _newbillvo.getStringValue("blcorpid"));//所属部门id
		isql.putFieldValue("blcorpname", _newbillvo.getStringValue("blcorpname"));//所属部门名称
		isql.putFieldValue("bsactid", _newbillvo.getStringValue("bsactid"));//业务活动id
		isql.putFieldValue("bsactname", _newbillvo.getStringValue("bsactname"));//业务活动名称
		isql.putFieldValue("possible", _newbillvo.getStringValue("possible"));//可能性
		isql.putFieldValue("serious", _newbillvo.getStringValue("serious"));//严重程度
		isql.putFieldValue("rank", _newbillvo.getStringValue("rank"));//风险等级 
		isql.putFieldValue("ctrlfneffect", _newbillvo.getStringValue("ctrlfneffect"));//控制措施有效性
		isql.putFieldValue("resdpossible", _newbillvo.getStringValue("resdpossible"));//剩余风险的可能性
		isql.putFieldValue("resdserious", _newbillvo.getStringValue("resdserious"));//剩余风险的严重程度 
		isql.putFieldValue("resdrank", _newbillvo.getStringValue("resdrank"));//剩余风险的风险等级
		isql.putFieldValue("possible2", _oldbillvo.getStringValue("possible"));//可能性
		isql.putFieldValue("serious2", _oldbillvo.getStringValue("serious"));//严重程度
		isql.putFieldValue("rank2", _oldbillvo.getStringValue("rank"));//风险等级 
		isql.putFieldValue("ctrlfneffect2", _oldbillvo.getStringValue("ctrlfneffect"));//控制措施有效性
		isql.putFieldValue("resdpossible2", _oldbillvo.getStringValue("resdpossible"));//剩余风险的可能性
		isql.putFieldValue("resdserious2", _oldbillvo.getStringValue("resdserious"));//剩余风险的严重程度 
		isql.putFieldValue("resdrank2", _oldbillvo.getStringValue("resdrank"));//剩余风险的风险等级
		isql.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getName());//修改人姓名
		UIUtil.executeUpdateByDS(null, isql);
	}

	public void insertDeleteRiskLog(BillVO _billvo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("cmp_risk_editlog"); //
		String str_newId = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISK_EDITLOG"); //主键
		isql.putFieldValue("id", str_newId); //
		isql.putFieldValue("edittype", "删除风险点");//编辑类型
		isql.putFieldValue("edittime", new TBUtil().getCurrTime());//编辑的时间
		isql.putFieldValue("cmpfile_id", _billvo.getStringValue("cmpfile_id"));//文件id
		isql.putFieldValue("cmpfile_name", _billvo.getStringValue("cmpfile_name"));//文件名称
		isql.putFieldValue("filestate", "1"); //文件状态,默认为编辑中，如果文件发布或废止了，应该改变该状态
		isql.putFieldValue("blcorpid", _billvo.getStringValue("blcorpid"));//所属部门id
		isql.putFieldValue("blcorpname", _billvo.getStringValue("blcorpname"));//所属部门名称
		isql.putFieldValue("bsactid", _billvo.getStringValue("bsactid"));//业务活动id
		isql.putFieldValue("bsactname", _billvo.getStringValue("bsactname"));//业务活动名称
		isql.putFieldValue("possible2", _billvo.getStringValue("possible"));//可能性
		isql.putFieldValue("serious2", _billvo.getStringValue("serious"));//严重程度
		isql.putFieldValue("rank2", _billvo.getStringValue("rank"));//风险等级 
		isql.putFieldValue("ctrlfneffect2", _billvo.getStringValue("ctrlfneffect"));//控制措施有效性
		isql.putFieldValue("resdpossible2", _billvo.getStringValue("resdpossible"));//剩余风险的可能性
		isql.putFieldValue("resdserious2", _billvo.getStringValue("resdserious"));//剩余风险的严重程度 
		isql.putFieldValue("resdrank2", _billvo.getStringValue("resdrank"));//剩余风险的风险等级
		isql.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getName());//删除人姓名
		UIUtil.executeUpdateByDS(null, isql);
	}

	/**
	 * 一个BOM图中的所有热点的RiskVO
	 * @param _bomtype  "RISK"、"PROCESS"、"CMPFILE"
	 * @param _datatype  "BLCORPNAME"、"ICTYPENAME"
	 * @param _alldatas  BOM图所有热点值，只有机构才需要设置
	 * @param _isSelfCorp  是否查询本机构
	 * @return
	 * @throws Exception
	 */
	public Hashtable getHashtableRiskVO(String _bomtype, String _datatype, ArrayList _alldatas, boolean _isSelfCorp) throws Exception {
		IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		return service.getHashtableRiskVO(_bomtype, _datatype, _alldatas, _isSelfCorp);
	}

	/**
	 * 批量导出流程文件正文及流程图【李春娟/2015-07-13】
	 * 只适用于在客户端合并，并且由正文生成word的情况
	 * @param _parent 父面板
	 * @param _billVOs 流程文件
	 */
	public void exportFilesAsWord(Container _parent, BillVO[] _billVOs) throws Exception {
		//先选择目录
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("选择一个目录!"); //
		int flag = chooser.showOpenDialog(_parent);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		String filedir = file.getPath();
		TBUtil tbutil = new TBUtil();
		StringBuffer sb_msg = new StringBuffer();
		int count = 0;
		for (int i = 0; i < _billVOs.length; i++) {
			String cmpfileid = _billVOs[i].getStringValue("id");
			//第一次远程调用，查询文件信息
			HashVO[] reffileVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + cmpfileid + "'");
			if (reffileVO == null || reffileVO.length == 0) {
				continue;
			} else {
				String reffile = reffileVO[0].getStringValue("reffile");
				if (reffile == null || reffile.trim().equals("")) {
					sb_msg.append("《" + reffileVO[0].getStringValue("cmpfilename") + "》的正文不存在!\r\n");
					continue;
				}
			}
			if (filedir.indexOf("\\") >= 0) {// 变换客户端的\\为/
				filedir = UIUtil.replaceAll(filedir, "\\", "/"); //
			}
			if (filedir.endsWith("/")) {// 如果客户端路径最后一位为/则去掉
				filedir = filedir.substring(0, filedir.length() - 1);
			}
			File wffile = new File(filedir);

			//第二次远程调用，下载流程文件正文到客户端，进行文档相关内容的替换。
			String reffilepath = null;
			try {
				String cmpfilename = reffileVO[0].getStringValue("cmpfilename");//需要处理一下，因为文件名中不能包括一下特殊符号：\/:*?"<>|
				cmpfilename = tbutil.replaceAll(cmpfilename, "\\", "＼");
				cmpfilename = tbutil.replaceAll(cmpfilename, "/", "／");
				cmpfilename = tbutil.replaceAll(cmpfilename, ":", "：");
				cmpfilename = tbutil.replaceAll(cmpfilename, "*", "×");
				cmpfilename = tbutil.replaceAll(cmpfilename, "?", "？");
				cmpfilename = tbutil.replaceAll(cmpfilename, "\"", "“");
				cmpfilename = tbutil.replaceAll(cmpfilename, "<", "＜");
				cmpfilename = tbutil.replaceAll(cmpfilename, ">", "＞");
				cmpfilename = tbutil.replaceAll(cmpfilename, "|", "│");//注意这两个竖线是不一样的哦
				cmpfilename = tbutil.replaceAll(cmpfilename, " ", "");
				reffilepath = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile", reffileVO[0].getStringValue("reffile"), true, filedir, cmpfilename + "_" + tbutil.getCurrDate(true, true) + ".doc", true);//这里增加一个随机数，否则在客户端word预览多次打开同一个流程文件会因进程占用而报错【李春娟/2012-11-07】
				reffilepath = UIUtil.replaceAll(reffilepath, "\\", "/"); //
				if (reffilepath.contains("/")) {
					reffilepath = filedir + reffilepath.substring(reffilepath.lastIndexOf("/"), reffilepath.length());
					System.out.println(">>>>>>>" + reffilepath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			//第三次远程调用，获得流程说明内容的二进制流
			IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			byte[] wfbytes = service.getDocContextBytes(cmpfileid, getWfBase64CodesMap(cmpfileid), true);
			String wffilename = filedir + "/wf_" + System.currentTimeMillis() + ".doc";// 流程说明word的完整路径，如C:/WebPushTemp/word/258_1.2.doc
			wffile = new File(wffilename);//在客户端创建流程说明文档
			FileOutputStream output = new FileOutputStream(wffile);
			output.write(wfbytes);
			output.close();

			WordTBUtil wordutil = new WordTBUtil();
			HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
			textmap.put("$文件名称$", reffileVO[0].getStringValue("cmpfilename", ""));
			textmap.put("$编码$", reffileVO[0].getStringValue("cmpfilecode", ""));
			textmap.put("$编制单位$", tbutil.changeStrToLonger(reffileVO[0].getStringValue("blcorpname", ""), 14, true));
			textmap.put("$发布日期$", tbutil.getCurrDate() + "    ");
			textmap.put("$相关文件$", reffileVO[0].getStringValue("item_addenda", ""));//新增了几个替换【李春娟/2014-09-22】
			textmap.put("$相关表单$", reffileVO[0].getStringValue("item_formids", ""));
			wordutil.mergeOrReplaceFile(wffilename, reffilepath, "$一图两表$", textmap, cmpfileid);//合并文件和替换文本
			if (wffile.exists()) {
				wffile.delete();//java虚拟机退出时删除
			}
			count++;
		}
		sb_msg.append("\r\n共导出" + count + "个文件。");
		MessageBox.show(_parent, sb_msg.toString());
	}

}
