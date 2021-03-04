package cn.com.infostrategy.to.print;

import java.io.Serializable;

/**
 * ��ӡģ��VO
 * @author xch
 *
 */
public class PubPrintTempletVO implements Serializable {

	private static final long serialVersionUID = 5489087080171895910L;

	private Long id = null; //
	private String templetcode = null; //��ӡģ�����
	private String templetname = null; //��ӡģ������

	private PubPrintItemBandVO[] itemBandVOs = null; //��ϸ��VO

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public String getTempletname() {
		return templetname;
	}

	public void setTempletname(String templetname) {
		this.templetname = templetname;
	}

	public PubPrintItemBandVO[] getItemBandVOs() {
		return itemBandVOs;
	}

	public void setItemBandVOs(PubPrintItemBandVO[] itemBandVOs) {
		this.itemBandVOs = itemBandVOs;
	}

}
