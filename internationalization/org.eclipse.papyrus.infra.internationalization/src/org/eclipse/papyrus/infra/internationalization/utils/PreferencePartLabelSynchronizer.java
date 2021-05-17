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

package org.eclipse.papyrus.infra.internationalization.utils;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;

/**
 * A class taking in charge the synchronization of the partName and the label.
 * When label change, the other is automatically updated.
 */
public class PreferencePartLabelSynchronizer {

	/**
	 * The preference store.
	 */
	private IPreferenceStore preferenceStore;

	/**
	 * The EObject corresponding to the graphical editor part.
	 */
	private EObject graphicalEObject;

	/**
	 * The corresponding editor part (implementing
	 * {@link IInternationalizationEditor} for the internationalization).
	 */
	private IInternationalizationEditor editorPart;

	/**
	 * The {@link internationalizationModelResource} to get the labels needed.
	 */
	private InternationalizationModelResource modelResource;

	/**
	 * Listener on label change.
	 */
	private final IPropertyChangeListener propertyListener = new IPropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if (event.getProperty().equals(InternationalizationPreferencesConstants.USE_INTERNATIONALIZATION_PREFERENCE)
					|| event.getProperty().equals(InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE)) {
				// Modify the part name
				editorPart.modifyPartName(getLabel(graphicalEObject));
				// Refresh the editor part
				editorPart.refreshEditorPart();
			}
		}
	};

	/**
	 * Constructor.
	 *
	 * @param preferenceStore
	 *            The preference store to manage.
	 * @param graphicalEObject
	 *            The EObject corresponding to the owner of the editor part.
	 * @param editorPart
	 *            The editor part to change label.
	 * @param modelResource
	 *            The model resource corresponding to the internationalization.
	 */
	public PreferencePartLabelSynchronizer(final IPreferenceStore preferenceStore, final EObject graphicalEObject,
			final IInternationalizationEditor editorPart, final InternationalizationModelResource modelResource) {
		this.editorPart = editorPart;
		this.modelResource = modelResource;
		this.graphicalEObject = graphicalEObject;
		setPreferenceStore(preferenceStore);
	}

	/**
	 * Change the associated entry.
	 *
	 * @param preferenceStore
	 *            The preference store
	 */
	public void setPreferenceStore(final IPreferenceStore preferenceStore) {
		if (null != preferenceStore) {
			preferenceStore.removePropertyChangeListener(this.propertyListener);

			// Set new table internationalization entry
			this.preferenceStore = preferenceStore;

			// Set editor name
			editorPart.modifyPartName(getLabel(graphicalEObject));

			// Listen to name change
			preferenceStore.addPropertyChangeListener(propertyListener);
		}
	}

	/**
	 * This allows to dispose the synchronizer.
	 */
	public void dispose() {
		this.editorPart = null;
		this.modelResource = null;
		if (null != preferenceStore) {
			this.preferenceStore.removePropertyChangeListener(this.propertyListener);
		}
	}

	/**
	 * This allows to get the object label from the model resource.
	 *
	 * @param object
	 *            The object to get its label.
	 * @return The object label.
	 */
	protected String getLabel(final EObject object) {
		String value = ""; //$NON-NLS-1$
		// if (object instanceof Table) {
		// value = getTableLabel((Table) object);
		// } else if (object instanceof Diagram) {
		// value = getDiagramLabel((Diagram) object);
		// }
		return value;
	}

	/**
	 * This allows to get the table label from the model resource.
	 *
	 * @param table
	 *            The table to get its label (name if label returns
	 *            <code>null</code>).
	 * @return The table label or table name if label returns <code>null</code>.
	 */
	// protected String getTableLabel(final Table table) {
	// String result = null;
	// EObject tableOwner = table.getOwner();
	// if(null == tableOwner){
	// tableOwner = table.getContext();
	// }
	// if (InternationalizationPreferencesUtils.getInternationalizationPreference(tableOwner)) {
	// result = modelResource.getValueForEntryKey(tableOwner.eResource().getURI(), table);
	// }
	// return null != result ? result : table.getName();
	// }

	/**
	 * This allows to get the diagram label from the model resource.
	 *
	 * @param diagram
	 *            The diagram to get its label (name if label returns
	 *            <code>null</code>).
	 * @return The diagram label or diagram name if label returns
	 *         <code>null</code>.
	 */
	// protected String getDiagramLabel(final Diagram diagram) {
	// String result = null;
	// if (InternationalizationPreferencesUtils.getInternationalizationPreference(QualifiedNameUtils.getOwner(diagram))) {
	// result = modelResource.getValueForEntryKey(diagram.eResource().getURI(), diagram);
	// }
	// return null != result ? result : diagram.getName();
	// }
}