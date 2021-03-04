package cn.com.infostrategy.ui.workflow.design;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 工作流标尺 Y轴 【杨科/2012-11-12】
 */

public class StaffYPanel extends JPanel {
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g; 
		for (int i = 0; i < 2000; i++) {
			if(i!=0&&i%100==0){
				g2.setColor(Color.BLACK);
				g2.drawLine(0, i, 5, i); 
				g2.drawString(i/10+"", 3, i+10);
/*				g2.rotate(90 * Math.PI / 180);
				g2.drawString(i/10+"", i, -3);
				g2.rotate(270 * Math.PI / 180);*/
			}else if(i/10==56){
				g2.setColor(Color.RED);
				g2.drawLine(0, i, 3, i); 
				g2.drawString("A4", 3, i+10);
			}else if(i/10==96){
				g2.setColor(Color.RED);
				g2.drawLine(0, i, 3, i); 
				g2.drawString("A4", 3, i+10);
			}else{
				g2.setColor(Color.BLACK);
				g2.drawLine(0, i, 3, i); 
				if(i==0){
					g2.drawString("0", 3, i+10);
				}
			}
			i+=9;
		}

	}

	public static void main(String[] _args) {
		JPanel staff = new StaffYPanel();
		
		JFrame frame = new JFrame("test"); 
		frame.setSize(800, 600); 
		frame.getContentPane().add(staff); 
		frame.setVisible(true); 
	}
}
