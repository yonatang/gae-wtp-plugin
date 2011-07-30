package me.yonatan.gwp.ui.editor;

//import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
//import org.eclipse.ui.forms.widgets.ExpandableComposite;
//import org.eclipse.ui.forms.widgets.FormToolkit;
//import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

public class WorkingDirectorySection extends ServerEditorSection {
	@Override
	public void createSection(Composite parent) {
		super.createSection(parent);
		System.out.println("Section");

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
				| ExpandableComposite.TITLE_BAR);
		System.err.println(section);
		section.setText("Working directory");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, false));
		Label explanation = toolkit.createLabel(composite, "Working directory of the server");
		GridData d = new GridData();
		d.horizontalSpan = 2;
		explanation.setLayoutData(d);

		Composite inner = toolkit.createComposite(composite);
		inner.setLayout(new GridLayout(1, false));
		toolkit.createButton(inner, "Default", SWT.RADIO);
		toolkit.createButton(inner, "Custom", SWT.RADIO);

		Composite customDirComposite = toolkit.createComposite(inner);
		customDirComposite.setLayout(new GridLayout(3, false));

		Label customDirLabel = toolkit.createLabel(customDirComposite, "Directory");
		final Text customDirText = toolkit.createText(customDirComposite, "");

		Button customDirButton = toolkit.createButton(customDirComposite, "Browse", SWT.PUSH);
		customDirButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog d = new DirectoryDialog(new Shell());
				d.setFilterPath("c:/gae/deploy");
				String x = d.open();
				if (x != null)
					customDirText.setText(x);
				// tempDeployText.setText(page.makeRelative(x));
			}
		});

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
	}
}
