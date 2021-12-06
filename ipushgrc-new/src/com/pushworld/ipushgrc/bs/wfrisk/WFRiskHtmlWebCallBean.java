package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.ImageServlet;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;

/**
 * ��һ�������ļ���Html�ļ��������,����ͼ�б�!!!
 * ��Ҫ�����Ż�����:
 * 1.���Ū��Ư����,��������������,����Ҫ��ɫ!!���Ҫ�̶�
 * 2.ͼ�ϵķ��յ�����WFRiskUIUtil�д���,��������ͼ���ټ��ط��յ�!!!
 * 3.ͼ�ϵ��ȵ�Ҫ��������ͼ�е�x,y,width,height,��̬����,Ȼ����alt����ʾ�������յ����Ϣ!!! �������м�ֵ�ĵط�!!!!
 * 4.�������ͼ�ϵĸ������ڵ��ȵ�,������ת�������Ӧ����иû��ڵ�˵���ĵط�!!!
 * 5.�ڱ����������,���ڹ��,���Լ������������,Ȼ��鿴��Ӧ��������ڹ���ϸ��Ϣ!!!
 * 6.����������ϢҲҪ֧�ֵ���鿴��ϸ,�����´����м�������!!!
 * @author xch
 *
 */
public class WFRiskHtmlWebCallBean implements WebCallBeanIfc {

	/**
	 * ���Html������!!!
	 */
	public String getHtmlContent(HashMap _pasrMap) throws Exception {
		String str_cmpFileid = (String) _pasrMap.get("cmpfileid"); //
		String str_regid = (String) _pasrMap.get("regcacheid"); //
		int htmlStyle = Integer.parseInt(_pasrMap.get("htmlStyle").toString()); //
		if (str_cmpFileid == null) {
			return null;
		}
		return new WFRiskHtmlBuilder().getHtmlContent(str_cmpFileid, str_regid, htmlStyle); //
	}

	/**
	 * ��һ��ע�Ỻ��!!!
	 * @param _par
	 * @return
	 */
	public HashMap registCacheCode(HashMap _par) throws Exception {
		String str_cacheId = ImageServlet.registCacheCode(_par); //
		HashMap returnMap = new HashMap(); //
		returnMap.put("regcacheid", str_cacheId); //
		return returnMap; //
	}

}
