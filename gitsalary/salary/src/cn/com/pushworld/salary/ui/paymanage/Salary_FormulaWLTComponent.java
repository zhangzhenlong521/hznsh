package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.undo.UndoManager;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ScrollablePopupFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.MultiStyleTextPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.PopQuickSearchDialog;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

/**
 * �Զ���ؼ� ��Ҫ�����û���д���Ӽ��㹫ʽ.����Ҫʵ�֣����Զ���ʾ�û�������д����ѡ����Щ���ݣ������Զ�������õ���ȷ�ԣ���ʶ��������ݡ����ӿɶ��Եȵȡ�
 * 
 * @author haoming create by 2013-6-24
 */
public class Salary_FormulaWLTComponent extends AbstractWLTCompentPanel implements KeyListener, ActionListener, MouseMotionListener {
	private static final long serialVersionUID = 5217782444939197020L;
	private JLabel label = null;
	public MultiStyleTextPanel textPanel = null;
	private Pub_Templet_1_ItemVO templetItemVO;
	private int li_width_all;

	private WLTButton btn_i_tip, btn_reflectOther, btn_fomula, btn_action;

	private HashVO allFactor[];

	private HashMap allFactorMap = new HashMap();

	private SalaryServiceIfc service; // ��Чģ��ͨ��Զ�̵��÷���

	private JPopupMenu rightClickKeyText = new JPopupMenu(); //�Ҽ��ؼ��ʵ���
	private JMenuItem right_view = new JMenuItem("�鿴");
	private JMenuItem right_edit = new JMenuItem("�༭");

	public Salary_FormulaWLTComponent(Pub_Templet_1_ItemVO _templetVO, BillPanel _billPanel) {
		super();
		templetItemVO = _templetVO; //
		billPanel = _billPanel;
		initialize();
	}

	private void initialize() {
		try {
			allFactor = UIUtil.getHashVoArrayByDS(null, "select * from sal_factor_def ");
			for (int i = 0; i < allFactor.length; i++) {
				allFactor[i].setToStringFieldName("name");
				allFactorMap.put(allFactor[i].getStringValue("name"), allFactor[i]); // ���뻺����
			}
			HashVO newvos[] = new HashVO[allFactor.length + 1];
			System.arraycopy(allFactor, 0, newvos, 1, allFactor.length);
			newvos[0] = new HashVO();
			newvos[0].setAttributeValue("name", "��������"); //
			newvos[0].setAttributeValue("show", "��������");
			newvos[0].setAttributeValue("$special", "");
			newvos[0].setToStringFieldName("name");
			allFactor = newvos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		int li_tablewidth = 300; //
		if (templetItemVO.getCardwidth() != null) {
			li_tablewidth = templetItemVO.getCardwidth().intValue(); //
		}

		int li_tableheight = 80; //
		if (templetItemVO.getCardHeight() != null) {
			li_tableheight = templetItemVO.getCardHeight().intValue() + 20; //
		}
		if (li_tableheight < 80) {
			li_tableheight = 80;
		}
		WLTPanel mainPanel = new WLTPanel(new BorderLayout());
		label = createLabel(templetItemVO); // ���ø����ṩ�ķ�������Label
		li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); // //
		this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
		this.add(label, BorderLayout.WEST); //
		textPanel = new MultiStyleTextPanel();
		addComponentUndAndRedoFunc(textPanel);
		textPanel.addKeyListener(this);
		textPanel.addMouseMotionListener(this);
		textPanel.addMouseListener(new MouseAction());
		JScrollPane scrollPane = new JScrollPane(textPanel);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		btn_reflectOther = new WLTButton("����", UIUtil.getImage("office_190.gif"));
		btn_fomula = new WLTButton("��ʽʾ��", UIUtil.getImage("office_053.gif"));
		btn_action = new WLTButton("���㿴", UIUtil.getImage("office_149.gif"));
		btn_reflectOther.addActionListener(this);
		btn_fomula.addActionListener(this);
		btn_action.addActionListener(this);
		btn_i_tip = new WLTButton(UIUtil.getImage("question-white.png"));
		WLTPanel btn_panel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
		btn_panel.add(btn_reflectOther);
		btn_panel.add(btn_fomula);
		btn_panel.add(btn_action);
		btn_panel.add(btn_i_tip);
		WLTLabel label = new WLTLabel("ctrl+˫���ı���ɷŴ�༭");
		label.setPreferredSize(new Dimension(170, 19));
		btn_panel.add(label);
		mainPanel.add(btn_panel, BorderLayout.SOUTH);
		rightClickKeyText.add(right_view);
		right_view.addActionListener(this);
		rightClickKeyText.add(right_edit);
		right_edit.addActionListener(this);
		this.add(mainPanel);
	}

