package cn.com.infostrategy.ui.common;

import java.awt.Color;

import cn.com.infostrategy.ui.mdata.hmui.I_ButtonUI;

public class WLTButtonUI extends I_ButtonUI {

	public WLTButtonUI() {
		this(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM); //Ĭ����
	}

	public WLTButtonUI(int _directType) {
		this(_directType, Color.WHITE);
	}

	/**
	 * @param int   _direct   ָ�����Ƶķ���
	 * @param Color _colorEnd ָ����ɫ�ı仯Ŀ�꣨��ԭ���ı���ɫ���䵽����ָ������ɫ��
	 */
	public WLTButtonUI(int _directType, Color _colorEnd) {
		super(_directType, _colorEnd);
	}

}
