package cn.com.infostrategy.to.report;

import java.io.FileOutputStream;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * Word�ļ�����Ʒ����
 * 
 * @author Administrator
 * 
 */
public class WordFileUtil extends AbstractDMO {

	/**
	 * ��һ��ͼƬ��һ����������һ��Xml��ʽ��Word
	 * @param _data
	 * @return
	 */
	public String getWorldFileXmlByImageAndTable(String _img64Code, int _imgWidth, int _imgHeight, String[][] _data) {
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append(getWordFileBegin()); //wordͷ!
		sb_xml.append(getImageXml(_img64Code, _imgWidth, _imgHeight)); //�����ͼƬ!!!
		sb_xml.append(getTableXml(_data, 2160, "left", "CCFFCC")); //��������!!!2160
		sb_xml.append(getWordFileEnd()); //wordβ!
		return sb_xml.toString(); //
	}

	/**
	 * ��һ����ά����ֱ�������һ��������XML��ʽ��Word�ļ�!!!
	 * @param _data
	 * @return
	 */
	public String getWorldFileXmlByTable(String[][] _data) {
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append(getWordFileBegin()); //
		sb_xml.append(getTableXml(_data, 2160, "left", "CCFFCC")); //CCFFCC
		sb_xml.append(getWordFileEnd()); //
		return sb_xml.toString(); //
	}

