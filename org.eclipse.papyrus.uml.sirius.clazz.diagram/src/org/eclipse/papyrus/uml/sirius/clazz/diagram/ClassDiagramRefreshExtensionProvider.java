package org.eclipse.papyrus.uml.sirius.clazz.diagram;

import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.business.api.refresh.IRefreshExtension;
import org.eclipse.sirius.diagram.business.api.refresh.IRefreshExtensionProvider;

public class ClassDiagramRefreshExtensionProvider implements IRefreshExtensionProvider {

	public ClassDiagramRefreshExtensionProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean provides(DDiagram viewPoint) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IRefreshExtension getRefreshExtension(DDiagram viewPoint) {
		// TODO Auto-generated method stub
		return null;
	}

}
