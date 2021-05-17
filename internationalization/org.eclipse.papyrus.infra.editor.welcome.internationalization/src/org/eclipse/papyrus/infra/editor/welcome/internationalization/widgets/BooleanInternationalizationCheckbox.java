/*****************************************************************************
 * Copyright (c) 2017 CEA LIST and others.
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

package org.eclipse.papyrus.infra.editor.welcome.internationalization.widgets;

import org.eclipse.papyrus.infra.editor.welcome.internationalization.widgets.editors.BooleanInternationalizationChecbox;
import org.eclipse.papyrus.infra.properties.ui.widgets.AbstractPropertyEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * The boolean internationalization checkbox widget for the internationalization.
 */
public class BooleanInternationalizationCheckbox extends AbstractPropertyEditor {

	/**
	 * The Boolean internationalization checkbox widget.
	 */
	protected BooleanInternationalizationChecbox editor;

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            The composite in which the widget will be displayed.
	 * @param style
	 *            The style for the widget.
	 */
	public BooleanInternationalizationCheckbox(final Composite parent, final int style) {
		setEditor(createLanguageDialogEditor(parent, style));
	}

	/**
	 * Creates the boolean internationalization checkbox.
	 *
	 * @param parent
	 *            The composite in which the widget will be displayed.
	 * @param style
	 *            The style for the widget.
	 * @return The boolean internationalization checkbox.
	 */
	protected org.eclipse.papyrus.infra.editor.welcome.internationalization.widgets.editors.BooleanInternationalizationChecbox createLanguageDialogEditor(
			final Composite parent, final int style) {
		return editor = new org.eclipse.papyrus.infra.editor.welcome.internationalization.widgets.editors.BooleanInternationalizationChecbox(parent, style);
	}

}
