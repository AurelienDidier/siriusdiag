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
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.nattable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.internationalization.InternationalizationEntry;
import org.eclipse.papyrus.infra.internationalization.InternationalizationFactory;
//import org.eclipse.papyrus.infra.nattable.model.nattable.Table;
import org.eclipse.papyrus.infra.internationalization.gmf.utils.QualifiedNameUtils;
import org.eclipse.papyrus.infra.nattable.model.nattable.Table;

/**
 * The internationalization key resolver for the infra elements (Diagrams an Tables).
 */
public class InternationalizationKeyResolver extends org.eclipse.papyrus.infra.internationalization.utils.InternationalizationKeyResolver {

	/**
	 * The '_labelTable_' prefix of each table entry.
	 */
	protected static final String LABEL_TABLE_PREFIX_QN = "_labelTable_"; //$NON-NLS-1$

	/**
	 * The '_label_' prefix of each entry.
	 */
	public static final String LABEL_PREFIX = "_label_"; //$NON-NLS-1$

	/**
	 * The notation extension file (cannot use the
	 * NotationModel.NOTATION_FILE_EXTENSION because of dependencies cycle).
	 */
	protected static final String NOTATION_FILE_EXTENSION = "notation"; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public InternationalizationKeyResolver() {
		// Do nothing
	}

	/**
	 * This allows to create an internationalization entry corresponding to the
	 * key in parameter and creating table or diagram reference if possible.
	 *
	 * @param key
	 *            The key.
	 * @param resourceSet
	 *            The current resource set.
	 * @param uri
	 *            The uri of the file to manage.
	 * @return The created internationalization entry.
	 */
	@Override
	public InternationalizationEntry createInternationalizationEntryByKey(final String key, final ResourceSet resourceSet,
			final URI uri) {
		final InternationalizationEntry entry = InternationalizationFactory.eINSTANCE.createInternationalizationEntry();

		if (key.startsWith(LABEL_TABLE_PREFIX_QN)) {
			final String keyWithoutPrefix = key.substring(LABEL_TABLE_PREFIX_QN.length());
			final String qualifiedName = keyWithoutPrefix.substring(0, keyWithoutPrefix.indexOf(LABEL_PREFIX));
			final String tableName = keyWithoutPrefix
					.substring(keyWithoutPrefix.indexOf(LABEL_PREFIX) + LABEL_PREFIX.length());

			final Resource umlResource = resourceSet
					.getResource(uri.trimFileExtension().appendFileExtension(NOTATION_FILE_EXTENSION), true);

			if (null != umlResource && null != umlResource.getContents() && !umlResource.getContents().isEmpty()) {
				final Table foundTable = QualifiedNameUtils.getTable(umlResource, tableName, qualifiedName);
				entry.setKey(foundTable);
			}
		} else {
			entry.setKey(key);
		}

		return entry;
	}

	/**
	 * This allows to get the entry key (can be override to get element string
	 * identifier instead of string key).
	 *
	 * @param entry
	 *            The internationalization entry.
	 * @return The key as String.
	 */
	@Override
	public String getKey(final InternationalizationEntry entry) {
		final StringBuilder result = new StringBuilder();
		if (entry.getKey() instanceof Table) {
			result.append(LABEL_TABLE_PREFIX_QN);
			final Table table = (Table) entry.getKey();
			final EObject tableContainer = table.getOwner();
			result.append(QualifiedNameUtils.getQualifiedName(tableContainer));
			result.append(LABEL_PREFIX);
			result.append(table.getName());
		} else {
			result.append((String) entry.getKey());
		}
		return result.toString();

	}

	/**
	 * This allows to dispose the key resolver.
	 */
	@Override
	public void dispose() {
		// Do nothing here
	}

}
