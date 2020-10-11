package cn.com.infostrategy.to.workflow.design;

import java.awt.Color;
import java.io.Serializable;

public class RiskVO implements Serializable {

	private static final long serialVersionUID = -7421931434767413140L;
	public static Color color1 = Color.RED; //
	public static Color color2 = Color.YELLOW; //
	public static Color color3 = Color.GREEN; // 
	public static Color color4 = Color.YELLOW; // zqx 2012_05_04
	// 作为控制点的颜色，倒三角形的颜色

	// 合规风险点统计
	private int level1RiskCount = 0; // 灾难
	private int level2RiskCount = 0; // 中等,较大
	private int level3RiskCount = 0; // 极小,较小
	
	// kri_value
	private String red ; // 红
	private String yellow; // 黄
	private String green; // 绿
	
	private String r_value ; // 红
	private String y_value; // 黄
	private String g_value; // 绿
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


	// 操作风险点统计
	private int level1RiskCount1 = 0; // 灾难
	private int level2RiskCount1 = 0; // 中等,较大
	private int level3RiskCount1 = 0; // 极小,较小

	// 控制点
	private boolean isHaveControlWarn = false; // zqx 2012_05_04 控制点的情况

	// 【sunfujun/20120507/GW由于一汽需要在一张BOM图上展现最多3种形状不同颜色的功能,也就是保留现有功能同时提供可以设置颜色与形状的功能】
	private int shape1 = 0;
	private int shape2 = 0;
	private int shape3 = 0;
	private Color color1_ = Color.RED; // 由于前面静态的变量没法使用故增加了字段
	private Color color2_ = Color.YELLOW;
	private Color color3_ = Color.GREEN;

	public Color getColor1_() {
		return color1_;
	}

	/**
	 * 设置颜色,支持bom图与流程图
	 * 
	 * @param color1_
	 */
	public void setColor1_(Color color1_) {
		this.color1_ = color1_;
	}

	public Color getColor2_() {
		return color2_;
	}
	/*信息提示，语言简介*/
	public void setInfoalert(String infoalert) {
		this.infoalert = infoalert;
	}
	/**
	 * 设置颜色,支持bom图与流程图
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
	 * 设置颜色,支持bom图与流程图
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
	 * 目前提供5种形状 默认0代表圆形 1代表矩形 2代表圆角矩形 3代表三角形 4代表菱形 支持bom图与流程图
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
	 * 目前提供5种形状 默认0代表圆形 1代表矩形 2代表圆角矩形 3代表三角形 4代表菱形
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
	 * 目前提供5种形状 默认0代表圆形 1代表矩形 2代表圆角矩形 3代表三角形 4代表菱形
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
