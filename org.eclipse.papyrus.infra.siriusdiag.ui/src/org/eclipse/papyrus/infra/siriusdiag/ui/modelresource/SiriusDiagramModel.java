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

package org.eclipse.papyrus.infra.siriusdiag.ui.modelresource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.papyrus.infra.core.resource.AbstractDynamicModel;
import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.business.api.session.DefaultLocalSessionCreationOperation;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionCreationOperation;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.business.internal.session.SessionTransientAttachment;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.ui.business.api.session.SessionUIManager;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelectionCallback;
import org.eclipse.sirius.viewpoint.description.Viewpoint;



/**
 * This class manages PapyrusDocument in aird model resource.
 *
 *
 */
public class SiriusDiagramModel extends AbstractDynamicModel<DSemanticDiagram> {

	/**
	 * Document Model ID.
	 */
	public static final String SIRIUS_DIAGRAM_MODEL_ID = "org.eclipse.papyrus.infra.siriusdiag.ui.DSemanticDiagram"; //$NON-NLS-1$

	/**
	 * the file extension where document are stored.
	 */
	public static final String SIRIUS_DIAGRAM_MODEL_FILE_EXTENSION = "aird"; // $NON-NLS-1$

	/**
	 *
	 * Constructor.
	 *
	 */
	public SiriusDiagramModel() {
		super();
	}

	/**
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#getModelFileExtension()
	 *
	 * @return
	 */
	@Override
	protected String getModelFileExtension() {
		return SIRIUS_DIAGRAM_MODEL_FILE_EXTENSION;
	}

	/**
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractModel#getIdentifier()
	 *
	 * @return
	 */
	@Override
	public String getIdentifier() {
		return SIRIUS_DIAGRAM_MODEL_ID;
	}

	/**
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#loadModel(org.eclipse.emf.common.util.URI)
	 *
	 * @param uriWithoutExtension
	 */
	@Override
	public void loadModel(URI uriWithoutExtension) {
		// It is a common use case that this resource does not (and will not)
		// exist
		if (exists(uriWithoutExtension)) {
			try {
				super.loadModel(uriWithoutExtension);
			} catch (Exception ex) {
				createModel(uriWithoutExtension);
			}
		}

		if (resource == null) {
			createModel(uriWithoutExtension);
		}
	}

	/**
	 * Add a new initialized document to the aird model.
	 *
	 * @param document
	 * @param context
	 *            we need the context to be able to calculate the resource name were the DSemanticDiagram will be saved.
	 *            because this value is maybe not yet set to {@link DSemanticDiagram#setSemanticContext(EObject)}
	 */
	public void addDiagram(final DSemanticDiagram document, final EObject context) {
		if (context != null) { // we check the resource for control mode feature

			Resource targetResource;
			Resource contextResource = context.eResource();
			final URI semanticUri = contextResource.getURI();
			if (!contextResource.getURI().trimFileExtension().equals(getResource().getURI().trimFileExtension())) {
				ResourceSet set = contextResource.getResourceSet();
				targetResource = set.getResource(contextResource.getURI(), true);
			} else {
				targetResource = getResource();
			}
			if (targetResource != null) {
				// targetResource.getContents().add(newInstance);
				URI uri = contextResource.getURI();
				uri = uri.trimFileExtension();
				uri = uri.appendFileExtension(getModelFileExtension());

				// URI airdUri = URI.createURI(uri);
				// final Session session = SessionManager.INSTANCE.getSession(uri, new NullProgressMonitor());
				SessionCreationOperation o = new DefaultLocalSessionCreationOperation(uri, new NullProgressMonitor());
				try {
					o.execute();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Session session = o.getCreatedSession();
				context.eAdapters().add(new SessionTransientAttachment(session));

				// session.open(new NullProgressMonitor());
				session.getTransactionalEditingDomain().getCommandStack()
						.execute(new RecordingCommand(session.getTransactionalEditingDomain()) {
							@Override
							protected void doExecute() {
								session.addSemanticResource(semanticUri, new NullProgressMonitor());
								final ViewpointSelectionCallback selected = new ViewpointSelectionCallback();
								for (final Viewpoint previouslySelected : session.getSelectedViewpoints(false)) {
									selected.deselectViewpoint(previouslySelected, session, new NullProgressMonitor());
								}
								selected.selectViewpoint(
										ViewpointRegistry.getInstance()
												.getViewpoint(URI.createURI("viewpoint:/org.eclipse.papyrus.uml.sirius.clazz.diagram/ClassDiagram")),
										session, new NullProgressMonitor());
								selected.selectViewpoint(
										ViewpointRegistry.getInstance()
												.getViewpoint(URI.createURI("viewpoint:/org.eclipse.papyrus.uml.sirius.sequence.diagram/SequenceDiagram")),
										session, new NullProgressMonitor());
								selected.selectViewpoint(
										ViewpointRegistry.getInstance()
												.getViewpoint(URI.createURI("viewpoint:/org.eclipse.papyrus.uml.sirius.statemachine.diagram/StateMachineDiagram")),
										session, new NullProgressMonitor());
							}
							// selected.selectViewpoint(
							// ViewpointRegistry.getInstance()
							// .getViewpoint(URI.createURI("viewpoint:/org.eclipse.papyrus.uml.sirius.clazz.diagram/ClassDiagram")),
							// session, new NullProgressMonitor());
							//
							// }
						});
				session.open(new NullProgressMonitor());
				SessionManager.INSTANCE.add(session);
				SessionUIManager.INSTANCE.getOrCreateUISession(session);
				session.save(new NullProgressMonitor());
				// session.close(new NullProgressMonitor());
			}
		}
	}

	/**
	 *
	 * @see org.eclipse.papyrus.infra.core.resource.IEMFModel#canPersist(org.eclipse.emf.ecore.EObject)
	 *
	 * @param object
	 * @return
	 */
	@Override
	public boolean canPersist(EObject object) {
		return (getResource() != null) && isSupportedRoot(object);
	}

	/**
	 *
	 * @param object
	 * @return
	 */
	protected boolean isSupportedRoot(EObject object) {
		return object instanceof DSemanticDiagram;
	}

	/**
	 *
	 * @see org.eclipse.papyrus.infra.core.resource.IEMFModel#persist(org.eclipse.emf.ecore.EObject)
	 *
	 * @param object
	 */
	@Override
	public void persist(EObject object) {
		if (!canPersist(object)) {
			throw new IllegalArgumentException("cannot persist " + object); //$NON-NLS-1$
		}

		getResource().getContents().add(object);
	}

	/**
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#configureResource(org.eclipse.emf.ecore.resource.Resource)
	 *
	 * @param resource
	 */
	@Override
	protected void configureResource(Resource resource) {
		super.configureResource(resource);
	}
}
