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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProvider;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceSetItemProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor;
import org.eclipse.papyrus.infra.internationalization.utils.utils.LabelInternationalization;
import org.eclipse.papyrus.infra.internationalization.utils.utils.LabelInternationalizationUtils;
import org.eclipse.papyrus.infra.siriusdiag.ui.Activator;
import org.eclipse.papyrus.infra.ui.lifecycleevents.ISaveAndDirtyService;
import org.eclipse.papyrus.infra.widgets.util.IRevealSemanticElement;
import org.eclipse.papyrus.infra.widgets.util.NavigationTarget;
import org.eclipse.sirius.business.api.dialect.command.RefreshRepresentationsCommand;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.provider.DiagramItemProviderAdapterFactory;
import org.eclipse.sirius.diagram.ui.tools.internal.editor.DDiagramEditorImpl;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * DocumenView Editor.
 *
 * This editor is contributed throw the extension point org.eclipse.papyrus.infra.ui.papyrusDiagram.
 *
 * In order to get the new child menu, we register the action bar contribution using this same extension point and we use if for this editor.
 */
@SuppressWarnings("restriction")
public class NestedSiriusDiagramViewEditor extends DDiagramEditorImpl implements IEditingDomainProvider, IInternationalizationEditor, IRevealSemanticElement, NavigationTarget {

	// shall use SiriusDiagramEditorInput

	/** the service registry */
	protected ServicesRegistry servicesRegistry;

	/**
	 * The edited document template
	 */
	private DSemanticDiagram diagram;

	private TransactionalEditingDomain editingDomain;

	private CommandStackListener commandStackListener;

	private ComposedAdapterFactory adapterFactory;

