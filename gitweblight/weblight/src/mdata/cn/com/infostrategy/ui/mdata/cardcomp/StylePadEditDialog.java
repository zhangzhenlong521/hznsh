package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTStylePadPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

/**
 * ����༭����,��֧�ִ���б���Ч���ı༭����!!!
 * @author xch
 *
 */
public class StylePadEditDialog extends BillDialog implements ActionListener {

	private String str_inittext = null; //��ʼ����ֵ
	private String str_initbatchid = null; //��ʼ����ֵ
	private String[] str_updatesqls = null; //��д��ҳ���SQL!!!

	private WLTStylePadPanel stylePadPanel = null; //����༭��!!!
	private WLTButton btn_confirm, btn_cancel; //

	private String returnBatchId = null; //
	private String returnText = null; //

	public StylePadEditDialog(Container _parent, String _title, String _text, String _padid, String[] _sqls) {
		super(_parent, _title, 650, 650);
		this.str_inittext = _text; //
		this.str_initbatchid = _padid; //
		this.str_updatesqls = _sqls; //
		initialize(); //
	}

	/**
	 * ����ҳ��!!!
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		stylePadPanel = new WLTStylePadPanel(); //
		if (str_initbatchid == null) {
			stylePadPanel.setText(str_inittext); //
		} else {
			try {
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_stylepaddoc where batchid='" + str_initbatchid + "' order by seq"); //
				if (hvs == null || hvs.length == 0) {
					stylePadPanel.setText(str_inittext); //���û�ҵ�,��ֱ��ʹ��ԭ����!������Ǩ��ʱ©����!
				} else {
					StringBuilder sb_64code = new StringBuilder(); //
					String str_item = null; //
					for (int i = 0; i < hvs.length; i++) { //����!
						for (int j = 0; j < 10; j++) {
							str_item = hvs[i].getStringValue("doc" + j); //
							if (str_item != null && !str_item.equals("")) { //�����ֵ
								sb_64code.append(str_item.trim()); //ƴ������!!
							} else { //���ֵΪ��
								break; //�ж��˳�!!!��Ϊֻ���������һ������ͷ,����ֻ��Ҫ��ѭ���жϼ���!!
							}
						}
					}
					stylePadPanel.load64CodeToDocument(sb_64code.toString()); //��������!!!!
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		JScrollPane scroll = new JScrollPane(stylePadPanel); //
		this.add(scroll, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel; //
	}
	
	public void setEditble(boolean bo) {
		stylePadPanel.setEditble(bo);
		btn_confirm.setEnabled(bo);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		}
	}

	/**
	 * ���ȷ��,Ҫ��ƽ̨���в�������!!!Ȼ�󷵻�һ���ַ���!!! 
	 * ���ص��ַ�����980����,�����������!!!!
	 */
	private void onConfirm() {
		try {
			String str_text = stylePadPanel.getDocumentText(); //�ı�
			String str_64code = stylePadPanel.getDocument64Code(); //64λ��!!!
			if (str_text == null || str_text.trim().equals("")) { //����ı�ʵ������Ϊ��,����Ϊ64λ����ҲΪ��!!
				str_64code = null;
				str_text = ""; //
			} else {
				str_text = new TBUtil().subStrByGBLength(str_text, 1950, true); //ֻȡǰ1950��!
			}
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			String str_batchid = service.saveStylePadDocument(this.str_initbatchid, str_64code, str_text, str_updatesqls); //ʵ�ʲ������ݿ�!!
			returnBatchId = str_batchid; //
			returnText = str_text; //
			this.setCloseType(1); //
			this.dispose(); //�ر�!!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onCancel() {
		this.setCloseType(2); //
		this.dispose(); //
	}

	public String getReturnBatchId() {
		return returnBatchId;
	}

	public String getReturnText() {
		return returnText;
	}
}
