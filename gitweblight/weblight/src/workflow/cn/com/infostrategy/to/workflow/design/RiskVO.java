package cn.com.infostrategy.to.workflow.design;

import java.awt.Color;
import java.io.Serializable;

public class RiskVO implements Serializable {

	private static final long serialVersionUID = -7421931434767413140L;
	public static Color color1 = Color.RED; //
	public static Color color2 = Color.YELLOW; //
	public static Color color3 = Color.GREEN; // 
	public static Color color4 = Color.YELLOW; // zqx 2012_05_04
	// ��Ϊ���Ƶ����ɫ���������ε���ɫ

	// �Ϲ���յ�ͳ��
	private int level1RiskCount = 0; // ����
	private int level2RiskCount = 0; // �е�,�ϴ�
	private int level3RiskCount = 0; // ��С,��С
	
	// kri_value
	private String red ; // ��
	private String yellow; // ��
	private String green; // ��
	
	private String r_value ; // ��
	private String y_value; // ��
	private String g_value; // ��
	private String infoalert = null;
	public String getInfoalert() {
		return infoalert;
	}
	
	public String getRed() {
		return red;
	}

	public String getR_value() {
		return r_value;
	}

	public void setR_value(String rValue) {
		r_value = rValue;
	}

	public String getY_value() {
		return y_value;
	}

	public void setY_value(String yValue) {
		y_value = yValue;
	}

	public String getG_value() {
		return g_value;
	}

	public void setG_value(String gValue) {
		g_value = gValue;
	}

	public void setRed(String red) {
		this.red = red;
	}

	public String getYellow() {
		return yellow;
	}

	public void setYellow(String yellow) {
		this.yellow = yellow;
	}

	public String getGreen() {
		return green;
	}

	public void setGreen(String green) {
		this.green = green;
	}


	// �������յ�ͳ��
	private int level1RiskCount1 = 0; // ����
	private int level2RiskCount1 = 0; // �е�,�ϴ�
	private int level3RiskCount1 = 0; // ��С,��С

	// ���Ƶ�
	private boolean isHaveControlWarn = false; // zqx 2012_05_04 ���Ƶ�����

	// ��sunfujun/20120507/GW����һ����Ҫ��һ��BOMͼ��չ�����3����״��ͬ��ɫ�Ĺ���,Ҳ���Ǳ������й���ͬʱ�ṩ����������ɫ����״�Ĺ��ܡ�
	private int shape1 = 0;
	private int shape2 = 0;
	private int shape3 = 0;
	private Color color1_ = Color.RED; // ����ǰ�澲̬�ı���û��ʹ�ù��������ֶ�
	private Color color2_ = Color.YELLOW;
	private Color color3_ = Color.GREEN;

	public Color getColor1_() {
		return color1_;
	}

	/**
	 * ������ɫ,֧��bomͼ������ͼ
	 * 
	 * @param color1_
	 */
	public void setColor1_(Color color1_) {
		this.color1_ = color1_;
	}

	public Color getColor2_() {
		return color2_;
	}
	/*��Ϣ��ʾ�����Լ��*/
	public void setInfoalert(String infoalert) {
		this.infoalert = infoalert;
	}
	/**
	 * ������ɫ,֧��bomͼ������ͼ
	 * 
	 * @param color2_
	 */
	public void setColor2_(Color color2_) {
		this.color2_ = color2_;
	}

	public Color getColor3_() {
		return color3_;
	}

	/**
	 * ������ɫ,֧��bomͼ������ͼ
	 * 
	 * @param color3_
	 */
	public void setColor3_(Color color3_) {
		this.color3_ = color3_;
	}

	public int getShape1() {
		return shape1;
	}

	/**
	 * Ŀǰ�ṩ5����״ Ĭ��0����Բ�� 1������� 2����Բ�Ǿ��� 3���������� 4�������� ֧��bomͼ������ͼ
	 * 
	 * @param shape1
	 */
	public void setShape1(int shape1) {
		this.shape1 = shape1;
	}

	public int getShape2() {
		return shape2;
	}

	/**
	 * Ŀǰ�ṩ5����״ Ĭ��0����Բ�� 1������� 2����Բ�Ǿ��� 3���������� 4��������
	 * 
	 * @param shape1
	 */
	public void setShape2(int shape2) {
		this.shape2 = shape2;
	}

	public int getShape3() {
		return shape3;
	}

	/**
	 * Ŀǰ�ṩ5����״ Ĭ��0����Բ�� 1������� 2����Բ�Ǿ��� 3���������� 4��������
	 * 
	 * @param shape1
	 */
	public void setShape3(int shape3) {
		this.shape3 = shape3;
	}

	public RiskVO() {
	}

	public RiskVO(int _count1, int _count2, int _count3) {
		level1RiskCount = _count1;
		level2RiskCount = _count2;
		level3RiskCount = _count3;
	}

	public int getLevel1RiskCount() {
		return level1RiskCount;
	}

	public void setLevel1RiskCount(int level1RiskCount) {
		this.level1RiskCount = level1RiskCount;
	}

	public int getLevel2RiskCount() {
		return level2RiskCount;
	}

	public void setLevel2RiskCount(int level2RiskCount) {
		this.level2RiskCount = level2RiskCount;
	}

	public int getLevel3RiskCount() {
		return level3RiskCount;
	}

	public void setLevel3RiskCount(int level3RiskCount) {
		this.level3RiskCount = level3RiskCount;
	}

	@Override
	public String toString() {
		return "" + level1RiskCount + "," + level2RiskCount + "," + level3RiskCount; //
	}

	public int getLevel1RiskCount1() {
		return level1RiskCount1;
	}

	public void setLevel1RiskCount1(int level1RiskCount1) {
		this.level1RiskCount1 = level1RiskCount1;
	}

	public int getLevel2RiskCount1() {
		return level2RiskCount1;
	}

	public void setLevel2RiskCount1(int level2RiskCount1) {
		this.level2RiskCount1 = level2RiskCount1;
	}

	public int getLevel3RiskCount1() {
		return level3RiskCount1;
	}

	public void setLevel3RiskCount1(int level3RiskCount1) {
		this.level3RiskCount1 = level3RiskCount1;
	}

	public boolean isHaveControlPoint() {
		return isHaveControlWarn;
	}

	public void setHaveControlPoint(boolean isHaveControlPoint) {
		this.isHaveControlWarn = isHaveControlPoint;
	}

}
