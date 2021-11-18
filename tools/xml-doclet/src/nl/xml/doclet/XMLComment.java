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
 *  This class is used to extract the Comments for the different objects.
 *  A comment consists of 2 parts, a 'head' and a 'detail'.
 *
 * @author     Dick Knol
 * @created    11 mei 2001
 */
public class XMLComment
{

    Doc commentdoc;
    Element parentElement;


    public XMLComment(Doc commentdoc)
    {
	this.commentdoc = commentdoc;
    }


    public XMLComment(Doc commentdoc, Element parentElement)
    {
	this.commentdoc = commentdoc;
	this.parentElement = parentElement;
    }


    public Node toXML(Document doc)
    {
	Tag[] alltags = commentdoc.tags();
	String comment = commentdoc.commentText();

	Element commentElement = doc.createElement("comment");

	Tag firstTags[] = commentdoc.firstSentenceTags();
	if (firstTags.length > 0)
	    {
		Vector vector = new Vector();
		Element leadElement = doc.createElement("lead");
		for (int j = 0; j < firstTags.length; j++)
		    {
			Tag tag1 = firstTags[j];
			if (tag1.kind().equals("Text") || tag1.kind().equals("@code"))
			    {
				vector.addElement(tag1.text());
				Text leadText = doc.createTextNode(XMLUtil.removeTags(tag1.text()));
				leadElement.appendChild(leadText);
			    }
			else if (tag1 instanceof SeeTag)
			    {
				vector.addElement(tag1);
				XMLSeeTag seetag = new XMLSeeTag((SeeTag) tag1);
				commentElement.appendChild(seetag.toXML(doc));
				//processSeeTag ((SeeTag)tag1);
				leadElement.appendChild(doc.createTextNode(XMLUtil.removeTags(tag1.text())));
			    }
		    }
		commentElement.appendChild(leadElement);
		Element detailElement = null;
		Text detailText = null;
		Enumeration enumeration = vector.elements();
		Tag inlineTags[] = commentdoc.inlineTags();
		for (int k = 0; k < inlineTags.length; k++)
		    {
			Tag tag2 = inlineTags[k];
			Object obj = enumeration.hasMoreElements() ? enumeration.nextElement() : null;
			if (obj == null)
			    {
				if (detailElement == null)
				    {
					detailElement = doc.createElement("detail");
					commentElement.appendChild(detailElement);
				    }
			    }
			else
			    {
				if (obj instanceof String)
				    {
					String s2 = (String) obj;
					String s3 = tag2.text();
					if (s3.length() > s2.length())
					    {
						detailElement = doc.createElement("detail");
						detailText = doc.createTextNode(XMLUtil.removeTags(s3.substring(s2.length())));
						detailElement.appendChild(detailText);
					    }
				    }
				continue;
			    }
			if (tag2.kind().equals("Text") || tag2.kind().equals("@code"))
			    {
				detailText = doc.createTextNode(XMLUtil.removeTags(tag2.text()));
				detailElement.appendChild(detailText);
			    }
			else
			    if (tag2 instanceof SeeTag)
				{
				    XMLSeeTag seetag = new XMLSeeTag((SeeTag) tag2);
				    commentElement.appendChild(seetag.toXML(doc));
				    detailText = doc.createTextNode(XMLUtil.removeTags(tag2.text()));
				    detailElement.appendChild(detailText);
				}
				//processSeeTag (detailElement, (SeeTag)tag2);

		    }
		if (detailElement != null)
		    {
			commentElement.appendChild(detailElement);
		    }
		if (alltags.length > 0)
		    {
			for (int i = 0; i < alltags.length; i++)
			    {
				Tag tag = alltags[i];
				if (tag instanceof SeeTag)
				    {
					XMLSeeTag seetag = new XMLSeeTag((SeeTag) tag);
					commentElement.appendChild(seetag.toXML(doc));
					//	processSeeTag(descriptionElement, (SeeTag)tag);
				    }
				else
				    if (!(tag instanceof ParamTag) && !(tag instanceof ThrowsTag) && !tag.name().equals("@return"))
					{
					    try
						{
						    StringTokenizer stringtokenizer = new StringTokenizer(alltags[i].name(), "@,.:;");
						    String s1 = stringtokenizer.nextToken();
						    Element element4 = doc.createElement(s1);
						    if (alltags[i].text() != "")
							{
							    element4.appendChild(doc.createTextNode(alltags[i].text()));
							}
						    if (parentElement != null)
							{
							    parentElement.appendChild(element4);
							}
						    else
							{
							    commentElement.appendChild(element4);
							}
						}
					    catch (Throwable _ex)
						{
						    System.err.println("bad tag: " + alltags[i].toString());
						}
					}
			    }

		    }

	    }
	return commentElement;
    }
}

