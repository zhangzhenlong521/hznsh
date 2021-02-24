package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 输出体系文件历史版本中的Word内容!!
 * @author xch
 *
 */
public class WFRiskHistHtmlViewWebCallBean implements WebCallBeanIfc {

	public String getHtmlContent(HashMap map) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_cmpfileid = (String) map.get("cmpfilehistid"); //体系文件主键
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + str_cmpfileid + "' and contentname='HTML' order by seq"); //
		StringBuilder sb_doc = new StringBuilder(); //
		String str_itemValue = null; //
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j); //
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break; //
				} else {
					sb_doc.append(str_itemValue.trim()); //拼接!!!
				}
			}
		}
		String str_64code = sb_doc.toString(); //
		byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
		byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //解压!!
		return new String(unZipedBytes, "UTF-8"); //返回解压后的64位码!!
	}

}
