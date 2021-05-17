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

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.papyrus.infra.properties.ui.modelelement.EMFModelElement;

/**
 * The internationalization welcome model element.
 */
public class InternationalizationWelcomeModelElement extends EMFModelElement {

	/**
	 * The private storage identifier.
	 */
	private final String PRIVATE_STORAGE = "privateStorage"; //$NON-NLS-1$

	/**
	 * The useInternationalization identifier.
	 */
	private final String USE_INTERNATIONALIZATION = "useInternationalization"; //$NON-NLS-1$

	/**
	 * The language identifier.
	 */
	private final String LANGUAGE = "language"; //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param source
	 *            The source eObject.
	 * @param domain
	 *            The current editing domain.
	 */
	public InternationalizationWelcomeModelElement(final EObject source, final EditingDomain domain) {
		super(source, domain);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.properties.ui.modelelement.EMFModelElement#doGetObservable(java.lang.String)
	 */
	@Override
	protected IObservable doGetObservable(final String propertyPath) {
		IObservable result = null;

		switch (propertyPath) {
		case PRIVATE_STORAGE:
			result = new PrivateInternationalizationPreferenceObservableValue(domain);
			break;
		case USE_INTERNATIONALIZATION:
			result = new UseInternationalizationObservableValue(domain);
			break;
		case LANGUAGE:
			result = new LanguageObservableValue(domain);
			break;
		default:
			break;
		}

		return null != result ? result : super.doGetObservable(propertyPath);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.properties.ui.modelelement.EMFModelElement#isFeatureEditable(java.lang.String)
	 */
	@Override
	protected boolean isFeatureEditable(final String propertyPath) {
		boolean result;

		switch (propertyPath) {
		case PRIVATE_STORAGE:
			result = true;
			break;
		case USE_INTERNATIONALIZATION:
			result = true;
			break;
		case LANGUAGE:
			result = true;
			break;
		default:
			result = super.isFeatureEditable(propertyPath);
			break;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.properties.ui.modelelement.EMFModelElement#isElementEditable()
	 */
	@Override
	protected boolean isElementEditable() {
		return true;
	}
}
