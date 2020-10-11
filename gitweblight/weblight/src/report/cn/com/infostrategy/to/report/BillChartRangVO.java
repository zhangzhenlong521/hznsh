package cn.com.infostrategy.to.report;

import java.awt.Color;
import java.io.Serializable;

/**
 * ͼ����ɫ�� �����/2013-06-14��
 */
public class BillChartRangVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private double start_border = 0; //��ʼֵ
	private double end_border = 0; //����ֵ
	private String rang_text = ""; //�ı�
	private Color color = null; //��ɫ
	
	public BillChartRangVO(double start, double end, String rang, Color color) {
		this.start_border = start;
		this.end_border = end;
		this.rang_text = rang;
		this.color = color;
	}
	
	public double getStart_border() {
		return start_border;
	}
	public void setStart_border(double start_border) {
		this.start_border = start_border;
	}
	public double getEnd_border() {
		return end_border;
	}
	public void setEnd_border(double end_border) {
		this.end_border = end_border;
	}
	public String getRang_text() {
		return rang_text;
	}
	public void setRang_text(String rang_text) {
		this.rang_text = rang_text;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
