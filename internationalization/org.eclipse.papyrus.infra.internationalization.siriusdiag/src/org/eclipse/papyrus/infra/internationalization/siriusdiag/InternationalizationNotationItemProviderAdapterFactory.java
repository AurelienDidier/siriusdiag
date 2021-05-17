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

package org.eclipse.papyrus.infra.internationalization.siriusdiag;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gmf.runtime.notation.NotationEditPlugin;
import org.eclipse.gmf.runtime.notation.provider.DiagramItemProvider;
import org.eclipse.gmf.runtime.notation.provider.NotationItemProviderAdapterFactory;
import org.eclipse.papyrus.infra.internationalization.siriusdiag.utils.LabelInternationalization;
import org.eclipse.sirius.diagram.DSemanticDiagram;

/**
 * This allows to re-implement the diagram adapter for the label
 * internationalization call.
 */
public class InternationalizationNotationItemProviderAdapterFactory extends NotationItemProviderAdapterFactory {

	/**
	 * Constructor.
	 */
	public InternationalizationNotationItemProviderAdapterFactory() {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.gmf.runtime.notation.provider.NotationItemProviderAdapterFactory#createDiagramAdapter()
	 */
	// @Override
	@Override
	public Adapter createDiagramAdapter() {
		if (diagramItemProvider == null) {
			diagramItemProvider = new DiagramItemProvider(this) {
				@Override
				public String getText(Object object) {
					String label = LabelInternationalization.getInstance().getDiagramLabel((DSemanticDiagram) object);
					return label == null || label.length() == 0
							? NotationEditPlugin.INSTANCE.getString("_UI_Diagram_type", true) //$NON-NLS-1$
							: NotationEditPlugin.INSTANCE.getString("_UI_Diagram_type", true) + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
				}
			};
		}

		return diagramItemProvider;
	}
}
