package me.yonatan.gwp.ui.editor;

//import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.forms.widgets.ExpandableComposite;
//import org.eclipse.ui.forms.widgets.FormToolkit;
//import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

public class WorkingDirectorySection extends ServerEditorSection {
	@Override
	public void createSection(Composite parent) {
		super.createSection(parent);
		System.out.println("Section");
		
//		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
//		Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
//				| ExpandableComposite.TITLE_BAR);
//		section.setText("Working directory");
//		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

	}
}
