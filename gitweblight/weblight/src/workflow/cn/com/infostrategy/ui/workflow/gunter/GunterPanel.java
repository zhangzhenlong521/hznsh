package cn.com.infostrategy.ui.workflow.gunter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import sun.awt.SunHints;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * ʱ������ͼ���,�ǳ��������һ������!����Ŀ�����Ӧ�����ر�����!
 * �����˫����ʱ������ͼ��,�ȸ���ͼ�������ã���ȻҲ�����Ѷ�!
 * @author xch
 *
 */
public class GunterPanel extends JPanel {

	private static final long serialVersionUID = 8025585872963903829L;
	private HashVO[] hvs = null;
	private int li_oneDayWidth = 60; //һ��Ŀ��!

	private Color bgLineColor = new Color(200, 200, 200); //�����ߵ���ɫ!!��ǳɫ!!
	private TBUtil tbUtil = new TBUtil(); //
	private Font font = new Font("������", Font.PLAIN, 12);
	private Color brokeColor = new Color(255, 0, 255); //���ߵ���ɫ!
	private Color waveColor = new Color(0, 0, 255); //�����ߵ���ɫ

	private HashMap beginDayMap = new HashMap(); //
	private int li_Alldays = 0; // 
	private String str_lastWorkCode = null; //���һ������
	private StringBuffer sb_exception = new StringBuffer(); //

	/**
	 * ���췽��...
	 * @param _hvs
	 */
	public GunterPanel(HashVO[] _hvs) {
		this.hvs = _hvs;
		computeBeginDay(); //������ʼ��!
		this.setBackground(Color.WHITE); //�����ǰ�ɫ!!
		this.setPreferredSize(new Dimension((li_Alldays + 2) * li_oneDayWidth + 60, 600)); //

	}

	@Override
	public void paint(Graphics _g) {
		super.paint(_g); //
		Graphics2D g2 = (Graphics2D) _g; //
		g2.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_QUALITY);

