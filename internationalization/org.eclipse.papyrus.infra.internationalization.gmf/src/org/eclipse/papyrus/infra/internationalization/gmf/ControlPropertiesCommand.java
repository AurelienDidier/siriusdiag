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
 *   Asma Smaoui (CEA) asma.smaoui@cea.fr - Bug 536172
 *
 *****************************************************************************/

package org.eclipse.papyrus.infra.internationalization.gmf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationModel;
import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationUtils;
//import org.eclipse.papyrus.infra.gmfdiag.common.model.NotationModel;
import org.eclipse.papyrus.infra.internationalization.InternationalizationEntry;
import org.eclipse.papyrus.infra.internationalization.InternationalizationFactory;
import org.eclipse.papyrus.infra.internationalization.InternationalizationLibrary;
import org.eclipse.papyrus.infra.internationalization.controlmode.utils.ControlPropertiesUtils;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationModelResource;
import org.eclipse.papyrus.infra.internationalization.utils.PropertiesFilesUtils;
import org.eclipse.papyrus.infra.nattable.model.nattable.Table;
//import org.eclipse.papyrus.infra.nattable.model.nattable.Table;
import org.eclipse.papyrus.infra.services.controlmode.ControlModeRequest;
import org.eclipse.papyrus.infra.services.controlmode.commands.AbstractControlCommand;

/**
 * The control command for the properties files.
 */
public class ControlPropertiesCommand extends AbstractControlCommand {

	/**
	 * The Constant COMMAND_TITLE.
	 */
	private static final String COMMAND_TITLE = "Properties control command"; //$NON-NLS-1$

	/**
	 * The old internationalization model resource.
	 */
	private InternationalizationModelResource internationalizationModelResource = null;

	/**
	 * The old notation resource.
	 */
	private Resource oldNotationResource = null;

	/**
	 * Constructor.
	 *
	 * @param request
	 *            The request.
	 */
	public ControlPropertiesCommand(final ControlModeRequest request) {
		this(COMMAND_TITLE, request);
	}

