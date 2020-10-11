package cn.com.infostrategy.bs.sysapp.systemfile;

import java.io.File;

public class SystemFileDMO {

	public File[] getSystemFiles(File file){
		if(file == null){
			return File.listRoots();
		}else{
			return file.listFiles();
		}
	}
	
	public boolean hasDirectory(File file){
		File[] files = null;
		if(file != null){
			files = getSystemFiles(file);
			if(files != null && files.length > 0){
				for(int i = 0;i < files.length;i++){
					if(files[i].isDirectory()){
						return true;
					}
				}
			}else{
				return false;
			}
		}
		return false;
	}
}
