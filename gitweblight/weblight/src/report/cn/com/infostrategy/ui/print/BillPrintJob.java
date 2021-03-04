package cn.com.infostrategy.ui.print;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.jfree.report.Boot;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.PageFooter;
import org.jfree.report.PageHeader;
import org.jfree.report.ReportFooter;
import org.jfree.report.ReportHeader;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.print.PubPrintItemBandVO;
import cn.com.infostrategy.to.print.PubPrintTempletVO;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * ��ӡ���񹤾�
 *
 * @author xch
 */
public class BillPrintJob {

	private PubPrintTempletVO templetVO = null;

	private BillVO billVO = null; //

	private HashMap mapdata = null; //

	TBUtil tbUtil = new TBUtil(); //

	private BillPrintJob() {
	}

	/**
	 * Default constructor.
	 */
	public BillPrintJob(Container _parent, String _templetCode, BillVO _billVO, HashMap _mapdata) {
		try {
			this.billVO = _billVO; //
			this.mapdata = _mapdata;

			//��ȡ��ģ��VO
			ReportServiceIfc printService = (ReportServiceIfc) RemoteServiceFactory.getInstance().lookUpService(ReportServiceIfc.class);
			templetVO = printService.getPubPrintTempletVO(_templetCode); //

			Boot.start();
			JFreeReport report = createReport(); //����һ��JFreeReport����
			report.setData(new DefaultTableModel(1, 1)); //�������ݶ���..

			PreviewDialog dialog = new PreviewDialog(report, (JDialog) _parent); //
			dialog.setSize(1000, 666); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * Creates the page header.ҳü
	 *
	 * @return the page header.
	 */
	private PageHeader createPageHeader() {
		final PageHeader header = new PageHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 20));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));
		//header.setDisplayOnFirstPage(true); //�Ƿ��ڵ�һҳ��ʾ
		//header.setDisplayOnLastPage(true); //�Ƿ������һҳ��ʾ

		// is by default true, but it is defined in the xml template, so I add it here too.
		//header.addElement(StaticShapeElementFactory.createRectangleShapeElement(null, Color.decode("#AFAFAF"), null, new Rectangle2D.Float(0, 0, -100, -100), false, true)); //

		final LabelElementFactory factory = new LabelElementFactory();
		factory.setName("Label 1");
		factory.setAbsolutePosition(new Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 20));
		factory.setHorizontalAlignment(ElementAlignment.RIGHT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("ҳü");
		//factory.setDynamicHeight(Boolean.TRUE);
		header.addElement(factory.createElement());

		//header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", Color.decode("#CFCFCF"), new BasicStroke(2), new Line2D.Float(0, 16, 0, 16)));
		return header;
	}

	/**
	 * Creates a page footer. ҳ��
	 *
	 * @return The page footer.
	 */
	private PageFooter createPageFooter() {
		final PageFooter pageFooter = new PageFooter();
		pageFooter.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
		pageFooter.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Dialog", 10)); //����

		//pageFooter.addElement(StaticShapeElementFactory.createRectangleShapeElement(null, Color.black, null, new Rectangle2D.Float(0, 0, -100, -100), true, false));

		final LabelElementFactory factory = new LabelElementFactory();
		factory.setName("Label 2");
		factory.setAbsolutePosition(new Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 20));
		factory.setHorizontalAlignment(ElementAlignment.RIGHT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("ҳ��");
		//factory.setDynamicHeight(Boolean.TRUE);
		pageFooter.addElement(factory.createElement());

		return pageFooter;
	}

	/**
	 * Creates the report header.
	 *
	 * @return the report header.
	 */
	private ReportHeader createReportHeader() {
		final ReportHeader header = new ReportHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 48));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 20, true, false, false, false));

		final LabelElementFactory factory = new LabelElementFactory();
		factory.setName("Label 1");
		factory.setAbsolutePosition(new Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("����ͷ");
		header.addElement(factory.createElement());
		return header;
	}

	/**
	 * Creates the report footer.
	 *
	 * @return the report footer.
	 */
	private ReportFooter createReportFooter() {
		final ReportFooter footer = new ReportFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 48));
		footer.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 16, true, false, false, false));

		final LabelElementFactory factory = new LabelElementFactory();
		factory.setName("Label 2");
		factory.setAbsolutePosition(new Point2D.Float(0, 0));
		factory.setMinimumSize(new FloatDimension(-100, 24));
		factory.setHorizontalAlignment(ElementAlignment.CENTER);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("*** ����β ***");
		footer.addElement(factory.createElement());
		return footer;
	}

	/**
	 * Creates the itemBand. ����ж�����
	 *
	 * @return the item band.
	 */
	private ItemBand createItemBand() {
		final ItemBand items = new ItemBand(); //
		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 10));
		//items.getBandDefaults().setFontDefinitionProperty(new FontDefinition("System", 9)); //��������

		//�Ȼ��ײ��..
		for (int i = 0; i < this.templetVO.getItemBandVOs().length; i++) {
			if (this.templetVO.getItemBandVOs()[i].getLayer() == JLayeredPane.DEFAULT_LAYER.intValue()) {
				addMyItem(items, this.templetVO.getItemBandVOs()[i]); //
			}
		}

		//�ٻ���һ���
		for (int i = 0; i < this.templetVO.getItemBandVOs().length; i++) {
			if (this.templetVO.getItemBandVOs()[i].getLayer() == JLayeredPane.PALETTE_LAYER.intValue()) {
				addMyItem(items, this.templetVO.getItemBandVOs()[i]); //
			}
		}

		return items;
	}

	private void addMyItem(ItemBand items, PubPrintItemBandVO _itemBandVO) {
		String str_itemkey = _itemBandVO.getItemkey(); //
		String str_itemname = _itemBandVO.getItemname(); //
		int x = (int) _itemBandVO.getX(); //Xλ��
		int y = (int) _itemBandVO.getY(); //Yλ��
		int width = _itemBandVO.getWidth(); //���
		int height = _itemBandVO.getHeight(); //�߶�

		String bgcolor = _itemBandVO.getBackground(); //������ɫ
		items.addElement(StaticShapeElementFactory.createRectangleShapeElement("background", tbUtil.getColor(bgcolor), new BasicStroke(0), new Rectangle2D.Float(x, y, width, height), false, true));

		if (_itemBandVO.isShowBorder()) { //����б߿�,���ú�ɫ��һ���߿�
			items.addElement(StaticShapeElementFactory.createRectangleShapeElement("background", Color.BLACK, new BasicStroke(0), new Rectangle2D.Float(x, y, width, height), true, false));
		}

		//�����ı�
		LabelElementFactory factory = new LabelElementFactory();
		factory.setName(str_itemkey);

		factory.setAbsolutePosition(new Point2D.Double(x, y + 2)); //����λ��
		factory.setMinimumSize(new FloatDimension(width, height)); //���ô�С

		factory.setFontName(_itemBandVO.getFonttype()); //��������
		factory.setFontSize(new Integer(_itemBandVO.getFontsize())); //��������
		factory.setColor(tbUtil.getColor(_itemBandVO.getForeground())); //�����ֵ���ɫ

		if (_itemBandVO.getFontstyle() == 1) {
			factory.setBold(Boolean.TRUE); //�Ƿ����
		}

		if (_itemBandVO.getFontstyle() == 2) {
			factory.setItalic(Boolean.TRUE); //�Ƿ�б��
		}

		//��������λ��
		int li_align = _itemBandVO.getHalign(); //
		if (li_align == SwingConstants.LEFT) {
			factory.setHorizontalAlignment(ElementAlignment.LEFT);
		} else if (li_align == SwingConstants.CENTER) {
			factory.setHorizontalAlignment(ElementAlignment.CENTER);
		} else if (li_align == SwingConstants.RIGHT) {
			factory.setHorizontalAlignment(ElementAlignment.CENTER);
		}

		int li_valign = _itemBandVO.getValign(); //
		if (li_valign == SwingConstants.TOP) {
			factory.setVerticalAlignment(ElementAlignment.TOP); //
		} else if (li_valign == SwingConstants.CENTER) {
			factory.setVerticalAlignment(ElementAlignment.MIDDLE); //
		} else if (li_valign == SwingConstants.BOTTOM) {
			factory.setVerticalAlignment(ElementAlignment.BOTTOM); //
		}

		//
		if (_itemBandVO.isShowBaseline()) { //
			factory.setUnderline(Boolean.TRUE); //
		}

		String str_value = str_itemname; //����ֵ
		if (this.billVO != null && billVO.containsKey(str_itemkey)) { //����ж�Ӧ������
			str_value = billVO.getStringValue(str_itemkey) == null ? "" : billVO.getStringValue(str_itemkey); //���ȡ����.
		} else if (this.mapdata != null && mapdata.containsKey(str_itemkey)) {
			str_value = mapdata.get(str_itemkey) == null ? "" : "" + mapdata.get(str_itemkey); //���ȡ����.
		}

		factory.setText(str_value); //
		items.addElement(factory.createElement());
	}

	/**
	 <pre>
	 <groups>

	 ... create the groups and add them to the list ...

	 </groups>
	 </pre>
	 *
	 * @return the groups.
	 */
	private GroupList createGroups() {
		final GroupList list = new GroupList();
		list.add(createContinentGroup()); //
		return list;
	}

	/**
	 <pre>
	 <group name="Continent Group">
	 <groupheader height="18" fontname="Monospaced" fontstyle="bold" fontsize="9" pagebreak="false">
	 <label name="Label 5" x="0" y="1" width="76" height="9" alignment="left">CONTINENT:</label>
	 <string-field name="Continent Element" x="96" y="1" width="76" height="9" alignment="left"
	 fieldname="Continent"/>
	 <line name="line1" x1="0" y1="12" x2="0" y2="12" weight="0.5"/>
	 </groupheader>
	 <groupfooter height="18" fontname="Monospaced" fontstyle="bold" fontsize="9">
	 <label name="Label 6" x="0" y="0" width="450" height="12" alignment="left"
	 baseline="10">Population:</label>
	 <number-function x="260" y="0" width="76" height="12" alignment="right" baseline="10"
	 format="#,##0" function="sum"/>
	 </groupfooter>
	 <fields>
	 <field>Continent</field>
	 </fields>
	 </group>
	 </pre>
	 *
	 * @return the continent group.
	 */
	private Group createContinentGroup() {
		final Group continentGroup = new Group();
		continentGroup.setName("Continent Group");
		continentGroup.addField("Continent"); //����һ�н��з���

		//���ͷ��
		final GroupHeader header = new GroupHeader(); //
		header.setPagebreakBeforePrint(true); //
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 100));
		header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("System", 9, true, false, false, false));

		int i = 0;
		//		for (i = 0; i < 5; i++) {
		LabelElementFactory factory = new LabelElementFactory();
		factory.setName("Label 5" + i);
		factory.setAbsolutePosition(new Point2D.Float(0, 20 * i));
		factory.setMinimumSize(new FloatDimension(100, 20));
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setText("������:" + i);
		header.addElement(factory.createElement());
		//		}

		//header.addElement(StaticShapeElementFactory.createLineShapeElement("line1", null, new BasicStroke(0.5f), new Line2D.Float(0, 12, 0, 12)));
		continentGroup.setHeader(header);

		return continentGroup;
	}

	/**
	 * Creates the report.
	 *
	 * @return the constructed report.
	 *
	 * @throws FunctionInitializeException if there was a problem initialising any of the functions.
	 */
	public JFreeReport createReport() throws FunctionInitializeException {
		final JFreeReport report = new JFreeReport(); //
		report.setName(this.templetVO.getTempletname()); //
		report.setItemBand(createItemBand()); //
		return report;
	}

}
