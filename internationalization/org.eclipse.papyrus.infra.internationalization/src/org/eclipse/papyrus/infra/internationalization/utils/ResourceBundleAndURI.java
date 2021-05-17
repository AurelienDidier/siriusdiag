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

import java.util.ResourceBundle;

import org.eclipse.emf.common.util.URI;

/**
 * This class allows to store the resource bundle with the real uri
 * corresponding.
 */
public class ResourceBundleAndURI {

	/**
	 * The resource bundle.
	 */
	private ResourceBundle resourceBundle;

	/**
	 * The uri of the resource.
	 */
	private URI uri;

	/**
	 * Constructor.
	 *
	 * @param resourceBundle
	 *            The resource bundle.
	 * @param uri
	 *            The uri of the resource.
	 */
	public ResourceBundleAndURI(final ResourceBundle resourceBundle, final URI uri) {
		this.resourceBundle = resourceBundle;
		this.uri = uri;
	}

	/**
	 * Get the resource bundle.
	 * 
	 * @return The resource bundle.
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Set the resource bundle.
	 * 
	 * @param resourceBundle
	 *            The resource bundle.
	 */
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	/**
	 * Get the resource uri.
	 * 
	 * @return The resource uri.
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Set the resource uri.
	 * 
	 * @param uri
	 *            The resource uri.
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}

}
