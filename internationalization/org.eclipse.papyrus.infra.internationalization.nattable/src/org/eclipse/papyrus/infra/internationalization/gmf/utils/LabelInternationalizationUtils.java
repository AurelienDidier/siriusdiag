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

package org.eclipse.papyrus.infra.internationalization.gmf.utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForResourceSet;
import org.eclipse.papyrus.infra.internationalization.InternationalizationEntry;
import org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.nattable.model.nattable.Table;

/**
 * This allows to manage the utils methods for the internationalization.
 */
public class LabelInternationalizationUtils {

	/**
	 * This allows to get the label without getting name if the label is null.
	 *
	 * @param eObject
	 *            The object.
	 * @param localize
	 *            Boolean to determinate if the localize must be used or not.
	 * @return The label of the object.
	 */
	public static String getLabelWithoutSubstract(final EObject eObject, final boolean localize) {
		String result = null;

		try {
			final InternationalizationModelResource internationalizationResource = getInternationalizationModelResource(
					eObject.eResource());

			if (null != internationalizationResource) {
				result = internationalizationResource.getValueForEntryKey(eObject.eResource().getURI(), eObject);
			}
		} catch (final Exception e) {
			// Do nothing
		}

		return result;
	}

	/**
	 * This allows to set the label.
	 *
	 * @param eObject
	 *            The object.
	 * @param value
	 *            The value to set.
	 * @param locale
	 *            The locale for which set the value (if <code>null</code> set
	 *            it for the current locale).
	 */
	public static void setLabel(final EObject eObject, final String value, final Locale locale) {
		try {
			final InternationalizationModelResource internationalizationResource = getInternationalizationModelResource(
					eObject.eResource());

			final EObject parentEObject = getParentEObject(eObject);

			if (null != internationalizationResource) {

				Locale localeToUse = locale;
				if (null == localeToUse) {
					localeToUse = InternationalizationPreferencesUtils.getLocalePreference(parentEObject);
				}

				internationalizationResource.setValue(parentEObject.eResource().getURI(), eObject, value, localeToUse);
			}
		} catch (final Exception e) {
			// Do nothing
		}
	}

	/**
	 * This allows to get the set label command.
	 *
	 * @param domain
	 *            The editing domain to use.
	 * @param eObject
	 *            The object.
	 * @param value
	 *            The value to set.
	 * @param locale
	 *            The locale for which set the value (if <code>null</code> set
	 *            it for the current locale).
	 * @return The command which allow to set the label.
	 */
	public static Command getSetLabelCommand(final EditingDomain domain, final EObject eObject, final String value,
			final Locale locale) {
		Command resultCommand = null;

		final EObject parentEObject = getParentEObject(eObject);

		try {
			final InternationalizationModelResource internationalizationResource = getInternationalizationModelResource(
					parentEObject.eResource());

			if (null != internationalizationResource) {

				Locale localeToUse = locale;
				if (null == localeToUse) {
					localeToUse = InternationalizationPreferencesUtils.getLocalePreference(parentEObject);
				}

				resultCommand = internationalizationResource.getSetValueCommand(domain,
						parentEObject.eResource().getURI(), eObject, value, localeToUse);
			}
		} catch (final Exception e) {
			// Do nothing
		}
		return null != resultCommand ? resultCommand : UnexecutableCommand.INSTANCE;
	}

	/**
	 * This allows to get the {@link InternationalizationEntry} corresponding to
	 * the object and the key.
	 *
	 * @param eObject
	 *            The object to search.
	 * @param key
	 *            The key to search in the internationalization library.
	 * @return The corresponding {@link InternationalizationEntry}.
	 */
	public static InternationalizationEntry getInternationalizationEntry(EObject eObject, Object key) {
		return getInternationalizationEntry(eObject, key, null);
	}

	/**
	 * This allows to get the {@link InternationalizationEntry} corresponding to
	 * the object and the key.
	 *
	 * @param eObject
	 *            The object to search.
	 * @param key
	 *            The key to search in the internationalization library.
	 * @param locale
	 *            The locale of which get the value
	 * @return The corresponding {@link InternationalizationEntry}.
	 */
	public static InternationalizationEntry getInternationalizationEntry(EObject eObject, Object key, Locale locale) {
		EObject parent = getParentEObject(eObject);
		if (parent != null) {
			Resource resource = parent.eResource();
			try {
				InternationalizationModelResource internationalizationResource = getInternationalizationModelResource(resource);

				if (internationalizationResource != null && resource != null) {
					if (locale == null) {
						locale = InternationalizationPreferencesUtils.getLocalePreference(parent);
					}

					return internationalizationResource.getEntryForKey(resource.getURI(), key, locale);
				}
			} catch (Exception e) {
				// Do nothing
			}
		}

		return null;
	}

	/**
	 * Get the available locales for the resource.
	 *
	 * @param resource
	 *            The resource.
	 * @return The available locales of loaded properties files for resource.
	 */
	public static Set<Locale> getAvailableLocales(final Resource resource) {
		Set<Locale> locales = null;

		try {
			final InternationalizationModelResource internationalizationResource = getInternationalizationModelResource(
					resource);

			locales = internationalizationResource.getAvailablePropertiesLocales(resource.getURI());
		} catch (final Exception e) {
			// Do nothing
		}

		return null != locales ? locales : new HashSet<>();
	}

	/**
	 * Get the internationalizationModelResource from the model set.
	 *
	 * @param resource
	 *            The initial resource.
	 * @return The internationalization model resource.
	 * @throws ServiceException
	 *             The service registry exception.
	 * @throws NotFoundException
	 *             The model not found exception.
	 */
	public static InternationalizationModelResource getInternationalizationModelResource(final Resource resource)
			throws ServiceException, NotFoundException {
		final ServicesRegistry servicesRegistry = ServiceUtilsForResourceSet.getInstance()
				.getServiceRegistry(resource.getResourceSet());
		final ModelSet modelSet = ServiceUtils.getInstance().getModelSet(servicesRegistry);
		return (InternationalizationModelResource) modelSet.getModelChecked(InternationalizationModelResource.MODEL_ID);
	}

	/**
	 * This allows to manage part label synchronizer for the object
	 * corresponding to the editor part.
	 *
	 * @param eObject
	 *            The EObject (seems to be Table).
	 * @param editorPart
	 *            The editor part corresponding to the EObject.
	 */
	public static void managePartLabelSynchronizer(final EObject eObject,
			final IInternationalizationEditor editorPart) {
		try {
			final InternationalizationModelResource internationalizationResource = getInternationalizationModelResource(
					eObject.eResource());

			internationalizationResource.addEditorPartForEObject(eObject, editorPart);
		} catch (final Exception e) {
			// Do nothing
		}
	}

	/**
	 * Get the eObject or diagram/table owner if needed.
	 *
	 * @param eObject
	 *            The initial EObject.
	 * @return The eObject or diagram/table owner.
	 */
	protected static EObject getParentEObject(final EObject eObject) {
		EObject parentEObject = eObject;
		if (eObject instanceof Table) {
			parentEObject = ((Table) eObject).getOwner();
			if (null == parentEObject) {
				parentEObject = ((Table) eObject).getContext();
			}
		}
		return parentEObject;
	}
}
