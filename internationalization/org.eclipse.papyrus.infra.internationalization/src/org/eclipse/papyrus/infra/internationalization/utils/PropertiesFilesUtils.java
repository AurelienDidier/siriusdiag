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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.osgi.framework.Bundle;

/**
 * This needed methods was copied from UML2Util because we can't use it (because of visibility).
 */
public class PropertiesFilesUtils {
	
	/**
	 * The default URI converter for resource bundle look-ups.
	 */
	protected static final URIConverter DEFAULT_URI_CONVERTER = new ExtensibleURIConverterImpl();

	/**
	 * A cache of resource bundles.
	 */
	protected static final Map<URI, Map<Locale, ResourceBundleAndURI>> RESOURCE_BUNDLES = Collections
			.synchronizedMap(new WeakHashMap<URI, Map<Locale, ResourceBundleAndURI>>());
	
	/**
	 * The empty string.
	 */
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * The platform line separator.
	 */
	protected static final String LINE_SEPARATOR = System.getProperty(Platform.PREF_LINE_SEPARATOR);

	/**
	 * The scheme for platform URIs.
	 */
	public static final String URI_SCHEME_PLATFORM = "platform"; //$NON-NLS-1$

	/**
	 * The first segment for platform plugin URIs.
	 */
	public static final String URI_SEGMENT_PLUGIN = "plugin"; //$NON-NLS-1$

	/**
	 * The first segment for platform resource URIs.
	 */
	public static final String URI_SEGMENT_RESOURCE = "resource"; //$NON-NLS-1$

	/**
	 * The standard extension for properties files.
	 */
	public static final String PROPERTIES_FILE_EXTENSION = "properties"; //$NON-NLS-1$

	/**
	 * Retrieves the candidate resource bundle URIs based on the specified base
	 * URI and base segment in the specified locale.
	 * 
	 * @param baseURI
	 *            The base URI (i.e. without the last segment) for the candidate
	 *            resource bundle URIs.
	 * @param locale
	 *            The locale within which to base the candidate resource bundle
	 *            URIs.
	 * @param baseSegment
	 *            The base segment (i.e. the last segment without the extension)
	 *            for the candidate resource bundle URIs.
	 * @return The candidate resource bundle URIs with the base URI and base
	 *         segment in the locale.
	 */
	protected static List<URI> getResourceBundleURIs(final URI baseURI, final Locale locale, final String initialBaseSegment) {
		String baseSegment = initialBaseSegment;
		final List<URI> resourceBundleURIs = new ArrayList<URI>();
		final String language = locale.getLanguage();

		if (language.length() > 0) {
			baseSegment += ('_' + language);
			resourceBundleURIs.add(0,
					baseURI.appendSegment(baseSegment).appendFileExtension(PROPERTIES_FILE_EXTENSION));

			String country = locale.getCountry();

			if (country.length() > 0) {
				baseSegment += ('_' + country);
				resourceBundleURIs.add(0,
						baseURI.appendSegment(baseSegment).appendFileExtension(PROPERTIES_FILE_EXTENSION));

				String variant = locale.getVariant();

				if (variant.length() > 0) {
					baseSegment += ('_' + variant);
					resourceBundleURIs.add(0,
							baseURI.appendSegment(baseSegment).appendFileExtension(PROPERTIES_FILE_EXTENSION));
				}
			}
		}

		return resourceBundleURIs;
	}

	/**
	 * Retrieves the candidate resource bundle URIs for the specified URI in the
	 * specified locale (if specified).
	 * 
	 * @param uri
	 *            The URI upon which to base the candidate resource bundle URIs.
	 * @param locale
	 *            The locale within which to base the candidate resource bundle
	 *            URIs, or <code>null</code>.
	 * @return The candidate resource bundle URIs for the URI in the locale (if
	 *         specified).
	 */
	protected static List<URI> getResourceBundleURIs(final URI uri, final Locale locale) {
		final List<URI> resourceBundleURIs = new ArrayList<URI>();
		final URI baseURI = uri.trimSegments(1);
		final String baseSegment = uri.trimFileExtension().lastSegment();

		if (null != baseSegment) {
			resourceBundleURIs.add(baseURI.appendSegment(baseSegment).appendFileExtension(PROPERTIES_FILE_EXTENSION));

			if (null != locale && !locale.toString().isEmpty()) {
				resourceBundleURIs.addAll(0, getResourceBundleURIs(baseURI, locale, baseSegment));
			}
		}

		return resourceBundleURIs;
	}

	/**
	 * Retrieves the (cached) resource bundle for the specified object,
	 * localized in the default locale if indicated.
	 * 
	 * @param eObject
	 *            The object for which to retrieve the resource bundle.
	 * @param localize
	 *            Whether to retrieve the resource bundle based on (the default)
	 *            locale.
	 * @return The resource bundle for the object (in the default locale).
	 */
	public static ResourceBundleAndURI getResourceBundle(final EObject eObject, final boolean localize) {
		return getResourceBundle(eObject, localize ? Locale.getDefault() : null);
	}

	/**
	 * Retrieves the (cached) resource bundle for the specified object in the
	 * specified locale (if specified).
	 * 
	 * @param eObject
	 *            The object for which to retrieve the resource bundle.
	 * @param locale
	 *            The locale in which to retrieve the resource bundle, or
	 *            <code>null</code>.
	 * @return The resource bundle for the object in the locale (if specified).
	 */
	public static ResourceBundleAndURI getResourceBundle(final EObject eObject, final Locale locale) {
		final Resource resource = eObject.eResource();

		if (null != resource) {
			return getResourceBundle(resource.getURI(), locale);
		}
		return null;
	}

