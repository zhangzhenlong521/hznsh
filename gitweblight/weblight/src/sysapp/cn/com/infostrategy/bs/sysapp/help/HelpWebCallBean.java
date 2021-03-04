package cn.com.infostrategy.bs.sysapp.help;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 以后要将一些非常关键的：实施方法论,需要分析的关键点与技巧,实施接触面,开发人员技巧!
 * 例子代码,都统统直接放在系统中,让实施人员,开发人员都能快速查看!
 * @author xch
 *
 */
public class HelpWebCallBean implements WebDispatchIfc {

	private int li_onerowCount = 5; //表格一行有几列?
	private boolean isLocalHref = false; //

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap map) throws Exception {
		String str_helpfile = (String) map.get("file"); //看是读哪个文件!
		if (str_helpfile == null || str_helpfile.trim().equals("")) {
			_response.setCharacterEncoding("GBK"); //
			_response.setContentType("text/html"); //
			_response.getWriter().println(getDefaultHtml()); //
		} else {
			String str_fileType = str_helpfile.substring(str_helpfile.indexOf(".") + 1, str_helpfile.length()); //文件类型!!
			if (str_fileType.equalsIgnoreCase("txt")) { //如果是文本文件!!
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/plain"); //纯文本!!
				_response.getWriter().println(getFileTextContent(str_helpfile)); //
				_response.flushBuffer(); //
			} else if (str_fileType.equalsIgnoreCase("htm") || str_fileType.equalsIgnoreCase("html")) { //如果是html文件
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				_response.getWriter().println(getFileTextContent(str_helpfile)); //
			} else if (str_fileType.equalsIgnoreCase("doc")) { //以后还可能是doc,即Word文件!

			}
		}
	}

	//默认首页!
	private String getDefaultHtml() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\n"); //
		sb_html.append("<head>\n"); //
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\n"); //
		sb_html.append("<TITLE>方法论/知识库中心</TITLE>\n"); //
		sb_html.append("<style type=\"text/css\"> \n"); //
		sb_html.append(".p_text  { font-size: 12px;};\n"); //
		sb_html.append(".table {  border-collapse:   collapse; font-size: 12px; };\n"); //
		sb_html.append(".td    {  border:   solid   1px   #888888; font-size: 12px; };\n"); //
		sb_html.append("</style>\n"); //
		sb_html.append("</head>\n"); //
		sb_html.append("<body>\n"); //
		sb_html.append("<span align=\"center\" class=\"p_text\">方法论/知识库中心</span><br>\n"); //
		sb_html.append("<table width=\"90%\" align=\"center\" class=\"table\" cellspacing=0 cellpadding=8>\n"); //

		//纯业务知识
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">纯业务</td></tr>\r\n"); //
		ArrayList<String> al_yw = new ArrayList<String>(); //
		al_yw.add(getTD("银行的组织结构", "yw_orgn.txt")); //
		al_yw.add(getTD("银行主要业务", "yw_yw.txt")); //
		al_yw.add(getTD("法律合规部的结构与职能", "yw_hgb.txt")); //
		al_yw.add(getTD("内控基本规范", "yw_nkgf.txt")); //
		al_yw.add(getTD("18号指引", "yw_18zy.txt")); //
		al_yw.add(getTD("巴塞尔协议", "yw_basaier.txt")); //
		al_yw.add(getTD("萨班斯法案", "yw_sbs.txt")); //
		al_yw.add(getTD("三大风险(信用,市场,操作)的关系与区别", "yw_risk3.txt")); //
		sb_html.append(getHtmlByList(al_yw)); //

		//产品方面的内容!!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">产品方面</td></tr>\r\n"); //
		ArrayList<String> al_cp = new ArrayList<String>(); //
		al_cp.add(getTD("产品总体思路与主要模块", "cp_1.txt")); //
		al_cp.add(getTD("产品各模块的核心需求点", "cp_2.txt")); //
		al_cp.add(getTD("产品的“关键管理逻辑/流程”", "cp_3.txt")); //
		al_cp.add(getTD("★★★数据权限与主要角色", "cp_datapolicy.htm")); //
		al_cp.add(getTD("★★★功能权限与主要角色", "cp_funcpolicy.htm")); //
		al_cp.add(getTD("合规,内控,风险产品之间的区别", "cp_4.txt")); //
		al_cp.add(getTD("产品问题100问", "cp_5.txt")); //
		al_cp.add(getTD("产品介绍之必讲“亮点”", "cp_liandian.txt")); //
		al_cp.add(getTD("内控五原则在产品中的体现点", "cp_5.txt")); //
		al_cp.add(getTD("产品最终输出的管理指标", "cp_5.txt")); //
		sb_html.append(getHtmlByList(al_cp)); //

		//实施方面的内容!!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">实施方面</td></tr>\r\n"); //
		ArrayList<String> al_ss = new ArrayList<String>(); //
		al_ss.add(getTD("系统实施6步骤", "ss_6bz.txt")); //
		al_ss.add(getTD("系统实施10个接触面", "ss_10jcm.txt")); //
		al_ss.add(getTD("需求分析要点8项清理", "ss_8xql.txt")); //
		al_ss.add(getTD("咨询师/IT人员;产品/二次开发之间的边界区分原则", "ss_6bz.txt")); //
		al_ss.add(getTD("业务需求转变系统需求之技巧(角色,功能点,流程,数据对象设计)", "ss_8xql.txt")); //
		al_ss.add(getTD("实施过程常见刁难问题应对技巧", "ss_custquestion.htm")); //
		al_ss.add(getTD("测试环节关键注意事项", "ss_test.htm")); //
		al_ss.add(getTD("实施人员必备的第三方技术知识", "ss_10jcm.txt")); //

		sb_html.append(getHtmlByList(al_ss)); //

		//开发-基本/常用
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">开发方面-基本/常用的</td></tr>\r\n"); //
		ArrayList<String> al_kf_comm = new ArrayList<String>(); //
		al_kf_comm.add(getTD("平台技术路线说明及10大特点", "kf_ptjslx.htm")); //
		al_kf_comm.add(getTD("平台所有33个功能", "kf_pt33.htm")); //
		al_kf_comm.add(getTD("平台-模板配置所有功能", "kf_ptbill.htm")); //
		al_kf_comm.add(getTD("平台-工作流引擎所有功能", "kf_ptwf.htm")); //
		al_kf_comm.add(getTD("平台-报表开发所有功能", "kf_ptreport.htm")); //

		al_kf_comm.add(getTD("开发人员学习路线图", "kf_newstudy.html")); //
		al_kf_comm.add(getTD("第一个基本应用", "kf_comm_firstpage.txt")); //
		al_kf_comm.add(getTD("自定义远程调用", "kf_comm_remotecall.htm")); //
		al_kf_comm.add(getTD("写一个最简单的面板", "myfirst.txt")); //
		al_kf_comm.add(getTD("UIUTil,TBUtil,CommDMO,SysAppDMO这四个类的函数", ".txt")); //
		al_kf_comm.add(getTD("弹出框MessageBox使用", "kf_comm_msgbox.htm")); //
		al_kf_comm.add(getTD("使用SQL查询,修改数据库", ".txt")); //
		al_kf_comm.add(getTD("打开一个服务器的文件", ".txt")); //
		al_kf_comm.add(getTD("Office控件的使用", ".txt")); //
		al_kf_comm.add(getTD("首页消息中心的内容构造", ".txt")); //
		al_kf_comm.add(getTD("单点登录与IE访问原理", "kf_comm_singlelogin.htm")); //
		al_kf_comm.add(getTD("导出Word,图片,Excel的技术原理", ".txt")); //
		al_kf_comm.add(getTD("★影响性能的致命注意点", ".txt")); //
		al_kf_comm.add(getTD("开发人员必备之Java技术点", ".txt")); //
		al_kf_comm.add(getTD("开发人员必备之SQL技术点", ".txt")); //
		al_kf_comm.add(getTD("开发常用词语中英文参照", "CN_EN.txt")); //
		sb_html.append(getHtmlByList(al_kf_comm)); //

		//开发-元数据
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">开发方面-单据模板</td></tr>\r\n"); //
		ArrayList<String> al_kf_meta = new ArrayList<String>(); //
		al_kf_meta.add(getTD("列表常用监听事件与函数", "kf_mt_billlist.htm")); //
		al_kf_meta.add(getTD("树型面板监听事件与函数", "kf_mt_billtree.htm")); //
		al_kf_meta.add(getTD("卡片监听事件与函数", "kf_mt_billcard.htm")); //
		al_kf_meta.add(getTD("BillHtml面板的使用", "kf_howtousebillhtmlpanel.htm")); //
		al_kf_meta.add(getTD("BillCellPanel的使用技巧", "kf_howtousebillcellpanel.htm")); //
		al_kf_meta.add(getTD("BillBomPanel的使用技巧", "kf_howtousebillbompanel.htm")); //
		al_kf_meta.add(getTD("构造模板的常用几种方法", "kf_methodofcreatemeta.htm")); //
		al_kf_meta.add(getTD("BillVO与HashVO的区别与使用", "kf_billvoandhashvo.htm")); //
		al_kf_meta.add(getTD("数据权限的使用", "kf_howtousedataright.htm")); //
		al_kf_meta.add(getTD("利用查询面板生成条件SQL", "kf_getsqlbybillquerypanel.htm")); //
		al_kf_meta.add(getTD("加载公式/编辑公式", "kf_loadandeditformula.htm")); //
		al_kf_meta.add(getTD("自定义参照开发", "kf_selfdescref.htm")); //
		al_kf_meta.add(getTD("自己定义控件开发", "kf_selfdesccomponent.htm")); //
		al_kf_meta.add(getTD("与另一个表关联的开发逻辑点", "")); //
		al_kf_meta.add(getTD("与另一个表关联的开发逻辑点", "")); //
		sb_html.append(getHtmlByList(al_kf_meta)); //

		//开发-工作流!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">开发方面-工作流</td></tr>\r\n"); //
		ArrayList<String> al_kf_wf = new ArrayList<String>(); //
		al_kf_wf.add(getTD("工作流开发的基本过程", "")); //
		al_kf_wf.add(getTD("★★★流程图设计原则(识别重用与拆分的原则)", "kf_wf_design.htm")); //
		al_kf_wf.add(getTD("流程图配置之参与者机制说明", "")); //
		al_kf_wf.add(getTD("工作流相关拦截器介绍", "")); //
		al_kf_wf.add(getTD("工作流涉及的类,表及逻辑", "")); //
		al_kf_wf.add(getTD("流程执行日志导出报表", "")); //
		sb_html.append(getHtmlByList(al_kf_wf)); //

		//开发-报表!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">开发方面-报表</td></tr>\r\n"); //
		ArrayList<String> al_kf_report = new ArrayList<String>(); //
		al_kf_report.add(getTD("风格报表1(单列表)", "kf_report_style1.htm"));
		al_kf_report.add(getTD("风格报表2(可合并明细列表)", "kf_report_style2.htm"));
		al_kf_report.add(getTD("风格报表3(Chart图表)", "")); //
		al_kf_report.add(getTD("万维交叉报表的开发", "kf_report_multi.htm")); //
		al_kf_report.add(getTD("Html报表的开发", "")); //
		al_kf_report.add(getTD("Word报表的开发", "")); //
		al_kf_report.add(getTD("4种风格报表的开发", "")); //
		sb_html.append(getHtmlByList(al_kf_report)); //

		//其他
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">其他</td></tr>\r\n"); //
		ArrayList<String> al_others = new ArrayList<String>(); //
		al_others.add(getTD("为什么要有这样一个知识库?", "")); //
		al_others.add(getTD("", "")); //
		al_others.add(getTD("", "")); //
		al_others.add(getTD("", "")); //
		al_others.add(getTD("", "")); //
		sb_html.append(getHtmlByList(al_others)); //

		sb_html.append("</table>\n"); //
		sb_html.append("</body>\n"); //
		sb_html.append("</html>\n"); //
		return sb_html.toString(); //
	}

	//因为以后存在有中间插入某个帮助项,为了动态输出,只能使用List
	//就是每4个输出一行!再把余数处理一下!!
	private Object getHtmlByList(ArrayList<String> al_cp) {

		StringBuilder sb_html = new StringBuilder(); //
		int li_size = al_cp.size(); //
		int li_row = li_size / li_onerowCount; //
		int li_mod = li_size % li_onerowCount; //余数
		for (int i = 0; i < li_row; i++) { //遍历各行!!
			sb_html.append("<tr>\n"); //
			for (int j = 0; j < li_onerowCount; j++) { //
				sb_html.append((String) al_cp.get(i * li_onerowCount + j)); //
			}
			sb_html.append("</tr>\n"); //
		}
		if (li_mod > 0) { //如果有余数!则输出最后一行!
			sb_html.append("<tr>\n"); //
			for (int i = 0; i < li_mod; i++) {
				sb_html.append((String) al_cp.get(li_row * li_onerowCount + i)); //
			}
			for (int i = 0; i < li_onerowCount - li_mod; i++) { //最后的用空格,否则表格不全!
				sb_html.append(getTD(null, null)); //空格!
			}
			sb_html.append("</tr>\n"); //
		}

		return sb_html.toString(); //
	}

	private String getTD(String _text, String _fileName) {
		if (_text == null || _text.equals("")) {
			return "<td class=\"td\" align=\"center\">&nbsp;</td>\r\n"; //
		} else {
			if (isLocalHref) {
				return "<td class=\"td\" align=\"center\"><a href=\"./" + _fileName + "\" target=\"_blank\">" + _text + "</a></td>\r\n"; //
			} else {
				return "<td class=\"td\" align=\"center\"><a href=\"./WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean&file=" + _fileName + "\" target=\"_blank\">" + _text + "</a></td>\r\n"; //
			}
		}
	}

	//将一个文本文件读出来
	private String getFileTextContent(String _fileName) {
		InputStream ins = this.getClass().getResourceAsStream("/cn/com/infostrategy/bs/sysapp/help/" + _fileName); //
		if (ins == null) {
			return "没有找到文件[" + _fileName + "]!";
		}
		String str_text = TBUtil.getTBUtil().readFromInputStreamToStr(ins); //
		return str_text; //
	}

	public static void main(String[] _args) {
		HelpWebCallBean wb = new HelpWebCallBean(); //
		wb.isLocalHref = true; //
		String str_html = wb.getDefaultHtml(); //
		try {
			TBUtil.getTBUtil().writeStrToOutputStream(new FileOutputStream(new File("C:/default.htm")), str_html); //
			System.out.println("写文件[C:\\default.htm]成功!!!"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
}
