package cn.com.infostrategy.bs.common;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

/**
 * Java转XML的工具类,
 * 为了统一管理和以后可能要做一个通用的导入导出xml的方法，将原元数据实现类FrameWorkMetaDataServiceImpl中的导入导出xml格式的方法转到本类中
 * 
 * @author xch
 *
 */
public class XMLIOUtil {
	private CommDMO commdmo = new CommDMO();

	/** 
	  * 把java的可序列化的对象(实现Serializable接口)
	  * 序列化保存到XML文件里面,如果想一次保存多个
	  * 可序列化对象请用集合进行封装 
	  * 保存时将会用现在的对象原来的XML文件内容 
	  * @param obj 要序列化的可序列化的对象 
	  * @param _xmlFileName 带完全的保存路径的文件名  
	  * @throws FileNotFoundException 指定位置的文件不存在 
	  * @throws IOException 输出时发生异常 
	  * @throws Exception 其他运行时异常 
	  */
	public void writeObjToXML(Object obj, String _xmlFileName) throws FileNotFoundException, IOException, Exception {
		FileOutputStream fos = new FileOutputStream(new File(_xmlFileName), false); //创建文件输出流
		XMLEncoder encoder = new XMLEncoder(fos); //创建XML文件对象输出类实例 
		encoder.writeObject(obj); //对象序列化输出到XML文件 
		encoder.flush(); //关闭序列化工具 
		encoder.close(); //关闭输出流 
		fos.close();
	}

	/**
	 * 将一个对象导出成XML字符串.
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public String exportObjToXMLString(Object obj) throws Exception {
		ByteArrayOutputStream bo = new ByteArrayOutputStream(); //二进制输出流
		XMLEncoder encoder = new XMLEncoder(bo); //创建XML文件对象输出类实例 
		encoder.writeObject(obj); //对象序列化输出到XML文件 
		encoder.flush(); //关闭序列化工具 
		encoder.close(); //关闭输出流 

		byte[] bytes = bo.toByteArray(); //
		String str_xml = new String(bytes, "UTF-8"); //
		bo.close();
		return str_xml; //
	}

	/** 
	 * 读取由objSource指定的XML文件中的序列化保存的对象,返回的结果经过了List封装 
	 * @param _xmlFileName 带全部文件路径的文件全名 
	 * @return 由XML文件里面保存的对象构成的List列表(可能是一个或者多个的序列化保存的对象)   
	 * @throws FileNotFoundException 指定的对象读取资源不存在 
	 * @throws IOException 读取发生错误 
	 * @throws Exception 其他运行时异常发生 
	 */
	public Object loadObjFromXML(String _xmlFileName) throws FileNotFoundException, IOException, Exception {
		FileInputStream fis = new FileInputStream(new File(_xmlFileName));
		XMLDecoder decoder = new XMLDecoder(fis);
		Object obj = decoder.readObject(); //
		fis.close();
		decoder.close();
		return obj;
	}

	/**
	 * 导入XML格式模版
	 */
	public void importXMLTemplet(String str_dataSource, String textarea) throws Exception {
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets = doc.getRootElement().getChildren(); // 遍历所有模板
		for (int i = 0; i < al_billtemplets.size(); i++) {
			org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象

			// 处理主表信息
			InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_templet_1"); //
			System.out.println(">>>>>>>>>>>>>>>"+node_billtemplet.getChild("TEMPLETCODE").getText());
			commdmo.executeUpdateByDS(str_dataSource, "delete from pub_templet_1_item where pk_pub_templet_1 in (select pk_pub_templet_1 from pub_templet_1 where templetcode = '" + node_billtemplet.getChild("templetcode").getText() + "')");
			commdmo.executeUpdateByDS(str_dataSource, "delete from pub_templet_1 where templetcode ='" + node_billtemplet.getChild("templetcode").getText() + "'");
			String pk_pub_templet_1 = new CommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1");
			isql_1.putFieldValue("pk_pub_templet_1", pk_pub_templet_1);
			isql_1.putFieldValue("templetcode", node_billtemplet.getChild("templetcode").getText());
			isql_1.putFieldValue("templetname", node_billtemplet.getChild("templetname").getText());
			isql_1.putFieldValue("datasourcename", node_billtemplet.getChild("datasourcename").getText());
			isql_1.putFieldValue("tablename", node_billtemplet.getChild("tablename").getText());
			isql_1.putFieldValue("dataconstraint", node_billtemplet.getChild("dataconstraint").getText());
			isql_1.putFieldValue("pkname", node_billtemplet.getChild("pkname").getText());
			isql_1.putFieldValue("pksequencename", node_billtemplet.getChild("pksequencename").getText());
			isql_1.putFieldValue("savedtablename", node_billtemplet.getChild("savedtablename").getText());
			isql_1.putFieldValue("cardcustpanel", node_billtemplet.getChild("cardcustpanel").getText());
			isql_1.putFieldValue("listcustpanel", node_billtemplet.getChild("listcustpanel").getText());
			isql_1.putFieldValue("templetname_e", node_billtemplet.getChild("templetname_e").getText());
			isql_1.putFieldValue("cardwidth", node_billtemplet.getChild("cardwidth").getText());
			isql_1.putFieldValue("isshowlistpagebar", node_billtemplet.getChild("isshowlistpagebar").getText());
			isql_1.putFieldValue("isshowlistopebar", node_billtemplet.getChild("isshowlistopebar").getText());
			isql_1.putFieldValue("isshowcardborder", node_billtemplet.getChild("isshowcardborder").getText());
			isql_1.putFieldValue("cardborder", node_billtemplet.getChild("cardborder").getText());
			isql_1.putFieldValue("treepk", node_billtemplet.getChild("treepk").getText());
			isql_1.putFieldValue("treeparentpk", node_billtemplet.getChild("treeparentpk").getText());
			isql_1.putFieldValue("propbeanclassname", node_billtemplet.getChild("propbeanclassname").getText());
			isql_1.putFieldValue("cardlayout", node_billtemplet.getChild("cardlayout").getText());
			isql_1.putFieldValue("treeviewfield", node_billtemplet.getChild("treeviewfield").getText());
			isql_1.putFieldValue("treeseqfield", node_billtemplet.getChild("treeseqfield").getText());
			isql_1.putFieldValue("treeisshowroot", node_billtemplet.getChild("treeisshowroot").getText());
			isql_1.putFieldValue("isshowlistquickquery", node_billtemplet.getChild("isshowlistquickquery").getText());
			isql_1.putFieldValue("isshowlistcustbtn", node_billtemplet.getChild("isshowlistcustbtn").getText());
			isql_1.putFieldValue("listcustbtndesc", node_billtemplet.getChild("listcustbtndesc").getText());
			isql_1.putFieldValue("templetbtns", node_billtemplet.getChild("templetbtns").getText());
			isql_1.putFieldValue("listrowheight", node_billtemplet.getChild("listrowheight").getText());
			isql_1.putFieldValue("listheaderisgroup", node_billtemplet.getChild("listheaderisgroup").getText());
			isql_1.putFieldValue("isshowcardcustbtn", node_billtemplet.getChild("isshowcardcustbtn").getText());
			isql_1.putFieldValue("ordercondition", node_billtemplet.getChild("ordercondition").getText());
			isql_1.putFieldValue("treeisonlyone", node_billtemplet.getChild("treeisonlyone").getText());
			isql_1.putFieldValue("treeisshowtoolbar", node_billtemplet.getChild("treeisshowtoolbar").getText());
			isql_1.putFieldValue("islistautorowheight", node_billtemplet.getChild("islistautorowheight").getText());
			isql_1.putFieldValue("cardcustbtndesc", node_billtemplet.getChild("cardcustbtndesc").getText());
			isql_1.putFieldValue("treecustbtndesc", node_billtemplet.getChild("treecustbtndesc").getText());
			isql_1.putFieldValue("bsdatafilterformula", node_billtemplet.getChild("bsdatafilterformula").getText());
			isql_1.putFieldValue("bsdatafilterclass", node_billtemplet.getChild("bsdatafilterclass").getText());
			isql_1.putFieldValue("cardinitformula", node_billtemplet.getChild("cardinitformula").getText());
			isql_1.putFieldValue("listinitformula", node_billtemplet.getChild("listinitformula").getText());
			al.add(isql_1);
			// UIUtil.executeUpdateByDS(null, sb_sql.toString());

			// 得到所有子表信息
			java.util.List al_templet_item = node_billtemplet.getChildren("templet_item"); // 得到所有子表信息
			for (int j = 0; j < al_templet_item.size(); j++) {
				org.jdom.Element node_item = (org.jdom.Element) al_templet_item.get(j); // 模板的各个明细
				// insert 子表
				String pk_pub_templet_1_item = new CommDMO().getSequenceNextValByDS(str_dataSource, "S_PUB_TEMPLET_1_ITEM");

				InsertSQLBuilder isql_item = new InsertSQLBuilder("pub_templet_1_item"); //
				isql_item.putFieldValue("pk_pub_templet_1_item", pk_pub_templet_1_item);
				isql_item.putFieldValue("pk_pub_templet_1", pk_pub_templet_1);
				isql_item.putFieldValue("itemkey", node_item.getChild("itemkey").getText());
				isql_item.putFieldValue("itemname", node_item.getChild("itemname").getText());
				isql_item.putFieldValue("itemtype", node_item.getChild("itemtype").getText());
				isql_item.putFieldValue("comboxdesc", node_item.getChild("comboxdesc").getText());
				isql_item.putFieldValue("refdesc", node_item.getChild("refdesc").getText());
				isql_item.putFieldValue("issave", node_item.getChild("issave").getText());
				isql_item.putFieldValue("ismustinput", node_item.getChild("ismustinput").getText());
				isql_item.putFieldValue("loadformula", node_item.getChild("loadformula").getText());
				isql_item.putFieldValue("editformula", node_item.getChild("editformula").getText());
				isql_item.putFieldValue("defaultvalueformula", node_item.getChild("defaultvalueformula").getText());
				isql_item.putFieldValue("showorder", node_item.getChild("showorder").getText());
				isql_item.putFieldValue("listwidth", node_item.getChild("listwidth").getText());
				isql_item.putFieldValue("cardwidth", node_item.getChild("cardwidth").getText());
				isql_item.putFieldValue("listisshowable", node_item.getChild("listisshowable").getText());
				isql_item.putFieldValue("listiseditable", node_item.getChild("listiseditable").getText());
				isql_item.putFieldValue("cardisshowable", node_item.getChild("cardisshowable").getText());
				isql_item.putFieldValue("cardiseditable", node_item.getChild("cardiseditable").getText());
				isql_item.putFieldValue("iswrap", node_item.getChild("iswrap").getText());
				isql_item.putFieldValue("itemname_e", node_item.getChild("itemname_e").getText());
				isql_item.putFieldValue("grouptitle", node_item.getChild("grouptitle").getText());
				isql_item.putFieldValue("propisshowable", node_item.getChild("propisshowable").getText());
				isql_item.putFieldValue("propiseditable", node_item.getChild("propiseditable").getText());
				isql_item.putFieldValue("hyperlinkdesc", node_item.getChild("hyperlinkdesc").getText());
				isql_item.putFieldValue("showbgcolor", node_item.getChild("showbgcolor").getText());
				isql_item.putFieldValue("listishtmlhref", node_item.getChild("listishtmlhref").getText());
				isql_item.putFieldValue("isquickquerywrap", node_item.getChild("isquickquerywrap").getText());
				isql_item.putFieldValue("isquickqueryshowable", node_item.getChild("isquickqueryshowable").getText());
				isql_item.putFieldValue("isquickqueryeditable", node_item.getChild("isquickqueryeditable").getText());
				isql_item.putFieldValue("iscommquerywrap", node_item.getChild("iscommquerywrap").getText());
				isql_item.putFieldValue("iscommqueryshowable", node_item.getChild("iscommquerywrap").getText());
				isql_item.putFieldValue("iscommqueryeditable", node_item.getChild("iscommqueryeditable").getText());
				isql_item.putFieldValue("querydefaultformula", node_item.getChild("querydefaultformula").getText());
				isql_item.putFieldValue("querywidth", node_item.getChild("querywidth").getText());
				isql_item.putFieldValue("isquerymustinput", node_item.getChild("isquerymustinput").getText());
				isql_item.putFieldValue("iskeeptrace", node_item.getChild("iskeeptrace").getText());
				isql_item.putFieldValue("workflowiseditable", node_item.getChild("workflowiseditable").getText());
				isql_item.putFieldValue("pushuser", node_item.getChild("pushuser").getText());
				isql_item.putFieldValue("queryitemtype", node_item.getChild("queryitemtype").getText());
				isql_item.putFieldValue("queryitemdefine", node_item.getChild("queryitemdefine").getText());
				isql_item.putFieldValue("isrefcanedit", node_item.getChild("isrefcanedit").getText());
				isql_item.putFieldValue("isuniquecheck", node_item.getChild("isuniquecheck").getText());
				isql_item.putFieldValue("itemtiptext", node_item.getChild("itemtiptext").getText());
				isql_item.putFieldValue("querycreatetype", node_item.getChild("querycreatetype").getText());
				isql_item.putFieldValue("querycreatecustdef", node_item.getChild("querycreatecustdef").getText());
				al.add(isql_item);
			}
		}
		commdmo.executeBatchByDS(str_dataSource, al);
	}

