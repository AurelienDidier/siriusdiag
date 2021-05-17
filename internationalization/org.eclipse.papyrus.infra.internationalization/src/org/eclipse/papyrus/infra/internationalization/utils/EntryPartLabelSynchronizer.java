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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
//import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.internationalization.InternationalizationEntry;
import org.eclipse.papyrus.infra.internationalization.InternationalizationPackage;
import org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
//import org.eclipse.papyrus.infra.nattable.model.nattable.Table;

/**
 * A class taking in charge the synchronization of the partName and the label.
 * When label change, the other is automatically updated.
 */
public class EntryPartLabelSynchronizer {

	/**
	 * The internationalization entry.
	 */
	private InternationalizationEntry entry;

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
	private final Adapter labelListener = new AdapterImpl() {

		/**
		 *
		 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
		 *
		 * @param notification
		 */
		@Override
		public void notifyChanged(final Notification notification) {
			if (notification.getFeature() == InternationalizationPackage.eINSTANCE
					.getInternationalizationEntry_Value()) {
				editorPart.modifyPartName(getLabel(entry.getKey()));
			}
		}
	};

	/**
	 * Constructor.
	 *
	 * @param entry
	 *            The internationalization entry to manage.
	 * @param editorPart
	 *            The editor part to change label.
	 * @param modelResource
	 *            The model resource corresponding to the internationalization.
	 */
	public EntryPartLabelSynchronizer(final InternationalizationEntry entry,
			final IInternationalizationEditor editorPart, final InternationalizationModelResource modelResource) {
		this.editorPart = editorPart;
		this.modelResource = modelResource;
		setEntry(entry);
	}

	/**
	 * Change the associated entry.
	 *
	 * @param entry
	 *            The internationalization entry.
	 */
	public void setEntry(final InternationalizationEntry entry) {
		if (null != entry && null != entry.getKey()) {
			entry.eAdapters().remove(this.labelListener);

			// Set new table internationalization entry
			this.entry = entry;

			// Set editor name
			editorPart.modifyPartName(getLabel(entry.getKey()));

			// Listen to name change
			entry.eAdapters().add(this.labelListener);
		}
	}

	/**
	 * This allows to dispose the synchronizer.
	 */
	public void dispose() {
		this.editorPart = null;
		this.modelResource = null;
		if (null != entry) {
			this.entry.eAdapters().remove(this.labelListener);
		}
	}

	/**
	 * This allows to get the object label from the model resource.
	 *
	 * @param object
	 *            The object to get its label.
	 * @return The object label.
	 */
	protected String getLabel(final Object object)

	{
		String value = ""; //$NON-NLS-1$
		// if (object instanceof Table) {
		// value = getTableLabel((Table) entry.getKey());
		// } else if (object instanceof Diagram) {
		// value = getDiagramLabel((Diagram) entry.getKey());
		// }
		return value;
	}

	/**
	 * This allows to get the table label from the model resource.
	 *
	 * @param table
	 *            The table to get its label (name if label returns
	 *            <code>null</code>).
	 * @return The table label of table name if label returns <code>null</code>.
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
	 * @return The diagram label of diagram name if label returns
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