	/**
	 *
	 * Constructor.
	 *
	 * @param servicesRegistry
	 *            the Papyrus service registry, it can't be <code>null</code>
	 * @param rawModel
	 *            the edited element, it can't be <code>null</code>
	 */
	public NestedSiriusDiagramViewEditor(ServicesRegistry servicesRegistry, DSemanticDiagram rawModel) {
		super();

		// Need to manage the part label synchronizer for the table labels
		LabelInternationalizationUtils.managePartLabelSynchronizer(diagram, this);

		// Register this part to the ISaveAndDirtyService.
		// This will allows to be notified of saveAs events, and the isDirty
		// flag will be taken into
		// account.
		ISaveAndDirtyService saveAndDirtyService = null;
		try {
			saveAndDirtyService = servicesRegistry.getService(ISaveAndDirtyService.class);
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		saveAndDirtyService.registerIsaveablePart(this);

		this.diagram = rawModel;
		this.servicesRegistry = servicesRegistry;
		try {
			editingDomain = servicesRegistry.getService(TransactionalEditingDomain.class);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.isNotNull(this.diagram, "The edited diagram is null. The Diagram Editor creation failed"); //$NON-NLS-1$
		Assert.isNotNull(this.servicesRegistry, "The papyrus ServicesRegistry is null. The Diagram Editor creation failed."); //$NON-NLS-1$
		initializeEditingDomain();
	}

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.ui.internal.editor.CustomDocumentStructureTemplateEditor#initializeEditingDomain()
	 *
	 */
	public void initializeEditingDomain() {
		if (this.servicesRegistry == null) {
			return;
		}
		// super.initializeEditingDomain();
		initAdapterFactory();
		initDomainAndStack();


	}

	// public TransactionalEditingDomain getEditingDomain() {
	// if (this.editingDomain instanceof TransactionalEditingDomain) {
	// return this.editingDomain;
	// }
	// return null;
	// }


	/**
	 * this method is in charge to init the Editing Domain and the CommandStack
	 */
	protected void initDomainAndStack() {
		this.editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
		addCommandStackListener(this.editingDomain.getCommandStack());

		// final CommandStack commandStack = new BasicCommandStack();
		// addCommandStackListener(commandStack);
		// editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
	}

	protected void addCommandStackListener(final CommandStack commandStack) {
		commandStack.addCommandStackListener(this.commandStackListener = new CommandStackListener() {

			@Override
			public void commandStackChanged(EventObject event) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * Init the adapter factory
	 */
	protected void initAdapterFactory() {
		adapterFactory = createComposedAdapterFactory();
		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new DiagramItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
	}

	/**
	 *
	 * @return
	 *         the created ComposedAdapterFactory
	 */
	protected ComposedAdapterFactory createComposedAdapterFactory() {
		return new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
	}

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#getEditingDomain()
	 *
	 * @return
	 */
	@Override
	public TransactionalEditingDomain getEditingDomain() {
		try {
			return ServiceUtils.getInstance().getTransactionalEditingDomain(this.servicesRegistry);
		} catch (final ServiceException e) {
			Activator.log.error(e);
		}
		return null;
	}

	// @Override
	// public void doSave(IProgressMonitor monitor) {
	// // manage by the Papyrus main editor
	// }

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#doSaveAs()
	 *
	 */
	// @Override
	// public void doSaveAs() {
	// }

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 *
	 * @param site
	 * @param input
	 */
	// @Override
	@Override
	public void init(IEditorSite site, IEditorInput input) {// throws PartInitException {
		try {
			super.init(site, input);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPartName(LabelInternationalization.getInstance().getDiagramLabel(getDiagram()));// TODO
		// titleImage = DIAG_IMG_DESC.createImage();
		// setTitleImage(titleImage);
	}


	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#isDirty()
	 *
	 * @return
	 */
	// @Override
	// @Override
	// public boolean isDirty() {
	// // manage by the Papyrus main editor
	// return false;
	// }

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#isSaveAsAllowed()
	 *
	 * @return
	 */
	// @Override
	// public boolean isSaveAsAllowed() {
	// // manage by the Papyrus main editor
	// return false;
	// }


	// /**
	// *
	// * @param commandStack
	// */
	// @Override
	// protected void initDomainAndStack() {
	// final TransactionalEditingDomain domain = getEditingDomain();
	// Assert.isTrue(domain instanceof AdapterFactoryEditingDomain);
	// this.editingDomain = (AdapterFactoryEditingDomain) domain;
	// final CommandStack stack = this.editingDomain.getCommandStack();
	// addCommandStackListener(stack);
	// }

	// /**
	// * @see org.eclipse.papyrus.infra.siriusdiag.ui.internal.editor.CustomDocumentStructureTemplateEditor#initAdapterFactory()
	// *
	// */
	// @Override
	// protected void initAdapterFactory() {
	// adapterFactory = createComposedAdapterFactory();
	// adapterFactory.addAdapterFactory(new CustomResourceItemProviderAdapterFactory());
	// adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
	// adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
	// }

	// /**
	// * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	// *
	// * @param editor
	// * @return
	// */
	// @Override
	protected IEditorSite createSite(IEditorPart editor) {
		// used to be able to have the error editor part nested in the embedded emf editor
		return getEditorSite();
	}

	/**
	 *
	 * Custom ResourceItemProviderAdapterFactory to be able to show only the structure of the DSemanticDiagram
	 * and not other elements contained in the file
	 *
	 */
	private class CustomResourceItemProviderAdapterFactory extends ResourceItemProviderAdapterFactory {

		/**
		 * @see org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory#createResourceSetAdapter()
		 *
		 * @return
		 */
		@Override
		public Adapter createResourceSetAdapter() {
			return new CustomResourceSetItemProvider(this);
		}

		/**
		 * @see org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory#createResourceAdapter()
		 *
		 * @return
		 */
		@Override
		public Adapter createResourceAdapter() {
			return new CustomResourceItemProvider(this);
		}
	}


	/**
	 *
	 * Custom ResourceSetItemProvider used to display only the edited documentemplate and not other file contents
	 *
	 */
	private class CustomResourceSetItemProvider extends ResourceSetItemProvider {

		/**
		 * Constructor.
		 *
		 * @param adapterFactory
		 */
		public CustomResourceSetItemProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}


		/**
		 * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#getElements(java.lang.Object)
		 *
		 * @param object
		 * @return
		 */
		@Override
		public Collection<?> getElements(Object object) {
			return Collections.singleton(diagram.eResource());
		}
	}

	/**
	 *
	 * Custom ResourceItemProvider to get only the {@link DSemanticDiagram} displayed in the EcoreEditor
	 *
	 */
	private class CustomResourceItemProvider extends ResourceItemProvider {

		/**
		 * Constructor.
		 *
		 * @param adapterFactory
		 */
		public CustomResourceItemProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		/**
		 *
		 * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#getElements(java.lang.Object)
		 *
		 * @param object
		 * @return
		 */
		@Override
		public Collection<?> getElements(Object object) {
			return super.getElements(object);
		}

		/**
		 *
		 * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#hasChildren(java.lang.Object)
		 *
		 * @param object
		 * @return
		 */
		@Override
		public boolean hasChildren(Object object) {
			if (object instanceof Resource) {
				return true;
			}
			return super.hasChildren(object);
		}

		/**
		 *
		 * @see org.eclipse.emf.edit.provider.resource.ResourceItemProvider#getParent(java.lang.Object)
		 *
		 * @param object
		 * @return
		 */
		@Override
		public Object getParent(Object object) {
			return super.getParent(object);
		}

		/**
		 *
		 * @see org.eclipse.emf.edit.provider.resource.ResourceItemProvider#getChildren(java.lang.Object)
		 *
		 * @param object
		 * @return
		 */
		@Override
		public Collection<?> getChildren(Object object) {
			if (object instanceof Resource) {
				return Collections.singletonList(diagram);
			}
			return super.getChildren(object);
		}

	}

	/**
	 * reveal all editpart that represent an element in the given list.
	 *
	 * @see org.eclipse.papyrus.infra.core.ui.IRevealSemanticElement#revealSemanticElement(java.util.List)
	 *
	 */
	@Override
	public void revealSemanticElement(List<?> elementList) {
		revealElement(elementList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean revealElement(Object element) {
		return revealElement(Collections.singleton(element));
	}

	public Object getSemanticElement(Object wrapper) {
		if (wrapper instanceof IGraphicalEditPart) {
			return ((IGraphicalEditPart) wrapper).resolveSemanticElement();
		}
		if (wrapper instanceof IAdaptable) {
			return ((IAdaptable) wrapper).getAdapter(EObject.class);
		}
		return null;
	}


	/**
	 * {@inheritDoc}
	 *
	 * reveal all editpart that represent an element in the given list.
	 *
	 * @see org.eclipse.papyrus.infra.core.ui.IRevealSemanticElement#revealSemanticElement(java.util.List)
	 *
	 */
	@Override
	public boolean revealElement(Collection<?> elementList) {

		// get the graphical viewer
		GraphicalViewer graphicalViewer = getGraphicalViewer();
		if (graphicalViewer != null) {

			// look amidst all edit part if the semantic is contained in the list
			Iterator<?> iter = graphicalViewer.getEditPartRegistry().values().iterator();
			IGraphicalEditPart researchedEditPart = null;
			List<?> clonedList = new ArrayList<>(elementList);
			List<IGraphicalEditPart> partSelection = new ArrayList<>();

			while (iter.hasNext() && !clonedList.isEmpty()) {
				Object currentEditPart = iter.next();
				// look only amidst IPrimary editpart to avoid compartment and labels of links
				if (currentEditPart instanceof IPrimaryEditPart) {
					Object currentElement = getSemanticElement(currentEditPart);
					if (clonedList.contains(currentElement)) {
						clonedList.remove(currentElement);
						researchedEditPart = ((IGraphicalEditPart) currentEditPart);
						partSelection.add(researchedEditPart);

					}
				}
			}

			// We may also search for a GMF View (Instead of a semantic model Element)
			if (!clonedList.isEmpty()) {
				for (Iterator<?> iterator = clonedList.iterator(); iterator.hasNext();) {
					Object element = iterator.next();
					if (graphicalViewer.getEditPartRegistry().containsKey(element) && !clonedList.isEmpty()) {
						iterator.remove();
						researchedEditPart = (IGraphicalEditPart) graphicalViewer.getEditPartRegistry().get(element);
						partSelection.add(researchedEditPart);
					}
				}
			}

			// the second test, as the model element is not a PrimaryEditPart, is to allow the selection even if the user selected it with other elements
			// and reset the selection if only the model is selected
			if (clonedList.isEmpty() || (clonedList.size() == 1 && clonedList.get(0) == getDiagram().getElement())) {
				// all parts have been found
				IStructuredSelection sSelection = new StructuredSelection(partSelection);
				// this is used instead of graphicalViewer.select(IGraphicalEditPart) as the later only allows the selection of a single element
				graphicalViewer.setSelection(sSelection);
				if (!partSelection.isEmpty()) {
					graphicalViewer.reveal(partSelection.get(0));
				}
				return true;
			}
		}

		return false;
	}



	/**
	 * @see org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor#modifyPartName(java.lang.String)
	 *
	 * @param name
	 */
	@Override
	public void modifyPartName(final String name) {
		setPartName(name);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor#refreshEditorPart()
	 */
	@Override
	public void refreshEditorPart() {
		if (null == getDiagramEditPart()) {
			return;// see bug 551530
		}

		// TODO: Implement a better refresh that good
		DRepresentation representation = getRepresentation();
		if (representation != null) {
			Session session = SessionManager.INSTANCE.getSession(representation.eContainer());
			session.getTransactionalEditingDomain().getCommandStack().execute(new RefreshRepresentationsCommand(session.getTransactionalEditingDomain(), new NullProgressMonitor(), representation));
		}

		// old version DiagramHelper.forceRefresh(getDiagramEditPart());
	}


	/**
	 *
	 * The command stack listener (implementation duplicated from the super class)
	 *
	 */
	// private class CustomCommandStackListener implements CommandStackListener {
	//
	// /**
	// *
	// * @see org.eclipse.emf.common.command.CommandStackListener#commandStackChanged(java.util.EventObject)
	// *
	// * @param event
	// */
	// @Override
	// public void commandStackChanged(final EventObject event) {
	// final Composite container = getContainer();
	// if (container.isDisposed()) {
	// return; // to avoid an exception!
	// }
	// getContainer().getDisplay().asyncExec(new Runnable() {
	// @Override
	// public void run() {
	// firePropertyChange(IEditorPart.PROP_DIRTY);
	//
	// // Try to select the affected objects.
	// //
	// Command mostRecentCommand = ((CommandStack) event.getSource()).getMostRecentCommand();
	// if (mostRecentCommand != null) {
	// setSelectionToViewer(mostRecentCommand.getAffectedObjects());
	// }
	// for (Iterator<PropertySheetPage> i = propertySheetPages.iterator(); i.hasNext();) {
	// PropertySheetPage propertySheetPage = i.next();
	// if (propertySheetPage.getControl().isDisposed()) {
	// i.remove();
	// } else {
	// propertySheetPage.refresh();
	// }
	// }
	// }
	// });
	// }
	// }

}
