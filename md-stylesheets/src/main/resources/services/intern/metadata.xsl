<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	xmlns:gmd="http://www.isotc211.org/2005/gmd"
  	xmlns:srv="http://www.isotc211.org/2005/srv"
  	xmlns:gco="http://www.isotc211.org/2005/gco"
  	xmlns:gml="http://www.opengis.net/gml"
  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xmlns:xlink="http://www.w3.org/1999/xlink" version="1.0">
  	
  	<xsl:output method="html" version="4.0" encoding="iso-8859-1" indent="yes" omit-xml-declaration="yes"/>
  
	<xsl:template match="/">
	  	<html>
	  		<head>
				<title>
					<xsl:apply-templates select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:title"/>
				</title>
				<style>
				 	html {
	    				overflow-y:scroll;
	    				overflow-x:hidden;
					}
					body {
					    background-color:#FFFFFF;
					    margin:0px auto 20px auto;
					    font-family:Verdana, Geneva, sans-serif;
						border-left:1px solid #AAAAAA;
						border-right:1px solid #AAAAAA;
						width: 1100px;
						padding:0px 20px;
					}
					
					p {
						font-family:Verdana;
						font-size:16px;
						color:#000000;
						line-height:25px;
						margin-top:0;
						margin-bottom:0;
					}
					
					a.link {
						text-decoration:underline;
					}
					
					a.link:hover {
						color:#FF0000;
					}
					
					p.titel {
						color:#A50821;
						font-weight:bold;
					}
					
					p.bestandsnaam {
						font-family:Verdana;
						font-size:21px;
						color:#000000;
						line-height:25px;
					}
					
					p.contacttitel {
						color:#A50821;
					}
					
					div.logo {
						margin-top:20px;margin-bottom:20px;
					}
					
					div.titelbalk {
						margin-top:20px;margin-bottom:20px;
					}
					
					h1.titel {
						padding:.3em .5em;
						border-radius:0 12px 0 0;
						background-color:#255c9f;
						margin-left:0;
						margin-right:0;
						color:#ffffff;
						font-size:1.625em;
						font-weight:bold;
						font-style:normal;
						line-height:47px;
					}
					
					.deel {
						margin-top:20px;
						margin-bottom:20px;
					}
					
					.blok {
						margin-bottom:20px;
					}
					
					.proclaimer {
						margin-top:40px;
					}
					
					
					.inspringen {
						padding-left:25px;
					}
					
					.spatie {
						margin-right:5px;
					}
					
					#wie { display:none; }
					#waar { display:none; }
					#wanneer { display:none; }
					#details { display:none; }
					
					div.tabs:after {content:'';display:block;clear: both;}
					a.tabs {height:100%;width:100%;display:block;}
					.grid {background-repeat:repeat;margin-left: -100%;margin-right: -100%;padding-left: 100%;padding-right: 100%;margin-top:20px;margin-bottom:20px;padding-top:5px;padding-bottom:5px;}
					.grid:after {content:'';display:block;clear: both;}
					
					
					.toptaak-element {float:left;width:19.95%;height:85px;display:block;}
					.toptaak-outer {height:100%;padding:1px;}
					.toptaak-inner {height:100%;background-color:#831625;}
					.toptaak-inner-active {height:100%;background-color:#dc0000;}
					.toptaak-inner:hover {background-color:#dc0000;cursor:pointer;}
					.toptaak-titel {color:#ffffff;display:inline-block;text-align:left;margin-top:13px;margin-left:57px;line-height:1.2em;font-size:18px;padding-right:20px;}
					
					#tabs-beheer {margin-left:auto;margin-right:auto;width:40%;}
					.toptaak-element-beheer {float:left;width:50%;height:85px;display:block;}
					
					.row {margin-right: -15px;margin-left: -15px;}
					.row:before {display: table;content: " ";}
					.row:after {display: table;content: " ";clear: both;}
					.col-md-7 {width: 58.33333333%;position: relative;float: left;min-height: 1px;padding-right: 15px;padding-left: 15px;}
					.pull-right {float: right !important;}
					
					.watIcon {background-image:url("/META-INF/resources/webjars/md-stylesheets/1.0/id.png");background-position:25px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:14px;}
					.wieIcon {background-image:url("/META-INF/resources/webjars/md-stylesheets/1.0/user.png");background-position:15px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					.waarIcon {background-image:url("/META-INF/resources/webjars/md-stylesheets/1.0/world.png");background-position:10px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					.wanneerIcon {background-image:url("/META-INF/resources/webjars/md-stylesheets/1.0/time.png");background-position:10px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					.detailsIcon {background-image:url("/META-INF/resources/webjars/md-stylesheets/1.0/detail.png");background-position:10px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					
					<!-- 
					#abstract {max-height:180px;overflow: hidden;}
					#abstract-more {font-weight:bold;color:#005B99;}
					-->
				 </style>
				 <script>
				 	function tabs(e) {
						var a = e.currentTarget;
						
						document.getElementById("wat").style.display = "none";
						document.getElementById("tabWat").className = "toptaak-inner";
						
						document.getElementById("wie").style.display = "none";
						document.getElementById("tabWie").className = "toptaak-inner";
						
						document.getElementById("waar").style.display = "none";
						document.getElementById("tabWaar").className = "toptaak-inner";
						
						document.getElementById("wanneer").style.display = "none";
						document.getElementById("tabWanneer").className = "toptaak-inner";
						
						document.getElementById("details").style.display = "none";
						document.getElementById("tabDetails").className = "toptaak-inner";
						
						if(a.id == "tabWat") {
							document.getElementById("wat").style.display = "block";
							document.getElementById("tabWat").className = "toptaak-inner-active";
						}
						if(a.id == "tabWie") {
							document.getElementById("wie").style.display = "block";
							document.getElementById("tabWie").className = "toptaak-inner-active";
						}
						if(a.id == "tabWaar") {
							document.getElementById("waar").style.display = "block";
							document.getElementById("tabWaar").className = "toptaak-inner-active";
						}
						if(a.id == "tabWanneer") {
							document.getElementById("wanneer").style.display = "block";
							document.getElementById("tabWanneer").className = "toptaak-inner-active";
						}
						if(a.id == "tabDetails") {
							document.getElementById("details").style.display = "block";
							document.getElementById("tabDetails").className = "toptaak-inner-active";
						}
					}
					
					<!-- 
					function abstractMore() {
						if(document.getElementById("abstract-more").innerHTML === "Lees verder") {
							document.getElementById("abstract").style.maxHeight = "unset";
							document.getElementById("abstract-more").innerHTML = "Inklappen";
						} else {
							document.getElementById("abstract").style.maxHeight = "180px";
							document.getElementById("abstract-more").innerHTML = "Lees verder";
						}
					}
					-->
				 </script>
			</head>
			<body>
				<div class="logo">
					<a href="http://www.overijssel.nl">
						<img src="/META-INF/resources/webjars/md-stylesheets/1.0/logo_overijssel.png" class="img-responsive" alt="Responsive image"></img>
					</a>
				</div>
				<div class="titelbalk">
					<h1 class="titel">Servicebeschrijving Geoportaal</h1>
				</div>
				<div class="grid">
					<div class="tabs">
						<div class="toptaak-element">
							<div class="toptaak-outer">
								<div class="toptaak-inner-active" id="tabWat" onclick="tabs(event);">
									<a class="tabs watIcon">
										<span class="toptaak-titel">
											Wat
										</span>
									</a>
								</div>
							</div>
						</div>
					
						<div class="toptaak-element">
							<div class="toptaak-outer">
								<div class="toptaak-inner" id="tabWie" onclick="tabs(event);">
									<a class="tabs wieIcon">
										<span class="toptaak-titel">
											Wie									
										</span>
									</a>
								</div>
							</div>
						</div>
						
						<div class="toptaak-element">
							<div class="toptaak-outer">
								<div class="toptaak-inner" id="tabWaar" onclick="tabs(event);">
									<a class="tabs waarIcon">
										<span class="toptaak-titel">
											Waar
										</span>
									</a>
								</div>
							</div>
						</div>
						
						<div class="toptaak-element">
							<div class="toptaak-outer">
								<div class="toptaak-inner" id="tabWanneer" onclick="tabs(event);">
									<a class="tabs wanneerIcon">
										<span class="toptaak-titel">
											Wanneer
										</span>
									</a>
								</div>
							</div>
						</div>
						
						<div class="toptaak-element">
							<div class="toptaak-outer">
								<div class="toptaak-inner" id="tabDetails" onclick="tabs(event);">
									<a class="tabs detailsIcon">
										<span class="toptaak-titel">
											Details
										</span>
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			
			
				<div id="metadata"></div>
				<xsl:apply-templates/>
			</body>
	  		<script>
	  			<!-- 
	  			var abstractHeight = document.getElementById('abstract').clientHeight;
	  			if(abstractHeight >= 180) {
	  				//do nothing
	  			} else {
	  				document.getElementById('abstract-more').style.display = "none";
	  			}
	  			-->
	  		</script>
  		</html>
	</xsl:template>
	
	<xsl:template match="gmd:MD_Metadata">
		<div id="titel">
			<div class="blok">
				<p class="bestandsnaam">
					<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/>
				</p>
			</div>
		</div>
		
		<div id="wat">
			<div class="blok">
				<div id="abstract">
					<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:abstract/gco:CharacterString"/>
				</div>
				<!-- 
				<p>
					<a id="abstract-more" onclick="abstractMore()">Lees verder</a>
				</p>
				-->
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine[1]"/>
			</div>
			<div class="blok">
				<p>
					<b><xsl:text>Lagen: </xsl:text></b>
				</p>
				<p>
					<ul>
						<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn/@xlink:href"/>
					</ul>
				</p>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints[1]"/>
			</div>
		</div>
		<div id="wie">
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:contact/gmd:CI_ResponsibleParty"/>
			</div>
		</div>
		<div id="waar">
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:description/gco:CharacterString"/>
			</div>
			<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox"/>
		</div>
		<div id="wanneer">
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:dateStamp/gco:Date"/>
			</div>			
		</div>
		<div id="details">
			<div class="blok">
				<xsl:apply-templates select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue"/>
			</div>
			<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn"/>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:couplingType/srv:SV_CouplingType/@codeListValue"/>
				
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType/gco:LocalName"/>
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:operationName/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:DCP/srv:DCPList/@codeListValue"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:fileIdentifier/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:language/gmd:LanguageCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:metadataStandardName/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:metadataStandardVersion/gco:CharacterString"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints[position() > 1]"/>
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:classification/gmd:MD_ClassificationCode/@codeListValue"/>
			</div>
			<div class="proclaimer">
				<p>Deze informatie wordt beschikbaar gesteld door het Geoportaal van de Provincie Overijssel.</p>
				<p>De gegevens zijn gemaakt voor eigen (intern) gebruik door de provincie Overijssel.</p>
				<p>Zijn er suggesties of vragen, neem dan contact op met
					<a href="mailto:beleidsinformatie@overijssel.nl">beleidsinformatie@overijssel.nl</a>
				</p>
			</div>
		</div>		
 	</xsl:template>
 	
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString">
  		<xsl:if test=". != ''">
  			<b><xsl:text>Naam van de service: </xsl:text></b>
  			<xsl:value-of select="."/>
  		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:title">
  		<xsl:if test="gco:CharacterString/. != ''">
  			<xsl:value-of select="."/>
  		</xsl:if>
  	</xsl:template>
 	
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:abstract/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Samenvatting: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode">
  		<xsl:choose>
 			<xsl:when test="@codeListValue = 'revision'">
 				<p>
					<b><xsl:text>Laatste wijziging service: </xsl:text></b>
	  				<xsl:value-of select="../../gmd:date/gco:Date"/>
	 			</p>
 			</xsl:when>
 		</xsl:choose>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty">
 		<xsl:if test=". != ''">
	 		<div class="blok">
		 		<b>
			 		<p>
			 			<xsl:choose>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'resourceProvider'">
		 						<xsl:text>Verstrekker</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'custodian'">
		 						<xsl:text>Beheerder</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'owner'">
		 						<xsl:text>Eigenaar</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'user'">
		 						<xsl:text>Gebruiker</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'distributor'">
		 						<xsl:text>Distributeur</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'originator'">
		 						<xsl:text>Maker</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'pointOfContact'">
		 						<xsl:text>Contactpunt</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'principalInvestigator'">
		 						<xsl:text>Inwinner</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'processor'">
		 						<xsl:text>Bewerker</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'publisher'">
		 						<xsl:text>Uitgever</xsl:text>
		 					</xsl:when>
		 					<xsl:when test="gmd:role/gmd:CI_RoleCode/@codeListValue = 'author'">
		 						<xsl:text>Auteur</xsl:text>
		 					</xsl:when>
			 				<xsl:otherwise>
			 					<xsl:value-of select="gmd:role/gmd:CI_RoleCode/@codeListValue"/>
			 				</xsl:otherwise>
		 				</xsl:choose>
			 			van de service
		 			</p>
		 		</b>
		 		<xsl:if test="gmd:organisationName/gco:CharacterString != ''">
		 			<p>
		 				<b><xsl:text>Naam: </xsl:text></b>
		 				<xsl:value-of select="gmd:organisationName/gco:CharacterString"/>
		 			</p>
		 		</xsl:if>
		 		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL != ''">
		 			<p>
		 				<b><xsl:text>Website: </xsl:text></b>
		 				<xsl:choose>
							<xsl:when test="substring(substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http'),1,3) = '://'">
								<xsl:value-of select="substring-before(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http://')"/>
								<xsl:text> </xsl:text>
								<a href="http://{substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http://')}">
									http://<xsl:value-of select="substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http://')"/>
								</a>
							</xsl:when>
							<xsl:when test="substring(substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https'),1,3) = '://'">
								<xsl:value-of select="substring-before(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https://')"/>
								<xsl:text> </xsl:text>
								<a href="https://{substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https://')}">
									https://<xsl:value-of select="substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https://')"/>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
							</xsl:otherwise>
						</xsl:choose>
		 			</p>
		 		</xsl:if>
			  	<xsl:if test="gmd:individualName/gco:CharacterString != ''">
		 			<p>
		 				<b><xsl:text>Naam: </xsl:text></b>
		 				<xsl:value-of select="gmd:individualName/gco:CharacterString"/>
		 			</p>
		 		</xsl:if>
			  	<xsl:if test="gmd:positionName/gco:CharacterString != ''">
		 			<p>
		 				<b><xsl:text>Rol: </xsl:text></b>
		 				<xsl:value-of select="gmd:positionName/gco:CharacterString"/>
		 			</p>
		 		</xsl:if>
			  	<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString != ''">
		 			<p>
		 				<b><xsl:text>E-mail: </xsl:text></b>
		 				<a href="mailto:{gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString}">
		 					<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString"/>
		 				</a>
		 			</p>
		 		</xsl:if>
			  	<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Adres: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString"/>
					</p>
				</xsl:if>
			  	<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Postcode: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString"/>
					</p>
				</xsl:if>
			  	<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Plaats: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString"/>
					</p>
				</xsl:if>
			  	<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:administrativeArea/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Provincie: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:administrativeArea/gco:CharacterString"/>
					</p>
				</xsl:if>
			  	<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Land: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Telefoonnummer: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Faxnummer: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString"/>
					</p>
				</xsl:if>
			</div>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:description/gco:CharacterString">
  		<xsl:if test=". != ''">
  			<p><b>Beschrijving geografisch gebied: </b> <xsl:value-of select="."/></p>
  		</xsl:if>
	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox">
  		<xsl:if test=". != ''">
  			<div class="blok">
	  			<b><p>Omgrenzende rechthoek in decimale graden</p></b>
	  			<xsl:if test="gmd:westBoundLongitude/gco:Decimal != ''">
					<p>
						<b><xsl:text>Minimum x-coördinaat: </xsl:text></b>
						<xsl:value-of select="gmd:westBoundLongitude/gco:Decimal"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:eastBoundLongitude/gco:Decimal != ''">
					<p>
						<b><xsl:text>Maximum x-coördinaat: </xsl:text></b>
						<xsl:value-of select="gmd:eastBoundLongitude/gco:Decimal"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:southBoundLatitude/gco:Decimal != ''">
					<p>	
						<b><xsl:text>Minimum y-coördinaat: </xsl:text></b>
						<xsl:value-of select="gmd:southBoundLatitude/gco:Decimal"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:northBoundLatitude/gco:Decimal != ''">
					<p>
						<b><xsl:text>Maximum y-coördinaat: </xsl:text></b>
						<xsl:value-of select="gmd:northBoundLatitude/gco:Decimal"/>
					</p>
				</xsl:if>
			</div>
			<div class="blok">
				<p>
					<b>Code referentiesysteem: </b>
					84
				</p>
				<p>
					<b>Organisatie referentiesysteem: </b>
					WGS
				</p>
			</div>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:dateStamp/gco:Date">
  		<xsl:if test=". != ''">
			<div class="blok">
				<p>
					<b><xsl:text>Laatste wijziging servicebeschrijving: </xsl:text></b>
	  				<xsl:value-of select="."/>
  				</p>
 			</div>
		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date">
 		<xsl:if test=". != ''">
	 		<xsl:choose>
	 			<xsl:when test="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'creation'">
	 				<p>
						<b><xsl:text>Voltooiing service: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 			<xsl:when test="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'publication'">
	 				<p>
						<b><xsl:text>Publicatie service: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 			<xsl:when test="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'revision'">
	 				<p>
						<b><xsl:text>Laatste wijziging service: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 		</xsl:choose>
 		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue">
  		<xsl:if test=". != ''">
			<xsl:choose>
		  		<xsl:when test=". = 'dataset'">
					<p>
						<b><xsl:text>Metadata hiërarchieniveau: </xsl:text></b>
						<xsl:text>Dataset</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'series'">
					<p>
						<b><xsl:text>Metadata hiërarchieniveau: </xsl:text></b>
						<xsl:text>Series</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'featureType'">
					<p>
						<b><xsl:text>Metadata hiërarchieniveau: </xsl:text></b>
						<xsl:text>Feature type</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'service'">
					<p>
						<b><xsl:text>Metadata hiërarchieniveau: </xsl:text></b>
						<xsl:text>Service</xsl:text>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<b><xsl:text>Metadata hiërarchieniveau: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine[1]">
  		<xsl:if test="gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString != ''">
  			<p>
				<b><xsl:value-of select="gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString"/>: </b>
  				<xsl:if test="gmd:CI_OnlineResource/gmd:linkage/gmd:URL != ''">
  					<xsl:value-of select="gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
  				</xsl:if>
 			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn">
  		<div class="blok">
	  		<xsl:if test="@uuidref != ''">
				<p>
					<b><xsl:text>Gekoppeld bestand: </xsl:text></b>
					<xsl:value-of select="@uuidref"/>
				</p>
			</xsl:if>
			<xsl:if test="@xlink:href != ''">
				<p>
					<b><xsl:text>URL: </xsl:text></b>
					<xsl:choose>
						<xsl:when test="substring(substring-after(@xlink:href,'http'),1,3) = '://'">
							<xsl:value-of select="substring-before(@xlink:href,'http://')"/>
							<xsl:text> </xsl:text>
							<a href="http://{substring-after(@xlink:href,'http://')}">
								http://<xsl:value-of select="substring-after(@xlink:href,'http://')"/>
							</a>
						</xsl:when>
						<xsl:when test="substring(substring-after(@xlink:href,'https'),1,3) = '://'">
							<xsl:value-of select="substring-before(@xlink:href,'https://')"/>
							<xsl:text> </xsl:text>
							<a href="https://{substring-after(@xlink:href,'https://')}">
								https://<xsl:value-of select="substring-after(@xlink:href,'https://')"/>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@xlink:href"/>
						</xsl:otherwise>
					</xsl:choose>
				</p>
			</xsl:if>
		</div>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:couplingType/srv:SV_CouplingType/@codeListValue">
  		<xsl:if test=". != ''">
			<xsl:choose>
		  		<xsl:when test=". = 'loose'">
					<p>
						<b><xsl:text>Koppeltype: </xsl:text></b>
						<xsl:text>Niet gekoppeld met een specifieke dataset of dataset serie.</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'mixed'">
					<p>
						<b><xsl:text>Koppeltype: </xsl:text></b>
						<xsl:text>Gekoppeld met een specifieke dataset of dataset serie, kan ook gebruikt worden met externe data.</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'tight'">
					<p>
						<b><xsl:text>Koppeltype: </xsl:text></b>
						<xsl:text>Gekoppeld met een specifieke dataset of dataset serie.</xsl:text>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<b><xsl:text>Koppeltype: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType/gco:LocalName">
  		<xsl:if test=". != ''">
  			<p>
				<b><xsl:text>Type service: </xsl:text></b>
  				<xsl:value-of select="."/>
 			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:operationName/gco:CharacterString">
  		<xsl:if test=". != ''">
  			<p>
				<b><xsl:text>Operatie naam: </xsl:text></b>
  				<xsl:value-of select="."/>
 			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations/srv:SV_OperationMetadata/srv:DCP/srv:DCPList/@codeListValue">
  		<xsl:if test=". != ''">
  			<p>
				<b><xsl:text>DCP: </xsl:text></b>
				<xsl:value-of select="."/>
			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString">
  		<xsl:if test=". != ''">
  			<p>
				<b><xsl:text>Trefwoord: </xsl:text></b>
				<xsl:value-of select="."/>
			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation">
  		<xsl:if test="gmd:title/gco:CharacterString != ''">
		  	<p>
		  		<b><xsl:text>Thesaurus trefwoorden: </xsl:text></b>
		  		<xsl:value-of select="gmd:title/gco:CharacterString"/>
		  	</p>
	  	</xsl:if>
		<xsl:if test="gmd:date/gmd:CI_Date/gmd:date/gco:Date != ''">
		  	<p>
		  		<xsl:choose>
		  			<xsl:when test="gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'creation'">
		  				<b><xsl:text>Creatie datum thesaurus: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gmd:CI_Date/gmd:date/gco:Date"/>
		  			</xsl:when>
		  			<xsl:when test="gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'publication'">
		  				<b><xsl:text>Publicatie datum thesaurus: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gmd:CI_Date/gmd:date/gco:Date"/>
		  			</xsl:when>
		  			<xsl:when test="gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'revision'">
		  				<b><xsl:text>Revisie datum thesaurus: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gmd:CI_Date/gmd:date/gco:Date"/>
		  			</xsl:when>
		  		</xsl:choose>
		  	</p>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue">
  		<xsl:if test=". != ''">
			<xsl:choose>
		  		<xsl:when test=". = 'dataset'">
					<p>
						<b><xsl:text>Niveau kwaliteitbeschrijving: </xsl:text></b>
						<xsl:text>Dataset</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'series'">
					<p>
						<b><xsl:text>Niveau kwaliteitbeschrijving: </xsl:text></b>
						<xsl:text>Series</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'featureType'">
					<p>
						<b><xsl:text>Niveau kwaliteitbeschrijving: </xsl:text></b>
						<xsl:text>Feature type</xsl:text>
					</p>
				</xsl:when>
				<xsl:when test=". = 'service'">
					<p>
						<b><xsl:text>Niveau kwaliteitbeschrijving: </xsl:text></b>
						<xsl:text>Service</xsl:text>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<b><xsl:text>Niveau kwaliteitbeschrijving: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString">
		<xsl:if test=". != ''">
			<p>
		  		<b>Metadata unieke identifier: </b>
		  		<xsl:value-of select="."/>
	  		</p>
		</xsl:if>
	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:language/gmd:LanguageCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<xsl:choose>
		  		<xsl:when test=". = 'dut'">
					<p>
						<b><xsl:text>Metadata taal: </xsl:text></b>
						<xsl:text>Nederlands</xsl:text>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<b><xsl:text>Metadata taal: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:metadataStandardName/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Metadata standaard naam: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
		</xsl:if>	
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:metadataStandardVersion/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Metadata standaard versie: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints[1]">
 		<xsl:if test="gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString != ''">
	  		<p>
		  		<b>Gebruiksbeperkingen: </b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints[position() > 1]">
 		<xsl:if test="gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString != ''">
	  		<p>
		  		<b><xsl:text>Gebruiksbeperkingen: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue">
 		<xsl:if test=". != ''">
	  		<xsl:choose>
	  			<xsl:when test=". = 'copyright'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Copyright</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'patent'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Patent</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'patentPending'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Patent in wording</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'trademark'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Merknaam</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'license'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Licentie</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'intellectualPropertyRights'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Intellectueel</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'restricted'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Niet toegankelijk</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'otherRestrictions'">
	  				<p>
				  		<b><xsl:text>(Juridische) toegangsrestricties: </xsl:text></b>
				  		<xsl:text>Anders</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:otherwise>
	  				<p>
				  		<b>(Juridische) toegangsrestricties: </b>
				  		<xsl:value-of select="."/>
			  		</p>
	  			</xsl:otherwise>
	  		</xsl:choose>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString">
 		<xsl:if test=". != ''">
  			<p>
		  		<b><xsl:text>Overige beperkingen: </xsl:text></b>
		  		<xsl:choose>
					<xsl:when test="substring(substring-after(.,'http'),1,3) = '://'">
						<xsl:value-of select="substring-before(.,'http://')"/>
						<xsl:text> </xsl:text>
						<a href="http://{substring-after(.,'http://')}">
							http://<xsl:value-of select="substring-after(.,'http://')"/>
						</a>
					</xsl:when>
					<xsl:when test="substring(substring-after(.,'https'),1,3) = '://'">
						<xsl:value-of select="substring-before(.,'https://')"/>
						<xsl:text> </xsl:text>
						<a href="https://{substring-after(.,'https://')}">
							https://<xsl:value-of select="substring-after(.,'https://')"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:classification/gmd:MD_ClassificationCode/@codeListValue">
 		<xsl:if test=". != ''">
	  		<xsl:choose>
	  			<xsl:when test=". = 'unclassified'">
	  				<p>
				  		<b><xsl:text>Classificatie: </xsl:text></b>
				  		<xsl:text>Vrij toegankelijk</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'restricted'">
	  				<p>
				  		<b><xsl:text>Classificatie: </xsl:text></b>
				  		<xsl:text>Niet toegankelijk</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'confidential'">
	  				<p>
				  		<b><xsl:text>Classificatie: </xsl:text></b>
				  		<xsl:text>Vertrouwelijk</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'secret'">
	  				<p>
				  		<b><xsl:text>Classificatie: </xsl:text></b>
				  		<xsl:text>Geheim</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'topSecret'">
	  				<p>
				  		<b><xsl:text>Classificatie: </xsl:text></b>
				  		<xsl:text>Zeer geheim</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:otherwise>
	  				<p>
				  		<b>Classificatie: </b>
				  		<xsl:value-of select="."/>
			  		</p>
	  			</xsl:otherwise>
	  		</xsl:choose>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:contact/gmd:CI_ResponsibleParty">
  		<xsl:if test=". != ''">
	  		<div class="blok">
		  		<b><p>Auteur servicebeschrijving</p></b>
		  		<xsl:if test="gmd:organisationName/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Naam: </xsl:text></b>
		  				<xsl:value-of select="gmd:organisationName/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL != ''">
		  			<p>
	  					<b><xsl:text>Website: </xsl:text></b>
	  					<xsl:choose>
							<xsl:when test="substring(substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http'),1,3) = '://'">
								<xsl:value-of select="substring-before(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http://')"/>
								<xsl:text> </xsl:text>
								<a href="http://{substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http://')}">
									http://<xsl:value-of select="substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'http://')"/>
								</a>
							</xsl:when>
							<xsl:when test="substring(substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https'),1,3) = '://'">
								<xsl:value-of select="substring-before(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https://')"/>
								<xsl:text> </xsl:text>
								<a href="https://{substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https://')}">
									https://<xsl:value-of select="substring-after(gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL,'https://')"/>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
							</xsl:otherwise>
						</xsl:choose>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:individualName/gco:CharacterString != ''">
		  			<p>	
		  				<b><xsl:text>Naam: </xsl:text></b>
		  				<xsl:value-of select="gmd:individualName/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:positionName/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Rol: </xsl:text></b>
		  				<xsl:value-of select="gmd:positionName/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>E-mail: </xsl:text></b>
		  				<a href="mailto:{gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString}">
		  					<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString"/>
		  				</a>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Adres: </xsl:text></b>
		  				<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Postcode: </xsl:text></b>
		  				<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Plaats: </xsl:text></b>
		  				<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:administrativeArea/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Provincie: </xsl:text></b>
		  				<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:administrativeArea/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString != ''">
		  			<p>
		  				<b><xsl:text>Land: </xsl:text></b>
		  				<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString"/>
		  			</p>
		  		</xsl:if>
		  		<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Telefoonnummer: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Faxnummer: </xsl:text></b>
						<xsl:value-of select="gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString"/>
					</p>
				</xsl:if>
			</div>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn/@xlink:href">
 		<xsl:if test=". != ''">
  			<xsl:choose>
  				<xsl:when test="substring(substring-after(.,'http'),1,3) = '://'">
					<xsl:variable name="uuidDataMD" select="../@uuidref"/>
					<xsl:for-each select="../../srv:coupledResource/srv:SV_CoupledResource[srv:identifier/gco:CharacterString = $uuidDataMD]">
						<xsl:if test="gco:ScopedName != ''">
							<xsl:for-each select="../../srv:operatesOn[@uuidref = $uuidDataMD]">
								<li>
									<a href="http://{substring-after(@xlink:href,'http://')}">
										<xsl:for-each select="../srv:coupledResource/srv:SV_CoupledResource/srv:identifier[gco:CharacterString= $uuidDataMD]">
											<xsl:value-of select="../gco:ScopedName"/>
										</xsl:for-each>
									</a>
								</li>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:when test="substring(substring-after(.,'https'),1,3) = '://'">
					<xsl:variable name="uuidDataMD" select="../@uuidref"/>
					<xsl:for-each select="../../srv:coupledResource/srv:SV_CoupledResource[srv:identifier/gco:CharacterString = $uuidDataMD]">
						<xsl:if test="gco:ScopedName != ''">
							<xsl:for-each select="../../srv:operatesOn[@uuidref = $uuidDataMD]">
								<li>
									<a href="https://{substring-after(@xlink:href,'https://')}">
										<xsl:for-each select="../srv:coupledResource/srv:SV_CoupledResource/srv:identifier[gco:CharacterString= $uuidDataMD]">
											<xsl:value-of select="../gco:ScopedName"/>
										</xsl:for-each>
									</a>
								</li>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
  					<xsl:variable name="uuidDataMD" select="../@uuidref"/>
					<xsl:for-each select="../../srv:coupledResource/srv:SV_CoupledResource[srv:identifier/gco:CharacterString = $uuidDataMD]">
						<xsl:if test="gco:ScopedName != ''">
							<xsl:for-each select="../../srv:operatesOn[@uuidref = $uuidDataMD]">
								<li>
									<xsl:for-each select="../srv:coupledResource/srv:SV_CoupledResource/srv:identifier[gco:CharacterString= $uuidDataMD]">
										<xsl:value-of select="../gco:ScopedName"/>
									</xsl:for-each>
								</li>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</xsl:otherwise>
  			</xsl:choose>
  		</xsl:if>
  	</xsl:template>
</xsl:stylesheet>