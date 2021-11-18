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

import java.io.*;
import java.util.*;
import com.sun.javadoc.*;
import org.w3c.dom.*;

/**
 *
 * @author     Dick Knol
 * @created    11 mei 2001
 * @version    0.1
 */
public class XMLParameters
{

    Parameter[] parameters;
    ParamTag[] paramtags;


    public XMLParameters(Parameter[] parameters, ParamTag[] paramtags)
    {
	this.parameters = parameters;
	this.paramtags = paramtags;
    }

    public Node toXML(Document doc)
    {
	Element parametersElement = doc.createElement("parameters");
	for (int i = 0; i < parameters.length; i++)
	    {
		Element parameterElement = doc.createElement("parameter");
		parameterElement.setAttribute("name", parameters[i].name());
		parameterElement.setAttribute("type", parameters[i].type().typeName());

		String packagename = XMLUtil.packagename(parameters[i].type());
		if (packagename.length() > 0)
		    {
			parameterElement.setAttribute("package", packagename);
		    }

		String dimension = parameters[i].type().dimension();
		if (dimension.length() > 0)
		    {
			parameterElement.setAttribute("dimension", dimension);
		    }

		// comment
		for (int k = 0; k < paramtags.length; k++)
		    {

			if (paramtags[k].parameterName().equals(parameters[i].name()))
			    {
				Element commentElement = doc.createElement("comment");
				Text commentText = doc.createTextNode(XMLUtil.removeTags(paramtags[k].parameterComment()));
				commentElement.appendChild(commentText);
				parameterElement.appendChild(commentElement);
			    }
		    }
		parametersElement.appendChild(parameterElement);
	    }
	if (parametersElement.getFirstChild() == null)
	    {
		return null;
	    }
	else
	    {
		return parametersElement;
	    }
    }
}


