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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProvider;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceSetItemProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramCommandStack;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditDomain;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.internationalization.common.editor.IInternationalizationEditor;
import org.eclipse.papyrus.infra.siriusdiag.representation.SiriusDiagramPrototype;
import org.eclipse.papyrus.infra.ui.lifecycleevents.ISaveAndDirtyService;
import org.eclipse.papyrus.infra.widgets.util.IRevealSemanticElement;
import org.eclipse.papyrus.infra.widgets.util.NavigationTarget;
import org.eclipse.sirius.business.api.dialect.command.RefreshRepresentationsCommand;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.common.tools.api.util.EclipseUtil;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.ui.tools.internal.editor.DDiagramCommandStack;
import org.eclipse.sirius.diagram.ui.tools.internal.editor.DDiagramEditorImpl;
import org.eclipse.sirius.ui.business.api.dialect.DialectUI;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.ui.business.api.session.SessionEditorInput;
import org.eclipse.sirius.ui.business.internal.dialect.DialectUIManagerImpl;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.description.audit.provider.AuditItemProviderAdapterFactory;
import org.eclipse.sirius.viewpoint.description.provider.DescriptionItemProviderAdapterFactory;
import org.eclipse.sirius.viewpoint.description.style.provider.StyleItemProviderAdapterFactory;
import org.eclipse.sirius.viewpoint.description.tool.provider.ToolItemProviderAdapterFactory;
import org.eclipse.sirius.viewpoint.description.validation.provider.ValidationItemProviderAdapterFactory;
import org.eclipse.sirius.viewpoint.provider.ViewpointItemProviderAdapterFactory;
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

	/** the service registry */
	protected ServicesRegistry servicesRegistry;

	/**
	 * The edited document template
	 */
	private SiriusDiagramPrototype proto;

	private Session protoSession;

	private URI uri;

	private TransactionalEditingDomain editingDomain;

	private CommandStackListener commandStackListener;

	private ComposedAdapterFactory composedAdapterFactory;

	private DSemanticDiagram diagram;

	@Override
	public Object getAdapter(final Class type) {
		Object adapter = null;
		return super.getAdapter(type);
	}

	/**
	 *
	 * Constructor.
	 *
	 * @param servicesRegistry
	 *            the Papyrus service registry, it can't be <code>null</code>
	 * @param prototype
	 *            the edited element, it can't be <code>null</code>
	 */
	public NestedSiriusDiagramViewEditor(ServicesRegistry servicesRegistry, SiriusDiagramPrototype prototype) {
		super();

		ISaveAndDirtyService saveAndDirtyService = null;
		try {
			saveAndDirtyService = servicesRegistry.getService(ISaveAndDirtyService.class);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		saveAndDirtyService.registerIsaveablePart(this);

		this.diagram = prototype.getDSemanticDiagram();
		this.proto = prototype;
		this.uri = prototype.getUri();
		this.protoSession = prototype.getSession();
		this.servicesRegistry = servicesRegistry;
		this.editingDomain = prototype.getSession().getTransactionalEditingDomain();


		Assert.isNotNull(this.proto, "The edited diagram is null. The Diagram Editor creation failed"); //$NON-NLS-1$
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
		initAdapterFactory();
		initDomainAndStack();
	}


	/**
	 * this method is in charge to init the Editing Domain and the CommandStack
	 */
	protected void initDomainAndStack() {
		// this.editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
		this.editingDomain.getCommandStack().addCommandStackListener(new CommandStackListener() {

			@Override
			public void commandStackChanged(final EventObject event) {
				getSite().getShell().getDisplay().asyncExec(() -> {
					firePropertyChange(IEditorPart.PROP_DIRTY);
				});
			}
		});

	}

	/**
	 * @generated
	 */
	@Override
	protected void configureDiagramEditDomain() {

		DiagramEditDomain editDomain = new DiagramEditDomain(this);
		editDomain.setActionManager(createActionManager());
		setEditDomain(editDomain);

		if (editDomain != null) {
			final CommandStack stack = editDomain.getCommandStack();

			if (stack != null) {
				// dispose the old stack
				stack.dispose();
			}

			// create and assign the new stack
			final DiagramCommandStack diagramStack = new DDiagramCommandStack(getDiagramEditDomain());
			diagramStack.setOperationHistory(getOperationHistory());

			// changes made on the stack can be undone from this editor
			diagramStack.setUndoContext(getUndoContext());

			editDomain.setCommandStack(diagramStack);
		}

	}



	/**
	 * Init the adapter factory
	 */
	protected void initAdapterFactory() {

		final DialectUIManagerImpl manager = new DialectUIManagerImpl();
		composedAdapterFactory = new ComposedAdapterFactory();
		if (SiriusPlugin.IS_ECLIPSE_RUNNING) {
			final List<DialectUI> parsedDialects = EclipseUtil.getExtensionPlugins(DialectUI.class, DialectUIManager.ID, DialectUIManager.CLASS_ATTRIBUTE);
			for (final DialectUI dialect : parsedDialects) {
				// manager.enableDialectUI(dialect);
				composedAdapterFactory.addAdapterFactory(dialect.getServices().createAdapterFactory());
			}
		}


		composedAdapterFactory.addAdapterFactory(new DescriptionItemProviderAdapterFactory());
		composedAdapterFactory.addAdapterFactory(new ViewpointItemProviderAdapterFactory());
		composedAdapterFactory.addAdapterFactory(new StyleItemProviderAdapterFactory());
		composedAdapterFactory.addAdapterFactory(new ToolItemProviderAdapterFactory());
		composedAdapterFactory.addAdapterFactory(new ValidationItemProviderAdapterFactory());
		composedAdapterFactory.addAdapterFactory(new AuditItemProviderAdapterFactory());
		// composedAdapterFactory.addAdapterFactory(new EMFCommandFactory());

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
		return this.editingDomain;
	}

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 *
	 * @param site
	 * @param input
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) {// throws PartInitException {
		final SiriusDiagramEditorInput diagramViewEditorInput = new SiriusDiagramEditorInput(this.proto);

		this.editingDomain.getCommandStack().execute(new RecordingCommand(this.editingDomain) {

			@Override
			protected void doExecute() {
				final SessionEditorInput sessionEditorInput = new SessionEditorInput(uri, diagram.getName(), protoSession);
				try {
					NestedSiriusDiagramViewEditor.super.init(site, diagramViewEditorInput);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void doSetInput(IEditorInput input, boolean releaseEditorContents) throws CoreException {
		super.doSetInput(input, releaseEditorContents);
	}



	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#isDirty()
	 *
	 * @return
	 */
	@Override
	public boolean isDirty() {
		// manage by the Papyrus main editor
		return false;
	}

	/**
	 *
	 * @see org.eclipse.papyrus.infra.siriusdiag.presentation.DocumentStructureTemplateEditor#isSaveAsAllowed()
	 *
	 * @return
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// manage by the Papyrus main editor
		return false;
	}


	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	 *
	 * @param editor
	 * @return
	 */
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
			return Collections.singleton(proto.eResource());
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
				return Collections.singletonList(proto);
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

		// TODO: Implement a better refresh
		DRepresentation representation = getRepresentation();
		if (representation != null) {
			Session session = SessionManager.INSTANCE.getSession(representation.eContainer());
			session.getTransactionalEditingDomain().getCommandStack().execute(new RefreshRepresentationsCommand(session.getTransactionalEditingDomain(), new NullProgressMonitor(), representation));
		}
		// old version DiagramHelper.forceRefresh(getDiagramEditPart());
	}
}
