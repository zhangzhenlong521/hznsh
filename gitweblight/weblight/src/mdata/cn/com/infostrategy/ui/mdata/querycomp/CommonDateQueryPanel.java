package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTLabelUI;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.common.WLTTreeUI;

/**
 * ���õ�ʱ���ѯ���,�����һ��������ҳǩ,һ���������ͽṹ��ʾ������;��;��;��,һ�����������ķ�ʽ��ʾ
 * @author xch
 *
 */
public class CommonDateQueryPanel extends JPanel {
	private static final long serialVersionUID = 1L; //

	private String[] timeType = new String[] { "��", "��", "��", "��", "����" }; //
	private int li_level = 4; //���,������ȡֵ,��=1,��=2,��=3,��=4.
	private boolean isHaveBetweenDate = true; //�Ƿ������������ȽϵĹ���
	private WLTTabbedPane tabPanel_choosePeriod; //ѡ��ʱ��ε����ַ�ʽ!!!
	private JTree jtree = null; //
	private QueryDateModelPanel daily_1, daily_2;

	private CommonDateQueryPanel() {
	}

	public CommonDateQueryPanel(String _constraint) {
		this.setLayout(new BorderLayout()); ////
		if (_constraint == null) {
			_constraint = "��;��;��;��;����"; //
		}
		TBUtil tbUtil = new TBUtil(); //
		timeType = tbUtil.split(_constraint, ";"); //�ȷָ�
		if (existItem(timeType, "��")) {
			li_level = 4; //
		} else if (existItem(timeType, "��")) {
			li_level = 3; //
		} else if (existItem(timeType, "��")) {
			li_level = 2; //
		} else if (existItem(timeType, "��")) {
			li_level = 1; //
		}

		if (existItem(timeType, "����")) {
			isHaveBetweenDate = true;
		} else {
			isHaveBetweenDate = false;
		}

		//����ʱ����
		int li_beginyear = 2000; //
		int li_endyear = 2020; //
		try {
			int li_optionYear = tbUtil.getSysOptionIntegerValue("���ڲ�ѯ�ؼ���ʼ���", li_beginyear); //����ҵ��������Ҫ��1990�꿪ʼ,Ϊ�˷�ֹ��ʽ����,�����쳣!!
			li_beginyear = li_optionYear; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		DefaultMutableTreeNode[] node_years = new DefaultMutableTreeNode[li_endyear - li_beginyear + 1]; //
		for (int i = li_beginyear; i <= li_endyear; i++) {
			HashVO hvo_year = new HashVO(); //
			hvo_year.setAttributeValue("id", i + "��"); //
			hvo_year.setAttributeValue("type", "��"); //
			hvo_year.setAttributeValue("name", i + "��"); //
			hvo_year.setAttributeValue("isenable", new Boolean(existItem(timeType, "��"))); //
			hvo_year.setToStringFieldName("name"); //
			node_years[i - li_beginyear] = new DefaultMutableTreeNode(hvo_year); //

			if (li_level >= 2) { //������ڼ���,���м���һ˵..
				for (int j = 1; j <= 4; j++) { //�ĸ�����
					HashVO hvo_season = new HashVO(); //
					hvo_season.setAttributeValue("id", i + "��" + j + "����"); //
					hvo_season.setAttributeValue("type", "��"); //
					hvo_season.setAttributeValue("name", i + "��" + j + "����"); //
					hvo_season.setAttributeValue("isenable", new Boolean(existItem(timeType, "��"))); //
					hvo_season.setToStringFieldName("name"); //
					DefaultMutableTreeNode node_season = new DefaultMutableTreeNode(hvo_season); //
					node_years[i - li_beginyear].add(node_season); //

					if (li_level >= 3) { //��������·�
						int li_beginmonth = 1;
						int li_endmonth = 3;
						if (j == 1) {
							li_beginmonth = 1;
							li_endmonth = 3;
						} else if (j == 2) {
							li_beginmonth = 4;
							li_endmonth = 6;
						} else if (j == 3) {
							li_beginmonth = 7;
							li_endmonth = 9;
						} else if (j == 4) {
							li_beginmonth = 10;
							li_endmonth = 12;
						}

						for (int k = li_beginmonth; k <= li_endmonth; k++) {
							HashVO hvo_month = new HashVO(); //
							hvo_month.setAttributeValue("id", i + "��" + ("" + (100 + k)).substring(1, 3) + "��"); //
							hvo_month.setAttributeValue("type", "��"); //
							hvo_month.setAttributeValue("name", i + "��" + ("" + (100 + k)).substring(1, 3) + "��"); //
							hvo_month.setAttributeValue("isenable", new Boolean(existItem(timeType, "��"))); //
							hvo_month.setToStringFieldName("name"); //
							DefaultMutableTreeNode node_month = new DefaultMutableTreeNode(hvo_month); //
							node_season.add(node_month); //�ڼ����ϼ����¶�

							if (li_level >= 4) { //���������,����ȷ����!
								int li_dayscount = getOneMonthDays(i, k); //
								for (int d = 1; d <= li_dayscount; d++) { //�������е���!!!
									HashVO hvo_day = new HashVO(); //
									hvo_day.setAttributeValue("id", i + "-" + ("" + (100 + k)).substring(1, 3) + "-" + ("" + (100 + d)).substring(1, 3)); //
									hvo_day.setAttributeValue("type", "��"); //
									hvo_day.setAttributeValue("name", i + "-" + ("" + (100 + k)).substring(1, 3) + "-" + ("" + (100 + d)).substring(1, 3)); //
									hvo_day.setAttributeValue("isenable", new Boolean(existItem(timeType, "��"))); //
									hvo_day.setToStringFieldName("name"); //
									DefaultMutableTreeNode node_day = new DefaultMutableTreeNode(hvo_day); //
									node_month.add(node_day); //�ڼ����ϼ����¶�
								}
							}
						}
					}
				}
			}
		}

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ѡ��ʱ��"); //
		DefaultMutableTreeNode currNode = null; //

		if (li_level >= 3) { //��Ϊѡ��ĳһ��̫ϸ�ˣ����ڿ�ѡ���»��춼Ĭ��ѡ�е�ǰ�¡����/2012-06-20��
			boolean iffind = false;
			String str_currmonth = tbUtil.getCurrDate().substring(0, 4) + "��" + tbUtil.getCurrDate().substring(5, 7) + "��";
			for (int i = 0; i < node_years.length; i++) {
				rootNode.add(node_years[i]); //
				if (!iffind) {
					for (int j = 0; j < node_years[i].getChildCount(); j++) {
						DefaultMutableTreeNode monthnode = (DefaultMutableTreeNode) node_years[i].getChildAt(j);
						for (int m = 0; m < monthnode.getChildCount(); m++) {
							if (((DefaultMutableTreeNode) monthnode.getChildAt(m)).getUserObject().toString().equals(str_currmonth)) {
								currNode = ((DefaultMutableTreeNode) monthnode.getChildAt(m)); //
								iffind = true;
								break;
							}
						}
					}
				}
			}
		} else if (li_level == 2) {//���ֻ��ѡ�����ȣ���Ĭ��ѡ�е�ǰ����
			boolean iffind = false;
			String str_currseason = tbUtil.getCurrDate().substring(0, 4) + "��"; //����!!
			String str_currmonth = tbUtil.getCurrDate().substring(5, 7);
			if (str_currmonth.compareTo("04") < 0) {
				str_currseason += "1����";
			} else if (str_currmonth.compareTo("07") < 0) {
				str_currseason += "2����";
			} else if (str_currmonth.compareTo("10") < 0) {
				str_currseason += "3����";
			} else {
				str_currseason += "4����";
			}
			for (int i = 0; i < node_years.length; i++) {
				rootNode.add(node_years[i]); //
				if (!iffind) {
					for (int j = 0; j < node_years[i].getChildCount(); j++) {
						if (((DefaultMutableTreeNode) node_years[i].getChildAt(j)).getUserObject().toString().equals(str_currseason)) {
							currNode = ((DefaultMutableTreeNode) node_years[i].getChildAt(j)); //
							iffind = true;
							break;
						}
					}
				}
			}
		} else {//���ֻ��ѡ���꣬��Ĭ��ѡ�е���
			String str_curryear = tbUtil.getCurrDate().substring(0, 4) + "��"; //����!!
			for (int i = 0; i < node_years.length; i++) {
				rootNode.add(node_years[i]); //
				if (node_years[i].getUserObject().toString().equals(str_curryear)) {
					currNode = node_years[i]; //
				}
			}
		}

		jtree = new JTree(rootNode); //
		jtree.setUI(new WLTTreeUI()); //
		jtree.setOpaque(false); //
		jtree.setRootVisible(false); //
		jtree.setShowsRootHandles(true); //
		jtree.setRowHeight(18); //
		jtree.setCellRenderer(new MyTimeTreeCellRender()); //
		JScrollPane scrollPane = new JScrollPane(jtree); //
		scrollPane.setOpaque(false); //

		try { //�Զ���������ǰ���,��Ϊ���̫��ʱÿ�ζ�Ҫ����һ��!
			if (currNode != null) {
				TreeNode[] currYearPathnodes = ((DefaultTreeModel) jtree.getModel()).getPathToRoot(currNode);
				TreePath currYearPath = new TreePath(currYearPathnodes); //
				if (currYearPath != null) {
					//jtree.expandPath(currYearPath); //
					jtree.makeVisible(currYearPath);
					jtree.scrollPathToVisible(currYearPath);
					jtree.setSelectionPath(currYearPath); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		scrollPane.setBorder(BorderFactory.createEmptyBorder()); //
		tabPanel_choosePeriod = new WLTTabbedPane(); //
		tabPanel_choosePeriod.setFocusable(false); //

		JPanel panel_tmp = new JPanel(new BorderLayout()); //
		panel_tmp.add(scrollPane, BorderLayout.CENTER); //\
		if (isHaveBetweenDate) {//�������������ѡ������ͣ��������ʾ��Ϣ�����򲻼���ʾ��Ϣ�����/2012-06-20��
			JLabel label = new JLabel("��ʾ:��������ҵ�ʱ��β��������б���,��ʹ�á�����ѡ����ѡ��!"); //
			label.setUI(new WLTLabelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE)); //
			label.setBackground(LookAndFeel.defaultShadeColor1); //
			label.setForeground(new Color(0, 128, 192)); //
			panel_tmp.add(label, BorderLayout.SOUTH); //
			tabPanel_choosePeriod.addTab("����ѡ��", panel_tmp); //

			JPanel panel_twodaily = new JPanel(new BorderLayout()); //
			panel_twodaily.setOpaque(false); //
			daily_1 = new QueryDateModelPanel();
			daily_2 = new QueryDateModelPanel();
			daily_1.setPreferredSize(new Dimension(235, 235));
			daily_2.setPreferredSize(new Dimension(235, 235));
			panel_twodaily.add(daily_1, BorderLayout.WEST); //
			panel_twodaily.add(new JLabel("<==>", JLabel.CENTER), BorderLayout.CENTER); //
			panel_twodaily.add(daily_2, BorderLayout.EAST); //
			tabPanel_choosePeriod.addTab("����ѡ��", panel_twodaily); //

			if (TBUtil.getTBUtil().getSysOptionBooleanValue("���ڲ�ѯ�ؼ��Ƿ�����ѡ������ǰ��", false)) { //
				tabPanel_choosePeriod.setSelectedIndex(1); //ֻҪ����һ�������,�︻��ԭ�����޸�addTab��˳��,�����������ط���Ҫ�޸�!��ɵ!��xch/2012-08-24��
			}
		} else {
			tabPanel_choosePeriod.addTab("����ѡ��", panel_tmp); //
		}
		this.add(tabPanel_choosePeriod, BorderLayout.CENTER); ////
	}

	public RefItemVO getSelectedRefItemVO() {
		RefItemVO refItemVO = null; //
		if (tabPanel_choosePeriod.getSelectedIndex() == 0) { //�����ʱ����!!!
			TreePath[] paths = jtree.getSelectionPaths(); //
			if (paths == null) {
				MessageBox.show(this, "��ѡ��һ��ʱ���!"); //
				return null;
			}

			ArrayList al_hvs = new ArrayList(); //
			for (int i = 0; i < paths.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent(); //
				HashVO hvo = (HashVO) node.getUserObject(); //
				if (hvo.getBooleanValue("isenable").booleanValue()) { //��������Ч��!
					al_hvs.add(hvo); //
				}
			}

			HashVO[] hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]); //
			String[][] str_begin_end_date = new String[hvs.length][2]; //
			StringBuilder str_refIds = new StringBuilder(); //
			StringBuilder str_refCodes = new StringBuilder(); //
			StringBuilder str_refNames = new StringBuilder(); //

			for (int i = 0; i < hvs.length; i++) {
				str_refIds.append(hvs[i].getStringValue("id") + ";"); //
				str_refCodes.append(hvs[i].getStringValue("type") + ";"); //
				str_refNames.append(hvs[i].getStringValue("name") + ";"); //
				str_begin_end_date[i][0] = getBeginDate(hvs[i].getStringValue("id"), hvs[i].getStringValue("type")); //
				str_begin_end_date[i][1] = getEndDate(hvs[i].getStringValue("id"), hvs[i].getStringValue("type")); //
			}

			HashVO hvo = new HashVO(); // 

			StringBuilder sb_querycondition = new StringBuilder(); //
			sb_querycondition.append("(");
			for (int i = 0; i < str_begin_end_date.length; i++) {
				if (i != 0) { //������ǵ�һ����Ҫ��or
					sb_querycondition.append(" or ");
				}
				sb_querycondition.append("(");
				sb_querycondition.append("{itemkey}>='" + str_begin_end_date[i][0] + "' and {itemkey}<='" + str_begin_end_date[i][1] + " 24:00:00'"); //
				sb_querycondition.append(")");
			}
			sb_querycondition.append(")");
			hvo.setAttributeValue("querycondition", sb_querycondition.toString()); //
			refItemVO = new RefItemVO(str_refIds.toString(), str_refCodes.toString(), str_refNames.toString(), hvo); //
		} else if (tabPanel_choosePeriod.getSelectedIndex() == 1) { //��������������ķ�ʽ!
			String str_date1 = daily_1.getDataStringValue(); ////
			String str_date2 = daily_2.getDataStringValue(); ////
			if (str_date1.compareTo(str_date2) > 0) {
				MessageBox.show(this, "��ʼʱ�����С�ڽ���ʱ��!!"); //
				return null;
			}
			HashVO hvo = new HashVO(); //
			hvo.setAttributeValue("querycondition", "({itemkey}>='" + str_date1 + "' and {itemkey}<='" + str_date2 + " 24:00:00')"); //
			refItemVO = new RefItemVO(str_date1 + ";" + str_date2 + "", "����", "��[" + str_date1 + "]��[" + str_date2 + "]֮��", hvo); //
		}

		if (refItemVO.getId().trim().equals("")) { //���Ϊ��,���п���ѡ����һ����Ч��ʱ��η�Χ!!
			MessageBox.show(this, "��ѡ��һ����Ч��ʱ���!"); //
			return null;
		}
		return refItemVO; //
	}

	/**
	 * ��ʼ����..
	 * @param stringValue
	 * @param stringValue2
	 * @return
	 */
	private String getBeginDate(String _dateValue, String _type) {
		if (_type.equals("��")) {
			return getBeginDateByYear(_dateValue); //
		} else if (_type.equals("��")) {
			return getBeginDateBySeason(_dateValue); //
		} else if (_type.equals("��")) {
			return getBeginDateByMonth(_dateValue); //
		} else if (_type.equals("��")) {
			return _dateValue; //
		}

		return null; //
	}

	/**
	 * ȡ�ý�������
	 * @param _dateValue
	 * @param _type
	 * @return
	 */
	private String getEndDate(String _dateValue, String _type) {
		if (_type.equals("��")) {
			return getEndDateByYear(_dateValue); //
		} else if (_type.equals("��")) {
			return getEndDateBySeason(_dateValue); //
		} else if (_type.equals("��")) {
			return getEndDateByMonth(_dateValue); //
		} else if (_type.equals("��")) {
			return _dateValue; //
		}

		return null; //
	}

	/**
	 * ������ȵĵ�һ��
	 * @param _year
	 * @return
	 */
	private String getBeginDateByYear(String _year) {
		return _year.substring(0, 4) + "-01-01";
	}

	/**
	 * ������ȵĵ�һ��
	 * @param _year
	 * @return
	 */
	private String getEndDateByYear(String _year) {
		return _year.substring(0, 4) + "-12-31";
	}

	/**
	 * ������ȵĵ�һ��
	 * @param _year
	 * @return
	 */
	private String getBeginDateBySeason(String _season) {
		String str_year = _season.substring(0, 4); //
		String str_season = _season.substring(5, 6); //
		if (str_season.equals("1")) {
			return str_year + "-01-01"; //
		} else if (str_season.equals("2")) {
			return str_year + "-04-01"; //
		} else if (str_season.equals("3")) {
			return str_year + "-07-01"; //
		} else if (str_season.equals("4")) {
			return str_year + "-10-01"; //
		} else {
			return null; //
		}
	}

	/**
	 * ���ؼ��ȵ����һ��
	 * @param _year
	 * @return
	 */
	private String getEndDateBySeason(String _season) {
		String str_year = _season.substring(0, 4); //
		String str_season = _season.substring(5, 6); //
		if (str_season.equals("1")) {
			return str_year + "-03-31"; //
		} else if (str_season.equals("2")) {
			return str_year + "-06-30"; //
		} else if (str_season.equals("3")) {
			return str_year + "-09-30"; //
		} else if (str_season.equals("4")) {
			return str_year + "-12-31"; //
		} else {
			return null; //
		}
	}

	/**
	 * ȡ��ĳһ������ʼ��
	 * @param _month
	 * @return
	 */
	private String getBeginDateByMonth(String _month) {
		String str_year = _month.substring(0, 4); //
		String str_month = _month.substring(5, 7); //
		return str_year + "-" + str_month + "-01"; //
	}

	/**
	 * ȡ��ĳһ������ʼ��
	 * @param _month
	 * @return
	 */
	private String getEndDateByMonth(String _month) {
		String str_year = _month.substring(0, 4); //
		String str_month = _month.substring(5, 7); //
		return str_year + "-" + str_month + "-" + getOneMonthDays(Integer.parseInt(str_year), Integer.parseInt(str_month)); //
	}

	private boolean existItem(String[] _array, String _item) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i].equals(_item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ȡ��ĳһ���µ��ܹ�����
	 * @param _year
	 * @param _month
	 * @return
	 */
	private int getOneMonthDays(int _year, int _month) {
		if (_month == 2) {
			if (_year % 4 == 0) {
				return 29;
			} else {
				return 28;
			}
		} else if (_month == 1 || _month == 3 || _month == 5 || _month == 7 || _month == 8 || _month == 10 || _month == 12) {
			return 31;
		} else {
			return 30; //
		}
	}

	public WLTTabbedPane getTabPanel_choosePeriod() {
		return tabPanel_choosePeriod;
	}

	public JTree getJtree() {
		return jtree;
	}

	public QueryDateModelPanel getDaily_1() {
		return daily_1;
	}

	public QueryDateModelPanel getDaily_2() {
		return daily_2;
	}

	class MyTimeTreeCellRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1150121537427225362L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel oldlabel = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus); //
			JLabel label = new JLabel(oldlabel.getText()); //
			label.setFont(oldlabel.getFont()); //
			label.setIcon(oldlabel.getIcon()); //           
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) value; //
			if (!thisNode.isRoot()) {
				HashVO hvo = (HashVO) (thisNode.getUserObject()); //
				if (!hvo.getBooleanValue("isenable")) { //�����Ч!
					label.setOpaque(true); //
					if (sel) {
						label.setBackground(Color.LIGHT_GRAY); //
						label.setForeground(Color.GRAY); //
					} else {
						label.setBackground(new Color(240, 240, 240)); //
						label.setForeground(Color.GRAY); //
					}
				} else {
					if (sel) {
						label.setOpaque(true); //
						label.setBackground(Color.YELLOW); //
						label.setForeground(Color.RED); //
					} else {
						label.setOpaque(false); //
					}
				}
			}

			return label; //
		}
	}

}
