package com.pushworld.ipushgrc.bs.wfrisk;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import javax.swing.text.DefaultStyledDocument;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;
import com.pushworld.ipushgrc.bs.StyleDocumentConverter;

/**
 * 体系文件生成word的类,非常关键的类! 它与WFRiskHtmlBuilder是两个体系文件模块成功关键的最重要两个类之一!!
 * 也可能将是以后经常反复优化的两个类!!
 * @author lcj
 */
public class WFRiskWordBuilder {
	private String cmpfileid = null;
	private HashMap wfBase64CodesMap = null;
	private HashMap itemMap = null;
	private String cmpfiletype = null;
	private String cmpfiletype_code = null;
	private ByteArrayOutputStream byteOutStream = null;
	private Font filetitleFont = null;
	private Font titleFont = null;
	private Font underlinetitleFont = null;
	private Font littletitleFont = null;
	private Font littletitleFont2 = null;
	private Font contextFont = null;
	private Font rankFont1 = null;
	private Font rankFont2 = null;
	private Font rankFont3 = null;
	private Font headerFont_1 = null;
	private Font headerFont_2 = null;
	private CommDMO commDMO = new CommDMO();
	private TBUtil tbUtil = new TBUtil();
	private Document document = null;
	private Chapter chapter2 = null;
	private Image titleimg = null;
	private boolean hasStation = tbUtil.getSysOptionBooleanValue("流程导出word时无流程表格是否显示阶段", true);//郭申普在台前项目提出，如果无流程的流程文件发布正文时一图两表部分的表格就不显示阶段了，否则这一列都是空【李春娟/2013-06-21】
	private String process_fontstyle = tbUtil.getSysOptionStringValue("流程导出word时流程表格内容所用字体格式", "默认");
	private boolean showActivity = tbUtil.getSysOptionBooleanValue("流程导出word时无流程表格是否显示环节名称", false);//肖达仁说，无流程的流程文件导出word，如果管理/操作要求 列不显示环节名称，不好区分写的什么内容。 他超级纠结，我本身是很不想改的！【李春娟/2014-04-09】

	public WFRiskWordBuilder(String _cmpfileid, HashMap _wfBase64CodesMap) {
		this.cmpfileid = _cmpfileid;
		this.wfBase64CodesMap = _wfBase64CodesMap;
	}

