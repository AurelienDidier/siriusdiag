/*****************************************************************************
 * Copyright (c) 2016, 2017 CEA LIST, Christian W. Damus, and others.
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
 *   Christian W. Damus - bug 528343
 *   
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.common.utils;

import static org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants.LANGUAGE_PREFERENCE;
import static org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesConstants.USE_INTERNATIONALIZATION_PREFERENCE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.sasheditor.DiModel;
import org.eclipse.papyrus.infra.internationalization.common.Activator;
import org.eclipse.papyrus.infra.internationalization.common.IPreferenceStoreListener;

/**
 * The internationalization preference utils methods which allow to get or
 * change the internationalization preference value.
 */
public class InternationalizationPreferencesUtils {

	private static final CopyOnWriteArrayList<InternationalizationPreferenceListener> listeners = new CopyOnWriteArrayList<>();

	static {
		IPreferenceStoreListener preferenceStoreListener = new IPreferenceStoreListener() {
			private final Map<IPreferenceStore, IPropertyChangeListener> forwarders = new WeakHashMap<>();

			@Override
			public void preferenceStoreCreated(IProject project, String papyrusProject, IPreferenceStore store) {
				store.addPropertyChangeListener(forwarders.computeIfAbsent(store, __ -> createForwarder(project, papyrusProject)));
			}

			@Override
			public void preferenceStoreDisposed(IProject project, String papyrusProject, IPreferenceStore store) {
				// The equal object will be removed from the store's listeners
				store.removePropertyChangeListener(forwarders.get(store));
			}

			IPropertyChangeListener createForwarder(IProject project, String papyrusProject) {
				return event -> preferenceStorePropertyChanged(project, papyrusProject, event);
			}
		};

		Activator.getDefault().addPreferenceStoreListener(preferenceStoreListener);
	}

	/**
	 * This allows to get the load of internationalization preference value.
	 * 
	 * @return <code>true</code> if the internationalization need to be loaded, <code>false</code> otherwise.
	 */
	public static boolean isInternationalizationNeedToBeLoaded() {
		boolean result = false;

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		if (null != preferenceStore) {
			if (preferenceStore.contains(InternationalizationPreferencesConstants.LOAD_INTERNATIONALIZATION)) {
				result = preferenceStore.getBoolean(InternationalizationPreferencesConstants.LOAD_INTERNATIONALIZATION);
			}
		}

		return result;
	}

	/**
	 * This allows to get the load of internationalization of external files preference value.
	 * 
	 * @return <code>true</code> if the internationalization of external files need to be loaded, <code>false</code> otherwise.
	 */
	public static boolean isInternationalizationExternalFilesNeedToBeLoaded() {
		boolean result = false;

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		if (null != preferenceStore) {
			if (preferenceStore.contains(InternationalizationPreferencesConstants.LOAD_INTERNATIONALIZATION_OF_EXTERNAL_FILES)) {
				result = preferenceStore.getBoolean(InternationalizationPreferencesConstants.LOAD_INTERNATIONALIZATION_OF_EXTERNAL_FILES);
			}
		}

		return result;
	}


	/**
	 * Get the preference store for the project containing the {@link EObject}
	 * in parameter.
	 * 
	 * @param eObject
	 *            The eObject.
	 * @return The preference store for the project containing the EObject.
	 */
	public static IPreferenceStore getPreferenceStore(final EObject eObject) {
		URI resourceURI = eObject.eResource().getURI();
		final ResourceSet resourceSet = eObject.eResource().getResourceSet();
		if (resourceSet instanceof ModelSet && null != ((ModelSet) resourceSet).getURIWithoutExtension()) {
			resourceURI = ((ModelSet) resourceSet).getURIWithoutExtension().appendFileExtension(DiModel.DI_FILE_EXTENSION);
		}
		return getPreferenceStore(resourceURI);
	}