	/**
	 * 往模板子表中中插入数据
	 */
	public String insertTempletItem(String _templetId, String[][] _items) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		HashMap mapAlready = commDMO.getHashMapBySQLByDS(null, "select itemkey,itemname from pub_templet_1_item where pk_pub_templet_1='" + _templetId + "'", false, true, false); //
		ArrayList al_sqls = new ArrayList(); //
		int li_count = 0; //
		for (int i = 0; i < _items.length; i++) {
			if (mapAlready.containsKey(_items[i][0].toLowerCase())) { //如果已有该key了,则跳过!
				continue; //
			}

			InsertSQLBuilder isql = new InsertSQLBuilder("pub_templet_1_item"); //
			String str_newId = commDMO.getSequenceNextValByDS(null, "S_PUB_TEMPLET_1_ITEM"); //主键
			isql.putFieldValue("pk_pub_templet_1_item", str_newId); //
			isql.putFieldValue("pk_pub_templet_1", _templetId); //模板主表主键
			isql.putFieldValue("itemkey", _items[i][0]); //itemkey
			isql.putFieldValue("itemname", _items[i][1]); //itemkey
			isql.putFieldValue("itemtype", "文本框");
			isql.putFieldValue("issave", "N"); //不参与保存
			isql.putFieldValue("iswrap", "Y"); //是否换行
			isql.putFieldValue("listwidth", "120"); //列表宽度
			isql.putFieldValue("cardwidth", "120"); //卡片宽度
			isql.putFieldValue("listiseditable", "4"); //列表是否可编辑
			isql.putFieldValue("cardiseditable", "4"); //列表是否可编辑
			isql.putFieldValue("listisshowable", _items[i][2]); //列表是否显示
			isql.putFieldValue("cardisshowable", _items[i][2]); //卡片是否显示
			if (_items[i][0].equalsIgnoreCase("prins_mylastsubmittime")) { //如果是我的最后处理时间
				isql.putFieldValue("loadformula", "setItemValue(\"prins_mylastsubmittime\",getMapStrItemValue(getItemValue(\"prins_mylastsubmittime\"),getLoginUserId()));"); //定义加载公式
			}
			if (_items[i][0].equalsIgnoreCase("prins_mylastsubmitmsg")) { //如果是我的最后处理意见
				isql.putFieldValue("loadformula", "setItemValue(\"prins_mylastsubmitmsg\",getMapStrItemValue(getItemValue(\"prins_mylastsubmitmsg\"),getLoginUserId()));"); //定义加载公式
			}
			isql.putFieldValue("showorder", 200 + (i + 1)); //排序,为了保证排在最后,从200开始!
			al_sqls.add(isql.getSQL()); ////送入!!!
			li_count++; //
		}
		commDMO.executeBatchByDS(null, al_sqls); //实际插入数据库!!
		return "共插入[" + li_count + "]个字段!"; //
	}

	/**
	 * 导出某个模板成xml,以前的机制都是硬写代码,所以不够通用!!! 关键是随着模板表结构的变化,这里还需要修改代码! 扩展性与灵活性不够!!
	 * 最好的办法是要能做到以下几点:
	 * 1.模板表结构变化了,这里不需代码!!
	 * 2.导出的XML与导入的实际表结构之间发生差异时,永远不报错,即导入时先计算下目标库中有几个字段! 然后交叉匹配!!
	 * 3.主键能自动处理,这是最重要也是最难的! 单表还是很好处理的(比如指定一个序列名),多表关联时,需要一个很好的机制! 表明之间的关联,主要就是子表的外键如何表达?
	 * 4.既支持纯导入,也支持动态导入
	 * 5.支持反复导入(可以先不考虑) 
	 * @param str_templete_code
	 * @param selectrows
	 * @param str_sourceDS
	 * @return
	 * @throws Exception
	 */
	public String exportXMLTemplet(String[] str_templete_code, int[] selectrows, String str_sourceDS) throws Exception {
		CommDMO commdmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < selectrows.length; i++) {
			String str_templet = str_templete_code[i];// billlistpanel.getValueAt(selectrows[i],
			// "TEMPLETCODE").toString();
			// //
			//HashVO[] hashvo = commdmo.getHashVoArrayByDS(str_sourceDS, "select * from pub_templet_1 where pk_pub_templet_1='" + str_templet + "'");
			HashVO[] hashvo = commdmo.getHashVoArrayByDS(str_sourceDS, "select * from pub_templet_1 where templetcode='" + str_templet + "'");
			if (hashvo.length > 0) {
				String[] str_allparentcolumns = hashvo[0].getKeys(); //
				for (int j = 0; j < hashvo.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo[j].getStringValue("templetcode") + "******************************** -->\r\n"); //      
					sb_xml.append("<billtemplet>\r\n"); //              
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns.length; q++) {
						String parent_content = hashvo[j].getStringValue(str_allparentcolumns[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						}
					}

					// 得到所有子表信息
					HashVO[] hashvo1 = commdmo.getHashVoArrayByDS(str_sourceDS, "select * from pub_templet_1_item where pk_pub_templet_1='" + hashvo[j].getStringValue("pk_pub_templet_1") + "' order by showorder");

					if (hashvo1.length > 0) {
						String[] str_allcolumns = hashvo1[0].getKeys(); //
						for (int k = 0; k < hashvo1.length; k++) {
							sb_xml.append("  <templet_item showorder=\"" + hashvo1[k].getStringValue("showorder") + "\">\r\n"); //
							for (int r = 0; r < str_allcolumns.length; r++) {
								String item_content = hashvo1[k].getStringValue(str_allcolumns[r]);
								if (item_content != null && (item_content.indexOf(">") >= 0 || item_content.indexOf("<") >= 0)) {
									sb_xml.append("    <" + str_allcolumns[r].toLowerCase() + "><![CDATA[" + (item_content == null ? "" : item_content) + "]]></" + str_allcolumns[r].toLowerCase() + ">\r\n"); //
								} else {
									sb_xml.append("    <" + str_allcolumns[r].toLowerCase() + ">" + (item_content == null ? "" : item_content) + "</" + str_allcolumns[r].toLowerCase() + ">\r\n"); //
								}
							}
							sb_xml.append("  </templet_item>\r\n"); //
							if (k != hashvo1.length - 1) {
								sb_xml.append("\r\n");
							}
						}
					}
					sb_xml.append("</billtemplet>"); //
					sb_xml.append("\r\n"); //
				}
			}
			if (i != selectrows.length - 1) {
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 导入XML格式注册按钮
	 */
	public void importXMLRegButton(String textarea) throws Exception {
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets = doc.getRootElement().getChildren("refbutton"); // 遍历所有注册按钮
		for (int i = 0; i < al_billtemplets.size(); i++) {
			org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象
			// 处理主表信息
			commdmo.executeUpdateByDS(null, "delete from pub_regbuttons where code ='" + node_billtemplet.getChild("code").getText() + "'");
			String pub_regbuttons_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_regbuttons");
			InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_regbuttons");

			sb_sql.putFieldValue("id", pub_regbuttons_id);
			sb_sql.putFieldValue("code", node_billtemplet.getChild("code").getText());
			sb_sql.putFieldValue("btnimg", node_billtemplet.getChild("btnimg").getText());
			sb_sql.putFieldValue("btntext", node_billtemplet.getChild("btntext").getText());
			sb_sql.putFieldValue("btntooltiptext", node_billtemplet.getChild("btntooltiptext").getText());
			sb_sql.putFieldValue("btndescr", node_billtemplet.getChild("btndescr").getText());
			sb_sql.putFieldValue("btntype", node_billtemplet.getChild("btntype").getText());
			sb_sql.putFieldValue("clickingformula", node_billtemplet.getChild("clickingformula").getText());
			sb_sql.putFieldValue("clickedformula", node_billtemplet.getChild("clickedformula").getText());
			sb_sql.putFieldValue("allowroles", node_billtemplet.getChild("allowroles").getText());
			sb_sql.putFieldValue("allowusers", node_billtemplet.getChild("allowusers").getText());
			al.add(sb_sql);
		}
		commdmo.executeBatchByDS(null, al);
	}

	/**
	 * 导入XML格式注册样板
	 */
	public void importXMLRegFormat(String textarea) throws Exception {
		try {
			CommDMO commdmo = new CommDMO();
			ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
			org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
			ArrayList al = new ArrayList();
			java.util.List al_billtemplets = doc.getRootElement().getChildren("regformat"); // 遍历所有模板
			for (int i = 0; i < al_billtemplets.size(); i++) {
				org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象
				// 处理主表信息
				String sql = "delete from pub_regformatpanel where code = '" + node_billtemplet.getChild("code").getText() + "'";
				commdmo.executeUpdateByDS(null, sql);
				// insert 主表
				String pk_pub_templet_1 = new CommDMO().getSequenceNextValByDS(null, "s_pub_regformatpanel");

				InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_regformatpanel");
				sb_sql.putFieldValue("id", pk_pub_templet_1);
				sb_sql.putFieldValue("code", node_billtemplet.getChild("code").getText());
				sb_sql.putFieldValue("formatformula", node_billtemplet.getChild("formatformula").getText());
				sb_sql.putFieldValue("descr", node_billtemplet.getChild("descr").getText());
				sb_sql.putFieldValue("suggestwidth", node_billtemplet.getChild("suggestwidth").getText());
				sb_sql.putFieldValue("suggestheight", node_billtemplet.getChild("suggestheight").getText());
				sb_sql.putFieldValue("eventbindformula", node_billtemplet.getChild("eventbindformula").getText());
				sb_sql.putFieldValue("regformula1", node_billtemplet.getChild("regformula1").getText());
				sb_sql.putFieldValue("regformula2", node_billtemplet.getChild("regformula2").getText());
				sb_sql.putFieldValue("regformula3", node_billtemplet.getChild("regformula3").getText());
				al.add(sb_sql);
			}
			commdmo.executeBatchByDS(null, al);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入XML格式注册参照
	 */
	public void importXMLRegRef(String textarea) throws Exception {
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets = doc.getRootElement().getChildren("refregister"); // 遍历所有注册按钮
		for (int i = 0; i < al_billtemplets.size(); i++) {
			org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象
			// 处理主表信息
			commdmo.executeUpdateByDS(null, "delete from pub_refregister where id ='" + node_billtemplet.getChild("id").getText() + "'");
			String pub_regref_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_refregister");

			InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_refregister");
			sb_sql.putFieldValue("id", pub_regref_id);
			sb_sql.putFieldValue("reftype", node_billtemplet.getChild("reftype").getText());
			sb_sql.putFieldValue("name", node_billtemplet.getChild("name").getText());
			sb_sql.putFieldValue("refdefine", node_billtemplet.getChild("refdefine").getText());
			sb_sql.putFieldValue("descr", node_billtemplet.getChild("descr").getText());
			al.add(sb_sql);
		}
		commdmo.executeBatchByDS(null, al);
	}

	/**
	 * 通用导入XML程序 武坤萌 2008-11-21
	 */
	public void importXML(String text) throws Exception {
		CommDMO commdmo = new CommDMO();
		String ID = null;
		HashVO[] newId = null;
		java.util.List list = new java.util.ArrayList();

		InsertSQLBuilder sql = null;
		ByteArrayInputStream byin = new ByteArrayInputStream(text.getBytes("GBK"));
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin);
		java.util.List exportAnyBill = doc.getRootElement().getChildren("exportanybill");
		System.out.println("-----------------------------------处理中------------------------------------");
		for (int i = 0; i < exportAnyBill.size(); i++) {

			sql = new InsertSQLBuilder("pub_billcelltemplet_h");
			org.jdom.Element node_billtemplet_p = (org.jdom.Element) exportAnyBill.get(i);
			if (node_billtemplet_p.getAttributeValue("type").equals("EXCEL模板")) {// getChild("templetcode")
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("exceltemplet");
				list.add("delete from pub_billcelltemplet_h where templetcode='" + node_billtemplet.getChild("templetcode").getText() + "'");
				newId = commdmo.getHashVoArrayByDS(null, "select * from pub_billcelltemplet_h where templetcode='" + node_billtemplet.getChild("templetcode").getText() + "'");
				if (newId != null && newId.length != 0) {
					list.add("delete from pub_billcelltemplet_d where templet_h_id in " + getID(newId, null));
				}
				ID = commdmo.getSequenceNextValByDS(null, "S_pub_billcelltemplet_h");
				sql.putFieldValue("id", ID);
				sql.putFieldValue("templetcode", node_billtemplet.getChild("templetcode").getText());
				sql.putFieldValue("templetname", node_billtemplet.getChild("templetname").getText());
				sql.putFieldValue("descr", node_billtemplet.getChild("descr").getText());
				sql.putFieldValue("billno", node_billtemplet.getChild("billno").getText());
				sql.putFieldValue("rowlength", node_billtemplet.getChild("rowlength").getText());
				sql.putFieldValue("collength", node_billtemplet.getChild("collength").getText());
				list.add(sql);
				java.util.List node = node_billtemplet.getChildren("templet_item");
				for (int j = 0; j < node.size(); j++) {
					org.jdom.Element node_show = (org.jdom.Element) node.get(j);

					sql = new InsertSQLBuilder("pub_billcelltemplet_d");
					sql.putFieldValue("id", commdmo.getSequenceNextValByDS(null, "S_pub_billcelltemplet_d"));
					sql.putFieldValue("templet_h_id", ID);
					sql.putFieldValue("cellrow", node_show.getChild("cellrow").getText());
					sql.putFieldValue("cellcol", node_show.getChild("cellcol").getText());
					sql.putFieldValue("cellvalue", node_show.getChild("cellvalue").getText());
					sql.putFieldValue("foreground", node_show.getChild("foreground").getText());
					sql.putFieldValue("background", node_show.getChild("background").getText());
					sql.putFieldValue("fonttype", node_show.getChild("fonttype").getText());
					sql.putFieldValue("fontstyle", node_show.getChild("fontstyle").getText());
					sql.putFieldValue("fontsize", node_show.getChild("fontsize").getText());
					sql.putFieldValue("span", node_show.getChild("span").getText());
					sql.putFieldValue("rowheight", node_show.getChild("rowheight").getText());
					sql.putFieldValue("colwidth", node_show.getChild("colwidth").getText());
					sql.putFieldValue("cellhelp", node_show.getChild("cellhelp").getText());
					sql.putFieldValue("celltype", node_show.getChild("celltype").getText());
					sql.putFieldValue("iseditable", node_show.getChild("iseditable").getText());
					sql.putFieldValue("cellkey", node_show.getChild("cellkey").getText());
					sql.putFieldValue("celldesc", node_show.getChild("celldesc").getText());
					sql.putFieldValue("valign", node_show.getChild("valign").getText());
					sql.putFieldValue("halign", node_show.getChild("halign").getText());
					sql.putFieldValue("loadformula", node_show.getChild("loadformula").getText());
					sql.putFieldValue("editformula", node_show.getChild("editformula").getText());
					sql.putFieldValue("validateformula", node_show.getChild("validateformula").getText());
					list.add(sql);
				}

			} else if (node_billtemplet_p.getAttributeValue("type").equals("元源模板")) {// 处理元源模板
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("billtemplet");
				list.add("delete from pub_templet_1 where templetcode='" + node_billtemplet.getChild("templetcode").getText() + "'");
				newId = commdmo.getHashVoArrayByDS(null, "select * from pub_templet_1 where templetcode='" + node_billtemplet.getChild("templetcode").getText() + "'");
				if (newId != null && newId.length != 0) {
					list.add("delete from pub_templet_1_item where pk_pub_templet_1 in " + getID(newId, "pk_pub_templet_1"));
				}
				InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_templet_1");

				String pk_pub_templet_1 = new CommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1");// 获取ID
				sb_sql.putFieldValue("pk_pub_templet_1", pk_pub_templet_1);
				sb_sql.putFieldValue("templetcode", node_billtemplet.getChild("templetcode").getText());
				sb_sql.putFieldValue("templetname", node_billtemplet.getChild("templetname").getText());
				sb_sql.putFieldValue("datasourcename", node_billtemplet.getChild("datasourcename").getText());
				sb_sql.putFieldValue("tablename", node_billtemplet.getChild("tablename").getText());
				sb_sql.putFieldValue("dataconstraint", node_billtemplet.getChild("dataconstraint").getText());
				sb_sql.putFieldValue("pkname", node_billtemplet.getChild("pkname").getText());
				sb_sql.putFieldValue("pksequencename", node_billtemplet.getChild("pksequencename").getText());
				sb_sql.putFieldValue("savedtablename", node_billtemplet.getChild("savedtablename").getText());
				sb_sql.putFieldValue("cardcustpanel", node_billtemplet.getChild("cardcustpanel").getText());
				sb_sql.putFieldValue("listcustpanel", node_billtemplet.getChild("listcustpanel").getText());
				sb_sql.putFieldValue("templetname_e", node_billtemplet.getChild("templetname_e").getText());
				sb_sql.putFieldValue("cardwidth", node_billtemplet.getChild("cardwidth").getText());
				sb_sql.putFieldValue("isshowlistpagebar", node_billtemplet.getChild("isshowlistpagebar").getText());
				sb_sql.putFieldValue("isshowlistopebar", node_billtemplet.getChild("isshowlistopebar").getText());
				sb_sql.putFieldValue("isshowcardborder", node_billtemplet.getChild("isshowcardborder").getText());
				sb_sql.putFieldValue("cardborder", node_billtemplet.getChild("cardborder").getText());
				sb_sql.putFieldValue("treepk", node_billtemplet.getChild("treepk").getText());
				sb_sql.putFieldValue("treeparentpk", node_billtemplet.getChild("treeparentpk").getText());
				sb_sql.putFieldValue("propbeanclassname", node_billtemplet.getChild("propbeanclassname").getText());
				sb_sql.putFieldValue("cardlayout", node_billtemplet.getChild("cardlayout").getText());
				sb_sql.putFieldValue("treeviewfield", node_billtemplet.getChild("treeviewfield").getText());
				sb_sql.putFieldValue("treeseqfield", node_billtemplet.getChild("treeseqfield").getText());
				sb_sql.putFieldValue("treeisshowroot", node_billtemplet.getChild("treeisshowroot").getText());
				sb_sql.putFieldValue("isshowlistquickquery", node_billtemplet.getChild("isshowlistquickquery").getText());
				sb_sql.putFieldValue("isshowlistcustbtn", node_billtemplet.getChild("isshowlistcustbtn").getText());
				sb_sql.putFieldValue("listcustbtndesc", node_billtemplet.getChild("listcustbtndesc").getText());
				sb_sql.putFieldValue("templetbtns", node_billtemplet.getChild("templetbtns").getText());
				sb_sql.putFieldValue("listrowheight", node_billtemplet.getChild("listrowheight").getText());
				sb_sql.putFieldValue("listheaderisgroup", node_billtemplet.getChild("listheaderisgroup").getText());
				sb_sql.putFieldValue("isshowcardcustbtn", node_billtemplet.getChild("isshowcardcustbtn").getText());
				sb_sql.putFieldValue("ordercondition", node_billtemplet.getChild("ordercondition").getText());
				sb_sql.putFieldValue("treeisonlyone", node_billtemplet.getChild("treeisonlyone").getText());
				sb_sql.putFieldValue("treeisshowtoolbar", node_billtemplet.getChild("treeisshowtoolbar").getText());
				sb_sql.putFieldValue("islistautorowheight", node_billtemplet.getChild("islistautorowheight").getText());
				sb_sql.putFieldValue("cardcustbtndesc", node_billtemplet.getChild("cardcustbtndesc").getText());
				sb_sql.putFieldValue("treecustbtndesc", node_billtemplet.getChild("treecustbtndesc").getText());
				sb_sql.putFieldValue("bsdatafilterformula", node_billtemplet.getChild("bsdatafilterformula").getText());
				sb_sql.putFieldValue("bsdatafilterclass", node_billtemplet.getChild("bsdatafilterclass").getText());
				sb_sql.putFieldValue("cardinitformula", node_billtemplet.getChild("cardinitformula").getText());
				sb_sql.putFieldValue("listinitformula", node_billtemplet.getChild("listinitformula").getText());
				list.add(sb_sql);
				java.util.List al_templet_item = node_billtemplet.getChildren("templet_item"); // 得到所有子表信息
				for (int j = 0; j < al_templet_item.size(); j++) {
					org.jdom.Element node_item = (org.jdom.Element) al_templet_item.get(j); // 模板的各个明细
					String pk_pub_templet_1_item = new CommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1_ITEM");

					InsertSQLBuilder sb_sql_item = new InsertSQLBuilder("pub_templet_1_item");
					sb_sql_item.putFieldValue("pk_pub_templet_1_item", pk_pub_templet_1_item);
					sb_sql_item.putFieldValue("pk_pub_templet_1", pk_pub_templet_1);
					sb_sql_item.putFieldValue("itemkey", node_item.getChild("itemkey").getText());
					sb_sql_item.putFieldValue("itemname", node_item.getChild("itemname").getText());
					sb_sql_item.putFieldValue("itemtype", node_item.getChild("itemtype").getText());
					sb_sql_item.putFieldValue("comboxdesc", node_item.getChild("comboxdesc").getText());
					sb_sql_item.putFieldValue("refdesc", node_item.getChild("refdesc").getText());
					sb_sql_item.putFieldValue("issave", node_item.getChild("issave").getText());
					sb_sql_item.putFieldValue("ismustinput", node_item.getChild("ismustinput").getText());
					sb_sql_item.putFieldValue("loadformula", node_item.getChild("loadformula").getText());
					sb_sql_item.putFieldValue("editformula", node_item.getChild("editformula").getText());
					sb_sql_item.putFieldValue("defaultvalueformula", node_item.getChild("defaultvalueformula").getText());
					sb_sql_item.putFieldValue("showorder", node_item.getChild("showorder").getText());
					sb_sql_item.putFieldValue("listwidth", node_item.getChild("listwidth").getText());
					sb_sql_item.putFieldValue("cardwidth", node_item.getChild("cardwidth").getText());
					sb_sql_item.putFieldValue("listisshowable", node_item.getChild("listisshowable").getText());
					sb_sql_item.putFieldValue("listiseditable", node_item.getChild("listiseditable").getText());
					sb_sql_item.putFieldValue("cardisshowable", node_item.getChild("cardisshowable").getText());
					sb_sql_item.putFieldValue("cardiseditable", node_item.getChild("cardiseditable").getText());
					sb_sql_item.putFieldValue("iswrap", node_item.getChild("iswrap").getText());
					sb_sql_item.putFieldValue("itemname_e", node_item.getChild("itemname_e").getText());
					sb_sql_item.putFieldValue("grouptitle", node_item.getChild("grouptitle").getText());
					sb_sql_item.putFieldValue("propisshowable", node_item.getChild("propisshowable").getText());
					sb_sql_item.putFieldValue("propiseditable", node_item.getChild("propiseditable").getText());
					sb_sql_item.putFieldValue("hyperlinkdesc", node_item.getChild("hyperlinkdesc").getText());
					sb_sql_item.putFieldValue("showbgcolor", node_item.getChild("showbgcolor").getText());
					sb_sql_item.putFieldValue("listishtmlhref", node_item.getChild("listishtmlhref").getText());
					sb_sql_item.putFieldValue("isquickquerywrap", node_item.getChild("isquickquerywrap").getText());
					sb_sql_item.putFieldValue("isquickqueryshowable", node_item.getChild("isquickquerywrap").getText());
					sb_sql_item.putFieldValue("isquickqueryeditable", node_item.getChild("isquickqueryeditable").getText());
					sb_sql_item.putFieldValue("iscommquerywrap", node_item.getChild("iscommquerywrap").getText());
					sb_sql_item.putFieldValue("iscommqueryshowable", node_item.getChild("iscommqueryshowable").getText());
					sb_sql_item.putFieldValue("iscommqueryeditable", node_item.getChild("iscommqueryeditable").getText());
					sb_sql_item.putFieldValue("querydefaultformula", node_item.getChild("querydefaultformula").getText());
					sb_sql_item.putFieldValue("querywidth", node_item.getChild("querywidth").getText());
					list.add(sb_sql_item);
				}

			} else if (node_billtemplet_p.getAttributeValue("type").equals("注册按钮")) {// 处理注册按钮
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("refbutton");
				list.add("delete from pub_regbuttons where code='" + node_billtemplet.getChild("code").getText() + "'");

				InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_regbuttons");
				String pub_regbuttons_id = commdmo.getSequenceNextValByDS(null, "S_pub_regbuttons");
				sb_sql.putFieldValue("id", pub_regbuttons_id);
				sb_sql.putFieldValue("code", node_billtemplet.getChild("code").getText());
				sb_sql.putFieldValue("btnimg", node_billtemplet.getChild("btnimg").getText());
				sb_sql.putFieldValue("btntext", node_billtemplet.getChild("btntext").getText());
				sb_sql.putFieldValue("btntooltiptext", node_billtemplet.getChild("btntooltiptext").getText());
				sb_sql.putFieldValue("btndescr", node_billtemplet.getChild("btndescr").getText());
				sb_sql.putFieldValue("btntype", node_billtemplet.getChild("btntype").getText());
				sb_sql.putFieldValue("clickingformula", node_billtemplet.getChild("clickingformula").getText());
				sb_sql.putFieldValue("clickedformula", node_billtemplet.getChild("clickedformula").getText());
				sb_sql.putFieldValue("allowroles", node_billtemplet.getChild("allowroles").getText());
				sb_sql.putFieldValue("allowusers", node_billtemplet.getChild("allowusers").getText());
				list.add(sb_sql);
			} else if (node_billtemplet_p.getAttributeValue("type").equals("注册参照")) {// 处理注册参照
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("refregister");

				InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_refregister");
				list.add("delete from pub_refregister where name='" + node_billtemplet.getChild("name").getText() + "'");
				String pub_regref_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_refregister");
				sb_sql.putFieldValue("id", pub_regref_id);
				sb_sql.putFieldValue("reftype", node_billtemplet.getChild("reftype").getText());
				sb_sql.putFieldValue("name", node_billtemplet.getChild("name").getText());
				sb_sql.putFieldValue("refdefine", node_billtemplet.getChild("refdefine").getText());
				sb_sql.putFieldValue("descr", node_billtemplet.getChild("descr").getText());
				list.add(sb_sql);
			} else if (node_billtemplet_p.getAttributeValue("type").equals("注册样板")) {// 处理注册样板
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("regformat");

				InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_regformatpanel");
				list.add("delete from pub_regformatpanel where code = '" + node_billtemplet.getChild("code").getText() + "'");
				String pk_pub_templet_1 = new CommDMO().getSequenceNextValByDS(null, "s_pub_regformatpanel");

				sb_sql.putFieldValue("id", pk_pub_templet_1);
				sb_sql.putFieldValue("code", node_billtemplet.getChild("code").getText());
				sb_sql.putFieldValue("formatformula", node_billtemplet.getChild("formatformula").getText());
				sb_sql.putFieldValue("descr", node_billtemplet.getChild("descr").getText());
				sb_sql.putFieldValue("suggestwidth", node_billtemplet.getChild("suggestwidth").getText());
				sb_sql.putFieldValue("suggestheight", node_billtemplet.getChild("suggestheight").getText());
				sb_sql.putFieldValue("eventbindformula", node_billtemplet.getChild("eventbindformula").getText());
				sb_sql.putFieldValue("regformula1", node_billtemplet.getChild("regformula1").getText());
				sb_sql.putFieldValue("regformula2", node_billtemplet.getChild("regformula2").getText());
				sb_sql.putFieldValue("regformula3", node_billtemplet.getChild("regformula3").getText());
				list.add(sb_sql);
			} else if (node_billtemplet_p.getAttributeValue("type").equals("下拉框")) {// 处理数据库表
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("billtemplet");
			} else if (node_billtemplet_p.getAttributeValue("type").equals("数据库表")) {// 处理数据库表
				org.jdom.Element node_billtemplet = node_billtemplet_p.getChild("billtemplet");
			}
			commdmo.executeBatchByDS(null, list);
			list.clear();
		}
		System.out.println("-----------------------------------处理结束------------------------------------");
	}

	/**
	 * 导入XML格式下拉框
	 */
	public void importXMLCombobox(String textarea) throws Exception {
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets = doc.getRootElement().getChildren("combobox"); // 遍历所有注册按钮
		for (int i = 0; i < al_billtemplets.size(); i++) {
			org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象

			// 处理主表信息

			InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_comboboxdict");

			commdmo.executeUpdateByDS(null, "delete from pub_comboboxdict where type ='" + node_billtemplet.getChild("type").getText() + "'");
			String pub_combobox_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_comboboxdict");
			sb_sql.putFieldValue("pk_pub_comboboxdict", pub_combobox_id);
			sb_sql.putFieldValue("type", node_billtemplet.getChild("type").getText());
			sb_sql.putFieldValue("id", node_billtemplet.getChild("id").getText());
			sb_sql.putFieldValue("code", node_billtemplet.getChild("code").getText());
			sb_sql.putFieldValue("name", node_billtemplet.getChild("name").getText());
			sb_sql.putFieldValue("descr", node_billtemplet.getChild("descr").getText());
			sb_sql.putFieldValue("seq", node_billtemplet.getChild("seq").getText());
			al.add(sb_sql);

		}
		commdmo.executeBatchByDS(null, al);

	}

	/**
	 * 导入XML格式流程
	 */
	public void importXMLProcess(String textarea) throws Exception {
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets = doc.getRootElement().getChildren("process"); // 遍历所有流程
		java.util.List al_billtemplets_activity = doc.getRootElement().getChildren("activity"); // 遍历所有环节
		java.util.List al_billtemplets_group = doc.getRootElement().getChildren("group"); // 遍历所有组
		java.util.List al_billtemplets_transition = doc.getRootElement().getChildren("transition"); // 遍历所有线
		for (int i = 0; i < al_billtemplets.size(); i++) {
			org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象
			// 处理主表信息
			commdmo.executeUpdateByDS(null, "delete from pub_wf_process where id ='" + node_billtemplet.getChild("id").getText() + "'");
			String pub_process_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_process");

			InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_wf_process");
			sb_sql.putFieldValue("id", pub_process_id);
			sb_sql.putFieldValue("code", node_billtemplet.getChild("code").getText());
			sb_sql.putFieldValue("name", node_billtemplet.getChild("name").getText());
			sb_sql.putFieldValue("userdef01", node_billtemplet.getChild("userdef01").getText());
			sb_sql.putFieldValue("userdef02", node_billtemplet.getChild("userdef02").getText());
			sb_sql.putFieldValue("userdef03", node_billtemplet.getChild("userdef03").getText());
			sb_sql.putFieldValue("userdef04", node_billtemplet.getChild("userdef04").getText());
			sb_sql.putFieldValue("userdef05", node_billtemplet.getChild("userdef05").getText());
			sb_sql.putFieldValue("userdef06", node_billtemplet.getChild("userdef06").getText());
			sb_sql.putFieldValue("userdef07", node_billtemplet.getChild("userdef07").getText());
			sb_sql.putFieldValue("userdef08", node_billtemplet.getChild("userdef08").getText());
			sb_sql.putFieldValue("userdef09", node_billtemplet.getChild("userdef09").getText());
			sb_sql.putFieldValue("userdef10", node_billtemplet.getChild("userdef10").getText());
			sb_sql.putFieldValue("userdef11", node_billtemplet.getChild("userdef11").getText());
			sb_sql.putFieldValue("userdef12", node_billtemplet.getChild("userdef12").getText());
			sb_sql.putFieldValue("userdef13", node_billtemplet.getChild("userdef13").getText());
			sb_sql.putFieldValue("userdef14", node_billtemplet.getChild("userdef14").getText());
			sb_sql.putFieldValue("userdef15", node_billtemplet.getChild("userdef15").getText());
			sb_sql.putFieldValue("userdef16", node_billtemplet.getChild("userdef16").getText());
			sb_sql.putFieldValue("userdef17", node_billtemplet.getChild("userdef17").getText());
			sb_sql.putFieldValue("userdef18", node_billtemplet.getChild("userdef18").getText());
			sb_sql.putFieldValue("userdef19", node_billtemplet.getChild("userdef19").getText());
			sb_sql.putFieldValue("userdef20", node_billtemplet.getChild("userdef20").getText());
			al.add(sb_sql);
			HashMap hashmap = new HashMap();
			for (int j = 0; j < al_billtemplets_activity.size(); j++) {
				org.jdom.Element node_billtemplet_activity = (org.jdom.Element) al_billtemplets_activity.get(j); // 每个模板结点对象
				commdmo.executeUpdateByDS(null, "delete from pub_wf_activity where processid ='" + node_billtemplet.getChild("id").getText() + "'");
				String pub_acitivity_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
				hashmap.put(node_billtemplet_activity.getChild("id").getText(), pub_acitivity_id);

				InsertSQLBuilder sb_sql_acitivity = new InsertSQLBuilder("pub_wf_activity");
				sb_sql_acitivity.putFieldValue("id", pub_acitivity_id);
				sb_sql_acitivity.putFieldValue("processid", pub_process_id);
				sb_sql_acitivity.putFieldValue("code", node_billtemplet_activity.getChild("code").getText());
				sb_sql_acitivity.putFieldValue("wfname", node_billtemplet_activity.getChild("wfname").getText());
				sb_sql_acitivity.putFieldValue("uiname", node_billtemplet_activity.getChild("uiname").getText());
				sb_sql_acitivity.putFieldValue("x", node_billtemplet_activity.getChild("x").getText());
				sb_sql_acitivity.putFieldValue("y", node_billtemplet_activity.getChild("y").getText());
				sb_sql_acitivity.putFieldValue("autocommit", node_billtemplet_activity.getChild("autocommit").getText());
				sb_sql_acitivity.putFieldValue("isassignapprover", node_billtemplet_activity.getChild("isassignapprover").getText());
				sb_sql_acitivity.putFieldValue("approvemodel", node_billtemplet_activity.getChild("approvemodel").getText());
				sb_sql_acitivity.putFieldValue("approvenumber", node_billtemplet_activity.getChild("approvenumber").getText());
				sb_sql_acitivity.putFieldValue("participate_user", node_billtemplet_activity.getChild("participate_user").getText());
				sb_sql_acitivity.putFieldValue("participate_group", node_billtemplet_activity.getChild("participate_group").getText());
				sb_sql_acitivity.putFieldValue("participate_dynamic", node_billtemplet_activity.getChild("participate_dynamic").getText());
				sb_sql_acitivity.putFieldValue("messageformat", node_billtemplet_activity.getChild("messageformat").getText());
				sb_sql_acitivity.putFieldValue("messagereceiver", node_billtemplet_activity.getChild("messagereceiver").getText());
				sb_sql_acitivity.putFieldValue("descr", node_billtemplet_activity.getChild("descr").getText());
				sb_sql_acitivity.putFieldValue("activitytype", node_billtemplet_activity.getChild("activitytype").getText());
				sb_sql_acitivity.putFieldValue("iscanback", node_billtemplet_activity.getChild("iscanback").getText());
				sb_sql_acitivity.putFieldValue("isneedmsg", node_billtemplet_activity.getChild("isneedmsg").getText());
				sb_sql_acitivity.putFieldValue("intercept1", node_billtemplet_activity.getChild("intercept1").getText());
				sb_sql_acitivity.putFieldValue("intercept2", node_billtemplet_activity.getChild("intercept2").getText());
				sb_sql_acitivity.putFieldValue("checkuserpanel", node_billtemplet_activity.getChild("checkuserpanel").getText());
				sb_sql_acitivity.putFieldValue("width", node_billtemplet_activity.getChild("width").getText());
				sb_sql_acitivity.putFieldValue("height", node_billtemplet_activity.getChild("height").getText());
				sb_sql_acitivity.putFieldValue("viewtype", node_billtemplet_activity.getChild("viewtype").getText());
				sb_sql_acitivity.putFieldValue("belongdeptgroup", node_billtemplet_activity.getChild("belongdeptgroup").getText());
				sb_sql_acitivity.putFieldValue("belongstationgroup", node_billtemplet_activity.getChild("belongstationgroup").getText());
				sb_sql_acitivity.putFieldValue("canhalfstart", node_billtemplet_activity.getChild("canhalfstart").getText());
				sb_sql_acitivity.putFieldValue("halfstartrole", node_billtemplet_activity.getChild("halfstartrole").getText());
				sb_sql_acitivity.putFieldValue("canhalfend", node_billtemplet_activity.getChild("canhalfend").getText());
				sb_sql_acitivity.putFieldValue("canselfaddparticipate", node_billtemplet_activity.getChild("canselfaddparticipate").getText());
				sb_sql_acitivity.putFieldValue("showparticimode", node_billtemplet_activity.getChild("showparticimode").getText());
				sb_sql_acitivity.putFieldValue("isneedreport", node_billtemplet_activity.getChild("isneedreport").getText());
				al.add(sb_sql_acitivity);
			}

			for (int j = 0; j < al_billtemplets_transition.size(); j++) {
				org.jdom.Element node_billtemplet_transition = (org.jdom.Element) al_billtemplets_transition.get(j); // 每个模板结点对象

				InsertSQLBuilder sb_sql_transition = new InsertSQLBuilder("pub_wf_transition");
				commdmo.executeUpdateByDS(null, "delete from pub_wf_transition where processid ='" + node_billtemplet.getChild("id").getText() + "'");
				String pub_transition_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");
				sb_sql_transition.putFieldValue("id", pub_transition_id);
				sb_sql_transition.putFieldValue("processid", pub_process_id);
				sb_sql_transition.putFieldValue("code", node_billtemplet_transition.getChild("code").getText());
				sb_sql_transition.putFieldValue("wfname", node_billtemplet_transition.getChild("wfname").getText());
				sb_sql_transition.putFieldValue("uiname", node_billtemplet_transition.getChild("uiname").getText());
				sb_sql_transition.putFieldValue("fromactivity", node_billtemplet_transition.getChild("fromactivity").getText());
				sb_sql_transition.putFieldValue("toactivity", node_billtemplet_transition.getChild("toactivity").getText());
				sb_sql_transition.putFieldValue("conditions", node_billtemplet_transition.getChild("conditions").getText());
				sb_sql_transition.putFieldValue("points", node_billtemplet_transition.getChild("points").getText());
				sb_sql_transition.putFieldValue("dealtype", node_billtemplet_transition.getChild("dealtype").getText());
				sb_sql_transition.putFieldValue("reasoncodesql", node_billtemplet_transition.getChild("reasoncodesql").getText());
				sb_sql_transition.putFieldValue("mailsubject", node_billtemplet_transition.getChild("mailsubject").getText());
				sb_sql_transition.putFieldValue("mailcontent", node_billtemplet_transition.getChild("mailcontent").getText());
				sb_sql_transition.putFieldValue("intercept", node_billtemplet_transition.getChild("intercept").getText());
				al.add(sb_sql_transition);
			}

			for (int j = 0; j < al_billtemplets_group.size(); j++) {
				org.jdom.Element node_billtemplet_group = (org.jdom.Element) al_billtemplets_group.get(j); // 每个模板结点对象

				InsertSQLBuilder sb_sql_group = new InsertSQLBuilder("pub_wf_group");
				commdmo.executeUpdateByDS(null, "delete from pub_wf_group where processid ='" + node_billtemplet.getChild("id").getText() + "'");
				String pub_group_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
				sb_sql_group.putFieldValue("id", pub_group_id);
				sb_sql_group.putFieldValue("processid", pub_process_id);
				sb_sql_group.putFieldValue("grouptype", node_billtemplet_group.getChild("grouptype").getText());
				sb_sql_group.putFieldValue("code", node_billtemplet_group.getChild("code").getText());
				sb_sql_group.putFieldValue("wfname", node_billtemplet_group.getChild("wfname").getText());
				sb_sql_group.putFieldValue("uiname", node_billtemplet_group.getChild("uiname").getText());
				sb_sql_group.putFieldValue("x", node_billtemplet_group.getChild("x").getText());
				sb_sql_group.putFieldValue("y", node_billtemplet_group.getChild("y").getText());
				sb_sql_group.putFieldValue("width", node_billtemplet_group.getChild("width").getText());
				sb_sql_group.putFieldValue("height", node_billtemplet_group.getChild("height").getText());
				sb_sql_group.putFieldValue("descr", node_billtemplet_group.getChild("descr").getText());
				al.add(sb_sql_group);
			}
		}
		commdmo.executeBatchByDS(null, al);
	}

	/**
	 * 导入迪博xml格式的流程
	 */
	public String importDBXMLProcess(String wf_code, String wf_name, String str_xml, String userdef01, String wf_securitylevel) throws Exception {
		CommDMO commdmo = new CommDMO();
		ByteArrayInputStream bis = new ByteArrayInputStream(str_xml.getBytes()); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(bis); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets_activity = doc.getRootElement().getChild("vertexs").getChildren("vertex"); // 遍历所有环节
		java.util.List al_billtemplets_transition = doc.getRootElement().getChild("edges").getChildren("edge"); // 遍历所有线

		InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_wf_process");
		String pub_process_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_process");
		sb_sql.putFieldValue("id", pub_process_id);
		sb_sql.putFieldValue("code", wf_code);
		sb_sql.putFieldValue("name", wf_name);
		sb_sql.putFieldValue("userdef01", userdef01);
		sb_sql.putFieldValue("wftype", "体系流程");
		sb_sql.putFieldValue("userdef02", wf_securitylevel);
		al.add(sb_sql);

		HashMap hashmap = new HashMap();
		int WFA_code = 0; //环节编号
		List normalActivity = new ArrayList(); //普通环节
		List startOrEndActivity = new ArrayList(); //开始或结束环节
		List deptList = new ArrayList(); //部门
		List postList = new ArrayList(); //岗位
		List stationList = new ArrayList(); //阶段
		List triangleActivity = new ArrayList(); //三角图标
		float go_right = 34;
		float go_bottom = 30;
		for (int j = 0; j < al_billtemplets_activity.size(); j++) {
			org.jdom.Element node_billtemplet_activity = (org.jdom.Element) al_billtemplets_activity.get(j);
			String str_type = node_billtemplet_activity.getAttributeValue("type");
			if ("1".equals(str_type) || "2".equals(str_type)) { //(1.开始环节;2.结束环节;4.一部分是y值约为36.0 最上面的处理人员，其他的是一般环节;311部门；321阶段)
				startOrEndActivity.add(node_billtemplet_activity);
			} else if ("4".equals(str_type) || "6".equals(str_type) || "7".equals(str_type) || "8".equals(str_type)) { //最小值为36.0，还看到过有37.0
				normalActivity.add(node_billtemplet_activity); //环节             
			} else if ("311".equals(str_type) || "312".equals(str_type)) { //部门 或者 输出文档列
				deptList.add(node_billtemplet_activity);
			} else if ("321".equals(str_type)) { //阶段
				stationList.add(node_billtemplet_activity);
			} else if ("20".equals(str_type)) { //环节右上角的小三角
				triangleActivity.add(node_billtemplet_activity);
			}
		}

		//开始或结束环节
		for (int j = 0; j < startOrEndActivity.size(); j++) {
			org.jdom.Element node_billtemplet_activity = (org.jdom.Element) startOrEndActivity.get(j); // 每个模板结点对象
			String str_type = node_billtemplet_activity.getAttributeValue("type");
			float activity_x = Float.parseFloat(node_billtemplet_activity.getChild("x").getText().trim());
			float activity_y = Float.parseFloat(node_billtemplet_activity.getChild("y").getText().trim());
			String belongdeptgroup = "";
			String belongstationgroup = "";

			InsertSQLBuilder sb_sql_acitivity = new InsertSQLBuilder("pub_wf_activity");
			String pub_acitivity_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
			hashmap.put(node_billtemplet_activity.getAttributeValue("id"), pub_acitivity_id);
			sb_sql_acitivity.putFieldValue("id", pub_acitivity_id);
			sb_sql_acitivity.putFieldValue("processid", pub_process_id);
			if ("1".equals(str_type)) {
				sb_sql_acitivity.putFieldValue("code", "WFA0");
				sb_sql_acitivity.putFieldValue("wfname", node_billtemplet_activity.getChild("text").getText());
				sb_sql_acitivity.putFieldValue("uiname", "0");
				sb_sql_acitivity.putFieldValue("x", activity_x + go_right);
				sb_sql_acitivity.putFieldValue("y", activity_y + go_bottom);
				sb_sql_acitivity.putFieldValue("autocommit", "N");
				sb_sql_acitivity.putFieldValue("isassignapprover", "N");
				sb_sql_acitivity.putFieldValue("approvemodel", "1");
				sb_sql_acitivity.putFieldValue("activitytype", "START");
			} else {
				sb_sql_acitivity.putFieldValue("code", "WFA999");
				sb_sql_acitivity.putFieldValue("wfname", node_billtemplet_activity.getChild("text").getText());
				sb_sql_acitivity.putFieldValue("uiname", "9");
				sb_sql_acitivity.putFieldValue("x", activity_x + go_right);
				sb_sql_acitivity.putFieldValue("y", activity_y + go_bottom);
				sb_sql_acitivity.putFieldValue("autocommit", "N");
				sb_sql_acitivity.putFieldValue("isassignapprover", "N");
				sb_sql_acitivity.putFieldValue("approvemodel", "1");
				sb_sql_acitivity.putFieldValue("activitytype", "END");
			}
			sb_sql_acitivity.putFieldValue("iscanback", "N");
			sb_sql_acitivity.putFieldValue("isneedmsg", "N");
			sb_sql_acitivity.putFieldValue("width", node_billtemplet_activity.getChild("width").getText());
			sb_sql_acitivity.putFieldValue("height", node_billtemplet_activity.getChild("height").getText());
			sb_sql_acitivity.putFieldValue("viewtype", "1");
			for (int z = 0; z < deptList.size(); z++) {
				org.jdom.Element deptElement = (org.jdom.Element) deptList.get(z);
				float dept_x = Float.parseFloat(deptElement.getChild("x").getText().trim());
				float dept_width = Float.parseFloat(deptElement.getChild("width").getText().trim());
				if (activity_x >= dept_x && activity_x <= (dept_x + dept_width)) { //环节最左上角在某部门内
					belongdeptgroup = deptElement.getChild("text").getText();
					break;
				}
			}
			for (int z = 0; z < stationList.size(); z++) {
				org.jdom.Element stationElement = (org.jdom.Element) stationList.get(z);
				float station_x = Float.parseFloat(stationElement.getChild("x").getText().trim());
				float station_width = Float.parseFloat(stationElement.getChild("width").getText().trim());
				if (activity_x >= station_x && activity_x <= (station_x + station_width)) { //环节最左上角在某阶段内
					belongstationgroup = stationElement.getChild("text").getText();
					break;
				}
			}
			sb_sql_acitivity.putFieldValue("belongdeptgroup", belongdeptgroup);
			sb_sql_acitivity.putFieldValue("belongstationgroup", belongstationgroup);
			sb_sql_acitivity.putFieldValue("canhalfstart", "N");
			sb_sql_acitivity.putFieldValue("canhalfend", "N");
			sb_sql_acitivity.putFieldValue("canselfaddparticipate", "N");
			sb_sql_acitivity.putFieldValue("showparticimode", "1");
			sb_sql_acitivity.putFieldValue("isneedreport", "Y");
			sb_sql_acitivity.putFieldValue("fonttype", "宋体");
			sb_sql_acitivity.putFieldValue("fontsize", "12");
			sb_sql_acitivity.putFieldValue("foreground", "0,0,0");
			sb_sql_acitivity.putFieldValue("background", "225,255,225");
			al.add(sb_sql_acitivity);
		}

		//普通环节
		for (int j = 0; j < normalActivity.size(); j++) {
			org.jdom.Element node_billtemplet_activity = (org.jdom.Element) normalActivity.get(j); // 每个模板结点对象、
			String activity_text = node_billtemplet_activity.getChild("text").getText();
			if ("".equals(activity_text.trim())) { //为空串，一般是会签下面的框框（要先执行sql，这样流程图中，框框才会在会签环节下面！！）
				normalActivity.remove(node_billtemplet_activity);
				normalActivity.add(0, node_billtemplet_activity); //一直往前加
			}
		}

		//普通环节
		for (int j = 0; j < normalActivity.size(); j++) {
			org.jdom.Element node_billtemplet_activity = (org.jdom.Element) normalActivity.get(j); // 每个模板结点对象、
			float activity_y = Float.parseFloat(node_billtemplet_activity.getChild("y").getText());
			if (activity_y < 40.0) {
				postList.add(node_billtemplet_activity); //岗位
				continue;
			}
			String str_type = node_billtemplet_activity.getAttributeValue("type");
			float activity_x = Float.parseFloat(node_billtemplet_activity.getChild("x").getText());
			float activity_width = Float.parseFloat(node_billtemplet_activity.getChild("width").getText());
			float activity_height = Float.parseFloat(node_billtemplet_activity.getChild("height").getText());
			String belongdeptgroup = "";
			String belongstationgroup = "";
			WFA_code++;

			InsertSQLBuilder sb_sql_acitivity = new InsertSQLBuilder("pub_wf_activity");
			String pub_acitivity_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
			hashmap.put(node_billtemplet_activity.getAttributeValue("id"), pub_acitivity_id);
			sb_sql_acitivity.putFieldValue("id", pub_acitivity_id); // id 
			sb_sql_acitivity.putFieldValue("processid", pub_process_id); // processid 
			if (node_billtemplet_activity.getChild("text").getText() != null && !node_billtemplet_activity.getChild("text").getText().trim().equals("") && node_billtemplet_activity.getChild("text").getText().trim().length() > 1) {
				String code = node_billtemplet_activity.getChild("text").getText().trim().substring(0, 2);
				if (Character.isDigit(code.charAt(0)) && Character.isDigit(code.charAt(1))) { //如果前两位为数字
					sb_sql_acitivity.putFieldValue("code", "WFA" + code); // code 
					sb_sql_acitivity.putFieldValue("wfname", node_billtemplet_activity.getChild("text").getText()); // wfname 
					sb_sql_acitivity.putFieldValue("uiname", code); // uiname 
				} else {
					sb_sql_acitivity.putFieldValue("code", "WFA" + WFA_code); // code 
					sb_sql_acitivity.putFieldValue("wfname", node_billtemplet_activity.getChild("text").getText()); // wfname 
					sb_sql_acitivity.putFieldValue("uiname", WFA_code); // uiname 
				}
			} else {
				sb_sql_acitivity.putFieldValue("code", "WFA" + WFA_code); // code 
				sb_sql_acitivity.putFieldValue("wfname", node_billtemplet_activity.getChild("text").getText()); // wfname 
				sb_sql_acitivity.putFieldValue("uiname", WFA_code); // uiname 
			}
			sb_sql_acitivity.putFieldValue("x", activity_x + go_right);
			sb_sql_acitivity.putFieldValue("y", activity_y + 30);
			sb_sql_acitivity.putFieldValue("autocommit", "N");
			sb_sql_acitivity.putFieldValue("isassignapprover", "N");
			sb_sql_acitivity.putFieldValue("approvemodel", "1");
			sb_sql_acitivity.putFieldValue("activitytype", "NORMAL");
			sb_sql_acitivity.putFieldValue("iscanback", "N");
			sb_sql_acitivity.putFieldValue("isneedmsg", "N");
			sb_sql_acitivity.putFieldValue("width", activity_width);
			sb_sql_acitivity.putFieldValue("height", activity_height);
			if ("8".equals(str_type)) {
				sb_sql_acitivity.putFieldValue("viewtype", "7");
			} else if ("6".equals(str_type)) {
				sb_sql_acitivity.putFieldValue("viewtype", "3");
			} else if ("7".equals(str_type)) {
				sb_sql_acitivity.putFieldValue("viewtype", "2");
			} else {
				int flag = 0;
				for (int z = 0; z < triangleActivity.size(); z++) { //环节上有小三角！！
					org.jdom.Element node_activity = (org.jdom.Element) triangleActivity.get(z); // 每个模板结点对象
					float node_activity_x = Float.parseFloat(node_activity.getChild("x").getText().trim());
					float node_activity_y = Float.parseFloat(node_activity.getChild("y").getText().trim());
					float node_activity_height = Float.parseFloat(node_activity.getChild("height").getText().trim());
					if (activity_x < node_activity_x && node_activity_x < (activity_x + activity_width) && (activity_y - node_activity_y - node_activity_height > -3 && activity_y - node_activity_y - node_activity_height < 3)) {
						sb_sql_acitivity.putFieldValue("viewtype", "5"); // viewtype 
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					sb_sql_acitivity.putFieldValue("viewtype", "1"); // viewtype 
				}
			}
			for (int z = 0; z < deptList.size(); z++) {
				org.jdom.Element deptElement = (org.jdom.Element) deptList.get(z);
				float dept_x = Float.parseFloat(deptElement.getChild("x").getText().trim());
				float dept_width = Float.parseFloat(deptElement.getChild("width").getText().trim());
				if (activity_x >= dept_x && activity_x < (dept_x + dept_width)) { //环节最左上角在某部门内
					belongdeptgroup = deptElement.getChild("text").getText();
					break;
				}
			}
			for (int z = 0; z < stationList.size(); z++) {
				org.jdom.Element stationElement = (org.jdom.Element) stationList.get(z);
				float station_x = Float.parseFloat(stationElement.getChild("x").getText().trim());
				float station_width = Float.parseFloat(stationElement.getChild("width").getText().trim());
				if (activity_x >= station_x && activity_x < (station_x + station_width)) { //环节最左上角在某阶段内
					belongstationgroup = stationElement.getChild("text").getText();
					break;
				}
			}

			sb_sql_acitivity.putFieldValue("belongdeptgroup", belongdeptgroup);
			sb_sql_acitivity.putFieldValue("belongstationgroup", belongstationgroup);
			sb_sql_acitivity.putFieldValue("canhalfstart", "N");
			sb_sql_acitivity.putFieldValue("canhalfend", "N");
			sb_sql_acitivity.putFieldValue("canselfaddparticipate", "N");
			sb_sql_acitivity.putFieldValue("showparticimode", "1");
			sb_sql_acitivity.putFieldValue("isneedreport", "Y");
			sb_sql_acitivity.putFieldValue("fonttype", "宋体");
			sb_sql_acitivity.putFieldValue("fontsize", "12");
			if ("自动控制".equals(node_billtemplet_activity.getChild("controlType").getText())) { //自动控制：字体和框框都为蓝色； 手工控制为黑色
				sb_sql_acitivity.putFieldValue("foreground", "51,51,255");
				sb_sql_acitivity.putFieldValue("background", "232,238,247");
			} else {
				sb_sql_acitivity.putFieldValue("foreground", "0,0,0");
				sb_sql_acitivity.putFieldValue("background", "232,238,247");
			}
			al.add(sb_sql_acitivity);
		}

		//连线
		for (int j = 0; j < al_billtemplets_transition.size(); j++) {
			org.jdom.Element node_billtemplet_transition = (org.jdom.Element) al_billtemplets_transition.get(j); // 
			String fromid = (String) hashmap.get(node_billtemplet_transition.getAttributeValue("fromId"));
			String toid = (String) hashmap.get(node_billtemplet_transition.getAttributeValue("toId"));
			if (fromid == null || toid == null) { //如果找不到连线的两个环节，就不画线了！
				continue;
			}
			InsertSQLBuilder sb_sql_transition = new InsertSQLBuilder("pub_wf_transition");
			String pub_transition_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");
			sb_sql_transition.putFieldValue("id", pub_transition_id);
			sb_sql_transition.putFieldValue("processid", pub_process_id);
			sb_sql_transition.putFieldValue("code", "WFT");
			sb_sql_transition.putFieldValue("fromactivity", hashmap.get(node_billtemplet_transition.getAttributeValue("fromId")).toString());
			sb_sql_transition.putFieldValue("toactivity", hashmap.get(node_billtemplet_transition.getAttributeValue("toId")).toString());
			sb_sql_transition.putFieldValue("dealtype", "SUBMIT");
			List pointList = node_billtemplet_transition.getChild("points").getChildren();
			StringBuffer sqlpoint = new StringBuffer();
			boolean flag = false;
			for (int z = 1; z < pointList.size() - 1; z++) { //迪博图中记录了起点和终点位置，而我们系统中不用记录！
				org.jdom.Element pointElement = (Element) pointList.get(z);
				sqlpoint.append(Float.parseFloat(pointElement.getChild("x").getText()) + go_right + "," + (Float.parseFloat(pointElement.getChild("y").getTextTrim()) + go_bottom));
				flag = true;
				sqlpoint.append(";");
			}
			if (flag) {
				sb_sql_transition.putFieldValue("points", sqlpoint.substring(0, sqlpoint.length() - 1));
			} else {
				sb_sql_transition.putFieldValue("points", "");
			}
			sb_sql_transition.putFieldValue("fonttype", "宋体");
			sb_sql_transition.putFieldValue("fontsize", "12");
			sb_sql_transition.putFieldValue("foreground", "0,0,0");
			sb_sql_transition.putFieldValue("background", "0,0,0");
			al.add(sb_sql_transition);
		}
		//岗位位置排序
		for (int j = 0; j < postList.size() - 1; j++) {
			org.jdom.Element node = (org.jdom.Element) postList.get(j);
			float min_x = Float.parseFloat(node.getChild("x").getTextTrim());
			int x = j;
			for (int i = j + 1; i < postList.size(); i++) {
				org.jdom.Element node1 = (org.jdom.Element) postList.get(i);
				float li_x = Float.parseFloat(node1.getChild("x").getTextTrim());
				if (min_x > li_x) {
					x = i;
					min_x = Float.parseFloat(node1.getChild("x").getTextTrim());
				}
			}
			if (x != j) {
				postList.set(j, postList.get(x));
				postList.set(x, node);
			}
		}
		//部门
		for (int j = 0; j < deptList.size(); j++) {
			org.jdom.Element node_billtemplet_group = (org.jdom.Element) deptList.get(j); // 每个模板结点对象
			float dept_x = Float.parseFloat(node_billtemplet_group.getChild("x").getText());
			float dept_width = Float.parseFloat(node_billtemplet_group.getChild("width").getText());

			InsertSQLBuilder sb_sql_group = new InsertSQLBuilder("pub_wf_group");
			String pub_group_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
			sb_sql_group.putFieldValue("id", pub_group_id);
			sb_sql_group.putFieldValue("processid", pub_process_id);
			sb_sql_group.putFieldValue("grouptype", "DEPT");
			sb_sql_group.putFieldValue("code", node_billtemplet_group.getChild("code").getText());
			sb_sql_group.putFieldValue("wfname", node_billtemplet_group.getChild("wfname").getText());
			sb_sql_group.putFieldValue("uiname", node_billtemplet_group.getChild("uiname").getText());
			sb_sql_group.putFieldValue("x", dept_x + go_right);
			sb_sql_group.putFieldValue("y", Float.parseFloat(node_billtemplet_group.getChild("y").getTextTrim()));
			sb_sql_group.putFieldValue("width", dept_width);
			sb_sql_group.putFieldValue("height", node_billtemplet_group.getChild("height").getText());
			sb_sql_group.putFieldValue("fonttype", "宋体");
			sb_sql_group.putFieldValue("fontsize", "12");
			sb_sql_group.putFieldValue("foreground", "0,0,0");
			sb_sql_group.putFieldValue("background", "232,238,247");
			StringBuffer posts = new StringBuffer("");
			for (int i = 0; i < postList.size(); i++) {
				org.jdom.Element node_billtemplet_post = (org.jdom.Element) postList.get(i); // 每个模板结点对象
				float post_x = Float.parseFloat(node_billtemplet_post.getChild("x").getText());
				float post_width = Float.parseFloat(node_billtemplet_post.getChild("width").getText());
				if ((post_x + post_width / 2) > dept_x && (post_x + post_width / 2) < (dept_x + dept_width)) {
					posts.append(node_billtemplet_post.getChild("text").getText() + ";");
				}
			}
			sb_sql_group.putFieldValue("posts", posts.toString());
			al.add(sb_sql_group);
		}
		//      //岗位
		//      for (int j = 0; j < postList.size(); j++) {
		//          org.jdom.Element node_billtemplet_group = (org.jdom.Element) postList.get(j); // 每个模板结点对象
		//          StringBuffer sb_sql_group = new StringBuffer();
		//          String pub_group_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
		//          sb_sql_group.append("insert into pub_wf_group");
		//          sb_sql_group.append("(");
		//          sb_sql_group.append("id,"); // id (id)
		//          sb_sql_group.append("processid,"); // processid (processid)
		//          sb_sql_group.append("grouptype,"); // grouptype (grouptype)
		//          sb_sql_group.append("code,"); // code (code)
		//          sb_sql_group.append("wfname,"); // wfname (wfname)
		//          sb_sql_group.append("uiname,"); // uiname (uiname)
		//          sb_sql_group.append("x,"); // x (x)
		//          sb_sql_group.append("y,"); // y (y)
		//          sb_sql_group.append("width,"); // width (width)
		//          sb_sql_group.append("height,"); // 
		//          sb_sql_group.append("fonttype,"); //
		//          sb_sql_group.append("fontsize,"); //
		//          sb_sql_group.append("foreground,"); //
		//          sb_sql_group.append("background"); //
		//          sb_sql_group.append(")");
		//          sb_sql_group.append(" values ");
		//          sb_sql_group.append("(");
		//          sb_sql_group.append("" + pub_group_id + ","); // id (id)
		//          sb_sql_group.append("" + pub_process_id + ","); // processid
		//          sb_sql_group.append("'POST','"); // grouptype
		//          sb_sql_group.append(node_billtemplet_group.getChild("text").getText() + "','"); // code
		//          sb_sql_group.append(node_billtemplet_group.getChild("text").getText() + "','"); // wfname
		//          sb_sql_group.append(node_billtemplet_group.getChild("text").getText() + "',"); // uiname
		//          sb_sql_group.append(Float.parseFloat(node_billtemplet_group.getChild("x").getTextTrim()) + go_right + ","); // x
		//          sb_sql_group.append("68.0,"); // y
		//          sb_sql_group.append(convertSQLValue(node_billtemplet_group.getChild("width").getText()) + ","); // width
		//          sb_sql_group.append("40.0,"); // height
		//          sb_sql_group.append("'宋体','12','0,0,0','232,238,247')"); // height,fonttype,fontsize,foreground,background
		//
		//          al.add(sb_sql_group.toString());
		//      }
		//阶段
		for (int j = 0; j < stationList.size(); j++) {
			org.jdom.Element node_billtemplet_group = (org.jdom.Element) stationList.get(j); // 每个模板结点对象
			float station_y = Float.parseFloat(node_billtemplet_group.getChild("y").getTextTrim());

			InsertSQLBuilder sb_sql_group = new InsertSQLBuilder("pub_wf_group");
			String pub_group_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
			sb_sql_group.putFieldValue("id", pub_group_id);
			sb_sql_group.putFieldValue("processid", pub_process_id);
			sb_sql_group.putFieldValue("grouptype", "STATION");
			sb_sql_group.putFieldValue("code", node_billtemplet_group.getChild("code").getText());
			sb_sql_group.putFieldValue("wfname", node_billtemplet_group.getChild("wfname").getText());
			sb_sql_group.putFieldValue("uiname", node_billtemplet_group.getChild("uiname").getText());
			sb_sql_group.putFieldValue("x", "10");
			if (station_y < 40.0) { //最上面一个阶段要加上岗位的高度！
				station_y += 60;
			} else {
				station_y += 20;
			}
			sb_sql_group.putFieldValue("y", station_y);
			sb_sql_group.putFieldValue("width", Float.parseFloat(node_billtemplet_group.getChild("width").getTextTrim()));
			sb_sql_group.putFieldValue("height", "40");
			sb_sql_group.putFieldValue("fonttype", "宋体"); //
			sb_sql_group.putFieldValue("fontsize", "12");
			sb_sql_group.putFieldValue("foreground", "0,0,0");
			sb_sql_group.putFieldValue("background", "0,0,0");
			al.add(sb_sql_group);
		}
		commdmo.executeBatchByDS(null, al);
		return pub_process_id;
	}

	//获得表所有字段
	private HashSet<String> getTableItemMap(String _tableName) throws Exception {
		TableDataStruct tabstrct = commdmo.getTableDataStructByDS(null, (new StringBuilder("select * from ")).append(_tableName).append(" where 1=2").toString());
		HashSet hstCols = new HashSet();
		String str_cols[] = tabstrct.getHeaderName();
		for (int k = 0; k < str_cols.length; k++)
			hstCols.add(str_cols[k].toUpperCase());
		return hstCols;
	}

	/**
	 * 导入XML格式流程,不删除原有，其实就是复制功能
	 * 根据导入的数据库字段，然后和xml中匹配。方便扩展字段
	 * by haoming 2016-04-13
	 */
	public void importXMLProcess_Copy(String textarea) throws Exception {
		ByteArrayInputStream byin = new ByteArrayInputStream(textarea.getBytes("GBK")); // 读入xml内容
		org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(byin); // 加载XML,生成一个document对象
		ArrayList al = new ArrayList();
		java.util.List al_billtemplets = doc.getRootElement().getChildren("process"); // 遍历所有流程
		java.util.List al_billtemplets_activity = doc.getRootElement().getChildren("activity"); // 遍历所有环节
		java.util.List al_billtemplets_group = doc.getRootElement().getChildren("group"); // 遍历所有组
		java.util.List al_billtemplets_transition = doc.getRootElement().getChildren("transition"); // 遍历所有线
		for (int i = 0; i < al_billtemplets.size(); i++) {
			org.jdom.Element node_billtemplet = (org.jdom.Element) al_billtemplets.get(i); // 每个模板结点对象
			// 处理主表信息
			InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_wf_process");
			String pub_process_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_process");
			HashSet<String> hstCols = getTableItemMap("pub_wf_process");
			List allItem = node_billtemplet.getChildren(); //得到所有字段
			for (int j = 0; j < allItem.size(); j++) {
				org.jdom.Element childNode = (Element) allItem.get(j);
				String itemName = childNode.getName().toUpperCase();
				String itemValue = childNode.getText();
				if (TBUtil.isEmpty(itemValue)) {
					continue;
				}
				if (hstCols.contains(itemName)) { //数据库有这个字段
					sb_sql.putFieldValue(itemName, itemValue);
				}
			}
			sb_sql.putFieldValue("ID", pub_process_id);
			al.add(sb_sql);
			HashMap hashmap = new HashMap();
			hstCols = getTableItemMap("pub_wf_activity");
			for (int j = 0; j < al_billtemplets_activity.size(); j++) {
				org.jdom.Element node_billtemplet_activity = (org.jdom.Element) al_billtemplets_activity.get(j); // 每个模板结点对象
				InsertSQLBuilder sb_sql_acitivity = new InsertSQLBuilder("pub_wf_activity");
				String pub_acitivity_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
				hashmap.put(node_billtemplet_activity.getChild("id").getText(), pub_acitivity_id);

				List activityAllItem = node_billtemplet_activity.getChildren(); //得到所有字段
				for (int k = 0; k < activityAllItem.size(); k++) {
					org.jdom.Element childNode = (Element) activityAllItem.get(k);
					String itemName = childNode.getName().toUpperCase();
					String itemValue = childNode.getText();
					if (TBUtil.isEmpty(itemValue)) {
						continue;
					}
					if (hstCols.contains(itemName)) { //数据库有这个字段
						sb_sql_acitivity.putFieldValue(itemName, itemValue);
					}
				}
				sb_sql_acitivity.putFieldValue("ID", pub_acitivity_id);
				sb_sql_acitivity.putFieldValue("PROCESSID", pub_process_id);
				al.add(sb_sql_acitivity);
			}
			hstCols = getTableItemMap("pub_wf_transition");
			for (int j = 0; j < al_billtemplets_transition.size(); j++) {
				org.jdom.Element node_billtemplet_transition = (org.jdom.Element) al_billtemplets_transition.get(j); // 每个模板结点对象
				InsertSQLBuilder sb_sql_transition = new InsertSQLBuilder("pub_wf_transition");

				String pub_transition_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");

				List transitionAllNode = node_billtemplet_transition.getChildren(); //得到所有字段
				for (int k = 0; k < transitionAllNode.size(); k++) {
					org.jdom.Element childNode = (Element) transitionAllNode.get(k);
					String itemName = childNode.getName().toUpperCase();
					String itemValue = childNode.getText();
					if (TBUtil.isEmpty(itemValue)) {
						continue;
					}
					if (hstCols.contains(itemName)) { //数据库有这个字段
						sb_sql_transition.putFieldValue(itemName, itemValue);
					}
				}
				sb_sql_transition.putFieldValue("ID", pub_transition_id);
				sb_sql_transition.putFieldValue("PROCESSID", pub_process_id);
				sb_sql_transition.putFieldValue("FROMACTIVITY", (String) hashmap.get(node_billtemplet_transition.getChild("fromactivity").getText()));
				sb_sql_transition.putFieldValue("TOACTIVITY", (String) hashmap.get(node_billtemplet_transition.getChild("toactivity").getText()));
				al.add(sb_sql_transition);
			}
			hstCols = getTableItemMap("pub_wf_group");
			for (int j = 0; j < al_billtemplets_group.size(); j++) {
				org.jdom.Element node_billtemplet_group = (org.jdom.Element) al_billtemplets_group.get(j); // 每个模板结点对象

				InsertSQLBuilder sb_sql_group = new InsertSQLBuilder("pub_wf_group");
				String pub_group_id = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
				List groupAllNode = node_billtemplet_group.getChildren(); //得到所有字段
				for (int k = 0; k < groupAllNode.size(); k++) {
					org.jdom.Element childNode = (Element) groupAllNode.get(k);
					String itemName = childNode.getName().toUpperCase();
					String itemValue = childNode.getText();
					if (TBUtil.isEmpty(itemValue)) {
						continue;
					}
					if (hstCols.contains(itemName)) { //数据库有这个字段
						sb_sql_group.putFieldValue(itemName, itemValue);
					}
				}
				sb_sql_group.putFieldValue("ID", pub_group_id);
				sb_sql_group.putFieldValue("PROCESSID", pub_process_id);
				al.add(sb_sql_group);
			}
		}
		commdmo.executeBatchByDS(null, al);
	}

	/**
	 * 导出XML格式注册按钮
	 */
	public String exportXMLRegButton(String[] str_refbutton_code, int[] selectrows) throws Exception {
		CommDMO commdmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < selectrows.length; i++) {
			String str_templet = str_refbutton_code[i];// billlistpanel.getValueAt(selectrows[i],
			// "TEMPLETCODE").toString();
			// //
			HashVO[] hashvo = commdmo.getHashVoArrayByDS(null, "select * from pub_regbuttons where id='" + str_templet + "'");

			if (hashvo.length > 0) {
				String[] str_allparentcolumns = hashvo[0].getKeys(); //
				for (int j = 0; j < hashvo.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo[j].getStringValue("code") + "******************************** -->\r\n"); //     
					sb_xml.append("<refbutton>\r\n"); //                
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns.length; q++) {
						String parent_content = hashvo[j].getStringValue(str_allparentcolumns[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						}
					}

					sb_xml.append("</refbutton>"); //
					sb_xml.append("\r\n"); //
				}
			}
			if (i != selectrows.length - 1) {
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 导出XML格式注册样板
	 */
	public String exportXMLRegFormat(String[] ids) throws Exception {
		CommDMO cmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer();
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //

		for (int i = 0; i < ids.length; i++) {

			String str_code = ids[i];

			String sql = "select * from pub_regformatpanel where id = '" + str_code + "'";

			HashVO[] hvs = cmo.getHashVoArrayByDS(null, sql);
			if (hvs.length > 0) {
				String[] parent = hvs[0].getKeys();// 所有列
				for (int j = 0; j < hvs.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hvs[j].getStringValue("code") + "******************************** -->\r\n"); //        
					sb_xml.append("<regformat>\r\n"); //                
					// 处理主表信息
					for (int k = 0; k < parent.length; k++) {
						String parent_content = hvs[j].getStringValue(parent[k]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + parent[k].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + parent[k].toLowerCase() + ">\r\n"); //

						} else {
							sb_xml.append("  <" + parent[k].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + parent[k].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</regformat>"); //
					sb_xml.append("\r\n"); //
				}
			}
			if (i != ids.length - 1) {
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //

		return sb_xml.toString();

	}

	/**
	 * 导出XML格式注册参照
	 */
	public String exportXMLRegRef(String[] str_refref_code, int[] selectrows) throws Exception {
		CommDMO commdmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < selectrows.length; i++) {
			String str_templet = str_refref_code[i];// billlistpanel.getValueAt(selectrows[i],
			// "TEMPLETCODE").toString();
			// //
			HashVO[] hashvo = commdmo.getHashVoArrayByDS(null, "select * from pub_refregister where id='" + str_templet + "'");
			if (hashvo.length > 0) {
				String[] str_allparentcolumns = hashvo[0].getKeys(); //
				for (int j = 0; j < hashvo.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo[j].getStringValue("name") + "******************************** -->\r\n"); //     
					sb_xml.append("<refregister>\r\n"); //              
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns.length; q++) {
						String parent_content = hashvo[j].getStringValue(str_allparentcolumns[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</refregister>"); //
					sb_xml.append("\r\n"); //
				}
			}
			if (i != selectrows.length - 1) {
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 导出XML格式的Excel
	 * @param templetcode
	 * @return
	 */
	public String exportXMLForExcel(String templetcode) {
		CommDMO commdmo = new CommDMO();
		StringBuffer output = null;
		HashVO[] excelHash = null;
		HashVO[] cellHash = null;
		try {
			excelHash = commdmo.getHashVoArrayByDS(null, "select * from pub_billcelltemplet_h where templetcode='" + templetcode + "'");
			if (excelHash != null && excelHash.length == 1) {
				cellHash = commdmo.getHashVoArrayByDS(null, "select * from pub_billcelltemplet_d where templet_h_id=" + excelHash[0].getStringValue("id"));
			} else {
				return "该模板不在数据库中；或数据库中数据冗余！";
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		output = new StringBuffer();
		for (int i = 0; i < excelHash.length; i++) {
			output.append("<exportanybill type=\"EXCEL模板\">\r\n");
			output.append("\t<exceltemplet>\r\n");
			output.append("\t\t<id>" + excelHash[0].getStringValue("id") + "</id>\r\n");
			output.append("\t\t<templetcode>" + (excelHash[0].getStringValue("templetcode") == null ? "" : excelHash[0].getStringValue("templetcode")) + "</templetcode>\r\n");
			output.append("\t\t<templetname>" + (excelHash[0].getStringValue("templetname") == null ? "" : excelHash[0].getStringValue("templetname")) + "</templetname>\r\n");
			output.append("\t\t<descr>" + (excelHash[0].getStringValue("descr") == null ? "" : excelHash[0].getStringValue("descr")) + "</descr>\r\n");
			output.append("\t\t<billno>" + (excelHash[0].getStringValue("billno") == null ? "" : excelHash[0].getStringValue("billno")) + "</billno>\r\n");
			output.append("\t\t<rowlength>" + (excelHash[0].getStringValue("rowlength") == null ? "" : excelHash[0].getStringValue("rowlength")) + "</rowlength>\r\n");
			output.append("\t\t<collength>" + (excelHash[0].getStringValue("collength") == null ? "" : excelHash[0].getStringValue("collength")) + "</collength>\r\n");
			if (cellHash != null) {
				for (int j = 0; j < cellHash.length; j++) {
					output.append(getCellValue(cellHash[j], j + 1));
				}
			}
			output.append("\t</exceltemplet>\r\n");
			output.append("</exportanybill>\r\n");
		}
		return output.toString();
	}

	/**
	 * 导出Excel的单元格成为XML格式
	 * 
	 * @param theCellHash
	 * @param order
	 * @return String
	 */
	public String getCellValue(HashVO theCellHash, int order) {
		if (theCellHash == null) {
			return "";
		}
		StringBuffer output = new StringBuffer();
		output.append("\t\t<templet_item showorder=\"" + order + "\">\r\n");
		output.append("\t\t\t<id>" + (theCellHash.getStringValue("id")) + "</id>\r\n");
		output.append("\t\t\t<templet_h_id>" + (theCellHash.getStringValue("templet_h_id") == null ? "" : theCellHash.getStringValue("templet_h_id")) + "</templet_h_id>\r\n");
		output.append("\t\t\t<cellrow>" + (theCellHash.getStringValue("cellrow") == null ? "" : theCellHash.getStringValue("cellrow")) + "</cellrow>\r\n");
		output.append("\t\t\t<cellcol>" + (theCellHash.getStringValue("cellcol") == null ? "" : theCellHash.getStringValue("cellcol")) + "</cellcol>\r\n");
		if (theCellHash.getStringValue("cellvalue") != null && (theCellHash.getStringValue("cellvalue").indexOf(">") >= 0 || theCellHash.getStringValue("cellvalue").indexOf("<") >= 0)) {
			output.append("\t\t\t<cellvalue><![CDATA[" + (theCellHash.getStringValue("cellvalue") == null ? "" : theCellHash.getStringValue("cellvalue")) + "]]></cellvalue>\r\n");
		} else {
			output.append("\t\t\t<cellvalue>" + (theCellHash.getStringValue("cellvalue") == null ? "" : theCellHash.getStringValue("cellvalue")) + "</cellvalue>\r\n");
		}
		output.append("\t\t\t<foreground>" + (theCellHash.getStringValue("foreground") == null ? "" : theCellHash.getStringValue("foreground")) + "</foreground>\r\n");
		output.append("\t\t\t<background>" + (theCellHash.getStringValue("background") == null ? "" : theCellHash.getStringValue("background")) + "</background>\r\n");
		output.append("\t\t\t<fonttype>" + (theCellHash.getStringValue("fonttype") == null ? "" : theCellHash.getStringValue("fonttype")) + "</fonttype>\r\n");
		output.append("\t\t\t<fontstyle>" + (theCellHash.getStringValue("fontstyle") == null ? "" : theCellHash.getStringValue("fontstyle")) + "</fontstyle>\r\n");
		output.append("\t\t\t<fontsize>" + (theCellHash.getStringValue("fontsize") == null ? "" : theCellHash.getStringValue("fontsize")) + "</fontsize>\r\n");
		output.append("\t\t\t<span>" + (theCellHash.getStringValue("span") == null ? "" : theCellHash.getStringValue("span")) + "</span>\r\n");
		output.append("\t\t\t<rowheight>" + (theCellHash.getStringValue("rowheight") == null ? "" : theCellHash.getStringValue("rowheight")) + "</rowheight>\r\n");
		output.append("\t\t\t<colwidth>" + (theCellHash.getStringValue("colwidth") == null ? "" : theCellHash.getStringValue("colwidth")) + "</colwidth>\r\n");
		output.append("\t\t\t<cellhelp>" + (theCellHash.getStringValue("cellhelp") == null ? "" : theCellHash.getStringValue("cellhelp")) + "</cellhelp>\r\n");
		output.append("\t\t\t<celltype>" + (theCellHash.getStringValue("celltype") == null ? "" : theCellHash.getStringValue("celltype")) + "</celltype>\r\n");
		output.append("\t\t\t<iseditable>" + (theCellHash.getStringValue("iseditable") == null ? "" : theCellHash.getStringValue("iseditable")) + "</iseditable>\r\n");
		output.append("\t\t\t<cellkey>" + (theCellHash.getStringValue("cellkey") == null ? "" : theCellHash.getStringValue("cellkey")) + "</cellkey>\r\n");
		output.append("\t\t\t<celldesc>" + (theCellHash.getStringValue("celldesc") == null ? "" : theCellHash.getStringValue("celldesc")) + "</celldesc>\r\n");
		output.append("\t\t\t<valign>" + (theCellHash.getStringValue("valign") == null ? "" : theCellHash.getStringValue("valign")) + "</valign>\r\n");
		output.append("\t\t\t<halign>" + (theCellHash.getStringValue("halign") == null ? "" : theCellHash.getStringValue("halign")) + "</halign>\r\n");
		if (theCellHash.getStringValue("loadformula") != null && (theCellHash.getStringValue("loadformula").indexOf(">") >= 0 || theCellHash.getStringValue("loadformula").indexOf("<") >= 0)) {
			output.append("\t\t\t<loadformula><![CDATA[" + (theCellHash.getStringValue("loadformula") == null ? "" : theCellHash.getStringValue("loadformula")) + "]]></loadformula>\r\n");
		} else {
			output.append("\t\t\t<loadformula>" + (theCellHash.getStringValue("loadformula") == null ? "" : theCellHash.getStringValue("loadformula")) + "</loadformula>\r\n");
		}
		if (theCellHash.getStringValue("editformula") != null && (theCellHash.getStringValue("editformula").indexOf(">") >= 0 || theCellHash.getStringValue("editformula").indexOf("<") >= 0)) {
			output.append("\t\t\t<editformula><![CDATA[" + (theCellHash.getStringValue("editformula") == null ? "" : theCellHash.getStringValue("editformula")) + "]]></editformula>\r\n");
		} else {
			output.append("\t\t\t<editformula>" + (theCellHash.getStringValue("editformula") == null ? "" : theCellHash.getStringValue("editformula")) + "</editformula>\r\n");
		}
		if (theCellHash.getStringValue("validateformula") != null && (theCellHash.getStringValue("validateformula").indexOf(">") >= 0 || theCellHash.getStringValue("validateformula").indexOf("<") >= 0)) {
			output.append("\t\t\t<validateformula><![CDATA[" + (theCellHash.getStringValue("validateformula") == null ? "" : theCellHash.getStringValue("validateformula")) + "]]></validateformula>\r\n");
		} else {
			output.append("\t\t\t<validateformula>" + (theCellHash.getStringValue("validateformula") == null ? "" : theCellHash.getStringValue("validateformula")) + "</validateformula>\r\n");
		}
		output.append("\t\t</templet_item>\r\n");
		return output.toString();
	}

	/**
	 * 导出XML格式下拉框表
	 */
	public String exportXMLCombobox(String[] str_combobox_code, int[] selectrows) throws Exception {
		CommDMO commdmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < selectrows.length; i++) {
			String str_templet = str_combobox_code[i];// billlistpanel.getValueAt(selectrows[i],
			// "TEMPLETCODE").toString();
			// //

			HashVO[] hashvo = commdmo.getHashVoArrayByDS(null, "select * from pub_comboboxdict where TYPE='" + str_templet + "'");

			if (hashvo.length > 0) {
				String[] str_allparentcolumns = hashvo[0].getKeys(); //
				for (int j = 0; j < hashvo.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo[j].getStringValue("type") + "******************************** -->\r\n"); //     
					sb_xml.append("<combobox>\r\n"); //             
					// 处理主表信息

					for (int q = 0; q < str_allparentcolumns.length; q++) {
						String parent_content = hashvo[j].getStringValue(str_allparentcolumns[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</combobox>"); //
					sb_xml.append("\r\n"); //
				}
			}
			if (i != selectrows.length - 1) {
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 导出XML格式流程
	 */
	public String exportXMLProcess(String[] str_process_code, int[] selectrows) throws Exception {
		CommDMO commdmo = new CommDMO();
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < selectrows.length; i++) {
			String str_templet = str_process_code[i];// billlistpanel.getValueAt(selectrows[i],
			// "TEMPLETCODE").toString();
			// //
			HashVO[] hashvo = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_process where id='" + str_templet + "'");
			if (hashvo.length > 0) {
				String[] str_allparentcolumns = hashvo[0].getKeys(); //
				for (int j = 0; j < hashvo.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo[j].getStringValue("name") + "******************************** -->\r\n"); //     
					sb_xml.append("<process>\r\n"); //              
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns.length; q++) {
						String parent_content = hashvo[j].getStringValue(str_allparentcolumns[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns[q].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</process>"); //
					sb_xml.append("\r\n"); //
				}
			}
			HashVO[] hashvo_activity = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_activity where processid='" + str_templet + "'");
			if (hashvo_activity.length > 0) {
				String[] str_allparentcolumns_activity = hashvo_activity[0].getKeys(); //
				for (int j = 0; j < hashvo_activity.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo_activity[j].getStringValue("wfname") + "******************************** -->\r\n"); //      
					sb_xml.append("<activity>\r\n"); //             
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns_activity.length; q++) {
						String parent_content = hashvo_activity[j].getStringValue(str_allparentcolumns_activity[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns_activity[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns_activity[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns_activity[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns_activity[q].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</activity>"); //
					sb_xml.append("\r\n"); //
				}
			}

			HashVO[] hashvo_group = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_group where processid='" + str_templet + "'");
			if (hashvo_group.length > 0) {
				String[] str_allparentcolumns_group = hashvo_group[0].getKeys(); //
				for (int j = 0; j < hashvo_group.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo_group[j].getStringValue("wfname") + "******************************** -->\r\n"); //     
					sb_xml.append("<group>\r\n"); //                
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns_group.length; q++) {
						String parent_content = hashvo_group[j].getStringValue(str_allparentcolumns_group[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns_group[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns_group[q].toLowerCase() + ">\r\n"); //

						} else {
							sb_xml.append("  <" + str_allparentcolumns_group[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns_group[q].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</group>"); //
					sb_xml.append("\r\n"); //
				}
			}
			HashVO[] hashvo_line = commdmo.getHashVoArrayByDS(null, "select * from pub_wf_transition where processid='" + str_templet + "'");
			if (hashvo_line.length > 0) {
				String[] str_allparentcolumns_line = hashvo_line[0].getKeys(); //
				for (int j = 0; j < hashvo_line.length; j++) {
					sb_xml.append("<!-- ****************************(" + (i + 1) + ")" + hashvo_line[j].getStringValue("wfname") + "******************************** -->\r\n"); //      
					sb_xml.append("<transition>\r\n"); //               
					// 处理主表信息
					for (int q = 0; q < str_allparentcolumns_line.length; q++) {
						String parent_content = hashvo_line[j].getStringValue(str_allparentcolumns_line[q]);
						if (parent_content != null && (parent_content.indexOf(">") >= 0 || parent_content.indexOf("<") >= 0)) {
							sb_xml.append("  <" + str_allparentcolumns_line[q].toLowerCase() + "><![CDATA[" + (parent_content == null ? "" : parent_content) + "]]></" + str_allparentcolumns_line[q].toLowerCase() + ">\r\n"); //
						} else {
							sb_xml.append("  <" + str_allparentcolumns_line[q].toLowerCase() + ">" + (parent_content == null ? "" : parent_content) + "</" + str_allparentcolumns_line[q].toLowerCase() + ">\r\n"); //
						}
					}
					sb_xml.append("</transition>"); //
					sb_xml.append("\r\n"); //
				}
			}

			if (i != selectrows.length - 1) {
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 遍历ID值 返回IN的条件 武坤萌
	 * 
	 * @param theHash
	 * @return
	 */
	public String getID(HashVO[] theHash, String isID) {
		StringBuffer returnString = null;
		if (theHash == null || theHash.length == 0) {
			return "";
		}
		returnString = new StringBuffer();
		returnString.append("(");
		if (isID != null) {
			for (int i = 0; i < theHash.length; i++) {
				if (i == theHash.length - 1) {
					returnString.append(theHash[i].getStringValue(isID));
				} else {
					returnString.append(theHash[i].getStringValue(isID) + ",");
				}
			}
			returnString.append(")");
			return returnString.toString();
		}
		for (int i = 0; i < theHash.length; i++) {
			if (i == theHash.length - 1) {
				returnString.append(theHash[i].getStringValue("id"));
			} else {
				returnString.append(theHash[i].getStringValue("id") + ",");
			}
		}
		returnString.append(")");
		return returnString.toString();
	}

	/**
	 * 在两个数据源中迁移模板!! 
	 */
	public void transportTempletToDataSource(String[] templete_codes, String str_sourceDS, Object[] _destDSs) throws Exception {
		//导出XML格式模版
		String str_xml = exportXMLTemplet(templete_codes, new int[] { 1 }, str_sourceDS); //
		//向各个数据源导入XML格式模版
		for (int i = 0; i < _destDSs.length; i++) {
			importXMLTemplet(_destDSs[i].toString(), str_xml);
		}
	}

	/**
	 * 导出数据库中所有表的tables.xml【李春娟/2012-08-24】
	 */

	public String exportAllTables() throws Exception {
		CommDMO commdmo = new CommDMO();
		MetaDataDMO datadmo = new MetaDataDMO();
		String[][] alltables = commdmo.getAllSysTableAndDescr(null, null, false, false);//取得数据库中所有的表,不包括视图，返回表名、类型（TABLE或VIEW）、说明
		if (alltables == null || alltables.length == 0) {
			return null;
		}
		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		HashVO[] hashvos = (HashVO[]) datadmo.getAllXmlTemplet_(null, null).get(0);//将所有xml都找出来

		for (int i = 0; i < alltables.length; i++) {
			String tablename = alltables[i][0];
			TableDataStruct tabstruct = commdmo.getTableDataStructByDS(null, "select * from " + tablename + " where 1=2", false);

			String[] tabHeaders = tabstruct.getHeaderName();//字段名称
			String[] tabHeaderTypes = tabstruct.getHeaderTypeName();//字段类型
			int[] tabHeaderLength = tabstruct.getHeaderLength();//字段长度
			int[] tabPrecision = tabstruct.getPrecision();
			int[] tabScale = tabstruct.getScale();
			boolean isfindtemplet = false;//是否存在该表模板

			if (hashvos != null && hashvos.length > 0) {
				for (int z = 0; z < hashvos.length; z++) {
					if (tablename.equalsIgnoreCase(hashvos[z].getStringValue("tablename"))) {//查询表
						Pub_Templet_1VO templetVO = datadmo.getPub_Templet_1VO(hashvos[z].getStringValue("templetcode"));
						if (templetVO != null) {//如果找到模板，则根据模板中配置的字段名称及模板名称来补充说明字段
							isfindtemplet = true;
							sb_xml.append("<table name=\"" + tablename.toLowerCase() + "\"  pkname=\"" + templetVO.getPkname() + "\"  descr=\"" + (alltables[i][2] == null ? templetVO.getTempletname() : alltables[i][2]) + "\">\r\n"); //   
							sb_xml.append("  <columns>\r\n");
							for (int j = 0; j < tabHeaders.length; j++) {
								String coltype = convertColType(tabHeaderTypes[j]);
								sb_xml.append("    <col name=\"");
								sb_xml.append(convertStr(tabHeaders[j].toLowerCase() + "\"", 20));
								sb_xml.append(" type=\"" + convertStr(coltype + "\"", 10));
								sb_xml.append(" length=\"");
								if ("decimal".equalsIgnoreCase(coltype)) {
									if (tabScale[j] == 0) {
										sb_xml.append(convertStr(tabPrecision[j] + "\"", 10));
									} else {
										sb_xml.append(convertStr(tabPrecision[j] + "," + tabScale[j] + "\"", 10));
									}
								} else {
									sb_xml.append(convertStr(tabHeaderLength[j] + "\"", 10));
								}
								sb_xml.append(" descr=\"");
								Pub_Templet_1_ItemVO itemvo = templetVO.getItemVo(tabHeaders[j].toLowerCase());
								if (itemvo != null) {
									sb_xml.append(templetVO.getItemVo(tabHeaders[j].toLowerCase()).getItemname()); // 
								}
								sb_xml.append("\"/>\r\n");
							}
							break;
						}
					}
				}
			}
			if (!isfindtemplet) {//如果没有找到模板
				String pkname = "id";
				boolean hasid = false;//是否有id字段
				for (int j = 0; j < tabHeaders.length; j++) {
					if ("id".equalsIgnoreCase(tabHeaders[j])) {
						hasid = true;
						break;
					}
				}
				if (!hasid) {//如果没有id字段，先暂时将第一个字段作为主键
					pkname = tabHeaders[0].toLowerCase();
				}
				sb_xml.append("<table name=\"" + tablename.toLowerCase() + "\"  pkname=\"" + pkname + "\"  descr=\"" + (alltables[i][2] == null ? "" : alltables[i][2]) + "\">\r\n"); //   
				sb_xml.append("  <columns>\r\n");
				for (int j = 0; j < tabHeaders.length; j++) {
					sb_xml.append("    <col name=\"" + convertStr(tabHeaders[j].toLowerCase() + "\"", 20) + " type=\"" + convertStr(convertColType(tabHeaderTypes[j]) + "\"", 10) + " length=\"" + convertStr(tabHeaderLength[j] + "\"", 10) + "	descr=\"\"/>\r\n"); //    
				}

			}

			sb_xml.append("  </columns>\r\n");
			//以后这里也要增加现有的索引
			sb_xml.append("</table>\r\n\r\n");
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	public String convertStr(String _oldstr, int _num) {
		if (_oldstr == null || "".equals(_oldstr)) {
			return "  ";
		}
		int i = _oldstr.getBytes().length;
		StringBuffer sb_str = new StringBuffer(_oldstr);
		for (; i < _num; i++) {
			sb_str.append(" ");
		}
		return sb_str.toString();
	}

	/**
	 * 将各种数据库字段类型转换成mysql的字段类型【李春娟/2012-08-27】
	 * @param _oldtype
	 * @return
	 */
	private String convertColType(String _oldtype) {
		String str_oldtype = _oldtype.toUpperCase(); // //
		if (str_oldtype.startsWith("NUMBER") || str_oldtype.startsWith("DECIMAL") || str_oldtype.startsWith("INTEGER")) { //
			return "decimal"; //
		} else if (str_oldtype.startsWith("VARCHAR")) {
			return "varchar"; //
		} else if (str_oldtype.startsWith("CHAR") || str_oldtype.startsWith("CHARACTER")) {
			return "char"; //
		} else if (str_oldtype.startsWith("TEXT") || str_oldtype.startsWith("CLOB")) {
			return "text"; //
		} else {
			return "varchar"; //
		}
	}

	public static void main(String[] _args) {
		XMLIOUtil util = new XMLIOUtil(); //
		HashMap map1 = new HashMap(); //
		Vector v_1 = new Vector();
		v_1.add("aaa"); //
		v_1.add("bbb"); //
		v_1.add("ccc"); //

		Vector v_2 = new Vector();
		v_2.add("xxx"); //
		v_2.add("yyy"); //
		v_2.add("zzz"); //

		map1.put("key1", v_1);
		map1.put("key2", v_2);
		map1.put("key3", "cccc");

		try {
			util.writeObjToXML(map1, "C:\\123.xml");
			System.out.println("输出对象成XML成功!!"); //
			Object obj = util.loadObjFromXML("C:\\123.xml");
			HashMap map2 = (HashMap) obj; //
			String str_value3 = (String) map2.get("key3"); //
			System.out.println("成功恢复对象，值=[" + str_value3 + "]"); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
