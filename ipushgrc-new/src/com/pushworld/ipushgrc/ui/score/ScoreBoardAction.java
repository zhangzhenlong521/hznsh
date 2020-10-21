package com.pushworld.ipushgrc.ui.score;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.ui.score.p020.RegisterEditWKPanel;

/**
 * 首页违规积分点击事件【李春娟/2013-06-05】
 * @author lcj
 *
 */
public class ScoreBoardAction extends AbstractAction {
	private JPanel deskTopPanel;
	private String viewtype = TBUtil.getTBUtil().getSysOptionStringValue("违规积分通知单查看方式", "WORD");
	private HashVO selectvo = null;

	public void actionPerformed(ActionEvent e) {
		deskTopPanel = (JPanel) this.getValue("DeskTopPanel");
		HashVO selectVO = (HashVO) this.getValue("DeskTopNewsDataVO");
		selectvo = selectVO;
		String filepath = selectVO.getStringValue("PUBLISHFILEPATH");//认定通知书的相对路径
		if (!"WORD".equals(viewtype)) {
			viewRDByCellPanel();
			return;
		}
		if (filepath == null || "".equals(filepath.trim())) {
			MessageBox.show(deskTopPanel, "该认定通知书已被删除,不可查看!");
			return;
		}
		onShowRegisterWorld(filepath);
	}

	//查看认定通知单
	private void viewRDByCellPanel() {
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分认定通知书模板", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分认定通知书模板]模版.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%认定%'");
				BillListDialog listdialog = new BillListDialog(deskTopPanel, "请选择想对应的模版", listpanel);
				listdialog.setVisible(true);
				if (listdialog.getCloseType() != 1) {
					return;
				}
				BillVO rtvos[] = listdialog.getReturnBillVOs();
				if (rtvos.length > 0) {
					cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
				} else {
					return;
				}
			}
			final BillDialog dialog = new BillDialog(deskTopPanel, "预览", 800, 800);
			HashVO hvo = new HashVO();
			BillVO bvo [] = UIUtil.getBillVOsByDS(null, "select * from v_score_user where id = " + selectvo.getStringValue("id"), UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_Q01"));
			if(bvo==null || bvo.length == 0){
				return;
			}
			BillVO vo = bvo[0];
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			String currdate = UIUtil.getServerCurrDate();
			hvo.setAttributeValue("认定日期", currdate);
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
				if ("PUBLISHDATE".equals(keys[i])) {
					String date = vo.getStringViewValue(keys[i]);
					if (!TBUtil.isEmpty(date)) {
						hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
					}
				} else {
					hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
				}
			}
			Pub_Templet_1VO templet_1VO = UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_E01");//子模板
			LinkedHashMap hashmap = new LinkedHashMap();
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new,true,false);
			dialog.add(cellPanel, BorderLayout.CENTER);
			JPanel southpanel = new JPanel(new FlowLayout());//dialog下方的按钮面板
			WLTButton btn_close = new WLTButton("关闭"); //
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.setCloseType(BillDialog.CANCEL);
					dialog.dispose(); //
				}
			});
			southpanel.add(btn_close);
			dialog.add(southpanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (Exception ex) {
			MessageBox.show(deskTopPanel, ex);
		}
	}

	/**
	 * 查看认定通知书
	 **/
	private void onShowRegisterWorld(String filename) {
		OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //
		officeVO.setIfshowsave(false);
		officeVO.setIfshowprint_all(false);
		officeVO.setIfshowprint_fen(false);
		officeVO.setIfshowprint_tao(false);
		officeVO.setIfshowedit(false);
		officeVO.setToolbar(false);
		officeVO.setIfshowclose(false);
		officeVO.setPrintable(false);
		officeVO.setMenubar(false);
		officeVO.setMenutoolbar(false);
		officeVO.setIfshowhidecomment(false);
		officeVO.setTitlebar(false);
		officeVO.setIfshowprint(false);
		officeVO.setIfshowhidecomment(false);
		officeVO.setIfshowshowcomment(false);
		officeVO.setIfshowacceptedit(false);
		officeVO.setIfshowshowedit(false);
		officeVO.setIfshowhideedit(false);
		officeVO.setIfshowwater(false);
		officeVO.setIfShowResult(false); //不显示结果区域显示。
		officeVO.setIfselfdesc(true); //关键
		officeVO.setSubdir("upload");
		try {
			Class cls = Class.forName("cn.com.infostrategy.ui.mdata.BillOfficePanel");
			Constructor cons = cls.getConstructor(new Class[] { String.class, OfficeCompentControlVO.class }); //
			JPanel pane = (JPanel) cons.newInstance(new Object[] { filename, officeVO }); //
			BillDialog dialog = new BillDialog(deskTopPanel);
			dialog.getContentPane().add(pane);
			dialog.addOptionButtonPanel(new String[] { "关闭" });
			dialog.maxToScreenSizeBy1280AndLocationCenter();
			dialog.setTitle("我的认定通知书");
			dialog.setVisible(true);
		} catch (Exception e) {
			MessageBox.showException(deskTopPanel, e);
		}
	}
}
