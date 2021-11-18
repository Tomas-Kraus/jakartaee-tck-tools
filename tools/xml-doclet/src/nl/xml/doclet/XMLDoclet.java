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

/**
 *  Description of these Class
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLDoclet
{

    private String filename;


    private XMLDoclet(RootDoc rootdoc)
    {
	parseOptions(rootdoc.options());
	long start = System.currentTimeMillis();

	XMLDocument xml = new XMLDocument(rootdoc.specifiedPackages());
	xml.toFile(filename);

	System.out.println(XMLUtil.PREFIX + "ran " + (System.currentTimeMillis() - start) + " ms.");
    }


    private void parseOptions(String[][] options)
    {
	for (int i = 0; i < options.length; i++)
	    {
		String[] option = options[i];
		if (option[0].equals("-file"))
		    {
			filename = option[1];
		    }
	    }
    }


    public static boolean start(RootDoc rootdoc)
    {
	XMLDoclet doclet = new XMLDoclet(rootdoc);
	return true;
    }


    public static boolean validOptions(String[][] options, DocErrorReporter reporter)
    {
	boolean foundFileOption = false;
	for (int i = 0; i < options.length; i++)
	    {
		String[] option = options[i];
		if (option[0].equals("-file"))
		    {
			if (foundFileOption)
			    {
				reporter.printError(XMLUtil.PREFIX + "Only one '-file' option allowed.");
				return false;
			    }
			else
			    {
				foundFileOption = true;
			    }
		    }
	    }
	if (!foundFileOption)
	    {
		reporter.printError("Please specify option(s):");
		reporter.printError("\t-file [XML output filename]");
	    }
	return foundFileOption;
    }


    public static int optionLength(String option)
    {
	if (option.equals("-file"))
	    {
		return 2;
	    }
	else
	    {
		return 0;
	    }
    }
}

