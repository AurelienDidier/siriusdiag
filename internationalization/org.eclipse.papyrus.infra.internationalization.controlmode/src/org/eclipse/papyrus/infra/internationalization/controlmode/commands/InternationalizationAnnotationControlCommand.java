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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationModel;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.commands.AbstractControlCommand;

/**
 * The internationalization annotation in the notation file must be copied in
 * the new controlled file.
 */
public class InternationalizationAnnotationControlCommand extends AbstractControlCommand {

	/**
	 * The Constant COMMAND_TITLE.
	 */
	private static final String COMMAND_TITLE = "Annotation internationalization control command"; //$NON-NLS-1$

	/**
	 * The old notation resource.
	 */
	private Resource oldNotationResource = null;

	/**
	 * Constructor.
	 * 
	 * @param request
	 *            The request.
	 * @param oldNotationResource
	 *            The old notation resource.
	 */
	public InternationalizationAnnotationControlCommand(final ControlModeRequest request,
			final Resource oldNotationResource) {
		super(COMMAND_TITLE, null, request);
		this.oldNotationResource = oldNotationResource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		CommandResult commandResult = null;

		// Check if the old notation resource is not null
		if (null == oldNotationResource) {
			commandResult = CommandResult.newErrorCommandResult(
					String.format("The source %s model does not exist", NotationModel.NOTATION_FILE_EXTENSION)); //$NON-NLS-1$
		} else {
			// Get the new notation resource and check if this is not null
			final Resource newNotationResource = getTargetResource();
			if (null == newNotationResource) {
				commandResult = CommandResult.newErrorCommandResult("The notation model has not been created"); //$NON-NLS-1$
			} else {
				EAnnotation annotation = null;

				// Search about the internationalization preference
				for (final EObject objectContent : oldNotationResource.getContents()) {
					if (objectContent instanceof EAnnotation
							&& InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL
									.equals(((EAnnotation) objectContent).getSource())) {
						annotation = EcoreUtil.copy((EAnnotation) objectContent);
					}
				}

				// If the annotation exists, add a copy for the new notation
				// resource
				if (null != annotation) {
					newNotationResource.getContents().add(annotation);
					commandResult = CommandResult.newOKCommandResult(newNotationResource);
				}
			}
		}

		return null != commandResult ? commandResult : CommandResult.newOKCommandResult();
	}

	/**
	 * Retrieve the target resource from the request
	 *
	 * @return The target resource.
	 */
	protected Resource getTargetResource() {
		return request.getTargetResource(NotationModel.NOTATION_FILE_EXTENSION);
	}
}
