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

package org.eclipse.papyrus.infra.internationalization.common.utils;

import java.util.Locale;

/**
 * This allows to manage easily name for the locale.
 */
public class LocaleNameResolver {

	/**
	 * The underscore constant.
	 */
	public static final String UNDERSCORE = "_"; //$NON-NLS-1$

	/**
	 * This allows to get the corresponding locale from a text.
	 * 
	 * @param initialString
	 *            The initial string representing a locale.
	 * @return The locale corresponding locale from the text in parameter.
	 */
	public static final Locale getLocaleFromString(final String initialString) {
		String language = ""; //$NON-NLS-1$
		String country = ""; //$NON-NLS-1$
		String variant = ""; //$NON-NLS-1$

		if (initialString.contains(UNDERSCORE)) {
			language = initialString.substring(0, initialString.indexOf(UNDERSCORE));
			final String substringFirstUnderscore = initialString.substring(initialString.indexOf(UNDERSCORE) + 1,
					initialString.length());
			if (substringFirstUnderscore.contains(UNDERSCORE)) {
				country = substringFirstUnderscore.substring(0, substringFirstUnderscore.indexOf(UNDERSCORE));
				variant = substringFirstUnderscore.substring(substringFirstUnderscore.indexOf(UNDERSCORE) + 1,
						substringFirstUnderscore.length());
			} else {
				country = substringFirstUnderscore;
			}
		} else {
			language = initialString;
		}

		return new Locale(language, country, variant);
	}
}
