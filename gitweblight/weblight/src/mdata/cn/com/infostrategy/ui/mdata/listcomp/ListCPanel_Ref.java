/**************************************************************************
 * $RCSfile: ListCPanel_Ref.java,v $  $Revision: 1.7 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

public class ListCPanel_Ref extends UIRefPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * ֱ���ֹ�����
	 * @param _key Ψһ��ʶ
	 * @param _name  ����
	 * @param _refdesc ���ն���
	 * @param _type ��������
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public ListCPanel_Ref(String _key, String _name, String _refdesc, String _type, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_key, _name, _refdesc, _type, _initRefItemVO, _billPanel); ////
		initialize();
	}

	/**
	 * ��Ԫԭģ�崴��
	 * @param _templetVO
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public ListCPanel_Ref(Pub_Templet_1_ItemVO _templetVO, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_templetVO, _initRefItemVO, _billPanel);
		initialize();
	}

	/**
	 * ����ҳ��
	 */
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //�Զ���UI���Ѿ�ȥ��border��
			textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //���б����ı���ı߿���������!!!
		}
		if (this.type.equals(WLTConstants.COMP_DATE) || this.type.equals(WLTConstants.COMP_DATETIME)) {
			textField.setEnabled(false);
		}
		this.add(textField, BorderLayout.CENTER); //�ȼ����ı���

		JPanel panel_east = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		panel_east.add(btn_ref);
		boolean onlyonebtn = true;
		if (templetItemVO != null) {
			if ((WLTConstants.COMP_OFFICE).equals(templetItemVO.getItemtype())) {
				if (importB != null) {
					panel_east.add(importB); //
					onlyonebtn = false;
				}
			}
		}

		int li_hyperlinkwidth = 0;
		if (hyperlinks != null) { //����г�����
			for (int i = 0; i < hyperlinks.length; i++) {
				li_hyperlinkwidth = li_hyperlinkwidth + (int) hyperlinks[i].getPreferredSize().getWidth(); //
				panel_east.add(hyperlinks[i]); //���볬�������
				onlyonebtn = false;
			}
		}
		if (onlyonebtn) {
			this.add(btn_ref, BorderLayout.EAST); //���ֻ��һ����ť��ֱ�Ӽ��뵽����У���ťͬ�ı����ͬʱ���Ӹ߶ȡ�hm 2013-5-31
		} else {
			this.add(panel_east, BorderLayout.EAST); //
		}
	}

	public int getAllWidth() {
		return 0;
	}

}
