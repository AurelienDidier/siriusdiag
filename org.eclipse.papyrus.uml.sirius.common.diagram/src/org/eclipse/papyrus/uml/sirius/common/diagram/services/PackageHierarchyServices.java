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
package org.eclipse.papyrus.uml.sirius.common.diagram.services;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.uml.sirius.common.diagram.core.services.AbstractDiagramServices;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.VisibilityKind;

/**
 * A set of services to handle the Package Hierarchy diagram.
 *
 */
public class PackageHierarchyServices extends AbstractDiagramServices {

	/**
	 * Check if a package import is public.
	 *
	 * @param pkgImport
	 *            Package import
	 * @return True if is public otherwise false
	 */
	public boolean isNotPublic(PackageImport pkgImport) {
		return !pkgImport.getVisibility().equals(VisibilityKind.PUBLIC_LITERAL);
	}

	/**
	 * Check if a reconnect is possible and is not involving creating a cycle in the model.
	 *
	 * @param host
	 *            the current element.
	 * @param source
	 *            potential source of the edge.
	 * @param target
	 *            potential target of the edge
	 * @param element
	 *            element represented by the edge.
	 * @return true if no cycle is detected.
	 */
	public boolean reconnectContainmentPrecondition(Element host, Element source, Element target,
			Element element) {
		if (element == target) {
			return false;
		}
		final Iterator<EObject> it = element.eAllContents();
		while (it.hasNext()) {
			final EObject child = it.next();
			if (child == target) {
				return false;
			}
		}
		return true;
	}

}
