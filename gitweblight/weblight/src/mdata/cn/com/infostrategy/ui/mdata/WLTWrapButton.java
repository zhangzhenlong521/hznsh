package cn.com.infostrategy.ui.mdata;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * 将文字坚起来显示的按钮,在流程图中很有用
 * @author xch
 *
 */
public class WLTWrapButton extends JButton {

	private static final long serialVersionUID = 2234508244563151756L;

	public WLTWrapButton(String label) {
		super(label);
		setContentAreaFilled(false);
		this.setFont(new Font("System", 0, 12)); //
		this.setOpaque(true); //
		this.setPreferredSize(new java.awt.Dimension(28, 5 + this.getText().length() * 20)); //
		this.setMargin(new Insets(0, 0, 0, 0)); //
		//this.setBackground(new Color(240,240,240));
		this.setBorder(BorderFactory.createRaisedBevelBorder()); //
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g); //
		g.clearRect(0, 0, getWidth(), getHeight() + 60); //
		g.setColor(new Color(210,210,210));
		g.fillRect(0, 0, getWidth(), getHeight() + 60); //
		String str_text = this.getText(); //
		char[] cs = str_text.toCharArray();
		g.setColor(Color.black);
		for (int i = 0; i < cs.length; i++) {
			g.drawChars(cs, i, 1, 8, 20 + (i * 15)); //
		}
	}

	public boolean contains(int x, int y) {
		return true;
	}

	public static void main(String[] args) {
		JButton button = new WLTWrapButton("流程属性");
		JFrame frame = new JFrame();
		frame.setSize(150, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(button);
		frame.setVisible(true);
	}
}