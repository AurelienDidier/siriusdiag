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

package org.eclipse.papyrus.infra.editor.welcome.internationalization.modelelements;

import static org.eclipse.core.databinding.observable.Diffs.createValueDiff;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.editor.welcome.internationalization.Activator;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForResourceSet;
import org.eclipse.papyrus.infra.internationalization.commands.InternationalizationPreferenceCommandFactory;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationPreferenceModel;
import org.eclipse.papyrus.infra.internationalization.utils.InternationalizationPreferenceModelUtils;
import org.eclipse.papyrus.infra.ui.editor.IMultiDiagramEditor;

/**
 * Encapsulation of the "shared layout" property of the Papyrus Editor.
 */
public class PrivateInternationalizationPreferenceObservableValue extends AbstractObservableValue<Boolean> implements PropertyChangeListener {

	/**
	 * The internationalization preference model.
	 */
	private InternationalizationPreferenceModel internationalizationPrefModel;

	/**
	 * The current editing domain.
	 */
	protected final EditingDomain domain;

	/**
	 * Constructor.
	 *
	 * @param domain
	 *            The current editing domain.
	 */
	public PrivateInternationalizationPreferenceObservableValue(final EditingDomain domain) {
		this(Realm.getDefault(), domain);
	}

	/**
	 * Constructor.
	 *
	 * @param realm
	 *            The {@link Realm}.
	 * @param domain
	 *            The current editing domain.
	 */
	public PrivateInternationalizationPreferenceObservableValue(final Realm realm, final EditingDomain domain) {
		super(realm);

		this.domain = domain;

		internationalizationPrefModel = InternationalizationPreferenceModelUtils.getInternationalizationPreferenceModel((ModelSet) domain.getResourceSet());
		if (null != internationalizationPrefModel) {
			internationalizationPrefModel.addPropertyChangeListener(InternationalizationPreferenceModel.PROPERTY_LEGACY_MODE, this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.AbstractObservable#dispose()
	 */
	@Override
	public synchronized void dispose() {
		if (null != internationalizationPrefModel) {
			internationalizationPrefModel.removePropertyChangeListener(InternationalizationPreferenceModel.PROPERTY_LEGACY_MODE, this);
			internationalizationPrefModel = null;
		}

		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doGetValue()
	 */
	@Override
	protected Boolean doGetValue() {
		if(!InternationalizationPreferencesUtils.isInternationalizationNeedToBeLoaded()) {
			return false;
		}
		
		return internationalizationPrefModel.isLegacyMode();
	}

	/**
	 * Get the command for the private storage.
	 * 
	 * @param factory
	 *            The factory to create the command.
	 * @return The command to manage the private storage.
	 */
	protected Command getToggleCommand(final InternationalizationPreferenceCommandFactory factory) {
		return factory.createTogglePrivateStorageCommand();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
	 */
	@Override
	public Object getValueType() {
		return Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(final Boolean value) {
		Boolean oldValue = getValue();
		if (!Objects.equals(oldValue, value)) {
			IMultiDiagramEditor editor;

			try {
				editor = ServiceUtilsForResourceSet.getInstance().getService(IMultiDiagramEditor.class, domain.getResourceSet());
				final ModelSet modelSet = (ModelSet) editor.getAdapter(EditingDomain.class).getResourceSet();
				final Command command = getToggleCommand(new InternationalizationPreferenceCommandFactory(modelSet));
				if (command.canExecute()) {
					// Don't do this in an undoable command on the history
					try {
						domain.getCommandStack().execute(command);
						fireValueChange(Diffs.createValueDiff(oldValue, value));
					} catch (Exception e) {
						Activator.log.error("Failed to toggle editor page layout option", e); //$NON-NLS-1$
					}
				}
			} catch (final ServiceException e) {
				throw new IllegalStateException("No editor available in the service registry", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == internationalizationPrefModel) {
			switch (evt.getPropertyName()) {
			case InternationalizationPreferenceModel.PROPERTY_LEGACY_MODE:
				fireValueChange(createValueDiff(!(Boolean) evt.getOldValue(), !(Boolean) evt.getNewValue()));
				break;
			}
		}
	}
}
