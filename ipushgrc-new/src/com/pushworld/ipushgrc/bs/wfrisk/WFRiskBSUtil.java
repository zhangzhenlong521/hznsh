package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.DefaultStyledDocument;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 流程与风险模块的工具类!!
 * @author xch
 *
 */
public class WFRiskBSUtil {

	/**
	 * 无参构造方法
	 */
	public WFRiskBSUtil() {
	}

	/**
	 * 判断流程文件是否已编辑锁定，如果没有就锁定，返回true（表示锁定成功，锁定人可以编辑了），否则返回false（表示锁定失败，原状态就是锁定状态）
	 * @param _cmpfileid 流程文件id
	 * @param _cmpfilename 流程文件名称
	 * @param _username  操作用户名
	 * @return
	 * @throws Exception
	 */
	public boolean lockCmpFileById(String _cmpfileid, String _cmpfilename, String _username) throws Exception {
		String lockedcmpfileids = ServerEnvironment.getProperty("LOCKEDCMPFILEIDS");//格式为：";流程文件id&流程文件名称&用户名&当前时间;"，例如：";1504&zhangsan&转授责管理&2011-05-04 11:04:28;264&代收代付业务管理&lisi&2011-05-05 09:14:36;"
		if (lockedcmpfileids == null || "".equals(lockedcmpfileids)) {//
			StringBuffer sb_cmpfile = new StringBuffer(";");
			sb_cmpfile.append(_cmpfileid);
			sb_cmpfile.append("&");
			sb_cmpfile.append(_cmpfilename);
			sb_cmpfile.append("&");
			sb_cmpfile.append(_username);
			sb_cmpfile.append("&");
			sb_cmpfile.append(new TBUtil().getCurrTime());
			sb_cmpfile.append(";");
			lockedcmpfileids = sb_cmpfile.toString();
			ServerEnvironment.setProperty("LOCKEDCMPFILEIDS", lockedcmpfileids);
			return true;
		}
		if (lockedcmpfileids.contains(";" + _cmpfileid + "&")) {
			return false;
		}
		StringBuffer sb_cmpfile = new StringBuffer();
		sb_cmpfile.append(_cmpfileid);
		sb_cmpfile.append("&");
		sb_cmpfile.append(_cmpfilename);
		sb_cmpfile.append("&");
		sb_cmpfile.append(_username);
		sb_cmpfile.append("&");
		sb_cmpfile.append(new TBUtil().getCurrTime());
		sb_cmpfile.append(";");
		lockedcmpfileids += sb_cmpfile.toString();
		ServerEnvironment.setProperty("LOCKEDCMPFILEIDS", lockedcmpfileids);
		return true;
	}

	/**
	 * 某个流程文件编辑完后要解除流程文件的编辑锁，即其他人可以编辑了
	 * @param _cmpfileid 流程文件id
	 * @return
	 * @throws Exception
	 */
	public void unlockCmpFileById(String _cmpfileid) throws Exception {
		String lockedcmpfileids = ServerEnvironment.getProperty("LOCKEDCMPFILEIDS");
		String str_cmpfileid = ";" + _cmpfileid + "&";
		if (lockedcmpfileids == null || "".equals(lockedcmpfileids) || !lockedcmpfileids.contains(str_cmpfileid)) {//如果该文件没有被锁定，就直接返回
			return;
		}
		int li_index = lockedcmpfileids.indexOf(str_cmpfileid);
		String cmpfiletemp = lockedcmpfileids.substring(0, li_index);
		cmpfiletemp += lockedcmpfileids.substring(lockedcmpfileids.indexOf(";", li_index + 1));
		ServerEnvironment.setProperty("LOCKEDCMPFILEIDS", cmpfiletemp);
	}

	/**
	 * 判断某个流程文件是否处于锁定状态
	 * @param _cmpfileid 流程文件id
	 * @return
	 * @throws Exception
	 */
	public boolean isCmpFileLocked(String _cmpfileid) throws Exception {
		String lockedcmpfileids = ServerEnvironment.getProperty("LOCKEDCMPFILEIDS");
		if (lockedcmpfileids == null || "".equals(lockedcmpfileids)) {//如果该文件没有被锁定
			return false;
		}
		return lockedcmpfileids.contains(";" + _cmpfileid + "&");
	}

	/**
	 * 得到所有处于锁定状态的流程文件信息
	 * @return
	 * @throws Exception
	 */
	public String getAllLockedCmpFiles() throws Exception {
		return ServerEnvironment.getProperty("LOCKEDCMPFILEIDS");
	}

