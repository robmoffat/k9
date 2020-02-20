<xsl:stylesheet xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns="http://www.kite9.org/schema/adl"
	xmlns:adl="http://www.kite9.org/schema/adl">
	<xsl:template match="/">
		<svg:svg zoomAndPan="magnify" preserveAspectRatio="xMidYMid">
			<svg:defs>
				<svg:style type="text/css"> @import
					url("/public/context/admin/admin.css");</svg:style>
			</svg:defs>
			<diagram>
				<xsl:apply-templates />
			</diagram>
		</svg:svg>
	</xsl:template>

	<xsl:template name="entity-box">
		<xsl:attribute name="id">
			<xsl:value-of select="adl:links[adl:rel='self']/adl:href" />
		</xsl:attribute>
		<xsl:attribute name="k9-ui">
			<xsl:value-of select="adl:commands" />
		</xsl:attribute>
		<xsl:copy-of select="adl:title" />
		<xsl:copy-of select="adl:icon" />
		<xsl:copy-of select="adl:description" />
		<lastUpdated>
			<xsl:value-of
				select="translate(adl:lastUpdated, 'T.', '  ')" />
		</lastUpdated>
	</xsl:template>
	
	<xsl:template name="entity-list">
		<xsl:attribute name="id">
			<xsl:value-of select="adl:links[adl:rel='self']/adl:href" />
		</xsl:attribute>
		<xsl:attribute name="k9-ui">
			<xsl:value-of select="adl:commands" />
		</xsl:attribute>
		<xsl:copy-of select="adl:icon" />
		<xsl:copy-of select="adl:title" />
	</xsl:template>

	<xsl:template name="user-page">
		<container class="main">
			<container class="left" id="userbox">
				<user>
					<xsl:call-template name="entity-box" />
				</user>
			</container>
			<container class="right grid" id="projectbox">
				<xsl:attribute name="subject-uri"><xsl:value-of
					select="adl:links[adl:rel='self']/adl:href" /></xsl:attribute>

				<xsl:for-each select="adl:repositories">
					<repository>
						<xsl:call-template name="entity-box" />
					</repository>
				</xsl:for-each>
				<label>My Repositories</label>
			</container>
			<xsl:if test="adl:organisations">
				<container class="right grid" id="projectbox">
					<xsl:for-each select="adl:organisations">
						<organisation>
							<xsl:call-template name="entity-box" />
						</organisation>
					</xsl:for-each>
					<label>My Organisations</label>
				</container>
			</xsl:if>
		</container>
	</xsl:template>


	<xsl:template name="directory-page">
		<container class="main">
			<container class="left" id="projectbox">
				<xsl:for-each select="adl:parent[adl:type='user']">
					<user>
						<xsl:call-template name="entity-box" />
					</user>
				</xsl:for-each>		
				<xsl:for-each select="adl:parent[adl:type='organisation']">
					<organisation>
						<xsl:call-template name="entity-box" />
					</organisation>
				</xsl:for-each>		
				<xsl:for-each select="adl:parent[adl:type='directory']">
					<directory>
						<xsl:call-template name="entity-box" />
					</directory>
				</xsl:for-each>
				<xsl:for-each select="adl:parent[adl:type='project']">
					<project>
						<xsl:call-template name="entity-box" />
					</project>
				</xsl:for-each>
			</container>
			<container class="right grid" id="documentbox">
				<xsl:attribute name="subject-uri"><xsl:value-of
					select="adl:links[adl:rel='self']/adl:href" /></xsl:attribute>
				<xsl:for-each select="adl:documents">
					<document>
						<xsl:call-template name="entity-box" />
					</document>
				</xsl:for-each>
				<label>Diagrams</label>
			</container>
				<container class="right list" id="projectbox">
				<xsl:attribute name="subject-uri"><xsl:value-of
					select="adl:links[adl:rel='self']/adl:href" /></xsl:attribute>

				<xsl:for-each select="adl:subDirectories">
					<directory>
						<xsl:call-template name="entity-list" />
					</directory>
				</xsl:for-each>
				<label>Sub-Folders</label>
			</container>
		</container>
	</xsl:template>

	<xsl:template match="/adl:entity[adl:type='user']">
		<xsl:call-template name="user-page" />
	</xsl:template>

	<xsl:template match="/adl:entity[adl:type='directory']">
		<xsl:call-template name="directory-page" />
	</xsl:template>

	<xsl:template match="*">
	</xsl:template>
</xsl:stylesheet>