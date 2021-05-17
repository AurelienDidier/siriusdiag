/*****************************************************************************
 * Copyright (c) 2016 CEA LIST and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Nicolas FAUVERGUE (ALL4TEC) nicolas.fauvergue@all4tec.net - Initial API and implementation
 *   
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.controlmode.participants;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationModel;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.controlmode.Activator;
import org.eclipse.papyrus.infra.internationalization.controlmode.commands.InternationalizationAnnotationControlCommand;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant;
import org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant;

/**
 * The internationalization annotation control participant for the preference
 * annotation.
 */
public class InternationalizationAnnotationControlParticipant
		implements IControlCommandParticipant, IUncontrolCommandParticipant {

	/**
	 * The Constant PARTICIPANT_ID.
	 */
	private static final String PARTICIPANT_ID = "org.eclipse.papyrus.infra.internationalization.controlmode.InternationalizationAnnotationControlParticipant"; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public InternationalizationAnnotationControlParticipant() {

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlModeParticipant#getID()
	 */
	@Override
	public String getID() {
		return PARTICIPANT_ID;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlModeParticipant#getPriority()
	 */
	@Override
	public int getPriority() {
		return 20;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant#provideUnControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public boolean provideUnControlCommand(final ControlModeRequest request) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant#getPreUncontrolCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPreUncontrolCommand(final ControlModeRequest request) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant#getPostUncontrolCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPostUncontrolCommand(final ControlModeRequest request) {
		// Don't create the notation resource since it is already handled
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant#provideControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public boolean provideControlCommand(final ControlModeRequest request) {
		boolean result = false;

		// Check if the annotation for the internationalization is in the
		// notation file
		final Iterator<EObject> objectContentsIterator = getSourceResource(request).getContents().iterator();
		while (objectContentsIterator.hasNext() && !result) {
			EObject objectContent = objectContentsIterator.next();
			if (objectContent instanceof EAnnotation
					&& InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL
							.equals(((EAnnotation) objectContent).getSource())) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant#getPreControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPreControlCommand(final ControlModeRequest request) {
		// Don't create the notation resource since it is already handled
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant#getPostControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPostControlCommand(final ControlModeRequest request) {
		return new InternationalizationAnnotationControlCommand(request, getSourceResource(request));
	}

	/**
	 * Gets the old notation resource.
	 *
	 * @param request
	 *            The control request.
	 * @return the old notation resource.
	 */
	protected Resource getSourceResource(final ControlModeRequest request) {
		Resource oldNotationResource = null;

		try {
			oldNotationResource = request.getModelSet().getResource(getOldNotationURI(request), true);
		} catch (Exception e) {
			Activator.log.error("Unable to retrieve old notation resource", e); //$NON-NLS-1$
		}

		return oldNotationResource;
	}

	/**
	 * Get the old notation URI for request.
	 *
	 * @param request
	 *            The control request.
	 * @return The old notation resource.
	 * @throws ExecutionException
	 *             The exception when the notation resource is not found.
	 */
	protected URI getOldNotationURI(final ControlModeRequest request) throws ExecutionException {
		URI uri = request.getSourceURI();

		if (null != uri) {
			return uri.trimFileExtension().appendFileExtension(NotationModel.NOTATION_FILE_EXTENSION);
		}
		throw new ExecutionException("Unable to retreive URI of the old notation model"); //$NON-NLS-1$
	}
}
