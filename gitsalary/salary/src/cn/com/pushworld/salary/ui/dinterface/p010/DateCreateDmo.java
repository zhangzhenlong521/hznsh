package cn.com.pushworld.salary.ui.dinterface.p010;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.UIUtil;

public class DateCreateDmo extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;
	private String tablename;
	private String parentid;
	private String itemname;
	private String[] titlenames;
	private HashMap titlemap;
	private boolean isSql;
	private boolean isedit = false;

	public DateCreateDmo(String tablename, String parentid, String itemname, boolean isSql) {
		this.tablename = tablename;
		this.parentid = parentid;
		this.itemname = itemname;
		this.isSql = isSql;
	}

	public DateCreateDmo(String tablename, String[] titlenames, HashMap titlemap, boolean isSql, boolean _isedit) {
		this.tablename = tablename;
		this.titlenames = titlenames;
		this.titlemap = titlemap;
		this.isSql = isSql;
		this.isedit = _isedit;
	}

	private HashVO[] getTitleHashVOs() {
		HashVO[] titleVO = null;
		try {
			if (isSql) {
				titleVO = UIUtil.getHashVoArrayByDS(null, "select itemcode, itemname from " + itemname + " where parentid ='" + parentid + "' order by seq asc");
			} else {
				titleVO = new HashVO[titlenames.length];
				for (int i = 0; i < titlenames.length; i++) {
					titleVO[i] = new HashVO();
					titleVO[i].setAttributeValue("itemname", titlenames[i]);
					titleVO[i].setAttributeValue("itemcode", titlemap.get(titlenames[i]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return titleVO;
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", tablename + "_code1"); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", "����ͬ��ģ��"); //ģ������
		vo.setAttributeValue("templetname_e", "����ͬ��"); //ģ������
		vo.setAttributeValue("tablename", tablename); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); //������
		vo.setAttributeValue("pksequencename", "S_" + tablename); //������
		vo.setAttributeValue("savedtablename", tablename); //�������ݵı���
		vo.setAttributeValue("CardWidth", "577"); //��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������

		vo.setAttributeValue("TREEPK", "id"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeviewfield", "name"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowtoolbar", "Y"); //������ʾ������
		vo.setAttributeValue("Treeisonlyone", "N"); //������ʾ������
		vo.setAttributeValue("Treeseqfield", "seq"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowroot", "Y"); //�б��Ƿ���ʾ������ť��

		vo.setAttributeValue("isshowlistquickquery", "Y"); //�б��Ƿ���ʾ��ѯ���

		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "id"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "����"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", ""); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "325"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", isedit ? "1" : "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		HashVO[] hashVOs = getTitleHashVOs();
		if (hashVOs != null && hashVOs.length > 0) {
			for (int i = 0; i < hashVOs.length; i++) {
				itemVO = new HashVO();
				itemVO.setAttributeValue("itemkey", hashVOs[i].getStringValue("itemcode")); //Ψһ��ʶ,����ȡ���뱣��
				itemVO.setAttributeValue("itemname", hashVOs[i].getStringValue("itemname")); //��ʾ����
				itemVO.setAttributeValue("itemname_e", hashVOs[i].getStringValue("itemcode")); //��ʾ����
				itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
				itemVO.setAttributeValue("comboxdesc", null); //��������
				itemVO.setAttributeValue("refdesc", null); //���ն���
				itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
				itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
				itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
				itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
				itemVO.setAttributeValue("editformula", null); //�༭��ʽ
				itemVO.setAttributeValue("defaultvalueformula", ""); //Ĭ��ֵ��ʽ
				itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
				itemVO.setAttributeValue("cardwidth", "325"); //��Ƭʱ���
				itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
				itemVO.setAttributeValue("listiseditable", isedit ? "1" : "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
				itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
				itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
				itemVO.setAttributeValue("iswrap", "N");
				vector.add(itemVO);
			}
		}
		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
