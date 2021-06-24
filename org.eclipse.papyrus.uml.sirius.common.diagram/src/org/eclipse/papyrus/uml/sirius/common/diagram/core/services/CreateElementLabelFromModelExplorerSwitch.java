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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.util.UMLSwitch;

/**
 * A switch that handle the create element label from an UI command.
 *
 */
public class CreateElementLabelFromModelExplorerSwitch extends UMLSwitch<String> {

	private static String genName(NamedElement context, String prefix) {
		final StringBuffer result = new StringBuffer(prefix);
		result.append(context.getLabel());
		return result.toString();
	}

	/**
	 * Interaction element name prefix.
	 */
	private static final String INTERACTION_PREFIX = ""; //$NON-NLS-1$

	/**
	 * State machine element name prefix.
	 */
	private static final String STATE_MACHINE_PREFIX = ""; //$NON-NLS-1$

	/**
	 * Activity element name prefix.
	 */
	private static final String ACTIVITY_PREFIX = ""; //$NON-NLS-1$

	/**
	 * Profile element name prefix.
	 */
	private static final String PROFILE_PREFIX = ""; //$NON-NLS-1$

	@Override
	public String caseActivity(Activity object) {
		return genName(object.getPackage(), ACTIVITY_PREFIX);
	}

	@Override
	public String caseInteraction(Interaction object) {
		return genName(object.getPackage(), INTERACTION_PREFIX);
	}

	@Override
	public String caseProfile(Profile object) {
		return genName(object.getNestingPackage(), PROFILE_PREFIX);
	}

	@Override
	public String caseStateMachine(StateMachine object) {
		return genName(object.getPackage(), STATE_MACHINE_PREFIX);
	}

	/**
	 * Return a new label for the given object.
	 *
	 * @param object
	 *            the context
	 * @return a new label for the given object
	 */
	public String getNewLabel(EObject object) {
		return doSwitch(object);
	}
}
