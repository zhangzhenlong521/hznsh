package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * �����ϵ�ļ���ʷ�汾�е�Word����!!
 * @author xch
 *
 */
public class WFRiskHistHtmlViewWebCallBean implements WebCallBeanIfc {

	public String getHtmlContent(HashMap map) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_cmpfileid = (String) map.get("cmpfilehistid"); //��ϵ�ļ�����
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + str_cmpfileid + "' and contentname='HTML' order by seq"); //
		StringBuilder sb_doc = new StringBuilder(); //
		String str_itemValue = null; //
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j); //
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break; //
				} else {
					sb_doc.append(str_itemValue.trim()); //ƴ��!!!
				}
			}
		}
		String str_64code = sb_doc.toString(); //
		byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
		byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //��ѹ!!
		return new String(unZipedBytes, "UTF-8"); //���ؽ�ѹ���64λ��!!
	}

}
