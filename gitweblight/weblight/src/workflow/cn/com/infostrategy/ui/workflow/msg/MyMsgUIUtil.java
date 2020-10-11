package cn.com.infostrategy.ui.workflow.msg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class MyMsgUIUtil {

	/**
	 * 这里应该到服务端去发送消息，可以根据消息类型来发送
	 * @param parent
	 */
	public void sendMsg(JPanel parent) {
		BillCardPanel cardPanel = null;
		if (parent instanceof MyMsgCenterWFPanel) {
			cardPanel = new BillCardPanel(((MyMsgCenter_UnReadWFPanel) ((MyMsgCenterWFPanel) parent).getUnReadPanel()).getBl().getTempletVO());
		} else if (parent instanceof MyMsgCenter_SendedWFPanel) {
			cardPanel = new BillCardPanel(((MyMsgCenter_SendedWFPanel) parent).getBl().getTempletVO());
		} else if (parent instanceof MyMsgCenter_ReadedWFPanel) {
			cardPanel = new BillCardPanel(((MyMsgCenter_ReadedWFPanel) parent).getBl().getTempletVO());
		} else if (parent instanceof MyMsgCenter_UnReadWFPanel) {
			cardPanel = new BillCardPanel(((MyMsgCenter_UnReadWFPanel) parent).getBl().getTempletVO());
		}else {
			cardPanel = new BillCardPanel("PUB_MSGCENTER_CODE1");
		}
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		final BillCardDialog dialog = new BillCardDialog(parent, "发信息", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.getBtn_save().setVisible(false);
		dialog.getBtn_confirm().setText("发送");
		dialog.billcardPanel.setRealValueAt("MSGTYPE", "系统消息");
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				try {
					dialog.billcardPanel.stopEditing();
					if(dialog.billcardPanel.getRealValueAt("RECEIVER") == null && dialog.billcardPanel.getRealValueAt("RECVCORP") == null && dialog.billcardPanel.getRealValueAt("RECVROLE") == null) {
						MessageBox.show(dialog, "发布范围不能为空!");
						return;
					}
					if (!dialog.billcardPanel.checkValidate()) {
						return;
					}
					dialog.setCloseType(1);
					dialog.dispose();
				} catch (Exception e) {
					MessageBox.showException(dialog, e);
				}
			}
		});
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			HashMap param = new HashMap();
			param.put("vo", cardPanel.getBillVO());
			try {
				UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MsgBsUtil", "sendMsg", param);
				MessageBox.show(parent, "发送成功!");
				if (parent instanceof MyMsgCenter_SendedWFPanel) {
					((MyMsgCenter_SendedWFPanel) parent).refresh();
				} else if (parent instanceof MyMsgCenter_ReadedWFPanel) {
					((MyMsgCenter_ReadedWFPanel) parent).refresh();
				} else if (parent instanceof MyMsgCenter_UnReadWFPanel) {
					((MyMsgCenter_UnReadWFPanel) parent).refresh();
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(parent, e);
			}
		}
	}
	
	public boolean commSendMsg(ArrayList msgs) { //模块消息调用UI方法 【杨科/2012-08-15】
		HashMap hm = new HashMap();
		hm.put("msgs", msgs);
		try {
			UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MsgBsUtil", "commSendMsg", hm);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}	

	public void readMsg(BillVO vo, JPanel jp, int c) throws Exception {
		if (vo != null) {
			String msgType = vo.getStringValue("msgtype");
			if (msgType != null && !"".equals(msgType)) {
				String sendclass = UIUtil.getStringValueByDS(null, "select clickimpl from pub_msgcenter_rule where name='" + msgType + "' ");
				if (sendclass != null && !"".equals(sendclass)) {
					Object obj = Class.forName(sendclass).newInstance();
					if (obj instanceof MsgReadExtFunctionIFC) {
						((MsgReadExtFunctionIFC) obj).read(vo, jp, c);
					}else {
						 defaultReadMsg(vo, jp, c);
					}
				}else {
					 defaultReadMsg(vo, jp, c);
				}
			}else {
				 defaultReadMsg(vo, jp, c);
			}
		}
	}

	public void defaultReadMsg(BillVO vo, JPanel jp, int c) {
		BillCardPanel bc = null;
		if (jp instanceof MyMsgCenterWFPanel) {
			bc = new BillCardPanel(((MyMsgCenter_UnReadWFPanel) ((MyMsgCenterWFPanel) jp).getUnReadPanel()).getBl().getTempletVO());
		} else if (jp instanceof MyMsgCenter_SendedWFPanel) {
			bc = new BillCardPanel(((MyMsgCenter_SendedWFPanel) jp).getBl().getTempletVO());
		} else if (jp instanceof MyMsgCenter_ReadedWFPanel) {
			bc = new BillCardPanel(((MyMsgCenter_ReadedWFPanel) jp).getBl().getTempletVO());
		} else if (jp instanceof MyMsgCenter_UnReadWFPanel) {
			bc = new BillCardPanel(((MyMsgCenter_UnReadWFPanel) jp).getBl().getTempletVO());
		}else {
			bc = new BillCardPanel("PUB_MSGCENTER_CODE1");
		}
//		bc.setGroupExpandable("<html><font color='#FF0000'>*</font>发布范围</html>", false);
//		bc.setGroupExpandable("发送人信息", false);
		bc.setBillVO(vo);
		BillCardDialog dialog = new BillCardDialog(jp, "信息查看", bc, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
		if(c == 0) {
			InsertSQLBuilder isq = new InsertSQLBuilder();
			isq.setTableName("pub_msgcenter_readed");
			try {
				isq.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_MSGCENTER_READED"));
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(jp, e);
			}
			isq.putFieldValue("isdelete", "N");
			isq.putFieldValue("msgid", vo.getPkValue());
			isq.putFieldValue("readtime", UIUtil.getCurrTime());
			isq.putFieldValue("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			isq.putFieldValue("sender", vo.getStringValue("sender"));
			isq.putFieldValue("msgtitle", vo.getStringValue("msgtitle"));
			isq.putFieldValue("senddate", vo.getStringValue("senddate"));
			try {
				UIUtil.executeBatchByDS(null, new String[] { isq.getSQL() });
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(jp, e);
			}
			if (jp instanceof MyMsgCenterWFPanel) {
				((MyMsgCenterWFPanel)jp).refresh();
			} else if (jp instanceof MyMsgCenter_SendedWFPanel) {
				((MyMsgCenter_SendedWFPanel) jp).refresh();
			} else if (jp instanceof MyMsgCenter_ReadedWFPanel) {
				((MyMsgCenter_ReadedWFPanel) jp).refresh();
			} else if (jp instanceof MyMsgCenter_UnReadWFPanel) {
				((MyMsgCenter_UnReadWFPanel) jp).refresh();
			}
		}
	}

	public void deleteMsg(JPanel parent, BillListPanel bl) {

		BillVO[] vos = bl.getSelectedBillVOs();
		if (vos == null || vos.length < 1) {
			MessageBox.showInfo(bl, "请选择需要删除的消息!");
			return;
		}
		if(MessageBox.showOptionDialog(bl, "确定删除信息吗?", "提示", new String[]{"确定", "取消"}) != 0) {
			return;
		}
		List sql = new ArrayList();
		if (parent instanceof MyMsgCenter_UnReadWFPanel) {//未读消息删除逻辑
			for (int i = 0; i < vos.length; i++) {
				InsertSQLBuilder isq = new InsertSQLBuilder();
				isq.setTableName("pub_msgcenter_readed");
				try {
					isq.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_MSGCENTER_READED"));
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.showException(bl, e);
				}
				isq.putFieldValue("isdelete", "Y");
				isq.putFieldValue("msgid", vos[i].getPkValue());
				isq.putFieldValue("userid", ClientEnvironment.getCurrLoginUserVO().getId());
				isq.putFieldValue("sender", vos[i].getStringValue("sender"));
				isq.putFieldValue("msgtitle", vos[i].getStringValue("msgtitle"));
				isq.putFieldValue("senddate", vos[i].getStringValue("senddate"));
				sql.add(isq.getSQL());
			}

		} else if (parent instanceof MyMsgCenter_ReadedWFPanel){
			UpdateSQLBuilder ds = new UpdateSQLBuilder();
			ds.setTableName("pub_msgcenter_readed");
			ds.putFieldValue("isdelete", "Y");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < vos.length; i++) {
				if (i == 0) {
					sb.append("'" + vos[i].getPkValue() + "'");
				} else {
					sb.append(",'" + vos[i].getPkValue() + "'");
				}
			}
			ds.setWhereCondition(" msgid in(" + sb.toString() + ") and userid='" + ClientEnvironment.getCurrLoginUserVO().getId() + "'");
			sql.add(ds.getSQL());
		} else {
			UpdateSQLBuilder ds = new UpdateSQLBuilder();
			ds.setTableName(bl.getTempletVO().getSavedtablename());
			ds.putFieldValue("isdelete", "Y");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < vos.length; i++) {
				if (i == 0) {
					sb.append("'" + vos[i].getPkValue() + "'");
				} else {
					sb.append(",'" + vos[i].getPkValue() + "'");
				}
			}
			ds.setWhereCondition(bl.getTempletVO().getPkname() + " in(" + sb.toString() + ") ");
			sql.add(ds.getSQL());
		}

		try {
			UIUtil.executeBatchByDS(null, sql);
			MessageBox.show(bl, "删除成功!");
			if (parent instanceof MyMsgCenter_SendedWFPanel) {
				((MyMsgCenter_SendedWFPanel) parent).refresh();
			} else if (parent instanceof MyMsgCenter_ReadedWFPanel) {
				((MyMsgCenter_ReadedWFPanel) parent).refresh();
			} else if (parent instanceof MyMsgCenter_UnReadWFPanel) {
				((MyMsgCenter_UnReadWFPanel) parent).refresh();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(bl, e);
		}
	}
}
