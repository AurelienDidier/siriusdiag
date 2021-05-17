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
package org.eclipse.papyrus.infra.internationalization.controlmode.commands;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.infra.internationalization.common.utils.LocaleNameResolver;
import org.eclipse.papyrus.infra.internationalization.controlmode.utils.ControlPropertiesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.commands.CreateControlResource;

/**
 * Command used to create new controlled properties resource. This command will
 * create the new resources. It will also handle correctly undo and redo.
 */
public class CreatePropertiesControlResourceCommand extends CreateControlResource {

	/**
	 * The locale to add in the file name.
	 */
	private Locale locale;

	/**
	 * The created resource. We need it to remove it for undo command.
	 */
	private Resource createdResource;

	/**
	 * The internationalization model resource.
	 */
	private InternationalizationModelResource internationalizationModelResource;

	/**
	 * Constructor.
	 *
	 * @param request
	 *            {@link ControlModeRequest} used to compute the command
	 * @param newFileExtension
	 *            File extension use to create the resource. If not specified
	 *            then it should be already provided in the {@link URI}
	 * @param locale
	 *            The locale to add in the file name.
	 */
	public CreatePropertiesControlResourceCommand(final ControlModeRequest request, final String newFileExtension,
			final Locale locale) {
		super(request, newFileExtension);
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.AbstractControlResourceCommand#getTargetUri()
	 */
	@Override
	public URI getTargetUri() {
		return getModifiedURI(getRequest().getNewURI());
	}

	/**
	 * Get the URI with the locale if not null and the extension if not null.
	 * 
	 * @param initialURI
	 *            The initial URI.
	 * @return The modified URI.
	 */
	protected URI getModifiedURI(final URI initialURI) {
		URI targetURI = initialURI;
		if (null != locale) {
			// Get the existing file extension
			final String existingFileExtension = targetURI.fileExtension();
			// Get the last segment
			String lastSegment = targetURI.trimFileExtension().lastSegment();
			// Add the locale to the last segment
			lastSegment = lastSegment + LocaleNameResolver.UNDERSCORE + locale.toString();
			// Replace the last segment (managing file extension
			targetURI = targetURI.trimFileExtension().trimSegments(1).appendSegment(lastSegment)
					.appendFileExtension(existingFileExtension);
		}
		if (null != newFileExtension) {
			targetURI = targetURI.trimFileExtension().appendFileExtension(newFileExtension);
		}
		return targetURI;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.CreateControlResource#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor progressMonitor, final IAdaptable info)
			throws ExecutionException {
		final CommandResult res = super.doExecuteWithResult(progressMonitor, info);

		// Get the created resource
		createdResource = getRequest().getTargetResource(newFileExtension);

		// Manage a map of created properties resources, because several
		// properties resources can be created
		if (null == getRequest().getParameter(ControlPropertiesUtils.CREATED_PROPERTIES_RESOURCES)) {
			getRequest().setParameter(ControlPropertiesUtils.CREATED_PROPERTIES_RESOURCES,
					new HashMap<Locale, Resource>());
		}
		((Map<Locale, Resource>) getRequest().getParameter(ControlPropertiesUtils.CREATED_PROPERTIES_RESOURCES))
				.put(locale, createdResource);

		// Add the resource to the internationalization model resource
		final InternationalizationModelResource modelResource = getInternationalizationModelResource();
		if (null != modelResource) {
			modelResource.addResourceToModel(getRequest().getNewURI().trimFileExtension(), createdResource, locale);
		}

		return res;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.CreateControlResource#doUndo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		getRequest().setTargetResource(createdResource, getFileExtension());

		// Unload and remove the resource from the internationalization model
		// resource
		final InternationalizationModelResource modelResource = getInternationalizationModelResource();
		if (null != modelResource) {
			modelResource.unload(createdResource);
			modelResource.getResources().remove(createdResource);
		}

		return super.doUndo(monitor, info);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.CreateControlResource#doRedo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		getRequest().setTargetResource(createdResource, getFileExtension());
		final IStatus result = super.doRedo(monitor, info);

		// Add the resource to the internationalization model resource
		final InternationalizationModelResource modelResource = getInternationalizationModelResource();
		if (null != modelResource) {
			modelResource.addResourceToModel(getRequest().getNewURI().trimFileExtension(), createdResource, locale);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.CreateControlResource#getNewURI()
	 */
	@Override
	protected URI getNewURI() {
		return getModifiedURI(getRequest().getNewURI());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.CreateControlResource#getTargetResrource(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected Resource getTargetResrource(final EObject objectToUncontrol) {
		Resource resultResource = null;
		URI targetURI = null;

		if (null != objectToUncontrol) {
			targetURI = objectToUncontrol.eResource().getURI().trimFileExtension();
			if (null != locale) {
				String lastSegment = targetURI.lastSegment();
				lastSegment = lastSegment + LocaleNameResolver.UNDERSCORE + locale.toString();
				targetURI = targetURI.trimSegments(1).appendSegment(lastSegment);
			}
			targetURI = targetURI.appendFileExtension(getFileExtension());
		}

		if (null != targetURI) {
			final InternationalizationModelResource modelResource = getInternationalizationModelResource();
			if (null != modelResource) {
				resultResource = modelResource.getResourceForURIAndLocale(objectToUncontrol.eResource().getURI()
						.trimFileExtension().appendFileExtension(getFileExtension()), locale);
			}
		}

		return resultResource;
	}

	/**
	 * Get the internationalization model resource.
	 * 
	 * @return The internationalization model resource.
	 */
	protected InternationalizationModelResource getInternationalizationModelResource() {
		if (null == internationalizationModelResource) {
			final ModelSet modelSet = getRequest().getModelSet();
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.workspace.AbstractEMFOperation#dispose()
	 */
	@Override
	public void dispose() {
		internationalizationModelResource = null;
		createdResource = null;
		locale = null;
		super.dispose();
	}
}
