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

package org.eclipse.papyrus.infra.internationalization.common;

import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.papyrus.infra.core.log.LogHelper;
import org.eclipse.papyrus.infra.core.resource.PapyrusProjectScope;
import org.eclipse.papyrus.infra.ui.preferences.PapyrusScopedPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference store manager with support for lifecycle listeners.
 */
class PreferenceStoreManager extends AbstractUIPlugin {
	/**
	 * The internationalization preference node label.
	 */
	static final String INTERNATIONALIZATION_NODE_LABEL = "internationalization"; //$NON-NLS-1$


	/**
	 * Storage for preferences, per scope.
	 */
	private final Map<ScopeKey, IPreferenceStore> preferenceStores = new HashMap<>();

	private final CopyOnWriteArrayList<IPreferenceStoreListener> preferenceStoreListeners = new CopyOnWriteArrayList<>();

	private final LogHelper log;

	/**
	 * Initializes me with my log helper.
	 * 
	 * @param log
	 *            my log helper
	 */
	PreferenceStoreManager(LogHelper log) {
		super();

		this.log = log;
	}

	public void dispose() {
		disposePreferenceStores();
		preferenceStoreListeners.clear();
	}

	/**
	 * Get the preference store and create it if necessary.
	 * 
	 * @param project
	 *            The current project
	 * @param papyrusProjectName
	 *            The current papyrus project name.
	 * @return The preference store.
	 */
	public IPreferenceStore getInternationalizationPreferenceStore(final IProject project,
			final String papyrusProjectName) {

		ScopeKey key = new ScopeKey(project, papyrusProjectName);
		IPreferenceStore result = preferenceStores.get(key);
		if (null == result) {
			final IScopeContext scope = new PapyrusProjectScope(project, papyrusProjectName);
			result = new PapyrusScopedPreferenceStore(scope, INTERNATIONALIZATION_NODE_LABEL);
			preferenceStores.put(new ScopeKey(project, papyrusProjectName), result);
			created(project, papyrusProjectName, result);
		}

		return result;
	}

	/**
	 * Get the first preference store if existing, else a scoped preference
	 * store must be created and added to the list of preferences store.
	 * 
	 * @return The preference store.
	 */
	public IPreferenceStore getInternationalizationPreferenceStore() {
		IPreferenceStore result = preferenceStores.get(ScopeKey.INSTANCE_SCOPE);

		if (result == null) {
			// Create the preference store lazily.
			IScopeContext scope = InstanceScope.INSTANCE;
			result = new ScopedPreferenceStore(scope, getBundle().getSymbolicName());
			preferenceStores.put(ScopeKey.INSTANCE_SCOPE, result);
		}

		return result;
	}

	/**
	 * Add a listener for events in the preference store management cycle.
	 * Has no effect if the listener is already added. If, at the
	 * time of this call, there are already preference stores in existence,
	 * the {@code listener} will be
	 * {@link IPreferenceStoreListener#preferenceStoreCreated(IProject, String, IPreferenceStore) informed}
	 * belatedly of their creation.
	 * 
	 * @param listener
	 *            a listener to add
	 */
	void addPreferenceStoreListener(IPreferenceStoreListener listener) {
		if (preferenceStoreListeners.addIfAbsent(listener)) {
			added(listener);
		}
	}

	/**
	 * Remove a listener for events in the preference store management cycle.
	 * Has no effect if the listener is not (still) registered.
	 * 
	 * @param listener
	 *            a listener to remove
	 */
	void removePreferenceStoreListener(IPreferenceStoreListener listener) {
		preferenceStoreListeners.remove(listener);
	}

	private void added(IPreferenceStoreListener listener) {
		if (hasProjectPreferences()) {
			for (Map.Entry<ScopeKey, IPreferenceStore> next : preferenceStores.entrySet()) {
				ScopeKey key = next.getKey();
				if (key.isProjectScope()) {
					try {
						listener.preferenceStoreCreated(key.getProject(), key.getPapyrusProject(), next.getValue());
					} catch (Exception e) {
						log.error("Uncaught exception in preference store listener.", e);
					}
				}
			}
		}
	}

	private void created(IProject project, String papyrusProject, IPreferenceStore store) {
		// Notify listeners
		if (!preferenceStoreListeners.isEmpty()) {
			for (IPreferenceStoreListener next : preferenceStoreListeners) {
				try {
					next.preferenceStoreCreated(project, papyrusProject, store);
				} catch (Exception e) {
					log.error("Uncaught exception in preference store listener.", e);
				}
			}
		}
	}

	private boolean hasProjectPreferences() {
		return !preferenceStores.isEmpty()
				&& !preferenceStores.keySet().equals(singleton(ScopeKey.INSTANCE_SCOPE));
	}

	private void disposePreferenceStores() {
		for (Map.Entry<ScopeKey, IPreferenceStore> next : preferenceStores.entrySet()) {
			ScopeKey key = next.getKey();
			if (key.isProjectScope()) {
				disposed(key.getProject(), key.getPapyrusProject(), next.getValue());
			}
		}
		preferenceStores.clear();
	}

	private void disposed(IProject project, String papyrusProject, IPreferenceStore store) {
		// Notify listeners
		if (!preferenceStoreListeners.isEmpty()) {
			for (IPreferenceStoreListener next : preferenceStoreListeners) {
				try {
					next.preferenceStoreDisposed(project, papyrusProject, store);
				} catch (Exception e) {
					log.error("Uncaught exception in preference store listener.", e);
				}
			}
		}
	}

	//
	// Nested types
	//

	private static final class ScopeKey {
		static final ScopeKey INSTANCE_SCOPE = new ScopeKey(null, null);

		private final IProject project;
		private final String papyrusProject;

		ScopeKey(IProject project, String papyrusProject) {
			if (project == null && INSTANCE_SCOPE != null) {
				throw new NullPointerException("project"); //$NON-NLS-1$
			}
			if (papyrusProject == null && project != null) {
				throw new NullPointerException("papyrusProject"); //$NON-NLS-1$
			}

			this.project = project;
			this.papyrusProject = papyrusProject;
		}

		IProject getProject() {
			return project;
		}

		String getPapyrusProject() {
			return papyrusProject;
		}

		boolean isProjectScope() {
			return project != null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((papyrusProject == null) ? 0 : papyrusProject.hashCode());
			result = prime * result + ((project == null) ? 0 : project.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ScopeKey other = (ScopeKey) obj;
			if (papyrusProject == null) {
				if (other.papyrusProject != null) {
					return false;
				}
			} else if (!papyrusProject.equals(other.papyrusProject)) {
				return false;
			}
			if (project == null) {
				if (other.project != null) {
					return false;
				}
			} else if (!project.equals(other.project)) {
				return false;
			}
			return true;
		}

	}
}
