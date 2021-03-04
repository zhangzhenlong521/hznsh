package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;

public class CreateHtmlForTempletCompare implements WebCallBeanIfc {

	private CommDMO commDMO = null;

	private String[] str_parentFieldNames_new = new String[] { "templetcode", "templetname", "tablename", "savedtablename" }; //
	private String[] str_childFieldNames_new = new String[] { "itemkey", "itemname", "itemtype" }; //

	private String[] str_parentFieldNames_compare = new String[] { "templetcode", "templetname", "tablename", "dataconstraint", "pkname", "pksequencename", "savedtablename", "cardcustpanel", "listcustpanel", "cardwidth", "isshowlistpagebar", "isshowlistopebar", "isshowcardborder", "treepk",
			"treeparentpk", "propbeanclassname", "treeviewfield", "treeseqfield", "treeisshowroot", "isshowlistquickquery", "isshowlistcustbtn", "listcustbtndesc", "templetbtns", "listrowheight", "listheaderisgroup", "isshowcardcustbtn", "ordercondition", "treeisonlyone", "treeisshowtoolbar",
			"islistautorowheight", "cardcustbtndesc", "treecustbtndesc", "bsdatafilterformula", "bsdatafilterclass", "cardinitformula", "listinitformula", "treeischecked" }; //

	private String[] str_childFieldNames_compare = new String[] { "itemkey", "itemname", "itemtype", "comboxdesc", "refdesc", "issave", "ismustinput", //
			"loadformula", "editformula", "defaultvalueformula", //
			"showorder", "listwidth", "cardwidth", "listisshowable", "listiseditable", "cardisshowable", "cardiseditable", "iswrap", "itemname_e", //
			"grouptitle", "propisshowable", "propiseditable", "hyperlinkdesc", "showbgcolor", "listishtmlhref", "isquickquerywrap", "isquickqueryshowable", //
			"isquickqueryeditable", "iscommquerywrap", "iscommqueryshowable", "iscommqueryeditable", "querydefaultformula", "querywidth", "isquerymustinput", //
			"iskeeptrace", "workflowiseditable" }; //

	/**
	 * ����Html����..
	 */
	public String getHtmlContent(HashMap map) throws Exception {
		String type = (String) map.get("type");
		String dataSource1 = map.get("datasource1").toString(); //
		String dataSource2 = map.get("datasource2").toString(); //
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

		//������ݿ��в�ͬ������
		if (type.equals("0")) {
			sb_html.append(getNewParentHtml(dataSource1, dataSource2)); //
			sb_html.append(getNewChildHtml(dataSource1, dataSource2)); //
		} else if (type.equals("1")) {
			sb_html.append(getNewParentHtml(dataSource2, dataSource1)); //
			sb_html.append(getNewChildHtml(dataSource2, dataSource1)); //
		} else if (type.equals("2")) {
			sb_html.append(getCompareParentHtml(dataSource1, dataSource2)); //
			sb_html.append(getCompareChildHtml(dataSource1, dataSource2)); //
		}

		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * ������ģ������
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getNewParentHtml(String _dsname_1, String _dsname_2) throws Exception {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		for (int i = 0; i < str_parentFieldNames_new.length; i++) {
			sb_sql.append(str_parentFieldNames_new[i]); //
			if (i != str_parentFieldNames_new.length - 1) { //����������һ��,��Ӹ�����!!
				sb_sql.append(",");
			}
		}

		sb_sql.append(" from pub_templet_1 order by pk_pub_templet_1"); /////
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, sb_sql.toString());
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, sb_sql.toString());

		VectorMap vm_parent_1 = getParentMap(hvs_ds1); //����Դ1���е�����
		VectorMap vm_parent_2 = getParentMap(hvs_ds2); //����Դ2���е�����

		String[] str_keys_1 = vm_parent_1.getKeysAsString(); //����Դ1�����е�key.
		int li_count = 0; //

