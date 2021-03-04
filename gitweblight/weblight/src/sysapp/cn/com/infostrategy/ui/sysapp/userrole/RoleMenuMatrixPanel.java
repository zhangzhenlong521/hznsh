package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class RoleMenuMatrixPanel extends JPanel implements ActionListener {
	private WLTButton btn_save, btn_exportHtml, btn_exportExcel = null;
	private BillCellPanel cellpanel = null;
	private BillCellVO billCellVO = null;
	private BillCellItemVO[][] cellItemVOs = null;

	public RoleMenuMatrixPanel() {
		initialize(); //
	}

	public void initialize() {
		try {
			this.setLayout(new BorderLayout(0, 10)); //
			JPanel toppanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			toppanel.setOpaque(false);

			btn_save = new WLTButton("保 存");
			btn_exportHtml = new WLTButton("导出Html", UIUtil.getImage("office_192.gif"));
			btn_exportExcel = new WLTButton("导出Excel", UIUtil.getImage("office_170.gif"));

			btn_save.addActionListener(this);
			btn_exportHtml.addActionListener(this);
			btn_exportExcel.addActionListener(this);

			toppanel.add(btn_save);
			toppanel.add(btn_exportHtml);
			toppanel.add(btn_exportExcel);
			HashMap parMap = new HashMap();
			HashMap rhm = UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.userrole.RoleMenuMatrixDMO", "getCellData", parMap);
			String[][] celldata = (String[][]) rhm.get("celldata"); //

			billCellVO = new BillCellVO();
			billCellVO.setRowlength(celldata.length);
			billCellVO.setCollength(celldata[0].length);

			cellItemVOs = new BillCellItemVO[celldata.length][celldata[0].length];
			for (int i = 0; i < cellItemVOs.length; i++) {
				for (int j = 0; j < cellItemVOs[i].length; j++) {
					cellItemVOs[i][j] = new BillCellItemVO();
					cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA);
					cellItemVOs[i][j].setCellrow(i);
					cellItemVOs[i][j].setCellcol(j);

					if (i == 0 && j == 0) {
						cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA);
						cellItemVOs[i][j].setRowheight("70");
						cellItemVOs[i][j].setColwidth("300");
						cellItemVOs[i][j].setBackground("255,255,160");
						cellItemVOs[i][j].setCellvalue(celldata[i][j]);
						cellItemVOs[i][j].setHalign(3);
						cellItemVOs[i][j].setValign(3);
					}

					if (i == 0 && j != 0) {
						cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA);
						cellItemVOs[i][j].setRowheight("70");
						cellItemVOs[i][j].setColwidth("60");
						cellItemVOs[i][j].setBackground("155,215,30");
						cellItemVOs[i][j].setCellvalue(celldata[i][j]);
						cellItemVOs[i][j].setHalign(2);
						cellItemVOs[i][j].setValign(2);
					}

					if (i != 0 && j == 0) {
						cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA);
						cellItemVOs[i][j].setRowheight("30");
						cellItemVOs[i][j].setColwidth("300");
						cellItemVOs[i][j].setBackground("204,255,204");
						cellItemVOs[i][j].setCellvalue(celldata[i][j]);
						cellItemVOs[i][j].setHalign(1);
						cellItemVOs[i][j].setValign(2);
					}

					if (i != 0 && j != 0) {
						cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_CHECKBOX);
						cellItemVOs[i][j].setRowheight("30");
						cellItemVOs[i][j].setColwidth("60");
						cellItemVOs[i][j].setCellkey(celldata[i][j]);
						cellItemVOs[i][j].setHalign(2);
						cellItemVOs[i][j].setValign(2);

						String celldata_mark = celldata[i][j].substring(0, 1);
						cellItemVOs[i][j].setCellvalue(celldata_mark);
						if (celldata_mark.equals("Y")) {
							cellItemVOs[i][j].setBackground("230,200,230");
						}
					}
				}
			}

			billCellVO.setCellItemVOs(cellItemVOs);
			cellpanel = new BillCellPanel(billCellVO);

			this.add(toppanel, BorderLayout.NORTH);
			this.add(cellpanel, BorderLayout.CENTER);
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.setLayout(new BorderLayout()); //
			this.add(new JLabel("加载页面发生异常,请至控制台查看详细.")); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			try {
				save();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == btn_exportHtml) {
			cellpanel.exportHtml();
		} else if (e.getSource() == btn_exportExcel) {
			cellpanel.exportExcel();
		}
	}

	public void save() throws Exception {
		cellpanel.stopEditing();
		ArrayList list_sqls = new ArrayList();
		for (int i = 1; i < cellItemVOs.length; i++) {
			for (int j = 1; j < cellItemVOs[i].length; j++) {
				String cellvalue[] = cellItemVOs[i][j].getCellkey().split("-");
				String old_cellvalue = cellvalue[0];
				String new_cellvalue = cellItemVOs[i][j].getCellvalue();
				if (!(new_cellvalue.equals(old_cellvalue))) {
					if (old_cellvalue.equals("Y")) {
						list_sqls.add("delete from pub_role_menu where menuid = '" + cellvalue[1] + "' and  roleid = '" + cellvalue[2] + "'");
					} else {
						String str_newid = UIUtil.getSequenceNextValByDS(null, "s_pub_role_menu");
						list_sqls.add("insert into pub_role_menu (id,roleid,menuid) values ('" + str_newid + "','" + cellvalue[2] + "','" + cellvalue[1] + "')");
					}
				}
			}
		}

		UIUtil.executeBatchByDS(null, list_sqls);
		MessageBox.show(this, "角色授权成功!");
	}

}
