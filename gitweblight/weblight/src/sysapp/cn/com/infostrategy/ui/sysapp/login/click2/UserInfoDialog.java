package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Button;

/**
 * 个人信息修改 【杨科/2012-08-29】
 */

public class UserInfoDialog extends BillDialog {

	private BillCardPanel cardPanel;
	private JButton save_btn;

	public UserInfoDialog(Container _parent) {
		super(_parent, "个人信息", 600, 330);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		String userInfPanel = TBUtil.getTBUtil().getSysOptionStringValue("自定义个人信息面板", null);
		if (userInfPanel != null && !"".equals(userInfPanel)) {//sunfujun/20121030/有时候项目中需要自己定义一下个人信息的界面
			try {
				this.getContentPane().add(getSelfDescPanel(userInfPanel), BorderLayout.CENTER);
			} catch (Exception e) {
				e.printStackTrace();
				this.getContentPane().add(new JPanel().add(new JLabel(e.getMessage())), BorderLayout.CENTER);
			}
		} else {
			String id = ClientEnvironment.getInstance().getLoginUserID();
			String deptName = ClientEnvironment.getInstance().getLoginUserCorpName();
			String bm = ClientEnvironment.getInstance().getLoginUserDeptName();

			cardPanel = new BillCardPanel("PUB_USER_MYINFO");
			cardPanel.setEditableByEditInit();
			cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			cardPanel.insertRow();
			cardPanel.queryDataByCondition(" id='" + id + "'");
			cardPanel.setValueAt("PK_DEPT", new StringItemVO(deptName));
			cardPanel.setValueAt("DEPTALLPATH", new StringItemVO(bm));

			save_btn = ((CardCPanel_Button) cardPanel.getCompentByKey("save_btn")).getButtontn();
			save_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						BillVO vo = cardPanel.getBillVO();
						UpdateSQLBuilder _sql = new UpdateSQLBuilder("pub_user");
						_sql.putFieldValue("STAFF", vo.getStringValue("STAFF"));
						_sql.putFieldValue("POST", vo.getStringValue("POST"));
						_sql.putFieldValue("SEX", vo.getStringValue("SEX"));
						_sql.putFieldValue("BIRTHYEAR", vo.getStringValue("BIRTHYEAR"));
						_sql.putFieldValue("DEGREEN", vo.getStringValue("DEGREEN"));
						_sql.putFieldValue("SPECIALTY", vo.getStringValue("SPECIALTY"));
						_sql.putFieldValue("TELEPHONE", vo.getStringValue("TELEPHONE"));
						_sql.putFieldValue("MOBILE", vo.getStringValue("MOBILE"));
						_sql.setWhereCondition(" id='" + vo.getStringValue("id") + "'");
						UIUtil.executeUpdateByDS(null, _sql.getSQL());
						MessageBox.show(cardPanel, "修改成功");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			this.getContentPane().add(cardPanel, BorderLayout.CENTER);
		}
	}

	public static AbstractWorkPanel getSelfDescPanel(String panelname) throws Exception {
		AbstractWorkPanel panelins = (AbstractWorkPanel) Class.forName(panelname).newInstance(); 
		panelins.initialize();
		return panelins;
	}

	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		BillDialog dialog = new UserInfoDialog(_parent);
		dialog.setVisible(true); 
	}

}
