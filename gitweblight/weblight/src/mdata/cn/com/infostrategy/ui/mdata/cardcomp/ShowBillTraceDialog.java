package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��ʷ�޸ĺۼ�..
 * @author xch
 *
 */
public class ShowBillTraceDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -471971777355434144L;
	private String str_tablename = null;
	private String str_fieldname = null;
	private String str_pkname = null;
	private String str_pkvalue = null;
	private BillListPanel billlist = null;
	private WLTButton btn_confirm = null; //
	private String str_sql = null; //
	private String[] str_fieldnames = null; //̫ƽ����Ŀ��Ҫ���

	/**
	 * 
	 * @param _parent
	 * @param _tablename
	 * @param _fieldname
	 */
	public ShowBillTraceDialog(Container _parent, String _tablename, String _pkname, String _pkvalue, String _fieldname) {
		super(_parent, "�鿴[" + _tablename + "].[" + _fieldname + "]��ʷ�޸ĺۼ�", 500, 300); //
		str_tablename = _tablename;
		str_pkname = _pkname;
		str_pkvalue = _pkvalue; //
		str_fieldname = _fieldname; //
		initialize();
	}

	//̫ƽ����Ŀ��Ҫ���
	public ShowBillTraceDialog(Container _parent, String _tablename, String _pkname, String _pkvalue, String[] _fieldnames) {
		super(_parent, "�鿴��ʷ�޸ĺۼ�", 500, 300); //
		str_tablename = _tablename;
		str_pkname = _pkname;
		str_pkvalue = _pkvalue; //
		str_fieldnames = _fieldnames; //
		initialize2();
	}

	private void initialize() {
		billlist = new BillListPanel(new TMO_TraceHist()); //
		str_sql = "select * from pub_bill_keeptrace where tablename='" + this.str_tablename + "' and pkname='" + str_pkname + "' and pkvalue='" + str_pkvalue + "' and fieldname='" + str_fieldname + "' order by tracetime desc";
		billlist.QueryData(str_sql); //
		JPanel panel_south = new JPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_confirm.addActionListener(this); //
		panel_south.add(btn_confirm); //

		this.getContentPane().add(billlist, BorderLayout.CENTER); //
		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //
	}

	private void initialize2() {
		TMO_TraceHist traceHistTMO = new TMO_TraceHist();
		billlist = new BillListPanel(traceHistTMO); //
		TBUtil tbUtil = new TBUtil();
		str_sql = "select * from pub_bill_keeptrace where " + "tablename='" + this.str_tablename + "' and pkname='" + str_pkname + "' and pkvalue='" + str_pkvalue + "' and fieldname in (" + tbUtil.getInCondition(str_fieldnames) + ") order by fieldname,tracetime desc";
		billlist.QueryData(str_sql); //
		JPanel panel_south = new JPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_confirm.addActionListener(this); //
		panel_south.add(btn_confirm); //

		this.getContentPane().add(billlist, BorderLayout.CENTER); //
		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //
	}

	public BillListPanel getBilllist() {
		return billlist;
	}

	public void setBilllist(BillListPanel billlist) {
		this.billlist = billlist;
	}

	public WLTButton getBtn_confirm() {
		return btn_confirm;
	}

	public void setBtn_confirm(WLTButton btn_confirm) {
		this.btn_confirm = btn_confirm;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == 17 || e.getModifiers() == 18) {
			MessageBox.showTextArea(this, str_sql); //
		} else {
			this.dispose(); //
		}
	}

	class TMO_TraceHist extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "trace"); //ģ�����,��������޸�
			vo.setAttributeValue("templetname", "�޸ĺۼ�"); //ģ������
			vo.setAttributeValue("templetname_e", ""); //ģ������
			vo.setAttributeValue("tablename", null); //��ѯ���ݵı�(��ͼ)��		
			return vo;
		}

		@Override
		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "id"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "id"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "tablename"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "tablename"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "pkname"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�����ֶ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "pkname"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "pkvalue"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�����ֶ�ֵ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "pkvalue"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldname"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�����ۼ��ֶ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "fieldname"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldvalue"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ʷ�޸�ֵ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "fieldvalue"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "400*75"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "tracer"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�޸���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "tracer"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "tracetime"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�޸�ʱ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "tracetime"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}

	}

}
