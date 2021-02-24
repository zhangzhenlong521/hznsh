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
 * Sys模块没有一个UIUtil,元数据,工作流,报表,Comm都有!它也应该有一个!!!
 * @author Administrator
 *
 */
public class SysUIUtil implements Serializable {

	public boolean isCanDoAsSuperAdmin(java.awt.Container _parent, String _queryTableName, String _savedTableName) {
		return isCanDoAsSuperAdmin(_parent, _queryTableName, _savedTableName, false); //
	}

	/**
	 * 判断我是否可以作为一个超级管理员对某个数据对象进行处理??
	 * 比如想作为【制度超级管理员】查看所有制度!!!!或者在【编辑】按钮灰掉的情况下,仍然可以操作之!!! 
	 * 或者查看工作流中相关加密意见!!!
	 * @param _parent
	 * @param _queryTableName
	 * @param _savedTableName
	 * @param _isQuite 是否是安静模式!
	 * @return
	 */
	public boolean isCanDoAsSuperAdmin(java.awt.Container _parent, String _queryTableName, String _savedTableName, boolean _isQuite) {
		try {
			String str_key = "$本人作为超级管理员有权控制的数据对象"; //
			if (ClientEnvironment.getInstance().containsKey(str_key)) { //
				HashSet hst = (HashSet) ClientEnvironment.getInstance().get(str_key); //
				if ((_queryTableName != null && hst.contains(_queryTableName.trim().toUpperCase())) || (_savedTableName != null && hst.contains(_savedTableName.trim().toUpperCase()))) { //
					return true;
				}
			}

			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //登录人员Id
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			HashMap rtMap = service.isCanDoAsSuperAdmin(str_loginUserId, _queryTableName, _savedTableName); //远程计算!复杂的逻辑计算在服务器端!!
			String str_rtType = (String) rtMap.get("ReturnType"); //
			if (str_rtType.equals("1") || str_rtType.equals("2")) {
				if (!_isQuite) {
					MessageBox.show(_parent, (String) rtMap.get("ReturnMsg")); //
				}
				return false; //
			} else if (str_rtType.equals("3")) { //因为本人直接与某个超级管理员关联了,所以直接返回true
				//需要返回我所有关联的超级管理员清单!然后全部插入缓存!!
				//ClientEnvironment.getInstance().put("$超级管理员不再提示输入密码-" + hvsSPRoles[_index].getStringValue("code"), "Y"); //加入缓存!!!
				addCache(_queryTableName, _savedTableName); //缓存注册
				return true;
			} else if (str_rtType.equals("4")) { //需要弹出【超级管理员密码输入框】,如果输对了,才返回True
				if (_isQuite) { //如果是安静模式,则直接返回false！！ key是固定的,值是所有输入过的【超级管理员】,然后有我,则自动解密!!否
					return false;
				} else {
					HashVO[] hvs = (HashVO[]) rtMap.get("AllSuperRoleVOs"); //
					InputSpuerAdminPwdDialog dialog = new InputSpuerAdminPwdDialog(_parent, hvs); //
					if (dialog.getCloseType() == 1) { //如果能成功返回的,则必须表示密码对了!
						addCache(_queryTableName, _savedTableName); // 缓存注册
						return true; //
					} else { //密码不对,则返回false
						return false;
					}
				}
			} else { //这种情况理论上应该根本不会存在!
				MessageBox.show(_parent, "根据[" + _queryTableName + "/" + _savedTableName + "]去服务器进行isCanDoAsSuperAdmin()计算时,返回了未知类型[" + str_rtType + "]"); //
				return false; //
			}
		} catch (Exception _ex) {
			MessageBox.showException(_parent, _ex); //
			return false; //
		}
	}

	private void addCache(String _queryTableName, String _savedTableName) {
		String str_key = "$本人作为超级管理员有权控制的数据对象"; //
		if (!ClientEnvironment.getInstance().containsKey(str_key)) { //
			HashSet hst = new HashSet(); //
			if (_queryTableName != null) {
				hst.add(_queryTableName.trim().toUpperCase()); //
			}
			if (_savedTableName != null) {
				hst.add(_savedTableName.trim().toUpperCase()); //
			}
			ClientEnvironment.getInstance().put(str_key, hst); //塞入缓存!
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
	 * 导出数据库中所有表的数据字典，反向生成tables.xml【李春娟/2012-08-27】
	 * @param _parent
	 */
	public void exportAllTables(java.awt.Container _parent) {
		final String str_path = getChooseDir(_parent); //弹出文件选择框!选择一个路径!!!
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
	 * 导出数据中所有表的数据字典时选择保存路径的方法【李春娟/2012-08-27】
	 * @param _parent
	 * @return
	 */
	private String getChooseDir(java.awt.Container _parent) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ClientEnvironment.str_downLoadFileDir));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("请选择目录"); //
		int li_rewult = 0;
		li_rewult = chooser.showSaveDialog(_parent);
		if (li_rewult == JFileChooser.CANCEL_OPTION) {
			return null;// 点击取消什么也不做
		}
		if (chooser.getSelectedFile().isFile()) {
			MessageBox.show(_parent, "请选择导出目录!"); //
			return null;
		}
		String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
		if (str_pathdir.endsWith("\\")) {
			str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
		}
		return str_pathdir; //
	}

	/**
	 * 导出数据库中所有表的数据字典，反向生成tables.xml的真正导出逻辑【李春娟/2012-08-27】
	 * @param _splash
	 * @param _path
	 */
	private void export(SplashWindow _splash, String _path) {
		try {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			String tables = service.exportAllTables();
			TBUtil tbutil = new TBUtil();
			FileOutputStream fileOut = new FileOutputStream(_path + "\\tables.xml", false); //
			tbutil.writeStrToOutputStream(fileOut, tables); //输出至文件
			MessageBox.show(_splash, "导出成功!");
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.show(_splash, "导出失败!");
		}
	}
}
