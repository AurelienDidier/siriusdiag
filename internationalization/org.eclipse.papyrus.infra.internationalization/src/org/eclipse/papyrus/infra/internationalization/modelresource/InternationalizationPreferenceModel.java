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

package org.eclipse.papyrus.infra.internationalization.modelresource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.papyrus.infra.core.resource.EMFLogicalModel;
import org.eclipse.papyrus.infra.core.resource.IModel;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.emf.commands.AddToResourceCommand;
import org.eclipse.papyrus.infra.emf.gmf.command.GMFtoEMFCommandWrapper;
import org.eclipse.papyrus.infra.internationalization.Activator;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationAnnotationResourceUtils;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;

/**
 * The model for the internationalization preference. It must be managed as
 * legacy (platform:/resource/model/model.notation) or not
 * (file:/~workspace/.metadata/.plugins/org.eclipse.papyrus.infra.core/model/model.internationalization).
 * The internationalization preference will be managed as EAnnotation.
 */
public class InternationalizationPreferenceModel extends EMFLogicalModel implements IModel {

	/**
	 * Model ID.
	 */
	public static final String MODEL_ID = "org.eclipse.papyrus.infra.internationalization.InternationalizationPreferenceModel"; //$NON-NLS-1$

	/**
	 * The private resource URI property.
	 */
	public static final String PROPERTY_PRIVATE_RESOURCE_URI = "privateResourceURI"; //$NON-NLS-1$

	/**
	 * The shared resource URI property.
	 */
	public static final String PROPERTY_SHARED_RESOURCE_URI = "sharedResourceURI"; //$NON-NLS-1$

	/**
	 * The legacy mode property.
	 */
	public static final String PROPERTY_LEGACY_MODE = "legacyMode"; //$NON-NLS-1$

	/**
	 * File extension for the internationalization preference model.
	 */
	public static final String INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION = "internationalization"; //$NON-NLS-1$

	/**
	 * The notation extension file (cannot use the
	 * NotationModel.NOTATION_FILE_EXTENSION because of dependencies cycle).
	 */
	public static final String NOTATION_FILE_EXTENSION = "notation"; //$NON-NLS-1$

	/**
	 * The provider manager of the internationalization preference.
	 */
	private InternationalizationPreferenceModelProviderManager providerManager;

	/**
	 * The property change support for the legacy mode.
	 */
	private final PropertyChangeSupport bean = new PropertyChangeSupport(this);

	/**
	 * The model storage adapter for the internationalization preference
	 */
	private Adapter internationalizationPreferenceModelStorageAdapter;

	/**
	 * Boolean to determinate if the resource is managed as legacy.
	 */
	private volatile Boolean legacyMode;

