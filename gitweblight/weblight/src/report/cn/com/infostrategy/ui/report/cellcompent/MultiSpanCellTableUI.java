package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

/**
 * ������,swing��ײ��,���������������Ƿݴ����������������!!
 * ���Ǿ������޸ĺ��!
 * @author xch
 *
 */
public class MultiSpanCellTableUI extends BasicTableUI {

	private AttributiveCellTableModel tableModel = null; //
	private CellSpan cellAtt = null; //
	private TableCellRenderer renderer = null; //
	private Component rendererComponent = null; //

	public void paint(Graphics g, JComponent c) {
		Rectangle oldClipBounds = g.getClipBounds(); //��ǰ��ȡ��!!
		//Rectangle oldClipBounds = c.getVisibleRect(); //��仰���������������ʱ����ְ���������޸�

		tableModel = (AttributiveCellTableModel) table.getModel(); //
		cellAtt = (CellSpan) tableModel.getCellAttribute();

		Rectangle clipBounds = new Rectangle(oldClipBounds);
		//int tableWidth = table.getColumnModel().getTotalColumnWidth();
		//clipBounds.width = Math.min(clipBounds.width, tableWidth);
		//g.setClip(clipBounds);

		//�������˲��ֲ���ˢ�³��ְ��������⣬����������������Һϲ��бȽ϶�Ļ����������������ر𿨡�
		//��Ϊһ���������ǰ�����еĺϲ������Ƚ϶࣬���Դ��ұ���ȡ���жϿ�ʼ�ͽ����кţ�����ʦ˵�п����ұ���Ҳ�кö�����ϲ��������Ժ�Ҫд����������ȷ�ж���ʾ����Ŀ�ʼ�ͽ����кţ�����޸�
		int firstIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y)) - 1; //���Կ����ĵ�һ��!��ǰ��new Point(0, clipBounds.y)���������ֺ������������ʱ���ͳ��ְ�����������޸�
		int allIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height)) + 1; //���ж�����table.rowAtPoint(new Point(0, clipBounds.width)); 
		//System.out.println(">>>>>>" + firstIndex + "--" + allIndex + ">>>>>>>" + clipBounds.x + "==" + clipBounds.y + "==" + clipBounds.width + "==" + clipBounds.height);
		int li_undrawn = 0; //
		for (int index = (firstIndex <= 0 ? 0 : firstIndex); index <= allIndex; index++) {
			boolean bo_isdrawnrow = paintRow(g, index); //����ڿ�������,��֮!
			if (!bo_isdrawnrow) {
				li_undrawn++; //
			}
			if (li_undrawn > 40) { //�����������40��û��,���˳�ѭ��!!����������,һֱû������!ͨ��Debug����,��ʱ��Ȼ�ᷢ����ǰ���оͿ�ʼ�Ͳ�����,�о���Swing��һ��Bug.�����Ҹɴ����һ����һ�����,��40
				break;
			}
		}
	}

	/**
	 * ��ĳһ��
	 * @param g
	 * @param row
	 */
	private boolean paintRow(Graphics g, int row) {
		Rectangle rect = g.getClipBounds();
		boolean drawn = false;
		int numColumns = table.getColumnCount(); //
		for (int column = 0; column < numColumns; column++) {
			Rectangle cellRect = table.getCellRect(row, column, true); //�������������������,
			int cellRow, cellColumn;
			if (cellAtt.isVisible(row, column)) {
				cellRow = row;
				cellColumn = column;
				//System.out.print("   " + column + " "); // debug
			} else {
				cellRow = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
				cellColumn = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
				//System.out.print("  (" + column + ")"); // debug
			}

			if (cellRect.intersects(rect)) {
				drawn = true; //����
				paintCell(g, cellRect, cellRow, cellColumn);
				//System.out.println("ʵ�ʻ�[" + cellRow + "]��"); //
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
		//g.setColor(Color.RED);

		g.drawRect(cellRect.x, cellRect.y, cellRect.width - 1, cellRect.height - 1);
		g.setColor(c);
		cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2, cellRect.width - spacingWidth, cellRect.height - spacingHeight);
		if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
			//System.out.println("�༭[" + row + "," + column + "]");  //
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			renderer = table.getCellRenderer(row, column); //ȡ�û�����
			rendererComponent = table.prepareRenderer(renderer, row, column); //ȡ��Render�еķ��صĿؼ�,����������,�ı����.

			if (rendererComponent.getParent() == null) {
				rendererPane.add(rendererComponent);
			}

			//System.out.println("����[" + row + "]����!!!"); //
			rendererPane.paintComponent(g, rendererComponent, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
		}
	}

}