	/**
	 * Get the preference store for the project containing the {@link Resource}
	 * in parameter.
	 * 
	 * @param resource
	 *            The resource.
	 * @return The preference store for the project containing the Resource.
	 */
	public static IPreferenceStore getPreferenceStore(final Resource resource) {
		IPreferenceStore result = null;

		if (null != resource) {
			result = getPreferenceStore(resource.getURI());
		}

		return result;
	}

	/**
	 * Get the preference store for the project containing the resource
	 * {@link URI} in parameter.
	 * 
	 * @param resource
	 *            The resource.
	 * @return The preference store for the project containing the resource URI.
	 */
	public static IPreferenceStore getPreferenceStore(final URI uri) {
		IPreferenceStore result = null;

		if (null != uri) {
			final String filePathString = CommonPlugin.resolve(uri).toFileString();

			if (null != filePathString) {
				final IFile resourceFile = ResourcesPlugin.getWorkspace().getRoot()
						.getFileForLocation(new Path(filePathString));

				if (null != resourceFile) {
					final IProject currentProject = resourceFile.getProject();
					final String papyrusProjectName = uri.trimFileExtension().lastSegment();

					if (null != currentProject && !papyrusProjectName.isEmpty()) {
						result = Activator.getDefault().getInternationalizationPreferenceStore(currentProject,
								papyrusProjectName);
					}
				}
			}
		}

		return result;
	}

	/**
	 * This allows to modify the internationalization preference value.
	 * 
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @param value
	 *            The new preference value.
	 */
	public static void setInternationalizationPreference(final EObject eObject, final boolean value) {
		URI resourceURI = eObject.eResource().getURI();
		final ResourceSet resourceSet = eObject.eResource().getResourceSet();
		if (resourceSet instanceof ModelSet && null != ((ModelSet) resourceSet).getURIWithoutExtension()) {
			resourceURI = ((ModelSet) resourceSet).getURIWithoutExtension().appendFileExtension(DiModel.DI_FILE_EXTENSION);
		}
		setInternationalizationPreference(resourceURI, value);
	}

	/**
	 * This allows to modify the internationalization preference value.
	 * 
	 * @param resource
	 *            The {@link Resource) to get the papyrus project preferences.
	 * @param value
	 *            The new preference value.
	 */
	public static void setInternationalizationPreference(final Resource resource, final boolean value) {
		setInternationalizationPreference(resource.getURI(), value);
	}

	/**
	 * This allows to modify the internationalization preference value.
	 * 
	 * @param resourceURI
	 *            The resource URI to get the papyrus project preferences.
	 * @param value
	 *            The new preference value.
	 */
	public static void setInternationalizationPreference(final URI resourceURI, final boolean value) {
		final IPreferenceStore preferenceStore = getPreferenceStore(resourceURI);

		if (null != preferenceStore) {
			preferenceStore.setValue(USE_INTERNATIONALIZATION_PREFERENCE,
					value);
		}
	}

	/**
	 * This allows to get the internationalization preference value.
	 * 
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @return <code>true</code> if the preference value is set to true,
	 *         <code>false</code> otherwise.
	 */
	public static boolean getInternationalizationPreference(final EObject eObject) {
		boolean result = false;
		if (null != eObject && null != eObject.eResource()) {
			URI resourceURI = eObject.eResource().getURI();
			final ResourceSet resourceSet = eObject.eResource().getResourceSet();
			if (resourceSet instanceof ModelSet && null != ((ModelSet) resourceSet).getURIWithoutExtension()) {
				resourceURI = ((ModelSet) resourceSet).getURIWithoutExtension().appendFileExtension(DiModel.DI_FILE_EXTENSION);
			}
			result = getInternationalizationPreference(resourceURI);
		}
		return result;
	}

	/**
	 * This allows to get the internationalization preference value.
	 * 
	 * @param resource
	 *            The {@link Resource) to get the papyrus project preferences.
	 * @return <code>true</code> if the preference value is set to true,
	 *         <code>false</code> otherwise.
	 */
	public static boolean getInternationalizationPreference(final Resource resource) {
		return getInternationalizationPreference(resource.getURI());
	}

