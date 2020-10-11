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
 * 时标网络图面板,非常有亮点的一个工具!在项目管理等应用中特别有用!
 * 这个“双代号时标网络图”,比甘特图更有作用！当然也更有难度!
 * @author xch
 *
 */
public class GunterPanel extends JPanel {

	private static final long serialVersionUID = 8025585872963903829L;
	private HashVO[] hvs = null;
	private int li_oneDayWidth = 60; //一天的宽度!

	private Color bgLineColor = new Color(200, 200, 200); //背景线的颜色!!是浅色!!
	private TBUtil tbUtil = new TBUtil(); //
	private Font font = new Font("新宋体", Font.PLAIN, 12);
	private Color brokeColor = new Color(255, 0, 255); //虚线的颜色!
	private Color waveColor = new Color(0, 0, 255); //波浪线的颜色

	private HashMap beginDayMap = new HashMap(); //
	private int li_Alldays = 0; // 
	private String str_lastWorkCode = null; //最后一步工作
	private StringBuffer sb_exception = new StringBuffer(); //

	/**
	 * 构造方法...
	 * @param _hvs
	 */
	public GunterPanel(HashVO[] _hvs) {
		this.hvs = _hvs;
		computeBeginDay(); //计算起始天!
		this.setBackground(Color.WHITE); //背景是白色!!
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
		if (sb_exception.toString().length() > 0) { //如果有异常内容(即在computeBeginDay方法中可能计算出某种数据异常,比如天数为空,顺序不对!)，则直接退出!!
			g2.setColor(Color.RED); //
			String[] str_items = tbUtil.split(sb_exception.toString(), "\r"); //
			for (int i = 0; i < str_items.length; i++) {
				g2.drawString(str_items[i], 85, 85 + (i * 20)); //
			}
			return; //
		}

		g2.setPaint(bgLineColor); //
		g2.drawLine(10, 10, 10 + (li_Alldays + 1) * li_oneDayWidth, 10); //第一道横线!
		g2.drawLine(10, 35, 10 + (li_Alldays + 1) * li_oneDayWidth, 35); //第二道横线!
		g2.drawLine(10, 60, 10 + (li_Alldays + 1) * li_oneDayWidth, 60); //第三道横线!

		g2.setPaint(Color.BLACK); //字是黑的!!
		g2.drawString("序号", 22, 28); //序号
		g2.drawString("日期", 22, 52); //序号

		long ll_beginTime = getDateTime("2014-06-15"); //从哪一天开始??!!!
		GregorianCalendar gc = new GregorianCalendar();
		for (int i = 0; i <= li_Alldays; i++) { //遍历各列!!!
			int li_x = 10 + (i * li_oneDayWidth);
			g2.setPaint(bgLineColor); //
			g2.drawLine(li_x, 10, li_x, 600); //输出各列!
			if (i >= 1) {
				gc.setTime(new Date(ll_beginTime + ((i - 1) * 1000 * 3600 * 24))); //修改日期!
				int li_month = gc.get(GregorianCalendar.MONTH) + 1; //
				int li_day = gc.get(GregorianCalendar.DAY_OF_MONTH); //第几天
				String str_day = "" + li_month + "/" + li_day + ""; //
				int li_weekDay = gc.get(GregorianCalendar.DAY_OF_WEEK); //
				if (li_weekDay == 1 || li_weekDay == 7) { //如果是周六日,则绿色
					g2.setPaint(Color.GREEN); //字是黑的!!
					g2.fillRect(li_x + 1, 36, li_oneDayWidth - 1, 25 - 1); //
				}

				g2.setPaint(Color.BLACK); //字是黑的!!
				g2.drawString("" + i, li_x + 30, 28); //序号
				g2.drawString(str_day, li_x + 20, 52); //日历!如果是周六日则彩色显示!
			}

			if (i == li_Alldays) { //循环中的最后一次!
				g2.setPaint(bgLineColor); //
				g2.drawLine(10 + (li_Alldays + 1) * li_oneDayWidth, 10, 10 + (li_Alldays + 1) * li_oneDayWidth, 600); //最后一根线
			}
		}

		g2.setPaint(bgLineColor); //
		g2.drawLine(10, 600, 10 + (li_Alldays + 1) * li_oneDayWidth, 600); //底线!!随着计算出来的实际层数，可能到时会中途增加!!

		g2.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_ON);

		HashSet lineAreaHst = new HashSet(); //记录各个线条的所画区域!
		HashMap actLevelMap = new HashMap(); //记录各个环节的实际层的Map
		HashMap ringMap = new HashMap(); //记录各个圆圈！！
		HashMap lineWayMap = new HashMap(); //记录线的流向的Map
		int li_index = 1;
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			int li_days = hvs[i].getIntegerValue("days"); //持续时间!!
			String str_fronts = hvs[i].getStringValue("fronts", ""); //紧前工作!
			int li_thisBeginDay = findBeginDay(str_code); //寻找出某个环节的起始天!稍微有点难度的算法!即要找出所有父亲链中最长的那个！
			int li_thisEndDay = li_thisBeginDay + li_days; //

			//首先找出他的父亲中那个能与自己紧贴的父亲！！找出他的层!!然后我肯定是优先紧着这个父亲后面！！
			String[] str_parent = findParentLevel(str_code, str_fronts, li_thisBeginDay, actLevelMap); //找出父亲工作的实际层!
			String str_parentCode = str_parent[0]; //我的最近的父亲!
			int li_parentLevel = Integer.parseInt(str_parent[1]); //父亲的层!!
			int li_level = findLevel(str_code, lineAreaHst, li_thisBeginDay, li_thisEndDay, li_parentLevel, actLevelMap); //从父亲工作的实际层往下找!!

			String[] str_items = tbUtil.split(str_fronts, ","); //看我有几个父亲
			if (str_items.length <= 1) { //如果没有父亲工作，或只有一个,则要判断一下，我前面的关是否已经画过了??如果画过了，则不要再画了!
				if (li_level == li_parentLevel) { //如果是同一层,则说明肯定是连接的,则直接画!
					drawActivity(g2, "" + li_index, li_thisBeginDay, li_level); //画出我的起始点!!可能不需要画! 即如果存在一个工作的紧前工作与我一样

					//记录我的父亲已画!!
					HashMap ringItemMap = new HashMap(); //圆圈中内容的Map值
					ringItemMap.put("day", new Integer(li_thisBeginDay)); //记录是第几天,也就是X坐标!
					ringItemMap.put("level", new Integer(li_level)); //记录是第几层,也就是Y坐标!
					ringItemMap.put("code", str_code); //记录是哪个工作!
					ringMap.put("" + li_index, ringItemMap); //记录该圆圈
					li_index++; //

					drawLine(g2, str_code, li_thisBeginDay, li_thisEndDay, li_level, lineWayMap, lineAreaHst); //画出各个线!需要判断在这个区间内，如果已经有线了，则要向下移一层画！！
				} else { //如果不是同一层,则说明不要画圈,但要画折线!
					int li_parEndDay = getEndDayByCode(str_items[0]);
					int li_parLevel = (Integer) actLevelMap.get(str_items[0]); //

					String str_ring = findRing(li_parLevel, li_parEndDay, ringMap); //这个位置是否有圆圈?
					if (str_ring == null) { //如果没有圈,则肯定要补上圆圈!
						drawActivity(g2, "" + li_index, li_parEndDay, li_parLevel); //画出我的起始点!!可能不需要画! 即如果存在一个工作的紧前工作与我一样

						//记录我的父亲已画!!
						HashMap ringItemMap = new HashMap(); //圆圈中内容的Map值
						ringItemMap.put("day", new Integer(li_parEndDay)); //记录是第几天,也就是X坐标!
						ringItemMap.put("level", new Integer(li_parLevel)); //记录是第几层,也就是Y坐标!
						ringItemMap.put("code", str_code); //记录是哪个工作!
						ringMap.put("" + li_index, ringItemMap); //记录该圆圈
						li_index++; //
					}

					//画折线!!
					drawLine2(g2, str_code, li_parEndDay, li_parLevel, li_thisEndDay, li_level, ringMap, lineWayMap, lineAreaHst); //画出各个线!需要判断在这个区间内，如果已经有线了，则要向下移一层画！！
				}
			} else { //如果有两个以上的父亲，则肯定要画前面的圈!
				drawActivity(g2, "" + li_index, li_thisBeginDay, li_level); //画出我的起始点!!可能不需要画! 即如果存在一个工作的紧前工作与我一样
				HashMap ringItemMap = new HashMap(); //圆圈中内容的Map值
				ringItemMap.put("day", new Integer(li_thisBeginDay)); //记录是第几天,也就是X坐标!
				ringItemMap.put("level", new Integer(li_level)); //记录是第几层,也就是Y坐标!
				ringItemMap.put("code", str_code); //记录是哪个工作!
				ringMap.put("" + li_index, ringItemMap); //记录该圆圈
				li_index++; //
				drawLine(g2, str_code, li_thisBeginDay, li_thisEndDay, li_level, lineWayMap, lineAreaHst); //画出各个线!需要判断在这个区间内，如果已经有线了，则要向下移一层画！！

				//记录走向!!
			}

