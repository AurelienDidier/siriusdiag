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

package org.eclipse.papyrus.infra.editor.welcome.internationalization.modelelements;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.emf.commands.AddEAnnotationDetailCommand;
import org.eclipse.papyrus.infra.emf.commands.AddToResourceCommand;
import org.eclipse.papyrus.infra.emf.commands.RemoveEAnnotationDetailCommand;
import org.eclipse.papyrus.infra.emf.commands.RemoveFromResourcecommand;
import org.eclipse.papyrus.infra.emf.gmf.command.GMFtoEMFCommandWrapper;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForResource;
import org.eclipse.papyrus.infra.internationalization.common.command.UseInternationalizationPreferenceCommand;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationAnnotationResourceUtils;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationPreferenceModel;
import org.eclipse.papyrus.infra.internationalization.ui.Activator;
import org.eclipse.papyrus.infra.internationalization.utils.InternationalizationPreferenceModelUtils;

/**
 * The observable value for the use internationalization preference and annotation detail.
 */
public class UseInternationalizationObservableValue extends AbstractObservableValue<Boolean> {

	/**
	 * The editing domain to execute the command.
	 */
	private EditingDomain domain;

	/**
	 * Constructor.
	 *
	 * @param domain
	 *            The editing domain.
	 */
	public UseInternationalizationObservableValue(final EditingDomain domain) {
		super();
		this.domain = domain;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
	 */
	@Override
	public Object getValueType() {
		return Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doGetValue()
	 */
	@Override
	protected Boolean doGetValue() {
		if(!InternationalizationPreferencesUtils.isInternationalizationNeedToBeLoaded()) {
			return false;
		}
		final InternationalizationPreferenceModel model = InternationalizationPreferenceModelUtils.getInternationalizationPreferenceModel((ModelSet) domain.getResourceSet());
		return InternationalizationPreferencesUtils.getInternationalizationPreference(model.getPrivateResourceURI());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(final Boolean value) {
		final Command command = getSetCommand(value);
		domain.getCommandStack().execute(command);
	}

	/**
	 * Returns the command used to edit the value.
	 *
	 * @param value
	 *            The new value
	 * @return
	 * 		The Set command used to edit the value
	 */
	protected Command getSetCommand(final Boolean value) {
		boolean needCreateAnnotation = true;

		final InternationalizationPreferenceModel model = InternationalizationPreferenceModelUtils.getInternationalizationPreferenceModel((ModelSet) domain.getResourceSet());
		final Resource resource = model.getResource();

		// Search the internationalization annotation in the notation resource
		EAnnotation annotation = InternationalizationAnnotationResourceUtils.getInternationalizationAnnotation(resource);

		final CompoundCommand compoundCommand = new CompoundCommand("Create internationalization command"); //$NON-NLS-1$

		if (null != annotation) {
			if (annotation.getDetails().containsKey(InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL)) {
				try {
					final TransactionalEditingDomain domainForResource = ServiceUtilsForResource.getInstance().getTransactionalEditingDomain(resource);

					// Remove the details from annotation
					compoundCommand.append(new RemoveEAnnotationDetailCommand(domainForResource, annotation, InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL));

					// If the detail is the only one, delete the annotation
					if (1 == annotation.getDetails().size()) {
						compoundCommand.append(new RemoveFromResourcecommand(domainForResource, resource, annotation));
					} else {
						needCreateAnnotation = false;
					}
				} catch (final ServiceException e) {
					Activator.log.error(e);
				}

				// Remove the preference by command
				compoundCommand.append(new UseInternationalizationPreferenceCommand(model.getPrivateResourceURI(), false));
			} else {
				needCreateAnnotation = false;
			}
		}

		if (value) {
			// Create the annotation if the internationalization value
			// is not false
			if (needCreateAnnotation) {
				annotation = EcoreFactory.eINSTANCE.createEAnnotation();
				annotation.setSource(InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL);
				annotation.getDetails().put(
						InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL,
						Boolean.toString(value));
				compoundCommand.append(new GMFtoEMFCommandWrapper(new AddToResourceCommand(((ModelSet) resource.getResourceSet()).getTransactionalEditingDomain(), resource, annotation)));
			} else {
				try {
					final TransactionalEditingDomain domainForResource = ServiceUtilsForResource.getInstance().getTransactionalEditingDomain(resource);

					compoundCommand.append(new AddEAnnotationDetailCommand(domainForResource, annotation, InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL, Boolean.toString(value)));
				} catch (final ServiceException e) {
					Activator.log.error(e);
				}
			}

			// Manage the preference by command
			compoundCommand.append(new UseInternationalizationPreferenceCommand(model.getPrivateResourceURI(),
					value));
		}

		return !compoundCommand.getCommandList().isEmpty() ? compoundCommand : UnexecutableCommand.INSTANCE;
	}
}
