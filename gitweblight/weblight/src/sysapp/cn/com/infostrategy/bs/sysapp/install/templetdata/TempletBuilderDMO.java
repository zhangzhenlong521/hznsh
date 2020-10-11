package cn.com.infostrategy.bs.sysapp.install.templetdata;

import java.io.InputStream;
import java.util.ArrayList;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;

public class TempletBuilderDMO {

	/**
	 * 根据XML文件名构造TMO,然后用它来构造单据模板!!!
	 * @param _xmlFile
	 * @return
	 * @throws Exception
	 */
	public DefaultTMO buildDefaultTMOFromXMLFile(String _xmlFile) throws Exception {
		return this.buildDefaultTMOFromXMLFile(_xmlFile, false);
	}
	
	
	public DefaultTMO buildDefaultTMOFromXMLFile(String _xmlFile, boolean issim) throws Exception {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		InputStream fileInStream = this.getClass().getResourceAsStream(_xmlFile); //!!!
		if (fileInStream == null) {
			throw new WLTAppException("没有在资源路径中找到xml文件[" + _xmlFile + "]"); ////
		}
		org.jdom.Document doc = builder.build(fileInStream); // 加载XML!!
		java.util.List list_records = doc.getRootElement().getChildren("record");

		HashVO parentVO = new HashVO(); //
		for (int i = 0; i < list_records.size(); i++) { //遍历!!!
			org.jdom.Element element = (org.jdom.Element) list_records.get(i); //
			String str_tabName = element.getAttributeValue("tabname"); //表名
			if (str_tabName.equalsIgnoreCase("pub_templet_1")) {
				java.util.List list_cols = element.getChildren(); //
				for (int j = 0; j < list_cols.size(); j++) { //遍历各列
					org.jdom.Element colnode = (org.jdom.Element) list_cols.get(j); //列结点
					String str_colName = colnode.getAttributeValue("name"); //列名!!!
					String str_colValue = colnode.getValue(); //
					if (str_colValue != null && str_colValue.trim().equals("")) {
						str_colValue = null;
					}
					parentVO.setAttributeValue(str_colName, str_colValue); //设置!!!
				}
				break; //
			}
		}
		
		if(issim) {
			return new DefaultTMO(parentVO, null); //
		}

		ArrayList al_childVOs = new ArrayList(); //
		for (int i = 0; i < list_records.size(); i++) { //遍历!!!
			org.jdom.Element element = (org.jdom.Element) list_records.get(i); //
			String str_tabName = element.getAttributeValue("tabname"); //表名
			if (str_tabName.equalsIgnoreCase("pub_templet_1_item")) { //如果是子表!!!
				HashVO childVO = new HashVO(); //
				java.util.List list_cols = element.getChildren(); //
				for (int j = 0; j < list_cols.size(); j++) { //遍历各列
					org.jdom.Element colnode = (org.jdom.Element) list_cols.get(j); //列结点
					String str_colName = colnode.getAttributeValue("name"); //列名!!!
					String str_colValue = colnode.getValue(); //
					if (str_colValue != null && str_colValue.trim().equals("")) {
						str_colValue = null;
					}
					childVO.setAttributeValue(str_colName, str_colValue); //设置!!!
				}
				al_childVOs.add(childVO); //
			}
		}
		HashVO[] childVOs = (HashVO[]) al_childVOs.toArray(new HashVO[0]); //
		return new DefaultTMO(parentVO, childVOs); //
	}

	/**
	 * 将XML中的数据插入数据库!!!需要处理主外键的关系!!!
	 * @param _xmlFile
	 * @throws Exception
	 */
	public void insertDBFromXML(String _xmlFile) throws Exception {

	}
}
