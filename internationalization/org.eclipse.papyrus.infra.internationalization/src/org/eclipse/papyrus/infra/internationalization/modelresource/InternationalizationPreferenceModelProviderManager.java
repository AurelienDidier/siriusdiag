/*****************************************************************************
 * Copyright (c) 2016, 2019 CEA LIST and others.
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
 *   Jeremie Tatibouet (CEA LIST) jeremie.tatibouet@cea.fr - Bug 553878
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.modelresource;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.internationalization.Activator;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * This is the InternationalizationPreferenceModelProviderManager type.
 */
class InternationalizationPreferenceModelProviderManager {

	/**
	 * The provider identifier for the extension point of the internationalization preference model provider.
	 */
	private static final String E_PROVIDER = "internationalizationPreferenceModelProvider"; //$NON-NLS-1$

	/**
	 * The extension point identifier.
	 */
	private static final String EXT_PT = Activator.PLUGIN_ID + "." + E_PROVIDER; //$NON-NLS-1$

	/**
	 * The current model set.
	 */
	private final ModelSet modelSet;

	/**
	 * The default internationalization preference model provider.
	 */
	private final IInternationalizationPreferenceModelProvider defaultInternationalizationPrefModelProvider;

	/**
	 * The descriptors.
	 */
	private final List<ProviderDescriptor> descriptors;

	/**
	 * Constructor.
	 *
	 * @param modelSet
	 *            The current model set.
	 */
	public InternationalizationPreferenceModelProviderManager(final ModelSet modelSet) {
		super();

		this.modelSet = modelSet;
		this.defaultInternationalizationPrefModelProvider = createDefaultInternationalizationPrefModelProvider();
		this.descriptors = new CopyOnWriteArrayList<>(createDescriptors());
	}

	/**
	 * This allows to dispose the objects in the class.
	 */
	protected void dispose() {
		for (final ProviderDescriptor next : descriptors) {
			next.dispose();
		}
	}

	/**
	 * Obtains the most appropriate Internationalization Preference model provider for the specified URI.
	 *
	 * @param userModelURI
	 *            The initial user model URI.
	 *
	 * @return the Internationalization Preference model provider, never {@code null} (there is always a default available)
	 */
	protected IInternationalizationPreferenceModelProvider getInternationalizationPreferenceModelProvider(final URI userModelURI) {
		IInternationalizationPreferenceModelProvider result = null;

		// We know at least the back-stop descriptor will match
		ProviderDescriptor descriptor = Iterables.find(descriptors, new Predicate<Predicate<URI>>() {

			@Override
			public boolean apply(Predicate<URI> input) {
				return input.apply(userModelURI);
			}
		});

		try {
			result = descriptor.getProvider();
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			result = defaultInternationalizationPrefModelProvider;

			// Remove the offending descriptor
			descriptors.remove(descriptor);
		}

		return result;
	}

	/**
	 * This allows to create the descriptors.
	 *
	 * @return The list of created descriptors.
	 */
	private List<ProviderDescriptor> createDescriptors() {
		List<ProviderDescriptor> result = Lists.newArrayListWithExpectedSize(1);

		for (final IConfigurationElement next : Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_PT)) {
			if (E_PROVIDER.equals(next.getName())) {
				try {
					result.add(new ProviderDescriptor(next));
				} catch (final CoreException e) {
					Activator.getDefault().getLog().log(e.getStatus());
				}
			}
		}

		// The back-stop that will match any URI not previously matched
		result.add(new ProviderDescriptor(defaultInternationalizationPrefModelProvider));

		return result;
	}

	/**
	 * This allows to create the default internationalization preference model provider.
	 *
	 * @return The created {@link IInternationalizationPreferenceModelProvider}.
	 */
	private IInternationalizationPreferenceModelProvider createDefaultInternationalizationPrefModelProvider() {
		return new AbstractInternationalizationPreferenceModelProvider() {

			@Override
			public URI getInternationalizationPreferenceModelURI(final URI userModelURI) {
				Assert.isNotNull(userModelURI);
				final IPath internationalizationWorkspaceLocation = Activator.getDefault().getStateLocation();
				final URI modelURI = userModelURI.trimFileExtension();
				URI internationalizationModelURI = null;
				if (modelURI.isPlatform()) {
					internationalizationModelURI = URI.createFileURI(internationalizationWorkspaceLocation.append(modelURI.toPlatformString(true)).toString());
					internationalizationModelURI = internationalizationModelURI.appendFileExtension(InternationalizationPreferenceModel.INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION);
				} else if (modelURI.isFile()) {
					final String workspaceURI = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().toString();
					internationalizationModelURI = URI.createFileURI(internationalizationWorkspaceLocation.append(modelURI.toString().replaceFirst(workspaceURI, "")).toString()); //$NON-NLS-1$
					internationalizationModelURI = internationalizationModelURI.appendFileExtension(InternationalizationPreferenceModel.INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION);
				} else {
					internationalizationModelURI = modelURI.appendFileExtension(InternationalizationPreferenceModel.INTERNATIONALIZATION_PREFERENCE_FILE_EXTENSION);
				}

				return internationalizationModelURI;
			}
		};
	}

	//
	// Nested types
	//

	private class ProviderDescriptor implements Predicate<URI> {

		private static final String A_SCHEME = "scheme"; //$NON-NLS-1$

		private static final String A_PATTERN = "pattern"; //$NON-NLS-1$

		private static final String A_CLASS = "class"; //$NON-NLS-1$

		private final IConfigurationElement config;

		private final String scheme;

		private final Pattern pattern;

		private IInternationalizationPreferenceModelProvider provider;

		ProviderDescriptor(IInternationalizationPreferenceModelProvider provider) {
			this.provider = provider;

			this.config = null;
			this.scheme = null;
			this.pattern = null;
		}

		ProviderDescriptor(IConfigurationElement config) throws CoreException {
			this.config = config;

			this.scheme = config.getAttribute(A_SCHEME);
			String pattern = config.getAttribute(A_PATTERN);

			if (Strings.isNullOrEmpty(scheme) && Strings.isNullOrEmpty(pattern)) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Internationalization Preference model provider missing both scheme and pattern filter.")); //$NON-NLS-1$
			}

			try {
				this.pattern = (pattern == null) ? null : Pattern.compile(pattern);
			} catch (PatternSyntaxException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid Internationalization Preference model provider URI pattern filter.", e)); //$NON-NLS-1$
			}
		}

		@Override
		public boolean apply(URI input) {
			return ((scheme == null) || scheme.equals(input.scheme())) // Scheme filter
					&& ((pattern == null) || pattern.matcher(input.toString()).find()); // Pattern filter
		}

		IInternationalizationPreferenceModelProvider getProvider() throws CoreException {
			if (provider == null) {
				try {
					provider = (IInternationalizationPreferenceModelProvider) config.createExecutableExtension(A_CLASS);
					provider.initialize(modelSet);
				} catch (CoreException e) {
					throw e;
				} catch (ClassCastException e) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid Internationalization Preference model provider implementation.", e)); //$NON-NLS-1$
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to initialize Internationalization Preference model provider implementation.", e)); //$NON-NLS-1$
				}
			}

			return provider;
		}

		void dispose() {
			if (provider != null) {
				try {
					provider.dispose();
				} catch (Exception e) {
					Activator.log.error("Failed to initialize Internationalization Preference model provider implementation.", e); //$NON-NLS-1$
				}

				provider = null;
			}
		}
	}
}
