package cn.com.infostrategy.ui.mdata.hmui.ninepatch;

import java.util.HashMap;

public abstract class RawCache<NinePatch> {
	private HashMap<String, NinePatch> rawCache = new HashMap();

	public Object getRaw(String relativePath, Class baseClass) {
		NinePatch ic = null;
		String key = relativePath + baseClass.getCanonicalName();
		if (this.rawCache.containsKey(key)) {
			ic = this.rawCache.get(key);
		} else {
			try {
				ic = getResource(relativePath, baseClass);
				this.rawCache.put(key, ic);
			} catch (Exception e) {
				System.out.println("取本地磁盘资源文件出错,path=" + key + "," + e.getMessage());
				e.printStackTrace();
			}
		}
		return ic;
	}

	protected abstract NinePatch getResource(String paramString, Class paramClass);
}