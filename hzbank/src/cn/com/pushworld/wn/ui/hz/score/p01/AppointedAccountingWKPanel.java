package cn.com.pushworld.wn.ui.hz.score.p01;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;
import cn.com.infostrategy.ui.sysapp.other.BigFileUpload;
import cn.com.infostrategy.ui.sysapp.other.RefDialog_Month;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * zzl 2020-9-3 委派会履职考核 select * from v_sal_personinfo where STATIONKIND like
 * '%委派会计' select name,descr,weights from sal_person_check_list where 1=1 and
 * (type='101') select COLUMN_NAME from cols where TABLE_NAME='SAL_WPKJ_SCORE'
 */
<<<<<<< HEAD
public class AppointedAccountingWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener, BillListSelectListener {
    private BillListPanel listPanel=null;
    private WLTButton start_btn=null;
    private String selectDate = "";
    private BillListPanel userPanel=null;
    private WLTSplitPane splitPanel_all = null;
    private BillListPanel listPfPanel=null;
    private WLTButton save_btn=null;

    @Override
    public void initialize() {
        listPanel=new BillListPanel("SAL_WPKJ_SCORE_CODE1");
        start_btn=new WLTButton("开始评分");
        start_btn.addActionListener(this);
        listPanel.addBillListButton(start_btn);
        listPanel.repaintBillListButton();
        this.add(listPanel);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==start_btn){
            String [] datas=getDate(this);
            selectDate=datas[0].toString();
            if(datas.toString().equals("1") || Integer.parseInt(datas[1].toString())==1){
                viewScoreMode(selectDate);
            }
        }else if(actionEvent.getSource()==save_btn){
            save();
        }

    }

    /**
     * zzl
     * 评分保存
     */
    private void save(){
        BillVO [] vos=listPfPanel.getBillVOs();
        StringBuffer sb=new StringBuffer();
        int SCORE=0;
        int WEIGHTS=0;
        for(int i=0;i<vos.length;i++){
            if(vos[i].getStringValue("SCORE")==null || vos[i].getStringValue("SCORE").equals("") || vos[i].getStringValue("SCORE").equals(null)){
                sb.append("指标:【"+vos[i].getStringValue("name")+"】评分为空，请录入评分"+
                        System.getProperty("line.separator"));
            }else{
                SCORE=Math.abs(Integer.parseInt(vos[i].getStringValue("SCORE")));
            }
            WEIGHTS=Integer.parseInt(vos[i].getStringValue("WEIGHTS"));
            if(SCORE>WEIGHTS){
                sb.append("指标:【"+vos[i].getStringValue("name")+"】评分的绝对值大于权重分值请修改"+
                        System.getProperty("line.separator"));
            }
        }
        if(sb.length()>0){
            MessageBox.show(this,sb.toString());
            return;
        }
        UpdateSQLBuilder update=new UpdateSQLBuilder("SAL_WPKJ_SCORE");
        ArrayList list=new ArrayList();
        for(int i=0;i<vos.length;i++){
            update.setWhereCondition("name='"+vos[i].getStringValue("name")+"' and SCOREdate='"+vos[i].getStringValue("SCOREdate")+"' and username='"+vos[i].getStringValue("username")+"'");
            update.putFieldValue("SCORE",vos[i].getStringValue("SCORE"));
            update.putFieldValue("note",vos[i].getStringValue("note"));
            list.add(update.getSQL());
        }
        try {
            UIUtil.executeBatchByDS(null,list);
            MessageBox.show(this,"保存成功");
        } catch (Exception e) {
            MessageBox.show(this,"保存失败");
            e.printStackTrace();
        }
    }
    /**
     * zzl
     * 评分模板
     */
    private void viewScoreMode(String date){
        try {
            String [] all=UIUtil.getStringArrayFirstColByDS(null,"select username from SAL_WPKJ_SCORE where SCOREdate='"+date+"'");
            if(all.length<=0){
                insertSql(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BillDialog dialog=new BillDialog(this,"委派会计评分");
        userPanel=new BillListPanel("SAL_WPKJ_SCORE_CODE3");
//        userPanel.QueryDataByCondition("SCOREdate='"+date+"' group by userid");
        userPanel.queryDataByDS(null,"select username from SAL_WPKJ_SCORE where SCOREdate='"+date+"' group by username");
        userPanel.addBillListSelectListener(this);
        save_btn=new WLTButton("保存");
        save_btn.addActionListener(this);
        listPfPanel=new BillListPanel("SAL_WPKJ_SCORE_CODE2");
        listPfPanel.addBillListButton(save_btn);
        listPfPanel.repaintBillListButton();
        splitPanel_all=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, userPanel, listPfPanel); //
        splitPanel_all.setDividerLocation(300); //
        dialog.setSize(1600,800);
        dialog.add(splitPanel_all);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
        dialog.setLocation(x, y);
        dialog.setVisible(true);

    }

    /**
     * zzl
     * 开始插入评分数据
     * @param selectDate
     */
    private void insertSql(String selectDate){
        try {
            HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from v_sal_personinfo where STATIONKIND like '%委派会计'");
            HashVO [] zbvos=UIUtil.getHashVoArrayByDS(null,"select name,descr,weights from sal_person_check_list where 1=1  and (type='101')");
            String [] columns=UIUtil.getStringArrayFirstColByDS(null,"select COLUMN_NAME from cols where TABLE_NAME='SAL_WPKJ_SCORE'");
            ArrayList list=new ArrayList<String>();
            InsertSQLBuilder insert=new InsertSQLBuilder("SAL_WPKJ_SCORE");
            for(int i=0;i<vos.length;i++){
                for(int j=0;j<zbvos.length;j++){
                    String id=UIUtil.getSequenceNextValByDS(null,"S_SAL_WPKJ_SCORE");
                    for(int c=0;c<columns.length;c++){
                        if(columns[c].equals("ID")){
                            insert.putFieldValue("ID",id);
                        }else if(columns[c].equals("SCOREDATE")){
                            insert.putFieldValue("SCOREDATE",selectDate);
                        }else if(columns[c].equals("NAME") || columns[c].equals("DESCR") || columns[c].equals("WEIGHTS")){
                            insert.putFieldValue(columns[c],zbvos[j].getStringValue(columns[c]));
                        }else if(columns[c].equals("USERID") || columns[c].equals("USERCODE") || columns[c].equals("USERNAME")){
                            String ls=columns[c].replace("USER","");
                            insert.putFieldValue(columns[c],vos[i].getStringValue(ls));
                        }else{
                            insert.putFieldValue(columns[c],vos[i].getStringValue(columns[c]));
                        }

                    }
                    list.add(insert.getSQL());
                }
            }
            UIUtil.executeBatchByDS(null,list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 时间
     * @param _parent
     * @return
     */
    private String [] getDate(Container _parent) {
        String [] str=null;
        int a=0;
        try {
            RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "请选择上传数据的月份", new RefItemVO(selectDate, "", selectDate), null);
            chooseMonth.initialize();
            chooseMonth.setVisible(true);
            selectDate = chooseMonth.getReturnRefItemVO().getName();
            a=chooseMonth.getCloseType();
            str=new String[]{selectDate,String.valueOf(a)};
            return str;
        } catch (Exception e) {
            WLTLogger.getLogger(BigFileUpload.class).error(e);
        }
        return new String[]{"2013-08",String.valueOf(a)};
    }

    @Override
    public void onBillListSelectChanged(BillListSelectionEvent _event) {
        if(_event.getSource()==userPanel){
            BillVO billVO=userPanel.getSelectedBillVO();
            listPfPanel.queryDataByDS(null,"select * from SAL_WPKJ_SCORE where SCOREdate='"+selectDate+"' and username='"+billVO.getStringValue("username")+"'");
        }

    }

    @Override
    public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {

    }
=======
public class AppointedAccountingWKPanel extends AbstractWorkPanel implements
		ActionListener, BillTreeSelectListener, BillListSelectListener {
	private BillListPanel listPanel = null;
	private WLTButton start_btn = null;
	private String selectDate = "";
	private BillListPanel userPanel = null;
	private WLTSplitPane splitPanel_all = null;
	private BillListPanel listPfPanel = null;
	private WLTButton save_btn = null;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SAL_WPKJ_SCORE_CODE1");
		start_btn = new WLTButton("开始评分");
		start_btn.addActionListener(this);
		listPanel.addBillListButton(start_btn);
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == start_btn) {
			String[] datas = getDate(this);
			selectDate = datas[0].toString();
			if (datas.toString().equals("1")
					|| Integer.parseInt(datas[1].toString()) == 1) {
				viewScoreMode(selectDate);
			}
		} else if (actionEvent.getSource() == save_btn) {
			save();
		}

	}

	/**
	 * zzl 评分保存
	 */
	private void save() {
		BillVO[] vos = listPfPanel.getBillVOs();
		StringBuffer sb = new StringBuffer();
		double SCORE = 0;
		int WEIGHTS = 0;
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getStringValue("SCORE") == null
					|| vos[i].getStringValue("SCORE").equals("")
					|| vos[i].getStringValue("SCORE").equals(null)) {
				sb.append("指标:【" + vos[i].getStringValue("name")
						+ "】评分为空，请录入评分" + System.getProperty("line.separator"));
			} else {
				SCORE = Math.abs(Double.parseDouble(vos[i]
						.getStringValue("SCORE")));
			}
			WEIGHTS = Math.abs(Integer.parseInt(vos[i]
					.getStringValue("WEIGHTS")));
			if (SCORE > WEIGHTS) {
				sb.append("指标:【" + vos[i].getStringValue("name")
						+ "】评分的绝对值大于权重分值请修改"
						+ System.getProperty("line.separator"));
			}
		}
		if (sb.length() > 0) {
			MessageBox.show(this, sb.toString());
			return;
		}
		UpdateSQLBuilder update = new UpdateSQLBuilder("SAL_WPKJ_SCORE");
		ArrayList list = new ArrayList();
		for (int i = 0; i < vos.length; i++) {
			update.setWhereCondition("name='" + vos[i].getStringValue("name")
					+ "' and SCOREdate='" + vos[i].getStringValue("SCOREdate")
					+ "' and username='" + vos[i].getStringValue("username")
					+ "'");
			update.putFieldValue("SCORE", vos[i].getStringValue("SCORE"));
			update.putFieldValue("note", vos[i].getStringValue("note"));
			list.add(update.getSQL());
		}
		try {
			UIUtil.executeBatchByDS(null, list);
			MessageBox.show(this, "保存成功");
		} catch (Exception e) {
			MessageBox.show(this, "保存失败");
			e.printStackTrace();
		}
	}

	/**
	 * zzl 评分模板
	 */
	private void viewScoreMode(String date) {
		try {
			String[] all = UIUtil.getStringArrayFirstColByDS(null,
					"select username from SAL_WPKJ_SCORE where SCOREdate='"
							+ date + "' and SCORE is null");
			if (all.length <= 0) {
				insertSql(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		BillDialog dialog = new BillDialog(this, "委派会计评分");
		userPanel = new BillListPanel("SAL_WPKJ_SCORE_CODE3");
		// userPanel.QueryDataByCondition("SCOREdate='"+date+"' group by userid");
		userPanel.queryDataByDS(null,
				"select username from SAL_WPKJ_SCORE where SCOREdate='" + date
						+ "' group by username");
		userPanel.addBillListSelectListener(this);
		save_btn = new WLTButton("保存");
		save_btn.addActionListener(this);
		listPfPanel = new BillListPanel("SAL_WPKJ_SCORE_CODE2");
		listPfPanel.addBillListButton(save_btn);
		listPfPanel.repaintBillListButton();
		splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
				userPanel, listPfPanel); //
		splitPanel_all.setDividerLocation(300); //
		dialog.setSize(1600, 800);
		dialog.add(splitPanel_all);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
		int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
		dialog.setLocation(x, y);
		dialog.setVisible(true);

	}

	/**
	 * zzl 开始插入评分数据
	 * 
	 * @param selectDate
	 */
	private void insertSql(String selectDate) {
		try {
			HashVO[] vos = UIUtil
					.getHashVoArrayByDS(null,
							"select * from v_sal_personinfo where STATIONKIND like '%委派会计'");
			HashVO[] zbvos = UIUtil
					.getHashVoArrayByDS(
							null,
							"select name,descr,weights from sal_person_check_list where 1=1  and (type='101')");
			String[] columns = UIUtil
					.getStringArrayFirstColByDS(null,
							"select COLUMN_NAME from cols where TABLE_NAME='SAL_WPKJ_SCORE'");
			ArrayList list = new ArrayList<String>();
			// 增加其他三项考核(现金管理，授权业务，集中作业)
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			List<HashMap<String, String>> listVos = service
					.getKJHandSroce(selectDate);
			InsertSQLBuilder insert = new InsertSQLBuilder("SAL_WPKJ_SCORE");
			for (int i = 0; i < vos.length; i++) {
				String userCode = vos[i].getStringValue("code");// 获取到当前用户的userCode
				for (int j = 0; j < zbvos.length; j++) {
					String id = UIUtil.getSequenceNextValByDS(null,
							"S_SAL_WPKJ_SCORE");
					for (int c = 0; c < columns.length; c++) {
						if (columns[c].equals("ID")) {
							insert.putFieldValue("ID", id);
						} else if (columns[c].equals("SCOREDATE")) {
							insert.putFieldValue("SCOREDATE", selectDate);
						} else if (columns[c].equals("DESCR")
								|| columns[c].equals("WEIGHTS")) {
							insert.putFieldValue(columns[c],
									zbvos[j].getStringValue(columns[c]));

						} else if (columns[c].equals("NAME")) { // 插入考核项名称，如果name 为 授权业务 现金管理  集中作业 时，插入已经计算好的分数；
							insert.putFieldValue(columns[c],
									zbvos[j].getStringValue(columns[c]));
							String name = zbvos[j].getStringValue(columns[c]);
							insert.putFieldValue(columns[c], name);
							if ("授权业务".equals(name)) {
								insert.putFieldValue("score", listVos.get(0)
										.get(userCode) == null ? "0.0"
										: listVos.get(0).get(userCode));
							} else if ("现金管理".equals(name)) {
								insert.putFieldValue("score", listVos.get(1)
										.get(userCode) == null ? "0.0"
										: listVos.get(1).get(userCode));
							} else if ("集中作业".equals(name)) {
								insert.putFieldValue("score", listVos.get(2)
										.get(userCode) == null ? "0.0"
										: listVos.get(2).get(userCode));
							} else {
								insert.putFieldValue("score", "");
							}
						} else if (columns[c].equals("USERID")
								|| columns[c].equals("USERCODE")
								|| columns[c].equals("USERNAME")) {
							String ls = columns[c].replace("USER", "");
							insert.putFieldValue(columns[c],
									vos[i].getStringValue(ls));
						} else if(!"SCORE".equals(columns[c])) {
//							if (!"SCORE".equals(columns[c])) {
								insert.putFieldValue(columns[c],
										vos[i].getStringValue(columns[c]));
//							}
						}
					}

					list.add(insert.getSQL());
				}

			}
			UIUtil.executeBatchByDS(null, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 时间
	 * 
	 * @param _parent
	 * @return
	 */
	private String[] getDate(Container _parent) {
		String[] str = null;
		int a = 0;
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent,
					"请选择上传数据的月份", new RefItemVO(selectDate, "", selectDate),
					null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			a = chooseMonth.getCloseType();
			str = new String[] { selectDate, String.valueOf(a) };
			return str;
		} catch (Exception e) {
			WLTLogger.getLogger(BigFileUpload.class).error(e);
		}
		return new String[] { "2013-08", String.valueOf(a) };
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == userPanel) {
			BillVO billVO = userPanel.getSelectedBillVO();
			listPfPanel.queryDataByDS(
					null,
					"select * from SAL_WPKJ_SCORE where SCOREdate='"
							+ selectDate + "' and username='"
							+ billVO.getStringValue("username") + "'");
		}

	}

	@Override
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {

	}
>>>>>>> 84b932ce475d2fe124188eefdc0df6c22801efd2
}