		StringBuffer sb_htmltable = new StringBuffer(); //���ص�Html.
		sb_htmltable.append("<table>\r\n");
		sb_htmltable.append("<tr><td bgcolor=\"cyan\">���</td><td bgcolor=\"cyan\">ģ�����</td><td bgcolor=\"cyan\">ģ������</td><td bgcolor=\"cyan\">��ѯ����</td><td bgcolor=\"cyan\">�������</td></tr>\r\n"); //
		for (int i = 0; i < str_keys_1.length; i++) {
			if (!vm_parent_2.containsKey(str_keys_1[i])) { //�������Դ2�в�������ģ��
				li_count++; //
				HashVO hvo_data = (HashVO) vm_parent_1.get(str_keys_1[i]); //
				sb_htmltable.append("<tr>");
				sb_htmltable.append("<td>" + li_count + "</td>");
				sb_htmltable.append("<td>" + hvo_data.getStringValue("templetcode", true) + "</td>");
				sb_htmltable.append("<td>" + hvo_data.getStringValue("templetname", true) + "</td>");
				sb_htmltable.append("<td>" + hvo_data.getStringValue("tablename", true) + "</td>");
				sb_htmltable.append("<td>" + hvo_data.getStringValue("savedtablename", true) + "</td>");
				sb_htmltable.append("</tr>\r\n"); //

			}
		}
		sb_htmltable.append("</table>\r\n");

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<font size=2  color=\"blue\">����������Դ��" + _dsname_1 + "��������������Դ��" + _dsname_2 + "���е�ģ��(��" + li_count + "��)</font><br>\r\n"); //
		sb_html.append(sb_htmltable); //
		return sb_html.toString(); //
	}

	/**
	 * �������ӱ�
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getNewChildHtml(String _dsname_1, String _dsname_2) throws Exception {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //

		for (int i = 0; i < str_parentFieldNames_new.length; i++) {
			sb_sql.append("t1." + str_parentFieldNames_new[i] + ","); //
		}

		for (int i = 0; i < str_childFieldNames_new.length; i++) {
			sb_sql.append("t2." + str_childFieldNames_new[i]); //
			if (i != str_childFieldNames_new.length - 1) { //����������һ��,��Ӹ�����!!
				sb_sql.append(",");
			}
		}

		sb_sql.append(" from pub_templet_1 t1,pub_templet_1_item t2 where t1.pk_pub_templet_1=t2.pk_pub_templet_1 order by t2.pk_pub_templet_1_item"); ////
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, sb_sql.toString()); ////
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, sb_sql.toString()); ////

		VectorMap vm_child_1 = getChildMap(hvs_ds1); //
		VectorMap vm_child_2 = getChildMap(hvs_ds2); //

		String[] str_keys_1 = vm_child_1.getKeysAsString(); //
		String[] str_keys_2 = vm_child_2.getKeysAsString(); //

		HashMap map_templetCode_2 = new HashMap(); ///
		for (int i = 0; i < str_keys_2.length; i++) { ////
			int li_pos = str_keys_2[i].indexOf("#"); ////
			if (li_pos > 0) {
				map_templetCode_2.put(str_keys_2[i].substring(0, li_pos), null); //
			}
		}

		StringBuffer sb_htmltable = new StringBuffer(); //
		sb_htmltable.append("<table>\r\n");
		sb_htmltable.append("<tr><td bgcolor=\"cyan\">���</td><td bgcolor=\"cyan\">ģ�����</td><td bgcolor=\"cyan\">ItemKey</td><td bgcolor=\"cyan\">ItemName</td><td bgcolor=\"cyan\">ItemType</td></tr>\r\n"); //
		int li_count = 0; //
		for (int i = 0; i < str_keys_1.length; i++) { //
			if (!vm_child_2.containsKey(str_keys_1[i])) { //�������Դ2�в�����
				int li_pos = str_keys_1[i].indexOf("#"); //
				if (li_pos > 0) {
					String str_templetCode = str_keys_1[i].substring(0, li_pos); //ģ�����..
					if (map_templetCode_2.containsKey(str_templetCode)) { //�������Դ2�д��ڸ�ģ�����,�������Ҷ���,�����Ϊ�����������ӱ�!!
						li_count++; //
						HashVO hvo_data = (HashVO) vm_child_1.get(str_keys_1[i]); //
						sb_htmltable.append("<tr>");
						sb_htmltable.append("<td>" + li_count + "</td>");
						sb_htmltable.append("<td>" + hvo_data.getStringValue("templetcode") + "</td>");
						sb_htmltable.append("<td>" + hvo_data.getStringValue("itemkey", true) + "</td>");
						sb_htmltable.append("<td>" + hvo_data.getStringValue("itemname", true) + "</td>");
						sb_htmltable.append("<td>" + hvo_data.getStringValue("itemtype", true) + "</td>");
						sb_htmltable.append("</tr>\r\n"); //
					}
				}
			}
		}
		sb_htmltable.append("</table>\r\n"); //
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<br><font size=2 color=\"blue\">����������Դ��" + _dsname_1 + "��������������Դ��" + _dsname_2 + "���е���ģ������(��" + li_count + "��)</font><br>\r\n"); //
		sb_html.append(sb_htmltable); //
		return sb_html.toString(); //
	}

	/**
	 * ģ������Ƚϵ�Html
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getCompareParentHtml(String _dsname_1, String _dsname_2) throws Exception {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		for (int i = 0; i < str_parentFieldNames_compare.length; i++) {
			sb_sql.append(str_parentFieldNames_compare[i]); //
			if (i != str_parentFieldNames_compare.length - 1) { //����������һ��,��Ӹ�����!!
				sb_sql.append(",");
			}
		}

		sb_sql.append(" from pub_templet_1 order by pk_pub_templet_1"); /////
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, sb_sql.toString());
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, sb_sql.toString());

		VectorMap vm_parent_1 = getParentMap(hvs_ds1); //����Դ1���е�����
		VectorMap vm_parent_2 = getParentMap(hvs_ds2); //����Դ2���е�����

		String[] str_keys_1 = vm_parent_1.getKeysAsString(); //
		TBUtil tbUtil = new TBUtil(); //
		int li_count = 0; //
		int li_count_item = 0; //
		StringBuilder sb_htmltable = new StringBuilder(); //
		for (int i = 0; i < str_keys_1.length; i++) {
			if (vm_parent_2.containsKey(str_keys_1[i])) { //Ŀ��Դ�������
				HashVO hvo_1 = (HashVO) vm_parent_1.get(str_keys_1[i]); //
				HashVO hvo_2 = (HashVO) vm_parent_2.get(str_keys_1[i]); //
				StringBuilder sb_htmltr = new StringBuilder(); //
				boolean bo_isdif = false; //��ס�Ƿ�������!
				for (int j = 0; j < str_parentFieldNames_compare.length; j++) {
					if (!tbUtil.compareTwoString(hvo_1.getStringValue(str_parentFieldNames_compare[j]), hvo_2.getStringValue(str_parentFieldNames_compare[j]))) { //���������һ���
						li_count_item++; //
						sb_htmltr.append("<tr><td width=30>" + str_parentFieldNames_compare[j] + "</td><td width=300>" + hvo_1.getStringValue(str_parentFieldNames_compare[j], true) + "</td><td width=300>" + hvo_2.getStringValue(str_parentFieldNames_compare[j], true) + "</td></tr>\r\n"); //
						bo_isdif = true; //
					}
				}

				if (bo_isdif) { //�����������!
					li_count++; //
					sb_htmltable.append("<tr><td colspan=3 bgcolor=\"#CCCCCC\"><font size=2>[" + li_count + "]ģ��[" + str_keys_1[i] + "]�в���</font></td></tr>\r\n"); //
					sb_htmltable.append(sb_htmltr.toString()); //
				}
			}
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<font size=2 color=\"blue\">����Դ��" + _dsname_1 + "��������Դ��" + _dsname_2 + "����ģ������������Ĺ�(" + li_count + ")��ģ��,(" + li_count_item + ")����</font><br>\r\n"); //
		sb_html.append("<table>\r\n");
		sb_html.append("<tr><td  bgcolor=\"cyan\">��Ӧ��</td><td bgcolor=\"cyan\" width=300>����Դ��" + _dsname_1 + "��</td><td bgcolor=\"cyan\" width=300>����Դ��" + _dsname_2 + "��</td></tr>\r\n"); //
		sb_html.append(sb_htmltable); //
		sb_html.append("</table>\r\n");
		return sb_html.toString();
	}

	/**
	 * �Ƚ�ģ���ӱ�
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getCompareChildHtml(String _dsname_1, String _dsname_2) throws Exception {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //

		for (int i = 0; i < str_parentFieldNames_compare.length; i++) {
			sb_sql.append("t1." + str_parentFieldNames_compare[i] + ","); //
		}

		for (int i = 0; i < str_childFieldNames_compare.length; i++) {
			sb_sql.append("t2." + str_childFieldNames_compare[i]); //
			if (i != str_childFieldNames_compare.length - 1) { //����������һ��,��Ӹ�����!!
				sb_sql.append(",");
			}
		}

		sb_sql.append(" from pub_templet_1 t1,pub_templet_1_item t2 where t1.pk_pub_templet_1=t2.pk_pub_templet_1 order by t2.pk_pub_templet_1_item"); ////
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, sb_sql.toString()); ////
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, sb_sql.toString()); ////

		VectorMap vm_child_1 = getChildMap(hvs_ds1); //
		VectorMap vm_child_2 = getChildMap(hvs_ds2); //

		String[] str_keys_1 = vm_child_1.getKeysAsString(); //
		TBUtil tbUtil = new TBUtil(); //
		int li_count = 0; //
		int li_count_item = 0; //
		StringBuilder sb_htmltable = new StringBuilder(); //
		for (int i = 0; i < str_keys_1.length; i++) {
			if (vm_child_2.containsKey(str_keys_1[i])) { //Ŀ��Դ�������
				HashVO hvo_1 = (HashVO) vm_child_1.get(str_keys_1[i]); //
				HashVO hvo_2 = (HashVO) vm_child_2.get(str_keys_1[i]); //
				StringBuilder sb_htmltr = new StringBuilder(); //
				boolean bo_isdif = false; //��ס�Ƿ�������!
				for (int j = 0; j < str_childFieldNames_compare.length; j++) {
					if (!tbUtil.compareTwoString(hvo_1.getStringValue(str_childFieldNames_compare[j]), hvo_2.getStringValue(str_childFieldNames_compare[j]))) { //���������һ���
						li_count_item++; //
						sb_htmltr.append("<tr><td width=30>" + str_childFieldNames_compare[j] + "</td><td width=300>" + hvo_1.getStringValue(str_childFieldNames_compare[j], true) + "</td><td width=300>" + hvo_2.getStringValue(str_childFieldNames_compare[j], true) + "</td></tr>\r\n"); //
						bo_isdif = true; //
					}
				}

				if (bo_isdif) { //�����������!
					li_count++; //
					sb_htmltable.append("<tr><td colspan=3 bgcolor=\"#CCCCCC\"><font size=2>[" + li_count + "]ģ����ϸ[" + str_keys_1[i] + "]�в���</font></td></tr>\r\n"); //
					sb_htmltable.append(sb_htmltr.toString()); //
				}
			}
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<br><font size=2 color=\"blue\">����Դ��" + _dsname_1 + "��������Դ��" + _dsname_2 + "����ģ����ϸ��������Ĺ�(" + li_count + ")����ϸ,(" + li_count_item + ")����</font><br>\r\n"); //
		sb_html.append("<table>\r\n");
		sb_html.append("<tr><td  bgcolor=\"cyan\">��ϸ��Ӧ��</td><td bgcolor=\"cyan\" width=300>����Դ��" + _dsname_1 + "��</td><td bgcolor=\"cyan\" width=300>����Դ��" + _dsname_2 + "��</td></tr>\r\n"); //
		sb_html.append(sb_htmltable); //
		sb_html.append("</table>\r\n");
		return sb_html.toString();
	}


	private String getHrefHtml(String _ds1, String _ds2, String _ischild, String _templetcode, String _itemkey, String _fieldname) {
		StringBuffer sb_href = new StringBuffer(); //
		sb_href.append("<a href=\""); //
		sb_href.append("./QuickUpdateTempletServlet?type=updatepub_templet"); ///
		sb_href.append("&ds1=" + _ds1); //
		sb_href.append("&ds2=" + _ds2); //
		sb_href.append("&ischild=" + _ischild); //
		sb_href.append("&templetcode=" + _templetcode); //
		sb_href.append("&itemkey=" + _itemkey); //
		sb_href.append("&fieldname=" + _fieldname); //
		sb_href.append("\">��[" + _ds1 + "]Ϊ׼</a>"); //

		sb_href.append("&nbsp;&nbsp;"); //

		sb_href.append("<a href=\""); //
		sb_href.append("./QuickUpdateTempletServlet?type=updatepub_templet"); ///
		sb_href.append("&ds1=" + _ds2); //
		sb_href.append("&ds2=" + _ds1); //
		sb_href.append("&ischild=" + _ischild); //
		sb_href.append("&templetcode=" + _templetcode); //
		sb_href.append("&itemkey=" + _itemkey); //
		sb_href.append("&fieldname=" + _fieldname); //
		sb_href.append("\">��[" + _ds2 + "]Ϊ׼</a>"); //

		return sb_href.toString(); //
	}

	private String convertStr(String _str) {
		if (_str == null) {
			return "";
		}
		return _str;
	}

	/**
	 * ���������ϣ��
	 * @param _hvs
	 * @return
	 */
	private VectorMap getParentMap(HashVO[] _hvs) {
		VectorMap map_parent_1 = new VectorMap(); //
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue("templetcode") != null) {
				String str_templetcode = _hvs[i].getStringValue("templetcode").toLowerCase(); //ģ�����
				map_parent_1.put(str_templetcode, _hvs[i]); //
			}
		}

		return map_parent_1;
	}

	/**
	 * �����ӱ�Ĺ�ϣ��
	 * @param _hvs
	 * @return
	 */
	private VectorMap getChildMap(HashVO[] hvs_ds1) {
		VectorMap map_child_1 = new VectorMap(); //
		for (int i = 0; i < hvs_ds1.length; i++) { //
			if (hvs_ds1[i].getStringValue("templetcode") != null && hvs_ds1[i].getStringValue("itemkey") != null) {
				String str_templetcode = hvs_ds1[i].getStringValue("templetcode").toLowerCase(); //ģ�����
				String str_itemkey = hvs_ds1[i].getStringValue("itemkey").toLowerCase(); //itemkey
				String str_span_templetCode_itemkey = str_templetcode + "#" + str_itemkey; //
				map_child_1.put(str_span_templetCode_itemkey, hvs_ds1[i]); //
			}
		}

		return map_child_1;
	}

}