	/**
	 * 使用itext计算Word文件的内容,返回的是整个word的byte[]
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes() throws Exception {
		return getDocContextBytes(false); //返回
	}

	/**
	 * 使用itext计算Word文件的内容,返回的是整个word的byte[]
	 * @param _onlyWf 是否只有流程说明
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(boolean _onlyWf) throws Exception {
		createDoc(_onlyWf);//创建文档，并设置字体等风格
		if (_onlyWf) {
			document.setPageSize(PageSize.A4);
			chapter2 = new Chapter(-1);
			addAllWf(false, false);//处理流程图,横向导出，不是合规手册
			document.add(chapter2);
		} else {
			addHomePage();//添加首页信息（第一页）
			addSecondPage();//添加修改记录（第二页）
			addContentPage();//添加文件内容
		}
		document.close();
		return byteOutStream.toByteArray(); //返回
	}

	/**
	 * 使用itext计算Word文件的内容,返回的是整个word的byte[]
	 * @param _onlyWf 是否只有流程说明
	 * @param _ishandbook 是否为安徽合规手册的风格,如果该字段为true，不再判断_onlyWf
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(boolean _onlyWf, boolean _ishandbook) throws Exception {
		if (!_ishandbook) {
			return getDocContextBytes(_onlyWf);
		}
		createDoc(true);//创建文档，并设置字体等风格，并且只有流程说明部分
		document.setPageSize(PageSize.A4);
		chapter2 = new Chapter(-1);
		addAllWf(false, true);//处理流程图,纵向显示，是合规手册
		document.close();
		return byteOutStream.toByteArray(); //返回
	}

	/**
	 * 创建文档，并设置字体等风格
	 * @param _onlyWf 是否只有流程图
	 * @throws Exception
	 */
	private void createDoc(boolean _onlyWf) throws Exception {
		document = new Document(PageSize.A4);// 设置纸张大小 		
		byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中 

		document.open();
		document.setMargins(60, 60, 80, 60);//设置页边距

		//设置字体风格
		filetitleFont = new RtfFont("宋_体", 26, Font.BOLD);// 文件名字体风格 
		//filetitleFont.setStyle(7);//设置格式
		titleFont = new RtfFont("宋_体", 16, Font.BOLD);// 标题字体风格 
		underlinetitleFont = new RtfFont("宋_体", 16, Font.UNDERLINE);// 下划线标题字体风格 

		String url = this.getClass().getResource("/").toString();
		System.out.println(">>>>>>WFRiskWordBuilder类路径：" + url);

		//通过系统参数设置，改变word预览流程文件一图两表处表格字体格式[YangQing/2013-06-26]本次修改中遇到的难题是,这个字体的行间距无法设置
		String font_style = "默认";//类型：字体、字体文件,初始值，默认12号宋体
		String font_family = "宋_体"; 
		String font_size = "";
		if (process_fontstyle.contains(";")) {//如果参数值中包含分号,说明不是默认
			String[] family_size = process_fontstyle.split(";");
			font_style = family_size[0];
			font_family = family_size[1];
			font_size = family_size[2];
		}
		int fontSize = 12;
		if (!font_size.equals("")) {
			try {
				fontSize = Integer.parseInt(font_size);//
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (font_style.equals("字体文件")) {//指定字体文件
			BaseFont basefont = BaseFont.createFont(url.substring(0, url.indexOf("WEB-INF") - 1) + "/help/" + font_family, "UTF-8", BaseFont.NOT_EMBEDDED);
			littletitleFont = new Font(basefont, 15, Font.BOLD);//此处，以后优化，先控制死表头字体大小
			littletitleFont2 = new Font(basefont, fontSize, Font.BOLD);
			contextFont = new Font(basefont, fontSize, Font.NORMAL);
			rankFont1 = new Font(basefont, fontSize, Font.NORMAL);
			rankFont2 = new Font(basefont, fontSize, Font.NORMAL);
			rankFont3 = new Font(basefont, fontSize, Font.NORMAL);
		} else {//没有设参数，默认宋体;或设了字体;或参数格式错误
			littletitleFont = new RtfFont(font_family, fontSize, Font.BOLD);// 小标题字体风格 ,五号字体
			littletitleFont2 = new RtfFont(font_family, fontSize, Font.BOLD);// 小标题字体风格 ,五号字体
			contextFont = new RtfFont(font_family, fontSize, Font.NORMAL);// 正文字体风格,五号字体
			rankFont1 = new RtfFont(font_family, fontSize, Font.NORMAL);// 正文字体风格,五号字体
			rankFont2 = new RtfFont(font_family, fontSize, Font.NORMAL);// 正文字体风格,五号字体
			rankFont3 = new RtfFont(font_family, fontSize, Font.NORMAL);// 正文字体风格,五号字体
		}

		littletitleFont.setColor(0, 0, 0);
		littletitleFont2.setColor(0, 0, 0);
		contextFont.setColor(0, 0, 0);//必须设置一下，否则因风险等级的字体设置了颜色，导致跟风险等级的颜色一致了
		rankFont1.setColor(51, 153, 102);//低风险
		rankFont2.setColor(255, 102, 0);//中等风险
		rankFont3.setColor(250, 0, 0);//高风险

		headerFont_1 = new RtfFont("宋_体", 14, Font.BOLD, Color.BLACK);
		headerFont_2 = new RtfFont("仿_宋", 12, Font.ITALIC, Color.BLACK);
		//设置页眉页脚
		Font footerFont = new RtfFont("宋_体", 10, Font.NORMAL);
		Phrase ph_header = new Phrase();

		try {
			titleimg = Image.getInstance(url.substring(0, url.indexOf("WEB-INF") - 1) + "/images/logo.png");//公司logo，修改为png类型，有透明效果，并增加了logo最大高度的参数【李春娟/2014-02-20】
			float li_height = titleimg.getHeight();
			float max_height = tbUtil.getSysOptionIntegerValue("流程导出word时流程页眉logo最大高度", 30);//保证高度最大30，按比例放缩，如果超过这个最大高度才需要放缩【李春娟/2012-11-13】
			if (li_height > max_height) {
				titleimg.scalePercent(max_height * 100 / li_height);
			}
			titleimg.setAlignment(Element.ALIGN_LEFT);

			ph_header.add(new Chunk(titleimg, titleimg.getWidth(), titleimg.getHeight()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//文件分类
		cmpfiletype = commDMO.getStringValueByDS(null, "select cmpfiletype from v_cmp_cmpfile where id=" + this.cmpfileid);
		cmpfiletype_code = commDMO.getStringValueByDS(null, "select code from PUB_COMBOBOXDICT where type='文件分类' and name='" + cmpfiletype + "'");
		if (_onlyWf) {//如果只有流程则横向显示
			ph_header.add(new Chunk("  " + convertStr(tbUtil.getSysOptionStringValue("流程导出word时页眉显示的内容", "    "), 24), headerFont_1));
		} else {
			ph_header.add(new Chunk("  " + convertStr(tbUtil.getSysOptionStringValue("流程导出word时页眉显示的内容", "    "), 24), headerFont_1));
		}
		//ph_header.add(new Chunk("流程合规手册", headerFont_2));//内控系统中这里是内控手册而非合规手册，故干脆去掉【李春娟/2012-11-13】
		HeaderFooter Header = new HeaderFooter(ph_header, false);
		document.setHeader(Header);//设置页眉

		HeaderFooter footer = new HeaderFooter(new Phrase("第", footerFont), new Phrase("页", footerFont));
		footer.setBorder(Rectangle.NO_BORDER);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);//设置页脚
	}

	/**
	 * 添加首页信息，包括公司logo，文件名，编制单位，发布日期，生效日期等信息
	 * @throws Exception
	 */
	private void addHomePage() throws Exception {
		HashVO[] hvs_cmpfile = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id=" + cmpfileid + " order by id"); //查询流程文件的相关信息

		//		String url = this.getClass().getResource("/").toString();
		//		Image titleimg = Image.getInstance(url.substring(0, url.indexOf("WEB-INF") - 1) + "/images/logo.jpg");//公司logo
		//		document.add(titleimg);

		Paragraph filetitle = new Paragraph(hvs_cmpfile[0].getStringValue("cmpfilename"), filetitleFont);//文件名
		filetitle.setAlignment(Element.ALIGN_CENTER);// 设置标题格式对齐方式 
		filetitle.setLeading(240);//与前段的间距
		document.add(filetitle);

		Paragraph title_record = new Paragraph("合规流程文件编码：" + hvs_cmpfile[0].getStringValue("cmpfilecode", ""), titleFont);//文件编号
		title_record.setAlignment(Element.ALIGN_CENTER);// 设置标题格式对齐方式 
		document.add(title_record);

		Chunk chunk1 = new Chunk("编制单位：", titleFont);
		Chunk chunk2 = new Chunk(convertStr(hvs_cmpfile[0].getStringValue("blcorpname"), 24), underlinetitleFont);
		Paragraph title_dept = new Paragraph();//编制单位
		title_dept.setFirstLineIndent(110);//该段首行缩进
		title_dept.setSpacingBefore(120);
		title_dept.setLeading(120);//与前段的间距
		title_dept.add(chunk1);
		title_dept.add(chunk2);
		document.add(title_dept);

		Chunk chunk3 = new Chunk("发布日期：", titleFont);
		Chunk chunk4 = new Chunk(convertStr(hvs_cmpfile[0].getStringValue("publishdate"), 24), underlinetitleFont);

		Paragraph title_publishdate = new Paragraph();//编制单位
		title_publishdate.setFirstLineIndent(110);//该段首行缩进
		title_publishdate.setLeading(30);
		title_publishdate.add(chunk3);
		title_publishdate.add(chunk4);
		document.add(title_publishdate);

		Chunk chunk5 = new Chunk("生效日期：", titleFont);
		Chunk chunk6 = new Chunk(convertStr("", 24), underlinetitleFont);

		Paragraph title_effectivedate = new Paragraph();//编制单位
		title_effectivedate.setFirstLineIndent(110);//设置该段首行缩进的列数
		title_effectivedate.setLeading(30);//
		title_effectivedate.add(chunk5);
		title_effectivedate.add(chunk6);
		document.add(title_effectivedate);
	}

	/**
	 * 添加第二页信息，即修改记录
	 * @throws Exception
	 */
	private void addSecondPage() throws Exception {
		Paragraph editrecord = new Paragraph("修改记录", littletitleFont);//修改记录（第二页）
		editrecord.setAlignment(Element.ALIGN_CENTER);
		Chapter chapter1 = new Chapter(editrecord, 0);

		// 设置 Table 表格 
		Table table_editrecord = new Table(4);
		int width[] = { 20, 25, 25, 50 };
		table_editrecord.setWidths(width);//设置每列所占比例 
		table_editrecord.setWidth(90); // 占页面宽度 90% 
		table_editrecord.setPadding(5);

		//设置表头 
		Cell cell1 = new Cell(new Phrase("序号", littletitleFont));
		Cell cell2 = new Cell(new Phrase("修改日期", littletitleFont));
		Cell cell3 = new Cell(new Phrase("版本号", littletitleFont));
		Cell cell4 = new Cell(new Phrase("修改原因和修改内容提示", littletitleFont));

		cell1.setHeader(true);//将该单元格作为表头信息显示
		cell2.setHeader(true);
		cell3.setHeader(true);
		cell4.setHeader(true);

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		cell1.setVerticalAlignment(Element.ALIGN_CENTER);
		cell2.setVerticalAlignment(Element.ALIGN_CENTER);
		cell3.setVerticalAlignment(Element.ALIGN_CENTER);
		cell4.setVerticalAlignment(Element.ALIGN_CENTER);

		cell1.setBackgroundColor(new Color(204, 204, 204));
		cell2.setBackgroundColor(new Color(204, 204, 204));
		cell3.setBackgroundColor(new Color(204, 204, 204));
		cell4.setBackgroundColor(new Color(204, 204, 204));

		table_editrecord.addCell(cell1);
		table_editrecord.addCell(cell2);
		table_editrecord.addCell(cell3);
		table_editrecord.addCell(cell4);
		table_editrecord.endHeaders();//设置当表格跨页后，显示表头信息 

		HashVO[] hvs_cmpfile_hist = commDMO.getHashVoArrayByDS(null, "select cmpfile_publishdate,cmpfile_versionno from cmp_cmpfile_hist where cmpfile_id=" + cmpfileid + "  and cmpfile_versionno not like '%._1' order by cmpfile_publishdate,cmpfile_versionno");
		int histcount = 0;
		for (; histcount < hvs_cmpfile_hist.length; histcount++) {
			Cell cell_hist1 = new Cell(new Phrase(histcount + 1 + "", contextFont));
			Cell cell_hist2 = new Cell(new Phrase(hvs_cmpfile_hist[histcount].getStringValue("cmpfile_publishdate"), contextFont));
			Cell cell_hist3 = new Cell(new Phrase(hvs_cmpfile_hist[histcount].getStringValue("cmpfile_versionno"), contextFont));
			Cell cell_hist4 = new Cell(new Phrase("", contextFont));

			cell_hist1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_hist2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_hist3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_hist4.setHorizontalAlignment(Element.ALIGN_CENTER);

			table_editrecord.addCell(cell_hist1);
			table_editrecord.addCell(cell_hist2);
			table_editrecord.addCell(cell_hist3);
			table_editrecord.addCell(cell_hist4);
		}
		for (int i = 0; i < 30 - histcount; i++) {//如果历史记录少于30条，则添加空行
			for (int j = 0; j < 4; j++) {
				Cell cell = new Cell(new Phrase("", contextFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table_editrecord.addCell(cell);
			}
		}
		chapter1.add(table_editrecord);
		document.add(chapter1);//添加第一章 修改记录标题后面有一修改记录表		
	}

	/**
	 * 添加文件各部分内容，包括目的，适用范围，定义、缩写和分类，职责与权限，基本原则，流程说明，相关文件、附录等信息
	 * @throws Exception
	 */
	private void addContentPage() throws Exception {
		itemMap = new WFRiskBSUtil().getCmpFileItemDocument(cmpfileid, false);//返回一个流程文件中的富文本框中实际对应的Document对象，如果没有格式则返回字符串
		String show_item = tbUtil.getSysOptionStringValue("体系流程_流程文件子项的显示", null);
		int num = 1;//第几节
		String[] show_items = null;
		if (show_item == null) {//判断是否配置了子项的显示，如果没有配置则显示所有，如果配置了，则按配置判断是否显示
			addOneItem(1, "目的", "item_target");
			addOneItem(2, "适用范围", "item_userarea");
			addOneItem(3, "定义、缩写和分类", "item_keywords");
			addOneItem(4, "职责与权限", "item_duty");
			addOneItem(5, "基本原则", "item_bsprcp");
			num = 6;
			//流程及相关相关文件、附录最后处理
		} else {//按配置判断是否显示
			show_items = tbUtil.split(show_item, ";");
			if ("Y".equals(show_items[0])) {
				addOneItem(num, "目的", "item_target");
				num++;
			}
			if ("Y".equals(show_items[1])) {
				addOneItem(num, " 适用范围", "item_userarea");
				num++;
			}
			if ("Y".equals(show_items[2])) {
				addOneItem(num, "定义、缩写和分类", "item_keywords");
				num++;
			}
			if ("Y".equals(show_items[3])) {
				addOneItem(num, "职责与权限", "item_duty");
				num++;
			}
			if ("Y".equals(show_items[4])) {
				addOneItem(num, "基本原则", "item_bsprcp");
				num++;
			}
		}
		//统一处理流程及相关相关文件、附录,默认流程说明必须得有
		if (chapter2 == null) {
			chapter2 = new Chapter(new Paragraph("流程说明", littletitleFont), num);
		} else {
			chapter2.add(new Paragraph(num + ". 流程说明", littletitleFont));
		}
		addAllWf(false, false);//处理流程图,非横向导出，不是合规手册
		num++;

		if (show_items != null && "Y".equals(show_items[5])) {
			chapter2.add(new Chunk("\n"));
			chapter2.add(new Paragraph(num + ". 相关文件、附录", littletitleFont));//
			String str_item_addenda = (String) itemMap.get("item_addenda");
			Paragraph addenda_content2 = new Paragraph(str_item_addenda, contextFont);
			addenda_content2.setFirstLineIndent(30);
			chapter2.add(addenda_content2);
		}
		document.add(chapter2);
	}

	/**
	 * 增加一个富文本内容
	 * @param _chapter 章
	 * @param _title 标题
	 * @param _itemkey 对应字段
	 * @return
	 */
	private void addOneItem(int num, String _title, String _itemkey) {
		if (chapter2 == null) {
			chapter2 = new Chapter(new Paragraph(_title, littletitleFont2), num);//标题
		} else {
			chapter2.add(new Paragraph(num + ". " + _title, littletitleFont2));//标题
		}
		if (itemMap.get(_itemkey) instanceof String) {
			String str_item = (String) itemMap.get(_itemkey);
			chapter2.add(new Paragraph(str_item + "\n", contextFont));//内容
		} else {
			DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get(_itemkey);
			StyleDocumentConverter converter = new StyleDocumentConverter(doc);
			converter.toWord(chapter2);
			chapter2.add(new Chunk("\n"));//一个换行
		}
	}

	/**
	 * 流程说明的所有内容，包括流程图和流程概述表格和环节信息表格
	 * @param _isHorizontal 页面是否横向显示
	 * @param _ishandbook   是否是安徽版本的合规手册的格式
	 * @throws Exception
	 */
	private void addAllWf(boolean _isHorizontal, boolean _ishandbook) throws Exception {
		if (wfBase64CodesMap == null) {//如果没有流程，就直接返回
			return;
		}
		String show_processref1 = tbUtil.getSysOptionHashItemStringValue("体系流程_一图两表自定义格式类", "WORD", null);//项目中可自定义一图两表生成word和html的格式【李春娟/2012-05-21】
		if (show_processref1 != null && !"".equals(show_processref1)) {
			HashMap hashmap = new HashMap();
			hashmap.put("isHorizontal", _isHorizontal);
			hashmap.put("ishandbook", _ishandbook);
			hashmap.put("parent", this);
			hashmap = tbUtil.reflectCallCommMethod(show_processref1 + ".addAllWf()", hashmap);
			return;
		}
		String[] str_imgKeys = (String[]) wfBase64CodesMap.keySet().toArray(new String[0]); //
		HashVO[] hvs_processes = null;
		HashVO[] hvs_activitys = null;
		HashVO[] hvs_opereqs = null;
		HashVO[] hvs_laws = null;
		HashVO[] hvs_rules = null;
		HashVO[] hvs_risks = null;

		String show_processref = tbUtil.getSysOptionStringValue("体系流程_流程相关", null);
		boolean showwfdesc = true;
		if (show_processref == null || "".equals(show_processref.trim()) || "Y".equals(show_processref.substring(0, 1))) {//如果没有配置或者配置为显示
			hvs_processes = commDMO.getHashVoArrayByDS(null, "select process.wfprocess_id,process.wfprocess_code,process.wfprocess_name,process.blcorpname,wfdesc.wfdesc from v_process_file process left join cmp_cmpfile_wfdesc wfdesc on process.wfprocess_id=wfdesc.wfprocess_id where process.cmpfile_id=" + cmpfileid);//
		} else {
			showwfdesc = false;
		}
		String show_activityref = tbUtil.getSysOptionStringValue("体系流程_环节相关", null);
		boolean showopereq = true;//是否有流程描述及操作规范
		boolean showlaw = true;//是否有相关法规
		boolean showrule = true;//是否有相关制度
		boolean showrisk = true;//是否有风险要点（风险点）
		if (show_activityref == null || "".equals(show_activityref.trim())) {
			hvs_opereqs = commDMO.getHashVoArrayByDS(null, "select dept.name operatedept,post.name operatepost,opereq.operaterefpost,opereq.operatedesc,opereq.operatereq,opereq.wfactivity_id from cmp_cmpfile_wfopereq opereq left join pub_corp_dept dept on opereq.operatedept=dept.id left join pub_post post on opereq.operatepost=post.id  where opereq.cmpfile_id =" + cmpfileid
					+ " order by opereq.id"); //查询该流程文件所有的需要导出的操作规范
			hvs_laws = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_law where cmpfile_id =" + cmpfileid + " order by wfactivity_id,law_id,id"); //查询该流程文件所有环节的相关法规，排序字段 wfactivity_id,law_id必须有
			hvs_rules = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_rule where cmpfile_id =" + cmpfileid + " order by wfactivity_id,rule_id,id"); //查询该流程文件所有环节的相关制度，排序字段 wfactivity_id,rule_id必须有
			hvs_risks = commDMO.getHashVoArrayByDS(null, "select * from cmp_risk where cmpfile_id =" + cmpfileid + " order by id"); //查询该流程文件所有环节的风险要点
		} else {
			String[] show_activityrefs = tbUtil.split(show_activityref, ";");
			if ("Y".equals(show_activityrefs[0])) {
				hvs_opereqs = commDMO.getHashVoArrayByDS(null, "select dept.name operatedept,post.name operatepost,opereq.operaterefpost,opereq.operatedesc,opereq.operatereq,opereq.wfactivity_id from cmp_cmpfile_wfopereq opereq left join pub_corp_dept dept on opereq.operatedept=dept.id left join pub_post post on opereq.operatepost=post.id where opereq.cmpfile_id =" + cmpfileid
						+ " order by opereq.id"); //查询该流程文件所有的需要导出的操作规范
			} else {
				showopereq = false;
			}
			if ("Y".equals(show_activityrefs[1])) {
				hvs_laws = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_law where cmpfile_id =" + cmpfileid + " order by wfactivity_id,law_id,id"); //查询该流程文件所有环节的相关法规
			} else {
				showlaw = false;
			}
			if ("Y".equals(show_activityrefs[2])) {
				hvs_rules = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_rule where cmpfile_id =" + cmpfileid + " order by wfactivity_id,rule_id,id"); //查询该流程文件所有环节的相关制度
			} else {
				showrule = false;
			}
			//if ("Y".equals(show_activityrefs[5])) {
			//无论是合规系统还是内控系统，必然有风险点，内控系统中很多情况重写了维护风险点的逻辑，故这里直接显示风险提示，不进行判断了，判断只对一图两表环节相关的风险点的显示起作用【李春娟/2012-07-10】
			hvs_risks = commDMO.getHashVoArrayByDS(null, "select * from cmp_risk where cmpfile_id =" + cmpfileid + " order by id"); //查询该流程文件所有环节的风险要点
			//} else {
			//	showrisk = false;
			//}
		}

		int column_activiy = 1;//环节信息表格的列数
		boolean show_activitystyle = tbUtil.getSysOptionBooleanValue("流程文件生成word时环节表格是否为简约风格", true);
		if (_ishandbook) {//如果是合规手册则默认为非简约风格
			show_activitystyle = false;//默认为非简约风格
		}
		String dbType = ServerEnvironment.getInstance().getDefaultDataSourceType().toUpperCase();//取得数据库类型,因为要集成以前系统中排序以“环节”开头的问题，使用了cast()函数，下面sql只能在mysql执行，以后要判断数据库！！！

		boolean show_judgeactivity = tbUtil.getSysOptionBooleanValue("流程文件生成word时判断环节是否导出", false);//以前的项目有用判断环节录入风险等相关信息的，故需要配置来判断是否导出【李春娟/2012-06-12】
		if (dbType.equalsIgnoreCase("MYSQL")) {//act.uiname 必须查出来，否则位置有问题！
			hvs_activitys = commDMO.getHashVoArrayByDS(null, "select act.id,act.wfname,act.belongstationgroup,act.processid from pub_wf_activity act left join pub_wf_group grp on act.processid = grp.processid and act.belongstationgroup = grp.wfname where act.processid in(" + tbUtil.getInCondition(str_imgKeys) + ") " + (show_judgeactivity ? "" : "and act.viewtype <>3")
					+ " and act.viewtype <>7 order by act.processid,grp.y,cast(act.uiname as signed)");//查询该流程文件所有非判断类型的环节信息，排序字段processid,belongstationgroup必须有。一汽项目王雷提出如果是文档环节，也不需要导出【李春娟/2012-03-31】
		} else if (dbType.equalsIgnoreCase("ORACLE") || dbType.equalsIgnoreCase("DB2")) {//必须保证环节uiname不为空串和没有文字（新项目是没有问题的，平台排序已设置为数字）
			hvs_activitys = commDMO.getHashVoArrayByDS(null, "select act.id,act.wfname,act.belongstationgroup,act.processid from pub_wf_activity act left join pub_wf_group grp on act.processid = grp.processid and act.belongstationgroup = grp.wfname where act.processid in(" + tbUtil.getInCondition(str_imgKeys) + ") " + (show_judgeactivity ? "" : "and act.viewtype <>3")
					+ " and act.viewtype <>7 order by act.processid,grp.y,to_number(act.uiname)");
		} else {//SQLSERVER等
			hvs_activitys = commDMO.getHashVoArrayByDS(null, "select id,wfname,belongstationgroup,processid from pub_wf_activity where processid in(" + tbUtil.getInCondition(str_imgKeys) + ") " + (show_judgeactivity ? "" : "and viewtype <>3") + " and viewtype <>7 order by processid,belongstationgroup,uiname");
		}
		if (show_activitystyle && showopereq) {
			column_activiy++;
		} else if (showopereq) {
			column_activiy += 3;
		}
		if (showrisk) {
			column_activiy++;
		}
		if (!show_activitystyle && showlaw) {//简约风格是没有法规和制度的。
			column_activiy++;
		}
		if (!show_activitystyle && showrule) {
			column_activiy++;
		}

		//流程图和环节信息表格的for循环开始
		for (int i = 0; i < str_imgKeys.length; i++) { //遍历哈希表中的所有值!!
			Chapter chapter_desc = new Chapter(-1);
			String processid = str_imgKeys[i]; //
			if (cmpfiletype_code.contains("有流程")) {//如果文件分类中编码为有流程则导出流程图，反之编码为无流程就不导出流程图
				byte[] imgBytes = (byte[]) wfBase64CodesMap.get(processid);
				imgBytes = tbUtil.decompressBytes(imgBytes); //解压!!!
				Image img = Image.getInstance(imgBytes);
				img.setAlignment(Image.MIDDLE);//设置图片显示位置 
				float li_width = img.getWidth();
				float li_height = img.getHeight();
				float max_width = 430;
				float max_height = 600;//正好一页的高度
				if (_isHorizontal) {
					max_width = 700;
					max_height = 360;
				}
				if (li_width > max_width || li_height > max_height) {
					float percent = 0;
					if (li_width / max_width > li_height / max_height) {
						percent = max_width / li_width * 100;//表示显示的大小为原尺寸的百分之几
						if (percent < 60) {
							img.scalePercent(percent + percent / 10, (percent + 20 > 100 ? 100 : percent + 20));//宽的比例要比高小些，也就是缩小的比例要比高更小些
						} else {
							img.scalePercent(percent, (percent + 10 > 100 ? 100 : percent));
						}
					} else {
						percent = max_height / li_height * 100;
						if (percent < 60) {
							img.scalePercent((percent + 20 > 100 ? 100 : percent + 20), percent + percent / 10);
						} else {
							img.scalePercent((percent + 10 > 100 ? 100 : percent + 10), percent);
						}

					}
				}
				if (_ishandbook) {
					Chapter chapter_wf = new Chapter(-1);
					chapter_wf.add(img);
					document.setPageSize(PageSize.A4);//流程图都是纵向的，需要设置一下，因为上面可能有横向的表格
					document.add(chapter_wf);
					if (showwfdesc && _ishandbook) {//如果显示流程概述表格并且是合规手册则显示流程概述表格,则横向显示
						addWfdescTable(chapter_desc, processid, hvs_processes);
					}
				} else {
					chapter2.add(img);
					chapter2.newPage();
					if (showwfdesc && _ishandbook) {//如果显示流程概述表格并且是合规手册则显示流程概述表格
						addWfdescTable(chapter2, processid, hvs_processes);
					}
				}
			}
			if (show_activitystyle && column_activiy > 1) {
				addActivityDescTable(column_activiy, processid, showopereq, showrisk, hvs_activitys, hvs_opereqs, hvs_risks, _ishandbook, chapter_desc);//添加环节信息表格
			} else if (column_activiy > 1) {
				addActivityDescTable2(column_activiy, processid, showopereq, showlaw, showrule, showrisk, hvs_activitys, hvs_opereqs, hvs_laws, hvs_rules, hvs_risks, _ishandbook, chapter_desc);//添加环节信息表格
			}
		}//流程图和环节信息表格的for循环结束
	}

	/**
	 * 非简约风格的环节信息表格，最多7列。这是安徽版本的合规手册。
	 * 第一列：阶段/环节
	 * 第二列：部门/岗位（操作部门/操作岗位）
	 * 第三列：职责
	 * 第四列：操作规范
	 * 第五列：风险要点（风险点的相关信息，同下简约风格的风险提示，包括风险描述、风险等级、风险分类、控制措施、控制部门/岗位）//惠州农商行萧达仁提出需要显示风险分类【李春娟/2015-07-09】
	 * 第六列：法规
	 * 第七列：制度
	 * 第六列和第七列合并为合规要求。
	 * @param column_activiy  表格列数
	 * @param processid	   流程id
	 * @param showopereq	   是否显示职责及操作规范
	 * @param showlaw		   是否显示相关外规
	 * @param showrule	       是否显示相关内规
	 * @param showrisk	       是否显示风险要点（风险点）
	 * @param hvs_activitys   该文件的所有环节信息
	 * @param hvs_opereqs     该文件关联的所有操作要求信息
	 * @param hvs_laws		   该文件关联的所有外规信息
	 * @param hvs_rules	   该文件关联的所有制度信息
	 * @param hvs_risks	   该文件关联的所有风险点信息
	 * @param _ishandbook	   是否是安徽版本的合规手册的格式
	 * @throws Exception
	 */
	private void addActivityDescTable2(int column_activiy, String processid, boolean showopereq, boolean showlaw, boolean showrule, boolean showrisk, HashVO[] hvs_activitys, HashVO[] hvs_opereqs, HashVO[] hvs_laws, HashVO[] hvs_rules, HashVO[] hvs_risks, boolean _ishandbook, Chapter chapter_desc) throws Exception {
		Table table_activity = new Table(column_activiy);
		if (column_activiy == 2) {
			table_activity.setWidths(new int[] { 1, 4 });//设置每列所占比例 
		} else if (column_activiy == 3) {
			table_activity.setWidths(new int[] { 1, 3, 3 });//设置每列所占比例 
		} else if (column_activiy == 4) {
			table_activity.setWidths(new int[] { 2, 5, 5, 5 });//设置每列所占比例 
		} else if (column_activiy == 5) {
			table_activity.setWidths(new int[] { 1, 1, 2, 2, 2 });//设置每列所占比例 
		} else if (column_activiy == 6) {
			table_activity.setWidths(new int[] { 2, 2, 3, 7, 5, 5 });//设置每列所占比例 
		} else if (column_activiy == 7) {
			table_activity.setWidths(new int[] { 2, 2, 3, 7, 4, 4, 4 });//设置每列所占比例 
		}
		table_activity.setWidth(100); // 占页面宽度 100% 
		table_activity.setPadding(5);
		//设置环节信息表头 
		Cell cell_1 = new Cell(new Phrase("阶段/环节", littletitleFont));
		cell_1.setHeader(true);//将该单元格作为表头信息显示
		cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell_1.setVerticalAlignment(Element.ALIGN_CENTER);
		cell_1.setBackgroundColor(new Color(204, 204, 204));
		if (showlaw && showrule) {
			cell_1.setRowspan(2);
		}
		table_activity.addCell(cell_1);

		if (showopereq) {//环节相关的操作规范是否显示
			Cell cell_2 = new Cell(new Phrase("部门/岗位", littletitleFont));
			Cell cell_3 = new Cell(new Phrase("职责", littletitleFont));
			Cell cell_4 = new Cell(new Phrase("操作规范", littletitleFont));

			cell_2.setHeader(true);
			cell_3.setHeader(true);
			cell_4.setHeader(true);

			cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);

			cell_2.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_3.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);

			cell_2.setBackgroundColor(new Color(204, 204, 204));
			cell_3.setBackgroundColor(new Color(204, 204, 204));
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			if (showlaw && showrule) {
				cell_2.setRowspan(2);
				cell_3.setRowspan(2);
				cell_4.setRowspan(2);
			}

			table_activity.addCell(cell_2);
			table_activity.addCell(cell_3);
			table_activity.addCell(cell_4);
		}
		if (showrisk) {
			Cell cell_3 = new Cell(new Phrase("风险要点", littletitleFont));
			cell_3.setHeader(true);
			cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_3.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_3.setBackgroundColor(new Color(204, 204, 204));
			if (showlaw && showrule) {
				cell_3.setRowspan(2);
			}
			table_activity.addCell(cell_3);
		}
		if (showlaw && showrule) {
			Cell cell_4 = new Cell(new Phrase("合规要求", littletitleFont));
			cell_4.setHeader(true);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			cell_4.setColspan(2);
			table_activity.addCell(cell_4);

			Cell cell_5 = new Cell(new Phrase("法规", littletitleFont));
			cell_5.setHeader(true);
			cell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_5.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_5.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_5);

			Cell cell_6 = new Cell(new Phrase("制度", littletitleFont));
			cell_6.setHeader(true);
			cell_6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_6.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_6.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_6);
		} else if (showlaw) {
			Cell cell_4 = new Cell(new Phrase("相关法规", littletitleFont));
			cell_4.setHeader(true);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_4);
		} else if (showrule) {
			Cell cell_4 = new Cell(new Phrase("相关制度", littletitleFont));
			cell_4.setHeader(true);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_4);
		}

		table_activity.endHeaders();//设置当表格跨页后，显示表头信息 
		boolean hasruleitem = tbUtil.getSysOptionBooleanValue("制度是否分条目", true);
		StringBuffer sb_activity = new StringBuffer();
		//环节信息表格除表头外的内容for循环开始
		for (int j = 0; j < hvs_activitys.length; j++) {
			if (!processid.equals(hvs_activitys[j].getStringValue("processid"))) {
				continue;
			}
			sb_activity.delete(0, sb_activity.length());
			String belongstationgroup = hvs_activitys[j].getStringValue("belongstationgroup");
			if (belongstationgroup != null && !"".equals(belongstationgroup.trim())) {
				sb_activity.append(belongstationgroup);
				sb_activity.append("/");
			}
			sb_activity.append(hvs_activitys[j].getStringValue("wfname"));
			Cell cell_item1 = new Cell(new Phrase(sb_activity.toString().replaceAll("\n", ""), littletitleFont2));//东阳农信联社要求这里阶段名称要加粗【李春娟/2012-07-10】
			table_activity.addCell(cell_item1);//第一列：阶段/环节

			String activityid = hvs_activitys[j].getStringValue("id");
			if (showopereq) {
				boolean iffind = false;
				for (int m = 0; m < hvs_opereqs.length; m++) {
					if (activityid.equals(hvs_opereqs[m].getStringValue("wfactivity_id"))) {
						String deptandpost = hvs_opereqs[m].getStringValue("operatedept");
						String str_post = hvs_opereqs[m].getStringValue("operatepost");
						if (deptandpost == null && str_post == null) {//单选，如果操作部门和岗位都为空的话，输出无
							deptandpost = "";
						} else if (deptandpost == null) {
							deptandpost = str_post;
						} else if (deptandpost != null && str_post != null) {
							deptandpost = deptandpost + "/" + str_post;
						}

						String refpost = hvs_opereqs[m].getStringValue("operaterefpost", "");//相关岗位
						if (!"".equals(refpost)) {
							refpost = tbUtil.getInCondition(tbUtil.split(refpost, ";"));
							refpost = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + refpost + ")")).replaceAll("'", "");
							if (!"".equals(refpost) && !"-99999".equals(refpost)) {
								if ("".equals(deptandpost)) {
									deptandpost = "相关岗位：" + refpost;//如果有相关岗位就加上，没有就不加了
								} else {
									deptandpost = deptandpost + "\n相关岗位：" + refpost;
								}
							}
						}
						table_activity.addCell(new Phrase(deptandpost, contextFont));//第二列：部门-岗位 ,相关岗位
						table_activity.addCell(new Phrase(hvs_opereqs[m].getStringValue("operatedesc", ""), contextFont));//第三列：职责
						table_activity.addCell(new Phrase(hvs_opereqs[m].getStringValue("operatereq", ""), contextFont));//第四列：操作规范
						iffind = true;
						break;
					}
				}
				if (!iffind) {
					table_activity.addCell("");//如果没有数据，加空行
					table_activity.addCell("");//如果没有数据，加空行
					table_activity.addCell("");//如果没有数据，加空行
				}
			}
			if (showrisk) {
				Phrase paragraph_risk = new Phrase();//风险要点
				int n = 1;
				for (int m = 0; m < hvs_risks.length; m++) {
					if (!activityid.equals(hvs_risks[m].getStringValue("wfactivity_id"))) {
						continue;
					}
					paragraph_risk.add(new Chunk((n++) + "、风险描述：", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("riskdescr", "无") + "\n", contextFont));
					String rank = hvs_risks[m].getStringValue("rank", "无");
					if ("低风险".equals(rank) || "极小风险".equals(rank)) {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont1));
					} else if ("中等风险".equals(rank)) {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk("中等风险\n", rankFont2));
					} else if ("高风险".equals(rank) || "极大风险".equals(rank)) {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont3));
					} else {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk("无\n", contextFont));
					}
					paragraph_risk.add(new Chunk("风险分类：", littletitleFont2));//萧达仁提出，风险分类是必输项，为增强文件完整性，需要导出时显示出来【李春娟/2015-07-09】
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("risktype", "无") + "\n", contextFont));

					paragraph_risk.add(new Chunk("控制措施：", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("ctrlfn3", "无") + "\n", contextFont));//控制措施
					paragraph_risk.add(new Chunk("控制部门/岗位：", littletitleFont2));
					String depts = hvs_risks[m].getStringValue("ctrldept");
					String posts = hvs_risks[m].getStringValue("ctrlpost");
					String deptandpost = "";
					if (depts == null && posts == null) {//
						deptandpost = "无\n";//多选
					} else if (depts == null) {
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = (posts.equals("-99999") ? "无" : posts) + "\n";
					} else if (posts == null) {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						deptandpost = (depts.equals("-99999") ? "无" : depts) + "\n";
					} else {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = depts + "/" + posts + "\n";
					}
					paragraph_risk.add(new Chunk(deptandpost, contextFont));//多选
				}
				table_activity.addCell(paragraph_risk);//第三列：风险要点
			}

			if (showlaw) {
				Phrase paragraph_law = new Phrase();//相关法规
				int num_law = 1;
				for (int m = 0; m < hvs_laws.length; m++) {
					if (!activityid.equals(hvs_laws[m].getStringValue("wfactivity_id"))) {
						continue;
					}
					if (num_law > 1 && hvs_laws[m].getStringValue("law_id").equals(hvs_laws[m - 1].getStringValue("law_id"))) {
						paragraph_law.add(new Chunk(";" + hvs_laws[m].getStringValue("lawitem_title", "无"), contextFont));
					} else {
						paragraph_law.add(new Chunk((num_law++) + "、法规名称：", littletitleFont2));
						paragraph_law.add(new Chunk(hvs_laws[m].getStringValue("law_name", "无") + "\n", contextFont));
						paragraph_law.add(new Chunk("法规条文标题：", littletitleFont2));
						paragraph_law.add(new Chunk(hvs_laws[m].getStringValue("lawitem_title", "无") + "\n", contextFont));
					}
				}
				table_activity.addCell(paragraph_law);//第四列：相关法规
			}
			if (showrule) {
				Phrase paragraph_rule = new Phrase();//相关制度
				int num_rule = 1;
				if (hasruleitem) {//制度是否分条文
					for (int m = 0; m < hvs_rules.length; m++) {
						if (!activityid.equals(hvs_rules[m].getStringValue("wfactivity_id"))) {
							continue;
						}
						if (num_rule > 1 && hvs_rules[m].getStringValue("rule_id").equals(hvs_rules[m - 1].getStringValue("rule_id"))) {
							paragraph_rule.add(new Chunk(";" + hvs_rules[m].getStringValue("ruleitem_title", "无"), contextFont));
						} else {
							paragraph_rule.add(new Chunk((num_rule++) + "、制度名称：", littletitleFont2));
							paragraph_rule.add(new Chunk(hvs_rules[m].getStringValue("rule_name", "无") + "\n", contextFont));
							paragraph_rule.add(new Chunk("制度条文标题：", littletitleFont2));
							paragraph_rule.add(new Chunk(hvs_rules[m].getStringValue("ruleitem_title", "无") + "\n", contextFont));
						}
					}
				} else {
					for (int m = 0; m < hvs_rules.length; m++) {
						if (!activityid.equals(hvs_rules[m].getStringValue("wfactivity_id"))) {
							continue;
						}
						paragraph_rule.add(new Chunk((num_rule++) + "、" + hvs_rules[m].getStringValue("rule_name", "") + "\n", contextFont));
					}
				}
				table_activity.addCell(paragraph_rule);//第五列：相关制度
			}

		}//环节信息表格除表头外的内容for循环结束
		if (_ishandbook) {
			chapter_desc.add(table_activity);//添加流程图后的环节信息表格
			document.setPageSize(PageSize.A4.rotate());
			afterRotate();
			document.add(chapter_desc);
		} else {
			chapter2.add(table_activity);//添加流程图后的环节信息表格
		}
	}

	/**
	 * 简约风格的环节信息表格，最多3列，一般情况都显示，由正文发布版本时调用。
	 * 第一列：阶段，同环节的阶段要合并；
	 * 第二列：如果根据该文件的文件类型，判断是有流程的，则显示列名为“流程描述与控制要求”（环节名称、操作部门/岗位、职责和操作规范合并为一列），否则显示“管理/操作要求”；
	 * 第三列：风险提示（风险点的相关信息，包括风险描述、风险等级、控制措施、控制部门/岗位）。
	 * @param column_activiy  表格列数
	 * @param processid	   流程id
	 * @param showopereq	   是否显示职责及操作规范
	 * @param showrisk	       是否显示风险要点（风险点）
	 * @param hvs_activitys   该文件的所有环节信息
	 * @param hvs_opereqs     该文件关联的所有操作要求信息
	 * @param hvs_risks	   该文件关联的所有风险点信息
	 * @param _ishandbook	   是否是安徽版本的合规手册的格式
	 * @throws Exception
	 */
	private void addActivityDescTable(int column_activiy, String processid, boolean showopereq, boolean showrisk, HashVO[] hvs_activitys, HashVO[] hvs_opereqs, HashVO[] hvs_risks, boolean _ishandbook, Chapter chapter_desc) throws Exception {
		Table table_activity = null;
		if (!hasStation && cmpfiletype_code.contains("无流程")) {//流程导出word时无流程表格是否显示阶段
			column_activiy -= 1;//去掉第一列阶段
			table_activity = new Table(column_activiy);
			if (column_activiy == 1) {
				table_activity.setWidths(new int[] { 4 });//设置每列所占比例 
			} else if (column_activiy == 2) {
				table_activity.setWidths(new int[] { 4, 2 });//设置每列所占比例 
			}
		} else {
			table_activity = new Table(column_activiy);
			if (column_activiy == 2) {
				table_activity.setWidths(new int[] { 1, 4 });//设置每列所占比例 
			} else if (column_activiy == 3) {
				table_activity.setWidths(new int[] { 1, 4, 2 });//设置每列所占比例 
			}
		}

		table_activity.setWidth(100); // 占页面宽度 100% 
		table_activity.setPadding(5);

		if (hasStation || cmpfiletype_code.contains("有流程")) {
			//设置环节信息表头 
			Cell cell_1 = new Cell(new Phrase("阶段", littletitleFont));
			cell_1.setHeader(true);//将该单元格作为表头信息显示
			cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_1.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_1.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_1);
		}
		if (showopereq) {//环节相关的操作规范是否显示
			Cell cell_2 = null;
			if (cmpfiletype_code.contains("有流程")) {//如果文件分类中编码为有流程则显示列名为“流程描述与控制要求”，否则显示“管理/操作要求”，但表格里的内容不变
				cell_2 = new Cell(new Phrase("流程描述与控制要求", littletitleFont));
			} else {
				cell_2 = new Cell(new Phrase("管理/操作要求", littletitleFont));
			}
			cell_2.setHeader(true);
			cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_2.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_2.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_2);
		}
		if (showrisk) {
			Cell cell_3 = new Cell(new Phrase("风险提示", littletitleFont));
			cell_3.setHeader(true);
			cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_3.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_3.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_3);
		}

		table_activity.endHeaders();//设置当表格跨页后，显示表头信息 

		Cell stationCell = null;
		int rows = 1;
		//环节信息表格除表头外的内容for循环开始
		for (int j = 0; j < hvs_activitys.length; j++) {
			if (!processid.equals(hvs_activitys[j].getStringValue("processid"))) {
				continue;
			}
			if (hasStation || cmpfiletype_code.contains("有流程")) {
				String belongstationgroup = hvs_activitys[j].getStringValue("belongstationgroup", "");
				if (rows > 1) {
					rows--;
				} else {
					for (int m = j; m < hvs_activitys.length - 1; m++) {
						if (!processid.equals(hvs_activitys[m + 1].getStringValue("processid"))) {//如果下一环节的流程id跟本流程id不一致，则跳出循环，因为是先按流程id排序的
							break;
						}
						if (hvs_activitys[m].getStringValue("belongstationgroup", "").equals(hvs_activitys[m + 1].getStringValue("belongstationgroup", ""))) {
							rows++;
						} else {
							break;
						}
					}
					stationCell = new Cell(new Phrase(belongstationgroup, littletitleFont));
					if (rows > 1) {
						stationCell.setRowspan(rows);
					}
					table_activity.addCell(stationCell);//第一列：阶段
				}
			}
			String activityid = hvs_activitys[j].getStringValue("id");
			if (showopereq) {
				Phrase paragraph_opereq = new Phrase();//流程描述及风险要点
				boolean iffind = false;
				for (int m = 0; m < hvs_opereqs.length; m++) {
					if (activityid.equals(hvs_opereqs[m].getStringValue("wfactivity_id"))) {
						if (cmpfiletype_code.contains("有流程")) {
							paragraph_opereq.add(new Chunk("环节名称：", littletitleFont2));
							paragraph_opereq.add(new Chunk(hvs_activitys[j].getStringValue("wfname", "") + "\n", contextFont));//从环节表中取数据
							paragraph_opereq.add(new Chunk("操作部门/岗位：", littletitleFont2));
							String deptandpost = hvs_opereqs[m].getStringValue("operatedept");
							String str_post = hvs_opereqs[m].getStringValue("operatepost");
							if (deptandpost == null && str_post == null) {//单选，如果操作部门和岗位都为空的话，输出无
								deptandpost = "无";
							} else if (deptandpost == null) {
								deptandpost = str_post;
							} else if (deptandpost != null && str_post != null) {
								deptandpost = deptandpost + "/" + str_post;
							}

							paragraph_opereq.add(new Chunk(deptandpost + "\n", contextFont));//操作部门和岗位都为单选

							String refpost = hvs_opereqs[m].getStringValue("operaterefpost", "");//相关岗位
							if (!"".equals(refpost)) {
								refpost = tbUtil.getInCondition(tbUtil.split(refpost, ";"));
								refpost = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + refpost + ")")).replaceAll("'", "");
								if (!"".equals(refpost) && !"-99999".equals(refpost)) {
									paragraph_opereq.add(new Chunk("相关岗位：" + refpost + "\n", contextFont));//如果有相关岗位就加上，没有就不加了
								}
							}
							if (!"".equals(hvs_opereqs[m].getStringValue("operatedesc", ""))) {
								paragraph_opereq.add(new Chunk("职责：", littletitleFont2));
								paragraph_opereq.add(new Chunk(hvs_opereqs[m].getStringValue("operatedesc", "无") + "\n", contextFont));
							}
							paragraph_opereq.add(new Chunk("操作规范：", littletitleFont2));
							paragraph_opereq.add(new Chunk(hvs_opereqs[m].getStringValue("operatereq", "无"), contextFont));
						} else {
							if (showActivity) {//肖达仁说，无流程的流程文件导出word，如果管理/操作要求 列不显示环节名称，不好区分写的什么内容。 他超级纠结，我本身是很不想改的！【李春娟/2014-04-09】
								paragraph_opereq.add(new Chunk(new Chunk(hvs_activitys[j].getStringValue("wfname", "") + "\n", littletitleFont2)));
							}
							paragraph_opereq.add(new Chunk(hvs_opereqs[m].getStringValue("operatereq", ""), contextFont));
						}
						iffind = true;
						break;
					}
				}
				if (!iffind) {
					if (cmpfiletype_code.contains("有流程")) {
						paragraph_opereq.add(new Chunk("环节名称：", littletitleFont2));
						paragraph_opereq.add(new Chunk(hvs_activitys[j].getStringValue("wfname", "") + "\n", contextFont));//从环节表中取数据
						paragraph_opereq.add(new Chunk("操作部门/岗位：", littletitleFont2));
						paragraph_opereq.add(new Chunk("无\n", contextFont));
						paragraph_opereq.add(new Chunk("操作规范：", littletitleFont2));
						paragraph_opereq.add(new Chunk("无", contextFont));
					}
				}
				table_activity.addCell(paragraph_opereq);//第二列：流程描述及风险要点
			}
			if (showrisk) {
				Phrase paragraph_risk = new Phrase();//风险要点
				int n = 1;
				for (int m = 0; m < hvs_risks.length; m++) {
					if (!activityid.equals(hvs_risks[m].getStringValue("wfactivity_id"))) {
						continue;
					}
					paragraph_risk.add(new Chunk((n++) + "、风险描述：", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("riskdescr", "无") + "\n", contextFont));
					String rank = hvs_risks[m].getStringValue("rank", "无");
					if ("低风险".equals(rank) || "极小风险".equals(rank)) {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont1));
					} else if ("中等风险".equals(rank)) {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk("中等风险\n", rankFont2));
					} else if ("高风险".equals(rank) || "极大风险".equals(rank)) {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont3));
					} else {
						paragraph_risk.add(new Chunk("风险等级：", littletitleFont2));
						paragraph_risk.add(new Chunk("无\n", contextFont));
					}
					paragraph_risk.add(new Chunk("风险分类：", littletitleFont2));//萧达仁提出，风险分类是必输项，为增强文件完整性，需要导出时显示出来【李春娟/2015-07-09】
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("risktype", "无") + "\n", contextFont));

					paragraph_risk.add(new Chunk("控制措施：", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("ctrlfn3", "无") + "\n", contextFont));//控制措施
					paragraph_risk.add(new Chunk("控制部门/岗位：", littletitleFont2));//多选

					String depts = hvs_risks[m].getStringValue("ctrldept");
					String posts = hvs_risks[m].getStringValue("ctrlpost");
					String deptandpost = "";
					if (depts == null && posts == null) {//
						deptandpost = "无\n";//多选
					} else if (depts == null) {
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = (posts.equals("-99999") ? "无" : posts) + "\n";
					} else if (posts == null) {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						deptandpost = (depts.equals("-99999") ? "无" : depts) + "\n";
					} else {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = depts + "/" + posts + "\n";
					}
					paragraph_risk.add(new Chunk(deptandpost, contextFont));//多选
				}
				table_activity.addCell(paragraph_risk);//第三列：风险要点
			}
		}//环节信息表格除表头外的内容for循环结束

		if (_ishandbook) {
			chapter_desc.add(table_activity);//添加流程图后的环节信息表格
			document.setPageSize(PageSize.A4.rotate());//流程概述和环节信息表格添加在章节chapter_desc中，document添加chapter_desc前要设置页面为横向
			afterRotate();
			document.add(chapter_desc);
		} else {
			chapter2.add(table_activity);//添加流程图后的环节信息表格，chapter2在此不需要加入document，因为后面chapter2可能还要加入某些元素
		}
	}

	private void addWfdescTable(Chapter chapter_wf, String processid, HashVO[] hvs_processes) throws DocumentException {
		for (int j = 0; j < hvs_processes.length; j++) {
			if (processid.equals(hvs_processes[j].getStringValue("wfprocess_id"))) {
				Table table_process = new Table(6);
				table_process.setWidths(new int[] { 1, 2, 1, 2, 1, 2 });//设置每列所占比例 
				table_process.setWidth(100);
				table_process.setPadding(5);
				Cell cell_1 = new Cell(new Phrase("流程编号", littletitleFont));
				Cell cell_2 = new Cell(new Phrase(hvs_processes[j].getStringValue("wfprocess_code", "无"), contextFont));
				Cell cell_3 = new Cell(new Phrase("流程名称", littletitleFont));
				Cell cell_4 = new Cell(new Phrase(hvs_processes[j].getStringValue("wfprocess_name", "无"), contextFont));
				Cell cell_5 = new Cell(new Phrase("编制单位", littletitleFont));
				Cell cell_6 = new Cell(new Phrase(hvs_processes[j].getStringValue("blcorpname", "无"), contextFont));
				Cell cell_7 = new Cell(new Phrase("流程概述", littletitleFont));
				Cell cell_8 = new Cell(new Phrase(hvs_processes[j].getStringValue("wfdesc", "无"), contextFont));

				cell_8.setColspan(5);
				cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_7.setHorizontalAlignment(Element.ALIGN_CENTER);

				cell_1.setVerticalAlignment(Element.ALIGN_CENTER);
				cell_3.setVerticalAlignment(Element.ALIGN_CENTER);
				cell_5.setVerticalAlignment(Element.ALIGN_CENTER);
				cell_7.setVerticalAlignment(Element.ALIGN_CENTER);

				table_process.addCell(cell_1);
				table_process.addCell(cell_2);
				table_process.addCell(cell_3);
				table_process.addCell(cell_4);
				table_process.addCell(cell_5);
				table_process.addCell(cell_6);
				table_process.addCell(cell_7);//第二行
				table_process.addCell(cell_8);//第二行
				chapter_wf.add(table_process);//添加流程概述表格
				break;//找到该流程就直接跳出循环
			}
		}
	}

	/**
	 * 如果页面变为横向的话，重新设置页眉，流程合规手册六个字要居右显示，如果用段落，后面会有个换行，但用短语又不能实现居右显示，所以用空格来控制，如果页面变为横向的话，要调用这个方法哦
	 */
	private void afterRotate() {
		Phrase ph_header = new Phrase();
		if (titleimg != null) {
			ph_header.add(new Chunk(titleimg, titleimg.getWidth(), titleimg.getHeight()));
		}
		ph_header.add(new Chunk("  " + convertStr(tbUtil.getSysOptionStringValue("流程导出word时页眉显示的内容", "    "), 55), headerFont_1));
		ph_header.add(new Chunk("流程合规手册", headerFont_2));
		HeaderFooter Header = new HeaderFooter(ph_header, false);
		document.setHeader(Header);
	}

	public String convertStr(String _oldstr, int _num) {
		if (_oldstr == null || "".equals(_oldstr)) {
			return "                        ";
		}
		int i = _oldstr.getBytes().length;
		StringBuffer sb_str = new StringBuffer(_oldstr);
		for (; i < _num; i++) {
			sb_str.append(" ");
		}
		return sb_str.toString();
	}

	public String getCmpfileid() {
		return cmpfileid;
	}

	public HashMap getWfBase64CodesMap() {
		return wfBase64CodesMap;
	}

	public HashMap getItemMap() {
		return itemMap;
	}

	public String getCmpfiletype() {
		return cmpfiletype;
	}

	public String getCmpfiletype_code() {
		return cmpfiletype_code;
	}

	public ByteArrayOutputStream getByteOutStream() {
		return byteOutStream;
	}

	public Font getFiletitleFont() {
		return filetitleFont;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public Font getUnderlinetitleFont() {
		return underlinetitleFont;
	}

	public Font getLittletitleFont() {
		return littletitleFont;
	}

	public Font getContextFont() {
		return contextFont;
	}

	public Font getRankFont1() {
		return rankFont1;
	}

	public Font getRankFont2() {
		return rankFont2;
	}

	public Font getRankFont3() {
		return rankFont3;
	}

	public Font getHeaderFont_1() {
		return headerFont_1;
	}

	public Font getHeaderFont_2() {
		return headerFont_2;
	}

	public TBUtil getTbUtil() {
		return tbUtil;
	}

	public Document getDocument() {
		return document;
	}

	public Chapter getChapter2() {
		return chapter2;
	}

	public Image getTitleimg() {
		return titleimg;
	}

	public void setChapter2(Chapter chapter2) {
		this.chapter2 = chapter2;
	}
}
