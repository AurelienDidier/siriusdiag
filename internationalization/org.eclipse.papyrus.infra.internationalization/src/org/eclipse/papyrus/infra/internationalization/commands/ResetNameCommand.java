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

package org.eclipse.papyrus.infra.internationalization.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.command.AbstractOverrideableCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * This allows to define a command to reset the feature value needed.
 */
public class ResetNameCommand extends AbstractOverrideableCommand {

	/**
	 * The owner to modify.
	 */
	private EObject owner;

	/**
	 * The feature tu reset.
	 */
	private EStructuralFeature feature;

	/**
	 * This caches the label.
	 */
	protected static final String LABEL = EMFEditPlugin.INSTANCE.getString("_UI_SetCommand_label");

	/**
	 * This caches the description.
	 */
	protected static final String DESCRIPTION = EMFEditPlugin.INSTANCE.getString("_UI_SetCommand_description");

	/**
	 * Constructor.
	 *
	 * @param domain
	 *            The current editing domain.
	 * @param owner
	 *            The owner to modify.
	 * @param feature
	 *            The feature to reset.
	 */
	public ResetNameCommand(final EditingDomain domain, final EObject owner, final EStructuralFeature feature) {
		super(domain, LABEL, DESCRIPTION);

		this.owner = owner;
		this.feature = feature;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
	 */
	@Override
	protected boolean prepare() {
		return null != this.owner.eClass().getEStructuralFeature(feature.getFeatureID())
				&& null != this.owner.eGet(this.feature);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.edit.command.AbstractOverrideableCommand#doExecute()
	 */
	@Override
	public void doExecute() {
		final Object oldValue = this.owner.eGet(this.feature);
		this.owner.eSet(feature, null);
		this.owner.eSet(feature, oldValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.edit.command.AbstractOverrideableCommand#doUndo()
	 */
	@Override
	public void doUndo() {
		final Object oldValue = this.owner.eGet(this.feature);
		this.owner.eSet(feature, null);
		this.owner.eSet(feature, oldValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.edit.command.AbstractOverrideableCommand#doRedo()
	 */
	@Override
	public void doRedo() {
		doExecute();
	}
}
