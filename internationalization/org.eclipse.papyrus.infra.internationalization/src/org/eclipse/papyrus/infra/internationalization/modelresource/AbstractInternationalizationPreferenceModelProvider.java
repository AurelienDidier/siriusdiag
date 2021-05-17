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

package org.eclipse.papyrus.infra.internationalization.modelresource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.sasheditor.ISashModelProvider;

/**
 * A partial implementation of the {@link ISashModelProvider} protocol.
 */
public abstract class AbstractInternationalizationPreferenceModelProvider
		implements IInternationalizationPreferenceModelProvider {

	/**
	 * The model set managed.
	 */
	private ModelSet modelSet;

	/**
	 * Constructor.
	 */
	public AbstractInternationalizationPreferenceModelProvider() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.internationalization.modelresource.IInternationalizationPreferenceModelProvider#initialize(org.eclipse.papyrus.infra.core.resource.ModelSet)
	 */
	@Override
	public void initialize(final ModelSet modelSet) {
		this.modelSet = modelSet;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.internationalization.modelresource.IInternationalizationPreferenceModelProvider#dispose()
	 */
	@Override
	public void dispose() {
		modelSet = null;
	}

	/**
	 * Get the model set.
	 * 
	 * @return The model set.
	 */
	protected ModelSet getModelSet() {
		return modelSet;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.internationalization.modelresource.IInternationalizationPreferenceModelProvider#loadInternationalizationPreferenceModel(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Resource loadInternationalizationPreferenceModel(final URI internationalizationPrefModelURI) {
		return modelSet.getResource(internationalizationPrefModelURI, true);
	}

}
