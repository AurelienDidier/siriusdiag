/*******************************************************************************
 * Copyright (c) 2017 Christian W. Damus and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Christian W. Damus - initial API and implementation
 *******************************************************************************/
package org.eclipse.papyrus.infra.internationalization.common;

import java.util.EventListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A listener to which the {@link Activator} can call back on creation
 * and disposal of project-scoped {@link IPreferenceStore}s for internationalization.
 *
 * @author Christian W. Damus
 * 
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPreferenceStoreListener extends EventListener {

	/**
	 * Notifies me that a preference {@code store} was created.
	 * 
	 * @param project
	 *            the project for which a preference store was created
	 * @param papyrusProject
	 *            the Papyrus project (name of the model within the {@code project})
	 *            for which it was created
	 * @param store
	 *            the store that was created
	 */
	void preferenceStoreCreated(IProject project, String papyrusProject, IPreferenceStore store);

	/**
	 * Notifies me that a preference {@code store} was disposed.
	 * 
	 * @param project
	 *            the project for which a preference store was disposed
	 * @param papyrusProject
	 *            the Papyrus project (name of the model within the {@code project})
	 * @param store
	 *            the store that was disposed
	 */
	void preferenceStoreDisposed(IProject project, String papyrusProject, IPreferenceStore store);
}
