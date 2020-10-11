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

public class Calculator implements ActionListener { //导入动作监听接口
	//设计面板中的单位
	private JFrame frame;
	private JTextField textAnswer;
	private JPanel panel, panel1, panel2, panel3;
	private JMenuBar mainMenu;
	private JTextField textMemory;
	private JLabel labelMemSpace; //labelMemSpace单纯做摆设，控制面板的形状
	private JButton buttonBk, buttonCe, buttonC;
	private JButton button[];
	private JButton buttonMC, buttonMR, buttonMS, buttonMAdd;
	private JButton buttonDot, buttonAddAndSub, buttonAdd, buttonSub, buttonMul, buttonDiv, buttonMod;
	private JButton buttonSqrt, buttonDao, buttonEqual;
	private JMenu editMenu, viewMenu, helpMenu;
	private JMenuItem copyItem, pasteItem, tItem, sItem, numberGroup, topHelp, aboutCal;
	private DecimalFormat df; //设置数据输出精度
	private boolean clickable; //控制当前能否按键
	private double memoryd; //使用内存中存储的数字

	private int memoryi;
	private double vard, answerd; //用来保存double型数据的中间值(vard)和最后结果(answerd)
	private short key = -1, prekey = -1; //key用来保存当前进行何种运算,prekey用来保存前次进行何种运算
	private String copy; //做复制用
	private JTextArea help; //帮助
	private JScrollPane scrollHelp;

	//构造函数

