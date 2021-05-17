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

package org.eclipse.papyrus.infra.internationalization.utils;

import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.ModelUtils;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.internationalization.modelresource.InternationalizationPreferenceModel;

/**
 * Set of utility methods for the InternationalizationPreferenceModel.
 */
public class InternationalizationPreferenceModelUtils {

	/**
	 * Gets the InternationalizationPreferenceModel from the {@link ModelSet}.
	 * <br>
	 *
	 * @param modelsManager
	 *            The modelManager containing the requested model.
	 *
	 * @return The {@link InternationalizationPreferenceModel} registered in
	 *         modelManager, or null if not found.
	 */
	public static InternationalizationPreferenceModel getInternationalizationPreferenceModel(ModelSet modelsManager) {
		return (InternationalizationPreferenceModel) modelsManager
				.getModel(InternationalizationPreferenceModel.MODEL_ID);
	}

	/**
	 * Gets the InternationalizationPreferenceModel from the {@link ModelSet}.
	 * <br>
	 *
	 * @param ServicesRegistry
	 *            The servie registry under which the ModelSet is registered.
	 *
	 * @return The {@link InternationalizationPreferenceModel} registered in
	 *         modelManager, or null if not found.
	 */
	public static InternationalizationPreferenceModel getInternationalizationPreferenceModel(
			ServicesRegistry servicesRegistry) {
		try {
			return (InternationalizationPreferenceModel) ModelUtils.getModelSetChecked(servicesRegistry)
					.getModel(InternationalizationPreferenceModel.MODEL_ID);
		} catch (ServiceException e) {
			return null;
		}
	}
}
