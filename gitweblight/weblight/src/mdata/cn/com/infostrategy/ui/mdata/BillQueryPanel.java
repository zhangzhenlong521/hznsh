package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.XMLIOUtil;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPopupButton;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.querycomp.BillQueryQuickSQLCreaterIFC;
import cn.com.infostrategy.ui.mdata.querycomp.CommQueryDialog;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_Button;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_TextField;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;

/**
 * ��ѯ���,������Ԫԭģ���������һ�����ٲ�ѯ���,ͨ�ò�ѯ���,�߼���ѯ���!!
 * ��ѯ�����ƽ̨�зǳ��ؼ���һ�����ܣ���Ϊ��ѯ��ϵͳ�е�������Ҫ��һ������!!
 * ��ѯ�����Ҫ������������ķ�װ��Ӵ���,��Ϊ��ѯһֱ��Ӱ�쿪�����ܵ�һ���缲!! ������!! һ��Ҫ�������ֲ�ѯ�����������,����չ!!!
 * ͨ�����÷���getQuerySQLConditionByItemKeyMapping()���Կ��ٵõ���ѯ������ɵĲ�ѯ����!! Ȼ��ǰ�����"select * from ���� where 1=1" + ���ɵ�����!! 
 * @author xch
 * 
 */
public class BillQueryPanel extends BillPanel {

	private static final long serialVersionUID = 1L;
	private String str_templetcode = null; // ģ�����
	private Pub_Templet_1VO templetVO = null; // ģ���������
	private Pub_Templet_1_ItemVO[] templetItemVOs = null; // ģ���ӱ����

	private JMenuItem menuItem_commquery, menuItem_complexquery, menuItem_reset, menuItem_exportConditiontoXML; //

	private ArrayList v_compents = new ArrayList();
	private ArrayList v_compentsaves = new ArrayList();
	private HashMap v_compents_map = new HashMap();
	private Vector v_listeners = new Vector(); // ����ע����¼�������!!!
	private BillListPanel billlist = null; //

	Logger logger = WLTLogger.getLogger(BillQueryPanel.class);
	private boolean bo_isallowtriggereditevent = true; // �Ƿ��������༭�¼�

	private BillQueryQuickSQLCreaterIFC quickSQLCreater = null; // //

	private VFlowLayoutPanel commqueryPanel = null;
	private CommQueryDialog commQueryDialog = null; //

	private WLTPopupButton popupButton = null; //
	private WLTButton queryButton = null; //�ʴ���ѯȥ��ͷ �����/2012-09-10��	
	private WLTButton btn_reset, btn_complexquery = null; // �������
	private boolean complexquery = false;
	private String str_currquicksql = null; // F

	private ActionListener allActionListener = null;
	private ActionListener custActionListener = null; //
	private ActionListener custCommonActionListener = null; //

	private boolean isContainQueryButton = true; //
	private boolean isContainCommonQueryButton = false; // ��Щ�ط�����Ҫͨ�ò�ѯ trueΪ����ʾ
	private boolean isQuickQueryQuiet = false; // ������ٲ�ѯʱ�Ƿ��ǰ���ģʽ����û�е���һ���ȴ���!��Ϊ��ʱ�ͻ���Ҫ�Լ��Զ��巽��
	private TBUtil tbUtil = null; //������!
	private JPanel queryPanel = null;
	private WLTPanel btnpanel = null;

	private String dataPolicyCondition = null;//Ԭ����20120913��ӣ�������������Ȩ�����⣬���ڱ����õĲ�����billlist�����Բ�����$SQL_1��ͨ�Ĳ�ѯ��Ȩ�޻��� ����Ҫ���⴦��

	/**
	 * ����ģ����봴����ѯ���
	 * 
	 * @param _templetcode
	 */
	public BillQueryPanel(String _templetcode) {
		this(_templetcode, true);
	}

