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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;

/**
 * This allows to define a command to reset the feature value needed.
 */
public class ResetNameTransactionalCommand extends AbstractTransactionalCommand {

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
	 * Constructor.
	 *
	 * @param domain
	 *            The current editing domain.
	 * @param owner
	 *            The owner to modify.
	 * @param feature
	 *            The feature to reset.
	 */
	public ResetNameTransactionalCommand(final TransactionalEditingDomain domain, final EObject owner,
			final EStructuralFeature feature) {
		super(domain, LABEL, null);

		this.owner = owner;
		this.feature = feature;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.commands.operations.AbstractOperation#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return null != this.owner.eClass().getEStructuralFeature(feature.getFeatureID())
				&& null != this.owner.eGet(this.feature);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		final Object oldValue = this.owner.eGet(this.feature);
		this.owner.eSet(feature, null);
		this.owner.eSet(feature, oldValue);
		return CommandResult.newOKCommandResult(this.owner);
	}
}
