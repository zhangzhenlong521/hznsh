package cn.com.infostrategy.ui.sysapp.install;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.IAppletLoader;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.install.ModuleStatusWKPanel 
 * @Description: 安装、升级模块管理界面。
 * @author haoming
 * @date May 6, 2013 6:25:15 PM
 *  
*/
public class ModuleStatusWKPanel extends AbstractWorkPanel implements IAppletLoader, BillListHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JPanel modulePanel;
	private JPanel textPanel;
	private JTextArea textArea;
	BillListPanel listPanel = new BillListPanel(new TMO_PUB_MODULEINSTALL());
	private SysAppServiceIfc services;

	private WLTButton btn_history;

	private HashVO[] modules; //所有模块

	private HashVO installVO = null; //选择的安装内容

	//菜单调用实例化
	public void initialize() {
		mainPanel = this;
		init();
	}

	//客户端调用实例化。支持客户端Applet直接调用。/install
	public void loadApplet(JApplet _applet, JPanel panel) throws Exception {
		mainPanel = panel;
		init();
	}

	//初始化
	private void init() {
		if (mainPanel != null) {
			mainPanel.setLayout(new BorderLayout());
			JSplitPane splitPanel;
			splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getModuleStatePanel(), getShowTextPanel());
			splitPanel.setDividerLocation(500);
			mainPanel.add(splitPanel);
			loadModuleInfo();
		}

	}

	//得到各个模块的状态面板
	public JPanel getModuleStatePanel() {
		if (modulePanel == null) {
			modulePanel = new JPanel(new BorderLayout());
		}
		listPanel.addBillListHtmlHrefListener(this);//
		listPanel.putClientProperty("action", this);//缓存事件，在InstallScheduleTableEditor中用到。
		listPanel.setCanShowCardInfo(false);
		listPanel.getTable().addMouseListener(new MouseAdapter() {
		});
		listPanel.addBillListMouseDoubleClickedListener(new BillListMouseDoubleClickedListener() {
			public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
			}
		});
		btn_history = new WLTButton("查看安装历史"); //为了安全起见，最好把大部分数据des加密/解密 
		listPanel.addBatchBillListButton(new WLTButton[] { btn_history });
		listPanel.repaintBillListButton();
		modulePanel.add(listPanel, BorderLayout.CENTER);
		return modulePanel;
	}

	//得到操作的文本内容
	public JPanel getShowTextPanel() {
		if (textPanel == null) {
			textPanel = new JPanel(new BorderLayout());
			textArea = new JTextArea();
			textArea.setEditable(false);
			JScrollPane scrollPanel = new JScrollPane(textArea);
			textPanel.add(scrollPanel, BorderLayout.CENTER);
		}
		return textPanel;
	}

	//加载模块数据。
	//从服务器端获取可安装信息。
	private void loadModuleInfo() {
		listPanel.removeAllRows();

		new SwingWorker() {//swing线程工具
			//worker机制首先执行的逻辑
			protected Object doInBackground() throws Exception {
				modules = getService().getAllInstallModuleStatus();
				return modules;
			}

			//在doInBackground调用完毕后，会执行此方法。
			protected void done() {
				for (int i = 0; i < modules.length; i++) {
					int rowNum = listPanel.insertEmptyRow(listPanel.getRowCount());
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("id")), rowNum, "id");
					try {
						ImageIcon img = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImageFromServerRespath(modules[i].getStringValue("packagepath") + modules[i].getStringValue("icon")).getImage(), 80, 80));
						listPanel.setValueAt(img, rowNum, "icon");
					} catch (Exception ex) {
						System.out.println("图片加载失败");
					}
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("name")), rowNum, "name");
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("code")), rowNum, "code");
					List list = (List) modules[i].getAttributeValue("updates");
					String currValue = modules[i].getStringValue("version");
					if (currValue == null || "-1".equals(currValue)) {
						currValue = "未知";
					} else if (!currValue.contains("v") && !currValue.contains("V")) {
						currValue = "V" + currValue;
					}
					JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
						public String toString() {//表格的 ToolTipText调用
							return "";
						}
					};
					cellPanel.setOpaque(false);
					JLabel label = null;
					if (list != null && list.size() > 0) {
						String newVersion = (String) list.get(list.size() - 1);
						if (!newVersion.contains("v") && !newVersion.contains("V")) {
							newVersion = "V" + newVersion;
						}
						label = new WLTLabel("<html>当前版本：" + currValue + "<Br>可升级到：" + newVersion + "</html>");
					} else {
						label = new JLabel(currValue);
						label.setOpaque(false);
						label.setHorizontalAlignment(JLabel.CENTER);
					}
					cellPanel.add(label); //加入到面板上
					if (!WLTConstants.MODULE_INSTALL_STATUS_YAZ.equals(modules[i].getStringValue("control"))) { //如果不是 已经安装
						JButton btn = new WLTButton("查看", UIUtil.getImage("zoomnormal.gif"));
						btn.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								showSelectVersionInfo();
							}
						});
						btn.setForeground(new Color(0x037DC9));
						btn.setPreferredSize(new Dimension(60, 23));
						btn.setBorder(BorderFactory.createEmptyBorder());
						cellPanel.add(btn);
					}
					listPanel.setValueAt(cellPanel, rowNum, "version");
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("date")), rowNum, "date");
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("control")), rowNum, "control");
				}
			}
		}.execute();
	}

	//显示选择的版本信息。
	private void showSelectVersionInfo() {
		String selectID = listPanel.getSelectedBillVO().getStringValue("id");
		HashVO selectVO = null;
		for (int i = 0; i < modules.length; i++) {
			if (modules[i].getStringValue("id").equals(selectID)) {
				selectVO = modules[i];
				break;
			}
		}
		if (selectVO == null || selectVO.getStringValue("descr") == null) {
			MessageBox.showInfo(mainPanel, "暂无信息");
		} else {
			MessageBox.showInfo(mainPanel, selectVO.getStringValue("descr"));
		}

	}

	private SysAppServiceIfc getService() {
		if (services != null) {
			return services;
		}
		try {
			services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return services;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().contains("control")) {
			doClickInstallAction(_event);
		} else { //其他字段可能也有连接。
			_event.getBillListPanel().getSelectedBillVO();
		}
	}

	private void doClickInstallAction(BillListHtmlHrefEvent _event) {
		BillVO selectVO = listPanel.getSelectedBillVO();
		String str[] = TBUtil.getTBUtil().split(_event.getItemkey(), ";");
		if (str != null && str.length == 2) { //连接
			String buttonName = str[0];
			if ("等待安装".equals(buttonName)) {
				return;
			}
			if (!"配置".equals(buttonName)) {
				boolean flatInstalled = false; //平台是否已经安装，主要用于首次安装系统
				for (int i = 0; i < modules.length; i++) {
					if (modules[i].getStringValue("id").equals(selectVO.getStringValue("id"))) {
						installVO = modules[i];
					}
					if ("FLAT".equalsIgnoreCase(modules[i].getStringValue("code"))) {//如果是平台，检测当前平台状态。
						if (!WLTConstants.MODULE_INSTALL_STATUS_KAZ.equals(modules[i].getStringValue("control"))) {//如果是可安装，在安装其他模块是，必须先安装平台。
							flatInstalled = true;
						}
					}
				}
				try {
					if (WLTConstants.MODULE_INSTALL_STATUS_KSJ.equals(installVO.getStringValue("control"))) { //升级
						if (!MessageBox.confirm(mainPanel, "升级该模块功能前请务必备份数据库！是否继续？")) {
							return;
						}
						gotoInstallOrUpdate(installVO, "update");
					} else if (WLTConstants.MODULE_INSTALL_STATUS_KAZ.equals(installVO.getStringValue("control"))) {//安装，步骤如：弹出授权声明，必须点击同意继续。
						if (!"FLAT".equalsIgnoreCase(installVO.getStringValue("code")) && !flatInstalled) { //如果不是平台,需要判断平台是否已经安装过。
							if (MessageBox.confirm(mainPanel, "您还没有安装WebPush平台，是否立即安装？")) { //强行安装平台
								HashVO flatInstallVO = null;
								for (int i = 0; i < modules.length; i++) {
									if ("FLAT".equalsIgnoreCase(modules[i].getStringValue("code"))) {
										flatInstallVO = modules[i];
										break;
									}
								}
								if (flatInstallVO != null) {
									gotoInstallOrUpdate(flatInstallVO, "install");
								} else {
									MessageBox.showWarn(mainPanel, "不能检测到WebPush平台安装内容，请联系开发商！");
									return;
								}
							} else {
								return;
							}
						}
						String showText = "确定要安装此模块吗？";
						if (installVO != null) {
							if (installVO.containsKey("license")) {
								showText = installVO.getStringValue("license");
							}
						}
						if (MessageBox.confirm(mainPanel, showText)) {
							gotoInstallOrUpdate(installVO, "install");
						}
					} else if (WLTConstants.MODULE_INSTALL_STATUS_WSQ.equals(installVO.getStringValue("control"))) {
						String value = JOptionPane.showInputDialog(mainPanel, "请输入授权序列号：");
						if (value != null) {
							installVO.setAttributeValue("sn", value);
							gotoInstallOrUpdate(installVO, "validate");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				String code = selectVO.getStringValue("code");
				try {
					String configNum = UIUtil.getStringValueByDS(null, "select count(*) from pub_module_on_off where modulecode = '" + code + "'"); //看配置的内容数
					if (configNum != null && !"0".equals(configNum)) { //数据库有配置
						BillListDialog listDialog = new BillListDialog(mainPanel, selectVO.getStringValue("name") + "配置", "PUB_MODULE_ON_OFF_CODE1") {
							public void onConfirm() {
								billlistPanel.saveData();
								this.setCloseType(BillDialog.CONFIRM);
								this.dispose(); //
							}
						};
						BillListPanel listPanel = listDialog.getBilllistPanel();
						listPanel.setDataFilterCustCondition(" modulecode='" + code + "' ");
						listPanel.QueryDataByCondition(null);
						listPanel.addBillListMouseDoubleClickedListener(new BillListMouseDoubleClickedListener() {
							public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) { //不做任何处理
							}
						});
						listDialog.setTitle(listDialog.getTitle() + " (点击确定保存所有操作)");

						HashMap map = new HashMap();
						BillVO vos[] = listPanel.getAllBillVOs();
						for (int i = 0; i < vos.length; i++) {
							map.put(vos[i].getPkValue(), vos[i].getStringValue("mvalue"));
						}
						listDialog.setVisible(true);

						if (listDialog.getCloseType() == 1) {
							vos = listPanel.getAllBillVOs();
							List idslist = new ArrayList();
							for (int i = 0; i < vos.length; i++) {
								if (map.containsKey(vos[i].getPkValue()) && !map.get(vos[i].getPkValue()).toString().equals(vos[i].getStringValue("mvalue"))) {
									idslist.add(vos[i].getPkValue());
								}
							}
							if (idslist.size() > 0) {
								getService().refreshModuleOn_OffByIds(idslist);
							}
						}
					} else {
						MessageBox.show(mainPanel, "该模块没有可配置项");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String flag; //必须是全局变量，用来判断是否为空。

	//不能支持多任务并行安装哦。需要一个一个来。
	private void gotoInstallOrUpdate(HashVO vo, String _type) {
		textArea.append("\r\n*******************开始执行**********************\r\n");
		new Timer().schedule(new InstallTask(vo, _type), 0, 500);

		try {
			FrameWorkCommServiceIfc commServices = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			ClientEnvironment.getInstance().setClientSysOptions(commServices.reLoadDataFromDB(false)); //重置客户端pub_option系统缓存
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	//执行进度。
	class InstallTask extends TimerTask {
		private HashVO vo;
		private String _type;

		public InstallTask(HashVO _vo, String _type) {
			this.vo = _vo;
			this._type = _type;
		}

		public void run() {
			if (flag != null && !flag.equals("")) { //如果flag不为null，证明已经有系统在安装执行。需要等待。
				listPanel.setValueAt(new StringItemVO("等待安装"), vo.getIntegerValue("id"), "control");
				return;
			}
			flag = "";
			//负责刷新进度条，打印详细操作内容。
			new Timer().schedule(new TimerTask() {
				int currValue = 0;

				public void run() {
					try {
						List list = getService().getInstallOrUpdateSchedule();
						if (list == null || list.size() == 0) {
							return;
						}
						for (int i = 0; i < list.size(); i++) {
							Object[] msg = (Object[]) list.get(i);
							if ((Float) msg[1] != -1) {
								float f = (Float) msg[1];
								int per = (int) f;
								if (per > 0) {
									if (per - currValue > 10) { //如果前后差10以上，自动补全。
										for (int j = currValue; j < per; j++) {
											listPanel.setValueAt(j, vo.getIntegerValue("id"), "control");
											Thread.sleep(10);
										}
									}
									currValue = per;
									listPanel.setValueAt(per, vo.getIntegerValue("id"), "control");
								}
							}
							if (msg[0] != null) {
								textArea.append((String) msg[0] + "\r\n");
								int length = textArea.getDocument().getLength();
								textArea.setSelectionStart(length);
								textArea.setSelectionEnd(length);
							}
							Thread.sleep(10);
						}
						if (!flag.equals("")) {
							List list1 = getService().getInstallOrUpdateSchedule(); //执行完成前再去一次服务器端进度日志。
							if (list1 != null) {
								for (int i = 0; i < list1.size(); i++) {
									Object[] msg = (Object[]) list1.get(i);
									if ((Float) msg[1] != -1) {
										float f = (Float) msg[1];
										int per = (int) f;
										if (per > 0) {
											if (per - currValue > 10) {////如果前后差10以上，自动补全。
												for (int j = currValue; j < per; j++) {
													listPanel.setValueAt(j, vo.getIntegerValue("id"), "control");
													Thread.sleep(10);
												}
											}
											currValue = per;
											listPanel.setValueAt(per, vo.getIntegerValue("id"), "control");
										}
									}
									if (msg[0] != null) {
										textArea.append((String) msg[0] + "\r\n");
										int length = textArea.getDocument().getLength();
										textArea.setSelectionStart(length);
										textArea.setSelectionEnd(length);
									}
									Thread.sleep(10);
								}
							}
							if ("fail".equals(flag)) {
								MessageBox.show(mainPanel, _type + "失败");
							}
							flag = "";
							refreshActionModule(vo.getStringValue("code"));
							this.cancel();
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageBox.show(mainPanel, _type + "失败");
						flag = "";
						refreshActionModule(vo.getStringValue("code"));
						this.cancel();
					} finally {
					}

				}
			}, 0, 1000);

			//负责执行某个操作，安装，升级，验证等
			new Timer().schedule(new TimerTask() {
				public void run() {
					try {
						flag = getService().installOrUpdateOperateModule(vo, _type);
					} catch (Exception e) {
						flag = "fail";
						e.printStackTrace();
					}
				}
			}, 0);
			this.cancel(); //主线程启动完两个子线程后退出即可
			return;
		}
	}

	//执行完某个操作后刷新。
	public void refreshActionModule(String _code) {
		try {
			modules = getService().getAllInstallModuleStatus();
			listPanel.stopEditing();
			for (int i = 0; i < modules.length; i++) {
				int rowNum = i;
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("id")), rowNum, "id");
				try {
					ImageIcon img = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImageFromServerRespath(modules[i].getStringValue("packagepath") + modules[i].getStringValue("icon")).getImage(), 80, 80));
					listPanel.setValueAt(img, rowNum, "icon");
				} catch (Exception ex) {
					System.out.println("图片加载失败");
				}
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("name")), rowNum, "name");
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("code")), rowNum, "code");
				List list = (List) modules[i].getAttributeValue("updates");
				String currValue = modules[i].getStringValue("version");
				if (currValue == null || "-1".equals(currValue)) {
					currValue = "未知";
				} else if (!currValue.contains("v") && !currValue.contains("V")) {
					currValue = "V" + currValue;
				}
				JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
					@Override
					public String toString() {
						return "";
					}
				};
				cellPanel.setOpaque(false);
				JLabel label = null;
				if (list != null && list.size() > 0) {
					String newVersion = (String) list.get(list.size() - 1);
					if (!newVersion.contains("v") && !newVersion.contains("V")) {
						newVersion = "V" + newVersion;
					}
					label = new WLTLabel("<html>当前版本：" + currValue + "<Br>可升级到：" + newVersion + "</html>");
				} else {
					label = new JLabel(currValue);
					label.setOpaque(false);
					label.setHorizontalAlignment(JLabel.CENTER);
				}
				cellPanel.add(label); //加入到面板上
				if (!WLTConstants.MODULE_INSTALL_STATUS_YAZ.equals(modules[i].getStringValue("control"))) { //如果不是 已经安装
					JButton btn = new WLTButton("查看", UIUtil.getImage("zoomnormal.gif"));
					btn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							showSelectVersionInfo();
						}
					});
					btn.setForeground(new Color(0x037DC9));
					btn.setPreferredSize(new Dimension(60, 23));
					btn.setBorder(BorderFactory.createEmptyBorder());
					cellPanel.add(btn);
				}
				listPanel.setValueAt(cellPanel, rowNum, "version");
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("date")), rowNum, "date");
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("control")), rowNum, "control");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
