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

package org.eclipse.papyrus.infra.siriusdiag.representation.architecture.commands;

import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;

/**
 * Create a DSemanticDiagram Editor view
 */
public class CreateSiriusDiagramEditorViewCommand extends AbstractCreatePapyrusEditorViewCommand<DSemanticDiagram> {

	/**
	 * the {@link SiriusDiagramPrototype} used to create the {@link DSemanticDiagram} model and its editor view
	 */
	private final SiriusDiagramPrototype prototype;

	/**
	 * the main title of the created {@link DSemanticDiagram}
	 */
	private final String mainTitle;

	/**
	 *
	 * Constructor.
	 *
	 * @param domain
	 * @param documentTemplatePrototype
	 * @param documentName
	 * @param documentMainTitle
	 * @param semanticContext
	 * @param graphicalContext
	 * @param openAfterCreation
	 */
	public CreateSiriusDiagramEditorViewCommand(final TransactionalEditingDomain domain, final SiriusDiagramPrototype documentTemplatePrototype, final String documentName, final String documentMainTitle, final EObject semanticContext,
			final EObject graphicalContext, final boolean openAfterCreation) {
		super(domain, "Create new Sirius Diagram", documentName, semanticContext, graphicalContext, openAfterCreation); //$NON-NLS-1$
		this.prototype = documentTemplatePrototype;
		this.mainTitle = documentMainTitle;
	}

	/**
	 *
	 * Constructor.
	 *
	 * @param domain
	 * @param documentTemplatePrototype
	 * @param documentName
	 * @param documentMainTitle
	 * @param semanticContext
	 * @param openAfterCreation
	 */
	public CreateSiriusDiagramEditorViewCommand(final TransactionalEditingDomain domain, final SiriusDiagramPrototype documentTemplatePrototype, final String documentName, final String documentMainTitle, final EObject semanticContext,
			final boolean openAfterCreation) {
		this(domain, documentTemplatePrototype, documentName, documentMainTitle, semanticContext, null, openAfterCreation);
	}

	/**
	 *
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 *
	 */
	@Override
	protected void doExecute() {
		final DSemanticDiagram diag = this.prototype.getSiriusDiagramPrototype();

		final DSemanticDiagram newInstance = EcoreUtil.copy(diag);

		attachToResource(semanticContext, newInstance);
		// if (SiriusDiagramPrototype instanceof DDiagram) {

		// TODO get Session
		Session session = SessionManager.INSTANCE.getSessions().iterator().next();
		// Get Representation
		EObject model = this.semanticContext;
		Collection<RepresentationDescription> descs = DialectManager.INSTANCE.getAvailableRepresentationDescriptions(session.getSelectedViewpoints(false), model);
		for (RepresentationDescription desc : descs) {
			if (DialectManager.INSTANCE.canCreate(model, desc)) {
				DRepresentation rep = DialectManager.INSTANCE.createRepresentation(session.toString()/* Mettre le path du aird ici */, model, desc, session, new NullProgressMonitor());
				DialectUIManager.INSTANCE.openEditor(session, rep, new NullProgressMonitor());
				session.save(new NullProgressMonitor());
			}
		}

		// DialectUIManager.INSTANCE.openEditor(session, myDiagram,
		// new NullProgressMonitor());
		// newInstance.setMainTitle(this.mainTitle);
		// End test
		// }



		// final IDocumentStructureGeneratorConfiguration generator = newInstance.getDocumentStructureGeneratorConfiguration();
		// if (null != generator) {
		// generator.setDocumentName(this.editorViewName);
		// }

		// newInstance.setDDiagram(this.prototype);
		// newInstance.setGraphicalContext(this.graphicalContext);
		// newInstance.setSemanticContext(this.semanticContext);
		// newInstance.setName(this.editorViewName);
		if (this.openAfterCreation) {
			openEditor(newInstance);
		}
		if (newInstance.eResource() != null) {
			// we suppose all is ok
			this.createdEditorView = newInstance;
		}

	}

}
