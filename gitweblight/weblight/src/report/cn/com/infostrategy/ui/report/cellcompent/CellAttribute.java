/*
 * (swing1.1beta3)
 * 
 */

package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.Dimension;
import java.io.Serializable;

/**
 * @version 1.0 11/22/98
 */

public interface CellAttribute extends Serializable {

	public void addColumn();

	public void addRow();

	public void insertRow(int row);

	public Dimension getSize();

	public void setSize(Dimension size);

}
