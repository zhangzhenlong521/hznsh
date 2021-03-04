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
 * @Description: ��װ������ģ�������档
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

	private HashVO[] modules; //����ģ��

	private HashVO installVO = null; //ѡ��İ�װ����

	//�˵�����ʵ����
	public void initialize() {
		mainPanel = this;
		init();
	}

	//�ͻ��˵���ʵ������֧�ֿͻ���Appletֱ�ӵ��á�/install
	public void loadApplet(JApplet _applet, JPanel panel) throws Exception {
		mainPanel = panel;
		init();
	}

	//��ʼ��
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

	//�õ�����ģ���״̬���
	public JPanel getModuleStatePanel() {
		if (modulePanel == null) {
			modulePanel = new JPanel(new BorderLayout());
		}
		listPanel.addBillListHtmlHrefListener(this);//
		listPanel.putClientProperty("action", this);//�����¼�����InstallScheduleTableEditor���õ���
		listPanel.setCanShowCardInfo(false);
		listPanel.getTable().addMouseListener(new MouseAdapter() {
		});
		listPanel.addBillListMouseDoubleClickedListener(new BillListMouseDoubleClickedListener() {
			public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
			}
		});
		btn_history = new WLTButton("�鿴��װ��ʷ"); //Ϊ�˰�ȫ�������ðѴ󲿷�����des����/���� 
		listPanel.addBatchBillListButton(new WLTButton[] { btn_history });
		listPanel.repaintBillListButton();
		modulePanel.add(listPanel, BorderLayout.CENTER);
		return modulePanel;
	}

	//�õ��������ı�����
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

	//����ģ�����ݡ�
	//�ӷ������˻�ȡ�ɰ�װ��Ϣ��
	private void loadModuleInfo() {
		listPanel.removeAllRows();

		new SwingWorker() {//swing�̹߳���
			//worker��������ִ�е��߼�
			protected Object doInBackground() throws Exception {
				modules = getService().getAllInstallModuleStatus();
				return modules;
			}

			//��doInBackground������Ϻ󣬻�ִ�д˷�����
			protected void done() {
				for (int i = 0; i < modules.length; i++) {
					int rowNum = listPanel.insertEmptyRow(listPanel.getRowCount());
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("id")), rowNum, "id");
					try {
						ImageIcon img = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImageFromServerRespath(modules[i].getStringValue("packagepath") + modules[i].getStringValue("icon")).getImage(), 80, 80));
						listPanel.setValueAt(img, rowNum, "icon");
					} catch (Exception ex) {
						System.out.println("ͼƬ����ʧ��");
					}
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("name")), rowNum, "name");
					listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("code")), rowNum, "code");
					List list = (List) modules[i].getAttributeValue("updates");
					String currValue = modules[i].getStringValue("version");
					if (currValue == null || "-1".equals(currValue)) {
						currValue = "δ֪";
					} else if (!currValue.contains("v") && !currValue.contains("V")) {
						currValue = "V" + currValue;
					}
					JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
						public String toString() {//���� ToolTipText����
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
						label = new WLTLabel("<html>��ǰ�汾��" + currValue + "<Br>����������" + newVersion + "</html>");
					} else {
						label = new JLabel(currValue);
						label.setOpaque(false);
						label.setHorizontalAlignment(JLabel.CENTER);
					}
					cellPanel.add(label); //���뵽�����
					if (!WLTConstants.MODULE_INSTALL_STATUS_YAZ.equals(modules[i].getStringValue("control"))) { //������� �Ѿ���װ
						JButton btn = new WLTButton("�鿴", UIUtil.getImage("zoomnormal.gif"));
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

	//��ʾѡ��İ汾��Ϣ��
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
			MessageBox.showInfo(mainPanel, "������Ϣ");
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
		} else { //�����ֶο���Ҳ�����ӡ�
			_event.getBillListPanel().getSelectedBillVO();
		}
	}

	private void doClickInstallAction(BillListHtmlHrefEvent _event) {
		BillVO selectVO = listPanel.getSelectedBillVO();
		String str[] = TBUtil.getTBUtil().split(_event.getItemkey(), ";");
		if (str != null && str.length == 2) { //����
			String buttonName = str[0];
			if ("�ȴ���װ".equals(buttonName)) {
				return;
			}
			if (!"����".equals(buttonName)) {
				boolean flatInstalled = false; //ƽ̨�Ƿ��Ѿ���װ����Ҫ�����״ΰ�װϵͳ
				for (int i = 0; i < modules.length; i++) {
					if (modules[i].getStringValue("id").equals(selectVO.getStringValue("id"))) {
						installVO = modules[i];
					}
					if ("FLAT".equalsIgnoreCase(modules[i].getStringValue("code"))) {//�����ƽ̨����⵱ǰƽ̨״̬��
						if (!WLTConstants.MODULE_INSTALL_STATUS_KAZ.equals(modules[i].getStringValue("control"))) {//����ǿɰ�װ���ڰ�װ����ģ���ǣ������Ȱ�װƽ̨��
							flatInstalled = true;
						}
					}
				}
				try {
					if (WLTConstants.MODULE_INSTALL_STATUS_KSJ.equals(installVO.getStringValue("control"))) { //����
						if (!MessageBox.confirm(mainPanel, "������ģ�鹦��ǰ����ر������ݿ⣡�Ƿ������")) {
							return;
						}
						gotoInstallOrUpdate(installVO, "update");
					} else if (WLTConstants.MODULE_INSTALL_STATUS_KAZ.equals(installVO.getStringValue("control"))) {//��װ�������磺������Ȩ������������ͬ�������
						if (!"FLAT".equalsIgnoreCase(installVO.getStringValue("code")) && !flatInstalled) { //�������ƽ̨,��Ҫ�ж�ƽ̨�Ƿ��Ѿ���װ����
							if (MessageBox.confirm(mainPanel, "����û�а�װWebPushƽ̨���Ƿ�������װ��")) { //ǿ�а�װƽ̨
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
									MessageBox.showWarn(mainPanel, "���ܼ�⵽WebPushƽ̨��װ���ݣ�����ϵ�����̣�");
									return;
								}
							} else {
								return;
							}
						}
						String showText = "ȷ��Ҫ��װ��ģ����";
						if (installVO != null) {
							if (installVO.containsKey("license")) {
								showText = installVO.getStringValue("license");
							}
						}
						if (MessageBox.confirm(mainPanel, showText)) {
							gotoInstallOrUpdate(installVO, "install");
						}
					} else if (WLTConstants.MODULE_INSTALL_STATUS_WSQ.equals(installVO.getStringValue("control"))) {
						String value = JOptionPane.showInputDialog(mainPanel, "��������Ȩ���кţ�");
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
					String configNum = UIUtil.getStringValueByDS(null, "select count(*) from pub_module_on_off where modulecode = '" + code + "'"); //�����õ�������
					if (configNum != null && !"0".equals(configNum)) { //���ݿ�������
						BillListDialog listDialog = new BillListDialog(mainPanel, selectVO.getStringValue("name") + "����", "PUB_MODULE_ON_OFF_CODE1") {
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
							public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) { //�����κδ���
							}
						});
						listDialog.setTitle(listDialog.getTitle() + " (���ȷ���������в���)");

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
						MessageBox.show(mainPanel, "��ģ��û�п�������");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String flag; //������ȫ�ֱ����������ж��Ƿ�Ϊ�ա�

	//����֧�ֶ������а�װŶ����Ҫһ��һ������
	private void gotoInstallOrUpdate(HashVO vo, String _type) {
		textArea.append("\r\n*******************��ʼִ��**********************\r\n");
		new Timer().schedule(new InstallTask(vo, _type), 0, 500);

		try {
			FrameWorkCommServiceIfc commServices = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			ClientEnvironment.getInstance().setClientSysOptions(commServices.reLoadDataFromDB(false)); //���ÿͻ���pub_optionϵͳ����
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	//ִ�н��ȡ�
	class InstallTask extends TimerTask {
		private HashVO vo;
		private String _type;

		public InstallTask(HashVO _vo, String _type) {
			this.vo = _vo;
			this._type = _type;
		}

		public void run() {
			if (flag != null && !flag.equals("")) { //���flag��Ϊnull��֤���Ѿ���ϵͳ�ڰ�װִ�С���Ҫ�ȴ���
				listPanel.setValueAt(new StringItemVO("�ȴ���װ"), vo.getIntegerValue("id"), "control");
				return;
			}
			flag = "";
			//����ˢ�½���������ӡ��ϸ�������ݡ�
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
									if (per - currValue > 10) { //���ǰ���10���ϣ��Զ���ȫ��
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
							List list1 = getService().getInstallOrUpdateSchedule(); //ִ�����ǰ��ȥһ�η������˽�����־��
							if (list1 != null) {
								for (int i = 0; i < list1.size(); i++) {
									Object[] msg = (Object[]) list1.get(i);
									if ((Float) msg[1] != -1) {
										float f = (Float) msg[1];
										int per = (int) f;
										if (per > 0) {
											if (per - currValue > 10) {////���ǰ���10���ϣ��Զ���ȫ��
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
								MessageBox.show(mainPanel, _type + "ʧ��");
							}
							flag = "";
							refreshActionModule(vo.getStringValue("code"));
							this.cancel();
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageBox.show(mainPanel, _type + "ʧ��");
						flag = "";
						refreshActionModule(vo.getStringValue("code"));
						this.cancel();
					} finally {
					}

				}
			}, 0, 1000);

			//����ִ��ĳ����������װ����������֤��
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
			this.cancel(); //���߳��������������̺߳��˳�����
			return;
		}
	}

	//ִ����ĳ��������ˢ�¡�
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
					System.out.println("ͼƬ����ʧ��");
				}
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("name")), rowNum, "name");
				listPanel.setValueAt(new StringItemVO(modules[i].getStringValue("code")), rowNum, "code");
				List list = (List) modules[i].getAttributeValue("updates");
				String currValue = modules[i].getStringValue("version");
				if (currValue == null || "-1".equals(currValue)) {
					currValue = "δ֪";
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
					label = new WLTLabel("<html>��ǰ�汾��" + currValue + "<Br>����������" + newVersion + "</html>");
				} else {
					label = new JLabel(currValue);
					label.setOpaque(false);
					label.setHorizontalAlignment(JLabel.CENTER);
				}
				cellPanel.add(label); //���뵽�����
				if (!WLTConstants.MODULE_INSTALL_STATUS_YAZ.equals(modules[i].getStringValue("control"))) { //������� �Ѿ���װ
					JButton btn = new WLTButton("�鿴", UIUtil.getImage("zoomnormal.gif"));
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
