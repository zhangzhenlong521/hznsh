package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.styletemplet.t03.AbstractStyleWorkPanel_03;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * 机构维护,使用风格模板3实现..
 * @author xch
 *
 */
public class CorpDeptManagerWFPanel extends AbstractStyleWorkPanel_03 {

	private static final long serialVersionUID = -6909843946321417628L;

	private JMenuItem menuItem_all; //
	private JMenuItem menuItem_7; //
	private JMenuItem menuItem_export;

	private ActionListener al = null; //

	private boolean isFilter = false; //是否权限过滤

	public CorpDeptManagerWFPanel() {
	}

	public CorpDeptManagerWFPanel(String _isfilter) {
		isFilter = Boolean.parseBoolean(_isfilter); //
	}

	public String getTempletcode() {
		HashMap confmap = this.getMenuConfMap();//菜单参数
		//以前的机构过滤无法满足分支行系统管理员管理自己本分支行的机构和人员，总行可管理所有机构和人员的需求，故为了扩展，可在配置菜单参数，在机构模板中设置机构查询策略即可【李春娟/2016-03-18】
		if (confmap.get("机构模板") != null) {
			return (String) confmap.get("机构模板");
		}

		return "PUB_CORP_DEPT_CODE1"; //
	}

	@Override
	public void afterInitialize() {
		if (isFilter) {
			String str_loginUserBLDeptCondition = UIUtil.getLoginUserBLDeptCondition(); //
			if (str_loginUserBLDeptCondition != null) {
				getBillTreePanel().setCustCondition(str_loginUserBLDeptCondition); //
			}
		}

		menuItem_7 = new JMenuItem("快速设置机构类型", UIUtil.getImage("office_131.gif")); //
		al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked(e.getSource()); //				
			}
		};

		menuItem_7.addActionListener(al); //

		this.getBillTreePanel().addAppMenuItems(new JMenuItem[] { new JMenuItem(""), menuItem_7 });
		this.getBillTreePanel().addBillTreeMovedListener(this); //
		
		//机构导出
		menuItem_export = new JMenuItem("导出", UIUtil.getImage("zt_068.gif")); //
		al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExport(e.getSource()); //				
			}
		};

		menuItem_export.addActionListener(al); //

		this.getBillTreePanel().addAppMenuItems(new JMenuItem[] { new JMenuItem(""), menuItem_export });
		this.getBillTreePanel().addBillTreeMovedListener(this); 
	}

	/**
	 * 点击
	 * @param _obj
	 */
	private void onClicked(Object _obj) {
		if (_obj == menuItem_7) {
			try {
				BillVO[] billVOs = getBillTreePanel().getSelectedVOs(); //
				if (billVOs == null || billVOs.length == 0) {
					MessageBox.show(this, "请选择一个机构!"); //
					return;
				}
				String str_oldtype = billVOs[0].getStringValue("corptype"); //
				CardCPanel_ComboBox comBox = new CardCPanel_ComboBox("corptype", "机构类型", "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型') order by seq"); //
				comBox.setOpaque(false); //透明
				if (str_oldtype != null && !str_oldtype.trim().equals("")) { //
					comBox.setValue(str_oldtype); //
				}
				if (JOptionPane.showConfirmDialog(this, comBox, "您确定要设置机构类型?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					String str_corptype = comBox.getValue(); //机构类型....
					if (str_corptype == null || str_corptype.trim().equals("")) {
						MessageBox.show(this, "机构类型不能为空!"); //
						return;
					}

					ArrayList al_sqls = new ArrayList(); //
					for (int i = 0; i < billVOs.length; i++) {
						al_sqls.add("update pub_corp_dept set corptype='" + str_corptype + "' where id='" + billVOs[i].getStringValue("id") + "'"); //设置机构类型...
					}
					UIUtil.executeBatchByDS(null, al_sqls); //提交后台直接处理数据库
					for (int i = 0; i < billVOs.length; i++) { //为了保证前后数据一致,必须修改前台数据,这上最痛苦也是最容易遗忘的,一般有两种：一种是重新查询一下，但性能低；一种是手工再重置前台，但费劲！
						billVOs[i].setObject("corptype", new StringItemVO(str_corptype)); //
					}
					getBillTreePanel().setBillVOs(billVOs); //回写控件，一定不能遗忘，否则会造成前后台数据不一致，下次再取BillVO时，是旧的，脏的！！
					reloaCorpCacheData(); //重新加载服务器机构缓存
					MessageBox.show(this, "将[" + al_sqls.size() + "]个机构的类型更新为[" + str_corptype + "]成功!!"); //
				}
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		}

	}
	
	/**
	 * 导出..
	 */
	private void onExport(Object _obj) {
		if (_obj != menuItem_export) {
			return;
		}
			
		DefaultMutableTreeNode[] allNodes = getBillTreePanel().getSelectedNodes(); //
		if (allNodes == null) {
			MessageBox.show(this, "请至少选择一个节点");
			return;
		}
		int li_maxlevel = 0; //

		Vector v_nodes = new Vector(); //
		for (int i = 0; i < allNodes.length; i++) { //选择的所有结点..
			DefaultMutableTreeNode[] childNodes = getBillTreePanel().getOneNodeChildPathNodes(allNodes[i]); //
			for (int j = 0; j < childNodes.length; j++) {
				if (childNodes[j].getLevel() > li_maxlevel) { //
					li_maxlevel = childNodes[j].getLevel(); //
				}
				v_nodes.add(childNodes[j]); //
			}
		}

		String[][] str_data = new String[v_nodes.size()][li_maxlevel]; //
		int li_leafcount = 1; //
		int li_dircount_1 = 1; //
		int li_dircount_2 = 1; //
		for (int i = 0; i < v_nodes.size(); i++) { //
			DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode) v_nodes.get(i); //
			int li_level = tmp_node.getLevel(); //
			if (tmp_node.isLeaf()) { //如果是叶子结点..
				str_data[i][li_level - 1] = "<font color=\"blue\">" + tmp_node + "</font>"; //
				li_leafcount++;
			} else {
				if (li_level == 0) {
					str_data[i][li_level] = "" + tmp_node; //如果模板中配置显示根节点，此时选中根节点导出时，li_level=0，需要判断一下，否则会报数组越界异常，李春娟2012-02-22修改
				} else if (li_level == 1) {
					str_data[i][li_level - 1] = tmp_node.toString();
					li_dircount_1++; //
				} else if (li_level == 2) {
					str_data[i][li_level - 1] = tmp_node.toString();
					li_dircount_2++;
				} else {
					str_data[i][li_level - 1] = tmp_node.toString();
				}
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					String filename = file.getName();
					return filename.endsWith(".html");
				}
			}

			public String getDescription() {
				return "*.html";
			}
		});
		try {
			File file = new File(new File("C:/corpdept.html").getCanonicalPath());
			chooser.setSelectedFile(file);
			int li_rewult = chooser.showSaveDialog(getBillTreePanel());
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					new TBUtil().writeStringArrayToHtmlTableFile(str_data, curFile.getPath()); // 以前是直接保存到c盘根目录，但win7 的c盘下不能写文件，导致导出失败，所以改为选择路径保存
					//MessageBox.show(this, "导出" + curFile.getPath() + "成功!!!"); //
					String str_filename = curFile.getAbsolutePath(); //
					if (JOptionPane.showConfirmDialog(this, "导出数据成功!!文件路径是[" + str_filename + "],你是否想立即打开该文件?", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
						Runtime.getRuntime().exec("explorer.exe \"" + str_filename + "\""); //
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onInsert() {
		super.onInsert();
		reloaCorpCacheData(); //重新加载服务器机构缓存
	}

	@Override
	protected void onEdit() throws Exception {
		super.onEdit();
		reloaCorpCacheData(); //重新加载服务器机构缓存
	}

	@Override
	public void onDelete() {
		super.onDelete();
		reloaCorpCacheData(); //重新加载服务器机构缓存
	}

	@Override
	public void onRefresh() {
		super.onRefresh();
		reloaCorpCacheData();
	}

	private void reloaCorpCacheData() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			service.registeCorpCacheData(); //重新注册一下机构缓存数据!!
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