	public Calculator() {

		clickable = true;

		answerd = 0;

		frame = new JFrame("计算器");

		df = new DecimalFormat("0.##############"); //设置数据输出精度(对于double型值)

		textAnswer = new JTextField(15);

		textAnswer.setText("");

		textAnswer.setEditable(false);

		textAnswer.setBackground(new Color(255, 255, 255));

		panel = new JPanel();

		frame.getContentPane().add(panel);

		panel1 = new JPanel();

		panel2 = new JPanel();

		panel.setLayout(new BorderLayout());
		//设计整个面板

		mainMenu = new JMenuBar();

		editMenu = new JMenu("编辑(E)");

		viewMenu = new JMenu("查看(V)");

		helpMenu = new JMenu("帮助(H)");

		copyItem = new JMenuItem("   复制(C) Ctrl+C");

		copyItem.addActionListener(this);

		pasteItem = new JMenuItem("   粘贴(V) Ctrl+V");

		pasteItem.addActionListener(this);

		editMenu.add(copyItem);

		editMenu.add(pasteItem);

		tItem = new JMenuItem("●标准型(T)");

		tItem.addActionListener(this);

		sItem = new JMenuItem("   科学型(S)");

		sItem.addActionListener(this);

		numberGroup = new JMenuItem("   数字分组(I)");

		numberGroup.addActionListener(this);

		viewMenu.add(tItem);

		viewMenu.add(sItem);

		viewMenu.add(numberGroup);

		topHelp = new JMenuItem("   帮助主题(H)");

		topHelp.addActionListener(this);

		help = new JTextArea(5, 20);

		scrollHelp = new JScrollPane(help);

		help.setEditable(false);

		help.append("执行简单计算\n");

		help.append("1.  键入计算的第一个数字。\n");

		help.append("2.  单击“+”执行加、“-”执行减、“*”执行乘或“/”执行除。\n");

		help.append("3.  键入计算的下一个数字。\n");

		help.append("4.  输入所有剩余的运算符和数字。\n");

		help.append("5.  单击“=”。\n");
		aboutCal = new JMenuItem("   关于计算器(A)");

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

		//将所有行为与监听绑定

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
		boolean sign = false; //判断是否是double型数参与运算，是为true，不是为false
		Object temp = event.getSource();
		try {
			//如果按下数据按钮，将按下的按钮代表的数据插入的当前文本框字符串之后
			for (int i = 0; i <= 9; i++)
				if (temp == button[i] && clickable == true)

					textAnswer.setText(textAnswer.getText() + Integer.toString(i));

			//按下'.'按钮时，判断当前文本框内字符串中含不含'.'，如果已含，则不允许再插入'.'

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

				//'+'操作

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

							textAnswer.setText("除数不能为零");

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

				//'-'操作

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

							textAnswer.setText("除数不能为零");

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

				//'*'操作

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

							textAnswer.setText("除数不能为零");

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

				//'/'操作

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

							textAnswer.setText("除数不能为零");

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

			//'='操作

			if (temp == buttonEqual && clickable == true) {

				//如果连续按'=',则进行连续运算

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

							textAnswer.setText("除数不能为零");

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

							textAnswer.setText("除数不能为零");

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

			//'%'操作，对第二个操作数除以100

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

					//如果是double数，除100

					if (isDot == true) {

						double dtemp = Double.parseDouble(textAnswer.getText());

						dtemp = dtemp / 100.0;

						textAnswer.setText(Double.toString(dtemp));

					}

					else {

						//如果是int数但能被100整除，则去掉末尾两个零

						if (Integer.parseInt(textAnswer.getText()) % 100 == 0) {

							int itemp = Integer.parseInt(textAnswer.getText());

							itemp /= 100;

							textAnswer.setText(Integer.toString(itemp));

						}

						//如果是int数，但不能被100整除，则按double数处理

						else {

							double dtemp = Double.parseDouble(textAnswer.getText());

							dtemp = dtemp / 100.0;

							textAnswer.setText(Double.toString(dtemp));

						}

					}

				}

			}

			//开根号运算

			if (temp == buttonSqrt && clickable == true) {

				String s = textAnswer.getText();

				if (s.charAt(0) == '-') {

					textAnswer.setText("负数不能开根号");

					clickable = false;

				}

				else

					textAnswer.setText(Double.toString(java.lang.Math.sqrt(Double.

					parseDouble(textAnswer.getText()))));

			}

			//倒数运算

			if (temp == buttonDao && clickable == true) {

				if (textAnswer.getText().charAt(0) == '0' &&

				textAnswer.getText().length() == 1) {

					textAnswer.setText("零不能求倒数");

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

			//按下'+/-'按钮时处理

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

					//如果当前字符串首字母有'-'号,代表现在是个负数,再按下时,则将首符号去掉

					if (s.charAt(0) == '-') {

						textAnswer.setText("");

						for (int i = 1; i < s.length(); i++) {

							char a = s.charAt(i);

							textAnswer.setText(textAnswer.getText() + a);

						}

					}

					//如果当前字符串第一个字符不是符号，则添加一个符号在首字母处

					else

						textAnswer.setText('-' + s);

				}

			}

			//计算器有关内存操作

			//'MC'的操作，将内存清0

			if (temp == buttonMC && clickable == true) {

				memoryd = memoryi = 0;

				textMemory.setText("");

			}

			//'MS'的操作，将当前文本框内容保存入内存，显示'M'

			if (temp == buttonMS && clickable == true) {

				boolean isDot = false;

				textMemory.setText("   M");

				for (int i = 0; i < textAnswer.getText().length(); i++)

					if ('.' == textAnswer.getText().charAt(i)) {

						isDot = true;

						break;

					}

				//如果是double,则存入memoryd(double存储器)

				if (isDot == true) {

					memoryd = Double.parseDouble(textAnswer.getText());

					memoryi = 0; //保证存储器中存放最新的值

				}

				//如果是int,则存入memoryi(int存储器)

				else {

					memoryi = Integer.parseInt(textAnswer.getText());

					memoryd = 0; //保证存储器中存放最新的值

				}

			}

			//'MR'的操作，将存储器中的信息输出

			if (temp == buttonMR && clickable == true) {

				if (memoryd != 0)

					textAnswer.setText(Double.toString(memoryd));

				if (memoryi != 0)

					textAnswer.setText(Integer.toString(memoryi));

			}

			//'M+'的功能，将当前文本框里的数据和存储器中数据相加后，再存入存储器

			if (temp == buttonMAdd && clickable == true) {

				boolean isDot = false;

				for (int i = 0; i < textAnswer.getText().length(); i++)

					if ('.' == textAnswer.getText().charAt(i)) {

						isDot = true;

						break;

					}

				if (memoryi != 0) { //存储中是一个int型数

					if (isDot == false) //被加数是一个int型数

						memoryi += Integer.parseInt(textAnswer.getText());

					else { //被加数是一个double型数，则将int存储器中数传入double存储器与当前数相加，int存储器清零

						memoryd = memoryi + Double.parseDouble(textAnswer.getText());

						memoryi = 0;

					}

				}

				else

					memoryd += Double.parseDouble(textAnswer.getText());

			}

			//按下'Backspace'键，利用循环将当前字符串中的最后一个字母删除

			if (temp == buttonBk && clickable == true) {

				String s = textAnswer.getText();

				textAnswer.setText("");

				for (int i = 0; i < s.length() - 1; i++) {

					char a = s.charAt(i);

					textAnswer.setText(textAnswer.getText() + a);

				}

			}

			//按下'CE'按钮，将当前文本框内数据清除

			if (temp == buttonCe) {

				textAnswer.setText("");

				clickable = true;

			}

			//按下'C'按钮，文本框内数据清除，同时var,answer清0

			if (temp == buttonC) {

				vard = answerd = 0;

				textAnswer.setText("");

				clickable = true;

			}
			//	    按下'复制'菜单栏

			if (temp == copyItem) {

				copy = textAnswer.getText();

			}
			//按下'粘贴'菜单栏

			if (temp == pasteItem) {

				textAnswer.setText(copy);

			}
			if (temp == sItem) {

				JOptionPane.showMessageDialog(panel, "当前是标准型计算器,\n科学型计算器有待更新.");

			}

			//按下'帮助主题'菜单栏

			if (temp == topHelp) {
				JOptionPane.showMessageDialog(panel, scrollHelp);

			}
			//按下'数字分组'菜单栏

			if (temp == numberGroup) {

				if (numberGroup.getText().compareTo("   数字分组(I)") == 0)

					numberGroup.setText("√数字分组(I)");

				else

					numberGroup.setText("   数字分组(I)");

			}
			//按下'关于'菜单栏

			if (temp == aboutCal) {

				JOptionPane.showMessageDialog(panel, "计算器1.00版\n开发者：楼竞");

			}

		} catch (Exception e) {//输入中如果有操作非法，比如按下两次'+'，捕获异常
			textAnswer.setText("操作非法!");
			clickable = false;

		}

	}

	//主函数

	public static void main(String args[]) {

		new Calculator();

	}

}
/**************************************************************************
 * $RCSfile: Calculator.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: Calculator.java,v $
 * Revision 1.5  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
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
