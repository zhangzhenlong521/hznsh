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
 * ��ϵ�ļ�����word����,�ǳ��ؼ�����! ����WFRiskHtmlBuilder��������ϵ�ļ�ģ��ɹ��ؼ�������Ҫ������֮һ!!
 * Ҳ���ܽ����Ժ󾭳������Ż���������!!
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
	private boolean hasStation = tbUtil.getSysOptionBooleanValue("���̵���wordʱ�����̱���Ƿ���ʾ�׶�", true);//��������̨ǰ��Ŀ�������������̵������ļ���������ʱһͼ�����ֵı��Ͳ���ʾ�׶��ˣ�������һ�ж��ǿա����/2013-06-21��
	private String process_fontstyle = tbUtil.getSysOptionStringValue("���̵���wordʱ���̱���������������ʽ", "Ĭ��");
	private boolean showActivity = tbUtil.getSysOptionBooleanValue("���̵���wordʱ�����̱���Ƿ���ʾ��������", false);//Ф����˵�������̵������ļ�����word���������/����Ҫ�� �в���ʾ�������ƣ���������д��ʲô���ݡ� ���������ᣬ�ұ����Ǻܲ���ĵģ������/2014-04-09��

	public WFRiskWordBuilder(String _cmpfileid, HashMap _wfBase64CodesMap) {
		this.cmpfileid = _cmpfileid;
		this.wfBase64CodesMap = _wfBase64CodesMap;
	}

	/**
	 * ʹ��itext����Word�ļ�������,���ص�������word��byte[]
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes() throws Exception {
		return getDocContextBytes(false); //����
	}

	/**
	 * ʹ��itext����Word�ļ�������,���ص�������word��byte[]
	 * @param _onlyWf �Ƿ�ֻ������˵��
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(boolean _onlyWf) throws Exception {
		createDoc(_onlyWf);//�����ĵ�������������ȷ��
		if (_onlyWf) {
			document.setPageSize(PageSize.A4);
			chapter2 = new Chapter(-1);
			addAllWf(false, false);//��������ͼ,���򵼳������ǺϹ��ֲ�
			document.add(chapter2);
		} else {
			addHomePage();//�����ҳ��Ϣ����һҳ��
			addSecondPage();//����޸ļ�¼���ڶ�ҳ��
			addContentPage();//����ļ�����
		}
		document.close();
		return byteOutStream.toByteArray(); //����
	}

	/**
	 * ʹ��itext����Word�ļ�������,���ص�������word��byte[]
	 * @param _onlyWf �Ƿ�ֻ������˵��
	 * @param _ishandbook �Ƿ�Ϊ���պϹ��ֲ�ķ��,������ֶ�Ϊtrue�������ж�_onlyWf
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(boolean _onlyWf, boolean _ishandbook) throws Exception {
		if (!_ishandbook) {
			return getDocContextBytes(_onlyWf);
		}
		createDoc(true);//�����ĵ�������������ȷ�񣬲���ֻ������˵������
		document.setPageSize(PageSize.A4);
		chapter2 = new Chapter(-1);
		addAllWf(false, true);//��������ͼ,������ʾ���ǺϹ��ֲ�
		document.close();
		return byteOutStream.toByteArray(); //����
	}

	/**
	 * �����ĵ�������������ȷ��
	 * @param _onlyWf �Ƿ�ֻ������ͼ
	 * @throws Exception
	 */
	private void createDoc(boolean _onlyWf) throws Exception {
		document = new Document(PageSize.A4);// ����ֽ�Ŵ�С 		
		byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������ 

		document.open();
		document.setMargins(60, 60, 80, 60);//����ҳ�߾�

		//����������
		filetitleFont = new RtfFont("��_��", 26, Font.BOLD);// �ļ��������� 
		//filetitleFont.setStyle(7);//���ø�ʽ
		titleFont = new RtfFont("��_��", 16, Font.BOLD);// ���������� 
		underlinetitleFont = new RtfFont("��_��", 16, Font.UNDERLINE);// �»��߱��������� 

		String url = this.getClass().getResource("/").toString();
		System.out.println(">>>>>>WFRiskWordBuilder��·����" + url);

		//ͨ��ϵͳ�������ã��ı�wordԤ�������ļ�һͼ������������ʽ[YangQing/2013-06-26]�����޸���������������,���������м���޷�����
		String font_style = "Ĭ��";//���ͣ����塢�����ļ�,��ʼֵ��Ĭ��12������
		String font_family = "��_��"; 
		String font_size = "";
		if (process_fontstyle.contains(";")) {//�������ֵ�а����ֺ�,˵������Ĭ��
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
		if (font_style.equals("�����ļ�")) {//ָ�������ļ�
			BaseFont basefont = BaseFont.createFont(url.substring(0, url.indexOf("WEB-INF") - 1) + "/help/" + font_family, "UTF-8", BaseFont.NOT_EMBEDDED);
			littletitleFont = new Font(basefont, 15, Font.BOLD);//�˴����Ժ��Ż����ȿ�������ͷ�����С
			littletitleFont2 = new Font(basefont, fontSize, Font.BOLD);
			contextFont = new Font(basefont, fontSize, Font.NORMAL);
			rankFont1 = new Font(basefont, fontSize, Font.NORMAL);
			rankFont2 = new Font(basefont, fontSize, Font.NORMAL);
			rankFont3 = new Font(basefont, fontSize, Font.NORMAL);
		} else {//û���������Ĭ������;����������;�������ʽ����
			littletitleFont = new RtfFont(font_family, fontSize, Font.BOLD);// С���������� ,�������
			littletitleFont2 = new RtfFont(font_family, fontSize, Font.BOLD);// С���������� ,�������
			contextFont = new RtfFont(font_family, fontSize, Font.NORMAL);// ����������,�������
			rankFont1 = new RtfFont(font_family, fontSize, Font.NORMAL);// ����������,�������
			rankFont2 = new RtfFont(font_family, fontSize, Font.NORMAL);// ����������,�������
			rankFont3 = new RtfFont(font_family, fontSize, Font.NORMAL);// ����������,�������
		}

		littletitleFont.setColor(0, 0, 0);
		littletitleFont2.setColor(0, 0, 0);
		contextFont.setColor(0, 0, 0);//��������һ�£���������յȼ���������������ɫ�����¸����յȼ�����ɫһ����
		rankFont1.setColor(51, 153, 102);//�ͷ���
		rankFont2.setColor(255, 102, 0);//�еȷ���
		rankFont3.setColor(250, 0, 0);//�߷���

		headerFont_1 = new RtfFont("��_��", 14, Font.BOLD, Color.BLACK);
		headerFont_2 = new RtfFont("��_��", 12, Font.ITALIC, Color.BLACK);
		//����ҳüҳ��
		Font footerFont = new RtfFont("��_��", 10, Font.NORMAL);
		Phrase ph_header = new Phrase();

		try {
			titleimg = Image.getInstance(url.substring(0, url.indexOf("WEB-INF") - 1) + "/images/logo.png");//��˾logo���޸�Ϊpng���ͣ���͸��Ч������������logo���߶ȵĲ��������/2014-02-20��
			float li_height = titleimg.getHeight();
			float max_height = tbUtil.getSysOptionIntegerValue("���̵���wordʱ����ҳülogo���߶�", 30);//��֤�߶����30���������������������������߶Ȳ���Ҫ���������/2012-11-13��
			if (li_height > max_height) {
				titleimg.scalePercent(max_height * 100 / li_height);
			}
			titleimg.setAlignment(Element.ALIGN_LEFT);

			ph_header.add(new Chunk(titleimg, titleimg.getWidth(), titleimg.getHeight()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//�ļ�����
		cmpfiletype = commDMO.getStringValueByDS(null, "select cmpfiletype from v_cmp_cmpfile where id=" + this.cmpfileid);
		cmpfiletype_code = commDMO.getStringValueByDS(null, "select code from PUB_COMBOBOXDICT where type='�ļ�����' and name='" + cmpfiletype + "'");
		if (_onlyWf) {//���ֻ�������������ʾ
			ph_header.add(new Chunk("  " + convertStr(tbUtil.getSysOptionStringValue("���̵���wordʱҳü��ʾ������", "    "), 24), headerFont_1));
		} else {
			ph_header.add(new Chunk("  " + convertStr(tbUtil.getSysOptionStringValue("���̵���wordʱҳü��ʾ������", "    "), 24), headerFont_1));
		}
		//ph_header.add(new Chunk("���̺Ϲ��ֲ�", headerFont_2));//�ڿ�ϵͳ���������ڿ��ֲ���ǺϹ��ֲᣬ�ʸɴ�ȥ�������/2012-11-13��
		HeaderFooter Header = new HeaderFooter(ph_header, false);
		document.setHeader(Header);//����ҳü

		HeaderFooter footer = new HeaderFooter(new Phrase("��", footerFont), new Phrase("ҳ", footerFont));
		footer.setBorder(Rectangle.NO_BORDER);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);//����ҳ��
	}

	/**
	 * �����ҳ��Ϣ��������˾logo���ļ��������Ƶ�λ���������ڣ���Ч���ڵ���Ϣ
	 * @throws Exception
	 */
	private void addHomePage() throws Exception {
		HashVO[] hvs_cmpfile = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id=" + cmpfileid + " order by id"); //��ѯ�����ļ��������Ϣ

		//		String url = this.getClass().getResource("/").toString();
		//		Image titleimg = Image.getInstance(url.substring(0, url.indexOf("WEB-INF") - 1) + "/images/logo.jpg");//��˾logo
		//		document.add(titleimg);

		Paragraph filetitle = new Paragraph(hvs_cmpfile[0].getStringValue("cmpfilename"), filetitleFont);//�ļ���
		filetitle.setAlignment(Element.ALIGN_CENTER);// ���ñ����ʽ���뷽ʽ 
		filetitle.setLeading(240);//��ǰ�εļ��
		document.add(filetitle);

		Paragraph title_record = new Paragraph("�Ϲ������ļ����룺" + hvs_cmpfile[0].getStringValue("cmpfilecode", ""), titleFont);//�ļ����
		title_record.setAlignment(Element.ALIGN_CENTER);// ���ñ����ʽ���뷽ʽ 
		document.add(title_record);

		Chunk chunk1 = new Chunk("���Ƶ�λ��", titleFont);
		Chunk chunk2 = new Chunk(convertStr(hvs_cmpfile[0].getStringValue("blcorpname"), 24), underlinetitleFont);
		Paragraph title_dept = new Paragraph();//���Ƶ�λ
		title_dept.setFirstLineIndent(110);//�ö���������
		title_dept.setSpacingBefore(120);
		title_dept.setLeading(120);//��ǰ�εļ��
		title_dept.add(chunk1);
		title_dept.add(chunk2);
		document.add(title_dept);

		Chunk chunk3 = new Chunk("�������ڣ�", titleFont);
		Chunk chunk4 = new Chunk(convertStr(hvs_cmpfile[0].getStringValue("publishdate"), 24), underlinetitleFont);

		Paragraph title_publishdate = new Paragraph();//���Ƶ�λ
		title_publishdate.setFirstLineIndent(110);//�ö���������
		title_publishdate.setLeading(30);
		title_publishdate.add(chunk3);
		title_publishdate.add(chunk4);
		document.add(title_publishdate);

		Chunk chunk5 = new Chunk("��Ч���ڣ�", titleFont);
		Chunk chunk6 = new Chunk(convertStr("", 24), underlinetitleFont);

		Paragraph title_effectivedate = new Paragraph();//���Ƶ�λ
		title_effectivedate.setFirstLineIndent(110);//���øö���������������
		title_effectivedate.setLeading(30);//
		title_effectivedate.add(chunk5);
		title_effectivedate.add(chunk6);
		document.add(title_effectivedate);
	}

	/**
	 * ��ӵڶ�ҳ��Ϣ�����޸ļ�¼
	 * @throws Exception
	 */
	private void addSecondPage() throws Exception {
		Paragraph editrecord = new Paragraph("�޸ļ�¼", littletitleFont);//�޸ļ�¼���ڶ�ҳ��
		editrecord.setAlignment(Element.ALIGN_CENTER);
		Chapter chapter1 = new Chapter(editrecord, 0);

		// ���� Table ��� 
		Table table_editrecord = new Table(4);
		int width[] = { 20, 25, 25, 50 };
		table_editrecord.setWidths(width);//����ÿ����ռ���� 
		table_editrecord.setWidth(90); // ռҳ���� 90% 
		table_editrecord.setPadding(5);

		//���ñ�ͷ 
		Cell cell1 = new Cell(new Phrase("���", littletitleFont));
		Cell cell2 = new Cell(new Phrase("�޸�����", littletitleFont));
		Cell cell3 = new Cell(new Phrase("�汾��", littletitleFont));
		Cell cell4 = new Cell(new Phrase("�޸�ԭ����޸�������ʾ", littletitleFont));

		cell1.setHeader(true);//���õ�Ԫ����Ϊ��ͷ��Ϣ��ʾ
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
		table_editrecord.endHeaders();//���õ�����ҳ����ʾ��ͷ��Ϣ 

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
		for (int i = 0; i < 30 - histcount; i++) {//�����ʷ��¼����30��������ӿ���
			for (int j = 0; j < 4; j++) {
				Cell cell = new Cell(new Phrase("", contextFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table_editrecord.addCell(cell);
			}
		}
		chapter1.add(table_editrecord);
		document.add(chapter1);//��ӵ�һ�� �޸ļ�¼���������һ�޸ļ�¼��		
	}

	/**
	 * ����ļ����������ݣ�����Ŀ�ģ����÷�Χ�����塢��д�ͷ��ְ࣬����Ȩ�ޣ�����ԭ������˵��������ļ�����¼����Ϣ
	 * @throws Exception
	 */
	private void addContentPage() throws Exception {
		itemMap = new WFRiskBSUtil().getCmpFileItemDocument(cmpfileid, false);//����һ�������ļ��еĸ��ı�����ʵ�ʶ�Ӧ��Document�������û�и�ʽ�򷵻��ַ���
		String show_item = tbUtil.getSysOptionStringValue("��ϵ����_�����ļ��������ʾ", null);
		int num = 1;//�ڼ���
		String[] show_items = null;
		if (show_item == null) {//�ж��Ƿ��������������ʾ�����û����������ʾ���У���������ˣ��������ж��Ƿ���ʾ
			addOneItem(1, "Ŀ��", "item_target");
			addOneItem(2, "���÷�Χ", "item_userarea");
			addOneItem(3, "���塢��д�ͷ���", "item_keywords");
			addOneItem(4, "ְ����Ȩ��", "item_duty");
			addOneItem(5, "����ԭ��", "item_bsprcp");
			num = 6;
			//���̼��������ļ�����¼�����
		} else {//�������ж��Ƿ���ʾ
			show_items = tbUtil.split(show_item, ";");
			if ("Y".equals(show_items[0])) {
				addOneItem(num, "Ŀ��", "item_target");
				num++;
			}
			if ("Y".equals(show_items[1])) {
				addOneItem(num, " ���÷�Χ", "item_userarea");
				num++;
			}
			if ("Y".equals(show_items[2])) {
				addOneItem(num, "���塢��д�ͷ���", "item_keywords");
				num++;
			}
			if ("Y".equals(show_items[3])) {
				addOneItem(num, "ְ����Ȩ��", "item_duty");
				num++;
			}
			if ("Y".equals(show_items[4])) {
				addOneItem(num, "����ԭ��", "item_bsprcp");
				num++;
			}
		}
		//ͳһ�������̼��������ļ�����¼,Ĭ������˵���������
		if (chapter2 == null) {
			chapter2 = new Chapter(new Paragraph("����˵��", littletitleFont), num);
		} else {
			chapter2.add(new Paragraph(num + ". ����˵��", littletitleFont));
		}
		addAllWf(false, false);//��������ͼ,�Ǻ��򵼳������ǺϹ��ֲ�
		num++;

		if (show_items != null && "Y".equals(show_items[5])) {
			chapter2.add(new Chunk("\n"));
			chapter2.add(new Paragraph(num + ". ����ļ�����¼", littletitleFont));//
			String str_item_addenda = (String) itemMap.get("item_addenda");
			Paragraph addenda_content2 = new Paragraph(str_item_addenda, contextFont);
			addenda_content2.setFirstLineIndent(30);
			chapter2.add(addenda_content2);
		}
		document.add(chapter2);
	}

	/**
	 * ����һ�����ı�����
	 * @param _chapter ��
	 * @param _title ����
	 * @param _itemkey ��Ӧ�ֶ�
	 * @return
	 */
	private void addOneItem(int num, String _title, String _itemkey) {
		if (chapter2 == null) {
			chapter2 = new Chapter(new Paragraph(_title, littletitleFont2), num);//����
		} else {
			chapter2.add(new Paragraph(num + ". " + _title, littletitleFont2));//����
		}
		if (itemMap.get(_itemkey) instanceof String) {
			String str_item = (String) itemMap.get(_itemkey);
			chapter2.add(new Paragraph(str_item + "\n", contextFont));//����
		} else {
			DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get(_itemkey);
			StyleDocumentConverter converter = new StyleDocumentConverter(doc);
			converter.toWord(chapter2);
			chapter2.add(new Chunk("\n"));//һ������
		}
	}

	/**
	 * ����˵�����������ݣ���������ͼ�����̸������ͻ�����Ϣ���
	 * @param _isHorizontal ҳ���Ƿ������ʾ
	 * @param _ishandbook   �Ƿ��ǰ��հ汾�ĺϹ��ֲ�ĸ�ʽ
	 * @throws Exception
	 */
	private void addAllWf(boolean _isHorizontal, boolean _ishandbook) throws Exception {
		if (wfBase64CodesMap == null) {//���û�����̣���ֱ�ӷ���
			return;
		}
		String show_processref1 = tbUtil.getSysOptionHashItemStringValue("��ϵ����_һͼ�����Զ����ʽ��", "WORD", null);//��Ŀ�п��Զ���һͼ��������word��html�ĸ�ʽ�����/2012-05-21��
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

		String show_processref = tbUtil.getSysOptionStringValue("��ϵ����_�������", null);
		boolean showwfdesc = true;
		if (show_processref == null || "".equals(show_processref.trim()) || "Y".equals(show_processref.substring(0, 1))) {//���û�����û�������Ϊ��ʾ
			hvs_processes = commDMO.getHashVoArrayByDS(null, "select process.wfprocess_id,process.wfprocess_code,process.wfprocess_name,process.blcorpname,wfdesc.wfdesc from v_process_file process left join cmp_cmpfile_wfdesc wfdesc on process.wfprocess_id=wfdesc.wfprocess_id where process.cmpfile_id=" + cmpfileid);//
		} else {
			showwfdesc = false;
		}
		String show_activityref = tbUtil.getSysOptionStringValue("��ϵ����_�������", null);
		boolean showopereq = true;//�Ƿ������������������淶
		boolean showlaw = true;//�Ƿ�����ط���
		boolean showrule = true;//�Ƿ�������ƶ�
		boolean showrisk = true;//�Ƿ��з���Ҫ�㣨���յ㣩
		if (show_activityref == null || "".equals(show_activityref.trim())) {
			hvs_opereqs = commDMO.getHashVoArrayByDS(null, "select dept.name operatedept,post.name operatepost,opereq.operaterefpost,opereq.operatedesc,opereq.operatereq,opereq.wfactivity_id from cmp_cmpfile_wfopereq opereq left join pub_corp_dept dept on opereq.operatedept=dept.id left join pub_post post on opereq.operatepost=post.id  where opereq.cmpfile_id =" + cmpfileid
					+ " order by opereq.id"); //��ѯ�������ļ����е���Ҫ�����Ĳ����淶
			hvs_laws = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_law where cmpfile_id =" + cmpfileid + " order by wfactivity_id,law_id,id"); //��ѯ�������ļ����л��ڵ���ط��棬�����ֶ� wfactivity_id,law_id������
			hvs_rules = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_rule where cmpfile_id =" + cmpfileid + " order by wfactivity_id,rule_id,id"); //��ѯ�������ļ����л��ڵ�����ƶȣ������ֶ� wfactivity_id,rule_id������
			hvs_risks = commDMO.getHashVoArrayByDS(null, "select * from cmp_risk where cmpfile_id =" + cmpfileid + " order by id"); //��ѯ�������ļ����л��ڵķ���Ҫ��
		} else {
			String[] show_activityrefs = tbUtil.split(show_activityref, ";");
			if ("Y".equals(show_activityrefs[0])) {
				hvs_opereqs = commDMO.getHashVoArrayByDS(null, "select dept.name operatedept,post.name operatepost,opereq.operaterefpost,opereq.operatedesc,opereq.operatereq,opereq.wfactivity_id from cmp_cmpfile_wfopereq opereq left join pub_corp_dept dept on opereq.operatedept=dept.id left join pub_post post on opereq.operatepost=post.id where opereq.cmpfile_id =" + cmpfileid
						+ " order by opereq.id"); //��ѯ�������ļ����е���Ҫ�����Ĳ����淶
			} else {
				showopereq = false;
			}
			if ("Y".equals(show_activityrefs[1])) {
				hvs_laws = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_law where cmpfile_id =" + cmpfileid + " order by wfactivity_id,law_id,id"); //��ѯ�������ļ����л��ڵ���ط���
			} else {
				showlaw = false;
			}
			if ("Y".equals(show_activityrefs[2])) {
				hvs_rules = commDMO.getHashVoArrayByDS(null, "select * from cmp_cmpfile_rule where cmpfile_id =" + cmpfileid + " order by wfactivity_id,rule_id,id"); //��ѯ�������ļ����л��ڵ�����ƶ�
			} else {
				showrule = false;
			}
			//if ("Y".equals(show_activityrefs[5])) {
			//�����ǺϹ�ϵͳ�����ڿ�ϵͳ����Ȼ�з��յ㣬�ڿ�ϵͳ�кܶ������д��ά�����յ���߼���������ֱ����ʾ������ʾ���������ж��ˣ��ж�ֻ��һͼ��������صķ��յ����ʾ�����á����/2012-07-10��
			hvs_risks = commDMO.getHashVoArrayByDS(null, "select * from cmp_risk where cmpfile_id =" + cmpfileid + " order by id"); //��ѯ�������ļ����л��ڵķ���Ҫ��
			//} else {
			//	showrisk = false;
			//}
		}

		int column_activiy = 1;//������Ϣ��������
		boolean show_activitystyle = tbUtil.getSysOptionBooleanValue("�����ļ�����wordʱ���ڱ���Ƿ�Ϊ��Լ���", true);
		if (_ishandbook) {//����ǺϹ��ֲ���Ĭ��Ϊ�Ǽ�Լ���
			show_activitystyle = false;//Ĭ��Ϊ�Ǽ�Լ���
		}
		String dbType = ServerEnvironment.getInstance().getDefaultDataSourceType().toUpperCase();//ȡ�����ݿ�����,��ΪҪ������ǰϵͳ�������ԡ����ڡ���ͷ�����⣬ʹ����cast()����������sqlֻ����mysqlִ�У��Ժ�Ҫ�ж����ݿ⣡����

		boolean show_judgeactivity = tbUtil.getSysOptionBooleanValue("�����ļ�����wordʱ�жϻ����Ƿ񵼳�", false);//��ǰ����Ŀ�����жϻ���¼����յ������Ϣ�ģ�����Ҫ�������ж��Ƿ񵼳������/2012-06-12��
		if (dbType.equalsIgnoreCase("MYSQL")) {//act.uiname ��������������λ�������⣡
			hvs_activitys = commDMO.getHashVoArrayByDS(null, "select act.id,act.wfname,act.belongstationgroup,act.processid from pub_wf_activity act left join pub_wf_group grp on act.processid = grp.processid and act.belongstationgroup = grp.wfname where act.processid in(" + tbUtil.getInCondition(str_imgKeys) + ") " + (show_judgeactivity ? "" : "and act.viewtype <>3")
					+ " and act.viewtype <>7 order by act.processid,grp.y,cast(act.uiname as signed)");//��ѯ�������ļ����з��ж����͵Ļ�����Ϣ�������ֶ�processid,belongstationgroup�����С�һ����Ŀ�������������ĵ����ڣ�Ҳ����Ҫ���������/2012-03-31��
		} else if (dbType.equalsIgnoreCase("ORACLE") || dbType.equalsIgnoreCase("DB2")) {//���뱣֤����uiname��Ϊ�մ���û�����֣�����Ŀ��û������ģ�ƽ̨����������Ϊ���֣�
			hvs_activitys = commDMO.getHashVoArrayByDS(null, "select act.id,act.wfname,act.belongstationgroup,act.processid from pub_wf_activity act left join pub_wf_group grp on act.processid = grp.processid and act.belongstationgroup = grp.wfname where act.processid in(" + tbUtil.getInCondition(str_imgKeys) + ") " + (show_judgeactivity ? "" : "and act.viewtype <>3")
					+ " and act.viewtype <>7 order by act.processid,grp.y,to_number(act.uiname)");
		} else {//SQLSERVER��
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
		if (!show_activitystyle && showlaw) {//��Լ�����û�з�����ƶȵġ�
			column_activiy++;
		}
		if (!show_activitystyle && showrule) {
			column_activiy++;
		}

		//����ͼ�ͻ�����Ϣ����forѭ����ʼ
		for (int i = 0; i < str_imgKeys.length; i++) { //������ϣ���е�����ֵ!!
			Chapter chapter_desc = new Chapter(-1);
			String processid = str_imgKeys[i]; //
			if (cmpfiletype_code.contains("������")) {//����ļ������б���Ϊ�������򵼳�����ͼ����֮����Ϊ�����̾Ͳ���������ͼ
				byte[] imgBytes = (byte[]) wfBase64CodesMap.get(processid);
				imgBytes = tbUtil.decompressBytes(imgBytes); //��ѹ!!!
				Image img = Image.getInstance(imgBytes);
				img.setAlignment(Image.MIDDLE);//����ͼƬ��ʾλ�� 
				float li_width = img.getWidth();
				float li_height = img.getHeight();
				float max_width = 430;
				float max_height = 600;//����һҳ�ĸ߶�
				if (_isHorizontal) {
					max_width = 700;
					max_height = 360;
				}
				if (li_width > max_width || li_height > max_height) {
					float percent = 0;
					if (li_width / max_width > li_height / max_height) {
						percent = max_width / li_width * 100;//��ʾ��ʾ�Ĵ�СΪԭ�ߴ�İٷ�֮��
						if (percent < 60) {
							img.scalePercent(percent + percent / 10, (percent + 20 > 100 ? 100 : percent + 20));//��ı���Ҫ�ȸ�СЩ��Ҳ������С�ı���Ҫ�ȸ߸�СЩ
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
					document.setPageSize(PageSize.A4);//����ͼ��������ģ���Ҫ����һ�£���Ϊ��������к���ı��
					document.add(chapter_wf);
					if (showwfdesc && _ishandbook) {//�����ʾ���̸���������ǺϹ��ֲ�����ʾ���̸������,�������ʾ
						addWfdescTable(chapter_desc, processid, hvs_processes);
					}
				} else {
					chapter2.add(img);
					chapter2.newPage();
					if (showwfdesc && _ishandbook) {//�����ʾ���̸���������ǺϹ��ֲ�����ʾ���̸������
						addWfdescTable(chapter2, processid, hvs_processes);
					}
				}
			}
			if (show_activitystyle && column_activiy > 1) {
				addActivityDescTable(column_activiy, processid, showopereq, showrisk, hvs_activitys, hvs_opereqs, hvs_risks, _ishandbook, chapter_desc);//��ӻ�����Ϣ���
			} else if (column_activiy > 1) {
				addActivityDescTable2(column_activiy, processid, showopereq, showlaw, showrule, showrisk, hvs_activitys, hvs_opereqs, hvs_laws, hvs_rules, hvs_risks, _ishandbook, chapter_desc);//��ӻ�����Ϣ���
			}
		}//����ͼ�ͻ�����Ϣ����forѭ������
	}

	/**
	 * �Ǽ�Լ���Ļ�����Ϣ������7�С����ǰ��հ汾�ĺϹ��ֲᡣ
	 * ��һ�У��׶�/����
	 * �ڶ��У�����/��λ����������/������λ��
	 * �����У�ְ��
	 * �����У������淶
	 * �����У�����Ҫ�㣨���յ�������Ϣ��ͬ�¼�Լ���ķ�����ʾ�������������������յȼ������շ��ࡢ���ƴ�ʩ�����Ʋ���/��λ��//����ũ���������������Ҫ��ʾ���շ��ࡾ���/2015-07-09��
	 * �����У�����
	 * �����У��ƶ�
	 * �����к͵����кϲ�Ϊ�Ϲ�Ҫ��
	 * @param column_activiy  �������
	 * @param processid	   ����id
	 * @param showopereq	   �Ƿ���ʾְ�𼰲����淶
	 * @param showlaw		   �Ƿ���ʾ������
	 * @param showrule	       �Ƿ���ʾ����ڹ�
	 * @param showrisk	       �Ƿ���ʾ����Ҫ�㣨���յ㣩
	 * @param hvs_activitys   ���ļ������л�����Ϣ
	 * @param hvs_opereqs     ���ļ����������в���Ҫ����Ϣ
	 * @param hvs_laws		   ���ļ����������������Ϣ
	 * @param hvs_rules	   ���ļ������������ƶ���Ϣ
	 * @param hvs_risks	   ���ļ����������з��յ���Ϣ
	 * @param _ishandbook	   �Ƿ��ǰ��հ汾�ĺϹ��ֲ�ĸ�ʽ
	 * @throws Exception
	 */
	private void addActivityDescTable2(int column_activiy, String processid, boolean showopereq, boolean showlaw, boolean showrule, boolean showrisk, HashVO[] hvs_activitys, HashVO[] hvs_opereqs, HashVO[] hvs_laws, HashVO[] hvs_rules, HashVO[] hvs_risks, boolean _ishandbook, Chapter chapter_desc) throws Exception {
		Table table_activity = new Table(column_activiy);
		if (column_activiy == 2) {
			table_activity.setWidths(new int[] { 1, 4 });//����ÿ����ռ���� 
		} else if (column_activiy == 3) {
			table_activity.setWidths(new int[] { 1, 3, 3 });//����ÿ����ռ���� 
		} else if (column_activiy == 4) {
			table_activity.setWidths(new int[] { 2, 5, 5, 5 });//����ÿ����ռ���� 
		} else if (column_activiy == 5) {
			table_activity.setWidths(new int[] { 1, 1, 2, 2, 2 });//����ÿ����ռ���� 
		} else if (column_activiy == 6) {
			table_activity.setWidths(new int[] { 2, 2, 3, 7, 5, 5 });//����ÿ����ռ���� 
		} else if (column_activiy == 7) {
			table_activity.setWidths(new int[] { 2, 2, 3, 7, 4, 4, 4 });//����ÿ����ռ���� 
		}
		table_activity.setWidth(100); // ռҳ���� 100% 
		table_activity.setPadding(5);
		//���û�����Ϣ��ͷ 
		Cell cell_1 = new Cell(new Phrase("�׶�/����", littletitleFont));
		cell_1.setHeader(true);//���õ�Ԫ����Ϊ��ͷ��Ϣ��ʾ
		cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell_1.setVerticalAlignment(Element.ALIGN_CENTER);
		cell_1.setBackgroundColor(new Color(204, 204, 204));
		if (showlaw && showrule) {
			cell_1.setRowspan(2);
		}
		table_activity.addCell(cell_1);

		if (showopereq) {//������صĲ����淶�Ƿ���ʾ
			Cell cell_2 = new Cell(new Phrase("����/��λ", littletitleFont));
			Cell cell_3 = new Cell(new Phrase("ְ��", littletitleFont));
			Cell cell_4 = new Cell(new Phrase("�����淶", littletitleFont));

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
			Cell cell_3 = new Cell(new Phrase("����Ҫ��", littletitleFont));
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
			Cell cell_4 = new Cell(new Phrase("�Ϲ�Ҫ��", littletitleFont));
			cell_4.setHeader(true);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			cell_4.setColspan(2);
			table_activity.addCell(cell_4);

			Cell cell_5 = new Cell(new Phrase("����", littletitleFont));
			cell_5.setHeader(true);
			cell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_5.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_5.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_5);

			Cell cell_6 = new Cell(new Phrase("�ƶ�", littletitleFont));
			cell_6.setHeader(true);
			cell_6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_6.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_6.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_6);
		} else if (showlaw) {
			Cell cell_4 = new Cell(new Phrase("��ط���", littletitleFont));
			cell_4.setHeader(true);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_4);
		} else if (showrule) {
			Cell cell_4 = new Cell(new Phrase("����ƶ�", littletitleFont));
			cell_4.setHeader(true);
			cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_4.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_4.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_4);
		}

		table_activity.endHeaders();//���õ�����ҳ����ʾ��ͷ��Ϣ 
		boolean hasruleitem = tbUtil.getSysOptionBooleanValue("�ƶ��Ƿ����Ŀ", true);
		StringBuffer sb_activity = new StringBuffer();
		//������Ϣ������ͷ�������forѭ����ʼ
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
			Cell cell_item1 = new Cell(new Phrase(sb_activity.toString().replaceAll("\n", ""), littletitleFont2));//����ũ������Ҫ������׶�����Ҫ�Ӵ֡����/2012-07-10��
			table_activity.addCell(cell_item1);//��һ�У��׶�/����

			String activityid = hvs_activitys[j].getStringValue("id");
			if (showopereq) {
				boolean iffind = false;
				for (int m = 0; m < hvs_opereqs.length; m++) {
					if (activityid.equals(hvs_opereqs[m].getStringValue("wfactivity_id"))) {
						String deptandpost = hvs_opereqs[m].getStringValue("operatedept");
						String str_post = hvs_opereqs[m].getStringValue("operatepost");
						if (deptandpost == null && str_post == null) {//��ѡ������������ź͸�λ��Ϊ�յĻ��������
							deptandpost = "";
						} else if (deptandpost == null) {
							deptandpost = str_post;
						} else if (deptandpost != null && str_post != null) {
							deptandpost = deptandpost + "/" + str_post;
						}

						String refpost = hvs_opereqs[m].getStringValue("operaterefpost", "");//��ظ�λ
						if (!"".equals(refpost)) {
							refpost = tbUtil.getInCondition(tbUtil.split(refpost, ";"));
							refpost = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + refpost + ")")).replaceAll("'", "");
							if (!"".equals(refpost) && !"-99999".equals(refpost)) {
								if ("".equals(deptandpost)) {
									deptandpost = "��ظ�λ��" + refpost;//�������ظ�λ�ͼ��ϣ�û�оͲ�����
								} else {
									deptandpost = deptandpost + "\n��ظ�λ��" + refpost;
								}
							}
						}
						table_activity.addCell(new Phrase(deptandpost, contextFont));//�ڶ��У�����-��λ ,��ظ�λ
						table_activity.addCell(new Phrase(hvs_opereqs[m].getStringValue("operatedesc", ""), contextFont));//�����У�ְ��
						table_activity.addCell(new Phrase(hvs_opereqs[m].getStringValue("operatereq", ""), contextFont));//�����У������淶
						iffind = true;
						break;
					}
				}
				if (!iffind) {
					table_activity.addCell("");//���û�����ݣ��ӿ���
					table_activity.addCell("");//���û�����ݣ��ӿ���
					table_activity.addCell("");//���û�����ݣ��ӿ���
				}
			}
			if (showrisk) {
				Phrase paragraph_risk = new Phrase();//����Ҫ��
				int n = 1;
				for (int m = 0; m < hvs_risks.length; m++) {
					if (!activityid.equals(hvs_risks[m].getStringValue("wfactivity_id"))) {
						continue;
					}
					paragraph_risk.add(new Chunk((n++) + "������������", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("riskdescr", "��") + "\n", contextFont));
					String rank = hvs_risks[m].getStringValue("rank", "��");
					if ("�ͷ���".equals(rank) || "��С����".equals(rank)) {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont1));
					} else if ("�еȷ���".equals(rank)) {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk("�еȷ���\n", rankFont2));
					} else if ("�߷���".equals(rank) || "�������".equals(rank)) {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont3));
					} else {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk("��\n", contextFont));
					}
					paragraph_risk.add(new Chunk("���շ��ࣺ", littletitleFont2));//��������������շ����Ǳ����Ϊ��ǿ�ļ������ԣ���Ҫ����ʱ��ʾ���������/2015-07-09��
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("risktype", "��") + "\n", contextFont));

					paragraph_risk.add(new Chunk("���ƴ�ʩ��", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("ctrlfn3", "��") + "\n", contextFont));//���ƴ�ʩ
					paragraph_risk.add(new Chunk("���Ʋ���/��λ��", littletitleFont2));
					String depts = hvs_risks[m].getStringValue("ctrldept");
					String posts = hvs_risks[m].getStringValue("ctrlpost");
					String deptandpost = "";
					if (depts == null && posts == null) {//
						deptandpost = "��\n";//��ѡ
					} else if (depts == null) {
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = (posts.equals("-99999") ? "��" : posts) + "\n";
					} else if (posts == null) {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						deptandpost = (depts.equals("-99999") ? "��" : depts) + "\n";
					} else {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = depts + "/" + posts + "\n";
					}
					paragraph_risk.add(new Chunk(deptandpost, contextFont));//��ѡ
				}
				table_activity.addCell(paragraph_risk);//�����У�����Ҫ��
			}

			if (showlaw) {
				Phrase paragraph_law = new Phrase();//��ط���
				int num_law = 1;
				for (int m = 0; m < hvs_laws.length; m++) {
					if (!activityid.equals(hvs_laws[m].getStringValue("wfactivity_id"))) {
						continue;
					}
					if (num_law > 1 && hvs_laws[m].getStringValue("law_id").equals(hvs_laws[m - 1].getStringValue("law_id"))) {
						paragraph_law.add(new Chunk(";" + hvs_laws[m].getStringValue("lawitem_title", "��"), contextFont));
					} else {
						paragraph_law.add(new Chunk((num_law++) + "���������ƣ�", littletitleFont2));
						paragraph_law.add(new Chunk(hvs_laws[m].getStringValue("law_name", "��") + "\n", contextFont));
						paragraph_law.add(new Chunk("�������ı��⣺", littletitleFont2));
						paragraph_law.add(new Chunk(hvs_laws[m].getStringValue("lawitem_title", "��") + "\n", contextFont));
					}
				}
				table_activity.addCell(paragraph_law);//�����У���ط���
			}
			if (showrule) {
				Phrase paragraph_rule = new Phrase();//����ƶ�
				int num_rule = 1;
				if (hasruleitem) {//�ƶ��Ƿ������
					for (int m = 0; m < hvs_rules.length; m++) {
						if (!activityid.equals(hvs_rules[m].getStringValue("wfactivity_id"))) {
							continue;
						}
						if (num_rule > 1 && hvs_rules[m].getStringValue("rule_id").equals(hvs_rules[m - 1].getStringValue("rule_id"))) {
							paragraph_rule.add(new Chunk(";" + hvs_rules[m].getStringValue("ruleitem_title", "��"), contextFont));
						} else {
							paragraph_rule.add(new Chunk((num_rule++) + "���ƶ����ƣ�", littletitleFont2));
							paragraph_rule.add(new Chunk(hvs_rules[m].getStringValue("rule_name", "��") + "\n", contextFont));
							paragraph_rule.add(new Chunk("�ƶ����ı��⣺", littletitleFont2));
							paragraph_rule.add(new Chunk(hvs_rules[m].getStringValue("ruleitem_title", "��") + "\n", contextFont));
						}
					}
				} else {
					for (int m = 0; m < hvs_rules.length; m++) {
						if (!activityid.equals(hvs_rules[m].getStringValue("wfactivity_id"))) {
							continue;
						}
						paragraph_rule.add(new Chunk((num_rule++) + "��" + hvs_rules[m].getStringValue("rule_name", "") + "\n", contextFont));
					}
				}
				table_activity.addCell(paragraph_rule);//�����У�����ƶ�
			}

		}//������Ϣ������ͷ�������forѭ������
		if (_ishandbook) {
			chapter_desc.add(table_activity);//�������ͼ��Ļ�����Ϣ���
			document.setPageSize(PageSize.A4.rotate());
			afterRotate();
			document.add(chapter_desc);
		} else {
			chapter2.add(table_activity);//�������ͼ��Ļ�����Ϣ���
		}
	}

	/**
	 * ��Լ���Ļ�����Ϣ������3�У�һ���������ʾ�������ķ����汾ʱ���á�
	 * ��һ�У��׶Σ�ͬ���ڵĽ׶�Ҫ�ϲ���
	 * �ڶ��У�������ݸ��ļ����ļ����ͣ��ж��������̵ģ�����ʾ����Ϊ���������������Ҫ�󡱣��������ơ���������/��λ��ְ��Ͳ����淶�ϲ�Ϊһ�У���������ʾ������/����Ҫ�󡱣�
	 * �����У�������ʾ�����յ�������Ϣ�������������������յȼ������ƴ�ʩ�����Ʋ���/��λ����
	 * @param column_activiy  �������
	 * @param processid	   ����id
	 * @param showopereq	   �Ƿ���ʾְ�𼰲����淶
	 * @param showrisk	       �Ƿ���ʾ����Ҫ�㣨���յ㣩
	 * @param hvs_activitys   ���ļ������л�����Ϣ
	 * @param hvs_opereqs     ���ļ����������в���Ҫ����Ϣ
	 * @param hvs_risks	   ���ļ����������з��յ���Ϣ
	 * @param _ishandbook	   �Ƿ��ǰ��հ汾�ĺϹ��ֲ�ĸ�ʽ
	 * @throws Exception
	 */
	private void addActivityDescTable(int column_activiy, String processid, boolean showopereq, boolean showrisk, HashVO[] hvs_activitys, HashVO[] hvs_opereqs, HashVO[] hvs_risks, boolean _ishandbook, Chapter chapter_desc) throws Exception {
		Table table_activity = null;
		if (!hasStation && cmpfiletype_code.contains("������")) {//���̵���wordʱ�����̱���Ƿ���ʾ�׶�
			column_activiy -= 1;//ȥ����һ�н׶�
			table_activity = new Table(column_activiy);
			if (column_activiy == 1) {
				table_activity.setWidths(new int[] { 4 });//����ÿ����ռ���� 
			} else if (column_activiy == 2) {
				table_activity.setWidths(new int[] { 4, 2 });//����ÿ����ռ���� 
			}
		} else {
			table_activity = new Table(column_activiy);
			if (column_activiy == 2) {
				table_activity.setWidths(new int[] { 1, 4 });//����ÿ����ռ���� 
			} else if (column_activiy == 3) {
				table_activity.setWidths(new int[] { 1, 4, 2 });//����ÿ����ռ���� 
			}
		}

		table_activity.setWidth(100); // ռҳ���� 100% 
		table_activity.setPadding(5);

		if (hasStation || cmpfiletype_code.contains("������")) {
			//���û�����Ϣ��ͷ 
			Cell cell_1 = new Cell(new Phrase("�׶�", littletitleFont));
			cell_1.setHeader(true);//���õ�Ԫ����Ϊ��ͷ��Ϣ��ʾ
			cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_1.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_1.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_1);
		}
		if (showopereq) {//������صĲ����淶�Ƿ���ʾ
			Cell cell_2 = null;
			if (cmpfiletype_code.contains("������")) {//����ļ������б���Ϊ����������ʾ����Ϊ���������������Ҫ�󡱣�������ʾ������/����Ҫ�󡱣������������ݲ���
				cell_2 = new Cell(new Phrase("�������������Ҫ��", littletitleFont));
			} else {
				cell_2 = new Cell(new Phrase("����/����Ҫ��", littletitleFont));
			}
			cell_2.setHeader(true);
			cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_2.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_2.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_2);
		}
		if (showrisk) {
			Cell cell_3 = new Cell(new Phrase("������ʾ", littletitleFont));
			cell_3.setHeader(true);
			cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_3.setVerticalAlignment(Element.ALIGN_CENTER);
			cell_3.setBackgroundColor(new Color(204, 204, 204));
			table_activity.addCell(cell_3);
		}

		table_activity.endHeaders();//���õ�����ҳ����ʾ��ͷ��Ϣ 

		Cell stationCell = null;
		int rows = 1;
		//������Ϣ������ͷ�������forѭ����ʼ
		for (int j = 0; j < hvs_activitys.length; j++) {
			if (!processid.equals(hvs_activitys[j].getStringValue("processid"))) {
				continue;
			}
			if (hasStation || cmpfiletype_code.contains("������")) {
				String belongstationgroup = hvs_activitys[j].getStringValue("belongstationgroup", "");
				if (rows > 1) {
					rows--;
				} else {
					for (int m = j; m < hvs_activitys.length - 1; m++) {
						if (!processid.equals(hvs_activitys[m + 1].getStringValue("processid"))) {//�����һ���ڵ�����id��������id��һ�£�������ѭ������Ϊ���Ȱ�����id�����
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
					table_activity.addCell(stationCell);//��һ�У��׶�
				}
			}
			String activityid = hvs_activitys[j].getStringValue("id");
			if (showopereq) {
				Phrase paragraph_opereq = new Phrase();//��������������Ҫ��
				boolean iffind = false;
				for (int m = 0; m < hvs_opereqs.length; m++) {
					if (activityid.equals(hvs_opereqs[m].getStringValue("wfactivity_id"))) {
						if (cmpfiletype_code.contains("������")) {
							paragraph_opereq.add(new Chunk("�������ƣ�", littletitleFont2));
							paragraph_opereq.add(new Chunk(hvs_activitys[j].getStringValue("wfname", "") + "\n", contextFont));//�ӻ��ڱ���ȡ����
							paragraph_opereq.add(new Chunk("��������/��λ��", littletitleFont2));
							String deptandpost = hvs_opereqs[m].getStringValue("operatedept");
							String str_post = hvs_opereqs[m].getStringValue("operatepost");
							if (deptandpost == null && str_post == null) {//��ѡ������������ź͸�λ��Ϊ�յĻ��������
								deptandpost = "��";
							} else if (deptandpost == null) {
								deptandpost = str_post;
							} else if (deptandpost != null && str_post != null) {
								deptandpost = deptandpost + "/" + str_post;
							}

							paragraph_opereq.add(new Chunk(deptandpost + "\n", contextFont));//�������ź͸�λ��Ϊ��ѡ

							String refpost = hvs_opereqs[m].getStringValue("operaterefpost", "");//��ظ�λ
							if (!"".equals(refpost)) {
								refpost = tbUtil.getInCondition(tbUtil.split(refpost, ";"));
								refpost = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + refpost + ")")).replaceAll("'", "");
								if (!"".equals(refpost) && !"-99999".equals(refpost)) {
									paragraph_opereq.add(new Chunk("��ظ�λ��" + refpost + "\n", contextFont));//�������ظ�λ�ͼ��ϣ�û�оͲ�����
								}
							}
							if (!"".equals(hvs_opereqs[m].getStringValue("operatedesc", ""))) {
								paragraph_opereq.add(new Chunk("ְ��", littletitleFont2));
								paragraph_opereq.add(new Chunk(hvs_opereqs[m].getStringValue("operatedesc", "��") + "\n", contextFont));
							}
							paragraph_opereq.add(new Chunk("�����淶��", littletitleFont2));
							paragraph_opereq.add(new Chunk(hvs_opereqs[m].getStringValue("operatereq", "��"), contextFont));
						} else {
							if (showActivity) {//Ф����˵�������̵������ļ�����word���������/����Ҫ�� �в���ʾ�������ƣ���������д��ʲô���ݡ� ���������ᣬ�ұ����Ǻܲ���ĵģ������/2014-04-09��
								paragraph_opereq.add(new Chunk(new Chunk(hvs_activitys[j].getStringValue("wfname", "") + "\n", littletitleFont2)));
							}
							paragraph_opereq.add(new Chunk(hvs_opereqs[m].getStringValue("operatereq", ""), contextFont));
						}
						iffind = true;
						break;
					}
				}
				if (!iffind) {
					if (cmpfiletype_code.contains("������")) {
						paragraph_opereq.add(new Chunk("�������ƣ�", littletitleFont2));
						paragraph_opereq.add(new Chunk(hvs_activitys[j].getStringValue("wfname", "") + "\n", contextFont));//�ӻ��ڱ���ȡ����
						paragraph_opereq.add(new Chunk("��������/��λ��", littletitleFont2));
						paragraph_opereq.add(new Chunk("��\n", contextFont));
						paragraph_opereq.add(new Chunk("�����淶��", littletitleFont2));
						paragraph_opereq.add(new Chunk("��", contextFont));
					}
				}
				table_activity.addCell(paragraph_opereq);//�ڶ��У���������������Ҫ��
			}
			if (showrisk) {
				Phrase paragraph_risk = new Phrase();//����Ҫ��
				int n = 1;
				for (int m = 0; m < hvs_risks.length; m++) {
					if (!activityid.equals(hvs_risks[m].getStringValue("wfactivity_id"))) {
						continue;
					}
					paragraph_risk.add(new Chunk((n++) + "������������", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("riskdescr", "��") + "\n", contextFont));
					String rank = hvs_risks[m].getStringValue("rank", "��");
					if ("�ͷ���".equals(rank) || "��С����".equals(rank)) {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont1));
					} else if ("�еȷ���".equals(rank)) {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk("�еȷ���\n", rankFont2));
					} else if ("�߷���".equals(rank) || "�������".equals(rank)) {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk(rank + "\n", rankFont3));
					} else {
						paragraph_risk.add(new Chunk("���յȼ���", littletitleFont2));
						paragraph_risk.add(new Chunk("��\n", contextFont));
					}
					paragraph_risk.add(new Chunk("���շ��ࣺ", littletitleFont2));//��������������շ����Ǳ����Ϊ��ǿ�ļ������ԣ���Ҫ����ʱ��ʾ���������/2015-07-09��
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("risktype", "��") + "\n", contextFont));

					paragraph_risk.add(new Chunk("���ƴ�ʩ��", littletitleFont2));
					paragraph_risk.add(new Chunk(hvs_risks[m].getStringValue("ctrlfn3", "��") + "\n", contextFont));//���ƴ�ʩ
					paragraph_risk.add(new Chunk("���Ʋ���/��λ��", littletitleFont2));//��ѡ

					String depts = hvs_risks[m].getStringValue("ctrldept");
					String posts = hvs_risks[m].getStringValue("ctrlpost");
					String deptandpost = "";
					if (depts == null && posts == null) {//
						deptandpost = "��\n";//��ѡ
					} else if (depts == null) {
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = (posts.equals("-99999") ? "��" : posts) + "\n";
					} else if (posts == null) {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						deptandpost = (depts.equals("-99999") ? "��" : depts) + "\n";
					} else {
						depts = tbUtil.getInCondition(tbUtil.split(depts, ";"));
						depts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + depts + ")")).replaceAll("'", "");
						posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
						posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
						deptandpost = depts + "/" + posts + "\n";
					}
					paragraph_risk.add(new Chunk(deptandpost, contextFont));//��ѡ
				}
				table_activity.addCell(paragraph_risk);//�����У�����Ҫ��
			}
		}//������Ϣ������ͷ�������forѭ������

		if (_ishandbook) {
			chapter_desc.add(table_activity);//�������ͼ��Ļ�����Ϣ���
			document.setPageSize(PageSize.A4.rotate());//���̸����ͻ�����Ϣ���������½�chapter_desc�У�document���chapter_descǰҪ����ҳ��Ϊ����
			afterRotate();
			document.add(chapter_desc);
		} else {
			chapter2.add(table_activity);//�������ͼ��Ļ�����Ϣ���chapter2�ڴ˲���Ҫ����document����Ϊ����chapter2���ܻ�Ҫ����ĳЩԪ��
		}
	}

	private void addWfdescTable(Chapter chapter_wf, String processid, HashVO[] hvs_processes) throws DocumentException {
		for (int j = 0; j < hvs_processes.length; j++) {
			if (processid.equals(hvs_processes[j].getStringValue("wfprocess_id"))) {
				Table table_process = new Table(6);
				table_process.setWidths(new int[] { 1, 2, 1, 2, 1, 2 });//����ÿ����ռ���� 
				table_process.setWidth(100);
				table_process.setPadding(5);
				Cell cell_1 = new Cell(new Phrase("���̱��", littletitleFont));
				Cell cell_2 = new Cell(new Phrase(hvs_processes[j].getStringValue("wfprocess_code", "��"), contextFont));
				Cell cell_3 = new Cell(new Phrase("��������", littletitleFont));
				Cell cell_4 = new Cell(new Phrase(hvs_processes[j].getStringValue("wfprocess_name", "��"), contextFont));
				Cell cell_5 = new Cell(new Phrase("���Ƶ�λ", littletitleFont));
				Cell cell_6 = new Cell(new Phrase(hvs_processes[j].getStringValue("blcorpname", "��"), contextFont));
				Cell cell_7 = new Cell(new Phrase("���̸���", littletitleFont));
				Cell cell_8 = new Cell(new Phrase(hvs_processes[j].getStringValue("wfdesc", "��"), contextFont));

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
				table_process.addCell(cell_7);//�ڶ���
				table_process.addCell(cell_8);//�ڶ���
				chapter_wf.add(table_process);//������̸������
				break;//�ҵ������̾�ֱ������ѭ��
			}
		}
	}

	/**
	 * ���ҳ���Ϊ����Ļ�����������ҳü�����̺Ϲ��ֲ�������Ҫ������ʾ������ö��䣬������и����У����ö����ֲ���ʵ�־�����ʾ�������ÿո������ƣ����ҳ���Ϊ����Ļ���Ҫ�����������Ŷ
	 */
	private void afterRotate() {
		Phrase ph_header = new Phrase();
		if (titleimg != null) {
			ph_header.add(new Chunk(titleimg, titleimg.getWidth(), titleimg.getHeight()));
		}
		ph_header.add(new Chunk("  " + convertStr(tbUtil.getSysOptionStringValue("���̵���wordʱҳü��ʾ������", "    "), 55), headerFont_1));
		ph_header.add(new Chunk("���̺Ϲ��ֲ�", headerFont_2));
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
