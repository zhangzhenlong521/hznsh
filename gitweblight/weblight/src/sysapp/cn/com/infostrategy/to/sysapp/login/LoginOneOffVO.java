package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;

/**
 * ��¼ʱ,һ����ȡ����������.
 * @author xch
 *
 */
public class LoginOneOffVO implements Serializable {

	private static final long serialVersionUID = -6049777544712829594L;
	private Log4jConfigVO log4jConfigVO = null; //Log4j��������Ϣ
	private DataSourceVO[] dataSourceVOs = null; //����ԴVO
	private CurrLoginUserVO currLoginUserVO = null; //��¼��ԱVO
	private DeskTopVO deskTopVO = null; //��¼ҳ�������VO

	public Log4jConfigVO getLog4jConfigVO() {
		return log4jConfigVO;
	}

	public void setLog4jConfigVO(Log4jConfigVO log4jConfigVO) {
		this.log4jConfigVO = log4jConfigVO;
	}

	public DataSourceVO[] getDataSourceVOs() {
		return dataSourceVOs;
	}

	public void setDataSourceVOs(DataSourceVO[] dataSourceVOs) {
		this.dataSourceVOs = dataSourceVOs;
	}

	public CurrLoginUserVO getCurrLoginUserVO() {
		return currLoginUserVO;
	}

	public void setCurrLoginUserVO(CurrLoginUserVO currLoginUserVO) {
		this.currLoginUserVO = currLoginUserVO;
	}

	public DeskTopVO getDeskTopVO() {
		return deskTopVO;
	}

	public void setDeskTopVO(DeskTopVO deskTopVO) {
		this.deskTopVO = deskTopVO;
	}

}
