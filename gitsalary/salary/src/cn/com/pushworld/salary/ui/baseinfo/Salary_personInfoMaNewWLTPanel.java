package cn.com.pushworld.salary.ui.baseinfo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardFrame;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ��н������йص���Ա��Ϣά������ ������һ����Ϣ��sal_personInfo ���ű������洢��׼�������Ϣ һЩ������Ϣ��Ҫ���û����е���
 * Ӧ����һ�����û����������Ϣ�Ĺ��� id��ʹ���û����id�����ű����Ϣ���ͬ�� ��ʱ������
 */
public class Salary_personInfoMaNewWLTPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, ActionListener {
	private BillTreePanel corp_tree = null; // ������
	private BillListPanel user_list = null; // ��Ա�б�
	private WLTSplitPane main_spit = null; // ���ָ����
	private WLTButton editBtn, watchBtn; 

	public void initialize() {
		corp_tree = new BillTreePanel("PUB_CORP_DEPT_SELF");
		corp_tree.queryDataByCondition(" 1=1 ");
		corp_tree.addBillTreeSelectListener(this);
		user_list = new BillListPanel("PUB_USER_CODE2"); // PUB_USER_POST_DEFAULT
		user_list.setItemVisible("PK_DEPT", false);
		user_list.setBillListTitleName("Ա����Ϣ-�û����");
		initBtn();
		main_spit = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corp_tree, user_list);
		main_spit.setDividerLocation(200);
		this.add(main_spit, BorderLayout.CENTER);
	}
	
	public void initBtn() {
		editBtn = new WLTButton("�༭��Ա��Ϣ", "user_edit.png");
		watchBtn = new WLTButton("�鿴��Ա��Ϣ", "user.gif");
		editBtn.addActionListener(this);
		watchBtn.addActionListener(this);
		user_list.addBatchBillListButton(new WLTButton[] {editBtn, watchBtn});
		user_list.repaintBillListButton();
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		if (event.getCurrSelectedVO() != null && event.getCurrSelectedVO().getPkValue() != null) {
			String str_currdeptid = event.getCurrSelectedVO().getStringValue("id");
//			user_list.QueryDataByCondition("id in (select userid from pub_user_post where userdept='" + str_currdeptid + "' ) ");
			user_list.QueryData("select u.*  from (select distinct userid from pub_user_post where userdept='" + str_currdeptid + "')  p  left join  pub_user u on u.id=p.userid ");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editBtn) {
			onEdit(true); 
		} else if (e.getSource() == watchBtn) {
			onEdit(false);
		}
	}
	
	public void onEdit(boolean iseditable) {
		BillVO uservo = user_list.getSelectedBillVO();
		if (uservo == null) {
			MessageBox.show(user_list, "��ѡ��һ����Ա��Ϣ�����д˲���!");
			return;
		}
		String str_useridid = uservo.getPkValue();
		BillCardPanel user_card = new BillCardPanel("SAL_PERSONINFO_CODE1");
		user_card.queryDataByCondition("id=" + str_useridid);
		String edittype = WLTConstants.BILLDATAEDITSTATE_INIT;
		if (iseditable) {
			if (user_card.getRealValueAt("id") == null || "".equals(user_card.getRealValueAt("id"))) {
				user_card.setRealValueAt("id", str_useridid);
				user_card.setRealValueAt("name", uservo.getStringValue("NAME"));
				user_card.setRealValueAt("code", uservo.getStringValue("CODE"));
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
				edittype = WLTConstants.BILLDATAEDITSTATE_INSERT;
				user_card.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			} else {
				user_card.setEditableByEditInit();
				edittype = WLTConstants.BILLDATAEDITSTATE_UPDATE;
				user_card.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);	
			}	
			BillCardDialog bd = new BillCardDialog(user_list, "��Ա��Ϣ�༭", user_card, edittype);
			bd.setVisible(true);
		} else {
			BillCardFrame bf = new BillCardFrame(user_list, "��Ա��Ϣ�鿴", user_card, edittype);
			bf.setVisible(true);
		}
	}
}
