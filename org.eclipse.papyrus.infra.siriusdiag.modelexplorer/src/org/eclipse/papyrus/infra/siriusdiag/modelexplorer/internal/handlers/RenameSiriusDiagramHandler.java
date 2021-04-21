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
package org.eclipse.papyrus.infra.siriusdiag.modelexplorer.internal.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.papyrus.infra.emf.gmf.command.GMFtoEMFCommandWrapper;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;
import org.eclipse.papyrus.infra.internationalization.utils.utils.LabelInternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.utils.utils.LabelInternationalizationUtils;
import org.eclipse.papyrus.infra.siriusdiag.modelexplorer.internal.commands.RenameSiriusDiagramLabelCommand;
import org.eclipse.papyrus.infra.siriusdiag.modelexplorer.internal.messages.Messages;
import org.eclipse.papyrus.views.modelexplorer.commands.RenameElementCommand;
import org.eclipse.papyrus.views.modelexplorer.util.ModelExplorerEditionUtil;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.viewpoint.ViewpointPackage;

/**
 * This handler provides the method to rename a document.
 *
 */
public class RenameSiriusDiagramHandler extends AbstractSiriusDiagramCommandHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCommand(IEvaluationContext context) {
		TransactionalEditingDomain editingDomain = getEditingDomain(context);
		List<DSemanticDiagram> documents = getSelectedDSemanticDiagrams();
		if (editingDomain != null && documents.size() == 1) {

			final DSemanticDiagram document = documents.get(0);

			final String documentLabel = LabelInternationalizationUtils.getLabelWithoutSubstract(document, true);
			if (null != documentLabel && LabelInternationalizationPreferencesUtils.getInternationalizationPreference(document)) {
				AbstractTransactionalCommand cmd = new RenameSiriusDiagramLabelCommand(editingDomain, "ChangeDSemanticDiagramLabelCommand", document, documentLabel, Messages.RenameSiriusDiagramHandler_Label_DialogTitle); //$NON-NLS-1$
				return new GMFtoEMFCommandWrapper(cmd);
			} else {
				final String currentName = document.getName();
				if (null != currentName) {
					EStructuralFeature nameFeature = ViewpointPackage.eINSTANCE.getDRepresentation_Name();
					return new RenameElementCommand(editingDomain, "RenameSiriusDiagramCommand", document, currentName, nameFeature, Messages.RenameSiriusDiagramHandler_RenameAnExistingDSemanticDiagram, Messages.RenameSiriusDiagramHandler_NewName); //$NON-NLS-1$
				}
			}
		}
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean computeEnabled(final IEvaluationContext context) {
		boolean computeEnabled = super.computeEnabled(context);
		if (computeEnabled) {
			List<EObject> selectedElements = getSelectedElements();
			EObject selection = selectedElements.get(0);
			computeEnabled = !EMFHelper.isReadOnly(selection);
		}
		return computeEnabled;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Overridden to inline edit a document if it is handled by direct editor.
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		EObject selectedDocument = getSelectedElement();

		// If the selected document could be handled by direct editor
		if (ModelExplorerEditionUtil.isHandledByDirectEditor(selectedDocument)) {
			// Call the edit element method from the model explorer to trigger DirectEditor
			ModelExplorerEditionUtil.editElement(selectedDocument);
		} else {
			// Otherwise, show the model dialog to get user input
			super.execute(event);
		}

		return null;
	}

}
