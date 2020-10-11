package cn.com.infostrategy.to.report;

import java.io.Serializable;

public class BillChartVO implements Serializable {

	private static final long serialVersionUID = 8174302667474220257L;
	private String title = "ͼ��"; //
	private String[] xSerial = null; //
	private String[] ySerial = null; //
	private String yHeadName = null; //
	private String xHeadName = null; //
	private BillChartItemVO[][] dataVO = null; //��ǰֻ��double[][],�ڽ��кϼ�ʱ������ƽ����ʱ��û��Ū,���뽫sum��countֵ�����ȥ,��BillChartItemVO�Ĺ��췽��ͬʱ������ǰ�ķ���

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getXSerial() {
		return xSerial;
	}

	public void setXSerial(String[] serial) {
		xSerial = serial;
	}

	public String[] getYSerial() {
		return ySerial;
	}

	public void setYSerial(String[] serial) {
		ySerial = serial;
	}

	public BillChartItemVO[][] getDataVO() {
		return dataVO;
	}

	public void setDataVO(BillChartItemVO[][] dataVO) {
		this.dataVO = dataVO;
	}

	public BillChartVO convertXY() {
		String[] str_x = new String[this.ySerial.length];
		String[] str_y = new String[this.xSerial.length];

		for (int i = 0; i < str_x.length; i++) {
			str_x[i] = ySerial[i];
		}

		for (int i = 0; i < str_y.length; i++) {
			str_y[i] = xSerial[i];
		}

		BillChartVO convertVO = new BillChartVO(); //
		convertVO.setXSerial(str_x);
		convertVO.setYSerial(str_y);
		if (dataVO != null) {
			BillChartItemVO[][] ld_data = new BillChartItemVO[dataVO[0].length][dataVO.length]; //
			for (int i = 0; i < dataVO.length; i++) {
				for (int j = 0; j < dataVO[i].length; j++) {
					ld_data[j][i] = dataVO[i][j]; //
				}
			}
			convertVO.setDataVO(ld_data); //
		}

		return convertVO;
	}

	public String getYHeadName() {
		return yHeadName;
	}

	public void setYHeadName(String headName) {
		yHeadName = headName;
	}

	public String getXHeadName() {
		return xHeadName;
	}

	public void setXHeadName(String headName) {
		xHeadName = headName;
	}

}
