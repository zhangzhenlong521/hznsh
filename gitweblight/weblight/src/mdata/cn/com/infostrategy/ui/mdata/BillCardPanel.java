/**************************************************************************
 *  $RCSfile: BillCardPanel.java,v $  $Revision: 1.69 $  $Date: 2013/01/10 08:31:52 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Button;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTableImport;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_FileDeal;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ImageUpload;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Label;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_StylePadArea;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextArea;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;
import cn.com.infostrategy.ui.mdata.cardcomp.CardGroupTitlePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RecordShowDialog;

public class BillCardPanel extends BillPanel {

	private static final long serialVersionUID = 9086081912297545806L; //

	private String str_templetcode = null;
	private Pub_Templet_1VO templetVO = null;
	private Pub_Templet_1_ItemVO[] templetItemVOs = null;

	private AbstractWorkPanel loadedWorkPanel = null; // ���������Ƭ����frame,�����Ǹ��ַ��ģ��Frame!
	private boolean is_admin = false;
	private JScrollPane scrollPanel = null;
	private ArrayList v_compents = new ArrayList();
	private HashMap hm_groupcompents = new HashMap(); //
	private JComponent[] hflowPanels = null; //
	private VFlowLayoutPanel vflowPanel = null; //
	private boolean canRefreshParent = false; //20130509��� �޸�ˢ�¸�ҳ������

	private HashMap hm_hflowPanels_key = new HashMap(); //�洢hflowPanels����keyӳ��,���setVisiable(key, false)��������7�������� �����/2013-01-09��

	public String str_rownumberMark = "_RECORD_ROW_NUMBER";
	private Vector v_listeners = new Vector(); //����ע����¼�������!!!
	private Vector v_cardbtnListener = new Vector(); // �Զ��尴ť�����¼�

	private VectorMap rowPanelMap = new VectorMap(); //

	private Border border = null;

	private TBUtil tBUtil = null; //ת������!!

	private RowNumberItemVO rowNumberItemVO = null; // �к�����VO.....
	private boolean bo_isallowtriggereditevent = true; // �Ƿ��������༭�¼�

	private BillButtonPanel billCardBtnPanel = null; //

	private JPanel mainPanel = null;
	private int li_cardallwidth = 510; //
	private int li_panelallheight = 0;
	private BillFormatPanel loaderBillFormatPanel = null;

	private HashMap lastKeepTrace = new HashMap(); //������һ���汾���ݵĹ�ϣ��!!

	private Logger logger = WLTLogger.getLogger(BillCardPanel.class);
	private BillCardPanel() {
	}

	public BillCardPanel(String _templetcode) {
		if (_templetcode.indexOf(".") > 0) { //����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	public BillCardPanel(Pub_Templet_1VO _TempletVO) {
		this.str_templetcode = _TempletVO.getTempletcode();
		templetVO = _TempletVO;
		templetItemVOs = templetVO.getItemVos(); // ����
		initialize(); //
	}

	/**
	 * ����ֱ�����ɵĳ���ģ��VO����ҳ��!!!,���������ݲ����� pub_teplet_1����pub_teplet_1_item���е�!!!
	 * 
	 * @param _abstractTempletVO
	 */
	public BillCardPanel(AbstractTMO _abstractTempletVO) {
		init_2(_abstractTempletVO);
	}

	public BillCardPanel(String _templetName, String[] _items) {
		String[][] str_items = new String[_items.length][2]; //
		for (int i = 0; i < str_items.length; i++) {
			str_items[i][0] = _items[i];
			str_items[i][1] = "225";
		}
		AbstractTMO tmo = DefaultTMO.getCardTMO(_templetName, str_items); //
		init_2(tmo);
		setEditable(true); //���ֹ�����,Ĭ�Ͽ϶��ǿɱ༭!!
	}

	/**
	 * ����ٵĹ���һ�����!����������Ҫ
	 * BillCardPanel card = new BillCardPanel("�ҵ���ʾ��", new String[][] { { "����", "150" }, { "����", "150" } }); //
	 * @param _templetName
	 * @param _items
	 */
	public BillCardPanel(String _templetName, String[][] _items) {
		AbstractTMO tmo = DefaultTMO.getCardTMO(_templetName, _items); //
		init_2(tmo);
		setEditable(true); //���ֹ�����,Ĭ�Ͽ϶��ǿɱ༭!!

	}

	/**
	 * ����ServerTMO����..
	 * @param _serverTMO
	 */
	public BillCardPanel(ServerTMODefine _serverTMO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); //����..
			str_templetcode = templetVO.getTempletcode();
			initialize(); // ��ʼ��!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void init_2(AbstractTMO _abstractTempletVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode(); //
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	/**
	 * ���¼���ҳ��
	 */
	public void reload() {
		reload(this.str_templetcode); //
	}

	/**
	 * ���¼���ҳ��
	 */
	public void reload(String _templetcode) {
		v_compents.clear(); //
		rowPanelMap.clear(); //
		v_listeners.clear();
		v_cardbtnListener.clear();
		mainPanel = null;
		vflowPanel = null; //���¼���ʱ��ˢ��ҳ��  20130313  Ԭ����  Ϊʵ�ֲ����´�ҳ��ֱ��ˢ��ģ������   �������������ϣ����򲻻����¼���
		init_1(_templetcode); //
	}

	/**
	 * ��ʼ��ҳ��
	 * 
	 */
	private void initialize() {
		this.removeAll(); //
		//this.setFocusable(true); //
		li_cardallwidth = templetVO.getCardwidth().intValue(); //��Ƭ���
		this.is_admin = ((ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) ? true : false);
		this.setRowNumberItemVO(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, 0)); //
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getBorderName(), TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // �����߿�		

		this.setLayout(new BorderLayout());
		this.setBorderColor(Color.BLACK); //

		JPanel panel_content = new JPanel(new BorderLayout()); //�������
		panel_content.setOpaque(false); //͸��!
		panel_content.setBorder(border); //���ñ߿�!
		panel_content.add(getMainPanel(), BorderLayout.CENTER); //ȡ�������,�ؼ��߼�!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		JPanel wholePanel = null; //�������,��Ϊ��Ҫ������,���ֲ����ð�ť�������һ���Ƶ�! ���Ը���������!!
		boolean isJianbian = true; //�Ƿ񽥱�??
		if (isJianbian) { //����ǽ���!!!
			wholePanel = new WLTPanel(BackGroundDrawingUtil.INCLINE_NW_TO_SE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); //
		} else {
			wholePanel = new JPanel(); //
			wholePanel.setBackground(LookAndFeel.defaultShadeColor1); //
		}
		wholePanel.add(panel_content, BorderLayout.CENTER);
		wholePanel.add(getBillCardBtnPanel(), BorderLayout.NORTH);

		this.setLayout(new BorderLayout()); //
		this.add(wholePanel); //
	}

	/**
	 * �б�����Զ���һЩ���ٰ�ť
	 * 
	 * @return
	 */
	public BillButtonPanel getBillCardBtnPanel() {
		if (billCardBtnPanel != null) {
			return billCardBtnPanel;
		}

		billCardBtnPanel = new BillButtonPanel(this.templetVO.getCardcustbtns(), this); //
		billCardBtnPanel.paintButton(); //
		return billCardBtnPanel;
	}

	/**
	 * �õ�ĳһ����ť
	 * @param _text
	 * @return
	 */
	public WLTButton getBillCardBtn(String _text) {
		return getBillCardBtnPanel().getButtonByCode(_text); //
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void addBillCardButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillCardBtnPanel().addButton(_btn); //
	}

	/**
	 * ����һ����ť
	 * @param _btns
	 */
	public void addBatchBillCardButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillCardBtnPanel().addBatchButton(_btns);
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void insertBillCardButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillCardBtnPanel().insertButton(_btn); //
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void insertBatchBillCardButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillCardBtnPanel().insertBatchButton(_btns); //
	}

	/**
	 * ���»��ư�ť
	 */
	public void repaintBillCardButton() {
		getBillCardBtnPanel().paintButton(); //
	}

	public void addBillCardButtonActinoListener(BillCardButtonActinoListener _listener) {
		v_cardbtnListener.add(_listener); //
	}

	/**
	 * ���еĲ��ֶ������������!!�ؼ�,����!!
	 * @return
	 */
	public JPanel getMainPanel() {
		if (vflowPanel != null) {
			return vflowPanel; //
		}

		for (int i = 0; i < templetItemVOs.length; i++) { //�������пؼ�!!
			final String str_itemkey = templetItemVOs[i].getItemkey();
			String str_type = templetItemVOs[i].getItemtype();
			AbstractWLTCompentPanel compentPanel = null; //�ȶ���ÿؼ�!!!һ����©!!
			if (str_type.equals(WLTConstants.COMP_LABEL)) { //Label
				compentPanel = new CardCPanel_Label(templetItemVOs[i]); //
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_NUMBERFIELD) || str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || str_type.equals(WLTConstants.COMP_REGULAR)) { //�ı���,���ֿ�,�����//zzl����������ʽ
				compentPanel = new CardCPanel_TextField(templetItemVOs[i], str_type, null, this); //
				((CardCPanel_TextField) compentPanel).getTextField().addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) { //���ʧȥʱ�����¼�!!
						onChanged(str_itemkey);
					}
				});

				//�����¼�
				((CardCPanel_TextField) compentPanel).getTextField().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onChangedAndFocusNext(str_itemkey); //������ûس��Ļ�,��Ҫ�����..
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //������
				compentPanel = new CardCPanel_ComboBox(templetItemVOs[i], null, this); //
				((CardCPanel_ComboBox) compentPanel).getComBox().addItemListener(new ItemListener() { //�����¼�
							public void itemStateChanged(ItemEvent e) {
								if (e.getStateChange() == ItemEvent.SELECTED) {
									onChanged(str_itemkey);
								}
							}
						});
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //��ѡ����1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //�б�ģ�����
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //����ģ�����
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //ע���������
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //ע�����
					str_type.equals(WLTConstants.COMP_DATE) || //����
					str_type.equals(WLTConstants.COMP_DATETIME) || //ʱ��
					str_type.equals(WLTConstants.COMP_BIGAREA) || //���ı���
					//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���!!!����ǰҲ�ǵ�һ�����մ���,�������������û�Ҫ�����õ���ҳ������,�Է��ϴ�����ҳϵͳ��Ч��,�ٵ�һ��,��
					str_type.equals(WLTConstants.COMP_COLOR) || //��ɫѡ���
					str_type.equals(WLTConstants.COMP_CALCULATE) || //������
					str_type.equals(WLTConstants.COMP_PICTURE) || //ͼƬѡ���
					str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
					str_type.equals(WLTConstants.COMP_OFFICE) //office
			) { //����Ǹ��ֲ���
				compentPanel = new CardCPanel_Ref(templetItemVOs[i], null, this); //�����������!!!һ��ʼ��û�г�ʼֵ��!!
				((CardCPanel_Ref) compentPanel).addBillCardEditListener(new BillCardEditListener() {
					public void onBillCardValueChanged(BillCardEditEvent _evt) {
						onChanged(str_itemkey); //�ڲ����м��������
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //�����ı���
				compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
				((CardCPanel_TextArea) compentPanel).getArea().addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) { //���ʧȥʱ�����¼�!!
						onChanged(str_itemkey);
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { //��ť
				compentPanel = new CardCPanel_Button(templetItemVOs[i], null, this);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { //��ѡ��
				compentPanel = new CardCPanel_CheckBox(templetItemVOs[i]);
				((CardCPanel_CheckBox) compentPanel).getCheckBox().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onChanged(str_itemkey); //
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_LINKCHILD)) { //�����ӱ�,������һ�����,�Ӷ�ʵ����HTNLЧ��һ����ҳ��!!
				compentPanel = new CardCPanel_ChildTable(templetItemVOs[i], this); //
				compentPanel.setItemEditable(false); //�����ӱ�Ĭ���ǲ��ɱ༭...
				//�ݲ����༭����!!!
			} else if (str_type.equals(WLTConstants.COMP_IMPORTCHILD)) { //�����ӱ�!!
				compentPanel = new CardCPanel_ChildTableImport(templetItemVOs[i], this); //
				compentPanel.setItemEditable(false); //�����ӱ�Ĭ���ǲ��ɱ༭...
				//�ݲ����༭����!!!
			} else if (str_type.equals(WLTConstants.COMP_STYLEAREA)) { //���ı���
				compentPanel = new CardCPanel_StylePadArea(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { //�ļ�ѡ���,�ڴ����û�ǿ��Ҫ���£������õ���ҳ����������ʵ������ʹ��ҳ�����,���û�����ν!!ֻҪ�ٵ�һ�Σ��ؼ�����OA�Ĳ���ϰ����һ���ģ�����
				compentPanel = new CardCPanel_FileDeal(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_IMAGEUPLOAD)) { //ͼƬ�ϴ�,��ֱ���ϴ�һ��ͼƬ�洢�����ݿ���,Ȼ���ڿ�Ƭ��ֱ����Ⱦ��ͼƬ! ����HRϵͳ�е���Ա��Ƭ��Ч���ͻ��������ֿؼ�����! ��Ϊ���ϴ��ļ��洢��Ŀ¼�²�һ��,���Ǵ洢�����ݿ��е�!����Ǩ�Ʒ���!
				compentPanel = new CardCPanel_ImageUpload(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_SELFDESC)) {
				if (templetItemVOs[i].getUCDfVO() != null) {
					String str_clsName = templetItemVOs[i].getUCDfVO().getConfValue("��Ƭ�е���"); //
					if (str_clsName != null && !str_clsName.trim().equals("")) { //�����Ϊ��,�򴴽�
						try {
							compentPanel = (AbstractWLTCompentPanel) Class.forName(str_clsName).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class, BillPanel.class }).newInstance(new Object[] { templetItemVOs[i], this });
						} catch (Exception e) {
							e.printStackTrace();
							compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
						}
					} else {
						compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
					}
				} else {
					compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
				}
			} else {
				continue; //
			}

			compentPanel.setOpaque(false); //-isOpaque
			v_compents.add(compentPanel); //�������м���!!
			boolean bo_iscardedit = false; // ��ʼ���治�ܱ༭!!
			compentPanel.setItemEditable(bo_iscardedit); //�����Ƿ�ɱ༭!!
			compentPanel.setBillPanel(this); //�ڿؼ���ע��BillCardPanel
		} //end for,�������пؼ����ͽ���

		ArrayList al_allRows = new ArrayList(); //������
		ArrayList al_oneRowCompents = null; //һ��
		String last_grouptitle = ""; //���һ�����������

		int li_rowcount = -1; //
		for (int i = 0; i < v_compents.size(); i++) { //
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i); //
			String str_itemkey = compent.getItemKey(); ////
			Pub_Templet_1_ItemVO tmpitemVO = findTempletItemVO(str_itemkey); //
			boolean bo_iscardshow = tmpitemVO.getCardisshowable().booleanValue(); //
			boolean bo_iswrap = tmpitemVO.getIswrap().booleanValue(); //
			String str_grouptitle = tmpitemVO.getGrouptitle() == null ? "" : tmpitemVO.getGrouptitle(); //����..
			if (bo_iscardshow) { //����ڿ�Ƭ��ʾ!!!��Ϊ�е������ص�!
				//compent.setVisible(true);
				if (str_grouptitle.equals(last_grouptitle)) { //�������������һ��������ͬ,���ж��Ƿ���!!
					if (bo_iswrap) {
						al_oneRowCompents = new ArrayList(); //����ǻ���,�����´���һ��������!!
						al_allRows.add(al_oneRowCompents); //
						al_oneRowCompents.add(compent); //����ؼ�
						hm_hflowPanels_key.put(str_itemkey, al_allRows.size());
						hm_hflowPanels_key.put(al_allRows.size(), str_itemkey);
						li_rowcount++; //��һ��
					} else {
						if (al_oneRowCompents == null) { //
							al_oneRowCompents = new ArrayList(); //
							al_allRows.add(al_oneRowCompents); //
							li_rowcount++; //��һ��
						}
						al_oneRowCompents.add(compent); //ֱ����ԭ�������������µĿؼ�!!!
						hm_hflowPanels_key.put(str_itemkey, al_allRows.size());
						if (hm_hflowPanels_key.containsKey(al_allRows.size())) {
							hm_hflowPanels_key.put(al_allRows.size(), hm_hflowPanels_key.get(al_allRows.size()) + ";" + str_itemkey);
						} else {
							hm_hflowPanels_key.put(al_allRows.size(), str_itemkey);
						}
					}

					if (!str_grouptitle.equals("")) { //���������Ϊ�գ���û�󶨣����鶨��ϵ
						String str_rowindexs = (String) hm_groupcompents.get(str_grouptitle); //ԭ�����к�
						if (str_rowindexs.indexOf(li_rowcount + ";") < 0) { //���ǲ���������
							hm_groupcompents.put(str_grouptitle, str_rowindexs + li_rowcount + ";"); //�µ��к�
						}
					}
				} else { //�������������һ���鲻ͬ,��˵�����µķ���,��ǿ�л��У������ȼ���һ��������!!!
					if (!str_grouptitle.equals("")) {
						li_rowcount++; //��һ��
						CardGroupTitlePanel cardTitlePanel = new CardGroupTitlePanel(str_grouptitle, li_rowcount); //��ؼ�,��ǰ���и���ť���Ե������չ������������!!
						cardTitlePanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM, false));
						cardTitlePanel.getButton().addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (e.getModifiers() == 18) {
									onClickGroupTitleCtrl((JButton) e.getSource());
								} else {
									/*if (templetVO.getGroupisonlyone() != null && templetVO.getGroupisonlyone()) {
										onClickGroupTitleOnlyOne((JButton) e.getSource());
									} else*/
									onClickGroupTitle((JButton) e.getSource());
								}
							}
						});

						al_allRows.add(cardTitlePanel); //��������
					}

					al_oneRowCompents = new ArrayList(); //���
					al_allRows.add(al_oneRowCompents); //
					al_oneRowCompents.add(compent); //����ؼ�
					hm_hflowPanels_key.put(str_itemkey, al_allRows.size());
					hm_hflowPanels_key.put(al_allRows.size(), str_itemkey);
					li_rowcount++; //��һ��

					if (!str_grouptitle.equals("")) {
						hm_groupcompents.put(str_grouptitle, li_rowcount + ";"); //�Ƚ��飬���°󶨹�ϵ!!���ڼ���
					}
				}
				last_grouptitle = str_grouptitle; //������һ��������
			} else {
				//compent.setVisible(false);
			}
		}

		hflowPanels = new JComponent[al_allRows.size()]; //
		for (int i = 0; i < hflowPanels.length; i++) {
			Object obj = al_allRows.get(i); // 
			if (obj instanceof ArrayList) {
				ArrayList al_row = (ArrayList) obj; //
				//hflowPanels[i] = new HFlowLayoutPanel(al_row, LookAndFeel.cardbgcolor); ////
				hflowPanels[i] = new HFlowLayoutPanel(al_row); ////
			} else if (obj instanceof JComponent) { //
				hflowPanels[i] = (JComponent) obj; //
			}
		}

		//vflowPanel = new VFlowLayoutPanel(hflowPanels, LookAndFeel.cardbgcolor); //��ֱ����...
		vflowPanel = new VFlowLayoutPanel(hflowPanels); //��ֱ����...
		//        vflowPanel.setOpaque(false); //--isOpaque
		//vflowPanel.setBackground(LookAndFeel.cardbgcolor);

		//ִ�г�ʼ����ʽ!!���������Ȱ�ĳЩ�ؼ����ص�
		JepFormulaParseAtUI jepParse = new JepFormulaParseAtUI(this); //
		String str_cardinitformula = this.templetVO.getCardinitformula(); //��Ƭ�ĳ�ʼ����ʽ
		if (str_cardinitformula != null && !str_cardinitformula.trim().equals("")) {
			String[] str_initformulas = getTBUtil().split1(str_cardinitformula, ";"); //
			for (int i = 0; i < str_initformulas.length; i++) {
				jepParse.execFormula(str_initformulas[i]); //ִ�г�ʼ����ʽ!!!
			}
		}

		/*if (templetVO.getGroupisonlyone() != null && templetVO.getGroupisonlyone()) {//��Ƭ�Ƿ�ֻ�ܴ�һ��
			setAllGroupVisiable();
		}*/

		return vflowPanel;
	}

	/**
	 * ����ĳһ���Ƿ���ʾ,�����鱾������
	 * @param _groupname
	 * @param _visiable
	 */
	public void setGroupVisiable(String _groupname, boolean _visiable) {
		String al_rowindexs = (String) hm_groupcompents.get(_groupname); //
		if (al_rowindexs != null && !"".equals(al_rowindexs.trim())) { // ������ָ���˲����ڵ��鱨��ָ�������/sunfujun/20130514
			String[] str_items = al_rowindexs.split(";"); //
			int li_grouprow = Integer.parseInt(str_items[0]) - 1; //�������о��ǵ�һ�е�ǰ��һ��!!!
			CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
			grouptitlePanel.setVisible(_visiable); //
			grouptitlePanel.setExpandSate(_visiable); //
			for (int i = str_items.length - 1; i >= 0; i--) {
				vflowPanel.setRowVisiable(Integer.parseInt(str_items[i]), _visiable);
				//			vflowPanel.setRowItemVisiable(Integer.parseInt(str_items[i]), _visiable); //	�����һ�鲻��ʾ����ô�ڱ�����У��ʱ��û��Ҫ��У����
			}
		}
	}

	/**
	 * ����ĳһ���Ƿ�չ��,��ֻ�������ڸ���Ŀؼ�!!
	 * @param _groupname
	 * @param _expandable
	 */
	public void setGroupExpandable(String _groupname, boolean _expandable) {
		String al_rowindexs = (String) hm_groupcompents.get(_groupname); //
		if (al_rowindexs != null) {
			String[] str_items = al_rowindexs.split(";"); //�������һ����������
			int li_grouprow = Integer.parseInt(str_items[0]) - 1; //�������о��ǵ�һ�е�ǰ��һ��!!!
			for (int i = str_items.length - 1; i >= 0; i--) {
				vflowPanel.setRowVisiable(Integer.parseInt(str_items[i]), _expandable); //
			}

			CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
			grouptitlePanel.setExpandSate(_expandable); //
		}
	}

	public boolean isGroupVisiable(String _groupname) {
		String al_rowindexs = (String) hm_groupcompents.get(_groupname); //
		String[] str_items = al_rowindexs.split(";"); //
		int li_grouprow = Integer.parseInt(str_items[0]) - 1; //�������о��ǵ�һ�е�ǰ��һ��!!!
		CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
		return grouptitlePanel.isVisible();
	}

	/**
	 * ���������鲻��ʾ,�����鱾������
	 */
	public void setAllGroupVisiable() {
		if (hm_groupcompents != null && hm_groupcompents.size() > 0) {
			String[] str_GroupNames = (String[]) (hm_groupcompents.keySet().toArray(new String[0]));
			int firstGroup = 10000;
			for (int i = 0; i < str_GroupNames.length; i++) {
				String al_rowindexs = (String) hm_groupcompents.get(str_GroupNames[i]); //
				if (al_rowindexs != null) {
					String[] str_items = al_rowindexs.split(";"); //�������һ����������
					int li_grouprow = Integer.parseInt(str_items[0]) - 1; //�������о��ǵ�һ�е�ǰ��һ��!!!
					if (li_grouprow < firstGroup) {
						firstGroup = li_grouprow;
					}
					for (int j = str_items.length - 1; j >= 0; j--) {
						vflowPanel.setRowVisiable(Integer.parseInt(str_items[j]), false); //
					}

					CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
					grouptitlePanel.setExpandSate(false); //
				}
			}
			if (((CardGroupTitlePanel) hflowPanels[firstGroup]) != null) {
				setGroupExpandable(((CardGroupTitlePanel) hflowPanels[firstGroup]).getTitlename(), true); //
			}
		}
	}

	public void onClickGroupTitle(JButton _btn) {
		String str_titlename = (String) _btn.getClientProperty("grouptitle"); //
		Integer lit_row = (Integer) _btn.getClientProperty("row"); //
		CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[lit_row]; //
		if (grouptitlePanel.getExpandState()) {
			setGroupExpandable(str_titlename, false); //
		} else {
			setGroupExpandable(str_titlename, true); //
		}
	}

	public void onClickGroupTitleCtrl(JButton _btn) {
		String str_titlename = (String) _btn.getClientProperty("grouptitle"); //
		Integer lit_row = (Integer) _btn.getClientProperty("row"); //
		CardGroupTitlePanel grouptitlePanel2 = (CardGroupTitlePanel) hflowPanels[lit_row]; //
		boolean s = grouptitlePanel2.getExpandState();
		for (int i = 0; i < hm_groupcompents.size(); i++) {
			String al_rowindexs = ((String[]) hm_groupcompents.values().toArray(new String[0]))[i]; //
			if (al_rowindexs != null) {
				CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[Integer.parseInt(al_rowindexs.split(";")[0]) - 1];
				if (s) {
					setGroupExpandable((String) grouptitlePanel.getButton().getClientProperty("grouptitle"), false); //
				} else {
					setGroupExpandable((String) grouptitlePanel.getButton().getClientProperty("grouptitle"), true); //
				}
			}
		}
	}

	public void onClickGroupTitleOnlyOne(JButton _btn) {
		String str_titlename = (String) _btn.getClientProperty("grouptitle"); //
		Integer lit_row = (Integer) _btn.getClientProperty("row"); //
		CardGroupTitlePanel grouptitlePanel2 = (CardGroupTitlePanel) hflowPanels[lit_row]; //
		boolean s = grouptitlePanel2.getExpandState();
		for (int i = 0; i < hm_groupcompents.size(); i++) {
			String al_rowindexs = ((String[]) hm_groupcompents.values().toArray(new String[0]))[i]; //
			if (al_rowindexs != null) {
				CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[Integer.parseInt(al_rowindexs.split(";")[0]) - 1];
				setGroupExpandable((String) grouptitlePanel.getButton().getClientProperty("grouptitle"), false); //
			}
		}
		if (!s) {
			setGroupExpandable((String) grouptitlePanel2.getButton().getClientProperty("grouptitle"), true); //
		}
	}

	public void setRowPanelVisiable(String _itemkey, boolean _visiable) {
		JPanel panel = getRowPanel(_itemkey);
		if (panel != null) {
			if (_visiable) {
				if (!panel.isVisible()) {
					showRowPanel(panel);
				}
			} else {
				if (panel.isVisible()) {
					hiddenRowPanel(panel); //
				}
			}
		}
	}

	private void showRowPanel(JPanel _rowPanel) {
		_rowPanel.setVisible(true);
		li_panelallheight = li_panelallheight + (int) _rowPanel.getPreferredSize().getHeight(); //��ԭ���ĸ߶��ϼ�ȥ���и߶�!!!
		mainPanel.setBounds(0, 0, (int) mainPanel.getPreferredSize().getWidth(), li_panelallheight);
	}

	private void hiddenRowPanel(JPanel _rowPanel) {
		_rowPanel.setVisible(false);
		li_panelallheight = li_panelallheight - (int) _rowPanel.getPreferredSize().getHeight(); //��ԭ���ĸ߶��ϼ�ȥ���и߶�!!!
		mainPanel.setBounds(0, 0, (int) mainPanel.getPreferredSize().getWidth(), li_panelallheight);
	}

	public JPanel getRowPanel(String _itemkey) {
		return (JPanel) rowPanelMap.get(_itemkey);
	}

	public JPanel[] getGroupPanel(String _grouptitle) {
		String[] str_rowPanelKeys = rowPanelMap.getKeysAsString();
		Vector v_data = new Vector();
		for (int i = 0; i < str_rowPanelKeys.length; i++) {
			String str_grouptitle = getTempletItemVO(str_rowPanelKeys[i]).getGrouptitle();
			if (str_grouptitle != null && str_grouptitle.equalsIgnoreCase(_grouptitle)) {
				v_data.add(getRowPanel(str_rowPanelKeys[i]));
			}
		}
		return (JPanel[]) v_data.toArray(new JPanel[0]);
	}

	public void setScrollable(boolean _bo) {
		//		if (_bo) {
		//			scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //
		//			scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
		//		} else {
		//			scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); //
		//			scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //
		//		}
	}

	public void setTitleable(boolean _bo) {
		if (_bo) {
			border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getBorderName(), TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // �����߿�
			((TitledBorder) border).setTitleColor(Color.BLACK); //
			if (scrollPanel != null) {
				scrollPanel.setBorder(border); //
			}
		} else {
			border = BorderFactory.createEmptyBorder(); // �����߿�
			if (scrollPanel != null) {
				scrollPanel.setBorder(border); //
			}
		}
	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();
		JMenuItem menu_table_showRecord = new JMenuItem("�鿴���ݿ�����");
		JMenuItem item_table_templetmodify_2 = new JMenuItem("��������");
		JMenuItem menu_print = new JMenuItem("��ӡ");

		if (!is_admin) {
			item_table_templetmodify_2.setEnabled(false);
		}

		menu_table_showRecord.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (templetVO.getPkname() == null || templetVO.getPkname().length() <= 0) {
					try {
						String[] allValues = new String[templetVO.getRealViewItemVOs().length];
						for (int i = 0; i < templetVO.getRealViewItemVOs().length; i++) {
							allValues[i] = String.valueOf(getRealValueAt(templetVO.getItemKeys()[i]));
						}
						new RecordShowDialog(BillCardPanel.this, templetVO.getTablename(), templetVO.getItemKeys(), allValues);
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageBox.show(BillCardPanel.this, "�ñ������ݿ��в����ڣ�", WLTConstants.MESSAGE_ERROR);
					}
				} else {
					try {
						new RecordShowDialog(BillCardPanel.this, templetVO.getTablename(), templetVO.getPkname(), getCompentRealValue(templetVO.getPkname()));
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageBox.show(BillCardPanel.this, "�ñ������ݿ��в����ڣ�", WLTConstants.MESSAGE_ERROR);
					}
				}
			}
		});

		item_table_templetmodify_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyTemplet2(templetVO.getTempletcode());
			}
		});

		menu_print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPrintThis(); //
			}
		});

		if (is_admin) {
			popmenu_header.add(item_table_templetmodify_2); // ����ģ��༭
			popmenu_header.add(menu_table_showRecord);
		}

		popmenu_header.add(menu_print);
		popmenu_header.show(_compent, _x, _y); //
	}

	private void modifyTemplet2(String _templetCode) {
		try {
			boolean res = new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, null);
			if (res) {
				this.reload(); //����ˢ��ҳ��
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return; //
		}
	}

	public String getSQL(String _condition) {
		String str_constraintFilterCondition = getDataconstraint();
		String str_return = null; //
		if (str_constraintFilterCondition == null) {
			if (_condition == null) {
				str_return = "select * from " + templetVO.getTablename();
			} else {
				str_return = "select * from " + templetVO.getTablename() + " where " + _condition; // ��RowID����!!
			}
		} else {
			if (_condition == null) {
				str_return = "select * from " + templetVO.getTablename() + " where (" + str_constraintFilterCondition + ")";
			} else {
				str_return = "select * from " + templetVO.getTablename() + " where (" + str_constraintFilterCondition + ") and (" + _condition + ")"; // ��RowID����!!
			}
		}

		return str_return;
	}

	//	/**
	//	 * �ж��Ƿ��������ģ��,�������Class���͵�,�򲻿�����,�����XML���͵�,����Ҫ���Ƶ�DB�н�������!! �����DB��,��֣����XML��Ҳ��,����Խ�XML�и�����Ƚ�,Ҳ����ֱ��ɾ���Լ���ʹ��XML�е�! ���XML��û��������ǰ���߼�!! 
	//	 * @return
	//	 */
	//	private boolean checkIsCanConfigTemplet(boolean _isquiet) {
	//		try {
	//			String str_buildFromType = templetVO.getBuildFromType(); //����������!!
	//			String str_buildFromInfo = templetVO.getBuildFromInfo(); //��������Ϣ!!
	//			String str_templetCode = templetVO.getTempletcode(); //ģ�����
	//			String str_templetName = templetVO.getTempletname();
	//			return new MetaDataUIUtil().checkTempletIsCanConfig(this, str_buildFromType, str_buildFromInfo, str_templetCode, str_templetName, _isquiet); //
	//		} catch (Exception ex) {
	//			MessageBox.showException(this, ex); //
	//			return false; //
	//		}
	//	}

	/**
	 * ˢ������
	 * 
	 * @param _condition
	 */
	public void refreshData(String _condition) {
		queryData(getSQL(_condition)); //
	}

	public void queryDataByCondition(String _condition) {
		queryData(getSQL(_condition)); //
	}

	public void queryDataByDSAndCondition(String _datasourcename, String _condition) {
		queryDataByDS(_datasourcename, getSQL(_condition)); //
	}

	public String getDataSourceName() {
		if (templetVO.getDatasourcename() == null || templetVO.getDatasourcename().trim().equals("null") || templetVO.getDatasourcename().trim().equals("")) {
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); // Ĭ������Դ
		} else {
			return new TBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDatasourcename()); // �������Դ!!
		}
	}

	/**
	 * �õ�����Ȩ�޹�������
	 * @return
	 */
	public String getDataconstraint() {
		if (templetVO.getDataconstraint() == null || templetVO.getDataconstraint().trim().equals("null") || templetVO.getDataconstraint().trim().equals("")) {
			return null; // Ĭ������Դ
		} else {
			return new TBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDataconstraint()); // �������Դ!!
		}
	}

	/**
	 * Ĭ��ȡ����!
	 * @param _sql
	 */
	public void queryData(String _sql) {
		queryDataByDS(getDataSourceName(), _sql); //
	}

	/**
	 * ��ָ������Դ��ȡ��!
	 * @param _dsName
	 * @param _sql
	 */
	public void queryDataByDS(String _dsName, String _sql) {
		this.reset();
		Object[] objs = null;
		try {
			objs = UIUtil.getBillCardDataByDS(_dsName, _sql, this.templetVO);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (objs != null) {
			setValue(objs); // ��������,�����к��е�RowID!!
		}

		//this.getRowNumberItemVO().setState(RowNumberItemVO.INIT); // ����Ϊ��ʼ״̬
		setBorderColor(Color.BLACK); // ���ñ߿���ɫ
		setBorderText("[" + getBorderName() + "]"); // ����ģ������!!
		//this.updateUI(); //
	}

	/**
	 * ȡ�ò���˵���е�SQL���!!!
	 * 
	 * @param _allrefdesc
	 * @return
	 */
	public String getRefDescSQL(String _allrefdesc) {
		String str_type = null;
		String str_sql = null;
		int li_pos = _allrefdesc.indexOf(":"); //
		if (li_pos < 0) {
			str_type = "TABLE";
		} else {
			str_type = _allrefdesc.substring(0, li_pos).toUpperCase(); //
		}

		if (str_type.equalsIgnoreCase("TABLE")) {
			if (li_pos < 0) {
				str_sql = _allrefdesc;
			} else {
				str_sql = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			}
		} else if (str_type.equalsIgnoreCase("TREE")) {
			_allrefdesc = _allrefdesc.trim(); // ��ȥ�ո�
			String str_remain = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			int li_pos_tree_1 = str_remain.indexOf(";"); //
			str_sql = str_remain.substring(0, li_pos_tree_1); // SQL���
		} else if (str_type.equalsIgnoreCase("CUST")) {
		}
		return str_sql;
	}

	public void reset() {
		//this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); // �����к�VO!!
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			try {
				compents[i].reset(); // ����ֵ
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void clear() {
		this.reset();
		this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		this.setEditable(false); //
	}

	public void reset(String _itemkey) {
		//this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); // �����к�VO!!
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equals(_itemkey)) {
				compents[i].reset(); //����ֵ
				break;
			}
		}
	}

	public void resetOthers(String[] _itemKeys) {
		//this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); // �����к�VO!!
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			boolean bo_iffind = false;
			for (int j = 0; j < _itemKeys.length; j++) {
				if (compents[i].getItemKey().equals(_itemKeys[j])) {
					bo_iffind = true; //˵�������ڹ�������,���ڹ��������ҵ��˸���!!!
				}
			}

			if (bo_iffind) {
			} else { //��������
				compents[i].reset(); // ����ֵ
			}
		}
	}

	/**
	 * ���һ��ؼ���ֵ
	 * @param _grouptitle
	 */
	public void resetByGrouptitle(String _grouptitle) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (_grouptitle.equals(templetItemVOs[i].getGrouptitle())) {
				reset(templetItemVOs[i].getItemkey());
			}
		}
	}

	public void setEditable(boolean _bo) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (this.templetVO.getItemType(compents[i].getItemKey()).equals("��ť")) {
				compents[i].setItemEditable(true); // ����ֵ

			} else {
				if (!compents[i].getItemKey().equalsIgnoreCase(this.templetVO.getPkname()))
					compents[i].setItemEditable(_bo); // ����ֵ
			}
		}
	}

	public void setEditable(String _itemkey, boolean _bo) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_itemkey)) {
				compents[i].setItemEditable(_bo); // ����ֵ
			}
		}
	}

	public void setVisiable(String _itemkey, boolean _bo) {
		AbstractWLTCompentPanel panel = getCompentByKey(_itemkey);//������������������ĳ���ֶβ���ʾ��������ģ���и��ֶ�ɾ���ˣ���ᱨ�������������ж�һ�¡����/2012-09-26��
		if (panel != null) {
			panel.setVisible(_bo);
		}

		//���ػ���ʾ�о� �����/2013-01-09��
		if (hm_hflowPanels_key.containsKey(_itemkey)) {
			int row_number = (Integer) hm_hflowPanels_key.get(_itemkey);
			if (_bo) {
				vflowPanel.setRowVisiable(row_number - 1, _bo);
			} else {
				String[] itemkeys = ((String) hm_hflowPanels_key.get(row_number)).split(";");
				for (int i = 0; i < itemkeys.length; i++) {
					if (getCompentByKey(itemkeys[i]).isVisible()) {
						_bo = true;
						break;
					}
				}

				if (!_bo) {
					vflowPanel.setRowVisiable(row_number - 1, _bo);
				}
			}
		}
	}

	public void setVisiable(String[] _itemkeys, boolean _bo) {
		for (int i = 0; i < _itemkeys.length; i++) {
			/*			AbstractWLTCompentPanel panel = getCompentByKey(_itemkeys[i]);//������������������ĳ���ֶβ���ʾ��������ģ���и��ֶ�ɾ���ˣ���ᱨ�������������ж�һ�¡����/2012-09-26��
						if (panel != null) {
							panel.setVisible(_bo);
						}*/
			setVisiable(_itemkeys[i], _bo);
		}
	}

	public void setBorderTitle(String _title) {
		setBorderText(_title); //
	}

	public String getInsertSQL() {
		return this.getBillVO().getInsertSQL(getiEncryptKeys());
	}

	public String getUpdateSQL() {
		return this.getBillVO().getUpdateSQL(getiEncryptKeys());
	}

	//����������Ҫ���ܵ��ֶ��嵥!!!
	public String[] getiEncryptKeys() {
		ArrayList al_keys = null; //
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getIsencrypt()) { //�����Ҫ����,�����
				if (al_keys == null) {
					al_keys = new ArrayList();
				}
				al_keys.add(templetItemVOs[i].getItemkey()); //����!
			}
		}
		if (al_keys == null) {
			return null; //
		}
		return (String[]) al_keys.toArray(new String[0]); //
	}

	/**
	 * �޸�����!!
	 * @throws Exception
	 */
	public void updateData() throws Exception {
		String str_currEditState = this.getEditState();
		if (str_currEditState == null) {
			return;
		}

		if (!str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_INSERT) && !str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			return;
		}

		String str_sql = getUpdateDataSQL(); //ȡ��SQL,������InsertҲ������Update!!!
		if (str_currEditState == WLTConstants.BILLDATAEDITSTATE_INSERT) { //
			boolean isps = UIUtil.getCommonService().getSysOptionBooleanValue("�Ƿ�����Ԥ����", false);
			if (isps) {
				UIUtil.executeUpdateByDSPS(getDataSourceName(), str_sql); //������!!
			} else {
				UIUtil.executeUpdateByDS(getDataSourceName(), str_sql); //������!!
			}
			this.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		} else if (str_currEditState == WLTConstants.BILLDATAEDITSTATE_UPDATE) { ////...
			cascadeUpdate(str_sql); //�����޸�!
			this.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		}
		saveKeepTrace(); //������ʷ�ۼ�//�����Ҫ������ʷ�汾,��洢һ����ʷ�汾,���洢��pub_bill_keeptrace��,�����ж��,ֻ���ڿ�Ƭ����²��ܴ洢��ʷ�汾��//����ҲҪ��¼�������һ����¼�����ݶ�ʧ��
		getUIDataToTraceMap(); //��ҳ���ϵ����ݸ��°汾����
		this.updateUI(); //
	}

	/**
	 * ȡ�ô����SQL
	 * @return
	 */
	public String getUpdateDataSQL() {
		String str_currEditState = this.getEditState();
		if (str_currEditState == null) {
			return null;
		}

		if (str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { //
			return getInsertSQL(); ////������SQL..
		} else if (str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { ////...
			return getUpdateSQL(); ////���ĵ�SQL
		} else {
			return null;
		}
	}

	/**
	 * ���ؼ��е�ֵ�����ʽ������..
	 */
	private void getUIDataToTraceMap() {
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_itemkey = templetItemVOs[i].getItemkey(); //
			Object obj = getCompentObjectValue(templetItemVOs[i].getItemkey()); // ȡ�����ж���
			lastKeepTrace.put(str_itemkey, obj); //
		}
	}

	//�����޸�!!!
	private void cascadeUpdate(String _sql) {
		try {
			String str_tableName = this.getTempletVO().getSavedtablename(); //����ı���!!
			if (str_tableName == null || str_tableName.trim().equals("")) {
				return; //
			}
			String[][] str_changed = getChangedItemValues(); //���б仯������!!
			cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc service = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class); //
			service.doCascadeUpdateSQL(str_tableName, str_changed, _sql, true); //ʵ��ִ��,��֤��һ������!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ȡ�ñ����ۼ���SQL
	 * @return
	 */
	public void saveKeepTrace() {//sunfujun/20120810/���ģ��2��Ҫ��һ��
		try {
			HashMap map_fieldvalus = new HashMap(); //
			for (int i = 0; i < templetItemVOs.length; i++) {
				if (templetItemVOs[i].getIskeeptrace()) { //�����Ҫ�����ۼ�
					String str_itemkey = templetItemVOs[i].getItemkey(); //
					Object obj_ui = getValueAt(str_itemkey); // ��ҳ����ȡֵ
					Object obj_trace = lastKeepTrace.get(str_itemkey); //
					if (obj_ui != null) {
						if (!obj_ui.toString().equals("" + obj_trace)) { //���ҳ���ϵ�����������޸���ʷ��һ��
							map_fieldvalus.put(str_itemkey, obj_ui.toString()); //
						}
					}
				}
			}

			//�������Ҫ�����,�����Զ�̷�����б���.
			if (map_fieldvalus.size() > 0) {
				String str_tablename = this.templetVO.getTablename(); //����
				String str_pkname = this.templetVO.getPkname();
				String str_pkvalue = this.getRealValueAt(str_pkname); //
				if (str_tablename != null && str_pkname != null && str_pkvalue != null) {
					String str_tracer = ClientEnvironment.getInstance().getLoginUserName(); //��¼��Ա����..
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					service.saveKeepTrace(str_tablename, str_pkname, str_pkvalue, map_fieldvalus, str_tracer); //
				}
			}
		} catch (Exception ex) {
			this.logger.error("������ʷ�ۼ�ʧ��!"); //
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��������,��ִ��Ĭ��ֵ��ʽ.
	 * @throws WLTAppException
	 */
	public void insertRow() throws WLTAppException {
		insertRow(true);
	}

	/**
	 * ������������,��ָ���Ƿ�ִ��Ĭ��ֵ��ʽ!
	 * @param _execdefaultfomula
	 * @throws WLTAppException
	 */
	public void insertRow(boolean _execdefaultfomula) throws WLTAppException {
		reset(); // ���������
		this.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); // �����к�VO,״̬��������״̬!!!����rowIdΪ��!!
		resetAllChildTable(); //������������ӱ�
		setAllChildTableEditable(true); //�������������ӱ��ɱ༭

		// ���ô�������ֵ!!!!
		String str_sequencename = templetVO.getPksequencename(); //
		if (str_sequencename == null || str_sequencename.trim().equals("")) {
			// JOptionPane.showMessageDialog(this, "û�ж���������,�޷�Ϊ��������ֵ!!"); //
		} else {
			if (templetVO.getPkname() != null) {
				AbstractWLTCompentPanel compent = getCompentByKey(templetVO.getPkname());
				if (compent == null) {
					//JOptionPane.showMessageDialog(this, "�����û��ItemKey����������[" + templetVO.getPkname() + "]�����!!");
				} else {
					try {
						String sequenceValue = UIUtil.getSequenceNextValByDS(getDataSourceName(), str_sequencename);
						compent.setValue(sequenceValue); //��������ֵ!!!
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "��������[" + str_sequencename + "]��nextValʧ��!" + e.getMessage());
					} //
				}
			}

		}

		// ����Ĭ��ֵ��ʽ!!!!
		if (_execdefaultfomula) { //�����Ҫִ��Ĭ��ֵ��ʽ
			execDefaultValueFormula(); // //ִ��Ĭ��ֵ��ʽ
		}
		this.updateUI(); //
	}

	/**
	 * ���õ����������ӱ��Ƿ�ɱ༭...
	 * @param _bo
	 */
	public void setAllChildTableEditable(boolean _bo) {
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (object instanceof CardCPanel_ChildTable) { ////
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object; ////
				compent.setItemEditable(_bo); //�����Ƿ�ɱ༭!!!
			}
		}
	}

	/**
	 * ����������������...
	 * @param _bo
	 */
	public void resetAllChildTable() {
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (object instanceof CardCPanel_ChildTable) { ////
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object; ////
				if (compent != null && compent.getBillListPanel() != null) {
					compent.getBillListPanel().clearTable(); //
				}
			}
		}
	}

	//ȡ������ɾ���ӱ��������� �����/2013-03-26��
	public void dealChildTable(boolean isclear) {
		ArrayList list_allsqls = new ArrayList();
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i);
			if (object instanceof CardCPanel_ChildTable) {
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object;
				//��isclearΪtrue ��Ϊ�����򱣴� �����ӱ�������ʱɾ��sql ׷���ӱ�ɾ����ʱɾ��sql
				//��isclearΪfalse ��Ϊȡ����ر� �����ӱ�ɾ����ʱɾ��sql ׷���ӱ�������ʱɾ��sql
				compent.clearList_sqls(!isclear);
				ArrayList list_sqls = compent.getList_sqls(isclear);
				for (int j = 0; j < list_sqls.size(); j++) {
					list_allsqls.add(list_sqls.get(j));
				}
			}
		}

		if (list_allsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(getDataSourceName(), list_allsqls);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * �޸ĵ�ǰ�м�¼!!!!
	 * 
	 */
	public void updateCurrRow() {
		this.getRowNumberItemVO().setState(RowNumberItemVO.UPDATE); //
		setBorderColor(Color.BLUE); // ������ɫ״̬
		setBorderText(this.getBorderName()); //

		// ���ø��ؼ��Ƿ�ɱ༭!!!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			if (compent != null) {
				if (templetItemVOs[i].getItemkey().equalsIgnoreCase(templetVO.getPkname())) {
					compent.setItemEditable(false); // ���������,��ʼ�ղ��ܱ༭!!!
				} else {
					if (templetItemVOs[i].getCardiseditable().equals("1") || templetItemVOs[i].getCardiseditable().equals("3")) {
						compent.setItemEditable(true);
					} else {
						compent.setItemEditable(false); //
					}
				}
			}
		} //

		this.updateUI(); //
	}

	/**
	 * �޸ĵ�ǰ�м�¼!!!!
	 * 
	 */
	public void initCurrRow() {
		this.getRowNumberItemVO().setState(RowNumberItemVO.INIT); //
		setBorderColor(Color.BLACK); // ������ɫ״̬
		setBorderText(this.getTempletVO().getTempletname()); //

		// ���ø��ؼ��Ƿ�ɱ༭!!!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			compent.setItemEditable(false);
		} //

		this.updateUI(); //
	}

	public String getCompentRealValue(String _key) {
		int li_pos = _key.indexOf(".");
		String str_itemkey = null;
		String str_subfix = null;
		if (li_pos > 0) {
			str_itemkey = _key.substring(0, li_pos); //
			str_subfix = _key.substring(li_pos + 1, _key.length()); //
		} else {
			str_itemkey = _key;
		}

		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(str_itemkey)) {
				if (li_pos > 0) { //����к��
					Object obj = compents[i].getObject();
					if (obj instanceof ComBoxItemVO) {
						return ((ComBoxItemVO) obj).getItemValue(str_subfix); //
					} else if (obj instanceof RefItemVO) {
						return ((RefItemVO) obj).getItemValue(str_subfix); //
					}
				} else {
					return compents[i].getValue();
				}
			}
		}
		return "";
	}

	private String getObjectRealValue(Object _obj) {
		if (_obj == null) {
			return null;
		}
		if (_obj instanceof StringItemVO) {
			return ((StringItemVO) _obj).getStringValue(); //
		}

		if (_obj instanceof ComBoxItemVO) {
			return ((ComBoxItemVO) _obj).getId(); //
		}

		if (_obj instanceof RefItemVO) {
			return ((RefItemVO) _obj).getId(); //
		}

		return _obj.toString();
	}

	public Pub_Templet_1_ItemVO getTempletItemVO(String key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(key))
				return templetItemVOs[i];
		}
		return null;
	}

	public Object getCompentObjectValue(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_key)) {
				return compents[i].getObject(); //
			}
		}
		return null;
	}

	public Object getValueAt(String _key) {
		return getCompentObjectValue(_key);
	}

	public String getRealValueAt(String _key) {
		return getCompentRealValue(_key);
	}

	/**
	 * ����������ֵ
	 * 
	 * @param _key
	 * @param _value
	 */
	public void setRealValueAt(String _key, String _value) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent == null) {
			return;
		}

		compent.reset(); //

		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(_key);
		String str_type = itemVO.getItemtype();
		if (str_type.equals(WLTConstants.COMP_LABEL) || //Labe
				str_type.equals(WLTConstants.COMP_TEXTFIELD) || //�ı���
				str_type.equals(WLTConstants.COMP_NUMBERFIELD) || //���ֿ�
				str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || //����
				str_type.equals(WLTConstants.COMP_TEXTAREA) || //�����ı���
				str_type.equals(WLTConstants.COMP_CHECKBOX) //��ѡ��
		) { //�ı���
			setCompentObjectValue(_key, new StringItemVO(_value));
		} else if (str_type.equals(WLTConstants.COMP_NUMBERFIELD)) { //���ֿ�
			int li_pos = _value.lastIndexOf(".0");
			if (li_pos > 0) {
				setCompentObjectValue(_key, new StringItemVO(_value.substring(0, li_pos))); //
			} else {
				setCompentObjectValue(_key, new StringItemVO(_value)); //
			}
		} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //������
			CardCPanel_ComboBox comBoxPanel = (CardCPanel_ComboBox) getCompentByKey(_key); //
			JComboBox comboBox = comBoxPanel.getComBox(); // ȡ��������
			for (int i = 0; i < comboBox.getItemCount(); i++) { // ����!!
				ComBoxItemVO vo = (ComBoxItemVO) comboBox.getItemAt(i);
				if (vo.getId().equals(_value)) {
					comboBox.setSelectedIndex(i);
					break;
				}
			}
		} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT)) { //����Ǹ��ֲ���1
			CardCPanel_Ref refPanel = (CardCPanel_Ref) getCompentByKey(_key); //
			if (refPanel.getObject() == null) { //���Ϊ��,��ֱ������
				refPanel.setObject(new RefItemVO(_value, _value, _value)); //ֱ������
			} else { //����ֻ�޸�ID
				RefItemVO refitemVO = (RefItemVO) refPanel.getObject(); //
				refitemVO.setId(_value); //
			}
		} else if (str_type.equals(WLTConstants.COMP_DATE) || //����
				str_type.equals(WLTConstants.COMP_DATETIME) || //ʱ��
				str_type.equals(WLTConstants.COMP_BIGAREA) || //���ı���
				str_type.equals(WLTConstants.COMP_CALCULATE) || //������
				str_type.equals(WLTConstants.COMP_PICTURE) || //ͼƬ
				str_type.equals(WLTConstants.COMP_COLOR) || //��ɫ
				str_type.equals(WLTConstants.COMP_LINKCHILD) || //�����ӱ�
				str_type.equals(WLTConstants.COMP_IMPORTCHILD) || //�����ӱ�
				str_type.equals(WLTConstants.COMP_FILECHOOSE) //�ļ�ѡ��
		) {
			setCompentObjectValue(_key, new RefItemVO(_value, null, _value)); //����������ؼ�!!
		} else {
			setCompentObjectValue(_key, new StringItemVO(_value)); //����������ؼ�!!
		}
	}

	//���ø����ؼ���ǰ����ɫ!�ı���,������,���յ�,�ݲ�ʵ��,�Ժ�����ؿ�����ͨ���޸�ֵ�ٴ����¼���ʵ�֣�����ֱ���޸Ŀؼ�������ʵ��!����α�ֵ֤��ؼ����Ա���һ��!
	public void setItemForeGroundColor(String _key, String _foreGroundColor) {

	}

	public HashMap getAllRealValuesWithHashMap() {
		HashMap map = new HashMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase("VERSION")) {
				if (getCompentRealValue(templetItemVOs[i].getItemkey()) != null && !getCompentRealValue(templetItemVOs[i].getItemkey()).toString().equals("")) {
					int preversion = new Integer(getCompentRealValue(templetItemVOs[i].getItemkey()).toString()).intValue();
					Object version = new Integer(preversion + 1);
					map.put("VERSION", version);
				} else
					map.put(templetItemVOs[i].getItemkey(), new Integer(1).toString());
			} else
				map.put(templetItemVOs[i].getItemkey(), getCompentRealValue(templetItemVOs[i].getItemkey()));
		}
		return map;
	}

	public Object[] getAllObjectValues() {
		Object[] objs = new Object[templetItemVOs.length];
		for (int i = 0; i < objs.length; i++) {
			objs[i] = getCompentObjectValue(templetItemVOs[i].getItemkey()); // ȡ�����ж���
		}
		return objs;
	}

	public HashMap getAllObjectValuesWithHashMap() {
		HashMap map = new HashMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase("VERSION")) {
				if (getCompentObjectValue(templetItemVOs[i].getItemkey()) != null && !getCompentObjectValue(templetItemVOs[i].getItemkey()).toString().equals("")) {
					int preversion = new Integer(getCompentObjectValue(templetItemVOs[i].getItemkey()).toString()).intValue();
					Object version = new Integer(preversion + 1);
					map.put("VERSION", version);
				} else
					map.put(templetItemVOs[i].getItemkey(), new Integer(1).toString());
			} else
				map.put(templetItemVOs[i].getItemkey(), getCompentObjectValue(templetItemVOs[i].getItemkey()));
		}
		return map;
	}

	public VectorMap getAllObjectValuesWithVectorMap() {
		VectorMap map = new VectorMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			map.put(templetItemVOs[i].getItemkey(), getCompentObjectValue(templetItemVOs[i].getItemkey()));
		}
		return map;
	}

	public void setValue(Object[] _objs) {
		reset(); // �������пؼ�
		this.setRowNumberItemVO((RowNumberItemVO) _objs[0]); // �����к�����VO!!!
		for (int i = 1; i < _objs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByIndex(i - 1); //
			String str_key = compent.getItemKey(); //
			setCompentObjectValue(str_key, _objs[i]); //
		}
	}

	public void setValue(HashMap _map) {
		reset();
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			setCompentObjectValue(str_key, _map.get(str_key)); //
		}
	}

	//����BillVO,Ӧ�����ڴ��м�¼����ʱ���õ�ֵ!Ȼ����ȷ����ťʱ,���Խ�ҳ���ϵ�ʵ��ֵ��ԭ����ֵ���бȽ�,�Ӷ��жϳ��Ƿ����˱仯!! 
	public void setBillVO(BillVO _billVO) {
		reset(); //�����card���������ݣ�
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			try {
				setCompentObjectValue(str_key, _billVO.getObject(str_key)); //
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * ����ӱ��ģ����õ���_billVO���ֶ�Ҫ��BillCardPanel ����ֶ��ٵĻ����������BillCardPanel���ֵ���ǣ�����Ϊ�գ��������ṩ�÷���
	 * @param _billVO
	 */
	public void setBillVOHasKey(BillVO _billVO) {
		String[] billVO_keys = _billVO.getKeys();
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			try {
				for (int j = 0; j < billVO_keys.length; j++) {
					if (str_key.equals(billVO_keys[j])) {
						setCompentObjectValue(str_key, _billVO.getObject(str_key)); //
						break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void setValue(HashVO _vo) {
		reset();
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			setCompentObjectValue(str_key, _vo.getObjectValue(str_key)); //
		}
	}

	//����ֵ....
	public void setCompentObjectValue(String _key, Object _obj) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent != null) {
			lastKeepTrace.put(_key, _obj); //������ʷ�汾�м�¼�µ�ǰҳ���ϵ�����!!!!
			compent.setObject(_obj); //
		}
	}

	/**
	 *ȡ�÷����仯�����ݵľ�����,�����һ��ͨ��API��ֵ��ֵ!!
	 * @return
	 */
	public HashMap getChangedItemOldValues() {
		HashMap hm_return = new HashMap(); //
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (compent.isVisible()) { //����ÿؼ�����ʾ,��Ϊ����ʾ,�Ͳ��ܲ鿴,����û������
				String str_itemkey = compent.getItemKey(); ////
				Object old_obj = lastKeepTrace.get(str_itemkey); //oldversion,������
				Object new_obj = compent.getObject(); //��ǰҳ������!!
				if (compent instanceof CardCPanel_CheckBox) { //����ǹ�ѡ��,��Ҫ�ر���,���Ƿ�!
					if (old_obj == null) {
						old_obj = new StringItemVO("N"); //
					}

					if (new_obj == null) {
						new_obj = new StringItemVO("N"); //
					}

					if (!old_obj.equals(new_obj)) {
						hm_return.put(str_itemkey, old_obj); //
					}
					continue; //
				}

				if (old_obj == null) { //�����ֵΪ��!!
					if (new_obj == null) { //�����ֵ��Ϊ��
					} else {
						if (!new_obj.equals(old_obj)) { //������߲���
							hm_return.put(str_itemkey, old_obj); //
						}
					}
				} else { //�����ֵ��Ϊ��
					if (new_obj == null) { //�����ֵΪ��
						if (!old_obj.equals(new_obj)) { //������߲���
							hm_return.put(str_itemkey, old_obj); //
						}
					} else {
						if (old_obj.equals(new_obj)) { //���߶���Ϊ��!!
						} else {
							hm_return.put(str_itemkey, old_obj); //���߲���,�򴴽�֮
						}
					}
				}
			}
		}
		return hm_return; //
	}

	//ȡ�ñ仯��ֵ!!
	public String[][] getChangedItemValues() {
		ArrayList al_return = new ArrayList(); //
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (compent.isVisible()) { //����ÿؼ�����ʾ,��Ϊ����ʾ,�Ͳ��ܲ鿴,����û������
				String str_itemkey = compent.getItemKey(); ////
				Object old_obj = lastKeepTrace.get(str_itemkey); //oldversion,������
				String str_oldRealValue = getObjectRealValue(old_obj); //
				String str_newRealValue = getRealValueAt(str_itemkey); //��ǰҳ������!!
				boolean isEquals = getTBUtil().compareTwoString(str_oldRealValue, str_newRealValue); //
				if (!isEquals) { //��������!!
					al_return.add(new String[] { str_itemkey, str_oldRealValue, str_newRealValue }); //
				}
			}
		}
		String[][] str_return = new String[al_return.size()][3]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = (String[]) al_return.get(i); //
		}
		return str_return; //
	}

	/**
	 * ȡ�÷����仯����ľ����ݵ�SQL
	 * @return
	 */
	public String[] getChangedItemOldValueSQL() {
		HashMap hm_changed = getChangedItemOldValues(); //
		String[] str_keys = (String[]) hm_changed.keySet().toArray(new String[0]); //
		String[] str_sqls = new String[str_keys.length]; //

		for (int i = 0; i < str_keys.length; i++) {
			String str_value = "null"; //
			Object obj = hm_changed.get(str_keys[i]); //
			if (obj != null) {
				str_value = "'" + obj + "'";
			}
			str_sqls[i] = "insert into pub_billcolhistory (id,creater,createtime,tabname,colname,colvalue) value ('','','" + str_keys[i] + "'," + str_value + ")"; //
			//System.out.println(str_sqls[i]); //
		}

		return str_sqls;
	}

	//����ֵ..
	public void setValueAt(String _key, Object _obj) {
		setCompentObjectValue(_key, _obj);
	}

	public AbstractWLTCompentPanel[] getAllCompents() {
		return (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
	}

	public AbstractWLTCompentPanel getCompentByIndex(int _index) {
		String key = templetItemVOs[_index].getItemkey();
		return getCompentByKey(key);
	}

	public AbstractWLTCompentPanel getCompentByKey(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_key)) {
				return compents[i];
			}
		}

		return null;
	}

	/**
	 * ȡ��ĳһ������й�ʽ,����һ������
	 * 
	 * @param _itemkey
	 * @return
	 */
	public String[] getEditFormulas(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				String str_formula = templetItemVOs[i].getEditformula();
				if (str_formula != null && !str_formula.trim().equals("null") && !str_formula.trim().equals("")) {
					String[] str_editformulas = getTBUtil().split1(str_formula, ";");
					return str_editformulas;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private synchronized void onChanged(String _itemkey) {
		if (bo_isallowtriggereditevent) { // ���������
			bo_isallowtriggereditevent = false;
			Object obj = this.getCompentObjectValue(_itemkey); // ȡ�õ�ǰֵ
			execEditFormula(_itemkey, false); // ִ�б༭��ʽ..������һ�д���˭��˭���д���һ������!!!!!!
			for (int i = 0; i < v_listeners.size(); i++) {
				BillCardEditListener listener = (BillCardEditListener) v_listeners.get(i);
				listener.onBillCardValueChanged(new BillCardEditEvent(_itemkey, obj, this)); //
			}

			bo_isallowtriggereditevent = true; //
		}
	}

	private synchronized void onChangedAndFocusNext(String _itemkey) {
		onChanged(_itemkey);
		AbstractWLTCompentPanel actionpanel = this.getNextCompent(_itemkey); //
		//���������һ��!
		if (actionpanel != null) {
			actionpanel.focus(); //
		}
	}

	/**
	 * 
	 * @param _itemkey
	 * @return
	 */
	private AbstractWLTCompentPanel getNextCompent(String _itemkey) {
		int li_startindex = findIndex(_itemkey);
		for (int i = li_startindex + 1; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].isCardisshowable().booleanValue()) { //���ĳ���ؼ��ǿ�Ƭ��ʾ��!!
				AbstractWLTCompentPanel panel = getCompentByKey(templetItemVOs[i].getItemkey()); //
				if (panel != null && panel.isVisible()) { //����ؼ���,������ʾ��!!
					return panel;
				}
			}
		}

		return null;
	}

	private int findIndex(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return i;
			}
		}
		return 9999;
	}

	/**
	 * ����ĳһ��ģ��ItemVO
	 * @param _itemkey
	 * @return
	 */
	private Pub_Templet_1_ItemVO findTempletItemVO(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return templetItemVOs[i]; //
			}
		}

		return null; //
	}

	/**
	 * ע���¼�
	 * 
	 * @param _listener
	 */
	public void addBillCardEditListener(BillCardEditListener _listener) {
		v_listeners.add(_listener); //
	}

	/**
	 * ִ��ĳһ��ļ��ع�ʽ!
	 */
	public void execEditFormula(String _itemkey) {
		execEditFormula(_itemkey, true);
	}

	/**
	 * �Ƿ�����ִ�м����ܿؼ��Ƿ�ɱ༭Ҳִ��
	 * @param _itemkey
	 * @param iseverExec
	 */
	public void execEditFormula(String _itemkey, boolean iseverExec) {
		if (!iseverExec) {
			AbstractWLTCompentPanel compent = getCompentByKey(_itemkey);
			if (compent == null || !compent.isItemEditable()) { // ����ؼ����ɱ༭��ִ�б༭��ʽ
				return;
			}
		}
		String[] str_editformulas = getEditFormulas(_itemkey); //
		if (str_editformulas == null || str_editformulas.length == 0) {
			return;
		}

		// ѭ������ʽ...
		for (int i = 0; i < str_editformulas.length; i++) {
			dealFormula(str_editformulas[i]); // ����ִ�й�ʽ!!!
			logger.debug("��Ƭִ��[" + _itemkey + "]�ı༭��ʽ:[" + str_editformulas[i] + "]"); //
		}
	}

	/**
	 * ָ�����ͣ��ĳ���ؼ���!
	 * @param _itemKey
	 */
	public void focusAt(String _itemKey) {
		AbstractWLTCompentPanel[] itemCompents = getAllCompents(); //
		for (int i = 0; i < itemCompents.length; i++) {
			if (itemCompents[i].getItemKey().equalsIgnoreCase(_itemKey)) {
				itemCompents[i].focus(); //���ͣ������!!!
				break; //
			}
		}
	}

	/**
	 * ��ǰ������ֱ�ӵ㱣�水ťʱ��ִ�й�����ڵ����պ�һ���ؼ��ı༭��ʽ!
	 * �����ṩ�÷���,����ȥ��һ���������λ��!Ȼ��ǿ��ִ��һ�±༭��ʽ!!
	 */
	public void stopEditing() {
		AbstractWLTCompentPanel[] itemCompents = getAllCompents(); //
		for (int i = 0; i < itemCompents.length; i++) {
			if (itemCompents[i].isFocusOwner()) { //����ÿؼ��ǹ������,��ִ����༭��ʽ!!! �пؼ��ع��˸÷���!! �����ı���!!
				execEditFormula(itemCompents[i].getItemKey(), false); //�˹�ִ�б༭��ʽ!!
				this.setFocusable(true); //
				this.requestFocus(); //���Լ��õ����,��֤�´β����ظ������༭��ʽ!
				break; //
			}
		}
	}

	/**
	 * ִ��Ĭ��ֵ��ʽ
	 * 
	 */
	public void execDefaultValueFormula() throws WLTAppException {// Ŀǰֱ�ӵ��ַ�������{Date(10)},{Date(19)}
		for (int i = 0; i < templetItemVOs.length; i++) {
			Pub_Templet_1_ItemVO tempitem = templetItemVOs[i];
			String str_key = tempitem.getItemkey(); //
			String str_type = tempitem.getItemtype(); //
			String formula = tempitem.getDefaultvalueformula();
			if (formula != null && !formula.trim().equals("")) {
				JepFormulaParseAtUI jepParse = new JepFormulaParseAtUI(this); //
				String[] str_formulas = getTBUtil().split1(formula, ";"); //
				for (int j = 0; j < str_formulas.length; j++) {
					//System.out.println("ִ��Ĭ��ֵ��ʽ:\r\n" + str_formulas[j]); 
					Object obj = jepParse.execFormula(str_formulas[j]); //

					//System.out.println("Ĭ��ֵ��ʽִ�еĽ��:" + obj);  
					if (j == str_formulas.length - 1) { //���������һ��,����Ϊ�Ƿ���ֵ
						if ("Y".equals(tempitem.getIsmustinput2())) { //����Ǳ�����..
							if (obj == null) { //
								throw new WLTAppException("��ѡ��һ��[" + tempitem.getItemname() + "]!"); //
							}

							if (obj instanceof String) { //
								String vo = (String) obj; //
								if (vo == null || vo.equals("")) {
									throw new WLTAppException("��ѡ��һ��[" + tempitem.getItemname() + "]!"); //
								}
							} else if (obj instanceof StringItemVO) {
								StringItemVO vo = (StringItemVO) obj; //
								if (vo == null || vo.getStringValue() == null || vo.getStringValue().equals("")) {
									throw new WLTAppException("��ѡ��һ��[" + tempitem.getItemname() + "]!"); //
								}
							} else if (obj instanceof ComBoxItemVO) {
								ComBoxItemVO vo = (ComBoxItemVO) obj; //
								if (vo == null || vo.getId() == null || vo.getId().equals("")) {
									throw new WLTAppException("��ѡ��һ��[" + tempitem.getItemname() + "]!"); //
								}
							} else if (obj instanceof RefItemVO) {
								RefItemVO vo = (RefItemVO) obj; //
								if (vo == null || vo.getId() == null || vo.getId().equals("")) {
									throw new WLTAppException("��ѡ��һ��[" + tempitem.getItemname() + "]!"); //
								}
							}
						} //end ����Ǳ�������

						try {
							if (obj instanceof String) { //
								String str_objstr = (String) obj; //
								if (str_objstr.equals("ok")) { //�����ok������
									continue; //
								}

								if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //�����������
									setCompentObjectValue(str_key, new ComBoxItemVO(str_objstr, str_objstr, str_objstr)); //
								} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
										str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���1
										str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //��ѡ����1
										str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
										str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //�б�ģ�����
										str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //����ģ�����
										str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //ע���������
										str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //ע�����
										str_type.equals(WLTConstants.COMP_DATE) || //����
										str_type.equals(WLTConstants.COMP_DATETIME) || //ʱ��
										str_type.equals(WLTConstants.COMP_BIGAREA) || //���ı���
										str_type.equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���
										str_type.equals(WLTConstants.COMP_COLOR) || //��ɫѡ���
										str_type.equals(WLTConstants.COMP_CALCULATE) || //������
										str_type.equals(WLTConstants.COMP_PICTURE) //ͼƬѡ���
								) { //����ǲ���...
									setCompentObjectValue(str_key, new RefItemVO(str_objstr, str_objstr, str_objstr)); //
								} else {
									setCompentObjectValue(str_key, new StringItemVO(str_objstr)); //
								}
							} else if (obj instanceof java.lang.Double) {
								setCompentObjectValue(str_key, new StringItemVO("" + obj)); //
							} else {
								setCompentObjectValue(str_key, obj); //
							}
						} catch (Exception ex) {
							this.logger.error("����[" + str_key + "]��Ĭ��ֵʧ��!!!", ex); //
						}

					}
				}

			}
		}
	} //

	/**
	 * ����ִ��ĳһ����ʽ..ʹ��JEPȥִ��!!
	 * 
	 * @param _formula
	 */
	private void dealFormula(String _formula) {
		//String str_formula = UIUtil.replaceAll(_formula, " ", "");
		String str_subfix_new_value = getJepFormulaValue(_formula); //
	}

	private String getJepFormulaValue(String _exp) {
		JepFormulaParse jepParse = new JepFormulaParseAtUI(this); //����������!!
		Object obj = jepParse.execFormula(_exp);
		if (obj == null) {
			return "";
		}
		String str_return = "";

		if (obj instanceof Double) {
			Double d_value = (Double) obj;
			double ld_tmp = d_value.doubleValue();
			long ll_tmp = d_value.longValue(); //
			if (ld_tmp == ll_tmp) {
				return "" + ll_tmp;
			} else {
				return "" + ld_tmp;
			}
		} else if (obj instanceof String) {
			str_return = "" + obj;
		}

		return str_return;
	}

	/**
	 * ȡ���к�����VO
	 * 
	 * @return
	 */
	public RowNumberItemVO getRowNumberItemVO() {
		return rowNumberItemVO; //
	}

	/**
	 * �����к�����VO
	 * 
	 * @param rowNumberItemVO
	 */
	public void setRowNumberItemVO(RowNumberItemVO rowNumberItemVO) {
		this.rowNumberItemVO = rowNumberItemVO;
	}

	/**
	 * ����ȡ�õ�ǰ״̬!
	 * 
	 * @return
	 */
	public String getEditState() {
		return rowNumberItemVO.getState(); //
	}

	public void setEditState(String _state) {
		if (_state.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			this.rowNumberItemVO.setState(_state);
			setBorderColor(Color.BLACK); //
		} else if (_state.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			this.rowNumberItemVO.setState(_state);
			setBorderColor(new Color(0, 128, 0)); //
			this.updateUI();
		} else if (_state.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			this.rowNumberItemVO.setState(_state);
			setBorderColor(Color.BLUE); //
		}

		this.updateUI();
	}

	/**
	 * �������пؼ�״̬Ϊģ���ж��������ʱ��״̬
	 */
	public void setEditableByInsertInit() {
		this.setEditable(false);
		// ���ø��ؼ��Ƿ�ɱ༭!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			if (compent != null) {
				if (templetItemVOs[i].getItemkey().equalsIgnoreCase(templetVO.getPkname())) {
					compent.setItemEditable(false); // ���������,��ʼ�ղ��ܱ༭!!!
				} else {
					if (templetItemVOs[i].getCardiseditable().equals("1") || templetItemVOs[i].getCardiseditable().equals("2")) {
						compent.setItemEditable(true);
					} else {
						compent.setItemEditable(false);
					}
				}
			}
		} //
	}

	/**
	 * �������пؼ�״̬Ϊģ���ж���ı༭ʱ��״̬
	 */
	public void setEditableByEditInit() {
		this.setEditable(false);
		// ���ø��ؼ��Ƿ�ɱ༭!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			if (compent != null) {
				if (templetItemVOs[i].getItemkey().equalsIgnoreCase(templetVO.getPkname())) {
					compent.setItemEditable(false); // ���������,��ʼ�ղ��ܱ༭!!!
				} else {
					if (templetItemVOs[i].getCardiseditable().equals("1") || templetItemVOs[i].getCardiseditable().equals("3")) {
						compent.setItemEditable(true);
					} else {
						compent.setItemEditable(false);
					}
				}
			}
		} //
	}

	/**
	 * �������пؼ�״̬Ϊģ���ж���Ĺ�����ʱ��״̬
	 */
	public void setEditableByWorkFlowInit() {
		//this.setEditable(false);  //ͳͳ����
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			boolean isWFEditable = templetItemVOs[i].getWorkflowiseditable().booleanValue();
			if (compent != null) {
				if (compent instanceof CardCPanel_Button) {
					compent.setItemEditable(true);
				} else if (compent instanceof CardCPanel_ChildTable) {
					CardCPanel_ChildTable cchildTable = (CardCPanel_ChildTable) compent;
					cchildTable.setOnlyLookat(); //ֻ���Բ鿴��
				} else {
					compent.setItemEditable(isWFEditable); //
				}
			}
		}
	}

	private void setBorderColor(Color _color) {
		if (border instanceof TitledBorder) {
			((TitledBorder) this.border).setTitleColor(_color); //
		}
	}

	private void setBorderText(String _text) {
		if (border instanceof TitledBorder) {
			((TitledBorder) this.border).setTitle(_text); //
		}
	}

	public BillVO getBillVO() {
		return getBillVO(true);
	}

	/**
	 * �õ���ǰ����
	 * 
	 * @return
	 */
	public BillVO getBillVO(boolean _bo) {
		BillVO vo = new BillVO();
		vo.setTempletCode(templetVO.getTempletcode()); //
		vo.setTempletName(templetVO.getTempletname()); //
		vo.setQueryTableName(templetVO.getTablename());
		vo.setSaveTableName(templetVO.getSavedtablename()); //
		vo.setPkName(templetVO.getPkname()); // �����ֶ���
		vo.setSequenceName(templetVO.getPksequencename()); // ������
		vo.setToStringFieldName(templetVO.getTostringkey()); //����ToString���ֶ���!!!�����������,�ڹ���������Ҫָ����������!!

		int li_length = templetItemVOs.length;

		// ����ItemKey
		String[] all_Keys = new String[li_length + 1]; //
		all_Keys[0] = this.str_rownumberMark; // �к�
		for (int i = 1; i < all_Keys.length; i++) {
			all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
		}

		// ���е�����
		String[] all_Names = new String[li_length + 1]; //
		all_Names[0] = "�к�"; // �к�
		for (int i = 1; i < all_Names.length; i++) {
			all_Names[i] = this.templetVO.getItemNames()[i - 1];
		}

		String[] all_Types = new String[li_length + 1]; //
		all_Types[0] = "�к�"; // �к�
		for (int i = 1; i < all_Types.length; i++) {
			all_Types[i] = this.templetVO.getItemTypes()[i - 1];
		}

		String[] all_ColumnTypes = new String[li_length + 1]; //
		all_ColumnTypes[0] = "NUMBER"; // �к�
		for (int i = 1; i < all_ColumnTypes.length; i++) {
			all_ColumnTypes[i] = this.templetItemVOs[i - 1].getSavedcolumndatatype(); //
		}

		boolean[] bo_isNeedSaves = new boolean[li_length + 1];
		bo_isNeedSaves[0] = false; // �к�
		for (int i = 1; i < bo_isNeedSaves.length; i++) {
			bo_isNeedSaves[i] = this.templetItemVOs[i - 1].isNeedSave();
		}

		vo.setKeys(all_Keys); //
		vo.setNames(all_Names); //
		vo.setItemType(all_Types); // �ؼ�����!!
		vo.setColumnType(all_ColumnTypes); // ���ݿ�����!!
		vo.setNeedSaves(bo_isNeedSaves); // �Ƿ���Ҫ����!!

		Object[] allObjs = getAllObjectValues();
		Object[] newObjs = new Object[allObjs.length + 1]; //
		newObjs[0] = getRowNumberItemVO(); //
		for (int i = 0; i < allObjs.length; i++) {
			newObjs[i + 1] = allObjs[i];
		}
		if (_bo) {
			for (int i = 1; i < all_Types.length; i++) {
				String type = this.templetVO.getItemTypes()[i - 1];
				if (type.endsWith("�ӱ�")) {
					String key = all_Keys[i];
					BillListPanel list = null;
					if (type.equals(WLTConstants.COMP_LINKCHILD)) {
						CardCPanel_ChildTable childListPanel = (CardCPanel_ChildTable) this.getCompentByKey(key);
						childListPanel.setObject(null);
						list = childListPanel.getBillListPanel();
					} else {
						CardCPanel_ChildTableImport tableImport = (CardCPanel_ChildTableImport) this.getCompentByKey(key);
						//tableImport.setObject(vo.getRefItemVOValue(key));
						list = tableImport.getBillListPanel();
					}

					Object[] childTableInfo = new Object[2];
					childTableInfo[0] = list.getBillVOs();
					childTableInfo[1] = list.getTempletVO();
					vo.setUserObject(key, childTableInfo);
				}
			}
		}
		vo.setDatas(newObjs); //
		return vo; //
	}

	public boolean checkValidate() {
		return checkValidate(this);
	}
	/**
	 * У��
	 * @return
	 */
	public boolean checkValidate(java.awt.Container _parent) {
		return (checkIsNullValidate() && checkItemLengthValidate(_parent) && checkIsUniqueValidate(_parent)&&checkSelfDescValidate()); //��ֻ����ǿ�У��,�Ժ󻹻�����У�鹫ʽ!!
	}

	public boolean newCheckValidate(String type) {//����������ύ��ȷ��������֤save��submit
		return newCheckValidate(type, this);
	}

	public boolean newCheckValidate(String type, java.awt.Container _parentContainer) {//����������ύ��ȷ��������֤save��submit
		if ("save".equals(type)) {
			if (this.getTempletVO().getCardsaveifcheck()) {
				return (checkSelfDescValidate() && checkItemLengthValidate(_parentContainer));
			} else {
				return checkValidate();
			}
		} else {
			return checkValidate();
		}

	}

	/**
	 * �Զ�����֤ 
	 * �Զ�����֤���̳�AbstractCardSaveSelfCheck
	 * @return
	 */
	private boolean checkSelfDescValidate() {
		String desc = this.getTempletVO().getCardsaveselfdesccheck();
		if (desc != null && !"".equals(desc.trim())) {
			try {
				AbstractCardSaveSelfCheck acsc = (AbstractCardSaveSelfCheck) Class.forName(desc).newInstance();
				return acsc.checkValidate(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean checkIsNullValidate() {
		return checkIsNullValidate(this); //
	}

	/**
	 * �ǿպ;���У��,
	 * @return
	 */
	private boolean checkIsNullValidate(java.awt.Container _parent) {
		String[] str_keys = this.getTempletVO().getItemKeys(); //���е�key
		String[] str_names = this.getTempletVO().getItemNames(); //���е�Name
		boolean[] bo_isMustInputs = this.getTempletVO().getItemIsMustInputs(); //�Ƿ������
		boolean[] bo_cardShowAble = this.getTempletVO().getItemCardShowAble();//�Ƿ�ɼ�
		StringBuffer showmsg = new StringBuffer();
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		boolean ifcheck = false;
		for (int i = 0; i < str_keys.length; i++) { //����������
			if (bo_isMustInputs[i] && bo_cardShowAble[i]) {
				for (int j = 0; j < compents.length; j++) {
					if (compents[j].getItemKey().equalsIgnoreCase(str_keys[i])) {
						String grouptitle = this.getTempletItemVO(compents[j].getItemKey()).getGrouptitle();
						if (grouptitle == null || "".equals(grouptitle.trim())) {
							if (compents[j].isVisible() && compents[j].isItemEditable()) { //����ÿؼ�û����Ļ������ҿؼ���ʾ����У�飻����У��
								ifcheck = true;
								break;
							}
							break;
						} else if (compents[j].isVisible() && compents[j].isItemEditable() && isGroupVisiable(grouptitle)) { //�ÿؼ����飬�ؼ���ʾ���ҿؼ���������ʾ����У�飻����У��
							ifcheck = true;
							break;
						}
						break;
					}
				}

				if (ifcheck) {
					ifcheck = false;
					Object obj = getCompentObjectValue(str_keys[i]);
					if (obj == null) {
						showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "������Ϊ��!\r\n"); //
					} else {
						if (obj instanceof StringItemVO) {
							StringItemVO new_name = (StringItemVO) obj;
							if (new_name == null || "".equals(new_name.toString().trim())) {
								showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "������Ϊ��!\r\n"); //
							}
						} else if (obj instanceof RefItemVO) {
							RefItemVO new_name = (RefItemVO) obj;
							if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {//
								showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "������Ϊ��!\r\n"); //
							}
						} else if (obj instanceof ComBoxItemVO) {
							ComBoxItemVO new_name = (ComBoxItemVO) obj;
							if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {//�������id������Ӧ���ж�id�Ƿ�Ϊ�ա����/2012-08-15��
								showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "������Ϊ��!\r\n"); //
							}
						} else {
							String new_name = (String) obj;
							if (new_name == null || "".equals(new_name.trim())) {
								showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "������Ϊ��!\r\n"); //
							}
						}
					}
				}
			}
		}

		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) { //����������Ϊ�գ����жϾ����			
			boolean[] bo_isWarnInputs = this.getTempletVO().getItemIsWarnInputs(); //�Ƿ񾯸���			
			for (int i = 0; i < str_keys.length; i++) {
				if (bo_isWarnInputs[i] && bo_cardShowAble[i]) { //����Ǿ�����ҿɼ�!!
					for (int j = 0; j < compents.length; j++) {
						if (compents[j].getItemKey().equalsIgnoreCase(str_keys[i])) {
							String grouptitle = this.getTempletItemVO(compents[j].getItemKey()).getGrouptitle();
							if (grouptitle == null || "".equals(grouptitle.trim())) {
								if (compents[j].isVisible()) { //����ÿؼ�û����Ļ������ҿؼ���ʾ����У�飻����У��
									ifcheck = true;
									break;
								}
								break;
							} else if (compents[j].isVisible() && isGroupVisiable(grouptitle)) { //�ÿؼ����飬�ؼ���ʾ���ҿؼ���������ʾ����У�飻����У��
								ifcheck = true;
								break;
							}
							break;
						}
					}
					if (ifcheck) {
						ifcheck = false;
						Object obj = getCompentObjectValue(str_keys[i]);
						if (obj == null) {
							showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "��Ϊ��!\r\n"); //
						} else {
							if (obj instanceof StringItemVO) {
								StringItemVO new_name = (StringItemVO) obj;
								if (new_name == null || "".equals(new_name.toString().trim())) {
									showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "��Ϊ��!\r\n"); //
								}
							} else if (obj instanceof RefItemVO) {
								RefItemVO new_name = (RefItemVO) obj;
								if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {
									showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "��Ϊ��!\r\n"); //
								}
							} else if (obj instanceof ComBoxItemVO) {
								ComBoxItemVO new_name = (ComBoxItemVO) obj;
								if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {
									showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "��Ϊ��!\r\n"); //
								}
							} else {
								String new_name = (String) obj;
								if (new_name == null || "".equals(new_name.trim())) {
									showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "��Ϊ��!\r\n"); //
								}
							}
						}
					}
				}
			}
			if ("".equals(showmsg.toString())) {
				return true;
			}
			showmsg.append(" �Ƿ����?");
			int option = MessageBox.showConfirmDialog(_parent, showmsg.toString(), "��ʾ", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		str_showmsg = str_showmsg.substring(0, str_showmsg.length() - 2); //
		MessageBox.show(_parent, str_showmsg);
		return false;
	}

	/**
	 * ����У��,
	 * @return
	 */
	private boolean checkItemLengthValidate(java.awt.Container _parent) {
		String[] str_keys = this.getTempletVO().getItemKeys(); //���е�key
		String[] str_names = this.getTempletVO().getItemNames(); //���е�Name
		int[] str_lengths = this.getTempletVO().getItemLengths(); //���е�Name��Ӧ��length
		int[] str_lengthscale = this.getTempletVO().getItemLengthsScale(); //���е�Name��Ӧ��length
		StringBuffer showmsg = new StringBuffer();
		String zfj = getTBUtil().getSysOptionStringValue("���ݿ��ַ���", "GBK");//��ǰֻ�ж�GBK�������UTF-8��һ������ռ�����ֽڣ���У�����ͨ�������������ݿⱨ�������øò��������/2016-04-26��
		for (int i = 0; i < str_keys.length; i++) {
			Object obj = getCompentObjectValue(str_keys[i]);
			boolean ifsave = getTempletItemVO(str_keys[i]).getIssave();//ֻ�в��뱣��Ĳ�������У��
			if (obj != null && ifsave) {
				int length = 0;
				int dobbefore = 0;//��󵯳�������ʱ���õ�  20140127Ԭ��������
				boolean checkSuc = true;
				boolean checktemp = true;//��Ҫ�ж�����Ϊ���ε������ݿ���Ϊ�����͵�����
				if (obj instanceof StringItemVO) {
					StringItemVO new_name = (StringItemVO) obj;
					if (str_lengthscale[i] == 0) {//�����������û��С�����
						//20140127 Ԭ���������߼� ��������ݿ����ֶδ���(9,2)����mysql�е�DECIMAL�ĳ���Ϊ10,2֮���
						if (str_lengths[i] != -1) {
							try {
								length = new_name.toString().getBytes(zfj).length;
							} catch (Exception e) {
								this.getTBUtil().getStrUnicodeLength(new_name.toString());//��һ����GBK�����ʽ���жϳ��ִ���/�쳣,�����UNICODE�����ʽ��ѯ�ַ������ֽڳ���
							}
							if (length > str_lengths[i]) {
								checkSuc = false;
							}
						}
					} else {//ֻ��Ϊ������
						//���С����ǰ�Ĵ������ݿ��е��򱨴�   ���С�����Ĵ������ݿ��е����ȡ 
						int dotplace = -1;
						try {
							length = new_name.toString().getBytes(zfj).length;//ʵ������������ַ��ĳ���
							dotplace = new_name.toString().lastIndexOf(".");//С�����λ��
							if (dotplace != -1) {//���������С����
								dobbefore = dotplace;//���ʵ�ʵ�С����ǰ��λ��
							}
						} catch (Exception e) {
							this.getTBUtil().getStrUnicodeLength(new_name.toString());//��һ����GBK�����ʽ���жϳ��ִ���/�쳣,�����UNICODE�����ʽ��ѯ�ַ������ֽڳ���
						}
						if (dotplace == -1) {
							if (length > str_lengths[i]) {
								checkSuc = false;
							}
							if (length > str_lengths[i] - str_lengthscale[i]) {//Ԭ���� 20140226 ���
								checkSuc = false;
								checktemp = false;
							}
						} else if (dobbefore > str_lengths[i] - str_lengthscale[i]) {//�����С������С����ǰ��λ��Ӧ��Ϊ9-2=7
							checkSuc = false;
						}
					}
				} else if (obj instanceof RefItemVO) {
					if (str_lengths[i] != -1) {
						RefItemVO new_name = (RefItemVO) obj;
						if (new_name.getId() == null) {
							continue;
						}
						try {
							length = new_name.getId().getBytes(zfj).length;
						} catch (Exception e) {
							this.getTBUtil().getStrUnicodeLength(new_name.getId());//��һ����GBK�����ʽ���жϳ��ִ���/�쳣,�����UNICODE�����ʽ��ѯ�ַ������ֽڳ���
						}
						if (length > str_lengths[i]) {
							checkSuc = false;
						}
					}
				} else if (obj instanceof ComBoxItemVO) {
					if (str_lengths[i] != -1) {
						ComBoxItemVO new_name = (ComBoxItemVO) obj;
						if (new_name.getId() == null) {
							continue;
						}
						try {
							length = new_name.getId().getBytes(zfj).length;
						} catch (Exception e) {
							this.getTBUtil().getStrUnicodeLength(new_name.getId());//��һ����GBK�����ʽ���жϳ��ִ���/�쳣,�����UNICODE�����ʽ��ѯ�ַ������ֽڳ���
						}
						if (length > str_lengths[i]) {
							checkSuc = false;
						}
					}
				}
				if (!checkSuc) {
					if (!checktemp) {
						int ii = str_lengths[i] - str_lengthscale[i];
						showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "�������ݳ���(" + length + "���ֽ�)����ϵͳԼ��������(" + ii + "���ֽ�),���ܱ���!\r\n"); //
					} else if (dobbefore == 0) {//���ݿ��в���С����Ļ����������������û��С�����
						showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "�������ݳ���(" + length + "���ֽ�)����ϵͳԼ��������(" + str_lengths[i] + "���ֽ�),���ܱ���!\r\n"); //
					} else {
						int ii = str_lengths[i] - str_lengthscale[i];
						showmsg.append("��" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "����С����ǰ�����ݳ���(" + dobbefore + "���ֽ�)����ϵͳԼ��������(" + ii + "���ֽ�),���ܱ���!\r\n"); //
					}
				}
			}
		}

		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			return true;
		}
		str_showmsg = str_showmsg.substring(0, str_showmsg.length() - 2); //
		MessageBox.showTextArea(_parent, str_showmsg);
		return false;
	}

	private boolean checkIsUniqueValidate(java.awt.Container _parent) {
		String[] str_keys = this.getTempletVO().getItemKeys(); // ���е�key
		String[] str_names = this.getTempletVO().getItemNames(); // ���е�Name
		boolean[] bo_isUnique = this.getTempletVO().getItemIsUnique(); // �Ƿ�У��Ψһ��
		StringBuffer showmsg = new StringBuffer();
		Vector v_temp = new Vector();

		for (int j = 0; j < str_keys.length; j++) {
			if (bo_isUnique[j]) { // �����ҪУ��Ψһ��!!
				v_temp.add("select '" + str_names[j] + "' from " + this.getTempletVO().getTablename() + " where " + this.getBillVO().getPkName() + "<>" + this.getBillVO().getPkValue() + " and " + str_keys[j] + "='" + this.getRealValueAt(str_keys[j]) + "'");
			}
		}
		if (v_temp.size() == 0) { //û����ҪУ��Ψһ�Ե���
			return true;
		}
		String[] sqls = (String[]) v_temp.toArray(new String[0]);
		try {
			v_temp = UIUtil.getCommonService().getHashVoArrayReturnVectorByDS(null, sqls);

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (v_temp.size() == 0) {
			return true;
		} else {
			HashVO[] hashvo = null;
			for (int i = 0; i < v_temp.size(); i++) {
				hashvo = (HashVO[]) v_temp.get(i);
				if (hashvo != null && hashvo.length > 0) {
					if (hashvo[0] != null) {
						showmsg.append("��" + hashvo[0].getStringValue(0) + "��������Ψһ�ԣ���������������!\r\n");
					}
				}
			}
		}
		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			return true;
		}
		str_showmsg = str_showmsg.substring(0, str_showmsg.length() - 2); //
		MessageBox.showTextArea(_parent, str_showmsg);
		return false;
	}

	/**
	 * ��ʾҳ���ϵ�ʵ������
	 */
	public void showUIRecord() {
		String[] str_tabcolumnnames = new String[] { "ItemKey", "ItemName", "����", "ʵ��ֵ����", "ʵ��ֵ����" }; //
		String[] str_itemkeys = this.templetVO.getItemKeys(); //
		String[] str_itemnames = this.templetVO.getItemNames(); //
		String[] str_itemtypes = this.templetVO.getItemTypes(); //
		String[] str_valuetype = new String[str_itemkeys.length]; //
		String[] str_valuedata = new String[str_itemkeys.length]; //
		for (int i = 0; i < str_itemkeys.length; i++) {
			Object obj = this.getValueAt(str_itemkeys[i]); //
			if (obj != null) {
				str_valuetype[i] = obj.getClass().getName(); //
				if (obj instanceof ComBoxItemVO) {
					ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
					str_valuedata[i] = "id=[" + itemVO.getId() + "],code=[" + itemVO.getCode() + "],name=[" + itemVO.getName() + "]"; //
				} else if (obj instanceof RefItemVO) {
					RefItemVO itemVO = (RefItemVO) obj; //
					str_valuedata[i] = "id=[" + itemVO.getId() + "],code=[" + itemVO.getCode() + "],name=[" + itemVO.getName() + "]"; //
				} else {
					str_valuedata[i] = obj.toString(); //
				}
			}
		}

		String[][] str_tabdata = new String[str_itemkeys.length][5]; //
		for (int i = 0; i < str_itemkeys.length; i++) {
			str_tabdata[i][0] = str_itemkeys[i];
			str_tabdata[i][1] = str_itemnames[i];
			str_tabdata[i][2] = str_itemtypes[i];
			str_tabdata[i][3] = str_valuetype[i];
			str_tabdata[i][4] = str_valuedata[i];
		}

		JTable tmptable = new JTable(str_tabdata, str_tabcolumnnames); //
		tmptable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //

		tmptable.getColumnModel().getColumn(0).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(1).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(2).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(3).setPreferredWidth(255);
		tmptable.getColumnModel().getColumn(4).setPreferredWidth(150);

		BillDialog dialog = new BillDialog(this, "�鿴UIʵ������", 800, 350); //
		dialog.getContentPane().add(new JScrollPane(tmptable)); //

		JLabel label_warn = new JLabel("�����������ĳ���ֶ�����Ϊ��,��ǧ��Ҫע������Ǵ�ĳ����ͼ��ѯ��ɵ�!"); //
		label_warn.setBackground(Color.RED); //
		label_warn.setForeground(Color.WHITE); //
		label_warn.setOpaque(true); //
		dialog.getContentPane().add(label_warn, BorderLayout.NORTH); //

		JLabel label_warn2 = new JLabel("��������о�ĳ���ֶ����ݲ���,��ע��ʹ����һ�����鿴DBʵ�����ݡ����ܽ��бȽ�!"); //
		label_warn2.setBackground(Color.RED); //
		label_warn2.setForeground(Color.WHITE); //
		label_warn2.setOpaque(true); //
		dialog.getContentPane().add(label_warn2, BorderLayout.SOUTH); //
		dialog.setVisible(true); //
	}

	/**
	 * ��ʾ���ݿ�ʵ������
	 */
	public void showDBRecordData() {
		try {
			String str_tablename = this.templetVO.getTablename();
			String str_pkfieldname = this.templetVO.getPkname(); //
			String str_pkvalue = this.getRealValueAt(str_pkfieldname); //
			if (str_tablename == null || str_pkfieldname == null || str_pkvalue == null) {
				MessageBox.show(this, "����/������/����ֵΪ��,tablename=[" + str_tablename + "],pkfieldname=[" + str_pkfieldname + "],pkvalue[" + str_pkvalue + "]");//
				return;
			}
			new RecordShowDialog(this, str_tablename, str_pkfieldname, str_pkvalue);
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ��ҳ�浼����Html
	 */
	public void exportToHTml() throws Exception {
		cn.com.infostrategy.ui.report.BillHtmlDialog dialog = new cn.com.infostrategy.ui.report.BillHtmlDialog(this, this.getTempletVO().getTempletname(), getExportHtml()); //
		dialog.setVisible(true); //
	}

	/**
	 * ����html��������ģ���ӱ��е�cardisexport����
	 */
	public void exportToReportHtml() {
		String htmlStr = "";
		try {
			FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
			htmlStr = service.getCardReportHtml(this.getTempletVO(), this.getBillVO());
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		cn.com.infostrategy.ui.report.BillHtmlDialog dialog = new cn.com.infostrategy.ui.report.BillHtmlDialog(this, this.getTempletVO().getTempletname(), htmlStr); //
		dialog.setSize(900, dialog.getHeight());
		dialog.setVisible(true); //
	}

	private String getHtmlHead() {
		StringBuilder sb_html = new StringBuilder(); //
		//����htmlͷ
		sb_html.append("<html>\r\n"); //
		sb_html.append("<head>\r\n"); //
		sb_html.append("<title>BillCard����</title>\r\n"); //
		sb_html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n"); //
		//sb_html.append("<LINK href=\"./applet/exportHTML.css\" type=text/css rel=stylesheet />\r\n"); //
		sb_html.append("<style type=\"text/css\" src=\"/WEB-INF/classes/test.css\" />\r\n"); //
		sb_html.append("<style type=\"text/css\">\r\n"); //
		//������ʾ
		sb_html.append("html{background:#D2D460;text-align:center;}\r\n");
		sb_html.append("body{width:778px;margin:0 auto;background:#fff;text-align:left;}\r\n");

		sb_html.append("<!--\r\n"); //
		sb_html.append(".Style_TRTD  { border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:0px;border-left-width:0px;font-size:12px; }\r\n"); //
		sb_html.append(".Style_Compent { word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb_html.append(".Style_CompentLabel {background: #EEEEEE;word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb_html.append(".Style_CompentValue { word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb_html.append("#list{border:1px solid;border-collapse: collapse;table-layout: fixed;}\r\n");
		sb_html.append("#list th{background: #EEEEEE;font-weight: normal}\r\n");
		sb_html.append("#list th,td{border:1px solid;font-size:12px;}\r\n");
		//sb_html.append("table {width:100%;}\r\n");
		sb_html.append("fieldset legend {\r\n");
		sb_html.append("    font-size:12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append("-->\r\n"); //
		sb_html.append("</style>\r\n"); //
		sb_html.append("<script type=text/javascript>                   \r\n"); //  
		sb_html.append("<!--                                            \r\n"); //  
		sb_html.append("function setFolding(controler, itemName){       \r\n"); //  
		sb_html.append("    var items = document.getElementsByTagName('tr');\r\n");//
		sb_html.append("    var str = controler.innerText;              \r\n"); //  
		sb_html.append("    str = str.substring(1,str.length);		    \r\n"); //  
		sb_html.append("    for(var i=0;i<items.length;i++) {           \r\n"); //  
		sb_html.append("	    var item = items[i];                    \r\n"); // 
		sb_html.append("        if(item.name != itemName) {             \r\n"); // 
		sb_html.append("            continue;                           \r\n"); // 
		sb_html.append("        }                                       \r\n"); // 
		sb_html.append("	    if(item.style.display==\"none\") {      \r\n"); //  
		sb_html.append("		   item.style.display=\"\";		        \r\n"); //  
		sb_html.append("		   controler.innerText= \"-\" + str;    \r\n"); //  
		sb_html.append("		} else {							    \r\n"); //  
		sb_html.append("			item.style.display=\"none\";	    \r\n"); //  
		sb_html.append("			controler.innerText= \"+\" + str;	\r\n"); //  
		sb_html.append("		}									    \r\n"); //  
		sb_html.append("	}										    \r\n"); //  
		sb_html.append("}											    \r\n"); //  
		sb_html.append("-->											    \r\n"); //  
		sb_html.append("</script>									    \r\n"); //  
		sb_html.append("</head>										    \r\n"); //  
		return sb_html.toString();
	}

	/**
	 * ����Word��������ģ���ӱ��е�cardisexport����
	 */
	public void exportToReportWord() {
		FrameWorkMetaDataServiceIfc service = null;
		FileOutputStream os = null;
		try {
			service = UIUtil.getMetaDataService();
			byte[] bytes = service.getCardReportWord(this.getTempletVO(), this.getBillVO());
			if (bytes != null) {
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File file) {
						String filename = file.getName();
						return file.isDirectory() || filename.endsWith(".doc");
					}

					public String getDescription() {
						return "*.doc";
					}
				});
				File f = new File(new File("c:/exportData.doc").getCanonicalPath());
				chooser.setSelectedFile(f);
				int li_rewult = chooser.showSaveDialog(this);
				if (li_rewult == JFileChooser.APPROVE_OPTION) {
					File curFile = chooser.getSelectedFile(); //
					String fileName = curFile.getName();
					if (!(fileName.endsWith(".doc") || fileName.endsWith(".doc"))) {
						curFile = new File(curFile.getAbsolutePath() + ".doc");
					}
					if (curFile != null) {
						os = new FileOutputStream(curFile, false); //
						os.write(bytes); //
						os.close(); //
						String str_filename = curFile.getAbsolutePath(); //
						if (MessageBox.confirm(this, "�����ļ�[" + curFile.getAbsolutePath() + "]�ɹ�,���Ƿ�����������?")) {
							Desktop.open(curFile); //
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getExportHtml() {
		return getExportHtml(-1); //-1Ĭ�Ͽ��
	}

	/**
	 * ȡ�ý��ÿ�Ƭ�㵼��Html���ַ���!!!
	 * ����ָ��ҳ����
	 * @return
	 */
	public String getExportHtml(int pageWidth) {
		String str_itemkey = ""; //
		StringBuilder sb_html = new StringBuilder();
		sb_html.append(getHtmlHead());
		try {
			JComponent[] allCompents = vflowPanel.getAllCompents(); //�õ�����ؼ�,һ�����һ�����

			sb_html.append("<body>										    \r\n"); //  
			sb_html.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=720>\r\n"); //
			TBUtil tbUtil = new TBUtil(); //

			//����ÿ��cardgroup
			String str_cardGroupId = "";//��������ÿ��cardgroup���ݵ�չ��������
			for (int i = 0; i < allCompents.length; i++) {
				JComponent compent = allCompents[i]; //
				if (!compent.isVisible()) {//����html�Ϳؼ���ʾ������һ��,Ϊʲô��ǰ��ע�͵��ˣ�
					continue;
				}
				if (compent instanceof HFlowLayoutPanel) {
					HFlowLayoutPanel flowPanel = (HFlowLayoutPanel) compent; //
					JComponent[] rowCompents = flowPanel.getAllCompents(); //
					//�����жϣ���ʱ��һ��ֻ��һ���ؼ����Ѿ����ò���ʾ ���ǻ���ʾһ��������
					if (rowCompents != null) {
						boolean ifallnovisble = true;
						for (int j = 0; j < rowCompents.length; j++) { //
							if (rowCompents[j].isVisible()) {
								ifallnovisble = false;
								break;
							}
						}
						if (ifallnovisble) {
							continue;
						}
					} else {
						continue;
					}
					sb_html.append("<tr ");
					sb_html.append(" name=\"" + str_cardGroupId + "\">\r\n");
					if (i != allCompents.length - 1) {
						sb_html.append("<td class=\"Style_TRTD\">\r\n");
					} else {
						sb_html.append("<td style=\"border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:1px;border-left-width:0px;font-size:12px;\">\r\n");
					}

					sb_html.append("<table style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n");
					sb_html.append("<tr>\r\n");
					for (int j = 0; j < rowCompents.length; j++) { //
						AbstractWLTCompentPanel realCompent = (AbstractWLTCompentPanel) rowCompents[j]; //
						if (!realCompent.isVisible()) {
							continue;
						}
						str_itemkey = realCompent.getItemKey(); //
						JLabel label = realCompent.getLabel(); //
						if (label == null) {
							//������ȡ����ֵ�������ǰ�ť���ͣ�Ӧ����δ���? Ŀǰ�Ⱥ���
							continue;
						}
						String str_textColor = "000000"; //
						if (label != null && label.getForeground() != null) {
							str_textColor = tbUtil.convertColor(label.getForeground()); //
						}
						Object objValue = realCompent.getObject();
						int li_label_width = (int) label.getPreferredSize().getWidth(); //
						int li_comp_width = (int) (realCompent.getPreferredSize().getWidth() - li_label_width); //�ؼ������������ȼ�ȥLabel�Ŀ��!!
						String str_label_text = label.getText(); //
						str_label_text = tbUtil.replaceAll(str_label_text, "<html>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "</html>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "<body>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "</body>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "\n", "<br>"); //�����з��滻��<br>
						int li_label_height = (int) label.getPreferredSize().getHeight(); //
						if ("% ".equals(str_label_text)) {
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + (li_label_width - 10) + "\" height=\"" + li_label_height + "\" align=\"left\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //�ؼ���Label˵��
							continue;

						}
						if (!str_textColor.equals("000000") && str_label_text != null && !str_label_text.trim().equals("")) {
							str_label_text = "<font color=\"#" + str_textColor + "\">" + str_label_text + "</font>"; //
						}

						String str_objectValueText = (objValue == null ? "" : ("" + objValue)); //
						if (realCompent instanceof CardCPanel_TextField) {
							if (((CardCPanel_TextField) realCompent).getType().equals(WLTConstants.COMP_PASSWORDFIELD)) {
								if (str_objectValueText != null && !"".equals(str_objectValueText.trim())) {
									char[] newchar = new char[str_objectValueText.length()];
									for (int ii = 0; ii < str_objectValueText.length(); ii++) {
										newchar[ii] = ('*');
									}
									str_objectValueText = new String(newchar);
								}
							}
						}

						if (realCompent instanceof CardCPanel_ChildTable) {
							//���������ӱ��html  WLTConstants.COMP_LINKCHILD
							BillListPanel list = ((CardCPanel_ChildTable) realCompent).getBillListPanel();
							if (list.getRowCount() == 0) {
								str_objectValueText = "";
							} else {
								str_objectValueText = "\r\n" + list.getHtmlTableText() + "\r\n";
							}
						}

						String str_itemtype = "";
						if (realCompent instanceof CardCPanel_Ref) {
							str_itemtype = ((CardCPanel_Ref) realCompent).getType();
						}
						if (str_itemtype.equals(WLTConstants.COMP_OFFICE)) {
							str_objectValueText = getComponentToHrefHtml(this.getBillVO(), str_objectValueText, str_itemkey, str_itemtype);
						}
						if (realCompent instanceof CardCPanel_FileDeal) {
							str_objectValueText = getComponentToHrefHtml(this.getBillVO(), str_objectValueText, str_itemkey, WLTConstants.COMP_FILECHOOSE);
						}
						if (realCompent instanceof CardCPanel_CheckBox) {
							if ("N".equals(str_objectValueText)) {
								str_objectValueText = "��";
							} else if ("Y".equals(str_objectValueText)) {
								str_objectValueText = "��";
							}
						}

						if (realCompent instanceof CardCPanel_TextArea) {
							sb_html.append("<td class=\"Style_CompentLabel\" width=\"" + li_label_width + "\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //�ؼ���Label˵��
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + li_comp_width + "\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_objectValueText.replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>") + "</td>\r\n"); //�ؼ������ݿ��е�ֵ
						} else if (realCompent instanceof CardCPanel_StylePadArea) { //���ı���html����ȥ��׺ �����/2012-10-30��
							int li_pos_1 = str_objectValueText.lastIndexOf("#@$");
							int li_pos_2 = str_objectValueText.lastIndexOf("$@#");
							if (str_objectValueText.endsWith("$@#") && li_pos_1 > 0 && li_pos_2 > 0) { //������ԡ�$@#����β��! #@$12586$@#
								str_objectValueText = str_objectValueText.substring(0, li_pos_1);
							}
							sb_html.append("<td class=\"Style_CompentLabel\" width=\"" + li_label_width + "\" height=\"" + li_label_height + "\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //�ؼ���Label˵��
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + li_comp_width + "\" " + "height=\"" + li_label_height + "\" align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_objectValueText + "</td>\r\n"); //�ؼ������ݿ��е�ֵ
						} else {
							sb_html.append("<td class=\"Style_CompentLabel\" width=\"" + li_label_width + "\" height=\"" + li_label_height + "\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //�ؼ���Label˵��
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + li_comp_width + "\" " + "height=\"" + li_label_height + "\" align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_objectValueText + "</td>\r\n"); //�ؼ������ݿ��е�ֵ
						}
					}

					sb_html.append("</tr>\r\n");
					sb_html.append("</table>\r\n");
					sb_html.append("</td>\r\n");
					sb_html.append("</tr>\r\n\r\n");

				} else if (compent instanceof CardGroupTitlePanel) {
					str_cardGroupId = "table" + i;
					sb_html.append("<tr>\r\n");
					sb_html.append("<td class=\"Style_TRTD\">\r\n");
					sb_html.append("<table width=\"100%\" style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"2\">\r\n");
					sb_html.append("<tr>\r\n");
					sb_html.append("<td class=\"Style_Compent\" width=\"100%\" align=\"left\" bgcolor=\"#EEDDFF\">");
					sb_html.append("<div onclick=\"setFolding(this, '" + str_cardGroupId + "');\">");
					if (((CardGroupTitlePanel) compent).getExpandState()) {
						sb_html.append("-");
					} else {
						sb_html.append("+");
					}
					sb_html.append(((CardGroupTitlePanel) compent).getTitlename());
					sb_html.append("</div></td>\r\n");
					sb_html.append("</tr>\r\n");
					sb_html.append("</table>\r\n");
					sb_html.append("</td>\r\n");
					sb_html.append("</tr>\r\n");
				} else {
					System.out.println("��֪ɶ:" + compent); //
				}
			}
			sb_html.append("</table>\r\n");
			//			sb_html.append("</fieldset>\r\n"); //
			sb_html.append("</body>\r\n");
			sb_html.append("</html>\r\n");
			return sb_html.toString(); //
		} catch (Exception e) {
			logger.error("��Ϊ[" + str_itemkey + "]����Html�����쳣", e); //
			return "<html><body><font size=2 color=\"red\">��Ϊ[" + str_itemkey + "]����Html�����쳣,�뵽����̨�鿴��ϸ��Ϣ</font></body></html>"; //
		}
	}

	/**
	 * ȡ�ü������billListPanel��Frame,�����Ǹ��ַ��ģ��!!
	 * 
	 * @return
	 */
	public AbstractWorkPanel getLoadedWorkPanel() {
		return loadedWorkPanel;
	}

	/**
	 * ��ӡҳ��..
	 */
	private void onPrintThis() {
		try {
			String str_fileName = System.getProperty("ClientCodeCache") + "\\BillCardPanel_" + System.currentTimeMillis() + ".jpg"; //
			getTBUtil().saveCompentAsJPGFile(mainPanel, str_fileName); //
			Desktop.open(new File(str_fileName)); //���ò���ϵͳ�Ļ�ͼ�����ͼƬ,Ȼ�����û�ͼ���ߵĹ��ܽ��д�ӡ..
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ���ü������billListPanel��Frame,�����Ǹ��ַ��ģ��!!
	 * 
	 * @param loadedFrame
	 */
	public void setLoadedWorkPanel(AbstractWorkPanel _loadedWorkPanel) {
		this.loadedWorkPanel = _loadedWorkPanel;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public void setTempletVO(Pub_Templet_1VO templetVO) {
		this.templetVO = templetVO;
	}

	public boolean containsItemKey(String _itemKey) {
		return this.getTempletVO().containsItemKey(_itemKey);
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	private String getBorderName() {
		if (ClientEnvironment.getInstance().isEngligh()) {
			if ("DB".equals(this.getTempletVO().getBuildFromType())) { //����������:XML2,DB,CLASS
				return templetVO.getTempletname_e() + "-";
			} else if ("CLASS".equals(this.getTempletVO().getBuildFromType())) {
				return templetVO.getTempletname_e() + "=";
			} else {
				return templetVO.getTempletname_e();
			}
		} else {
			if ("DB".equals(this.getTempletVO().getBuildFromType())) { //����������:XML2,DB,CLASS
				return templetVO.getTempletname() + "-";
			} else if ("CLASS".equals(this.getTempletVO().getBuildFromType())) {
				return templetVO.getTempletname() + "=";
			} else {
				return templetVO.getTempletname();
			}
		}
	}

	public BillFormatPanel getLoaderBillFormatPanel() {
		return loaderBillFormatPanel;
	}

	public void setLoaderBillFormatPanel(BillFormatPanel loaderBillFormatPanel) {
		this.loaderBillFormatPanel = loaderBillFormatPanel;
	}

	public JComponent[] getHflowPanels() {
		return hflowPanels;
	}

	public boolean isCanRefreshParent() {
		return canRefreshParent;
	}

	public void setCanRefreshParent(boolean canRefreshParent) {
		this.canRefreshParent = canRefreshParent;
	}
	/**
	 * zzl[2018-12-4]
	 * ���������ӱ��BillListPanel
	 * @return
	 */
	public BillListPanel getdealBillListPanel(){
		BillListPanel list=null;
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i);
			if (object instanceof CardCPanel_ChildTable) {
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object;
				list=compent.getBillListPanel();
			}
	    }
		return list;
	}

}
