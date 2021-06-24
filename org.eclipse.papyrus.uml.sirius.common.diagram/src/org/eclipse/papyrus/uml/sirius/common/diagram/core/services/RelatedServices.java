/******************************************************************************
 * Copyright (c) 2021 CEA LIST, Artal Technologies
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Aurelien Didier (ARTAL) - aurelien.didier51@gmail.com - Bug 569174
 *****************************************************************************/
package org.eclipse.papyrus.uml.sirius.common.diagram.core.services;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

/**
 * Utilities to get related elements.
 *
 */
public class RelatedServices {
	/**
	 * A singleton instance to be accessed by other java services.
	 */
	public static final RelatedServices INSTANCE = new RelatedServices();

	/**
	 * Hidden constructor.
	 */
	private RelatedServices() {

	}

	/**
	 * Get related elements.
	 *
	 * @param cur
	 *            Element
	 * @return Related elements
	 */
	public Collection<EObject> getRelated(EObject cur) {
		return new RelatedElementsSwitch().getRelatedElements(cur);
	}
}
