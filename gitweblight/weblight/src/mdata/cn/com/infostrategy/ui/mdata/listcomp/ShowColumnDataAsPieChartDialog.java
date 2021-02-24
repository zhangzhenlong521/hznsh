/**************************************************************************
 * $RCSfile: ShowColumnDataAsPieChartDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.ListAndTableFactoty;

/**
 * ��ͼ�鿴ĳ������
 * 
 * @author user
 * 
 */
public class ShowColumnDataAsPieChartDialog extends BillDialog {

	private static final long serialVersionUID = 1L;

	private final static Font textFont = new Font("����", Font.PLAIN, 12);

	private String title = null;

	private String[] data = null;

	private String[] send_str = null;

	private Vector row_vec = null;

	private Pair pair = null;

	private Vector pair_vec = null;

	private PieChartPanel pieChartPanel = null;

	private double values[] = null;

	private double row_value[] = null;

	private JTable table = null;

	private String[] DefaultColoumnNames = { "����", "��¼��(/��)",
			"��ռ����(/%)", "ͼ����ɫ" };

	private JButton confirmButton = null;

	private int index_selected = -1;

	/**
	 * �ù��췽����Ҫ�ʺ��ڣ���Ҫ�Դ����_data����ͳ��
	 * Ȼ�����_data�еĸ���Ԫ�صı��أ����б�ͼ��ʾ
	 * @param _parent��ĸ��
	 * @param _columnname������
	 * @param _data����������
	 */
	public ShowColumnDataAsPieChartDialog(Container _parent,
			String _columnname, String[] _data) {
		super(_parent, _columnname + "��ͼ�鿴", 550, 500); //
		this.title = _columnname;
		this.data = _data;
		initialize();
	}

	/**
	 * �ù��췽����Ҫ�ʺ��ڣ�Ҫ��ʾ��Ԫ�صı����Ѿ���ã�ֱ��Ҫ��ʾ
	 * @param _parent
	 * @param _title
	 * @param _row_name
	 * @param _value
	 */
	public ShowColumnDataAsPieChartDialog(Container _parent, String _title,
			Vector _row_name, double[] _value) {
		super(_parent, _title + "��ͼ�鿴", 550, 500); //
		this.title = _title;
		this.row_vec = _row_name;
		this.row_value = _value;
		getColumnNames();
		getPairVec();
		initialize();
	}

	private void initialize() {
		getValues();
		pieChartPanel = new PieChartPanel(this,title, send_str, values);
		pieChartPanel.setPreferredSize(new Dimension(250, 280));

		JScrollPane scroll = getJSPTable();
		scroll.setPreferredSize(new Dimension(300, 100));

		JTabbedPane jtp_table = new JTabbedPane();
		jtp_table.add("������չʾ", scroll);

		confirmButton = new JButton("ȷ��");
		confirmButton.setFont(textFont);
		confirmButton.setPreferredSize(new Dimension(75, 20));
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createGlue());
		buttonBox.add(confirmButton);
		buttonBox.add(Box.createGlue());

