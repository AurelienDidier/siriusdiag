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
public class LocaleInternationalizationPreferenceCommand extends AbstractCommand {

	/**
	 * The current resource URI corresponding to the internationalization
	 * preference.
	 */
	protected URI resourceURI;

	/**
	 * The new language value.
	 */
	protected String newLanguageValue;

	/**
	 * The old language value.
	 */
	protected String oldLanguageValue;

	/**
	 * Constructor.
	 *
	 * @param resourceURI
	 *            The current resource URI.
	 * @param newLanguageValue
	 *            The new language value.
	 */
	public LocaleInternationalizationPreferenceCommand(final URI resourceURI, final String newLanguageValue) {
		super("Modify locale internationalization preference"); //$NON-NLS-1$
		this.resourceURI = resourceURI;
		this.newLanguageValue = newLanguageValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.Command#execute()
	 */
	@Override
	public void execute() {
		oldLanguageValue = InternationalizationPreferencesUtils.getLocalePreference(resourceURI).toString();
		InternationalizationPreferencesUtils.setLanguagePreference(resourceURI, newLanguageValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.AbstractCommand#undo()
	 */
	@Override
	public void undo() {
		InternationalizationPreferencesUtils.setLanguagePreference(resourceURI, oldLanguageValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.Command#redo()
	 */
	@Override
	public void redo() {
		InternationalizationPreferencesUtils.setLanguagePreference(resourceURI, newLanguageValue);
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
