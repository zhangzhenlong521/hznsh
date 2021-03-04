package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * JPanel��UI,ʵ�ֽ���Ч��
 * @author xch
 *
 */
public class WLTPanelUI extends BasicPanelUI {

	public static final int HORIZONTAL_LEFT_TO_RIGHT = BackGroundDrawingUtil.HORIZONTAL_LEFT_TO_RIGHT;
	public static final int HORIZONTAL_FROM_MIDDLE = BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE;
	public static final int INCLINE_NW_TO_SE = BackGroundDrawingUtil.INCLINE_NW_TO_SE;
	public static final int INCLINE_SE_TO_NW = BackGroundDrawingUtil.INCLINE_SE_TO_NW;

	private int directType = 0;
	private boolean isDynamicChange = true; //�Ƿ�̬�仯,��������������˸�Ч��,����ҳ������߶ȱ仯,��̬�ı佥��Ч��!! Ĭ���Ǳ仯��!
	//private Color colorBegin = null;
	private Color colorEnd = null;
	private boolean isViewRect = true; //

	private BackGroundDrawingUtil drawutil = new BackGroundDrawingUtil(); //

	public WLTPanelUI() {
		this(WLTPanelUI.INCLINE_SE_TO_NW, LookAndFeel.defaultShadeColor1, true);
	}

	public WLTPanelUI(int _direct) {
		this(_direct, Color.WHITE, false); //
	}

	public WLTPanelUI(int _direct, boolean _isDynamicChange) {
		this(_direct, Color.WHITE, _isDynamicChange); //
	}

	/**
	 * @param int   _direct   ָ�����Ƶķ���
	 * @param Color _colorEnd ָ����ɫ�ı仯Ŀ�꣨��ԭ���ı���ɫ���䵽����ָ������ɫ��
	 */
	public WLTPanelUI(int _direct, Color _colorEnd, boolean _isDynamicChange) {
		this(_direct, _colorEnd, _isDynamicChange, true); //
	}

	public WLTPanelUI(int _direct, Color _colorEnd, boolean _isDynamicChange, boolean _isViewRect) {
		super();
		this.directType = _direct;
		this.colorEnd = _colorEnd;
		this.isDynamicChange = _isDynamicChange; //�Ƿ�̬�仯Ч��
		this.isViewRect = _isViewRect; //
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Color colorBegin = c.getBackground(); //
		Rectangle rect = null; //
		if (isViewRect) {
			rect = c.getVisibleRect(); //һ��Ҫ����ʾ����!!Ч���ź�!!!
		} else {
			rect = c.getBounds(); //
		}
		if (isDynamicChange) { //�����Ҫ��̬��ʾЧ��!!!
			int panelWidth = (int) rect.getWidth(); //
			int panelHeight = (int) rect.getHeight(); //
			if (panelHeight > panelWidth * 1.2) {
				directType = BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT; //
			} else {
				if (panelWidth > panelHeight * 1.2) {
					directType = BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE; //
				} else {
					directType = BackGroundDrawingUtil.INCLINE_NW_TO_SE;
				}
			}
		}
		drawutil.draw(directType, g, rect, colorBegin, colorEnd); //
	}

}
