package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_RegFormatTemplet extends AbstractTMO {

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "pub_regformatpanel"); // ģ�����,��������޸�
		vo.setAttributeValue("templetname", "ע�����"); // ģ������
		vo.setAttributeValue("templetname_e", "pub_regformatpanel"); // ģ������
		vo.setAttributeValue("tablename", "pub_regformatpanel"); // ��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); // ������
		vo.setAttributeValue("pksequencename", null); // ������
		vo.setAttributeValue("savedtablename", null); // �������ݵı���
		vo.setAttributeValue("CardWidth", "577"); // ��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); // �б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); // �б��Զ������
		vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������

		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "code"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ע�����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "Code"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getItemValue(\"ID\")"); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "80,125"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "100,135"); //
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "formatformula"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ʽ����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "formatformula"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_CUST); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.sysapp.other.RegisterFormatPanelRefDialog\")"); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "225"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "eventbindformula"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�¼���ʽ"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "eventbindformula"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_CUST); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.sysapp.other.RegisterFormatEventBindRefDialog\")"); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "225"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "descr"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "˵��"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "formatformula"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "225"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
