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

import java.util.Locale;

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
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.commands.RemoveControlResourceCommand;
import org.eclipse.papyrus.infra.services.controlmode.messages.Messages;

/**
 * Command used to create new controlled properties resource. This command will
 * create the new resources. It will also handle correctly undo and redo.
 */
public class RemovePropertiesControlResourceCommand extends RemoveControlResourceCommand {

	/**
	 * The resource to remove. We need it to remove it for undo command.
	 */
	private Resource resourceToRemove;

	/**
	 * The locale to add in the file name.
	 */
	private Locale locale;

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
	 * @param resourceToRemove
	 *            The resource to remove.
	 * @param locale
	 *            The locale to add in the file name.
	 */
	public RemovePropertiesControlResourceCommand(final ControlModeRequest request, final String fileExtension,
			final Resource resourceToRemove, final Locale locale) {
		super(request, fileExtension);
		this.resourceToRemove = resourceToRemove;
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.RemoveControlResourceCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		CommandResult commandResult = null;
		EObject objectToControl = getRequest().getTargetObject();
		if (null == objectToControl) {
			commandResult = CommandResult.newErrorCommandResult(CONTROL_OBJECT_ERROR);
		} else {
			ModelSet modelSet = getRequest().getModelSet();
			if (null == modelSet) {
				commandResult = CommandResult.newErrorCommandResult(RESOURCESET_ERROR);
			} else {

				// Delete resource back-end on save
				modelSet.getResourcesToDeleteOnSave().add(resourceToRemove.getURI());

				// Save source and target resource
				Resource targetResource = getTargetResource(objectToControl);
				if (null == targetResource) {
					commandResult = CommandResult
							.newErrorCommandResult(Messages.getString(TARGET_RESOURCE_ERROR, getFileExtension()));
				} else {

					// The target resource needs to be saved else the resolution
					// will not operate
					targetResource.setModified(true);

					getRequest().setTargetResource(targetResource, getFileExtension());
					getRequest().setSourceResource(resourceToRemove, getFileExtension());

					// remove resource set
					modelSet.getResources().remove(resourceToRemove);

					// Unload and remove the resource from the
					// internationalization model resource
					final InternationalizationModelResource modelResource = getInternationalizationModelResource();
					if (null != modelResource) {
						modelResource.unload(resourceToRemove.getURI());
						modelResource.getResources().remove(resourceToRemove);
					}

					commandResult = CommandResult.newOKCommandResult();
				}
			}
		}

		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.RemoveControlResourceCommand#doUndo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final IStatus result = super.doUndo(monitor, info);

		// Add the resource to the internationalization model resource
		final InternationalizationModelResource modelResource = getInternationalizationModelResource();
		if (null != modelResource) {
			modelResource.addResourceToModel(getRequest().getSourceURI().trimFileExtension(), resourceToRemove, locale);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.RemoveControlResourceCommand#getSourceResource()
	 */
	@Override
	protected Resource getSourceResource() {
		return resourceToRemove;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.RemoveControlResourceCommand#doRedo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final IStatus result = super.doRedo(monitor, info);

		// Unload and remove the resource from the internationalization model
		// resource
		final InternationalizationModelResource modelResource = getInternationalizationModelResource();
		if (null != modelResource) {
			modelResource.unload(resourceToRemove.getURI());
			modelResource.getResources().remove(resourceToRemove);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.services.controlmode.commands.RemoveControlResourceCommand#getTargetResource(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected Resource getTargetResource(final EObject objectToUncontrol) {
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
			InternationalizationModelResource modelResource = getInternationalizationModelResource();
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
		resourceToRemove = null;
		locale = null;
		super.dispose();
	}
}
