package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_RefFile extends AbstractTMO {

	private static final long serialVersionUID = 1L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", ""); // ģ�����,��������޸�
		vo.setAttributeValue("templetname", ""); // ģ������
		vo.setAttributeValue("templetname_e", ""); // ģ������
		vo.setAttributeValue("ISSHOWLISTCUSTBTN", "Y"); // ģ������

		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "filename"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ļ��б�"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "filename"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_CUST); //Ϊ��ʵ����ʾʱֻ��ʾ�ļ���,����ʾ�����ŵ�Ч��!�ĳɲ���
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "205"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "edit"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", ""); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "30"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y"); // ����ɱ༭
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "download"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", ""); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "30"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y"); // ����ɱ༭
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "delete"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", ""); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "30"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y"); // ����ɱ༭
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
