package cn.com.infostrategy.ui.mdata;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;

/**
 * �������Ĺ�ͬ����
 * @author xch
 *
 */
public abstract class BillPanel extends JPanel {
	/**
	 * ͨ����Ϊ��Ĭ��ʵ�֣���ȡ��ǰ����format���
	 * @return
	 */
	public BillFormatPanel getLoaderBillFormatPanel() {
		return null;
	}

	/**
	 * ��������������ɶ�Ӧ��html
	 * @param _billVO
	 * @param _objectValueText
	 * @param _itemkey
	 * @param _itemtype
	 * @return
	 */
	protected String getComponentToHrefHtml(final BillVO _billVO, final String _objectValueText, final String _itemkey, final String _itemtype) {
		StringBuffer strbf_html = new StringBuffer("&nbsp;&nbsp;");
		StringBuffer strbf_url = new StringBuffer();
		if (_itemtype.equals(WLTConstants.COMP_OFFICE)) {
			//��htmlҳ��������ļ����ӳ�����
			String str_file = _billVO.getStringValue(_itemkey); //
			strbf_url.append(System.getProperty("CALLURL") + "/DownLoadFileServlet?pathtype=" + "office" + "&filename=" + str_file); //��ǰ����Office�ؼ��򿪵�,��������ҵ�ͻ����־�Ȼoffice�ؼ����ܱ���,���Ըɴ�����ֱ�����ص�!
			strbf_html.append("<a target=\"_blank\" href=\"").append(strbf_url.toString()).append("\" >").append(_objectValueText).append("</a>");
		} else if (_itemtype.equals(WLTConstants.COMP_FILECHOOSE)) {
			//��htmlҳ��ĸ����ļ����ӡ����ء��ĳ�����
			String str_file = _billVO.getStringValue(_itemkey);
			if (str_file == null || "".endsWith(str_file)) {
				return "";
			}
			String[] files = str_file.split(";");
			String[] viewfiles = _objectValueText.split(";");
			for (int i = 0; i < viewfiles.length; i++) {
				strbf_url.append(System.getProperty("CALLURL")).append("/DownLoadFileServlet?pathtype=upload&filename=").append(files[i]); //
				strbf_html.append("<a target=\"_blank\" href=\"").append(strbf_url.toString()).append("\" >").append(viewfiles[i]).append("</a><br>&nbsp;&nbsp;");
				strbf_url.delete(0, strbf_url.length());
			}
		}
		return strbf_html.toString();
	}

	public Pub_Templet_1VO getTempletVO() {
		if (this instanceof BillListPanel) {
			return ((BillListPanel) this).getTempletVO(); //
		} else if (this instanceof BillCardPanel) {
			return ((BillCardPanel) this).getTempletVO(); //
		} else if (this instanceof BillTreePanel) {
			return ((BillTreePanel) this).getTempletVO(); //
		} else {
			return null;
		}
	}

}
