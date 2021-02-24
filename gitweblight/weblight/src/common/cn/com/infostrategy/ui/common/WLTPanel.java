package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * ����ʵ�ֽ���Ч����Panel,ͬʱ����ʵ��͸��!�����ֶ���!
 * 
 * @author xch
 *
 */
public class WLTPanel extends JPanel {
	public static final int SINGLE_COLOR_UNCHANGE = BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE;
	public static final int HORIZONTAL_LEFT_TO_RIGHT = BackGroundDrawingUtil.HORIZONTAL_LEFT_TO_RIGHT;
	public static final int HORIZONTAL_RIGHT_TO_LEFT = BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT;
	public static final int VERTICAL_TOP_TO_BOTTOM = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM; //���ϵ���
	public static final int VERTICAL_TOP_TO_BOTTOM2 = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM2; //���ϵ���
	public static final int VERTICAL_BOTTOM_TO_TOP = BackGroundDrawingUtil.VERTICAL_BOTTOM_TO_TOP; //��������
	public static final int VERTICAL_LIGHT = BackGroundDrawingUtil.VERTICAL_LIGHT; //�������Ϸ���!!
	public static final int VERTICAL_LIGHT2 = BackGroundDrawingUtil.VERTICAL_LIGHT2; //�������Ϸ���!!
	public static final int VERTICAL_OUTSIDE_TO_MIDDLE = BackGroundDrawingUtil.VERTICAL_OUTSIDE_TO_MIDDLE; //�������м��!!

	public static final int INCLINE_NW_TO_SE = BackGroundDrawingUtil.INCLINE_NW_TO_SE;
	public static final int INCLINE_NE_TO_SW = BackGroundDrawingUtil.INCLINE_NE_TO_SW;
	public static final int INCLINE_SE_TO_NW = BackGroundDrawingUtil.INCLINE_SE_TO_NW;
	public static final int INCLINE_SW_TO_NE = BackGroundDrawingUtil.INCLINE_SW_TO_NE;
	public static final int HORIZONTAL_FROM_MIDDLE = BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE;

	private static final long serialVersionUID = 1L;
	private boolean isCrystal = false; //�Ƿ�͸��,Ĭ���ǲ�͸��!!setOpaque
	private int shadowType = 0; //��ɫ��0�� ������ʽ,1,2,3,4,5,6������μ�class cn.com.infostrategy.ui.common.BackGroundDrawingUtil
	private Color colorTo = Color.WHITE; //����н�����Ⱦ����ôĬ�Ϸ�ʽ�Ǵ���������ɫ��colorTo����ָ������ɫ�仯

	private boolean isDynamicChange = true; //�Ƿ�̬�仯,��������������˸�Ч��,����ҳ������߶ȱ仯,��̬�ı佥��Ч��!! Ĭ���Ǳ仯��!
	private boolean isViewRect = true; //

	/**
	 * ͸����壬�޲���
	 */
	public WLTPanel() {
		this.isCrystal = true; //�Ƿ�͸��=Y
		this.setOpaque(!isCrystal);
	}

	/**
	 * ͸�����,�в���
	 * @param layout  ��岼������
	 */
	public WLTPanel(LayoutManager layout) {
		super(layout);
		this.isCrystal = true; //
		this.setOpaque(!isCrystal); //
	}

	/**
	 * ��͸����壨�޲��֣�������ɫ������Ⱦ
	 * @param _shadowType  ��ɫ��������
	 */
	public WLTPanel(int _shadowType) {
		super();
		this.shadowType = _shadowType;
		initialize();
	}

	/**
	 * ��͸����壨�޲��֣�������ɫ������Ⱦ
	 * @param _shadowType  ��ɫ��������
	 * @param _bgColor      ����ɫ
	 */
	public WLTPanel(int _shadowType, Color _bgColor) {
		super();
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		initialize();
	}

	public WLTPanel(int _shadowType, Color _bgColor, boolean _isDynamicChange) {
		super();
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.isDynamicChange = _isDynamicChange; //
		initialize();
	}

	/**
	 * ��͸����壨�в��֣�������ɫ������Ⱦ
	 * @param _shadowType  ��ɫ��������
	 * @param layout       ��岼������
	 */
	public WLTPanel(int _shadowType, LayoutManager layout) {
		super(layout);
		this.shadowType = _shadowType;
		initialize();
	}

	public WLTPanel(int _shadowType, LayoutManager layout, boolean _isDynamicChange) {
		super(layout);
		this.shadowType = _shadowType;
		this.isDynamicChange = _isDynamicChange; //
		initialize();
	}

	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor, boolean _isDynamicChange) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.isDynamicChange = _isDynamicChange; //
		initialize();
	}

	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor, boolean _isDynamicChange, boolean _isViewRect) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.isDynamicChange = _isDynamicChange; //
		this.isViewRect = _isViewRect; //
		initialize();
	}

	/**
	 * ��͸����壨�в��֣�������ɫ������Ⱦ
	 * @param _shadowType   ��ɫ��������
	 * @param layout        ��岼������
	 * @param _bgColor      ����ɫ
	 */
	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		initialize();
	}

	/**
	 * ��͸����壨�в��֣�������ɫ������Ⱦ����_bgColor���䵽_colorTo
	 * @param _shadowType   ��ɫ��������
	 * @param layout        ��岼������
	 * @param _bgColor      ����ɫ
	 * @param _colorTo      ��Ⱦ��ɫ����ֵ
	 */
	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor, Color _colorTo) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.colorTo = _colorTo;
		initialize();
	}

	private void initialize() {
		this.setOpaque(!isCrystal);
		//assert shadowType >= BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE && shadowType <= BackGroundDrawingUtil.INCLINE_SW_TO_NE : "����ֵԽ�磡";
		if (!isCrystal && shadowType > 0) {//�������͸����岢�������˽������ͣ���ô����UI
			this.setUI(new WLTPanelUI(shadowType, colorTo, isDynamicChange, isViewRect));
		}
	}

	public static JPanel createDefaultPanel() {
		WLTPanel panel = new WLTPanel(WLTPanel.INCLINE_SE_TO_NW, LookAndFeel.defaultShadeColor1); //
		return panel; //
	}

	public static JPanel createDefaultPanel(LayoutManager _layOut) {
		WLTPanel panel = new WLTPanel(WLTPanel.INCLINE_SE_TO_NW, _layOut, LookAndFeel.defaultShadeColor1); //
		return panel; //
	}

	public static JPanel createDefaultPanel(LayoutManager _layOut, int _shadowType) {
		WLTPanel panel = new WLTPanel(_shadowType, _layOut, LookAndFeel.defaultShadeColor1, false); //
		return panel; //
	}

	@Override
	public void setBackground(Color bg) {
		if (isCrystal) { //�����͸�����������������ñ���ɫ
			return;
		}
		super.setBackground(bg);
	}

}
