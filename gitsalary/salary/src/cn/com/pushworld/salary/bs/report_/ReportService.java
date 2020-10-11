package cn.com.pushworld.salary.bs.report_;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

public interface ReportService {
	
	/**
	 * 报表结果集
	 */
	public BillCellVO getReportResult(HashVO[] _hvos, String _filename) throws Exception;
	
	/**
	 * 报表结果集
	 */
	public BillCellVO getReportCellVO(HashMap _where, String _filename) throws Exception;
	
}
