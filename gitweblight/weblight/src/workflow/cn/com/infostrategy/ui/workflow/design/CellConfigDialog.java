package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * �޸�ĳһ������,����,�������������Ϣ��
 * ���������޸�����,�����С,ǰ����ɫ,����ɫ!
 * �ֵ�����!
 * @author xch
 *
 */
public class CellConfigDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -6389651731318330892L;
	private String fontType = "System";
	private Font font = null;
	private int fontSize = 12; //
	private Color foreGround = null; //
	private Color backGround = null; //
	private String contentStr = null; //����
	private String imgstr = null; //ͼƬ
	private String seq = null; // ����
	private String postsStr = null; //
	private String celltype = null;//"DEPT","STATION","ACTIVITY","TRANSITION"
	private int viewtype = 1; //��������

	private JComboBox combox_fonttype, combox_fontsize; //��������,�����С
	private CardCPanel_Ref ref_foreground, ref_background, ref_img; //ǰ����ɫ,����ɫ
	private JTextArea textarea_content; //
	private BillListPanel list_post;
	private JTextField textarea_seq;//����
	private JComboBox combox_type, combox_single;//��������������
	private Integer lineType;
	private boolean isSingle;
	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1; //�ر�����
	private boolean isCanSetCellColor = true;//ũ��������༭���Ž׶μ�����ʱ��Ҫ����������ɫ�ͱ�����ɫ����ֹ����ͼ���༭�Ļ�����ڵģ������ô˲���
	private DefaultTableModel defaultModel = null;//���ñ��Ĭ����ʽ   [Ԭ����20120907���]
	JTable table = null;//�����λ���   [Ԭ����20120907���]

	/**
	 * ���췽��
	 * @param _parent
	 * @param _title
	 * @param _y 
	 * @param _x 
	 * @param _cellBackcolor 
	 * @param _cellForecolor 
	 * @param _cellFont 
	 * @param _contentStr 
	 */
	public CellConfigDialog(java.awt.Container _parent, String _title, int _x, int _y, Font _cellFont, Color _cellForecolor, Color _cellBackcolor, String _contentStr, String _seq, int _viewtype, String imgstr, String _postsStr, String _celltype) {
		this(_parent, _title, _x, _y, _cellFont, _cellForecolor, _cellBackcolor, _contentStr, _seq, _viewtype, imgstr, _postsStr, _celltype, null, true);
	}

	public CellConfigDialog(java.awt.Container _parent, String _title, int _x, int _y, Font _cellFont, Color _cellForecolor, Color _cellBackcolor, String _contentStr, String _seq, int _viewtype, String imgstr, String _postsStr, String _celltype, Integer _lineType, boolean _isSingle) {
		super(_parent, _title, 389, 280);
		this.setLocation(_x, _y);
		this.setResizable(false); //
		this.font = _cellFont;
		this.foreGround = _cellForecolor;
		this.backGround = _cellBackcolor; //
		this.contentStr = _contentStr; //
		this.seq = _seq;
		this.viewtype = _viewtype;
		this.imgstr = imgstr;
		this.postsStr = _postsStr;
		this.celltype = _celltype;
		this.lineType = _lineType;
		this.isSingle = _isSingle;
		this.setResizable(true);
		initialize(); //��ʼ��ҳ��
	}

	/**
	 * ��ʼ��ҳ��!!
	 */
	private void initialize() {
		this.getContentPane().add(getMainPanel(), BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getMainPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(null); //
		JLabel label_fonttype = new JLabel("�������� ", JLabel.RIGHT); //

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();

		combox_fonttype = new JComboBox(fontNames); //
		JLabel label_fontsize = new JLabel("�����С ", JLabel.RIGHT); //
		combox_fontsize = new JComboBox(new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "24", "36" }); //
		if (font == null) {
			combox_fonttype.setSelectedItem("����"); //��������
			combox_fontsize.setSelectedItem("12"); //���������С
		} else {
			if (existFont(fontNames, font.getName())) {
				combox_fonttype.setSelectedItem(font.getName()); //
			} else {
				combox_fonttype.setSelectedItem("����"); //��������
			}
			combox_fontsize.setSelectedItem("" + font.getSize()); //���������С
		}
		ref_foreground = new CardCPanel_Ref("������ɫ ", "������ɫ ", null, WLTConstants.COMP_COLOR, 70, 80, null, null); //
		if (foreGround != null) {
			ref_foreground.setObject(new RefItemVO(convertColorToStr(foreGround), null, convertColorToStr(foreGround)));//����������ɫ
		} else {
			ref_foreground.setObject(new RefItemVO("0,0,0", null, "0,0,0")); //Ĭ����ɫ�Ǻ�ɫ
		}

		ref_background = new CardCPanel_Ref("������ɫ", "������ɫ", null, WLTConstants.COMP_COLOR, 50, 80, null, null); //
		if (backGround != null) {
			ref_background.setObject(new RefItemVO(convertColorToStr(backGround), null, convertColorToStr(backGround)));//���ñ�����ɫ
		} else {
			ref_background.setObject(new RefItemVO("200,214,234", null, "200,214,234")); //Ĭ����Visio�ı�׼ɫ
		}

		ref_img = new CardCPanel_Ref("ͼƬ", "ͼƬ", null, WLTConstants.COMP_PICTURE, 50, 80, null, null); //
		if (imgstr != null && !"".equals(imgstr)) {
			ref_img.setObject(new RefItemVO(imgstr, null, imgstr));//����ͼƬ
		}

		JLabel label_content = new JLabel("���� ", JLabel.RIGHT); //
		textarea_content = new JTextArea(); //
		textarea_content.setLineWrap(false); //
		textarea_content.setText(this.contentStr); //��������
		textarea_content.requestFocus(); //
		JScrollPane scrollPanel = new JScrollPane(textarea_content); ///

		label_fonttype.setBounds(5, 5, 70, 20); //��������Label
		combox_fonttype.setBounds(75, 5, 135, 20); //��������������

		label_fontsize.setBounds(210, 5, 60, 20); //�����СLabel
		combox_fontsize.setBounds(270, 5, 50, 20); //�����С������

		ref_foreground.setBounds(5, 30, 170, 20); //������ɫ
		ref_background.setBounds(175, 30, 160, 20); //������ɫ

		label_content.setBounds(5, 55, 70, 20); //����Label
		scrollPanel.setBounds(75, 55, 250, 70);//����

		panel.add(label_fonttype); //��������
		panel.add(combox_fonttype); //
		panel.add(label_fontsize); //�����С
		panel.add(combox_fontsize); //

		panel.add(ref_foreground); //
		if ("ACTIVITY".equalsIgnoreCase(this.celltype) && this.viewtype == 8) {//����ǻ��ڣ�������ͼƬ���������ر�����ɫ����������ͼƬ
			ref_background.setItemEditable(false);
		} else {
			ref_img.setVisible(false);
		}
		panel.add(ref_background);//������ɫ
		panel.add(label_content); //
		panel.add(scrollPanel); //

		if ("ACTIVITY".equalsIgnoreCase(this.celltype)) {//����ǻ��ڣ�����ʾ����ͻ�������
			JLabel label_seq = new JLabel("���� ", JLabel.RIGHT); //
			label_seq.setBounds(5, 130, 70, 20); //

			textarea_seq = new JFormattedTextField(); //��ʽ�������ֿ�
			textarea_seq.setHorizontalAlignment(SwingConstants.RIGHT); //
			textarea_seq.setDocument(new NumberFormatdocument()); //�������ֿ�ֻ����������,������ĸ���ü���!!!!
			textarea_seq.setText(this.seq);
			//textarea_seq.requestFocus(); //panel����󣬻�ý����������,�������requestFocus���������á���ҫ��/2012-03-12��
			textarea_seq.setBounds(75, 130, 100, 20);

			JLabel label_type = new JLabel("�������� ", JLabel.RIGHT); //
			label_type.setBounds(5, 150, 70, 20); //
			ref_img.setBounds(175, 150, 160, 20); //ͼƬ
			combox_type = new JComboBox(new Object[] { UIUtil.getImage("workflow/cellview_1.gif"), UIUtil.getImage("workflow/cellview_2.gif"), UIUtil.getImage("workflow/cellview_3.gif"), UIUtil.getImage("workflow/cellview_4.gif"), UIUtil.getImage("workflow/cellview_5.gif"), UIUtil.getImage("workflow/cellview_6.gif"), UIUtil.getImage("workflow/cellview_7.gif"), UIUtil.getImage("pic2.gif") }); //

			combox_type.setSelectedIndex(viewtype - 1); //
			combox_type.setBounds(75, 150, 100, 20); //
			combox_type.addItemListener(new ItemListener() { //�����¼�
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								onTypeChanged();
							}
						}
					});
			panel.add(label_seq); //
			panel.add(textarea_seq); //
			panel.add(label_type); //
			panel.add(combox_type); //
			panel.add(ref_img); //
			//Ԭ����20120918���  �޸�֮ǰ�������Ϊ����ӣ����տ��ӵĿ�Ⱥʹ�����������λ
			//Ԭ����20121114�޸�   �޸�֮ǰ����ѡ��Ϊ��ѡ�񣬲��޸���һЩbug			
			//-----------------------------------------------------------------begin-----------------------------------------------------------------------------------
		} else if ("DEPT".equalsIgnoreCase(this.celltype)) {//����ǻ���������ʾ��λ����
			this.setSize(600, 400);
			ref_background.setVisible(false);//Ŀǰ���ű�����ɫ�ǲ������޸ĵģ���û��Ҫ��ʾ��������ɫ��
			JLabel label_posts = new JLabel("��λ���� ", JLabel.RIGHT); ////
			label_posts.setBounds(5, 130, 70, 20);
			int len = 0;
			if (postsStr != null && !postsStr.trim().equals("")) {
				String[] posts = postsStr.trim().split(";");
				len = posts.length;
			}
			if (len == 0) {
				len += 1;
			}
			if (null == postsStr || "".endsWith(postsStr)) {//����Ĭ����ʾ��
				len = 3;
			}
			Object[][] rowData = new Object[1][len];//����������ʾ
			Object[] columnNames = new Object[len];//���ñ�ͷ��ʾ
			int[] colLength = new int[len];
			//�ȳ�ʼ�����ݣ����������ʾ��û����ֻ��ʾһ��
			if (postsStr != null && !postsStr.trim().equals("")) {
				String[] temp = postsStr.trim().split(";");//���Ȱ��շֺŽ�ȡ
				String[] posts = new String[temp.length];//ȡ�����Ƶ�����
				for (int i = 0; i < temp.length; i++) {
					String[] temps = temp[i].split("#");
					posts[i] = temps[0];
					if (temps.length == 1) {
						colLength[i] = 150;//����в�����Ҫ������ݣ�����"���ղ�#123;�Ϲ沿;"��Ĭ�����ÿ��Ϊ150������ᱨ�쳣�����/2014-10-30��
					} else {
						colLength[i] = Integer.parseInt(temps[1]);
					}
				}
				for (int i = 0; i < posts.length; i++) {
					if (posts[i] == null || posts[i].trim().equals("")) {
						continue;
					}
					columnNames[i] = "��λ����" + (i + 1);
					rowData[0][i] = posts[i];
				}
			} else { //Ĭ�ϵ���ʾ����
				rowData[0][0] = "";
				rowData[0][1] = "";
				rowData[0][2] = "";
				columnNames[0] = "��λ����1";
				columnNames[1] = "��λ����2";
				columnNames[2] = "��λ����3";
			}
			defaultModel = new DefaultTableModel(rowData, columnNames);
			table = new JTable(defaultModel);
			table.setPreferredScrollableViewportSize(new Dimension(600, 200));
			table.setRowHeight(30);//�����и߶�
			table.setEditingColumn(2);
			table.setShowGrid(true);//�Ƿ���ʾ������
			table.setBounds(75, 150, 600, 100);
			table.setRowMargin(5);
			table.setDragEnabled(true);
			table.setColumnSelectionAllowed(true);//����Ϊ��ѡ��ģʽ
			table.setRowSelectionAllowed(false);
			JTableHeader tableHeader = table.getTableHeader();
			tableHeader.setReorderingAllowed(true);
			JScrollPane sss = new JScrollPane(table);//�����Ҫ��ͷ����������仰
			sss.setBounds(75, 150, 500, 51);//���ñ��ĸ߶ȣ�Ĭ�Ͽ��Ϊ500
			WLTButton bt_add = new WLTButton("���");
			bt_add.setBounds(75, 130, 50, 20);
			//�����ⲿ��Ϊ���ô���һ���������Ŀ��
			//Ĭ�Ͽ��Ϊ500������Ҫ������Ӧ�������ÿ��Ϊ��Ӧ����
			Float sum = new Float(0);
			for (int l = 0; l < len; l++) { //����ͣ��������
				sum += colLength[l];
			}
			if (sum != 0) {
				for (int m = 0; m < len; m++) {//���ݴ�������ֵ����ÿ��Ĭ�ϱ��Ŀ��
					TableColumn firstColumn1 = table.getColumnModel().getColumn(m);//��õ�m��
					firstColumn1.setPreferredWidth((int) (colLength[m] / sum * 500));//Ԥ�ÿ��,��ΪĬ�ϵĿ��Ϊ500
				}
			}
			bt_add.addActionListener(new ActionListener() { //��Ӱ�ť����Ӧ����
						public void actionPerformed(ActionEvent e) {
							int columncount = defaultModel.getColumnCount();
							int nowindex = table.getSelectedColumn();//��ȡ��ǰѡ�����
							if (nowindex == -1) {//�����ѡ����Ĭ�ϼ������һ���Ҳ��ƶ�
								nowindex = columncount;
								defaultModel.addColumn("��λ����" + (columncount + 1));
							} else {//�����ѡ������������ƶ�
								if (null != table.getCellEditor()) {
									table.getCellEditor().stopCellEditing();//�����ǰ�Ѿ�ѡ���ˣ���ѵ�ǰ���ȱ���
								}
								defaultModel.addColumn("��λ����" + (columncount + 1));
								table.getColumnModel().moveColumn(columncount, nowindex + 1);
							}
							//defaultModel.
						}
					});
			WLTButton bt_del = new WLTButton("ɾ��");
			bt_del.setBounds(125, 130, 50, 20);
			bt_del.addActionListener(new ActionListener() { //ɾ����ť����Ӧ����
						public void actionPerformed(ActionEvent e) {
							int columncount = defaultModel.getColumnCount() - 1;
							int nowindex = table.getSelectedColumn();//��ȡ��ǰѡ�����
							if (nowindex == -1) {
								nowindex = columncount;
							}
							if (null != defaultModel.getDataVector() && defaultModel.getDataVector().size() > 0) {
								Vector colVector = (Vector) defaultModel.getDataVector().get(0);//��ñ�����
								Vector def = new Vector();//�����ͷ����
								for (int i = 0; i <= columncount; i++) {
									def.add(defaultModel.getColumnName(i));
								}
								if (columncount > -1) {//�����жϣ���ֹɾ������֮���ٵ�ɾ���򱨴�
									def.removeElementAt(nowindex);//��ɾ����ͷ����
									TableColumnModel columnModel = table.getColumnModel();
									TableColumn tableColumn = columnModel.getColumn(nowindex);
									colVector.removeElementAt(nowindex);//ɾ��������
									Object[][] rowDatanew = new Object[1][colVector.size()];
									rowDatanew[0] = colVector.toArray();
									defaultModel = new DefaultTableModel(rowDatanew, def.toArray());//����������
									table.setModel(defaultModel);//��������
									table.updateUI();//����ˢ������
								}
							}
						}
					});
			table.revalidate();//����ػ�
			panel.add(label_posts);
			panel.add(bt_add);
			panel.add(bt_del);
			panel.add(sss); //
			//-----------------------------------------------------------------end-----------------------------------------------------------------------------------
		} else if ("STATION".equalsIgnoreCase(this.celltype)) {//����ǽ׶�

		} else if ("TRANSITION".equalsIgnoreCase(this.celltype)) {//��sunfujun/20120426/���Ӽ�ͷ���͵�����_���֡�
			JLabel label_type = new JLabel("��ͷ����", JLabel.RIGHT); //
			label_type.setBounds(5, 130, 70, 20); //
			combox_type = new JComboBox(new Object[] { new ComBoxItemVO("2", "", "Ĭ��"), new ComBoxItemVO("1", "", "����"), new ComBoxItemVO("4", "", "��Լ"), new ComBoxItemVO("5", "", "Բ��"), new ComBoxItemVO("7", "", "����"), new ComBoxItemVO("8", "", "˫��"), new ComBoxItemVO("9", "", "����"), new ComBoxItemVO("0", "", "��") }); //
			combox_type.addItemListener(new ItemListener() { //�����¼�
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								onLineTypeChanged();
							}
						}
					});
			combox_type.setBounds(75, 130, 100, 20); //
			combox_type.setSelectedIndex(0);
			for (int i = 0; i < combox_type.getItemCount(); i++) {
				if ((this.lineType + "").equals(((ComBoxItemVO) combox_type.getItemAt(i)).getId())) {
					combox_type.setSelectedIndex(i);
					break;
				}
			}
			JLabel label_single = new JLabel("�Ƿ���", JLabel.RIGHT); //
			label_single.setBounds(5, 150, 70, 20); //
			combox_single = new JComboBox(new Object[] { "��", "��" }); //
			combox_single.setBounds(75, 150, 100, 20); //
			combox_single.addItemListener(new ItemListener() { //�����¼�
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								onSingleChanged();
							}
						}
					});
			if (this.isSingle) {
				combox_single.setSelectedIndex(0);
			} else {
				combox_single.setSelectedIndex(1);
			}
			panel.add(label_type); //
			panel.add(combox_type); //
			panel.add(label_single); //
			panel.add(combox_single); //
		}
		return panel; //
	}

	/**
		 * panel����󣬻�ý���������塾��ҫ��/2012-03-12��
		 * ���������⣬���ѡ�������С����������ʱ��ִ��ˢ�£������ѡ�񻷽����ͣ�ͼƬ�����ˢ�£�
		 * ��ʱˢ���߼�����ִ�������ƿ��ý�����߼����ʸо����������ܵ㲻����
		 * �����/2012-05-24��
		 * 
		 */
	/*
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			textarea_content.requestFocus(); //
		}*/

	public String getFontType() {
		return fontType;
	}

	public int getFontSize() {
		return fontSize;
	}

	public Color getForeGround() {
		return foreGround;
	}

	public Color getBackGround() {
		return backGround;
	}

	public String getImgstr() {
		return imgstr;
	}

	public String getContentStr() {
		return contentStr;
	}

	public String getPostsStr() {
		return postsStr;
	}

	public String getSeq() {
		return seq;
	}

	public int getViewType() {
		return combox_type.getSelectedIndex() + 1;
	}

	public int getCloseType() {
		return closeType;
	}

	public boolean isCanSetCellColor() {
		return isCanSetCellColor;
	}

	public void setCanSetCellColor(boolean isCanSetCellColor) {
		this.isCanSetCellColor = isCanSetCellColor;
		if (this.isCanSetCellColor) {
			ref_foreground.setVisible(true);
			ref_background.setVisible(true);
		} else {
			ref_foreground.setVisible(false);
			ref_background.setVisible(false);
		}
	}

	private boolean existFont(String[] _allFonts, String _font) {
		for (int i = 0; i < _allFonts.length; i++) {
			if (_allFonts[i].equals(_font)) {
				return true;
			}
		}
		return false;
	}

	protected void onTypeChanged() {
		if (viewtype == 8 && getViewType() != 8) {//�ı�ǰ״̬ΪͼƬ����
			ref_img.setVisible(false);
			ref_background.setItemEditable(true);
			ref_background.getTextField().setBackground(convertStrToColor(ref_background.getValue())); //���ñ�����ɫ
		} else if (viewtype != 8 && getViewType() == 8) {
			ref_img.setVisible(true);
			ref_background.setItemEditable(false);
		}
		viewtype = getViewType();
	}

	protected void onLineTypeChanged() {
		this.lineType = Integer.parseInt(((ComBoxItemVO) combox_type.getSelectedItem()).getId());
	}

	protected void onSingleChanged() {
		if (combox_single.getSelectedIndex() == 0) {
			this.isSingle = true;
		} else {
			this.isSingle = false;
		}
	}

	public Integer getLineType() {
		return lineType;
	}

	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	public boolean isSingle() {
		return isSingle;
	}

	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}

	/**
	 * ����ɫת�����ַ���,�Զ��Ž�R,G,B���
	 * @param _color
	 * @return
	 */
	private String convertColorToStr(Color _color) {
		return _color.getRed() + "," + _color.getGreen() + "," + _color.getBlue(); //
	}

	/**
	 * ���ַ���ת������ɫ
	 * @param _str
	 * @return
	 */
	private Color convertStrToColor(String _str) {
		String[] items = _str.split(","); //
		return new Color(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]));
	}

	/**
	 * ��ť���
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //

		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private void onConfirm() {
		if (null != table) {//Ԭ����20121119���ģ���Ҫ����޸Ļ�������ʱΪnull������
			if (null != table.getCellEditor()) {
				table.getCellEditor().stopCellEditing();//�����ǰ�Ѿ�ѡ���ˣ���ѵ�ǰ���ȱ���
			}
		}
		if (combox_fonttype.getSelectedItem() != null && !((String) combox_fonttype.getSelectedItem()).equals("")) {
			this.fontType = (String) combox_fonttype.getSelectedItem(); //
		}
		if (combox_fontsize.getSelectedItem() != null && !((String) combox_fontsize.getSelectedItem()).equals("")) {
			this.fontSize = Integer.parseInt((String) combox_fontsize.getSelectedItem()); //
		}
		this.foreGround = convertStrToColor(ref_foreground.getValue()); //ǰ����ɫ.
		this.backGround = convertStrToColor(ref_background.getValue()); //����ɫ.
		this.imgstr = ref_img.getValue();
		this.contentStr = textarea_content.getText(); //
		if ("ACTIVITY".equalsIgnoreCase(this.celltype)) {//����ǻ���
			this.seq = textarea_seq.getText();
		} else if ("DEPT".equalsIgnoreCase(this.celltype)) {
			//Ԭ����20120906���  �޸�֮ǰ�������Ϊ����ӣ����տ��ӵĿ�Ⱥʹ�����������λ
			//-----------------------------------------------------------------begin-----------------------------------------------------------------------------------
			StringBuffer sb_post = new StringBuffer();
			for (int i = 0; i < table.getColumnCount(); i++) {
				String columnName = table.getColumnName(i);
				if (null != table.getValueAt(0, i) && !table.getValueAt(0, i).equals("")) {//�����λ��Ϊ������ӣ��������
					sb_post.append(table.getValueAt(0, i) + "#" + table.getColumn(columnName).getWidth() + ";");//���ظ�λ���ƺͿ�ȣ��м��÷ֺŸ�������λ���ƺͿ��֮����$$����
				}
			}
			this.postsStr = sb_post.toString();
			//-----------------------------------------------------------------end-----------------------------------------------------------------------------------			
		}

		this.closeType = 1;
		this.dispose(); //
	}

	private void onCancel() {
		this.closeType = 2;
		this.dispose(); //
	}
}
