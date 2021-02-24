package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

public class CreateHtmlForFormatPanelCompare implements WebCallBeanIfc {

	private CommDMO commDMO = null;

	public String getHtmlContent(HashMap map) throws Exception {
		//ȡ�������������
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

	private String getNewTabOrColumnHtml(String _dsname_1, String _dsname_2) throws Exception {
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from pub_regformatpanel");
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from pub_regformatpanel");
		//�ҳ�ֻ������1��������2�е�����..
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"100%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">ע���������</td><td align=\"center\">����</td><td align=\"center\">����ֵ</td></tr>\r\n"); //
		int li_count = 1; //

		for (int i = 0; i < hvs_ds1.length; i++) {
			int j = 0;
			for (; j < hvs_ds2.length; j++) {
				if (hvs_ds1[i].getStringValue("code").equals(hvs_ds2[j].getStringValue("code"))) {

					break;
				}
			}
			if (j >= hvs_ds2.length) {
				sb_html.append("<tr><td align=\"left\" bgcolor=\"#DDDDDD\" colspan=\"3\">" + "[<font color=red><strong>" + li_count + "</strong></font>]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]���ע����������</td></tr>\r\n"); //
				sb_html.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">formatformula</td><td align=\"center\">" + hvs_ds1[i].getStringValue("formatformula") + "</td></tr>\r\n"); //
				sb_html.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">eventbindformula</td><td align=\"center\">" + hvs_ds1[i].getStringValue("eventbindformula") + "</td></tr>\r\n"); //
				sb_html.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">regformula1</td><td align=\"center\">" + hvs_ds1[i].getStringValue("regformula1") + "</td></tr>\r\n");
				sb_html.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">regformula2</td><td align=\"center\">" + hvs_ds1[i].getStringValue("regformula2") + "</td></tr>\r\n");
				sb_html.append("<tr><td align=\"center\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\">regformula3</td><td align=\"center\">" + hvs_ds1[i].getStringValue("regformula3") + "</td></tr>\r\n");
				li_count++;
			}
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * �Ƚ��в�һ����
	 * @return
	 * @throws Excpetion
	 */

	private String getCompareHtml(String _dsname_1, String _dsname_2) throws Exception {
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from pub_regformatpanel");
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from pub_regformatpanel");
		//�ҳ�ֻ������1��������2�е�����..
		StringBuffer sb_html = new StringBuffer(); //
		StringBuffer tr = null;
		sb_html.append("<table width=\"100%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">ע���������</td><td align=\"center\">����</td><td align=\"center\">����Դ1</td><td align=\"center\">����Դ2</td></tr>\r\n"); //
		int li_count = 1; //

		for (int i = 0; i < hvs_ds1.length; i++) {
			int j = 0;
			tr = new StringBuffer();
			for (; j < hvs_ds2.length; j++) {
				if (hvs_ds1[i].getStringValue("code").equals(hvs_ds2[j].getStringValue("code"))) {
					if (!isSameFormatformula(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"left\" valign=\"top\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>formatformula</font></td><td align=\"left\" valign=\"top\">"
								+ hvs_ds1[i].getStringValue("formatformula") + "</td><td align=\"left\" valign=\"top\">" + hvs_ds2[j].getStringValue("formatformula") + "</td></tr>\r\n"); //
					}
					if (!isSameEventbindformula(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"left\" valign=\"top\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>eventbindformula</font></td><td align=\"left\" valign=\"top\">"
								+ hvs_ds1[i].getStringValue("eventbindformula") + "</td><td align=\"left\" valign=\"top\">" + hvs_ds2[j].getStringValue("eventbindformula") + "</td></tr>\r\n"); //
					}
					if (!isSameRegformula1(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"left\" valign=\"top\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>regformula1</font></td><td align=\"left\" valign=\"top\">" + hvs_ds1[i].getStringValue("regformula1")
								+ "</td><td align=\"left\" valign=\"top\">" + hvs_ds2[j].getStringValue("regformula1") + "</td></tr>\r\n"); //
					}
					if (!isSameRegformula2(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"left\" valign=\"top\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>regformula2</font></td><td align=\"left\" valign=\"top\">" + hvs_ds1[i].getStringValue("regformula2")
								+ "</td><td align=\"center\">" + hvs_ds2[j].getStringValue("regformula2") + "</td></tr>\r\n"); //
					}
					if (!isSameRegformula3(hvs_ds1[i], hvs_ds2[j])) {
						tr.append("<tr><td align=\"left\" valign=\"top\" width=\"100\"><font color=blue>" + hvs_ds1[i].getStringValue("code") + "</font></td><td align=\"center\"><font color=blue>regformula3</font></td><td align=\"left\" valign=\"top\">" + hvs_ds1[i].getStringValue("regformula3")
								+ "</td><td align=\"center\">" + hvs_ds2[j].getStringValue("regformula3") + "</td></tr>\r\n"); //
					}
				}
			}
			if (tr.length() > 0) {
				sb_html.append("<tr><td align=\"left\" bgcolor=\"#DDDDDD\" colspan=\"4\">[<font color=red><strong>" + li_count + "</strong></font>]��������Դ�ڱ���Ϊ[<strong>" + hvs_ds1[i].getStringValue("code") + "</strong>]��ע�������в�һ������������</td></tr>\r\n");
				sb_html.append(tr.toString());
				li_count++;
			}
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * ���湫ʽ�Ƚ�
	 * @param newTempButton
	 * @param oldTempButton
	 * @return
	 */
	private boolean isSameFormatformula(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}
		return new TBUtil().compareTwoStringdIgnoreWrap(newTempButton.getStringValue("formatformula"), oldTempButton.getStringValue("formatformula"));
	}

	private boolean isSameEventbindformula(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}

		return new TBUtil().compareTwoStringdIgnoreWrap(newTempButton.getStringValue("eventbindformula"), oldTempButton.getStringValue("eventbindformula"));
	}

	private boolean isSameRegformula1(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}
		return new TBUtil().compareTwoStringdIgnoreWrap(newTempButton.getStringValue("regformula1"), oldTempButton.getStringValue("regformula1"));
	}

	private boolean isSameRegformula2(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}

		return new TBUtil().compareTwoStringdIgnoreWrap(newTempButton.getStringValue("regformula2"), oldTempButton.getStringValue("regformula2"));
	}

	private boolean isSameRegformula3(HashVO newTempButton, HashVO oldTempButton) {
		if (newTempButton == null && oldTempButton == null) {
			return true;
		}

		return new TBUtil().compareTwoStringdIgnoreWrap(newTempButton.getStringValue("regformula3"), oldTempButton.getStringValue("regformula3"));
	}
}
