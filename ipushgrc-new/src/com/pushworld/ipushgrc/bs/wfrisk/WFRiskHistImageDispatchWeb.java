package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 调用图片的servlet,访问方法是 ./DispatchServlet?clsname=com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistImageDispatchWeb&cmpfilehistid=125&imgname=IMAGE_1
 * @author xch
 *
 */
public class WFRiskHistImageDispatchWeb implements WebDispatchIfc {

	private static final long serialVersionUID = -8875442295920012349L;

	public void service(HttpServletRequest _request, HttpServletResponse _response,HashMap _parMap) throws Exception {
		String str_cmpfilehistid = (String) _request.getParameter("cmpfilehistid"); //历史版本id 
		String str_imagename = (String) _request.getParameter("imgname"); //图片名称,比如IMAGE_1,IMAGE_2...
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + str_cmpfilehistid + "' and contentname='" + str_imagename + "' order by seq"); ////
		StringBuilder sb_image = new StringBuilder(); //
		String str_itemValue = null; //
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j); //
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break; //
				} else {
					sb_image.append(str_itemValue.trim()); //拼接!!!
				}
			}
		}
		TBUtil tbUtil = new TBUtil(); //
		byte[] bytes = tbUtil.convert64CodeToBytes(sb_image.toString()); //
		byte[] unzipedBytes = tbUtil.decompressBytes(bytes);  //解压!!!
		_response.setContentType("image/jpeg"); //格式内容!
		_response.setHeader("Content-Disposition", "image/jpeg; filename=cmpfile.jpg"); //
		ServletOutputStream outstream = _response.getOutputStream(); //
		outstream.write(unzipedBytes); //输出内容!!
		outstream.flush(); //
		outstream.close(); //
	}

}
