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

package org.eclipse.papyrus.infra.internationalization.resource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmf.runtime.emf.core.internal.util.EMFCoreConstants;
import org.eclipse.papyrus.infra.internationalization.utils.InternationalizationKeyResolver;
import org.eclipse.papyrus.infra.internationalization.utils.InternationalizationResourceOptionsConstants;

/**
 * The resource factory for the properties files.
 */
@SuppressWarnings("restriction")
public class InternationalizationResourceFactory extends XMIResourceFactoryImpl {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Resource createResource(final URI uri) {

		final XMIResource resource = new InternationalizationResource(uri);

		if (!resource.getEncoding().equals(EMFCoreConstants.XMI_ENCODING)) {
			resource.setEncoding(EMFCoreConstants.XMI_ENCODING);
		}

		final InternationalizationKeyResolver keyResolver = new InternationalizationKeyResolver();
		resource.getDefaultLoadOptions().put(InternationalizationResourceOptionsConstants.LOAD_SAVE_OPTION_KEY_RESOLVER,
				keyResolver);

		// For the properties file with underscore in name, we need to check if
		// a locale is available in the name
		// If this is true, the file name is the name without the locale name
		// Example: projectName_en_US.properties must return projectName
		String fileNameWithoutExtension = uri.trimFileExtension().toString();
		Locale localeFound = null;
		if (fileNameWithoutExtension.contains("_")) {
			// Get the available locales
			final List<Locale> availableLocales = Arrays.asList(Locale.getAvailableLocales());
			String substring = fileNameWithoutExtension;

			// Loop until no underscore in the name or if a locale is found
			while (substring.contains("_") && null == localeFound) { //$NON-NLS-1$
				// Remove the first part of the name to check the possible
				// locale
				substring = substring.substring(substring.indexOf("_") + 1); //$NON-NLS-1$

				final Iterator<Locale> localesIterator = availableLocales.iterator();

				// Loop on available locales
				while (localesIterator.hasNext() && null == localeFound) {
					final Locale currentAvailableLocale = localesIterator.next();

					// The available locale is corresponding to the substring of
					// the file name
					if (currentAvailableLocale.toString().equals(substring)) {
						localeFound = currentAvailableLocale;
						// Get the initial name without the locale as string
						fileNameWithoutExtension = fileNameWithoutExtension.substring(0,
								fileNameWithoutExtension.length() - substring.length() - 1);
					}
				}
			}
		}

		resource.getDefaultLoadOptions().put(InternationalizationResourceOptionsConstants.LOAD_OPTION_URI,
				URI.createFileURI(fileNameWithoutExtension));

		if (null != localeFound) {
			resource.getDefaultLoadOptions().put(InternationalizationResourceOptionsConstants.LOAD_OPTION_LOCALE,
					localeFound);
		}

		return resource;
	}

}
