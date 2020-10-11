package cn.com.infostrategy.ui.workflow.engine;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_WaterFileList extends AbstractTMO {
	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "id"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "id"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150*20"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "processid"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��������"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "processid"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150*20"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "filename"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�����ļ�"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "filename"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "600"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150*20"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("listishtmlhref", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "fileid"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ļ�����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "fileid"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150*20"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userid"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�û�����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "processid"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150*20"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "username"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ӡ��"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "username"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150*20"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "waterFileListPanel"); // ģ�����,��������޸�
		vo.setAttributeValue("templetname", "�Ѹ����ļ�"); // ģ������
		vo.setAttributeValue("templetname_e", "waterFileListPanel"); // ģ������
		vo.setAttributeValue("Islistautorowheight", "Y"); // ģ�������б��Ƿ��Զ��Ÿ�
		return vo;
	}
}