	/**
	 * This allows to get the internationalization preference value.
	 * 
	 * @param resourceURI
	 *            The resource URI to get the papyrus project preferences.
	 * @return <code>true</code> if the preference value is set to true,
	 *         <code>false</code> otherwise.
	 */
	public static boolean getInternationalizationPreference(final URI resourceURI) {
		boolean result = false;

		final IPreferenceStore preferenceStore = getPreferenceStore(resourceURI);

		if (null != preferenceStore && preferenceStore
				.contains(USE_INTERNATIONALIZATION_PREFERENCE)) {
			result = preferenceStore
					.getBoolean(USE_INTERNATIONALIZATION_PREFERENCE);
		}

		return result;
	}

	/**
	 * Get the language preference (create it with the current locale if not
	 * existing).
	 * 
	 * @param resource
	 *            The {@link Resource) to get the papyrus project preferences.
	 * @return The language in the preferences.
	 */
	private static String getLanguagePreference(final URI uri) {
		String result = null;

		final IPreferenceStore preferenceStore = getPreferenceStore(uri);
		if (null != preferenceStore
				&& preferenceStore.contains(LANGUAGE_PREFERENCE)) {
			result = preferenceStore.getString(LANGUAGE_PREFERENCE);
		}

		return result;
	}

	/**
	 * Get the locale preference created from the language preference.
	 * 
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @return The locale preference.
	 */
	public static Locale getLocalePreference(final EObject eObject) {
		Locale result = null;
		if (null != eObject && null != eObject.eResource()) {
			URI resourceURI = eObject.eResource().getURI();
			final ResourceSet resourceSet = eObject.eResource().getResourceSet();
			if (resourceSet instanceof ModelSet && null != ((ModelSet) resourceSet).getURIWithoutExtension()) {
				resourceURI = ((ModelSet) resourceSet).getURIWithoutExtension().appendFileExtension(DiModel.DI_FILE_EXTENSION);
			}
			result = getLocalePreference(resourceURI);
		}
		return result;
	}

	/**
	 * Get the locale preference created from the language preference.
	 * 
	 * @param resource
	 *            The {@link Resource) to get the papyrus project preferences.
	 * @return The locale preference.
	 */
	public static Locale getLocalePreference(final URI uri) {
		final String localePreference = getLanguagePreference(uri);

		Locale result = null;

		if (null != localePreference) {
			result = LocaleNameResolver.getLocaleFromString(localePreference);
		} else {
			result = Locale.getDefault();
		}

		return result;
	}

	/**
	 * This allows to set the language preference.
	 * 
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @param language
	 *            The language to set.
	 */
	public static void setLanguagePreference(final EObject eObject, final String language) {
		URI resourceURI = eObject.eResource().getURI();
		final ResourceSet resourceSet = eObject.eResource().getResourceSet();
		if (resourceSet instanceof ModelSet && null != ((ModelSet) resourceSet).getURIWithoutExtension()) {
			resourceURI = ((ModelSet) resourceSet).getURIWithoutExtension().appendFileExtension(DiModel.DI_FILE_EXTENSION);
		}
		setLanguagePreference(resourceURI, language);
	}

	/**
	 * This allows to set the language preference.
	 * 
	 * @param resource
	 *            The {@link Resource) to get the papyrus project preferences.
	 * @param language
	 *            The language to set.
	 */
	public static void setLanguagePreference(final Resource resource, final String language) {
		setLanguagePreference(resource.getURI(), language);
	}

	/**
	 * This allows to set the language preference.
	 * 
	 * @param resourceURI
	 *            The resource URI to get the papyrus project preferences.
	 * @param language
	 *            The language to set.
	 */
	public static void setLanguagePreference(final URI resourceURI, final String language) {
		final IPreferenceStore preferenceStore = getPreferenceStore(resourceURI);

		if (null != preferenceStore) {
			preferenceStore.setValue(LANGUAGE_PREFERENCE, language);
		}
	}

