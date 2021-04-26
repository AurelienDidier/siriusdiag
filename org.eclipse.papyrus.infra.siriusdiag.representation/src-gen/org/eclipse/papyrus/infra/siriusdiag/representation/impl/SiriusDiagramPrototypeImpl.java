/******************************************************************************
 * Copyright (c) 2021 CEA LIST, Artal Technologies
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Aurelien Didier (ARTAL) - aurelien.didier51@gmail.com - Bug 569174
 *****************************************************************************/
package org.eclipse.papyrus.infra.siriusdiag.representation.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.papyrus.infra.architecture.representation.impl.PapyrusRepresentationKindImpl;
import org.eclipse.papyrus.infra.siriusdiag.representation.RepresentationPackage;
import org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype;
import org.eclipse.papyrus.infra.siriusdiag.representation.util.RepresentationValidator;
import org.eclipse.sirius.diagram.DSemanticDiagram;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sirius Diagram Prototype</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.papyrus.infra.siriusdiag.representation.impl.SiriusDiagramPrototypeImpl#getSiriusDiagramPrototype <em>Sirius Diagram Prototype</em>}</li>
 * <li>{@link org.eclipse.papyrus.infra.siriusdiag.representation.impl.SiriusDiagramPrototypeImpl#getCreationCommandClass <em>Creation Command Class</em>}</li>
 * <li>{@link org.eclipse.papyrus.infra.siriusdiag.representation.impl.SiriusDiagramPrototypeImpl#getViewpointName <em>Viewpoint Name</em>}</li>
 * <li>{@link org.eclipse.papyrus.infra.siriusdiag.representation.impl.SiriusDiagramPrototypeImpl#getDiagramName <em>Diagram Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SiriusDiagramPrototypeImpl extends PapyrusRepresentationKindImpl implements SiriusDiagramPrototype {
	/**
	 * The cached value of the '{@link #getSiriusDiagramPrototype() <em>Sirius Diagram Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getSiriusDiagramPrototype()
	 * @generated
	 * @ordered
	 */
	protected DSemanticDiagram siriusDiagramPrototype;

	/**
	 * The default value of the '{@link #getCreationCommandClass() <em>Creation Command Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getCreationCommandClass()
	 * @generated
	 * @ordered
	 */
	protected static final String CREATION_COMMAND_CLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreationCommandClass() <em>Creation Command Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getCreationCommandClass()
	 * @generated
	 * @ordered
	 */
	protected String creationCommandClass = CREATION_COMMAND_CLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getViewpointName() <em>Viewpoint Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getViewpointName()
	 * @generated
	 * @ordered
	 */
	protected static final String VIEWPOINT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getViewpointName() <em>Viewpoint Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getViewpointName()
	 * @generated
	 * @ordered
	 */
	protected String viewpointName = VIEWPOINT_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDiagramName() <em>Diagram Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getDiagramName()
	 * @generated
	 * @ordered
	 */
	protected static final String DIAGRAM_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDiagramName() <em>Diagram Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getDiagramName()
	 * @generated
	 * @ordered
	 */
	protected String diagramName = DIAGRAM_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected SiriusDiagramPrototypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RepresentationPackage.Literals.SIRIUS_DIAGRAM_PROTOTYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public DSemanticDiagram getSiriusDiagramPrototype() {
		if (siriusDiagramPrototype != null && siriusDiagramPrototype.eIsProxy()) {
			InternalEObject oldSiriusDiagramPrototype = (InternalEObject) siriusDiagramPrototype;
			siriusDiagramPrototype = (DSemanticDiagram) eResolveProxy(oldSiriusDiagramPrototype);
			if (siriusDiagramPrototype != oldSiriusDiagramPrototype) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__SIRIUS_DIAGRAM_PROTOTYPE, oldSiriusDiagramPrototype, siriusDiagramPrototype));
				}
			}
		}
		return siriusDiagramPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public DSemanticDiagram basicGetSiriusDiagramPrototype() {
		return siriusDiagramPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setSiriusDiagramPrototype(DSemanticDiagram newSiriusDiagramPrototype) {
		DSemanticDiagram oldSiriusDiagramPrototype = siriusDiagramPrototype;
		siriusDiagramPrototype = newSiriusDiagramPrototype;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__SIRIUS_DIAGRAM_PROTOTYPE, oldSiriusDiagramPrototype, siriusDiagramPrototype));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getCreationCommandClass() {
		return creationCommandClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setCreationCommandClass(String newCreationCommandClass) {
		String oldCreationCommandClass = creationCommandClass;
		creationCommandClass = newCreationCommandClass;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__CREATION_COMMAND_CLASS, oldCreationCommandClass, creationCommandClass));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getViewpointName() {
		return viewpointName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setViewpointName(String newViewpointName) {
		String oldViewpointName = viewpointName;
		viewpointName = newViewpointName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__VIEWPOINT_NAME, oldViewpointName, viewpointName));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getDiagramName() {
		return diagramName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDiagramName(String newDiagramName) {
		String oldDiagramName = diagramName;
		diagramName = newDiagramName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__DIAGRAM_NAME, oldDiagramName, diagramName));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isValidClass(DiagnosticChain chain, Map<Object, Object> context) {
		// TODO: implement this method
		// -> specify the condition that violates the invariant
		// -> verify the details of the diagnostic, including severity and message
		// Ensure that you remove @generated or mark it @generated NOT
		if (false) {
			if (chain != null) {
				chain.add(new BasicDiagnostic(Diagnostic.ERROR,
						RepresentationValidator.DIAGNOSTIC_SOURCE,
						RepresentationValidator.SIRIUS_DIAGRAM_PROTOTYPE__IS_VALID_CLASS,
						EcorePlugin.INSTANCE.getString("_UI_GenericInvariant_diagnostic", new Object[] { "isValidClass", EObjectValidator.getObjectLabel(this, context) }), //$NON-NLS-1$ //$NON-NLS-2$
						new Object[] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__SIRIUS_DIAGRAM_PROTOTYPE:
			if (resolve) {
				return getSiriusDiagramPrototype();
			}
			return basicGetSiriusDiagramPrototype();
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__CREATION_COMMAND_CLASS:
			return getCreationCommandClass();
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__VIEWPOINT_NAME:
			return getViewpointName();
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__DIAGRAM_NAME:
			return getDiagramName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__SIRIUS_DIAGRAM_PROTOTYPE:
			setSiriusDiagramPrototype((DSemanticDiagram) newValue);
			return;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__CREATION_COMMAND_CLASS:
			setCreationCommandClass((String) newValue);
			return;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__VIEWPOINT_NAME:
			setViewpointName((String) newValue);
			return;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__DIAGRAM_NAME:
			setDiagramName((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__SIRIUS_DIAGRAM_PROTOTYPE:
			setSiriusDiagramPrototype((DSemanticDiagram) null);
			return;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__CREATION_COMMAND_CLASS:
			setCreationCommandClass(CREATION_COMMAND_CLASS_EDEFAULT);
			return;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__VIEWPOINT_NAME:
			setViewpointName(VIEWPOINT_NAME_EDEFAULT);
			return;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__DIAGRAM_NAME:
			setDiagramName(DIAGRAM_NAME_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__SIRIUS_DIAGRAM_PROTOTYPE:
			return siriusDiagramPrototype != null;
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__CREATION_COMMAND_CLASS:
			return CREATION_COMMAND_CLASS_EDEFAULT == null ? creationCommandClass != null : !CREATION_COMMAND_CLASS_EDEFAULT.equals(creationCommandClass);
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__VIEWPOINT_NAME:
			return VIEWPOINT_NAME_EDEFAULT == null ? viewpointName != null : !VIEWPOINT_NAME_EDEFAULT.equals(viewpointName);
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE__DIAGRAM_NAME:
			return DIAGRAM_NAME_EDEFAULT == null ? diagramName != null : !DIAGRAM_NAME_EDEFAULT.equals(diagramName);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
		case RepresentationPackage.SIRIUS_DIAGRAM_PROTOTYPE___IS_VALID_CLASS__DIAGNOSTICCHAIN_MAP:
			return isValidClass((DiagnosticChain) arguments.get(0), (Map<Object, Object>) arguments.get(1));
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (creationCommandClass: "); //$NON-NLS-1$
		result.append(creationCommandClass);
		result.append(", viewpointName: "); //$NON-NLS-1$
		result.append(viewpointName);
		result.append(", diagramName: "); //$NON-NLS-1$
		result.append(diagramName);
		result.append(')');
		return result.toString();
	}

} // SiriusDiagramPrototypeImpl
