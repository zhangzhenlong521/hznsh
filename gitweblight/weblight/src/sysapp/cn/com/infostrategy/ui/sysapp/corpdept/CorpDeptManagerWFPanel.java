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
 * ����ά��,ʹ�÷��ģ��3ʵ��..
 * @author xch
 *
 */
public class CorpDeptManagerWFPanel extends AbstractStyleWorkPanel_03 {

	private static final long serialVersionUID = -6909843946321417628L;

	private JMenuItem menuItem_all; //
	private JMenuItem menuItem_7; //
	private JMenuItem menuItem_export;

	private ActionListener al = null; //

	private boolean isFilter = false; //�Ƿ�Ȩ�޹���

	public CorpDeptManagerWFPanel() {
	}

	public CorpDeptManagerWFPanel(String _isfilter) {
		isFilter = Boolean.parseBoolean(_isfilter); //
	}

	public String getTempletcode() {
		HashMap confmap = this.getMenuConfMap();//�˵�����
		//��ǰ�Ļ��������޷������֧��ϵͳ����Ա�����Լ�����֧�еĻ�������Ա�����пɹ������л�������Ա�����󣬹�Ϊ����չ���������ò˵��������ڻ���ģ�������û�����ѯ���Լ��ɡ����/2016-03-18��
		if (confmap.get("����ģ��") != null) {
			return (String) confmap.get("����ģ��");
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

		menuItem_7 = new JMenuItem("�������û�������", UIUtil.getImage("office_131.gif")); //
		al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked(e.getSource()); //				
			}
		};

		menuItem_7.addActionListener(al); //

		this.getBillTreePanel().addAppMenuItems(new JMenuItem[] { new JMenuItem(""), menuItem_7 });
		this.getBillTreePanel().addBillTreeMovedListener(this); //
		
		//��������
		menuItem_export = new JMenuItem("����", UIUtil.getImage("zt_068.gif")); //
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
	 * ���
	 * @param _obj
	 */
	private void onClicked(Object _obj) {
		if (_obj == menuItem_7) {
			try {
				BillVO[] billVOs = getBillTreePanel().getSelectedVOs(); //
				if (billVOs == null || billVOs.length == 0) {
					MessageBox.show(this, "��ѡ��һ������!"); //
					return;
				}
				String str_oldtype = billVOs[0].getStringValue("corptype"); //
				CardCPanel_ComboBox comBox = new CardCPanel_ComboBox("corptype", "��������", "select id,code,name from pub_comboboxdict where type in ('��������','��������') order by seq"); //
				comBox.setOpaque(false); //͸��
				if (str_oldtype != null && !str_oldtype.trim().equals("")) { //
					comBox.setValue(str_oldtype); //
				}
				if (JOptionPane.showConfirmDialog(this, comBox, "��ȷ��Ҫ���û�������?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					String str_corptype = comBox.getValue(); //��������....
					if (str_corptype == null || str_corptype.trim().equals("")) {
						MessageBox.show(this, "�������Ͳ���Ϊ��!"); //
						return;
					}

					ArrayList al_sqls = new ArrayList(); //
					for (int i = 0; i < billVOs.length; i++) {
						al_sqls.add("update pub_corp_dept set corptype='" + str_corptype + "' where id='" + billVOs[i].getStringValue("id") + "'"); //���û�������...
					}
					UIUtil.executeBatchByDS(null, al_sqls); //�ύ��ֱ̨�Ӵ������ݿ�
					for (int i = 0; i < billVOs.length; i++) { //Ϊ�˱�֤ǰ������һ��,�����޸�ǰ̨����,������ʹ��Ҳ��������������,һ�������֣�һ�������²�ѯһ�£������ܵͣ�һ�����ֹ�������ǰ̨�����Ѿ���
						billVOs[i].setObject("corptype", new StringItemVO(str_corptype)); //
					}
					getBillTreePanel().setBillVOs(billVOs); //��д�ؼ���һ��������������������ǰ��̨���ݲ�һ�£��´���ȡBillVOʱ���Ǿɵģ���ģ���
					reloaCorpCacheData(); //���¼��ط�������������
					MessageBox.show(this, "��[" + al_sqls.size() + "]�����������͸���Ϊ[" + str_corptype + "]�ɹ�!!"); //
				}
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		}

	}
	
	/**
	 * ����..
	 */
	private void onExport(Object _obj) {
		if (_obj != menuItem_export) {
			return;
		}
			
		DefaultMutableTreeNode[] allNodes = getBillTreePanel().getSelectedNodes(); //
		if (allNodes == null) {
			MessageBox.show(this, "������ѡ��һ���ڵ�");
			return;
		}
		int li_maxlevel = 0; //

		Vector v_nodes = new Vector(); //
		for (int i = 0; i < allNodes.length; i++) { //ѡ������н��..
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
			if (tmp_node.isLeaf()) { //�����Ҷ�ӽ��..
				str_data[i][li_level - 1] = "<font color=\"blue\">" + tmp_node + "</font>"; //
				li_leafcount++;
			} else {
				if (li_level == 0) {
					str_data[i][li_level] = "" + tmp_node; //���ģ����������ʾ���ڵ㣬��ʱѡ�и��ڵ㵼��ʱ��li_level=0����Ҫ�ж�һ�£�����ᱨ����Խ���쳣�����2012-02-22�޸�
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
					new TBUtil().writeStringArrayToHtmlTableFile(str_data, curFile.getPath()); // ��ǰ��ֱ�ӱ��浽c�̸�Ŀ¼����win7 ��c���²���д�ļ������µ���ʧ�ܣ����Ը�Ϊѡ��·������
					//MessageBox.show(this, "����" + curFile.getPath() + "�ɹ�!!!"); //
					String str_filename = curFile.getAbsolutePath(); //
					if (JOptionPane.showConfirmDialog(this, "�������ݳɹ�!!�ļ�·����[" + str_filename + "],���Ƿ��������򿪸��ļ�?", "��ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
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
		reloaCorpCacheData(); //���¼��ط�������������
	}

	@Override
	protected void onEdit() throws Exception {
		super.onEdit();
		reloaCorpCacheData(); //���¼��ط�������������
	}

	@Override
	public void onDelete() {
		super.onDelete();
		reloaCorpCacheData(); //���¼��ط�������������
	}

	@Override
	public void onRefresh() {
		super.onRefresh();
		reloaCorpCacheData();
	}

	private void reloaCorpCacheData() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			service.registeCorpCacheData(); //����ע��һ�»�����������!!
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