	/**
	 * ���췽��
	 * 
	 * @param _templetcode
	 * @param _containQueryButton,�Ƿ������ѯ��ť,��Ϊ��ʱ���ܲ���Ҫ��ʾ��ѯ��ť
	 */
	public BillQueryPanel(String _templetcode, boolean _containQueryButton) {
		this.isContainQueryButton = _containQueryButton; //
		if (_templetcode.indexOf(".") > 0) { // ����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	/**
	 * @param _templetcode
	 *            ģ�����
	 * @param _containQueryButton
	 *            �Ƿ���Ҫ��ѯ��ť
	 * @param __containCommonQueryButton
	 *            �Ƿ���Ҫͨ�ò�ѯ��ť
	 */
	public BillQueryPanel(String _templetcode, boolean _containQueryButton, boolean _containCommonQueryButton) {
		this.isContainQueryButton = _containQueryButton; //
		this.isContainCommonQueryButton = _containCommonQueryButton;
		if (_templetcode.indexOf(".") > 0) { // ����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	public BillQueryPanel(Pub_Templet_1VO _TempletVO) {
		this(_TempletVO, true);
	}

	public BillQueryPanel(Pub_Templet_1VO _TempletVO, boolean _containQueryButton) {
		this.str_templetcode = _TempletVO.getTempletcode();
		this.isContainQueryButton = _containQueryButton; //
		templetVO = _TempletVO;
		templetItemVOs = templetVO.getItemVos(); // ����
		this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
		initialize(); //
	}

	public BillQueryPanel(Pub_Templet_1VO _TempletVO, boolean _containQueryButton, String dataPolicyCondition) { //Ԭ����20120913��ӱ���Ĺ��췽�������һ������Ϊ���������Ȩ��
		this.dataPolicyCondition = dataPolicyCondition;
		this.str_templetcode = _TempletVO.getTempletcode();
		this.isContainQueryButton = _containQueryButton; //
		templetVO = _TempletVO;
		templetItemVOs = templetVO.getItemVos(); // ����
		this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
		initialize(); //
	}

	public BillQueryPanel(BillListPanel _billList) {
		this.billlist = _billList; //
		templetVO = billlist.getTempletVO();
		templetItemVOs = templetVO.getItemVos(); // ����
		this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
		initialize(); //
	}

	private void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode(); //
			this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
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
			this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	/**
	 * ���¼���ҳ��   20130314  Ԭ�������
	 */
	public void reload() {
		reload(this.str_templetcode); //
	}

	/**
	 * ���¼���ҳ��
	 */
	public void reload(String _templetcode) {
		v_compents.clear(); //
		v_compents_map.clear(); //
		v_listeners.clear();
		queryPanel = null;
		btnpanel = null;
		init_1(_templetcode); //
	}

	/**
	 * ��ʼ��ҳ��
	 * 
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false);
		// this.setBackground(LookAndFeel.defaultShadeColor1);
		// this.setUI(new
		// WLTPanelUI(BackGroundDrawingUtil.VERTICAL_BOTTOM_TO_TOP));
		VFlowLayoutPanel panel_query = getQueryPanel(1); //
		queryPanel = new JPanel(new BorderLayout());
		queryPanel.setOpaque(false);
		queryPanel.add(panel_query);
		this.add(queryPanel, BorderLayout.WEST);

		allActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btn_reset) {
					resetAllQuickQueryCompent();
				} else if (e.getSource() == menuItem_commquery) {
					onCommonQuery();
				} else if (e.getSource() == menuItem_complexquery) {
					//complexQuery(); //
				} else if (e.getSource() == menuItem_reset) { //�������!!
					resetAllQuickQueryCompent();
				} else if (e.getSource() == menuItem_exportConditiontoXML) {
					exportConditionToXML(); // ������������XML
				} else if (e.getSource() == btn_complexquery) {
					showOrHidenComplexQuery();
				}
			}
		};
		// �����ҷ���ť
		if (isContainQueryButton) {
			btnpanel = new WLTPanel();
			// btnpanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
			btnpanel.setLayout(new BorderLayout());

			JPopupMenu popup = new JPopupMenu("PopupMenu");
			menuItem_commquery = new JMenuItem(UIUtil.getLanguage("ͨ�ò�ѯ")); //
			menuItem_complexquery = new JMenuItem(UIUtil.getLanguage("�߼���ѯ")); //���Ǽ�����һ���и�����!!!
			menuItem_reset = new JMenuItem(" �� �� "); //һ��ʼ�Ƿ��������е�,������,��ҵ�ȿͻ���˵Ҫ�ų���,�ų�������������Ф������˵Ҫȥ��!!ֻ�ܸ����������!!!
			menuItem_exportConditiontoXML = new JMenuItem(UIUtil.getLanguage("��������")); //

			menuItem_commquery.setBackground(LookAndFeel.systembgcolor);
			menuItem_complexquery.setBackground(LookAndFeel.systembgcolor);
			menuItem_reset.setBackground(LookAndFeel.systembgcolor); //
			menuItem_exportConditiontoXML.setBackground(LookAndFeel.systembgcolor);

			Dimension dim_size = new Dimension(78, 19); //
			menuItem_commquery.setPreferredSize(dim_size); //ͨ�ò�ѯ
			menuItem_complexquery.setPreferredSize(dim_size); //���Ӳ�ѯ
			menuItem_reset.setPreferredSize(dim_size); //�������
			menuItem_exportConditiontoXML.setPreferredSize(dim_size); //��������
			menuItem_commquery.addActionListener(allActionListener);
			menuItem_reset.addActionListener(allActionListener);
			menuItem_complexquery.addActionListener(allActionListener);
			menuItem_exportConditiontoXML.addActionListener(allActionListener);
			boolean isHaveOneCommQueryItem = false;
			if (isContainCommonQueryButton) {
				for (int i = 0; i < templetItemVOs.length; i++) { //���Ƿ����ͨ�ò�ѯ!!����ҵ�������еĿͻ���ӳ,û������һ��ͨ�ò�ѯ���������һ���հ�,����!���Լ���һ������,���һ��û��,������ť��û��!��xch/2012-04-25��
					if (templetItemVOs[i].getIsCommQueryShowable()) { //
						isHaveOneCommQueryItem = true; //
						break; //ֻҪ��һ����ʾ,�͵���!
					}
				}

				if (isHaveOneCommQueryItem) { //ֻҪ��һ���ͼ���,��ֻ��Ϊ0ʱ�Ų�����!!
					boolean isavecommquery = getTBUtil().getSysOptionBooleanValue("��ѯ���Ƿ���ʾͨ�ò�ѯ", true);
					if (isavecommquery) {
						popup.add(menuItem_commquery); //
					}
				}
			}
			boolean isRestBtnInPop = getTBUtil().getSysOptionBooleanValue("��ѯ�����������ť�Ƿ���������˵���", false); //
			if (isRestBtnInPop) { //����Ƿ��������˵���,�����
				popup.add(menuItem_reset); //
			}

			if (ClientEnvironment.isAdmin()) { // ����ǹ������
				popup.add(menuItem_exportConditiontoXML); //
			}

			popupButton = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, UIUtil.getLanguage(" �� ѯ"), null, popup); //
			if (ClientEnvironment.isAdmin()) {
				popupButton.getButton().setToolTipText("��סShift���Ҽ����Բ鿴����!"); //
			} else {
				popupButton.getButton().setToolTipText("�����ѯ"); //
			}
			if (getTBUtil().getSysOptionBooleanValue("��ѯ���ѯ��ť�Ƿ�ͻ����ʾ", true)) {
				popupButton.getButton().setFont(new Font("����", Font.BOLD, 13)); //
				popupButton.getButton().setForeground(Color.BLUE); //
				//				popupButton.getButton().setIcon(UIUtil.getImage("office_089.gif")); //
			} else { //��������Ŀ����Ϊ��ν��UI��׼,������ָ��������,��û��ͼ��,��Ҳ�Ǻ�ɫ��!!��ʵ��û���ǵ�Ĭ�Ϻÿ��ģ���û�취!!!
				popupButton.getButton().setFont(LookAndFeel.font); //
			}
			popupButton.getButton().setBackground(LookAndFeel.defaultShadeColor1); //systembgcolor
			popupButton.getPopButton().setBackground(LookAndFeel.systembgcolor); //
			//popupButton.getButton().addActionListener(allActionListener);

			popupButton.getButton().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						onQuickQueryClicked(e.isControlDown());
					} else if (e.getButton() == MouseEvent.BUTTON3 && (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN || e.isShiftDown())) { //����Ҽ�,����ǹ���Ա,����Shift�Ҽ�!!����Ԥ����ʾSQL!!! 
						showQuickSQL(); //
					}
				}
			}); //

			popupButton.setPreferredSize(new Dimension(80, 25)); // ���С
			if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
				this.setToolTipText("����Ҽ����Բ鿴��ϸ��ѯSQL!");
			}

			queryButton = new WLTButton("  ��  ѯ  "); //�ʴ���ѯȥ��ͷ �����/2012-09-10��	
			if (ClientEnvironment.isAdmin()) {
				queryButton.setToolTipText("��סShift���Ҽ����Բ鿴����!");
			} else {
				queryButton.setToolTipText("�����ѯ");
			}
			if (getTBUtil().getSysOptionBooleanValue("��ѯ���ѯ��ť�Ƿ�ͻ����ʾ", true)) {
				queryButton.setFont(new Font("����", Font.BOLD, 13));
				queryButton.setForeground(Color.BLUE);
			} else { //��������Ŀ����Ϊ��ν��UI��׼,������ָ��������,��û��ͼ��,��Ҳ�Ǻ�ɫ��!!��ʵ��û���ǵ�Ĭ�Ϻÿ��ģ���û�취!!!
				queryButton.setFont(LookAndFeel.font);
			}
			queryButton.setBackground(LookAndFeel.defaultShadeColor1);
			queryButton.setBackground(LookAndFeel.systembgcolor);

			queryButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					queryButton.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					BillQueryPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					if (e.getButton() == MouseEvent.BUTTON1) {
						onQuickQueryClicked(e.isControlDown());
					} else if (e.getButton() == MouseEvent.BUTTON3 && (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN || e.isShiftDown())) { //����Ҽ�,����ǹ���Ա,����Shift�Ҽ�!!����Ԥ����ʾSQL!!! 
						showQuickSQL();
					}
					queryButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					BillQueryPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				}
			});

			queryButton.setPreferredSize(new Dimension(80, 25));
			if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
				this.setToolTipText("����Ҽ����Բ鿴��ϸ��ѯSQL!");
			}

			btn_reset = new WLTButton("  ��  ��   ");
			btn_reset.addActionListener(allActionListener);
			btn_reset.setBackground(LookAndFeel.defaultShadeColor1); //
			btn_complexquery = new WLTButton("��ʾ�߼���ѯ");
			btn_complexquery.addActionListener(allActionListener);
			btn_complexquery.setBackground(LookAndFeel.defaultShadeColor1);
			btn_complexquery.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
			JPanel tempPanel_reset = new JPanel(new BorderLayout(0, 0)); //
			tempPanel_reset.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); //
			tempPanel_reset.setOpaque(false);
			List btnList = new ArrayList();
			boolean isavecomplexquery = getTBUtil().getSysOptionBooleanValue("��ѯ���Ƿ���ʾ�߼���ѯ", false);
			if (!isRestBtnInPop) { //��������������˵��вż���!!!
				if (!getTBUtil().getSysOptionBooleanValue("��ѯ�����������ť�Ƿ�������", false)) { //��������Ŀ��Ф���ε�UI��׼��Ȼ�����ð�ť������,�ܱ�̬!!!
				//					btn_reset.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY)); //
					if (ClientEnvironment.isAdmin() || !getTBUtil().getSysOptionBooleanValue("��ѯ�Ƿ�ȥ��ͷ", false)) {
						btnList.add(popupButton);
					} else {
						btnList.add(queryButton);
					}
					btnList.add(btn_reset);
				} else {
					//					btn_reset.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.GRAY)); //
					btnList.add(btn_reset);
					if (ClientEnvironment.isAdmin() || !getTBUtil().getSysOptionBooleanValue("��ѯ�Ƿ�ȥ��ͷ", false)) {
						btnList.add(popupButton);
					} else {
						btnList.add(queryButton);
					}
				}
				if (isavecomplexquery && isHaveOneCommQueryItem) {
					btnList.add(btn_complexquery);
				}
			} else {
				if (ClientEnvironment.isAdmin() || !getTBUtil().getSysOptionBooleanValue("��ѯ�Ƿ�ȥ��ͷ", false)) {
					btnList.add(popupButton);
				} else {
					btnList.add(queryButton);
				}
				if (isavecomplexquery && isHaveOneCommQueryItem) {
					btnList.add(btn_complexquery);
				}
			}
			tempPanel_reset.add(new VFlowLayoutPanel(btnList, 1), BorderLayout.NORTH); //��������!!
			btnpanel.add(tempPanel_reset, BorderLayout.NORTH); // ��ť���...
			this.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

			JPanel panel_temp = new JPanel(new BorderLayout()); //
			panel_temp.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0)); // �ձ߿�
			panel_temp.setOpaque(false); // ͸����
			boolean isSide = new TBUtil().getSysOptionBooleanValue("��ѯ���ѯ��ť�Ƿ񿿱���ʾ", false); //����/����/��ҵ��˵�����Ų�ѯ��,������˵Ҫ����,û�취,ֻ�ܸ㿪�ز������ɻ��!!!
			if (isSide) {
				panel_temp.add(btnpanel, BorderLayout.EAST); //
			} else {
				panel_temp.add(btnpanel, BorderLayout.WEST); //
			}
			if (queryPanel.getPreferredSize().getHeight() <= btnpanel.getPreferredSize().getHeight()) {//����ѯ���Ȱ�ť��尫��ʱ��2��֮��ļ����Щ���⣬���Ѱ�ť������Ҽ��ˣ�û�ҵ��ð취
				queryPanel.setPreferredSize(new Dimension(queryPanel.getPreferredSize().width, btnpanel.getPreferredSize().height));
			}
			this.add(panel_temp, BorderLayout.CENTER); //
		}
	}

	/**
	 * ������п��ٲ�ѯ����еĿؼ�!
	 */
	public void resetAllQuickQueryCompent() {
		for (int i = 0; i < this.v_compents.size(); i++) {
			AbstractWLTCompentPanel quickQueryCompent = (AbstractWLTCompentPanel) v_compents.get(i); //
			quickQueryCompent.reset(); //
		}
	}

	public void resetAllHideQueryCompent() {
		for (int i = 0; i < this.v_compents.size(); i++) {
			AbstractWLTCompentPanel quickQueryCompent = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (!quickQueryCompent.isShowing()) {
				quickQueryCompent.reset(); //
			}
		}
	}

	private VFlowLayoutPanel getQueryPanel(int _type) {
		ArrayList al_allRows = new ArrayList(); // ������
		ArrayList al_oneRowCompents = null; // һ��
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (_type == 1) {
				if (!templetItemVOs[i].getIsQuickQueryShowable()) {
					continue; // ������ٲ�ѯ����ʾ,������.
				}
			} else if (_type == 2) {
				if (!templetItemVOs[i].getIsCommQueryShowable()) {
					continue; // ���ͨ�ò�ѯ����ʾ,������.
				}
			}

			String str_type = templetItemVOs[i].getItemtype(); // �༭ʱ�Ŀؼ�����
			if (templetItemVOs[i].getQueryItemType() != null && !templetItemVOs[i].getQueryItemType().trim().equals("")) { // ��������˲�ѯ������
				str_type = templetItemVOs[i].getQueryItemType(); // ��������˲�ѯ��Ŀؼ�����
			}
			AbstractWLTCompentPanel compentPanel = null;
			if (v_compents_map.containsKey(templetItemVOs[i].getItemkey())) {//
				compentPanel = (AbstractWLTCompentPanel) v_compents_map.get(templetItemVOs[i].getItemkey());
			} else {
				if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_TEXTAREA) || str_type.equals(WLTConstants.COMP_BIGAREA) || str_type.equals(WLTConstants.COMP_FILECHOOSE) || str_type.equals(WLTConstants.COMP_STYLEAREA)) { // �ı���,�����ı���,����
					compentPanel = getTextFieldCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //������,
					compentPanel = getComboBoxCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // ���β���
						str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // ���Ͳ���
						str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // ��ѡ����
						str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // �Զ������
						str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || // �б�ģ�����
						str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || // ����ģ�����
						str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // ע���������
						str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // ע�����
						str_type.equals(WLTConstants.COMP_DATE) || // �����ؼ�
						str_type.equals(WLTConstants.COMP_DATETIME) || // ʱ��ؼ�
						str_type.equals(WLTConstants.COMP_NUMBERFIELD) // ���ֿ�
				) {
					compentPanel = getUIRefCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { // ��ѡ��
					compentPanel = getCheckBoxCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { // ��ť
					compentPanel = new QueryCPanel_Button(templetItemVOs[i], this);
				} else {
					continue;
				}
				v_compents.add(compentPanel); // �ȼ��뻺��
				if (templetItemVOs[i].getIssave()) {
					v_compentsaves.add(compentPanel);
				}
				v_compents_map.put(templetItemVOs[i].getItemkey(), compentPanel);
			}

			if (_type == 1) {
				compentPanel.setItemEditable(templetItemVOs[i].getIsQuickQueryEditable()); // �Ƿ�ɱ༭
			} else if (_type == 2) {
				compentPanel.setItemEditable(templetItemVOs[i].getIsCommQueryEditable()); // �Ƿ�ɱ༭
			}
			compentPanel.setOpaque(false);
			boolean bo_iswrap = false; //
			if (_type == 1) {
				bo_iswrap = templetItemVOs[i].getIsQuickQueryWrap(); // �Ƿ���
			} else if (_type == 2) {
				bo_iswrap = templetItemVOs[i].getIsCommQueryWrap(); //
			}

			if (bo_iswrap) { // ������ٲ�ѯ����
				al_oneRowCompents = new ArrayList(); //
				al_allRows.add(al_oneRowCompents); //
				al_oneRowCompents.add(v_compents_map.get(templetItemVOs[i].getItemkey())); // ����ؼ�
			} else {
				if (al_oneRowCompents == null) { //
					al_oneRowCompents = new ArrayList(); //
					al_allRows.add(al_oneRowCompents); //
				}
				al_oneRowCompents.add(v_compents_map.get(templetItemVOs[i].getItemkey())); // ֱ����ԭ�������������µĿؼ�!!!
			}
		} // end forѭ��

		// ִ��Ĭ��ֵ��ʽ
		execDefaultFormula(); //

		// ����ҳ��..
		JComponent[] hflowPanels = new JComponent[al_allRows.size()]; //
		for (int i = 0; i < hflowPanels.length; i++) {
			ArrayList al_row = (ArrayList) al_allRows.get(i); //
			hflowPanels[i] = new HFlowLayoutPanel(al_row); //
		}

		VFlowLayoutPanel vflowPanel = new VFlowLayoutPanel(hflowPanels); // //��ֱ����..
		return vflowPanel; //
	}

	private String findItemType(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return templetItemVOs[i].getItemtype(); //
			}
		}
		return null;
	}

