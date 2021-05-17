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

package org.eclipse.papyrus.infra.internationalization.utils.utils;

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
	// public String getDiagramLabelWithoutName(final Diagram diagram) {
	// return getDiagramLabelWithoutName(diagram, true);
	// }

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
	// public String getDiagramLabelWithoutName(final Diagram diagram, final boolean localize) {
	// return LabelInternationalizationUtils.getLabelWithoutSubstract(diagram, localize);
	// }

	/**
	 * This allows to get the label of the diagram.
	 *
	 * @param diagram
	 *            The diagram.
	 * @return The label of the diagram or the name if the label is null.
	 */
	// public String getDiagramLabel(final Diagram diagram) {
	// return getDiagramLabel(diagram, true);
	// }

	/**
	 * This allows to get the label of the diagram.
	 *
	 * @param diagram
	 *            The diagram.
	 * @param localize
	 *            Boolean to determinate if the locale must be used.
	 * @return The label of the diagram or the name if the label is null.
	 */
	// public String getDiagramLabel(final Diagram diagram, final boolean localize) {
	// String result = null;
	// if (null != diagram.eResource() && getInternationalizationPreference(diagram)) {
	// result = getDiagramLabelWithoutName(diagram, localize);
	// }
	// return null != result ? result : diagram.getName();
	// }

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
	// public void setDiagramLabel(final Diagram diagram, final String value, final Locale locale) {
	// LabelInternationalizationUtils.setLabel(diagram, value, locale);
	// }

	/**
	 * This allows to modify the internationalization preference value for the
	 * diagram owner.
	 *
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @param value
	 *            The new preference value.
	 */
	// public void setInternationalizationPreference(final Diagram diagram, final boolean value) {
	// InternationalizationPreferencesUtils.setInternationalizationPreference(QualifiedNameUtils.getOwner(diagram),
	// value);
	// }

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
	// public boolean getInternationalizationPreference(final Diagram diagram) {
	// return InternationalizationPreferencesUtils
	// .getInternationalizationPreference(QualifiedNameUtils.getOwner(diagram));
	// }

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
	// public Command getSetDiagramLabelCommand(final EditingDomain domain, final Diagram diagram, final String value,
	// final Locale locale) {
	// return LabelInternationalizationUtils.getSetLabelCommand(domain, diagram, value, locale);
	// }

	/**
	 * This allows to get the label of the table without the getName when the
	 * label is null.
	 *
	 * @param table
	 *            The table.
	 * @return The label of the table.
	 */
	// public String getTableLabelWithoutName(final Table table) {
	// return getTableLabelWithoutName(table, true);
	// }

	/**
	 * This allows to get the label of the table without the getName when the
	 * label is null.
	 *
	 * @param table
	 *            The table.
	 * @param localize
	 *            Boolean to determinate if the locale must be used.
	 * @return The label of the table.
	 */
	// public String getTableLabelWithoutName(final Table table, final boolean localize) {
	// return LabelInternationalizationUtils.getLabelWithoutSubstract(table, localize);
	// }

	/**
	 * This allows to get the label of the table.
	 *
	 * @param table
	 *            The table.
	 * @return The label of the table or the name if the label is null.
	 */
	// public String getTableLabel(final Table table) {
	// return getTableLabel(table, true);
	// }

	/**
	 * This allows to get the label of the table.
	 *
	 * @param table
	 *            The table.
	 * @param localize
	 *            Boolean to determinate if the locale must be used.
	 * @return The label of the table or the name if the label is null.
	 */
	// public String getTableLabel(final Table table, final boolean localize) {
	// String result = null;
	// if (null != table.eResource() && getInternationalizationPreference(table)) {
	// result = getTableLabelWithoutName(table, localize);
	// }
	// return null != result ? result : table.getName();
	// }

	/**
	 * This allows to set the table label.
	 *
	 * @param table
	 *            The table.
	 * @param value
	 *            The label value.
	 * @param locale
	 *            The locale for which set the value (if <code>null</code> set
	 *            it for the current locale).
	 */
	// public void setTableLabel(final Table table, final String value, final Locale locale) {
	// LabelInternationalizationUtils.setLabel(table, value, locale);
	// }

	/**
	 * This allows to modify the internationalization preference value for the
	 * table owner.
	 *
	 * @param eObject
	 *            The {@link EObject) corresponding (to get its resource).
	 * @param value
	 *            The new preference value.
	 */
	// public void setInternationalizationPreference(final Table table, final boolean value) {
	// EObject tableOwner = table.getOwner();
	// if(null == tableOwner){
	// tableOwner = table.getContext();
	// }
	// InternationalizationPreferencesUtils.setInternationalizationPreference(tableOwner, value);
	// }

	/**
	 * This allows to get the internationalization preference value for the
	 * table (but it is the table owner because the diagram is in the notation
	 * file).
	 *
	 * @param diagram
	 *            The {@link Diagram) corresponding.
	 * @return <code>true</code> if the preference value is set to true,
	 *         <code>false</code> otherwise.
	 */
	// public boolean getInternationalizationPreference(final Table table) {
	// EObject tableOwner = table.getOwner();
	// if(null == tableOwner){
	// tableOwner = table.getContext();
	// }
	// return InternationalizationPreferencesUtils.getInternationalizationPreference(tableOwner);
	// }

	/**
	 * This allows to get the set table label command.
	 *
	 * @param domain
	 *            The editing domain to use.
	 * @param table
	 *            The table.
	 * @param value
	 *            The value to set.
	 * @param locale
	 *            The locale for which set the value (if <code>null</code> set
	 *            it for the current locale).
	 * @return The command which allow to set the table label.
	 */
	// public Command getSetTableLabelCommand(final EditingDomain domain, final Table table, final String value,
	// final Locale locale) {
	// return LabelInternationalizationUtils.getSetLabelCommand(domain, table, value, locale);
	// }

}
