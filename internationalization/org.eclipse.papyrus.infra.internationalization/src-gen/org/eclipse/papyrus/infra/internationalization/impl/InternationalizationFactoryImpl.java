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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.papyrus.infra.internationalization.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class InternationalizationFactoryImpl extends EFactoryImpl implements InternationalizationFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static InternationalizationFactory init() {
		try {
			InternationalizationFactory theInternationalizationFactory = (InternationalizationFactory) EPackage.Registry.INSTANCE
					.getEFactory(InternationalizationPackage.eNS_URI);
			if (theInternationalizationFactory != null) {
				return theInternationalizationFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new InternationalizationFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InternationalizationFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case InternationalizationPackage.INTERNATIONALIZATION_LIBRARY:
			return createInternationalizationLibrary();
		case InternationalizationPackage.INTERNATIONALIZATION_ENTRY:
			return createInternationalizationEntry();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InternationalizationLibrary createInternationalizationLibrary() {
		InternationalizationLibraryImpl internationalizationLibrary = new InternationalizationLibraryImpl();
		return internationalizationLibrary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InternationalizationEntry createInternationalizationEntry() {
		InternationalizationEntryImpl internationalizationEntry = new InternationalizationEntryImpl();
		return internationalizationEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InternationalizationPackage getInternationalizationPackage() {
		return (InternationalizationPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static InternationalizationPackage getPackage() {
		return InternationalizationPackage.eINSTANCE;
	}

} //InternationalizationFactoryImpl
