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
		maintab.addTab("��ǰ��������", getFirstPanel());
		maintab.addTab("��ʷ���ݲ鿴", getSecondPanel());
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
		WLTButton btn = new WLTButton("�ύ");
		WLTButton btn_ = new WLTButton("����");
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
		WLTButton btn = new WLTButton("�鿴");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BillVO svo = bl.getSelectedBillVO();
				if (svo == null) {
					MessageBox.show(bl, "��ѡ��һ����¼�����д˲���!");
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
					BillDialog bd = new BillDialog(bl, "�鿴", 950, 600);
					bd.add(dr, BorderLayout.CENTER);
					bd.addOptionButtonPanel(new String[] { "ȡ��" });
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
			MessageBox.show(bc, "�Ѿ��ύ,���ܱ�����ظ��ύ!");
		} else {
			if (btn.getText().equals("����")) {
				onSubmit(bc, "���ύ");
			} else {
				onSubmit(bc, "���ύ");
			}
		}
	}

	private void onSubmit(BillCellPanel bc, String state) {
//		if ("���ύ".equals(state)) {
//			if (MessageBox.showConfirmDialog(bc, "�ύ�������޸�,��ȷ���ύ��?", "����", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
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
								MessageBox.show(bc, "��д�˵������벹���������!");
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
				MessageBox.show(this, "�����ɹ�!");
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
			Rectangle oldClipBounds = g.getClipBounds(); // ��ǰ��ȡ��!!
			// Rectangle oldClipBounds = c.getVisibleRect();
			// //��仰���������������ʱ����ְ���������޸�

			tableModel = (AttributiveCellTableModel) table.getModel(); //
			cellAtt = (CellSpan) tableModel.getCellAttribute();
			Rectangle clipBounds = new Rectangle(oldClipBounds);
			// �������˲��ֲ���ˢ�³��ְ��������⣬����������������Һϲ��бȽ϶�Ļ����������������ر𿨡�
			// ��Ϊһ���������ǰ�����еĺϲ������Ƚ϶࣬���Դ��ұ���ȡ���жϿ�ʼ�ͽ����кţ�����ʦ˵�п����ұ���Ҳ�кö�����ϲ��������Ժ�Ҫд����������ȷ�ж���ʾ����Ŀ�ʼ�ͽ����кţ�����޸�
			int firstIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y)) - 1; // ���Կ����ĵ�һ��!��ǰ��new
			int allIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height)) + 1; // ���ж�����table.rowAtPoint(new
			int li_undrawn = 0; //
			for (int index = (firstIndex <= 0 ? 0 : firstIndex); index <= allIndex; index++) {
				boolean bo_isdrawnrow = paintRow(g, index); // ����ڿ�������,��֮!
				if (!bo_isdrawnrow) {
					li_undrawn++; //
				}
				if (li_undrawn > 40) { // �����������40��û��,���˳�ѭ��!!����������,һֱû������!ͨ��Debug����,��ʱ��Ȼ�ᷢ����ǰ���оͿ�ʼ�Ͳ�����,�о���Swing��һ��Bug.�����Ҹɴ����һ����һ�����,��40
					break;
				}
			}
		}

		/**
		 * ��ĳһ��
		 * 
		 * @param g
		 * @param row
		 */
		private boolean paintRow(Graphics g, int row) {
			Rectangle rect = g.getClipBounds();
			boolean drawn = false;
			int numColumns = table.getColumnCount(); //
			for (int column = 0; column < numColumns; column++) {
				Rectangle cellRect = table.getCellRect(row, column, true); // �������������������,
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
					drawn = true; // ����
					paintCell(g, cellRect, cellRow, cellColumn);
					// System.out.println("ʵ�ʻ�[" + cellRow + "]��"); //
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
				// System.out.println("�༭[" + row + "," + column + "]"); //
				Component component = table.getEditorComponent();
				component.setBounds(cellRect);
				component.validate();
			} else {
				renderer = table.getCellRenderer(row, column); // ȡ�û�����
				rendererComponent = table.prepareRenderer(renderer, row, column); // ȡ��Render�еķ��صĿؼ�,����������,�ı����.

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
				g2d.setFont(new Font("����", Font.BOLD, 25));
				double a = table.getWidth() / 2 - 50; // .getVisibleRect().getCenterX()
														// // �����Ͳ��ᶯ��
				double b = table.getHeight() / 2 - 20; // getVisibleRect().getCenterY()
				double x = a * Math.cos(Math.PI / 180 * 30) + b * Math.sin(Math.PI / 180 * 30);
				double y = b * Math.cos(Math.PI / 180 * 30) - a * Math.sin(Math.PI / 180 * 30);
				g2d.drawString("�� �� ��", (int) x, (int) y);
			}
		}

	}

}
