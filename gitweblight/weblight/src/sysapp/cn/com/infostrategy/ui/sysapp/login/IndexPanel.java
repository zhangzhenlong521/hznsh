package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;

/**
 * ��ҳ! �ؼ���!!! ��ǰ�Ƿ��鷽ʽ,�������ÿ�,�µĻ����Ƿ�����ҳ,��ͷ��ͼƬ,�й�������! ÿ�������ĳ����Ͻ���һ��ͼƬ!�Ե������ҳ!!!
 * 
 * @author xch
 * 
 */
public class IndexPanel extends JPanel implements ItemListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DeskTopNewGroupVO[] allTaskVOs = null; // ���е�����
	private DeskTopPanel deskTopPanel = null; //
	private TBUtil tbUtil = new TBUtil(); //

	public IndexPanel(DeskTopPanel _deskTopPanel) {
		try {
			this.deskTopPanel = _deskTopPanel; //
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			allTaskVOs = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode()); // ��ȡ����������!!!
			if (tbUtil.getSysOptionBooleanValue("������Ϣ�Ƿ����ô�BOMͼ", false)) {//Ĭ��Ϊfasle  20180709 Ԭ�����޸� ֮ǰ�ύ��������
				initialize2();
			} else {
				initialize();
			}
			//
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ����ҳ��!!!  ��Ϊ��˧����ӵ����ͣ����Ǿ�����Ҫ���ľɵ��߼�
	 */
	private void initialize2() throws Exception {
		JPanel realContentPanel = new JPanel(); //
		realContentPanel.setFocusable(true); // �ɵ��,�������¼����ܹ���
		realContentPanel.setOpaque(false); // ͸��!!!
		realContentPanel.setLayout(null); // //����ֵ�ܾ�!��ǰ���Ƕ�׵Ĳ�����,��Ϊ�е�ǿ���Ͽ�Ч��������!
		// ����������˹�����Ч����������!��.setLayout(null);
		int li_space = 20; // ������֮���������,��ǰ��10,�ܾ��ö���һ��!
		int li_maxheigth = 0; //
		int li_yy_start = 0; //
		// �Ƿ���ʾϵͳ��Ϣ,����������������(����ͼƬ����������ſ�)
		boolean isShowSysMsg = tbUtil.getSysOptionBooleanValue("�Ƿ���ʾ������Ϣ", true);
		if (isShowSysMsg) { // ����й���ͼƬ���������,������������ʾ����������,һ���ǹ���ͼƬ,һ���ǹ�������,��Щ��Ϣ����ƽ̨�������õ�!!
			// ����ͼƬ
			IndexRollImagePanel rollImgPanel = new IndexRollImagePanel(); // ����ͼƬ���
			rollImgPanel.setBounds(565, 0, 550, 225); //
			realContentPanel.add(rollImgPanel); //

			// ��������!
			IndexRollMsgPanel rollMsgPanel = new IndexRollMsgPanel(); // ��������
			// rollMsgPanel.setBorder(BorderFactory.createLineBorder(new
			// Color(220, 220, 220), 1)); //
			rollMsgPanel.setBounds(565, 225, 550, 225); //
			realContentPanel.add(rollMsgPanel); //

			li_yy_start = 450 + 10; //
		}

		// �������е�����!!!Ҫ����[����/ȫ��]
		int li_xx_start = 0; // X�������ʼλ��!!
		boolean isFirstHalft = false; // ��¼��ѭ���е�����Ƿ��ǵ�һ������
		for (int i = 0; i < allTaskVOs.length; i++) { // ����!��������
			String str_dataType = allTaskVOs[i].getDefineVO().getDatatype(); // ��������!!
			String str_viewCols = allTaskVOs[i].getDefineVO().getViewcols(); // ��ʾ������!!!
			String str_capimg = allTaskVOs[i].getDefineVO().getCapimg(); // ñ��ͼƬ!
			String templetcode = allTaskVOs[i].getDefineVO().getTempletcode();
			String custclass = allTaskVOs[i].getDefineVO().getDatabuildername(); // �Զ����ࡾzzl  2017/3/31��

			int li_width = 0; // ���
			if ("ȫ��".equals(str_viewCols)) {
				li_width = 1100; //
			} else { // ����!!
				li_width = 550; //
			}
			JPanel itemPanel = null; //
			if (templetcode != null && str_dataType.endsWith("Bomͼ") && custclass != null) {//zzl[2017/3/29]
				itemPanel = new IndexCustPanel(deskTopPanel, allTaskVOs[i]);
				//				itemPanel = new IndexRiskOfMapWorkPanel();
				itemPanel.setBounds(0, 0, 550, 458);
			} else {
				if (str_dataType != null && str_dataType.endsWith("ͼ")) {
					itemPanel = new IndexChartPanel(deskTopPanel, allTaskVOs[i]); // �����ͼ�ε�!
				} else if (str_dataType != null && str_dataType.endsWith("��")) { // ��ҳ׷���Զ������
					// �����/2012-11-12��
					String str_clasName = allTaskVOs[i].getDefineVO().getDatabuildername();
					if (str_clasName != null && !str_clasName.equals("")) {
						try {
							itemPanel = (JPanel) Class.forName(str_clasName).newInstance();
						} catch (Exception e) {
							continue;
						}
					}
				} else {
					itemPanel = new IndexItemTaskPanel(deskTopPanel, allTaskVOs[i], li_width); // ��������ֵ�!
				}
				// itemPanel.setBorder(BorderFactory.createLineBorder(new
				// Color(220, 220, 220), 1)); //
				itemPanel.setBounds(li_xx_start, li_yy_start, li_width, 210); //
			}
			if (li_xx_start == 0 && li_width == 550) { //
				isFirstHalft = true; //
			} else {
				isFirstHalft = false; //
			}
			if (str_capimg != null && !str_capimg.trim().equals("")) { // �����ñ��!��ǿ������һ���Ȼ���ñ��,Ȼ����ǿ������һ��!
				if (i != 0) {
					li_yy_start = li_yy_start + 210 + li_space; // ������һ��!

				}
				JLabel labelpp = null; //
				ImageIcon capImg = UIUtil.getImageFromServer("/images/" + str_capimg); // .getImage()
				if (capImg != null) { // ����ҵ�,�����Сǿ������Ϊ735*80
					if (capImg.getIconWidth() != 735 || capImg.getIconHeight() != 90) { // �����������ͼƬ��С��������Ҫ��735*90,��������촦��!
						capImg = new ImageIcon(tbUtil.getImageScale(capImg.getImage(), 735, 90)); //
					}
					labelpp = new JLabel(capImg); //
				} else {
					labelpp = new JLabel("����ͼƬ[%WebRoot%/images/" + str_capimg + "]ʧ��,����û�и�ͼƬ[735*90]!"); //
				}
				labelpp.setToolTipText(str_capimg + "[735*90]"); //
				labelpp.setBounds(0, li_yy_start, 735, 90); // ͼƬ��90,����sohu,sina,163��ҳͼƬ����90
				realContentPanel.add(labelpp); //

				li_xx_start = 0; //
				li_yy_start = li_yy_start + 90 + li_space; // ͼƬ��80
			} else {
				// X��Y����λ��!!!
				if (i != 0) {
					if ("ȫ��".equals(str_viewCols)) { // �����ȫ��!
						li_xx_start = 0; //
						li_yy_start = li_yy_start + 210; //
					} else { // ����ǰ���
						if (isFirstHalft) { // ����ǰ��һ�����ǵ�һ������,���Ǳ����ǵڶ�������!
							li_xx_start = 560; // X��375,Y����
						} else { // Ҳ��ǰ����ȫ��,�����ǵڶ�������,����������һ��!
							li_xx_start = 0; //
							li_yy_start = li_yy_start + 210; //
						}
					}
				}
			}

			realContentPanel.add(itemPanel); //
			li_maxheigth = li_yy_start + li_space; // ��ߵĵط�
		}

		// �û���¼��!!!
		JLabel label_loginCount = new JLabel(getLoginCount()); //
		label_loginCount.setToolTipText("ͨ��ϵͳ����[��ҳ�ײ�˵��]����"); //
		label_loginCount.setForeground(new Color(2, 68, 152)); //
		label_loginCount.setBounds(465, li_maxheigth, 300, 20); // X����ʼλ����Ҫ�������ֵ�ʵ�ʶ��ٷ���!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_loginCount); //

		// ��ҳȥ��Ȩ���� �����/2012-11-20��
		String year = UIUtil.getCurrDate().substring(0, 4);
		String copyright = "��Ȩ���С�" + System.getProperty("LICENSEDTO") + "  Copyright 1997-" + year + " all rights reserved";
		boolean isHaveCopyright = tbUtil.getSysOptionBooleanValue("�Ƿ���ʾCopyright", true);
		if (!isHaveCopyright) {
			copyright = "                  ��Ȩ���С�" + System.getProperty("LICENSEDTO"); // �ӿո�Ϊ����
		}

		// ��Ȩ����
		JLabel label_copyright = new JLabel(copyright); //
		label_copyright.setForeground(new Color(2, 68, 152)); //
		label_copyright.setBounds(400, li_maxheigth, 600, 20); // X����ʼλ����Ҫ�������ֵ�ʵ�ʶ��ٷ���!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_copyright); //

		// �ұߵ�ͼƬ��,���������������ȿͻ���ϲ�����!
		int li_allWidth = 800; //
		String str_rightbarConf = tbUtil.getSysOptionStringValue("��ҳ�ұߵ�ͼƬ", null); //
		if (str_rightbarConf != null && !str_rightbarConf.trim().equals("")) {
			JPanel rightPanel = getRightImageBarPanel(str_rightbarConf); //
			int li_rightWidth = (int) rightPanel.getPreferredSize().getWidth(); //
			int li_rightHeight = (int) rightPanel.getPreferredSize().getHeight(); //
			rightPanel.setBounds(750, 0, li_rightWidth, li_rightHeight); // ���ÿ����1000,�������Ҫ��ߵĲ˵���,����ƥ��1024�ķֱ���!!
			// �����1280�ķֱ���,�����ü�����ߵĲ˵���,�����������Ŀ������!!
			realContentPanel.add(rightPanel); //
			if (li_rightHeight > li_maxheigth) {
				li_maxheigth = li_rightHeight;
			}
			li_allWidth = 750 + li_rightWidth + 5; //
		}

		if (tbUtil.getSysOptionBooleanValue("������Ϣ�Ƿ����ô�BOMͼ", true)) {
			realContentPanel.setPreferredSize(new Dimension(1150, li_maxheigth + 30)); // �����߶�!!!
		} else {
			realContentPanel.setPreferredSize(new Dimension(li_allWidth, li_maxheigth + 30)); //�����߶�!!!
		}

		// ���岼��!!!
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); //
		contentPanel.setOpaque(false); // ͸��!!!
		contentPanel.setFocusable(true); // �ɵ��,�������¼����ܹ���!!
		contentPanel.add(realContentPanel); //

		JPopupMenu popMenu = new JPopupMenu(); //
		JMenuItem menuItem = new JMenuItem("ˢ����ҳ", UIUtil.getImage("office_191.gif")); //
		menuItem.addActionListener(this); //
		popMenu.add(menuItem); //
		contentPanel.setComponentPopupMenu(popMenu); //

		JScrollPane scrollPanel = new JScrollPane(contentPanel); //
		scrollPanel.setFocusable(true); //
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20); // ����ʱ���!!
		scrollPanel.setOpaque(false); //
		scrollPanel.getViewport().setOpaque(false); // ͸��!!!

		this.setBackground(LookAndFeel.desktop_Background); // //
		this.setLayout(new BorderLayout()); //
		this.setUI(new WLTPanelUI(WLTPanel.HORIZONTAL_FROM_MIDDLE, false)); //
		this.add(scrollPanel, BorderLayout.CENTER); //

	}

	/**
	 * ����ҳ��!!!
	 */
	private void initialize() throws Exception {
		JPanel realContentPanel = new JPanel(); //
		realContentPanel.setFocusable(true); //�ɵ��,�������¼����ܹ���
		realContentPanel.setOpaque(false); //͸��!!!
		realContentPanel.setLayout(null); ////����ֵ�ܾ�!��ǰ���Ƕ�׵Ĳ�����,��Ϊ�е�ǿ���Ͽ�Ч��������! ����������˹�����Ч����������!

		int li_space = 15; //������֮���������,��ǰ��10,�ܾ��ö���һ��!
		int li_maxheigth = 0; //
		int li_yy_start = 0; //
		//�Ƿ���ʾϵͳ��Ϣ,����������������(����ͼƬ����������ſ�)
		boolean isShowSysMsg = tbUtil.getSysOptionBooleanValue("�Ƿ���ʾ������Ϣ", true);
		if (isShowSysMsg) { //����й���ͼƬ���������,������������ʾ����������,һ���ǹ���ͼƬ,һ���ǹ�������,��Щ��Ϣ����ƽ̨�������õ�!!
			//����ͼƬ
			IndexRollImagePanel rollImgPanel = new IndexRollImagePanel(); //����ͼƬ���
			rollImgPanel.setBounds(0, 0, 360, 185); //
			realContentPanel.add(rollImgPanel); //

			//��������!
			IndexRollMsgPanel rollMsgPanel = new IndexRollMsgPanel(); //��������
			//			rollMsgPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); //
			rollMsgPanel.setBounds(375, 0, 360, 185); //
			realContentPanel.add(rollMsgPanel); //

			li_yy_start = 185 + 10; //
		}

		//�������е�����!!!Ҫ����[����/ȫ��]
		int li_xx_start = 0; //X�������ʼλ��!!
		boolean isFirstHalft = false; //��¼��ѭ���е�����Ƿ��ǵ�һ������
		for (int i = 0; i < allTaskVOs.length; i++) { //����!��������
			String str_dataType = allTaskVOs[i].getDefineVO().getDatatype(); //��������!!
			String str_viewCols = allTaskVOs[i].getDefineVO().getViewcols(); //��ʾ������!!!
			String str_capimg = allTaskVOs[i].getDefineVO().getCapimg(); //ñ��ͼƬ!
			String str_descr = allTaskVOs[i].getDefineVO().getDescr(); //��ע
			if (str_capimg != null && !str_capimg.trim().equals("")) { //�����ñ��!��ǿ������һ���Ȼ���ñ��,Ȼ����ǿ������һ��!
				if (i != 0) {
					li_yy_start = li_yy_start + 210 + li_space; //������һ��!
				}
				JLabel labelpp = null; //
				ImageIcon capImg = UIUtil.getImageFromServer("/images/" + str_capimg); //.getImage()
				if (capImg != null) { //����ҵ�,�����Сǿ������Ϊ735*80
					if (capImg.getIconWidth() != 735 || capImg.getIconHeight() != 90) { //�����������ͼƬ��С��������Ҫ��735*90,��������촦��!
						capImg = new ImageIcon(tbUtil.getImageScale(capImg.getImage(), 735, 90)); //
					}
					labelpp = new JLabel(capImg); //
				} else {
					labelpp = new JLabel("����ͼƬ[%WebRoot%/images/" + str_capimg + "]ʧ��,����û�и�ͼƬ[735*90]!"); //
				}
				labelpp.setToolTipText(str_capimg + "[735*90]"); //
				labelpp.setBounds(0, li_yy_start, 735, 90); //ͼƬ��90,����sohu,sina,163��ҳͼƬ����90
				realContentPanel.add(labelpp); //

				li_xx_start = 0; //
				li_yy_start = li_yy_start + 90 + li_space; //ͼƬ��80
			} else {
				//X��Y����λ��!!!
				if (i != 0) {
					if ("ȫ��".equals(str_viewCols)) { //�����ȫ��!
						li_xx_start = 0; //
						li_yy_start = li_yy_start + 210 + li_space; //
					} else { //����ǰ���
						if (isFirstHalft) { //����ǰ��һ�����ǵ�һ������,���Ǳ����ǵڶ�������!
							li_xx_start = 375; //X��375,Y����
						} else { //Ҳ��ǰ����ȫ��,�����ǵڶ�������,����������һ��!
							li_xx_start = 0; //
							li_yy_start = li_yy_start + 210 + li_space; //
						}
					}
				}
			}

			int li_width = 0; //���
			if ("ȫ��".equals(str_viewCols)) {
				li_width = 735; //
			} else { //����!!
				li_width = 360; //
			}

			JPanel itemPanel = null; //
			if (str_dataType != null && str_dataType.endsWith("ͼ")) {
				itemPanel = new IndexChartPanel(deskTopPanel, allTaskVOs[i]); //�����ͼ�ε�!
			} else if (str_dataType != null && str_dataType.endsWith("��")) { //��ҳ׷���Զ������ �����/2012-11-12��
				String str_clasName = allTaskVOs[i].getDefineVO().getDatabuildername();
				if (str_clasName != null && !str_clasName.equals("")) {
					try {
						itemPanel = (JPanel) Class.forName(str_clasName).newInstance();
					} catch (Exception e) {
						continue;
					}
				}
			} else if(str_dataType != null && str_dataType.endsWith("����")){//zzl [2019-7-8],��Чϵͳ��Ϣ����Ϊ�չ�д�ɹ�����Ϣ�����Ų�����ô����
				itemPanel=new IndexChartPanel().getIndexGDPanel(itemPanel, allTaskVOs[i]); //zzl [2019-7-8] ��������ҹ���
			}else{
				itemPanel = new IndexItemTaskPanel(deskTopPanel, allTaskVOs[i], li_width); //��������ֵ�!
			}
			//			itemPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); //
			itemPanel.setBounds(li_xx_start, li_yy_start, li_width, 210); //
			if (li_xx_start == 0 && li_width == 360) { //
				isFirstHalft = true; //
			} else {
				isFirstHalft = false; //
			}

			realContentPanel.add(itemPanel); //
			li_maxheigth = li_yy_start + 210 + li_space; //��ߵĵط�
		}

		//�û���¼��!!!
		JLabel label_loginCount = new JLabel(getLoginCount()); //
		label_loginCount.setToolTipText("ͨ��ϵͳ����[��ҳ�ײ�˵��]����"); //
		label_loginCount.setForeground(new Color(2, 68, 152)); //
		label_loginCount.setBounds(265, li_maxheigth, 300, 20); //X����ʼλ����Ҫ�������ֵ�ʵ�ʶ��ٷ���!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_loginCount); //

		//��ҳȥ��Ȩ���� �����/2012-11-20��
		String year = UIUtil.getCurrDate().substring(0, 4);
		String copyright = "��Ȩ���С�" + System.getProperty("LICENSEDTO") + "  Copyright 1997-" + year + " all rights reserved";
		boolean isHaveCopyright = tbUtil.getSysOptionBooleanValue("�Ƿ���ʾCopyright", true);
		if (!isHaveCopyright) {
			copyright = "                  ��Ȩ���С�" + System.getProperty("LICENSEDTO"); //�ӿո�Ϊ����
		}

		//��Ȩ����
		JLabel label_copyright = new JLabel(copyright); //
		label_copyright.setForeground(new Color(2, 68, 152)); //
		label_copyright.setBounds(200, li_maxheigth, 600, 20); //X����ʼλ����Ҫ�������ֵ�ʵ�ʶ��ٷ���!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_copyright); //

		//�ұߵ�ͼƬ��,���������������ȿͻ���ϲ�����!
		int li_allWidth = 800; //
		String str_rightbarConf = tbUtil.getSysOptionStringValue("��ҳ�ұߵ�ͼƬ", null); //
		if (str_rightbarConf != null && !str_rightbarConf.trim().equals("")) {
			JPanel rightPanel = getRightImageBarPanel(str_rightbarConf); //
			int li_rightWidth = (int) rightPanel.getPreferredSize().getWidth(); //
			int li_rightHeight = (int) rightPanel.getPreferredSize().getHeight(); //
			rightPanel.setBounds(750, 0, li_rightWidth, li_rightHeight); //���ÿ����1000,�������Ҫ��ߵĲ˵���,����ƥ��1024�ķֱ���!! �����1280�ķֱ���,�����ü�����ߵĲ˵���,�����������Ŀ������!!
			realContentPanel.add(rightPanel); //
			if (li_rightHeight > li_maxheigth) {
				li_maxheigth = li_rightHeight;
			}
			li_allWidth = 750 + li_rightWidth + 5; //
		}
		realContentPanel.setPreferredSize(new Dimension(li_allWidth, li_maxheigth + 30)); //�����߶�!!!

		//���岼��!!!
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); //
		contentPanel.setOpaque(false); //͸��!!!
		contentPanel.setFocusable(true); //�ɵ��,�������¼����ܹ���!!
		contentPanel.add(realContentPanel); //

		JPopupMenu popMenu = new JPopupMenu(); //
		JMenuItem menuItem = new JMenuItem("ˢ����ҳ", UIUtil.getImage("office_191.gif")); //
		menuItem.addActionListener(this); //
		popMenu.add(menuItem); //
		contentPanel.setComponentPopupMenu(popMenu); //

		JScrollPane scrollPanel = new JScrollPane(contentPanel); //
		scrollPanel.setFocusable(true); //
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20); //����ʱ���!!
		scrollPanel.setOpaque(false); //
		scrollPanel.getViewport().setOpaque(false); //͸��!!!

		this.setBackground(LookAndFeel.desktop_Background); ////
		this.setLayout(new BorderLayout()); //
		this.setUI(new WLTPanelUI(WLTPanel.HORIZONTAL_FROM_MIDDLE, false)); //
		this.add(scrollPanel, BorderLayout.CENTER); //

	}

	public void actionPerformed(ActionEvent e) {
		deskTopPanel.refreshAllTaskGroup(); //
	}

	/**
	 * �����ͻ�����ϲ���ұ���ЩͼƬ��������������,�о��������վ!! ������������������Ҫ��!
	 * 
	 * @return
	 */
	private JPanel getRightImageBarPanel(String _conf) {
		HashMap map = tbUtil.convertStrToMapByExpress(_conf, ";", "="); //
		JPanel panel = new JPanel(); //
		panel.setOpaque(false); // ͸��!!
		panel.setLayout(null); //

		int li_imgwidth = 200, li_imgheight = 80; //
		String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
		int li_y = 0; //
		for (int i = 0; i < str_keys.length; i++) { //
			ImageIcon img = UIUtil.getImageFromServer("/images/" + str_keys[i]); //
			JButton btn = new JButton(); // ��*��=250*100
			if (img == null) {
				btn.setText("����ͼƬ[" + str_keys[i] + "]ʧ��!"); //
			} else {
				// ǿ�и��´�С
				if (img.getIconWidth() != li_imgwidth || img.getIconHeight() != li_imgheight) { // �����������ͼƬ��С����ָ����С,��ǿ������һ��!
					img = new ImageIcon(tbUtil.getImageScale(img.getImage(), li_imgwidth, li_imgheight)); //
				}
				btn.setIcon(img); //
			}
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onClickedRightBarImg((JButton) e.getSource()); //
				}

			}); //
			btn.setBorder(BorderFactory.createEmptyBorder()); //
			btn.setMargin(new Insets(0, 0, 0, 0)); //
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //

			if (ClientEnvironment.isAdmin()) {
				if (i == 0) {
					btn.setToolTipText("<html>" + str_keys[i] + "=" + map.get(str_keys[i]) + "<br>ͨ��ϵͳ����[��ҳ�ұߵ�ͼƬ]����!<br>��ʽ��[rg1.gif=451;rg2.gif=562;],key��ͼƬ����,value�ǲ˵�id<br>ͼƬ�������WebRoot/images/Ŀ¼��</html>"); //
				} else { //
					btn.setToolTipText(str_keys[i] + "=" + map.get(str_keys[i])); //
				}
			}

			btn.setBounds(0, li_y, li_imgwidth, li_imgheight); //
			btn.putClientProperty("menuid", (String) map.get(str_keys[i])); //
			panel.add(btn); //
			li_y = li_y + li_imgheight + 10; //
		}

		li_y = li_y + 10; //
		JComboBox comBox = new JComboBox(); //
		if (ClientEnvironment.isAdmin()) {
			comBox.setToolTipText("ͨ��ϵͳ����[��ҳ�������]����!"); //
		}
		comBox.setFocusable(false); //
		comBox.addItem(new ComBoxItemVO("�������", null, "�������")); //

		String str_links = tbUtil.getSysOptionStringValue("��ҳ�������", ""); //
		if (!str_links.trim().equals("")) {
			HashMap linkmap = tbUtil.parseStrAsMap(str_links); //
			String[] str_linkkeys = (String[]) linkmap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_linkkeys.length; i++) {
				comBox.addItem(new ComBoxItemVO((String) linkmap.get(str_linkkeys[i]), null, str_linkkeys[i])); //
			}
		}
		comBox.setBounds(20, li_y, 180, 25); //
		comBox.addItemListener(this); //
		panel.add(comBox); //
		li_y = li_y + 30; //

		panel.setPreferredSize(new Dimension(li_imgwidth, li_y)); //
		return panel; //
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		JComboBox comBox = (JComboBox) e.getSource(); //
		if (comBox.getSelectedIndex() == 0) {
			return; //
		}

		ComBoxItemVO itemVO = (ComBoxItemVO) comBox.getSelectedItem(); //
		String str_url = itemVO.getId(); //
		try {
			Runtime.getRuntime().exec("explorer.exe " + str_url); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ����ұ�ͼƬ��ť,�����൱�ڴ�һ�����ܲ˵�
	 * 
	 * @param _btn
	 */
	protected void onClickedRightBarImg(JButton _btn) {
		try {
			_btn.setCursor(new Cursor(Cursor.WAIT_CURSOR)); // ���ʱͼʾҪ��ɵȴ�Ч��!!
			String str_menuid = (String) _btn.getClientProperty("menuid"); //
			deskTopPanel.openAppMainFrameWindowById(str_menuid); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			_btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	private String getLoginCount() throws Exception {
		try {
			if ("Y".equalsIgnoreCase(System.getProperty("ISLOADRUNDERCALL"))) { //
				return ""; //
			}
			String str_text = tbUtil.getSysOptionStringValue("��ҳ�ײ�˵��", "���ǵڡ�${TotalCount}��λ������,����¼��${UserCount}����"); //
			if (str_text.indexOf("${TotalCount}") >= 0 || str_text.indexOf("${UserCount}") >= 0) { // ���������������
				SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
				String str_totalCount = service.setTotalLoginCount(); // ������Զ�̷���Ҫ�ϲ���һ��!!!
				String str_userCount = service.setUserLoginCount(); //
				str_text = tbUtil.replaceAll(str_text, "${TotalCount}", str_totalCount); //
				str_text = tbUtil.replaceAll(str_text, "${UserCount}", str_userCount); //
			}

			return str_text; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return ""; //
		}
	}

}
