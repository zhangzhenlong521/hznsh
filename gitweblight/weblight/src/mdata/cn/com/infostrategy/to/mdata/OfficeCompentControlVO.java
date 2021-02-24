package cn.com.infostrategy.to.mdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Office控件的定义,可以指定是否可编辑/拷贝,是否可打印,书签替换
 * @author xch
 *
 */
public class OfficeCompentControlVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean editable = true; //
	private boolean printable = true; //
	private boolean titlebar = true;
	private boolean menubar = true;
	private boolean toolbar = true;
	private boolean saveas = false;//
	private boolean menutoolbar = true;
	private boolean ifshowsave = true;//是否显示保存
	private boolean ifshowprint_tao = true;//是否显示套打
	private boolean ifshowprint_all = true;//是否显示全打
	private boolean ifshowprint_fen = true;//是否显示分打
	private boolean ifshowprint = true;//是否显示打印
	private boolean ifshowwater = true;//是否显示水印
	private boolean ifshowshowcomment = true;//是否显示显示批注
	private boolean ifshowhidecomment = true;//隐藏批注
	private boolean ifshowedit = true;//修订
	private boolean ifshowshowedit = true;//显示修订
	private boolean ifshowhideedit = true;//隐藏修订
	private boolean ifshowacceptedit = true;//接收修订
	private boolean ifshowclose = true;//关闭
	private boolean ifselfdesc = false;
	private String subdir = null; //

	private HashMap bookmarkValue = null;
	private boolean isEditSaveAsEditable = true; // liuxuanfei
	private boolean ifshowresult = true; // haoming
	private String encryCode = null; // liuxuanfei
	private String saveAsName = "";//设置另存为的默认文件名称【李春娟/2012-03-12】
	private boolean isAbsoluteSeverDir = false;

	/**
	 * @return the isAbsoluteSeverDir
	 */
	public boolean isAbsoluteSeverDir() {
		return isAbsoluteSeverDir;
	}

	/**
	 * @param isAbsoluteSeverDir the isAbsoluteSeverDir to set
	 */
	public void setAbsoluteSeverDir(boolean isAbsoluteSeverDir) {
		this.isAbsoluteSeverDir = isAbsoluteSeverDir;
	}

	public OfficeCompentControlVO() {

	}

	public OfficeCompentControlVO(boolean _editable, boolean _printable, boolean _titlebar, HashMap _bookMarkMap) {
		this.editable = _editable;
		this.printable = _printable; //
		this.titlebar = _titlebar;
		this.bookmarkValue = _bookMarkMap; //
	}

	public OfficeCompentControlVO(boolean _editable, boolean _printable, boolean _titlebar, boolean _menubar, boolean _toolbar, boolean menutoolbar, HashMap _bookMarkMap) {
		this.editable = _editable;
		this.printable = _printable; //
		this.titlebar = _titlebar;
		this.menubar = _menubar;
		this.toolbar = _toolbar;
		this.menutoolbar = menutoolbar;
		this.bookmarkValue = _bookMarkMap; //
	}

	public OfficeCompentControlVO(boolean isSaveas) {
		this.saveas = isSaveas;
	}

	public boolean isMenubar() {
		return menubar;
	}

	public void setMenubar(boolean menubar) {
		this.menubar = menubar;
	}

	public boolean isToolbar() {
		return toolbar;
	}

	public void setToolbar(boolean toolbar) {
		this.toolbar = toolbar;
	}

	public boolean isMenutoolbar() {
		return menutoolbar;
	}

	public void setMenutoolbar(boolean menutoolbar) {
		this.menutoolbar = menutoolbar;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isTitlebar() {
		return titlebar;
	}

	public void setTitlebar(boolean titlebar) {
		this.titlebar = titlebar;
	}

	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public HashMap getBookmarkValue() {
		return bookmarkValue;
	}

	public void setBookmarkValue(HashMap bookmarkValue) {
		this.bookmarkValue = bookmarkValue;
	}

	/**
	 * 深度克隆..
	 * @return
	 */
	public OfficeCompentControlVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (OfficeCompentControlVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	public String getSubdir() {
		return subdir;
	}

	public void setSubdir(String subdir) {
		this.subdir = subdir;
	}

	public boolean isIfshowsave() {
		return ifshowsave;
	}

	public void setIfshowsave(boolean ifshowsave) {
		this.ifshowsave = ifshowsave;
	}

	public boolean isIfshowprint_tao() {
		return ifshowprint_tao;
	}

	public void setIfshowprint_tao(boolean ifshowprint_tao) {
		this.ifshowprint_tao = ifshowprint_tao;
	}

	public boolean isIfshowprint_all() {
		return ifshowprint_all;
	}

	public void setIfshowprint_all(boolean ifshowprint_all) {
		this.ifshowprint_all = ifshowprint_all;
	}

	public boolean isIfshowprint_fen() {
		return ifshowprint_fen;
	}

	public void setIfshowprint_fen(boolean ifshowprint_fen) {
		this.ifshowprint_fen = ifshowprint_fen;
	}

	public boolean isIfshowprint() {
		return ifshowprint;
	}

	public void setIfshowprint(boolean ifshowprint) {
		this.ifshowprint = ifshowprint;
	}

	public boolean isIfshowwater() {
		return ifshowwater;
	}

	public void setIfshowwater(boolean ifshowwater) {
		this.ifshowwater = ifshowwater;
	}

	public boolean isIfshowshowcomment() {
		return ifshowshowcomment;
	}

	public void setIfshowshowcomment(boolean ifshowshowcomment) {
		this.ifshowshowcomment = ifshowshowcomment;
	}

	public boolean isIfshowhidecomment() {
		return ifshowhidecomment;
	}

	public void setIfshowhidecomment(boolean ifshowhidecomment) {
		this.ifshowhidecomment = ifshowhidecomment;
	}

	public boolean isIfshowedit() {
		return ifshowedit;
	}

	public void setIfshowedit(boolean ifshowedit) {
		this.ifshowedit = ifshowedit;
	}

	public boolean isIfshowshowedit() {
		return ifshowshowedit;
	}

	public void setIfshowshowedit(boolean ifshowshowedit) {
		this.ifshowshowedit = ifshowshowedit;
	}

	public boolean isIfshowhideedit() {
		return ifshowhideedit;
	}

	public void setIfshowhideedit(boolean ifshowhideedit) {
		this.ifshowhideedit = ifshowhideedit;
	}

	public boolean isIfshowacceptedit() {
		return ifshowacceptedit;
	}

	public void setIfshowacceptedit(boolean ifshowacceptedit) {
		this.ifshowacceptedit = ifshowacceptedit;
	}

	public boolean isIfshowclose() {
		return ifshowclose;
	}

	public void setIfshowclose(boolean ifshowclose) {
		this.ifshowclose = ifshowclose;
	}

	public boolean isIfselfdesc() {
		return ifselfdesc;
	}

	public void setIfselfdesc(boolean ifselfdesc) {
		this.ifselfdesc = ifselfdesc;
	}

	public boolean isSaveas() {
		return saveas;
	}

	public void setSaveas(boolean saveas) {
		this.saveas = saveas;
	}

	public boolean isEditSaveAsEditable() {
		return isEditSaveAsEditable;
	}

	public void setEditSaveAsEditable(boolean isEditSaveAsEditable) {
		this.isEditSaveAsEditable = isEditSaveAsEditable;
	}

	public boolean ifshowresult() {
		return ifshowresult;
	}

	public void setIfShowResult(boolean ifshowResult) {
		this.ifshowresult = ifshowResult;
	}

	public String getEncryCode() {
		return encryCode;
	}

	public void setEncryCode(String encryCode) {
		this.encryCode = encryCode;
	}

	public String getSaveAsName() {
		return saveAsName;
	}

	public void setSaveAsName(String saveAsName) {
		this.saveAsName = saveAsName;
	}
}