	/**
	 * �õ�Word�ļ���ͷXML
	 * 
	 * @return
	 */
	public String getWordFileBegin() {
		StringBuffer sb_word = new StringBuffer(); //
		sb_word.append("<?xml version='1.0' encoding='GBK' standalone='yes'?>\r\n");
		sb_word.append("<?mso-application progid='Word.Document'?>\r\n");

		sb_word.append("<w:wordDocument xmlns:w='http://schemas.microsoft.com/office/word/2003/wordml' "); // ����Word�ļ���xml��ʽ�ĸ����!!!����w:wordDocument
		sb_word.append("xmlns:v='urn:schemas-microsoft-com:vml' ");
		sb_word.append("xmlns:w10='urn:schemas-microsoft-com:office:word' ");
		sb_word.append("xmlns:sl='http://schemas.microsoft.com/schemaLibrary/2003/core' ");
		sb_word.append("xmlns:aml='http://schemas.microsoft.com/aml/2001/core' ");
		sb_word.append("xmlns:wx='http://schemas.microsoft.com/office/word/2003/auxHint' ");
		sb_word.append("xmlns:o='urn:schemas-microsoft-com:office:office' ");
		sb_word.append("xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' ");
		sb_word.append("xmlns:wsp='http://schemas.microsoft.com/office/word/2003/wordml/sp2' ");
		sb_word.append("w:macrosPresent='no' w:embeddedObjPresent='no' w:ocxPresent='no' xml:space='preserve'>\r\n");

		sb_word.append("<w:ignoreElements w:val='http://schemas.microsoft.com/office/word/2003/wordml/sp2'/>\r\n");

		// ͷ�ļ����ĵ����ԣ��������⣬���ߣ�����ʱ�䣬�汾�ȵ�..
		sb_word.append("<o:DocumentProperties>\r\n");
		sb_word.append("<o:Title> </o:Title>\r\n");
		sb_word.append("<o:Author>Push World</o:Author>\r\n");
		sb_word.append("<o:LastAuthor>Push World</o:LastAuthor>\r\n");
		sb_word.append("<o:Revision>8</o:Revision>\r\n");
		sb_word.append("<o:TotalTime>2</o:TotalTime>\r\n");
		sb_word.append("<o:Created>2008-07-30T09:14:00Z</o:Created>\r\n");
		sb_word.append("<o:LastSaved>2008-08-04T04:35:00Z</o:LastSaved>\r\n");
		sb_word.append("<o:Pages>1</o:Pages>\r\n");
		sb_word.append("<o:Words>0</o:Words>\r\n");
		sb_word.append("<o:Characters>1</o:Characters>\r\n");
		sb_word.append("<o:Company>΢���й�</o:Company>\r\n");
		sb_word.append("<o:Lines>1</o:Lines>\r\n");
		sb_word.append("<o:Paragraphs>1</o:Paragraphs>\r\n");
		sb_word.append("<o:CharactersWithSpaces>1</o:CharactersWithSpaces>\r\n");
		sb_word.append("<o:Version>11.0000</o:Version>\r\n");
		sb_word.append("</o:DocumentProperties>\r\n");

		// �����������
		sb_word.append("<w:fonts>\r\n");
		sb_word.append("<w:defaultFonts w:ascii='Times New Roman' w:fareast='����' w:h-ansi='Times New Roman' w:cs='Times New Roman'/>\r\n");

		sb_word.append("<w:font w:name='����'>\r\n");
		sb_word.append("<w:altName w:val='SimSun'/>\r\n");
		sb_word.append("<w:panose-1 w:val='02010600030101010101'/>\r\n");
		sb_word.append("<w:charset w:val='86'/>\r\n");
		sb_word.append("<w:family w:val='Auto'/>\r\n");
		sb_word.append("<w:pitch w:val='variable'/>\r\n");
		sb_word.append("<w:sig w:usb-0='00000003' w:usb-1='080E0000' w:usb-2='00000010' w:usb-3='00000000' w:csb-0='00040001' w:csb-1='00000000'/>\r\n");
		sb_word.append("</w:font>\r\n");

		sb_word.append("<w:font w:name='@����'>\r\n");
		sb_word.append("<w:panose-1 w:val='02010600030101010101'/>\r\n");
		sb_word.append("<w:charset w:val='86'/>\r\n");
		sb_word.append("<w:family w:val='Auto'/>\r\n");
		sb_word.append("<w:pitch w:val='variable'/>\r\n");
		sb_word.append("<w:sig w:usb-0='00000003' w:usb-1='080E0000' w:usb-2='00000010' w:usb-3='00000000' w:csb-0='00040001' w:csb-1='00000000'/>\r\n");
		sb_word.append("</w:font>\r\n");

		sb_word.append("</w:fonts>\r\n");

		// ������
		sb_word.append("<w:styles>\r\n");
		sb_word.append("<w:versionOfBuiltInStylenames w:val='4'/>\r\n");
		sb_word.append("<w:latentStyles w:defLockedState='off' w:latentStyleCount='156'/>\r\n");

		sb_word.append("<w:style w:type='paragraph' w:default='on' w:styleId='a'>\r\n");
		sb_word.append("<w:name w:val='Normal'/>\r\n");
		sb_word.append("<wx:uiName wx:val='����'/>\r\n");
		sb_word.append("<w:rsid w:val='00C42288'/>\r\n");

		// �����������
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:widowControl w:val='off'/>\r\n");
		sb_word.append("<w:jc w:val='both'/>\r\n");
		sb_word.append("</w:pPr>\r\n");

		// �л�������
		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<wx:font wx:val='Times New Roman'/>\r\n");
		sb_word.append("<w:kern w:val='2'/>\r\n");
		sb_word.append("<w:sz w:val='21'/>\r\n");
		sb_word.append("<w:sz-cs w:val='24'/>");
		sb_word.append("<w:lang w:val='EN-US' w:fareast='ZH-CN' w:bidi='AR-SA'/>\r\n");
		sb_word.append("</w:rPr>\r\n");

		sb_word.append("</w:style>\r\n");

		// Ӧ����������
		sb_word.append("<w:style w:type='character' w:default='on' w:styleId='a0'>\r\n");
		sb_word.append("<w:name w:val='Default Paragraph Font'/>\r\n");
		sb_word.append("<wx:uiName wx:val='Ĭ�϶�������'/>\r\n");
		sb_word.append("<w:semiHidden/>\r\n");
		sb_word.append("</w:style>\r\n");

		// Ӧ���Ǳ����
		sb_word.append("<w:style w:type='table' w:default='on' w:styleId='a1'>\r\n");
		sb_word.append("<w:name w:val='Normal Table'/>\r\n");
		sb_word.append("<wx:uiName wx:val='��ͨ���'/>\r\n");
		sb_word.append("<w:semiHidden/>\r\n");

		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<wx:font wx:val='Times New Roman'/>\r\n");
		sb_word.append("</w:rPr>\r\n");

		// �������
		sb_word.append("<w:tblPr>\r\n");
		sb_word.append("<w:tblInd w:w='0' w:type='dxa'/>\r\n");
		// ���ĵ�Ԫ��λ��
		sb_word.append("<w:tblCellMar>\r\n");
		sb_word.append("<w:top w:w='0' w:type='dxa'/>\r\n");
		sb_word.append("<w:left w:w='108' w:type='dxa'/>\r\n");
		sb_word.append("<w:bottom w:w='0' w:type='dxa'/>\r\n");
		sb_word.append("<w:right w:w='108' w:type='dxa'/>\r\n");
		sb_word.append("</w:tblCellMar>\r\n");

		sb_word.append("</w:tblPr>\r\n");
		sb_word.append("</w:style>\r\n");

		// �б��������÷��
		sb_word.append("<w:style w:type='list' w:default='on' w:styleId='a2'>\r\n");
		sb_word.append("<w:name w:val='No List'/>\r\n");
		sb_word.append("<wx:uiName wx:val='���б�'/>\r\n");
		sb_word.append("<w:semiHidden/>\r\n");
		sb_word.append("</w:style>\r\n");
		// ����Ļ������
		sb_word.append("<w:style w:type='paragraph' w:styleId='a3'>\r\n");
		sb_word.append("<w:name w:val='header'/>\r\n");
		sb_word.append("<wx:uiName wx:val='ҳü'/>\r\n");
		sb_word.append("<w:basedOn w:val='a'/>\r\n");
		sb_word.append("<w:rsid w:val='006B052F'/>\r\n");

		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a3'/>\r\n");
		sb_word.append("<w:pBdr>\r\n");
		sb_word.append("<w:bottom w:val='single' w:sz='6' wx:bdrwidth='15' w:space='1' w:color='auto'/>\r\n");
		sb_word.append("</w:pBdr>\r\n");
		sb_word.append("<w:tabs>\r\n");
		sb_word.append("<w:tab w:val='center' w:pos='4153'/>\r\n");
		sb_word.append("<w:tab w:val='right' w:pos='8306'/>\r\n");
		sb_word.append("</w:tabs>\r\n");
		sb_word.append("<w:snapToGrid w:val='off'/>\r\n");
		sb_word.append("<w:jc w:val='center'/>\r\n");
		sb_word.append("</w:pPr>\r\n");

		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<wx:font wx:val='Times New Roman'/>\r\n");
		sb_word.append("<w:sz w:val='18'/>\r\n");
		sb_word.append("<w:sz-cs w:val='18'/>\r\n");
		sb_word.append("</w:rPr>\r\n");
		sb_word.append("</w:style>\r\n");

		sb_word.append("<w:style w:type='paragraph' w:styleId='a4'>\r\n");
		sb_word.append("<w:name w:val='footer'/>\r\n");
		sb_word.append("<wx:uiName wx:val='ҳ��'/>\r\n");
		sb_word.append("<w:basedOn w:val='a'/>\r\n");
		sb_word.append("<w:rsid w:val='006B052F'/>\r\n");

		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a4'/>\r\n");
		sb_word.append("<w:tabs>\r\n");
		sb_word.append("<w:tab w:val='center' w:pos='4153'/>\r\n");
		sb_word.append("<w:tab w:val='right' w:pos='8306'/>\r\n");
		sb_word.append("</w:tabs>\r\n");
		sb_word.append("<w:snapToGrid w:val='off'/>\r\n");
		sb_word.append("<w:jc w:val='left'/>\r\n");
		sb_word.append("</w:pPr>\r\n");

		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<wx:font wx:val='Times New Roman'/>\r\n");
		sb_word.append("<w:sz w:val='18'/>\r\n");
		sb_word.append("<w:sz-cs w:val='18'/>\r\n");
		sb_word.append("</w:rPr>\r\n");
		sb_word.append("</w:style>\r\n");
		sb_word.append("</w:styles>\r\n");

		sb_word.append("<w:shapeDefaults>\r\n");
		sb_word.append("<o:shapedefaults v:ext='edit' spidmax='7170'/>\r\n");
		sb_word.append("<o:shapelayout v:ext='edit'>\r\n");
		sb_word.append("<o:idmap v:ext='edit' data='2'/>\r\n");
		sb_word.append("</o:shapelayout>\r\n");
		sb_word.append("</w:shapeDefaults>\r\n");

		sb_word.append("<w:docPr>\r\n");
		sb_word.append("<w:view w:val='print'/>\r\n");
		sb_word.append("<w:zoom w:percent='100'/>\r\n");
		sb_word.append("<w:bordersDontSurroundHeader/>\r\n");
		sb_word.append("<w:bordersDontSurroundFooter/>\r\n");
		sb_word.append("<w:attachedTemplate w:val=''/>\r\n");
		sb_word.append("<w:defaultTabStop w:val='420'/>\r\n");
		sb_word.append("<w:drawingGridVerticalSpacing w:val='156'/>\r\n");
		sb_word.append("<w:displayHorizontalDrawingGridEvery w:val='0'/>\r\n");
		sb_word.append("<w:displayVerticalDrawingGridEvery w:val='2'/>\r\n");
		sb_word.append("<w:punctuationKerning/>\r\n");
		sb_word.append("<w:characterSpacingControl w:val='CompressPunctuation'/>\r\n");
		sb_word.append("<w:optimizeForBrowser/>\r\n");
		sb_word.append("<w:validateAgainstSchema/>\r\n");
		sb_word.append("<w:saveInvalidXML w:val='off'/>\r\n");
		sb_word.append("<w:ignoreMixedContent w:val='off'/>\r\n");
		sb_word.append("<w:alwaysShowPlaceholderText w:val='off'/>\r\n");

		sb_word.append("<w:hdrShapeDefaults>\r\n");
		sb_word.append("<o:shapedefaults v:ext='edit' spidmax='7170'/>\r\n");
		sb_word.append("</w:hdrShapeDefaults>\r\n");

		sb_word.append("<w:footnotePr>\r\n");
		sb_word.append("<w:footnote w:type='separator'>\r\n");
		sb_word.append("<w:p wsp:rsidR='00CC6D5F' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:r>\r\n");
		sb_word.append("<w:separator/>\r\n");
		sb_word.append("</w:r>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:footnote>\r\n");

		sb_word.append("<w:footnote w:type='continuation-separator'>\r\n");
		sb_word.append("<w:p wsp:rsidR='00CC6D5F' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:r>\r\n");
		sb_word.append("<w:continuationSeparator/>\r\n");
		sb_word.append("</w:r>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:footnote>\r\n");
		sb_word.append("</w:footnotePr>\r\n");

		sb_word.append("<w:endnotePr>\r\n");
		sb_word.append("<w:endnote w:type='separator'>\r\n");
		sb_word.append("<w:p wsp:rsidR='00CC6D5F' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:r>\r\n");
		sb_word.append("<w:separator/>\r\n");
		sb_word.append("</w:r>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:endnote>\r\n");

		sb_word.append("<w:endnote w:type='continuation-separator'>\r\n");
		sb_word.append("<w:p wsp:rsidR='00CC6D5F' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:r>\r\n");
		sb_word.append("<w:continuationSeparator/>\r\n");
		sb_word.append("</w:r>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:endnote>\r\n");
		sb_word.append("</w:endnotePr>\r\n");

		sb_word.append("<w:compat>\r\n");
		sb_word.append("<w:spaceForUL/>\r\n");
		sb_word.append("<w:balanceSingleByteDoubleByteWidth/>\r\n");
		sb_word.append("<w:doNotLeaveBackslashAlone/>\r\n");
		sb_word.append("<w:ulTrailSpace/>\r\n");
		sb_word.append("<w:doNotExpandShiftReturn/>\r\n");
		sb_word.append("<w:adjustLineHeightInTable/>\r\n");
		sb_word.append("<w:breakWrappedTables/>\r\n");
		sb_word.append("<w:snapToGridInCell/>\r\n");
		sb_word.append("<w:wrapTextWithPunct/>\r\n");
		sb_word.append("<w:useAsianBreakRules/>\r\n");
		sb_word.append("<w:dontGrowAutofit/>\r\n");
		sb_word.append("<w:useFELayout/>\r\n");
		sb_word.append("</w:compat>\r\n");

		sb_word.append("<wsp:rsids>\r\n");
		sb_word.append("<wsp:rsidRoot wsp:val='006B052F'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00021238'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='000B0B67'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='000C3EE2'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='001308F3'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00173D47'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00174DF6'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='001D5A07'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='0029586F'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='0029745E'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='003141DF'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='0047537C'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='004E4D6B'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='005D04B3'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='005E6978'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00616601'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='006B052F'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='008C2096'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00AF50FA'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00B610DC'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00B67FD3'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00BA64D8'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00C42288'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00C64C2D'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00C80DEF'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00CC6D5F'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00CE3B8C'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00CF0148'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00D169A6'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00DA2D5A'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00DC0A2F'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00DD40FA'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00E0499A'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00E51B90'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00EF2CC7'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00F10FC0'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00F85F23'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00FB7D1C'/>\r\n");
		sb_word.append("<wsp:rsid wsp:val='00FF61F8'/>\r\n");
		sb_word.append("</wsp:rsids>\r\n");

		sb_word.append("</w:docPr>\r\n");
		sb_word.append("<w:body>\r\n"); // word�ļ������ݾ�����w:body��ʼ��

		sb_word.append("<wx:sect>\r\n\r\n");

		return sb_word.toString(); //

	}