	/**
	 * 取得一个体系文件中的富文本框中实际对应的内容! 即需要从pub_stylepaddoc中寻找!!!
	 * 返回的哈希表中的key是【"item_target", "item_userarea", "item_keywords"】,value就是实际值!!
	 * @param _cmpfileId
	 * @param _returnStr 是否只返回文本内容，true：即使有格式也只返回Document的内容；false：如果有格式则返回Document对象，如果没有就返回字符串
	 * @return
	 * @throws Exception
	 */
	public HashMap getCmpFileItemDocument(String _cmpfileId, boolean _returnStr) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id='" + _cmpfileId + "'"); //
		String[] str_fields = new String[] { "item_target", "item_userarea", "item_keywords", "item_duty", "item_bsprcp" }; //
		HashMap returnMap = new HashMap(); //
		HashMap batchIdMap = new HashMap(); //
		for (int i = 0; i < str_fields.length; i++) {
			String str_item_value = hvs[0].getStringValue(str_fields[i]); //取得值
			if (str_item_value == null || str_item_value.trim().equals("")) {
				returnMap.put(str_fields[i], "无"); //
			} else {
				int li_pos_1 = str_item_value.lastIndexOf("#@$"); //
				int li_pos_2 = str_item_value.lastIndexOf("$@#"); //
				if (li_pos_1 > 0 && li_pos_2 > 0 && (li_pos_2 - li_pos_1) < 30) {
					String str_batchid = str_item_value.substring(li_pos_1 + 3, li_pos_2); //
					batchIdMap.put(str_fields[i], str_batchid); //
					returnMap.put(str_fields[i], str_item_value.subSequence(0, li_pos_1)); //
				} else {
					returnMap.put(str_fields[i], str_item_value); //
				}
			}
		}
		//相关文件及附录特殊处理，不是富文本框，只存了文件的相对路径
		String str_item_addenda = hvs[0].getStringValue("item_addenda"); //取得值
		if (str_item_addenda == null || str_item_addenda.trim().equals("")) {
			returnMap.put("item_addenda", "无"); //
		} else {
			String[] item_values = tbUtil.split(str_item_addenda, ";");
			StringBuffer sb_addenda = new StringBuffer();
			for (int i = 0; i < item_values.length; i++) {
				sb_addenda.append("（");
				sb_addenda.append(i + 1);
				sb_addenda.append("）");
				if (item_values[i].contains("_")) {//判断是否含有文件前缀，文件前缀是系统加的，最后显示文件名要去掉
					String str_filename = tbUtil.convertHexStringToStr(item_values[i].substring(item_values[i].indexOf("_") + 1, item_values[i].lastIndexOf(".")));//将16进制的文件名转成原文件名
					sb_addenda.append(str_filename);
					sb_addenda.append(item_values[i].substring(item_values[i].lastIndexOf(".")));//加上文件后缀
				} else {
					sb_addenda.append(tbUtil.convertHexStringToStr(item_values[i].substring(0, item_values[i].lastIndexOf("."))));//将16进制的文件名转成原文件名
					sb_addenda.append(item_values[i].substring(item_values[i].lastIndexOf(".")));//加上文件后缀
				}
				sb_addenda.append("\n");
			}
			returnMap.put("item_addenda", sb_addenda.toString()); //
		}

		if (batchIdMap.size() > 0) { //如果存在有宏代码的!!!
			String[] str_batchids = (String[]) batchIdMap.values().toArray(new String[0]);
			String str_inCondition = tbUtil.getInCondition(str_batchids); //
			HashVO[] hvs_stylepad = commDMO.getHashVoArrayByDS(null, "select * from pub_stylepaddoc where batchid in (" + str_inCondition + ") order by batchid,seq"); //
			String[] str_keys = (String[]) batchIdMap.keySet().toArray(new String[0]);
			for (int i = 0; i < str_keys.length; i++) { //遍历各个列!
				String str_batchid = (String) batchIdMap.get(str_keys[i]); //
				HashVO[] thisBatchVOs = getOneBatchHashVOs(hvs_stylepad, str_batchid); //
				if (thisBatchVOs.length > 0) { //如果找到
					String str_docitemValue = null; //
					StringBuilder sb_doc = new StringBuilder(); //
					for (int j = 0; j < thisBatchVOs.length; j++) { //遍历各行!!!
						for (int k = 0; k < 10; k++) {
							str_docitemValue = thisBatchVOs[j].getStringValue("doc" + k); //
							if (str_docitemValue == null) {
								break; //
							} else {
								sb_doc.append(str_docitemValue); //
							}
						}
					}
					String str_64code = sb_doc.toString(); //64位码!!!
					byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //转成二进制码
					byte[] unzipedBytes = tbUtil.decompressBytes(bytes); //解压后的二进制!!!
					DefaultStyledDocument document = (DefaultStyledDocument) tbUtil.deserialize(unzipedBytes); //反序列化!!!
					if (_returnStr) {
						String str_realText = document.getText(0, document.getLength()); //实际的内容!!!
						returnMap.put(str_keys[i], str_realText); //重新置入
					} else {
						returnMap.put(str_keys[i], document); //重新置入
					}
				}
			}
		}
		return returnMap; //
	}

	private HashVO[] getOneBatchHashVOs(HashVO[] _allHVS, String _batchid) {
		ArrayList al_tmp = new ArrayList(); //
		for (int i = 0; i < _allHVS.length; i++) {
			if (_allHVS[i].getStringValue("batchid").equals(_batchid)) {
				al_tmp.add(_allHVS[i]); //
			}
		}
		return (HashVO[]) al_tmp.toArray(new HashVO[0]); //

	}
}
