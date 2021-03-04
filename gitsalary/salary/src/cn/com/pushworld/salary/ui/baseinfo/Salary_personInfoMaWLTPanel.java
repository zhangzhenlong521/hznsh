package cn.com.pushworld.salary.ui.baseinfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 与薪酬计算有关的人员信息维护界面 新增了一张信息表sal_personInfo 这张表用来存储精准的相关信息 一些基础信息需要从用户表中导入
 * 应该有一个从用户表导入基础信息的功能 id就使用用户表的id，两张表的信息如何同步 暂时不考虑
 */
public class Salary_personInfoMaWLTPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener {
	private BillTreePanel corp_tree = null; // 机构树
	private BillListPanel station_list = null; // 岗位列表
	private BillCardPanel station_card = null; // 岗位卡片
	private BillListPanel user_list = null; // 人员列表
	private JPanel info_card = null; // 人员卡片
	private BillCardPanel user_card = null;
	private WLTSplitPane main_spit = null; // 主分隔面板
	private WLTSplitPane rigth_spit = null; // 右边分隔面板
	private WLTSplitPane station_spit = null; // 岗位分隔面板
	private WLTSplitPane user_spit = null; // 用户分隔面板

	public void initialize() {
		corp_tree = new BillTreePanel("PUB_CORP_DEPT_SELF");
		corp_tree.queryDataByCondition(" 1=1 ");
		corp_tree.addBillTreeSelectListener(this);
		station_list = new BillListPanel("PUB_POST_CODE_SALARY");
		station_list.addBillListSelectListener(this);
		user_list = new BillListPanel("PUB_USER_POST_DEFAULT");
		user_list.setBillListTitleName("员工信息-用户相关");
		user_list.addBillListSelectListener(this);
		info_card = new JPanel();
		info_card.setLayout(new BorderLayout());
		info_card.setBackground(LookAndFeel.defaultShadeColor1);
		info_card.setUI(new WLTPanelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE, Color.WHITE, false));
		station_spit = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, station_list, user_list);
		station_spit.setDividerLocation(300);
		rigth_spit = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, station_spit, info_card);
		rigth_spit.setDividerLocation(350);
		rigth_spit.setDividerSize(1);
		main_spit = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corp_tree, rigth_spit);
		main_spit.setDividerLocation(200);
		this.add(main_spit, BorderLayout.CENTER);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		if (event.getCurrSelectedVO() != null && event.getCurrSelectedVO().getPkValue() != null) {
			String str_currdeptid = event.getCurrSelectedVO().getStringValue("id");
			station_list.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq");
			user_list.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq");
		}
	}
	
	public void onStationListSelect(final BillListSelectionEvent _event) {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String str_postid = _event.getCurrSelectedVO().getStringValue("id");
				user_list.queryDataByCondition("postid='" + str_postid + "'", "seq");
				if (station_card == null) {
					station_card = new BillCardPanel(station_list.getTempletVO());
					station_card.addBillCardButton(WLTButton.createButtonByType(WLTButton.CARD_SAVE));
					station_card.getBillCardBtn("保存").addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (station_card.checkValidate(station_card)) {
								try {
									station_card.updateData();
									MessageBox.show(station_card, "保存成功!");
									station_list.refreshCurrSelectedRow();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					});
					station_card.repaintBillCardButton();
				}
				station_card.queryDataByCondition("id=" + str_postid);
				if (station_card.getRealValueAt("id") == null || "".equals(station_card.getRealValueAt("id"))) {
					station_card.setRealValueAt("id", str_postid);
					station_card.setEditableByInsertInit();
					station_card.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				} else {
					station_card.setEditableByEditInit();
					station_card.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);	
				}
				info_card.removeAll();
				info_card.add(station_card, BorderLayout.CENTER);
				info_card.updateUI();
			}
		});
	}
	
	public void onUserListSelect(final BillListSelectionEvent _event) {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				station_list.clearSelection();
				String str_useridid = _event.getCurrSelectedVO().getStringValue("userid");
				if (user_card == null) {
					user_card = new BillCardPanel("SAL_PERSONINFO_CODE1");
					user_card.addBillCardButton(WLTButton.createButtonByType(WLTButton.CARD_SAVE));
					user_card.getBillCardBtn("保存").addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							user_card.stopEditing();
							if (user_card.checkValidate(user_card)) {
								try {
									user_card.updateData();
									MessageBox.show(user_card, "保存成功!");
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					});
					user_card.repaintBillCardButton();
				}
				user_card.queryDataByCondition("id=" + str_useridid);
				if (user_card.getRealValueAt("id") == null || "".equals(user_card.getRealValueAt("id"))) {
					user_card.setRealValueAt("id", str_useridid);
					user_card.setRealValueAt("name", _event.getCurrSelectedVO().getStringValue("username"));
					user_card.setRealValueAt("code", _event.getCurrSelectedVO().getStringValue("usercode"));
					//
					try {
						HashVO[] all = UIUtil.getHashVoArrayByDS(null, "select userdept, isdefault, postid from pub_user_post where userid='" + str_useridid + "' and userdept in (select id from pub_corp_dept) order by id asc");
						if (all != null && all.length > 0) {
							String maindeptid = all[0].getStringValue("userdept");
							for (int i = 0 ; i < all.length ; i++) {
								if ("Y".equals(all[i].getStringValue("isdefault"))) {
									maindeptid = all[i].getStringValue("userdept");
									break;
								}
							}
							List postid = new ArrayList();
							for (int i = 0 ; i < all.length ; i++) {
								if (all[i].getStringValue("postid") != null && !"".equals(all[i].getStringValue("postid"))) {
									postid.add(all[i].getStringValue("postid"));
								}
							}
//							user_card.setRealValueAt("maindeptid", maindeptid);
							FrameWorkCommServiceIfc commService = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
							HashMap returnMap = commService.getTreePathNameByRecords(null, "pub_corp_dept", "id", "name", "parentid", new String[]{maindeptid});
							user_card.setValueAt("maindeptid", new RefItemVO(maindeptid, "", returnMap.get(maindeptid) + ""));
							String[][] mainpost = UIUtil.getStringArrayByDS(null, "select name,stationkind from pub_post where deptid='" + maindeptid + "' and name is not null and id in (" + TBUtil.getTBUtil().getInCondition(postid) + ") order by seq, id ");
							if (mainpost != null && mainpost.length > 0) {
								user_card.setValueAt("mainstation", mainpost[0][0]);
								user_card.setRealValueAt("stationkind", mainpost[0][1]);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					user_card.setEditableByInsertInit();
					user_card.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				} else {
					user_card.setEditableByEditInit();
					user_card.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);	
				}
				info_card.removeAll();
				info_card.add(user_card, BorderLayout.CENTER);
				info_card.updateUI();
			}
		});
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == station_list && station_list != null) {
			onStationListSelect(_event);
		} else if (_event.getSource() == user_list && user_list != null) {
			onUserListSelect(_event);
		}

	}
}