	/**
	 * Constructor.
	 */
	public InternationalizationPreferenceModel() {
		super();

		internationalizationPreferenceModelStorageAdapter = new AdapterImpl() {
			@Override
			public void notifyChanged(final Notification msg) {
				if (getResources().contains(msg.getNotifier())) {
					switch (msg.getFeatureID(Resource.class)) {
					case Resource.RESOURCE__CONTENTS:
						invalidateLegacyMode();
						break;
					}
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractModel#init(org.eclipse.papyrus.infra.core.resource.ModelSet)
	 */
	@Override
	public void init(final ModelSet modelSet) {
		super.init(modelSet);

		this.providerManager = new InternationalizationPreferenceModelProviderManager(modelSet);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.EMFLogicalModel#unload()
	 */
	@Override
	public void unload() {
		if (null != providerManager) {
			providerManager.dispose();
			providerManager = null;
		}

		getResources().forEach(res -> res.eAdapters().remove(internationalizationPreferenceModelStorageAdapter));

		super.unload();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.EMFLogicalModel#isRelatedResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public boolean isRelatedResource(final Resource resource) {
		boolean result = false;

		if (null != resource) {
			// We only handle the main internationalization preference resource.
			// Imported *.internationalization are not relevant
			if (resource == getResource()) {
				result = true;
			} else {
				// We can only calculate these related URIs if the ModelSet is
				// initialized
				result = resource.getURI().equals(getSharedResourceURI())
						|| resource.getURI().equals(getPrivateResourceURI());
			}
		}

		return result;
	}

	/**
	 * Just open this method to public.
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractModel#getModelManager()
	 */
	@Override
	public ModelSet getModelManager() {
		return super.getModelManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.EMFLogicalModel#configureResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	protected void configureResource(final Resource resourceToConfigure) {
		super.configureResource(resourceToConfigure);

		if (null != resourceToConfigure) {
			resourceToConfigure.eAdapters().add(internationalizationPreferenceModelStorageAdapter);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#loadModel(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public void loadModel(final URI uriWithoutExtension) {
		final URI sashModelURI = getInternationalizationPreferenceModelURI(uriWithoutExtension);
		this.resourceURI = sashModelURI;

		try {
			super.loadModel(sashModelURI.trimFileExtension());
			loadInternationalizationPreference(sashModelURI);
		} catch (final Exception ex) {
			// Unload it and try again by creating it
			final Resource resource = getModelManager().getResource(sashModelURI, false);
			if (null != resource) {
				resource.unload();
				resource.getResourceSet().getResources().remove(resource);
			}

			// This will create the private internationalization preference
			// model if that's what we need
			createModel(uriWithoutExtension);
		}

		if (null == resource) {
			// This will create the private internationalization preference
			// model if that's what we need
			createModel(uriWithoutExtension);
		}
	}

	/**
	 * This allows to load the internationalization preference as annotation in
	 * the root content.
	 * 
	 * @param uri
	 *            The URI of the properties file with the extension.
	 */
	protected void loadInternationalizationPreference(final URI uri) {
		if (null != resource && null != resource.getContents() && !resource.getContents().isEmpty()) {

			boolean useInternationalizationValue = false;
			String languagePreference = null;

			// Get the internationalization annotation
			final EAnnotation annotation = InternationalizationAnnotationResourceUtils
					.getInternationalizationAnnotation(resource);

			// Get the details values
			if (null != annotation) {
				// Get the 'use' details value
				final String useInternationalizationStringValue = annotation.getDetails()
						.get(InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL);
				useInternationalizationValue = Boolean.parseBoolean(useInternationalizationStringValue);

				// Get the 'language' details value
				final String languageValue = annotation.getDetails()
						.get(InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE);

				languagePreference = languageValue;
			}

			// Set values into the preference store corresponding to the
			// papyrus model
			URI sharedResource = getPrivateResourceURI();
			InternationalizationPreferencesUtils.setInternationalizationPreference(sharedResource,
					useInternationalizationValue);
			if (null != languagePreference) {
				InternationalizationPreferencesUtils.setLanguagePreference(sharedResource,
						languagePreference.toString());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#createModel(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public void createModel(final URI uriWithoutExtension) {
		if (isLegacy(uriWithoutExtension)) {
			final URI internationalizationPrefURI = getInternationalizationPreferenceModelStoreURI(uriWithoutExtension);
			try {
				super.createModel(internationalizationPrefURI.trimFileExtension());
				intantiateTemplate(internationalizationPrefURI);
				loadInternationalizationPreference(uriWithoutExtension);
			} catch (final IOException e) {
				Activator.log.error("Failed to initialize workspace private internationalization model", e); //$NON-NLS-1$
				super.createModel(internationalizationPrefURI.trimFileExtension());
			}
		} else {
			super.createModel(uriWithoutExtension);
		}
	}

	/**
	 * Intantiates the internationalization preference resource template on the
	 * given URI.
	 * 
	 * @param internationalizationPrefResourceURI
	 *            The new internationalization preference resource URI.
	 * @throws IOException
	 *             on failure to copy the template to this URI for any reason
	 */
	protected void intantiateTemplate(final URI internationalizationPrefResourceURI) throws IOException {

		// Create the internationalization annotation with
		// - Use internationalization preference to true
		// - Language preference to default language
		final EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL);
		annotation.getDetails().put(InternationalizationPreferencesConstants.USE_DETAIL_ANNOTATION_LABEL,
				Boolean.toString(true));
		annotation.getDetails().put(InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE,
				Locale.getDefault().toString());

		// The command to add the annotation in resource content
		final GMFtoEMFCommandWrapper command = new GMFtoEMFCommandWrapper(new AddToResourceCommand(
				((ModelSet) resource.getResourceSet()).getTransactionalEditingDomain(), resource, annotation));
		command.execute();

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.EMFLogicalModel#setModelURI(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public void setModelURI(final URI uriWithoutExtension) {
		final URI oldPrivateURI = getSharedResourceURI();
		final URI oldSharedURI = getPrivateResourceURI();

		URI newURI;
		if ((null != resourceURI) && isLegacy(resourceURI.trimFileExtension())) {
			newURI = getLegacyURI(uriWithoutExtension);
		} else {
			newURI = getInternationalizationPreferenceModelStoreURI(uriWithoutExtension);
		}

		super.setModelURI(newURI.trimFileExtension());

		bean.firePropertyChange(PROPERTY_PRIVATE_RESOURCE_URI, oldPrivateURI, getSharedResourceURI());
		bean.firePropertyChange(PROPERTY_SHARED_RESOURCE_URI, oldSharedURI, getPrivateResourceURI());
	}

	/**
	 * Determinates if this is the legacy URI.
	 * 
	 * @param uriWithoutExtension
	 *            The uri to check.
	 * @return <code>true</code> if the uri is corresponding to the legacy mode,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isLegacy(final URI uriWithoutExtension) {
		boolean result = false;
		if (null != uriWithoutExtension) {
			result = Objects.equals(uriWithoutExtension, getModelManager().getURIWithoutExtension());
		}
		return result;
	}

	/**
	 * Returns the internationalization preference model URI (With file
	 * extension)
	 *
	 * It may be either the Legacy URI (platform:/resource/model/model.notation)
	 * or the 1.0.0 URI
	 * (file:/~workspace/.metadata/.plugins/org.eclipse.papyrus.infra.core/model/model.internationalization)
	 *
	 * @param uriWithoutExtension
	 *            The initial URI.
	 * @return The internationalization preference model URI.
	 */
	protected URI getInternationalizationPreferenceModelURI(final URI uriWithoutExtension) {
		final URIConverter converter = getModelManager().getURIConverter();
		final URI legacyURI = getLegacyURI(uriWithoutExtension);

		// If the notation file exists and contains the internationalization
		// annotation, this is a
		// legacy model
		if (converter.exists(legacyURI, Collections.emptyMap())) {
			try {
				final Resource notationResource = getModelManager().getResource(legacyURI, true);
				if (null != InternationalizationAnnotationResourceUtils
						.getInternationalizationAnnotation(notationResource)) {
					return legacyURI;
				}
			} catch (final Exception ex) {
				// Temporary workaround: the notation file may exist and be
				// empty
				// Log the error and continue
				Activator.log.error(ex);
			}
		}

		return getInternationalizationPreferenceModelStoreURI(uriWithoutExtension);
	}

	/**
	 * Get the legacy URI. It must be like
	 * platform:/resource/model/model.notation.
	 * 
	 * @param uriWithoutExtension
	 *            The initial uri.
	 * @return The legacy URI.
	 */
	protected URI getLegacyURI(final URI uriWithoutExtension) {
		return uriWithoutExtension.appendFileExtension(NOTATION_FILE_EXTENSION);
	}

	/**
	 * Get the internationalization preference model store URI. It must be like
	 * file:/~workspace/.metadata/.plugins/org.eclipse.papyrus.infra.core/model/model.internationalization.
	 * 
	 * @param uriWithoutExtension
	 *            The initial URI.
	 * @return The non-legacy URI.
	 */
	protected URI getInternationalizationPreferenceModelStoreURI(final URI uriWithoutExtension) {
		URI fullURI = uriWithoutExtension.appendFileExtension(INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION);
		return providerManager.getInternationalizationPreferenceModelProvider(fullURI)
				.getInternationalizationPreferenceModelURI(fullURI);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#getSaveOptions()
	 */
	@Override
	protected Map<Object, Object> getSaveOptions() {
		final Map<Object, Object> saveOptions = super.getSaveOptions();

		saveOptions.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.FALSE);
		saveOptions.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.FALSE);
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

		return saveOptions;
	}

	/**
	 * Get the legacy mode.
	 * 
	 * @return <code>true</code> if this is the legacy mode, <code>false</code>
	 *         otherwise.
	 */
	public boolean isLegacyMode() {
		if (null == legacyMode) {
			legacyMode = false; // Assume not

			// Does the shared DI resource contain the sash layout?
			final URI sharedURI = getPrivateResourceURI();
			if (null != sharedURI) {
				for (final Resource next : getResources()) {
					if (sharedURI.equals(next.getURI())) {
						legacyMode = null != InternationalizationAnnotationResourceUtils
								.getInternationalizationAnnotation(next);
						break;
					}
				}
			}
		}

		return legacyMode;
	}

	/**
	 * This allows to recalculate the legacy mode.
	 */
	protected void invalidateLegacyMode() {
		final boolean oldValue = isLegacyMode();

		legacyMode = null;

		final boolean newValue = isLegacyMode();

		if (oldValue != newValue) {
			bean.firePropertyChange(PROPERTY_LEGACY_MODE, oldValue, newValue);
		}
	}

	/**
	 * Gets the URI of the internationalization preference model resource in the
	 * shared (collocated with the user model) area, irrespective of whether
	 * that actually is the resource that currently stores the
	 * internationalization preference model.
	 * 
	 * @return the shared internationalization preference model resource URI
	 */
	public URI getSharedResourceURI() {
		URI modelURI = (null == getModelManager()) ? null : getModelManager().getURIWithoutExtension();
		return (null == modelURI) ? null : getInternationalizationPreferenceModelStoreURI(modelURI);
	}

	/**
	 * Gets the URI of the internationalization preference model resource in the
	 * user private area, irrespective of whether that actually is the resource
	 * that currently stores the internationalization preference model.
	 * 
	 * @return the private sash-model resource URI
	 */
	public URI getPrivateResourceURI() {
		URI modelURI = (null == getModelManager()) ? null : getModelManager().getURIWithoutExtension();
		return (null == modelURI) ? null : modelURI.appendFileExtension(NOTATION_FILE_EXTENSION);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#isRootElement(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected boolean isRootElement(final EObject object) {
		return super.isRootElement(object) && object instanceof EAnnotation
				&& InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL
						.equals(((EAnnotation) object).getSource());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.EMFLogicalModel#isSupportedRoot(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected boolean isSupportedRoot(final EObject object) {
		return object instanceof EAnnotation
				&& InternationalizationPreferencesConstants.INTERNATIONALIZATION_ANNOTATION_LABEL
						.equals(((EAnnotation) object).getSource());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractBaseModel#getModelFileExtension()
	 */
	@Override
	public String getModelFileExtension() {
		if (isLegacy((null == resourceURI) ? resourceURI : resourceURI.trimFileExtension())) {
			return NOTATION_FILE_EXTENSION;
		} else {
			return INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AbstractModel#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return MODEL_ID;
	}

	/**
	 * This allows to add a property change listener.
	 * 
	 * @param listener
	 *            The property change listener to add.
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		bean.addPropertyChangeListener(listener);
	}

	/**
	 * This allows to remove a property change listener.
	 * 
	 * @param listener
	 *            The property change listener to remove.
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		bean.removePropertyChangeListener(listener);
	}

	/**
	 * This allows to add a property change listener for a property name..
	 * 
	 * @param propertyName
	 *            The property name for which one add the property change
	 *            listener.
	 * @param listener
	 *            The property change listener to add.
	 */
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		bean.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * This allows to remove a property change listener for a property name..
	 * 
	 * @param propertyName
	 *            The property name for which one remove the property change
	 *            listener.
	 * @param listener
	 *            The property change listener to remove.
	 */
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		bean.removePropertyChangeListener(propertyName, listener);
	}

}
