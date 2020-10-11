package cn.com.infostrategy.ui.mdata;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cn.com.infostrategy.bs.sysapp.servertmo.TMO_AutoBuildData;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * �Զ�����Demo���ݣ�
 * 
 * @author hm
 * 
 */
public class AutoBuildDataDialog extends BillDialog implements ActionListener, BillListMouseDoubleClickedListener {
	private Pub_Templet_1VO templet;
	private Pub_Templet_1_ItemVO[] templetItem;
	private WLTButton btn_buildData, btn_exportXML, btn_importXML,btn_help;

	private BillListPanel listPanel;

	public AutoBuildDataDialog(Container _parent, Pub_Templet_1VO _templet, Pub_Templet_1_ItemVO[] _templetItem) {
		super(_parent);
		this.templet = _templet;
		this.templetItem = _templetItem;
		initialize();
	}

	private void initialize() {
		initListPanel();
		this.getContentPane().add(listPanel);
		this.setSize(new Dimension(650, 600));
		this.locationToCenterPosition();
		this.setVisible(true);
	}

	public void initListPanel() {
		listPanel = new BillListPanel(new TMO_AutoBuildData());
		btn_buildData = new WLTButton("����Demo����");
		btn_exportXML = new WLTButton("����Xml");
		btn_importXML = new WLTButton("����Xml");
		btn_help = new WLTButton("�������",UIUtil.getImage("office_037.gif"));
		btn_buildData.addActionListener(this);
		btn_exportXML.addActionListener(this);
		btn_importXML.addActionListener(this);
		btn_help.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_buildData, btn_exportXML, btn_importXML ,btn_help});
		listPanel.addBillListMouseDoubleClickedListener(this);
		listPanel.repaintBillListButton();
		for (int i = 0; i < templetItem.length; i++) {
			Pub_Templet_1_ItemVO itemVO = templetItem[i];
			if (itemVO.isNeedSave()) {
				String itemKey = itemVO.getItemkey();
				String itemName = itemVO.getItemname();
				HashMap value = new HashMap();
				value.put("itemkey", new StringItemVO(itemKey));
				if (itemVO.getIsmustinput()) {
					itemKey = "*" + itemKey;
				}
				value.put("name", new StringItemVO(itemKey));
				value.put("info", new StringItemVO(itemName));
				if (itemVO.isPrimaryKey()) {
					value.put("type", new ComBoxItemVO("����", "����", "����"));
				}
				listPanel.insertRowWithInitStatus(-1, value);
			}
		}
		listPanel.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_buildData) {
			onBuildData();
		} else if (obj == btn_exportXML) {
			onExportXML();
		} else if (obj == btn_importXML) {
			onImportXML();
		} else if (obj == btn_help){
			onHelp();
		}
	}

	public void onBuildData() { // ��������Demo����
		String str = JOptionPane.showInputDialog(this, "������������ɵ�����������");
		if (str == null || str.equals("")) {
			return;
		}
		String saveTable = templet.getSavedtablename();
		if(!checkConfig()){
			return;
		}
		try {
			UIUtil.getMetaDataService().buildDemoData(saveTable, listPanel.getAllBillVOs(), Integer.parseInt(str));
			MessageBox.show(this, "�������ɳɹ���");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, new Throwable(e));
		}
	}

	/*
	 * �������
	 */
	public boolean checkConfig() {
		String saveTable = templet.getSavedtablename();
		boolean ren = true;
		try {
			String count = UIUtil.getStringValueByDS(null, "select count("+ templet.getPkname() +") from " + saveTable + " where id<0");
			if (!"0".equals(count)) {
				boolean flag = MessageBox.confirm(this, "��" + saveTable + "���а���IDΪ����ֵ���Ƿ�ɾ������");
				if (flag) {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + saveTable + " where id <0" });
				} else {
					ren = false;
				}
			}
		} catch (Exception e) {
			ren = false;
			MessageBox.showException(this, new Throwable(e));
		}
		return ren;
	}

	/*
	 * ����������Ϣxml
	 */
	public void onExportXML() {
		BillVO vos[] = listPanel.getAllBillVOs();
		StringBuffer sb = new StringBuffer(); // дmain.xml���ļ�
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n");
		sb.append("<root table=\"" + templet.getSavedtablename() + "\">\r\n");
		for (int i = 0; i < vos.length; i++) {
			String str_itemValue = vos[i].getStringValue("value", "");
			str_itemValue = str_itemValue.replaceAll("<", "&ls;");
			str_itemValue = str_itemValue.replaceAll(">", "&gt;");
			str_itemValue = str_itemValue.replaceAll("&", "&amp;");
			str_itemValue = str_itemValue.replaceAll("'", "&apos;");
			str_itemValue = str_itemValue.replaceAll("\"", "&quot;");
			sb.append("<item key=\"" + vos[i].getStringValue("itemkey") + "\" type=\"" + vos[i].getStringValue("type", "") + "\" value=\"" + str_itemValue + "\" autono =\"" + vos[i].getStringValue("autono", "N") + "\"/>\r\n");
		}
		sb.append("</root>\r\n");
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ����Ŀ¼");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path);
		if (!f.exists()) {
			MessageBox.show(this, "·��:" + str_path + " �����ڣ�");
			return;
		}
		f = new File(str_path + "/" + templet.getTablename() + ".xml");
		FileOutputStream output;
		try {
			output = new FileOutputStream(f);
			output.write(sb.toString().getBytes());
			output.flush();
			output.close();
			MessageBox.show(this, "�����ɹ�");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ����������Ϣxml
	 */
	public void onImportXML() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("����Demo����������Ϣ(*.xml)", "xml");
		chooser.addChoosableFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		BillVO allVOS[] = listPanel.getAllBillVOs();
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(file);
			Element element = document.getRootElement();
			String tableName = element.getAttributeValue("table");
			if (!templet.getTablename().equalsIgnoreCase(tableName)) {
				if (!MessageBox.confirm(this, "���ļ������д˱����ģ�������ܴ������⣬�Ƿ������")) {
					return;
				}
			}
			List itemElement = element.getChildren("item");
			for (int i = 0; i < itemElement.size(); i++) {
				Element item = (Element) itemElement.get(i);
				String key = item.getAttributeValue("key");
				String type = item.getAttributeValue("type");
				String value = item.getAttributeValue("value");
				String autono = item.getAttributeValue("autono");
				for (int j = 0; j < allVOS.length; j++) {
					if (key.equalsIgnoreCase(allVOS[j].getStringValue("itemkey"))) { // ���xml�е��ֶκ��б��е�һ����
						if (!"".equals(type)) {
							listPanel.setValueAt(new ComBoxItemVO(type, type, type), i, "type");
						}
						if (!"".equals(value)) {
							listPanel.setValueAt(new RefItemVO(value, "", value), i, "value");
						}
						listPanel.setValueAt(new StringItemVO(autono), i, "autono");
						break;
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void onHelp(){
		MessageBox.showTextArea(this, "������� �������� ����Ϊ�����С�\r\n��ֵ����ֵ�����÷ֺŰ�ѡ��ֵ��������дһ��select��ѯ��䣬Ĭ�϶�ȡ��һ������\r\n��������ֶζ�Ӧ������ͬһ�ű�������$+�ֶν��й���\r\n�����ƶ�" +
				"���ܲ���(blcorp)�����ܲ�������(blcorp_name)��������pub_corp_dept��\r\nblcorp�ֶ�ֵ����Ϊ��select id,name from pub_corp_dept����\r\nblcorp_name����Ϊ($blcorp)��\r\n��ˮ��ֻ���ڵ�ֵ�����0000*��������š�");
	}

	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {

	}
}
