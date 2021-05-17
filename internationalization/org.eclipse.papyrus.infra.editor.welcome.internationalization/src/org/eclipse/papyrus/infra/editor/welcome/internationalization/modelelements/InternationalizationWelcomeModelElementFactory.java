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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.papyrus.infra.editor.welcome.internationalization.Activator;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;
import org.eclipse.papyrus.infra.properties.contexts.DataContextElement;
import org.eclipse.papyrus.infra.properties.ui.modelelement.AbstractEMFModelElementFactory;

/**
 * The internationalization welcome model element factory for the welcome properties of the internationalization.
 */
public class InternationalizationWelcomeModelElementFactory extends AbstractEMFModelElementFactory<InternationalizationWelcomeModelElement> {

	/**
	 * Constructor.
	 */
	public InternationalizationWelcomeModelElementFactory() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.properties.ui.modelelement.AbstractModelElementFactory#doCreateFromSource(java.lang.Object, org.eclipse.papyrus.infra.properties.contexts.DataContextElement)
	 */
	@Override
	protected InternationalizationWelcomeModelElement doCreateFromSource(final Object sourceElement, final DataContextElement context) {
		final EObject source = EMFHelper.getEObject(sourceElement);
		if (null == source) {
			Activator.log.warn("Unable to resolve the selected element to an EObject"); //$NON-NLS-1$
			return null;
		}

		final EditingDomain domain = EMFHelper.resolveEditingDomain(source);
		return new InternationalizationWelcomeModelElement(source, domain);
	}
}
