package com.dubture.doctrine.ui.dialog;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

import com.dubture.doctrine.ui.actions.GenerateGettersHandler.GetterSetterEntry;

/**
 * 
 * 
 * Dialog for generating getter and setters.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class GetterSetterDialog extends CheckedTreeSelectionDialog {
	
	private final IType type;	
	private int visibility;
	
	private static class ExistingGetterSetterFilter extends ViewerFilter {

		private final IType type;
		
		public ExistingGetterSetterFilter(IType type) {			
			super();
			this.type = type;
			
			
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			if (element instanceof GetterSetterEntry) {
			
				GetterSetterEntry entry = (GetterSetterEntry) element;
				
				try {
					for (IMethod method : type.getMethods()) {
				
						if (entry.isGetter) {							
							if (method.getElementName().compareToIgnoreCase("get" + entry.getRawFieldName()) == 0) {
								return false;
							}
						} else {
							if (method.getElementName().compareToIgnoreCase("set" + entry.getRawFieldName()) == 0) {
								return false;
							}							
						}
					}
				} catch (ModelException e) {
					e.printStackTrace();
				}				
			}
			return true;
		}		
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Control control = super.createDialogArea(parent);
		
		Group group = new Group(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
        group.setLayoutData(gd);
		group.setText("Access modifiers");
		GridLayout layout = new GridLayout();
        layout.makeColumnsEqualWidth = true;
        layout.numColumns = 4;
        group.setLayout(layout);		
		
		 String[] labels = new String[] {
                 "public",
                 "protected",
                 "private", };
		 
         Integer[] data = new Integer[] { new Integer(Modifiers.AccPublic),
                 new Integer(Modifiers.AccProtected),
                 new Integer(Modifiers.AccPrivate) };
         
         
         visibility = Modifiers.AccPublic;
         Integer initialVisibility = new Integer(visibility);
         
         for (int i = 0; i < labels.length; i++) {
        	 
             Button radio = new Button(group, SWT.RADIO);
             Integer visibilityCode = data[i];
             radio.setLayoutData(new GridData(
                     GridData.HORIZONTAL_ALIGN_FILL));

             radio.setText(labels[i]);
             radio.setData(visibilityCode);             
             radio.setSelection(visibilityCode.equals(initialVisibility));
             
             radio.addSelectionListener(new SelectionAdapter() {
                 public void widgetSelected(SelectionEvent event) {                	 
                	 visibility = ((Integer) event.widget.getData()).intValue();
                 }
             });
         }		
         
		return control;		
		
	}
	
	
	public GetterSetterDialog(Shell parent, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider, IType type) {
		super(parent, labelProvider, contentProvider);
		
		this.type = type;

	}

	
	
	
	@Override
	protected CheckboxTreeViewer createTreeViewer(Composite parent) {
	
		CheckboxTreeViewer viewer = super.createTreeViewer(parent);		
		viewer.addFilter(new ExistingGetterSetterFilter(type));
		
		viewer.expandAll();
		return viewer;
		
	}


	public int getModifier() {

		return visibility;

	}
}