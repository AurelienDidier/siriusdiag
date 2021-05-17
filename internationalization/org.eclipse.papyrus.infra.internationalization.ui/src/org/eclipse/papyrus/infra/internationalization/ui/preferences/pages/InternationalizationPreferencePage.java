/*****************************************************************************
 * Copyright (c) 2017 CEA LIST and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Initial API and implementation
 *   
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.ui.preferences.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.papyrus.infra.internationalization.common.Activator;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.ui.messages.Messages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The internationalization preference page.
 */
public class InternationalizationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 */
	public InternationalizationPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		// Nothing
	}

	/**
	 * Creates the field editors.
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(InternationalizationPreferencesConstants.LOAD_INTERNATIONALIZATION, Messages.InternationalizationPreferencePage_LoadInternationalizationFieldName, getFieldEditorParent()));
		addField(
				new BooleanFieldEditor(InternationalizationPreferencesConstants.LOAD_INTERNATIONALIZATION_OF_EXTERNAL_FILES, Messages.InternationalizationPreferencePage_LoadInternationalizationOfExternalFilesFieldName, getFieldEditorParent()));
	}

}
