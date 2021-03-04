package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;

/**
 * 登录时,一次性取得所有数据.
 * @author xch
 *
 */
public class LoginOneOffVO implements Serializable {

	private static final long serialVersionUID = -6049777544712829594L;
	private Log4jConfigVO log4jConfigVO = null; //Log4j的配置信息
	private DataSourceVO[] dataSourceVOs = null; //数据源VO
	private CurrLoginUserVO currLoginUserVO = null; //登录人员VO
	private DeskTopVO deskTopVO = null; //登录页面的数据VO

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
