/**
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
 */
package org.eclipse.papyrus.infra.internationalization;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Library</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationPackage#getInternationalizationLibrary()
 * @model
 * @generated
 */
public interface InternationalizationLibrary extends EObject {
	/**
	 * Returns the value of the '<em><b>Entries</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.papyrus.infra.internationalization.InternationalizationEntry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entries</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entries</em>' containment reference list.
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationPackage#getInternationalizationLibrary_Entries()
	 * @model containment="true"
	 * @generated
	 */
	EList<InternationalizationEntry> getEntries();

} // InternationalizationLibrary
