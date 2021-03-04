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
 * 阶段的背景,即无数客户提出的要仿照Visio在阶段的外面包一个框子!即称之为阶段的背景Cell
 * 这个背景Cell其实就是一个普通的环节! 只不过有特定的宣染器而已!! 
 * 其实以前的部门组与阶段组也可以直接使用一个标准的环节,然后通过指定的特定的渲染器来搞定的!!
 * 现在是使用两个标准环节拼成一个group来实现的! 特别是阶段还是使用了两个环节加一个连线搞的！
 * 但到底是否真的需要将组变成一个标准环节加特定渲染器搞,还有待继续考虑....
 * 但考虑到在航天科工项目中还需要在部门矩阵中加入岗位,结果李春娟又搞了个新的组,然后部门拖动时不能自动拖动岗位联动! 
 * 所以从这个角度讲,将部门组变成标准环节加特定的渲染器还是很有必要的,因为他保证了部门是一个真正的整体!
 * @author Administrator
 *
 */
public class CellView_StationBackCell extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;
	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	private TBUtil tbUtil = new TBUtil(); //
	private boolean isCenter = tbUtil.getSysOptionBooleanValue("流程图中阶段名称是否纵向居中", false);//虽然本人觉得阶段名称纵向居中比较好看，但农行客户用习惯以前的格式，又纠结说让改回来，哎！【李春娟/2012-02-29】

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
			DefaultGraphCell cell = (DefaultGraphCell) getCell(); //环节..
			WorkFlowDesignWPanel wfPanel = (WorkFlowDesignWPanel) cell.getAttributes().get("工作流编辑器"); //
			String[][] str_allStations = wfPanel.getStationCellsInfo(); ////

			Graphics2D g2 = (Graphics2D) ggg;
			Dimension d = getSize(); //取得大小
			setOpaque(false); //透明

			//画边框
			g2.setColor(GraphConstants.getBackground(cell.getAttributes())); //
			g2.fillRect(0, 0, d.width - 1, d.height - 5); //

			g2.setColor(Color.BLACK); //
			g2.drawRect(0, 0, d.width - 1, d.height - 5); //

			//写字,其实是要取得各个环节的Y位置,高度,字名,然后在对应的位置写字!!
			if (str_allStations != null) { //如果有值!!!

				if (isCenter) {
					for (int i = 0; i < str_allStations.length; i++) { //遍历各个阶段在对应的位置写字!!!
						String str_text = str_allStations[i][0]; //
						String[] str_textItems = tbUtil.split(str_text, "\n"); //
						if (str_textItems != null) {
							int s4_fontsize = Integer.parseInt(str_allStations[i][4]);
							int onefontsize = s4_fontsize + 4;//一个字的大小，包括字体大小和字间距
							int s1_y = Integer.parseInt(str_allStations[i][1]);
							isCenter = true;
							if (s1_y < 100) {
								isCenter = false;//平台配置可居中，但该阶段太靠近部门组了，默认从下向上写字
							} else {
								int s2_height2 = s1_y - 80;//两相邻阶段的间隔
								if (i != 0) {
									s2_height2 = s1_y - Integer.parseInt(str_allStations[i - 1][1]);//相邻两个阶段的高度
								}
								if ((onefontsize) * str_textItems.length > s2_height2) {//如果字的高度大于阶段高度，则默认向上写字，即不居中显示
									isCenter = false;
								} else {
									s1_y = s1_y - s2_height2 / 2 - (onefontsize) * str_textItems.length / 2 + onefontsize; //阶段离顶端高度减去两相邻阶段的一半，再减去字高度的一半，即向下开始写第一个字的位置
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
								g2.setColor(new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));// 设置前景颜色.
								if (isCenter) {//是否真正居中显示,如果字数过多并且可能超过两阶段的间距，则不是真正的居中显示，而需要向上写字
									g2.drawString(str_textItems[j], li_x, (s1_y) + j * (onefontsize)); //向下写!
								} else {
									g2.drawString(str_textItems[j], li_x, (s1_y) - ((str_textItems.length - 1 - j) * (onefontsize))); //向上写!
								}
							}
						}
					}
				} else {
					for (int i = 0; i < str_allStations.length; i++) { //遍历各个阶段在对应的位置写字!!!
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
								g2.setColor(new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));// 设置前景颜色.
								g2.drawString(str_textItems[j], li_x, (li_y - 18) - ((str_textItems.length - 1 - j) * 20)); //倒着写!
							}
						}
					}
				}
			}

		}
	}
}