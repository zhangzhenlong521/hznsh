package cn.com.infostrategy.ui.sysapp.myfavorite;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 收藏夹按钮 【杨科/2012-09-04】
 */

public class JoinMyFavoriteBtn implements ActionListener{
	private BillListPanel billList = null;
	private JTree favoriteTree = null; 
	private WLTButton btn_favorite,btn_exit; 
	private BillDialog dialog = null;
	
    public JoinMyFavoriteBtn(BillListPanel billList){
        this.billList = billList;
		
		favoriteTree = new MyFavoriteWKPanel().getTree();
		favoriteTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {

			}
		});
		
		JPanel treePanel = new JPanel(); 
		treePanel.setLayout(new BorderLayout()); 
		treePanel.add(new JScrollPane(favoriteTree)); 
		
		btn_favorite = new WLTButton("加入收藏"); 
		btn_exit = new WLTButton("返回"); 
		
		btn_favorite.addActionListener(this); 
		btn_exit.addActionListener(this); 
		
        WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
		btnPanel.add(btn_favorite);
		btnPanel.add(btn_exit);
		
		dialog = new BillDialog(billList, "加入收藏", 230, 350);
		dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(treePanel, "Center");
        dialog.getContentPane().add(btnPanel, "South");
        dialog.setVisible(true);
    }

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == btn_favorite) {
				DefaultMutableTreeNode treeNode = getSelectedNode();
				 
				if(treeNode==null||treeNode.isRoot()){
					MessageBox.show("请选择要加入的收藏目录!");
					return;
				}
				
				HashVO hvo = (HashVO) treeNode.getUserObject(); 
				String id = hvo.getStringValue("id");
				String text = hvo.getStringValue("text");
				
				String templetcode = billList.templetVO.getTempletcode();
				String tablename = billList.templetVO.getTablename();
				String pkey = billList.templetVO.getPkname();
				String pvalue = billList.getSelectedBillVO().getPkValue();
				String tostr = billList.getSelectedBillVO().toString();
				
				String newid = UIUtil.getSequenceNextValByDS(null, "s_pub_myfavorites");
				UIUtil.executeUpdateByDS(null, new InsertSQLBuilder("pub_myfavorites", new String[][] { { "id", newid }, 
						{ "dirid", id }, { "dirname", text }, { "tostr", tostr }, { "templetcode", templetcode }, { "tablename", tablename }, 
						{ "pkey", pkey }, { "pvalue", pvalue }, { "creattime", UIUtil.getCurrTime() } }).getSQL());
				
				MessageBox.show("加入成功!");
				dialog.dispose();
			}else if (e.getSource() == btn_exit) {
				dialog.dispose();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public DefaultMutableTreeNode getSelectedNode() {
		TreePath path = favoriteTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}

}
