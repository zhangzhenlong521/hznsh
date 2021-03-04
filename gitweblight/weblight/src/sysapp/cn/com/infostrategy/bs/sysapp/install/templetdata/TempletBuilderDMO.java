package cn.com.infostrategy.bs.sysapp.install.templetdata;

import java.io.InputStream;
import java.util.ArrayList;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;

public class TempletBuilderDMO {

	/**
	 * ����XML�ļ�������TMO,Ȼ�����������쵥��ģ��!!!
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
			throw new WLTAppException("û������Դ·�����ҵ�xml�ļ�[" + _xmlFile + "]"); ////
		}
		org.jdom.Document doc = builder.build(fileInStream); // ����XML!!
		java.util.List list_records = doc.getRootElement().getChildren("record");

		HashVO parentVO = new HashVO(); //
		for (int i = 0; i < list_records.size(); i++) { //����!!!
			org.jdom.Element element = (org.jdom.Element) list_records.get(i); //
			String str_tabName = element.getAttributeValue("tabname"); //����
			if (str_tabName.equalsIgnoreCase("pub_templet_1")) {
				java.util.List list_cols = element.getChildren(); //
				for (int j = 0; j < list_cols.size(); j++) { //��������
					org.jdom.Element colnode = (org.jdom.Element) list_cols.get(j); //�н��
					String str_colName = colnode.getAttributeValue("name"); //����!!!
					String str_colValue = colnode.getValue(); //
					if (str_colValue != null && str_colValue.trim().equals("")) {
						str_colValue = null;
					}
					parentVO.setAttributeValue(str_colName, str_colValue); //����!!!
				}
				break; //
			}
		}
		
		if(issim) {
			return new DefaultTMO(parentVO, null); //
		}

		ArrayList al_childVOs = new ArrayList(); //
		for (int i = 0; i < list_records.size(); i++) { //����!!!
			org.jdom.Element element = (org.jdom.Element) list_records.get(i); //
			String str_tabName = element.getAttributeValue("tabname"); //����
			if (str_tabName.equalsIgnoreCase("pub_templet_1_item")) { //������ӱ�!!!
				HashVO childVO = new HashVO(); //
				java.util.List list_cols = element.getChildren(); //
				for (int j = 0; j < list_cols.size(); j++) { //��������
					org.jdom.Element colnode = (org.jdom.Element) list_cols.get(j); //�н��
					String str_colName = colnode.getAttributeValue("name"); //����!!!
					String str_colValue = colnode.getValue(); //
					if (str_colValue != null && str_colValue.trim().equals("")) {
						str_colValue = null;
					}
					childVO.setAttributeValue(str_colName, str_colValue); //����!!!
				}
				al_childVOs.add(childVO); //
			}
		}
		HashVO[] childVOs = (HashVO[]) al_childVOs.toArray(new HashVO[0]); //
		return new DefaultTMO(parentVO, childVOs); //
	}

	/**
	 * ��XML�е����ݲ������ݿ�!!!��Ҫ����������Ĺ�ϵ!!!
	 * @param _xmlFile
	 * @throws Exception
	 */
	public void insertDBFromXML(String _xmlFile) throws Exception {

	}
}
