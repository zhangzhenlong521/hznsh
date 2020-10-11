package cn.com.pushworld.salary.ui.target.p100;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.cellcompent.AttributiveCellTableModel;
import cn.com.infostrategy.ui.report.cellcompent.CellSpan;
import cn.com.infostrategy.ui.report.cellcompent.MultiSpanCellTableUI;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

public class TargetCheckReviseResultWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTTabbedPane maintab = null;
	private SalaryServiceIfc ifc = null;

	public void initialize() {
		maintab = new WLTTabbedPane();
		maintab.addTab("当前考核数据", getFirstPanel());
		maintab.addTab("历史数据查看", getSecondPanel());
		this.add(maintab, BorderLayout.CENTER);
	}

	public JPanel getFirstPanel() {
		WLTTabbedPane tab = new WLTTabbedPane();
		try {
			BillCellVO[] vos = getIfc().getTarget_Check_ReviseVO(null);
			if (vos != null && vos.length > 0) {
				for (int i = 0; i < vos.length; i++) {
					BillCellPanel bc = new BillCellPanel();
					// if (vos[i].getSeq() != null) {
					getSouthPanel(bc);
					// }
					bc.loadBillCellData(vos[i]);
					bc.getTable().putClientProperty("issubmit", vos[i].getSeq());
					bc.getTable().setUI(new MyUI());
					bc.setAllowShowPopMenu(false);
					bc.setToolBarVisiable(false);
					if (vos.length == 1) {
						return bc;
					}
					tab.addTab(vos[i].getDescr(), bc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tab;
	}

	private void getSouthPanel(BillCellPanel bc) {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT));
		WLTButton btn = new WLTButton("提交");
		WLTButton btn_ = new WLTButton("保存");
		btn.putClientProperty("pa", bc);
		btn_.putClientProperty("pa", bc);
		btn.addActionListener(this);
		btn_.addActionListener(this);
//		panel.add(btn);
		panel.add(btn_);
		bc.add(panel, BorderLayout.NORTH);
	}

	public BillListPanel getSecondPanel() {
		final BillListPanel bl = new BillListPanel("SAL_TARGET_CHECK_LOG_CODE1");
		bl.QueryDataByCondition(null);
		WLTButton btn = new WLTButton("查看");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BillVO svo = bl.getSelectedBillVO();
				if (svo == null) {
					MessageBox.show(bl, "请选择一条记录来进行此操作!");
					return;
				}
				try {
					String logid = svo.getStringValue("id");
					DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
					dr.getBillQueryPanel().setVisible(false);
					BillCellVO[] vos = getIfc().getTarget_Check_ReviseVO(logid);
					dr.getBillCellPanel().loadBillCellData(vos[0]);
					dr.getBillCellPanel().getTable().putClientProperty("issubmit", vos[0].getSeq());
					dr.getBillCellPanel().getTable().setUI(new MyUI());
					BillDialog bd = new BillDialog(bl, "查看", 950, 600);
					bd.add(dr, BorderLayout.CENTER);
					bd.addOptionButtonPanel(new String[] { "取消" });
					bd.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		bl.addBatchBillListButton(new WLTButton[] { btn });
		bl.repaintBillListButton();
		bl.getBillListBtnPanel().setAllBillListBtnVisiable(false);
		btn.setVisible(true);
		return bl;
	}

	public SalaryServiceIfc getIfc() {
		if (ifc == null) {
			try {
				ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ifc;
	}

	public void actionPerformed(ActionEvent e) {
		WLTButton btn = (WLTButton) e.getSource();
		BillCellPanel bc = (BillCellPanel) btn.getClientProperty("pa");
		if ("Y".equals(bc.getTable().getClientProperty("issubmit"))) {
			MessageBox.show(bc, "已经提交,不能保存或重复提交!");
		} else {
			if (btn.getText().equals("保存")) {
				onSubmit(bc, "待提交");
			} else {
				onSubmit(bc, "已提交");
			}
		}
	}

	private void onSubmit(BillCellPanel bc, String state) {
//		if ("已提交".equals(state)) {
//			if (MessageBox.showConfirmDialog(bc, "提交后不允许修改,您确认提交吗?", "提醒", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
//				return;
//			}
//		}
		bc.stopEditing();
		BillCellItemVO[][] items = bc.getBillCellVO().getCellItemVOs();
		List sql = new ArrayList();
		try {
			if (items != null && items.length > 0) {
				for (int i = 0; i < items.length; i++) {
					if ("value".equals(items[i][2].getCellkey())) {
						if (!"".equals(items[i][2].getCellvalue()) && !"0".equals(items[i][2].getCellvalue())) {
							if (items[i][3].getCellvalue() == null || "".equals(items[i][3].getCellvalue().trim())) {
								MessageBox.show(bc, "填写了调整分请补充调整理由!");
								return;
							}
						}
						if (items[i][2].getCustProperty("id") != null && !"".equals(items[i][2].getCustProperty("id"))) {
							UpdateSQLBuilder usb = new UpdateSQLBuilder("sal_target_check_revise_result");
							usb.putFieldValue("revisescore", items[i][2].getCellvalue());
							usb.putFieldValue("descr", items[i][3].getCellvalue());
							usb.setWhereCondition("id=" + items[i][2].getCustProperty("id"));
							sql.add(usb.getSQL());
						}
					}
				}
			}
			if (sql.size() > 0) {
				UIUtil.executeBatchByDS(null, sql);
				MessageBox.show(this, "操作成功!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyUI extends MultiSpanCellTableUI {

		private AttributiveCellTableModel tableModel = null; //
		private CellSpan cellAtt = null; //
		private TableCellRenderer renderer = null; //
		private Component rendererComponent = null; //

		public void paint(Graphics g, JComponent c) {
			Rectangle oldClipBounds = g.getClipBounds(); // 以前的取法!!
			// Rectangle oldClipBounds = c.getVisibleRect();
			// //这句话在纵向滚动条滚动时会出现白屏，李春娟修改

			tableModel = (AttributiveCellTableModel) table.getModel(); //
			cellAtt = (CellSpan) tableModel.getCellAttribute();
			Rectangle clipBounds = new Rectangle(oldClipBounds);
			// 这里解决了部分不能刷新出现白屏的问题，但是如果数据量大并且合并行比较多的话，滚动滚动条会特别卡。
			// 因为一般情况下是前两三列的合并行数比较多，所以从右边列取点判断开始和结束行号，徐老师说有可能右边列也有好多纵向合并，所以以后要写个方法，精确判断显示区域的开始和结束行号！李春娟修改
			int firstIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y)) - 1; // 可以看见的第一行!以前是new
			int allIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height)) + 1; // 总行多少行table.rowAtPoint(new
			int li_undrawn = 0; //
			for (int index = (firstIndex <= 0 ? 0 : firstIndex); index <= allIndex; index++) {
				boolean bo_isdrawnrow = paintRow(g, index); // 如果在可视区内,则画之!
				if (!bo_isdrawnrow) {
					li_undrawn++; //
				}
				if (li_undrawn > 40) { // 如果连续发生40次没画,则退出循环!!这个问题很妖,一直没搞明白!通过Debug发现,有时忽然会发生从前几行就开始就不画了,感觉是Swing的一个Bug.所以我干脆放了一个大一点的数,即40
					break;
				}
			}
		}

		/**
		 * 画某一行
		 * 
		 * @param g
		 * @param row
		 */
		private boolean paintRow(Graphics g, int row) {
			Rectangle rect = g.getClipBounds();
			boolean drawn = false;
			int numColumns = table.getColumnCount(); //
			for (int column = 0; column < numColumns; column++) {
				Rectangle cellRect = table.getCellRect(row, column, true); // 可能是这个方法有问题,
				int cellRow, cellColumn;
				if (cellAtt.isVisible(row, column)) {
					cellRow = row;
					cellColumn = column;
					// System.out.print("   " + column + " "); // debug
				} else {
					cellRow = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
					cellColumn = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
					// System.out.print("  (" + column + ")"); // debug
				}

				if (cellRect.intersects(rect)) {
					drawn = true; // 画了
					paintCell(g, cellRect, cellRow, cellColumn);
					// System.out.println("实际画[" + cellRow + "]行"); //
				} else {
					if (drawn)
						break;
				}
			}

			return drawn; //
		}

		private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
			int spacingHeight = table.getRowMargin();
			int spacingWidth = table.getColumnModel().getColumnMargin();

			Color c = g.getColor();
			g.setColor(table.getGridColor());
			g.drawRect(cellRect.x, cellRect.y, cellRect.width - 1, cellRect.height - 1);
			g.setColor(c);
			cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2, cellRect.width - spacingWidth, cellRect.height - spacingHeight);
			if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
				// System.out.println("编辑[" + row + "," + column + "]"); //
				Component component = table.getEditorComponent();
				component.setBounds(cellRect);
				component.validate();
			} else {
				renderer = table.getCellRenderer(row, column); // 取得绘制器
				rendererComponent = table.prepareRenderer(renderer, row, column); // 取得Render中的返回的控件,比如下拉框,文本框等.

				if (rendererComponent.getParent() == null) {
					rendererPane.add(rendererComponent);
				}
				rendererPane.paintComponent(g, rendererComponent, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
			}
			if ("Y".equals(table.getClientProperty("issubmit"))) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.translate(15, 8);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
				g2d.setColor(Color.red.brighter());
				g2d.rotate(Math.PI / 180 * 30);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(new BasicStroke(3.5f));
				g2d.setFont(new Font("黑体", Font.BOLD, 25));
				double a = table.getWidth() / 2 - 50; // .getVisibleRect().getCenterX()
														// // 这样就不会动了
				double b = table.getHeight() / 2 - 20; // getVisibleRect().getCenterY()
				double x = a * Math.cos(Math.PI / 180 * 30) + b * Math.sin(Math.PI / 180 * 30);
				double y = b * Math.cos(Math.PI / 180 * 30) - a * Math.sin(Math.PI / 180 * 30);
				g2d.drawString("已 提 交", (int) x, (int) y);
			}
		}

	}

}
