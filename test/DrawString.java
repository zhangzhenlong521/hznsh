
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;

import javax.swing.*;

/**
 *
 * @author zzl
 * Rectangle2D.Double 矩形
 * RoundRectangle2D.Double 圆形
 */
public class DrawString extends JFrame{

    private Shape rect;//矩形对象
    private Font font;//字体对象
    private Date date;//当前日期对象
    public DrawString(){
        rect=new RoundRectangle2D.Double(0,0,150,150,150,150);
        font=new Font("宋体",Font.BOLD,16);
        date=new Date();
        this.setSize(230,140);//设置窗体的大小
        //设置窗体的关闭方式
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(new CanvasPanel());//设置窗体面板为绘图面板对象
        this.setTitle("绘图文本");//设置窗体标题
    }
    class CanvasPanel extends JPanel{
        public void paint(Graphics g){
            super.paint(g);
            Graphics2D g2=(Graphics2D)g;//强制类型转换
            g2.setColor(Color.CYAN);//设置当前绘图颜色
            g2.fill(rect);//填充矩形
            g2.setColor(Color.blue);//设置当前绘图颜色
            g2.setFont(font);//设置字体
            g2.drawString("现在的时间是", 20, 30);//绘制文本
            g2.drawString(String.format("%tr", date), 50, 60);//绘制事件文本
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        DrawString ds=new DrawString();//
        ds.setVisible(true);
    }

}