	private static boolean hasListeners() {
		return !listeners.isEmpty();
	}

	private static void fire(InternationalizationPreferenceChangeEvent event) {
		for (InternationalizationPreferenceListener next : listeners) {
			try {
				next.internationalizationChanged(event);
			} catch (Exception e) {
				Activator.log.error("Uncaught exception in listener call-back.", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Adds a {@code listener} for changes in the internationalization preferences of resources.
	 * Has no effect if the {@code listener} is already attached.
	 * 
	 * @param listener
	 *            a listener to add
	 * 
	 * @since 1.1
	 */
	public static void addPreferenceListener(InternationalizationPreferenceListener listener) {
		listeners.addIfAbsent(listener);
	}

	/**
	 * Removes a {@code listener} for changes in the internationalization preferences of resources.
	 * Has no effect if the {@code listener} is not currently attached.
	 * 
	 * @param listener
	 *            a listener to remove
	 * 
	 * @since 1.1
	 */
	public static void removePreferenceListener(InternationalizationPreferenceListener listener) {
		listeners.remove(listener);
	}

	static void preferenceStorePropertyChanged(IProject project, String papyrusProjectName, PropertyChangeEvent event) {
		if (!hasListeners()) {
			return; // Nobody to notify
		}

		// FIXME: The preferences implementation assumes unique model resource names in the project
		Collection<URI> uris = getURIs(project, papyrusProjectName);
		switch (event.getProperty()) {
		case USE_INTERNATIONALIZATION_PREFERENCE:
			boolean newUse;
			if (event.getNewValue() instanceof Boolean) {
				newUse = ((Boolean) event.getNewValue()).booleanValue();
			} else {
				newUse = Boolean.valueOf(String.valueOf(event.getNewValue()));
			}

			uris.forEach(uri -> fire(new InternationalizationPreferenceChangeEvent(event.getSource(),
					uri, newUse)));
			break;
		case LANGUAGE_PREFERENCE:
			Locale locale = (event.getNewValue() == null) ? Locale.getDefault()
					: LocaleNameResolver.getLocaleFromString(String.valueOf(event.getNewValue()));
			uris.forEach(uri -> fire(new InternationalizationPreferenceChangeEvent(event.getSource(), uri, locale)));
			break;
		default:
			// Nobody interested
			break;
		}
	}

	private static Set<URI> getURIs(IProject project, String papyrusProjectName) {
		Set<URI> result = new HashSet<>();

		try {
			project.accept(new IResourceProxyVisitor() {

				@Override
				public boolean visit(IResourceProxy proxy) throws CoreException {
					if (proxy.getType() == IResource.FILE) {
						String name = trimFileExtension(proxy.getName());
						if (papyrusProjectName.equals(name)) {
							// Normalize the result to contain only the DI resource URIs
							// if DI resources exist
							URI uri = URI.createPlatformResourceURI(proxy.requestFullPath().toString(), true);
							URI trim = uri.trimFileExtension();
							if (DiModel.DI_FILE_EXTENSION.equals(uri.fileExtension())) {
								result.removeIf(u -> u.trimFileExtension().equals(trim));
								result.add(uri);
							} else {
								URI diURI = trim.appendFileExtension(DiModel.DI_FILE_EXTENSION);
								if (!result.contains(diURI)) {
									result.add(uri);
								}
							}
						}
					}
					return true;
				}

				private String trimFileExtension(String name) {
					int dot = name.lastIndexOf('.');
					// Only accept resources that have a file extension, as all Papyrus model
					// resources must
					return (dot >= 0) ? name.substring(0, dot) : null;
				}
			}, 0);
		} catch (CoreException e) {
			Activator.log.log(e.getStatus());
		}

		return result;
	}
}
