package cn.com.pushworld.salary.bs.dinterface;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * �ӿ�����ͨ���Զ������ת��Ϊϵͳ����
 * @author haoming
 * create by 2014-7-8
 */
public interface DataIFCConvertToReport {
	public Logger logger = WLTLogger.getLogger(DataIFCConvertToReport.class); //
	public String convert(HashVO _mainConfig, String _jobid, String _dataDate) throws Exception;
}
