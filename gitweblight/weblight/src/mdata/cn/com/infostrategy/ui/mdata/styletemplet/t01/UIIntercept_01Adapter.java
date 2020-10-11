package cn.com.infostrategy.ui.mdata.styletemplet.t01;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 风格模板1的UI端拦截器的适配器,实际的拦截器必须继承它
 * 强烈建议不要直接实现接口,因为接口以后会随时升级与变动或增加新的方法,如果直接继承于接口,则会出现“牵一发而动全身”的状况，很不好！！！
 * @author xch
 *
 */
public class UIIntercept_01Adapter implements IUIIntercept_01 {

	//后续初始化..
	public void afterInitialize(AbstractStyleWorkPanel_01 _styleWorkPanel) throws Exception {
	}

	public void actionBeforeDelete(BillListPanel panel, int _delerow) throws Exception {
	}

	public void actionBeforeInsert(BillListPanel panel, int _newrow) throws Exception {
	}

	public void actionBeforeUpdate(BillListPanel panel, int _updatedrow, String _itemkey) throws Exception {
	}

	public void dealAfterCommit(BillListPanel panel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception {
	}

	public void dealBeforeCommit(BillListPanel panel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception {
	}

}
