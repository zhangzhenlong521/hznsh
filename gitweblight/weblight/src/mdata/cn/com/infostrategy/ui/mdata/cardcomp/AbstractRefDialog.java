/**************************************************************************
 * $RCSfile: AbstractRefDialog.java,v $  $Revision: 1.13 $  $Date: 2012/11/08 07:27:52 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Container;
import java.awt.Frame;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 参照的基类,所有参照都继承于这个类!! 如果要写一个自定义参数,则也必须继承于这个类!
 * @author xch
 *
 */
public abstract class AbstractRefDialog extends BillDialog {

	private BillPanel billPanel = null;
	private RefItemVO initRefItemVO = null;
	private Pub_Templet_1_ItemVO pubTempletItemVO = null; //模板子表VO

	private int closeType = -1; //关闭类型
	private boolean isShowAfterInitialize = true; //是否在初始化后立即显示,按道理肯定是显示的,但在上似附件时,如果初始文件值为空,则不显示原来的窗口,而是显示直接上传文件!

	//	public abstract String getRefID(); //
	//
	//	public abstract String getRefCode(); //
	//
	//	public abstract String getRefName(); //

	public abstract void initialize(); //初始化页面!!需要要调用者调用

	/**
	 * 取得返回的RefItemVO
	 * 子类必须实现返回值,以前是三个方法,现在统一成一个方法,必须返回RefItemVO对象,
	 * 然后该对象中还可以包含一个HashVO,然后编辑公式就可以直接处理其中的值,强的很!
	 * @return
	 */
	public abstract RefItemVO getReturnRefItemVO(); //返回参照数据对象...

	/**
	 * 抽象参照窗口,只有一个可用的构造方法!!
	 * @param _parent
	 * @param _title
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public AbstractRefDialog(Container _parent, String _title, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_parent, _title); //不要在构造方法中调个什么静态方法,许多书上都建议不要这样搞..【xch/2012-11-08】
		this.initRefItemVO = _initRefItemVO; //初始值
		this.billPanel = _billPanel; //从哪个面板中带入的!!因为有的参照需要取到页面上任何一个位置的控件的值,比如主子表中,需要从子表的某一个列的参照中取到主表卡片中的某一项的值,或者直接操作之!!
		this.setResizable(true); //可改变大小的!!
		this.setSize(getInitWidth(), getInitHeight()); //设置宽度与高度
		initLocation(_parent); //初始化位置!!要居中!

		if (this.getTitle() != null && this.getTitle().toLowerCase().startsWith("<html>")) { //移到这儿,而且用这个判断,一看就明白!!【xch/2012-11-08】
			this.setTitle(htmlFilter(this.getTitle())); //
		}
	}

	/***
	 * 过滤Html信息, 只取其中的汉字部分和部分标点符号, 其它的不要 Gwang
	 */
	private String htmlFilter(String str) {
		String s = str.toLowerCase();
		StringBuffer sb = new StringBuffer();
		char[] chars = s.toCharArray();
		Pattern pattern = Pattern.compile("^[,.]");
		Matcher matcher = null;
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 2) {
				sb.append(new String(bytes));
			} else {
				matcher = pattern.matcher(String.valueOf(chars[i]));
				if (matcher.find()) {
					sb.append(new String(bytes));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 初始化页面大小与位置!!
	 * @param _parent
	 */
	private void initLocation(Container _parent) {
		Frame frame = JOptionPane.getFrameForComponent(_parent);
		double ld_screenWidth = frame.getSize().getWidth();
		double ld_screenHeight = frame.getSize().getHeight();
		double ld_x = frame.getLocation().getX();
		double ld_y = frame.getLocation().getY();
		double ld_thisX = ld_x + ld_screenWidth / 2 - getInitWidth() / 2; //
		double ld_thisY = ld_y + ld_screenHeight / 2 - getInitHeight() / 2; //
		if (ld_thisX < 0) {
			ld_thisX = 0;
		}
		if (ld_thisY < 0) {
			ld_thisY = 0;
		}
		this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue()); //
	}

	public boolean isShowAfterInitialize() {
		return this.isShowAfterInitialize; //是否在初始化后立即显示!!
	}

	public void setShowAfterInitialize(boolean _isShowAfterInitialize) {
		this.isShowAfterInitialize = _isShowAfterInitialize;
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 650;
	}

	public int getInitHeight() {
		return 500;
	}

	public int getCloseType() {
		return closeType;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}

	public RefItemVO getInitRefItemVO() {
		return initRefItemVO;
	}

	public BillPanel getBillPanel() {
		return billPanel;
	}

	public Pub_Templet_1_ItemVO getPubTempletItemVO() {
		return pubTempletItemVO;
	}

	public void setPubTempletItemVO(Pub_Templet_1_ItemVO pubTempletItemVO) {
		this.pubTempletItemVO = pubTempletItemVO;
	}

}
