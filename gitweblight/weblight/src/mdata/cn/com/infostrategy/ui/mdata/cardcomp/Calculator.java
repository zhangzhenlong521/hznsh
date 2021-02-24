/**************************************************************************
 * $RCSfile: Calculator.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Calculator implements ActionListener { //���붯�������ӿ�
	//�������еĵ�λ
	private JFrame frame;
	private JTextField textAnswer;
	private JPanel panel, panel1, panel2, panel3;
	private JMenuBar mainMenu;
	private JTextField textMemory;
	private JLabel labelMemSpace; //labelMemSpace���������裬����������״
	private JButton buttonBk, buttonCe, buttonC;
	private JButton button[];
	private JButton buttonMC, buttonMR, buttonMS, buttonMAdd;
	private JButton buttonDot, buttonAddAndSub, buttonAdd, buttonSub, buttonMul, buttonDiv, buttonMod;
	private JButton buttonSqrt, buttonDao, buttonEqual;
	private JMenu editMenu, viewMenu, helpMenu;
	private JMenuItem copyItem, pasteItem, tItem, sItem, numberGroup, topHelp, aboutCal;
	private DecimalFormat df; //���������������
	private boolean clickable; //���Ƶ�ǰ�ܷ񰴼�
	private double memoryd; //ʹ���ڴ��д洢������

	private int memoryi;
	private double vard, answerd; //��������double�����ݵ��м�ֵ(vard)�������(answerd)
	private short key = -1, prekey = -1; //key�������浱ǰ���к�������,prekey��������ǰ�ν��к�������
	private String copy; //��������
	private JTextArea help; //����
	private JScrollPane scrollHelp;

	//���캯��

	public Calculator() {

		clickable = true;

		answerd = 0;

		frame = new JFrame("������");

		df = new DecimalFormat("0.##############"); //���������������(����double��ֵ)

		textAnswer = new JTextField(15);

		textAnswer.setText("");

		textAnswer.setEditable(false);

		textAnswer.setBackground(new Color(255, 255, 255));

		panel = new JPanel();

		frame.getContentPane().add(panel);

		panel1 = new JPanel();

		panel2 = new JPanel();

		panel.setLayout(new BorderLayout());
		//����������

		mainMenu = new JMenuBar();

		editMenu = new JMenu("�༭(E)");

		viewMenu = new JMenu("�鿴(V)");

		helpMenu = new JMenu("����(H)");

		copyItem = new JMenuItem("   ����(C) Ctrl+C");

		copyItem.addActionListener(this);

		pasteItem = new JMenuItem("   ճ��(V) Ctrl+V");

		pasteItem.addActionListener(this);

		editMenu.add(copyItem);

		editMenu.add(pasteItem);

		tItem = new JMenuItem("���׼��(T)");

		tItem.addActionListener(this);

		sItem = new JMenuItem("   ��ѧ��(S)");

		sItem.addActionListener(this);

		numberGroup = new JMenuItem("   ���ַ���(I)");

		numberGroup.addActionListener(this);

		viewMenu.add(tItem);

		viewMenu.add(sItem);

		viewMenu.add(numberGroup);

		topHelp = new JMenuItem("   ��������(H)");

		topHelp.addActionListener(this);

		help = new JTextArea(5, 20);

		scrollHelp = new JScrollPane(help);

		help.setEditable(false);

		help.append("ִ�м򵥼���\n");

		help.append("1.  �������ĵ�һ�����֡�\n");

		help.append("2.  ������+��ִ�мӡ���-��ִ�м�����*��ִ�г˻�/��ִ�г���\n");

		help.append("3.  ����������һ�����֡�\n");

		help.append("4.  ��������ʣ�������������֡�\n");

		help.append("5.  ������=����\n");
		aboutCal = new JMenuItem("   ���ڼ�����(A)");

		aboutCal.addActionListener(this);

		helpMenu.add(topHelp);

		helpMenu.add(aboutCal);

		mainMenu.add(editMenu);

		mainMenu.add(viewMenu);

		mainMenu.add(helpMenu);

		panel.add(mainMenu, BorderLayout.NORTH);

		panel.add(textAnswer, BorderLayout.CENTER);

		panel.add(panel1, BorderLayout.SOUTH);

		panel1.setLayout(new BorderLayout());

		textMemory = new JTextField(3);

		textMemory.setEditable(false);

		textMemory.setBackground(new Color(217, 217, 217));

		labelMemSpace = new JLabel("                   ");

		buttonBk = new JButton("Backspace");

		buttonBk.setForeground(new Color(255, 0, 0));

		buttonCe = new JButton("CE");

		buttonCe.setForeground(new Color(255, 0, 0));

		buttonC = new JButton("C");

		buttonC.setForeground(new Color(255, 0, 0));

		buttonBk.addActionListener(this);

		buttonCe.addActionListener(this);

		buttonC.addActionListener(this);

		panel1.add(panel2, BorderLayout.NORTH);

		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));

		panel2.add(textMemory);

		panel2.add(labelMemSpace);

		panel2.add(buttonBk);

		panel2.add(buttonCe);

		panel2.add(buttonC);

		panel3 = new JPanel();

		panel1.add(panel3, BorderLayout.CENTER);

		button = new JButton[10];

		for (int i = 0; i < button.length; i++) {

			button[i] = new JButton(Integer.toString(i));

			button[i].setForeground(new Color(0, 0, 255));

		}

		buttonMC = new JButton("MC");

		buttonMC.setForeground(new Color(255, 0, 0));

		buttonMR = new JButton("MR");

		buttonMR.setForeground(new Color(255, 0, 0));

		buttonMS = new JButton("MS");

		buttonMS.setForeground(new Color(255, 0, 0));

		buttonMAdd = new JButton("M+");

		buttonMAdd.setForeground(new Color(255, 0, 0));

		buttonDot = new JButton(".");

		buttonDot.setForeground(new Color(0, 0, 255));

		buttonAddAndSub = new JButton("+/-");

		buttonAddAndSub.setForeground(new Color(0, 0, 255));

		buttonAdd = new JButton("+");

		buttonAdd.setForeground(new Color(255, 0, 0));

		buttonSub = new JButton("-");

		buttonSub.setForeground(new Color(255, 0, 0));

		buttonMul = new JButton("*");

		buttonMul.setForeground(new Color(255, 0, 0));

		buttonDiv = new JButton("/");

		buttonDiv.setForeground(new Color(255, 0, 0));

		buttonMod = new JButton("%");

		buttonMod.setForeground(new Color(0, 0, 255));

		buttonSqrt = new JButton("sqrt");

		buttonSqrt.setForeground(new Color(0, 0, 255));

		buttonDao = new JButton("1/x");

		buttonDao.setForeground(new Color(0, 0, 255));

		buttonEqual = new JButton("=");

		buttonEqual.setForeground(new Color(255, 0, 0));

		//��������Ϊ�������

		panel3.setLayout(new GridLayout(4, 6));

		panel3.add(buttonMC);

		buttonMC.addActionListener(this);

		panel3.add(button[7]);

		button[7].addActionListener(this);

		panel3.add(button[8]);

		button[8].addActionListener(this);

		panel3.add(button[9]);

		button[9].addActionListener(this);

		panel3.add(buttonDiv);

		buttonDiv.addActionListener(this);

		panel3.add(buttonSqrt);

		buttonSqrt.addActionListener(this);

		panel3.add(buttonMR);

		buttonMR.addActionListener(this);

		panel3.add(button[4]);

		button[4].addActionListener(this);

		panel3.add(button[5]);

		button[5].addActionListener(this);

		panel3.add(button[6]);

		button[6].addActionListener(this);

		panel3.add(buttonMul);

		buttonMul.addActionListener(this);

		panel3.add(buttonMod);

		buttonMod.addActionListener(this);

		panel3.add(buttonMS);

		buttonMS.addActionListener(this);

		panel3.add(button[1]);

		button[1].addActionListener(this);

		panel3.add(button[2]);

		button[2].addActionListener(this);

		panel3.add(button[3]);

		button[3].addActionListener(this);

		panel3.add(buttonSub);

		buttonSub.addActionListener(this);

		panel3.add(buttonDao);

		buttonDao.addActionListener(this);

		panel3.add(buttonMAdd);

		buttonMAdd.addActionListener(this);

		panel3.add(button[0]);

		button[0].addActionListener(this);

		panel3.add(buttonAddAndSub);

		buttonAddAndSub.addActionListener(this);

		panel3.add(buttonDot);

		buttonDot.addActionListener(this);

		panel3.add(buttonAdd);

		buttonAdd.addActionListener(this);

		panel3.add(buttonEqual);

		buttonEqual.addActionListener(this);

		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

		frame.pack();

		frame.show();
	}

	public void actionPerformed(ActionEvent event) {
		boolean sign = false; //�ж��Ƿ���double�����������㣬��Ϊtrue������Ϊfalse
		Object temp = event.getSource();
		try {
			//����������ݰ�ť�������µİ�ť��������ݲ���ĵ�ǰ�ı����ַ���֮��
			for (int i = 0; i <= 9; i++)
				if (temp == button[i] && clickable == true)

					textAnswer.setText(textAnswer.getText() + Integer.toString(i));

			//����'.'��ťʱ���жϵ�ǰ�ı������ַ����к�����'.'������Ѻ����������ٲ���'.'

			if (temp == buttonDot && clickable == true) {

				boolean isDot = false;

				if (textAnswer.getText().length() == 0)

					isDot = true;

				for (int i = 0; i < textAnswer.getText().length(); i++)

					if ('.' == textAnswer.getText().charAt(i)) {

						isDot = true;

						break;

					}

				if (isDot == false)

					textAnswer.setText(textAnswer.getText() + ".");

			}

			if ((temp == buttonAdd || temp == buttonSub || temp == buttonMul ||

			temp == buttonDiv) && clickable == true) {

				//'+'����

				if (temp == buttonAdd) {

					switch (prekey) {

					case 0:

						answerd += Double.parseDouble(textAnswer.getText());

						break;

					case 1:

						answerd -= Double.parseDouble(textAnswer.getText());

						break;

					case 2:

						answerd *= Double.parseDouble(textAnswer.getText());

						break;

					case 3:

						if (Double.parseDouble(textAnswer.getText()) == 0) {

							textAnswer.setText("��������Ϊ��");

							clickable = false;

						}

						else

							answerd /= Double.parseDouble(textAnswer.getText());

						break;

					default:

						answerd = Double.parseDouble(textAnswer.getText());

					}

					textAnswer.setText("");

					prekey = key = 0;

				}

				//'-'����

				if (temp == buttonSub) {

					switch (prekey) {

					case 0:

						answerd += Double.parseDouble(textAnswer.getText());

						break;

					case 1:

						answerd -= Double.parseDouble(textAnswer.getText());

						break;

					case 2:

						answerd *= Double.parseDouble(textAnswer.getText());

						break;

					case 3:

						if (Double.parseDouble(textAnswer.getText()) == 0) {

							textAnswer.setText("��������Ϊ��");

							clickable = false;

						}

						else

							answerd /= Double.parseDouble(textAnswer.getText());

						break;

					default:

						answerd = Double.parseDouble(textAnswer.getText());

					}

					textAnswer.setText("");

					prekey = key = 1;

				}

				//'*'����

				if (temp == buttonMul) {

					switch (prekey) {

					case 0:

						answerd += Double.parseDouble(textAnswer.getText());

						break;

					case 1:

						answerd -= Double.parseDouble(textAnswer.getText());

						break;

					case 2:

						answerd *= Double.parseDouble(textAnswer.getText());

						break;

					case 3:

						if (Double.parseDouble(textAnswer.getText()) == 0) {

							textAnswer.setText("��������Ϊ��");

							clickable = false;

						}

						else

							answerd /= Double.parseDouble(textAnswer.getText());

						break;

					default:

						answerd = Double.parseDouble(textAnswer.getText());

					}

					textAnswer.setText("");

					prekey = key = 2;

				}

				//'/'����

				if (temp == buttonDiv) {

					switch (prekey) {

					case 0:

						answerd += Double.parseDouble(textAnswer.getText());

						break;

					case 1:

						answerd -= Double.parseDouble(textAnswer.getText());

						break;

					case 2:

						answerd *= Double.parseDouble(textAnswer.getText());

						break;

					case 3:

						if (Double.parseDouble(textAnswer.getText()) == 0) {

							textAnswer.setText("��������Ϊ��");

							clickable = false;

						}

						else

							answerd /= Double.parseDouble(textAnswer.getText());

						break;

					default:

						answerd = Double.parseDouble(textAnswer.getText());

					}

					textAnswer.setText("");

					prekey = key = 3;

				}

			}

			//'='����

			if (temp == buttonEqual && clickable == true) {

				//���������'=',�������������

				if (prekey == 5) {

					if (key == 0) {

						answerd += vard;

						textAnswer.setText(df.format(answerd));

					}

					if (key == 1) {

						answerd -= vard;

						textAnswer.setText(df.format(answerd));

					}

					if (key == 2) {

						answerd *= vard;

						textAnswer.setText(df.format(answerd));

					}

					if (key == 3) {

						if (Double.parseDouble(textAnswer.getText()) == 0) {

							textAnswer.setText("��������Ϊ��");

							clickable = false;

						}

						else {

							answerd /= vard;

							textAnswer.setText(df.format(answerd));

						}

					}

				}

				else {

					vard = Double.parseDouble(textAnswer.getText());

					if (key == 0) {

						prekey = -1;

						answerd += Double.parseDouble(textAnswer.getText());

						textAnswer.setText(df.format(answerd));

					}

					if (key == 1) {

						prekey = -1;

						answerd -= Double.parseDouble(textAnswer.getText());

						textAnswer.setText(df.format(answerd));

					}

					if (key == 2) {

						prekey = -1;

						answerd *= Double.parseDouble(textAnswer.getText());

						textAnswer.setText(df.format(answerd));

					}

					if (key == 3) {

						prekey = -1;

						if (Double.parseDouble(textAnswer.getText()) == 0) {

							textAnswer.setText("��������Ϊ��");

							clickable = false;

						}

						else {

							answerd /= Double.parseDouble(textAnswer.getText());

							textAnswer.setText(df.format(answerd));

						}

					}

				}

				prekey = 5;

			}

			//'%'�������Եڶ�������������100

			if (temp == buttonMod && clickable == true) {

				if (answerd == 0) {

					String s = textAnswer.getText();

					textAnswer.setText(s);

				}

				else {

					boolean isDot = false;

					for (int i = 0; i < textAnswer.getText().length(); i++)

						if ('.' == textAnswer.getText().charAt(i)) {

							isDot = true;

							break;

						}

					//�����double������100

					if (isDot == true) {

						double dtemp = Double.parseDouble(textAnswer.getText());

						dtemp = dtemp / 100.0;

						textAnswer.setText(Double.toString(dtemp));

					}

					else {

						//�����int�����ܱ�100��������ȥ��ĩβ������

						if (Integer.parseInt(textAnswer.getText()) % 100 == 0) {

							int itemp = Integer.parseInt(textAnswer.getText());

							itemp /= 100;

							textAnswer.setText(Integer.toString(itemp));

						}

						//�����int���������ܱ�100��������double������

						else {

							double dtemp = Double.parseDouble(textAnswer.getText());

							dtemp = dtemp / 100.0;

							textAnswer.setText(Double.toString(dtemp));

						}

					}

				}

			}

			//����������

			if (temp == buttonSqrt && clickable == true) {

				String s = textAnswer.getText();

				if (s.charAt(0) == '-') {

					textAnswer.setText("�������ܿ�����");

					clickable = false;

				}

				else

					textAnswer.setText(Double.toString(java.lang.Math.sqrt(Double.

					parseDouble(textAnswer.getText()))));

			}

			//��������

			if (temp == buttonDao && clickable == true) {

				if (textAnswer.getText().charAt(0) == '0' &&

				textAnswer.getText().length() == 1) {

					textAnswer.setText("�㲻������");

					clickable = false;

				}

				else {

					boolean isDec = true;

					int i, j, k;

					String s = Double.toString(1 / Double.parseDouble(textAnswer.getText()));

					for (i = 0; i < s.length(); i++)

						if (s.charAt(i) == '.')

							break;

					for (j = i + 1; j < s.length(); j++)

						if (s.charAt(j) != '0') {

							isDec = false;

							break;

						}

					if (isDec == true) {

						String stemp = "";

						for (k = 0; k < i; k++)

							stemp += s.charAt(k);

						textAnswer.setText(stemp);

					}

					else

						textAnswer.setText(s);

				}

			}

			//����'+/-'��ťʱ����

			if (temp == buttonAddAndSub && clickable == true) {

				boolean isNumber = true;

				String s = textAnswer.getText();

				for (int i = 0; i < s.length(); i++)

					if (!(s.charAt(i) >= '0' && s.charAt(i) <= '9' || s.charAt(i) == '.' ||

					s.charAt(i) == '-')) {

						isNumber = false;

						break;

					}

				if (isNumber == true) {

					//�����ǰ�ַ�������ĸ��'-'��,���������Ǹ�����,�ٰ���ʱ,���׷���ȥ��

					if (s.charAt(0) == '-') {

						textAnswer.setText("");

						for (int i = 1; i < s.length(); i++) {

							char a = s.charAt(i);

							textAnswer.setText(textAnswer.getText() + a);

						}

					}

					//�����ǰ�ַ�����һ���ַ����Ƿ��ţ������һ������������ĸ��

					else

						textAnswer.setText('-' + s);

				}

			}

			//�������й��ڴ����

			//'MC'�Ĳ��������ڴ���0

			if (temp == buttonMC && clickable == true) {

				memoryd = memoryi = 0;

				textMemory.setText("");

			}

			//'MS'�Ĳ���������ǰ�ı������ݱ������ڴ棬��ʾ'M'

			if (temp == buttonMS && clickable == true) {

				boolean isDot = false;

				textMemory.setText("   M");

				for (int i = 0; i < textAnswer.getText().length(); i++)

					if ('.' == textAnswer.getText().charAt(i)) {

						isDot = true;

						break;

					}

				//�����double,�����memoryd(double�洢��)

				if (isDot == true) {

					memoryd = Double.parseDouble(textAnswer.getText());

					memoryi = 0; //��֤�洢���д�����µ�ֵ

				}

				//�����int,�����memoryi(int�洢��)

				else {

					memoryi = Integer.parseInt(textAnswer.getText());

					memoryd = 0; //��֤�洢���д�����µ�ֵ

				}

			}

			//'MR'�Ĳ��������洢���е���Ϣ���

			if (temp == buttonMR && clickable == true) {

				if (memoryd != 0)

					textAnswer.setText(Double.toString(memoryd));

				if (memoryi != 0)

					textAnswer.setText(Integer.toString(memoryi));

			}

			//'M+'�Ĺ��ܣ�����ǰ�ı���������ݺʹ洢����������Ӻ��ٴ���洢��

			if (temp == buttonMAdd && clickable == true) {

				boolean isDot = false;

				for (int i = 0; i < textAnswer.getText().length(); i++)

					if ('.' == textAnswer.getText().charAt(i)) {

						isDot = true;

						break;

					}

				if (memoryi != 0) { //�洢����һ��int����

					if (isDot == false) //��������һ��int����

						memoryi += Integer.parseInt(textAnswer.getText());

					else { //��������һ��double��������int�洢����������double�洢���뵱ǰ����ӣ�int�洢������

						memoryd = memoryi + Double.parseDouble(textAnswer.getText());

						memoryi = 0;

					}

				}

				else

					memoryd += Double.parseDouble(textAnswer.getText());

			}

			//����'Backspace'��������ѭ������ǰ�ַ����е����һ����ĸɾ��

			if (temp == buttonBk && clickable == true) {

				String s = textAnswer.getText();

				textAnswer.setText("");

				for (int i = 0; i < s.length() - 1; i++) {

					char a = s.charAt(i);

					textAnswer.setText(textAnswer.getText() + a);

				}

			}

			//����'CE'��ť������ǰ�ı������������

			if (temp == buttonCe) {

				textAnswer.setText("");

				clickable = true;

			}

			//����'C'��ť���ı��������������ͬʱvar,answer��0

			if (temp == buttonC) {

				vard = answerd = 0;

				textAnswer.setText("");

				clickable = true;

			}
			//	    ����'����'�˵���

			if (temp == copyItem) {

				copy = textAnswer.getText();

			}
			//����'ճ��'�˵���

			if (temp == pasteItem) {

				textAnswer.setText(copy);

			}
			if (temp == sItem) {

				JOptionPane.showMessageDialog(panel, "��ǰ�Ǳ�׼�ͼ�����,\n��ѧ�ͼ������д�����.");

			}

			//����'��������'�˵���

			if (temp == topHelp) {
				JOptionPane.showMessageDialog(panel, scrollHelp);

			}
			//����'���ַ���'�˵���

			if (temp == numberGroup) {

				if (numberGroup.getText().compareTo("   ���ַ���(I)") == 0)

					numberGroup.setText("�����ַ���(I)");

				else

					numberGroup.setText("   ���ַ���(I)");

			}
			//����'����'�˵���

			if (temp == aboutCal) {

				JOptionPane.showMessageDialog(panel, "������1.00��\n�����ߣ�¥��");

			}

		} catch (Exception e) {//����������в����Ƿ������簴������'+'�������쳣
			textAnswer.setText("�����Ƿ�!");
			clickable = false;

		}

	}

	//������

	public static void main(String args[]) {

		new Calculator();

	}

}
/**************************************************************************
 * $RCSfile: Calculator.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: Calculator.java,v $
 * Revision 1.5  2012/09/14 09:22:56  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2012/08/06 09:21:05  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:44  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:29  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:41  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:24  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:31  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
