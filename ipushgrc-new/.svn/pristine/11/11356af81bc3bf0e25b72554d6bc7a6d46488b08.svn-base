package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 输出体系文件历史版本中的Word内容!!
 * @author xch
 *
 */
public class WFRiskHistDocViewWebCallBean implements WebDispatchIfc {

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap map) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		CommDMO dmo = new CommDMO();
		String str_cmpfilehistid = (String) map.get("cmpfilehistid"); //流程文件历史版本主键
		String str_cmpfilename = null;
		if (str_cmpfilehistid == null) {//如果不是在历史版本中查看，而是直接点击某个流程文件来查看文件
			String str_cmpfileid = (String) map.get("cmpfileid"); //流程文件主键
			str_cmpfilehistid = dmo.getStringValueByDS(null, "select id from cmp_cmpfile_hist where cmpfile_id =" + str_cmpfileid + " order by cmpfile_versionno desc");
			str_cmpfilename = dmo.getStringValueByDS(null, "select cmpfilename from cmp_cmpfile where id =" + str_cmpfileid);
			str_cmpfilename += "_" + tbUtil.getCurrDate(false, true);
		} else {
			str_cmpfilename = dmo.getStringValueByDS(null, "select cmpfile_name from cmp_cmpfile_hist where id =" + str_cmpfilehistid);
			str_cmpfilename += "_" + tbUtil.getCurrDate(false, true);
		}
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + str_cmpfilehistid + "' and contentname='DOC' order by seq"); //
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
		byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //解压!!这就是Word的实际内容,用于输出!!!
		_response.setContentType("application/msword"); //
		_response.setHeader("Content-disposition", "attachment; filename=\"" + new String((str_cmpfilename+".doc").getBytes("GBK"), "ISO-8859-1") + "\""); //
		_response.setContentLength(unZipedBytes.length); //
		_response.getOutputStream().write(unZipedBytes); //
		_response.getOutputStream().flush();
		_response.getOutputStream().close(); //
	}
}
