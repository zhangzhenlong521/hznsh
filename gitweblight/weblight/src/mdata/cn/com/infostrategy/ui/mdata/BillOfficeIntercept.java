package cn.com.infostrategy.ui.mdata;

/**
 * 有些需求需要在点击了某按钮后执行一些逻辑
 * 先只加打印按钮
 * @author Administrator
 *
 */
public abstract class BillOfficeIntercept {
	/**
	 * 保存点击后
	 */
	public abstract void afterSave(BillOfficePanel bf);
	/**
	 * 打印点击后
	 */
	public abstract void print(BillOfficePanel bf);

	/**
	 * 全打点击后
	 */
	public abstract void printAll(BillOfficePanel bf);

	/**
	 * 套打点击后
	 */
	public abstract void printTao(BillOfficePanel bf);

	/**
	 * 分打点击后
	 */
	public abstract void printFen(BillOfficePanel bf);
	
	/**
	 * 文档打开后
	 * @param bf
	 */
	public abstract void afterDocumenComplet(BillOfficePanel bf);

}