	/**
	 * Retrieves the (cached) resource bundle for the specified object,
	 * localized in the default locale if indicated.
	 * 
	 * @param uri
	 *            The URI of the object to manage.
	 * @param localize
	 *            Whether to retrieve the resource bundle based on (the default)
	 *            locale.
	 * @return The resource bundle for the object (in the default locale).
	 */
	public static ResourceBundleAndURI getResourceBundle(final URI uri, final boolean localize) {
		return getResourceBundle(uri, localize ? Locale.getDefault() : null);
	}

	/**
	 * Retrieves the (cached) resource bundle for the specified object in the
	 * specified locale (if specified).
	 * 
	 * @param uri
	 *            The URI of the object to manage.
	 * @param locale
	 *            The locale in which to retrieve the resource bundle, or
	 *            <code>null</code>.
	 * @return The resource bundle for the object in the locale (if specified).
	 */
	public static ResourceBundleAndURI getResourceBundle(final URI uri, final Locale locale) {
		if (null != uri) {
			Map<Locale, ResourceBundleAndURI> resourceBundles = RESOURCE_BUNDLES.get(uri);

			if (null == resourceBundles) {
				resourceBundles = Collections.synchronizedMap(new HashMap<Locale, ResourceBundleAndURI>());
				RESOURCE_BUNDLES.put(uri, resourceBundles);
			}

			if (!resourceBundles.containsKey(locale)) {
				URIConverter uriConverter = DEFAULT_URI_CONVERTER;

				List<URI> resourceBundleURIs = getResourceBundleURIs(uri, locale);

				if (EMFPlugin.IS_ECLIPSE_RUNNING) {
					URI normalizedURI = uriConverter.normalize(uri);
					int segmentCount = normalizedURI.segmentCount();

					if (URI_SCHEME_PLATFORM.equals(normalizedURI.scheme()) && segmentCount > 2
							&& URI_SEGMENT_PLUGIN.equals(normalizedURI.segment(0))) {

						Bundle bundle = Platform.getBundle(normalizedURI.segment(1));

						if (null != bundle) {
							Bundle[] fragments = Platform.getFragments(bundle);

							if (null != fragments) {
								String[] trailingSegments = normalizedURI.segmentsList().subList(2, segmentCount)
										.toArray(new String[] {});

								for (int f = 0; f < fragments.length; f++) {
									resourceBundleURIs.addAll(0,
											getResourceBundleURIs(normalizedURI.trimSegments(segmentCount - 1)
													.appendSegment(fragments[f].getSymbolicName())
													.appendSegments(trailingSegments), locale));
								}
							}
						}
					}
				}

				ResourceBundle resourceBundle = null;
				ResourceBundleAndURI resourceBundleAndURI = null;

				for (final Iterator<URI> rbu = resourceBundleURIs.iterator(); rbu.hasNext();) {

					try {
						final URI nextURI = rbu.next();
						InputStream inputStream = uriConverter.createInputStream(nextURI);
						try {
							resourceBundle = new PropertyResourceBundle(inputStream);
							resourceBundleAndURI = new ResourceBundleAndURI(resourceBundle, nextURI);
						} finally {
							inputStream.close();
						}
						break;
					} catch (IOException ioe) {
						// ignore
					}
				}

				resourceBundles.put(locale, resourceBundleAndURI);
			}

			return resourceBundles.get(locale);
		}

		return null;
	}
	
	public static URI getResourceBundleURIFromResourceURI(final URI resourceURI, final boolean localize){
		return getResourceBundleURIFromResourceURI(resourceURI, localize ? Locale.getDefault() : null);
	}
	
	public static URI getResourceBundleURIFromResourceURI(final URI resourceURI, final Locale locale){
		URI resultURI = null;
		
		if (null != resourceURI) {
			final Map<Locale, ResourceBundleAndURI> resourceBundlesUrisUsed = RESOURCE_BUNDLES.get(resourceURI);
			
			if(null != resourceBundlesUrisUsed){
				if(resourceBundlesUrisUsed.containsKey(locale)){
					resultURI = resourceBundlesUrisUsed.get(locale).getUri();
				}
			}
		}
		
		return null != resultURI ? resultURI : resourceURI;
	}

	/**
	 * Retrieves a string for the specified object, localized if indicated.
	 * 
	 * @param eObject
	 *            The object for which to retrieve a (localized) string.
	 * @param key
	 *            The key in the resource bundle.
	 * @param defaultString
	 *            The string to return if no string for the given key can be
	 *            found.
	 * @param localize
	 *            Whether the string should be localized.
	 * @return The (localized) string.
	 */
	public static String getString(EObject eObject, String key, String defaultString, boolean localize) {
		String string = defaultString;

		if (null != eObject) {

			try {
				ResourceBundleAndURI resourceBundleAndURI = getResourceBundle(eObject, localize);

				if (null != resourceBundleAndURI) {
					string = resourceBundleAndURI.getResourceBundle().getString(key);
				}
			} catch (MissingResourceException mre) {
				// ignore
			}
		}

		return string;
	}
	
	/**
	 * This allows to remove of the map the resource bundle.
	 * 
	 * @param uri The URI of the resource to remove.
	 */
	public static void removeResourceBundle(final URI uri){
		if(RESOURCE_BUNDLES.containsKey(uri)){
			RESOURCE_BUNDLES.remove(uri);
		}
	}
}