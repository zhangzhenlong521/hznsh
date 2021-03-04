package cn.com.pushworld.salary.bs.report_;

import java.util.Arrays;
import java.util.Random;

public class BaseUtil {

    /**
	 * �����������
	 */
	public static void randomSortArrays(String[] _arrays) {
		Random random = new Random();
		for (int i = 0; i < _arrays.length; i++) {
			int pos = random.nextInt(_arrays.length);
			String temp = _arrays[i];
			_arrays[i] = _arrays[pos];
			_arrays[pos] = temp;
		}
	}
	
    /**
	 * �������� ������һ������˳��
	 */
	public static void sortArraysByOrderarrays(String[] _arrays, String[] _orderarrays) {
		Arrays.sort(_arrays, new StringComparator(_orderarrays, _arrays.length));
	}

}