	@Override
	public void focus() {

	}

	@Override
	public int getAllWidth() {
		return li_width_all;
	}

	@Override
	public String getItemKey() {
		return templetItemVO.getItemkey();
	}

	@Override
	public String getItemName() {
		return templetItemVO.getItemname();
	}

	@Override
	public JLabel getLabel() {
		return label;
	}

	@Override
	public Object getObject() {
		return textPanel.getText();
	}

	@Override
	public String getValue() {
		return textPanel.getText();
	}

	@Override
	public boolean isItemEditable() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public void setItemEditable(boolean bo) {
		btn_i_tip.setEnabled(bo);
		btn_reflectOther.setEnabled(bo);
		btn_fomula.setEnabled(bo);
		btn_action.setEnabled(bo);
		textPanel.setEditable(bo);
	}

	@Override
	public void setItemVisiable(boolean bo) {

	}

	@Override
	public void setObject(Object _obj) {
		if (_obj != null) {
			String str = null;
			if (_obj instanceof StringItemVO) {
				str = ((StringItemVO) _obj).getStringValue(); //
			} else {
				str = _obj.toString(); //
			}
			str = str.replaceAll("\r", ""); //����س��������滻��.������ȡλ��Ȼ���ַ���ȡ�����⡣
			setValue(str);
		} else {
			setValue(null); //
		}
	}

	@Override
	public void setValue(String value) {
		textPanel.setText(value);
	}

