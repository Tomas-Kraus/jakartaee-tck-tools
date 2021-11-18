<!--

    Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License v. 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0.

    This Source Code may also be made available under the following Secondary
    Licenses when the conditions for such availability set forth in the
    Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
    version 2 with the GNU Classpath Exception, which is available at
    https://www.gnu.org/software/classpath/license.html.

    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0

-->
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="/">
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

		<!-- defines page layout -->
		<fo:layout-master-set>
			<fo:simple-page-master master-name="cover-page"
									page-height="29.7cm" 
									page-width="21cm"
									margin-top="0cm" 
									margin-bottom="1.5cm" 
									margin-left="2cm" 
									margin-right="1.5cm">
				<fo:region-body margin-top="2.5cm"
								margin-bottom="1.5cm"
								/>
				<fo:region-before extent="1.5cm"/>
				<fo:region-after extent="1.5cm"/>
			</fo:simple-page-master>

			<fo:simple-page-master master-name="simple"
									page-height="29.7cm" 
									page-width="21cm"
									margin-top="0cm" 
									margin-bottom="1.5cm" 
									margin-left="2cm" 
									margin-right="1.5cm">
				<fo:region-body margin-top="2.5cm"
								margin-bottom="1.5cm"/>
				<fo:region-before extent="1.5cm"/>
				<fo:region-after extent="1.5cm"/>
			</fo:simple-page-master>
		</fo:layout-master-set>

		<fo:page-sequence master-name="simple">
			<fo:static-content flow-name="xsl-region-before">
				<fo:block font-size="10pt" 
							font-family="serif" 
							line-height="14pt" 
							space-before.optimum="25pt">
					JavaDoc - API documentaion
				</fo:block>
			</fo:static-content> 
			<fo:static-content flow-name="xsl-region-after">
				<fo:block text-align="center" 
							font-size="10pt" 
							font-family="serif" 
							line-height="14pt" >
					  <fo:page-number/>
				</fo:block>
			</fo:static-content> 
			<fo:flow flow-name="xsl-region-body">
	       	<fo:block font-size="18pt" 
    	            font-family="sans-serif" 
        	        line-height="24pt"
            	    space-after.optimum="15pt"
                	background-color="blue"
                	color="white"
                	text-align="center">
            	JavaDoc
         	</fo:block>

        	<!-- generates table of contents and puts it into a table -->

         	<fo:block font-size="14pt" 
            	      font-family="sans-serif" 
        	          line-height="18pt"
    	              space-after.optimum="10pt"
	                  font-weight="bold">
            	Packages in this document
         	</fo:block>

         	<fo:block font-size="12pt" 
            	      line-height="16pt"
                	  font-family="sans-serif">
           		<xsl:for-each select="//package">
                	<fo:block>  
                    	<fo:basic-link color="blue">
                        	<xsl:attribute name="internal-destination">
                           		<xsl:value-of select="translate(./@name,' ),-.(','____')"/>
                           	</xsl:attribute>
                          	<xsl:value-of select="@name"/>
                     	</fo:basic-link>
        			</fo:block>  
           		</xsl:for-each>
			</fo:block>
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates/> 
		</fo:flow>
		</fo:page-sequence>
	</fo:root>
</xsl:template>

<fo:page-sequence-master master-name="cover">
<fo:single-page-master-reference master-name="cover-page"/>
</fo:page-sequence-master>


<xsl:template match="title">
	<fo:page-sequence master-name="cover">
		<fo:flow flow-name="xsl-region-body" font-size="24pt" font-family="serif">
			<fo:block text-align="center">
				<xsl:apply-templates/>
			</fo:block>
		</fo:flow>
	</fo:page-sequence>
</xsl:template>

<xsl:template match="package">
  <fo:block font-size="18pt" 
            font-family="sans-serif" 
            line-height="24pt"
            break-before="page"
            space-after.optimum="15pt"
            background-color="blue"
            color="white"
            text-align="center">
    <xsl:attribute name="id">
      <xsl:value-of select="translate(./@name,' ),-.(','____')"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/>
  </fo:block>
  <xsl:apply-templates select="./classes"/>
</xsl:template>

<xsl:include href="Classes.xsl"/>

<xsl:include href="Interfaces.xsl"/>

<xsl:include href="Class.xsl"/>

</xsl:stylesheet>
