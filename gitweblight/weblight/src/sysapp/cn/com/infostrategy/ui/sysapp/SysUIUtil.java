package cn.com.infostrategy.ui.sysapp;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * Sysģ��û��һ��UIUtil,Ԫ����,������,����,Comm����!��ҲӦ����һ��!!!
 * @author Administrator
 *
 */
public class SysUIUtil implements Serializable {

	public boolean isCanDoAsSuperAdmin(java.awt.Container _parent, String _queryTableName, String _savedTableName) {
		return isCanDoAsSuperAdmin(_parent, _queryTableName, _savedTableName, false); //
	}

	/**
	 * �ж����Ƿ������Ϊһ����������Ա��ĳ�����ݶ�����д���??
	 * ��������Ϊ���ƶȳ�������Ա���鿴�����ƶ�!!!!�����ڡ��༭����ť�ҵ��������,��Ȼ���Բ���֮!!! 
	 * ���߲鿴����������ؼ������!!!
	 * @param _parent
	 * @param _queryTableName
	 * @param _savedTableName
	 * @param _isQuite �Ƿ��ǰ���ģʽ!
	 * @return
	 */
	public boolean isCanDoAsSuperAdmin(java.awt.Container _parent, String _queryTableName, String _savedTableName, boolean _isQuite) {
		try {
			String str_key = "$������Ϊ��������Ա��Ȩ���Ƶ����ݶ���"; //
			if (ClientEnvironment.getInstance().containsKey(str_key)) { //
				HashSet hst = (HashSet) ClientEnvironment.getInstance().get(str_key); //
				if ((_queryTableName != null && hst.contains(_queryTableName.trim().toUpperCase())) || (_savedTableName != null && hst.contains(_savedTableName.trim().toUpperCase()))) { //
					return true;
				}
			}

			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //��¼��ԱId
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			HashMap rtMap = service.isCanDoAsSuperAdmin(str_loginUserId, _queryTableName, _savedTableName); //Զ�̼���!���ӵ��߼������ڷ�������!!
			String str_rtType = (String) rtMap.get("ReturnType"); //
			if (str_rtType.equals("1") || str_rtType.equals("2")) {
				if (!_isQuite) {
					MessageBox.show(_parent, (String) rtMap.get("ReturnMsg")); //
				}
				return false; //
			} else if (str_rtType.equals("3")) { //��Ϊ����ֱ����ĳ����������Ա������,����ֱ�ӷ���true
				//��Ҫ���������й����ĳ�������Ա�嵥!Ȼ��ȫ�����뻺��!!
				//ClientEnvironment.getInstance().put("$��������Ա������ʾ��������-" + hvsSPRoles[_index].getStringValue("code"), "Y"); //���뻺��!!!
				addCache(_queryTableName, _savedTableName); //����ע��
				return true;
			} else if (str_rtType.equals("4")) { //��Ҫ��������������Ա���������,��������,�ŷ���True
				if (_isQuite) { //����ǰ���ģʽ,��ֱ�ӷ���false���� key�ǹ̶���,ֵ������������ġ���������Ա��,Ȼ������,���Զ�����!!��
					return false;
				} else {
					HashVO[] hvs = (HashVO[]) rtMap.get("AllSuperRoleVOs"); //
					InputSpuerAdminPwdDialog dialog = new InputSpuerAdminPwdDialog(_parent, hvs); //
					if (dialog.getCloseType() == 1) { //����ܳɹ����ص�,������ʾ�������!
						addCache(_queryTableName, _savedTableName); // ����ע��
						return true; //
					} else { //���벻��,�򷵻�false
						return false;
					}
				}
			} else { //�������������Ӧ�ø����������!
				MessageBox.show(_parent, "����[" + _queryTableName + "/" + _savedTableName + "]ȥ����������isCanDoAsSuperAdmin()����ʱ,������δ֪����[" + str_rtType + "]"); //
				return false; //
			}
		} catch (Exception _ex) {
			MessageBox.showException(_parent, _ex); //
			return false; //
		}
	}

	private void addCache(String _queryTableName, String _savedTableName) {
		String str_key = "$������Ϊ��������Ա��Ȩ���Ƶ����ݶ���"; //
		if (!ClientEnvironment.getInstance().containsKey(str_key)) { //
			HashSet hst = new HashSet(); //
			if (_queryTableName != null) {
				hst.add(_queryTableName.trim().toUpperCase()); //
			}
			if (_savedTableName != null) {
				hst.add(_savedTableName.trim().toUpperCase()); //
			}
			ClientEnvironment.getInstance().put(str_key, hst); //���뻺��!
		} else {
			HashSet hst = (HashSet) ClientEnvironment.getInstance().get(str_key); ////
			if (_queryTableName != null) {
				hst.add(_queryTableName.trim().toUpperCase()); //
			}
			if (_savedTableName != null) {
				hst.add(_savedTableName.trim().toUpperCase()); //
			}
		}
	}

	/**
	 * �������ݿ������б�������ֵ䣬��������tables.xml�����/2012-08-27��
	 * @param _parent
	 */
	public void exportAllTables(java.awt.Container _parent) {
		final String str_path = getChooseDir(_parent); //�����ļ�ѡ���!ѡ��һ��·��!!!
		if (str_path == null) {
			return;
		}
		new SplashWindow(_parent, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				export((SplashWindow) e.getSource(), str_path);
			}
		}, 600, 130, 300, 300); //
	}

	/**
	 * �������������б�������ֵ�ʱѡ�񱣴�·���ķ��������/2012-08-27��
	 * @param _parent
	 * @return
	 */
	private String getChooseDir(java.awt.Container _parent) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ClientEnvironment.str_downLoadFileDir));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("��ѡ��Ŀ¼"); //
		int li_rewult = 0;
		li_rewult = chooser.showSaveDialog(_parent);
		if (li_rewult == JFileChooser.CANCEL_OPTION) {
			return null;// ���ȡ��ʲôҲ����
		}
		if (chooser.getSelectedFile().isFile()) {
			MessageBox.show(_parent, "��ѡ�񵼳�Ŀ¼!"); //
			return null;
		}
		String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
		if (str_pathdir.endsWith("\\")) {
			str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
		}
		return str_pathdir; //
	}

	/**
	 * �������ݿ������б�������ֵ䣬��������tables.xml�����������߼������/2012-08-27��
	 * @param _splash
	 * @param _path
	 */
	private void export(SplashWindow _splash, String _path) {
		try {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			String tables = service.exportAllTables();
			TBUtil tbutil = new TBUtil();
			FileOutputStream fileOut = new FileOutputStream(_path + "\\tables.xml", false); //
			tbutil.writeStrToOutputStream(fileOut, tables); //������ļ�
			MessageBox.show(_splash, "�����ɹ�!");
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.show(_splash, "����ʧ��!");
		}
	}
}
