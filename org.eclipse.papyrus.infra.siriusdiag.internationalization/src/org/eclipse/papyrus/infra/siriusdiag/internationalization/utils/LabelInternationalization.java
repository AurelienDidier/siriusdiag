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
package org.eclipse.papyrus.infra.siriusdiag.internationalization.utils;

import java.util.Locale;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.sirius.diagram.DSemanticDiagram;

/**
 * The internationalization label manager.
 */
public class LabelInternationalization {

	/**
	 * The singleton instance.
	 */
	private static LabelInternationalization instance;

	/**
	 * Constructor.
	 */
	protected LabelInternationalization() {

	}

	/**
	 * Get the singleton instance (create it if not existing).
	 * 
	 * @return The singleton instance.
	 */
	public static LabelInternationalization getInstance() {
		if (null == instance) {
			instance = new LabelInternationalization();
		}
		return instance;
	}

	/**
	 * This allows to get the label of the diagram without the getName when the
	 * label is null.
	 * 
	 * @param diagram
	 *            The diagram.
	 * @return The label of the diagram.
	 */
	public String getDiagramLabelWithoutName(final DSemanticDiagram diagram) {
		return getDiagramLabelWithoutName(diagram, true);
	}

	/**
	 * This allows to get the label of the diagram without the getName when the
	 * label is null.
	 * 
	 * @param diagram
	 *            The diagram.
	 * @param localize
	 *            Boolean to determinate if the locale must be used.
	 * @return The label of the diagram.
	 */
	public String getDiagramLabelWithoutName(final DSemanticDiagram diagram, final boolean localize) {
		return LabelInternationalizationUtils.getLabelWithoutSubstract(diagram, localize);
	}

	/**
	 * This allows to get the label of the diagram.
	 * 
	 * @param diagram
	 *            The diagram.
	 * @return The label of the diagram or the name if the label is null.
	 */
	public String getDiagramLabel(final DSemanticDiagram diagram) {
		return getDiagramLabel(diagram, true);
	}

	/**
	 * This allows to get the label of the diagram.
	 * 
	 * @param diagram
	 *            The diagram.
	 * @param localize
	 *            Boolean to determinate if the locale must be used.
	 * @return The label of the diagram or the name if the label is null.
	 */
	public String getDiagramLabel(final DSemanticDiagram diagram, final boolean localize) {
		String result = null;
		if (null != diagram.eResource() && getInternationalizationPreference(diagram)) {
			result = getDiagramLabelWithoutName(diagram, localize);
		}
		return null != result ? result : diagram.getName();
	}

	/**
	 * This allows to set the diagram label.
	 * 
	 * @param diagram
	 *            The diagram.
	 * @param value
	 *            The label value.
	 * @param locale
	 *            The locale for which set the value (if <code>null</code> set
	 *            it for the current locale).
	 */
	public void setDiagramLabel(final DSemanticDiagram diagram, final String value, final Locale locale) {
		LabelInternationalizationUtils.setLabel(diagram, value, locale);
	}

	/**
	 * This allows to modify the internationalization preference value for the
	 * diagram owner.
	 * 
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @param value
	 *            The new preference value.
	 */
	public void setInternationalizationPreference(final DSemanticDiagram diagram, final boolean value) {
//TODO:		InternationalizationPreferencesUtils.setInternationalizationPreference(QualifiedNameUtils.getOwner(diagram),
//				value);
	}

	/**
	 * This allows to get the internationalization preference value for the
	 * diagram (but it is the diagram owner because the diagram is in the
	 * notation file).
	 * 
	 * @param diagram
	 *            The {@link Diagram) corresponding.
	 * @return <code>true</code> if the preference value is set to true,
	 *         <code>false</code> otherwise.
	 */
	public boolean getInternationalizationPreference(final DSemanticDiagram diagram) {
//TODO		return InternationalizationPreferencesUtils
//				.getInternationalizationPreference(QualifiedNameUtils.getOwner(diagram));
		return true;
	}

	/**
	 * This allows to get the set diagram label command.
	 * 
	 * @param domain
	 *            The editing domain to use.
	 * @param diagram
	 *            The diagram.
	 * @param value
	 *            The value to set.
	 * @param locale
	 *            The locale for which set the value (if <code>null</code> set
	 *            it for the current locale).
	 * @return The command which allow to set the diagram label.
	 */
	public Command getSetDiagramLabelCommand(final EditingDomain domain, final DSemanticDiagram diagram, final String value,
			final Locale locale) {
		return LabelInternationalizationUtils.getSetLabelCommand(domain, diagram, value, locale);
	}


}
