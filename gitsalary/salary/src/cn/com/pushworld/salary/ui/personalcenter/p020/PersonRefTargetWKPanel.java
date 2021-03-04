package cn.com.pushworld.salary.ui.personalcenter.p020;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 员工相关的指标自主查询 包含与个人关联的员工定性、定量指标 与个人部门相关的部门定性、定量指标
 * 
 * @author Administrator
 * 
 */
public class PersonRefTargetWKPanel extends AbstractWorkPanel implements ChangeListener {
	private WLTTabbedPane maintab = null;
	private BillListPanel personTargetList, deptTargetList = null;

	// 页面分两个页签，员工指标、部门指标
	// 每个页签一个列表
	private boolean showweights = TBUtil.getTBUtil().getSysOptionBooleanValue("个人中心查看指标权重是否可见", true);

	public void initialize() {
		maintab = new WLTTabbedPane();
		personTargetList = new BillListPanel("SAL_PERSON_CHECK_LIST_PERSONCENTER");
		init();
		maintab.addTab("员工指标", UIUtil.getImage("office_015.gif"), personTargetList);
		maintab.addTab("部门指标", UIUtil.getImage("flag_green.png"), new JPanel(new BorderLayout()));
		maintab.addChangeListener(this);
		this.add(maintab);
	}

