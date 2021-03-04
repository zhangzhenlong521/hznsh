package cn.com.infostrategy.bs.report;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 直接在服务器端使用模板配置直接输出Html的工具
 * @author xch
 *
 */
public class BillPanelExportHtmlDMO extends AbstractDMO {

	/**
	 * 将一个卡片输出html,之所以使用模板输出,是为了调整宽度与顺序时报表立即会变,即充分利用模板的功能快速配置
	 * 而且可以充分利用加载公式的功能!!
	 * 以前在前台输出的方法是直接从UI控制中取,现在是直接在后台取!!!
	 * @param _templetCode 
	 * @param _sql
	 * @return
	 */
	public String getBillCardHtml(String _templetCode, String _sql) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		sb_html.append(getBillCardHtmlContent(_templetCode, _sql));
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString(); //
	}

	public String getBillListHtml(String _templetCode, String _sql) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		sb_html.append(getBillListHtmlContent(_templetCode, _sql));
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString();
	}

	public String getBillTreeHtml(String _templetCode, String _sql) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		sb_html.append(getBillTreeHtmlContent(_templetCode, _sql));
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString();
	}

	/**
	 * 有的时候常用需要一批显示,比如上面上一个卡片,下面是一个列表,再下面又是一个列表,即一眼清!!!
	 * 比如体系文件,关联了环节,关联了风险点,关联了内规,关联了外规,关联了相关流程!!等...需要一眼清的一下子显示出来!!!
	 * @param _allMulti N行3列的二维数组,第1列是类型(卡片/列表/树),第二列是模板编码,第3列是SQL
	 * @return
	 */
	public String getMultiHtml(String[][] _allMulti) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		TBUtil tbUtil = new TBUtil(); //
		sb_html.append(tbUtil.getHtmlHead());
		for (int i = 0; i < _allMulti.length; i++) {
			if (_allMulti[i][0].equalsIgnoreCase("卡片")) {
				sb_html.append(getBillCardHtmlContent(_allMulti[i][1], _allMulti[i][2])); //
			} else if (_allMulti[i][0].equalsIgnoreCase("列表")) {
				sb_html.append(getBillListHtmlContent(_allMulti[i][1], _allMulti[i][2])); //
			} else if (_allMulti[i][0].equalsIgnoreCase("树")) {
				sb_html.append(getBillTreeHtmlContent(_allMulti[i][1], _allMulti[i][2])); //
			}
		}
		sb_html.append(tbUtil.getHtmlTail());
		return sb_html.toString();
	}

	/**
	 * 一个纯卡片输出,就是一个表格<table></table>
	 * @param _templetCode
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	private String getBillCardHtmlContent(String _templetCode, String _sql) throws Exception {
		//先根据模板编码取出TempletVO,

		//然后根据TempletVO与sql调用getBillVOsByDS()方法,得到BillVO[]!!一定要使用该方法,因为需要使用加载公式自动计算出name等!!!!!

		//然后根据模板中定义自动换行,卡片是否显示,Title分组等属性绘制表格!!!同时从BillVO中取出实际数据填进去!!!要注意宽度上约束死的,即不能横向自动撑宽,要是纵向自动撑高!!(可以参照在UI端的实现逻辑!!!)
		//要注意多行文本框中的数据将\r\n转换成<br>
		//要注意文件附件转换成html的超链接的方法!!!
		StringBuffer sb_html = new StringBuffer(); //
		for (int i = 0; i < 10; i++) {
			sb_html.append("徐鹏程到此一游 <br>"); //
		}
		return sb_html.toString(); //
	}

	/***
	 * 一个纯列表输出
	 * @param _templetCode
	 * @param _sql
	 * @return
	 */
	public String getBillListHtmlContent(String _templetCode, String _sql) throws Exception {
		//先根据模板编码取出TempletVO,

		//然后根据TempletVO与sql调用getBillVOsByDS()方法,得到BillVO[]!!一定要使用该方法,因为需要使用加载公式自动计算出name等!!!!!

		//然后根据模板中定义列表是否显示,列表显示顺序,颜色公式,列表宽度等属性绘制表格!!!同时从BillVO中取出实际数据填进去!!!要注意宽度上约束死的,即不能横向自动撑宽,要是纵向自动撑高!!(可以参照在UI端的实现逻辑!!!)
		//要注意多行文本框中的数据将\r\n转换成<br>
		//要注意文件附件转换成html的超链接的方法!!!

		return null; //
	}

	/**
	 * 树型控件
	 * @param _templetCode
	 * @param _sql
	 * @return
	 */
	public String getBillTreeHtmlContent(String _templetCode, String _sql) throws Exception {

		return null;
	}

}
