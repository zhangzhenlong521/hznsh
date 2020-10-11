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
 * ���յĻ���,���в��ն��̳��������!! ���Ҫдһ���Զ������,��Ҳ����̳��������!
 * @author xch
 *
 */
public abstract class AbstractRefDialog extends BillDialog {

	private BillPanel billPanel = null;
	private RefItemVO initRefItemVO = null;
	private Pub_Templet_1_ItemVO pubTempletItemVO = null; //ģ���ӱ�VO

	private int closeType = -1; //�ر�����
	private boolean isShowAfterInitialize = true; //�Ƿ��ڳ�ʼ����������ʾ,������϶�����ʾ��,�������Ƹ���ʱ,�����ʼ�ļ�ֵΪ��,����ʾԭ���Ĵ���,������ʾֱ���ϴ��ļ�!

	//	public abstract String getRefID(); //
	//
	//	public abstract String getRefCode(); //
	//
	//	public abstract String getRefName(); //

	public abstract void initialize(); //��ʼ��ҳ��!!��ҪҪ�����ߵ���

	/**
	 * ȡ�÷��ص�RefItemVO
	 * �������ʵ�ַ���ֵ,��ǰ����������,����ͳһ��һ������,���뷵��RefItemVO����,
	 * Ȼ��ö����л����԰���һ��HashVO,Ȼ��༭��ʽ�Ϳ���ֱ�Ӵ������е�ֵ,ǿ�ĺ�!
	 * @return
	 */
	public abstract RefItemVO getReturnRefItemVO(); //���ز������ݶ���...

	/**
	 * ������մ���,ֻ��һ�����õĹ��췽��!!
	 * @param _parent
	 * @param _title
	 * @param _initRefItemVO
	 * @param _billPanel
	 */
	public AbstractRefDialog(Container _parent, String _title, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super(_parent, _title); //��Ҫ�ڹ��췽���е���ʲô��̬����,������϶����鲻Ҫ������..��xch/2012-11-08��
		this.initRefItemVO = _initRefItemVO; //��ʼֵ
		this.billPanel = _billPanel; //���ĸ�����д����!!��Ϊ�еĲ�����Ҫȡ��ҳ�����κ�һ��λ�õĿؼ���ֵ,�������ӱ���,��Ҫ���ӱ��ĳһ���еĲ�����ȡ������Ƭ�е�ĳһ���ֵ,����ֱ�Ӳ���֮!!
		this.setResizable(true); //�ɸı��С��!!
		this.setSize(getInitWidth(), getInitHeight()); //���ÿ����߶�
		initLocation(_parent); //��ʼ��λ��!!Ҫ����!

		if (this.getTitle() != null && this.getTitle().toLowerCase().startsWith("<html>")) { //�Ƶ����,����������ж�,һ��������!!��xch/2012-11-08��
			this.setTitle(htmlFilter(this.getTitle())); //
		}
	}

	/***
	 * ����Html��Ϣ, ֻȡ���еĺ��ֲ��ֺͲ��ֱ�����, �����Ĳ�Ҫ Gwang
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
	 * ��ʼ��ҳ���С��λ��!!
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
		return this.isShowAfterInitialize; //�Ƿ��ڳ�ʼ����������ʾ!!
	}

	public void setShowAfterInitialize(boolean _isShowAfterInitialize) {
		this.isShowAfterInitialize = _isShowAfterInitialize;
	}

	/**
	 * ��ʼ���
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
