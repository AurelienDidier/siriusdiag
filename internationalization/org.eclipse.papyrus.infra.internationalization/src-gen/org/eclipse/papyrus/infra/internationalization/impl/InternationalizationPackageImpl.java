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
package org.eclipse.papyrus.infra.internationalization.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.papyrus.infra.internationalization.InternationalizationEntry;
import org.eclipse.papyrus.infra.internationalization.InternationalizationFactory;
import org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary;
import org.eclipse.papyrus.infra.internationalization.InternationalizationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class InternationalizationPackageImpl extends EPackageImpl implements InternationalizationPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass internationalizationLibraryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass internationalizationEntryEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.papyrus.infra.internationalization.InternationalizationPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private InternationalizationPackageImpl() {
		super(eNS_URI, InternationalizationFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link InternationalizationPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static InternationalizationPackage init() {
		if (isInited) {
			return (InternationalizationPackage) EPackage.Registry.INSTANCE
					.getEPackage(InternationalizationPackage.eNS_URI);
		}

		// Obtain or create and register package
		InternationalizationPackageImpl theInternationalizationPackage = (InternationalizationPackageImpl) (EPackage.Registry.INSTANCE
				.get(eNS_URI) instanceof InternationalizationPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
						: new InternationalizationPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theInternationalizationPackage.createPackageContents();

		// Initialize created meta-data
		theInternationalizationPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theInternationalizationPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(InternationalizationPackage.eNS_URI, theInternationalizationPackage);
		return theInternationalizationPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInternationalizationLibrary() {
		return internationalizationLibraryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInternationalizationLibrary_Entries() {
		return (EReference) internationalizationLibraryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInternationalizationEntry() {
		return internationalizationEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInternationalizationEntry_Key() {
		return (EAttribute) internationalizationEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInternationalizationEntry_Value() {
		return (EAttribute) internationalizationEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InternationalizationFactory getInternationalizationFactory() {
		return (InternationalizationFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) {
			return;
		}
		isCreated = true;

		// Create classes and their features
		internationalizationLibraryEClass = createEClass(INTERNATIONALIZATION_LIBRARY);
		createEReference(internationalizationLibraryEClass, INTERNATIONALIZATION_LIBRARY__ENTRIES);

		internationalizationEntryEClass = createEClass(INTERNATIONALIZATION_ENTRY);
		createEAttribute(internationalizationEntryEClass, INTERNATIONALIZATION_ENTRY__KEY);
		createEAttribute(internationalizationEntryEClass, INTERNATIONALIZATION_ENTRY__VALUE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) {
			return;
		}
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(internationalizationLibraryEClass, InternationalizationLibrary.class, "InternationalizationLibrary", //$NON-NLS-1$
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInternationalizationLibrary_Entries(), this.getInternationalizationEntry(), null, "entries", //$NON-NLS-1$
				null, 0, -1, InternationalizationLibrary.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(internationalizationEntryEClass, InternationalizationEntry.class, "InternationalizationEntry", //$NON-NLS-1$
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInternationalizationEntry_Key(), ecorePackage.getEJavaObject(), "key", null, 1, 1, //$NON-NLS-1$
				InternationalizationEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getInternationalizationEntry_Value(), ecorePackage.getEString(), "value", null, 1, 1, //$NON-NLS-1$
				InternationalizationEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //InternationalizationPackageImpl
