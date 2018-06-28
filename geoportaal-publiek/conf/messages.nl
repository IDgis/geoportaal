unauthorized=Geen toegang

button.search=Zoek
button.help=Help
button.metadata=Meer informatie
button.xml=XML
button.download=Download
button.viewer=Bekijk in viewer
button.first=Eerste pagina
button.previous=Vorige pagina
button.next=Volgende pagina
button.last=Laatste pagina
button.admin=Beheer
search.label=Zoek
search.placeholder=Zoek op term
result.count.label.zero=Geen resultaat
result.count.label.one=resultaat
result.count.label.multiple=resultaten
results.expand=Resultaten uitklappen

tsv.language=dutch

metadata.type.dataset=GIS bestanden
metadata.type.service=Services
metadata.type.dc=Statische kaarten

metadata.sort.dataset=Sorteer op datum van dataset
metadata.sort.description=Sorteer op datum van beschrijving
metadata.date.none=geen datum bekend
metadata.description.none=Geen omschrijving beschikbaar.

tab.index=Home
tab.search=Uitgebreid zoeken
tab.browse=Bladeren
tab.about=Over deze site
tab.contact=Contact

index.files.info.recent=Vijf meest recente bestanden:

search.header=Uitgebreid zoeken
search.header.intern=Intern: Uitgebreid zoeken
search.intro=Zoek naar kaarten. Maak eventueel eerst een selectie: GIS bestanden, services of statische kaarten.
search.checkboxes.label=Zoeken naar

browse.header=Bladeren
browse.header.intern=Intern: Bladeren
browse.intro=Zoek naar kaarten en maak eventueel gebruik van één of meerdere trefwoorden.
browse.subject.list.label=Trefwoorden (groepen) waar je uit kan kiezen:
browse.button.select.all=Alles selecteren
browse.button.select.nothing=Niets selecteren

