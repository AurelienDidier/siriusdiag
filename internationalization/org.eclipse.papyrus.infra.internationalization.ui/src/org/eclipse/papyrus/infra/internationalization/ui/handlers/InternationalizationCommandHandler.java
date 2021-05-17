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

package org.eclipse.papyrus.infra.internationalization.ui.handlers;

import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.emf.commands.AddToResourceCommand;
import org.eclipse.papyrus.infra.emf.commands.RemoveFromResourcecommand;
import org.eclipse.papyrus.infra.emf.gmf.command.GMFtoEMFCommandWrapper;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForResource;
import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationModel;
import org.eclipse.papyrus.infra.internationalization.common.command.LocaleInternationalizationPreferenceCommand;
import org.eclipse.papyrus.infra.internationalization.common.command.UseInternationalizationPreferenceCommand;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.ui.Activator;
import org.eclipse.papyrus.infra.internationalization.ui.dialogs.InternationalizationDialog;
import org.eclipse.papyrus.infra.ui.command.AbstractCommandHandler;
import org.eclipse.ui.PlatformUI;

/**
 * The handler for the internationalization.
 */
public class InternationalizationCommandHandler extends AbstractCommandHandler {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.ui.command.AbstractCommandHandler#getCommand(org.eclipse.core.expressions.IEvaluationContext)
	 */
	@Override
	protected Command getCommand(final IEvaluationContext context) {
		Command resultCommand = UnexecutableCommand.INSTANCE;

		final EObject selectedElement = getSelectedElement();

		final URI neededURIResource = getRootNotationURI(selectedElement);

		Resource notationResource = null;
		final Iterator<Resource> resources = selectedElement.eResource().getResourceSet().getResources().iterator();
		while (resources.hasNext() && null == notationResource) {
			final Resource resource = resources.next();
			if (neededURIResource.equals(resource.getURI())) { // $NON-NLS-1$
				notationResource = resource;
			}
		}

		if (null != notationResource) {
			final InternationalizationDialog dialog = new InternationalizationDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), notationResource.getURI(), true);

			if (Window.OK == dialog.open()) {

				final boolean useInternationalizationValue = dialog.getUseInternationalizationValue();
				final Locale localeValue = dialog.getLocaleValue();

				EAnnotation annotation = null;

				// Search the internationalization annotation in the notation
				// resource
				for (final EObject objectContent : notationResource.getContents()) {
					if (objectContent instanceof EAnnotation
							&& InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL
									.equals(((EAnnotation) objectContent).getSource())) {
						annotation = (EAnnotation) objectContent;
					}
				}

				final CompoundCommand compoundCommand = new CompoundCommand("Create internationalization command"); //$NON-NLS-1$

				if (null != annotation) {
					// Remove the existing annotation
					try {
						compoundCommand.append(new RemoveFromResourcecommand(
								ServiceUtilsForResource.getInstance().getTransactionalEditingDomain(notationResource),
								notationResource, annotation));
					} catch (final ServiceException e) {
						Activator.log.error(e);
					}
					compoundCommand
							.append(new UseInternationalizationPreferenceCommand(notationResource.getURI(), false));
				}

				if (useInternationalizationValue && null != localeValue) {
					// Create the annotation if the internationalization value
					// is not false
					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation
							.setSource(InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL);
					if (useInternationalizationValue) {
						annotation.getDetails().put(
								InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL,
								Boolean.toString(useInternationalizationValue));
						compoundCommand.append(new UseInternationalizationPreferenceCommand(notationResource.getURI(),
								useInternationalizationValue));
					}
					if (null != localeValue) {
						annotation.getDetails().put(InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE,
								localeValue.toString());
						compoundCommand.append(new LocaleInternationalizationPreferenceCommand(
								notationResource.getURI(), localeValue.toString()));
					}
					compoundCommand.append(new GMFtoEMFCommandWrapper(new AddToResourceCommand(
							((ModelSet) notationResource.getResourceSet()).getTransactionalEditingDomain(),
							notationResource, annotation)));
				}

				if (!compoundCommand.isEmpty()) {
					resultCommand = compoundCommand;
				}
			}
		}

		return resultCommand;
	}

	/**
	 * Get the root notation URI (this allows to manage the control mode
	 * elements)
	 * 
	 * @param selectedObject
	 *            The initial selected object.
	 * @return The {@link URI} of the root notation.
	 */
	protected URI getRootNotationURI(final EObject selectedObject) {
		// Get the root container of the selected object
		final EObject rootContainer = EcoreUtil.getRootContainer(selectedObject);

		URI rootURI = rootContainer.eResource().getURI();
		// Remove the file extension and add the notation file extension
		return rootURI.trimFileExtension().appendFileExtension(NotationModel.NOTATION_FILE_EXTENSION);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.ui.command.AbstractCommandHandler#setEnabled(java.lang.Object)
	 */
	@Override
	public void setEnabled(final Object evaluationContext) {
		setBaseEnabled(true);
	}
}
