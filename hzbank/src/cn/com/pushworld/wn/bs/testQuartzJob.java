package cn.com.pushworld.wn.bs;

import java.io.File;

import cn.com.infostrategy.bs.common.WLTJobIFC;

public class testQuartzJob implements WLTJobIFC {

	@Override
	public String run() throws Exception {
		/*File file=new File("D:\\testjob.txt");
		if(file.exists()){
			file.delete();
		}else {
			file.createNewFile();
		}*/
		System.out.println("开始执行任务~~");
		return "执行成功";
	}
}