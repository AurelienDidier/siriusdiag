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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.core.command.UnexecutableCommand;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.infra.internationalization.controlmode.commands.ControlPropertiesCommand;
import org.eclipse.papyrus.infra.internationalization.controlmode.commands.CreatePropertiesControlResourceCommand;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.internationalization.utils.PropertiesFilesUtils;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant;

/**
 * The control participant for the properties files.
 */
public class PropertiesControlParticipant implements IControlCommandParticipant {

	/**
	 * The Constant PARTICIPANT_ID.
	 */
	private static final String PARTICIPANT_ID = "org.eclipse.papyrus.infra.internationalization.controlmode.PropertiesControlParticipant"; //$NON-NLS-1$

	/**
	 * The internationalization model resource.
	 */
	private InternationalizationModelResource internationalizationModelResource;

	/**
	 * Constructor.
	 */
	public PropertiesControlParticipant() {
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
		return 40;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant#provideControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public boolean provideControlCommand(final ControlModeRequest request) {
		boolean result = false;

		final EObject objectToControl = request.getTargetObject();
		if (null != objectToControl) {
			// If there is no properties resource in the old resource, then
			// there's nothing to do
			final InternationalizationModelResource modelResource = getInternationalizationModelResource(request);
			final Map<Locale, Resource> localesAndResourcesForURI = modelResource
					.getLocalesAndResourcesForURI(request.getSourceURI().trimFileExtension()
							.appendFileExtension(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION));

			result = null != localesAndResourcesForURI && !localesAndResourcesForURI.isEmpty();
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
		ICommand resultCommand = UnexecutableCommand.INSTANCE;

		// Get the used locale to determinate the properties file to create
		final InternationalizationModelResource modelResource = getInternationalizationModelResource(request);
		if (null != modelResource) {
			final Set<Locale> locales = modelResource.getAvailablePropertiesLocales(request.getSourceURI());

			resultCommand = new CompositeTransactionalCommand(request.getEditingDomain(), "Create properties files"); //$NON-NLS-1$

			// Loop on each locale to create associated properties file
			for (final Locale currentLocale : locales) {
				((CompositeTransactionalCommand) resultCommand).add(new CreatePropertiesControlResourceCommand(request,
						PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION, currentLocale));
			}
		}

		return resultCommand;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IControlCommandParticipant#getPostControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPostControlCommand(final ControlModeRequest request) {
		
		// Dispose the internationalizationModelResource
		internationalizationModelResource = null;
		
		return new ControlPropertiesCommand(request);
	}

	/**
	 * Get the internationalization model resource.
	 * 
	 * @return The internationalization model resource.
	 */
	protected InternationalizationModelResource getInternationalizationModelResource(final ControlModeRequest request) {
		if (null == internationalizationModelResource && null != request) {
			final ModelSet modelSet = request.getModelSet();
			if (null != modelSet) {
				try {
					internationalizationModelResource = (InternationalizationModelResource) modelSet
							.getModelChecked(InternationalizationModelResource.MODEL_ID);
				} catch (final NotFoundException e) {
					// Do nothing
				}
			}
		}

		return internationalizationModelResource;
	}
}
