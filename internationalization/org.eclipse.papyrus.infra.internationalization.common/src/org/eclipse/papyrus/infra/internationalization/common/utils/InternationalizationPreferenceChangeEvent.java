/*******************************************************************************
 * Copyright (c) 2017 Christian W. Damus and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Christian W. Damus - initial API and implementation
 *******************************************************************************/
package org.eclipse.papyrus.infra.internationalization.common.utils;

import java.util.EventObject;
import java.util.Locale;

import org.eclipse.emf.common.util.URI;

/**
 * Event indicating some change in the internationalization preferences
 * for a resource, by URI.
 *
 * @author Christian W. Damus
 * 
 * @since 1.1
 */
public final class InternationalizationPreferenceChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/** Event type indicating change in enablement of internationalization. */
	public static final int ENABLED = 0;
	
	/** Event type indicating change in locale of internationalization. */
	public static final int LOCALE = 1;
	
	private final int eventType;
	private final URI resourceURI;
	
	private final boolean enabled;
	private final Locale locale;
	
	/**
	 * Initializes me with my source, resource URI, and enablement change.
	 * 
	 * @param source my source, which is opaque to clients
	 * @param resourceURI my resource URI
	 * @param  enabled whether enablement changed to {@code true} or {@code false}
	 */
	public InternationalizationPreferenceChangeEvent(Object source, URI resourceURI, boolean enabled) {
		this(source, resourceURI, ENABLED, enabled, null);
	}
	
	/**
	 * Initializes me with my source, resource URI, and locale change.
	 * 
	 * @param source my source, which is opaque to clients
	 * @param resourceURI my resource URI
	 * @param locale the new locale for the resource
	 */
	public InternationalizationPreferenceChangeEvent(Object source, URI resourceURI, Locale locale) {
		this(source, resourceURI, LOCALE, false, locale);
	}
	
	/**
	 * Initializes me.
	 */
	private InternationalizationPreferenceChangeEvent(Object source, URI resourceURI, int eventType, boolean enabled, Locale locale) {
		super(source);
		
		this.resourceURI = resourceURI;
		this.eventType = eventType;
		this.enabled = enabled;
		this.locale = locale;
	}

	/**
	 * Queries the {@linkplain #ENABLED type} of event that I am.
	 * 
	 * @return my type
	 */
	public int getEventType() {
		return eventType;
	}
	
	/**
	 * Queries the resource URI to which I pertain.
	 * 
	 * @return the resource URI for which a preference changed
	 */
	public URI getResourceURI() {
		return resourceURI;
	}
	
	/**
	 * Queries whether the internationalization was enabled or disabled for
	 * the resource.  Only makes sense for the {@link #ENABLED} event type.
	 * 
	 * @return {@code true} if I am an {@linkplain #ENABLED enablement} event and my
	 * resource enabled internationalization; {@code false} if either I am
	 * not an enablement event or I am but my resource disabled internationalization
	 */
	public boolean isInternationalizationEnabled() {
		return enabled;
	}
	
	/**
	 * Queries the locale that was set for the internationalization of
	 * the resource.  Only makes sense for the {@link #LOCALE} event type.
	 * 
	 * @return the new locale if I am a {@linkplain #LOCALE locale} event;
	 *  {@code null} if either I am not a locale event
	 */
	public Locale getLocale() {
		return locale;
	}
}