			actLevelMap.put(str_code, new Integer(li_level)); //记录下该工作的实际层!因为他的后续工作默认应该就是与该层一样,即儿子与父亲原则上在同一层！
		}

		//重新循环一次!因为可能考虑到有些任务排序不巧，如果因为不知道后面的情况，所以画线不准!先补波浪线!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			String str_fronts = hvs[i].getStringValue("fronts", ""); //紧前工作!
			if (str_fronts == null || str_fronts.trim().equals("")) {
				continue;
			}

			String[] str_parents = tbUtil.split(str_fronts, ","); //
			if (str_parents.length <= 1) { //如果只有一个父亲也就算了!
				continue;
			}

			int li_beginDay = findBeginDay(str_code); //寻找出某个环节的起始天!稍微有点难度的算法!即要找出所有父亲链中最长的那个！
			int li_level = (Integer) actLevelMap.get(str_code); //

			for (int k = 0; k < str_parents.length; k++) { //遍历各父亲!
				int li_parLevel = (Integer) actLevelMap.get(str_parents[k]); //第几层!!即Y坐标
				int li_parEndDay = getEndDayByCode(str_parents[k]); //结束天数,即X坐标!!
				if (li_level == li_parLevel && li_beginDay == li_parEndDay) { //如果这个父亲的结束点与我的起始点正好重叠,说明该父亲就是我的亲爹!且连在一起,不需要做任何处理!!!
					continue;
				}

				String str_ring = findRing(li_parLevel, li_parEndDay, ringMap); //判断该父亲结束的位置是否有圆圈?
				if (str_ring == null) { //如果结束的位置有圆圈,则说明从该圆圈到我的起始圆圈的位置要画虚线!!!
					//画波浪线!!
					drawWaveLine(g2, str_parents[k], li_parEndDay, li_parLevel, li_beginDay, li_level, ringMap, lineWayMap, lineAreaHst, false); //
				} else { //如果结束的位置没有圆圈,则说明该父亲就一个儿子,而且就是我!只是没有直接相连!!即这个干爹没有亲儿子，只有我这一个干儿子!!
					if (!isMyRealBack(str_parents[k], (HashMap) ringMap.get(str_ring))) { //如果结束位置有圆圈,但却并不是我的真正父亲,则说明这个连接是错误的,需要
						drawWaveLine(g2, str_parents[k], li_parEndDay, li_parLevel, li_beginDay, li_level, ringMap, lineWayMap, lineAreaHst, true); //
					}
				}
			}
		}

		//再次循环补全虚线!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			String str_fronts = hvs[i].getStringValue("fronts", ""); //紧前工作!
			if (str_fronts == null || str_fronts.trim().equals("")) {
				continue;
			}
			String[] str_parents = tbUtil.split(str_fronts, ","); //
			if (str_parents.length <= 1) { //如果只有一个父亲也就算了!
				continue;
			}

			int li_beginDay = findBeginDay(str_code); //寻找出某个环节的起始天!稍微有点难度的算法!即要找出所有父亲链中最长的那个！
			int li_level = (Integer) actLevelMap.get(str_code); //
			for (int k = 0; k < str_parents.length; k++) { //遍历各父亲!
				int li_parLevel = (Integer) actLevelMap.get(str_parents[k]); //第几层!!即Y坐标
				int li_parEndDay = getEndDayByCode(str_parents[k]); //结束天数,即X坐标!!
				if (li_level == li_parLevel && li_beginDay == li_parEndDay) { //如果这个父亲的结束点与我的起始点正好重叠,说明该父亲就是我的亲爹!且连在一起,不需要做任何处理!!!
					continue;
				}
				String str_ring = findRing(li_parLevel, li_parEndDay, ringMap); //判断该父亲结束的位置是否有圆圈?
				if (str_ring != null) { //如果结束的位置有圆圈,则说明从该圆圈到我的起始圆圈的位置要画虚线!!!
					if (isMyRealBack(str_parents[k], (HashMap) ringMap.get(str_ring))) { //如果结束位置有圆圈,而且这个圆圈的确是他的后续工作,则说明是要画虚线的!
						drawBrokenLine(g2, str_parents[k], li_parEndDay, li_parLevel, li_beginDay, li_level, ringMap, lineWayMap, lineAreaHst); //
					}
				}
			}
		}

		//处理结尾情况
		int li_lastWorkLevel = (Integer) actLevelMap.get(str_lastWorkCode);
		int li_lastEndDay = getEndDayByCode(str_lastWorkCode); //
		drawActivity(g2, "" + li_index, li_lastEndDay, li_lastWorkLevel); //画最后结尾的圆圈!
		//处理其他结尾的,要指向这里!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			String str_backs = hvs[i].getStringValue("backs", ""); //紧后工作!
			if (!str_code.equals(str_lastWorkCode) && (str_backs == null || str_backs.trim().equals(""))) { //其他结尾
				int li_lastWorkLevel2 = (Integer) actLevelMap.get(str_code);
				int li_lastEndDay2 = getEndDayByCode(str_code); //
				//画波浪线
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

	//判断是否是我的真正的后续工作!!!
	private boolean isMyRealBack(String _code, HashMap _ringItemMap) {
		String str_actCode = (String) _ringItemMap.get("code"); //工作!!这个工作应该在我的backs定义中!
		if (str_actCode != null) { //肯定应该有的!
			HashVO hvo = getHashVOByCode(_code); //
			String str_backs = hvo.getStringValue("backs"); //
			if (str_backs != null) {
				String[] str_items = tbUtil.split(str_backs, ","); //
				return tbUtil.isExistInArray(str_actCode, str_items); //这个指向的是否是我的真正后续工作?有可能是因碰巧遇上的!
			}
		}
		return false;
	}

	//在某一个位置寻找是否有圆圈!
	private String findRing(int _level, int _day, HashMap _ringMap) {
		String[] str_keys = (String[]) _ringMap.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) { //遍历!!!
			HashMap itemMap = (HashMap) _ringMap.get(str_keys[i]); //
			int li_day = (Integer) itemMap.get("day");
			int li_level = (Integer) itemMap.get("level");
			if (li_level == _level && li_day == _day) { //如果天数与层正好都一样,则说明该位置是有圆圈的!!
				return str_keys[i]; //
			}
		}
		return null;
	}

	//根据一个位置寻找是否已经画了某个工作
	private String findDrawedWorkByPos(int _level, int _day, HashMap _actLevelMap) { //
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (findBeginDay(str_code) == _day) {
				if (_actLevelMap.containsKey(str_code)) { //如果已画!!
					if (_level == (Integer) _actLevelMap.get(str_code)) {
						return str_code; //
					}
				}
			}
		}
		return null; //
	}

	//找到某个工作的紧前工作的层!
	private String[] findParentLevel(String _thisCode, String _fronts, int _thisBeginDay, HashMap _actLevelMap) {
		if (_fronts == null || _fronts.equals("")) { //如果紧前工作为空,则说明肯定是第一层!!
			return new String[] { null, "1" }; //
		}
		String[] str_items = tbUtil.split(_fronts, ","); //可能有多个紧前工作!
		int li_endDay = 0; //记录最后的结点!
		String str_nearParent = null; //存储最亲近的父亲!!
		for (int i = 0; i < str_items.length; i++) { //遍历各个紧前工作
			HashVO hvo = getHashVOByCode(str_items[i]); //获得该工作的HashVO
			int li_beginDay = findBeginDay(hvo.getStringValue("code")); //
			int li_days = hvo.getIntegerValue("days"); //持续时间!!
			if ((li_beginDay + li_days) > li_endDay) {
				li_endDay = li_beginDay + li_days; //结束的天数!!!
				str_nearParent = hvo.getStringValue("code"); //记录最亲近的父亲!!!
			}
		}

		if ((_thisBeginDay - li_endDay) != 0) {
			System.err.println("[" + _thisCode + "]与其最近的父亲[" + str_nearParent + "]竟然不连成一一起[" + ((_thisBeginDay - li_endDay)) + "]?");
		} else {
			//System.out.println("[" + _thisCode + "]与其最近的父亲[" + str_nearParent + "]连在一起"); //
		}

		int li_level = (Integer) _actLevelMap.get(str_nearParent); //
		return new String[] { str_nearParent, "" + li_level }; //
	}

	//计算出某一工作结束于哪一天
	private int getEndDayByCode(String _code) {
		HashVO hvo = getHashVOByCode(_code); //
		int li_beginDay = findBeginDay(_code); //寻找出某个环节的起始天!稍微有点难度的算法!即要找出所有父亲链中最长的那个！
		return li_beginDay + hvo.getIntegerValue("days"); //
	}

	private HashVO getHashVOByCode(String _code) {
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			if (str_code.equalsIgnoreCase(_code)) {
				return hvs[i]; //
			}
		}
		return null; //
	}

	private int findLevel(String _thisCode, HashSet hst, int _beginDay, int _endDay, int _level, HashMap _actLevelMap) {
		boolean isFind = false; //
		for (int j = _beginDay; j < _endDay; j++) { //
			if (hst.contains(_level + "-" + j)) { //如果发现这一层中已经有了！！即画过了!
				isFind = true; //
				break; //
			}
		}

		if (isFind) { //如果发现了,则继续往下一层找！
			return findLevel(_thisCode, hst, _beginDay, _endDay, _level + 1, _actLevelMap);
		} else { //如果没发现，则返回这一层，说明这一层是行的
			//看目标处有没有已经画圈?即有一种情况是正好某工作的结尾处已经正好画了另一个工作!而且这个工作又不是我的后续工作，如果这时我画在这里，就会造成一种奇怪的错觉！感觉好象我是接上这个本来不是我后续工作的了!
			//其实这是错误的!!!所以必须要有这个判断!!!
			String str_endsWorkCode = findDrawedWorkByPos(_level, _endDay, _actLevelMap);
			if (str_endsWorkCode == null) { //如果结尾目标处没有画工作,则说明可行
				return _level; //
			} else { //如果已画！
				//System.out.println("已画东西[" + str_endsWorkCode + "]了,没地方了...");
				HashVO hvo = getHashVOByCode(_thisCode); //
				String str_backs = hvo.getStringValue("backs"); //我的后续工作!
				if (str_backs != null) {
					String[] str_items = tbUtil.split(str_backs, ","); //
					if (tbUtil.isExistInArray(str_endsWorkCode, str_items)) { //如果这个环节真好是我的某个后续工作,则是可以的!
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
	 * 计算项目总天数,以及各工作的起始天!至关重要的逻辑!!
	 */
	private void computeBeginDay() {
		//根据最后工作自动计算出紧前工作!!即我感觉其实只需要定义紧后工作即可!然后紧前工作能够自动计算得出来的!!
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			StringBuffer sb_fronts = new StringBuffer(); //
			for (int j = 0; j < i; j++) { //
				String str_code2 = hvs[j].getStringValue("code"); //工作编码!!!
				String str_backs = hvs[j].getStringValue("backs"); //工作编码!!!
				if (str_backs != null && !str_backs.equals("")) { //如果紧后工作
					String[] str_items = tbUtil.split(str_backs, ","); ////
					if (tbUtil.isExistInArray(str_code, str_items)) { //如果紧后工作包含我
						sb_fronts.append(str_code2 + ","); //加上
					}
				}
			}
			String str_fronts = sb_fronts.toString(); //
			if (str_fronts.endsWith(",")) { //如果有值!
				str_fronts = str_fronts.substring(0, str_fronts.length() - 1); //截掉!
				//System.out.println("工作[" + str_code + "]计算出来的紧前工作是[" + str_fronts + "]"); //
				hvs[i].setAttributeValue("fronts", str_fronts); //重置!!
			} else {
				if (i != 0) {
					sb_exception.append("工作【" + str_code + "】不是开始工作,同时又不是其他某个工作的紧后工作，这是不允许的!\r\n"); //
					return; //
				}
			}
		}

		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			String str_fronts = hvs[i].getStringValue("fronts"); //紧前工作!

			if (str_fronts != null && !str_fronts.trim().equals("")) { //如果有紧前工作
				String[] str_items = tbUtil.split(str_fronts, ","); //
				for (int j = 0; j < str_items.length; j++) {
					if (!isInBefore(str_items[j], i)) { //
						sb_exception.append("工作【" + str_code + "】中有一个紧前工作【" + str_items[j] + "】在前面没有定义,请重新定义或排序!\r\n"); //
						return; //
					}

					if (!isFrontInHerBacks(str_code, str_items[j], i)) {
						sb_exception.append("工作【" + str_code + "】中的紧前工作【" + str_items[j] + "】中定义的紧后工作没有本工作,请调整!\r\n"); //
						return; //
					}
				}
			}

			String str_backs = hvs[i].getStringValue("backs"); //紧后工作!
			if (str_backs != null && !str_backs.equals("")) { //如果紧后工作
				String[] str_items = tbUtil.split(str_backs, ","); ////
				for (int j = 0; j < str_items.length; j++) {
					if (!isInBack(str_items[j], i)) { ///
						sb_exception.append("工作【" + str_code + "】中有一个紧后工作【" + str_items[j] + "】在后面没有定义,请重新定义或排序!\r\n"); //
						return; //
					}

					if (!isBackInHerFronts(str_code, str_items[j], i)) {
						sb_exception.append("工作【" + str_code + "】中的紧前工作【" + str_items[j] + "】中定义的紧后工作没有本工作,请调整!\r\n"); //
						return; //
					}
				}
			}

			if ((str_fronts == null || str_fronts.trim().equals("")) && (str_backs == null || str_backs.trim().equals(""))) { //如果紧前紧后都为空,则也不对!
				sb_exception.append("工作【" + str_code + "】的紧前工作与紧后工作都为空,这是不允许的,请重新定义!\r\n"); //
				return; //
			}

			String str_days = hvs[i].getStringValue("days"); //天数
			if (str_days == null || str_days.trim().equals("")) {
				sb_exception.append("工作【" + str_code + "】有持续天数为空,请定义！\r\n"); //
				return; //
			} else {
				int li_days = Integer.parseInt(str_days); //天数
				int li_beginDay = computeBeginDay(str_code); //
				beginDayMap.put(str_code, new Integer(li_beginDay)); //加入缓存!!!
				if (li_beginDay + li_days >= li_Alldays) { //记录整个工程最多耗时天数!!!
					str_lastWorkCode = str_code; //记录最后一步工作!!
					li_Alldays = li_beginDay + li_days;
				}
			}
		}

		//校验紧后工作之间是否又存在先后父子关系?
		for (int i = 0; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //工作编码!!!
			String str_backs = hvs[i].getStringValue("backs"); //紧后工作!
			if (str_backs != null) {
				String[] str_items = tbUtil.split(str_backs, ","); //
				if (str_items.length > 1) { //如果有多个紧后工作!
					for (int p = 0; p < str_items.length; p++) {
						for (int t = 0; t < str_items.length; t++) {
							if (p != t) {
								if (isBackAndFront(str_items[p], str_items[t])) {
									sb_exception.append("工作【" + str_code + "】的两个紧后工作【" + str_items[p] + "】与【" + str_items[t] + "】之间本身又是先后关系,这是不可能的,请删除其中一个!\r\n"); //
									return; //
								}
							}
						}
					}
				}
			}
		}

		System.out.println("工程总天数是[" + li_Alldays + "],最后一项工作是[" + str_lastWorkCode + "]"); //
	}

	//判断两个工作之间是否是先后关系??
	private boolean isBackAndFront(String _code1, String _code2) {
		ArrayList leafList = new ArrayList(); //记录所有叶子结点的列表
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(_code1); //
		recursionAdd(_code1, rootNode, leafList); //递归计算出所有路径!!!

		//System.out.println("工作[" + _code1 + "]的所有路径包括:"); //
		//遍历各种可能存在的路径
		int li_maxBeginDays = 1; //
		for (int i = 0; i < leafList.size(); i++) { //遍历各个叶子,即可能存在的各种路径!!
			StringBuffer sb_text = new StringBuffer(); //
			DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) leafList.get(i); //某个结点!
			TreeNode[] allNodes = itemNode.getPath(); //
			for (int j = 0; j < allNodes.length; j++) { //遍历所有的!
				String str_itemCode = ((DefaultMutableTreeNode) allNodes[j]).getUserObject().toString(); //
				if (str_itemCode.equals(_code2)) {
					return true; //
				}
				//sb_text.append(str_itemCode + "->"); //
			}
			//System.out.println("第[" + (i + 1) + "]条路径=" + sb_text.toString());
		}
		//System.out.println(); //
		return false;
	}

	private boolean isInBefore(String _code, int _index) {
		for (int i = 0; i < _index; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_code)) { //如果有,则ok
				return true; //
			}
		}
		return false;
	}

	private boolean isFrontInHerBacks(String _thisCode, String _front, int _index) {
		for (int i = 0; i < _index; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_front)) { //如果有该紧前工作,则ok
				String str_backs = hvs[i].getStringValue("backs"); //
				if (str_backs != null && !str_backs.equals("")) {
					String[] str_items = tbUtil.split(str_backs, ","); //
					if (tbUtil.isExistInArray(_thisCode, str_items)) { //如果这个工作的紧后工作中有我,则说明是对的!
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
			if (str_code.equals(_code)) { //如果有,则ok
				return true; //
			}
		}
		return false;
	}

	private boolean isBackInHerFronts(String _thisCode, String _back, int _index) {
		for (int i = _index + 1; i < hvs.length; i++) {
			String str_code = hvs[i].getStringValue("code"); //
			if (str_code.equals(_back)) { //如果有该紧后工作,则ok
				String str_fronts = hvs[i].getStringValue("fronts"); //找出该工作的紧前工作
				if (str_fronts != null && !str_fronts.equals("")) {
					String[] str_items = tbUtil.split(str_fronts, ","); //
					if (tbUtil.isExistInArray(_thisCode, str_items)) { //如果这个工作的紧前工作中有我,则说明是对的!
						return true; //
					}
				}
			}
		}
		return false;
	}

	/**
	 * 计算出某个工作的起始天,一个非常关键与复杂的计算逻辑
	 * 它的原理是计算出该工作所有可能存有的父亲链，然后其中耗时最长的那条线路就是我的起始天!
	 * 这样计算出来的结果就能保证波浪线永远是从左到右，不可能从右到左! 即“空档期”永远是正向的！而不能反向的，因为时间不可倒流!!
	 * 需要递归算法!!!
	 */
	private int computeBeginDay(String _code) {
		ArrayList leafList = new ArrayList(); //记录所有叶子结点的列表
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(_code); //
		recursionAdd(_code, rootNode, leafList); //递归计算出所有路径!!!

		//System.out.println("工作[" + _code + "]的所有路径包括:"); //
		//遍历各种可能存在的路径
		int li_maxBeginDays = 1; //
		for (int i = 0; i < leafList.size(); i++) { //遍历各个叶子,即可能存在的各种路径!!
			StringBuffer sb_text = new StringBuffer(); //
			DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) leafList.get(i); //某个结点!
			TreeNode[] allNodes = itemNode.getPath(); //
			int li_days = 1; //
			for (int j = 0; j < allNodes.length; j++) { //遍历所有的!
				String str_itemCode = ((DefaultMutableTreeNode) allNodes[j]).getUserObject().toString(); //
				int li_itemDays = getHashVOByCode(str_itemCode).getIntegerValue("days"); //
				sb_text.append(str_itemCode + "(" + li_itemDays + ")->"); //
				if (j != 0) { //自己的天数不算,只计算前面的!
					li_days = li_days + li_itemDays; //累加出其天数!!
				}
			}
			//System.out.println("第[" + (i + 1) + "]条路径=" + sb_text.toString());
			if (li_days > li_maxBeginDays) { //
				li_maxBeginDays = li_days; //只记录最大的!!
			}
		}
		//System.out.println("工作[" + _code + "]的起始天是[" + li_maxBeginDays + "]!"); //
		//System.out.println(); //

		return li_maxBeginDays;
	}

	private int findBeginDay(String _code) {
		return (Integer) beginDayMap.get(_code); //
	}

	//递归算法!!
	private void recursionAdd(String _code, DefaultMutableTreeNode _node, ArrayList _list) {
		HashVO hvo = getHashVOByCode(_code); //找到该条记录!
		String str_fronts = hvo.getStringValue("fronts"); //前置结点!
		if (str_fronts == null || str_fronts.trim().equals("")) { //如果为空则不继续找了!
			_list.add(_node); //
			return; //
		}

		String[] str_items = tbUtil.split(str_fronts, ","); //看有几个父亲
		for (int i = 0; i < str_items.length; i++) { //遍历各个结点!
			//如果我的父亲链中已存在了,则不能加入，因为这会造成死循环!
			boolean isHave = ifHaveThisInMyPath(_node, str_items[i]); //
			if (isHave) { //如果已存在,则跳过,即再也不往下找了
				continue; //
			}
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(str_items[i]); //
			_node.add(childNode); //一定要加入
			recursionAdd(str_items[i], childNode, _list); //继续调用自己!!
		}
	}

	private boolean ifHaveThisInMyPath(DefaultMutableTreeNode _node, String _code) {
		return false;
	}

	//画一个环节，即一个圆形!!
	private void drawActivity(Graphics2D g2, String _text, int _day, int _level) {
		int li_x = getXposByDay(_day, 2); //
		int li_y = getYposByLevel(_level, 2); //
		g2.setColor(Color.WHITE); //
		g2.fillOval(li_x - 15, li_y - 15, 30, 30); //
		g2.setColor(Color.BLACK); //
		g2.drawOval(li_x - 15, li_y - 15, 30, 30);
		g2.drawString(_text, li_x - (_text.length() * 3), li_y + 5); //写字
	}

	//增加一个方向标记!!!
	private void addLineWayMark(HashMap _lineWayMap, String _key, String _mark) {
		if (!_lineWayMap.containsKey(_key)) { //
			_lineWayMap.put(_key, _mark + ";"); //
		} else {
			String str_value = (String) _lineWayMap.get(_key); //
			_lineWayMap.put(_key, str_value + _mark + ";"); //
		}
	}

	private void addLineAreaMark(HashSet _lineAreaSet, int _level, String _dir, int _movedigit, int _beginday, int _endDay) {
		//先记录该层的“起始天”-“结束天”已被占用!!
		for (int i = _beginday; i < _endDay; i++) { //加入!
			if (_movedigit > 0) { //
				_lineAreaSet.add(_level + (_dir == null ? "" : _dir) + "." + _movedigit + "-" + i); //
			} else {
				_lineAreaSet.add(_level + (_dir == null ? "" : _dir) + "-" + i); //
			}
		}
	}

	//在某个位置是否存在某种方向的线???这将决定是否需要错开!!!
	private boolean isHaveWay(HashMap _lineWayMap, String _key, String _mark) {
		if (_lineWayMap.containsKey(_key)) {
			String str_value = (String) _lineWayMap.get(_key); //
			String[] str_items = tbUtil.split(_mark, ";"); //
			for (int i = 0; i < str_items.length; i++) { //如果有其中某一个,就算有!
				if (str_value.indexOf(str_items[i] + ";") >= 0) {
					return true; //
				}
			}
		}

		return false; //
	}

	/**
	 * 画一条线,从哪个点开始到哪个点结束!!
	 * 要能判断得出是不是在同一层！如果不在同一层，则可能要用折线！！
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
		g2.drawLine(li_x1, li_y, li_x2, li_y); //画线!!

		//画箭头!!
		g2.fillPolygon(new int[] { li_x2 - 5, li_x2, li_x2 - 5 }, new int[] { li_y - 5, li_y, li_y + 5 }, 3); //画箭头!!

		//在线上面标出文字!
		int li_text_x = li_x1 + (li_x2 - li_x1) / 2 - 6; //
		int li_text_y = li_y - 5; //
		g2.setColor(Color.WHITE);
		g2.fillRect(li_text_x, li_text_y - 12, 30, 13); //

		g2.setColor(Color.BLACK);
		g2.drawString(_text, li_text_x, li_text_y); //
		g2.drawString("" + (_endDay - _beginDay), li_text_x, li_y + 17); //下面是天数

		//记录走向!!
		addLineWayMark(_lineWayMap, "" + _level + "-" + _beginDay, "右"); //
		addLineAreaMark(_lineAreaSet, _level, null, 0, _beginDay, _endDay); //
	}

	//画折线!!
	private void drawLine2(Graphics2D g2, String _code, int _fromDay, int _fromLevel, int _endDay, int _level, HashMap _ringMap, HashMap _lineWayMap, HashSet _lineAreaSet) {
		int li_x1 = getXposByDay(_fromDay, 2); //
		int li_x2 = getXposByDay(_endDay, 1); //
		int li_y1 = getYposByLevel(_fromLevel, 3); //
		int li_y2 = getYposByLevel(_level, 2); //

		g2.setColor(Color.BLACK); //

		//看这个纵向有没有圆圈?
		String[] str_rings = isDrillOneRing(_fromLevel, _level, _fromDay, _ringMap, false); //
		if (str_rings == null) {
			g2.drawLine(li_x1, li_y1, li_x1, li_y2); //画坚线!!
		} else { //如果有圆圈则避开之!
			//画三根线
			g2.drawLine(li_x1, li_y1, li_x1 + 26, li_y1 + 20); //画坚线!!
			g2.drawLine(li_x1 + 26, li_y1 + 20, li_x1 + 26, li_y2 - 20); //画坚线!!
			g2.drawLine(li_x1, li_y2, li_x1 + 26, li_y2 - 20); //画坚线!!
		}

		g2.drawLine(li_x1, li_y2, li_x2, li_y2); //画横线!!

		drawArrow(g2, li_x2, li_y2, "向右"); //画向右的箭头!

		//在线上面标出文字!
		int li_text_x = li_x1 + (li_x2 - li_x1) / 2 - 6; //
		int li_text_y = li_y2 - 5; //

		g2.setColor(Color.BLACK);
		g2.drawString(_code, li_text_x, li_text_y); //
		g2.drawString("" + (_endDay - _fromDay), li_text_x, li_y2 + 17); //下面是天数

		//记录走向!!!
		addLineWayMark(_lineWayMap, "" + _fromLevel + "-" + _fromDay, "下"); //
		addLineWayMark(_lineWayMap, "" + _level + "-" + _endDay, "左"); //
		addLineAreaMark(_lineAreaSet, _level, null, 0, _fromDay, _endDay); //
	}

	/**
	 * 画虚线,即这两个环节之间有连接，但并不是一个实际工作!!
	 * 1.画虚线也有两个难点,一是如果两者天数不一样,则要折线,则折线的横向不能重叠,要错开!
	 * 2.画纵向线时
	 */
	private void drawBrokenLine(Graphics2D g2, String _code, int _beginDay, int _beginLevel, int _endDay, int _endLevel, HashMap _ringMap, HashMap _lineWayMap, HashSet _lineAreaSet) {
		g2.setColor(brokeColor); //
		//g2.setStroke(new BasicStroke(1.5f)); //

		int li_x1 = getXposByDay(_beginDay, 2); //
		int li_x2 = getXposByDay(_endDay, 2); //
		int li_y1 = getYposByLevel(_beginLevel, _endLevel > _beginLevel ? 3 : 1); //
		int li_y2 = getYposByLevel(_endLevel, _endLevel > _beginLevel ? 1 : 3); //

		if (_beginDay == _endDay) { //如果X坐标相同,即是同一天,则画一条纵向直线!!中间有可能穿过圆圈么?
			//System.out.println("画直线!!" + _endLevel + "-" + _endDay); //
			if (_endLevel > _beginLevel) { //如果结束的在下面
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "下") || isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "上")) { //如果起始结点已有线了
					li_x1 = li_x1 - 10; //错开
					li_x2 = li_x2 - 10; //错开
				}
			} else {
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "上") || isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "下")) { //如果起始结点已有线了
					li_x1 = li_x1 + 10; //错开
					li_x2 = li_x2 + 10; //错开
				}
			}

			String[] str_rings = isDrillOneRing(_beginLevel, _endLevel, _beginDay, _ringMap, false); //看有没有穿过其他圆圈?
			if (str_rings != null) { //如果穿过其他圆圈,则要从左边避开!!要画四根线
				if (_endLevel > _beginLevel) {
					g2.drawLine(li_x1, li_y1, getXposByDay(_beginDay, 2) - 27, getYposByLevel(_beginLevel, 2) + 20); //
					drawBrokeLineV(g2, getXposByDay(_beginDay, 2) - 27, getYposByLevel(_beginLevel, 2) + 20, li_y2 - 20); //画纵向虚线!!
					drawBrokeLineH(g2, li_y2 - 20, getXposByDay(_beginDay, 2) - 27, li_x1); //
					drawBrokeLineV(g2, li_x1, li_y2 - 20, li_y2); //画纵向虚线!!
				} else {
					g2.drawLine(li_x1, li_y1, getXposByDay(_beginDay, 2) + 27, getYposByLevel(_beginLevel, 2) - 20); //
					drawBrokeLineV(g2, getXposByDay(_beginDay, 2) + 27, getYposByLevel(_beginLevel, 2) - 20, li_y2 + 20); //画纵向虚线!!
					drawBrokeLineH(g2, li_y2 + 20, getXposByDay(_beginDay, 2) + 27, li_x1); //
					drawBrokeLineV(g2, li_x1, li_y2, li_y2 + 20); //画纵向虚线!!
				}
			} else {
				drawBrokeLineV(g2, li_x1, li_y1, li_y2); //画纵向虚线!!
			}

			if (li_y2 > li_y1) {
				drawArrow(g2, li_x2, li_y2, "向下"); //画向下的箭头!
			} else {
				drawArrow(g2, li_x2, li_y2, "向上"); //画向上的箭头!
			}

			//记录走向!!...
			if (_endLevel > _beginLevel) {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "下"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "上"); //
			} else {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "上"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "下"); //
			}
		} else if (_beginLevel == _endLevel) { //如果Y相等!,则画一条横线!理论上来说不应该存在横向虚线与实线重叠的情况!即后续工作之间不应该再见有后续关系!!
			int li_y = getYposByLevel(_beginLevel, 2); //
			String[] str_rings = isDrillOneRing2(_beginLevel, _beginDay, _endDay, _ringMap, false); //看有没有穿过一些圆圈?
			if (str_rings == null) {
				drawBrokeLineH(g2, li_y, li_x1 + 15, li_x2 - 15); //
				drawArrow(g2, li_x2 - 15, li_y, "向右"); //画向右的箭头!
			} else {
				drawBrokeLineV(g2, li_x1 + 13, li_y - 8, li_y - 30); //
				drawBrokeLineH(g2, li_y - 30, li_x1 + 15, li_x2 - 15); //
				drawBrokeLineV(g2, li_x2 - 15, li_y - 30, li_y - 15); //
				drawArrow(g2, li_x2 - 15, li_y - 10, "向下"); //画向右的箭头!
			}

			//记录走向!!
			addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "右"); //
			addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "左"); //如果是从上面绕过来的,则虽然箭头是向下,但标记仍然是左,这是为了为以后实际从上方来的线留空间!!!这是一种特殊但合理的情况!!
		} else { //如果中间有跨度,则要画三段线!!!
			//System.out.println("画三段线!!" + _endLevel + "-" + _endDay + "&" + _lineWayMap.get("" + _endLevel + "-" + _endDay)); //
			int li_centerY = 0; //
			if (_endLevel > _beginLevel) { //如果后续结点的Y位置比前面结点的Y位置大!
				li_centerY = getYposByLevel(_beginLevel, 2) + 60; //以前是两者相差除以2,但那样可能正好与层重叠!!所以还不如直接拿起始位置为标准直接移半个层，这样肯定保证是平面在层的中间了!
			} else {
				li_centerY = getYposByLevel(_beginLevel, 2) - 60; //
			}

			int li_moveDigit = isAlreadyDrawLine(_lineAreaSet, (_endLevel > _beginLevel ? _beginLevel : _endLevel), "下", _beginDay, _endDay); //移位!
			if (li_moveDigit > 0) { //可能是1-6，1是向下,2是向上,3是向下两格!
				if (li_moveDigit % 2 == 0) { //如果是偶数!则向上移
					li_centerY = li_centerY - (li_moveDigit / 2) * 20; //向上偏移一格
				} else { //如果是奇数,则向下移!
					li_centerY = li_centerY + ((li_moveDigit / 2) + 1) * 20; //向下偏移一格!
				}
			}

			if (_endLevel > _beginLevel) { //如果结束的在下面
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "下")) { //如果起始结点已有线了
					li_x1 = li_x1 + 10; //
				}
				if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "上")) { //如果起始结点已有线了
					li_x2 = li_x2 - 10; //
				}
			} else {
				if (isHaveWay(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "上")) { //如果起始结点已有线了
					li_x1 = li_x1 + 10; //
				}
				if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, " 下")) { //如果起始结点已有线了
					li_x2 = li_x2 - 10; //
				}
			}

			//要画三段线!
			drawBrokeLineV(g2, li_x1, li_y1, li_centerY); //纵线
			drawWaveLineH(g2, li_centerY, li_x1, li_x2); //要画波浪线!!表示是要等待的时间!!

			//考虑到可能会穿过圆圈,所以需要做避开处理!
			String[] str_rings = isDrillOneRing(_beginLevel, _endLevel, _endDay, _ringMap, false); //看有没有穿过其他圆圈?
			if (str_rings != null) { //如果穿过其他圆圈,则要从左边避开!!要画四根线
				if (_endLevel > _beginLevel) {
					drawBrokeLineV(g2, li_x2, li_centerY, li_centerY + 20); //
					drawBrokeLineH(g2, li_centerY + 20, li_x2 - 27, li_x2); //
					drawBrokeLineV(g2, li_x2 - 27, li_centerY + 20, li_y2 - 20); //画纵向虚线!!
					drawBrokeLineH(g2, li_y2 - 20, li_x2 - 27, li_x2); //
					drawBrokeLineV(g2, li_x2, li_y2 - 20, li_y2); //画纵向虚线!!
				} else {
					drawBrokeLineH(g2, li_centerY, li_x2, li_x2 + 27); //
					drawBrokeLineV(g2, li_x2 + 27, li_centerY, li_y2 + 20); //画纵向虚线!!
					drawBrokeLineH(g2, li_y2 + 20, li_x2 + 27, li_x2); //
					drawBrokeLineV(g2, li_x2, li_y2 + 20, li_y2); //画纵向虚线!!
				}
			} else {
				drawBrokeLineV(g2, li_x2, li_centerY, li_y2); //纵线
			}

			if (li_y2 > li_y1) { //
				drawArrow(g2, li_x2, li_y2, "向下"); //画向下的箭头!
			} else {
				drawArrow(g2, li_x2, li_y2, "向上"); //画向下的箭头!
			}

			//记录走向!!...
			if (_endLevel > _beginLevel) {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "下"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "上"); //
			} else {
				addLineWayMark(_lineWayMap, "" + _beginLevel + "-" + _beginDay, "上"); //
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "下"); //
			}

			addLineAreaMark(_lineAreaSet, (_endLevel > _beginLevel ? _beginLevel : _endLevel), "下", li_moveDigit, _beginDay, _endDay); //记录偏移
		}
	}

	//画一条横向虚线
	private void drawBrokeLineH(Graphics2D _g2, int _y, int _x1, int _x2) {
		if (_x1 > _x2) { //如果y1比y2大,则交换一下位置!
			int tmpX = _x1; //
			_x1 = _x2; //
			_x2 = tmpX; //
		}
		int li_item = 4; //虚线中的每段的像素
		int li_space = 6; //虚线中每段的留白
		int li_yCount = 0; //
		while (1 == 1) { //
			int li_newX = _x1 + li_yCount * (li_item + li_space); //
			if (li_newX >= _x2) { //如果越界了则退出循环!!!
				break; //
			}

			int li_endX = li_newX + li_item; //
			if (li_endX > _x2) { //如果越界，则直接于边界尺寸!!
				li_endX = _x2; //
			}

			_g2.drawLine(li_newX, _y, li_endX, _y); //画线!!
			li_yCount++; //
		}
	}

	//画一条纵向虚线
	private void drawBrokeLineV(Graphics2D _g2, int _x, int _y1, int _y2) {
		if (_y1 > _y2) { //如果y1比y2大,则交换一下位置!
			int tmp_y = _y1; //
			_y1 = _y2; //
			_y2 = tmp_y; //
		}
		int li_item = 4; //虚线中的每段的像素
		int li_space = 6; //虚线中每段的留白
		int li_yCount = 0; //
		while (1 == 1) { //
			int li_newY = _y1 + li_yCount * (li_item + li_space); //
			if (li_newY >= _y2) { //如果越界了则退出循环!!!
				break; //
			}

			int li_endY = li_newY + li_item; //
			if (li_endY > _y2) { //如果越界，则直接于边界尺寸!!
				li_endY = _y2; //
			}

			_g2.drawLine(_x, li_newY, _x, li_endY); //画线!!
			li_yCount++; //
		}
	}

	/**
	 * 画波浪线!!波浪线的核心特征是只有一个直角,即先是向右画波浪线，然后向上画直接!
	 * 但麻烦的是有两点:
	 * 1.一是横向可能已经有内容了，这时要向下偏移
	 * 2.二是向上时可能遇到圆圈，这里要自动避让!
	 */
	private void drawWaveLine(Graphics2D g2, String _code, int _beginDay, int _beginLevel, int _endDay, int _endLevel, HashMap _ringMap, HashMap _lineWayMap, HashSet _lineAreaSet, boolean _isDealErr) {
		g2.setStroke(new BasicStroke(1)); //
		String str_betweenDay = "TF=" + (_endDay - _beginDay); //间隔天数!

		int li_x1 = getXposByDay(_beginDay, 1); //
		int li_x2 = getXposByDay(_endDay, _beginLevel == _endLevel ? 1 : 2); //

		int li_y1 = getYposByLevel(_beginLevel, 2); //
		int li_y2 = getYposByLevel(_endLevel, 2); //

		//先上补全原来不全的线!
		if (_isDealErr) { //如果是修补错误
			g2.setColor(Color.WHITE); //
			g2.fillRect(li_x1 - 15, li_y1 - 5, 15, 10); //清空原来箭头!!再往左多清空一些!因为后面向上画线就是这个位置!
		} else { //如果不是修补错误的情况下，则要补全!如果是修补错误说明这里就有一个圆圈，肯定不能再画了！
			g2.setColor(Color.WHITE); //
			g2.fillRect(li_x1 - 5, li_y1 - 5, 5, 10); //清空原来箭头!!

			g2.setColor(Color.BLACK); //
			g2.drawLine(li_x1 - 5, li_y1, li_x1 + 15, li_y1); //补全原来缺失的半个圆圈！！
		}

		g2.setColor(Color.BLACK); //

		int li_moveDigit = isAlreadyDrawLine(_lineAreaSet, _beginLevel, null, _beginDay, _endDay); //偏移几位
		if (li_moveDigit > 0) { //如果在这一层的这些天中已画了线!!则要向下偏移!
			//System.out.println("补全[" + _code + "]后面的波浪线,需要偏移!");

			//先画出向下的折线!!
			g2.drawLine(li_x1 + 15, li_y1, li_x1 + 15, li_y1 + (li_moveDigit * 20));
			li_y1 = li_y1 + (li_moveDigit * 20); //一个偏移层是多10个像素!!
		} else {
			//System.out.println("补全[" + _code + "]后面的波浪线,不需要偏移!");
		}

		if (_beginLevel == _endLevel) { //如果Y坐标相同,即是同一层,则是一条水平波浪线!!
			if (li_moveDigit == 0) { //如果没有偏移!
				drawWaveLineH(g2, li_y1, li_x1 + 15, li_x2 - 6); //
				g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //				//写字
				drawArrow(g2, li_x2, li_y2 + (li_moveDigit * 20), "向右"); //画向右的箭头!
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "左"); //
			} else { //如果有偏移,则为了好看,应该就像不同层的方式,从底下接入!
				if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, "下")) { //看目标是否下方有线??
					//li_x2 = li_x2 - 10; //向左偏移10
				}

				if (_endDay > _beginDay) { //如果大于才画波浪线!
					drawWaveLineH(g2, li_y1, li_x1 + 15, li_x2 + 15); //
					g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //				//写字
				}

				g2.drawLine(li_x2 + 15, li_y1, li_x2 + 15, li_y2 + 15); //画线!!
				drawArrow(g2, li_x2 + 15, li_y2 + 15, "向上"); //画向下的箭头!
				addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "下"); //
			}
		} else { //如果不是同一层,则要画两条实线!先画横向波浪线,再画纵向实线!!
			if (isHaveWay(_lineWayMap, "" + _endLevel + "-" + _endDay, (_endLevel > _beginLevel) ? "上" : "下")) { //看目标是否下方有线??
				li_x2 = li_x2 - 10; //向左偏移10
				g2.setColor(Color.WHITE); //
				g2.fillRect(li_x2, li_y1 - 5, 10, 10); //清空原来多出来的那一小段!!因为这就意味着前面补全的动作过头了!
				g2.setColor(Color.BLACK); //
			}

			String[] str_rings = isDrillOneRing(_beginLevel, _endLevel, _endDay, _ringMap, true);
			if (str_rings == null) { //如果没有穿过某个圆圈,则非常简单,直接画线
				if (_endDay > _beginDay) { //如果大于才画波浪线!,即有可能没有波浪线,直接是一个直角线!!
					drawWaveLineH(g2, li_y1, li_x1 + 15, li_x2); //
					g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
				}

				if (_endLevel > _beginLevel) { //如果后续结点比前面结点的Y位置大!!
					g2.drawLine(li_x2, li_y1, li_x2, li_y2 - 15); //画线!!
					drawArrow(g2, li_x2, li_y2 - 15, "向下"); //画向下的箭头!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "上"); //
				} else {
					g2.drawLine(li_x2, li_y1, li_x2, li_y2 + 15); //画线!!
					drawArrow(g2, li_x2, li_y2 + 15, "向上"); //画向下的箭头!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "下"); //
				}
			} else { //如果中间穿过某个圆圈,则需要一个复杂的自动拐弯避让的动作,即要从圆圈的旁边拐过去!!
				if (_endDay > _beginDay) { //如果横向有天数差异，则要先画波浪线!大于才画波浪线!,即有可能没有波浪线,直接是一个直角线!!
					if (_endLevel > _beginLevel) {
						drawWaveLineH(g2, li_y1, li_x1 + 15, getXposByDay(_endDay, 2) - 30); //
						g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
					} else {
						if (li_moveDigit == 0) { //如果没有偏移则从左边绕过去!
							drawWaveLineH(g2, li_y1, li_x1 + 15, getXposByDay(_endDay, 2) - 30); //
							g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
						} else { //如果有偏移,则从下方饶过去
							drawWaveLineH(g2, li_y1, li_x1 + 15, getXposByDay(_endDay, 2)); //
							g2.drawString(str_betweenDay, li_x1 + 15 + 10, li_y1 - 5); //
						}
					}
				}

				//先计算出最后一个圈的位置!
				String str_firstRing = str_rings[0]; //一定要是最后一个圆圈!
				HashMap itemMap_first = (HashMap) _ringMap.get(str_firstRing); //
				int li_firstRingLevel = (Integer) itemMap_first.get("level"); //最后一个圆圈的所在层!
				int li_firstRingY = getYposByLevel(li_firstRingLevel, 2); //最后一个圆圈的Y位置!

				String str_lastRing = str_rings[str_rings.length - 1]; //一定要是最后一个圆圈!
				HashMap itemMap = (HashMap) _ringMap.get(str_lastRing); //
				int li_lastRingLevel = (Integer) itemMap.get("level"); //最后一个圆圈的所在层!
				int li_lastRingY = getYposByLevel(li_lastRingLevel, 2); //最后一个圆圈的Y位置!

				if (_endLevel > _beginLevel) { //如果后续结点比前面结点的Y位置大!!
					//如果有偏移,也是三根线,而不是四根,因为向下画时，偏移也是向下,所以就会这样!
					//先清空原来多出的补的内容!
					g2.setColor(Color.WHITE); //
					g2.fillRect(getXposByDay(_endDay, 2) - 30, li_y1, 30, 10); //
					g2.setColor(Color.BLACK); //
					g2.drawLine(getXposByDay(_endDay, 2) - 30, li_y1, getXposByDay(_endDay, 2) - 30, li_firstRingY + 30); //画线!!
					if (_endDay == _beginDay) { //如果是同一天,则应该是直线!
						g2.drawLine(getXposByDay(_endDay, 2) - 30, li_firstRingY + 30, li_x2, li_firstRingY + 30); //画线!!
					} else { //画波浪线
						drawWaveLineH(g2, li_firstRingY + 30, getXposByDay(_endDay, 2) - 30, li_x2);
					}

					g2.drawLine(li_x2, li_firstRingY + 30, li_x2, li_y2 - 15); //画线!!
					drawArrow(g2, li_x2, li_y2 - 15, "向下"); //画向下的箭头!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "上"); //
				} else {
					if (li_moveDigit == 0) { //如果没有偏移则从左边绕过去!
						//要画三根线
						g2.drawLine(getXposByDay(_endDay, 2) - 30, li_y1, getXposByDay(_endDay, 2) - 30, li_lastRingY - 30); //画线!!
						if (_endDay == _beginDay) { //如果同一天，则是直线!
							g2.drawLine(getXposByDay(_endDay, 2) - 30, li_lastRingY - 30, li_x2, li_lastRingY - 30); //画线!!
						} else {
							drawWaveLineH(g2, li_lastRingY - 30, getXposByDay(_endDay, 2) - 30, li_x2);
						}
						g2.drawLine(li_x2, li_lastRingY - 30, li_x2, li_y2 + 15); //画线!!
					} else { //如果有偏移,要从右边绕过去!
						//要画四根线,第一根是短线,是绕过去用的,第二根是长线,是从该位置到最后一个圈处，正好在圈外!
						g2.drawLine(getXposByDay(_endDay, 2), li_y1, getXposByDay(_endDay, 2) + 30, li_y1); //画线!!

						//第二根线!
						g2.drawLine(getXposByDay(_endDay, 2) + 30, li_y1, getXposByDay(_endDay, 2) + 30, li_lastRingY - 30); //画线!!

						//第三根线!原来计算出来实际li_x2与上一个拐点的连接的横线
						g2.drawLine(li_x2, li_lastRingY - 30, getXposByDay(_endDay, 2) + 30, li_lastRingY - 30); //画线!!

						//第四根线
						g2.drawLine(li_x2, li_lastRingY - 30, li_x2, li_y2 + 15); //画线!!
					}

					drawArrow(g2, li_x2, li_y2 + 15, "向上"); //画向下的箭头!
					addLineWayMark(_lineWayMap, "" + _endLevel + "-" + _endDay, "下"); //
				}
			}
		}

		addLineAreaMark(_lineAreaSet, _beginLevel, null, li_moveDigit, _beginDay, _endDay); //
	}

	//在某个纵向上判断是否存在圆圈,如果有，则把这些记录下来!即到时不要穿过这些圆圈!!
	private String[] isDrillOneRing(int _level, int _level2, int _day, HashMap _ringMap, boolean _isAddFirst) { //
		ArrayList list = new ArrayList(); //
		if (_level2 > _level) {
			if (!_isAddFirst) { //如果第一个也不算!
				_level = _level + 1; //
			}
			for (int i = _level; i < _level2; i++) {
				String str_ring = findRing(i, _day, _ringMap); //
				if (str_ring != null) {
					list.add(str_ring); //
				}
			}
		} else {
			if (!_isAddFirst) { //如果第一个也不算!
				_level = _level - 1; //
			}
			for (int i = _level; i > _level2; i--) { //
				String str_ring = findRing(i, _day, _ringMap); //
				if (str_ring != null) {
					list.add(str_ring); //
				}
			}
		}

		if (list.size() > 0) { //如果有圆圈
			return (String[]) list.toArray(new String[0]); //
		} else {
			return null;
		}
	}

	private String[] isDrillOneRing2(int _level, int _beginDay, int _endDay, HashMap _ringMap, boolean _isAddFirst) { //
		ArrayList list = new ArrayList(); //
		if (!_isAddFirst) { //如果第一个也不算!
			_beginDay = _beginDay + 1; //
		}
		for (int i = _beginDay; i < _endDay; i++) {
			String str_ring = findRing(_level, i, _ringMap); //
			if (str_ring != null) {
				list.add(str_ring); //
			}
		}
		if (list.size() > 0) { //如果有圆圈
			return (String[]) list.toArray(new String[0]); //
		} else {
			return null;
		}
	}

	//判断是否已画线??
	private int isAlreadyDrawLine(HashSet _lineAreaSet, int _level, String _dir, int _beginDay, int _endDay) {
		for (int r = 0; r < 6; r++) { //最多12层!!因为一层是20个像素,而一层是120个像素!!
			ArrayList list = new ArrayList(); //
			for (int i = _beginDay; i < _endDay; i++) {
				if (r == 0) {
					list.add(_level + (_dir == null ? "" : _dir) + "-" + i); //
				} else {
					list.add(_level + (_dir == null ? "" : _dir) + "." + r + "-" + i); //
				}
			}
			String[] items = (String[]) list.toArray(new String[0]); //这段范围的记录!
			String[] allItems = (String[]) _lineAreaSet.toArray(new String[0]); //所有记录
			boolean isDrawed = tbUtil.containTwoArrayCompare(items, allItems); //
			if (!isDrawed) { //如果这一层没有发现重叠区域,则说明是可以画的,则返回这一偏移号!
				return r;
			}
		}

		return 1; //如果运气实在不少,则直接返回第一层,即实在重叠也没办法了!
	}

	//画箭头!!有三种类型,1-向右,2-向下,3-向上
	private void drawArrow(Graphics2D _g2, int _x, int _y, String _type) { //
		if (_type.equals("向右")) { //向右
			_g2.fillPolygon(new int[] { _x - 5, _x, _x - 5 }, new int[] { _y - 5, _y, _y + 5 }, 3); //画向右箭头!!
		} else if (_type.equals("向下")) { //向下
			_g2.fillPolygon(new int[] { _x - 5, _x + 5, _x }, new int[] { _y - 5, _y - 5, _y }, 3); //画向下箭头!!
		} else if (_type.equals("向上")) { //向上箭头
			_g2.fillPolygon(new int[] { _x - 5, _x, _x + 5 }, new int[] { _y + 5, _y, _y + 5 }, 3); //画向上箭头!!
		}
	}

	//画一条横向波浪线!
	private void drawWaveLineH(Graphics2D _g2, int _y, int _x1, int _x2) {
		int li_yCount = 0; //
		int li_XItem = 4; //波浪线横向每段跨度!!!
		int li_YItem = 2; //波浪锯齿

		if (_x1 > _x2) { //如果X1竟然大,则交换一下位置!!
			int tmpX = _x1; //
			_x1 = _x2; //
			_x2 = tmpX; //
		}

		while (1 == 1) { //
			int li_newX = _x1 + li_yCount * li_XItem; //
			if (li_newX >= _x2) { //如果越界了则退出循环!!!
				break; //
			}

			int li_y1 = _y; //
			int li_y2 = _y; //
			if (li_yCount == 0) { //如果是第一个
				li_y1 = _y; //
				li_y2 = _y - li_YItem; //
			} else {
				if ((li_yCount % 2) != 0) { //如果是奇数!!
					li_y1 = _y - li_YItem; //
					li_y2 = _y + li_YItem; //
				} else { //偶数!
					li_y1 = _y + li_YItem; //
					li_y2 = _y - li_YItem; //
				}
			}

			int li_endX = li_newX + li_XItem; //
			if (li_endX > _x2) { //如果越界，则直接于边界尺寸!!
				li_endX = _x2; //
				li_y2 = _y; //
			}

			_g2.drawLine(li_newX, li_y1, li_endX, li_y2); //画线!!
			li_yCount++; //
		}
	}

	private int getXposByDay(int _day, int _type) {
		int li_beginPos = 10 + li_oneDayWidth;
		if (_type == 1) { //1-左边,即要减去圆圈的一半!
			return li_beginPos + (_day - 1) * li_oneDayWidth - 15; //
		} else if (_type == 2) { //2-居中,不加也不减
			return li_beginPos + (_day - 1) * li_oneDayWidth; //	
		} else if (_type == 3) {
			return li_beginPos + (_day - 1) * li_oneDayWidth + 15; //	
		} else {
			return li_beginPos + (_day - 1) * li_oneDayWidth; //	
		}
	}

	//根据层计算出Y坐标!!
	private int getYposByLevel(int _level, int _type) {
		int li_beginPos = 125; //前辍高度!
		int li_oneLevelHeight = 120; //一层的高度!
		if (_type == 1) { //上面
			return li_beginPos + ((_level - 1) * li_oneLevelHeight); //
		} else if (_type == 2) { //中间
			return li_beginPos + ((_level - 1) * li_oneLevelHeight) + 15; //
		} else if (_type == 3) {
			return li_beginPos + ((_level - 1) * li_oneLevelHeight) + 30; //
		} else {
			return li_beginPos + ((_level - 1) * li_oneLevelHeight) + 15; //
		}
	}

	public static void main(String[] _args) {
		JFrame frame = new JFrame("测试"); //
		frame.setSize(1000, 700); //
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
		frame.getContentPane().add(new JScrollPane(new GunterPanel(null))); //
		frame.setVisible(true); //显示窗口!
	}

}
