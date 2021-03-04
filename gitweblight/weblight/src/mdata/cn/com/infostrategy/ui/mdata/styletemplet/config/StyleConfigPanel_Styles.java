/**************************************************************************
 * $RCSfile: StyleConfigPanel_Styles.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:58 $
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.sysapp.login.StyleTempletDefineBuilder;
import cn.com.infostrategy.to.sysapp.login.StyleTempletDefineVO;
import cn.com.infostrategy.ui.common.WLTButton;
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
public class StyleConfigPanel_Styles extends AbstractTempletRefPars implements ActionListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -6147862007359706108L;

	private WLTButton btnAddFont, btnDelFont, resetFont, clearText; //

	private MultiStyleTextPanel textPanel = null;
	private JTextField textField = null;

	private static int[][] column_width = null; //���п�
	private BillListPanel listPanel_allstyles = new BillListPanel(new TMO_AllStyles()); //����ģ��
	private BillListPanel listPanel_templet = new BillListPanel(new TMO_TEMPLET()); //����ģ��

	private JScrollPane jsp_sys_exp = null; //������
	private MouseAdapter adapter = null; //������
	private WLTTabbedPane jtp_detail = null; //��ҳǩ
	private JPanel southPanel = null;
	private final static Font textPaneFont = new Font("����", Font.PLAIN, 16);//

	public StyleConfigPanel_Styles(String _text) {
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(_text)); //
	}

	private JPanel getCenterPanel(String _text) {
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //  
		btnAddFont = new WLTButton("��������");
		btnDelFont = new WLTButton("��С����");
		resetFont = new WLTButton("��������"); //
		clearText = new WLTButton("���"); //

		btnAddFont.addActionListener(this);
		btnDelFont.addActionListener(this);
		resetFont.addActionListener(this);
		clearText.addActionListener(this); //

		panel_north.add(btnAddFont); //
		panel_north.add(btnDelFont); //
		panel_north.add(resetFont); //
		panel_north.add(clearText); //

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(panel_north, BorderLayout.NORTH);

		textPanel = new MultiStyleTextPanel(_text); // 
		textPanel.addKeyWordStyle(new String[] { "���ģ������" }, Color.RED, true); ////
		textPanel.addKeyWordStyle(new StyleTempletDefineBuilder().gettAllStyleTempletNames(), Color.MAGENTA, false); //

		jtp_detail = getDetailPane(); //
		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textPanel), jtp_detail); //
		splitPane.setDividerLocation(500); //
		panel.add(splitPane, BorderLayout.CENTER); //
		return panel;
	}

	/**
	 * ����±ߵ�ҳǩ���
	 * 
	 * @return
	 */
	private WLTTabbedPane getDetailPane() {
		if (jtp_detail != null) {
			return jtp_detail;
		}
		jtp_detail = new WLTTabbedPane(); //
		StyleTempletDefineBuilder stdb = new StyleTempletDefineBuilder(); //
		StyleTempletDefineVO[] stdvos = stdb.getAllStyleTempletDefineVOs(); //
		for (int i = 0; i < stdvos.length; i++) {
			int li_newrow = listPanel_allstyles.addEmptyRow(); //
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getName()), li_newrow, "Name"); //����
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getDescr()), li_newrow, "Descr"); //˵��
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getFormulaDefine()), li_newrow, "Formula"); //��ʽ
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getDefaultClassName()), li_newrow, "ClassName"); //ʵ����
		}
		listPanel_allstyles.clearSelection(); //

		listPanel_allstyles.addBillListMouseDoubleClickedListener(this); //
		listPanel_templet.addBillListMouseDoubleClickedListener(this); //

		jtp_detail.addTab("���з��ģ��", listPanel_allstyles); //
		jtp_detail.addTab("Ԫԭģ��", listPanel_templet); //
		jtp_detail.setPreferredSize(new Dimension(425, 500)); //
		return jtp_detail;
	}

	/**
	 * �ж��ı������Ƿ���ѡ�е��ı�
	 * 
	 * @return
	 */
	private boolean isTextSelected() {
		return (textPanel.getSelectionEnd() - textPanel.getSelectionStart()) != 0;
	}

	/**
	 * ���ϵͳ��ʽ
	 * 
	 * @return
	 */
	private String[][] getExpression() {
		Vector vec_function = FormatEventBindFormulaParse.getFunctionDetail();
		String[][] str_values = new String[vec_function.size()][];
		for (int i = 0; i < str_values.length; i++) {
			str_values[i] = (String[]) vec_function.get(i);
		}
		return str_values;
	}

	public VectorMap getParameters() {
		VectorMap map = new VectorMap(); //		
		map.put("FORMATFORMULA", textPanel.getText().trim()); //
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddFont) { //��������
			addFont();
		} else if (e.getSource() == btnDelFont) { //��С����
			delFont();
		} else if (e.getSource() == resetFont) {
			resetFont();
		} else if (e.getSource() == clearText) {
			clearText(); //
		}
	}

	private void addFont() {
		int li_size = textPanel.getFont().getSize();
		textPanel.setFont(new Font("����", Font.PLAIN, li_size + 2)); //
		textPanel.updateUI();
	}

	private void delFont() {
		int fontsize = textPanel.getFont().getSize();
		int li_newSize = fontsize - 2; //
		if (li_newSize < 9) {
			li_newSize = 9;
		}
		textPanel.setFont(new Font("����", Font.PLAIN, li_newSize)); //
		textPanel.updateUI();
	}

	private void resetFont() {
		textPanel.setFont(new Font("����", Font.PLAIN, 12)); //
		textPanel.updateUI();
	}

	private void clearText() {
		textPanel.setText(""); //
	}

	private void putStrIntextArea(String _text) {
		textPanel.inputText(_text); //
	}

	public MultiStyleTextPanel getTextArea() {
		return textPanel;
	}

	/**
	 * ˫���¼�..
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		if (_event.getSource() == listPanel_allstyles) {
			String str_formula = _event.getCurrSelectedVO().getStringValue("Formula"); //
			putStrIntextArea(str_formula); //
		} else if (_event.getSource() == listPanel_templet) {
			String str_templetCode = _event.getCurrSelectedVO().getStringValue("TEMPLETCODE"); //
			putStrIntextArea(str_templetCode); //
		}
	}

	class TMO_AllStyles extends AbstractTMO {
		private static final long serialVersionUID = 1L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "allstyles"); // ģ�����,��������޸�
			vo.setAttributeValue("templetname", "���з��ģ��"); // ģ������
			vo.setAttributeValue("templetname_e", "AllStyles"); // ģ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector(); //
			HashVO itemVO = null; //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Name"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Name"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
			itemVO.setAttributeValue("listisshowable", "Y"); // �б��Ƿ���ʾ
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭ��� 
			itemVO.setAttributeValue("listwidth", "75"); //�б��� 
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Descr"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
			itemVO.setAttributeValue("itemname_e", "Descr"); // ��ʾ����
			itemVO.setAttributeValue("listisshowable", "Y"); // �б��Ƿ���ʾ
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardwidth", "175"); //��Ƭ��� 
			itemVO.setAttributeValue("listwidth", "85"); //�б��� 
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Formula"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ʽ"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); // �ؼ�����
			itemVO.setAttributeValue("itemname_e", "Formula"); // ��ʾ����
			itemVO.setAttributeValue("listisshowable", "Y"); // �б��Ƿ���ʾ
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardwidth", "600*100"); //�б��� 
			itemVO.setAttributeValue("listwidth", "150"); //��Ƭ���
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ��� 
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ClassName"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ʵ����"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
			itemVO.setAttributeValue("itemname_e", "DefaultClassName"); // ��ʾ����
			itemVO.setAttributeValue("listisshowable", "N"); // �б��Ƿ���ʾ
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭ�Ƿ���ʾ
			itemVO.setAttributeValue("cardwidth", "600"); //�б��� 
			itemVO.setAttributeValue("listwidth", "150"); //��Ƭ���
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ��� 
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]); //
		}

	}

	class TMO_TEMPLET extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "pub_templet_1"); // ģ�����,��������޸�
			vo.setAttributeValue("templetname", "Ԫԭģ��"); // ģ������
			vo.setAttributeValue("templetname_e", "Templet"); // ģ������
			vo.setAttributeValue("tablename", "pub_templet_1"); // ��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", "ID"); // ������
			vo.setAttributeValue("pksequencename", null); // ������
			vo.setAttributeValue("savedtablename", null); // �������ݵı���
			vo.setAttributeValue("CardWidth", "577"); // ��Ƭ���
			vo.setAttributeValue("Isshowlistpagebar", "N"); // �б��Ƿ���ʾ��ҳ��
			vo.setAttributeValue("Isshowlistopebar", "N"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("listcustpanel", null); // �б��Զ������
			vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������

			vo.setAttributeValue("TREEPK", "id"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeviewfield", "name"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeseqfield", "seq"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeisshowroot", "Y"); // �б��Ƿ���ʾ������ť��
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TEMPLETCODE"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Templetcode"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "1"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", "getItemValue(\"ID\")"); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "175"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
			itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
			itemVO.setAttributeValue("querywidth", "60,60"); //
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TEMPLETNAME"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Templetname"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "3"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "125"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "225"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
			itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
			itemVO.setAttributeValue("querywidth", "60,70"); //
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
