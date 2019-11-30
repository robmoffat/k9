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
  
  <xsl:template match="adl:entity[@type]">
    <xsl:call-template name="entity">
    	<xsl:with-param name="id">/api<xsl:value-of select="substring-after(/adl:entity/adl:links[@rel='self']/@href,'/api')" /></xsl:with-param>
        <xsl:with-param name="focus"></xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="./adl:content" />
  </xsl:template>
  
  <xsl:template match="adl:entity[not(@type)]">
    <xsl:apply-templates select="adl:content" />
  </xsl:template>

  <xsl:template match="adl:value">
    <xsl:call-template name="entity">
    	<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>
		<xsl:with-param name="focus"> focus</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="adl:content[@type]">
    <xsl:call-template name="entity">
    	<xsl:with-param name="id"><xsl:value-of select="@localId" /></xsl:with-param>	
		<xsl:with-param name="focus"> focus</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  
  <xsl:template match="adl:content[adl:collectionValue = 'false']">
    <xsl:variable name="from">/api<xsl:value-of select="substring-after(../adl:links[@rel='self']/@href,'/api')" /></xsl:variable>
    <xsl:variable name="to"><xsl:value-of select="./adl:value/@localId" /></xsl:variable>
   	<xsl:variable name="rel">
   		<xsl:value-of select="adl:rel" />
   	</xsl:variable>
    
    <link>
      <xsl:attribute name="id"><xsl:value-of select="$from" />-<xsl:value-of select="$to" /></xsl:attribute>
      <xsl:choose>
        <xsl:when test="$rel = 'project' and /adl:entity/@type = 'document'">
          <xsl:attribute name="drawDirection">LEFT</xsl:attribute>
        </xsl:when>
        <xsl:when test="$rel = 'project' and /adl:entity/@type = 'member'">
          <xsl:attribute name="drawDirection">RIGHT</xsl:attribute>
        </xsl:when>
        <xsl:when test="$rel = 'user'">
          <xsl:attribute name="drawDirection">LEFT</xsl:attribute>
        </xsl:when>
      </xsl:choose>
      
      <from>
        <xsl:attribute name="reference"><xsl:value-of select="$from" /></xsl:attribute>
      </from>
      <to class="arrow">
        <xsl:attribute name="reference"><xsl:value-of select="$to" /></xsl:attribute>
      </to>
      <label end="from"><xsl:value-of select="adl:rel" /></label>     
    </link>
    
    <xsl:apply-templates select="adl:value[not(@type='revision')]" />

  </xsl:template>
 
  
  <xsl:template match="adl:content[adl:collectionValue = 'true']">
   	<xsl:variable name="rel"><xsl:value-of select="adl:rel" /></xsl:variable>
    <xsl:variable name="from">/api<xsl:value-of select="substring-after(../adl:links[@rel='self']/@href,'/api')" /></xsl:variable>
    <xsl:variable name="to">/api<xsl:value-of select="substring-after(/adl:entity/adl:links[@rel=$rel]/@href,'/api')" /></xsl:variable>
    
   <link>
      <xsl:attribute name="id"><xsl:value-of select="$from" />-<xsl:value-of select="$to" /></xsl:attribute>
      <xsl:choose>
        <xsl:when test="$rel = 'members'">
          <xsl:attribute name="drawDirection">LEFT</xsl:attribute>
        </xsl:when>
        <xsl:when test="$rel = 'documents'">
          <xsl:attribute name="drawDirection">RIGHT</xsl:attribute>
        </xsl:when>        
        <xsl:when test="$rel = 'revisions'">
          <xsl:attribute name="drawDirection">RIGHT</xsl:attribute>
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
      <xsl:attribute name="k9-ui">focus</xsl:attribute>
      <xsl:apply-templates select="adl:value" />
      <label><xsl:value-of select="$rel" /></label>
    </container>
  </xsl:template>
  
  
  <xsl:template match="*">
  </xsl:template>
</xsl:stylesheet>