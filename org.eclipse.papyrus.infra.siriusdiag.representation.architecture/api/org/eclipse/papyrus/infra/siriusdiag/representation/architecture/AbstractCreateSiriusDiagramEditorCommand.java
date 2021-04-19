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

package org.eclipse.papyrus.infra.siriusdiag.representation.architecture;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.emf.ui.providers.labelproviders.DelegatingToEMFLabelProvider;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForEObject;
import org.eclipse.papyrus.infra.siriusdiag.architecture.internal.messages.Messages;
import org.eclipse.papyrus.infra.siriusdiag.representation.ICreateSiriusDiagramEditorCommand;
import org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype;
import org.eclipse.papyrus.infra.siriusdiag.representation.architecture.commands.CreateSiriusDiagramEditorViewCommand;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides useful method to create a new Sirius Diagram and open its editor
 */
public abstract class AbstractCreateSiriusDiagramEditorCommand implements ICreateSiriusDiagramEditorCommand {

	/**
	 *
	 * @param dialogTitle
	 *            the dialog title
	 * @param proposedName
	 *            the proposed name
	 * @return
	 *         the name entered by the user, or <code>null</code> in case of cancel
	 */
	protected String askDocumentName(final String dialogTitle, final String proposedName) {
		final InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), dialogTitle, Messages.AbstractCreateSiriusDiagramEditorCommand_DialogMessage, proposedName, null);
		if (dialog.open() == Window.OK) {
			return dialog.getValue();
		}
		return null;
	}

	/**
	 *
	 * @param documentTemplatePrototype
	 *            the document template prototype used to create the {@link DSemanticDiagram}
	 * @param documentName
	 *            the name of the created document
	 * @param semanticContext
	 *            the semantic context used for the creation of the {@link DSemanticDiagram}
	 * @param openAfterCreation
	 *            if <code>true</code> the editor will be opened after the creation
	 * @return
	 *         the created {@link DSemanticDiagram}
	 */
	protected DSemanticDiagram execute(final SiriusDiagramPrototype documentTemplatePrototype, final String documentName, final EObject semanticContext, final boolean openAfterCreation) {
		return execute(documentTemplatePrototype, documentName, semanticContext, semanticContext, openAfterCreation);
	}

	/**
	 *
	 * @param docTemplateProto
	 *            the document template prototype used to create the {@link DSemanticDiagram}
	 * @param documentName
	 *            the name of the created document
	 * @param semanticContext
	 *            the semantic context used for the creation of the {@link DSemanticDiagram}
	 * @param graphicalContext
	 *            the graphical context used for the creation of the {@link DSemanticDiagram}
	 * @param openAfterCreation
	 *            if <code>true</code> the editor will be opened after the creation
	 * @return
	 *         the created {@link DSemanticDiagram}
	 */
	protected DSemanticDiagram execute(final SiriusDiagramPrototype docTemplateProto, final String documentName, final EObject semanticContext, final EObject graphicalContext, final boolean openAfterCreation) {
		final Resource res = semanticContext.eResource();
		final URI semanticURI = res.getURI();
		if (semanticURI.isPlatformPlugin()) {
			Activator.log.error(new UnsupportedOperationException("Documentation for element stored as platform plugin is not yet supported")); //$NON-NLS-1$
			return null;
		}

		final TransactionalEditingDomain domain = getEditingDomain(semanticContext);
		if (null == domain) {
			return null;
		}
		final String siriusDiagramMainTitle = getSiriusDiagramMainTitle(semanticContext);
		final CreateSiriusDiagramEditorViewCommand command = createDSemanticDiagramEditorCreationCommand(domain, docTemplateProto, documentName, siriusDiagramMainTitle, semanticContext, openAfterCreation);
		domain.getCommandStack().execute(command);
		return command.getCreatedEditorView();
	}


	/**
	 *
	 * @param editingDomain
	 *            the editing domain to use for the command
	 * @param documentPrototype
	 *            * the document template prototype used to create the {@link DSemanticDiagram}
	 * @param documentName
	 *            the name of the created document
	 * @param documentMainTitle
	 *            the main title of the document
	 * @param semanticContext
	 *            the semantic context used for the creation of the {@link DSemanticDiagram}
	 * @param graphicalContext
	 *            the graphical context used for the creation of the {@link DSemanticDiagram}
	 * @param openAfterCreation
	 *            if <code>true</code> the editor will be opened after the creation
	 * @return
	 *         the created {@link DSemanticDiagram}
	 */
	public CreateSiriusDiagramEditorViewCommand createDSemanticDiagramEditorCreationCommand(final TransactionalEditingDomain editingDomain,
			final SiriusDiagramPrototype documentPrototype,
			final String documentName,
			final String documentMainTitle,
			final EObject semanticContext,
			final EObject graphicalContext,
			final boolean openAfterCreation) {
		return new CreateSiriusDiagramEditorViewCommand(editingDomain, documentPrototype, documentName, documentMainTitle, semanticContext, graphicalContext, openAfterCreation);
	}

	/**
	 *
	 * @param editingDomain
	 *            the editing domain to use for the command
	 * @param documentPrototype
	 *            * the document template prototype used to create the {@link DSemanticDiagram}
	 * @param documentName
	 *            the name of the created document
	 * @param documentMainTitle
	 *            the main title of the document
	 * @param semanticContext
	 *            the semantic context used for the creation of the {@link DSemanticDiagram}
	 * @param openAfterCreation
	 *            if <code>true</code> the editor will be opened after the creation
	 * @return
	 *         the created {@link DSemanticDiagram}
	 */
	public CreateSiriusDiagramEditorViewCommand createDSemanticDiagramEditorCreationCommand(final TransactionalEditingDomain editingDomain,
			final SiriusDiagramPrototype documentPrototype,
			final String documentName,
			final String documentMainTitle,
			final EObject semanticContext,
			final boolean openAfterCreation) {
		return new CreateSiriusDiagramEditorViewCommand(editingDomain, documentPrototype, documentName, documentMainTitle, semanticContext, openAfterCreation);
	}

	/**
	 *
	 * @param modelElement
	 *            an element of the edited model
	 * @return
	 *         the service registry or <code>null</code> if not found
	 */
	protected final ServicesRegistry getServiceRegistry(final EObject modelElement) {
		try {
			return ServiceUtilsForEObject.getInstance().getServiceRegistry(modelElement);
		} catch (ServiceException e) {
			Activator.log.error("ServicesRegistry not found", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 *
	 * @param modelElement
	 *            an element of the edited model
	 * @return
	 *         the editing domain or <code>null</code> if not found
	 */
	protected final TransactionalEditingDomain getEditingDomain(final EObject modelElement) {
		final ServicesRegistry servicesRegistry = getServiceRegistry(modelElement);
		if (null == servicesRegistry) {
			return null;
		}
		try {
			return ServiceUtils.getInstance().getTransactionalEditingDomain(servicesRegistry);
		} catch (ServiceException e) {
			Activator.log.error("EditingDomain not found", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 *
	 * @param semanticContext
	 *            the semantic context for the create DSemanticDiagram
	 * @return
	 *         the label to use as main title for the generated document
	 */
	protected String getSiriusDiagramMainTitle(final EObject semanticContext) {
		return DelegatingToEMFLabelProvider.INSTANCE.getText(semanticContext);
	}

}
