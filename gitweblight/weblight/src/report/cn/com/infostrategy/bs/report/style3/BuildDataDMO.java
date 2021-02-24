package cn.com.infostrategy.bs.report.style3;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * 风格报表3后台实际计算的逻辑
 * 
 * @author xch
 * 
 */
public class BuildDataDMO extends AbstractDMO {

	public BillChartVO styleReport_3_BuildData(HashMap _condition, String _builderClassName, CurrLoginUserVO _loginUserVO) throws Exception {
		StyleReport_3_BuildDataIFC builder = (StyleReport_3_BuildDataIFC) Class.forName(_builderClassName).newInstance(); //
		HashVO[] hvs = builder.buildDataByCondition(_condition, _loginUserVO); //先构造数据,这时已会根据条件等，给类变量赋值了，然后其他重构方法都可以取得想要的值,比如维度名称,条件值等
		String str_ColHeadName = builder.getColHeadName();
		String str_RowHeadName = builder.getRowHeadName();
		String[] str_rowHeaderSorts = null; //
		String[] str_colHeaderSorts = null; //
		boolean isRowZeroReportType = false; //是否是零汇报机制
		boolean isColZeroReportType = false; //是否是零汇报机制
		String str_title = "图表";
		if (builder instanceof AbstractStyleReport_3_BuildDataAdapter) {
			str_rowHeaderSorts = ((AbstractStyleReport_3_BuildDataAdapter) builder).getRowHeaderViewSort(); //
			str_colHeaderSorts = ((AbstractStyleReport_3_BuildDataAdapter) builder).getColHeaderViewSort(); //
			isRowZeroReportType = ((AbstractStyleReport_3_BuildDataAdapter) builder).isRowHeaderZeroReportType(); //
			isColZeroReportType = ((AbstractStyleReport_3_BuildDataAdapter) builder).isColHeaderZeroReportType(); //
			str_title = ((AbstractStyleReport_3_BuildDataAdapter) builder).getChartTitle(); //图表标题
		}

		String str_NeedSelectCol = builder.getComputeItemName();
		String ComputeType = builder.getComputeType();
		BillChartVO chartVO = null;
		ReportUtil reportUtil = new ReportUtil();
		String str_ReportType = "";
		if (ComputeType.equals(StyleReport_3_BuildDataIFC.SELECT)) { //就是在业务数据构造器中已经做了统计，比如count,sum等
			if (hvs != null) {
				if (hvs.length == 0) {
					chartVO = new BillChartVO();
					chartVO.setXSerial(new String[] { "没有数据!" });
					chartVO.setYSerial(new String[] { "没有数据!" });
					chartVO.setDataVO(new BillChartItemVO[][] { { new BillChartItemVO(0) } });
				} else {
					chartVO = reportUtil.convertHashVOToChartVO(hvs, str_RowHeadName, str_ColHeadName, str_NeedSelectCol, str_rowHeaderSorts, str_colHeaderSorts, isRowZeroReportType, isColZeroReportType); //
					chartVO.setXHeadName(str_ColHeadName);
					chartVO.setYHeadName(str_RowHeadName);
				}
			}
		} else {
			str_ReportType = ComputeType + "(" + str_NeedSelectCol + ") " + str_NeedSelectCol;// 对某个字段进行汇总,比如sum(金额)
			String str_NeedGroupByField = str_RowHeadName + "," + str_ColHeadName;
			HashVO[] newHvs = reportUtil.groupHashVOs(hvs, str_ReportType, str_NeedGroupByField);
			chartVO = reportUtil.convertHashVOToChartVO(newHvs, str_RowHeadName, str_ColHeadName, str_NeedSelectCol, str_rowHeaderSorts, str_colHeaderSorts, isRowZeroReportType, isColZeroReportType); //
			if (chartVO == null) //wanglei于2010-09-08修改
			{
				chartVO = new BillChartVO();
				chartVO.setXSerial(new String[] { "没有数据!" });
				chartVO.setYSerial(new String[] { "没有数据!" });
				chartVO.setDataVO(new BillChartItemVO[][] { { new BillChartItemVO(0) } });
			} else {
				chartVO.setXHeadName(str_ColHeadName);
				chartVO.setYHeadName(str_RowHeadName);
			}
		}
		chartVO.setTitle(str_title); //
		return chartVO;
	}

}
