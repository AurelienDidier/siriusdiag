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
package org.eclipse.papyrus.infra.siriusdiag.internationalization.utils;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.papyrus.infra.internationalization.common.utils.InternationalizationPreferencesUtils;
import org.eclipse.sirius.diagram.DSemanticDiagram;


/**
 * Open the label internationalization preferences to the others plugins with the utils plugin.
 */
public class LabelInternationalizationPreferencesUtils {

	/**
	 * This allows to modify the internationalization preference value.
	 * 
	 * @param eObject The {@link EObject) corresponding (to get its resource).
	 * @param value The new preference value.
	 */
	public static void setInternationalizationPreference(final EObject eObject, final boolean value){
		if(eObject instanceof DSemanticDiagram){
			LabelInternationalization.getInstance().setInternationalizationPreference((DSemanticDiagram)eObject, value);
		}else{
			InternationalizationPreferencesUtils.setInternationalizationPreference(eObject, value);
		}
	}
	
	/**
	 * This allows to modify the internationalization preference value.
	 * 
	 * @param resource The {@link Resource) to get the papyrus project preferences.
	 * @param value The new preference value.
	 */
	public static void setInternationalizationPreference(final Resource resource, final boolean value){
		InternationalizationPreferencesUtils.setInternationalizationPreference(resource, value);
	}
	
	/**
	 * This allows to get the internationalization preference value.
	 * 
	 * @param eObject The {@link EObject) corresponding (to get its resource).
	 * @return <code>true</code> if the preference value is set to true, <code>false</code> otherwise.
	 */
	public static boolean getInternationalizationPreference(final EObject eObject){
		boolean result = false;
		if(eObject instanceof DSemanticDiagram){
			result = LabelInternationalization.getInstance().getInternationalizationPreference((DSemanticDiagram)eObject);
		}else{
			result = InternationalizationPreferencesUtils.getInternationalizationPreference(eObject);
		}
		return result;
	}
	
	/**
	 * This allows to get the internationalization preference value.
	 * 
	 * @param resource The {@link Resource) to get the papyrus project preferences.
	 * @return <code>true</code> if the preference value is set to true, <code>false</code> otherwise.
	 */
	public static boolean getInternationalizationPreference(final Resource resource){
		return InternationalizationPreferencesUtils.getInternationalizationPreference(resource);
	}
}
