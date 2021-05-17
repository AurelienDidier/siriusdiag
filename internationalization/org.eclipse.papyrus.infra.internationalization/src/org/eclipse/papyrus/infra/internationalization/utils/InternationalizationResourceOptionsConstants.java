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
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Initial API and implementation
 *   
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.utils;

/**
 * This defines the internationalization options constants available for the
 * internationalization resource load and save.
 */
public class InternationalizationResourceOptionsConstants {

	/**
	 * The deleted objects save option which can be used to not serialize the
	 * objects in this set.
	 */
	public static final String SAVE_OPTION_DELETED_OBJECTS = "internationalizationDeletedObjects"; //$NON-NLS-1$

	/**
	 * The sort save option to determinate if the properties entries must be
	 * sort.
	 */
	public static final String SAVE_OPTION_SORT = "internationalizationSort"; //$NON-NLS-1$

	/**
	 * The key resolver option which allow to calculate the properties entries
	 * keys.
	 */
	public static final String LOAD_SAVE_OPTION_KEY_RESOLVER = "internationalizationKeyResolver"; //$NON-NLS-1$
	
	/**
	 * The locale option to store the locale file to load.
	 */
	public static final String LOAD_OPTION_LOCALE = "internationalizationLocale"; //$NON-NLS-1$
	
	/**
	 * The uri of the resource without locale extension.
	 */
	public static final String LOAD_OPTION_URI = "internationalizationURI"; //$NON-NLS-1$
	
	/**
	 * The load option to determinate if the create content must be added by unsafe transaction or not.
	 */
	public static final String LOAD_OPTION_UNSAFE_ADD_CONTENT = "unsafeAddCommand"; //$NON-NLS-1$
	
	/**
	 * The load option to determinate if the create content must be added by unsafe transaction or not.
	 */
	public static final String LOAD_SAVE_OPTION_RESOURCE_CONTENT = "resourceContent"; //$NON-NLS-1$

}
