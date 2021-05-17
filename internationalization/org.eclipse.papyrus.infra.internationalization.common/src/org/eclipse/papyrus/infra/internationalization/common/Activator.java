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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.papyrus.infra.core.log.LogHelper;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.eclipse.papyrus.infra.internationalization.common"; //$NON-NLS-1$

	/**
	 * The internationalization preference node label.
	 */
	public static final String INTERNATIONALIZATION_NODE_LABEL = PreferenceStoreManager.INTERNATIONALIZATION_NODE_LABEL;

	/**
	 * The shared instance.
	 */
	private static Activator plugin;

	/**
	 * The log helper.
	 */
	public static LogHelper log;

	private PreferenceStoreManager preferenceStores;

	/**
	 * The constructor.
	 */
	public Activator() {
		super();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		log = new LogHelper(this);
		preferenceStores = new PreferenceStoreManager(log);
		plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		preferenceStores.dispose();
		preferenceStores = null;

		plugin = null;
		super.stop(context);
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

		return preferenceStores.getInternationalizationPreferenceStore(project, papyrusProjectName);
	}

	/**
	 * Get the first preference store if existing, else a scoped preference
	 * store must be created and added to the list of preferences store.
	 * 
	 * @return The preference store.
	 */
	public IPreferenceStore getInternationalizationPreferenceStore() {
		return preferenceStores.getInternationalizationPreferenceStore();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
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
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void addPreferenceStoreListener(IPreferenceStoreListener listener) {
		preferenceStores.addPreferenceStoreListener(listener);
	}

	/**
	 * Remove a listener for events in the preference store management cycle.
	 * Has no effect if the listener is not (still) registered.
	 * 
	 * @param listener
	 *            a listener to remove
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void removePreferenceStoreListener(IPreferenceStoreListener listener) {
		preferenceStores.removePreferenceStoreListener(listener);
	}

}
