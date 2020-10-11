package cn.com.infostrategy.ui.sysapp.database.transfer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.BillHtmlPanel;


public class CMPPanel_MultiTransfer extends AbstractWorkPanel  implements ActionListener {

	/**
	 * ��ʾ��
	 */
	private static final long serialVersionUID = 340089321889669875L;

	private JPanel jPanel = null;
	private JLabel jLabel_Left = null;
	private JLabel jLabel_Right = null;
	private JLabel jLabel_Type = null;   //���ݿ�����    20130425
	private JLabel jLabel_Prefix = null;   //����ǰ׺    20130428
	private JComboBox jComboBox_Left = null;
	private JComboBox jComboBox_Right = null;
	private DataSourceVO[] dataSource = null;
	private JComboBox jComboBox_Type = null;    //���ݿ�����    20130425
	private JTextField jTextPrefix  =null;    //����ǰ׺   20130428
	private JButton jButton1 = null;   //��ṹ�Ƚ�
	private JButton jButton2 = null;   //���������������
	private JButton jButton3 = null;   //�������б������
	private JButton jButton4 = null;//�����ݿ�1�е����ݵ��������ݿ�2��    Ԭ����   20130502 ���
	private BillHtmlPanel billHtmlPanel = null;
	private HashMap map = null;

	public void initialize() {
		dataSource = ClientEnvironment.getInstance().getDataSourceVOs();
		billHtmlPanel = new BillHtmlPanel();
		map = new HashMap();
		jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,10));
		jLabel_Left = new JLabel("����Դ1:", SwingConstants.RIGHT);
		jLabel_Right = new JLabel("����Դ2:", SwingConstants.RIGHT);
		jLabel_Type = new JLabel("����", SwingConstants.RIGHT);
		jLabel_Prefix = new JLabel("����ǰ׺:", SwingConstants.RIGHT);
		jTextPrefix=new JTextField();

		jLabel_Left.setPreferredSize(new Dimension(55, 20));
		jLabel_Right.setPreferredSize(new Dimension(55,20));
		jLabel_Type.setPreferredSize(new Dimension(55,20));
		jLabel_Prefix.setPreferredSize(new Dimension(55,20));

		jComboBox_Left = new JComboBox();
		jComboBox_Right = new JComboBox();
		jComboBox_Type = new JComboBox();
		for (int i = 0; i < dataSource.length; i++) {
			jComboBox_Left.addItem(dataSource[i].getName());
			jComboBox_Right.addItem(dataSource[i].getName());
		}
		jComboBox_Type.addItem("ORACLE");
		jComboBox_Type.addItem("Mysql");
		
		
		jButton1 = new WLTButton("��ṹ�Ƚ�");
		jButton2 = new WLTButton("��������������");
		jButton3 = new WLTButton("�������б�����");
		jButton4 = new WLTButton("������������");//���û�б�����Դ2�д�������������ȱ���в�ͬ����ӣ����Դ2�е��ظ�������������ɾ����
		jButton1.addActionListener(this);
		jButton2.addActionListener(this);
		jButton3.addActionListener(this);
		jButton4.addActionListener(this);

		jComboBox_Left.setPreferredSize(new Dimension(100, 20));
		jComboBox_Right.setPreferredSize(new Dimension(100, 20));
		jComboBox_Type.setPreferredSize(new Dimension(100, 20));
		jTextPrefix.setPreferredSize(new Dimension(70, 20));

		jPanel.add(jLabel_Left);
		jPanel.add(jComboBox_Left);
		jPanel.add(jLabel_Right);
		jPanel.add(jComboBox_Right);
		jPanel.add(jLabel_Type);
		jPanel.add(jComboBox_Type);
		
		jPanel.add(jLabel_Prefix);//����ǰ׺
		jPanel.add(jTextPrefix);  //����ǰ׺
		jPanel.add(jButton1);
		jPanel.add(jButton2);
		jPanel.add(jButton3);
		jPanel.add(jButton4);
		this.setLayout(new BorderLayout());
		this.add(jPanel, BorderLayout.NORTH);
		this.add(billHtmlPanel, BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent e) {
		if(jComboBox_Left.getSelectedItem().toString().equals(jComboBox_Right.getSelectedItem().toString())){
			MessageBox.show("���ܱȽ�������ͬ������Դ��");
			return ;
		}
		if (e.getSource() == jButton1) { //�Ƚ�
			map.put("type", "1");
			map.put("title", jButton1.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
			map.put("tabenamePrefix",jTextPrefix.getText());  //��ǰ׺
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
		}if (e.getSource() == jButton2) {//��������������
			if(MessageBox.confirm(this, "ȷ��Ҫ���������ı�ṹ��������")){
				map.put("type", "2");
				map.put("title", jButton1.getText());
				map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
				map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
				map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
				map.put("tabenamePrefix",jTextPrefix.getText());  //��ǰ׺
				billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
			}
		}if (e.getSource() == jButton3) {//�������б�����
			map.put("type", "3");
			map.put("title", jButton1.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
			map.put("tabenamePrefix",jTextPrefix.getText());  //��ǰ׺
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
		}else if (e.getSource() == jButton4) {  ////���û�б�����Դ2�д�������������ȱ���в�ͬ����ӣ����Դ2�е��ظ�������������ɾ����   Ԭ����  ��ӵ�������ݹ��ܰ�����ṹ
			String confirm_str="������Դ1�е����ݵ��뵽����Դ2�У����2��֮ǰ�����ݽ��ᱻɾ�����Ƿ������";
			if(!jTextPrefix.getText().trim().equals("")){
				confirm_str="������Դ1����"+jTextPrefix.getText().trim()+"��ͷ�ı�����ݵ��뵽����Դ2�У����2��֮ǰ��Ӧ�ı������ݽ��ᱻɾ�����Ƿ������";
			}else{
				confirm_str="������Դ1�е����ݵ��뵽����Դ2�У����2��֮ǰ��Ӧ�ı������ݽ��ᱻɾ�����Ƿ������";
			}
			boolean bb=MessageBox.confirm(this, confirm_str);
			if(bb){
				map.put("type", "4");
				map.put("title", jButton4.getText());
				map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
				map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
				map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
				map.put("tabenamePrefix",jTextPrefix.getText());  //��ǰ׺
				billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
			}
		}
	}
}
