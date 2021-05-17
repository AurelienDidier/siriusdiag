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
package org.eclipse.papyrus.infra.internationalization.common.utils;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Protocol for a listener to be notified of changes in the internationalization
 * preferences for resources.
 *
 * @author Christian W. Damus
 * 
 * @since 1.1
 */
public interface InternationalizationPreferenceListener extends EventListener {

	/**
	 * Handles the change of internationalization for some resource.
	 * The {@code event} object's {@link EventObject#getSource() source} is
	 * to be treated as opaque, not interpreted by the receiver.
	 * 
	 * @param event the internationalization change event
	 */
	void internationalizationChanged(InternationalizationPreferenceChangeEvent event);
}