	/**
	 * ��Word�ļ�����������һ�б���!����,�ĺ�,����
	 * 
	 * @param _title
	 * @return
	 */
	public String getWordTitle(String _title) {
		StringBuffer sb_word = new StringBuffer();
		sb_word.append("<w:p>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:jc w:val=\"center\"/>\r\n");
		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<w:rFonts w:hint=\"fareast\"/>\r\n");
		sb_word.append("<w:b/>\r\n");
		sb_word.append("<w:sz w:val=\"28\"/>\r\n");
		sb_word.append("<w:sz-cs w:val=\"28\"/>\r\n");
		sb_word.append("</w:rPr>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("<w:r>\r\n");
		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<w:rFonts w:hint=\"fareast\"/>\r\n");
		sb_word.append("<wx:font wx:val=\"����\"/>\r\n");
		sb_word.append("<w:b/>\r\n");
		sb_word.append("<w:sz w:val=\"28\"/>\r\n");
		sb_word.append("<w:sz-cs w:val=\"28\"/>\r\n");
		sb_word.append("</w:rPr>\r\n");
		sb_word.append("<w:t>" + _title + "</w:t>\r\n");
		sb_word.append("</w:r>\r\n");
		sb_word.append("</w:p>\r\n");
		return sb_word.toString(); //
	}

	/**
	 * ȡ���ļ�β,���Ծ����Ƿ����
	 * 
	 * @param _isLand
	 * @return
	 */
	public String getWordFileEnd() {
		return getWordFileEnd(false);
	}

	/**
	 * �õ�Word�ļ��Ľ�βXML
	 * 
	 * @return
	 */
	public String getWordFileEnd(boolean _isLand) {
		StringBuffer sb_word = new StringBuffer(); //

		sb_word.append("\r\n");
		sb_word.append("<w:sectPr wsp:rsidR='006B052F' wsp:rsidSect='00B610DC'>\r\n");

		sb_word.append("<w:hdr w:type='even'>\r\n");
		sb_word.append("<wx:pBdrGroup>\r\n");
		sb_word.append("<wx:borders>\r\n");
		sb_word.append("<wx:bottom wx:val='solid' wx:bdrwidth='15' wx:space='1' wx:color='auto'/>\r\n");
		sb_word.append("</wx:borders>\r\n");

		sb_word.append("<w:p wsp:rsidR='000B51A1' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a3'/>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</wx:pBdrGroup>\r\n");
		sb_word.append("</w:hdr>\r\n");

		sb_word.append("<w:hdr w:type='odd'>\r\n");
		sb_word.append("<w:p wsp:rsidR='000B51A1' wsp:rsidRDefault='00CC6D5F' wsp:rsidP='00E51B90'>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a3'/>\r\n");
		sb_word.append("<w:pBdr>\r\n");
		sb_word.append("<w:bottom w:val='none' w:sz='0' wx:bdrwidth='0' w:space='0' w:color='auto'/>\r\n");
		sb_word.append("</w:pBdr>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:hdr>\r\n");

		sb_word.append("<w:ftr w:type='even'>\r\n");
		sb_word.append("<w:p wsp:rsidR='000B51A1' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a4'/>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:ftr>\r\n");

		sb_word.append("<w:ftr w:type='odd'>\r\n");
		sb_word.append("<w:p wsp:rsidR='000B51A1' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a4'/>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:ftr>\r\n");

		sb_word.append("<w:hdr w:type='first'>\r\n");
		sb_word.append("<wx:pBdrGroup>\r\n");
		sb_word.append("<wx:borders>\r\n");
		sb_word.append("<wx:bottom wx:val='solid' wx:bdrwidth='15' wx:space='1' wx:color='auto'/>\r\n");
		sb_word.append("</wx:borders>\r\n");
		sb_word.append("<w:p wsp:rsidR='000B51A1' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a3'/>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</wx:pBdrGroup>\r\n");
		sb_word.append("</w:hdr>\r\n");

		sb_word.append("<w:ftr w:type='first'>\r\n");
		sb_word.append("<w:p wsp:rsidR='000B51A1' wsp:rsidRDefault='00CC6D5F'>\r\n");
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:pStyle w:val='a4'/>\r\n");
		sb_word.append("</w:pPr>\r\n");
		sb_word.append("</w:p>\r\n");
		sb_word.append("</w:ftr>\r\n");

		if (_isLand) { // ���Ҫ���Ǻ����,����ʱ���ɱ��̫��ʱ��Ҫ������ʾ
			sb_word.append("<w:pgSz w:w='16838' w:h='11906' w:orient='landscape'/>\r\n");
			sb_word.append("<w:pgMar w:top='1797' w:right='1440' w:bottom='1797' w:left='1440' w:header='851' w:footer='992' w:gutter='0'/>\r\n");
		} else { // ����������
			sb_word.append("<w:pgSz w:w='11906' w:h='16838'/>\r\n");
			sb_word.append("<w:pgMar w:top='1440' w:right='1800' w:bottom='1440' w:left='1800' w:header='851' w:footer='992' w:gutter='0'/>\r\n");
		}

		sb_word.append("<w:cols w:space='425'/>\r\n");
		sb_word.append("<w:docGrid w:type='lines' w:line-pitch='312'/>\r\n");

		sb_word.append("</w:sectPr>\r\n");

		sb_word.append("</wx:sect>\r\n");

		sb_word.append("</w:body>\r\n");
		sb_word.append("</w:wordDocument>\r\n"); // һ��Word�ļ��Ľ������!!

		return sb_word.toString();
	}

	/**
	 * ���������������ͷ����ҳüҳ�ź�����Ӵֵ����� zqx
	 */
	public String getIdNew(String width, String align, String name, int _spantype, boolean isYeMei, boolean isBold) {
		StringBuffer sb_word = new StringBuffer(); //
		sb_word.append("<w:tc>\r\n"); //

		// ���tcPr= table column propties
		sb_word.append("<w:tcPr>\r\n"); //
		sb_word.append("<w:tcW w:w=\"" + width + "\" w:type=\"dxa\" />\r\n"); //
		if (isYeMei) {
			sb_word.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"C0C0C0\"/>");
		}
		if (_spantype >= 0) {
			if (_spantype == 0) {
				sb_word.append("<w:vmerge w:val=\"restart\" />\r\n"); // �ϲ�������(�׶�)
			} else {
				sb_word.append("<w:vmerge />\r\n"); // �ϲ�������(�׶�)
			}
		}

		sb_word.append("</w:tcPr>\r\n"); //

		// ����
		sb_word.append("<w:p>\r\n");

		// pPr,����
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:jc w:val=\"" + align + "\" />\r\n"); // ����
		sb_word.append("<w:rPr><w:b /></w:rPr>\r\n");
		sb_word.append("</w:pPr>\r\n");
		// end pPr

		// ����
		sb_word.append("<w:r>\r\n"); //
		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<w:rFonts w:hint=\"fareast\" />\r\n"); //
		if (isBold) {
			sb_word.append("<wx:font wx:val=\"����\" /><w:b />\r\n");
		} else {
			sb_word.append("<wx:font wx:val=\"����\" />\r\n");
		}
		sb_word.append("</w:rPr>\r\n");
		sb_word.append("<w:t>" + name + "</w:t>\r\n"); // �ı�
		sb_word.append("</w:r>\r\n"); //

		sb_word.append("</w:p>\r\n"); //
		sb_word.append("</w:tc>\r\n"); //

		return sb_word.toString();

	}

	public String getTableXml(String[][] _data) {
		return getTableXml(_data, 2160, "left"); //
	}

	public String getTableXml(String[][] _data, int _colWidth) {
		return getTableXml(_data, _colWidth, "left"); //
	}

	public String getTableXml(String[][] _data, int _colWidth, String _align) {
		return getTableXml(_data, _colWidth, "left", null); //
	}

	/**
	 * ֱ�ӽ�һ����ά���������һ�����!
	 * @param _data
	 * @return
	 */
	public String getTableXml(String[][] _data, int _colWidth, String _align, String _headerBgColor) {
		StringBuffer sb_xml = new StringBuffer();
		String str_cellValue = null; //
		for (int i = 0; i < _data.length; i++) {
			sb_xml.append("<w:tr>");
			for (int j = 0; j < _data[i].length; j++) {
				str_cellValue = _data[i][j]; //
				if (str_cellValue == null) { //���Ϊ��,��ת���ɿմ�!!������һ��[null]
					str_cellValue = "";
				}
				if (str_cellValue.indexOf(">") >= 0 || str_cellValue.indexOf("<") >= 0) { //�����<>����,����ת���ɡ�![CDATA��
					str_cellValue = "<![CDATA[" + str_cellValue + "]]>"; //
				}
				if (i == 0 && _headerBgColor != null) { //����ǵ�һ��,����Զ����!���ض���ɫ!!
					sb_xml.append(getTd("" + _colWidth, "center", str_cellValue, -1, _headerBgColor)); //"CCFFCC"
				} else {
					sb_xml.append(getTd("" + _colWidth, _align, str_cellValue, -1));
				}
			}
			sb_xml.append("</w:tr>");
		}
		return getTableBegin() + sb_xml.toString() + getTableEnd(); //�������ͷ��+��������ݡ�+������β��
	}

	/**
	 * �õ����Ŀ�ʼ
	 * @return
	 */
	public String getTableBegin() {
		StringBuffer begin = new StringBuffer();
		begin.append("<w:tbl>");
		begin.append("<w:tblPr>");
		begin.append("<w:tblStyle w:val=\"a3\" />");
		begin.append("<w:tblW w:w=\"0\" w:type=\"auto\" />");
		begin.append("<w:tblBorders>");
		begin.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\" />");
		begin.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\" />");
		begin.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\" />");
		begin.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\" />");
		begin.append("<w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\" />");
		begin.append("<w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\" />");
		begin.append("</w:tblBorders>");
		begin.append("<w:tblLook w:val=\"01E0\" />");
		begin.append("</w:tblPr>");

		begin.append("<w:tblGrid>");
		begin.append("<w:gridCol w:w=\"2908\" />");
		begin.append("<w:gridCol w:w=\"1600\" />");
		begin.append("<w:gridCol	w:w=\"4000\" />");
		begin.append("</w:tblGrid>");

		return begin.toString();
	}

	/**
	 * �õ����Ľ�β
	 * @return
	 */
	public String getTableEnd() {
		return "</w:tbl><w:p/>";
	}

	public String getTd(String width, String align, String name, int _spantype) {
		return getTd(width, align, name, _spantype, null); //
	}

	/**
	 * 
	 * @param _width
	 * @param align,����ʽ,�С�center����left����right������
	 * @param _cellValue
	 * @param _spantype
	 * @return
	 */
	public String getTd(String _width, String _align, String _cellValue, int _spantype, String _bgColor) {
		StringBuffer sb_word = new StringBuffer(); //
		sb_word.append("<w:tc>\r\n"); //

		// ���tcPr= table column propties
		sb_word.append("<w:tcPr>\r\n"); //
		sb_word.append("<w:tcW w:w=\"" + _width + "\" w:type=\"dxa\" />\r\n"); //
		if (_spantype >= 0) {
			if (_spantype == 0) {
				sb_word.append("<w:vmerge w:val=\"restart\" />\r\n"); // �ϲ�������(�׶�)
			} else {
				sb_word.append("<w:vmerge />\r\n"); // �ϲ�������(�׶�)
			}
		}
		if (_bgColor != null && !_bgColor.trim().equals("")) { //���ָ����������ɫ
			sb_word.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"" + _bgColor + "\"/>\n"); //������ɫ!!
		}
		sb_word.append("</w:tcPr>\r\n"); //

		// ����
		sb_word.append("<w:p>\r\n");

		// pPr,����
		sb_word.append("<w:pPr>\r\n");
		sb_word.append("<w:jc w:val=\"" + _align + "\" />\r\n"); // ����
		sb_word.append("<w:rPr><w:b /></w:rPr>\r\n");
		sb_word.append("</w:pPr>\r\n");
		// end pPr

		// ����
		sb_word.append("<w:r>\r\n"); //
		sb_word.append("<w:rPr>\r\n");
		sb_word.append("<w:rFonts w:hint=\"fareast\" />\r\n"); //
		sb_word.append("<wx:font wx:val=\"����\" /><w:b />\r\n"); //
		sb_word.append("</w:rPr>\r\n");
		sb_word.append("<w:t>" + _cellValue + "</w:t>\r\n"); // �ı�
		sb_word.append("</w:r>\r\n"); //

		sb_word.append("</w:p>\r\n"); //
		sb_word.append("</w:tc>\r\n"); //

		return sb_word.toString();

	}

	public String getImageXml(String _code64, int _width, int _height) {
		return getImageXml(_code64, _width, _height, true); //
	}

	/**
	 * ����ͼƬ��64λ������ͼƬ��word xml
	 * 
	 * @param _code64
	 * @param _width
	 * @param _height
	 * @return
	 */
	public String getImageXml(String _code64, int _width, int _height, boolean _isAutoAdjust) {
		int li_realWidth = 0, li_realHeight = 0; //
		if (_isAutoAdjust) {
			int li_width_limit = 410; //��ȵ�����!��Ϊword��A4ֽ���ֻ������������!!!
			int li_height_limit = 680; //�߶ȵ�����!!
			if (_width > li_width_limit && _height > li_height_limit) { //���������Խ��,��ֻ��֤�߶�
				li_realHeight = li_height_limit; //�߶�
				li_realWidth = (_width * (li_height_limit * 100) / _height) / 100; //
				if (li_realWidth > li_width_limit) {
					li_realWidth = li_width_limit; //
				}
			} else {
				if (_width > li_width_limit) { //���ֻ�ǿ��Խ��,��ǿ�н����
					li_realWidth = li_width_limit; //
					li_realHeight = (_height * (li_width_limit * 100) / _width) / 100; //
				} else {
					li_realHeight = li_height_limit; //
					li_realWidth = (_width * (li_height_limit * 100) / _height) / 100; //
				}
			}

			if (li_realWidth > li_width_limit) {
				li_realWidth = li_width_limit;
			}
			if (li_realHeight > li_height_limit) {
				li_realHeight = li_height_limit;
			}
		} else {
			li_realWidth = _width; //
			li_realHeight = _height; //
		}

		StringBuffer sb_xml = new StringBuffer(); //
		// ͼƬ���������һ��������
		sb_xml.append("\r\n");
		sb_xml.append("<w:p>\r\n");
		sb_xml.append("<w:pPr>\r\n"); //
		sb_xml.append("<w:jc w:val='center'/>\r\n"); // ͼƬҪ����!!
		sb_xml.append("<w:rPr>\r\n");
		sb_xml.append("<w:rFonts w:hint='fareast'/>\r\n"); //
		sb_xml.append("</w:rPr>\r\n");
		sb_xml.append("</w:pPr>\r\n");
		sb_xml.append("<w:r>\r\n");
		sb_xml.append("<w:pict>\r\n");

		// ͼƬ�󶨵�����,��ؼ�
		long ll_jpgid = System.currentTimeMillis(); //
		sb_xml.append("<w:binData w:name=\"wordml://" + ll_jpgid + ".jpg\">\r\n");
		sb_xml.append(_code64); // ͼƬ��64λ��xml
		sb_xml.append("</w:binData>\r\n");

		sb_xml.append("<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:" + li_realWidth + "pt;height:" + li_realHeight + "pt\">\r\n");
		sb_xml.append("<v:imagedata src=\"wordml://" + ll_jpgid + ".jpg\" o:title=\"Sunset\" />\r\n");
		sb_xml.append("</v:shape>\r\n");

		sb_xml.append("</w:pict>\r\n");
		sb_xml.append("</w:r>\r\n");
		sb_xml.append("</w:p>\r\n"); //
		return sb_xml.toString(); //
	}

	/**
	 * HTML��ͷ
	 * 
	 * @param _title
	 * @return
	 */
	public String getHTMLBegin(String _title) {
		return getHTMLBegin(_title, "ffffff");
	}

	/**
	 * HTML��ͷ
	 * 
	 * @param _title
	 * @return
	 */
	public String getHTMLBegin(String _title, String _color) {
		StringBuffer sb_html = new StringBuffer();
		sb_html.append("<HTML>\r\n"); //
		sb_html.append("<HEAD>\r\n"); //
		sb_html.append("<TITLE>" + (_title == null ? "" : _title) + "</TITLE>\r\n"); //
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=gb2312\">\r\n");
		sb_html.append("</HEAD>\r\n"); //
		sb_html.append("<BODY bgcolor=#" + _color + ">\r\n"); //
		return sb_html.toString();
	}

	/**
	 * ���һ��Mht�ļ��Ŀ�ʼ����
	 * 
	 * @return
	 */
	public String getMHTBegin(String _title) {
		StringBuffer sb_html = new StringBuffer();
		sb_html.append("From: <�� Microsoft Internet Explorer 5 ����>\r\n");
		sb_html.append("Subject: export\r\n");
		sb_html.append("Date: Tue, 16 Sep 2008 13:53:06 +0800\r\n");
		sb_html.append("MIME-Version: 1.0\r\n");
		sb_html.append("Content-Type: multipart/related;\r\n");
		sb_html.append("  type=\"text/html\";\r\n");
		sb_html.append("  boundary=\"----=_NextPart_000_0000_01C91815.A55B3E50\"\r\n");
		sb_html.append("X-MimeOLE: Produced By Microsoft MimeOLE V6.00.2900.3198\r\n");
		sb_html.append("\r\n");
		sb_html.append("This is a multi-part message in MIME format.\r\n");
		sb_html.append("\r\n");
		sb_html.append("------=_NextPart_000_0000_01C91815.A55B3E50\r\n");
		sb_html.append("Content-Type: text/html;\r\n");
		sb_html.append("  charset=\"gb2312\"\r\n");
		sb_html.append("Content-Transfer-Encoding: quoted-printable\r\n");
		sb_html.append("Content-Location: =?gb2312?B?ZmlsZTovL0Q6XM7StcTOxLW1XNfAw+ZcYWFhYS5odG1s?=\r\n");
		sb_html.append("\r\n");
		sb_html.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n");
		sb_html.append("<HTML><HEAD><TITLE>" + (_title == null ? "" : _title) + "</TITLE>\r\n");
		sb_html.append("<META http-equiv=3DContent-Type content=3D\"text/html; charset=3Dgb2312\">\r\n");
		sb_html.append("<STYLE type=3Dtext/css>.style_1 {\r\n");
		sb_html.append("  FONT-SIZE: 12px; COLOR: #333333; LINE-HEIGHT: 18px; FONT-FAMILY: =\r\n");
		sb_html.append("=CB=CE=CC=E5");
		sb_html.append("}\r\n");
		sb_html.append("</STYLE>\r\n");
		sb_html.append("<META content=3D\"MSHTML 6.00.2900.3268\" name=3DGENERATOR>\r\n");
		sb_html.append("</HEAD>\r\n"); //
		sb_html.append("<BODY>\r\n");
		return sb_html.toString();
	}

	/**
	 * ��ͼƬ��װ��MHT����������!!! MHT���ǽ�ͼƬ���������һ���������,ֻ����ͨ���ļ�������������ݹ���������!!!
	 * 
	 * @param _fileName
	 * @param _64Code
	 * @return
	 */
	public String getMHTImageCode(String _fileName, String _64Code) {
		StringBuffer sb_html = new StringBuffer();
		sb_html.append("\r\n");
		sb_html.append("------=_NextPart_000_0000_01C91815.A55B3E50\r\n");
		sb_html.append("Content-Type: image/jpeg\r\n");
		sb_html.append("Content-Transfer-Encoding: base64\r\n");
		sb_html.append("Content-Location: file:///" + _fileName + "\r\n");
		sb_html.append("\r\n");
		sb_html.append(_64Code + "\r\n"); // ʵ�ʵ�64λ��!!
		sb_html.append("\r\n");
		sb_html.append("------------=_NextPart_000_0000_01C91815.A55B3E50--\r\n");
		sb_html.append("\r\n");
		return sb_html.toString();
	}

	public static void main(String[] _args) {
		String[][] str_data = new String[][] { { "��Ա", "����", "����" }, { "001", "����", "25" }, { "002", "�����", "25" }, { "003", "����", "32" } };
		String str_xml = new WordFileUtil().getWorldFileXmlByTable(str_data); //
		try {
			new TBUtil().writeStrToOutputStream(new FileOutputStream("C:/111222.doc", false), str_xml); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
}
