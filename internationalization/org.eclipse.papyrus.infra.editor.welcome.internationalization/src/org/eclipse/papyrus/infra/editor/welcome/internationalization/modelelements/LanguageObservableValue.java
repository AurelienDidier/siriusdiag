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

import org.eclipse.core.databinding.observable.IObserving;
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
import org.eclipse.papyrus.infra.internationalization.common.command.LocaleInternationalizationPreferenceCommand;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationAnnotationResourceUtils;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationPreferenceModel;
import org.eclipse.papyrus.infra.internationalization.ui.Activator;
import org.eclipse.papyrus.infra.internationalization.utils.InternationalizationPreferenceModelUtils;

/**
 * The observable value for the language internationalization preference and annotation detail.
 */
public class LanguageObservableValue extends AbstractObservableValue<String> implements IObserving {

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
	public LanguageObservableValue(final EditingDomain domain) {
		super();
		this.domain = domain;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.IObserving#getObserved()
	 */
	@Override
	public Object getObserved() {
		final InternationalizationPreferenceModel model = InternationalizationPreferenceModelUtils.getInternationalizationPreferenceModel((ModelSet) domain.getResourceSet());
		return model.getPrivateResourceURI();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
	 */
	@Override
	public Object getValueType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doGetValue()
	 */
	@Override
	protected String doGetValue() {
		if(!InternationalizationPreferencesUtils.isInternationalizationNeedToBeLoaded()) {
			return ""; //$NON-NLS-1$
		}
		
		final InternationalizationPreferenceModel model = InternationalizationPreferenceModelUtils.getInternationalizationPreferenceModel((ModelSet) domain.getResourceSet());
		return InternationalizationPreferencesUtils.getLocalePreference(model.getPrivateResourceURI()).toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(final String value) {
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
	protected Command getSetCommand(final String value) {
		boolean needCreateAnnotation = true;

		final InternationalizationPreferenceModel model = InternationalizationPreferenceModelUtils.getInternationalizationPreferenceModel((ModelSet) domain.getResourceSet());
		final Resource resource = model.getResource();

		// Search the internationalization annotation in the notation resource
		EAnnotation annotation = InternationalizationAnnotationResourceUtils.getInternationalizationAnnotation(resource);

		final CompoundCommand compoundCommand = new CompoundCommand("Create internationalization command"); //$NON-NLS-1$

		if (null != annotation) {
			if (annotation.getDetails().containsKey(InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE)) {
				try {
					final TransactionalEditingDomain domainForResource = ServiceUtilsForResource.getInstance().getTransactionalEditingDomain(resource);

					// Remove the details from annotation
					compoundCommand.append(new RemoveEAnnotationDetailCommand(domainForResource, annotation, InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE));

					// If the detail is the only one, delete the annotation
					if (1 == annotation.getDetails().size()) {
						compoundCommand.append(new RemoveFromResourcecommand(domainForResource, resource, annotation));
					} else {
						needCreateAnnotation = false;
					}
				} catch (final ServiceException e) {
					Activator.log.error(e);
				}
			} else {
				needCreateAnnotation = false;
			}
		}

		if (null != value) {
			// Create the annotation if the internationalization value
			// is not false
			if (needCreateAnnotation) {
				annotation = EcoreFactory.eINSTANCE.createEAnnotation();
				annotation
						.setSource(InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL);
				annotation.getDetails().put(InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE,
						value);
				compoundCommand.append(new GMFtoEMFCommandWrapper(new AddToResourceCommand(((ModelSet) resource.getResourceSet()).getTransactionalEditingDomain(), resource, annotation)));
			} else {
				try {
					final TransactionalEditingDomain domainForResource = ServiceUtilsForResource.getInstance().getTransactionalEditingDomain(resource);

					compoundCommand.append(new AddEAnnotationDetailCommand(domainForResource, annotation, InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE, value));
				} catch (final ServiceException e) {
					Activator.log.error(e);
				}
			}

			// Manage the preference by command
			compoundCommand.append(new LocaleInternationalizationPreferenceCommand(model.getPrivateResourceURI(),
					value));
		}

		return !compoundCommand.getCommandList().isEmpty() ? compoundCommand : UnexecutableCommand.INSTANCE;
	}
}