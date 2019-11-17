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
    
  <xsl:template name="rel-id">
  	<xsl:variable name="rel"><xsl:value-of select="../adl:rel" /></xsl:variable>
    <xsl:value-of select="substring-before(/adl:entity/adl:links[@rel=$rel]/@href, '{')" />
  </xsl:template>

  <xsl:template name="entity">
   	<xsl:param name="id"/>
    <xsl:element name="{@type}">
      <xsl:variable name="rel"><xsl:value-of select="../adl:rel" /></xsl:variable>
      <xsl:attribute name="entity">true</xsl:attribute>
      <xsl:attribute name="k9-ui"><xsl:value-of select="@commands" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
      <xsl:copy-of select="adl:title" />
      <xsl:copy-of select="adl:icon" />
      <xsl:copy-of select="adl:description" />
      <lastUpdated>
        <xsl:value-of select="translate(adl:lastUpdated, 'T.', '  ')" />
      </lastUpdated>
    </xsl:element>  
  </xsl:template>
  
  <xsl:template match="adl:entity[@type]">
    <xsl:call-template name="entity">
    	<xsl:with-param name="id"><xsl:value-of select="/adl:entity/adl:links[@rel='self']/@href" /></xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="./adl:content" />
  </xsl:template>
  
  <xsl:template match="adl:entity[not(@type)]">
    <xsl:apply-templates select="adl:content" />
  </xsl:template>

  <xsl:template match="adl:value">
    <xsl:call-template name="entity">
    	<xsl:with-param name="id"><xsl:call-template name="rel-id" /></xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="adl:content[@type]">
    <xsl:call-template name="entity">
    	<xsl:with-param name="id"><xsl:call-template name="rel-id" /></xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  
  <xsl:template match="adl:content[adl:collectionValue = 'false']">
    <xsl:variable name="from"><xsl:value-of select="../adl:links[@rel='self']/@href" /></xsl:variable>
    <xsl:variable name="to">
    	<xsl:for-each select="adl:value">
    		<xsl:call-template name="rel-id" />
    	</xsl:for-each>
   	</xsl:variable>
   	<xsl:variable name="rel">
   		<xsl:value-of select="adl:rel" />
   	</xsl:variable>
    
    <link>
      <xsl:attribute name="id"><xsl:value-of select="$from" />-<xsl:value-of select="$to" /></xsl:attribute>
      <xsl:choose>
        <xsl:when test="$rel = 'project'">
          <xsl:attribute name="drawDirection">UP</xsl:attribute>
        </xsl:when>
        <xsl:when test="$rel = 'currentRevision'">
          <xsl:attribute name="drawDirection">RIGHT</xsl:attribute>
        </xsl:when>
      </xsl:choose>
      
      <from>
        <xsl:attribute name="reference"><xsl:value-of select="$from" /></xsl:attribute>
      </from>
      <to class="arrow">
        <xsl:attribute name="reference"><xsl:value-of select="$to" /></xsl:attribute>
      </to>
      <label end="to"><xsl:value-of select="adl:rel" /></label>     
    </link>
    
    <xsl:apply-templates select="adl:value" />

  </xsl:template>
 
  
  <xsl:template match="adl:content[adl:collectionValue = 'true']">
    <xsl:variable name="from"><xsl:value-of select="../adl:links[@rel='self']/@href" /></xsl:variable>
    <xsl:variable name="to"><xsl:value-of select="adl:rel" /></xsl:variable>
   	<xsl:variable name="rel">
   		<xsl:value-of select="adl:rel" />
   	</xsl:variable>
    
   <link>
      <xsl:attribute name="id"><xsl:value-of select="$from" />-<xsl:value-of select="$to" /></xsl:attribute>
      <xsl:choose>
        <xsl:when test="$rel = 'members'">
          <xsl:attribute name="drawDirection">UP</xsl:attribute>
        </xsl:when>
        <xsl:when test="$rel = 'documents'">
          <xsl:attribute name="drawDirection">DOWN</xsl:attribute>
        </xsl:when>        
        <xsl:when test="$rel = 'revisions'">
          <xsl:attribute name="drawDirection">DOWN</xsl:attribute>
        </xsl:when>        
      </xsl:choose>
      <from>
        <xsl:attribute name="reference"><xsl:value-of select="$from" /></xsl:attribute>
      </from>
      <to class="arrow">
        <xsl:attribute name="reference"><xsl:value-of select="$to" /></xsl:attribute>
      </to>
    </link>
  
    <container>
      <xsl:attribute name="id"><xsl:value-of select="$to" /></xsl:attribute>
      <xsl:apply-templates select="adl:value" />
      <label><xsl:value-of select="$to" /></label>
    </container>
  </xsl:template>
  
  
  <xsl:template match="*">
  </xsl:template>
</xsl:stylesheet>