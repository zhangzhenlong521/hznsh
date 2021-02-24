package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * ������ϵ�ļ�ΪWord��ʽ�ĺ�̨�߼�!!! ����鿴��ʷ�汾��WFRiskHistDocViewWebCallBean���ű�������!! �鿴��ʷ�汾��Wrod��ֱ�Ӵ����ݿ�[cmp_cmpfile_histcontent]��ȡ!
 * ��ʵ�ʲ鿴������ǰ̨���ͼƬ����,Ȼ���ں�̨��������Word����!!!
 * @author xch
 *
 */
public class WFRiskDocViewWebDisPatch implements WebDispatchIfc {

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap _parMap) throws Exception {
		String str_cmpfileId = (String) _parMap.get("cmpfileid"); //
		HashMap imgBytesMap = (HashMap) _parMap.get("ImgCode"); //
		String ishandbook = (String) _parMap.get("ishandbook"); //�Ƿ񵼳��Ϲ��ֲ�
		WFRiskWordBuilder wordBuilder = new WFRiskWordBuilder(str_cmpfileId, imgBytesMap); //
		byte[] wordBytes = null;
		String cmpfilename = new CommDMO().getStringValueByDS(null, "select cmpfilename from cmp_cmpfile where id =" + str_cmpfileId);
		TBUtil tbutil = new TBUtil();
		String str_cmpfilename = null;
		if ("Y".equalsIgnoreCase(ishandbook)) {
			str_cmpfilename = cmpfilename + "_�Ϲ��ֲ�_" + tbutil.getCurrDate(false, true) + ".doc";
			wordBytes = wordBuilder.getDocContextBytes(true, true); //�����Ϲ��ֲ�
		} else {
			str_cmpfilename = cmpfilename + "_" + tbutil.getCurrDate(false, true) + ".doc";
			wordBytes = wordBuilder.getDocContextBytes(); //ʹ��Word���칤�ߵ�������Word����!!!��ʱ�ĵط�,һ����Ҫ300����!!
		}
		_response.setContentType("application/msword"); //
		_response.setHeader("Content-disposition", "attachment; filename=\"" + new String((str_cmpfilename).getBytes("GBK"), "ISO-8859-1") + "\""); //���Ϊʱ����ֱ�ӱ���
		_response.setContentLength(wordBytes.length); //
		_response.getOutputStream().write(wordBytes); //
		_response.getOutputStream().flush();
		_response.getOutputStream().close(); //
	}

}