	MypopQuickSearchDialog popDialog;

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == btn_reflectOther) {
			showQuickSearchPopupMenu(POP_SHOW_YZ_IMPORTBTN);
		} else if (arg0.getSource() == btn_fomula) { // ��ʾ��ʽ
			onShowFormula();
		} else if (arg0.getSource() == btn_action) {
			onAction();
		} else if (arg0.getSource() == right_view) {
			right_look_edit(false);
		} else if (arg0.getSource() == right_edit) {
			right_look_edit(true);
		}
	}

	int popMenuX = 0;
	int popMenuY = 0;
	BillDialog showFormuladialog = null;

	//�Ҽ��鿴���ù�ʽ��
	private void right_look_edit(boolean _isedit) {
		String key = (String) rightClickKeyText.getClientProperty("key");
		if (key != null) {
			BillCardDialog carddialog = new BillCardDialog(this, "SAL_FACTOR_DEF_CODE1", _isedit ? WLTConstants.BILLDATAEDITSTATE_UPDATE : WLTConstants.BILLDATAEDITSTATE_INIT);
			carddialog.setTitle("[" + key + "]�鿴");
			carddialog.getBillcardPanel().queryDataByCondition(" name = '" + key + "'");
			if (_isedit) {
				carddialog.getBillcardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			}
			carddialog.setVisible(true);
		}
	}

	private void onShowFormula() {
		showFormuladialog = new BillDialog(textPanel);
		showFormuladialog.setSize(850, 600);
		Pub_Templet_1VO tmo = null;
		try {
			tmo = UIUtil.getPub_Templet_1VO(new DefaultTMO("��ʽ(˫��ʹ��,Ҳ��ѡ�е�Ԫ�����ݽ��и���ctrl+c)", new String[][] { { "��ʽ", "200" }, { "��ʽ���", "360" }, { "˵��", "200" }, { "ʾ��", "200" } }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		tmo.setIslistautorowheight(true);
		Pub_Templet_1_ItemVO itemvos[] = tmo.getItemVos();
		itemvos[0].setListisshowable(false);
		itemvos[1].setItemtype("�����ı���");
		final BillListPanel billList_sysFormula = new BillListPanel(tmo);
		billList_sysFormula.setTableToolTipText(null);
		billList_sysFormula.putClientProperty("POPMENUTYPE", 44);
		//		MouseAdapter
		MouseListener[] mouse = billList_sysFormula.getTable().getMouseListeners();
		billList_sysFormula.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billList_sysFormula.getTable().removeMouseListener(mouse[mouse.length - 1]); //
		billList_sysFormula.getTable().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) { // ��������
					if (e.getClickCount() == 2) {
						onMouseDoubleClicked(billList_sysFormula); //
					}
				}
			}
		});
		billList_sysFormula.setPreferredSize(new Dimension(720, 400));
		billList_sysFormula.putSolidValue(SalaryFomulaParseUtil.getFormulaExpression());
		billList_sysFormula.autoSetRowHeight();
		showFormuladialog.add(billList_sysFormula);
		showFormuladialog.locationToCenterPosition();
		showFormuladialog.setVisible(true);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (popDialog != null && popDialog.isVisible()) {
				popDialog.moveDown();
				e.consume(); // �ı���ִ�м����¼�
			}
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (popDialog != null && popDialog.isVisible()) {
				popDialog.moveUp();
				e.consume();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER && popDialog != null && popDialog.isShowing()) { // �س�������ֵ
			popDialog.setVisible(false);
			popDialog.setObject();
			e.consume();
		}

	}

	private String querytextValue; // �ֹ�¼�������
	private Timer timer = new Timer();

	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP) { // �ǻس���ִ��
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // ESC�����رյ�����
				return;
			}
			if (e.getKeyChar() == '[') {
				String txt = textPanel.getText();
				int caretPosition = textPanel.getCaretPosition();
				if (caretPosition == txt.length() || (caretPosition < textPanel.getText().length() - 1 && txt.charAt(caretPosition + 1) != ']')) { // �����Ų���[]
					try {
						textPanel.getDocument().insertString(caretPosition, "]", null);
						textPanel.setCaretPosition(caretPosition);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
			timer.cancel(); // ֹͣ
			timer.purge();// �ͷ�
			timer = new Timer();

			timer.schedule(new TimerTask() { // ��������
						public void run() {
							showQuickSearchPopupMenu(e.getKeyChar() == '.' ? POP_SHOW_DX_ITEM : POP_SHOW_YZ_BY_TEXTCHANGE);
						}
					}, 300); //
		}

	}

	public void keyTyped(KeyEvent keyevent) {

	}

	public void onAction() {
		BillCardPanel cardPanel = (BillCardPanel) billPanel;
		BillVO bvo = cardPanel.getBillVO();
		HashVO hvo = null;
		if (!templetItemVO.getPub_Templet_1VO().getTablename().equals("sal_factor_def")) {
			hvo = new HashVO();
			hvo.setAttributeValue("name", "����");
			hvo.setAttributeValue("value", textPanel.getText());
			hvo.setAttributeValue("sourcetype", "�ı�");
		} else {
			hvo = bvo.convertToHashVO();
		}
		try {
			new Salary_FactorFormulaWKPanel().onAction(this, hvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setPopDialogVisible(boolean _falg) {
		if (timer != null) {
			timer.cancel(); // ֹͣ
			timer.purge();// �ͷ�
		}
		if (popDialog != null && popDialog.isVisible()) {
			popDialog.setVisible(false);
		}

	}

	//�����齨�������ͳ�������
	protected void addComponentUndAndRedoFunc(JTextComponent textComp) {
		final UndoManager undo = new UndoManager();
		Document doc = textComp.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});

		//�����¼�����
		textComp.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canUndo()) {
					undo.undo();
				}
			}
		});
		//Ϊ����󶨿�ݼ�Ctrl+Z
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "Undo");
		//�����¼�����
		textComp.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canRedo()) {
					undo.redo();
				}
			}
		});
		//Ϊ����󶨿�ݼ�Ctrl+Y
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), "Redo");
	}

	private int mark_begin = 0;

	public int getMark_begin() {
		return mark_begin;
	}

	private static final int POP_SHOW_YZ_BY_TEXTCHANGE = 0; // �ı����룬�Զ���ʾ���Ӷ���
	private static final int POP_SHOW_YSF = 1; // ������� + - * /
	private static final int POP_SHOW_YZ_IMPORTBTN = 2; // ������밴ť��ʾ����
	private static final int POP_SHOW_DX_ITEM = 3;// ��ʾ���������,������ֶ�����
	private int curr_pop_show;// ��ǰ��ʾ�����ͣ�0��1��2��3

	private void showQuickSearchPopupMenu(int _type) {
		int caretPosition = textPanel.getCaretPosition();// �õ����λ��
		DefaultCaret caret = (DefaultCaret) textPanel.getCaret();
		int x = (int) caret.getX();
		int y = (int) caret.getY();
		curr_pop_show = _type;
		if (POP_SHOW_DX_ITEM == _type) { // ��ʾ���������
			if (billPanel instanceof BillCardPanel) {
				String text = textPanel.getText(); // �õ���ǰ���ı����ݡ�
				text = text.replaceAll("\r", "");
				String beforeCaret = text.substring(0, caretPosition); // �õ����ǰ������
				String[][] itemdescr = null;
				if (caretPosition > 1) {
					if (beforeCaret.charAt(caretPosition - 1) == '.') {
						if (beforeCaret.indexOf("[") >= 0) {
							String factorName = beforeCaret.substring(beforeCaret.lastIndexOf("[") + 1, beforeCaret.length() - 1);
							itemdescr = systemTableItemName(factorName);
						}
					} else if (beforeCaret.indexOf("[") >= 0) {
						String factorName = beforeCaret.substring(beforeCaret.lastIndexOf("[") + 1, beforeCaret.length());
						itemdescr = systemTableItemName(factorName);
					}
				}
				if (itemdescr == null) {
					return;
				}
				if (itemdescr != null && itemdescr.length > 0) {
					popDialog = new MypopQuickSearchDialog(textPanel, Salary_FormulaWLTComponent.this, false);
					textPanel.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_NONE);
					popDialog.setCondition("");
					List list = new ArrayList();
					for (int i = 0; i < itemdescr.length; i++) {
						HashVO add = new HashVO();
						add.setAttributeValue("name", itemdescr[i][1]);
						add.setAttributeValue("show", itemdescr[i][1]);
						add.setAttributeValue("$special", "");
						add.setToStringFieldName("show");
						list.add(add);
					}
					popDialog.setList(list.toArray(new HashVO[0]));
					textPanel.requestFocus(false);
					popDialog.show(textPanel, x + 10, y + 4);
					return;
				}
			}

		} else if (POP_SHOW_YSF == _type || POP_SHOW_YZ_IMPORTBTN == _type) {// ���������ť����
			popDialog = new MypopQuickSearchDialog(textPanel, Salary_FormulaWLTComponent.this, false);
			textPanel.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_NONE);
			popDialog.setCondition("");
			switch (_type) {
			case POP_SHOW_YSF:
				String[][] ysf = new String[][] { { "+", "+ ����" }, { "-", "- ��ȥ" }, { "*", "* ����" }, { "/", "/ ����" }, };
				List list = new ArrayList();
				for (int i = 0; i < ysf.length; i++) {
					HashVO add = new HashVO();
					add.setAttributeValue("name", ysf[i][0]);
					add.setAttributeValue("show", ysf[i][1]);
					add.setAttributeValue("$special", "");
					add.setToStringFieldName("show");
					list.add(add);
				}
				popDialog.setList(list.toArray(new HashVO[0]));
				break;
			case POP_SHOW_YZ_IMPORTBTN:
				popDialog.setList(allFactor);
				break;
			}
			textPanel.requestFocus(false);
			popDialog.show(textPanel, x + 10, y + 4);
			return;
		} // �����ť

		if (caretPosition > 0) {
			String text = textPanel.getText(); // �õ���ǰ���ı����ݡ�
			text = text.replaceAll("\r", "");
			String beforeCaret = text.substring(0, caretPosition); // �õ����ǰ������
			mark_begin = beforeCaret.lastIndexOf("["); // ��ʼ���λ��
			if (mark_begin < 0) { // ���û�������š�˵����Ҫ
				setPopDialogVisible(false);
				return;
			}
			if (beforeCaret.lastIndexOf("]") > mark_begin) {
				setPopDialogVisible(false);
				return;
			}
			int mark_end = text.indexOf("]", mark_begin);
			String key = "";
			boolean showAll = false;
			if (mark_end < 0) { // ��û���ü�д���������,
				if (mark_begin < beforeCaret.length()) {
					key = beforeCaret.substring(mark_begin + 1, beforeCaret.length());
				}
			} else { // �����������
				if (mark_end == mark_begin + 1) {// �����[]�����ģ���ʾȫ��
					showAll = true;
				} else {
					key = beforeCaret.substring(mark_begin + 1, caretPosition);
				}
			}
			if (key.equals("") && !showAll) { // û��key,�Ҳ��������[]״̬
				return;
			}
			List list = new ArrayList();
			if (key.equals("")) {// ���û���ҵ������
				popDialog = new MypopQuickSearchDialog(textPanel, Salary_FormulaWLTComponent.this, false);
				textPanel.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_NONE);
				popDialog.setCondition("");
				popDialog.setList(allFactor);
				textPanel.requestFocus(false);
				popDialog.show(textPanel, x + 10, y + 4);
				return;
			}
			querytextValue = key;

			for (int i = 0; i < allFactor.length; i++) {
				String name = allFactor[i].getStringValue("name");
				if (name != null && !name.equals("") && name.indexOf(key) >= 0) { // ����������������
					list.add(allFactor[i]);// ���뵽��ʾ����ʾ��
				}
			}
			if (list.size() > 0) {
				popDialog = new MypopQuickSearchDialog(textPanel, Salary_FormulaWLTComponent.this, false);
				textPanel.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_NONE);
				popDialog.setCondition(querytextValue);
				popDialog.setList(list.toArray(new HashVO[0]));
				textPanel.requestFocus(false);
				popDialog.show(textPanel, x + 10, y + 4);
			} else {
				setPopDialogVisible(false);
			}
		}
	}

	/*
	 * ������ʾ��->ѡ���->ˢ�¶�������
	 */
	public void refreshDataByPopSelected(Object _returnValue) {
		String text = textPanel.getText();
		text = text.replaceAll("\r", "");
		if (!(_returnValue instanceof HashVO)) {
			textPanel.inputText(_returnValue.toString());
			return;
		}
		HashVO vo = (HashVO) _returnValue;
		if (curr_pop_show == POP_SHOW_YSF) { // ������������ֶ�ֵ
			textPanel.inputText(vo.getStringValue("name"));
			return;
		}
		int caretPosition = textPanel.getCaretPosition();// �õ����λ��
		String insertOrUpdateText = vo.getStringValue("name"); // �õ�Ҫ���µ�ֵ
		String beforeCaret = text.substring(0, caretPosition); // �õ����ǰ������
		mark_begin = beforeCaret.lastIndexOf("["); // ��ʼ���λ��
		if (curr_pop_show == POP_SHOW_DX_ITEM) {
			if (beforeCaret.lastIndexOf(".") != beforeCaret.length() - 1) {
				insertOrUpdateText = "." + insertOrUpdateText;
			}
		}
		if (mark_begin < 0) { // ���û�������š�
			insertOrUpdateText = "[" + insertOrUpdateText;
		}
		int mark_end = beforeCaret.lastIndexOf("]"); // �����ǰ���Ƿ��Ѿ�����[]�ԡ�
		if (mark_end > mark_begin) { // �������]λ�ô��ڿ�ʼ[����ô����һ����
			insertOrUpdateText = "[" + insertOrUpdateText;
		}

		StringBuffer newText = new StringBuffer();
		if (popDialog.getCondition() != null && !popDialog.getCondition().equals("")) {
			if (vo != null) {
				newText.append(text.substring(0, caretPosition));
				newText.replace(newText.length() - popDialog.getCondition().length(), newText.length(), insertOrUpdateText);
				newText.append(text.substring(caretPosition, text.length()));
				caretPosition += insertOrUpdateText.length() - popDialog.getCondition().length();
			}
		} else { // ���û��������������ʵ����ȫ����
			newText.append(text.substring(0, caretPosition));
			newText.append(insertOrUpdateText);
			newText.append(text.substring(caretPosition, text.length()));
			caretPosition += insertOrUpdateText.length();
		}
		textPanel.setText(newText.toString());
		if ("ϵͳ����".equals(vo.getStringValue("sourcetype"))) {
			textPanel.setCaretPosition(caretPosition);
			nextDo(POP_SHOW_DX_ITEM);
		} else {
			int kuohao = newText.toString().indexOf("]", caretPosition);
			if (kuohao < 0) {
				textPanel.moveCaretPosition(caretPosition);
				textPanel.inputText("]");
			} else {
				textPanel.moveCaretPosition(kuohao + 1);
			}
			setPopDialogVisible(false);
			textPanel.moveCaretPosition(textPanel.getText().length());
			nextDo(POP_SHOW_YSF);
		}

	}

	/*
	 * ��һ���Զ���ʾ
	 */
	public void nextDo(final int _type) {
		if (timer != null) {
			timer.cancel(); // ֹͣ
			timer.purge();// �ͷ�
		}
		timer = new Timer();
		timer.schedule(new TimerTask() { // ��������,�������߳̿��ơ�����ȡ����caret���ꡣ
					public void run() {
						showQuickSearchPopupMenu(_type);
					}
				}, 300); //
	}

	public void mouseDragged(MouseEvent mouseevent) {
	}

	Cursor hand = new Cursor(Cursor.HAND_CURSOR);
	DefaultHighlightPainter paint = new DefaultHighlightPainter(new Color(127, 252, 0, 100));

	private int currLightStartPosotion = -1;

	public void mouseMoved(MouseEvent mouseevent) {
		BasicTextUI textUI = (BasicTextUI) textPanel.getUI();
		int textIndexByMouse = textUI.viewToModel(textPanel, mouseevent.getPoint());
		if (textIndexByMouse >= 0 && textIndexByMouse < textPanel.getDocument().getLength()) {
			String s = "";
			String allText = textPanel.getText();
			s = allText.substring(textIndexByMouse, textIndexByMouse + 1);
			if ("+-*/".contains(s)) {
				textPanel.setCursor(hand);
				currLightStartPosotion = -1;
				textPanel.getHighlighter().removeAllHighlights();
			} else {
				if (textIndexByMouse > 0) {
					String beforeMouse = allText.substring(0, textIndexByMouse); // �õ����ǰ������
					int mark_begin_before_mouse = beforeMouse.lastIndexOf("["); // ��ʼ���λ��
					if (mark_begin_before_mouse >= 0) { // �������������
						int between = beforeMouse.lastIndexOf("]");// �ж�ǰ������һ��������
						if (between > mark_begin_before_mouse) {
							// ���� û���ҵ���˵���ʶ�������
							setHighlight(false);
						} else {
							int mark_end_behind_mouse = allText.indexOf("]", textIndexByMouse);
							String key = allText.substring(mark_begin_before_mouse + 1, mark_end_behind_mouse);
							if (key != null && key.contains(".")) { //�����.˵���������������ӵ�ĳ���ֶΡ�
								String table_item[] = tBUtil.split(key, ".");
								if (table_item.length == 2) {
									String table = table_item[0];
									if (!allFactorMap.containsKey(table)) {
										return;
									}
									if (currLightStartPosotion == mark_begin_before_mouse) {
										return;
									}
									String item = table_item[1];
									String[][] itemdescr = systemTableItemName(table);
									if (itemdescr != null && item != null) {
										for (int i = 0; i < itemdescr.length; i++) {
											if (item.equals(itemdescr[i][1])) {
												if (currLightStartPosotion == mark_begin_before_mouse) {
													return;
												} else {
													setHighlight(false);
												}
												currLightStartPosotion = mark_begin_before_mouse;
												try {
													textPanel.getHighlighter().addHighlight(mark_begin_before_mouse + 1, mark_end_behind_mouse, paint);
													setHighlight(true);
													return;
												} catch (BadLocationException e) {
													e.printStackTrace();
												}
											}
										}
									}

								}
							}
							if (allFactorMap.containsKey(key)) { // �������
								if (currLightStartPosotion == mark_begin_before_mouse) {
									return;
								} else {
									setHighlight(false);
								}
								currLightStartPosotion = mark_begin_before_mouse;
								try {
									textPanel.getHighlighter().addHighlight(mark_begin_before_mouse + 1, mark_end_behind_mouse, paint);
									setHighlight(true);
									return;
								} catch (BadLocationException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				setHighlight(false);
			}
		} else {
			setHighlight(false);
		}
	}

	private void setHighlight(boolean _flag) {
		if (_flag) {
			textPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			currLightStartPosotion = -1;
			textPanel.getHighlighter().removeAllHighlights();
			textPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}

	}

	/*
	 * ����ϵͳ�����������ƣ���ȡ��ṹ
	 */
	private String[][] systemTableItemName(String _factorName) {
		try {
			HashVO factorVO = (HashVO) allFactorMap.get(_factorName);
			if (factorVO == null) {
				return null;
			}
			String tableName = factorVO.getStringValue("value");
			if ("ϵͳ����".equals(factorVO.getStringValue("sourcetype"))) {
				String extend = factorVO.getStringValue("extend");
				if (!TBUtil.getTBUtil().isEmpty(extend)) {
					tableName = extend;
				}
			}
			String[][] descr = getService().getTableItemAndDescr(tableName);
			return descr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}

	public void onMouseDoubleClicked(BillListPanel _listpanel) {
		BillVO selectVO = _listpanel.getSelectedBillVO();
		String formula = selectVO.getStringValue("��ʽ");
		textPanel.inputText(formula);
		if (showFormuladialog != null) {
			showFormuladialog.dispose();
		}
	}

	class MouseAction extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.isControlDown() && e.getClickCount() == 2) { //���
					onMaxShow(); //
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				doubleKeyText(e);
			}
		}

		//˫���ؼ��ʣ�������ʾ����
		private void doubleKeyText(MouseEvent mouseevent) {
			BasicTextUI textUI = (BasicTextUI) textPanel.getUI();
			int textIndexByMouse = textUI.viewToModel(textPanel, mouseevent.getPoint());
			if (textIndexByMouse >= 0 && textIndexByMouse < textPanel.getDocument().getLength()) {
				String s = "";
				String allText = textPanel.getText();
				s = allText.substring(textIndexByMouse, textIndexByMouse + 1);
				if (!"+-*/".contains(s)) {
					if (textIndexByMouse > 0) {
						String beforeMouse = allText.substring(0, textIndexByMouse); // �õ����ǰ������
						int mark_begin_before_mouse = beforeMouse.lastIndexOf("["); // ��ʼ���λ��
						if (mark_begin_before_mouse >= 0) { // �������������
							int between = beforeMouse.lastIndexOf("]");// �ж�ǰ������һ��������
							if (between > mark_begin_before_mouse) {
								// ���� û���ҵ���˵���ʶ�������
							} else {
								int mark_end_behind_mouse = allText.indexOf("]", textIndexByMouse);
								String key = allText.substring(mark_begin_before_mouse + 1, mark_end_behind_mouse);
								if (key != null && key.contains(".") && key.indexOf(".") > 1) {
									key = key.substring(0, key.indexOf("."));
								}
								if (allFactorMap.containsKey(key)) { // �������
									rightClickKeyText.putClientProperty("key", key);
									rightClickKeyText.show(textPanel, mouseevent.getX(), mouseevent.getY());
								}
							}
						}
					}
				}
			}
		}
	}

	private void onMaxShow() {
		final BillDialog dialog = new BillDialog(this);
		dialog.setSize(800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		Salary_FormulaWLTComponent component = new Salary_FormulaWLTComponent(templetItemVO, billPanel);
		component.setItemEditable(textPanel.isEditable());
		component.setValue(getValue());
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); //
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(component);
		dialog.getContentPane().add(mainPanel);
		WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER));
		WLTButton confirm = new WLTButton("ȷ��");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				dialog.setCloseType(1);
				dialog.dispose();
			}
		});
		WLTButton cancel = new WLTButton("ȡ��");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				dialog.setCloseType(-1);
				dialog.dispose();
			}
		});
		btnPanel.add(confirm);
		btnPanel.add(cancel);
		dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);

		component.getLabel().setVisible(false);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
		if (dialog.getCloseType() != -1) {
			textPanel.setText(component.getValue());
		}

	}
}

class MypopQuickSearchDialog extends PopQuickSearchDialog {
	private Salary_FormulaWLTComponent parent;

	public MypopQuickSearchDialog(JComponent showOnComponent, Salary_FormulaWLTComponent _parent, boolean showDeleteBtn) {
		super(showOnComponent, _parent, showDeleteBtn);
		parent = _parent;
	}

	@Override
	public void setObject() {
		parent.refreshDataByPopSelected(getList().getSelectedValue());
	}
}
