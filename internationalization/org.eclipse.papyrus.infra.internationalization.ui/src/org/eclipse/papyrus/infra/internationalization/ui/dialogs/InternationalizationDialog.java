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

package org.eclipse.papyrus.infra.internationalization.ui.dialogs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.ui.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * The dialog to manage the internationalization configuration. This will
 * contains: - Checkbox to determinate if the internationalization must be used
 * or not - Viewer to select the language of the internationalization
 */
public class InternationalizationDialog extends TrayDialog {

	/**
	 * The resource URI corresponding to the papyrus model. This is needed to
	 * get the preference of the papyrus model.
	 */
	private URI resourceURI;

	/**
	 * The viewer for the language selection.
	 */
	protected TableViewer tableViewer;

	/**
	 * The widget to manage the use internationalization value.
	 */
	protected Button useInternationalizationButton;

	/**
	 * Keep the internationalization use value after the dialog closed.
	 */
	protected boolean useInternationalizationValue;

	/**
	 * Keep the locale value after the dialog closed.
	 */
	protected Locale localeValue;

	/**
	 * Boolean to determinate if the use internationalization must be displayed.
	 */
	private boolean displayUse;

	/**
	 * Constructor.
	 *
	 * @param parentShell
	 *            The parent shell.
	 * @param resourceURI
	 *            The resource URI corresponding to the papyrus model (need to
	 *            get preference of the papyrus model).
	 * @param displayUse
	 *            Boolean to determinate if the use internationalization must be
	 *            displayed.
	 */
	public InternationalizationDialog(final Shell parentShell, final URI resourceURI, final boolean displayUse) {
		super(parentShell);
		setResourceURI(resourceURI);
		this.displayUse = displayUse;
	}

	/**
	 * This allows to set the resource URI.
	 * 
	 * @param resourceURI
	 *            The resource URI to set.
	 */
	public void setResourceURI(final URI resourceURI) {
		this.resourceURI = resourceURI;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 300;
		data.heightHint = 300;
		composite.setLayoutData(data);

		getShell().setText("Configure internationalization"); //$NON-NLS-1$
		getShell().setImage(org.eclipse.papyrus.infra.widgets.Activator.getDefault().getImage("/icons/papyrus.png")); //$NON-NLS-1$

		if (displayUse()) {
			createUseInternationalization(composite);
		}
		createLanguage(composite);
		initializeValues();

		return composite;
	}

	/**
	 * Determinates if the use internationalization checkbox must be displayed.
	 * 
	 * @return <code>true</code> if this will be displayed, <code>false</code>
	 *         otherwise.
	 */
	protected boolean displayUse() {
		return displayUse;
	}

	/**
	 * This allows to create the checkbox which manage the use
	 * internationalization button.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	protected void createUseInternationalization(final Composite parent) {
		useInternationalizationButton = new Button(parent, SWT.CHECK);
		useInternationalizationButton.setText(Messages.InternationalizationDialog_UseInternationalizationLabel);
	}

	/**
	 * This allows to create the viewer to select the language which be used for
	 * the internationalization.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	protected void createLanguage(final Composite parent) {
		// Create the viewer and the grid data
		tableViewer = new TableViewer(parent, SWT.BORDER);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumHeight = 200;
		data.minimumWidth = 100;
		tableViewer.getTable().setLayoutData(data);

		// Set its label and content providers
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());

		// Set the input with the available locales
		final List<Locale> availableLocales = Arrays.asList(Locale.getAvailableLocales());
		availableLocales.sort(new Comparator<Locale>() {

			@Override
			public int compare(final Locale arg0, final Locale arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}

		});
		tableViewer.setInput(availableLocales);

		// Add a double click listener to close the dialog on double click
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				setReturnCode(OK);
				close();
			}
		});
	}

	/**
	 * This allows to initialize the values of fields.
	 */
	protected void initializeValues() {
		if (displayUse()) {
			// Manage the selection change for the internationalization use
			// button
			useInternationalizationButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					useInternationalizationValue = useInternationalizationButton.getSelection();
				}
			});
			// Initialize the boolean value from the preference of papyrus model
			final boolean useInternationalization = InternationalizationPreferencesUtils
					.getInternationalizationPreference(resourceURI);
			useInternationalizationButton.setSelection(useInternationalization);
			useInternationalizationValue = useInternationalization;
		}

		// Manage the selection change for locale
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ISelection selection = event.getSelection();
				final Locale localeSelected = (Locale) ((StructuredSelection) selection).getFirstElement();
				localeValue = localeSelected;
			}
		});
		// Initialize the locale value from the preference of papyrus model
		final Locale locale = InternationalizationPreferencesUtils.getLocalePreference(resourceURI);
		tableViewer.setSelection(new StructuredSelection(locale));
		tableViewer.reveal(locale);
	}

	/**
	 * Get the use internationalization button value.
	 * 
	 * @return The use internationalization button value.
	 */
	public boolean getUseInternationalizationValue() {
		return useInternationalizationValue;
	}

	/**
	 * Get the locale value to use for the internationalization.
	 * 
	 * @return The locale value.
	 */
	public Locale getLocaleValue() {
		return localeValue;
	}
}