about.header=Over deze site
about.header.intern=Intern: Over deze site 
about.paragraph.1.2=Een groot aantal datasets wordt vrij beschikbaar gesteld, zodat ook anderen ervan gebruik kunnen maken.
about.paragraph.1.3=Voor de geodatasets en de services heb je speciale software nodig. Hiervoor wordt de verzamelnaam "GIS" gebruikt (geografisch informatiesysteem). Een veel gebruikt GIS is het systeem ARCGIS van het bedrijf ESRI. Maar er zijn ook "open source" GIS systemen. Deze zijn veelal gratis. Een veelgebruikt gratis GIS is bijvoorbeeld QGIS.
about.paragraph.1.4=De datasets zijn veelal te downloaden, zodat je ze met een GIS verder kan gebruiken.
about.paragraph.1.5=Voor de andere kaarten (statische kaarten) heb je geen GIS nodig. Dit zijn plaatjes, meestal in de vorm van een PDF. Je kan dit soort bestanden bekijken met bijvoorbeeld Adobe reader.
about.paragraph.header.2=Tips voor specialisten:
about.paragraph.2.1=Er worden ook WMS en WFS services via het Geoportaal ontsloten. Deze kun je direct in je eigen GIS applicatie gebruiken. Maak je je eigen app en wil je Geojson gebruiken, dan kan dat ook. Gebruik hiervoor outputformat=json als je een Getfeature request doet op een WFS. Meer tips over Geojson zijn te vinden via onderstaande links:
about.paragraph.header.3=WFS JSON als output format:
about.paragraph.3.1=GML is voor veel webontwikkelaars niet de eerste keus. JSON en GeoJSON zijn laagdrempeliger waardoor veel webontwikkelaars daar de voorkeur aan geven. Gelukkig is het GeoJSON formaat ook beschikbaar bij de WFSen die GeoPublisher aanbiedt. Gebruik daarvoor de parameter outputformat=json bij een GetFeature request en je krijgt GeoJSON terug.
about.paragraph.3.2=Voorbeeld (voor Firefox, Chrome, Safari en Opera, niet geschikt voor IE):
about.paragraph.3.3=<a class="link" target="_blank" href="http://services.geodataoverijssel.nl/geoserver/B51_recreatie/wfs?service=WFS&version=2.0.0&request=GetFeature&typename=B51_recreatie:B5_Fietsroutes_in_Overijssel&count=100&startindex=1&outputformat=json">Fietsroutes in GeoJSON</a>
about.paragraph.3.4=Een GeoPublisher WFS steunt nog meer formaten, zoals gmp, shp, dxf, kml en csv. Zie voor een volledige opsomming het element 'ows:AllowedValues' onder 'ows:Parameter name="outputFormat"' in de <a class="link" target="_blank" href="http://services.geodataoverijssel.nl/geoserver/B51_recreatie/wfs?request=GetCapabilities">Capabilities van de WFS voor bijv. Recreatie</a>
about.paragraph.3.5=Van een aantal datasets zijn wij geen eigenaar. Meestal zijn hierbij dan beperkingen voordat ze gedeeld mogen worden met anderen. Deze beperkingen staan ook in de beschrijving bij de datasets.
about.paragraph.header.4=Over metadata:
about.paragraph.4.1=“Metadata” is de term die bij kaarten vaak wordt gebruikt voor de beschrijving van kaarten. Om het delen van gegevens makkelijker te maken, zijn standaarden gemaakt voor de beschrijving van kaarten. In technische termen: ISO19115 en ISO19119. Deze standaarden worden beheerd door een onafhankelijke organisatie (Geonovum: <a class="link" href="http://www.geonovum.nl" target="_blank">http://www.geonovum.nl</a>).
about.paragraph.header.5=Over downloaden:
about.paragraph.5.1=De bestanden kan je in veel gevallen op je eigen pc opslaan. Dat is handig, omdat je dan zelf de data verder kan bewerken, analyseren of weergeven. Precies zoals jij dat wilt, voor je eigen doeleinden.
about.paragraph.5.2=Gebruik hiervoor de knop “download”. Voor gebruik van dit soort gegevens is speciale software nodig. Dat wordt vaak een GIS genoemd (Geografisch Informatie Systeem). Veel gebruikte systemen zijn Arcgis en QGis. Voor meer informatie over dit soort systemen, zie: <a class="link" href="https://nl.wikipedia.org/wiki/Geografisch_informatiesysteem" target="_blank">https://nl.wikipedia.org/wiki/Geografisch_informatiesysteem</a>.
about.paragraph.5.3=Bij de download kan je kiezen uit verschillend formaten:
about.paragraph.5.3.option.1=<b>SHP</b> (shape, veel gebruikt open formaat, ontwikkeld door Esri)
about.paragraph.5.3.option.2=<b>CSV</b> (bestand waarbij de gegevens door komma's zijn gescheiden)
about.paragraph.5.3.option.3=<b>DXF</b> (Drawing exchange format, veel gebruikt formaat voor AutoCad)
about.paragraph.5.3.option.4=<b>GeoJson</b> (formaat, eenvoudig geschikt voor ontwikkelaars die JavaScript hanteren)
about.paragraph.5.3.option.5=<b>GML</b> (Geography Markup Language, een open standaard voor GIS data)
about.paragraph.5.3.option.6=<b>KML</b> (Keyhole Markup Language, te gebruiken in Google Earth)
about.paragraph.5.4=Door meerdere formaten beschikbaar te stellen, kan je zelf kiezen welk bestandsformaat het beste past bij de software die jij zelf wilt gebruiken.
about.paragraph.5.5=Heb je niet de beschikking over dergelijke GIS-software, maar wil je toch kaarten zien? Dat kan, omdat er ook veel pdf-bestanden beschikbaar gesteld worden. Zie hiervoor verder bij de toelichting over de “statische kaarten”.
about.paragraph.header.6=Over statische kaarten:
about.paragraph.6.1=Naast bestanden en services kan je ook “statische kaarten” vinden in het Geoportaal. Dat zijn kaarten (meestal pdf’s) die gemaakt zijn op basis van de bestanden en services. Deze kaarten worden speciaal gemaakt voor diverse communicatie-doeleinden.
about.paragraph.6.2=Je kan dit soort kaarten eenvoudig bekijken. Je hebt daarvoor geen geavanceerd GIS systeem voor nodig. Een pdf kan je openen met een PDF reader. Deze readers worden gratis beschikbaar gesteld, bijvoorbeeld door het bedrijf Adobe (<a class="link" href="http://www.adobe.nl" target="_blank">http://www.adobe.nl</a>).  De metadata van deze statische kaarten wordt ontsloten via de Dublin-core standaard.

contact.header=Contact
contact.header.intern=Intern: Contact
contact.paragraph.2=Heb je vragen, suggesties of tips, aarzel dan niet contact op te nemen.

help.header=Help
help.header.intern=Intern: Help
help.paragraph.header.1=Handleiding voor het gebruik van het Geoportaal
help.paragraph.1.1=Het Geoportaal is bedoeld voor het beheer en het beschikbaar stellen van kaarten.
help.paragraph.1.2=Dat soort bestanden worden ook wel ruimtelijke bestanden of “geodata” genoemd. Niet alleen de bestanden zelf, maar ook de beschrijvingen bij de bestanden kan je bekijken.
help.paragraph.header.2=Eenvoudig zoeken:
help.paragraph.2.1=Zoeken in het Geoportaal is heel eenvoudig, Je typt een woord in, en je drukt op "<b>zoek</b>". Je krijgt dan de lijst met zoekresultaten. Als er meer dan 10 resultaten zijn, dan worden die op meerdere pagina’s getoond. Je kan dan met de knop “<b>volgende</b>” en “<b>vorige</b>” tussen de pagina’s heen en weer bladeren. “<b>Uitklappen</b>” van de zoekresultaten toont de eerste paar regels van de beschrijving bij de zoekresultaten.
help.paragraph.header.3=Tips bij het zoeken:
help.paragraph.3.1=Kan je niet snel vinden wat je zoekt, probeer dan eerst eens een term die lijkt op de termen die je al gebruikt. Krijg je teveel zoekresultaten, probeer dan meerdere woorden te zoeken. Het zoekcriterium is “AND”. Dat wil zeggen dat je bij ieder extra woord je zoekresultaten zult beperken.
help.paragraph.3.2=Zoek je bijvoorbeeld “natuur” dan vind je honderden resultaten. Zoek je “natuur bos” dan vind je er al veel minder. Zoeken met wildcards (* of ?) is niet mogelijk. Zoeken naar een deel van een woord echter wel. Zoek je “natuur” dan krijg je ook resultaten waarin het woord “natuurgebied” in voorkomt.
help.paragraph.header.4=Over “Uitgebreid zoeken”:
help.paragraph.4.1=Wil je op een andere manier zoeken dan is dat ook mogelijk. Bij het onderdeel “<b>Uitgebreid zoeken</b>” kan je selecteren of je door alle informatie heen wilt zoeken, of dat je je wilt beperken tot bestanden (geodata), services of statische kaarten. Ook kan je besluiten bij dat onderdeel om de zoekresultaten “<b>uit te klappen</b>”, wat soms ook kan helpen bij het vinden wat je zoekt.
help.paragraph.header.5=Over “Bladeren”:
help.paragraph.5.1=Bij het onderdeel “<b>Bladeren</b>” is de mogelijkheid om te zoeken op trefwoorden. Wil je alles vinden over “economie”, zet dan alleen een vinkje voor het trefwoord “economie”. Wil je alles vinden over “economie” en ook over “flora en fauna”, dan zet je 2 vinkjes bij de betreffende trefwoorden.
help.paragraph.5.2=Zowel bij “Uitgebreid zoeken” als bij “Bladeren” kan je ook nog “gewoon” zoeken op woorden. Dat zorgt ervoor dat jij precies zo kan zoeken als je zelf het makkelijkst vind.
help.paragraph.6.1=Meer informatie over het Geoportaal, downloaden, bestanden en metadata vindt u bij het onderdeel <a class="link" href="/about" target="_blank">Over deze site</a>.
help.paragraph.header.7=Meer hulp nodig?
help.paragraph.7.1=Wij streven ernaar om de informatie zo goed mogelijk beschikbaar te stellen. We streven naar actualiteit en juistheid bij al onze beschikbare bestanden. En we horen ook graag hoe het nog beter zou kunnen.
help.paragraph.7.2=Heb je vragen over het Geoportaal? Of heb je tips of suggesties? Of wil je op de hoogte blijven van de laatste ontwikkelingen?