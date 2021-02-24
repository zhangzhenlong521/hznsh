/**************************************************************************
 * $RCSfile: GPCellViewFactory.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:18:02 $
 **************************************************************************/
package cn.com.infostrategy.ui.workflow.design;

import java.util.Map;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * A default view factory for a JGraph. This simple factory associate a given
 * cell class to a cell view. This is a javabean, just parameter it correctly in
 * order it meets your requirements (else subclass it or subclass
 * DefaultCellViewFactory). You can also recover the gpConfiguration of that
 * javabean via an XML file via XMLEncoder/XMLDecoder.
 * 
 * @author rvalyi, license of this file: LGPL as stated by the Free Software
 *         Foundation
 */
public class GPCellViewFactory extends DefaultCellViewFactory {

	public static final String VIEW_CLASS_KEY = "viewClassKey";

	public static final void setViewClass(Map map, String viewClass) {
		map.put(VIEW_CLASS_KEY, viewClass);
	}

	protected VertexView createVertexView(Object v) {
		try {
			DefaultGraphCell cell = (DefaultGraphCell) v;
			String viewClass = (String) cell.getAttributes().get(VIEW_CLASS_KEY);
			VertexView view = (VertexView) Class.forName(viewClass).newInstance();
			// VertexView view = (VertexView)
			// Thread.currentThread().getContextClassLoader().loadClass(viewClass).newInstance();
			view.setCell(v);
			return view;
		} catch (Exception ex) {
		}
		return super.createVertexView(v);
	}

	/*
	 * 创建自己重写的线的绘制方法。原有MyEdgeHandle类移到了CellView_edge中。[郝明2012-04-20]
	 * 
	 */
	protected EdgeView createEdgeView(Object cell) { // Override Superclass
		// Method to Return
		// Custom EdgeView
		return new CellView_edge(cell);
	}

}
/*******************************************************************************
 * $RCSfile: GPCellViewFactory.java,v $ $Revision: 1.6 $ $Date: 2012/04/20
 * 08:42:36 $
 * 
 * $Log: GPCellViewFactory.java,v $
 * Revision 1.6  2012/09/14 09:18:02  xch123
 * 邮储现场回来一起更新
 *
 * Revision 1.1  2012/08/28 09:41:17  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2012/04/20 08:42:58  haoming
 * *** empty log message ***
 * Revision 1.4 2012/04/20 08:42:36 haoming ***
 * empty log message ***
 * 
 * Revision 1.3 2011/10/10 06:32:17 wanggang restore
 * 
 * Revision 1.1 2010/05/17 10:23:52 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2010/05/05 11:32:35 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2010/04/08 04:34:05 xuchanghua *** empty log message ***
 * 
 * Revision 1.3 2010/02/08 11:01:54 sunfujun *** empty log message ***
 * 
 * Revision 1.1 2009/11/03 10:13:35 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2009/02/20 06:11:45 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2008/07/24 09:31:52 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2008/06/27 14:48:00 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2008/02/19 13:28:43 xuchanghua *** empty log message ***
 * 
 * Revision 1.2 2008/01/17 02:48:47 xch *** empty log message ***
 * 
 * Revision 1.1 2007/09/23 08:03:03 xch *** empty log message ***
 * 
 * Revision 1.1 2007/09/21 02:29:19 xch *** empty log message ***
 * 
 * Revision 1.4 2007/09/20 05:08:33 xch *** empty log message ***
 * 
 * Revision 1.1 2007/03/07 01:59:13 shxch *** empty log message ***
 * 
 * Revision 1.2 2007/01/30 03:53:26 lujian *** empty log message ***
 * 
 * 
 ******************************************************************************/
