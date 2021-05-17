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
package org.eclipse.papyrus.infra.internationalization.internal.readonly;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.papyrus.infra.core.resource.AbstractReadOnlyHandler;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.ReadOnlyAxis;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationPreferenceModel;
import org.eclipse.papyrus.infra.internationalization.utils.PropertiesFilesUtils;

import com.google.common.base.Optional;

/**
 * A ReadOnlyHandler for the Sash Model (*.internationalization or *.notation).
 *
 * If the model is located in the user preferences space, it may be considered
 * read-only by other read-only handlers, whereas it shouldn't.
 *
 * Its priority should be greater than EMFReadOnlyHandler, FSReadOnlyHandler and
 * ...
 *
 * This handler is discretion-based.
 *
 */
public class InternationalizationPropertiesReadOnlyHandler extends AbstractReadOnlyHandler {

	/**
	 * The model set.
	 */
	private ModelSet modelSet;

	/**
	 * Constructor.
	 *
	 * @param editingDomain
	 *            The current editing domain.
	 */
	public InternationalizationPropertiesReadOnlyHandler(final EditingDomain editingDomain) {
		super(editingDomain);

		if (getEditingDomain().getResourceSet() instanceof ModelSet) {
			modelSet = (ModelSet) getEditingDomain().getResourceSet();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.IReadOnlyHandler2#anyReadOnly(java.util.Set,
	 *      org.eclipse.emf.common.util.URI[])
	 */
	@Override
	public Optional<Boolean> anyReadOnly(final Set<ReadOnlyAxis> axes, final URI[] uris) {
		if ((null == modelSet) || !axes.contains(ReadOnlyAxis.DISCRETION)) {
			return Optional.absent();
		}

		// Only answer false if all of the resources in question are ones that
		// we know must be allowed to be written
		int knownWritableCount = 0;
		for (URI uri : uris) {
			if (InternationalizationPreferenceModel.INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION
					.equals(uri.fileExtension())
					|| InternationalizationPreferenceModel.NOTATION_FILE_EXTENSION.equals(uri.fileExtension())
					|| (uri.isPlatform() && PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION.equals(uri.fileExtension()))) {
				knownWritableCount++;
			}
		}

		return (knownWritableCount == uris.length) ? Optional.of(false) : Optional.<Boolean>absent();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.IReadOnlyHandler2#makeWritable(java.util.Set,
	 *      org.eclipse.emf.common.util.URI[])
	 */
	@Override
	public Optional<Boolean> makeWritable(final Set<ReadOnlyAxis> axes, final URI[] uris) {
		return Optional.absent(); // If the file is read-only, it can probably
									// made writable by other read-only handlers
									// (e.g. FSReadOnlyHandler).
	}

}
