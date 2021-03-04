package cn.com.pushworld.salary.bs.report_;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * XPath操作XML文件 
 */

public class XmlReportEngine {
	private Document document = null;

	public XmlReportEngine(String _filename) {
		String xml_path = "/cn/com/pushworld/salary/bs/report_/xml/";
		//String xml_path = "G:\\pushine\\files\\";
		document = getDocument(xml_path + _filename + ".xml");
	}

	/**
	 * 报表配置信息
	 */
	public ReportConfigVO getReportConfigVO() {
		ReportConfigVO rcvo = new ReportConfigVO();
		rcvo.setLefttitle(getNodeAttributeValues("/reports/report/left/title/@tname"));
		rcvo.setToptitle(getNodeAttributeValues("/reports/report/top/title/@tname"));
		rcvo.setCross(getNodeAttributeValues("/reports/report/cross/result/@cname"));

		rcvo.setLeftvos(getTitleNodeValues("/reports/report/left/title"));
		rcvo.setTopvos(getTitleNodeValues("/reports/report/top/title"));
		rcvo.setCrossvos(getCrossNodeValues("/reports/report/cross/result"));

		String topagain = getSingleNodeAttributeValue("/reports/report/cross/@topagain");
		if (!topagain.equals("")) {
			rcvo.setTopagain(topagain.split(";"));
		}

		String topagain_name = getSingleNodeAttributeValue("/reports/report/cross/@topagain_name");
		if (!topagain_name.equals("")) {
			rcvo.setTopagain_name(topagain_name.split(";"));
		}

		if (getSingleNodeAttributeValue("/reports/report/cross/@isseparation").equals("N")) {
			rcvo.setSeparation(false);
		}
		if (getSingleNodeAttributeValue("/reports/report/cross/@isfront_leftagain").equals("Y")) {
			rcvo.setFront_leftagain(true);
		}
		if (getSingleNodeAttributeValue("/reports/report/cross/@isfront_topagain").equals("Y")) {
			rcvo.setFront_topagain(true);
		}
		if (getSingleNodeAttributeValue("/reports/report/left/@iscombine").equals("N")) {
			rcvo.setCombine_left(false);
		}

		rcvo.setSql(getNodeValue("/reports/report/sql"));

		return rcvo;
	}

	private Document getDocument(String _filename) {
		try {
			SAXReader saxReader = new SAXReader();
			InputStream input = this.getClass().getResourceAsStream(_filename);
			return saxReader.read(input);
			//return saxReader.read(new File(_filename));
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 节点属性值组 /reports/report/left/title/@tname
	 */
	private String[] getNodeAttributeValues(String _attrname) {
		ArrayList<String> al = new ArrayList<String>();

		List list = document.selectNodes(_attrname);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Attribute attribute = (Attribute) iter.next();
			al.add(attribute.getValue());
		}

		return (String[]) al.toArray(new String[al.size()]);
	}

	/**
	 * 单节点属性值 /reports/report/cross/@isseparation
	 */
	private String getSingleNodeAttributeValue(String _attrname) {
		Attribute attribute = (Attribute) document.selectSingleNode(_attrname);

		if (attribute != null) {
			return attribute.getValue();
		}
		return "";
	}

	/**
	 * 节点值 /reports/report/sql
	 */
	private String getNodeValue(String _node) {
		List list = document.selectNodes(_node);

		if (list.size() > 0) {
			Element sql = (Element) list.get(0);
			return sql.getText();
		}
		return "";
	}

	/**
	 * 节点信息组 /reports/report/left/title
	 */
	private ReportConfigTitleVO[] getTitleNodeValues(String _nodename) {
		ArrayList<ReportConfigTitleVO> al = new ArrayList<ReportConfigTitleVO>();

		List list = document.selectNodes(_nodename);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			ReportConfigTitleVO rctvo = new ReportConfigTitleVO();
			Element element = (Element) iter.next();

			Attribute tname = element.attribute("tname");
			if (tname != null) {
				rctvo.setTname(tname.getValue());
			}

			Attribute subagain = element.attribute("subagain");
			if (subagain != null) {
				String value = subagain.getValue();
				if (!value.equals("")) {
					rctvo.setSubagain(value.split(";"));
				}
			}

			Attribute subagain_name = element.attribute("subagain_name");
			if (subagain_name != null) {
				String value = subagain_name.getValue();
				if (!value.equals("")) {
					rctvo.setSubagain_name(value.split(";"));
				}
			}

			Attribute orders = element.attribute("orders");
			if (orders != null) {
				String value = orders.getValue();
				if (!value.equals("")) {
					rctvo.setOrders(value.split(";"));
				}
			}

			al.add(rctvo);
		}

		return (ReportConfigTitleVO[]) al.toArray(new ReportConfigTitleVO[al.size()]);
	}

	/**
	 * 节点信息组 /reports/report/cross/result
	 */
	private ReportConfigCrossVO[] getCrossNodeValues(String _nodename) {
		ArrayList<ReportConfigCrossVO> al = new ArrayList<ReportConfigCrossVO>();

		List list = document.selectNodes(_nodename);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			ReportConfigCrossVO rccvo = new ReportConfigCrossVO();
			Element element = (Element) iter.next();

			Attribute cname = element.attribute("cname");
			if (cname != null) {
				rccvo.setCname(cname.getValue());
			}

			Attribute cname_name = element.attribute("cname_name");
			if (cname_name != null) {
				rccvo.setCname_name(cname_name.getValue());
			}

			Attribute calculate = element.attribute("calculate");
			if (calculate != null) {
				rccvo.setCalculate(calculate.getValue());
			}

			Attribute leftagain = element.attribute("leftagain");
			if (leftagain != null) {
				String value = leftagain.getValue();
				if (!value.equals("")) {
					rccvo.setLeftagain(value.split(";"));
				}
			}

			Attribute leftagain_name = element.attribute("leftagain_name");
			if (leftagain_name != null) {
				String value = leftagain_name.getValue();
				if (!value.equals("")) {
					rccvo.setLeftagain_name(value.split(";"));
				}
			}

			al.add(rccvo);
		}

		return (ReportConfigCrossVO[]) al.toArray(new ReportConfigCrossVO[al.size()]);
	}

}
