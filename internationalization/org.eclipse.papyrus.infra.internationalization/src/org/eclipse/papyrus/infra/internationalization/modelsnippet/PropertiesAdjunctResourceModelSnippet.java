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

package org.eclipse.papyrus.infra.internationalization.modelsnippet;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.papyrus.infra.core.resource.AdjunctResourceModelSnippet;
import org.eclipse.papyrus.infra.core.resource.EMFLogicalModel;
import org.eclipse.papyrus.infra.core.resource.IModel;
import org.eclipse.papyrus.infra.core.resource.ResourceAdapter;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.internationalization.utils.PropertiesFilesUtils;

/**
 * The adjunct resource model snippet for the properties files.
 */
public class PropertiesAdjunctResourceModelSnippet extends AdjunctResourceModelSnippet {

	/**
	 * The internationalization model.
	 */
	private InternationalizationModelResource model;

	/**
	 * The adapter.
	 */
	private Adapter adapter;

	/**
	 * Constructor.
	 */
	public PropertiesAdjunctResourceModelSnippet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AdjunctResourceModelSnippet#loadResource(org.eclipse.papyrus.infra.core.resource.EMFLogicalModel,
	 *      org.eclipse.emf.ecore.resource.ResourceSet,
	 *      org.eclipse.emf.common.util.URI)
	 */
	@Override
	protected void loadResource(final EMFLogicalModel model, final ResourceSet resourceSet, final URI adjunctURI) {
		if (adjunctURI.fileExtension().equals(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION)) {
			model.loadModel(adjunctURI.trimFileExtension());
		} else {
			super.loadResource(model, resourceSet, adjunctURI);
		}
	}

	/**
	 * Redefined to manage the load of adjunct resource. {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AdjunctResourceModelSnippet#start(org.eclipse.papyrus.infra.core.resource.IModel)
	 */
	@Override
	public void start(final IModel startingModel) {
		if (startingModel instanceof InternationalizationModelResource) {
			model = (InternationalizationModelResource) startingModel;

			adapter = new ResourceAdapter() {
				@Override
				protected void handleResourceLoaded(Resource resource) {
					loadAdjunctResource(resource);
				}
			};

			model.getModelManager().eAdapters().add(adapter);
		}
	}

	/**
	 * Load the adjunct resource.
	 * 
	 * @param resource
	 *            The resource to load.
	 */
	protected void loadAdjunctResource(final Resource resource) {
		// If the parameter resource is the model's own kind of resource,
		// then there is nothing to do
		if ((null != model) && !model.isRelatedResource(resource)) {
			final URI adjunctURI = resource.getURI().trimFileExtension()
					.appendFileExtension(model.getModelFileExtension());
			final ResourceSet resourceSet = resource.getResourceSet();

			if (!model.isLoadedResourcesForURI(adjunctURI)) {
				loadResource(model, resourceSet, adjunctURI);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.papyrus.infra.core.resource.AdjunctResourceModelSnippet#dispose(org.eclipse.papyrus.infra.core.resource.IModel)
	 */
	@Override
	public void dispose(final IModel stoppingModel) {
		if ((stoppingModel == model) && (null != adapter)) {
			model.getModelManager().eAdapters().remove(adapter);
			adapter = null;
			model = null;
		}
	}
}
