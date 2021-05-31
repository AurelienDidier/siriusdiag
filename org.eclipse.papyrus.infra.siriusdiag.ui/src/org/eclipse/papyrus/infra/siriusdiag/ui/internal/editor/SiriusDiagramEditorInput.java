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
package org.eclipse.papyrus.infra.siriusdiag.ui.internal.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.papyrus.infra.siriusdiag.ui.internal.messages.Messages;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * DocumentStructureTemplate EditorInput.
 *
 */
public class SiriusDiagramEditorInput implements IEditorInput {

	/** The input for the Document widget */
	private final DSemanticDiagram diagramInstance;

	/**
	 *
	 * Constructor.
	 *
	 * @param siriusDiagram
	 *            the document template to edit
	 */
	// TODO:Essayer sans la classe SiriusDiagramEditorInput
	public SiriusDiagramEditorInput(final DSemanticDiagram siriusDiagram) {
		this.diagramInstance = siriusDiagram;
	}

	/**
	 *
	 * @return
	 *         the document template for which we are opening an editor
	 */
	public DSemanticDiagram getDSemanticDiagram() {
		return this.diagramInstance;
	}

	/**
	 *
	 * @see org.eclipse.ui.IEditorInput#exists()
	 *
	 * @return
	 */
	@Override
	public boolean exists() {
		return false;// not required in a Papyrus context
	}

	/**
	 *
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 *
	 * @return
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return null; // not required in a Papyrus context
	}

	/**
	 *
	 * @see org.eclipse.ui.IEditorInput#getName()
	 *
	 * @return
	 */
	@Override
	public String getName() {
		final String name = this.diagramInstance.getName();
		return name == null || name.isEmpty() ? Messages.SiriusDiagramEditorInput_NoName : name;
	}

	/**
	 *
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 *
	 * @return
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;// not required in a Papyrus context
	}

	/**
	 *
	 * @return
	 *         the description
	 */
	private String getDescription() {
		final String description = this.diagramInstance.getDocumentation();
		return description == null || description.isEmpty() ? Messages.SiriusDiagramEditorInput_NoDescription : description;
	}

	/**
	 *
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 *
	 * @return
	 */
	@Override
	public String getToolTipText() {
		return NLS.bind(Messages.SiriusDiagramEditorInput_Tooltip, getName(), getDescription());
	}

	/**
	 *
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 *
	 * @param adapter
	 *            the only supported type are {@link URI} and {@link DSemanticDiagram}
	 * @return
	 *         the uri of the file containing the {@link DSemanticDiagram} to edit or the {@link DSemanticDiagram} itself
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == URI.class) {
			return adapter.cast(this.diagramInstance.eResource().getURI());
		}
		if (adapter == DSemanticDiagram.class) {
			return adapter.cast(this.diagramInstance);
		}
		return null;
	}

}
