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

package org.eclipse.papyrus.infra.internationalization.common.editor;

/**
 * Interface to define if the editor support the internationalization and the corresponding label modification.
 */
public interface IInternationalizationEditor {

	/**
	 * This allows to modify the part name.
	 * 
	 * @param name
	 *            The new name of the editor.
	 */
	public void modifyPartName(final String name);
	
	/**
	 * This allows to refresh the editor part.
	 */
	public void refreshEditorPart();

}
