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
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.core.command.UnexecutableCommand;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.infra.internationalization.controlmode.commands.ControlPropertiesCommand;
import org.eclipse.papyrus.infra.internationalization.controlmode.commands.CreatePropertiesControlResourceCommand;
import org.eclipse.papyrus.infra.internationalization.controlmode.commands.RemovePropertiesControlResourceCommand;
import org.eclipse.papyrus.infra.internationalization.controlmode.utils.ControlPropertiesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.internationalization.utils.PropertiesFilesUtils;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant;

/**
 * The uncontrol participant for the properties files.
 */
public class PropertiesUncontrolParticipant implements IUncontrolCommandParticipant {

	/**
	 * The Constant PARTICIPANT_ID.
	 */
	private static final String PARTICIPANT_ID = "org.eclipse.papyrus.infra.internationalization.controlmode.PropertiesUncontrolParticipant"; //$NON-NLS-1$

	/**
	 * The internationalization model resource.
	 */
	private InternationalizationModelResource internationalizationModelResource;

	/**
	 * Constructor.
	 */
	public PropertiesUncontrolParticipant() {
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
		return 70;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant#provideUnControlCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public boolean provideUnControlCommand(final ControlModeRequest request) {
		boolean result = false;

		final EObject objectToUncontrol = request.getTargetObject();
		if (null != objectToUncontrol) {
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
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant#getPreUncontrolCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPreUncontrolCommand(final ControlModeRequest request) {
		boolean result = setPropertiesTargetRequest(request);
		
		CompositeTransactionalCommand compositeCommand = null;
		
		if (result) {
			compositeCommand = new CompositeTransactionalCommand(request.getEditingDomain(), "Uncontrol command"); //$NON-NLS-1$
			
			// Before modify properties files, check that new properties files are available
			final URI newPropertiesURI = request.getNewURI().trimFileExtension()
					.appendFileExtension(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION);
			final URI sourcePropertiesURI = request.getSourceURI().trimFileExtension()
					.appendFileExtension(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION);
			final ModelSet modelSet = request.getModelSet();
	
			if (null != modelSet) {
	
				final InternationalizationModelResource modelResource = getInternationalizationModelResource(request);
				if (null != modelResource) {
					final Map<Locale, Resource> newLocalesAndResources = modelResource
							.getLocalesAndResourcesForURI(newPropertiesURI);
					final Map<Locale, Resource> sourceLocalesAndResources = modelResource
							.getLocalesAndResourcesForURI(sourcePropertiesURI);
					
					for(final Locale sourceLocale : sourceLocalesAndResources.keySet()){
						if(!newLocalesAndResources.containsKey(sourceLocale)){
							// The locale is not existing for the new properties resources, we need to create it
							compositeCommand.add(new CreatePropertiesControlResourceCommand(request,
									PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION, sourceLocale));
						}
					}
				}
			}
			
			compositeCommand.add(new ControlPropertiesCommand(request));
		}
		
		return null != compositeCommand ? compositeCommand : UnexecutableCommand.INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.participants.IUncontrolCommandParticipant#getPostUncontrolCommand(org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest)
	 */
	@Override
	public ICommand getPostUncontrolCommand(final ControlModeRequest request) {
		ICommand commandResult = UnexecutableCommand.INSTANCE;

		final Map<Locale, Resource> result = getPropertiesSourceRequest(request);
		if (null != result && !result.isEmpty()) {
			final CompositeTransactionalCommand compositeCommand = new CompositeTransactionalCommand(
					request.getEditingDomain(), "Remove properties resources"); //$NON-NLS-1$

			for (final Entry<Locale, Resource> entry : result.entrySet()) {
				compositeCommand.add(new RemovePropertiesControlResourceCommand(request,
						PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION, entry.getValue(), entry.getKey()));
			}

			commandResult = compositeCommand;
		}

		// Dispose the internationationalizationModelResource
		internationalizationModelResource = null;
		
		return commandResult;
	}

	/**
	 * Init target request to have the properties resources references.
	 *
	 * @param request
	 *            The request.
	 */
	protected boolean setPropertiesTargetRequest(final ControlModeRequest request) {
		boolean result = false;
		final URI propertiesURI = request.getNewURI().trimFileExtension()
				.appendFileExtension(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION);
		final ModelSet modelSet = request.getModelSet();

		if (null != modelSet) {

			final InternationalizationModelResource modelResource = getInternationalizationModelResource(request);
			if (null != modelResource) {
				final Map<Locale, Resource> localesAndResources = modelResource
						.getLocalesAndResourcesForURI(propertiesURI);

				if (!localesAndResources.isEmpty()) {
					request.setParameter(ControlPropertiesUtils.CREATED_PROPERTIES_RESOURCES, localesAndResources);
					// Nothing to do but everything is ok
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Init source request to have the properties resources references.
	 *
	 * @param request
	 *            The request.
	 */
	protected Map<Locale, Resource> getPropertiesSourceRequest(final ControlModeRequest request) {
		Map<Locale, Resource> result = null;
		final URI propertiesURI = request.getSourceURI().trimFileExtension()
				.appendFileExtension(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION);
		final ModelSet modelSet = request.getModelSet();

		if (null != modelSet) {
			final InternationalizationModelResource modelResource = getInternationalizationModelResource(request);
			if (null != modelResource) {
				result = modelResource.getLocalesAndResourcesForURI(propertiesURI);
			}
		}

		return result;
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
