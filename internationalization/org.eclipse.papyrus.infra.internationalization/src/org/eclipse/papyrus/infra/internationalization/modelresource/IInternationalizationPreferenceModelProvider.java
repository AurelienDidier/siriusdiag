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

/**
 * This is the IInternationalizationPreferenceModelProvider type.
 */
public interface IInternationalizationPreferenceModelProvider {

	/**
	 * Get the internationalization preference model URI from the user model
	 * URI.
	 * 
	 * @param userModelURI
	 *            The initial user model URI.
	 * @return The internationalization preference model URI.
	 */
	public URI getInternationalizationPreferenceModelURI(final URI userModelURI);

	/**
	 * This allows to initialize the model provider.
	 * 
	 * @param modelSet
	 *            The current model set.
	 */
	public void initialize(final ModelSet modelSet);

	/**
	 * This allows to load the internationalization preference model.
	 * 
	 * @param internationalizationPrefModelURI
	 *            The internationalization preference model URI.
	 * @return The loaded resource.
	 */
	public Resource loadInternationalizationPreferenceModel(final URI internationalizationPrefModelURI);

	/**
	 * This allows to manage the dispose of the model provider.
	 */
	public void dispose();
}
