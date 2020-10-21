package com.pushworld.ipushgrc.ui.rule.p010;

import java.awt.Color;
import java.awt.Container;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;

public class RuleWordBuilder {
	private ByteArrayOutputStream byteOutStream = null;
	private String id = null;
	private String rulename = null;
	Font filetitleFont;
	Font titleFont;
	private Font contextFont = null;
	private Document document = null;
	private HashVO[] childHashVOs = null;
	private Paragraph paragraph;
	private Paragraph paragraph1;
	private String itemtitle = "";
	private String itemcontent = "";
	private Container container = null;//������ʾ�����������������/2015-01-27��
	private boolean ifHaveItem = TBUtil.getTBUtil().getSysOptionBooleanValue("�ƶ��Ƿ����Ŀ", true);

	public RuleWordBuilder(String titilename, String id) {
		this(null, titilename, id);
	}

	public RuleWordBuilder(BillVO[] billVOs) {
		this(null, billVOs);
	}

	public RuleWordBuilder(Container _container, String titilename, String id) {
		this.id = id;
		this.rulename = titilename;
		this.container = _container;
		showTempRulebook();
	}

	/**
	 * ���������ƶ��ļ�[��Ӫ��/2013-04-10]
	 * */
	public RuleWordBuilder(Container _container, BillVO[] billVOs) {
		this.container = _container;
		exportAllTempRulebook(billVOs);
	}

	/**
	 * ʹ��itext����Word�ļ�������,���ص�������word��byte[]
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes() throws Exception {
		filetitleFont = new RtfFont("��_��", 16, Font.BOLD);// �ļ��������� 
		titleFont = new RtfFont("��_��", 14, Font.BOLD);// �½ڱ��������� 
		contextFont = new RtfFont("��_��", 12, Font.NORMAL);// ����������,�������
		contextFont.setColor(0, 0, 0);//��������һ�£���������յȼ���������������ɫ�����¸����յȼ�����ɫһ����
		document = new Document(PageSize.A4);
		byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);
		document.open();
		HeaderFooter footer = new HeaderFooter(null, true);
		footer.setBackgroundColor(new Color(SystemColor.window.getRed(), SystemColor.window.getGreen(), SystemColor.window.getBlue()));
		footer.setAlignment(Element.ALIGN_RIGHT);
		document.setFooter(footer);
		paragraph = new Paragraph(rulename.trim());
		paragraph.setSpacingBefore(40);
		paragraph.setSpacingAfter(20);
		paragraph.setAlignment(Element.ALIGN_CENTER);
		paragraph.setFont(filetitleFont);
		document.add(paragraph);
		addContentPage();
		document.close();
		return byteOutStream.toByteArray(); //����
	}

	/**
	 * ���ƶ���Ŀд�뵽word�ĵ���
	 */
	private void addContentPage() {
		try {
			for (int i = 0; i < childHashVOs.length; i++) {
				paragraph = new Paragraph();
				paragraph1 = new Paragraph();
				Chunk chunk = new Chunk();
				Chunk chunk1 = new Chunk();
				itemtitle = childHashVOs[i].getStringValue("itemtitle");
				itemcontent = childHashVOs[i].getStringValue("itemcontent");
				String[] ss = new String[2];
				if (itemtitle != null && !"".equals(itemtitle)) {
					if (itemtitle.contains(" ")) {
						ss[0] = itemtitle.substring(0, itemtitle.indexOf(" ")).trim();
						ss[1] = itemtitle.substring(itemtitle.indexOf(" "));
					} else {
						ss[0] = itemtitle;
						ss[1] = "";
					}
					paragraph.setFirstLineIndent(24);
					paragraph.setLeading(20);
					paragraph.setSpacingBefore(5);
					paragraph.setSpacingAfter(5);
					if (itemtitle != null && ((itemtitle.contains("��") || itemtitle.contains("��")) && ss[0].startsWith("��") && (ss[0].endsWith("��") || ss[0].endsWith("��")))) {
						paragraph.add(itemtitle.trim());
						paragraph.setFont(titleFont);
						paragraph.setAlignment(Element.ALIGN_CENTER);

						document.add(paragraph);

						if (itemcontent != null && !"".equals(itemcontent)) {
							paragraph1.add(itemcontent.trim());
							paragraph1.setFont(new RtfFont("��_��", 10, Font.NORMAL));
							paragraph1.setFirstLineIndent(24);
							paragraph1.setLeading(20);
							paragraph1.setSpacingBefore(5);
							paragraph1.setSpacingAfter(5);
							paragraph1.setAlignment(Element.ALIGN_LEFT);
							document.add(paragraph1);
						}
					} else {
						paragraph.setAlignment(Element.ALIGN_LEFT);
						chunk.append(itemtitle.trim());
						chunk.setFont(new RtfFont("��_��", 12, Font.BOLD));
						if (itemcontent != null && !"".equals(itemcontent)) {
							chunk1.append(itemcontent.trim());
						}
						chunk1.setFont(contextFont);
						paragraph.add(chunk);
						paragraph.add("  " + chunk1);
						paragraph.setAlignment(Element.ALIGN_LEFT);
						document.add(paragraph);
					}
				} else {
					paragraph1 = new Paragraph("");
					if (itemcontent != null && !"".equals(itemcontent)) {
						paragraph1.add(itemcontent.trim());
						paragraph1.setFont(new RtfFont("��_��", 10, Font.NORMAL));
						paragraph1.setFirstLineIndent(24);
						paragraph1.setLeading(20);
						paragraph1.setSpacingBefore(5);
						paragraph1.setSpacingAfter(5);
						paragraph1.setAlignment(Element.ALIGN_LEFT);
						document.add(paragraph1);
					}
				}

			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public void showTempRulebook() {
		JFileChooser chooser = new JFileChooser();
		try {
			//���ﻹ��ȷ���ļ����ͣ�����Ϊ.doc��.docx��.rar��.pdf �ȣ��ʲ������ļ����͡����/2016-03-03��
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + File.separator + rulename.trim()).getCanonicalPath());
			chooser.setSelectedFile(f);
			String str_path = "";
			int li_rewult = chooser.showSaveDialog(null);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					str_path = curFile.getAbsolutePath();
				}
			}
			if (str_path == null || str_path.equals("")) {
				return;
			}
			if (ifHaveItem) {//����ƶȷ���Ŀ�����/2015-01-27��
				String sql = "select * from RULE_RULE_ITEM where ruleid = '" + id + "'  order by linkcode,abs(id)";
				childHashVOs = UIUtil.getHashVoArrayAsTreeStructByDS(null, sql, "id", "parentid", "seq", null);
				if (childHashVOs.length > 0) {
					byte[] bytes = this.getDocContextBytes();
					if (!str_path.contains(".")) {
						str_path += ".doc";
					}
					FileOutputStream output = new FileOutputStream(str_path);
					output.write(bytes);
					output.close();
					Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler  \"" + str_path + "\"");
					return;
				}
			}

