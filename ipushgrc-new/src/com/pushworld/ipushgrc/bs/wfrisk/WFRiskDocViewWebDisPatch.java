package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 导出体系文件为Word格式的后台逻辑!!! 它与查看历史版本的WFRiskHistDocViewWebCallBean有着本质区别!! 查看历史版本的Wrod是直接从数据库[cmp_cmpfile_histcontent]中取!
 * 而实际查看是先在前台算出图片的码,然后在后台当场生成Word内容!!!
 * @author xch
 *
 */
public class WFRiskDocViewWebDisPatch implements WebDispatchIfc {

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap _parMap) throws Exception {
		String str_cmpfileId = (String) _parMap.get("cmpfileid"); //
		HashMap imgBytesMap = (HashMap) _parMap.get("ImgCode"); //
		String ishandbook = (String) _parMap.get("ishandbook"); //是否导出合规手册
		WFRiskWordBuilder wordBuilder = new WFRiskWordBuilder(str_cmpfileId, imgBytesMap); //
		byte[] wordBytes = null;
		String cmpfilename = new CommDMO().getStringValueByDS(null, "select cmpfilename from cmp_cmpfile where id =" + str_cmpfileId);
		TBUtil tbutil = new TBUtil();
		String str_cmpfilename = null;
		if ("Y".equalsIgnoreCase(ishandbook)) {
			str_cmpfilename = cmpfilename + "_合规手册_" + tbutil.getCurrDate(false, true) + ".doc";
			wordBytes = wordBuilder.getDocContextBytes(true, true); //导出合规手册
		} else {
			str_cmpfilename = cmpfilename + "_" + tbutil.getCurrDate(false, true) + ".doc";
			wordBytes = wordBuilder.getDocContextBytes(); //使用Word构造工具当场创建Word对象!!!耗时的地方,一般需要300毫秒!!
		}
		_response.setContentType("application/msword"); //
		_response.setHeader("Content-disposition", "attachment; filename=\"" + new String((str_cmpfilename).getBytes("GBK"), "ISO-8859-1") + "\""); //另存为时可以直接保存
		_response.setContentLength(wordBytes.length); //
		_response.getOutputStream().write(wordBytes); //
		_response.getOutputStream().flush();
		_response.getOutputStream().close(); //
	}

}
