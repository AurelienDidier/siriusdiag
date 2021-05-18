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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
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
