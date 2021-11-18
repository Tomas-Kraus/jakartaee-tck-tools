/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package nl.xml.doclet;

import com.sun.javadoc.*;
import org.w3c.dom.*;

/**
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLField
{

    FieldDoc fielddoc;


    public XMLField(FieldDoc fielddoc)
    {
	this.fielddoc = fielddoc;
    }


    public Node toXML(Document doc, String parentName) {
	Element fieldElement = doc.createElement("field");

	fieldElement.setAttribute("type", fielddoc.type().typeName());
	fieldElement.setAttribute("name", fielddoc.name());
	if (parentName != null) {
	    fieldElement.setAttribute("inherited.from", parentName);
	}

	String modifiers = fielddoc.modifiers();
	if (modifiers.length() > 0)
	    {
		fieldElement.setAttribute("modifiers", modifiers);
	    }

	String packagename = XMLUtil.packagename(fielddoc.type());
	if (packagename.length() > 0)
	    {
		fieldElement.setAttribute("package", packagename);
	    }

	String dimension = fielddoc.type().dimension();
	if (dimension.length() > 0)
	    {
		fieldElement.setAttribute("dimension", dimension.trim());
	    }
	// comment
	XMLComment comment = new XMLComment(fielddoc, fieldElement);
	fieldElement.appendChild(comment.toXML(doc));

	return fieldElement;
    }

    public Node toXML(Document doc)
    {
	return toXML(doc, null);
    }
}

