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

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.CreateEditPoliciesOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.IEditPolicyProvider;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.gmfdiag.common.utils.ServiceUtilsForEditPart;

/**
 * An EditPolicyProvider for Papyrus
 *
 * @author Shuai Li
 *
 */
public class SiriusShortcutEditPartProvider extends AbstractProvider implements IEditPolicyProvider {

	/**
	 * {@inheritDoc}
	 *
	 * This provider can handle GraphicalEditParts
	 */
	@Override
	public boolean provides(IOperation operation) {
		if (operation instanceof CreateEditPoliciesOperation) {
			CreateEditPoliciesOperation epOperation = (CreateEditPoliciesOperation) operation;
			EditPart editPart = epOperation.getEditPart();
			try {
				ServicesRegistry registry = ServiceUtilsForEditPart.getInstance().getServiceRegistry(editPart);
				if (registry == null) {
					// We're not in the Papyrus context
					return false;
				}
			} catch (ServiceException ex) {
				// We're not in the Papyrus context
				// Ignore the exception and do not provide the EditPolicy
				return false;
			}
			return editPart instanceof IGraphicalEditPart;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Installs the Papyrus edit policy
	 */
	@Override
	public void createEditPolicies(EditPart editPart) {
		if (editPart instanceof IPrimaryEditPart) {
			editPart.installEditPolicy(EditPolicyRoles.POPUPBAR_ROLE, new SiriusShortCutPreviewEditPolicy());
			editPart.installEditPolicy(EditPolicyRoles.OPEN_ROLE, new SiriusShortCutDiagramEditPolicy());
		}
	}
}
