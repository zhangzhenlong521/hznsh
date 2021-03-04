package cn.com.infostrategy.ui.mdata.hmui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.metal.MetalFileChooserUI;

public class I_FileChooserUI extends MetalFileChooserUI {

	public I_FileChooserUI(JFileChooser b) {
		super(b);
	}

	public static I_FileChooserUI createUI(JComponent c) {
		c.setOpaque(false);
		return new I_FileChooserUI((JFileChooser) c);
	}

	protected FilterComboBoxModel1 createFilterComboBoxModel() {
		return new FilterComboBoxModel1();
	}

	protected class FilterComboBoxModel1 extends FilterComboBoxModel implements ComboBoxModel, PropertyChangeListener {
		private static final long serialVersionUID = 7118417568824741943L;
		protected FileFilter[] filters;

		protected FilterComboBoxModel1() {
			super();
			filters = getFileChooser().getChoosableFileFilters();
		}

		public void propertyChange(PropertyChangeEvent e) {
			String prop = e.getPropertyName();
			if (prop == JFileChooser.CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY) {
				filters = (FileFilter[]) e.getNewValue();
				fireContentsChanged(this, -1, -1);
			} else if (prop == JFileChooser.FILE_FILTER_CHANGED_PROPERTY) {
				fireContentsChanged(this, -1, -1);
			}
		}

		public void setFilters(FileFilter[] filters) {
			this.filters = filters;
		}

		public void setSelectedItem(Object filter) {
			if (filter != null) {
				getFileChooser().setFileFilter((FileFilter) filter);
				fireContentsChanged(this, -1, -1);
			}
		}

		public Object getSelectedItem() {
			FileFilter currentFilter = getFileChooser().getFileFilter();
			boolean found = false;
			if (currentFilter != null) {
				for (int i = 0; i < filters.length; i++) {
					if (filters[i] == currentFilter) {
						found = true;
					}
				}
				if (found == false) {
					getFileChooser().addChoosableFileFilter(currentFilter);
				}
			}
			return getFileChooser().getFileFilter();
		}

		public int getSize() {
			if (filters != null) {
				return filters.length;
			} else {
				return 0;
			}
		}

		public Object getElementAt(int index) {
			if (index > getSize() - 1) {
				// This shouldn't happen. Try to recover gracefully.
				return getFileChooser().getFileFilter();
			}
			if (filters != null) {
				return filters[index];
			} else {
				return null;
			}
		}
	}
}
