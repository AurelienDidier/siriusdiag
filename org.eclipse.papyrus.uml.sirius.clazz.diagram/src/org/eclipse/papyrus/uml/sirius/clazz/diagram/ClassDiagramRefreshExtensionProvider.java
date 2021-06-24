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
package org.eclipse.papyrus.uml.sirius.clazz.diagram;

import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.business.api.refresh.IRefreshExtension;
import org.eclipse.sirius.diagram.business.api.refresh.IRefreshExtensionProvider;

public class ClassDiagramRefreshExtensionProvider implements IRefreshExtensionProvider {

	public ClassDiagramRefreshExtensionProvider() {
	}

	@Override
	public boolean provides(DDiagram viewPoint) {
		return false;
	}

	@Override
	public IRefreshExtension getRefreshExtension(DDiagram viewPoint) {
		return null;
	}

}
