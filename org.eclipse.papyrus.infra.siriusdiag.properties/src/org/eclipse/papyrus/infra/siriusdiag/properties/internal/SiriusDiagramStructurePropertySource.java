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

package org.eclipse.papyrus.infra.siriusdiag.properties.internal;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * The {@link PropertySource} used to provide a better edition way for some properties of the DocumentStructureTemplate metamodel
 */
public class SiriusDiagramStructurePropertySource extends PropertySource {

	/**
	 * Constructor.
	 *
	 * @param object
	 * @param itemPropertySource
	 */
	public SiriusDiagramStructurePropertySource(final Object object, final IItemPropertySource itemPropertySource) {
		super(object, itemPropertySource);
	}

	/**
	 * @see org.eclipse.emf.edit.ui.provider.PropertySource#createPropertyDescriptor(org.eclipse.emf.edit.provider.IItemPropertyDescriptor)
	 *
	 * @param itemPropertyDescriptor
	 * @return
	 */
	@Override
	protected IPropertyDescriptor createPropertyDescriptor(final IItemPropertyDescriptor itemPropertyDescriptor) {
		final EStructuralFeature f = (EStructuralFeature) itemPropertyDescriptor.getFeature(this.object);
		// TODO if (f == DiagramPackage.eINSTANCE.getEReferenceTemplate_EReference()) {
		// return new CustomPropertyDescriptor(this.object, itemPropertyDescriptor, new EReferenceTemplateEReferenceEditorFactory());
		// }
		// if (f == DiagramPackage.eINSTANCE.getEClassTemplate_EClass()) {
		// return new CustomPropertyDescriptor(this.object, itemPropertyDescriptor, new EClassTemplateEClassEditorFactory());
		// }
		// if (f == DiagramPackage.eINSTANCE.getEStructuralFeatureTemplate_Feature()) {
		// return new CustomPropertyDescriptor(this.object, itemPropertyDescriptor, new EStructuralFeatureTemplateEStructuralFeatureEditorFactory());
		// }
		// if (f == DiagramPackage.eINSTANCE.getEAttributeTemplate_EAttribute()) {
		// return new CustomPropertyDescriptor(this.object, itemPropertyDescriptor, new EAttributeTemplateEAttributeEditorFactory());
		// }
		return super.createPropertyDescriptor(itemPropertyDescriptor);
	}
}
