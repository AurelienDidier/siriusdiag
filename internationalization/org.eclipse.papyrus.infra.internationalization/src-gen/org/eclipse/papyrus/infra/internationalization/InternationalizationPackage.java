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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationFactory
 * @model kind="package"
 * @generated
 */
public interface InternationalizationPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "internationalization"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/papyrus/internationalization/model"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "internationalization"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	InternationalizationPackage eINSTANCE = org.eclipse.papyrus.infra.internationalization.impl.InternationalizationPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link org.eclipse.papyrus.infra.internationalization.impl.InternationalizationLibraryImpl <em>Library</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationLibraryImpl
	 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationPackageImpl#getInternationalizationLibrary()
	 * @generated
	 */
	int INTERNATIONALIZATION_LIBRARY = 0;

	/**
	 * The feature id for the '<em><b>Entries</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_LIBRARY__ENTRIES = 0;

	/**
	 * The number of structural features of the '<em>Library</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_LIBRARY_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Library</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_LIBRARY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.papyrus.infra.internationalization.impl.InternationalizationEntryImpl <em>Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationEntryImpl
	 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationPackageImpl#getInternationalizationEntry()
	 * @generated
	 */
	int INTERNATIONALIZATION_ENTRY = 1;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNATIONALIZATION_ENTRY_OPERATION_COUNT = 0;

	/**
	 * Returns the meta object for class '{@link org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary <em>Library</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Library</em>'.
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary
	 * @generated
	 */
	EClass getInternationalizationLibrary();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary#getEntries <em>Entries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entries</em>'.
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary#getEntries()
	 * @see #getInternationalizationLibrary()
	 * @generated
	 */
	EReference getInternationalizationLibrary_Entries();

	/**
	 * Returns the meta object for class '{@link org.eclipse.papyrus.infra.internationalization.InternationalizationEntry <em>Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entry</em>'.
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationEntry
	 * @generated
	 */
	EClass getInternationalizationEntry();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.papyrus.infra.internationalization.InternationalizationEntry#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationEntry#getKey()
	 * @see #getInternationalizationEntry()
	 * @generated
	 */
	EAttribute getInternationalizationEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.papyrus.infra.internationalization.InternationalizationEntry#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationEntry#getValue()
	 * @see #getInternationalizationEntry()
	 * @generated
	 */
	EAttribute getInternationalizationEntry_Value();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	InternationalizationFactory getInternationalizationFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.papyrus.infra.internationalization.impl.InternationalizationLibraryImpl <em>Library</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationLibraryImpl
		 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationPackageImpl#getInternationalizationLibrary()
		 * @generated
		 */
		EClass INTERNATIONALIZATION_LIBRARY = eINSTANCE.getInternationalizationLibrary();

		/**
		 * The meta object literal for the '<em><b>Entries</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERNATIONALIZATION_LIBRARY__ENTRIES = eINSTANCE.getInternationalizationLibrary_Entries();

		/**
		 * The meta object literal for the '{@link org.eclipse.papyrus.infra.internationalization.impl.InternationalizationEntryImpl <em>Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationEntryImpl
		 * @see org.eclipse.papyrus.infra.internationalization.impl.InternationalizationPackageImpl#getInternationalizationEntry()
		 * @generated
		 */
		EClass INTERNATIONALIZATION_ENTRY = eINSTANCE.getInternationalizationEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERNATIONALIZATION_ENTRY__KEY = eINSTANCE.getInternationalizationEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERNATIONALIZATION_ENTRY__VALUE = eINSTANCE.getInternationalizationEntry_Value();

	}

} //InternationalizationPackage
