<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  	xmlns:dc="http://purl.org/dc/elements/1.1/" 
  	xmlns:dcmiBox="http://dublincore.org/documents/2000/07/11/dcmi-box/" 
  	xmlns:dcterms="http://purl.org/dc/terms/" 
  	xmlns:ows="http://www.opengis.net/ows" version="1.0">
  	
  	<xsl:output method="html" version="4.0" encoding="iso-8859-1" indent="yes" omit-xml-declaration="yes"/>
  
	<xsl:template match="/">
	  	<html>
	  		<head>
				<title>
					<xsl:apply-templates select="rdf:RDF/rdf:Description/dc:title"/>
				</title>
				<style>
				 	html {
	    				overflow-y:scroll;
	    				overflow-x:hidden;
					}
					body {
					    background-color:#FFFFFF;
					    margin:0px auto 20px auto;
					    font-family:Verdana;
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
						background-color:#831625;
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
					.toptaak-inner {height:100%;background-color:#255c9f;}
					.toptaak-inner-active {height:100%;background-color:#0096D2;}
					.toptaak-inner:hover {background-color:#0096D2;cursor:pointer;}
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
					<h1 class="titel">Beschrijving statische kaarten Geoportaal</h1>
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
	<xsl:template match="rdf:RDF">
		<div id="titel">
			<div class="blok">
				<p class="bestandsnaam">
					<b><xsl:text>Naam statische kaart: </xsl:text></b>
					<xsl:apply-templates select="rdf:Description/dc:title"/>
				</p>
			</div>
		</div>
		<div id="wat">
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:description"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dcterms:relation"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:relation"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:rights[@rdf:datatype='gebruiksrestricties'][1]"/>
			</div>
		</div>
		<div id="wie">
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:creator"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:publisher"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:source"/>
			</div>
		</div>
		<div id="waar">
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dcterms:spatial"/>
			</div>
			<div class="blok">
				<p>
					<xsl:apply-templates select="rdf:Description/ows:WGS84BoundingBox"/>
				</p>
			</div>
		</div>
		<div id="wanneer">
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:date"/>
				<xsl:apply-templates select="rdf:Description/dcterms:issued"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dcterms:valid"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dcterms:temporal"/>
			</div>
		</div>
		<div id="details">
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:identifier"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dcterms:references"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:subject"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:rights"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dcterms:coverage"/>
			</div>
			<div class="blok">
				<xsl:apply-templates select="rdf:Description/dc:format"/>
				<xsl:apply-templates select="rdf:Description/dc:language"/>
				<xsl:apply-templates select="rdf:Description/dc:type"/>
			</div>
			
			
			<div class="proclaimer">
				<p>Deze gegevens worden beschikbaar gesteld door het Geoportaal van Overijssel: 
					<a target="_blank" href="http://www.geoportaaloverijssel.nl">http://www.geoportaaloverijssel.nl</a>
				</p>
				<p>In het Geoportaal staan actuele kaarten en beschrijvingen van die kaarten.</p>
				<p>Ter referentie zijn vaak ook nog oudere kaarten beschikbaar gesteld.</p>
				<p>De provincie Overijssel stelt zoveel mogelijk kaarten als "open data" voor iedereen beschikbaar.</p>
				<p>Heeft u suggesties of vragen? Stuur dan een email naar <a href="mailto:beleidsinformatie@overijssel.nl">beleidsinformatie@overijssel.nl</a></p>
				<p>Zie proclaimer: 
					<a target="_blank" href="http://www.overijssel.nl/algemene-onderdelen/proclaimer">http://www.overijssel.nl/algemene-onderdelen/proclaimer</a>
				</p>
			</div>
		</div>
		<!-- <xsl:apply-templates select="gmd:fileIdentifier"/> -->
 	</xsl:template>
 	<xsl:template match="rdf:RDF/rdf:Description/dc:title">
  		<xsl:if test=". != ''">
  			<xsl:value-of select="."/>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:description">
  		<xsl:if test=". != ''">
  			<b><xsl:text>Beschrijving: </xsl:text></b>
  			<xsl:value-of select="."/>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:rights">
  		<xsl:if test=". != ''">
  			<p>
	  			<b><xsl:text>Eigendomsrechten: </xsl:text></b>
	  			<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:rights[@rdf:datatype='gebruiksrestricties']">
  		<xsl:if test=". != ''">
  			<p>
	  			<b><xsl:text>Gebruiksrestricties: </xsl:text></b>
	  			<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:creator">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Eindverantwoordelijke: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:publisher">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Publicerende organisatie: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:source">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Bron: </xsl:text></b>
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
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:spatial">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Beschrijving geografisch gebied: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/ows:WGS84BoundingBox">
  		<xsl:if test="ows:LowerCorner != ''">
 			<b><xsl:text>Omgrenzende rechthoek</xsl:text></b>
 			<div class="blok">
	 			<p>
	 				<b><xsl:text>Coördinaten rechtsonder: </xsl:text></b>
	  				<xsl:value-of select="ows:LowerCorner"/>
	  			</p>
	  			<xsl:if test="ows:UpperCorner != ''">
		 			<p>
		 				<b><xsl:text>Coördinaten linksboven: </xsl:text></b>
		  				<xsl:value-of select="ows:UpperCorner"/>
		  			</p>
		  		</xsl:if>
		  	</div>
  			<p>
 				<b><xsl:text>Code referentiesysteem: </xsl:text></b>
 				<xsl:text>84</xsl:text>
 			</p>
 			<p>
 				<b><xsl:text>Organisatie referentiesysteem: </xsl:text></b>
 				<xsl:text>WGS</xsl:text>
 			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:date">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Datum creatie: </xsl:text></b>
  				<xsl:value-of select="substring(.,9,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(.,6,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(.,1,4)"/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:issued">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Datum publicatie: </xsl:text></b>
  				<xsl:value-of select="substring(.,9,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(.,6,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(.,1,4)"/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:valid">
  		<xsl:if test="start != ''">
 			<p>
 				<b><xsl:text>Datum geldig, van: </xsl:text></b>
  				<xsl:value-of select="substring(start,9,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(start,6,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(start,1,4)"/>
  			</p>
  		</xsl:if>
  		<xsl:if test="end != ''">
 			<p>
 				<b><xsl:text>Datum geldig, tot: </xsl:text></b>
  				<xsl:value-of select="substring(end,9,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(end,6,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(end,1,4)"/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:temporal">
  		<xsl:if test="start != ''">
 			<p>
 				<b><xsl:text>Dekking in tijd, van: </xsl:text></b>
  				<xsl:value-of select="substring(start,9,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(start,6,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(start,1,4)"/>
  			</p>
  		</xsl:if>
  		<xsl:if test="end != ''">
 			<p>
 				<b><xsl:text>Dekking in tijd, tot: </xsl:text></b>
  				<xsl:value-of select="substring(end,9,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(end,6,2)"/>
  				<xsl:text>-</xsl:text>
  				<xsl:value-of select="substring(end,1,4)"/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:identifier">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Identificatie: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:references">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Locatie: </xsl:text></b>
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
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:relation">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Nummer: </xsl:text></b>
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
  	<xsl:template match="rdf:RDF/rdf:Description/dc:subject">
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
  	<xsl:template match="rdf:RDF/rdf:Description/dcterms:coverage">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Dekking: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:format">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Formaat: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:language">
  		<xsl:if test=". != ''">
 			<xsl:choose>
		  		<xsl:when test=". = 'dut'">
					<p>
						<b><xsl:text>Taal: </xsl:text></b>
						<xsl:text>Nederlands</xsl:text>
					</p>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<b><xsl:text>Taal: </xsl:text></b>
						<xsl:value-of select="."/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:type">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Type: </xsl:text></b>
  				<xsl:value-of select="."/>
  			</p>
  		</xsl:if>
  	</xsl:template>
  	<xsl:template match="rdf:RDF/rdf:Description/dc:relation">
  		<xsl:if test=". != ''">
 			<p>
 				<b><xsl:text>Bijlage: </xsl:text></b>
  				<xsl:choose>
					<xsl:when test="substring(substring-after(.,'http'),1,3) = '://'">
						<xsl:value-of select="substring-before(.,'http://')"/>
						<xsl:text> </xsl:text>
						<a href="http://{substring-after(.,'http://')}" target="_blank">
							http://<xsl:value-of select="substring-after(.,'http://')"/>
						</a>
					</xsl:when>
					<xsl:when test="substring(substring-after(.,'https'),1,3) = '://'">
						<xsl:value-of select="substring-before(.,'https://')"/>
						<xsl:text> </xsl:text>
						<a href="https://{substring-after(.,'https://')}" target="_blank">
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
</xsl:stylesheet>