		JSplitPane _split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				pieChartPanel, jtp_table);
		_split.setDividerLocation(300);
		_split.setDividerSize(5);
		_split.setOneTouchExpandable(true);
		
		this.getContentPane().add(_split, BorderLayout.CENTER);
		this.getContentPane().add(buttonBox, BorderLayout.SOUTH);
	}

	protected void onConfirm() {
		this.dispose();
	}

	private void getColumnNames() {
		DefaultColoumnNames[1] = this.title;
	}

	private void dealSelectedChanged() {
		int selected_index = table.getSelectedRow();
		if (selected_index == table.getRowCount() - 1) {
			pieChartPanel.setSelectedKey(-1);
			return;
		}
		pieChartPanel.setSelectedKey(selected_index);
		return;
	}

	/**
	 * �������ݱ���ѡ�е��У��÷�����Ҫ�Ǳ���ͼ���
	 * �ڴ�����굥���������ݱ��������Ӧ�ľٶ��Ĵ���
	 * @param _index
	 */
	public void setSelectedRow(int _index){
		if (_index != -1 && index_selected != _index) {
			Rectangle rect = table.getCellRect(_index, 0, true);
			table.scrollRectToVisible(rect);
			table.setRowSelectionInterval(_index, _index);

			index_selected = _index;
		} else if (_index == -1) {
			table.clearSelection();
		}
	}

	/**
	 * ��ʼ�����ݱ�
	 */
	private JScrollPane getJSPTable() {
		ListAndTableFactoty latf_table = new ListAndTableFactoty();
		
		Object[][] obj_values = new Object[pair_vec.size() + 1][];
		DecimalFormat df = new DecimalFormat("##.00");
		int i;
		for (i = 0; i < pair_vec.size(); i++) {
			pair = (Pair) pair_vec.get(i);
			obj_values[i] = new Object[]{ (Object) pair.getKey(),
					new Double(pair.getValue()),
					(Object) (df.format(values[i] * 100)) };
		}
		obj_values[i] = new Object[]{ (Object) "�ϼ�",
				new Double(df.format(getSumValue())), new Integer(100) };
		JScrollPane jsp_temp = latf_table.getJSPTable(obj_values, DefaultColoumnNames, null);
		table = latf_table.getTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnSelectionAllowed(false);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					dealSelectedChanged();
				}
			}
		});
		latf_table.setAllColumnUnEditeable();
		
		TableCellRenderer myTableCellRenderer = new MyTableHeaderRenderer(); 
		latf_table.setColumnCellRender(myTableCellRenderer, DefaultColoumnNames.length - 1);
		
		return jsp_temp;
	}

	/**
	 * �鿴Vec�������Ƿ��Ѿ�����Ԫ�ص�pair
	 * ���������Ȼ���Ԫ��pair�ļ�ֵ
	 * @param _key
	 * @return
	 */
	private int containsKey(String _key) {
		for (int i = 0; i < pair_vec.size(); i++) {
			pair = (Pair) pair_vec.get(i);
			if (pair.getKey().equals(_key)) {
				return i;
			}
		}
		return -1;
	}

	private void getPairVec() {
		pair_vec = new Vector();
		for (int i = 0; i < this.row_vec.size(); i++) {
			Pair _pair = new Pair(row_vec.get(i).toString(), row_value[i]);
			pair_vec.add(_pair);
		}
	}

	/**
	 * ���ͳ��ֵ
	 * @return
	 */
	private double getSumValue() {
		double sum = 0;
		for (int i = 0; i < pair_vec.size(); i++) {
			pair = (Pair) pair_vec.get(i);
			sum = sum + pair.getValue();
		}
		return sum;
	}

	/**
	 * ���ݴ���������������ø���Ԫ�صı���ֵ
	 * 
	 */
	private void getValues() {
		if (pair_vec == null) {
			pair_vec = new Vector();

			for (int i = 0; i < data.length; i++) {

				if (containsKey(data[i]) != -1) {
					int item_count = (int) pair.getValue();
					item_count++;
					pair.setValue(item_count);
					pair_vec.set(containsKey(data[i]), pair);
				} else {
					pair = new Pair(data[i], 1);
					pair_vec.add(pair);
				}
			}
		}

		sortVec();
		values = new double[pair_vec.size()];
		send_str = new String[pair_vec.size()];

		for (int i = 0; i < pair_vec.size(); i++) {
			DecimalFormat df = new DecimalFormat("##.00");
			pair = (Pair) pair_vec.get(i);

			String pair_key = pair.getKey();
			double pair_value = pair.getValue();

			values[i] = pair_value / getSumValue();
			send_str[i] = pair_key + " " + df.format(values[i] * 100) + "%";
		}
	}

	/**
	 * ð������
	 */
	private void sortVec() {
		double value_first = 0;
		double value_second = 0;
		Pair pair_first = null;
		Pair pair_second = null;

		for (int i = 0; i < pair_vec.size(); i++) {
			for (int j = pair_vec.size() - 1; j > i; j--) {
				pair_first = (Pair) pair_vec.get(i);
				pair_second = (Pair) pair_vec.get(j);

				value_first = (double) pair_first.getValue();
				value_second = (double) pair_second.getValue();

				if (value_first < value_second) {
					Pair temp_pair = pair_first;
					pair_vec.set(i, pair_second);
					pair_vec.set(j, temp_pair);
				}
			}
		}
	}

	public void closeDialog() {
		this.dispose();
	}

	public static void main(String[] argv) {
		String[] data = new String[] { "aa", "bb", "cc", "dd", "aa", "ee",
				"aa", "cc", "aa", "ee", "dd" };
		ShowColumnDataAsPieChartDialog dialog = new ShowColumnDataAsPieChartDialog(
				null, "�˵�", data);
		dialog.setVisible(true);
	}

	class MyTableHeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -1546156018632933567L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JLabel label = (JLabel) super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);

			label.setFont(textFont);
			int li_rowcount = table.getModel().getRowCount();
			if (row == li_rowcount - 1) { // ��������һ��,���ɫ��ʾ
				label.setBackground(Color.WHITE);
			} else {
				int color_length = PieChartPanel.Colors.length;
				label.setBackground(Color.WHITE);
				label.setForeground(Color.BLACK);
				if (column == 3) {
					if (row > 0 && row % color_length == 0) {
						label.setBackground(PieChartPanel.Colors[color_length - 3]);
					} else {
						label.setBackground(PieChartPanel.Colors[row % color_length]);
					}
				}
			}
			return label;
		}

		public void validate() {
		}

		public void revalidate() {
		}

		protected void firePropertyChange(String propertyName, Object oldValue,
				Object newValue) {
		}

		public void firePropertyChange(String propertyName, boolean oldValue,
				boolean newValue) {
		}
	}
}

class Pair {
	private String key = null;

	private double value = 0;

	public Pair() {
		this.key = null;
		this.value = 0;
	}

	public Pair(String _key, double _value) {
		this.key = _key;
		this.value = _value;
	}

	public void setKey(String _key) {
		this.key = _key;
	}

	public String getKey() {
		return this.key;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(int _value) {
		this.value = _value;
	}
}
/**************************************************************************
 * $RCSfile: ShowColumnDataAsPieChartDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: ShowColumnDataAsPieChartDialog.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:41:00  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:44  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:31  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:43  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:20  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:51:57  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:30  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/