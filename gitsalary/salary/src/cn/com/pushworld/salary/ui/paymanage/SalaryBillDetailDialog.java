package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ClientSequenceFactory;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

public class SalaryBillDetailDialog extends BillDialog implements BillTreeSelectListener, ActionListener {
	private BillTreePanel corp_tree = null;
	private BillListPanel bl = null;
	private WLTSplitPane wsp = null;
	private String salarybillid = null;
	private BillVO salarybillvo = null;
	private WLTButton editModel, reCreateBill, editColumnDef = null;
	private SalaryServiceIfc service;

	public SalaryBillDetailDialog(Container _parent, BillVO _salarybillvo) {
		super(_parent, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
		this.setTitle("查看工资单明细");
		this.salarybillvo = _salarybillvo;
		this.salarybillid = _salarybillvo.getPkValue();
		init();
	}

	public void init() {
		corp_tree = new BillTreePanel("PUB_CORP_DEPT_SELF");
		corp_tree.queryDataByCondition(" 1=1 ");
		corp_tree.addBillTreeSelectListener(this);
		getSalaryBillDetailList(salarybillid);
		wsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corp_tree, bl);
		this.add(wsp, BorderLayout.CENTER);
	}

	public void getSalaryBillDetailList(String salarybillid) {
		try {
			HashVO[] allcolumn = UIUtil.getHashVoArrayByDS(null, "select s.viewname, s.factorid from sal_salarybill_factor s left join sal_factor_def f on s.factorid=f.id where s.salarybillid=" + salarybillid + " order by s.seq");
			ArrayList allcolumnname = new ArrayList();
			ArrayList allcolumnkey = new ArrayList();
			ArrayList allcolumnkeyandname = new ArrayList();
			String key = null;
			String name = null;
			for (int i = 0; i < allcolumn.length; i++) {
				key = allcolumn[i].getStringValue("factorid");
				name = allcolumn[i].getStringValue("viewname");
				allcolumnkeyandname.add(new String[] { key, name, "4", "Y", name.getBytes().length * 10 + "" });
				//				allcolumnname.add(allcolumn[i].getStringValue("viewname"));
				//				allcolumnkey.add(allcolumn[i].getStringValue("factorid"));
			}
			//			HashVOStruct hvst = new HashVOStruct(); //
			//			hvst.setHeaderName((String[])allcolumnname.toArray(new String[0]));
			//			hvst.setHashVOs(null);
			DefaultTMO tmo = new DefaultTMO("员工工资单", new String[] { "itemkey", "itemname", "listiseditable", "iswrap", "listwidth" }, (String[][]) allcolumnkeyandname.toArray(new String[0][0]));
			bl = new BillListPanel(tmo);
			editModel = new WLTButton("调整工资帐套", "book_edit.png");
			editColumnDef = new WLTButton("调整工资单项目定义", "office_096.gif");
			editColumnDef.setToolTipText("选中某一列");
			reCreateBill = new WLTButton("重新生成工资单明细", "zt_028.gif");
			editModel.addActionListener(this);
			editColumnDef.addActionListener(this);
			reCreateBill.addActionListener(this);
			bl.addBatchBillListButton(new WLTButton[] { editModel, editColumnDef, reCreateBill });
			bl.repaintBillListButton();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		final BillVO[] allnodes = corp_tree.getSelectedChildPathBillVOs();
		if (allnodes != null && allnodes.length > 0) {
			new SplashWindow(bl, "查询中,请稍候...", new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent arg0) {
					try {
						List<String> allids = new ArrayList<String>();
						for (int i = 0; i < allnodes.length; i++) {
							allids.add(allnodes[i].getPkValue());
						}
						//						bl.QueryDataByCondition(" maindeptid in (" + UIUtil.getSubSQLFromTempSQLTableByIDs((String[]) allids.toArray(new String[0])) + ") ");
						HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from sal_salarybill_detail d left join v_sal_personinfo v on d.userid=v.id left join pub_corp_dept c on v.maindeptid=c.id where d.salarybillid=" + salarybillid + " and v.maindeptid in (" + UIUtil.getSubSQLFromTempSQLTableByIDs((String[]) allids.toArray(new String[0])) + ") order by c.linkcode,v.code, d.seq");
						bl.clearTable();
						if (vos != null && vos.length > 0) {
							LinkedHashMap<String, HashVO> map = new LinkedHashMap<String, HashVO>();
							String userid = null;
							String key = null;
							String value = null;
							for (int i = 0; i < vos.length; i++) {
								userid = vos[i].getStringValue("userid");
								key = vos[i].getStringValue("factorid");
								value = vos[i].getStringValue("factorvalue");
								if (map.containsKey(userid)) {
									((HashVO) map.get(userid)).setAttributeValue(key, value);
								} else {
									HashVO vo = new HashVO();
									vo.setAttributeValue(key, value);
									map.put(userid, vo);
								}
							}
							bl.putValue((HashVO[]) map.values().toArray(new HashVO[0]));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editModel) {
			onEditModel();
		} else if (e.getSource() == editColumnDef) {
			onEditColumnDef();
		} else if (e.getSource() == reCreateBill) {
			onReCreateBill();
		}
	}

	public void onEditModel() {
		BillDialog bd = new BillDialog(this, "调整工资帐套");
		bd.setLayout(new BorderLayout());
		bd.setSize(800, 600);
		bd.locationToCenterPosition();
		Salary_AccountSetWKPanel p = new Salary_AccountSetWKPanel(salarybillvo.getStringValue("sal_account_setid"));
		p.initialize();
		bd.add(p.getAccount_fa_list(), BorderLayout.CENTER);
		bd.setVisible(true);
	}

	public void onEditColumnDef() {
		BillListDialog bld = new BillListDialog(bl, "调整工资单项目定义", "SAL_FACTOR_DEF_CODE1", 810, 600);
		bld.getBilllistPanel().setTitleLabelText("工资单项目定义");
		bld.getBilllistPanel().setDataFilterCustCondition("id in (select factorid from sal_account_factor where accountid=" + salarybillvo.getStringValue("sal_account_setid") + ")");
		bld.getBilllistPanel().QueryDataByCondition(" 1=1 ");
		bld.getBilllistPanel().getBillListBtnPanel().removeAllButtons();
		bld.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_POPEDIT));
		bld.getBilllistPanel().repaintBillListButton();
		if (bld.getBtn_confirm() != null) {
			bld.getBtn_confirm().setVisible(false);
		}

		if (bld.getBtn_cancel() != null) {
			bld.getBtn_cancel().setText("确定");
			bld.getBtn_cancel().setVisible(true);
		}
		bld.setVisible(true);
	}

	public void onReCreateBill() {
		if (MessageBox.showConfirmDialog(bl, "重新生成工资单明细将删除目前的明细,您确定重新生成明细吗?", "提示", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			new SplashWindow(this, "工资单生成中,请稍候...", new AbstractAction() {
				public void actionPerformed(final ActionEvent arg0) {
					Timer timer = new Timer();
					try {
						timer.schedule(new TimerTask() {
							SplashWindow w = (SplashWindow) arg0.getSource();

							public void run() { // 需要用线程来控制是否已经把数据加载进来了。
								String schedule = null;
								Object obj[] = null;
								try {
									obj = (Object[]) getService().getRemoteActionSchedule("", "工资计算");
								} catch (Exception e) {
									e.printStackTrace();
									this.cancel();
								}
								if (obj != null) {
									schedule = (String) obj[0];
									w.setWaitInfo(schedule);
								} else {
									w.setWaitInfo("系统正在努力计算中...");
								}
							}
						}, 20, 1000);
						UpdateSQLBuilder update = new UpdateSQLBuilder(salarybillvo.getSaveTableName());
						update.putFieldValue("blcorp", ClientEnvironment.getCurrSessionVO().getLoginUserPKDept());
						update.putFieldValue("createuser", ClientEnvironment.getCurrSessionVO().getLoginUserId());
						update.putFieldValue("createdate", UIUtil.getCurrDate());
						update.setWhereCondition(" id = " + salarybillvo.getPkValue());
						List sql = new ArrayList();
						sql.add(update);
						DeleteSQLBuilder dsb1 = new DeleteSQLBuilder("sal_salarybill_factor");
						dsb1.setWhereCondition("salarybillid=" + salarybillvo.getPkValue());
						sql.add(dsb1.getSQL());
						DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_salarybill_detail");
						dsb2.setWhereCondition("salarybillid=" + salarybillvo.getPkValue());
						sql.add(dsb2.getSQL());
						getService().onCreateSalaryBill(sql, salarybillvo.getPkValue(), salarybillvo.getStringValue("sal_account_setid"), salarybillvo.getStringValue("monthly"),new BillVO());
						MessageBox.show(bl, "操作成功!");
						// 刷新一下
						refresh(salarybillvo.getPkValue());
						timer.cancel();
					} catch (Exception e) {
						timer.cancel();
						e.printStackTrace();
						MessageBox.show(bl, "生成工资单发生异常请再次尝试或与管理员联系!");
						closeType = -1;
						return;
					}
				}
			}, false);
		}
	}

	public void refresh(String id) {
		getSalaryBillDetailList(salarybillid);
		wsp.setRightComponent(bl);
	}

	public String getSeq(String seqname) throws Exception {
		ClientSequenceFactory fac = ClientSequenceFactory.getInstance();
		Field f = ClientSequenceFactory.class.getDeclaredField("li_batchnumber");
		f.setAccessible(true);
		f.setInt(fac, 10000);
		String seq = fac.getSequence(seqname) + "";
		f.setInt(fac, 10);
		return seq;
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

}
