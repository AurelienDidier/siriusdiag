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

package org.eclipse.papyrus.infra.internationalization.common.command;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;

/**
 * The command to modify the internationalization preference. We can't use the
 * RecordingCommand because the modification are not applied to a resource
 * directly.
 */
public class UseInternationalizationPreferenceCommand extends AbstractCommand {

	/**
	 * The current resource URI corresponding to the internationalization
	 * preference.
	 */
	protected URI resourceURI;

	/**
	 * The new boolean value.
	 */
	protected boolean newBooleanValue;

	/**
	 * The old boolean value.
	 */
	protected boolean oldBooleanValue;

	/**
	 * Constructor.
	 *
	 * @param resourceURI
	 *            The current resource URI.
	 * @param newBooleanValue
	 *            The new boolean value.
	 */
	public UseInternationalizationPreferenceCommand(final URI resourceURI, final boolean newBooleanValue) {
		super("Modify use internationalization preference"); //$NON-NLS-1$
		this.resourceURI = resourceURI;
		this.newBooleanValue = newBooleanValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.Command#execute()
	 */
	@Override
	public void execute() {
		oldBooleanValue = InternationalizationPreferencesUtils.getInternationalizationPreference(resourceURI);
		InternationalizationPreferencesUtils.setInternationalizationPreference(resourceURI, newBooleanValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.AbstractCommand#undo()
	 */
	@Override
	public void undo() {
		InternationalizationPreferencesUtils.setInternationalizationPreference(resourceURI, oldBooleanValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.Command#redo()
	 */
	@Override
	public void redo() {
		InternationalizationPreferencesUtils.setInternationalizationPreference(resourceURI, newBooleanValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
	 */
	@Override
	protected boolean prepare() {
		return null != resourceURI;
	}

}
