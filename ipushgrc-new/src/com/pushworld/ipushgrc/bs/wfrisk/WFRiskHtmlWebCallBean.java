package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.ImageServlet;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;

/**
 * 将一个流程文件以Html文件生成输出,即有图有表!!!
 * 需要继续优化的有:
 * 1.表格弄得漂亮点,可以找美工处理,标题要彩色!!宽度要固定
 * 2.图上的风险点是在WFRiskUIUtil中处理,即在生成图后再加载风险点!!!
 * 3.图上的热点要根据流程图中的x,y,width,height,动态生成,然后在alt中显示各个风险点的信息!!! 这是最有价值的地方!!!!
 * 4.点击流程图上的各个环节的热点,可以跳转到下面对应表格中该环节的说明的地方!!!
 * 5.在表格关联到外规,或内规后,可以继续点击超链接,然后查看对应的外规与内规详细信息!!!
 * 6.风险评估信息也要支持点击查看明细,即在新窗口中坚起来看!!!
 * @author xch
 *
 */
public class WFRiskHtmlWebCallBean implements WebCallBeanIfc {

	/**
	 * 输出Html的内容!!!
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
	 * 第一次注册缓存!!!
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
