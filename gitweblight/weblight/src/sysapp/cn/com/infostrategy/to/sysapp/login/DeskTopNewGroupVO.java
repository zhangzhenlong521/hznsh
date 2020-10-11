package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 首页所有公告栏信息的对象
 * @author xch
 *
 */
public class DeskTopNewGroupVO implements Serializable {

	private static final long serialVersionUID = 5769632790132602793L;

	private DeskTopNewGroupDefineVO defineVO = null; //
	private HashVO[] dataVOs = null; //

	public DeskTopNewGroupDefineVO getDefineVO() {
		return defineVO;
	}

	public void setDefineVO(DeskTopNewGroupDefineVO defineVO) {
		this.defineVO = defineVO;
	}

	public HashVO[] getDataVOs() {
		return dataVOs;
	}

	public void setDataVOs(HashVO[] dataVOs) {
		this.dataVOs = dataVOs;
	}
}
