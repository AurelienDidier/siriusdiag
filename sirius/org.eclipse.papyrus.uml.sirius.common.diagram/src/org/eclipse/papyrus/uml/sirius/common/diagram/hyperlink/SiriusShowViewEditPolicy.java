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
 *    Aurelien Didier (ARTAL) - aurelien.didier51@gmail.com - Initial API and others
 *****************************************************************************/
package org.eclipse.papyrus.uml.sirius.common.diagram.hyperlink;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * The Class ShowViewEditPolicy.
 */
public class SiriusShowViewEditPolicy {

	/** The policy. */
	private static OpenEditPolicy policy = null;

	/**
	 * Gets the open edit policy.
	 *
	 * @return the open edit policy
	 */
	private static OpenEditPolicy getOpenEditPolicy() {
		if (policy == null) {
			policy = new OpenEditPolicy() {

				@Override
				protected Command getOpenCommand(Request request) {

					String viewId = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
		}
		return policy;
	}

	/**
	 * Listens to double-click event over some element of the diagram and shows
	 * properties tab.
	 *
	 * @return the open edit policy
	 */
	// @unused
	public static OpenEditPolicy createOpenEditPolicy() {
		return getOpenEditPolicy();
	}
}
