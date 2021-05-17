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

package org.eclipse.papyrus.infra.internationalization.commands;

import java.util.Collection;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationAnnotationResourceUtils;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationPreferenceModel;
import org.eclipse.papyrus.infra.internationalization.utils.InternationalizationPreferenceModelUtils;

/**
 * The internationalization preference command factory that allows to manage the
 * legacy of the storage.
 */
public class InternationalizationPreferenceCommandFactory {

	/**
	 * The model set.
	 */
	private final ModelSet modelSet;

	/**
	 * Constructor.
	 *
	 * @param modelSet
	 *            The model set.
	 */
	public InternationalizationPreferenceCommandFactory(final ModelSet modelSet) {
		super();

		this.modelSet = modelSet;
	}

	/**
	 * Creates a command that toggles whether the sash model is stored in the
	 * private workspace metadata area or in the shared {@code *.di} file.
	 * 
	 * @return a toggle command for the private layout storage
	 */
	public Command createTogglePrivateStorageCommand() {
		Command result = UnexecutableCommand.INSTANCE;

		final InternationalizationPreferenceModel internationalizationPrefModel = InternationalizationPreferenceModelUtils
				.getInternationalizationPreferenceModel(modelSet);
		if (null != internationalizationPrefModel) {
			result = new AbstractToggleCommand("Toggle Private Editor Layout") { //$NON-NLS-1$

				@Override
				public void execute() {

					final EAnnotation annotationToMove = InternationalizationAnnotationResourceUtils
							.getInternationalizationAnnotation(internationalizationPrefModel.getResource());

					// We don't record changes in the internationalization
					// preference model for undo/redo,
					// so we cannot assume that any changes to the current page
					// selections are undoable in the usual way
					if (!internationalizationPrefModel.isLegacyMode()) {
						final Resource internationalizationPrefResource = annotationToMove.eResource();
						final URI sharedURI = internationalizationPrefModel.getPrivateResourceURI();

						// Move the contents into the notation model. If the
						// notation resource isn't loaded, give up because
						// something is seriously wrong in that case
						final Resource notationResource = modelSet.getResource(sharedURI, false);
						if (null != notationResource && notationResource.isLoaded()) {
							moveContents(internationalizationPrefResource, notationResource, annotationToMove);

							if (internationalizationPrefResource.getContents().isEmpty()) {
								// Schedule deletion on save
								modelSet.getResourcesToDeleteOnSave().add(internationalizationPrefResource.getURI());
							}
						}
					} else {
						Resource internationalizationPrefResource;
						final URI privateURI = internationalizationPrefModel.getSharedResourceURI();

						// Move the contents into the internationalization
						// preference model. If the internationalization
						// preference resource isn't loaded or doesn't exist, it
						// will have to be handled
						if (modelSet.getURIConverter().exists(privateURI, null)) {
							internationalizationPrefResource = modelSet.getResource(privateURI, true);
						} else {
							internationalizationPrefResource = modelSet.createResource(privateURI);
						}

						// In case we had marked it for deletion, earlier
						modelSet.getResourcesToDeleteOnSave().remove(privateURI);

						final Resource notationResource = annotationToMove.eResource();
						moveContents(notationResource, internationalizationPrefResource, annotationToMove);
					}

					// Re-load from the new resource. Snippets might find this
					// odd, but it would be even more odd for there to be any
					// snippets on this model
					internationalizationPrefModel.loadModel(modelSet.getURIWithoutExtension());
				}
			};
		}

		return result;
	}

	/**
	 * Move the annotation from the first resource into the second one.
	 * 
	 * @param fromResource
	 *            The resource containing the annotation to move into the second
	 *            resource.
	 * @param toResource
	 *            The resource where move the annotation.
	 * @param annotation
	 *            The annotation to move from first resource into the second
	 *            one.
	 */
	protected void moveContents(final Resource fromResource, final Resource toResource, final EAnnotation annotation) {
		EObject toReplace = null;

		final Collection<EObject> existingAnnotations = EcoreUtil.getObjectsByType(toResource.getContents(),
				annotation.eClass());
		if (null != existingAnnotations && existingAnnotations.isEmpty()) {
			for (final EObject existingAnnotation : existingAnnotations) {
				if (existingAnnotation instanceof EAnnotation && ((EAnnotation) existingAnnotation).getSource()
						.equals(InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL)) {
					toReplace = existingAnnotation;
				}
			}
		}

		if (null != toReplace) {
			EcoreUtil.replace(toReplace, annotation);
		} else {
			toResource.getContents().add(annotation);
		}
	}

	/**
	 * The command for the toggle action. We don't need recording command here
	 * because this is managed by properties API that will be wrapped as
	 * recording command.
	 */
	private static abstract class AbstractToggleCommand extends AbstractCommand {

		/**
		 * Constructor.
		 *
		 * @param label
		 *            The label of the command.
		 */
		public AbstractToggleCommand(final String label) {
			super(label);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
		 */
		@Override
		protected boolean prepare() {
			// Nothing to prepare
			return true;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.emf.common.command.AbstractCommand#undo()
		 */
		@Override
		public void undo() {
			// The command modified a boolean value so the undo will do the
			// contrary of the previous command
			execute();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.emf.common.command.Command#redo()
		 */
		@Override
		public void redo() {
			execute();
		}
	}

}
