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
					<xsl:apply-templates select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title"/>
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
					
					.watIcon {background-image:url("@id.png@");background-position:25px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:14px;}
					.wieIcon {background-image:url("@user.png@");background-position:15px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					.waarIcon {background-image:url("@world.png@");background-position:10px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					.wanneerIcon {background-image:url("@time.png@");background-position:10px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					.detailsIcon {background-image:url("@detail.png@");background-position:10px 15px;width:60px !important;height:80px !important;background-repeat:no-repeat;background-size:35px;}
					
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
						<img src="@logo_overijssel.png@" class="img-responsive" alt="Responsive image"></img>
					</a>
				</div>
				<div class="titelbalk">
					<h1 class="titel">Bestandsbeschrijving Geoportaal</h1>
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
					<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/>
				</p>
			</div>
		</div>
		
		<div id="wat">
			<div class="blok">
				<div id="abstract">
					<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString"/>
				</div>
				<!-- 
				<p>
					<a id="abstract-more" onclick="abstractMore()">Lees verder</a>
				</p>
				-->
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType/gmd:MD_SpatialRepresentationTypeCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:purpose/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints[1]"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:supplementalInformation/gco:CharacterString"/>
			</div>
			
			
		</div>
		
		<div id="wie">
			<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty"/>
			<xsl:apply-templates select="gmd:contact/gmd:CI_ResponsibleParty"/>
			<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributorContact/gmd:CI_ResponsibleParty"/>
		</div>
		
		<div id="waar">
			<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString"/>
			<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox"/>
			<xsl:apply-templates select="gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier"/>
		</div>
		
		<div id="wanneer">
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:dateOfNextUpdate/gco:Date"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode/@codeListValue"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:dateStamp/gco:Date"/>
			</div>
			
			<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod"/>
			
		</div>
		
		<div id="details">
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:status/gmd:MD_ProgressCode/@codeListValue"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:edition/gco:CharacterString"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceSpecificUsage/gmd:MD_Usage/gmd:specificUsage/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_CompletenessOmission/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record"/>
				<xsl:apply-templates select="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_AbsoluteExternalPositionalAccuracy/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record"/>
			</div>
			
			<xsl:apply-templates select="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description/gco:CharacterString"/>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:topicCategory/gmd:MD_TopicCategoryCode"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints[position() > 1]"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:fileIdentifier/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:language/gmd:LanguageCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:metadataStandardName/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:metadataStandardVersion/gco:CharacterString"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:language/gmd:LanguageCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:aggregationInfo/gmd:MD_AggregateInformation/gmd:aggregateDataSetName/gmd:CI_Citation"/>
				<xsl:apply-templates select="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue"/>
			</div>
			
			<div class="blok">
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:fees/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:turnaround/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:unitsOfDistribution/gco:CharacterString"/>
				<xsl:apply-templates select="gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue"/>
			</div>
			
			<div class="proclaimer">
				<p>Deze informatie wordt beschikbaar gesteld door het Geoportaal van de Provincie Overijssel.</p>
				<p>De gegevens zijn gemaakt voor eigen (intern) gebruik door de provincie Overijssel.</p>
				<p>Zijn er suggesties of vragen, neem dan contact op met
					<a href="mailto:beleidsinformatie@overijssel.nl">beleidsinformatie@overijssel.nl</a>
				</p>
			</div>
		</div>
		
		<!-- <xsl:apply-templates select="gmd:fileIdentifier"/> -->
 		
		
 	</xsl:template>
 	
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString">
  		<xsl:if test=". != ''">
  			<b><xsl:text>Bestandsnaam: </xsl:text></b>
  			<xsl:value-of select="."/>
  		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title">
  		<xsl:if test="gco:CharacterString/. != ''">
  			<xsl:value-of select="."/>
  		</xsl:if>
  	</xsl:template>
 	
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Samenvatting: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString">
  		<xsl:if test=". != ''">
  			<div class="blok">
 				<p>
			  		<b><xsl:text>Voorbeeld: </xsl:text></b>
			  		<div class="deel">
			  			<img src="{translate(., '\', '/')}"/>
			  		</div>
			  	</p>
			  </div>
	  	</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Toepassingsschaal: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType/gmd:MD_SpatialRepresentationTypeCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<xsl:choose>
		  		<xsl:when test=". = 'vector'">
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:text>Vector</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'grid'">
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:text>Grid</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'textTable'">
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:text>Tekst tabel</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'tin'">
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:text>Tin</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'stereoModel'">
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:text>Stereo model</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'video'">
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:text>Video</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
		  				<b><xsl:text>Ruimtelijk schema: </xsl:text></b>
		  				<xsl:value-of select="."/>
		  			</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:purpose/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Doel van vervaardiging: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue">
 		<xsl:if test=". != ''">
	 		<xsl:choose>
	 			<xsl:when test=". = 'revision'">
	 				<p>
						<b><xsl:text>Laatste wijziging bestand: </xsl:text></b>
		  				<xsl:value-of select="../../../gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 		</xsl:choose>
 		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints[1]">
 		<xsl:if test="gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString != ''">
	  		<p>
		  		<b>Gebruiksbeperkingen: </b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:supplementalInformation/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Aanvullende informatie: </xsl:text></b>
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
 	
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty">
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
			 			van het bestand
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
  	<xsl:template match="gmd:contact/gmd:CI_ResponsibleParty">
  		<xsl:if test=". != ''">
	  		<div class="blok">
		  		<b><p>Auteur bestandsbeschrijving</p></b>
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
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributorContact/gmd:CI_ResponsibleParty">
		<xsl:if test=". != ''">
			<div class="blok">
				<b><p>Distributeur van het bestand</p></b>
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
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<div class="blok">
				<p>	
					<b><xsl:text>Beschrijving geografisch gebied: </xsl:text></b>
					<xsl:value-of select="."/>
				</p>
			</div>	
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox">
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
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier">
  		<xsl:if test=". != ''">
	  		<div class="blok">
	  			<xsl:if test="gmd:code/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Code referensysteem: </xsl:text></b>
						<xsl:value-of select="gmd:code/gco:CharacterString"/>
					</p>
				</xsl:if>
				<xsl:if test="gmd:codeSpace/gco:CharacterString != ''">
					<p>
						<b><xsl:text>Organisatie referentiesysteem: </xsl:text></b>
						<xsl:value-of select="gmd:codeSpace/gco:CharacterString"/>
					</p>
				</xsl:if>
	  		</div>
  		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:dateStamp/gco:Date">
  		<xsl:if test=". != ''">
			<div class="blok">
				<p>
					<b><xsl:text>Laatste wijziging bestandsbeschrijving: </xsl:text></b>
	  				<xsl:value-of select="."/>
  				</p>
 			</div>
		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date">
 		<xsl:if test=". != ''">
	 		<xsl:choose>
	 			<xsl:when test="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'creation'">
	 				<p>
						<b><xsl:text>Voltooiing bestand: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 			<xsl:when test="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'publication'">
	 				<p>
						<b><xsl:text>Publicatie bestand: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 			<xsl:when test="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'revision'">
	 				<p>
						<b><xsl:text>Laatste wijziging bestand: </xsl:text></b>
		  				<xsl:value-of select="gmd:date/gco:Date"/>
		 			</p>
	 			</xsl:when>
	 			<xsl:otherwise>
	 				<p>
	 					<b><xsl:text>Datum: </xsl:text></b>
	 					<xsl:value-of select="gmd:date/gco:Date"/>
	 				</p>
	 			</xsl:otherwise>
	 		</xsl:choose>
 		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:dateOfNextUpdate/gco:Date">
  		<xsl:if test=". != ''">
			<div class="blok">
				<p>
					<b><xsl:text>Volgende herziening bestand: </xsl:text></b>
	  				<xsl:value-of select="."/>
  				</p>
 			</div>
		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod">
  		<xsl:if test=". != ''">
	  		<div class="blok">
	  			<b><p>Temporele dekking</p></b>
	  			<xsl:if test="gml:begin/gml:TimeInstant/gml:timePosition != ''">
	 				<p>
	  					<b><xsl:text>Van datum: </xsl:text></b>
	  					<xsl:value-of select="gml:begin/gml:TimeInstant/gml:timePosition"/>
	 				</p>
	 			</xsl:if>
	  			<xsl:if test="gml:end/gml:TimeInstant/gml:timePosition != ''">
	  				<p>
	  					<b><xsl:text>Tot datum: </xsl:text></b>
	  					<xsl:value-of select="gml:end/gml:TimeInstant/gml:timePosition"/>
	  				</p>
	  			</xsl:if>
	  		</div>
  		</xsl:if>
  	</xsl:template>
 	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<div class="blok">
	  			<xsl:choose>
		  			<xsl:when test=". = 'continual'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>continu</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'daily'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>Dagelijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'weekly'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>Wekelijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'fortnightly'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>2-wekelijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'monthly'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>Maandelijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'quarterly'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>1 x per kwartaal</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'biannually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>1 x per half jaar</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>Jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '2annually'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>2-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '3annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>3-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '4annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>4-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '5annually'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>5-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '6annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>6-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '7annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>7-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '8annually'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>8-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '9annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>9-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = '10annually'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>10-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'moreThan10annually'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>Meer dan 10-jaarlijks</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'asNeeded'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>Indien nodig</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'irregular'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>Onregelmatig</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'notPlanned'">
		 				<p>
		  					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		  					<xsl:text>Niet gepland</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:when test=". = 'unknown'">
		 				<p>
		 					<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
		 					<xsl:text>Onbekend</xsl:text>
		 				</p>
		  			</xsl:when>
		  			<xsl:otherwise>
		  				<p>
			  				<b><xsl:text>Herzieningsfrequentie bestand: </xsl:text></b>
			  				<xsl:value-of select="."/>
		  				</p>
		  			</xsl:otherwise>
	  			</xsl:choose>
	  		</div>
	  	</xsl:if>
  	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Alternatieve titel: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:status/gmd:MD_ProgressCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<xsl:choose>
		  		<xsl:when test=". = 'completed'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>Compleet</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'historicalArchive'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>Historisch archief</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'obsolete'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>Niet relevant</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'onGoing'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>Continu geactualiseerd</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'planned'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>Gepland</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'required'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>Actualisatie vereist</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'underDevelopment'">
					<p>
		  				<b><xsl:text>Status: </xsl:text></b>
		  				<xsl:text>In ontwikkeling</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<b><xsl:text>Status: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine">
  		<xsl:variable name="url">
	  		<xsl:choose>
	  			<xsl:when test="gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString = 'download'">
		  			<p>
			  			<b><xsl:text>Download: </xsl:text></b>
			  			<a href="{gmd:CI_OnlineResource/gmd:linkage/gmd:URL}">
			  				<xsl:value-of select="gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
			  			</a>
			  		</p>
		  		</xsl:when>
		  		<xsl:otherwise>
			  		<p>
			  			<b><xsl:value-of select="gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString"/>: </b>
			  			<xsl:value-of select="gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
			  		</p>
		  		</xsl:otherwise>
		  	</xsl:choose>
  		</xsl:variable>
  		<xsl:for-each select="../../../../../gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString">
  			<xsl:if test = ". = 'Downloadable data'">
	  			<xsl:copy-of select="$url"/>
	  		</xsl:if>
  		</xsl:for-each>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:edition/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Versie: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Unieke identifier: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:aggregationInfo/gmd:MD_AggregateInformation/gmd:aggregateDataSetName/gmd:CI_Citation">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Gerelateerde dataset: </xsl:text></b>
		  		<xsl:value-of select="gmd:title/gco:CharacterString/."/>,
		  		<xsl:value-of select="gmd:date/gmd:CI_Date/gmd:date/gco:Date/."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:topicCategory/gmd:MD_TopicCategoryCode">
 		<xsl:if test=". != ''">
	 		<xsl:choose>
		 		<xsl:when test=". = 'farming'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Landbouw en veeteelt</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'biota'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Biota</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'boundaries'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Grenzen</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'climatologyMeteorologyAtmosphere'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Klimatologie, meteorologie atmosfeer</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'economy'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Economie</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'elevation'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Hoogte</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'environment'">
					<p>
						<b>Onderwerp: </b>
						<xsl:text>Natuur en milieu</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'geoscientificInformation'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Geowetenschappelijke data</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'health'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Gezondheid</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'imageryBaseMapsEarthCover'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Referentie materiaal aardbedekking</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'intelligenceMilitary'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Militair</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'inlandWaters'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Binnenwater</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'location'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Locatie</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'oceans'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Oceanen</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'planningCadastre'">
					<p>
						<b>Onderwerp: </b>
						<xsl:text>Ruimtelijke ordening en kadaster</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'society'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Maatschappij</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'structure'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>(Civiele) structuren</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'transportation'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Transport</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:when test=". = 'utilitiesCommunication'">
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:text>Nutsbedrijven communicatie</xsl:text>
		  			</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
		  				<b>Onderwerp: </b>
		  				<xsl:value-of select="."/>
		  			</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
	  			<b><xsl:text>Trefwoord: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
	  	</xsl:if>
  	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation">
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
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints[position() > 1]">
 		<xsl:if test="gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString != ''">
	  		<p>
		  		<b><xsl:text>Gebruiksbeperkingen: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue">
 		<xsl:if test=". != ''">
	  		<xsl:choose>
	  			<xsl:when test=". = 'copyright'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Copyright</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'patent'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Patent</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'patentPending'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Patent in wording</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'trademark'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Merknaam</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'license'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Licentie</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'intellectualPropertyRights'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Intellectueel</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'restricted'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Niet toegankelijk</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:when test=". = 'otherRestrictions'">
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:text>Anders</xsl:text>
			  		</p>
	  			</xsl:when>
	  			<xsl:otherwise>
	  				<p>
				  		<b><xsl:text>(Juridische) gebruiksrestricties: </xsl:text></b>
				  		<xsl:value-of select="."/>
			  		</p>
	  			</xsl:otherwise>
	  		</xsl:choose>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue">
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
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString">
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
  	<xsl:template match="gmd:MD_Metadata/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Metadata karakterset: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
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
				<xsl:otherwise>
					<p>
						<b><xsl:text>Metadata hiërarchieniveau: </xsl:text></b>
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
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:language/gmd:LanguageCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<xsl:choose>
	  			<xsl:when test=". = 'dut'">
	  				<p>
		  				<b><xsl:text>Taal van de bron: </xsl:text></b>
		  				<xsl:text>Nederlands</xsl:text>
		  			</p>
	  			</xsl:when>
	  			<xsl:otherwise>
					<p>
						<b><xsl:text>Taal van de bron: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
	  		</xsl:choose>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Karakterset van de bron: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
	  	</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:fees/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
	  			<b><xsl:text>Prijsinformatie: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
	  			<b><xsl:text>Orderprocedure: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:turnaround/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
	  			<b><xsl:text>Doorlooptijd orderprocedure: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:unitsOfDistribution/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
	  			<b><xsl:text>Leverings-/gebruikseenheid: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue">
  		<xsl:if test=". != ''">
	  		<p>
	  			<b><xsl:text>Naam medium: </xsl:text></b>
		  		<xsl:value-of select="."/>
	  		</p>
  		</xsl:if>
  	</xsl:template>
  	
  	<xsl:template match="gmd:MD_Metadata/gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Algemene beschrijving herkomst: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceSpecificUsage/gmd:MD_Usage/gmd:specificUsage/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Potentieel gebruik: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_CompletenessOmission/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Volledigheid: </xsl:text></b>
		  		<xsl:value-of select="."/>
		  	</p>
		</xsl:if>
  	</xsl:template>
  	<xsl:template match="gmd:MD_Metadata/gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_AbsoluteExternalPositionalAccuracy/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record">
  		<xsl:if test=". != ''">
	  		<p>
		  		<b><xsl:text>Geometrische nauwkeurigheid: </xsl:text></b>
		  		<xsl:value-of select="."/>
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
	  			<xsl:otherwise>
					<p>
						<b><xsl:text>Niveau kwaliteitbeschrijving: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
	  		</xsl:choose>
	  	</xsl:if>
  	</xsl:template>
	<xsl:template match="gmd:MD_Metadata/gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description/gco:CharacterString">
  		<xsl:if test=". != ''">
	  		<div class="blok">
		  		<b><p>Uitgevoerde bewerkingen</p></b>
		  		<p class="inspringen">
		  			<b><xsl:text>Beschrijving: </xsl:text></b>
		  			<xsl:value-of select="."/>
		  		</p>
		  	</div>
	  	</xsl:if>
  	</xsl:template>
</xsl:stylesheet>