package cn.com.infostrategy.to.mdata.templetvo;

import cn.com.infostrategy.to.common.HashVO;

/**
 * Ĭ�ϵ�TMO,��Ԫԭģ������VO!!
 * @author xch
 *
 */
public class DefaultTMO extends AbstractTMO {

	private static final long serialVersionUID = -5510217186481101473L;
	private HashVO parVO = null;
	private HashVO[] childVOs = null;

	public DefaultTMO() {
	}

	/**
	 * ��ʱ���ٴ���һ���򵥱��,������ѯʲô��,������ǹ���HashVOʲô�Ļ����鷳,��ֱ����촴��
	 * �������ÿһ�ж����ı���,���ɱ༭...
	 * ֻҪָ��ÿһ�е��������Ⱦ͹���,�ǳ���.
	 * @param _templetName
	 * @param _itemAndWidth ����,��һ����˵��,�ڶ����ǿ�� ���磺new String[][]{{"code","100"},{"name","150"}}
	 */
	public DefaultTMO(String _templetName, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", _templetName); //ģ������
		vo.setAttributeValue("templetname_e", _templetName); //ģ������
		vo.setAttributeValue("tablename", null); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("savedtablename", null); //�������ݵı���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������

		HashVO[] itemVOs = new HashVO[_itemAndWidth.length]; //
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i] = new HashVO(); //
			if (_itemAndWidth[i].length == 2) { //{"code","100"},key��name��һ����!!!
				itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][1])); //�б��ǿ��
				itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][1])); //��Ƭʱ���
				itemVOs[i].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
				itemVOs[i].setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			} else if (_itemAndWidth[i].length == 3) { //{"code","����","100"},�����3��,��key��name�ֱ���!!
				itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][1]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][2])); //�б��ǿ��
				itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][2])); //��Ƭʱ���
				itemVOs[i].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
				itemVOs[i].setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			} else if (_itemAndWidth[i].length == 4) { //{"code","����","100","Y"},�����4��,��key��name�ֱ���!!�һ�ָ���б�Ƭ�Ƿ���ʾ
				itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][1]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //Ψһ��ʶ,����ȡ���뱣��
				itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][2])); //�б��ǿ��
				itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][2])); //��Ƭʱ���
				itemVOs[i].setAttributeValue("listisshowable", _itemAndWidth[i][3]); //�б�ʱ�Ƿ���ʾ(Y,N)
				itemVOs[i].setAttributeValue("cardisshowable", _itemAndWidth[i][3]); //��Ƭʱ�Ƿ���ʾ(Y,N)
			}

			itemVOs[i].setAttributeValue("itemtype", "�ı���"); //Ψһ��ʶ,����ȡ���뱣��
			itemVOs[i].setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVOs[i].setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVOs[i].setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVOs[i].setAttributeValue("iswrap", "Y"); //��Ƭ�Ƿ���
		}

		this.parVO = vo;
		this.childVOs = itemVOs; //
	}

	/**
	 * ���ٴ���TMO,��һ��������ָ����Щ����!!! ��_type�Ŀ����ڶ��������Ķ�ά������п���һ����!!!
	 * @param _templetName
	 * @param _type
	 * @param _itemAndWidth
	 */
	public DefaultTMO(String _templetName, String[] _type, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", _templetName); //ģ������
		vo.setAttributeValue("templetname_e", _templetName); //ģ������
		vo.setAttributeValue("tablename", null); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("savedtablename", null); //�������ݵı���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������

		HashVO[] itemVOs = new HashVO[_itemAndWidth.length]; //
		for (int i = 0; i < itemVOs.length; i++) { //����!!!
			itemVOs[i] = new HashVO(); //
			for (int j = 0; j < _type.length; j++) { //
				itemVOs[i].setAttributeValue(_type[j], _itemAndWidth[i][j]);
			}
		}

		this.parVO = vo;
		this.childVOs = itemVOs; //
	}

	/**
	 * ��Ƭ��TMO,ר�����ڿ�Ƭ�����
	 * @param _templetName
	 * @param _items
	 * @return
	 */
	public static DefaultTMO getCardTMO(String _templetName, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", _templetName); //ģ������
		vo.setAttributeValue("templetname_e", _templetName); //ģ������
		vo.setAttributeValue("tablename", null); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("savedtablename", null); //�������ݵı���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
		HashVO[] itemVOs = new HashVO[_itemAndWidth.length]; //
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i] = new HashVO(); //
			itemVOs[i].setAttributeValue("itemtype", "�ı���"); //Ĭ�����ı���
			if (_itemAndWidth[i][1].indexOf("*") > 0) { //������Ǻ�,���Զ���Ϊ�Ƕ����ı���
				itemVOs[i].setAttributeValue("itemtype", "�����ı���"); //Ĭ�����ı���
			}
			String str_listWidth = _itemAndWidth[i][1]; //
			if (str_listWidth.indexOf(",") > 0) {
				str_listWidth = str_listWidth.substring(str_listWidth.indexOf(",") + 1, str_listWidth.length()); //
			}
			if (str_listWidth.indexOf("*") > 0) {
				str_listWidth = str_listWidth.substring(0, str_listWidth.indexOf("*")); //
			}

			boolean isMustInut = false; //
			String str_itemName = _itemAndWidth[i][0]; //
			if (str_itemName.startsWith("*")) {
				isMustInut = true; //�Ǻſ�ͷ
				str_itemName = str_itemName.substring(1, str_itemName.length()); //
			}
			itemVOs[i].setAttributeValue("itemkey", str_itemName); //Ψһ��ʶ,����ȡ���뱣��
			itemVOs[i].setAttributeValue("itemname", str_itemName); //Ψһ��ʶ,����ȡ���뱣��
			itemVOs[i].setAttributeValue("itemname_e", str_itemName); //Ψһ��ʶ,����ȡ���뱣��
			itemVOs[i].setAttributeValue("cardwidth", _itemAndWidth[i][1]); //��Ƭʱ���
			itemVOs[i].setAttributeValue("listwidth", str_listWidth); //�б��ǿ��
			itemVOs[i].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVOs[i].setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVOs[i].setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVOs[i].setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVOs[i].setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVOs[i].setAttributeValue("ismustinput", isMustInut ? "Y" : "N"); //�Ƿ����

			if (_itemAndWidth[i].length == 2) { //{"code","100"},key��name��һ����!!!
				itemVOs[i].setAttributeValue("iswrap", "Y"); //��Ƭ�Ƿ���
			} else if (_itemAndWidth[i].length == 3) { //{"code","����","100"},�����3��,��key��name�ֱ���!!
				itemVOs[i].setAttributeValue("iswrap", _itemAndWidth[i][2]); //��Ƭ�Ƿ���
			}

		}

		return new DefaultTMO(vo, itemVOs); //
	}

	/**
	 * �в����Ĺ��췽��!!!
	 * @param _parVO
	 * @param _childVOs
	 */
	public DefaultTMO(HashVO _parVO, HashVO[] _childVOs) {
		this.parVO = _parVO;
		this.childVOs = _childVOs;
	}

	/**
	 * ����
	 */
	public HashVO getPub_templet_1Data() {
		return parVO;
	}

	/**
	 * �ӱ�
	 */
	public HashVO[] getPub_templet_1_itemData() {
		return childVOs;
	}

	/**
	 * ����
	 */
	public void setPub_templet_1Data(HashVO _parVO) {
		this.parVO = _parVO;
	}

	/**
	 * �ӱ�
	 */
	public void setPub_templet_1_itemData(HashVO[] _childVOs) {
		this.childVOs = _childVOs;
	}

	private int getWidth(String _s) {
		try {
			return Integer.parseInt(_s);
		} catch (Exception e) {
			e.printStackTrace();
			return 100;
		}
	}
}
