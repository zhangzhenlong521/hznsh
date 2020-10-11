package cn.com.infostrategy.ui.workflow.pbom;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.I_MyJScrollPane;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.WorkTabbedPanel;

/**
 * һ���Զ������ͼ��Bom�������
 * 
 * @author xch
 * 
 */
public class BillBomPanel extends JPanel implements BomItemClickListener, ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel buttonBarPanel = null; //����İ�ť�����!!
	private JPanel toftPanel = new JPanel(); // һ�������,���Է�N��Bomͼ��!!!
	private CardLayout cardLayout = new CardLayout(); //
	private LinkedHashMap hm_Boms = new LinkedHashMap();//�洢BillItemPanel

	private Vector v_billbomlicklistener = new Vector(); //
	private WLTButton btn_skipfirst, btn_back; //

	private Stack stack = new Stack(); // //
	private String str_firstitemname = null; //

	private Hashtable ht_riskVO = null; //
	private boolean editable = true;//������ǰ�ģ�Ĭ��Ϊ�ɱ༭

	private TBUtil tbutil = new TBUtil(); //
	private boolean isBomimgAlignLeft = false; //��ǰͼƬ�Ǿ����,������Ū��Ĭ�Ͼ���!����һ�����пͻ�ϲ������,���Ը������!
	private String scrolltype = null;
	private boolean showNorthPanel = true; //�Ƿ���ʾBomͼ�ϵĵ�����ť, ������Bomͼ������ǰ�������(���״ε�¼ϵͳ�İ���ͼƬ) Gwang 2012-11-30

	/**
	 * @return the showNorthPanel
	 */
	public boolean isShowNorthPanel() {
		return showNorthPanel;
	}

	/**
	 * @param showNorthPanel the showNorthPanel to set
	 */
	public void setShowNorthPanel(boolean showNorthPanel) {
		this.showNorthPanel = showNorthPanel;
	}

	/**
	 * Ĭ�Ϲ��췽�����������
	 */
	private void BillBomPanel() {
	}

	/**
	 * 
	 * @param _bomcode
	 */
	public BillBomPanel(String _bomcode) {
		toftPanel.setOpaque(false); //͸��!!!
		toftPanel.setLayout(cardLayout); //
		stack.push(_bomcode); //ѹ���ջ!!
		BomItemPanel bomitemPanel = new BomItemPanel(this, _bomcode); // �Ƚ���һ��Bomͼ����
		bomitemPanel.addBomItemClickListener(this); //
		hm_Boms.put(bomitemPanel.getBomcode(), bomitemPanel); //������!!!
		isBomimgAlignLeft = tbutil.getSysOptionBooleanValue("BOMģ��ͼƬ�Ƿ����", false); //��ϵͳ��������,������Ĭ���Ǿ���!
		scrolltype = tbutil.getSysOptionStringValue("BOMģ����������", "1");//Ĭ��ȫ���ɹ�����������ǰϵͳ���
		//�������
		JPanel containerPanel = new JPanel(new FlowLayout((isBomimgAlignLeft ? FlowLayout.LEFT : FlowLayout.CENTER), 0, 0)); //Ϊ�˾���,Ūһ������
		containerPanel.setOpaque(false); //͸��!
		containerPanel.add(bomitemPanel);
		int li_item_width = (int) bomitemPanel.getPreferredSize().getWidth(); //
		int li_item_height = (int) bomitemPanel.getPreferredSize().getHeight(); //
		containerPanel.setPreferredSize(new Dimension(li_item_width, li_item_height)); //Ҫ���С!! ԭ�е���li_item_height���30 

		//		JScrollPane scroll = new JScrollPane(); //
		//		scroll.setOpaque(false); //͸��!
		//		scroll.getViewport().setOpaque(false); //͸��!
		//		scroll.getViewport().add(containerPanel); //
		//
		//		if ("1".equals(scrolltype)) {//bomͼ���������ã�1-ȫ���ɹ�����2-������ɹ�����3-������ɹ�����4-ȫ�����ɹ���
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//		} else if ("2".equals(scrolltype)) {
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//		} else if ("3".equals(scrolltype)) {
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//		} else if ("4".equals(scrolltype)) {
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//		}

		//		scroll.setBorder(BorderFactory.createEmptyBorder()); //���ù������ı߿�Ϊ��

		I_MyJScrollPane scroll = new I_MyJScrollPane(); //
		scroll.setOpaque(false); //͸��!
		scroll.getViewport().setOpaque(false); //͸��!
		scroll.getViewport().add(containerPanel); //�ڹ������м����������
		scroll.setBorder(BorderFactory.createEmptyBorder());

		toftPanel.add(bomitemPanel.getBomcode(), scroll); //���������!
		cardLayout.show(toftPanel, bomitemPanel.getBomcode()); //��ʾ
		str_firstitemname = bomitemPanel.getBomcode(); //

		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //��������һ����һ��!��Ϊ�����е�����ť!
		contentPanel.add(toftPanel, BorderLayout.CENTER); //�������Ч��!!!
		contentPanel.add(getNorthPanel(), BorderLayout.NORTH); // //�����и�������ť
		if (!ClientEnvironment.isAdmin()) { // ������ǹ���Ա,��Ĭ�ϲ��ɱ༭
			setEditable(false);
		}
		this.setLayout(new BorderLayout()); //
		this.add(contentPanel, BorderLayout.CENTER); //
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean _editable) {
		this.editable = _editable;
		BomItemPanel[] itemPanels = getBomItemPanels();
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setEditable(editable);
		}
	}

	/**
	 * ���÷��յ�
	 * 
	 * @param _htriskVO,�Ǹ���ϣ��,value������RiskVO
	 */
	public void setRiskVO(Hashtable _htriskVO) {
		ht_riskVO = _htriskVO; //
		BomItemPanel[] itemPanels = getBomItemPanels(); //
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setRiskVO(ht_riskVO); //
		}
	}

	public void setRiskVO(Hashtable _htriskVO, String type) {
		ht_riskVO = _htriskVO; //
		BomItemPanel[] itemPanels = getBomItemPanels(); //
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setRiskVO(ht_riskVO, type); //
		}
	}

	/**
	 * ���÷��յ�!
	 */
	public void setRiskVO(String _itemKey, RiskVO _riskVO) {
		if (ht_riskVO == null) {
			ht_riskVO = new Hashtable();
		}
		ht_riskVO.put(_itemKey, _riskVO); //����
		BomItemPanel[] itemPanels = getBomItemPanels(); //
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setRiskVO(ht_riskVO); //
		}
	}
	public void setCanClicked(boolean _canClick){
		BomItemPanel[] itemPanels = getBomItemPanels();
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setCanClicked(_canClick);
		}
	}

	/**
	 * �Ϸ����򵼰�ť
	 * 
	 * @return
	 */
	public JPanel getNorthPanel() {
		if (buttonBarPanel != null) {
			return buttonBarPanel; //
		}
		buttonBarPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT, 0, 2)); //
		buttonBarPanel.setOpaque(false); //͸��!!!

		btn_skipfirst = new WLTButton(UIUtil.getImage("office_038.gif")); //
		btn_skipfirst.setToolTipText("ֱ�ӷ��ص�һ��ҳ��"); //
		btn_skipfirst.addActionListener(this); //

		btn_back = new WLTButton(UIUtil.getImage("office_165.gif")); //
		btn_back.setToolTipText("������һҳ��"); //

		btn_back.setEnabled(false);
		btn_back.addActionListener(this); //

		btn_back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showStackMessage(); //
				}
			}
		});

		buttonBarPanel.add(btn_skipfirst); // ��һ��
		buttonBarPanel.add(btn_back); // ����
		buttonBarPanel.setVisible(false); //Ĭ�������ص�!
		return buttonBarPanel; //
	}

	/**
	 * ����¼�
	 */
	public void onBomItemClicked(final BomItemClickEvent _event) {
		String str_bomCode = _event.getBomcode(); //
		String str_itemKey = _event.getItemkey(); //
		String str_hashKey = str_bomCode + "#" + str_itemKey; //
		//ֱ�ӷ���
		if (str_hashKey.contains("back") || str_hashKey.contains("BACK") || str_hashKey.contains("����") || str_hashKey.contains("����")) {
			onBack();
			return;
		}
		if ("2".equals(_event.getLoadtype())) { //����ǵ������ڷ�ʽ����Ҫ�ȴ���,����Ῠ�ں���!!
			onClickedBomItem(_event);
		} else {
			new SplashWindow(this, new AbstractAction() {
				private static final long serialVersionUID = -287905438900197436L;

				public void actionPerformed(ActionEvent e) {
					onClickedBomItem(_event);
				}
			}, 366, 366);
		}
	}

	/**
	 * �����ÿ��BomItem�е��ȵ�
	 * 
	 * @param _event
	 */
	private void onClickedBomItem(BomItemClickEvent _event) {
		try {
			String str_bindType = _event.getBindtype(); //
			String str_bomCode = _event.getBomcode(); //
			String str_itemKey = _event.getItemkey(); //
			String str_hashKey = str_bomCode + "#" + str_itemKey; //
			if (str_bindType == null || str_bindType.equals("Bomͼ")) { // �����ͼ
				if (_event.getBindbomcode() != null && !_event.getBindbomcode().trim().equals("")) {
					if ("2".equals(_event.getLoadtype())) { //����ǵ�����ʽ,��ʹ��Dialog��,��Ϊ���������ڲ�������Ŀ�������뵯����,������һ��,Ҳ���Ǹ���̬�����!!!��xch/2012-03-09��
						BillBomPanel newBomPanel = new BillBomPanel(_event.getBindbomcode()); //������һ���µ�BillBom,������billBomItemPanel
						if (ht_riskVO != null) {
							newBomPanel.setRiskVO(ht_riskVO); //����ȥ!!
						}
						Point point = new Point(0, 0); //
						Point invokerOrigin = ((Component) _event.getSource()).getLocationOnScreen();
						int mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX() + 5; //��ȡ������Ļ����
						int mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY() + 5;
						//						int mouseX = (int) invokerOrigin.getX()?;
						//						int mouseY = (int) invokerOrigin.getY();
						int width = (int) newBomPanel.getPreferredSize().getWidth(); //��ô�Bom���Ĵ�С��
						int height = (int) newBomPanel.getPreferredSize().getHeight();
						final JWindow win = new JWindow(SwingUtilities.getWindowAncestor(this));
						win.setFocusable(true);
						win.addFocusListener(new FocusAdapter() {
							public void focusLost(FocusEvent e) {
								win.dispose();
							}
						});
						win.setBounds(mouseX, mouseY, width + 10, height + 30);
						newBomPanel.setFocusable(false);
						win.getContentPane().add(newBomPanel); //
						win.setVisible(true);
						//						BillDialog dialog = new BillDialog(this, _event.getBindbomcode(), width + 10, height + 30, mouseX, mouseY); //����ط�Ҫ�������λ���Զ����ô���λ��,����ͼƬ��С�Զ����ô��ڴ�С!
						//						dialog.getContentPane().add(newBomPanel); //
						//						dialog.setVisible(true); //
					} else {
						if ("1".equals(_event.getLoadtype())) {
							stack.push(str_hashKey); // �ڶ�ջ�м���
						} else if ("3".equals(_event.getLoadtype())) { //ֱ���滻
							stack.clear(); // �����һ��Ķ�ջ
							stack.push(str_hashKey); // �ڶ�ջ�м���
							if (hm_Boms.containsKey(str_hashKey)) { //ֱ����ʾ
								cardLayout.show(toftPanel, str_hashKey);
								return;
							} else {//�������(����δѡ������),��ֱ�Ӽ��롾���/2014-12-10��
								stack.push(str_hashKey); // �ڶ�ջ�м���
							}

						}
						BomItemPanel itemPanel = new BomItemPanel(this, _event.getBindbomcode()); // �Ƚ���һ��Bomͼ����
						itemPanel.addBomItemClickListener(this); //
						if (ht_riskVO != null) {
							itemPanel.setRiskVO(ht_riskVO);
						}
						itemPanel.setEditable(this.editable);

						//�������
						JPanel containerPanel = new JPanel(new FlowLayout((isBomimgAlignLeft ? FlowLayout.LEFT : FlowLayout.CENTER), 0, 0)); //Ϊ�˾���,Ūһ������
						containerPanel.setOpaque(false); //͸��!
						containerPanel.add(itemPanel); //
						int li_item_width = (int) itemPanel.getPreferredSize().getWidth(); //
						int li_item_height = (int) itemPanel.getPreferredSize().getHeight(); //
						containerPanel.setPreferredSize(new Dimension(li_item_width, li_item_height)); //Ҫ���С,����û����!!�����һ����Ȼ��bomͼ���������������߶ȡ�����ֹ�������[����2012-12-04]
						//������
						I_MyJScrollPane scroll = new I_MyJScrollPane(); //
						scroll.setOpaque(false); //͸��!
						scroll.getViewport().setOpaque(false); //͸��!
						scroll.getViewport().add(containerPanel); //�ڹ������м����������
						scroll.setBorder(BorderFactory.createEmptyBorder());
						if ("1".equals(scrolltype)) {//bomͼ���������ã�1-ȫ���ɹ�����2-������ɹ�����3-������ɹ�����4-ȫ�����ɹ���
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						} else if ("2".equals(scrolltype)) {
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						} else if ("3".equals(scrolltype)) {
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						} else if ("4".equals(scrolltype)) {
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						}

						hm_Boms.put(str_hashKey, itemPanel); //
						itemPanel.putClientProperty("parentname", _event.getItemname());
						toftPanel.add(str_hashKey, scroll); //
						cardLayout.show(toftPanel, str_hashKey); // ����Ѿ��������ͼ
						btn_back.setEnabled(true);
						getNorthPanel().setVisible(showNorthPanel); //
					}
				} else {
					MessageBox.show(this, "��Ӧ������ͼΪ��!!"); //
				}
			} else if (_event.getBindtype().equals("Class��")) { // �������
				JPanel panel = null;
				if (_event.getBindclass() != null && !_event.getBindclass().trim().equals("")) {
					stack.push(str_hashKey); // �ڶ�ջ�м���
					String str_className = _event.getBindclass(); //
					if (str_className.indexOf("(") > 0) {
						String str_csname = str_className.substring(0, str_className.indexOf("(")); //
						String str_par = str_className.substring(str_className.indexOf("(") + 1, str_className.lastIndexOf(")")); //�������п�����С���ţ���������ȡ���һ�������š����/2012-03-16��
						str_par = tbutil.replaceAll(str_par, "\"", ""); //
						String[] str_pars = tbutil.split(str_par, ","); //
						//��һ�ֹ������
						Class cp_1[] = new Class[str_pars.length];
						for (int i = 0; i < cp_1.length; i++) {
							cp_1[i] = java.lang.String.class;
						}
						//��2�ֹ������
						Class cp_2[] = new Class[str_pars.length + 1];
						for (int i = 0; i < cp_2.length; i++) {
							cp_2[i] = java.lang.String.class;
						}
						cp_2[cp_2.length - 1] = BillBomPanel.class; //
						Class panelclass = Class.forName(str_csname); //

						try { //����ʹ�õڶ��ֹ��췽��,��ֱ���ڹ��췽���д���BillBomPanel
							Constructor constructor = panelclass.getConstructor(cp_2); //
							Object[] obj_par2 = new Object[str_pars.length + 1];
							for (int i = 0; i < str_pars.length; i++) {
								obj_par2[i] = str_pars[i];
							}
							obj_par2[obj_par2.length - 1] = this; //
							Object oo = constructor.newInstance(obj_par2);
							if (!(oo instanceof JPanel)) {//����ϣ���Լ�ʵ��һ���߼�/sunfujun/20121203
								panelclass.getMethod("invokeMe", this.getClass(), BomItemClickEvent.class).invoke(oo, this, _event);
								return;
							}

							panel = (JPanel) oo; //
						} catch (NoSuchMethodException exx) { //���û����������췽��,��ʹ��
							Constructor constructor = panelclass.getConstructor(cp_1); //
							Object oo = constructor.newInstance(str_pars);
							if (!(oo instanceof JPanel)) {//����ϣ���Լ�ʵ��һ���߼�/sunfujun/20121203
								panelclass.getMethod("invokeMe", this.getClass(), BomItemClickEvent.class).invoke(oo, this, _event);
								return;
							}
							panel = (JPanel) oo; //
							if (panel instanceof DefaultBillBomHrefPanel) {
								DefaultBillBomHrefPanel bomHref = (DefaultBillBomHrefPanel) panel; //
								bomHref.setBillBomPanel(this); //
							}
						}
					} else {
						Object clas = Class.forName(str_className).newInstance();//ֱ�Ӵ���[����2012-12-04]
						if (clas instanceof JPanel) {
							panel = (JPanel) clas;
						} else {
							Class.forName(str_className).getMethod("invokeMe", this.getClass(), BomItemClickEvent.class).invoke(clas, this, _event);
							return;
						}
					}
					if ("2".equals(_event.getLoadtype())) { //����ǵ�����ʽ,��ʹ��Dialog��,��Ϊ���������ڲ�������Ŀ�������뵯����,������һ��,Ҳ���Ǹ���̬�����!!!��xch/2012-03-09��
						int width = (int) panel.getPreferredSize().getWidth(); //��ô����Ĵ�С
						int height = (int) panel.getPreferredSize().getHeight();
						BillDialog dialog = new BillDialog(this, str_hashKey, width + 10, height + 30); //
						dialog.getContentPane().add(panel); //
						dialog.setVisible(true); //
					} else {
						//�������������bomͼ���������bomͼ���ٽ��붨��Ĳ˵���û������ģ���������bomͼ�ٽ�����bomͼ�ͱ�ArrayStoreException�쳣�ˣ�
						//ԭ���Ǻ����õ�getBomItemPanels()������hm_Boms.values().toArray(new BomItemPanel[0]),��hm_Boms�д洢�Ĳ�������BomItemPanel����������������Jpanel����ע�͵���䡾���/2012-03-15��
						hm_Boms.put(str_hashKey, panel);
						toftPanel.add(str_hashKey, panel); // //
						cardLayout.show(toftPanel, str_hashKey); // ///
						btn_back.setEnabled(true);
						getNorthPanel().setVisible(showNorthPanel); //
					}
				} else {
					MessageBox.show(this, "��Ӧ��ClassΪ��!!"); //
					return;
				}
			} else if (_event.getBindtype().equals("�˵�")) { // ����ǲ˵�,�˵���Ϊ����һ��ҳǩ�򿪣����Բ��ü����ջ
				String str_menuid = _event.getBindmenu(); //�󶨵Ĳ˵�id  
				int li_menutype = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOneMenuType(str_menuid); //
				if (li_menutype < 0) {
					MessageBox.show(this, "��û��Ȩ�޷��ʶ�Ӧ�Ĺ��ܵ�!"); //
					return; //
				}
				if (li_menutype == 1) { //Ҷ�ӽ��,����ҳǩ�д򿪣�����Ҫ�����ջ!!
					if ("2".equals(_event.getLoadtype())) { //����ǵ�����ʽ����ͬ˫���˵���
						DeskTopPanel.getDeskTopPanel().openAppMainFrameWindowById(str_menuid); //�����/2015-04-10��
					} else {
						stack.push(str_hashKey); // �ڶ�ջ�м���
						DeskTopPanel.getDeskTopPanel().expandTopBtnPane(false);
						HashMap map = new HashMap();
						map.put("label_name", _event.getItemname());
						map.put("label_key", _event.getItemkey());
						map.put("bindclassname", _event.getBindclass());
						final WorkTabbedPanel panel = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOpenAppMainPanel(str_menuid, map);
						//					WLTButton btn = panel.getBtn_closeMe();
						//					if (btn != null) {
						//						btn.addCustActionListener(new ActionListener() {
						//							public void actionPerformed(ActionEvent e) {
						//								onBack();
						//								DeskTopPanel.getDeskTopPanel().getNewDeskTopTabPane().expandPane();
						//								System.gc();
						//							}
						//						});
						//					}
						JComponent closeBtn = panel.getBtn_closeMe();
						closeBtn.addMouseListener(new MouseAdapter() {
							public void mouseReleased(MouseEvent e) {
								JComponent btn = (JComponent) e.getSource();
								if (!(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
									return;
								}
								panel.getWorkPanel().beforeDispose();
								onBack();
								DeskTopPanel.getDeskTopPanel().expandTopBtnPane(true);
								System.gc();
							}
						});
						panel.putClientProperty("parentname", _event.getItemname()); //������ܼ��뵽hm_Boms������
						toftPanel.add(str_hashKey, panel); //
						cardLayout.show(toftPanel, str_hashKey); //
						hm_Boms.put(str_hashKey, panel);
						StringBuffer title_sb = new StringBuffer("��ǰλ��:");
						for (int i = 0; i < stack.size(); i++) {
							JComponent bompanel = (JComponent) hm_Boms.get(stack.get(i));
							if (bompanel != null) {
								String titlename = (String) bompanel.getClientProperty("parentname");
								if (titlename == null || titlename.trim().equals("")) {//�ж�һ�£����Ϊ�գ���ȡ���롾���/2013-02-26��
									if (bompanel instanceof BomItemPanel) {
										titlename = ((BomItemPanel) bompanel).getBomcode();
									}
								}
								title_sb.append(titlename + " > ");
							}
						}
						if (stack.size() > 0) {
							title_sb = new StringBuffer(title_sb.substring(0, title_sb.length() - 3));
							panel.setTitle(title_sb.toString());
						}
					}
				} else { //Ŀ¼���!!
					stack.push(str_hashKey); // �ڶ�ջ�м���
					//					HashVO[] hvs = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.findOneNodeAllChildNOdeVOs(str_menuid); //�ҳ���ģ���µ����й��ܽ��!!!
					//					cn.com.infostrategy.ui.sysapp.login.IndexShortCutPanel shortCutPanel = new cn.com.infostrategy.ui.sysapp.login.IndexShortCutPanel(hvs, false); //
					JPanel jp = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getIBarMenuAndWorkPanel(str_menuid, this);
					//�������������bomͼ���������bomͼ���ٽ��붨��Ĳ˵���û������ģ���������bomͼ�ٽ�����bomͼ�ͱ�ArrayStoreException�쳣�ˣ�
					//ԭ���Ǻ����õ�getBomItemPanels()������hm_Boms.values().toArray(new BomItemPanel[0]),��hm_Boms�д洢�Ĳ�������BomItemPanel����������������IndexShortCutPanel����ע�͵���䡾���/2012-03-15��
					hm_Boms.put(str_hashKey, jp); // //
					toftPanel.add(str_hashKey, jp); //
					cardLayout.show(toftPanel, str_hashKey); //
					btn_back.setEnabled(true); //
					getNorthPanel().setVisible(showNorthPanel); //
					DeskTopPanel.getDeskTopPanel().expandTopBtnPane(false);
				}
			}

			/**
			 * �ٵ������м����ߵķ���..
			 */
			for (int i = 0; i < v_billbomlicklistener.size(); i++) {
				BillBomClickedListener listener = (BillBomClickedListener) v_billbomlicklistener.get(i); //
				BillBomClickEvent event = new BillBomClickEvent(_event.getBomcode(), _event.getItemkey(), _event.getItemname(), _event.getBindbomcode(), this); //
				listener.onBillBomClicked(event); //
			}
		} catch (Exception _ex) {
			if (_ex instanceof java.lang.reflect.InvocationTargetException) {
				InvocationTargetException targetEx = (InvocationTargetException) _ex;
				MessageBox.showException(this, targetEx.getTargetException()); //
			} else {
				MessageBox.showException(this, _ex); //
			}
		}
	}

	public void addBillBomClickListener(BillBomClickedListener _listener) {
		v_billbomlicklistener.add(_listener); //
	}

	
	/**
	 * ��ָ�����ӵ��㲼������Ժ���
	 * @param key
	 * @param jp
	 */
	public void addToCardLayout(String key, JPanel jp) {
		if (hm_Boms.containsKey(key)) { //��ֻ֤��һ��
			cardLayout.show(toftPanel, key);
		} else {
			hm_Boms.put(key, jp);
			stack.push(key);
			toftPanel.add(key, jp);
			cardLayout.show(toftPanel, key);
			btn_back.setEnabled(true);
			getNorthPanel().setVisible(false); // 2013-3-12�������޸�
		}
	}

	/**
	 * ȡ��ĳһ��Bom���
	 * 
	 * @param _code
	 */
	public BomItemPanel getBomItemPanel(String _code) {
		return (BomItemPanel) hm_Boms.get(_code);
	}

	public BomItemPanel[] getBomItemPanels() {
		JComponent[] component = (JComponent[]) hm_Boms.values().toArray(new JComponent[0]);
		ArrayList l = new ArrayList();
		for (int i = 0; i < component.length; i++) {
			if (component[i] instanceof BomItemPanel) {
				l.add((BomItemPanel) component[i]);
			}
		}
		return (BomItemPanel[]) l.toArray(new BomItemPanel[0]); //
	}

	public JComponent[] getCardPanelAllComponent() {
		JComponent[] component = (JComponent[]) hm_Boms.values().toArray(new JComponent[0]);
		return component;
	}

	/*
	 * �õ���ǰ�����
	 */

	/**
	 * ȡ�ö�ջ�еĸ���һ·�������itemkey!!��ʵ���ǴӶ�ջ��ȡ��ֵ��xch/2012-03-07��
	 * @return
	 */
	public String[] getClickPath() {
		ArrayList al_return = new ArrayList(); //
		for (int i = 1; i < stack.size(); i++) { //����!!!��һ��ȥ��,��Ϊ��һ�������ȵ���!!���ǵ�һ��ͼ��bomcode!��ֻ�н���ڶ����Ż�������·���ĸ��xch/2012-03-07��
			al_return.add(stack.get(i)); //
		}
		return (String[]) al_return.toArray(new String[0]); //
	}

	private void showStackMessage() {
		String str_msg = ""; //
		for (int i = 0; i < stack.size(); i++) {
			str_msg = str_msg + stack.get(i) + "\n"; //
		}
		MessageBox.showTextArea(this, str_msg); //
	}

	public void setCurrItemPanelEdit() {
		if (stack.isEmpty()) {
			return; //
		}
		String str_currrCode = (String) stack.peek(); //
		BomItemPanel bomItemPanel = getBomItemPanel(str_currrCode); //
		bomItemPanel.onSetEditState(); //�༭
	}

	/**
	 * ����
	 */
	public void onBack() {
		if (!stack.isEmpty()) {
			String str_itemlevel = null;
			if (stack.size() == 1) { //���ֻʣһ����ֻȡ��ɾ
				str_itemlevel = (String) stack.peek(); //
			} else {
				hm_Boms.remove((String) stack.peek());
				stack.pop(); // ��ȡһ��
				str_itemlevel = (String) stack.peek(); //
			}
			cardLayout.show(toftPanel, str_itemlevel); // ����Ѿ��������ͼ
		}

		if (stack.size() == 1) { // ���ֻʣһ����ֻȡ��ɾ
			btn_back.setEnabled(false); //
			getNorthPanel().setVisible(false); //���ֻʣһ��,���һ�㲻��ʾ���������!��xch/2012-04-10��
		} else {
			getNorthPanel().setVisible(showNorthPanel); //
		}

	}

	/**
	 * ����...
	 */
	public void reSet() {
		onSkipToFirst(); // ���þ͵���������ҳ
	}

	/**
	 * ������һ��
	 */
	private void onSkipToFirst() {
		cardLayout.show(toftPanel, str_firstitemname); // ��ʾ��һ��
		stack.clear(); // ���
		stack.push(str_firstitemname); //
		btn_back.setEnabled(false); //
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_skipfirst) {
			onSkipToFirst(); //
		} else if (e.getSource() == btn_back) {
			onBack(); //
		}
	}

}
