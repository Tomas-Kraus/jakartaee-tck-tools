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
import java.util.*;

/**
 * This class is used to parse the Constructors.
 * 
 * @author Jonathan van Alteren
 * @created 24 april 2001
 */
public class XMLConstructor {

	ConstructorDoc constructordoc;

	private boolean exceptionProcessed(String name, List list) {
		boolean result = false;
		int numExceptions = list.size();
		for (int i = 0; i < numExceptions; i++) {
			String exceptionName = (String) (list.get(i));
			if (exceptionName.endsWith(name)) {
				result = true;
				// System.out.println("\t$$$ Exception Already Processed - Looking for \""
				// +
				// name + " Found \"" + exceptionName + "\"");
				break;
			}
		}
		if (!result) {
			// System.out.println("\t$$$ Exception NOT Processed - Was looking for \""
			// +
			// name + "\"");
		}
		return result;
	}

	/*
	 * Return the throws comment from the javadoc if it exists. The comment has
	 * to match the exception name, either the class name of the exception or
	 * the fully qualified exception name of the exception.
	 */
	private String extractTagComment(Tag[] tags, ClassDoc classDoc) {
		String result = "";
		if (tags == null || tags.length == 0 || classDoc == null) {
			return result;
		}
		// reflected exception names
		String exceptionName = classDoc.typeName();
		String qualifiedExceptionName = classDoc.qualifiedTypeName();
		for (int i = 0; i < tags.length; i++) {
			if (tags[i] instanceof ThrowsTag) {
				ThrowsTag throwsTag = (ThrowsTag) tags[i];
				String tagExceptionName = throwsTag.exceptionName();
				if (tagExceptionName.equalsIgnoreCase(exceptionName)
						|| tagExceptionName
								.equalsIgnoreCase(qualifiedExceptionName)) {
					result = throwsTag.exceptionComment();
					break;
				}
			}
		}
		return XMLUtil.removeTags(result);
	}

	public XMLConstructor(ConstructorDoc constructordoc) {
		this.constructordoc = constructordoc;
	}

	public Node toXML(Document doc) {
		return toXML(doc, null);
	}

	public Node toXML(Document doc, String parentName) {
		Element constructorElement = doc.createElement("constructor");
		if (parentName != null) {
			constructorElement.setAttribute("inherited.from", parentName);
		}

		String modifiers = constructordoc.modifiers();
		if (modifiers.length() > 0) {
			constructorElement.setAttribute("modifiers", modifiers);
		}

		// comment
		XMLComment comment = new XMLComment(constructordoc, constructorElement);
		constructorElement.appendChild(comment.toXML(doc));

		// deprecation
		Tag[] deprecationTags = constructordoc.tags("deprecated");
		if (deprecationTags.length > 0) {
			Element deprecation = doc.createElement("deprecated");
			deprecation.appendChild(doc.createTextNode("true"));
			constructorElement.appendChild(deprecation);
		}

		// parameters
		XMLParameters parameters = new XMLParameters(
				constructordoc.parameters(), constructordoc.paramTags());
		Element parametersElement = (Element) parameters.toXML(doc);
		if (parametersElement != null) {
			constructorElement.appendChild(parametersElement);
		}

		// Exceptions
		////
		// This feature is turned off by default since this is how the tool has
		// been since CTS 1.4.  If users want to generate assertions for each
		// exception thrown by a c'tor,they can pass the ant prop:
		//   include.ctor.exceptions with a value true.  
		//
		// If users also want doc'ed exceptions that are not declared by a
		// throws clause in the c'tor declaration, they'll also need to pass
		// the ant prop:
		//   include.comment.exceptions with a value of true.
		////
		
		String includeCtorExceptions = System.getProperty(
				"include.ctor.exceptions", "false");
		if (includeCtorExceptions.equalsIgnoreCase("true")) {

			ClassDoc[] throwsDeclared = constructordoc.thrownExceptions();
			Tag[] tagComments = constructordoc.tags();

			Element throwsElement = null;
			List processedExceptions = new ArrayList();
			int numTags = (throwsDeclared == null) ? 0 : throwsDeclared.length;
			if (numTags > 0) {
				throwsElement = doc.createElement("throws");
				for (int i = 0; i < numTags; i++) {
					Element throwElement = doc.createElement("throw");
					throwElement.setAttribute("name",
							throwsDeclared[i].qualifiedTypeName());
					throwElement.appendChild(doc
							.createTextNode(extractTagComment(tagComments,
									throwsDeclared[i])));
					throwsElement.appendChild(throwElement);
					processedExceptions.add(throwsDeclared[i]
							.qualifiedTypeName());
				}
				constructorElement.appendChild(throwsElement);
			}

			String addCommentExceptions = System.getProperty(
					"include.comment.exceptions", "false");
			if (addCommentExceptions.equalsIgnoreCase("true")) {
				ThrowsTag[] throwsTagsC = constructordoc.throwsTags();
				numTags = (throwsTagsC == null) ? 0 : throwsTagsC.length;
				if (numTags > 0) {
					if (throwsElement == null) {
						throwsElement = doc.createElement("throws");
						constructorElement.appendChild(throwsElement);
					}
					for (int i = 0; i < numTags; i++) {
						String exceptionName = (throwsTagsC[i].exception() == null) ? throwsTagsC[i]
								.exceptionName() : throwsTagsC[i].exception()
								.qualifiedName();
						if (!exceptionProcessed(exceptionName,
								processedExceptions)) {
							Element throwElement = doc.createElement("throw");
							throwElement.setAttribute("name", exceptionName);

							String removedTagsComment = XMLUtil
									.removeTags(throwsTagsC[i]
											.exceptionComment());
							throwElement.appendChild(doc
									.createTextNode(removedTagsComment));
							throwsElement.appendChild(throwElement);
						} // end if
					} // end for
				} // end if
			} // end if
		}

		return constructorElement;
	}
}
