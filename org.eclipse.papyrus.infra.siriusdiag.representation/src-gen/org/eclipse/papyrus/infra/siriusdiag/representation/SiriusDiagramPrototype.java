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
package org.eclipse.papyrus.infra.siriusdiag.representation;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.papyrus.infra.architecture.representation.PapyrusRepresentationKind;
import org.eclipse.sirius.diagram.DSemanticDiagram;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sirius Diagram Prototype</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class allows to reference the SiriusDiagramPrototype to use to create a new Sirius Diagram
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype#getSiriusDiagramPrototype <em>Sirius Diagram Prototype</em>}</li>
 * <li>{@link org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype#getCreationCommandClass <em>Creation Command Class</em>}</li>
 * </ul>
 *
 * @see org.eclipse.papyrus.infra.siriusdiag.representation.RepresentationPackage#getSiriusDiagramPrototype()
 * @model
 * @generated
 */
public interface SiriusDiagramPrototype extends PapyrusRepresentationKind {
	/**
	 * Returns the value of the '<em><b>Sirius Diagram Prototype</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sirius Diagram Prototype</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This feature references the SiriusDiagramPrototype to use to create a new Sirius Diagram
	 * <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Sirius Diagram Prototype</em>' reference.
	 * @see #setSiriusDiagramPrototype(SiriusDiagramPrototype)
	 * @see org.eclipse.papyrus.infra.siriusdiag.representation.RepresentationPackage#getSiriusDiagramPrototype_SiriusDiagramPrototype()
	 * @model required="true"
	 * @generated
	 */
	DSemanticDiagram getSiriusDiagramPrototype();

	/**
	 * Sets the value of the '{@link org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype#getSiriusDiagramPrototype <em>Sirius Diagram Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Sirius Diagram Prototype</em>' reference.
	 * @see #getSiriusDiagramPrototype()
	 * @generated
	 */
	void setSiriusDiagramPrototype(DSemanticDiagram value);

	/**
	 * Returns the value of the '<em><b>Creation Command Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This feature allows to define the class to use to create the new document. The class must implements ICreateDSemanticDiagramEditorCommand.
	 * <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Creation Command Class</em>' attribute.
	 * @see #setCreationCommandClass(String)
	 * @see org.eclipse.papyrus.infra.siriusdiag.representation.RepresentationPackage#getSiriusDiagramPrototype_CreationCommandClass()
	 * @model required="true"
	 * @generated
	 */
	String getCreationCommandClass();

	/**
	 * Sets the value of the '{@link org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype#getCreationCommandClass <em>Creation Command Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Creation Command Class</em>' attribute.
	 * @see #getCreationCommandClass()
	 * @generated
	 */
	void setCreationCommandClass(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * this methid is used by the emf validation framework
	 * <!-- end-model-doc -->
	 *
	 * @model
	 * @generated
	 */
	boolean isValidClass(DiagnosticChain chain, Map<Object, Object> context);

} // SiriusDiagramPrototype
