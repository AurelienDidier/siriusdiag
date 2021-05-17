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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Define the utils method corresponding to the internationalization annotation
 * and its resource.
 */
public class InternationalizationAnnotationResourceUtils {

	/**
	 * This allows to get the annotation corresponding to the
	 * internationalization in the resource in parameter if exists.
	 * 
	 * @param resource
	 *            The resource to check
	 * @return The internationalization annotation if exists, <code>null</code>
	 *         otherwise.
	 */
	public static EAnnotation getInternationalizationAnnotation(final Resource resource) {
		EAnnotation result = null;

		for (final EObject objectContent : resource.getContents()) {
			if (objectContent instanceof EAnnotation
					&& InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL
							.equals(((EAnnotation) objectContent).getSource())) {
				result = (EAnnotation) objectContent;
			}
		}

		return result;
	}

}
