package cn.com.infostrategy.ui.workflow.design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * �׶εı���,�������ͻ������Ҫ����Visio�ڽ׶ε������һ������!����֮Ϊ�׶εı���Cell
 * �������Cell��ʵ����һ����ͨ�Ļ���! ֻ�������ض�����Ⱦ������!! 
 * ��ʵ��ǰ�Ĳ�������׶���Ҳ����ֱ��ʹ��һ����׼�Ļ���,Ȼ��ͨ��ָ�����ض�����Ⱦ�����㶨��!!
 * ������ʹ��������׼����ƴ��һ��group��ʵ�ֵ�! �ر��ǽ׶λ���ʹ�����������ڼ�һ�����߸�ģ�
 * �������Ƿ������Ҫ������һ����׼���ڼ��ض���Ⱦ����,���д���������....
 * �����ǵ��ں���ƹ���Ŀ�л���Ҫ�ڲ��ž����м����λ,�������ָ��˸��µ���,Ȼ�����϶�ʱ�����Զ��϶���λ����! 
 * ���Դ�����ǶȽ�,���������ɱ�׼���ڼ��ض�����Ⱦ�����Ǻ��б�Ҫ��,��Ϊ����֤�˲�����һ������������!
 * @author Administrator
 *
 */
public class CellView_StationBackCell extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;
	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	private TBUtil tbUtil = new TBUtil(); //
	private boolean isCenter = tbUtil.getSysOptionBooleanValue("����ͼ�н׶������Ƿ��������", false);//��Ȼ���˾��ý׶�����������бȽϺÿ�����ũ�пͻ���ϰ����ǰ�ĸ�ʽ���־���˵�øĻ��������������/2012-02-29��

	public CellView_StationBackCell() {
		super();
	}

	public CellView_StationBackCell(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public class JGraphEllipseRenderer extends VertexRenderer {
		private static final long serialVersionUID = -2967962084609071057L;

		public JGraphEllipseRenderer() {
		}

		public void paint(Graphics ggg) {
			DefaultGraphCell cell = (DefaultGraphCell) getCell(); //����..
			WorkFlowDesignWPanel wfPanel = (WorkFlowDesignWPanel) cell.getAttributes().get("�������༭��"); //
			String[][] str_allStations = wfPanel.getStationCellsInfo(); ////

			Graphics2D g2 = (Graphics2D) ggg;
			Dimension d = getSize(); //ȡ�ô�С
			setOpaque(false); //͸��

			//���߿�
			g2.setColor(GraphConstants.getBackground(cell.getAttributes())); //
			g2.fillRect(0, 0, d.width - 1, d.height - 5); //

			g2.setColor(Color.BLACK); //
			g2.drawRect(0, 0, d.width - 1, d.height - 5); //

			//д��,��ʵ��Ҫȡ�ø������ڵ�Yλ��,�߶�,����,Ȼ���ڶ�Ӧ��λ��д��!!
			if (str_allStations != null) { //�����ֵ!!!

				if (isCenter) {
					for (int i = 0; i < str_allStations.length; i++) { //���������׶��ڶ�Ӧ��λ��д��!!!
						String str_text = str_allStations[i][0]; //
						String[] str_textItems = tbUtil.split(str_text, "\n"); //
						if (str_textItems != null) {
							int s4_fontsize = Integer.parseInt(str_allStations[i][4]);
							int onefontsize = s4_fontsize + 4;//һ���ֵĴ�С�����������С���ּ��
							int s1_y = Integer.parseInt(str_allStations[i][1]);
							isCenter = true;
							if (s1_y < 100) {
								isCenter = false;//ƽ̨���ÿɾ��У����ý׶�̫�����������ˣ�Ĭ�ϴ�������д��
							} else {
								int s2_height2 = s1_y - 80;//�����ڽ׶εļ��
								if (i != 0) {
									s2_height2 = s1_y - Integer.parseInt(str_allStations[i - 1][1]);//���������׶εĸ߶�
								}
								if ((onefontsize) * str_textItems.length > s2_height2) {//����ֵĸ߶ȴ��ڽ׶θ߶ȣ���Ĭ������д�֣�����������ʾ
									isCenter = false;
								} else {
									s1_y = s1_y - s2_height2 / 2 - (onefontsize) * str_textItems.length / 2 + onefontsize; //�׶��붥�˸߶ȼ�ȥ�����ڽ׶ε�һ�룬�ټ�ȥ�ָ߶ȵ�һ�룬�����¿�ʼд��һ���ֵ�λ��
								}
							}
							for (int j = 0; j < str_textItems.length; j++) { //
								int li_itemwidth = tbUtil.getStrUnicodeLength(str_textItems[j]) * 6; //
								int li_x = 5 + ((42 - li_itemwidth) / 2);
								if (li_x < 3) {
									li_x = 3;
								}
								g2.setFont(new Font(str_allStations[i][3], Font.PLAIN, s4_fontsize)); //
								String[] str_color = str_allStations[i][5].split(",");
								g2.setColor(new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));// ����ǰ����ɫ.
								if (isCenter) {//�Ƿ�����������ʾ,����������ಢ�ҿ��ܳ������׶εļ�࣬���������ľ�����ʾ������Ҫ����д��
									g2.drawString(str_textItems[j], li_x, (s1_y) + j * (onefontsize)); //����д!
								} else {
									g2.drawString(str_textItems[j], li_x, (s1_y) - ((str_textItems.length - 1 - j) * (onefontsize))); //����д!
								}
							}
						}
					}
				} else {
					for (int i = 0; i < str_allStations.length; i++) { //���������׶��ڶ�Ӧ��λ��д��!!!
						String str_text = str_allStations[i][0]; //
						int li_y = Integer.parseInt(str_allStations[i][1]); //
						String[] str_textItems = tbUtil.split(str_text, "\n"); //
						if (str_textItems != null) {
							for (int j = 0; j < str_textItems.length; j++) { //
								int li_itemwidth = tbUtil.getStrUnicodeLength(str_textItems[j]) * 6; //
								int li_x = 5 + ((42 - li_itemwidth) / 2);
								if (li_x < 3) {
									li_x = 3;
								}
								g2.setFont(new Font(str_allStations[i][3], Font.PLAIN, Integer.parseInt(str_allStations[i][4]))); //
								String[] str_color = str_allStations[i][5].split(",");
								g2.setColor(new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));// ����ǰ����ɫ.
								g2.drawString(str_textItems[j], li_x, (li_y - 18) - ((str_textItems.length - 1 - j) * 20)); //����д!
							}
						}
					}
				}
			}

		}
	}
}