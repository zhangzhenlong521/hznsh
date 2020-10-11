package cn.com.infostrategy.ui.workflow.msg;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * ��Ϣ����
 * 2��ҳǩ�ռ��䡢�����䣨�Ժ�ɿ��ǲݸ��䡢�����䣩
 * ��������ʦ��������������"
 * �������û�����Ҫһ��ר�ŵ���Ϣ���ı�,���͵���������,�������̽���ʱ��ĸ������˵�����! ������ϵͳ��Ϣ,����ϵͳ��ʲôʱ��������,ϵͳ�������°汾,ĳĳ��Ա����ϵͳ��!!
 * �����Ϣ���pub_msgcenter,���ֱ�����������Ҫ�ֶΣ�������,���ս�ɫ,���ջ�����Χ!! ����ʱ����ֱ�Ӹ�ĳ����,����ĳЩ��ɫ,��ĳ��������Χ�µ�ĳЩ��ɫ!
 * ���ڴ��ڸ�ĳЩ��ɫ�����,���Ի���Ҫһ�ű��¼ĳ����Ϣ�Ƿ��ѱ�ĳ���Ķ���,����"δ����Ϣ"��"�Ѷ���Ϣ"֮��!! �Ѷ���Ϣ������ pub_msgcenter_readed
 *"��仰��������ֻ������չ��һ�������pub_msgcenter_rule������ʵ�ַ����߼���鿴�߼�����չ
 */
public class MyMsgCenterWFPanel extends AbstractWorkPanel implements ChangeListener {
	private JTabbedPane wt = null;
	private JPanel unReadPanel = null;//δ����Ϣ
	private JPanel readedPanel = null;//�Ѷ���Ϣ
	private JPanel sendedPanel = null;//�ѷ���Ϣ
	private int unread, readed, sended = 0;
	private String unsql, resql, sesql = null;

	public MyMsgCenterWFPanel() {
		loadData();
		initialize();
	}

	public void loadData() {
		try {
			HashMap param = new HashMap();
			param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			param.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			param.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			HashMap countmap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MsgBsUtil", "getMsgCountMap", param);
			if (countmap != null && countmap.size() > 0) {
				unread = (Integer) countmap.get("δ����Ϣ");
				readed = (Integer) countmap.get("�Ѷ���Ϣ");
				sended = (Integer) countmap.get("�ѷ���Ϣ");
				unsql = (String) countmap.get("δ��SQL");
				resql = (String) countmap.get("�Ѷ�SQL");
				sesql = (String) countmap.get("�ѷ�SQL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh() {
		loadData();
		if (wt.getSelectedIndex() == 0) {
			((MyMsgCenter_UnReadWFPanel) unReadPanel).refresh();
			wt.setTitleAt(1, "�Ѷ���Ϣ(" + readed + ")");
			wt.setTitleAt(2, "�ѷ���Ϣ(" + sended + ")");
		} else if (wt.getSelectedIndex() == 1) {
			wt.setTitleAt(0, "δ����Ϣ(" + unread + ")");
			((MyMsgCenter_ReadedWFPanel) readedPanel).refresh();
			wt.setTitleAt(2, "�ѷ���Ϣ(" + sended + ")");
		} else if (wt.getSelectedIndex() == 2) {
			wt.setTitleAt(0, "δ����Ϣ(" + unread + ")");
			wt.setTitleAt(1, "�Ѷ���Ϣ(" + readed + ")");
			((MyMsgCenter_SendedWFPanel) sendedPanel).refresh();
		}
	}

	public void refresh(int c) {
		if (wt.getSelectedIndex() == 0) {
			((MyMsgCenter_UnReadWFPanel) unReadPanel).refresh();
		} else if (wt.getSelectedIndex() == 1) {
			((MyMsgCenter_ReadedWFPanel) readedPanel).refresh();
		} else if (wt.getSelectedIndex() == 2) {
			((MyMsgCenter_SendedWFPanel) sendedPanel).refresh();
		}
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		wt = new JTabbedPane(2);
		wt.addTab("δ����Ϣ(" + unread + ")", UIUtil.getImage("zt_062.gif"), getUnReadPanel());//PUB_MSGCENTER_CODE1
		wt.addTab("�Ѷ���Ϣ(" + readed + ")", UIUtil.getImage("zt_063.gif"), new JPanel());
		wt.addTab("�ѷ���Ϣ(" + sended + ")", UIUtil.getImage("office_039.gif"), new JPanel());
		wt.addChangeListener(this);//office_168.gif 039 167
		this.add(wt, BorderLayout.CENTER);
	}

	public JPanel getUnReadPanel() {
		if (unReadPanel == null) {
			unReadPanel = new MyMsgCenter_UnReadWFPanel(this, unsql);
		}
		return unReadPanel;
	}

	public JPanel getReadedPanel() {
		if (readedPanel == null) {
			readedPanel = new MyMsgCenter_ReadedWFPanel(this, resql);
		}
		return readedPanel;
	}

	public JPanel getSendedPanel() {
		if (sendedPanel == null) {
			sendedPanel = new MyMsgCenter_SendedWFPanel(this, sesql);
		}
		return sendedPanel;
	}

	public void stateChanged(ChangeEvent changeevent) {
		changeRadioPanel((JTabbedPane) changeevent.getSource());
	}

	public void changeRadioPanel(JTabbedPane _tabPane) {
		int selectkey = _tabPane.getSelectedIndex();
		if (selectkey == 0) {
			if (unReadPanel == null) {
				unReadPanel = new MyMsgCenter_UnReadWFPanel(this, unsql);
				JPanel jp = (JPanel) _tabPane.getSelectedComponent();
				jp.removeAll();
				jp.setLayout(new BorderLayout());
				jp.add(unReadPanel);
				jp.updateUI();
			} else {
				if (unread != ((MyMsgCenter_UnReadWFPanel) unReadPanel).getBl().getLi_TotalRecordCount()) {
					((MyMsgCenter_UnReadWFPanel) unReadPanel).refresh();
				}
			}
		} else if (selectkey == 1) {
			if (readedPanel == null) {
				readedPanel = new MyMsgCenter_ReadedWFPanel(this, resql);
				JPanel jp1 = (JPanel) _tabPane.getSelectedComponent();
				jp1.removeAll();
				jp1.setLayout(new BorderLayout());
				jp1.add(readedPanel);
				jp1.updateUI();
			} else {
				if (readed != ((MyMsgCenter_ReadedWFPanel) readedPanel).getBl().getLi_TotalRecordCount()) {
					((MyMsgCenter_ReadedWFPanel) readedPanel).refresh();
				}
			}
		} else if (selectkey == 2) {
			if (sendedPanel == null) {
				sendedPanel = new MyMsgCenter_SendedWFPanel(this, sesql);
				JPanel jp2 = (JPanel) _tabPane.getSelectedComponent();
				jp2.removeAll();
				jp2.setLayout(new BorderLayout());
				jp2.add(sendedPanel);
				jp2.updateUI();
			} else {
				if (sended != ((MyMsgCenter_SendedWFPanel) sendedPanel).getBl().getLi_TotalRecordCount()) {
					((MyMsgCenter_SendedWFPanel) sendedPanel).refresh();
				}
			}
		}
	}

	public JTabbedPane getWt() {
		return wt;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public int getReaded() {
		return readed;
	}

	public void setReaded(int readed) {
		this.readed = readed;
	}

	public int getSended() {
		return sended;
	}

	public void setSended(int sended) {
		this.sended = sended;
	}

}
