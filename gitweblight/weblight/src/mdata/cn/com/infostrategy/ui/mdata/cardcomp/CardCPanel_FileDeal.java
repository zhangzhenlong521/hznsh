package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ��Ƭ״̬�µ��ļ��ϴ������ؿ�
 * ��ǰ��û��������,��ǰ��Ƭ��Ҳ��һ�����յ�����,��������ٵ��������б�,Ҫ���һ��..
 * �����������û���һ�β��ܽ������ַ�ʽ������ԭ�����Ҳ������ݣ���OAʹ��ϰ�߼���һ������
 * ���Ի������������ӱ��Ч��������ҳ����,��Ϊ�����Թ̫��Ϊ�˼����鷳�����û����м�ֵ��һ�µ�..
 * ��ԭ���ĵ�������Ҫ�ģ���Ϊ�б��ϵ������������Ҫ�ģ�����
 * @author xch
 *
 */
public class CardCPanel_FileDeal extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = 1L;
	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String itemKey = null; //
	private String itemName = null; //
	private JLabel label = null;

	private int li_width_all = 445; //

	private RefFileDealPanel fileDealPanel = null; //ֱ�Ӵ����ļ���Ū���
	private BillPanel billCardPanel = null; //

	private CardCPanel_FileDeal() {
	}

	/**
	 * ���췽��.
	 * @param pub_Templet_1_ItemVO
	 * @param billCardPanel
	 */
	public CardCPanel_FileDeal(Pub_Templet_1_ItemVO _templet_1_ItemVO, BillPanel _billCardPanel) {
		this.setLayout(new BorderLayout()); //
		this.setBackground(LookAndFeel.cardbgcolor); //

		this.templetItemVO = _templet_1_ItemVO; //
		this.billCardPanel = _billCardPanel; //

		this.itemKey = _templet_1_ItemVO.getItemkey(); //
		this.itemName = _templet_1_ItemVO.getItemname(); //

		int li_tablewidth = 300; //
		if (templetItemVO.getCardwidth() != null) {
			li_tablewidth = templetItemVO.getCardwidth().intValue(); //
		}

		int li_tableheight = 55; //
		label = createLabel(templetItemVO); //���ø����ṩ�ķ�������Label
		li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); //

		fileDealPanel = new RefFileDealPanel(this, billCardPanel, templetItemVO.getUCDfVO()); //
		fileDealPanel.getBillListPanel().setItemWidth("filename", li_tablewidth - 130);//��ǰli_tablewidth - 150 ��ȼ�ȥ��̫���ˡ����/2012-03-26��
		fileDealPanel.getBillListPanel().getToolbarPanel().setOpaque(false); //
		this.add(label, BorderLayout.WEST); //
		this.add(fileDealPanel, BorderLayout.CENTER); //

		this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
	}

	public RefFileDealPanel getRefFileDealPanel() {

		return fileDealPanel;
	}

	public BillPanel getBillCardPanel() {
		return billCardPanel;
	}

	@Override
	public String getItemKey() {
		return itemKey;
	}

	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public JLabel getLabel() {
		return label;
	}

	@Override
	public Object getObject() {
		return fileDealPanel.getAllFileRefItemVOs();
	}

	@Override
	public String getValue() {
		RefItemVO refVO = (RefItemVO) getObject();
		if (refVO == null) {
			return null;
		}
		return refVO.getId();
	}

	@Override
	public void setObject(Object _obj) {
		try {
			RefItemVO refVO = (RefItemVO) _obj; //
			fileDealPanel.setRefItemVO(refVO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void setValue(String _value) {
		setObject(new RefItemVO(_value, _value, _value));
	}

	@Override
	public void setItemEditable(boolean _bo) {
		fileDealPanel.setEditabled(_bo); //
		//System.out.println("�ٴ������Ƿ�ɱ༭=[" + _bo + "]"); //
	}

	@Override
	public boolean isItemEditable() {
		return true;
	}

	@Override
	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo);
	}

	@Override
	public int getAllWidth() {
		return li_width_all;
	}

	@Override
	public void reset() {
		fileDealPanel.clearAllFile(); //
	}

	@Override
	public void focus() {

	}

	public Pub_Templet_1_ItemVO getTempletItemVO() {
		return templetItemVO;
	}

	public void setTempletItemVO(Pub_Templet_1_ItemVO templetItemVO) {
		this.templetItemVO = templetItemVO;
	}

}
