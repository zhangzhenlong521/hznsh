package cn.com.pushworld.wn;



import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main extends JFrame {
    private JPanel pan = new JPanel();

    private JLabel website = new JLabel();
    String str="牛逼";
    String url="http://www.baidu.cn/";
    public Main() {
        this.setSize(300, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String str="牛逼";
        String url="";
        website.setText("<html> "+str+":<a href=''>http://www.baidu.cn/</a></html>");
        website.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pan.add(website);
        this.setContentPane(pan);
        this.setVisible(true);
        goWebsite(website);
    }

    public static void main(String args[]) {
//        new Main().setVisible(true);
        String str="贵州省赫章县白果镇双山村冷水沟组";
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+str.substring(str.indexOf("镇")+1,str.indexOf("村")+1));
    }

    private void goWebsite(JLabel website) {
        website.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException ex) {
//                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (URISyntaxException ex) {

                }
            }
        });
    }
}

