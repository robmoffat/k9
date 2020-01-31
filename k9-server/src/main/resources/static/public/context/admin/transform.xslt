<xsl:stylesheet xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.kite9.org/schema/adl" xmlns:adl="http://www.kite9.org/schema/adl">
    <xsl:template match="/">
        <svg:svg zoomAndPan="magnify" preserveAspectRatio="xMidYMid" >
          <svg:defs>
            <svg:style type="text/css"> @import url("/public/context/admin/admin.css");</svg:style>
          </svg:defs>
          <diagram>
            <xsl:apply-templates />
          </diagram>
        </svg:svg>
    </xsl:template>

  <xsl:template name="entity">
   	<xsl:param name="id"/>
   	<xsl:param name="focus" />
   	<xsl:variable name="open">
   		<xsl:if test="@type = 'document'">
   			open
   		</xsl:if>
   	</xsl:variable>
    <xsl:element name="{@type}">
      <xsl:variable name="rel"><xsl:value-of select="../adl:rel" /></xsl:variable>
      <xsl:attribute name="entity">true</xsl:attribute>
      <xsl:attribute name="k9-ui"><xsl:value-of select="@commands" /> <xsl:value-of select="$focus" /> <xsl:value-of select="$open" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
      <xsl:attribute name="subject-uri"><xsl:value-of select="$id" /></xsl:attribute>
      <xsl:if test="@type = 'document'">
      	<xsl:attribute name="href"><xsl:value-of select="$id" />/content</xsl:attribute>
      </xsl:if>
      <xsl:copy-of select="adl:title" />
      <xsl:copy-of select="adl:icon" />
      <xsl:copy-of select="adl:description" />
      <lastUpdated>
        <xsl:value-of select="translate(adl:lastUpdated, 'T.', '  ')" />
      </lastUpdated>
    </xsl:element>  
  </xsl:template>
  
  <xsl:template name="user-page">
  	<container class="main">
  		<container class="left" id="userbox">
		    <xsl:call-template name="entity">
		    	<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>
		        <xsl:with-param name="focus"> auth</xsl:with-param>
		    </xsl:call-template> 		
  		</container>
  		<container class="right list" id="projectbox" k9-ui="NewProject">
  			<xsl:attribute name="subject-uri"><xsl:value-of select="@localId" /></xsl:attribute>
  			
  			<xsl:for-each select="//*[@type='member']">
  				<member>
  					<xsl:attribute name="k9-ui">focus</xsl:attribute>
  					<xsl:attribute name="id"><xsl:value-of select="adl:parent/@localId" /></xsl:attribute>
  					<xsl:copy-of select="adl:projectRole" />
				    <xsl:copy-of select="adl:parent/adl:icon" />
				    <xsl:copy-of select="adl:parent/adl:title" />
  					<xsl:copy-of select="adl:parent/adl:description" />
  				</member>
  			</xsl:for-each>
  			<label>My Projects</label>
  		</container>
  	</container>
  </xsl:template>
  
  <xsl:template name="project-page">
  	<container class="main">
  		<container class="left" id="projectbox">
		    <xsl:call-template name="entity">
		    	<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>
		        <xsl:with-param name="focus"></xsl:with-param>
		    </xsl:call-template>
		    
		    <container class="lower list" id="projectlist">
			    <xsl:for-each select="./adl:content/adl:value[@type='member']">
	  				<pmember>
	  					<xsl:attribute name="id"><xsl:value-of select="@localId" /></xsl:attribute>
	  					<xsl:copy-of select="adl:projectRole" />
					    <xsl:copy-of select="adl:icon" />
					    <xsl:copy-of select="adl:title" />
	  				</pmember>
	  			</xsl:for-each>
	  			<label>Members</label>
		    </container>
     	</container>
  		<container class="right grid" id="documentbox" k9-ui="NewDocument">
  			<xsl:attribute name="subject-uri"><xsl:value-of select="@localId" /></xsl:attribute>
		    <xsl:for-each select="./adl:content/adl:value[@type='document']">
		    	<xsl:call-template name="entity">
		    		<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>
		       		<xsl:with-param name="focus">focus</xsl:with-param>
		    	</xsl:call-template>
		    </xsl:for-each>
		    <label>Documents in <xsl:value-of select="adl:title" /></label>
  		</container>
  	</container>
  </xsl:template>
  
  <xsl:template name="document-page">
  	<container class="main">
  		<container class="left" id="projectbox">
		    <xsl:apply-templates select="./adl:content/adl:value[@type='project']" />
		</container>
  		<container class="left" id="documentbox">
		    <xsl:call-template name="entity">
		    	<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>
		        <xsl:with-param name="focus"></xsl:with-param>
		    </xsl:call-template>
		</container>
  		<container id="revisionbox">
  			<xsl:variable name="count"><xsl:value-of select="count(./adl:content/adl:value[@type='revision'])" /></xsl:variable>
   			<xsl:attribute name="class">right list <xsl:if test="$count > 10">more</xsl:if></xsl:attribute>
	  		<xsl:for-each select="./adl:content/adl:value[@type='revision' and count(preceding::adl:value) &lt; 11]">
  				<member>
  					<xsl:attribute name="k9-ui"></xsl:attribute>
  					<xsl:attribute name="id"><xsl:value-of select="@localId" /></xsl:attribute>
				    <xsl:copy-of select="adl:icon" />
				    <xsl:copy-of select="adl:title" />
				    <xsl:copy-of select="adl:lastUpdated" />
  				</member>
  			</xsl:for-each>
  			<xsl:choose>
  				<xsl:when test="$count > 10">
  					<label>Revisions of <xsl:value-of select="adl:title" /> (Latest 10 / <xsl:value-of select="$count" />)</label>
  				</xsl:when>	 
  				<xsl:otherwise>
  					<label>Revisions of <xsl:value-of select="adl:title" /></label>
  				</xsl:otherwise>
  			</xsl:choose>
	  	</container>
	</container>	  	
  </xsl:template>
  
  <xsl:template match="/adl:entity[@type='user']">
  	<xsl:call-template name="user-page" />
  </xsl:template>
  
  <xsl:template match="/adl:entity[@type='project']">
  	<xsl:call-template name="project-page" />
  </xsl:template>
  
  <xsl:template match="/adl:entity[@type='document']">
  	<xsl:call-template name="document-page" />
  </xsl:template>
  
  <xsl:template match="/adl:entity[not(@type)]">
  	<xsl:for-each select="adl:content">
  		<xsl:choose>
  			<xsl:when test="@type='user'">
	  			<xsl:call-template name="user-page" />
  			</xsl:when>
  			<xsl:otherwise>
		  	 	<xsl:call-template name="entity">
			    	<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>
			        <xsl:with-param name="focus"> focus</xsl:with-param>
			    </xsl:call-template>
  			</xsl:otherwise>
  		</xsl:choose>
  	</xsl:for-each>
  </xsl:template>

   
  <xsl:template match="*">
  </xsl:template>
</xsl:stylesheet>