	/**
	 * Constructor.
	 *
	 * @param commandTitle
	 *            The command title.
	 * @param request
	 *            The request.
	 */
	public ControlPropertiesCommand(final String commandTitle, final ControlModeRequest request) {
		super(commandTitle, null, request);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {

		final Set<Resource> createdResources = new HashSet<>();

		@SuppressWarnings("unchecked")
		final Map<Locale, Resource> createdPropertiesResources = (Map<Locale, Resource>) getRequest()
				.getParameter(ControlPropertiesUtils.CREATED_PROPERTIES_RESOURCES);
		for (final Locale locale : createdPropertiesResources.keySet()) {
			// Get the new properties resource for the locale
			final Resource newPropertiesResource = createdPropertiesResources.get(locale);
			if (null == newPropertiesResource) {
				return CommandResult.newErrorCommandResult("The properties model has not been created"); //$NON-NLS-1$
			}

			InternationalizationLibrary library = null;
			for (final EObject objectContant : newPropertiesResource.getContents()) {
				if (objectContant instanceof InternationalizationLibrary) {
					library = (InternationalizationLibrary) objectContant;
				}
			}

			if (null == library) {
				// The library does not exist, create it and add entries
				library = InternationalizationFactory.eINSTANCE.createInternationalizationLibrary();
				newPropertiesResource.getContents().add(library);
			}

			final List<InternationalizationEntry> entries = getInternationalizationEntries(locale);
			if (!entries.isEmpty()) {

				// The library exists, just add entries
				for (final InternationalizationEntry entry : entries) {
					library.getEntries().add(EcoreUtil.copy(entry));
				}

				final Resource oldPropertiesResource = getOldPropertiesResource(locale);

				if (oldPropertiesResource != null && !oldPropertiesResource.getContents().isEmpty()) {
					final InternationalizationLibrary oldLibrary = (InternationalizationLibrary) oldPropertiesResource
							.getContents().get(0);

					oldLibrary.getEntries().removeAll(entries);
				}
			}

			createdResources.add(newPropertiesResource);
		}

		return CommandResult.newOKCommandResult(createdResources);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doRedo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doRedo(final IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return doExecuteWithResult(monitor, info).getStatus();
	}

	/**
	 * Get the internationalization entries for the diagrams and the tables of
	 * objects in sub model.
	 *
	 * @param locale
	 *            The current locale to manage.
	 * @return The internationalization entries.
	 * @throws ExecutionException
	 *             The possible execution exception.
	 */
	protected List<InternationalizationEntry> getInternationalizationEntries(final Locale locale)
			throws ExecutionException {
		final List<InternationalizationEntry> entries = new ArrayList<>();

		getInternationalizationModelResource();

		if (null != internationalizationModelResource) {

			// Search the EObject labels to control
			final EObject targetObject = getRequest().getTargetObject();

			InternationalizationEntry entryForKey = internationalizationModelResource
					.getEntryForKey(getOldPropertiesURI(), targetObject, locale);
			if (null != entryForKey) {
				entries.add(entryForKey);
			}

			final TreeIterator<EObject> eAllContents = EcoreUtil.getAllProperContents(targetObject, true);
			while (eAllContents.hasNext()) {
				final EObject content = eAllContents.next();
				entryForKey = internationalizationModelResource.getEntryForKey(getOldPropertiesURI(), content, locale);
				if (null != entryForKey) {
					entries.add(entryForKey);
				}
			}

			final Resource oldNotationResource = getOldNotationResource();

			// Search the diagrams labels to control
			for (final Diagram diagram : NotationUtils.getDiagrams(oldNotationResource,
					getRequest().getTargetObject())) {
				entryForKey = internationalizationModelResource.getEntryForKey(getOldPropertiesURI(), diagram, locale);
				if (null != entryForKey) {
					entries.add(entryForKey);
				}
			}

			// Search the tables labels to control
			for (final Table table : getTables(oldNotationResource, getRequest().getTargetObject())) {
				entryForKey = internationalizationModelResource.getEntryForKey(getOldPropertiesURI(), table, locale);
				if (null != entryForKey) {
					entries.add(entryForKey);
				}
			}
		}

		return entries;
	}

	/**
	 * Gets the all the tables contained in the specified ancestor eObject.
	 *
	 * @param notationResource
	 *            The notation resource where search tables.
	 * @param eObject
	 *            The table to search in notation resource.
	 *
	 * @return all the contained tables.
	 */
	protected List<Table> getTables(final Resource notationResource, final EObject eObject) {
		final List<Table> tables = new LinkedList<>();
		if (null != notationResource) {
			for (final EObject obj : notationResource.getContents()) {
				if (obj instanceof Table) {
					Table table = (Table) obj;
					if (EcoreUtil.isAncestor(eObject, table.getOwner())) {
						tables.add(table);
					}
				}
			}
		}
		return tables;
	}

	/**
	 * Gets the old internationalization model resource.
	 *
	 * @return the old internationalization model resource.
	 */
	protected InternationalizationModelResource getInternationalizationModelResource() {

		if (null == internationalizationModelResource) {
			try {
				internationalizationModelResource = (InternationalizationModelResource) getRequest().getModelSet()
						.getModelChecked(InternationalizationModelResource.MODEL_ID);
			} catch (Exception e) {
				Activator.log.error("Unable to retrieve old properties resource", e); //$NON-NLS-1$
			}
		}

		return internationalizationModelResource;
	}

	/**
	 * Gets the old properties resource.
	 *
	 * @param locale
	 *            The current locale to manage.
	 * @return the old properties resource.
	 */
	private Resource getOldPropertiesResource(final Locale locale) {
		Resource resource = null;

		getInternationalizationModelResource();

		try {
			resource = internationalizationModelResource.getResourceForURIAndLocale(getOldPropertiesURI(), locale);
		} catch (Exception e) {
			Activator.log.error("Unable to retrieve old notation resource", e); //$NON-NLS-1$
		}

		return resource;
	}

	/**
	 * Get the old properties URI for request.
	 *
	 * @return The old properties resource URI.
	 * @throws ExecutionException
	 *             The exception when the notation resource is not found.
	 */
	protected URI getOldPropertiesURI() throws ExecutionException {
		URI uri = getRequest().getSourceURI();
		if (null != uri) {
			return uri.trimFileExtension().appendFileExtension(PropertiesFilesUtils.PROPERTIES_FILE_EXTENSION);
		}
		throw new ExecutionException("Unable to retreive URI of the old properties model"); //$NON-NLS-1$
	}

	/**
	 * Gets the old notation resource.
	 *
	 * @return the old notation resource
	 */
	private Resource getOldNotationResource() {

		if (null == oldNotationResource) {
			try {
				oldNotationResource = getRequest().getModelSet().getResource(getOldNotationURI(), true);
			} catch (Exception e) {
				Activator.log.error("Unable to retrieve old notation resource", e); //$NON-NLS-1$
			}
		}

		return oldNotationResource;
	}

	/**
	 * Get the old notation URI for request.
	 *
	 * @return The old notation resource URI.
	 * @throws ExecutionException
	 *             The exception when the notation resource is not found.
	 */
	protected URI getOldNotationURI() throws ExecutionException {
		URI uri = getRequest().getSourceURI();
		if (null != uri) {
			return uri.trimFileExtension().appendFileExtension(NotationModel.NOTATION_FILE_EXTENSION);
		}
		throw new ExecutionException("Unable to retreive URI of the old notation model"); //$NON-NLS-1$
	}
}