		g2.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
		g2.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);

		g2.setFont(font); //
		if (sb_exception.toString().length() > 0) { //������쳣����(����computeBeginDay�����п��ܼ����ĳ�������쳣,��������Ϊ��,˳�򲻶�!)����ֱ���˳�!!
			g2.setColor(Color.RED); //
			String[] str_items = tbUtil.split(sb_exception.toString(), "\r"); //
			for (int i = 0; i < str_items.length; i++) {
				g2.drawString(str_items[i], 85, 85 + (i * 20)); //
			}
			return; //
		}

		g2.setPaint(bgLineColor); //
		g2.drawLine(10, 10, 10 + (li_Alldays + 1) * li_oneDayWidth, 10); //��һ������!
		g2.drawLine(10, 35, 10 + (li_Alldays + 1) * li_oneDayWidth, 35); //�ڶ�������!
		g2.drawLine(10, 60, 10 + (li_Alldays + 1) * li_oneDayWidth, 60); //����������!

		g2.setPaint(Color.BLACK); //���Ǻڵ�!!
		g2.drawString("���", 22, 28); //���
		g2.drawString("����", 22, 52); //���

		long ll_beginTime = getDateTime("2014-06-15"); //����һ�쿪ʼ??!!!
		GregorianCalendar gc = new GregorianCalendar();
		for (int i = 0; i <= li_Alldays; i++) { //��������!!!
			int li_x = 10 + (i * li_oneDayWidth);
			g2.setPaint(bgLineColor); //
			g2.drawLine(li_x, 10, li_x, 600); //�������!
			if (i >= 1) {
				gc.setTime(new Date(ll_beginTime + ((i - 1) * 1000 * 3600 * 24))); //�޸�����!
				int li_month = gc.get(GregorianCalendar.MONTH) + 1; //
				int li_day = gc.get(GregorianCalendar.DAY_OF_MONTH); //�ڼ���
				String str_day = "" + li_month + "/" + li_day + ""; //
				int li_weekDay = gc.get(GregorianCalendar.DAY_OF_WEEK); //
				if (li_weekDay == 1 || li_weekDay == 7) { //�����������,����ɫ
					g2.setPaint(Color.GREEN); //���Ǻڵ�!!
					g2.fillRect(li_x + 1, 36, li_oneDayWidth - 1, 25 - 1); //
				}

				g2.setPaint(Color.BLACK); //���Ǻڵ�!!
				g2.drawString("" + i, li_x + 30, 28); //���
				g2.drawString(str_day, li_x + 20, 52); //����!��������������ɫ��ʾ!
			}

			if (i == li_Alldays) { //ѭ���е����һ��!
				g2.setPaint(bgLineColor); //
				g2.drawLine(10 + (li_Alldays + 1) * li_oneDayWidth, 10, 10 + (li_Alldays + 1) * li_oneDayWidth, 600); //���һ����
			}
		}

		g2.setPaint(bgLineColor); //
		g2.drawLine(10, 600, 10 + (li_Alldays + 1) * li_oneDayWidth, 600); //����!!���ż��������ʵ�ʲ��������ܵ�ʱ����;����!!

		g2.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_ON);

		HashSet lineAreaHst = new HashSet(); //��¼������������������!
		HashMap actLevelMap = new HashMap(); //��¼�������ڵ�ʵ�ʲ��Map
		HashMap ringMap = new HashMap(); //��¼����ԲȦ����
		HashMap lineWayMap = new HashMap(); //��¼�ߵ������Map
		int li_index = 1;
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			int li_days = hvs[i].getIntegerValue("days"); //����ʱ��!!
			String str_fronts = hvs[i].getStringValue("fronts", ""); //��ǰ����!
			int li_thisBeginDay = findBeginDay(str_code); //Ѱ�ҳ�ĳ�����ڵ���ʼ��!��΢�е��Ѷȵ��㷨!��Ҫ�ҳ����и�����������Ǹ���
			int li_thisEndDay = li_thisBeginDay + li_days; //

			//�����ҳ����ĸ������Ǹ������Լ������ĸ��ף����ҳ����Ĳ�!!Ȼ���ҿ϶������Ƚ���������׺��棡��
			String[] str_parent = findParentLevel(str_code, str_fronts, li_thisBeginDay, actLevelMap); //�ҳ����׹�����ʵ�ʲ�!
			String str_parentCode = str_parent[0]; //�ҵ�����ĸ���!
			int li_parentLevel = Integer.parseInt(str_parent[1]); //���׵Ĳ�!!
			int li_level = findLevel(str_code, lineAreaHst, li_thisBeginDay, li_thisEndDay, li_parentLevel, actLevelMap); //�Ӹ��׹�����ʵ�ʲ�������!!

			String[] str_items = tbUtil.split(str_fronts, ","); //�����м�������
			if (str_items.length <= 1) { //���û�и��׹�������ֻ��һ��,��Ҫ�ж�һ�£���ǰ��Ĺ��Ƿ��Ѿ�������??��������ˣ���Ҫ�ٻ���!
				if (li_level == li_parentLevel) { //�����ͬһ��,��˵���϶������ӵ�,��ֱ�ӻ�!
					drawActivity(g2, "" + li_index, li_thisBeginDay, li_level); //�����ҵ���ʼ��!!���ܲ���Ҫ��! ���������һ�������Ľ�ǰ��������һ��

					//��¼�ҵĸ����ѻ�!!
					HashMap ringItemMap = new HashMap(); //ԲȦ�����ݵ�Mapֵ
					ringItemMap.put("day", new Integer(li_thisBeginDay)); //��¼�ǵڼ���,Ҳ����X����!
					ringItemMap.put("level", new Integer(li_level)); //��¼�ǵڼ���,Ҳ����Y����!
					ringItemMap.put("code", str_code); //��¼���ĸ�����!
					ringMap.put("" + li_index, ringItemMap); //��¼��ԲȦ
					li_index++; //

					drawLine(g2, str_code, li_thisBeginDay, li_thisEndDay, li_level, lineWayMap, lineAreaHst); //����������!��Ҫ�ж�����������ڣ�����Ѿ������ˣ���Ҫ������һ�㻭����
				} else { //�������ͬһ��,��˵����Ҫ��Ȧ,��Ҫ������!
					int li_parEndDay = getEndDayByCode(str_items[0]);
					int li_parLevel = (Integer) actLevelMap.get(str_items[0]); //

					String str_ring = findRing(li_parLevel, li_parEndDay, ringMap); //���λ���Ƿ���ԲȦ?
					if (str_ring == null) { //���û��Ȧ,��϶�Ҫ����ԲȦ!
						drawActivity(g2, "" + li_index, li_parEndDay, li_parLevel); //�����ҵ���ʼ��!!���ܲ���Ҫ��! ���������һ�������Ľ�ǰ��������һ��

						//��¼�ҵĸ����ѻ�!!
						HashMap ringItemMap = new HashMap(); //ԲȦ�����ݵ�Mapֵ
						ringItemMap.put("day", new Integer(li_parEndDay)); //��¼�ǵڼ���,Ҳ����X����!
						ringItemMap.put("level", new Integer(li_parLevel)); //��¼�ǵڼ���,Ҳ����Y����!
						ringItemMap.put("code", str_code); //��¼���ĸ�����!
						ringMap.put("" + li_index, ringItemMap); //��¼��ԲȦ
						li_index++; //
					}

					//������!!
					drawLine2(g2, str_code, li_parEndDay, li_parLevel, li_thisEndDay, li_level, ringMap, lineWayMap, lineAreaHst); //����������!��Ҫ�ж�����������ڣ�����Ѿ������ˣ���Ҫ������һ�㻭����
				}
			} else { //������������ϵĸ��ף���϶�Ҫ��ǰ���Ȧ!
				drawActivity(g2, "" + li_index, li_thisBeginDay, li_level); //�����ҵ���ʼ��!!���ܲ���Ҫ��! ���������һ�������Ľ�ǰ��������һ��
				HashMap ringItemMap = new HashMap(); //ԲȦ�����ݵ�Mapֵ
				ringItemMap.put("day", new Integer(li_thisBeginDay)); //��¼�ǵڼ���,Ҳ����X����!
				ringItemMap.put("level", new Integer(li_level)); //��¼�ǵڼ���,Ҳ����Y����!
				ringItemMap.put("code", str_code); //��¼���ĸ�����!
				ringMap.put("" + li_index, ringItemMap); //��¼��ԲȦ
				li_index++; //
				drawLine(g2, str_code, li_thisBeginDay, li_thisEndDay, li_level, lineWayMap, lineAreaHst); //����������!��Ҫ�ж�����������ڣ�����Ѿ������ˣ���Ҫ������һ�㻭����

				//��¼����!!
			}

			actLevelMap.put(str_code, new Integer(li_level)); //��¼�¸ù�����ʵ�ʲ�!��Ϊ���ĺ�������Ĭ��Ӧ�þ�����ò�һ��,�������븸��ԭ������ͬһ�㣡
		}

		//����ѭ��һ��!��Ϊ���ܿ��ǵ���Щ���������ɣ������Ϊ��֪���������������Ի��߲�׼!�Ȳ�������!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			String str_fronts = hvs[i].getStringValue("fronts", ""); //��ǰ����!
			if (str_fronts == null || str_fronts.trim().equals("")) {
				continue;
			}

			String[] str_parents = tbUtil.split(str_fronts, ","); //
			if (str_parents.length <= 1) { //���ֻ��һ������Ҳ������!
				continue;
			}

			int li_beginDay = findBeginDay(str_code); //Ѱ�ҳ�ĳ�����ڵ���ʼ��!��΢�е��Ѷȵ��㷨!��Ҫ�ҳ����и�����������Ǹ���
			int li_level = (Integer) actLevelMap.get(str_code); //

			for (int k = 0; k < str_parents.length; k++) { //����������!
				int li_parLevel = (Integer) actLevelMap.get(str_parents[k]); //�ڼ���!!��Y����
				int li_parEndDay = getEndDayByCode(str_parents[k]); //��������,��X����!!
				if (li_level == li_parLevel && li_beginDay == li_parEndDay) { //���������׵Ľ��������ҵ���ʼ�������ص�,˵���ø��׾����ҵ��׵�!������һ��,����Ҫ���κδ���!!!
					continue;
				}

				String str_ring = findRing(li_parLevel, li_parEndDay, ringMap); //�жϸø��׽�����λ���Ƿ���ԲȦ?
				if (str_ring == null) { //���������λ����ԲȦ,��˵���Ӹ�ԲȦ���ҵ���ʼԲȦ��λ��Ҫ������!!!
					//��������!!
					drawWaveLine(g2, str_parents[k], li_parEndDay, li_parLevel, li_beginDay, li_level, ringMap, lineWayMap, lineAreaHst, false); //
				} else { //���������λ��û��ԲȦ,��˵���ø��׾�һ������,���Ҿ�����!ֻ��û��ֱ������!!������ɵ�û���׶��ӣ�ֻ������һ���ɶ���!!
					if (!isMyRealBack(str_parents[k], (HashMap) ringMap.get(str_ring))) { //�������λ����ԲȦ,��ȴ�������ҵ���������,��˵����������Ǵ����,��Ҫ
						drawWaveLine(g2, str_parents[k], li_parEndDay, li_parLevel, li_beginDay, li_level, ringMap, lineWayMap, lineAreaHst, true); //
					}
				}
			}
		}

		//�ٴ�ѭ����ȫ����!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			String str_fronts = hvs[i].getStringValue("fronts", ""); //��ǰ����!
			if (str_fronts == null || str_fronts.trim().equals("")) {
				continue;
			}
			String[] str_parents = tbUtil.split(str_fronts, ","); //
			if (str_parents.length <= 1) { //���ֻ��һ������Ҳ������!
				continue;
			}

			int li_beginDay = findBeginDay(str_code); //Ѱ�ҳ�ĳ�����ڵ���ʼ��!��΢�е��Ѷȵ��㷨!��Ҫ�ҳ����и�����������Ǹ���
			int li_level = (Integer) actLevelMap.get(str_code); //
			for (int k = 0; k < str_parents.length; k++) { //����������!
				int li_parLevel = (Integer) actLevelMap.get(str_parents[k]); //�ڼ���!!��Y����
				int li_parEndDay = getEndDayByCode(str_parents[k]); //��������,��X����!!
				if (li_level == li_parLevel && li_beginDay == li_parEndDay) { //���������׵Ľ��������ҵ���ʼ�������ص�,˵���ø��׾����ҵ��׵�!������һ��,����Ҫ���κδ���!!!
					continue;
				}
				String str_ring = findRing(li_parLevel, li_parEndDay, ringMap); //�жϸø��׽�����λ���Ƿ���ԲȦ?
				if (str_ring != null) { //���������λ����ԲȦ,��˵���Ӹ�ԲȦ���ҵ���ʼԲȦ��λ��Ҫ������!!!
					if (isMyRealBack(str_parents[k], (HashMap) ringMap.get(str_ring))) { //�������λ����ԲȦ,�������ԲȦ��ȷ�����ĺ�������,��˵����Ҫ�����ߵ�!
						drawBrokenLine(g2, str_parents[k], li_parEndDay, li_parLevel, li_beginDay, li_level, ringMap, lineWayMap, lineAreaHst); //
					}
				}
			}
		}

		//�����β���
		int li_lastWorkLevel = (Integer) actLevelMap.get(str_lastWorkCode);
		int li_lastEndDay = getEndDayByCode(str_lastWorkCode); //
		drawActivity(g2, "" + li_index, li_lastEndDay, li_lastWorkLevel); //������β��ԲȦ!
		//����������β��,Ҫָ������!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			String str_backs = hvs[i].getStringValue("backs", ""); //������!
			if (!str_code.equals(str_lastWorkCode) && (str_backs == null || str_backs.trim().equals(""))) { //������β
				int li_lastWorkLevel2 = (Integer) actLevelMap.get(str_code);
				int li_lastEndDay2 = getEndDayByCode(str_code); //
				//��������
				drawWaveLine(g2, str_code, li_lastEndDay2, li_lastWorkLevel2, li_lastEndDay, li_lastWorkLevel, ringMap, lineWayMap, lineAreaHst, false); //
			}
		}

		//System.out.println(lineAreaHst);  //
	}

	private long getDateTime(String _date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long ll_cd = sdf.parse(_date).getTime(); //
			return ll_cd;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return System.currentTimeMillis(); //
		}

	}

	//�ж��Ƿ����ҵ������ĺ�������!!!
	private boolean isMyRealBack(String _code, HashMap _ringItemMap) {
		String str_actCode = (String) _ringItemMap.get("code"); //����!!�������Ӧ�����ҵ�backs������!
		if (str_actCode != null) { //�϶�Ӧ���е�!
			HashVO hvo = getHashVOByCode(_code); //
			String str_backs = hvo.getStringValue("backs"); //
			if (str_backs != null) {
				String[] str_items = tbUtil.split(str_backs, ","); //
				return tbUtil.isExistInArray(str_actCode, str_items); //���ָ����Ƿ����ҵ�������������?�п��������������ϵ�!
			}
		}
		return false;
	}

	//��ĳһ��λ��Ѱ���Ƿ���ԲȦ!
	private String findRing(int _level, int _day, HashMap _ringMap) {
		String[] str_keys = (String[]) _ringMap.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) { //����!!!
			HashMap itemMap = (HashMap) _ringMap.get(str_keys[i]); //
			int li_day = (Integer) itemMap.get("day");
			int li_level = (Integer) itemMap.get("level");
			if (li_level == _level && li_day == _day) { //�������������ö�һ��,��˵����λ������ԲȦ��!!
				return str_keys[i]; //
			}
		}
		return null;
	}

	//����һ��λ��Ѱ���Ƿ��Ѿ�����ĳ������
	private String findDrawedWorkByPos(int _level, int _day, HashMap _actLevelMap) { //
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (findBeginDay(str_code) == _day) {
				if (_actLevelMap.containsKey(str_code)) { //����ѻ�!!
					if (_level == (Integer) _actLevelMap.get(str_code)) {
						return str_code; //
					}
				}
			}
		}
		return null; //
	}

	//�ҵ�ĳ�������Ľ�ǰ�����Ĳ�!
	private String[] findParentLevel(String _thisCode, String _fronts, int _thisBeginDay, HashMap _actLevelMap) {
		if (_fronts == null || _fronts.equals("")) { //�����ǰ����Ϊ��,��˵���϶��ǵ�һ��!!
			return new String[] { null, "1" }; //
		}
		String[] str_items = tbUtil.split(_fronts, ","); //�����ж����ǰ����!
		int li_endDay = 0; //��¼���Ľ��!
		String str_nearParent = null; //�洢���׽��ĸ���!!
		for (int i = 0; i < str_items.length; i++) { //����������ǰ����
			HashVO hvo = getHashVOByCode(str_items[i]); //��øù�����HashVO
			int li_beginDay = findBeginDay(hvo.getStringValue("code")); //
			int li_days = hvo.getIntegerValue("days"); //����ʱ��!!
			if ((li_beginDay + li_days) > li_endDay) {
				li_endDay = li_beginDay + li_days; //����������!!!
				str_nearParent = hvo.getStringValue("code"); //��¼���׽��ĸ���!!!
			}
		}

		if ((_thisBeginDay - li_endDay) != 0) {
			System.err.println("[" + _thisCode + "]��������ĸ���[" + str_nearParent + "]��Ȼ������һһ��[" + ((_thisBeginDay - li_endDay)) + "]?");
		} else {
			//System.out.println("[" + _thisCode + "]��������ĸ���[" + str_nearParent + "]����һ��"); //
		}

		int li_level = (Integer) _actLevelMap.get(str_nearParent); //
		return new String[] { str_nearParent, "" + li_level }; //
	}

	//�����ĳһ������������һ��
	private int getEndDayByCode(String _code) {
		HashVO hvo = getHashVOByCode(_code); //
		int li_beginDay = findBeginDay(_code); //Ѱ�ҳ�ĳ�����ڵ���ʼ��!��΢�е��Ѷȵ��㷨!��Ҫ�ҳ����и�����������Ǹ���
		return li_beginDay + hvo.getIntegerValue("days"); //
	}

	private HashVO getHashVOByCode(String _code) {
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			if (str_code.equalsIgnoreCase(_code)) {
				return hvs[i]; //
			}
		}
		return null; //
	}

	private int findLevel(String _thisCode, HashSet hst, int _beginDay, int _endDay, int _level, HashMap _actLevelMap) {
		boolean isFind = false; //
		for (int j = _beginDay; j < _endDay; j++) { //
			if (hst.contains(_level + "-" + j)) { //���������һ�����Ѿ����ˣ�����������!
				isFind = true; //
				break; //
			}
		}

		if (isFind) { //���������,���������һ���ң�
			return findLevel(_thisCode, hst, _beginDay, _endDay, _level + 1, _actLevelMap);
		} else { //���û���֣��򷵻���һ�㣬˵����һ�����е�
			//��Ŀ�괦��û���Ѿ���Ȧ?����һ�����������ĳ�����Ľ�β���Ѿ����û�����һ������!������������ֲ����ҵĺ��������������ʱ�һ�������ͻ����һ����ֵĴ�����о��������ǽ���������������Һ�����������!
			//��ʵ���Ǵ����!!!���Ա���Ҫ������ж�!!!
			String str_endsWorkCode = findDrawedWorkByPos(_level, _endDay, _actLevelMap);
			if (str_endsWorkCode == null) { //�����βĿ�괦û�л�����,��˵������
				return _level; //
			} else { //����ѻ���
				//System.out.println("�ѻ�����[" + str_endsWorkCode + "]��,û�ط���...");
				HashVO hvo = getHashVOByCode(_thisCode); //
				String str_backs = hvo.getStringValue("backs"); //�ҵĺ�������!
				if (str_backs != null) {
					String[] str_items = tbUtil.split(str_backs, ","); //
					if (tbUtil.isExistInArray(str_endsWorkCode, str_items)) { //����������������ҵ�ĳ����������,���ǿ��Ե�!
						return _level; //
					} else {
						return findLevel(_thisCode, hst, _beginDay, _endDay, _level + 1, _actLevelMap);
					}
				} else {
					return findLevel(_thisCode, hst, _beginDay, _endDay, _level + 1, _actLevelMap);
				}
			}
		}
	}

	/**
	 * ������Ŀ������,�Լ�����������ʼ��!������Ҫ���߼�!!
	 */
	private void computeBeginDay() {
		//����������Զ��������ǰ����!!���Ҹо���ʵֻ��Ҫ�������������!Ȼ���ǰ�����ܹ��Զ�����ó�����!!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			StringBuffer sb_fronts = new StringBuffer(); //
			for (int j = 0; j < i; j++) { //
				String str_code2 = hvs[j].getStringValue("code"); //��������!!!
				String str_backs = hvs[j].getStringValue("backs"); //��������!!!
				if (str_backs != null && !str_backs.equals("")) { //���������
					String[] str_items = tbUtil.split(str_backs, ","); ////
					if (tbUtil.isExistInArray(str_code, str_items)) { //���������������
						sb_fronts.append(str_code2 + ","); //����
					}
				}
			}
			String str_fronts = sb_fronts.toString(); //
			if (str_fronts.endsWith(",")) { //�����ֵ!
				str_fronts = str_fronts.substring(0, str_fronts.length() - 1); //�ص�!
				//System.out.println("����[" + str_code + "]��������Ľ�ǰ������[" + str_fronts + "]"); //
				hvs[i].setAttributeValue("fronts", str_fronts); //����!!
			} else {
				if (i != 0) {
					sb_exception.append("������" + str_code + "�����ǿ�ʼ����,ͬʱ�ֲ�������ĳ�������Ľ����������ǲ������!\r\n"); //
					return; //
				}
			}
		}

		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			String str_fronts = hvs[i].getStringValue("fronts"); //��ǰ����!

			if (str_fronts != null && !str_fronts.trim().equals("")) { //����н�ǰ����
				String[] str_items = tbUtil.split(str_fronts, ","); //
				for (int j = 0; j < str_items.length; j++) {
					if (!isInBefore(str_items[j], i)) { //
						sb_exception.append("������" + str_code + "������һ����ǰ������" + str_items[j] + "����ǰ��û�ж���,�����¶��������!\r\n"); //
						return; //
					}

					if (!isFrontInHerBacks(str_code, str_items[j], i)) {
						sb_exception.append("������" + str_code + "���еĽ�ǰ������" + str_items[j] + "���ж���Ľ�����û�б�����,�����!\r\n"); //
						return; //
					}
				}
			}

			String str_backs = hvs[i].getStringValue("backs"); //������!
			if (str_backs != null && !str_backs.equals("")) { //���������
				String[] str_items = tbUtil.split(str_backs, ","); ////
				for (int j = 0; j < str_items.length; j++) {
					if (!isInBack(str_items[j], i)) { ///
						sb_exception.append("������" + str_code + "������һ����������" + str_items[j] + "���ں���û�ж���,�����¶��������!\r\n"); //
						return; //
					}

					if (!isBackInHerFronts(str_code, str_items[j], i)) {
						sb_exception.append("������" + str_code + "���еĽ�ǰ������" + str_items[j] + "���ж���Ľ�����û�б�����,�����!\r\n"); //
						return; //
					}
				}
			}

			if ((str_fronts == null || str_fronts.trim().equals("")) && (str_backs == null || str_backs.trim().equals(""))) { //�����ǰ����Ϊ��,��Ҳ����!
				sb_exception.append("������" + str_code + "���Ľ�ǰ�������������Ϊ��,���ǲ������,�����¶���!\r\n"); //
				return; //
			}

			String str_days = hvs[i].getStringValue("days"); //����
			if (str_days == null || str_days.trim().equals("")) {
				sb_exception.append("������" + str_code + "���г�������Ϊ��,�붨�壡\r\n"); //
				return; //
			} else {
				int li_days = Integer.parseInt(str_days); //����
				int li_beginDay = computeBeginDay(str_code); //
				beginDayMap.put(str_code, new Integer(li_beginDay)); //���뻺��!!!
				if (li_beginDay + li_days >= li_Alldays) { //��¼������������ʱ����!!!
					str_lastWorkCode = str_code; //��¼���һ������!!
					li_Alldays = li_beginDay + li_days;
				}
			}
		}

		//У�������֮���Ƿ��ִ����Ⱥ��ӹ�ϵ?
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //��������!!!
			String str_backs = hvs[i].getStringValue("backs"); //������!
			if (str_backs != null) {
				String[] str_items = tbUtil.split(str_backs, ","); //
				if (str_items.length > 1) { //����ж��������!
					for (int p = 0; p < str_items.length; p++) {
						for (int t = 0; t < str_items.length; t++) {
							if (p != t) {
								if (isBackAndFront(str_items[p], str_items[t])) {
									sb_exception.append("������" + str_code + "����������������" + str_items[p] + "���롾" + str_items[t] + "��֮�䱾�������Ⱥ��ϵ,���ǲ����ܵ�,��ɾ������һ��!\r\n"); //
									return; //
								}
							}
						}
					}
				}
			}
		}

		System.out.println("������������[" + li_Alldays + "],���һ�����[" + str_lastWorkCode + "]"); //
	}

	//�ж���������֮���Ƿ����Ⱥ��ϵ??
	private boolean isBackAndFront(String _code1, String _code2) {
		ArrayList leafList = new ArrayList(); //��¼����Ҷ�ӽ����б�
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(_code1); //
		recursionAdd(_code1, rootNode, leafList); //�ݹ���������·��!!!

		//System.out.println("����[" + _code1 + "]������·������:"); //
		//�������ֿ��ܴ��ڵ�·��
		int li_maxBeginDays = 1; //
		for (int i = 0; i < leafList.size(); i++) { //��������Ҷ��,�����ܴ��ڵĸ���·��!!
			StringBuffer sb_text = new StringBuffer(); //
			DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) leafList.get(i); //ĳ�����!
			TreeNode[] allNodes = itemNode.getPath(); //
			for (int j = 0; j < allNodes.length; j++) { //�������е�!
				String str_itemCode = ((DefaultMutableTreeNode) allNodes[j]).getUserObject().toString(); //
				if (str_itemCode.equals(_code2)) {
					return true; //
				}
				//sb_text.append(str_itemCode + "->"); //
			}
			//System.out.println("��[" + (i + 1) + "]��·��=" + sb_text.toString());
		}
		//System.out.println(); //
		return false;
	}

	private boolean isInBefore(String _code, int _index) {
		for (int i = 0; i < _index; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_code)) { //�����,��ok
				return true; //
			}
		}
		return false;
	}

	private boolean isFrontInHerBacks(String _thisCode, String _front, int _index) {
		for (int i = 0; i < _index; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_front)) { //����иý�ǰ����,��ok
				String str_backs = hvs[i].getStringValue("backs"); //
				if (str_backs != null && !str_backs.equals("")) {
					String[] str_items = tbUtil.split(str_backs, ","); //
					if (tbUtil.isExistInArray(_thisCode, str_items)) { //�����������Ľ�����������,��˵���ǶԵ�!
						return true; //
					}
				}
			}
		}
		return false;
	}

	private boolean isInBack(String _code, int _index) {
		for (int i = _index + 1; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_code)) { //�����,��ok
				return true; //
			}
		}
		return false;
	}

	private boolean isBackInHerFronts(String _thisCode, String _back, int _index) {
		for (int i = _index + 1; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_back)) { //����иý�����,��ok
				String str_fronts = hvs[i].getStringValue("fronts"); //�ҳ��ù����Ľ�ǰ����
				if (str_fronts != null && !str_fronts.equals("")) {
					String[] str_items = tbUtil.split(str_fronts, ","); //
					if (tbUtil.isExistInArray(_thisCode, str_items)) { //�����������Ľ�ǰ����������,��˵���ǶԵ�!
						return true; //
					}
				}
			}
		}
		return false;
	}

	/**
	 * �����ĳ����������ʼ��,һ���ǳ��ؼ��븴�ӵļ����߼�
	 * ����ԭ���Ǽ�����ù������п��ܴ��еĸ�������Ȼ�����к�ʱ���������·�����ҵ���ʼ��!
	 * ������������Ľ�����ܱ�֤��������Զ�Ǵ����ң������ܴ��ҵ���! �����յ��ڡ���Զ������ģ������ܷ���ģ���Ϊʱ�䲻�ɵ���!!
	 * ��Ҫ�ݹ��㷨!!!
	 */
	private int computeBeginDay(String _code) {
		ArrayList leafList = new ArrayList(); //��¼����Ҷ�ӽ����б�
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(_code); //
		recursionAdd(_code, rootNode, leafList); //�ݹ���������·��!!!

		//System.out.println("����[" + _code + "]������·������:"); //
		//�������ֿ��ܴ��ڵ�·��
		int li_maxBeginDays = 1; //
		for (int i = 0; i < leafList.size(); i++) { //��������Ҷ��,�����ܴ��ڵĸ���·��!!
			StringBuffer sb_text = new StringBuffer(); //
			DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) leafList.get(i); //ĳ�����!
			TreeNode[] allNodes = itemNode.getPath(); //
			int li_days = 1; //
			for (int j = 0; j < allNodes.length; j++) { //�������е�!
				String str_itemCode = ((DefaultMutableTreeNode) allNodes[j]).getUserObject().toString(); //
				int li_itemDays = getHashVOByCode(str_itemCode).getIntegerValue("days"); //
				sb_text.append(str_itemCode + "(" + li_itemDays + ")->"); //
				if (j != 0) { //�Լ�����������,ֻ����ǰ���!
					li_days = li_days + li_itemDays; //�ۼӳ�������!!
				}
			}
			//System.out.println("��[" + (i + 1) + "]��·��=" + sb_text.toString());
			if (li_days > li_maxBeginDays) { //
				li_maxBeginDays = li_days; //ֻ��¼����!!
			}
		}
		//System.out.println("����[" + _code + "]����ʼ����[" + li_maxBeginDays + "]!"); //
		//System.out.println(); //

		return li_maxBeginDays;
	}

	private int findBeginDay(String _code) {
		return (Integer) beginDayMap.get(_code); //
	}

	//�ݹ��㷨!!
	private void recursionAdd(String _code, DefaultMutableTreeNode _node, ArrayList _list) {
		HashVO hvo = getHashVOByCode(_code); //�ҵ�������¼!
		String str_fronts = hvo.getStringValue("fronts"); //ǰ�ý��!
		if (str_fronts == null || str_fronts.trim().equals("")) { //���Ϊ���򲻼�������!
			_list.add(_node); //
			return; //
		}

		String[] str_items = tbUtil.split(str_fronts, ","); //���м�������
		for (int i = 0; i < str_items.length; i++) { //�����������!
			//����ҵĸ��������Ѵ�����,���ܼ��룬��Ϊ��������ѭ��!
			boolean isHave = ifHaveThisInMyPath(_node, str_items[i]); //
			if (isHave) { //����Ѵ���,������,����Ҳ����������
				continue; //
			}
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(str_items[i]); //
			_node.add(childNode); //һ��Ҫ����
			recursionAdd(str_items[i], childNode, _list); //���������Լ�!!
		}
	}

	private boolean ifHaveThisInMyPath(DefaultMutableTreeNode _node, String _code) {
		return false;
	}

	//��һ�����ڣ���һ��Բ��!!
	private void drawActivity(Graphics2D g2, String _text, int _day, int _level) {
		int li_x = getXposByDay(_day, 2); //
		int li_y = getYposByLevel(_level, 2); //
		g2.setColor(Color.WHITE); //
		g2.fillOval(li_x - 15, li_y - 15, 30, 30); //
		g2.setColor(Color.BLACK); //
		g2.drawOval(li_x - 15, li_y - 15, 30, 30);
		g2.drawString(_text, li_x - (_text.length() * 3), li_y + 5); //д��
	}

	//����һ��������!!!
	private void addLineWayMark(HashMap _lineWayMap, String _key, String _mark) {
		if (!_lineWayMap.containsKey(_key)) { //
			_lineWayMap.put(_key, _mark + ";"); //
		} else {
			String str_value = (String) _lineWayMap.get(_key); //
			_lineWayMap.put(_key, str_value + _mark + ";"); //
		}
	}

	private void addLineAreaMark(HashSet _lineAreaSet, int _level, String _dir, int _movedigit, int _beginday, int _endDay) {
		//�ȼ�¼�ò�ġ���ʼ�족-�������족�ѱ�ռ��!!
		for (int i = _beginday; i < _endDay; i++) { //����!
			if (_movedigit > 0) { //
				_lineAreaSet.add(_level + (_dir == null ? "" : _dir) + "." + _movedigit + "-" + i); //
			} else {
				_lineAreaSet.add(_level + (_dir == null ? "" : _dir) + "-" + i); //
			}
		}
	}

	//��ĳ��λ���Ƿ����ĳ�ַ������???�⽫�����Ƿ���Ҫ��!!!
	private boolean isHaveWay(HashMap _lineWayMap, String _key, String _mark) {
		if (_lineWayMap.containsKey(_key)) {
			String str_value = (String) _lineWayMap.get(_key); //
			String[] str_items = tbUtil.split(_mark, ";"); //
			for (int i = 0; i < str_items.length; i++) { //���������ĳһ��,������!
				if (str_value.indexOf(str_items[i] + ";") >= 0) {
					return true; //
				}
			}
		}

		return false; //
	}

	/**
	 * ��һ����,���ĸ��㿪ʼ���ĸ������!!
	 * Ҫ���жϵó��ǲ�����ͬһ�㣡�������ͬһ�㣬�����Ҫ�����ߣ���
	 * 
	 * @param g2
	 * @param _text
	 * @param _day
	 * @param _level
	 */
	private void drawLine(Graphics2D g2, String _text, int _beginDay, int _endDay, int _level, HashMap _lineWayMap, HashSet _lineAreaSet) { //
		int li_x1 = getXposByDay(_beginDay, 3); //
		int li_x2 = getXposByDay(_endDay, 1); //
		int li_y = getYposByLevel(_level, 2); //

		g2.setColor(Color.BLACK); //
		g2.drawLine(li_x1, li_y, li_x2, li_y); //����!!

		//����ͷ!!
		g2.fillPolygon(new int[] { li_x2 - 5, li_x2, li_x2 - 5 }, new int[] { li_y - 5, li_y, li_y + 5 }, 3); //����ͷ!!

		//��������������!
		int li_text_x = li_x1 + (li_x2 - li_x1) / 2 - 6; //
		int li_text_y = li_y - 5; //
		g2.setColor(Color.WHITE);
		g2.fillRect(li_text_x, li_text_y - 12, 30, 13); //

		g2.setColor(Color.BLACK);
		g2.drawString(_text, li_text_x, li_text_y); //
		g2.drawString("" + (_endDay - _beginDay), li_text_x, li_y + 17); //����������

		//��¼����!!
		addLineWayMark(_lineWayMap, "" + _level + "-" + _beginDay, "��"); //
		addLineAreaMark(_lineAreaSet, _level, null, 0, _beginDay, _endDay); //
	}

	//������!!
	private void drawLine2(Graphics2D g2, String _code, int _fromDay, int _fromLevel, int _endDay, int _level, HashMap _ringMap, HashMap _lineWayMap, HashSet _lineAreaSet) {
		int li_x1 = getXposByDay(_fromDay, 2); //
		int li_x2 = getXposByDay(_endDay, 1); //
		int li_y1 = getYposByLevel(_fromLevel, 3); //
		int li_y2 = getYposByLevel(_level, 2); //

		g2.setColor(Color.BLACK); //

		//�����������û��ԲȦ?
		String[] str_rings = isDrillOneRing(_fromLevel, _level, _fromDay, _ringMap, false); //
		if (str_rings == null) {
			g2.drawLine(li_x1, li_y1, li_x1, li_y2); //������!!
		} else { //�����ԲȦ��ܿ�֮!
			//��������
			g2.drawLine(li_x1, li_y1, li_x1 + 26, li_y1 + 20); //������!!
			g2.drawLine(li_x1 + 26, li_y1 + 20, li_x1 + 26, li_y2 - 20); //������!!
			g2.drawLine(li_x1, li_y2, li_x1 + 26, li_y2 - 20); //������!!
		}

		g2.drawLine(li_x1, li_y2, li_x2, li_y2); //������!!

		drawArrow(g2, li_x2, li_y2, "����"); //�����ҵļ�ͷ!

		//��������������!
		int li_text_x = li_x1 + (li_x2 - li_x1) / 2 - 6; //
		int li_text_y = li_y2 - 5; //

		g2.setColor(Color.BLACK);
		g2.drawString(_code, li_text_x, li_text_y); //
		g2.drawString("" + (_endDay - _fromDay), li_text_x, li_y2 + 17); //����������

		//��¼����!!!
		addLineWayMark(_lineWayMap, "" + _fromLevel + "-" + _fromDay, "��"); //
		addLineWayMark(_lineWayMap, "" + _level + "-" + _endDay, "��"); //
		addLineAreaMark(_lineAreaSet, _level, null, 0, _fromDay, _endDay); //
	}

	/**
	 * ������,������������֮�������ӣ���������һ��ʵ�ʹ���!!
	 * 1.������Ҳ�������ѵ�,һ���������������һ��,��Ҫ����,�����ߵĺ������ص�,Ҫ��!
	 * 2.��������ʱ
	 */
	private void drawBrokenLine(Graphics2D g2, String _code, int _beginDay, int _beginLevel, int _endDay, int _endLevel, HashMap _ringMap, HashMap _lineWayMap, HashSet _lineAreaSet) {
		g2.setColor(brokeColor); //
		//g2.setStroke(new BasicStroke(1.5f)); //

		int li_x1 = getXposByDay(_beginDay, 2); //
		int li_x2 = getXposByDay(_endDay, 2); //
		int li_y1 = getYposByLevel(_beginLevel, _endLevel > _beginLevel ? 3 : 1); //
		int li_y2 = getYposByLevel(_endLevel, _endLevel > _beginLevel ? 1 : 3); //

		if (_beginDay == _endDay) { //���X������ͬ,����ͬһ��,��һ������ֱ��!!�м��п��ܴ���ԲȦô?
			//System.out.println("��ֱ��!!" + _endLevel + "-" + _endDay); //
			if (_endLevel > _beginLevel) { //���������������
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��") || isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "��")) { //�����ʼ�����������
					li_x1 = li_x1 - 10; //��
					li_x2 = li_x2 - 10; //��
				}
			} else {
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��") || isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "��")) { //�����ʼ�����������
					li_x1 = li_x1 + 10; //��
					li_x2 = li_x2 + 10; //��
				}
			}

			String[] str_rings = isDrillOneRing(_beginLevel, _endLevel, _beginDay, _ringMap, false); //����û�д�������ԲȦ?
			if (str_rings != null) { //�����������ԲȦ,��Ҫ����߱ܿ�!!Ҫ���ĸ���
				if (_endLevel > _beginLevel) {
					g2.drawLine(li_x1, li_y1, getXposByDay(_beginDay, 2) - 27, getYposByLevel(_beginLevel, 2) + 20); //
					drawBrokeLineV(g2, getXposByDay(_beginDay, 2) - 27, getYposByLevel(_beginLevel, 2) + 20, li_y2 - 20); //����������!!
					drawBrokeLineH(g2, li_y2 - 20, getXposByDay(_beginDay, 2) - 27, li_x1); //
					drawBrokeLineV(g2, li_x1, li_y2 - 20, li_y2); //����������!!
				} else {
					g2.drawLine(li_x1, li_y1, getXposByDay(_beginDay, 2) + 27, getYposByLevel(_beginLevel, 2) - 20); //
					drawBrokeLineV(g2, getXposByDay(_beginDay, 2) + 27, getYposByLevel(_beginLevel, 2) - 20, li_y2 + 20); //����������!!
					drawBrokeLineH(g2, li_y2 + 20, getXposByDay(_beginDay, 2) + 27, li_x1); //
					drawBrokeLineV(g2, li_x1, li_y2, li_y2 + 20); //����������!!
				}
			} else {
				drawBrokeLineV(g2, li_x1, li_y1, li_y2); //����������!!
			}

			if (li_y2 > li_y1) {
				drawArrow(g2, li_x2, li_y2, "����"); //�����µļ�ͷ!
			} else {
				drawArrow(g2, li_x2, li_y2, "����"); //�����ϵļ�ͷ!
			}

			//��¼����!!...
			if (_endLevel > _beginLevel) {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
			} else {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
			}
		} else if (_beginLevel == _endLevel) { //���Y���!,��һ������!��������˵��Ӧ�ô��ں���������ʵ���ص������!����������֮�䲻Ӧ���ټ��к�����ϵ!!
			int li_y = getYposByLevel(_beginLevel, 2); //
			String[] str_rings = isDrillOneRing2(_beginLevel, _beginDay, _endDay, _ringMap, false); //����û�д���һЩԲȦ?
			if (str_rings == null) {
				drawBrokeLineH(g2, li_y, li_x1 + 15, li_x2 - 15); //
				drawArrow(g2, li_x2 - 15, li_y, "����"); //�����ҵļ�ͷ!
			} else {
				drawBrokeLineV(g2, li_x1 + 13, li_y - 8, li_y - 30); //
				drawBrokeLineH(g2, li_y - 30, li_x1 + 15, li_x2 - 15); //
				drawBrokeLineV(g2, li_x2 - 15, li_y - 30, li_y - 15); //
				drawArrow(g2, li_x2 - 15, li_y - 10, "����"); //�����ҵļ�ͷ!
			}

			//��¼����!!
			addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��"); //
			addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //����Ǵ������ƹ�����,����Ȼ��ͷ������,�������Ȼ����,����Ϊ��Ϊ�Ժ�ʵ�ʴ��Ϸ����������ռ�!!!����һ�����⵫��������!!
		} else { //����м��п��,��Ҫ��������!!!
			//System.out.println("��������!!" + _endLevel + "-" + _endDay + "&" + _lineWayMap.get("" + _endLevel + "-" + _endDay)); //
			int li_centerY = 0; //
			if (_endLevel > _beginLevel) { //�����������Yλ�ñ�ǰ�����Yλ�ô�!
				li_centerY = getYposByLevel(_beginLevel, 2) + 60; //��ǰ������������2,������������������ص�!!���Ի�����ֱ������ʼλ��Ϊ��׼ֱ���ư���㣬�����϶���֤��ƽ���ڲ���м���!
			} else {
				li_centerY = getYposByLevel(_beginLevel, 2) - 60; //
			}

			int li_moveDigit = isAlreadyDrawLine(_lineAreaSet, (_endLevel > _beginLevel ? _beginLevel : _endLevel), "��", _beginDay, _endDay); //��λ!
			if (li_moveDigit > 0) { //������1-6��1������,2������,3����������!
				if (li_moveDigit % 2 == 0) { //�����ż��!��������
					li_centerY = li_centerY - (li_moveDigit / 2) * 20; //����ƫ��һ��
				} else { //���������,��������!
					li_centerY = li_centerY + ((li_moveDigit / 2) + 1) * 20; //����ƫ��һ��!
				}
			}

			if (_endLevel > _beginLevel) { //���������������
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��")) { //�����ʼ�����������
					li_x1 = li_x1 + 10; //
				}
				if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "��")) { //�����ʼ�����������
					li_x2 = li_x2 - 10; //
				}
			} else {
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��")) { //�����ʼ�����������
					li_x1 = li_x1 + 10; //
				}
				if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, " ��")) { //�����ʼ�����������
					li_x2 = li_x2 - 10; //
				}
			}

			//Ҫ��������!
			drawBrokeLineV(g2, li_x1, li_y1, li_centerY); //����
			drawWaveLineH(g2, li_centerY, li_x1, li_x2); //Ҫ��������!!��ʾ��Ҫ�ȴ���ʱ��!!

			//���ǵ����ܻᴩ��ԲȦ,������Ҫ���ܿ�����!
			String[] str_rings = isDrillOneRing(_beginLevel, _endLevel, _endDay, _ringMap, false); //����û�д�������ԲȦ?
			if (str_rings != null) { //�����������ԲȦ,��Ҫ����߱ܿ�!!Ҫ���ĸ���
				if (_endLevel > _beginLevel) {
					drawBrokeLineV(g2, li_x2, li_centerY, li_centerY + 20); //
					drawBrokeLineH(g2, li_centerY + 20, li_x2 - 27, li_x2); //
					drawBrokeLineV(g2, li_x2 - 27, li_centerY + 20, li_y2 - 20); //����������!!
					drawBrokeLineH(g2, li_y2 - 20, li_x2 - 27, li_x2); //
					drawBrokeLineV(g2, li_x2, li_y2 - 20, li_y2); //����������!!
				} else {
					drawBrokeLineH(g2, li_centerY, li_x2, li_x2 + 27); //
					drawBrokeLineV(g2, li_x2 + 27, li_centerY, li_y2 + 20); //����������!!
					drawBrokeLineH(g2, li_y2 + 20, li_x2 + 27, li_x2); //
					drawBrokeLineV(g2, li_x2, li_y2 + 20, li_y2); //����������!!
				}
			} else {
				drawBrokeLineV(g2, li_x2, li_centerY, li_y2); //����
			}

			if (li_y2 > li_y1) { //
				drawArrow(g2, li_x2, li_y2, "����"); //�����µļ�ͷ!
			} else {
				drawArrow(g2, li_x2, li_y2, "����"); //�����µļ�ͷ!
			}

			//��¼����!!...
			if (_endLevel > _beginLevel) {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
			} else {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "��"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
			}

			addLineAreaMark(_lineAreaSet, (_endLevel > _beginLevel ? _beginLevel : _endLevel), "��", li_moveDigit, _beginDay, _endDay); //��¼ƫ��
		}
	}

	//��һ����������
	private void drawBrokeLineH(Graphics2D _g2, int _y, int _x1, int _x2) {
		if (_x1 > _x2) { //���y1��y2��,�򽻻�һ��λ��!
			int tmpX = _x1; //
			_x1 = _x2; //
			_x2 = tmpX; //
		}
		int li_item = 4; //�����е�ÿ�ε�����
		int li_space = 6; //������ÿ�ε�����
		int li_yCount = 0; //
		while (1 == 1) { //
			int li_newX = _x1 + li_yCount * (li_item + li_space); //
			if (li_newX >= _x2) { //���Խ�������˳�ѭ��!!!
				break; //
			}

			int li_endX = li_newX + li_item; //
			if (li_endX > _x2) { //���Խ�磬��ֱ���ڱ߽�ߴ�!!
				li_endX = _x2; //
			}

			_g2.drawLine(li_newX, _y, li_endX, _y); //����!!
			li_yCount++; //
		}
	}

	//��һ����������
	private void drawBrokeLineV(Graphics2D _g2, int _x, int _y1, int _y2) {
		if (_y1 > _y2) { //���y1��y2��,�򽻻�һ��λ��!
			int tmp_y = _y1; //
			_y1 = _y2; //
			_y2 = tmp_y; //
		}
		int li_item = 4; //�����е�ÿ�ε�����
		int li_space = 6; //������ÿ�ε�����
		int li_yCount = 0; //
		while (1 == 1) { //
			int li_newY = _y1 + li_yCount * (li_item + li_space); //
			if (li_newY >= _y2) { //���Խ�������˳�ѭ��!!!
				break; //
			}

			int li_endY = li_newY + li_item; //
			if (li_endY > _y2) { //���Խ�磬��ֱ���ڱ߽�ߴ�!!
				li_endY = _y2; //
			}

			_g2.drawLine(_x, li_newY, _x, li_endY); //����!!
			li_yCount++; //
		}
	}

	/**
	 * ��������!!�����ߵĺ���������ֻ��һ��ֱ��,���������һ������ߣ�Ȼ�����ϻ�ֱ��!
	 * ���鷳����������:
	 * 1.һ�Ǻ�������Ѿ��������ˣ���ʱҪ����ƫ��
	 * 2.��������ʱ��������ԲȦ������Ҫ�Զ�����!
	 */
	private void drawWaveLine(Graphics2D g2, String _code, int _beginDay, int _beginLevel, int _endDay, int _endLevel, HashMap _ringMap, HashMap _lineWayMap, HashSet _lineAreaSet, boolean _isDealErr) {
		g2.setStroke(new BasicStroke(1)); //
		String str_betweenDay = "TF=" + (_endDay - _beginDay); //�������!

		int li_x1 = getXposByDay(_beginDay, 1); //
		int li_x2 = getXposByDay(_endDay, _beginLevel == _endLevel ? 1 : 2); //

		int li_y1 = getYposByLevel(_beginLevel, 2); //
		int li_y2 = getYposByLevel(_endLevel, 2); //

		//���ϲ�ȫԭ����ȫ����!
		if (_isDealErr) { //������޲�����
			g2.setColor(Color.WHITE); //
			g2.fillRect(li_x1 - 15, li_y1 - 5, 15, 10); //���ԭ����ͷ!!����������һЩ!��Ϊ�������ϻ��߾������λ��!
		} else { //��������޲����������£���Ҫ��ȫ!������޲�����˵���������һ��ԲȦ���϶������ٻ��ˣ�
			g2.setColor(Color.WHITE); //
			g2.fillRect(li_x1 - 5, li_y1 - 5, 5, 10); //���ԭ����ͷ!!

			g2.setColor(Color.BLACK); //
			g2.drawLine(li_x1 - 5, li_y1, li_x1 + 15, li_y1); //��ȫԭ��ȱʧ�İ��ԲȦ����
		}

		g2.setColor(Color.BLACK); //

		int li_moveDigit = isAlreadyDrawLine(_lineAreaSet, _beginLevel, null, _beginDay, _endDay); //ƫ�Ƽ�λ
		if (li_moveDigit > 0) { //�������һ�����Щ�����ѻ�����!!��Ҫ����ƫ��!
			//System.out.println("��ȫ[" + _code + "]����Ĳ�����,��Ҫƫ��!");

			//�Ȼ������µ�����!!
			g2.drawLine(li_x1 + 15, li_y1, li_x1 + 15, li_y1 + (li_moveDigit * 20));
			li_y1 = li_y1 + (li_moveDigit * 20); //һ��ƫ�Ʋ��Ƕ�10������!!
		} else {
			//System.out.println("��ȫ[" + _code + "]����Ĳ�����,����Ҫƫ��!");
		}

		if (_beginLevel == _endLevel) { //���Y������ͬ,����ͬһ��,����һ��ˮƽ������!!
			if (li_moveDigit == 0) { //���û��ƫ��!
				drawWaveLineH(g2, li_y1, li_x1 + 15, li_x2 - 6); //
				g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //				//д��
				drawArrow(g2, li_x2, li_y2 + (li_moveDigit * 20), "����"); //�����ҵļ�ͷ!
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
			} else { //�����ƫ��,��Ϊ�˺ÿ�,Ӧ�þ���ͬ��ķ�ʽ,�ӵ��½���!
				if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "��")) { //��Ŀ���Ƿ��·�����??
					//li_x2 = li_x2 - 10; //����ƫ��10
				}

				if (_endDay > _beginDay) { //������ڲŻ�������!
					drawWaveLineH(g2, li_y1, li_x1 + 15, li_x2 + 15); //
					g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //				//д��
				}

				g2.drawLine(li_x2 + 15, li_y1, li_x2 + 15, li_y2 + 15); //����!!
				drawArrow(g2, li_x2 + 15, li_y2 + 15, "����"); //�����µļ�ͷ!
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
			}
		} else { //�������ͬһ��,��Ҫ������ʵ��!�Ȼ���������,�ٻ�����ʵ��!!
			if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, (_endLevel > _beginLevel) ? "��" : "��")) { //��Ŀ���Ƿ��·�����??
				li_x2 = li_x2 - 10; //����ƫ��10
				g2.setColor(Color.WHITE); //
				g2.fillRect(li_x2, li_y1 - 5, 10, 10); //���ԭ�����������һС��!!��Ϊ�����ζ��ǰ�油ȫ�Ķ�����ͷ��!
				g2.setColor(Color.BLACK); //
			}

			String[] str_rings = isDrillOneRing(_beginLevel, _endLevel, _endDay, _ringMap, true);
			if (str_rings == null) { //���û�д���ĳ��ԲȦ,��ǳ���,ֱ�ӻ���
				if (_endDay > _beginDay) { //������ڲŻ�������!,���п���û�в�����,ֱ����һ��ֱ����!!
					drawWaveLineH(g2, li_y1, li_x1 + 15, li_x2); //
					g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
				}

				if (_endLevel > _beginLevel) { //�����������ǰ�����Yλ�ô�!!
					g2.drawLine(li_x2, li_y1, li_x2, li_y2 - 15); //����!!
					drawArrow(g2, li_x2, li_y2 - 15, "����"); //�����µļ�ͷ!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
				} else {
					g2.drawLine(li_x2, li_y1, li_x2, li_y2 + 15); //����!!
					drawArrow(g2, li_x2, li_y2 + 15, "����"); //�����µļ�ͷ!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
				}
			} else { //����м䴩��ĳ��ԲȦ,����Ҫһ�����ӵ��Զ�������õĶ���,��Ҫ��ԲȦ���Ա߹չ�ȥ!!
				if (_endDay > _beginDay) { //����������������죬��Ҫ�Ȼ�������!���ڲŻ�������!,���п���û�в�����,ֱ����һ��ֱ����!!
					if (_endLevel > _beginLevel) {
						drawWaveLineH(g2, li_y1, li_x1 + 15, getXposByDay(_endDay, 2) - 30); //
						g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
					} else {
						if (li_moveDigit == 0) { //���û��ƫ���������ƹ�ȥ!
							drawWaveLineH(g2, li_y1, li_x1 + 15, getXposByDay(_endDay, 2) - 30); //
							g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
						} else { //�����ƫ��,����·��Ĺ�ȥ
							drawWaveLineH(g2, li_y1, li_x1 + 15, getXposByDay(_endDay, 2)); //
							g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
						}
					}
				}

				//�ȼ�������һ��Ȧ��λ��!
				String str_firstRing = str_rings[0]; //һ��Ҫ�����һ��ԲȦ!
				HashMap itemMap_first = (HashMap) _ringMap.get(str_firstRing); //
				int li_firstRingLevel = (Integer) itemMap_first.get("level"); //���һ��ԲȦ�����ڲ�!
				int li_firstRingY = getYposByLevel(li_firstRingLevel, 2); //���һ��ԲȦ��Yλ��!

				String str_lastRing = str_rings[str_rings.length - 1]; //һ��Ҫ�����һ��ԲȦ!
				HashMap itemMap = (HashMap) _ringMap.get(str_lastRing); //
				int li_lastRingLevel = (Integer) itemMap.get("level"); //���һ��ԲȦ�����ڲ�!
				int li_lastRingY = getYposByLevel(li_lastRingLevel, 2); //���һ��ԲȦ��Yλ��!

				if (_endLevel > _beginLevel) { //�����������ǰ�����Yλ�ô�!!
					//�����ƫ��,Ҳ��������,�������ĸ�,��Ϊ���»�ʱ��ƫ��Ҳ������,���Ծͻ�����!
					//�����ԭ������Ĳ�������!
					g2.setColor(Color.WHITE); //
					g2.fillRect(getXposByDay(_endDay, 2) - 30, li_y1, 30, 10); //
					g2.setColor(Color.BLACK); //
					g2.drawLine(getXposByDay(_endDay, 2) - 30, li_y1, getXposByDay(_endDay, 2) - 30, li_firstRingY + 30); //����!!
					if (_endDay == _beginDay) { //�����ͬһ��,��Ӧ����ֱ��!
						g2.drawLine(getXposByDay(_endDay, 2) - 30, li_firstRingY + 30, li_x2, li_firstRingY + 30); //����!!
					} else { //��������
						drawWaveLineH(g2, li_firstRingY + 30, getXposByDay(_endDay, 2) - 30, li_x2);
					}

					g2.drawLine(li_x2, li_firstRingY + 30, li_x2, li_y2 - 15); //����!!
					drawArrow(g2, li_x2, li_y2 - 15, "����"); //�����µļ�ͷ!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
				} else {
					if (li_moveDigit == 0) { //���û��ƫ���������ƹ�ȥ!
						//Ҫ��������
						g2.drawLine(getXposByDay(_endDay, 2) - 30, li_y1, getXposByDay(_endDay, 2) - 30, li_lastRingY - 30); //����!!
						if (_endDay == _beginDay) { //���ͬһ�죬����ֱ��!
							g2.drawLine(getXposByDay(_endDay, 2) - 30, li_lastRingY - 30, li_x2, li_lastRingY - 30); //����!!
						} else {
							drawWaveLineH(g2, li_lastRingY - 30, getXposByDay(_endDay, 2) - 30, li_x2);
						}
						g2.drawLine(li_x2, li_lastRingY - 30, li_x2, li_y2 + 15); //����!!
					} else { //�����ƫ��,Ҫ���ұ��ƹ�ȥ!
						//Ҫ���ĸ���,��һ���Ƕ���,���ƹ�ȥ�õ�,�ڶ����ǳ���,�ǴӸ�λ�õ����һ��Ȧ����������Ȧ��!
						g2.drawLine(getXposByDay(_endDay, 2), li_y1, getXposByDay(_endDay, 2) + 30, li_y1); //����!!

						//�ڶ�����!
						g2.drawLine(getXposByDay(_endDay, 2) + 30, li_y1, getXposByDay(_endDay, 2) + 30, li_lastRingY - 30); //����!!

						//��������!ԭ���������ʵ��li_x2����һ���յ�����ӵĺ���
						g2.drawLine(li_x2, li_lastRingY - 30, getXposByDay(_endDay, 2) + 30, li_lastRingY - 30); //����!!

						//���ĸ���
						g2.drawLine(li_x2, li_lastRingY - 30, li_x2, li_y2 + 15); //����!!
					}

					drawArrow(g2, li_x2, li_y2 + 15, "����"); //�����µļ�ͷ!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "��"); //
				}
			}
		}

		addLineAreaMark(_lineAreaSet, _beginLevel, null, li_moveDigit, _beginDay, _endDay); //
	}

	//��ĳ���������ж��Ƿ����ԲȦ,����У������Щ��¼����!����ʱ��Ҫ������ЩԲȦ!!
	private String[] isDrillOneRing(int _level, int _level2, int _day, HashMap _ringMap, boolean _isAddFirst) { //
		ArrayList list = new ArrayList(); //
		if (_level2 > _level) {
			if (!_isAddFirst) { //�����һ��Ҳ����!
				_level = _level + 1; //
			}
			for (int i = _level; i < _level2; i++) {
				String str_ring = findRing(i, _day, _ringMap); //
				if (str_ring != null) {
					list.add(str_ring); //
				}
			}
		} else {
			if (!_isAddFirst) { //�����һ��Ҳ����!
				_level = _level - 1; //
			}
			for (int i = _level; i > _level2; i--) { //
				String str_ring = findRing(i, _day, _ringMap); //
				if (str_ring != null) {
					list.add(str_ring); //
				}
			}
		}

		if (list.size() > 0) { //�����ԲȦ
			return (String[]) list.toArray(new String[0]); //
		} else {
			return null;
		}
	}

	private String[] isDrillOneRing2(int _level, int _beginDay, int _endDay, HashMap _ringMap, boolean _isAddFirst) { //
		ArrayList list = new ArrayList(); //
		if (!_isAddFirst) { //�����һ��Ҳ����!
			_beginDay = _beginDay + 1; //
		}
		for (int i = _beginDay; i < _endDay; i++) {
			String str_ring = findRing(_level, i, _ringMap); //
			if (str_ring != null) {
				list.add(str_ring); //
			}
		}
		if (list.size() > 0) { //�����ԲȦ
			return (String[]) list.toArray(new String[0]); //
		} else {
			return null;
		}
	}

	//�ж��Ƿ��ѻ���??
	private int isAlreadyDrawLine(HashSet _lineAreaSet, int _level, String _dir, int _beginDay, int _endDay) {
		for (int r = 0; r < 6; r++) { //���12��!!��Ϊһ����20������,��һ����120������!!
			ArrayList list = new ArrayList(); //
			for (int i = _beginDay; i < _endDay; i++) {
				if (r == 0) {
					list.add(_level + (_dir == null ? "" : _dir) + "-" + i); //
				} else {
					list.add(_level + (_dir == null ? "" : _dir) + "." + r + "-" + i); //
				}
			}
			String[] items = (String[]) list.toArray(new String[0]); //��η�Χ�ļ�¼!
			String[] allItems = (String[]) _lineAreaSet.toArray(new String[0]); //���м�¼
			boolean isDrawed = tbUtil.containTwoArrayCompare(items, allItems); //
			if (!isDrawed) { //�����һ��û�з����ص�����,��˵���ǿ��Ի���,�򷵻���һƫ�ƺ�!
				return r;
			}
		}

		return 1; //�������ʵ�ڲ���,��ֱ�ӷ��ص�һ��,��ʵ���ص�Ҳû�취��!
	}

	//����ͷ!!����������,1-����,2-����,3-����
	private void drawArrow(Graphics2D _g2, int _x, int _y, String _type) { //
		if (_type.equals("����")) { //����
			_g2.fillPolygon(new int[] { _x - 5, _x, _x - 5 }, new int[] { _y - 5, _y, _y + 5 }, 3); //�����Ҽ�ͷ!!
		} else if (_type.equals("����")) { //����
			_g2.fillPolygon(new int[] { _x - 5, _x + 5, _x }, new int[] { _y - 5, _y - 5, _y }, 3); //�����¼�ͷ!!
		} else if (_type.equals("����")) { //���ϼ�ͷ
			_g2.fillPolygon(new int[] { _x - 5, _x, _x + 5 }, new int[] { _y + 5, _y, _y + 5 }, 3); //�����ϼ�ͷ!!
		}
	}

	//��һ����������!
	private void drawWaveLineH(Graphics2D _g2, int _y, int _x1, int _x2) {
		int li_yCount = 0; //
		int li_XItem = 4; //�����ߺ���ÿ�ο��!!!
		int li_YItem = 2; //���˾��

		if (_x1 > _x2) { //���X1��Ȼ��,�򽻻�һ��λ��!!
			int tmpX = _x1; //
			_x1 = _x2; //
			_x2 = tmpX; //
		}

		while (1 == 1) { //
			int li_newX = _x1 + li_yCount * li_XItem; //
			if (li_newX >= _x2) { //���Խ�������˳�ѭ��!!!
				break; //
			}

			int li_y1 = _y; //
			int li_y2 = _y; //
			if (li_yCount == 0) { //����ǵ�һ��
				li_y1 = _y; //
				li_y2 = _y - li_YItem; //
			} else {
				if ((li_yCount % 2) != 0) { //���������!!
					li_y1 = _y - li_YItem; //
					li_y2 = _y + li_YItem; //
				} else { //ż��!
					li_y1 = _y + li_YItem; //
					li_y2 = _y - li_YItem; //
				}
			}

			int li_endX = li_newX + li_XItem; //
			if (li_endX > _x2) { //���Խ�磬��ֱ���ڱ߽�ߴ�!!
				li_endX = _x2; //
				li_y2 = _y; //
			}

			_g2.drawLine(li_newX, li_y1, li_endX, li_y2); //����!!
			li_yCount++; //
		}
	}

	private int getXposByDay(int _day, int _type) {
		int li_beginPos = 10 + li_oneDayWidth;
		if (_type == 1) { //1-���,��Ҫ��ȥԲȦ��һ��!
			return li_beginPos + (_day - 1) * li_oneDayWidth - 15; //
		} else if (_type == 2) { //2-����,����Ҳ����
			return li_beginPos + (_day - 1) * li_oneDayWidth; //	
		} else if (_type == 3) {
			return li_beginPos + (_day - 1) * li_oneDayWidth + 15; //	
		} else {
			return li_beginPos + (_day - 1) * li_oneDayWidth; //	
		}
	}

	//���ݲ�����Y����!!
	private int getYposByLevel(int _level, int _type) {
		int li_beginPos = 125; //ǰ꡸߶�!
		int li_oneLevelHeight = 120; //һ��ĸ߶�!
		if (_type == 1) { //����
			return li_beginPos + ((_level - 1) * li_oneLevelHeight); //
		} else if (_type == 2) { //�м�
			return li_beginPos + ((_level - 1) * li_oneLevelHeight) + 15; //
		} else if (_type == 3) {
			return li_beginPos + ((_level - 1) * li_oneLevelHeight) + 30; //
		} else {
			return li_beginPos + ((_level - 1) * li_oneLevelHeight) + 15; //
		}
	}

	public static void main(String[] _args) {
		JFrame frame = new JFrame("����"); //
		frame.setSize(1000, 700); //
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
		frame.getContentPane().add(new JScrollPane(new GunterPanel(null))); //
		frame.setVisible(true); //��ʾ����!
	}

}
