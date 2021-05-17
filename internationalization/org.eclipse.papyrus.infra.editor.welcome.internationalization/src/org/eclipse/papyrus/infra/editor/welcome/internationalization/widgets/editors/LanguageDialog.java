/*****************************************************************************
 * Copyright (c) 2016 CEA LIST and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Nicolas FAUVERGUE (ALL4TEC) nicolas.fauvergue@all4tec.net - Initial API and implementation
 *   
 *****************************************************************************/

package org.eclipse.papyrus.infra.editor.welcome.internationalization.widgets.editors;

import java.util.Locale;

import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.ui.dialogs.InternationalizationDialog;
import org.eclipse.papyrus.infra.widgets.Activator;
import org.eclipse.papyrus.infra.widgets.editors.AbstractValueEditor;
import org.eclipse.papyrus.infra.widgets.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * This allows to define the language editor.
 */
public class LanguageDialog extends AbstractValueEditor implements SelectionListener {

	/**
	 * The CLabel displaying the current value.
	 */
	protected final CLabel currentValueLabel;

	/**
	 * The Button used to modify the value.
	 */
	protected Button modifyValuesButton;

	/**
	 * The dialog used to select the internationalization value.
	 */
	protected final InternationalizationDialog dialog;

	/**
	 * The current value for this editor
	 */
	protected Object value;

	/**
	 * Boolean to determinate if the editors are read-only.
	 */
	protected boolean readOnly;

	/**
	 * Constructs a new LanguageDialog in the given parent Composite. The style
	 * will be applied to the CLabel displaying the current value.
	 *
	 * @param parent
	 *            The parent composite.
	 * @param style
	 *            The style of the composites.
	 */
	public LanguageDialog(final Composite parent, final int style) {
		super(parent, style);
		final GridData gridData = getDefaultLayoutData();

		currentValueLabel = factory.createCLabel(this, null, factory.getBorderStyle() | style);
		currentValueLabel.setLayoutData(gridData);
		currentValueLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editAction();
			}

		});

		dialog = createDialog(parent.getShell());

		createButton();
		updateControls();
		controlDecoration = new ControlDecoration(currentValueLabel, SWT.TOP | SWT.LEFT);

		gridData.horizontalIndent = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();
	}

	/**
	 * This allows to create the internationalization language dialog.
	 * 
	 * @param shell
	 *            The parent shell.
	 * @return The created dialog.
	 */
	protected InternationalizationDialog createDialog(final Shell shell) {
		return new InternationalizationDialog(shell, null, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.widgets.editors.AbstractValueEditor#setModelObservable(org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void setModelObservable(final IObservableValue modelProperty) {
		super.setModelObservable(modelProperty);

		if(InternationalizationPreferencesUtils.isInternationalizationNeedToBeLoaded()) {
			if (modelProperty instanceof IObserving) {
				Object observed = ((IObserving) modelProperty).getObserved();
				if (observed instanceof URI) {
					URI resourceURI = (URI) observed;
					dialog.setResourceURI(resourceURI);
	
					final String localeAsString = InternationalizationPreferencesUtils.getLocalePreference(resourceURI).toString();
					currentValueLabel.setText(localeAsString);
				}
			}
		}
	}

	/**
	 * This allow to create the buttons.
	 */
	protected void createButton() {
		((GridLayout) getLayout()).numColumns += 1;

		modifyValuesButton = factory.createButton(this, null, SWT.PUSH);
		modifyValuesButton.setImage(Activator.getDefault().getImage("/icons/Edit_12x12.gif")); //$NON-NLS-1$
		modifyValuesButton.setToolTipText(Messages.ReferenceDialog_EditValue);
		modifyValuesButton.addSelectionListener(this);
	}

	/**
	 * The action executed when the "edit" button is selected Edits the object
	 * that is currently selected
	 */
	protected void editAction() {
		int result = dialog.open();
		if (Window.OK == result) {
			final Locale newValue = dialog.getLocaleValue();
			if (null != newValue) {
				setValue(newValue.toString());
			}
		}
	}

	/**
	 * This allows to update the controls.
	 */
	protected void updateControls() {
		modifyValuesButton.setEnabled(!readOnly);
	}

	/**
	 * This allows to update the label text.
	 */
	protected void updateLabel() {
		currentValueLabel.setText((String) getValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.widgets.editors.AbstractValueEditor#getValue()
	 */
	@Override
	public Object getValue() {
		if (modelProperty != null) {
			return modelProperty.getValue();
		}
		return value;
	}

	/**
	 * Set the value.
	 * 
	 * @param value
	 *            The value to set.
	 */
	@SuppressWarnings("unchecked")
	protected void setValue(final Object value) {
		this.value = value;
		try {
			if (modelProperty != null) {
				modelProperty.setValue(value);
			}
		} catch (Exception e) {
			// Do nothing
		}

		updateLabel();
		commit();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.widgets.editors.AbstractEditor#getEditableType()
	 */
	@Override
	public Object getEditableType() {
		return Object.class;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.widgets.editors.AbstractEditor#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly || !InternationalizationPreferencesUtils.isInternationalizationNeedToBeLoaded();
		updateControls();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.widgets.editors.AbstractEditor#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {
		Widget widget = e.widget;
		if (widget == modifyValuesButton) {
			editAction();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Do nothing
	}

}
