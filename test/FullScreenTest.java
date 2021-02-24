import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FullScreenTest
 * zzl
 *
 * @author Dragon
 * @date 2020/12/14
 */
public class FullScreenTest {
    public static void  main(String[]   args)
    {
        final   JFrame   f   =   new   JFrame("FullScreenTest");
        final   JButton   btn   =   new   JButton("FullScreen");
        btn.addActionListener(new   ActionListener()
        {
            public   void   actionPerformed(ActionEvent e)
            {
                if   (btn.getText().equals("FullScreen"))   {
                    f.dispose();
                    f.setUndecorated(true);
                    f.getGraphicsConfiguration().getDevice().setFullScreenWindow(f);
                    f.setVisible(true);
                    btn.setText("NormalMode");
                }
                else   {
                    f.dispose();
                    f.setUndecorated(false);
                    f.getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
                    f.setVisible(true);
                    btn.setText("FullScreen");
                }
            }
        });

        f.getContentPane().setLayout(new FlowLayout());
        f.getContentPane().add(btn);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