			//������ƶ�û����Ŀ����鿴�Ƿ������ģ��Ƿ��и��������/2015-01-27��
			HashVO hvos[] = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where id = " + id);
			if (hvos.length > 0) {
				String textfile = hvos[0].getStringValue("textfile");
				String attachfile = hvos[0].getStringValue("attachfile");

				String path = str_path.substring(0, str_path.lastIndexOf(File.separator));
				String filename = str_path.substring(str_path.lastIndexOf(File.separator) + 1);
				if (!TBUtil.isEmpty(textfile)) {
					if (textfile.contains(".")) {//�Ż��ļ���׺����Ϊ�ͻ������������ϴ�pdf�ļ���ֱ�Ӻ�׺.doc�ᱨ�������/2016-03-03��
						if (filename.contains(".")) {
							filename = filename.substring(0, filename.indexOf(".")) + textfile.substring(textfile.lastIndexOf("."));
						} else {
							filename = filename + textfile.substring(textfile.lastIndexOf("."));
						}
					}
					UIUtil.downLoadFile("/officecompfile", textfile, false, path, filename, true);
				} else if (!TBUtil.isEmpty(attachfile)) {
					String[] attachfiles = TBUtil.getTBUtil().split(attachfile, ";");
					if (attachfiles == null || attachfiles.length == 0) {
						MessageBox.show(this.container, "���ƶ�û������.");
						return;
					} else {
						if (attachfiles[0].contains(".")) {//�Ż��ļ���׺����Ϊ�ͻ������ڸ����ϴ�rar�ļ���ֱ�Ӻ�׺.doc�ᱨ�������/2016-03-03��
							if (filename.contains(".")) {
								filename = filename.substring(0, filename.indexOf(".")) + attachfiles[0].substring(attachfiles[0].lastIndexOf("."));
							} else {
								filename = filename + attachfiles[0].substring(attachfiles[0].lastIndexOf("."));
							}
						}
						UIUtil.downLoadFile("/upload", attachfiles[0], false, path, filename, true);//����и�������ֻ���ص�һ��
					}
				} else {
					MessageBox.show(this.container, "���ƶ�û������.");
					return;
				}
				str_path = str_path.substring(0, str_path.lastIndexOf(File.separator) + 1) + filename;//�Ż��ļ���׺�����/2016-03-03��
				Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler  \"" + str_path + "\"");
			} else {
				MessageBox.show(this.container, "���ݿ���δ�ҵ����ƶ�.");
				return;
			}
		} catch (Exception ex) {
			MessageBox.show(this.container, "��һ����������ʹ�ô��ļ�");
			ex.printStackTrace(); //
		}
	}

	/**
	 * ���������߼�����[��Ӫ��/2013-04-10]
	 * */
	public void exportAllTempRulebook(BillVO[] billVO) {
		final BillVO[] billVOs = billVO;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ����Ŀ¼");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(null);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}

		SplashWindow _splash = new SplashWindow(null, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				List list = new ArrayList();
				List rulelist = new ArrayList();//û���ҵ����ݵ��ƶȡ����/2015-01-27��
				for (int i = 0; i < billVOs.length; i++) {
					try {
						StringBuilder builder = new StringBuilder();
						String name = billVOs[i].getStringValue("rulename");
						String id = billVOs[i].getStringValue("id");
						String state = billVOs[i].getStringValue("state");
						String filename = name;
						if (list.contains(name)) {
							filename += "(" + state + ")";
							while (list.contains(filename)) {
								filename += " ";//���ƺ�ӿո񣬷�ֹ�ظ������/2016-03-03��
							}
						}
						list.add(filename);
						exportCMPFileAsDocFile((SplashWindow) e.getSource(), i, billVOs.length, id, filename, str_path, name, rulelist);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if (rulelist.size() > 0) {
					StringBuffer sb_rule = new StringBuffer("����" + rulelist.size() + "���ƶ�û�����ݣ�\r\n");
					for (int i = 0; i < rulelist.size(); i++) {
						sb_rule.append((i + 1) + ".");
						sb_rule.append(rulelist.get(i));
						sb_rule.append("\r\n");
					}
					MessageBox.show(container, "�ɹ�����" + (billVOs.length - rulelist.size()) + "���ƶȣ�" + sb_rule.toString());
				} else {
					MessageBox.show(container, "�����ɹ�");
				}
			}
		}, 600, 130, 300, 300, false);

	}

	/**
	 * ���������߼�����[��Ӫ��/2013-04-10]
	 * */
	private void exportCMPFileAsDocFile(SplashWindow splash, int _num, int sum, String id, String ruleName, String path, String name, List _rulelist) throws Exception {
		this.id = id;
		this.rulename = name;
		String fileName = path + File.separator + ruleName + ".doc";
		if (ifHaveItem) {//����ƶȷ���Ŀ�����/2015-01-27��
			String sql = "select * from RULE_RULE_ITEM where ruleid = '" + id + "'  order by linkcode,abs(id)";
			childHashVOs = UIUtil.getHashVoArrayAsTreeStructByDS(null, sql, "id", "parentid", "seq", null);
			if (childHashVOs.length > 0) {//����ƶ�����Ŀ
				byte[] bytes = this.getDocContextBytes();
				FileOutputStream output = new FileOutputStream(fileName);
				output.write(bytes);
				output.close();
				splash.setWaitInfo("���ɵ�" + _num + "���ļ���" + ruleName + "�� ����" + sum + "���ļ�");
				return;
			}
		}
		//������ƶ�û����Ŀ����鿴�Ƿ������ģ��Ƿ��и��������/2015-01-27��
		HashVO hvos[] = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where id = " + id);
		if (hvos.length > 0) {
			String textfile = hvos[0].getStringValue("textfile");
			String attachfile = hvos[0].getStringValue("attachfile");
			if (!TBUtil.isEmpty(textfile)) {
				String filetype = ".doc";
				if (textfile.contains(".")) {//�Ż��ļ����͡����/2016-02-29��
					filetype = textfile.substring(textfile.lastIndexOf("."));
				}
				UIUtil.downLoadFile("/officecompfile", textfile, false, path, ruleName + filetype, true);
			} else if (!TBUtil.isEmpty(attachfile)) {
				String[] attachfiles = TBUtil.getTBUtil().split(attachfile, ";");
				if (attachfiles == null || attachfiles.length == 0) {
					_rulelist.add(ruleName);
				} else {
					String filetype = ".doc";
					if (attachfiles[0] != null && attachfiles[0].contains(".")) {//�Ż��ļ����͡����/2016-02-29��
						filetype = attachfiles[0].substring(attachfiles[0].lastIndexOf("."));
					}
					UIUtil.downLoadFile("/upload", attachfiles[0], false, path, ruleName + filetype, true);//����и�������ֻ���ص�һ��
				}
			} else {
				_rulelist.add(ruleName);
			}
		} else {
			_rulelist.add(ruleName);
		}
		splash.setWaitInfo("���ɵ�" + _num + "���ļ���" + ruleName + "�� ����" + sum + "���ļ�");
	}
}
