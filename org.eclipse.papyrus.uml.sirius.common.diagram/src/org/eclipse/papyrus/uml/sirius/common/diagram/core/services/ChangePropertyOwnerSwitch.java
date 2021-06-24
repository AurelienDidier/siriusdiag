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

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.util.UMLSwitch;

/**
 */
public class ChangePropertyOwnerSwitch extends UMLSwitch<Element>{

	/**
	 * The property to change owner.
	 */
	private final Property property;

	/**
	 * Constructor.
	 */
	public ChangePropertyOwnerSwitch(Property property) {
		this.property = property;
	}

	@Override
	public Element caseClass(Class object) {
		object.getOwnedAttributes().add(property);
		return object;
	}
	@Override
	public Element caseDataType(DataType object) {
		object.getOwnedAttributes().add(property);
		return object;
	}
	@Override
	public Element caseInterface(Interface object) {
		object.getOwnedAttributes().add(property);
		return object;
	}
}
