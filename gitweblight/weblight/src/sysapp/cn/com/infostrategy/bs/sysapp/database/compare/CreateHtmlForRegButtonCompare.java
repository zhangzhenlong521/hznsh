package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;

public class CreateHtmlForRegButtonCompare implements WebCallBeanIfc {

	private CommDMO commDMO = null;

	@SuppressWarnings("unchecked")
	public String getHtmlContent(HashMap map) throws Exception {
		//取得两个表的数据
		String type = (String) map.get("type"); //
		String dataSource1 = map.get("datasource1").toString();
		String dataSource2 = map.get("datasource2").toString();

		commDMO = new CommDMO();

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<style   type=\"text/css\"> \r\n");
		sb_html.append("<!--   \r\n");
		sb_html.append(" table   {  border-collapse:   collapse; }   \r\n");
		sb_html.append("td   {  font-size: 12px; border:solid   1px   #888888;  }   \r\n");
		sb_html.append(" -->   \r\n");
		sb_html.append(" </style>   \r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n");
		if (type.equals("0")) {
			sb_html.append(getNewTabOrColumnHtml(dataSource1, dataSource2)); //
		} else if (type.equals("1")) {
			sb_html.append(getNewTabOrColumnHtml(dataSource2, dataSource1)); //
		} else if (type.equals("2")) {
			sb_html.append(getCompareHtml(dataSource1, dataSource2)); //
		}

		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 比较只存于1,不存于2中的数据
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getNewTabOrColumnHtml(String _dsname_1, String _dsname_2) throws Exception {
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from pub_regbuttons");
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from pub_regbuttons");
		//找出只存在于1而不存于2中的数据..
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">注册按钮编码</td><td align=\"center\">类型</td><td align=\"center\">类型值</td></tr>\r\n"); //
		int li_count = 1; //

		for (int i = 0; i < hvs_ds1.length; i++) {
			int j = 0;
			for (; j < hvs_ds2.length; j++) {
				if (hvs_ds1[i].getStringValue("code").equals(hvs_ds2[j].getStringValue("code"))) {

					break;
				}
			}
			if (j >= hvs_ds2.length) {
				sb_html.append("<tr><td align=\"left\" bgcolor=\"#DDDDDD\" colspan=\"3\">" + "[<font color=red><strong>" + li_count + "</strong></font>]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]多的注册按钮如下</td></tr>\r\n"); //
				sb_html.append("<tr><td align=\"center\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">btntext</td><td align=\"center\">" + hvs_ds1[i].getStringValue("btntext") + "</td></tr>\r\n"); //
				sb_html.append("<tr><td align=\"center\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">btntype</td><td align=\"center\">" + hvs_ds1[i].getStringValue("btntype") + "</td></tr>\r\n"); //
				sb_html.append("<tr><td align=\"center\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">clickingformula</td><td align=\"center\">" + hvs_ds1[i].getStringValue("clickingformula") + "</td></tr>\r\n");
				sb_html.append("<tr><td align=\"center\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">clickedformula</td><td align=\"center\">" + hvs_ds1[i].getStringValue("clickedformula") + "</td></tr>\r\n");
				li_count++;
			}
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 比较类型不一样的
	 * @return
	 * @throws Excpetion
	 */

	private String getCompareHtml(String _dsname_1, String _dsname_2) throws Exception {
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from pub_regbuttons");
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from pub_regbuttons");
		//找出只存在于1而不存于2中的数据..
		StringBuffer sb_html = new StringBuffer(); //
		StringBuffer tr = null;
		sb_html.append("<table width=\"100%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">注册按钮编码</td><td align=\"center\">类型</td><td align=\"center\">数据源1</td><td align=\"center\">数据源2</td></tr>\r\n"); //
		int li_count = 1; //

		for (int i = 0; i < hvs_ds1.length; i++) {
			int j = 0;
			tr = new StringBuffer();
			for (; j < hvs_ds2.length; j++) {
				if (hvs_ds1[i].getStringValue("code").equals(hvs_ds2[j].getStringValue("code"))) {
					if (!isSameButtonText(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>btntext</font></td><td align=\"center\">" + hvs_ds1[i].getStringValue("btntext") + "</td><td align=\"center\">"
								+ hvs_ds2[j].getStringValue("btntext") + "</td></tr>\r\n"); //
					}
					if (!isSameButtonType(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>btntype</font></td><td align=\"center\">" + hvs_ds1[i].getStringValue("btntype") + "</td><td align=\"center\">"
								+ hvs_ds2[j].getStringValue("btntype") + "</td></tr>\r\n"); //
					}
					if (!isSameClickedformula(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>clickedformula</font></td><td align=\"center\">" + hvs_ds1[i].getStringValue("clickedformula")
								+ "</td><td align=\"center\">" + hvs_ds2[j].getStringValue("clickedformula") + "</td></tr>\r\n"); //
					}
					if (!isSameClickingformula(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>clickingformula</font></td><td align=\"center\">" + hvs_ds1[i].getStringValue("clickingformula")
								+ "</td><td align=\"center\">" + hvs_ds2[j].getStringValue("clickingformula") + "</td></tr>\r\n"); //
					}
				}
			}
			if (tr.length() > 0) {
				sb_html.append("<tr><td align=\"left\" bgcolor=\"#DDDDDD\" colspan=\"4\">[<font color=red><strong>" + li_count + "</strong></font>]两个数据源在编码为[<strong>" + hvs_ds1[i].getStringValue("code") + "</strong>]的注册按钮中不一样的类型如下</td></tr>\r\n");
				sb_html.append(tr.toString());
				li_count++;
			}
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	private boolean isSameButtonText(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}
		if (newTempButton.getStringValue("btntext") == null && oldTempButton.getStringValue("btntext") == null) {
			return true;
		}

		if (newTempButton.getStringValue("btntext") != null && oldTempButton.getStringValue("btntext") != null && newTempButton.getStringValue("btntext").equals(oldTempButton.getStringValue("btntext"))) {
			return true;
		}
		return false;
	}

	private boolean isSameButtonType(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}
		if (newTempButton.getStringValue("btntype") == null && oldTempButton.getStringValue("btntype") == null) {
			return true;
		}

		if (newTempButton.getStringValue("btntype") != null && oldTempButton.getStringValue("btntype") != null && newTempButton.getStringValue("btntype").equals(oldTempButton.getStringValue("btntype"))) {
			return true;
		}
		return false;
	}

	private boolean isSameClickingformula(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}
		if (newTempButton.getStringValue("clickingformula") == null && oldTempButton.getStringValue("clickingformula") == null) {
			return true;
		}

		if (newTempButton.getStringValue("clickingformula") != null && oldTempButton.getStringValue("clickingformula") != null && newTempButton.getStringValue("clickingformula").equals(oldTempButton.getStringValue("clickingformula"))) {
			return true;
		}
		return false;
	}

	private boolean isSameClickedformula(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}
		if (newTempButton.getStringValue("clickedformula") == null && oldTempButton.getStringValue("clickedformula") == null) {
			return true;
		}

		if (newTempButton.getStringValue("clickedformula") != null && oldTempButton.getStringValue("clickedformula") != null && newTempButton.getStringValue("clickedformula").equals(oldTempButton.getStringValue("clickedformula"))) {
			return true;
		}
		return false;
	}
}