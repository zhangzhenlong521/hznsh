package cn.com.infostrategy.bs.mdata;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * Ԫ�����е�BS�˵�Util��!!!
 * @author Administrator
 *
 */
public class MetaDataBSUtil {

	/**
	 * ����SQL,�����Զ������!!
	 * @param _className
	 * @param _itemValue
	 * @param _otherConditions
	 * @param _otherSQL
	 * @return
	 * @throws Exception
	 */
	public String getBillQueryPanelSQLCustCreate(String _className, String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception {
		IBillQueryPanelSQLCreateIFC creater = (IBillQueryPanelSQLCreateIFC) Class.forName(_className).newInstance(); //����ʵ��!!
		return creater.getSQLByCondition(_itemKey, _itemValue, _allItemValues, _allItemSQLs, _wholeSQL); //
	}

	//���ð�ťȨ��!!!
	public void setWltBtnAllow(ButtonDefineVO _btndfo, String _loginUserId, HashMap _sharePoolMap) throws Exception {
		if (_btndfo.getAllowifcbyinit() == null && _btndfo.getAllowroles() == null && _btndfo.getAllowusers() == null) { //���һ��û����,��ֱ�ӷ���!!!
			_btndfo.setAllowed(true); //���ʲô������,��Ĭ��Ϊ����Ч��!
			_btndfo.appendAllowResult("��Ϊû�ж����κ�Ȩ��,����ֱ����Ч!!\r\n");
			return;
		}

		//�ȴ�����Ա!!
		TBUtil tbUtil = new TBUtil(); //
		if (_btndfo.getAllowusers() != null) {
			String[] str_ids = tbUtil.split(_btndfo.getAllowusers(), ";"); //
			boolean iswhiteList = true; //�Ƿ������
			if (_btndfo.getAllowusertype() != null && _btndfo.getAllowusertype().equals("������")) {
				iswhiteList = false; //������
			}

			if (iswhiteList) { //����ǰ��������ж�
				if (tbUtil.isExistInArray(_loginUserId, str_ids)) { //�����¼��Ա�ڰ�������,��������Ч,�������˳�!!!
					_btndfo.setAllowed(true); //���ʲô������,��Ĭ��Ϊ����Ч��!
					_btndfo.appendAllowResult("��Ϊ����������Ա����������,����ֱ����Ч,�������������У��!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("��������Ա����������,���Ҳ���У��ʧ��,������������һ��У��!!\r\n");
				}
			} else { //����Ǻ�����
				if (!tbUtil.isExistInArray(_loginUserId, str_ids)) { //�����¼��Ա�ڰ�������,��������Ч,�������˳�!!!
					_btndfo.setAllowed(true); //���ʲô������,��Ĭ��Ϊ����Ч��!
					_btndfo.appendAllowResult("��Ϊ����������Ա����������,����ֱ����Ч!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("��������Ա����������,���Ҳ���У��ʧ��!\r\n");
				}
			}
		} else {
			_btndfo.appendAllowResult("û�ж�����Ա����Ȩ��,����!\r\n");
		}

		//��ɫ����
		if (_btndfo.getAllowroles() != null) { //�����ɫ������
			String[] str_ids = tbUtil.split(_btndfo.getAllowroles(), ";"); //������Ľ�ɫ!!
			if (!_sharePoolMap.containsKey("�ҵ����н�ɫ")) { //���û��ȡ��ɫ,��ȡһ��!����Ϊ���������,����ÿ�ζ�ȡ!!!
				_sharePoolMap.put("�ҵ����н�ɫ", new CommDMO().getStringArrayFirstColByDS(null, "select roleid from pub_user_role where userid='" + _loginUserId + "'")); ////
			}

			String[] str_myRoleIds = (String[]) _sharePoolMap.get("�ҵ����н�ɫ"); //
			boolean iswhiteList = true; //�Ƿ������??
			if (_btndfo.getAllowroletype() != null && _btndfo.getAllowroletype().equals("������")) {
				iswhiteList = false; //������
			}

			if (iswhiteList) { //����ǰ��������ж�
				if (tbUtil.containTwoArrayCompare(str_ids, str_myRoleIds)) {
					_btndfo.setAllowed(true); //���ʲô������,��Ĭ��Ϊ����Ч��!
					_btndfo.appendAllowResult("��Ϊ�������˽�ɫ����������,����ֱ����Ч,�������������У��!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("�����˽�ɫ����������,���Ҳ���У��ʧ��,������������һ��У��!!\r\n");
				}
			} else {
				if (!tbUtil.containTwoArrayCompare(str_ids, str_myRoleIds)) {
					_btndfo.setAllowed(true); //���ʲô������,��Ĭ��Ϊ����Ч��!
					_btndfo.appendAllowResult("��Ϊ�������˽�ɫ����������,����ֱ����Ч!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("�����˽�ɫ����������,���Ҳ���У��ʧ��!!\r\n");
				}
			}
		} else {
			_btndfo.appendAllowResult("û�ж����ɫ����Ȩ��,����!\r\n");
		}

		//�����ʼ������������Ϊ��,�����֮
		if (_btndfo.getAllowifcbyinit() != null) { //
			try {
				WLTButtonInitAllowValidateIfc allowIfc = (WLTButtonInitAllowValidateIfc) Class.forName(_btndfo.getAllowifcbyinit()).newInstance(); //
				boolean isAllowByIfc = allowIfc.allowValieDate(_btndfo, _loginUserId, _sharePoolMap); //
				String str_validateinfo = allowIfc.getAllowInfo(); //
				if (isAllowByIfc) { //
					_btndfo.appendAllowResult("��Ϊ��������������[" + _btndfo.getAllowifcbyinit() + "]��У��,������Ч,��Чԭ��[" + str_validateinfo + "]!\r\n");
					_btndfo.setAllowed(true); //��Ϊʧ���߷���!!!!
					return; //
				} else {
					_btndfo.appendAllowResult("������������[" + _btndfo.getAllowifcbyinit() + "]У��,���Ҳ���У��ʧ��,ʧ��ԭ��[" + str_validateinfo + "]!\r\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		} else {
			_btndfo.appendAllowResult("û�ж�������������Ȩ��,����!\r\n");
		}

		_btndfo.appendAllowResult("���ź�,���е�У���ж�ʧ��,��Ȩ����!\r\n");
		_btndfo.setAllowed(false); //��Ϊʧ���߷���!!!!
	}

	/**
	 * ������ݿ���XML���Ƿ����ĳ��ģ��!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap checkDBAndRegistXMLHaveTemplet(HashMap _parMap) throws Exception {
		String str_templetCode = (String) _parMap.get("templetcode"); //
		HashMap returnMap = new HashMap(); //
		//�ȼ�����ݿ�!!
		boolean isDBHave = false; //
		String str_count = new CommDMO().getStringValueByDS(null, "select count(*) c1 from pub_templet_1 where templetcode='" + str_templetCode + "'"); //
		int li_count = Integer.parseInt(str_count); //
		if (li_count > 0) { //����ҵ�!
			isDBHave = true;
		} else {
			isDBHave = false;
		}

		//�����Դ!��ƽ̨���Ʒ������!!
		boolean isXMLHave = false; //
		String[][] str_allinstallpackage = new BSUtil().getAllInstallPackages("/templetdata"); //
		String xmlfileName = null;
		ArrayList list_xmlfile = new ArrayList();
		for (int i = 0; i < str_allinstallpackage.length; i++) {
			String str_xmlfileName = str_allinstallpackage[i][0] + str_templetCode + ".xml"; //ת��ȥ��XML
			java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName); //
			if (fileUrl != null) { //�����ȷ�������Դ!!!��ȥȡ!
				isXMLHave = true; //�ҵ���!
				xmlfileName = str_xmlfileName;
				list_xmlfile.add(str_allinstallpackage[i][1]);
			}

		}
		if (xmlfileName != null) {
			returnMap.put("XMLPATH", xmlfileName);// ����Դ���С�WebPushƽ̨���Ϲ��Ʒ���ڿز�Ʒ����Ŀ�����򷵻ص�ģ��·�������ȼ��� ��Ŀ>�ڿز�Ʒ>�Ϲ��Ʒ>WebPushƽ̨�����/2012-07-18��
			if (list_xmlfile.size() > 1) {
				returnMap.put("XMLINFO", list_xmlfile.remove(list_xmlfile.size() - 1) + ":" + xmlfileName + "(" + list_xmlfile.toString().substring(1, list_xmlfile.toString().length() - 1) + "��Ҳ�и�ģ��)"); //����ж��ģ�壬��Ҫ��ʾһ�¡����/2012-07-18��
			} else {
				returnMap.put("XMLINFO", xmlfileName);
			}
		}

		//��������!!!
		returnMap.put("ISDBHAVE", isDBHave); //
		returnMap.put("ISXMLHAVE", isXMLHave); //XML���Ƿ���
		return returnMap; //
	}

	/**
	 * ɾ��ĳ��ģ��!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap deleteOneTempletByCode(HashMap _parMap) throws Exception {
		String str_templetCode = (String) _parMap.get("templetcode"); //
		CommDMO commDMO = new CommDMO(); //
		String str_pkid = commDMO.getStringValueByDS(null, "select pk_pub_templet_1 from pub_templet_1 where templetcode='" + str_templetCode + "'"); //
		if (str_pkid != null) {
			String str_delsql_1 = "delete from pub_templet_1      where pk_pub_templet_1='" + str_pkid + "'"; //
			String str_delsql_2 = "delete from pub_templet_1_item where pk_pub_templet_1='" + str_pkid + "'"; //
			commDMO.executeBatchByDS(null, new String[] { str_delsql_1, str_delsql_2 }); //
		}
		return null; //
	}

	/** ÿ��Item���嵽HashMap����
	String itemKey;//�ֶ�key
	 String itemName;//�ֶ�Name
	 String itemType;//�ؼ�����
	 String displayType;//ȫ�л���У�1.���� 2.ȫ��
	 String strValue;//�ַ�������һ����ά����(Ҫ��ʾ������)
	 */
	private List<HashMap<String, String>> getCardReportItems(Pub_Templet_1VO template, BillVO vo) {
		String[] itemKeys = template.getItemKeys();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < itemKeys.length; i++) {
			String itemKey = itemKeys[i];
			String exportType = template.getItemVo(itemKey).getCardisexport();
			if ((exportType == null) || (!exportType.equals("0"))) {//�������ָ���˲���������Ĭ�Ͼ����������exportType��null,Ĭ�ϰ��е���
				HashMap<String, String> itemMap = getDealOneItemMap(template, vo, itemKey);
				list.add(itemMap);
			}
		}
		return list;
	}

	/** ÿ��Item���嵽HashMap����
	String itemKey;//�ֶ�key
	 String itemName;//�ֶ�Name
	 String itemType;//�ؼ�����
	 String displayType;//ȫ�л���У�1.���� 2.ȫ��
	 String strValue;//�ַ�������һ����ά����(Ҫ��ʾ������)
	 */
	private HashMap<String, String> getDealOneItemMap(Pub_Templet_1VO template, BillVO vo, String itemKey) {
		Pub_Templet_1_ItemVO temp_1_itemvo = template.getItemVo(itemKey);
		itemKey = temp_1_itemvo.getItemkey();
		String itemName = temp_1_itemvo.getItemname();
		String itemType = temp_1_itemvo.getItemtype();//�ؼ�����
		String displayType = temp_1_itemvo.getCardisexport();//ȫ�л���У�1.���� 2.ȫ�У����NULL��Ĭ�ϰ��д���

		HashMap map = new HashMap();
		map.put("itemKey", itemKey);
		map.put("itemName", itemName);
		map.put("itemType", itemType);

		if (displayType == null) {//���NULL��Ĭ�ϰ��д���
			displayType = "1";
		}
		map.put("displayType", displayType);

		String strValue;//��Ҫ��ʾ���ַ�������Ҫ���ݿؼ��������ж�

		//���ݿؼ���������,ȷ����ʾ�����ݣ�dataToDisplay��valueHtml��
		if (itemType.endsWith("����")) {
			if (vo.getRefItemVOValue(itemKey) == null || vo.getRefItemVOValue(itemKey).getName() == null) {
				strValue = "";
			} else {
				strValue = vo.getRefItemVOValue(itemKey).getName();
			}
		} else if (itemType.equals(WLTConstants.COMP_CHECKBOX)) {//ѡ���
			strValue = vo.getStringValue(itemKey, "");
			if (((String) strValue).equalsIgnoreCase("N")) {
				strValue = "��";
			} else if (((String) strValue).equalsIgnoreCase("Y")) {
				strValue = "��";
			}
		} else if (itemType.equals(WLTConstants.COMP_COMBOBOX)) {//������
			if (vo.getComBoxItemVOValue(itemKey) == null || vo.getComBoxItemVOValue(itemKey).getName() == null) {
				strValue = "";
			} else {
				strValue = vo.getComBoxItemVOValue(itemKey).getName();
			}
		} else if (itemType.equals(WLTConstants.COMP_BUTTON)) {//��ť
			strValue = "����ť��";
		} else if ((itemType.equals(WLTConstants.COMP_LINKCHILD)) || (itemType.equals(WLTConstants.COMP_IMPORTCHILD))) {//�����ӱ�͵����ӱ�
			StringBuilder subTableSB = new StringBuilder();
			Object[] listInfo = (Object[]) vo.getUserObject(itemKey);
			BillVO[] vos = (BillVO[]) listInfo[0];
			if (vos != null && vos.length > 0) {//����ӱ�������
				Pub_Templet_1VO childTemplate = (Pub_Templet_1VO) listInfo[1];
				String[] keys = childTemplate.getItemKeys();
				ArrayList headers = new ArrayList();
				ArrayList showKeys = new ArrayList();

				for (int i = 0; i < keys.length; i++) {
					Pub_Templet_1_ItemVO itemVo = childTemplate.getItemVo(keys[i]);
					if (itemVo.getListisshowable()) {
						headers.add(itemVo.getItemname());
						showKeys.add(itemVo.getItemkey());
					}
				}
				if (headers.size() > 0) {
					for (int i = 0; i < headers.size(); i++) {
						subTableSB.append(headers.get(i) + ";");
					}
					subTableSB.deleteCharAt(subTableSB.length() - 1).append("\r\n");
					for (int j = 0; j < vos.length; j++) {
						for (int i = 0; i < headers.size(); i++) {
							String key = (String) showKeys.get(i);
							Object objvalue = vos[j].getObject(key);
							String value = "";
							if (objvalue instanceof ComBoxItemVO) {
								value = ((ComBoxItemVO) objvalue).getName();

							} else if (objvalue instanceof RefItemVO) {
								value = ((RefItemVO) objvalue).getName();
							} else {
								value = vos[j].getStringValue(key);
							}
							subTableSB.append(value + ";");
						}
						subTableSB.deleteCharAt(subTableSB.length() - 1).append("\r\n");
					}
					subTableSB.append("\r\n");
				}
				strValue = subTableSB.toString();
			} else {//����ӱ�������
				strValue = "";
			}

		} else if (itemType.equals(WLTConstants.COMP_FILECHOOSE)) {//�ļ�ѡ���
			if (vo.getRefItemVOValue(itemKey) != null) {
				String fileId = vo.getRefItemVOValue(itemKey).getId();
				String fileName = vo.getRefItemVOValue(itemKey).getName();
				strValue = fileName;
			} else {
				strValue = "";
			}
		} else if (itemType.equals(WLTConstants.COMP_OFFICE)) {//office�ؼ�
			if (vo.getRefItemVOValue(itemKey) != null) {
				String fileId = vo.getRefItemVOValue(itemKey).getId();
				String fileName = vo.getRefItemVOValue(itemKey).getName();
				strValue = fileName;
			} else {
				strValue = "";
			}
		} else {
			strValue = vo.getStringValue(itemKey, "");
		}
		map.put("strValue", strValue);
		return map;

	}

	/**
	 * �õ�����ģ��(���ж���ĸ����еĵ�����ʽ)����Billvo�õ�HTML
	 * @param templetVO
	 * @param billVO
	 * @return
	 */
	public String getReportHtml(Pub_Templet_1VO templetVO, BillVO billVO) {
		List<HashMap<String, String>> itemList = getCardReportItems(templetVO, billVO);
		StringBuilder sb = new StringBuilder();
		//����htmlͷ
		sb.append("<html>\r\n"); //
		sb.append("<head>\r\n"); //
		sb.append("<title>BillCard����</title>\r\n"); //
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n"); //
		//sb_html.append("<LINK href=\"./applet/exportHTML.css\" type=text/css rel=stylesheet />\r\n"); //
		sb.append("<style type=\"text/css\" src=\"/WEB-INF/classes/test.css\" />\r\n"); //
		sb.append("<style type=\"text/css\">\r\n"); //
		sb.append("<!--\r\n"); //
		sb.append(".Style_TRTD  { border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:0px;border-left-width:0px;font-size:12px; }\r\n"); //
		sb.append(".Style_Compent { word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb.append(".Style_CompentLabel {background: #EEEEEE;word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb.append(".Style_CompentValue { word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb.append("#list{border:1px solid;border-collapse: collapse;table-layout: fixed;}\r\n");
		sb.append("#list th{background: #EEEEEE;font-weight: normal}\r\n");
		sb.append("#list th,td{border:1px solid;font-size:12px;}\r\n");
		//sb_html.append("table {width:100%;}\r\n");
		sb.append("fieldset legend {\r\n");
		sb.append("    font-size:12px;\r\n");
		sb.append("}\r\n");
		sb.append("-->\r\n"); //
		sb.append("</style>\r\n"); //
		sb.append("<script type=text/javascript>                   \r\n"); //  
		sb.append("<!--                                            \r\n"); //  
		sb.append("function setFolding(controler, itemName){       \r\n"); //  
		sb.append("    var items = document.getElementsByTagName('tr');\r\n");//
		sb.append("    var str = controler.innerText;              \r\n"); //  
		sb.append("    str = str.substring(1,str.length);		    \r\n"); //  
		sb.append("    for(var i=0;i<items.length;i++) {           \r\n"); //  
		sb.append("	    var item = items[i];                    \r\n"); // 
		sb.append("        if(item.name != itemName) {             \r\n"); // 
		sb.append("            continue;                           \r\n"); // 
		sb.append("        }                                       \r\n"); // 
		sb.append("	    if(item.style.display==\"none\") {      \r\n"); //  
		sb.append("		   item.style.display=\"\";		        \r\n"); //  
		sb.append("		   controler.innerText= \"-\" + str;    \r\n"); //  
		sb.append("		} else {							    \r\n"); //  
		sb.append("			item.style.display=\"none\";	    \r\n"); //  
		sb.append("			controler.innerText= \"+\" + str;	\r\n"); //  
		sb.append("		}									    \r\n"); //  
		sb.append("	}										    \r\n"); //  
		sb.append("}											    \r\n"); //  
		sb.append("-->											    \r\n"); //  
		sb.append("</script>									    \r\n"); //  
		sb.append("</head>										    \r\n"); //  
		sb.append("<body>");
		sb.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		//ƴ��HTML
		for (int i = 0; i < itemList.size(); i++) {
			HashMap<String, String> map = itemList.get(i);
			String itemKey = map.get("itemKey");
			String itemName = map.get("itemName");
			String strValue = map.get("strValue").replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>");//HTML����
			String displayType = map.get("displayType");
			String tdWidth = "400";
			if (displayType.equals("1")) {
				tdWidth = "168";
			}
			sb.append("<tr  name=\"\">");
			sb.append("\r\n");
			sb.append("<td class=\"Style_TRTD\">");
			sb.append("\r\n");
			sb.append("<table style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">");
			sb.append("\r\n");
			sb.append("<tr>");
			sb.append("\r\n");
			sb.append("<td class=\"Style_CompentLabel\" width=\"120\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
			sb.append("\r\n");
			sb.append("<font color=\"#333333\">" + itemName + " </font>");
			sb.append("\r\n");
			sb.append("</td>");
			sb.append("\r\n");
			sb.append("<td class=\"Style_CompentValue\" width=\"" + tdWidth + "\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
			sb.append("\r\n");
			sb.append(strValue);
			sb.append("\r\n");
			sb.append("</td>");
			if (displayType.equals("1")) {//����ǰ��У����ж���һ��Item�Ƿ��ǰ��У�����ǣ�ֱ���ù������ں��档��������ǣ�����û����һ��Item�����������һ�������ݵİ���
				if (i == itemList.size() - 1) {//��ǰItemʱ���һ��
					sb.append("<td class=\"Style_CompentValue\" width=\"288\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
					sb.append("</td>");
				} else {
					HashMap<String, String> map2 = itemList.get(i + 1);
					String itemKey2 = map2.get("itemKey");
					String itemName2 = map2.get("itemName");
					String strValue2 = map2.get("strValue").replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>");//HTML����
					String displayType2 = map2.get("displayType");
					if (displayType2.equals("1")) {//�ж���һ��Item�Ƿ��ǰ��У�����ǣ�ֱ���ù������ں���
						sb.append("<td class=\"Style_CompentLabel\" width=\"120\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
						sb.append("\r\n");
						sb.append("<font color=\"#333333\">" + itemName2 + " </font>");
						sb.append("\r\n");
						sb.append("</td>");
						sb.append("\r\n");
						sb.append("<td class=\"Style_CompentValue\" width=\"" + tdWidth + "\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
						sb.append("\r\n");
						sb.append(strValue2);
						sb.append("\r\n");
						sb.append("</td>");
						i++;
					} else {
						sb.append("<td class=\"Style_CompentValue\" width=\"288\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
						sb.append("</td>");
					}
				}
			}
			sb.append("\r\n");
			sb.append("</tr>");
			sb.append("\r\n");
			sb.append("</table>");
			sb.append("\r\n");
			sb.append("</td>");
			sb.append("\r\n");
			sb.append("</tr>");

		}
		//֮���Լ�������У�����Ϊ����Χ���û���±߿�
		sb.append("<tr>");
		sb.append("<td class=\"Style_TRTD\">");
		sb.append("<table style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("</table>");
		sb.append("</body></html>");
		return sb.toString();
	}

	/**
	 * �õ�����ģ��(���ж���ĸ����еĵ�����ʽ)����Billvo�õ�word�ļ�
	 * @param templetVO
	 * @param billVO
	 * @return
	 */
	public byte[] getCardReportWord(Pub_Templet_1VO templetVO, BillVO billVO) throws Exception {
		Document document = new Document(PageSize.A4);// ����ֽ�Ŵ�С
		String tempWordPath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
		File tempWord = new File(tempWordPath + "/temp.doc");
		FileOutputStream os = new FileOutputStream(tempWord);
		RtfWriter2.getInstance(document, os);// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������ 
		document.open();
		Table table = new Table(4);
		table.setAlignment(Table.ALIGN_CENTER);
		List<HashMap<String, String>> itemList = this.getCardReportItems(templetVO, billVO);
		for (int i = 0; i < itemList.size(); i++) {
			HashMap<String, String> map = itemList.get(i);
			String itemName = map.get("itemName");
			String strValue = map.get("strValue");
			String displayType = map.get("displayType");
			if (displayType.equals("2")) {
				Cell nameCell = new Cell(itemName);
				nameCell.setBackgroundColor(Color.LIGHT_GRAY);
				nameCell.setVerticalAlignment(Cell.ALIGN_CENTER);
				Cell valueCell = new Cell(strValue);
				valueCell.setColspan(3);
				table.addCell(nameCell);
				table.addCell(valueCell);
			} else if (displayType.equals("1")) {
				//����ǰ��У����ж���һ��Item�Ƿ��ǰ��У�����ǣ�ֱ���ù������ں��档��������ǣ�����û����һ��Item�����������һ�������ݵİ���
				if (i == itemList.size() - 1) {//��ǰItemʱ���һ��
					Cell nameCell = new Cell(itemName);
					nameCell.setBackgroundColor(Color.LIGHT_GRAY);
					nameCell.setVerticalAlignment(Cell.ALIGN_CENTER);
					Cell valueCell = new Cell(strValue);
					Cell spaceCell = new Cell("");
					spaceCell.setColspan(2);
					table.addCell(nameCell);
					table.addCell(valueCell);
					table.addCell(spaceCell);
				} else {
					HashMap<String, String> map2 = itemList.get(i + 1);
					String itemName2 = map2.get("itemName");
					String strValue2 = map2.get("strValue");
					String displayType2 = map2.get("displayType");
					if (displayType2.equals("1")) {//�ж���һ��Item�Ƿ��ǰ��У�����ǣ�ֱ���ù������ں���
						Cell nameCell = new Cell(itemName);
						nameCell.setBackgroundColor(Color.LIGHT_GRAY);
						nameCell.setVerticalAlignment(Cell.ALIGN_CENTER);
						Cell valueCell = new Cell(strValue);
						table.addCell(nameCell);
						table.addCell(valueCell);

						Cell nameCell2 = new Cell(itemName2);
						nameCell2.setBackgroundColor(Color.LIGHT_GRAY);
						nameCell2.setVerticalAlignment(Cell.ALIGN_CENTER);
						Cell valueCell2 = new Cell(strValue2);
						table.addCell(nameCell2);
						table.addCell(valueCell2);
						i++;
					} else {
						Cell nameCell = new Cell(itemName);
						nameCell.setBackgroundColor(Color.LIGHT_GRAY);
						nameCell.setVerticalAlignment(Cell.ALIGN_CENTER);
						Cell valueCell = new Cell(strValue);
						Cell spaceCell = new Cell("");
						spaceCell.setColspan(2);
						table.addCell(nameCell);
						table.addCell(valueCell);
						table.addCell(spaceCell);
					}
				}

			}
		}
		table.setPadding(10f);
		document.add(table);
		document.close();
		os.flush();
		os.close();
		FileInputStream is = new FileInputStream(tempWord);
		byte[] bytes = new TBUtil().readFromInputStreamToBytes(is);
		is.close();
		tempWord.delete();
		return bytes;
	}
	
	/**
	 * ȡ��bs��ʹ�õ����й�ʽ
	 * @param paramMap
	 * @return
	 */
	public HashMap getJepBsFunctionName(HashMap paramMap) throws Exception {
		HashMap rtnMap = new HashMap();
		JepFormulaParseAtBS jepAtBs = new JepFormulaParseAtBS();
		JepFormulaParseAtBS jepAtBs2 = new JepFormulaParseAtBS(true);
		String[] allBsFuntionName = (String[])jepAtBs.getParser().getFunctionTable().keySet().toArray(new String[0]);
		String[] allBsFuntionName2 = (String[])jepAtBs2.getParser().getFunctionTable().keySet().toArray(new String[0]);
		rtnMap.put("allBsFunctionName", allBsFuntionName);
		rtnMap.put("allBsFunctionName2", allBsFuntionName);
		return rtnMap;
	}

}