	private String findItemQueryDefaultFormula(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return templetItemVOs[i].getQuerydefaultformula(); //
			}
		}
		return null;
	}

	private void execDefaultFormula() {
		JepFormulaParseAtUI jepParse = new JepFormulaParseAtUI(this); //
		for (int i = 0; i < v_compents.size(); i++) { //
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i);
			String str_key = compent.getItemKey();
			String str_type = findItemType(str_key); //
			String formula = findItemQueryDefaultFormula(str_key); //
			if (formula == null || formula.trim().equals("")) {
				continue;
			}
			String[] str_formulas = getTBUtil().split1(formula, ";"); //
			for (int j = 0; j < str_formulas.length; j++) {
				Object obj = jepParse.execFormula(str_formulas[j]); //
				if (j == str_formulas.length - 1) { // ���������һ��,����Ϊ�Ƿ���ֵ
					try {
						if (obj instanceof String) { //
							String str_objstr = (String) obj; //
							if (str_objstr.equals("ok")) { // �����ok������
								continue; //
							}

							if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || // �ı���
									str_type.equals(WLTConstants.COMP_TEXTAREA) || // �����ı���
									str_type.equals(WLTConstants.COMP_BIGAREA) || // ���ı���
									str_type.equals(WLTConstants.COMP_FILECHOOSE) // �ļ�ѡ���
							) {
								compent.setObject(new StringItemVO(str_objstr)); //
							} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // �����������
								setCompentObjectValue(str_key, new ComBoxItemVO(str_objstr, str_objstr, str_objstr)); //
							} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // ���Ͳ���1
									str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // ���Ͳ���1
									str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // ��ѡ����1
									str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // �Զ������
									str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || // �б�ģ�����
									str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || // ����ģ�����
									str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // ע���������
									str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // ע�����
									str_type.equals(WLTConstants.COMP_DATE) || // ����
									str_type.equals(WLTConstants.COMP_DATETIME) || // ʱ��
									str_type.equals(WLTConstants.COMP_COLOR) || // ��ɫѡ���
									str_type.equals(WLTConstants.COMP_CALCULATE) || // ������
									str_type.equals(WLTConstants.COMP_PICTURE) || // ͼƬѡ���
									str_type.equals(WLTConstants.COMP_NUMBERFIELD) // ���ֿ�
							) { // ����ǲ���...
								compent.setObject(new RefItemVO(str_objstr, str_objstr, str_objstr)); //
							} else {
								compent.setObject(new StringItemVO(str_objstr)); //
							}
						} else if (obj instanceof java.lang.Double) {
							compent.setObject(new StringItemVO("" + obj)); //
						} else {
							compent.setObject(obj); //
						}
					} catch (Exception ex) {
						this.logger.error("����[" + str_key + "]��Ĭ��ֵʧ��!!!", ex); //
					}

				}
			}
		}
	}

	/**
	 * �ı���
	 * 
	 * @return
	 */
	private AbstractWLTCompentPanel getTextFieldCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_TextField(this, _itemvo); //
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //

		((QueryCPanel_TextField) compentPanel).getTextField().addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) { // ���ʧȥʱ�����¼�!!
				onChanged(_itemvo.getItemkey());
			}
		});

		// �����¼�
		((QueryCPanel_TextField) compentPanel).getTextField().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChangedAndFocusNext(_itemvo.getItemkey()); // ������ûس��Ļ�,��Ҫ�����
			}
		});
		return compentPanel; //
	}

	/**
	 * ������ؼ�,��ǰ����һ����׼��������,�����Ľ��ɿ���֧�ֶ�ѡ��!��Ϊ��¼��ʱ�ǵ�ѡ��,����ѯʱӦ���Ƕ�ѡ��!
	 * @param _itemvo
	 * @return
	 */
	private AbstractWLTCompentPanel getComboBoxCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_ComboBox(_itemvo); // ������
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		((QueryCPanel_ComboBox) compentPanel).addActionListener(new ActionListener() { //��Ϊ�������ڲ�ѯ�б���˶�ѡ,�����Ǳ�׼��JcomboBox�ˣ����Բ���ItemListener����,�����Լ���װ�˸�ActionListener
					public void actionPerformed(ActionEvent e) {
						onChanged(_itemvo.getItemkey());
					}
				});
		return compentPanel; //
	}

	/**
	 * ���ֲ��տؼ�
	 * 
	 * @param _itemvo
	 * @return
	 */
	private AbstractWLTCompentPanel getUIRefCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_UIRefPanel(_itemvo, this, null); // ���Ͳ���
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		((QueryCPanel_UIRefPanel) compentPanel).addBillQueryEditListener(new BillQueryEditListener() {
			public void onBillQueryValueChanged(BillQueryEditEvent _evt) {
				onChanged(_itemvo.getItemkey()); // �ڲ����м��������
			}
		});
		return compentPanel; //
	}

	/**
	 * ��ѡ��ؼ�
	 * 
	 * @param _itemvo
	 * @return
	 */
	private AbstractWLTCompentPanel getCheckBoxCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_CheckBox(_itemvo); // ��ѡ��//
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		((QueryCPanel_CheckBox) compentPanel).getComBox().addItemListener(new ItemListener() { // �����¼�
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							onChanged(_itemvo.getItemkey());
						}
					}
				});
		return compentPanel; //
	}

	public JButton getQuickQueryButton() {
		return popupButton.getButton(); //
	}

	/**
	 * ��ȡ�����ĸ����
	 * 
	 * @return
	 */
	public BillListPanel getBillListPanel() {
		return billlist;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public HashMap getQuickQueryConditionAsMap() {
		return getQuickQueryConditionAsMap(false); //
	}

	public HashMap getQuickQueryConditionAsMap(boolean _isAdd$SQL) {
		return getQuickQueryConditionAsMap(_isAdd$SQL, true); //
	}

	/**
	 * �����ٲ�ѯ��������һ��HashMap�з���..
	 * 
	 * @return
	 */
	public HashMap getQuickQueryConditionAsMap(boolean _isAdd$SQL, boolean _isDocheck) {
		if (_isDocheck) {
			if (!checkQuickQueryConditionIsNull()) {
				return null;
			}
		}

		HashMap map_condition = new HashMap();
		AbstractWLTCompentPanel[] alcp = getAllQuickQueryCompents();
		for (int i = 0; i < alcp.length; i++) {
			if (alcp.length != 0 && alcp != null) {
				map_condition.put(alcp[i].getItemKey(), getRealValueAt(alcp[i].getItemKey())); //
				map_condition.put("obj_" + alcp[i].getItemKey(), getValueAt(alcp[i].getItemKey())); //
			}
		}

		if (_isAdd$SQL) { //�����Ҫֱ�ӽ���̬SQL�����������,����֮
			String str_sql = getQuerySQLConditionByItemKeyMapping(null, false, false, _isDocheck ? false : true); //��ʹ������Ȩ��
			String str_sql_1 = getQuerySQLConditionByItemKeyMapping(null, false, true, _isDocheck ? false : true); //ʹ������Ȩ��
			if (str_sql == null || str_sql.trim().equals("")) {
				str_sql = " and 'û������'='û������'"; //
			}
			if (str_sql_1 == null || str_sql_1.trim().equals("")) {
				str_sql_1 = " and 'û������'='û������'"; //
			}
			map_condition.put("$SQL", str_sql); //
			map_condition.put("$SQL_1", str_sql_1); //
		}
		return map_condition;
	}

	public void addBillQuickActionListener(ActionListener _custActionListener) {
		custActionListener = _custActionListener; //
	}

	public void addBillCommonActionListener(ActionListener _custActionListener) {
		custCommonActionListener = _custActionListener; //
	}

	public boolean isQuickQueryQuiet() {
		return isQuickQueryQuiet;
	}

	public void setQuickQueryQuiet(boolean isQuickQueryQuiet) {
		this.isQuickQueryQuiet = isQuickQueryQuiet;
	}

	public void onQuickQueryClicked(final boolean _isCtrl) {
		try {
			popupButton.getButton().setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			if (isQuickQueryQuiet) { // ����Ǿ�̬��ѯ!����ʱ������ȴ���!!
				if (custActionListener != null) {//sunfujun/20121207/�Զ����û�еȴ������޸�
					custActionListener.actionPerformed(new ActionEvent(BillQueryPanel.this, 1, "" + _isCtrl));
				} else {
					onQuickQuery(_isCtrl); //
				}
			} else {
				if (billlist != null) {
					billlist.stopEditing(); //
				}
				new SplashWindow(JOptionPane.getFrameForComponent(this), new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						if (custActionListener != null) {//sunfujun/20121207/�Զ����û�еȴ������޸�
							custActionListener.actionPerformed(new ActionEvent(BillQueryPanel.this, 1, "" + _isCtrl));
						} else {
							onQuickQuery(_isCtrl); //
						}
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			popupButton.getButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * ���ٲ�ѯ����
	 */
	public void onQuickQuery(boolean _isCtrlDown) {
		onQuickQuery(_isCtrlDown, false);

	}

	public void onQuickQuery(boolean _isCtrlDown, boolean _isRegHVOinRowNumberVO) {
		boolean isHiddenAfterQuery = getTBUtil().getSysOptionBooleanValue("�б��ѯ���Ƿ��������ز�ѯ��", false); //
		// ����Զ���ActionListener,����Ĭ������
		if (custActionListener != null) {
			custActionListener.actionPerformed(new ActionEvent(this, 1, "" + _isCtrlDown)); //
			if (isHiddenAfterQuery && billlist != null) {
				billlist.setBillQueryPanelVisible(false); //�������ݺ��������ز�ѯ��,�����ϱ�����Ĺ���!!
			}
			return;
		}
		try {
			if (billlist != null) {
				String str_sql = null; //
				if (quickSQLCreater != null) {
					str_sql = quickSQLCreater.getQuickSQL(this); // ����Զ�����SQL������,���ö����SQL����������SQL!!!
				} else {
					str_sql = getQuerySQL(); // ���û�ж���SQL������,���׼���߼�ƴ��!!!
				}
				if (str_sql == null) {
					return;
				}
				str_currquicksql = str_sql; //
				if (billlist.getOrderCondition() != null) {
					str_currquicksql = str_currquicksql + " order by " + billlist.getOrderCondition(); //����!!
				}
				billlist.queryDataByDS(billlist.getDataSourceName(), str_currquicksql, _isRegHVOinRowNumberVO); // ��ƴ����������SQL���в�ѯ!!!
				if (billlist.getBillVOs() == null || billlist.getBillVOs().length == 0) {
					MessageBox.show(billlist, "û�в�ѯ����¼,���޸��������²�ѯ!", 3, null); //
					return; //
				}

				//�������ݺ��������ز�ѯ��,�����ϱ�����Ĺ���!!
				if (isHiddenAfterQuery) {
					billlist.setBillQueryPanelVisible(false); //
				}
				afterQuickQueryAddHisToClient();
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	//������ѯ�󡣲鵽���ݰѿؼ�ֵ��ӵ����ü�¼�С�
	private void afterQuickQueryAddHisToClient() {
		AbstractWLTCompentPanel[] _compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		for (int i = 0; i < _compents.length; i++) {
			if (_compents[i] instanceof QueryCPanel_TextField) { //�ı�����
				Object str = _compents[i].getObject(); //��ȡ��ѯ����
				QuickSearchHisMap.putValue(templetVO.getTempletcode(), _compents[i].getItemKey(), QuickSearchHisMap.HIS_TYPE_QUERYPANEL, str);
			}
		}
	}

	/**
	 * У���������
	 * 
	 * @return
	 */
	public boolean checkValidate() {
		// У���Ƿ��б�������..
		String str_itemkey = null;
		String str_name = null;
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		for (int i = 0; i < compents.length; i++) {
			str_itemkey = compents[i].getItemKey();
			str_name = compents[i].getItemName();
			for (int j = 0; j < templetItemVOs.length; j++) {
				if (templetItemVOs[j].getItemkey().equals(str_itemkey)) {
					if ("Y".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) { // �����ѯ���������
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							MessageBox.show(this, str_name + "����Ϊ��!", 3, null);
							return false;
						}
					}
				}
			}
		}
		HashMap groupA = new HashMap();
		HashMap groupB = new HashMap();
		HashMap groupC = new HashMap();

		String groupAMustInput = null;
		String groupBMustInput = null;
		String groupCMustInput = null;

		for (int i = 0; i < compents.length; i++) {
			str_itemkey = compents[i].getItemKey();
			str_name = compents[i].getItemName();
			for (int j = 0; j < templetItemVOs.length; j++) {
				if (templetItemVOs[j].getItemkey().equals(str_itemkey)) {
					if (!"Y".equals(groupAMustInput) && "A".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) {
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							groupA.put(str_itemkey, str_name);
							groupAMustInput = "N";
						} else {
							groupAMustInput = "Y";
						}
					} else if (!"Y".equals(groupBMustInput) && "B".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) {
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							groupB.put(str_itemkey, str_name);
							groupBMustInput = "N";
						} else {
							groupBMustInput = "Y";
						}
					} else if (!"Y".equals(groupCMustInput) && "C".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) {
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							groupC.put(str_itemkey, str_name);
							groupCMustInput = "N";
						} else {
							groupCMustInput = "Y";
						}
					}
				}
			}
		}
		StringBuffer str_names = new StringBuffer("���²�ѯ����,����������һ�\r\n��");
		if ("N".equals(groupAMustInput)) {
			String[] str_groupA = (String[]) groupA.values().toArray(new String[0]);
			for (int i = 0; i < str_groupA.length; i++) {
				str_names.append(str_groupA[i] + ";");
			}
			MessageBox.show(billlist, str_names.substring(0, str_names.length() - 1) + "��", 3, null);
			return false;
		}
		if ("N".equals(groupBMustInput)) {
			String[] str_groupB = (String[]) groupB.values().toArray(new String[0]);
			for (int i = 0; i < str_groupB.length; i++) {
				str_names.append(str_groupB[i] + ";");
			}
			MessageBox.show(billlist, str_names.substring(0, str_names.length() - 1) + "��", 3, null);
			return false;
		}
		if ("N".equals(groupCMustInput)) {
			String[] str_groupC = (String[]) groupC.values().toArray(new String[0]);
			for (int i = 0; i < str_groupC.length; i++) {
				str_names.append(str_groupC[i] + ";");
			}
			MessageBox.show(billlist, str_names.substring(0, str_names.length() - 1) + "��", 3, null);
			return false;
		}

		return true; //
	}

	public String getQuerySQL() {
		AbstractWLTCompentPanel[] _compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQL(_compents); // ֱ�������пؼ�!
	}

	// �õ�����SQL
	public String getQuerySQL(AbstractWLTCompentPanel[] _compentPanels) {
		boolean isValidate = checkValidate(); // �Ƿ�ͨ��!!!
		if (!isValidate) { // ���ûͨ��,�򷵻�
			return null;
		}

		StringBuilder sb_sql_all = new StringBuilder(); //
		sb_sql_all.append("select * from " + templetVO.getTablename() + " where 1=1 "); // //
		sb_sql_all.append(getQuerySQLCondition(_compentPanels, true, false, null)); // ȡ������,���Ҹ�����,�Ѿ�����У����!
		return sb_sql_all.toString(); //
	}

	public String getTableAllItem() {
		StringBuffer sb_item = new StringBuffer();
		int itemlength = templetItemVOs.length;
		for (int i = 0; i < itemlength; i++) {
			if (templetItemVOs[i].isNeedSave()) {
				if (sb_item.length() == 0) {
					sb_item.append(" " + templetItemVOs[i].getItemkey());
				} else {
					sb_item.append("," + templetItemVOs[i].getItemkey());
				}
			}
		}
		sb_item.append(" ");
		if (sb_item.toString().trim().length() == 0) {
			return "*";
		}
		return sb_item.toString();
	}

	/**
	 * ��������ǳ�����!!,��������������Ҫ��ʱ�����!!!��ֻ��ƴ������Ĳ�ѯ����!
	 * 
	 * @return
	 */
	public String getQuerySQLCondition() {
		return getQuerySQLCondition(null);
	}

	/**
	 * �ǳ���Ҫ�ķ���!����ѯ���ʹ�õ�ͬһ��,��ҵ����е��ֶ����ֲ�һ��,��Ҫ����itemkeyӳ��!! ����ģ���е�itemkeyת�����ҵ�ʵ�����ݿ��ֶ�!!! 
	 * @param _mapping
	 * @return
	 */
	public String getQuerySQLConditionByItemKeyMapping(String[][] _fieldNameMap) {
		HashMap itemKeyMapping = new HashMap(); //
		if (_fieldNameMap != null) {
			for (int i = 0; i < _fieldNameMap.length; i++) {
				itemKeyMapping.put(_fieldNameMap[i][0], _fieldNameMap[i][1]); //
			}
		}
		return getQuerySQLConditionByItemKeyMapping(itemKeyMapping); //
	}

	/**
	 * ����ѯ���ʹ�õ�ͬһ��,��ҵ����е��ֶ����ֲ�һ��,��Ҫ����itemkeyӳ��!! ����ģ���е�itemkeyת�����ҵ�ʵ�����ݿ��ֶ�!!! 
	 * @param _mapping
	 * @return
	 */
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap) {
		return getQuerySQLConditionByItemKeyMapping(_fieldNameMap, true, true); //
	}

	//�Ƿ����ģ�嶨�������
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap, boolean _isIgnoreDefineCondition) {
		return getQuerySQLConditionByItemKeyMapping(_fieldNameMap, _isIgnoreDefineCondition, true); //
	}

	//�Ƿ����ģ�嶨�������,�Լ��Ƿ���������Ȩ��!!!
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap, boolean _isIgnoreDefineCondition, boolean _isUseDataAccess) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, false, _isIgnoreDefineCondition, _isUseDataAccess, null, _fieldNameMap); //
	}

	//�Ƿ����ģ�嶨�������,�Լ��Ƿ���������Ȩ��!!!
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap, boolean _isIgnoreDefineCondition, boolean _isUseDataAccess, boolean _isHaveCheck) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, _isHaveCheck, _isIgnoreDefineCondition, _isUseDataAccess, null, _fieldNameMap); //
	}

	public String getQuerySQLCondition(String _tablePrefix) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, false, false, true, _tablePrefix, null); //
	}

	public String getQuerySQLCondition(boolean _isIgnoreDefineCondition, String _tablePrefix) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, false, _isIgnoreDefineCondition, true, _tablePrefix, null); //
	}

	public String getQuerySQLCondition(AbstractWLTCompentPanel[] _compents, boolean _isHaveChecked, boolean _isIgnoreDefineCondition, String _tablePrefix) {
		return getQuerySQLCondition(_compents, _isHaveChecked, _isIgnoreDefineCondition, true, _tablePrefix, null); //
	}

	/**
	 * ����Ҫ�ķ���,ȡ�ø����ؼ��Ĳ�ѯ����,ƴ�ɺ����Where������!!
	 * 
	 * @param _compents
	 * @param _isHaveChecked �Ƿ�У���������,�������ѯ�����Ƿ��Ǳ�����!!!
	 * @return
	 */
	public String getQuerySQLCondition(AbstractWLTCompentPanel[] _compents, boolean _isHaveChecked, boolean _isIgnoreDefineCondition, boolean _isUseDataAccess, String _tablePrefix, HashMap _itemKeyMap) {
		if (!_isHaveChecked) { // ���û�м�������Ҫ���һ��!
			boolean isValidate = checkValidate(); // �Ƿ�ͨ��!!!
			if (!isValidate) { // ���ûͨ��,�򷵻�
				return null;
			}
		}

		StringBuffer sb_sql_all = new StringBuffer(""); //

		JepFormulaParseAtUI jepUI = null; //
		LinkedHashMap lhmap_itemValue = new LinkedHashMap(); // �洢����������ֵ
		LinkedHashMap lhmap_itemSQL = new LinkedHashMap(); // �洢��������ƴ�ɵ�SQL
		if (!_isIgnoreDefineCondition) { // �������ģ�嶨�������!��ƴ�䶨�������
			if (billlist != null) {
				String str_datafiltercondition = billlist.getDataconstraint(); //
				if (str_datafiltercondition != null && !str_datafiltercondition.trim().equals("")) {
					sb_sql_all.append(" and (" + str_datafiltercondition + ") "); //
					lhmap_itemValue.put("$datacondition", str_datafiltercondition); //
					lhmap_itemSQL.put("$datacondition", str_datafiltercondition); //
				}
			}
		}

		if (billlist != null) {
			String str_custdatafiltercondition = billlist.getDataFilterCustCondition(); //
			if (str_custdatafiltercondition != null && !str_custdatafiltercondition.trim().equals("")) {
				sb_sql_all.append(" and (" + str_custdatafiltercondition + ") "); //
				lhmap_itemValue.put("$custdatacondition", str_custdatafiltercondition); //
				lhmap_itemSQL.put("$custdatacondition", str_custdatafiltercondition); //
			}
		}

		//�ȼ����Ĭ�ϵĲ�ѯ�����пո�Ľ�����ϵ!!!
		String str_andorype = getTBUtil().getSysOptionStringValue("��ѯ�����пո�Ľ�����ϵ", "and"); //һ���ı���������Ĳ�ѯ�����пո�ʱ�Զ������ɶ������,���������֮�䵽����and�Ĺ�ϵ����or�Ĺ�ϵ,���е���!! ��ǰĬ����or�Ĺ�ϵ,����ҵ�����еĳ��Էǳ�����,��Ϊһ����and��ϵ!!
		if (str_andorype.trim().equalsIgnoreCase("or")) { //�������ȷ��or,����or
			str_andorype = "or"; //
		} else {
			str_andorype = "and"; //����Ĭ����Զ��and,����С���������ֵ,����Ϊ��and
		}

		LinkedHashMap lm_custSQLCreateClass = new LinkedHashMap(); // �����Զ���SQL��Class����,����ʵ�ֽӿ�IBillQueryPanelSQLCreateIFC
		//���������ؼ�!!!
		for (int i = 0; i < _compents.length; i++) { // �������пؼ�
			AbstractWLTCompentPanel comptmp = _compents[i]; // //
			Object valueobj = comptmp.getObject(); //
			if (valueobj == null) {
				continue; // ���ֵΪ��,������....
			}
			// ȡ��ʵ�ʵ�ֵ!!�����������շֱ�ȡid��ֵ!
			String str_realvalue = null; //
			String str_allChildIds = null; //�����ӽ������!�����ͽṹ��,����ǵ�ѡ,��Ҳͬʱ�����ӽ��!ֻ�����洢��һ�����������$�����ӽ��ID����,�Ժ�������óɡ�In���ơ�,��Ĭ��ǿ������һ�¸ñ���!�����,��ʹ�øñ���!
			if (valueobj instanceof StringItemVO) { // ������ı���
				str_realvalue = ((StringItemVO) valueobj).getStringValue(); //
			} else if (valueobj instanceof ComBoxItemVO) {
				str_realvalue = ((ComBoxItemVO) valueobj).getId(); //
			} else if (valueobj instanceof RefItemVO) {
				RefItemVO refVO = (RefItemVO) valueobj; //
				str_realvalue = refVO.getId(); //
				if (refVO.getHashVO() != null) {
					str_allChildIds = refVO.getHashVO().getStringValue("$�����ӽ��ID"); //
				}
			}
			if (str_realvalue == null || str_realvalue.trim().equals("")) { // ����ǿ�ֵ��մ�!!��ֱ������ȥ!
				continue; // ֱ������!// ������������Ŀ�з��ֶ�ѡ����ûѡʱ�����ǿմ�,�������SQL����!
			}

			if (isHenDeng(str_realvalue)) { //����Ǻ��ʽ,��ֱ�Ӽ��벢����ѭ����
				sb_sql_all.append(" and " + str_realvalue + " "); // ����ȫ��...
				continue; //
			}

			String str_itemkey = comptmp.getItemKey().toLowerCase(); // ͳͳת����Сд
			if (str_itemkey.startsWith("#")) {
				str_itemkey = str_itemkey.substring(1, str_itemkey.length()); // �����#��ͷ,���ȥ֮,����Ϊ�˽��itemkey��ͬ������
			}
			String str_queryitemkey = str_itemkey; //
			if (_itemKeyMap != null) { // �����ת��,����
				String[] str_itemkeyMappings = (String[]) _itemKeyMap.keySet().toArray(new String[0]); //
				for (int j = 0; j < str_itemkeyMappings.length; j++) {
					if (str_itemkeyMappings[j].equalsIgnoreCase(str_itemkey)) { // ���Դ�Сд
						str_queryitemkey = (String) _itemKeyMap.get(str_itemkeyMappings[j]); // ����!
						break; //
					}
				}
			}
			if (_tablePrefix != null) { // ���ָ����α������!!!�����ǰ��!!
				str_queryitemkey = _tablePrefix + "." + str_queryitemkey; //
			}
			lhmap_itemValue.put(str_itemkey, str_realvalue); // �����ʵ��ֵ!!
			lhmap_itemValue.put(str_itemkey + "_obj", valueobj); // ����ֵ!!

			Pub_Templet_1_ItemVO itemVO = templetVO.getItemVo(str_itemkey); //
			String str_itemtype = null; // �ؼ�����
			String str_SqlCreateType = itemVO.getQuerycreatetype(); // SQL��������!!

			StringBuffer sb_sql = new StringBuffer(); //
			if (str_SqlCreateType == null || str_SqlCreateType.trim().equals("") || str_SqlCreateType.trim().equals("Ĭ�ϻ���")) {// �����Ĭ�ϻ���
				if (itemVO.getQueryItemType() != null && !itemVO.getQueryItemType().trim().equals("")) {
					str_itemtype = itemVO.getQueryItemType(); // ���ָ�����ѯ�����Ͷ���,��ȡ֮,������Ĭ�ϵ�
				} else {
					str_itemtype = itemVO.getItemtype(); //
				}
				if (str_itemtype.equals(WLTConstants.COMP_TEXTFIELD) || str_itemtype.equals(WLTConstants.COMP_TEXTAREA)) { //�ı���,�����ı���
					str_realvalue = getTBUtil().replaceAll(str_realvalue.trim(), "'", "''");//�����ѯ�����������ַ������/2016-02-15��
					String[] str_items = getTBUtil().split(str_realvalue, " "); //������˶������,���Կո�ָ�,ÿ������֮����
					if (str_items.length <= 1) { //���ֻ��һ������!!
						if (str_realvalue.startsWith("~")) {
							sb_sql.append(" and " + str_queryitemkey + " like '" + str_realvalue.substring(1, str_realvalue.length()) + "%'"); //
						} else {
							sb_sql.append(" and " + str_queryitemkey + " like '%" + str_realvalue + "%'"); //
						}
					} else {
						sb_sql.append(" and (");
						for (int j = 0; j < str_items.length; j++) { //����ж������,��
							String str_itemTrimValue = str_items[j].trim(); // //
							if (str_itemTrimValue.startsWith("~")) {
								sb_sql.append(str_queryitemkey + " like '" + str_itemTrimValue.substring(1, str_itemTrimValue.length()) + "%'"); // //
							} else {
								sb_sql.append(str_queryitemkey + " like '%" + str_itemTrimValue + "%'"); // //
							}
							if (j != str_items.length - 1) { //����������һλ,�����and/or
								sb_sql.append(" " + str_andorype + " "); //Ĭ����and��ϵ,��ǰ��or�Ĺ�ϵ,����ҵ�����еĿͻ���Ҫ����and��ϵ!!��ʵ���߶���һ������!! ���ڵĻ�����Ĭ����and��ϵ,������ͨ����������ѯ�����пո�Ľ�����ϵ��=��or�����ı�!
							}
						}
						sb_sql.append(")");
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_NUMBERFIELD)) {// ���ֿ�
					RefItemVO refItemVO = (RefItemVO) valueobj;
					if (refItemVO.getHashVO() != null && refItemVO.getHashVO().getStringValue("querycondition") != null) {
						String str_macrocondition = refItemVO.getHashVO().getStringValue("querycondition"); //
						String str_convertcondition = getTBUtil().replaceAll(str_macrocondition, "{itemkey}", str_itemkey); //
						sb_sql.append(" and " + str_convertcondition); //
					} else {
						String[] str_value = refItemVO.getId().split(";");
						if (!str_value[0].equals("")) {
							if (str_value[1].equals("")) {
								sb_sql.append(" and " + str_queryitemkey + ">=" + str_value[0]);
							} else {
								sb_sql.append(" and (" + str_queryitemkey + ">=" + str_value[0] + " and " + str_queryitemkey + "<=" + str_value[1] + ")");
							}
						} else {
							if (!str_value[1].equals("")) {
								sb_sql.append(" and " + str_queryitemkey + "<=" + str_value[1]);
							}
						}
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_COMBOBOX)) { //������!!!!��ǰ��ѯ����������༭��һ����,������ѯ�ĳ��˶�ѡ!! ����Ȼ�Ǳ����in��!!
					if (str_realvalue.indexOf(";") >= 0) { //����зֺ�,��ָ�����,ʹ��in��ѯ!!! ��Ϊ�����Ļ������������ڲ�ѯʱ����˶�ѡ!������in������!!
						String[] str_in_items = getTBUtil().split(str_realvalue, ";"); //
						sb_sql.append(" and " + str_queryitemkey + " in (" + getTBUtil().getInCondition(str_in_items) + ")"); //
					} else {
						sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue + "'"); //
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_CHECKBOX)) { // ��ѡ��
					//����ǹ�ѡ�򣬲���ģ�������á����ٲ�ѯ�Ƿ�ɱ༭��Ϊ���񡿣� �Ͳ�ƴsql������ Gwang 2016-05-10
					if (!comptmp.isItemEditable()) {
						continue;
					}
					sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue + "'");
				} else if (str_itemtype.equals(WLTConstants.COMP_DATE) || str_itemtype.equals(WLTConstants.COMP_DATETIME)) { // ������ʱ��
					RefItemVO refItemVO = (RefItemVO) valueobj;
					if (refItemVO.getHashVO() != null && refItemVO.getHashVO().getStringValue("querycondition") != null) {
						String str_macrocondition = refItemVO.getHashVO().getStringValue("querycondition"); //
						String str_convertcondition = getTBUtil().replaceAll(str_macrocondition, "{itemkey}", str_queryitemkey); //
						sb_sql.append(" and " + str_convertcondition); //
					} else {
						sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue+"'"); //zzl [2019-1-7 bug����]
					}
				} else if (str_itemtype.equals("��ѡ����")) { // ��ѡ����!!!
					String[] tempid = getTBUtil().split(str_realvalue, ";"); // str_realvalue.split(";");
					if (tempid != null && tempid.length > 0) {
						sb_sql.append(" and (");
						for (int j = 0; j < tempid.length; j++) {
							sb_sql.append(str_queryitemkey + " like '%;" + tempid[j] + ";%'"); // 
							if (j != tempid.length - 1) { //
								sb_sql.append(" or ");
							}
						}
						sb_sql.append(") "); //
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_REFPANEL) || // ���β���
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_TREE) || // ���Ͳ���
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_CUST) || // �Զ������
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || // �б�ģ�����
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || // ����ģ�����
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // ע���������
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_REGEDIT) // ע�����
				) {
					if (str_itemtype.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) { //��������Ͳ���,�������ǵ�ѡ���Ƕ�ѡ,��Ĭ����in����!!!��Ϊ��In���ơ��������Ȼ���ݡ�=���ơ���,����������ô��û��!
						//��ǰ��֪��Ϊʲô���ж�����Ƕ�ѡ,����Ϊ��like or����?ֻ���ڴ洢���Ƕ�ѡʱ����LikeOr����!! ��Ϊ�����Ѿ������ˡ���ѯ���ơ�������ԣ�ֻ��ֱ�ӽ�����ѯ���ơ�����Ϊ��LikeOr���ơ���Ϳ�����!
						sb_sql.append(" and " + str_queryitemkey + " in (" + getDynInConditions(str_realvalue) + ") "); //
					} else {
						if (str_realvalue.indexOf(";") > 0) { //����������Ͳ���,����������(���ܻ��Ƕ�ѡŶ!),��Ӹ������ж�,�Ӷ��ж��ǡ�In���ơ����ǡ�=���ơ�
							sb_sql.append(" and " + str_queryitemkey + " in (" + getDynInConditions(str_realvalue) + ") ");
						} else {
							sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue + "'");
						}
					}

				} else if (str_itemtype.equals("�ļ�ѡ���") || str_itemtype.equals(WLTConstants.COMP_STYLEAREA)) { //������ļ�ѡ���,���ı���
					sb_sql.append(" and " + str_queryitemkey + " like '%" + str_realvalue + "%'");
				} else { //
					sb_sql.append(" and " + str_queryitemkey + " like '%" + str_realvalue + "%'"); //������������
				}
			} else if (str_SqlCreateType.equals("In����")) { // in����
				String str_parseStr = null; //
				if (str_allChildIds != null) { //����Ǹ����ն���,���ҷ��صĲ���VO�е�HashVO�д���һ����Ϊ��$�����ӽ��ID���ı���,���Զ�ʹ�øñ���!!
					str_parseStr = str_allChildIds; //
				} else {
					str_parseStr = str_realvalue; //
				}
				sb_sql.append(" and " + str_queryitemkey + " in (" + getDynInConditions(str_parseStr) + ") "); //
			} else if (str_SqlCreateType.equals("LikeOr����")) { // like����
				String[] tempid = getTBUtil().split(str_realvalue, ";"); //
				sb_sql.append(" and (");
				for (int j = 0; j < tempid.length; j++) {
					sb_sql.append(str_queryitemkey + " like '%;" + tempid[j] + ";%'"); // 
					if (j != tempid.length - 1) { //
						sb_sql.append(" or ");
					}
				}
				sb_sql.append(") "); //
			} else if (str_SqlCreateType.equals("SQLServerȫ�ļ���")) { // ȫ�ļ���!!
				// ������������SQLServer�� like '%����%'ʱ�������ȥ! ��󲻵ò���ȫ�ļ�������!
				sb_sql.append(" and contains(" + str_queryitemkey + ",'" + str_realvalue + "') ");
			} else if (str_SqlCreateType.equals("SQLServerȫ�ļ���2")) { // ȫ�ļ���!!,��Ϊ�������ķִ���������,���ò�����Ӣ��ʹ�ÿո�ǿ�зֲ�,�Ӷ���֤���е��������ܲ鵽!!!
				// ������������SQLServer�� like '%����%'ʱ�������ȥ!��󲻵ò���ȫ�ļ�������!
				sb_sql.append(" and contains(" + str_queryitemkey + ",'" + appendSearchCon(str_realvalue) + "') ");
			} else if (str_SqlCreateType.equals("�Զ���SQL")) {
				String str_SqlCreateCustDefine = itemVO.getQuerycreatecustdef(); // SQL�������Զ���!!
				if (str_SqlCreateCustDefine != null && !str_SqlCreateCustDefine.trim().equals("")) { //
					if (jepUI == null) {
						jepUI = new JepFormulaParseAtUI(); // ���û�д���,�򴴽�֮!!!
					}
					str_SqlCreateCustDefine = (String) jepUI.execFormula(str_SqlCreateCustDefine); // ִ�й�ʽ...
					String[] str_items = getTBUtil().split(str_realvalue, ";"); //
					String str_insqls = getTBUtil().getInCondition(str_items); //
					StringBuffer sb_LikeOrSqls = new StringBuffer(); // //
					sb_LikeOrSqls.append(" ("); // //
					for (int j = 0; j < str_items.length; j++) {
						sb_LikeOrSqls.append(str_queryitemkey + " like '%;" + str_items[j] + ";%'"); // 
						if (j != str_items.length - 1) { //
							sb_LikeOrSqls.append(" or ");
						}
					}
					sb_LikeOrSqls.append(") "); // //
					// ֧�����ֺ�����滻: {ItemValue},{InValues},{LikeOrSQL}
					str_SqlCreateCustDefine = getTBUtil().replaceAll(str_SqlCreateCustDefine, "{itemvalue}", str_realvalue); //
					str_SqlCreateCustDefine = getTBUtil().replaceAll(str_SqlCreateCustDefine, "{invalues}", str_insqls); //
					str_SqlCreateCustDefine = getTBUtil().replaceAll(str_SqlCreateCustDefine, "{LikeOrSQL}", sb_LikeOrSqls.toString()); //
					sb_sql.append(" and (" + str_SqlCreateCustDefine + ") "); // //
				} else {
					sb_sql.append(" and 'û�ж���SQL'='û�ж���SQL' "); //
				}
			} else if (str_SqlCreateType.equals("�Զ�����")) { // ����Զ�����!!
				String str_SqlCreateCustDefine = itemVO.getQuerycreatecustdef(); // SQL�������Զ���!!!
				if (str_SqlCreateCustDefine != null && !str_SqlCreateCustDefine.trim().equals("")) { //
					lm_custSQLCreateClass.put(str_itemkey, str_SqlCreateCustDefine); // ����!!!������ȥ,���һ�����,��Ϊ�����ܻ��õ�������SQL����,�Ӷ��������!!!
				}
			} else if (str_SqlCreateType.equals("������")) { // ���������Ͳ���
			}

			if (!sb_sql.toString().equals("")) {
				lhmap_itemSQL.put(str_itemkey, sb_sql.toString()); // //
				sb_sql_all.append(sb_sql.toString()); // ����ȫ��...
			}

		} // for ѭ������!!!

		// ��������Ȩ��!!!
		if (_isUseDataAccess && billlist != null) { //���ʹ��ģ�嶨�������Ȩ��!
			String str_AccessSqlCons = this.billlist.getDataPolicyCondition(); // ����Ȩ����Դ����!!
			if (str_AccessSqlCons != null) { //
				lhmap_itemSQL.put("$resaccess", " and (" + str_AccessSqlCons + ") "); // ��ԴȨ�����ɵ�!!!
				sb_sql_all.append(" and (" + str_AccessSqlCons + ") "); //
			}
		} else if (_isUseDataAccess && null != dataPolicyCondition && !dataPolicyCondition.equals("")) {//Ԭ����20120913���             �������ֵ�Ȩ������
			lhmap_itemSQL.put("$resaccess", " and (" + dataPolicyCondition + ") "); // ��ԴȨ�����ɵ�!!!
			sb_sql_all.append(" and (" + dataPolicyCondition + ") "); //

		}

		// ������Զ������,��Ϊ�������ʱ��,��Ϊ��Ҫ��ѯ�ؼ��ֵȣ�����Ϊ��������ܣ����뽫ǰ�����ɵ�SQL���͵���̨ȥ,����,Ҳ�����Ͳ�������!!�Ӷ��������
		String[] str_sqlCreates = (String[]) lm_custSQLCreateClass.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_sqlCreates.length; i++) { // ѭ�����������
			String str_className = (String) lm_custSQLCreateClass.get(str_sqlCreates[i]); // ����!!
			String str_thisItemValue = (String) lhmap_itemValue.get(str_sqlCreates[i]); //
			try {
				String str_sql = UIUtil.getMetaDataService().getBillQueryPanelSQLCustCreate(str_className, str_sqlCreates[i], str_thisItemValue, lhmap_itemValue, lhmap_itemSQL, sb_sql_all.toString());
				if (str_sql != null && !str_sql.trim().equals("")) { // �����Ϊ��!!!
					lhmap_itemSQL.put(str_sqlCreates[i], " and (" + str_sql + ") "); // �ټ���!!
					sb_sql_all.append(" and (" + str_sql + ") "); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}

		// System.out.println(sb_sql);
		return sb_sql_all.toString();
	}

	//�Ƿ���,���硾'��������'='��������'��??
	private boolean isHenDeng(String _str) {
		_str = _str.trim(); //
		if (_str.indexOf("=") > 0) {
			String str_1 = _str.substring(0, _str.indexOf("=")); //���ں�ǰ��
			String str_2 = _str.substring(_str.indexOf("=") + 1); //���ں�ǰ��
			if (str_1.trim().equals(str_2.trim())) {
				return true; //
			}
		}
		return false; //
	}

	/**
	 * ���ݽ�����ݶ�̬���������Ƿ�Ҫ����������תһ��???
	 * @param _initStr
	 * @return
	 * @throws Exception
	 */
	private String getDynInConditions(String _initStr) {
		try {
			String[] str_items = getTBUtil().split(_initStr, ";"); //; //
			if (str_items.length <= 999) { // �������1000������,�򲻸���,ֱ��ƴ!!
				return getTBUtil().getInCondition(str_items); //
			} else {
				String str_inCons = UIUtil.getSubSQLFromTempSQLTableByIDs(str_items); // ȡ���Ӳ�ѯ������,���û�г���800��ֱ�ӷ���!!
				return str_inCons; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "'�����쳣'";
		}
	}

	// //
	private String appendSearchCon(String _str) {
		_str = new TBUtil().replaceAll(_str, " ", ""); // ������������Ŀ�з����пո�ᱨ��,�����Ƚ��ո�ͳͳȥ��!!!
		StringBuilder sb_con = new StringBuilder(); //
		for (int i = 0; i < _str.length(); i++) {
			if (i != _str.length() - 1) {
				sb_con.append(_str.substring(i, i + 1) + "*"); //
			} else {
				sb_con.append(_str.substring(i, i + 1)); //
			}
		}
		return sb_con.toString(); //
	}

	public boolean checkQuickQueryConditionIsNull() {
		// У���Ƿ��б�������..
		String str_itemkey = null;
		String str_name = null;
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		for (int i = 0; i < compents.length; i++) {
			str_itemkey = compents[i].getItemKey();
			str_name = compents[i].getItemName();
			for (int j = 0; j < templetItemVOs.length; j++) {
				if (templetItemVOs[j].getItemkey().equals(str_itemkey)) {
					if ("Y".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) { // �����ѯ���������
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							MessageBox.show(this, str_name + "����Ϊ��!", 3, null);
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	//�Ҽ���ʾ��ѯ����!!
	private void showQuickSQL() {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("��ǰ�б�ʵ�ʲ�ѯ��SQL��:\r\n[" + this.str_currquicksql + "]\r\n\r\n"); //
		sb_sql.append("���÷���BillQueryPanel.getQuerySQLConditionByItemKeyMapping(HashMap _fieldMap,boolean _isIgnoreDefineCondition, boolean _isUseDataAccess)�õ���ѯ������SQL:\r\n"); //
		String str_sql = getQuerySQLConditionByItemKeyMapping(null, true, true, true); //�������������ֱ��ʾ:�Ƿ����ģ���Զ�������;�Ƿ���������Ȩ��
		sb_sql.append("[" + str_sql + "],��SQL���ƴ�ӳɡ�select * from ���� where 1=1 ${���ɵ�����}��\r\n"); //
		sb_sql.append("���в���_fieldMap��ʾ���Խ���Ӧ��itemkey�滻���µ�����,_isIgnoreDefineCondition��ʾ�Ƿ����ģ���ж���ĸ�������,_isUseDataAccess��ʾ�Ƿ�����ģ���ж��������Ȩ�޲���!"); //
		MessageBox.show(this, sb_sql.toString()); //
	}

	/**
	 * ͨ�ò�ѯ����. ���ǵ��ͨ�ò�ѯ��ť��ִ�еĶ���
	 */
	public void onCommonQuery() {
		onCommonQuery(false);
	}

	public void onCommonQuery(boolean _isRegHVOinRowNumberVO) {
		if (1 == 2) {//��ǰ�ǵ����������ʴ�ϣ�����������͸߼���ѯ��һ���߼�����ǰ������ٲ�ѯ��ͨ�ò�ѯ����ͬһ�������������⣬ֻ��һ�����������֣��༭��ʽ�������ڲ�����/sunfujun/20121207
			if (commQueryDialog == null) { // ���û�д�����ѯ��,�򴴽�֮
				commqueryPanel = getQueryPanel(2); // //
				int li_width = (int) (commqueryPanel.getPreferredSize().getWidth() + 100); //
				int li_height = (int) (commqueryPanel.getPreferredSize().getHeight() + 60); //

				if (li_width < 300) {
					li_width = 300;
				}
				if (li_width > 1000) {
					li_width = 1000;
				}

				if (li_height < 150) {
					li_height = 150;
				}
				if (li_width > 700) {
					li_width = 700;
				}

				commQueryDialog = new CommQueryDialog(this, this.templetVO.getTempletname() + "[ͨ�ò�ѯ]", li_width, li_height, commqueryPanel); //
			}
			commQueryDialog.setVisible(true); // ��ʾ��ѯ��.
			if (commQueryDialog.getCloseType() == BillDialog.CONFIRM) { // �����ȷ������
				if (custCommonActionListener != null) {
					custCommonActionListener.actionPerformed(new ActionEvent(this, 1, "commonquery")); //
					return;
				}
				String str_sql = null; //
				if (quickSQLCreater != null) {
					str_sql = quickSQLCreater.getQuickSQL(this); // ����Զ�����SQL������,���ö����SQL����������SQL!!!
				} else {
					str_sql = getQuerySQL(); // ���û�ж���SQL������,���׼���߼�ƴ��!!!
				}
				billlist.queryDataByDS(billlist.getDataSourceName(), str_sql, _isRegHVOinRowNumberVO); // ִ��SQL
			}
		} else {
			showOrHidenComplexQuery();
		}
	}

	/**
	 * ����ǰ���������XML
	 */
	public void exportConditionToXML() {
		HashMap conditionMap = getQuickQueryConditionAsMap(); // ��ȡ����ѯ����.
		if (conditionMap == null) {
			return;
		}
		XMLIOUtil xmlutil = new XMLIOUtil(); //
		try {
			String str_xml = xmlutil.exportObjToXMLString(conditionMap); //
			BillDialog dialog = new BillDialog(this, "��������", 800, 500); // //
			JTextArea textArea = new JTextArea(str_xml);
			textArea.setForeground(Color.BLUE); //
			textArea.setEditable(false); //
			textArea.setFont(LookAndFeel.font); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 
	 * @return
	 */
	public AbstractWLTCompentPanel[] getAllQuickQueryCompents() {
		return (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
	}

	public AbstractWLTCompentPanel[] getAllQuickQuerySaveCompents() {
		return (AbstractWLTCompentPanel[]) v_compentsaves.toArray(new AbstractWLTCompentPanel[0]); //
	}

	public AbstractWLTCompentPanel getCompent(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (_key.equalsIgnoreCase(compents[i].getItemKey())) {
				return compents[i];
			}
		}
		return null;
	}

	/**
	 * ����ִ��ĳһ����ʽ..ʹ��JEPȥִ��!!
	 * 
	 * @param _formula
	 */
	private void dealFormula(String _formula) {
		//String str_formula = UIUtil.replaceAll(_formula, " ", "");//����ͬBillCardPanel�ж���Ҫע�͵������������ʽ���в�ѯsql��䣬��ᱨ�����/2013-06-03��
		getJepFormulaValue(_formula); //
	}

	private String getJepFormulaValue(String _exp) {
		JepFormulaParse jepParse = new JepFormulaParseAtUI(this); // ����������!!
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
	 * ִ��ĳһ��ļ��ع�ʽ!
	 */
	public void execEditFormula(String _itemkey) {
		String[] str_editformulas = getEditFormulas(_itemkey); //
		if (str_editformulas == null || str_editformulas.length == 0) {
			return;
		}

		// ѭ������ʽ...
		for (int i = 0; i < str_editformulas.length; i++) {
			dealFormula(str_editformulas[i]); // ����ִ�й�ʽ!!!
			logger.debug("���ٲ�ѯ���ִ��[" + _itemkey + "]�ı༭��ʽ:[" + str_editformulas[i] + "]"); //
		}
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
					return getTBUtil().split1(str_formula, ";");
				} else {
					return null;
				}
			}
		}
		return null;
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
	 * 
	 * @param _itemkey
	 * @return
	 */
	private AbstractWLTCompentPanel getNextCompent(String _itemkey) {
		int li_startindex = findIndex(_itemkey);
		for (int i = li_startindex + 1; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].isCardisshowable().booleanValue()) { // ���ĳ���ؼ��ǿ�Ƭ��ʾ��!!
				AbstractWLTCompentPanel panel = getCompentByKey(templetItemVOs[i].getItemkey()); //
				if (panel != null && panel.isVisible()) { // ����ؼ���,������ʾ��!!
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
	 * �õ�������ֵ
	 */
	public String getRealValueAt(String _key) {
		return getCompentRealValue(_key);

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
				if (li_pos > 0) { // ����к��
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

	/**
	 * ����������ֵ
	 * 
	 * @param _key
	 * @param _value
	 */
	// ����ֵ..
	public void setCompentObjectValue(String _key, Object _obj) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent != null) {
			compent.setObject(_obj); //
		}
	}

	public Pub_Templet_1_ItemVO getTempletItemVO(String key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(key))
				return templetItemVOs[i];
		}
		return null;
	}

	public String getTempletCode() {
		return this.str_templetcode; //
	}

	public void setRealValueAt(String _key, String _value) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent == null) {
			return;
		}
		compent.setValue(_value); //
	}

	// ���ø����ؼ���ǰ����ɫ!�ı���,������,���յ�,�ݲ�ʵ��,�Ժ�����ؿ�����ͨ���޸�ֵ�ٴ����¼���ʵ�֣�����ֱ���޸Ŀؼ�������ʵ��!����α�ֵ֤��ؼ����Ա���һ��!
	public void setItemForeGroundColor(String _key, String _foreGroundColor) {

	}

	private synchronized void onChanged(String _itemkey) {
		if (bo_isallowtriggereditevent) { // ���������
			bo_isallowtriggereditevent = false;
			Object obj = this.getCompent(_itemkey).getObject(); // ȡ�õ�ǰֵ
			execEditFormula(_itemkey); // ִ�б༭��ʽ..������һ�д���˭��˭���д���һ������!!!!!!
			for (int i = 0; i < v_listeners.size(); i++) {
				BillQueryEditListener listener = (BillQueryEditListener) v_listeners.get(i);
				listener.onBillQueryValueChanged(new BillQueryEditEvent(_itemkey, obj, this)); //
			}

			bo_isallowtriggereditevent = true; //
		}
	}

	public void setVisiable(String _itemkey, boolean _bo) {
		AbstractWLTCompentPanel panel = getCompentByKey(_itemkey);
		if (panel != null) {//������Ҫ�ж�һ�£�ֻ���ҵ��˿ؼ��������Ƿ���ʾ������ᱨ��ָ��������/2012-08-09��
			panel.setItemVisiable(_bo); //
		}
	}

	public void setVisiable(String[] _itemkeys, boolean _bo) {
		for (int i = 0; i < _itemkeys.length; i++) {
			getCompentByKey(_itemkeys[i]).setItemVisiable(_bo); //
		}
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

	/**
	 * ע���¼�
	 * 
	 * @param _listener
	 */
	public void addBillQueryEditListener(BillQueryEditListener _listener) {
		v_listeners.add(_listener); //
	}

	private synchronized void onChangedAndFocusNext(String _itemkey) {
		onChanged(_itemkey);
		AbstractWLTCompentPanel actionpanel = this.getNextCompent(_itemkey); //
		// ���������һ��!
		if (actionpanel != null) {
			actionpanel.focus(); //
		}
	}

	public void setQuickSQLCreater(BillQueryQuickSQLCreaterIFC quickSQLCreater) {
		this.quickSQLCreater = quickSQLCreater;
	}

	public void showOrHidenComplexQuery() {
		queryPanel.removeAll();
		JPanel jp = null;
		if (complexquery) {
			jp = getQueryPanel(1);
			queryPanel.add(jp);
			complexquery = false;
			if (btn_complexquery != null) {
				btn_complexquery.setText("��ʾ�߼���ѯ");
			}
			if (menuItem_commquery != null) {
				menuItem_commquery.setText("ͨ�ò�ѯ");
			}
		} else {
			jp = getQueryPanel(2);
			queryPanel.add(jp);
			complexquery = true;
			if (btn_complexquery != null) {
				btn_complexquery.setText("���ظ߼���ѯ");
			}
			if (menuItem_commquery != null) {
				menuItem_commquery.setText("���ٲ�ѯ");
			}
		}
		resetAllHideQueryCompent();
		//Ԭ����20130408���ģ���Ҫ��������������ʾ�߼���ѯ���ȱ�С��֮������
		double height = (jp.getPreferredSize().getHeight() >= btnpanel.getPreferredSize().getHeight() ? jp.getPreferredSize().getHeight() : btnpanel.getPreferredSize().getHeight());
		double weight = (jp.getPreferredSize().getWidth() >= btnpanel.getPreferredSize().getWidth() ? jp.getPreferredSize().getWidth() : btnpanel.getPreferredSize().getWidth());
		queryPanel.setPreferredSize(new Dimension((int) weight, (int) height));

		queryPanel.updateUI();
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}
}
