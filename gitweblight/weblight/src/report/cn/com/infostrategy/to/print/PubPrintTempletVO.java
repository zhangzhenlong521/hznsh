package cn.com.infostrategy.to.print;

import java.io.Serializable;

/**
 * 打印模板VO
 * @author xch
 *
 */
public class PubPrintTempletVO implements Serializable {

	private static final long serialVersionUID = 5489087080171895910L;

	private Long id = null; //
	private String templetcode = null; //打印模板编码
	private String templetname = null; //打印模板名称

	private PubPrintItemBandVO[] itemBandVOs = null; //明细项VO

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
