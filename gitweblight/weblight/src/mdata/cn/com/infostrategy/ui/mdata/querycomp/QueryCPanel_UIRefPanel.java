/**************************************************************************
 * $RCSfile: QueryCPanel_UIRefPanel.java,v $  $Revision: 1.25 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryEditEvent;
import cn.com.infostrategy.ui.mdata.BillQueryEditListener;
import cn.com.infostrategy.ui.mdata.MetaDataUIUtil;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * ��ѯ���е����ڿؼ�!
 * @author xch
 *
 */
public class QueryCPanel_UIRefPanel extends AbstractWLTCompentPanel {
	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String key = null;
	private String name = null;
	private String itemtype = null; //
	private String refdesc = null; //����˵��

	private Vector v_Listeners = new Vector(); //
	private int li_label_width = 120;
	private int li_cardheight = 20;
	private RefItemVO currRefItemVO = null;
	private JLabel label = null;
	private JTextField textField = null;
	protected JButton btn_ref = null; //���հ�ť

	protected int li_width_all = 150; //ֻ�п�Ƭ�����ܿ���еĸ���,�б�������BorderLayout
	private AbstractRefDialog refDialog = null; //
	private TBUtil tBUtil = null; //ת������!!
	public QueryCPanel_UIRefPanel(Pub_Templet_1_ItemVO _templetVO, BillPanel billPanel, RefItemVO _initRefItemVO) { //
		super();
		this.currRefItemVO = _initRefItemVO;
		this.templetItemVO = _templetVO;
		setBillPanel(billPanel); //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		if (templetItemVO.getQueryItemType() != null && !templetItemVO.getQueryItemType().trim().equals("")) { //��������˲�ѯ��Ķ���
			this.itemtype = templetItemVO.getQueryItemType(); //
			this.refdesc = templetItemVO.getQueryItemDefine(); //
		} else {
			this.itemtype = templetItemVO.getItemtype(); //
			this.refdesc = templetItemVO.getRefdesc(); //
		}
		if (templetItemVO.getQueryUCDfVO() == null) { //���û�ж����ѯ��,��ֱ��ʹ�ÿؼ��������!!!
			templetItemVO.setQueryUCDfVO(templetItemVO.getUCDfVO()); //
		}
		if (templetItemVO.getQuerylabelwidth() != null) {
			this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //����˵���Ŀ��
		}
		if (templetItemVO.getQuerycompentwidth() != null) {
			this.li_width_all = templetItemVO.getQuerycompentwidth().intValue(); //��ѯ�ؼ����
		}
		if (templetItemVO.getQuerycompentheight() != null) {
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // ���ø߶�
		}
		initialize();
	}

	/**
	 * ��ʼ��ҳ�沼��,��Ƭ���о����Ĳ��������ʵ��Ҫ���������һ��!!!!
	 * ��Ƭ�еĲ���������������,һ����label��ǩ,һ�����ı���,���б�����û��Label��ǩ!
	 */
	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.systembgcolor); //

		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //���ø����ṩ�ķ�������Label
			if (templetItemVO.getIsmustinput()) {//�����Ƭ����Ϊ���������Ҫ�жϲ�ѯ������Ƿ�Ϊ��������/2012-03-19��
				if (!"Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//�����ѯ����в��Ǳ��������Ҫ���������"*"��ȥ��
					label.setText(label.getText().replace("*", ""));
				}
			} else if ("Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//�����Ƭ����Ϊ�Ǳ��������ѯ������Ǳ��������Ҫ����ϱ������"*"��
				label.setText("*" + label.getText());
				((WLTLabel) label).addStrItemColor("*", Color.RED); //
			}
		} else {
			label = new JLabel(name); //	
		}

		label.setBackground(LookAndFeel.systembgcolor);
		label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //���ÿ�Ⱥ͸߶�

		textField = new JTextField();
		addComponentUndAndRedoFunc(textField); // add Redo and Undo function
		textField.setForeground(LookAndFeel.systemLabelFontcolor); //
		textField.setEditable(false); //
		textField.setBackground(LookAndFeel.inputbgcolor_enable); //��Ϊ�ǲ��ɱ༭��,�����ǻ�ɫ��,����ҵ�ͻ����Ҫ�׵�,���Ը��˸��ӽ��׵�(xch)
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //�Զ���UI���Ѿ�ȥ��border��
			textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		textField.setPreferredSize(new Dimension(li_width_all - 19, li_cardheight)); //���ÿ��

		btn_ref = new JButton(UIUtil.getImage(getIconName(this.itemtype))); //��ͬ�Ŀؼ�ʹ�ò�ͬ�ı��!!!  
		btn_ref.setToolTipText("����Ҽ��������"); //
		btn_ref.setRequestFocusEnabled(false); //
		btn_ref.setPreferredSize(new Dimension(19, li_cardheight)); // ��Ť�Ŀ����߶�

		if (getTBUtil().getSysOptionBooleanValue("���ڿؼ��Ƿ����Ĭ��ֵ", false)) { //�����ؼ�׷��Ĭ��ֵ �����/2012-08-27��
			CommUCDefineVO vo = templetItemVO.getQueryUCDfVO();
			if (!(vo != null && vo.getConfValue("�Ƿ����Ĭ��ֵ") != null && vo.getConfValue("�Ƿ����Ĭ��ֵ").equals("false"))) {
				//��������ڿؼ�,��ǿ�б���Ĭ��ֵ,����ǰ����!��Ʋ���(xch/2012-08-27)
				if (itemtype.equals(WLTConstants.COMP_DATE) || itemtype.equals(WLTConstants.COMP_DATETIME)) {

					String str_curdate = tBUtil.getCurrDate();
					String str_begindate = tBUtil.getBeginDateByMonth(str_curdate);
					String str_enddate = tBUtil.getEndDateByMonth(str_curdate);
					String str_currmonth = str_curdate.substring(0, 4) + "��" + str_curdate.substring(5, 7) + "��;";

					//�����Ǳ���,�򱾼���,���ȡֵ���Բο�RefDialog_QueryDateTime,CommonDateQueryPanel��������
					if (templetItemVO.getQueryUCDfVO() != null) { //��ѯ����
						String str_frontmonth = templetItemVO.getQueryUCDfVO().getConfValue("ǰ�Ƽ�����"); //
						if (str_frontmonth != null) { //ʱ�䷶Χ�Զ����ǰ5����������
							//System.out.println("ǰ���·�:" + str_frontmonth); //
							try {
								String backmonth = tBUtil.getBackMonth(str_curdate, Integer.parseInt(str_frontmonth));
								if (!(backmonth.substring(0, 7)).equals(str_curdate.substring(0, 7))) {
									str_begindate = tBUtil.getBeginDateByMonth(backmonth);
									str_currmonth = backmonth.substring(0, 4) + "��" + backmonth.substring(5, 7) + "��" + "-" + str_curdate.substring(0, 4) + "��" + str_curdate.substring(5, 7) + "��";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					//SQL��������ʾֵĬ���Ǳ���,���������ǰ��,���Զ���ǰ5����,��ʾҲ���Ըĳ��ǡ�2011��11��-2012��5�¡�
					HashVO hvo = new HashVO(); //
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_begindate + "' and {itemkey}<='" + str_enddate + " 24:00:00'))"); //��ô������Ĳο�CommonDateQueryPanel,��һЩ�ؼ������Ƶ�TBUtil��!

					//RefItemVO refVO = new RefItemVO("2012��08��;", null, "2012��08��;", hvo);
					RefItemVO refVO = new RefItemVO(str_currmonth, null, str_currmonth, hvo);
					setObject(refVO); //����Ĭ��ֵ!
				}
			}
		}
		//Ԭ����  20130515 ���  ��Ҫ������ո�����Ĭ��ֵ����������Ĭ��ֵ���ɷ�Ϊ�ꡢ�¡��졢��
		if (itemtype.equals(WLTConstants.COMP_DATE) || itemtype.equals(WLTConstants.COMP_DATETIME)) {
			CommUCDefineVO vouc = templetItemVO.getQueryUCDfVO();
			if (vouc != null && vouc.getConfValue("��ѯʱ����Ĭ��ֵ����") != null && !vouc.getConfValue("��ѯʱ����Ĭ��ֵ����").equals("")){
				String dateType=vouc.getConfValue("��ѯʱ����Ĭ��ֵ����");
				String str_curdate = tBUtil.getCurrDate();
				String str_currmonth = str_curdate.substring(0, 4) + "��" + str_curdate.substring(5, 7) + "��;";
				String str_curryear = str_curdate.substring(0, 4) + "��;" ;
				String str_currseason = tBUtil.getCurrDateSeason() + ";" ;
				HashVO hvo = new HashVO(); //
				RefItemVO refVO = null;
				if(dateType.equals("��")){
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_curdate.substring(0, 4) + "-01-01" + "' and {itemkey}<='" + str_curdate.substring(0, 4) + "-12-31" + " 24:00:00'))"); //��ô������Ĳο�CommonDateQueryPanel,��һЩ�ؼ������Ƶ�TBUtil��!
					refVO=new RefItemVO(str_curryear, null, str_curryear, hvo);
				}else if(dateType.equals("��")){
					String str_begindate = tBUtil.getBeginDateByMonth(str_curdate);
					String str_enddate = tBUtil.getEndDateByMonth(str_curdate);
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_begindate + "' and {itemkey}<='" + str_enddate + " 24:00:00'))"); //��ô������Ĳο�CommonDateQueryPanel,��һЩ�ؼ������Ƶ�TBUtil��!
					refVO=new RefItemVO(str_currmonth, null, str_currmonth, hvo);
				}else if(dateType.equals("��")){
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_curdate + " 00:00:00' and {itemkey}<='" + str_curdate + " 24:00:00'))"); //��ô������Ĳο�CommonDateQueryPanel,��һЩ�ؼ������Ƶ�TBUtil��!
					refVO=new RefItemVO(str_curdate+";", null, str_curdate+";", hvo);
				}else if(dateType.equals("��")){
					String str_year = str_currseason.substring(0, 4); //
					String str_season = str_currseason.substring(5, 6); //
					String firstday="";
					String lastday="";
					if (str_season.equals("1")) {
						firstday= str_year + "-01-01"; //
						lastday= str_year + "-03-31"; //
					} else if (str_season.equals("2")) {
						firstday= str_year + "-04-01"; //
						lastday= str_year + "-06-30"; //
					} else if (str_season.equals("3")) {
						firstday= str_year + "-07-01"; //
						lastday= str_year + "-09-30"; //
					} else if (str_season.equals("4")) {
						firstday= str_year + "-10-01"; //
						lastday= str_year + "-12-31"; //
					} else {
						firstday= str_year + "-01-01"; //
						lastday= str_year + "-12-31"; //
					}
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + firstday + "' and {itemkey}<='" + lastday + " 24:00:00'))"); //��ô������Ĳο�CommonDateQueryPanel,��һЩ�ؼ������Ƶ�TBUtil��!
					refVO=new RefItemVO(str_currseason, null, str_currseason, hvo);
				}
				setObject(refVO); //����Ĭ��ֵ!
			}
		}
		
		

		btn_ref.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getID() != ActionEvent.SHIFT_MASK) {
					textField.putClientProperty("JTextField.DrawFocusBorder", "Y");
					textField.requestFocus();
					onButtonClicked();
					textField.putClientProperty("JTextField.DrawFocusBorder", null);
				}
			}
		});

		btn_ref.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					if (e.isShiftDown()) { //�������Shift��!!
						showRefMsg(); //��ʾ������Ϣ
					} else { //��������õ�
						if (((JButton) e.getSource()).isEnabled()) {
							reset(); //
							textField.requestFocus();
						}
					}
				}
			}

		}); //

		textField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTextField clickedTextField = ((JTextField) e.getSource()); //
					String str_text = clickedTextField.getText();
					if (str_text != null && !str_text.equals("")) { //�������Ϊ�գ��Ͳ�����������
						str_text = getTBUtil().replaceAll(str_text, ";", ";\n"); //��ǰ��һ��,�Եú���!��ҵ�ͻ�ʯ����Ǳ�Թ!����Ҫ������!!��xch/2012-04-25��
						MessageBox.showTextArea(clickedTextField, str_text); //
					}
				}
			}
		});
		btn_ref.putClientProperty("JButton.RefTextField", textField);
		textField.putClientProperty("JTextField.OverWidth", (int) btn_ref.getPreferredSize().getWidth());
		this.add(label); //
		this.add(textField); //
		this.add(btn_ref); //

		li_width_all = (int) (label.getPreferredSize().getWidth() + textField.getPreferredSize().getWidth() + btn_ref.getPreferredSize().getWidth()); //�ܿ��
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
	}

	/**
	 * �����ť!!!
	 */
	protected void onButtonClicked() {
		refDialog = null; //
		try {
			CommUCDefineVO queryCommUCDfVO = this.templetItemVO.getQueryUCDfVO(); //��ѯ�����ݶ���!!
			if (this.itemtype.equals(WLTConstants.COMP_REFPANEL) || this.itemtype.equals(WLTConstants.COMP_REFPANEL_TREE) || this.itemtype.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
					this.itemtype.equals(WLTConstants.COMP_REFPANEL_CUST) || this.itemtype.equals(WLTConstants.COMP_REFPANEL_REGEDIT)) { //
				if (this.refdesc == null || this.refdesc.trim().equalsIgnoreCase("null") || this.refdesc.trim().equals("")) {
					MessageBox.show(this, "û�ж������˵��,����!"); //
					return;
				}
				if (queryCommUCDfVO == null) { //����ؼ������ж���!!!
					MessageBox.show(this, "�������幫ʽ��\r\n" + this.refdesc + "\r\n��ʱʧ��,CommUCDefineVO����Ϊnull,���鶨���﷨!!\r\n��������:��ע���Ƿ���˻����˶���,˫����֮���(���������һ�����������׶������)..."); //
					return;
				}
			}
			if (queryCommUCDfVO == null) {
				queryCommUCDfVO = new CommUCDefineVO(this.itemtype); //
			}

			//��commUCDfVO��¡һ��,Ȼ���滻һЩ�ؼ������SQL����е�����!!��Ϊ�����ĳ�����BS�˽�����ʽ,�����޷�ȡ��getItemValue()���ֹ�ʽ��ֵ,��ֻ���ڷ������˽��к����ת��!!!
			queryCommUCDfVO = new MetaDataUIUtil().cloneCommUCDefineVO(queryCommUCDfVO, this.billPanel); //֮��Ҫ��¡,����Ϊһ���滻��,�ڶ��ξͲ������滻��,�Ӷ��ﲻ����Ҫʵ�ֵ��߼���!!//�Ժ���UI�˿����й�ʽ����̬�޸Ķ����е�ֵ,����SQL,�������¡,ֱ���޸�ԭ����,�����ɵڶ���ִ��ʱ�߼�����!!!
			if (queryCommUCDfVO == null) {
				return; //
			}

			if (itemtype.equals(WLTConstants.COMP_REFPANEL)) { //���β���
				refDialog = new RefDialog_QueryTable(this, this.name, this.currRefItemVO, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_TREE)) { //���Ͳ���
				refDialog = new RefDialog_QueryTree(this, this.name, null, this.billPanel, queryCommUCDfVO);
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_MULTI)) { //��ѡ����
				refDialog = new RefDialog_QueryMulti(this, this.name, null, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_CUST)) { //�Զ������,�Ƚϸ��ӵ�			
				refDialog = getCustRefDialog(queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET)) { //�б�ģ�����
				refDialog = new RefDialog_QueryTableModel(this, this.name, null, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) { //����ģ�����
				refDialog = new RefDialog_QueryTreeModel(this, this.name, this.currRefItemVO, this.billPanel, queryCommUCDfVO); //����currRefItemVO ����ģ����յķ���ѡ�� �����/2012-08-31��
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_REGFORMAT)) { //ע���������
				refDialog = new RefDialog_QueryRegFormat(this, this.name, null, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_DATE)) { //����
				refDialog = new RefDialog_QueryDateTime(this, this.name, null, null, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_DATETIME)) { //ʱ��
				refDialog = new RefDialog_QueryDateTime(this, this.name, null, null, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_NUMBERFIELD)) { //���ֿ�
				refDialog = new RefDialog_QueryNumber(this, this.name, null, null); //
			}

			refDialog.initialize(); //��ʼ������
			refDialog.setVisible(true); //�򿪴���
			textField.requestFocus();
			boolean bo_ifdataChanged = false;
			if (refDialog.getCloseType() == BillDialog.CONFIRM) { //�����ȷ������
				RefItemVO returnVO = refDialog.getReturnRefItemVO();
				textField.setText(returnVO.getName()); //
				bo_ifdataChanged = ifChanged(returnVO, this.currRefItemVO); //���緵��ֵ�뵱ǰֵ�Ƿ�һ��!
				setObject(returnVO); //���õ�ǰֵ,���޸Ŀؼ��ı����е�ֵ!!
				if (bo_ifdataChanged) { //����Ƿ����仯,���޸ĵ�ǰֵ
					onBillQueryValueChanged(new BillQueryEditEvent(this.key, this.getObject(), this)); //����ǿ�Ƭ,�򴥷��¼�!!
				}
			} else {
				bo_ifdataChanged = false; //
			}

			if (currRefItemVO != null) {
				currRefItemVO.setValueChanged(bo_ifdataChanged); //�������ݷ����仯
			}

			refDialog = null; //Ϊ��
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			refDialog = null;
			Runtime.getRuntime().gc(); //
		}
	}

	//�����Զ������VO��������!!!
	private AbstractRefDialog getCustRefDialog(CommUCDefineVO _dfvo) throws Exception {
		String str_clsName = _dfvo.getConfValue("�Զ�������"); //
		String[] str_paras = _dfvo.getAllConfKeys("����", true); //ȡ���в���!!
		if (str_paras.length == 0) { //���û�в���!!
			Class dialog_class = Class.forName(str_clsName);
			Class cp[] = { java.awt.Container.class, String.class, RefItemVO.class, BillPanel.class }; //
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(new Object[] { this, this.name, this.currRefItemVO, this.billPanel }); //
		} else { //����в���!!
			Class cp[] = new Class[4 + str_paras.length]; //ǰ4�������ǹ̶���,����ĵ���!!
			cp[0] = java.awt.Container.class;
			cp[1] = String.class;
			cp[2] = RefItemVO.class;
			cp[3] = BillPanel.class;
			for (int i = 4; i < cp.length; i++) {
				cp[i] = java.lang.String.class;
			}
			Object[] ob = new Object[4 + str_paras.length];
			ob[0] = this;
			ob[1] = this.name;
			ob[2] = this.currRefItemVO;
			ob[3] = this.billPanel;
			for (int j = 0; j < str_paras.length; j++) {
				ob[4 + j] = _dfvo.getConfValue(str_paras[j]); //���Զ������ֵ����ȥ
			}
			Class dialog_class = Class.forName(str_clsName);
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(ob); //
		}
	}

	/**
	 * ���ݲ�������,ȡ�ò���ͼƬ
	 * @param _type
	 * @return
	 */
	private String getIconName(String _type) {
		if (_type.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
				_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���
				_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //��ѡ����
				_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
				_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) //ע�����
		) {
			return getTBUtil().getSysOptionStringValue("Ĭ�ϲ���ͼ������", "refsearch.gif"); //zt_012.gif,��������Ŀ����Ϊ��ν��UI��׼,�����������ǵ�ͼ��,����ֻ�ܸ�ɻ��!!!
		} else if (_type.equals(WLTConstants.COMP_DATE)) { //����
			return getTBUtil().getSysOptionStringValue("���ڲ���ͼ������", "date.gif"); //zt_030.gif/zt_075.gif,��������Ŀ����Ϊ��ν��UI��׼,�����������ǵ�ͼ��,����ֻ�ܸ�ɻ��!!!
		} else if (_type.equals(WLTConstants.COMP_DATETIME)) { //ʱ��
			return "time.gif";
		} else if (_type.equals(WLTConstants.COMP_BIGAREA)) { //���ı���
			return "bigtextarea.gif";
		} else if (_type.equals(WLTConstants.COMP_FILECHOOSE)) { //�ļ�ѡ���
			return "filepath.gif";
		} else if (_type.equals(WLTConstants.COMP_COLOR)) { //��ɫѡ���
			return "colorchoose.gif";
		} else if (_type.equals(WLTConstants.COMP_CALCULATE)) { //������
			return "office_004.GIF";
		} else if (_type.equals(WLTConstants.COMP_PICTURE)) { //ͼƬѡ���
			return "pic2.gif";
		} else if (_type.equals(WLTConstants.COMP_NUMBERFIELD)) { //���ֿ�
			return "office_058.gif";
		} else {
			return getTBUtil().getSysOptionStringValue("Ĭ�ϲ���ͼ������", "refsearch.gif"); //zt_012.gif,��������Ŀ����Ϊ��ν��UI��׼,�����������ǵ�ͼ��,����ֻ�ܸ�ɻ��!!!
		}
	}

	public int getAllWidth() {
		return li_width_all;
	}

	@Override
	public void focus() {
		textField.requestFocus(); //
		textField.requestFocusInWindow(); //
	}

	@Override
	public String getItemKey() {
		return key;
	}

	@Override
	public String getItemName() {
		return name;
	}

	@Override
	public JLabel getLabel() {
		return this.label;
	}

	private boolean ifChanged(RefItemVO returnVO, RefItemVO _currVO) {
		if (returnVO == null) {
			if (_currVO == null) {
				return false; //����û�б仯
			} else {
				return true;
			}
		} else {
			if (_currVO == null) {
				return true;
			} else {
				if (returnVO.getId().equals(_currVO.getId())) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	public void addBillQueryEditListener(BillQueryEditListener _listener) {
		v_Listeners.add(_listener);
	}

	public void onBillQueryValueChanged(BillQueryEditEvent _evt) {
		for (int i = 0; i < v_Listeners.size(); i++) {
			BillQueryEditListener listener = (BillQueryEditListener) v_Listeners.get(i);
			listener.onBillQueryValueChanged(_evt); //
		}
	}

	@Override
	public Object getObject() {
		return currRefItemVO; //
	}

	@Override
	public String getValue() {
		if (currRefItemVO == null) {
			return null;
		}
		return currRefItemVO.getId();
	}

	@Override
	public void setValue(String _value) {
		if (currRefItemVO == null) {
			currRefItemVO = new RefItemVO(_value, _value, _value);
			this.textField.setText(_value); //
		} else {
			currRefItemVO.setId(_value);
		}
	}

	@Override
	public void reset() {
		currRefItemVO = null; //
		this.textField.setText(""); //����ı���
	}

	public void showRefMsg() {
		StringBuffer sb_info = new StringBuffer(); //
		if (currRefItemVO == null) {
			sb_info.append("��ǰֵΪnull"); //
		} else {
			sb_info.append("RefID=[" + currRefItemVO.getId() + "]\r\n");
			sb_info.append("RefCode=[" + currRefItemVO.getCode() + "]\r\n");
			sb_info.append("RefName=[" + currRefItemVO.getName() + "]\r\n");

			sb_info.append("\r\n----------- HashVO���� -------------\r\n");

			if (currRefItemVO.getHashVO() == null) {
				sb_info.append("HaVOΪ��\r\n");
			} else {
				String[] keys = currRefItemVO.getHashVO().getKeys(); //
				for (int i = 0; i < keys.length; i++) {
					sb_info.append(keys[i] + "=[" + currRefItemVO.getHashVO().getStringValue(keys[i]) + "]\r\n");
				}
			}
		}
		//////.....

		MessageBox.showTextArea(this, "����ʵ�ʰ󶨵�����", sb_info.toString()); //
	}

	@Override
	public void setItemEditable(boolean _bo) {
		this.btn_ref.setEnabled(_bo); //
	}

	@Override
	public boolean isItemEditable() {
		return btn_ref.isEnabled();
	}

	@Override
	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	@Override
	public void setObject(Object _obj) {
		try {
			currRefItemVO = (RefItemVO) _obj; //
			if (currRefItemVO == null) {
				textField.setText("");
			} else {
				textField.setText(currRefItemVO.getName()); //
			}
			textField.select(0, 0); //
		} catch (java.lang.ClassCastException ex) {
			System.out.println("�ؼ�[" + key + "][" + name + "]���������Ͳ���,��ҪRefItemVO,ʵ����[" + _obj.getClass().getName() + "]!"); //
			throw ex;
		}

	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	public Pub_Templet_1_ItemVO getTempletItemVO() {
		return templetItemVO;
	}

	/***��ӵ��������� by hm 2013-04-09***/
	public JButton getBtn_ref() {
		return btn_ref;
	}

	public JTextField getTextField() {
		return textField;
	}

}
