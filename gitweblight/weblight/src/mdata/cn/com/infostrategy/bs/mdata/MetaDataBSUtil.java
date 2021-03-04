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
 * 元数据中的BS端的Util类!!!
 * @author Administrator
 *
 */
public class MetaDataBSUtil {

	/**
	 * 创建SQL,根据自定义的类!!
	 * @param _className
	 * @param _itemValue
	 * @param _otherConditions
	 * @param _otherSQL
	 * @return
	 * @throws Exception
	 */
	public String getBillQueryPanelSQLCustCreate(String _className, String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception {
		IBillQueryPanelSQLCreateIFC creater = (IBillQueryPanelSQLCreateIFC) Class.forName(_className).newInstance(); //创建实例!!
		return creater.getSQLByCondition(_itemKey, _itemValue, _allItemValues, _allItemSQLs, _wholeSQL); //
	}

	//设置按钮权限!!!
	public void setWltBtnAllow(ButtonDefineVO _btndfo, String _loginUserId, HashMap _sharePoolMap) throws Exception {
		if (_btndfo.getAllowifcbyinit() == null && _btndfo.getAllowroles() == null && _btndfo.getAllowusers() == null) { //如果一个没定义,则直接返回!!!
			_btndfo.setAllowed(true); //如果什么都不设,则默认为是有效的!
			_btndfo.appendAllowResult("因为没有定义任何权限,所以直接有效!!\r\n");
			return;
		}

		//先处理人员!!
		TBUtil tbUtil = new TBUtil(); //
		if (_btndfo.getAllowusers() != null) {
			String[] str_ids = tbUtil.split(_btndfo.getAllowusers(), ";"); //
			boolean iswhiteList = true; //是否白名单
			if (_btndfo.getAllowusertype() != null && _btndfo.getAllowusertype().equals("黑名单")) {
				iswhiteList = false; //黑名单
			}

			if (iswhiteList) { //如果是白名单则判断
				if (tbUtil.isExistInArray(_loginUserId, str_ids)) { //如果登录人员在白名单中,则设置有效,并立即退出!!!
					_btndfo.setAllowed(true); //如果什么都不设,则默认为是有效的!
					_btndfo.appendAllowResult("因为我满足了人员白名单定义,所以直接有效,忽略下面的其他校验!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("定义了人员白名单分配,但我参与校验失败,将继续进行下一种校验!!\r\n");
				}
			} else { //如果是黑名单
				if (!tbUtil.isExistInArray(_loginUserId, str_ids)) { //如果登录人员在白名单中,则设置有效,并立即退出!!!
					_btndfo.setAllowed(true); //如果什么都不设,则默认为是有效的!
					_btndfo.appendAllowResult("因为我满足了人员黑名单定义,所以直接有效!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("定义了人员白名单分配,但我参与校验失败!\r\n");
				}
			}
		} else {
			_btndfo.appendAllowResult("没有定义人员类型权限,跳过!\r\n");
		}

		//角色处理
		if (_btndfo.getAllowroles() != null) { //如果角色定义了
			String[] str_ids = tbUtil.split(_btndfo.getAllowroles(), ";"); //所定义的角色!!
			if (!_sharePoolMap.containsKey("我的所有角色")) { //如果没有取角色,则取一把!这是为了提高性能,以免每次都取!!!
				_sharePoolMap.put("我的所有角色", new CommDMO().getStringArrayFirstColByDS(null, "select roleid from pub_user_role where userid='" + _loginUserId + "'")); ////
			}

			String[] str_myRoleIds = (String[]) _sharePoolMap.get("我的所有角色"); //
			boolean iswhiteList = true; //是否白名单??
			if (_btndfo.getAllowroletype() != null && _btndfo.getAllowroletype().equals("黑名单")) {
				iswhiteList = false; //黑名单
			}

			if (iswhiteList) { //如果是白名单则判断
				if (tbUtil.containTwoArrayCompare(str_ids, str_myRoleIds)) {
					_btndfo.setAllowed(true); //如果什么都不设,则默认为是有效的!
					_btndfo.appendAllowResult("因为我满足了角色白名单定义,所以直接有效,忽略下面的其他校验!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("定义了角色白名单分配,但我参与校验失败,将继续进行下一种校验!!\r\n");
				}
			} else {
				if (!tbUtil.containTwoArrayCompare(str_ids, str_myRoleIds)) {
					_btndfo.setAllowed(true); //如果什么都不设,则默认为是有效的!
					_btndfo.appendAllowResult("因为我满足了角色黑名单定义,所以直接有效!!\r\n");
					return; //
				} else {
					_btndfo.appendAllowResult("定义了角色黑名单分配,但我参与校验失败!!\r\n");
				}
			}
		} else {
			_btndfo.appendAllowResult("没有定义角色类型权限,跳过!\r\n");
		}

		//如果初始化的拦截器不为空,则计算之
		if (_btndfo.getAllowifcbyinit() != null) { //
			try {
				WLTButtonInitAllowValidateIfc allowIfc = (WLTButtonInitAllowValidateIfc) Class.forName(_btndfo.getAllowifcbyinit()).newInstance(); //
				boolean isAllowByIfc = allowIfc.allowValieDate(_btndfo, _loginUserId, _sharePoolMap); //
				String str_validateinfo = allowIfc.getAllowInfo(); //
				if (isAllowByIfc) { //
					_btndfo.appendAllowResult("因为我满足了拦截器[" + _btndfo.getAllowifcbyinit() + "]的校验,所以有效,有效原因[" + str_validateinfo + "]!\r\n");
					_btndfo.setAllowed(true); //作为失败者返回!!!!
					return; //
				} else {
					_btndfo.appendAllowResult("定义了拦截器[" + _btndfo.getAllowifcbyinit() + "]校验,但我参与校验失败,失败原因[" + str_validateinfo + "]!\r\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		} else {
			_btndfo.appendAllowResult("没有定义拦截器类型权限,跳过!\r\n");
		}

		_btndfo.appendAllowResult("很遗憾,所有的校验中都失败,无权访问!\r\n");
		_btndfo.setAllowed(false); //作为失败者返回!!!!
	}

	/**
	 * 检查数据库与XML中是否具有某个模板!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap checkDBAndRegistXMLHaveTemplet(HashMap _parMap) throws Exception {
		String str_templetCode = (String) _parMap.get("templetcode"); //
		HashMap returnMap = new HashMap(); //
		//先检查数据库!!
		boolean isDBHave = false; //
		String str_count = new CommDMO().getStringValueByDS(null, "select count(*) c1 from pub_templet_1 where templetcode='" + str_templetCode + "'"); //
		int li_count = Integer.parseInt(str_count); //
		if (li_count > 0) { //如果找到!
			isDBHave = true;
		} else {
			isDBHave = false;
		}

		//检查资源!从平台与产品依次找!!
		boolean isXMLHave = false; //
		String[][] str_allinstallpackage = new BSUtil().getAllInstallPackages("/templetdata"); //
		String xmlfileName = null;
		ArrayList list_xmlfile = new ArrayList();
		for (int i = 0; i < str_allinstallpackage.length; i++) {
			String str_xmlfileName = str_allinstallpackage[i][0] + str_templetCode + ".xml"; //转向去找XML
			java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName); //
			if (fileUrl != null) { //如果的确有这个资源!!!则去取!
				isXMLHave = true; //找到了!
				xmlfileName = str_xmlfileName;
				list_xmlfile.add(str_allinstallpackage[i][1]);
			}

		}
		if (xmlfileName != null) {
			returnMap.put("XMLPATH", xmlfileName);// 如资源中有【WebPush平台、合规产品、内控产品、项目】，则返回的模板路径的优先级： 项目>内控产品>合规产品>WebPush平台【李春娟/2012-07-18】
			if (list_xmlfile.size() > 1) {
				returnMap.put("XMLINFO", list_xmlfile.remove(list_xmlfile.size() - 1) + ":" + xmlfileName + "(" + list_xmlfile.toString().substring(1, list_xmlfile.toString().length() - 1) + "中也有该模板)"); //如果有多个模板，需要提示一下【李春娟/2012-07-18】
			} else {
				returnMap.put("XMLINFO", xmlfileName);
			}
		}

		//返回数据!!!
		returnMap.put("ISDBHAVE", isDBHave); //
		returnMap.put("ISXMLHAVE", isXMLHave); //XML中是否有
		return returnMap; //
	}

	/**
	 * 删除某个模板!!
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

	/** 每个Item定义到HashMap里面
	String itemKey;//字段key
	 String itemName;//字段Name
	 String itemType;//控件类型
	 String displayType;//全列或半列：1.半列 2.全列
	 String strValue;//字符串，或一个二维数组(要显示的数据)
	 */
	private List<HashMap<String, String>> getCardReportItems(Pub_Templet_1VO template, BillVO vo) {
		String[] itemKeys = template.getItemKeys();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < itemKeys.length; i++) {
			String itemKey = itemKeys[i];
			String exportType = template.getItemVo(itemKey).getCardisexport();
			if ((exportType == null) || (!exportType.equals("0"))) {//如果不是指定了不导出，则默认均导出，如果exportType是null,默认半列导出
				HashMap<String, String> itemMap = getDealOneItemMap(template, vo, itemKey);
				list.add(itemMap);
			}
		}
		return list;
	}

	/** 每个Item定义到HashMap里面
	String itemKey;//字段key
	 String itemName;//字段Name
	 String itemType;//控件类型
	 String displayType;//全列或半列：1.半列 2.全列
	 String strValue;//字符串，或一个二维数组(要显示的数据)
	 */
	private HashMap<String, String> getDealOneItemMap(Pub_Templet_1VO template, BillVO vo, String itemKey) {
		Pub_Templet_1_ItemVO temp_1_itemvo = template.getItemVo(itemKey);
		itemKey = temp_1_itemvo.getItemkey();
		String itemName = temp_1_itemvo.getItemname();
		String itemType = temp_1_itemvo.getItemtype();//控件类型
		String displayType = temp_1_itemvo.getCardisexport();//全列或半列：1.半列 2.全列，如果NULL，默认半列处理

		HashMap map = new HashMap();
		map.put("itemKey", itemKey);
		map.put("itemName", itemName);
		map.put("itemType", itemType);

		if (displayType == null) {//如果NULL，默认半列处理
			displayType = "1";
		}
		map.put("displayType", displayType);

		String strValue;//需要显示的字符串，需要根据控件类型来判断

		//根据控件类型来看,确定显示的数据（dataToDisplay，valueHtml）
		if (itemType.endsWith("参照")) {
			if (vo.getRefItemVOValue(itemKey) == null || vo.getRefItemVOValue(itemKey).getName() == null) {
				strValue = "";
			} else {
				strValue = vo.getRefItemVOValue(itemKey).getName();
			}
		} else if (itemType.equals(WLTConstants.COMP_CHECKBOX)) {//选择框
			strValue = vo.getStringValue(itemKey, "");
			if (((String) strValue).equalsIgnoreCase("N")) {
				strValue = "否";
			} else if (((String) strValue).equalsIgnoreCase("Y")) {
				strValue = "是";
			}
		} else if (itemType.equals(WLTConstants.COMP_COMBOBOX)) {//下拉框
			if (vo.getComBoxItemVOValue(itemKey) == null || vo.getComBoxItemVOValue(itemKey).getName() == null) {
				strValue = "";
			} else {
				strValue = vo.getComBoxItemVOValue(itemKey).getName();
			}
		} else if (itemType.equals(WLTConstants.COMP_BUTTON)) {//按钮
			strValue = "【按钮】";
		} else if ((itemType.equals(WLTConstants.COMP_LINKCHILD)) || (itemType.equals(WLTConstants.COMP_IMPORTCHILD))) {//引用子表和导入子表
			StringBuilder subTableSB = new StringBuilder();
			Object[] listInfo = (Object[]) vo.getUserObject(itemKey);
			BillVO[] vos = (BillVO[]) listInfo[0];
			if (vos != null && vos.length > 0) {//如果子表有数据
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
			} else {//如果子表无数据
				strValue = "";
			}

		} else if (itemType.equals(WLTConstants.COMP_FILECHOOSE)) {//文件选择框
			if (vo.getRefItemVOValue(itemKey) != null) {
				String fileId = vo.getRefItemVOValue(itemKey).getId();
				String fileName = vo.getRefItemVOValue(itemKey).getName();
				strValue = fileName;
			} else {
				strValue = "";
			}
		} else if (itemType.equals(WLTConstants.COMP_OFFICE)) {//office控件
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
	 * 得到根据模板(其中定义的各个列的导出方式)，和Billvo得到HTML
	 * @param templetVO
	 * @param billVO
	 * @return
	 */
	public String getReportHtml(Pub_Templet_1VO templetVO, BillVO billVO) {
		List<HashMap<String, String>> itemList = getCardReportItems(templetVO, billVO);
		StringBuilder sb = new StringBuilder();
		//生成html头
		sb.append("<html>\r\n"); //
		sb.append("<head>\r\n"); //
		sb.append("<title>BillCard导出</title>\r\n"); //
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
		//拼接HTML
		for (int i = 0; i < itemList.size(); i++) {
			HashMap<String, String> map = itemList.get(i);
			String itemKey = map.get("itemKey");
			String itemName = map.get("itemName");
			String strValue = map.get("strValue").replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>");//HTML换行
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
			if (displayType.equals("1")) {//如果是半列，则判断下一个Item是否是半列，如果是，直接拿过来放在后面。（如果不是，或者没有下一个Item）这里则放入一个无数据的半列
				if (i == itemList.size() - 1) {//当前Item时最后一个
					sb.append("<td class=\"Style_CompentValue\" width=\"288\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">");
					sb.append("</td>");
				} else {
					HashMap<String, String> map2 = itemList.get(i + 1);
					String itemKey2 = map2.get("itemKey");
					String itemName2 = map2.get("itemName");
					String strValue2 = map2.get("strValue").replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>");//HTML换行
					String displayType2 = map2.get("displayType");
					if (displayType2.equals("1")) {//判断下一个Item是否是半列，如果是，直接拿过来放在后面
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
		//之所以加这个空行，是因为最外围表格没有下边框
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
	 * 得到根据模板(其中定义的各个列的导出方式)，和Billvo得到word文件
	 * @param templetVO
	 * @param billVO
	 * @return
	 */
	public byte[] getCardReportWord(Pub_Templet_1VO templetVO, BillVO billVO) throws Exception {
		Document document = new Document(PageSize.A4);// 设置纸张大小
		String tempWordPath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
		File tempWord = new File(tempWordPath + "/temp.doc");
		FileOutputStream os = new FileOutputStream(tempWord);
		RtfWriter2.getInstance(document, os);// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中 
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
				//如果是半列，则判断下一个Item是否是半列，如果是，直接拿过来放在后面。（如果不是，或者没有下一个Item）这里则放入一个无数据的半列
				if (i == itemList.size() - 1) {//当前Item时最后一个
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
					if (displayType2.equals("1")) {//判断下一个Item是否是半列，如果是，直接拿过来放在后面
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
	 * 取得bs端使用的所有公式
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