	public void init() {
		try {
			String userid = ClientEnvironment.getCurrLoginUserVO().getId();
			String[][] stationkinds = UIUtil.getStringArrayByDS(null, "select p.stationkind,p.id from pub_user_post up left join pub_post p on up.postid=p.id where up.userid=" + userid);
			StringBuffer sb = new StringBuffer("select id from sal_person_check_type where 1=2 ");
			StringBuffer sb2 = new StringBuffer("select id from sal_person_check_post where 1=2 ");
			StringBuffer sb3 = new StringBuffer("select * from sal_post_check_list where 1=2 ");
			StringBuffer sb4 = new StringBuffer("select name,descr,type,weights from sal_target_list where 1=2 ");
			for (int i = 0; i < stationkinds.length; i++) {
				if (stationkinds[i][0] != null && !"".equals(stationkinds[i][0])) {
					sb.append(" or stationkinds like '%;" + stationkinds[i][0] + ";%' ");
				}
				if (stationkinds[i][1] != null && !"".equals(stationkinds[i][1])) {
					sb2.append(" or postid like '%;" + stationkinds[i][0] + ";%' ");
					sb3.append(" or checkedpost = '" + stationkinds[i][1] + "' ");
					sb4.append(" or rleader like '%;" + stationkinds[i][1] + ";%' ");
				}
			}
			sb4.append(" and state ='参与考核'");//只看参与考核的指标
			String[] checkobj = UIUtil.getStringArrayFirstColByDS(null, sb.toString());
			String[][] targets = UIUtil.getStringArrayByDS(null, "select name,name,targettype,weights from sal_person_check_list where type in (" + TBUtil.getTBUtil().getInCondition(checkobj) + ") and state ='参与考核' ");
			String[] quantifytargetsid = UIUtil.getStringArrayFirstColByDS(null, sb2.toString());
			String[][] quantifytargets = UIUtil.getStringArrayByDS(null, "select t1.name,t1.descr,t1.targettype,t2.weights from sal_person_check_list t1 left join sal_person_check_post t2 on t2.targetid = t1.id  where t1.state='参与考核' and t2.id in (" + TBUtil.getTBUtil().getInCondition(quantifytargetsid) + ")");
			HashVO[] ggtarget = UIUtil.getHashVoArrayByDS(null, sb3.toString());
			String[][] depttarget = UIUtil.getStringArrayByDS(null, sb4.toString());
			// 如果此人有高管定性指标则放在前面
			if (ggtarget != null) {
				for (int i = 0; i < ggtarget.length; i++) {
					personTargetList.insertEmptyRow(i);
					personTargetList.setValueAt(new ComBoxItemVO("高管定性指标", "", "高管定性指标"), i, "targettype");
					personTargetList.setValueAt(new StringItemVO("高管定性指标"), i, "name");
					personTargetList.setValueAt(new StringItemVO(ggtarget[i].getStringValue("name")), i, "descr");
				}
			}
			// 其次是定量指标
			if (quantifytargets != null && quantifytargets.length > 0) {
				for (int i = 0; i < quantifytargets.length; i++) {
					personTargetList.insertEmptyRow(personTargetList.getRowCount());
					personTargetList.setValueAt(new ComBoxItemVO(quantifytargets[i][2], "", quantifytargets[i][2]), personTargetList.getRowCount() - 1, "targettype");
					personTargetList.setValueAt(new StringItemVO(quantifytargets[i][0]), personTargetList.getRowCount() - 1, "name");
					personTargetList.setValueAt(new StringItemVO(quantifytargets[i][1]), personTargetList.getRowCount() - 1, "descr");
					personTargetList.setValueAt(new StringItemVO(quantifytargets[i][3]), personTargetList.getRowCount() - 1, "weights");
				}
			}
			// 然后是定性指标
			if (targets != null && targets.length > 0) {
				for (int i = 0; i < targets.length; i++) {
					personTargetList.insertEmptyRow(personTargetList.getRowCount());
					personTargetList.setValueAt(new ComBoxItemVO(targets[i][2], "", targets[i][2]), personTargetList.getRowCount() - 1, "targettype");
					personTargetList.setValueAt(new StringItemVO(targets[i][0]), personTargetList.getRowCount() - 1, "name");
					personTargetList.setValueAt(new StringItemVO(targets[i][1]), personTargetList.getRowCount() - 1, "descr");
					personTargetList.setValueAt(new StringItemVO(targets[i][3]), personTargetList.getRowCount() - 1, "weights");
				}
			}
			// 最后是部门指标
			if (depttarget != null && depttarget.length > 0) {
				for (int i = 0; i < depttarget.length; i++) {
					personTargetList.insertEmptyRow(personTargetList.getRowCount());
					personTargetList.setValueAt(new ComBoxItemVO(depttarget[i][2], "", depttarget[i][2]), personTargetList.getRowCount() - 1, "targettype");
					personTargetList.setValueAt(new StringItemVO(depttarget[i][0]), personTargetList.getRowCount() - 1, "name");
					personTargetList.setValueAt(new StringItemVO(depttarget[i][1]), personTargetList.getRowCount() - 1, "descr");
					personTargetList.setValueAt(new StringItemVO(depttarget[i][3]), personTargetList.getRowCount() - 1, "weights");
				}
			}
			personTargetList.autoSetRowHeight();
			if (!showweights) {
				personTargetList.setItemVisible("weights", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (maintab.getSelectedIndex() == 1) {
			if (deptTargetList == null) {
				new SplashWindow(maintab, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						deptTargetList = new BillListPanel("SAL_TARGET_LIST_PERSONCENTER");
						try {
							String userid = ClientEnvironment.getCurrLoginUserVO().getId();
							HashVO[] deptvos = UIUtil.getHashVoArrayByDS(null, "select * from pub_user_post where userid=" + userid);
							if (deptvos != null && deptvos.length > 0) {
								String maindeptid = null;
								for (int i = 0; i < deptvos.length; i++) {
									if ("Y".equals(deptvos[i].getStringValue("isdefault"))) {
										maindeptid = deptvos[i].getStringValue("userdept");
										if (maindeptid != null && !"".equals(maindeptid)) {
											break;
										}
									}
								}
								if (maindeptid == null || "".equals(maindeptid)) {
									maindeptid = deptvos[0].getStringValue("userdept");
								}
								String[] targetids = UIUtil.getStringArrayFirstColByDS(null, "select targetid from sal_target_checkeddept where deptid like '%;" + maindeptid + ";%'");
								deptTargetList.setDataFilterCustCondition("checkeddept like '%;" + maindeptid + ";%' or id in (" + TBUtil.getTBUtil().getInCondition(targetids) + ") ");
								deptTargetList.QueryDataByCondition(" state='参与考核'");//只看参与考核的指标
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						maintab.getComponentAt(1).add(deptTargetList);
						if (!showweights) {
							deptTargetList.setItemVisible("weights", false);
						}
					}
				});
			}
		}
	}
}
