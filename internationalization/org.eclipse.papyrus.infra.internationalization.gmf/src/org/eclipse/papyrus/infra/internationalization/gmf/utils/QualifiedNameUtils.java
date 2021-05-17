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

package org.eclipse.papyrus.infra.internationalization.gmf.utils;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.gmf.runtime.notation.Diagram;
//import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Style;
//import org.eclipse.papyrus.infra.nattable.model.nattable.Table;
import org.eclipse.papyrus.infra.nattable.model.nattable.Table;

/**
 * The utils methods corresponding to the qualified name calculation for the
 * {@linl EObject}.
 */
public class QualifiedNameUtils {

	/**
	 * The separator for the qualified name for the properties file.
	 */
	public static final String QN_SEPARATOR_FOR_PROPERTIES = "__"; //$NON-NLS-1$

	/**
	 * The qualified name for the {@link EObject} in parameter.
	 *
	 * @param eObject
	 *            The {@link EObject} to calculate qualified name.
	 * @return The qualified name for the EObject or the XMI:id if no feature
	 *         name.
	 */
	public static String getQualifiedName(final EObject eObject) {
		StringBuilder result = new StringBuilder();

		EObject parent = eObject;
		boolean hasNotFoundName = false;

		// Loop until no more parent to calculate qualified name
		while (null != parent && !hasNotFoundName) {
			if (parent.eResource().getURI().equals(eObject.eResource().getURI())) {
				hasNotFoundName = true;

				// Get the name feature if existing
				final EStructuralFeature feature = parent.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
				if (null != feature) {
					Object featureValue = parent.eGet(feature);
					if (featureValue instanceof String) {
						result = appendQualifiedName(result, (String) featureValue);
						hasNotFoundName = false;
					}
				}
			}

			parent = parent.eContainer();
		}

		// The feature name was not found for an EObject in hierarchy, return
		// the xmi:id of the object
		if (hasNotFoundName) {
			if (eObject.eResource() instanceof XMIResource) {
				result = new StringBuilder();
				result.append(((XMIResource) eObject.eResource()).getID(eObject));
			}
		}

		return result.toString();
	}

	/**
	 * Get the diagram owner.
	 *
	 * @param diagram
	 *            A diagram
	 * @return The diagram owner
	 */
	public static EObject getOwner(final Diagram diagram) {
		final Style pvs = getPapyrusViewStyle(diagram);
		if (null != pvs) {
			EStructuralFeature ownerFeature = getOwnerFeature(pvs);
			if (null != ownerFeature) {
				final Object value = pvs.eGet(ownerFeature);
				if (null != value && value instanceof EObject) {
					return (EObject) value;
				}
			}
		}
		return diagram.getElement();
	}

	/**
	 * Get the owner feature of style by reflexive if exists.
	 *
	 * @param style
	 *            The current style to check.
	 * @return The structural feature or <code>null</code> if doesn't exist
	 */
	private static EStructuralFeature getOwnerFeature(final Style style) {
		EStructuralFeature feature = null;

		Iterator<EStructuralFeature> existingFeatures = style.eClass().getEAllStructuralFeatures().iterator();
		while (existingFeatures.hasNext() && null == feature) {
			EStructuralFeature currentFeature = existingFeatures.next();
			if (currentFeature.getName().equals("owner")) { //$NON-NLS-1$
				feature = currentFeature;
			}
		}

		return feature;
	}

	/**
	 * Get the papyrus view style of diagram by reflexive if exists.
	 *
	 * @param diagram
	 *            The diagram.
	 * @return The papyrus view style or <code>null</code> if doesn't exist.
	 */
	private static final Style getPapyrusViewStyle(final Diagram diagram) {
		Style papyrusViewStyle = null;

		final Iterator<?> ownedStyles = diagram.getStyles().iterator();
		while (ownedStyles.hasNext() && null == papyrusViewStyle) {
			final Object ownedStyle = ownedStyles.next();
			if (ownedStyle instanceof Style) {
				if (((Style) ownedStyle).eClass().getName().equals("PapyrusViewStyle")) { //$NON-NLS-1$
					papyrusViewStyle = (Style) ownedStyle;
				}
			}
		}

		return papyrusViewStyle;
	}

	/**
	 * This allows to append a string to the StringBuilder in parameter.
	 *
	 * @param initialStringBuilder
	 *            The initial string builder.
	 * @param toAdd
	 *            The string to add to string builder.
	 * @return The modified string builder.
	 */
	private static StringBuilder appendQualifiedName(final StringBuilder initialStringBuilder, final String toAdd) {
		StringBuilder result = new StringBuilder();
		result.append(toAdd);
		// If the initial string builder is not empty, add the qualified name
		// separator
		if (!initialStringBuilder.toString().isEmpty()) {
			result.append(QN_SEPARATOR_FOR_PROPERTIES);
		}
		result.append(initialStringBuilder);
		return result;
	}

	/**
	 * Get a diagram by its name.
	 *
	 * @param The
	 *            EMF logical model.
	 * @param diagramName
	 *            Name of the diagram. This is the name set by the user.
	 * @param qualifiedName
	 *            The qualified name representing the diagram element or
	 *            <code>null</code>.
	 * @return The found diagram or <code>null</code>.
	 */
	public static Diagram getDiagram(final Resource resource, final String diagramName, final String qualifiedName) {
		if (null != diagramName && !diagramName.isEmpty()) {
			for (final EObject element : resource.getContents()) {
				if (element instanceof Diagram) {
					final Diagram diagram = (Diagram) element;

					if (diagramName.equals(diagram.getName())) {
						if (null == qualifiedName || qualifiedName.isEmpty()
								|| getQualifiedName(diagram.getElement()).equals(qualifiedName)) {
							// Found
							return diagram;
						}
					}
				}
			}
		}

		// not found
		return null;
	}

	/**
	 * Get a table by its name.
	 *
	 * @param model
	 *            The EMF logical model.
	 * @param tableName
	 *            Name of the table. This is the name set by the user.
	 * @param qualifiedName
	 *            The qualified name representing the table owner or
	 *            <code>null</code>.
	 * @return The found diagram or <code>null</code>.
	 */
	public static Table getTable(final Resource resource, final String tableName, final String qualifiedName) {
		if (null != tableName && !tableName.isEmpty()) {
			for (final EObject element : resource.getContents()) {
				if (element instanceof Table) {
					final Table table = (Table) element;

					if (tableName.equals(table.getName())) {
						// Found
						if (null == qualifiedName || qualifiedName.isEmpty()
								|| getQualifiedName(table.getOwner()).equals(qualifiedName)) {
							return table;
						}
					}
				}
			}
		}
		// not found
		return null;
	}
}
