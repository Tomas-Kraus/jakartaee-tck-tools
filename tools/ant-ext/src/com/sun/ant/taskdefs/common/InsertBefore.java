/*
 * Copyright (c) 2002, 2018 Oracle and/or its affiliates. All rights reserved.
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


package com.sun.ant.taskdefs.common;

import  org.apache.tools.ant.*;
import java.io.*;

public class InsertBefore extends Task{

    private String srcFile = null;       
    private String destFile = null;     
    private String searchString = null;      
    private String insertString = null;     
    private String insertFile = null;     
    private String occurrence = "all";     
    private String index = "0";     

    /**
     * Set the source file
     *
     * @param srcFile file
     */
    public void setSrcFile(String s){
        this.srcFile = s;
    }


    /**
     * Set the destination file 
     *
     * @param destFile file
     */
    public void setDestFile(String s){
        this.destFile = s;
    }

    /**
     * Set the search string
     *
     * @param searchString
     */
    public void setSearchString(String s){
        this.searchString = s;
    }

    /**
     * Set the insert string
     *
     * @param insertString
     */
    public void setInsertString(String s){
        this.insertString = s;
    }

    /**
     * Set the insert file
     *
     * @param insertFile
     */
    public void setInsertFile(String s){
        this.insertFile = s;
    }

    /**
     * Set the occurrence string
     * all - all occurrences
     * specific - a specific occurrence
     * after - all occurrences starting at a specific occurrence
     *
     * @param occurrence
     */
    public void setOccurrence(String s){
        this.occurrence = s;
    }

    /**
     * Set the occurrence index
     * 
     * @param index
     */
    public void setIndex(String s){
        this.index = s;
    }

    public void execute() throws BuildException {

		BufferedReader in = null;
		PrintWriter out = null;

        if (srcFile == null){
            throw new BuildException("The src attribute must be set.");
        }
        if (destFile == null){
            throw new BuildException("The dest attribute must be set.");
        }
        if (srcFile.equals(destFile)){
            throw new BuildException("file " + srcFile + " would overwrite its self");
        }
        if (searchString == null){
            throw new BuildException("a searchString must be specified");
        }
        if ((insertString == null) && (insertFile == null)){
            throw new BuildException("a insertString or insertFile attribute must be specified");
        }
        if ((insertString != null) && (insertFile != null)){
            throw new BuildException("only one attribute (insertString or insertFile) can be specified");
        }

        if (occurrence.equals("specific") || occurrence.equals("after")) {
			if (index.equals("0")) {
            		throw new BuildException("an index must be specified if an occurrence other than all is specified");
			}
        } else if (!occurrence.equals("all")) {
            	throw new BuildException("must specify all, specific, or after for the occurrence attribute");
	   }

		StringBuffer sb = null;
		// read in file contents
		if (insertFile != null) {
			sb = new StringBuffer();
			try {
        			in = new BufferedReader(new FileReader(insertFile));
			} catch (Exception e) {
				throw new BuildException(e);
			}
	
			String text = "";
			while (text!= null){
				try {
					text=in.readLine();
				} catch (IOException ioe) {
					throw new BuildException(ioe);
				}
				if (text != null) {
					sb.append(text).append("\n");
				}
			}
			try {
				in.close();
			} catch (Exception e) {
				throw new BuildException(e);
			}
		}

		try {
        		in = new BufferedReader(new FileReader(srcFile));
        		out = new PrintWriter(new BufferedWriter(new FileWriter(destFile)));
		} catch (Exception e) {
			throw new BuildException(e);
		}

		String text = "";
		int count=0;
		while (text!= null){
			try {
				text=in.readLine();
			} catch (IOException ioe) {
				throw new BuildException(ioe);
			}
			if (text != null) {
				if (text.indexOf(searchString)!= -1){
					count++;
					//found it, now insert text
					if ((occurrence.equals("all")) ||
					   ((occurrence.equals("specific") && (count == Integer.parseInt(index)))) ||
					   ((occurrence.equals("after") && (count > Integer.parseInt(index))))) {
						if (insertString != null) {
							out.println(insertString);	
						} else {
							out.println(sb.toString());	
						}
					}
				}
				out.println(text);	
			}
		}
		try {
			in.close();
			out.close();
		} catch (Exception e) {
			throw new BuildException(e);
		}

    }

}
