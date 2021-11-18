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
public class XMLSeeTag
{

    private SeeTag seetag;


    public XMLSeeTag(SeeTag seetag)
    {
	this.seetag = seetag;
    }


    public Node toXML(Document doc)
    {
	Element seeElement = doc.createElement("see");
	String membername = seetag.referencedMemberName();
	if (seetag.referencedClass() != null)
	    {
		String classname = XMLUtil.className((ProgramElementDoc) seetag.referencedClass());
		seeElement.setAttribute("class", classname);

		String packagename = XMLUtil.packagename((ProgramElementDoc) seetag.referencedClass());
		seeElement.setAttribute("package", packagename);
	    }

	if (membername != null)
	    {
		seeElement.setAttribute("member", membername);
	    }
	//                String packagename = XMLUtil.packagename ();

	return seeElement;
    }
}
