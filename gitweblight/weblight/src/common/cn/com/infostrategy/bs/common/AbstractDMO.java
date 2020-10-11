package cn.com.infostrategy.bs.common;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import cn.com.infostrategy.to.common.CurrSessionVO;


/**
 * DMO�Ļ���!!
 * @author xc
 *
 */
public abstract class AbstractDMO {

	/**
	 * ȡ�õ�ǰSessionVO!!!
	 */
	public final CurrSessionVO getCurrSession() {
		return new WLTInitContext().getCurrSession(); //
	}

	/**
	 * ȡ��WebLight�����ݿ�����!!
	 * @param _dsname
	 * @return
	 * @throws SQLException
	 */
	protected final WLTDBConnection getConn(String _dsname) throws Exception {
		return new WLTInitContext().getConn(_dsname); //
	}

	protected final String getCurrDate() {
		SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
		return sdf_date.format(new java.util.Date()); //
	}

	protected final String getCurrDateTime() {
		SimpleDateFormat sdf_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf_datetime.format(new java.util.Date()); //
	}
}
