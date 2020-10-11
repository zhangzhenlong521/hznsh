package cn.com.infostrategy.bs.mdata.styletemplet;

import java.util.HashMap;

import cn.com.infostrategy.bs.mdata.styletemplet.t01.DMO_01;
import cn.com.infostrategy.bs.mdata.styletemplet.t02.DMO_02;
import cn.com.infostrategy.bs.mdata.styletemplet.t03.DMO_03;
import cn.com.infostrategy.bs.mdata.styletemplet.t04.DMO_04;
import cn.com.infostrategy.bs.mdata.styletemplet.t05.DMO_05;
import cn.com.infostrategy.bs.mdata.styletemplet.t06.DMO_06;
import cn.com.infostrategy.bs.mdata.styletemplet.t07.DMO_07;
import cn.com.infostrategy.bs.mdata.styletemplet.t08.DMO_08;
import cn.com.infostrategy.bs.mdata.styletemplet.t09.DMO_09;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.styletemplet.StyleTempletServiceIfc;

public class StyleTempletServiceImpl implements StyleTempletServiceIfc {

	//风格模板1
	public HashMap style01_dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertVOs, BillVO[] _deleteVOs, BillVO[] _updateVOs) throws Exception {
		return new DMO_01().dealCommit(_dsName, _bsInterceptName, _insertVOs, _deleteVOs, _updateVOs); //
	}

	//风格模板2
	public BillVO style02_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_02().dealInsert(_dsName, _bsInterceptName, _vo);
	}

	public void style02_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		new DMO_02().dealDelete(_dsName, _bsInterceptName, _vo);
	}

	public BillVO style02_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_02().dealUpdate(_dsName, _bsInterceptName, _vo);
	}

	//
	//风格模板3
	public BillVO style03_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_03().dealInsert(_dsName, _bsInterceptName, _vo);
	}

	public void style03_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		new DMO_03().dealDelete(_dsName, _bsInterceptName, _vo);
	}

	//模板3的级联删除
	public void style03_dealCascadeDelete(String _dsName, String _bsInterceptName, HashVO[] _deleteobj, String tablename, String field) throws Exception {
		new DMO_03().dealDelete(_dsName, _bsInterceptName, _deleteobj, tablename, field);
	}

	public BillVO style03_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_03().dealUpdate(_dsName, _bsInterceptName, _vo);
	}

	//
	//风格模板4
	public HashMap style04_dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertVOs, BillVO[] _deleteVOs, BillVO[] _updateVOs) throws Exception {
		return new DMO_04().dealCommit(_dsName, _bsInterceptName, _insertVOs, _deleteVOs, _updateVOs); //
	}

	//
	//风格模板5
	public BillVO style05_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_05().dealInsert(_dsName, _bsInterceptName, _vo);
	}

	public void style05_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		new DMO_05().dealDelete(_dsName, _bsInterceptName, _vo);
	}

	public BillVO style05_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_05().dealUpdate(_dsName, _bsInterceptName, _vo);
	}

	//
	//风格模板6
	public HashMap style06_dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertVOs, BillVO[] _deleteVOs, BillVO[] _updateVOs) throws Exception {
		return new DMO_06().dealCommit(_dsName, _bsInterceptName, _insertVOs, _deleteVOs, _updateVOs); //
	}

	//
	//风格模板7
	public BillVO style07_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_07().dealInsert(_dsName, _bsInterceptName, _vo);
	}

	public void style07_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		new DMO_07().dealDelete(_dsName, _bsInterceptName, _vo);
	}

	public BillVO style07_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception {
		return new DMO_07().dealUpdate(_dsName, _bsInterceptName, _vo);
	}

	//
	//风格模板8
	public AggBillVO style08_dealInsert(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		return new DMO_08().dealInsert(_dsName, _bsInterceptName, _aggVO); //
	}

	public void style08_dealDelete(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		new DMO_08().dealDelete(_dsName, _bsInterceptName, _aggVO); //
	}

	public AggBillVO style08_dealUpdate(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		return new DMO_08().dealUpdate(_dsName, _bsInterceptName, _aggVO); //
	}

	//
	//风格模板9
	public AggBillVO style09_dealInsert(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		return new DMO_09().dealInsert(_dsName, _bsInterceptName, _aggVO);
	}

	public void style09_dealDelete(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		new DMO_09().dealDelete(_dsName, _bsInterceptName, _aggVO);
	}

	public AggBillVO style09_dealUpdate(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		return new DMO_09().dealUpdate(_dsName, _bsInterceptName, _aggVO);
	}

}
