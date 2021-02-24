/**************************************************************************
 * $RCSfile: StyleConfigPanel_0A.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTMouseAdapterAsMsgBox;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.MultiStyleTextPanel;
import cn.com.infostrategy.ui.mdata.formatcomp.FormatEventBindFormulaParse;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;

/**
 * ��ʽ���������ҳ��...
 * 
 * @author xch
 * 
 */
public class StyleConfigPanel_0A extends AbstractTempletRefPars implements ActionListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -6147862007359706108L;

	private WLTButton btn_adfont, btn_delfont, btn_resetfont, btn_clear;

	private MultiStyleTextPanel textPanel = null;
	private JTextField textField = null;

	private static int[][] column_width = null;
	private Vector v_btns = new Vector(); //

	private BillListPanel listPanel_formaElementList = null; //�ؼ��б�
	private BillListPanel listPanel_templet = null; //����ģ��..
	private BillListPanel listPanel_regformat = null; //����ע������..
	private BillListPanel listPanel_eventBind = null; //�¼��󶨹�ʽ..

	private String[] str_items = null;

	private WLTTabbedPane tabPanel = null; //
	private JScrollPane jsp_sys_exp = null;
	private MouseAdapter adapter = null;
	private JTabbedPane jtp_detail = null;
	private JPanel southPanel = null;

	private StyledDocument doc = null;

	private SimpleAttributeSet sab_doc;
	private final static Font textPaneFont = new Font("����", Font.PLAIN, 16);//

	/**
	 * ���췽��...
	 * @param _text
	 */
	public StyleConfigPanel_0A(String _text) {
		this.setLayout(new BorderLayout());
		str_items = getStrs(_text); //
		btn_adfont = new WLTButton("����+"); //
		btn_delfont = new WLTButton("����-"); //
		btn_resetfont = new WLTButton("��������"); //
		btn_clear = new WLTButton("���"); //
		btn_adfont.addActionListener(this); //
		btn_delfont.addActionListener(this); //
		btn_resetfont.addActionListener(this); //
		btn_clear.addActionListener(this); //
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(btn_adfont); //
		panel_north.add(btn_delfont); //
		panel_north.add(btn_resetfont); //
		panel_north.add(btn_clear); //

		textPanel = new MultiStyleTextPanel(str_items[0]); // 
		textPanel.addKeyWordStyle(new String[] { "getCard", "getList", "getTree", "getCell", "getWorkFlow", "getChart", "getChartReal", "getSplit", "getVFlowLayout", "getTab", "getPanel", // 
				"getRegisterFormat", "getQuery", "getButtons", "getRadioTab", "getLevel", "getBorderLayout", "getBom", "getMultiReport", "getLabel" }, Color.BLUE, false); //

		tabPanel = new WLTTabbedPane(); //
		listPanel_formaElementList = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.sysapp.servertmo.TMO_FormatPanelGetItem"));
		String[][] str_data = getFormatElementDefine();
		for (int i = 0; i < str_data.length; i++) {
			int li_newRow = listPanel_formaElementList.addEmptyRow(false); //
			listPanel_formaElementList.setValueAt(new StringItemVO(str_data[i][0]), li_newRow, "����");
			listPanel_formaElementList.setValueAt(new StringItemVO(str_data[i][1]), li_newRow, "����");
			listPanel_formaElementList.setValueAt(new StringItemVO(str_data[i][2]), li_newRow, "˵��");
		}

		listPanel_templet = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_PubTemplet_1")); //����ģ��
		listPanel_regformat = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_RegFormatTemplet")); //����formatģ��
		listPanel_eventBind = new BillListPanel(new DefaultTMO("�����¼�", new String[][] { { "����", "120" }, { "˵��", "150" } }));
		String[][] str_eventBind = getEventBindDefine(); //�õ������¼�����
		for (int i = 0; i < str_eventBind.length; i++) {
			int li_newRow = listPanel_eventBind.addEmptyRow(false); //
			listPanel_eventBind.setValueAt(new StringItemVO(str_eventBind[i][0]), li_newRow, "����");
			listPanel_eventBind.setValueAt(new StringItemVO(str_eventBind[i][1]), li_newRow, "˵��");
		}

		listPanel_formaElementList.addBillListMouseDoubleClickedListener(this); //
		listPanel_templet.addBillListMouseDoubleClickedListener(this); //
		listPanel_regformat.addBillListMouseDoubleClickedListener(this); //
		listPanel_eventBind.addBillListMouseDoubleClickedListener(this); //

		tabPanel.addTab("�ؼ��б�", listPanel_formaElementList); //
		tabPanel.addTab("Ԫԭģ��", listPanel_templet); //
		tabPanel.addTab("ע������", listPanel_regformat); //

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textPanel), tabPanel); //
		splitPane.setDividerLocation(550); //

		this.add(panel_north, BorderLayout.NORTH);
		this.add(splitPane, BorderLayout.CENTER); //
		this.add(getSourthPanel(), BorderLayout.SOUTH); //
	}

	/**
	 * �����¼������ҳǩ,��ע������ά��ҳ����,��һ����¼�������õ����!
	 */
	public void addEventBindTab() {
		tabPanel.addTab("�����¼�", listPanel_eventBind); //
	}

	/**
	 * ���ϵͳ��ʽ,���¼�����
	 * @return
	 */
	private String[][] getEventBindDefine() {
		Vector vec_function = FormatEventBindFormulaParse.getFunctionDetail();
		String[][] str_values = new String[vec_function.size()][];
		for (int i = 0; i < str_values.length; i++) {
			str_values[i] = (String[]) vec_function.get(i);
		}
		return str_values;
	}

	/**
	 * ���������ı���
	 * @return
	 */
	public JPanel getSourthPanel() {
		if (southPanel != null) {
			return southPanel;
		}
		southPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		JLabel label = new JLabel(" ������: ", JLabel.RIGHT); //
		southPanel.add(label, BorderLayout.WEST); //
		textField = new JTextField(str_items[1]); //
		textField.addMouseListener(new WLTMouseAdapterAsMsgBox(textField, 2, "����ʵ�ֽӿ�[cn.com.infostrategy.ui.mdata.styletemplet.format.IUIIntercept_0A]")); //
		textField.setPreferredSize(new Dimension(300, 20)); //
		textField.setForeground(Color.BLUE);
		southPanel.add(textField, BorderLayout.CENTER); //
		return southPanel; //
	}

	public VectorMap getParameters() {
		VectorMap map = new VectorMap(); //		
		map.put("FORMATFORMULA", textPanel.getText().trim()); //
		if (textField.getText() != null && !textField.getText().trim().equals("")) {
			map.put("INTERCEPTNAME", textField.getText().trim()); //
		}
		return map;
	}

	public void stopEdit() {

	}

	protected String bsInformation() {
		return null;
	}

	protected String uiInformation() {
		return null;
	}

	private String[] getStrs(String _text) {
		if (_text == null || _text.equals("")) {
			return new String[] { "", "" };
		}
		String[] str_return = new String[] { "", "" }; //
		int li_pos = _text.indexOf("INTERCEPTNAME");
		if (li_pos > 0) {
			str_return[0] = _text.substring(14, li_pos - 1); //
			str_return[1] = _text.substring(li_pos + 14, _text.length()); //
		} else {
			str_return[0] = _text.substring(14, _text.length());
		}
		return str_return; //
	}

	/**
	 * �����ť
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_adfont) {
			addFont(); //
		} else if (e.getSource() == btn_delfont) {
			deFont(); //
		} else if (e.getSource() == btn_resetfont) {
			textPanel.setFont(new Font("����", Font.PLAIN, 12)); //
			//textPanel.updateUI();
		} else if (e.getSource() == btn_clear) {
			textPanel.setText(""); //
		}
	}

	private void addFont() {
		int li_size = textPanel.getFont().getSize();
		int li_newSize = li_size + 3; //
		if (li_newSize > 60) {
			li_newSize = 60;
		}
		textPanel.setFont(new Font("����", Font.PLAIN, li_newSize)); //
		//textPanel.updateUI();
	}

	private void deFont() {
		int fontsize = textPanel.getFont().getSize();
		int li_newSize = fontsize - 3; //
		if (li_newSize < 8) {
			li_newSize = 8;
		}
		textPanel.setFont(new Font("����", Font.PLAIN, li_newSize)); //
		//textPanel.updateUI();
	}

	public WLTTabbedPane getTabPanel() {
		return tabPanel;
	}

	private void putStrIntextArea(String _text) {
		textPanel.inputText(_text); //
	}

	public JTextPane getTextArea() {
		return textPanel;
	}

	/**
	 * ˫��
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		if (_event.getBillListPanel() == listPanel_formaElementList) {
			int li_row = listPanel_formaElementList.getSelectedRow();
			String str_text = listPanel_formaElementList.getValueAt(li_row, "����").toString();
			putStrIntextArea(str_text); //
		} else if (_event.getBillListPanel() == listPanel_templet) {

		} else if (_event.getBillListPanel() == listPanel_regformat) {

		}
	}

	/**
	 * ��ʽ����
	 * @return
	 */
	private String[][] getFormatElementDefine() {
		return new String[][] { //
		{ "��ǩ", "getLabel(\"abcd\")", getHelpDescr_getLabel() }, //
				{ "��Ƭ", "getCard(\"test001\")", "���������������ֱ�Ӵ���һ��BillCardPanel�����൱��ֱ�ӵ���new BillCardPanel(\"test001\")" }, //
				{ "�б�", "getList(\"test001\")", "���������������ֱ�Ӵ���һ��BillListPanel�����൱��ֱ�ӵ���new BillListPanel(\"test001\")" }, //
				{ "��", "getTree(\"test001\")", "�����������ֱ�Ӵ���һ��BillTreePanel�����൱��ֱ�ӵ���new BillTreePanel(\"test001\")" }, //
				{ "��ѯ��", "getQuery(\"test001\")", "���������������һ�����ٲ�ѯ��壬���ٲ�ѯ����뿨Ƭ�����ƣ������һ����ѯ��ť����Ӧ������BillQueryPanel,��֧��addBillQuickActionListener()�����������൱�ڵ����� new BillQueryPanel(\"test001\")" }, //��ѯ��.������
				{ "��ť��", "getButtons(\"����\",\"�޸�\",\"ɾ��\")", "���������������һ����ť������ν��ť������һ������������Ͽ��Է�һ�Ű�ť����Ӧ������BillButtonPanel\r\n�ù�ʽ֧��������������һ����������һ����ť,�ڰ�ť�ϵ���Ҽ������Բ鿴��ť��ϸ��Ϣ" }, //��ť��.������
				{ "Excel", "getCell(\"test001\")", "���������������һ����Excel�Ŀؼ�����Ӧ������BillCellPanel���൱��ֱ�ӵ���new BillCellPanel(\"test001\")" }, //
				{ "������", "getWorkFlow(\"test001\")", "���������������һ���������ؼ�����Ӧ������WorkFlowDesignWPanel" }, //
				{ "������", "getWorkPanel(\"ģ�����\")", "���������������һ������������棬ֻ��ʾ��Ӧģ�������" }, //
				{ "��ʾͼ��", "getChart(\"����ͳ�Ʊ�<��������>\",\"���\",\"���յȼ�\",\"2005��,2006��,2007��,2008��,2009��,2010��,2011��\",\"�������,�߷���,�еȷ���,�ͷ���,��С����\")", "���������������һ����ʾ��ͼ��ؼ�,��Ӧ������BillChartPanel" }, //
				{ "SQLͼ��", "getChartReal(\"�������ͳ�Ʊ�\",\"���\",\"����\",\"select a,b,count(*) from table group by a,b\",\"false\")", "���������������һ��SQLֱ�Ӵ���һ��ͼ��ؼ�����Ӧ������BillChartPanel" }, //

				{ "��ά����", "getMultiReport(\"pub_corp_dept_CODE1\",\"�̳��ڳ�����:cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter\")", "���������,��ά����" }, //
				{ "��ʾ��ά����", "getMultiReport(\"WLTDUAL\",\"cn.com.infostrategy.bs.report.DemoReportBuilder\")", "���������,Ϊ�˿�����ʾ�õ���ά����" }, //ÿ��ȥ��ǰ��ʾǰ��Ҫ��һ����ά������ʾ����,�ܷ�!���Ա�������ʾͼ��һ���и�������ʾ����ά����!��xch/2012-06-11��

				{ "�ָ���", "getSplit(getLabel(\"�������\"),getLabel(\"�ұ�����\"),\"����\",200)", getHelpDescr_getSplit() }, //
				{ "��ҳǩ", "getTab(\"��һҳǩ\",getLabel(\"����1\"),\"�ڶ�ҳǩ\",getLabel(\"����2\"))", getHelpDescr_getTab() }, //
				{ "��ť�Ͷ�ҳǩ", "getRadioTab(\"��һҳǩ\",getLabel(\"����1\"),\"�ڶ�ҳǩ\",getLabel(\"����2\"))", getHelpDescr_getRadioTab() }, //��ť�Ͷ�ҳǩ
				{ "���", "getLevel(\"level_1\",getLabel(\"��һ��\"),\"level_2\",getLabel(\"�ڶ���\"))", "" }, //���
				{ "��ֱ����", "getVFlowLayout(getCard(\"test001\"),getList(\"test001\"),getCard(\"test001\"))", "" }, //
				{ "Border����", "getBorderLayout(getQuery(\"test001\"),getList(\"test001\"),\"\",\"������\")", "" }, //��������������,����ʹ��BorderLayout����,���봹ֱ���ֵ��������л��Զ�����,�м�������Զ����

				{ "BOMͼ", "getBom(\"bom001\")", "" }, //
				{ "�������", "getPanel(\"cn.com.cmbc.TestPanel\")", "" }, //
				{ "ע������", "getRegisterFormat(\"����_��λ\")", "" }, //

		};
	}

	private String getHelpDescr_getLabel() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("�����������ֱ�����һ���ַ���,ʵ�ʷ��ص���JPanel����,������˸�Jabel\r\n");
		sb_help.append("�����������÷�ʽ��һ���Ǿ�һ����������getLabel(\"��ӭ����\")��һ���Ƕ�������������:getLabel(\"content\",\"�����ѯ������ѯ����\"),��һ���Ǳ�ʶ�õı��룬�ڶ�����������ʵ����ʾ���ַ�����Ȼ�����ͨ��getLabelPanelByCode(\"content\")�õ��������,�����ػ�\r\n");
		sb_help.append("���һ�����þ���������һ����ѯ����������һ��LabelPanel����Ȼ������ѯʱ�ػ�����������£�\r\n");
		sb_help.append("public class Intercept_XCH implements IUIIntercept_0A, ActionListener {\r\n");
		sb_help.append("  \r\n");
		sb_help.append("  private BillQueryPanel queryPanel = null;  //�����ѯ�����\r\n");
		sb_help.append("  private JPanel contentPanel = null;  //������ʾ��ѯ���ݵ�������\r\n");
		sb_help.append("  //ʵ�ֽӿڷ�����ʵ��ҵ���߼�\r\n");
		sb_help.append("  public void afterSysInitialize(AbstractStyleWorkPanel_0A _panel) throws Exception {\r\n");
		sb_help.append("    queryPanel = _panel.getBillQueryPanel();  //��ȡ�ò�ѯ����\r\n");
		sb_help.append("    contentPanel = _panel.getLabelPanelByCode(\"content\"); //ȡ����ʾ���ݵ��������ע�⣬������õ��˸��ݱ���ȥȡ�ķ�����\r\n");
		sb_help.append("    queryPanel.addBillQuickActionListener(this); //�Բ�ѯ�����ӵ����ѯ�ļ�������\r\n");
		sb_help.append("  }\r\n");
		sb_help.append("\r\n");
		sb_help.append("  //�����ѯ��Ҫ�����߼�,�����ػ����\r\n");
		sb_help.append("  public void actionPerformed(ActionEvent e) {\r\n");
		sb_help.append("      contentPanel.removeAll(); //�Ƚ��������������ɾ����\r\n");
		sb_help.append("      contentPanel.setLayout(new BorderLayout()); //�������ò��֣�һ��Ҫ����һ���������������ӵĿؼ�����ʾ���ˣ����Ǿ����׷��Ĵ���\r\n");
		sb_help.append("      contentPanel.add(new BillCellPanel(\"�Ϲ��׼_09\")); //����������һ��Excel��壬ʵ��Ӧ�ø�������Ĳ�ѯ��������ƴ��SQL��������ȣ��������ҵ���߼��������б���Ƭ�����ȸ�����������������У�Ҳ����ֱ����ҵ���߼����ڣ���\r\n");
		sb_help.append("      contentPanel.updateUI();  //ˢ��һ��UI����ʱ��һ����Ҫ����\r\n");
		sb_help.append("  }\r\n");
		sb_help.append("}\r\n");
		return sb_help.toString();
	}

	private String getHelpDescr_getSplit() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("����������������һ�����һ����·ָ����������Ĳ��֣�ÿ�������ڿ����Ǹ������������\r\n");
		sb_help.append("һ���ĸ����������У�\r\n");
		sb_help.append("��һ����ڶ�����������������һ�����������������getCard(\"test001\")����getList(\"test002\")��..\r\n");
		sb_help.append("����������������ȡֵ��\"����\"��\"����\",������ʾ��ʲô�����Ϸָ�\r\n");
		sb_help.append("���ĸ�������ʾ�ָ������ϱ߻���ߵľ���");
		return sb_help.toString();
	}

	private String getHelpDescr_getTab() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("����������������һ����ҳǩ�Ĳ��֣�ÿ��ҳǩ�ڿ����Ǹ������������\r\n");
		sb_help.append("������������������������ǳ�˫�ɶԵģ�����������������ż���������1�����2����һ�ԣ���3�����4����һ��\r\n");
		sb_help.append("��һ���У���һ��������������ʾ��ҳǩ���ϵ��֣�����ֻ�֧��[]������ͼƬ���֣�����[office_042.gif]�����������ǰ���Լ�����һ��Сͼ�ꡣ��\r\n");
		sb_help.append("�ڶ������������Ǹ�����������������翨Ƭ���б�����\r\n");
		sb_help.append("�����﷨�磺getTab(\"[office_050.gif]��һҳǩ\",getList(\"test001\"),\"�ڶ�ҳǩ\",getCard(\"test002\"))");
		return sb_help.toString();
	}

	private String getHelpDescr_getRadioTab() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("����������������һ����ť�Ͷ�ҳǩ�Ĳ��֣�������һ��Radio(����)�Ͱ�ť�����ÿһ������ʾһ��ҳ�棬����getTab()������һ������������չʾ�е㲻ͬ��������Radio�Ͱ�ť�е��û���ϲ��Щ..\r\n");
		sb_help.append("������������������������ǳ�˫�ɶԵģ�����������������ż���������1�����2����һ�ԣ���3�����4����һ��\r\n");
		sb_help.append("��һ���У���һ��������������ʾ��ҳǩ���ϵ��֣�����ֻ�֧��[]������ͼƬ���֣�����[office_042.gif]�����������ǰ���Լ�����һ��Сͼ�ꡣ��\r\n");
		sb_help.append("�ڶ������������Ǹ�����������������翨Ƭ���б�����\r\n");
		sb_help.append("�����﷨�磺getTab(\"[office_050.gif]��һҳǩ\",getList(\"test001\"),\"�ڶ�ҳǩ\",getCard(\"test002\"))");
		return sb_help.toString();
	}
}
