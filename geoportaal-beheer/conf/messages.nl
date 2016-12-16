# Algemeen
login=Inloggen
loginhelp=Help inloggen
logout=Uitloggen
save=Opslaan
confirm.save=Toch opslaan
confirm=Bevestigen
cancel=Annuleren
execute=Uitvoeren
start=Start
unauthorized=Geen toegang
application.name=Geoportaal-Beheer

# Query
tsv.language=dutch

# Tabbladen
main.tab.index=Zoeken en wijzigen
main.tab.add=Toevoegen
main.tab.report=Rapportage
main.tab.help=Help

# Index
index.header=Geoportaal: zoeken en wijzigen metadata
index.intro=Hier kunt u naar metadata records zoeken en veranderingen doorvoeren.
index.text.label=Tekst
index.text.title=Zoek op een woord
index.supplier.label=Leverancier
index.supplier.title=Filter op leverancier
index.status.label=Status
index.status.title=Filter op status
index.date.create.start.label=Creatie datum tussen
index.date.create.start.title=Zoek op begindatum
index.date.create.end.label=en
index.date.create.end.title=Zoek op einddatum
index.date.update.start.label=Update datum tussen
index.date.update.start.title=Zoek op begindatum
index.date.update.end.label=en
index.date.update.end.title=Zoek op einddatum
index.button.search=Zoek
index.button.filter.clear=Filter opheffen
index.records.count.label=Aantal records:
index.records.selected.label=Aantal geselecteerde records:
index.button.action.label=Acties voor geselecteerde records:
index.button.action.concept=Concept
index.button.action.approval=Ter goedkeuring
index.button.action.publish=Publiceren
index.button.action.delete=Verwijderen naar prullenbak
index.button.action.supplier.edit=Leverancier wijzigen
index.warning.published.record=U heeft 1 of meerdere records geselecteerd, die reeds zijn gepubliceerd. U kunt hiervan niet de status veranderen. Wilt u toch de status veranderen, neem dan contact op met de beheerders van het Geoportaal.
index.warning.attachment.skipped=U heeft geprobeerd een metadata record op te slaan met bijlages die dezelfde naam hebben. Alleen de eerste is toegevoegd.

# Index modals
index.modal.status.title=Wijzigen status statische kaarten
index.modal.delete.title=Verwijderen statische kaarten
index.modal.delete.definitive=Definitief verwijderen
index.modal.delete.selected=Deze actie heeft betrekking op {0} {1}. 
index.modal.none.selected=U heeft geen records geselecteerd. Deze actie kan niet worden uitgevoerd.
index.modal.supplier.title=Wijzigen leverancier statische kaarten
index.modal.action.label=Aantal record(s) waar deze actie betrekking op heeft:
index.modal.confirmation=Weet u zeker dat u deze actie wilt uitvoeren?

# Formulier labels en teksten
form.create.header=Geoportaal: toevoegen metadata
form.edit.header=Geoportaal: wijzigen metadata
form.intro=Vul de velden zo volledig mogelijk in (* = verplicht)
form.title.label=Titel*
form.title.title=Vul de titel in
form.description.label=Omschrijving*
form.description.title=Vul de omschrijving in
form.location.label=Locatie*
form.location.title=Vul het pad naar het bestand op de computer in
form.fileid.label=Nummer*
form.fileid.title=Vul het nummer in
form.id.label=Unieke ID
form.id.title=De unieke ID van de data
form.id.value.create=De id zal bij het opslaan gegenereerd worden...
form.attachment.label=Bijlage
form.attachment.title=Voeg de bijlage(s) toe
form.attachment.button.add=Bijlage toevoegen
form.attachment.button.empty=Bijlage leegmaken
form.attachment.button.delete=Bijlage verwijderen
form.typeinformation.label=Type informatie
form.typeinformation.title=Selecteer het type informatie
form.creator.label=Eindverantwoordelijke*
form.creator.title=Selecteer de eindverantwoordelijke
form.creator.other.title=Voer eindverantwoordelijke van bestand in
form.rights.label=Eigendomsrechten
form.rights.title=Selecteer de eigendomsrechten
form.uselimitation.label=Gebruiksrestricties*
form.uselimitation.title=Selecteer de gebruiksrestrictie
form.format.label=Formaat applicatie
form.format.title=Selecteer het formaat van de data
form.source.label=Bron (leverancier)
form.source.title=Vul de persoon of organisatie in waar de bron vandaan komt
form.date.create.label=Datum creatie*
form.date.create.title=Vul de datum in wanneer de metadata gemaakt is
form.date.publication.label=Datum publicatie
form.date.publication.title=Vul de datum in wanneer de metadata gepubliceerd is
form.date.valid.from.label=Datum geldig, van
form.date.valid.from.title=Vul de datum in vanaf wanneer de bron geldig is
form.date.valid.end.label=Datum geldig, tot
form.date.valid.end.title=Vul de datum in tot wanneer de bron geldig is
form.subject.label=ISO onderwerp*
form.subject.title=Vink de onderwerpen aan die van toepassing zijn op de data

# Tooltips index
index.text.tooltip=Tekst in die je wilt zoeken. Meerdere woorden zijn mogelijk. Vul je niets in, dan wordt alles gekozen als je gaat zoeken.
index.supplier.tooltip=Leverancier van de data, waarmee de kaart is gemaakt. Vul je niets in, dan wordt alles gekozen als je gaat zoeken.
index.status.tooltip=Status. Vul je niets in, dan wordt alles gekozen als je gaat zoeken.
index.date.update.start.tooltip=Datum waarop het bestand is vernieuwd. Kies de begindatum. Vul je niets in, dan wordt alles gekozen als je gaat zoeken.
index.date.update.end.tooltip=Datum waarop het bestand is vernieuwd. Kies de einddatum. Vul je niets in, dan wordt alles gekozen als je gaat zoeken.

# Tooltips formulier
form.title.tooltip=Titel. Hou het beknopt maar geef wel alle belangrijke informatie, zoals jaartal. Geef geen overbodige woorden. Goed: Inventarisatie Korhoenders Salland 2012. Minder goed: De inventarisatie van Korhoenders in Overijssel. Tekstveld kan maximaal 200 tekens bevatten.
form.description.tooltip=Omschrijving. Probeer de data te omschrijven voor iemand die deze data niet kent. Gebruik minstens 2 zinnnen gewoon Nederlandse tekst. Tekstveld kan maximaal 100.000 tekens bevatten.
form.location.tooltip=Vul hier het pad in. Goed: K:\BAB\Geo-informatie\2015\ . Niet goed: Op C:\temp\ . Tekstveld kan maximaal 2000 tekens bevatten.
form.fileid.tooltip=Nummer van de dataset. Gebruik hier alleen het nummer. Geen volgnummers of andere code. Goed: 150047. Niet goed: 150047_Salland. Tekstveld kan maximaal 200 tekens bevatten.
form.id.tooltip=Unieke code. Hier hoef je niets in te vullen. Dit veld wordt automatisch gevuld.
form.attachment.tooltip=Koppel hier de bijlage (meestal een pdf). Meer bijlagen nodig? Klik op de PLUS. Zie je een OOG, dan kan je de bijlage bekijken. Bestanden die als bijlage zijn gekoppeld, die kan je verwijderen met het x-je. Heb je nog geen bestand gekoppeld, dan kan je het gommetje gebruiken. De maximale grootte is 200 MB. Dit geldt voor individuele bestanden. Je kan 1 bestand van 200 MB aanbieden, of 2 bestanden van 100 MB. Wil je nog meer data toevoegen, dan kan je dit record opslaan en daarna wijzigen. Je kan dan weer 200 MB maximaal toevoegen.
form.typeinformation.tooltip=Type informatie.
form.creator.tooltip=Organisatie die de data heeft gemaakt. Dit is de leverancier van de achterliggende data. Dat hoeft niet dezelfde organisatie te zijn als de organisatie die de kaart heeft gemaakt. Tekstveld bij selectie ''Andere'' kan maximaal 200 tekens bevatten.
form.rights.tooltip=Eigendomsrechten, die voor de data gelden.
form.uselimitation.tooltip=Beperkingen (intern of ook extern). Bij twijfel, kies intern of vraag het na bij de leverancier. Kies je voor extern dan wordt alle informatie ook extern zichtbaar. 
form.format.tooltip=Format van de oorspronkelijke dataset. MXD: meestal bij kaarten die met Arcgis zijn gemaakt. Adobe Illustrator meestal bij handgetekende kaarten.
form.source.tooltip=Leverancier van de data van de kaart. Tekstveld kan maximaal 200 tekens bevatten.
form.date.creation.tooltip=Datum waarop de kaart gemaakt is. Standaard is de huidige datum alvast ingevuld. Je kan deze datum ook aanpassen, indien relevant.
form.date.publication.tooltip=Datum waarop de kaart is gepubliceerd. 
form.date.valid.start.tooltip=Geldigheidsdatum: begindatum.
form.date.valid.end.tooltip=Geldigheidsdatum: einddatum.
form.subject.tooltip=Onderwerp. Onderwerpen waar de kaart bij hoort. Je kan meerdere onderwerpen kiezen, als de kaart bij meerdere onderwerpen hoort.

# Inloggen
login.header=Inloggen beheermodule Geoportaal
login.intro=Vul onderstaande velden in om in te loggen.
login.form.error.label=Inloggen mislukt
login.email.label=E-mailadres
login.password.label=Wachtwoord
login.button.password.edit=Wachtwoord wijzigen
login.button.password.forgot=Wachtwoord vergeten
login.button.help=Help
login.error.message=Ongeldige gebruikersnaam of wachtwoord
login.help.header=Help inloggen beheermodule Geoportaal
login.help.paragraph.1=Toelichting bij inloggen in het Geoportaal.
login.help.paragraph.2=Je kan, als je hiervoor de rechten hebt, inloggen in het Geoportaal. Je kan daarna je eigen (statische) kaarten aanbieden. Heb je een nieuwe kaart, neem deze dan op in het Geoportaal door hier in te loggen.
login.help.paragraph.3=Gebruik bij het inloggen je werk emailadres. Je mag zowel hoofdletters als kleine letters gebruiken. <a class="link" href="mailto:p.pietersen@werk.nl">p.pietersen@werk.nl</a> is goed. En ook <a class="link" href="mailto:P.Pietersen@Werk.nl">P.Pietersen@Werk.nl</a> is goed.
login.help.paragraph.4=Ben je je wachtwoord vergeten, dan kan je een nieuw wachtwoord aanvragen. Vul dan je emailadres in bij "WACHTWOORD VERGETEN" en je krijgt in je mailbox een nieuw wachtwoord.
login.help.paragraph.5=Bij "WACHTWOORD AANPASSEN" kan je eventueel je huidige wachtwoord aanpassen. Aangeraden wordt om dat 1x per maand te doen. Gebruik hier eventueel wel hoofdletters en/of andere bijzondere tekens.

# Wachtwoord wijzigen
password.edit.header=Wachtwoord wijzigen beheermodule Geoportaal
password.edit.intro=Vul onderstaande velden in om uw wachtwoord te wijzigen.
password.edit.form.error.label=Wachtwoord wijzigen
password.edit.email.label=E-mailadres
password.edit.password.old.label=Oud wachtwoord
password.edit.password.new.label=Nieuw wachtwoord
password.edit.password.new.repeat.label=Herhaal nieuw wachtwoord
password.edit.error.incomplete.message=Alle velden moeten ingevuld zijn
password.edit.error.mismatch.message=Gebruikersnaam en wachtwoord komen niet overeen
password.edit.error.repeat.message=Nieuwe wachtwoord en herhaling komen niet overeen
password.edit.success=Het wijzigen van het wachtwoord is succesvol uitgevoerd

# Wachtwoord vergeten
password.forgot.header=Wachtwoord vergeten beheermodule Geoportaal
password.forgot.intro=Vul onderstaande velden in om uw wachtwoord te resetten.
password.forgot.form.error.label=Wachtwoord vergeten
password.forgot.email.label=E-mailadres
password.forgot.email.message=Uw wachtwoord is op verzoek gereset. Uw nieuwe wachtwoord is {0}. U wordt aangeraden om zo snel als mogelijk het wachtwoord te veranderen.
password.forgot.email.subject=Uw wachtwoord voor het geoportaal-beheer is gereset
password.forgot.success=Er is een e-mail naar het door u opgegeven adres verstuurd

# Rapportage pagina
report.header=Rapportage
report.intro=Op deze pagina kun je onderstaande rapportages opvragen.
report.type.dataset=Geodata
report.type.service=Services
report.type.dc=Statische kaarten
report.type.download=Download
report.start.info.1=De rapportage-opdracht wordt gestart.
report.start.info.2=Als deze is afgerond dan is het CSV-bestand meestal te vinden in je Download-map. Let op: afhankelijk van je browser-instellingen moet je soms nog bevestigen dat dit bestand moet worden opgeslagen. Je krijgt in dat geval de keuze tussen "Openen" of "Opslaan". Kies dan voor "Opslaan" en het bestand komt in je Download-map.
report.start.info.3=Na opslaan van de CSV kan je verder gaan met een volgende rapportage, of naar een ander onderdeel van het Geoportaal.

# Help pagina
help.header=Geoportaal: help metadata
help.paragraph.1=<b>Toelichting bij het beheer van metadata.</b>
help.paragraph.2=Bij dit onderdeel van het Geoportaal kan je je eigen kaarten toevoegen. Het betreft hier statische kaarten (pdf's meestal).
help.paragraph.3=Bij de statische kaarten zijn enkele velden verplicht. Een aantal velden is al voor je ingevuld, zoals formaat en datum creatie. Deze velden kan je ook, indien nodig, zelf weer aanpassen.
help.paragraph.4=Er zijn enkele velden verplicht (aangegeven met *). Vul je hier niets in, dan kan je de betreffende kaart niet opslaan.
help.paragraph.5=Je kan 1 of meerdere kaarten opslaan, bij 1 enkel nummer. Dat is handig omdat sommige kaarten als serie bij elkaar horen. Ze zijn dan onder 1 nummer terug te vinden.
help.paragraph.6=Bij het onderdeel TOEVOEGEN kan je kaarten aanbieden.
help.paragraph.7=Bij ZOEKEN en WIJZIGEN kan je eventueel je kaarten die je hebt aangeboden beheren. Verwijderen of opnieuw aanpassen is mogelijk. Bij dit onderdeel zie je alleen je eigen kaarten. Zolang jouw kaart een concept status heeft, kan je die zelf aanpassen. Een beheerder zal hier niets mee doen, totdat je zelf de status verandert.
help.paragraph.8=Ben je klaar met de kaart, dan bied je de kaart aan "Ter goedkeuring". De beheerders van het Geoportaal zullen dan de kaart verder oppakken en publiceren.
help.paragraph.9=Zijn er vragen over bepaalde velden, dan is er meestal extra informatie aanwezig. Achter alle invoervelden zie je een knopje met een "i". Hier is extra informatie te vinden over het betreffende invoerveld.

# Lijst
list.action.header=Actie
list.number.header=Nummer
list.title.header=Titel
list.supplier.header=Leverancier
list.status.header=Status
list.date.header=Datum
list.action.view=Metadata bekijken
list.action.edit=Bestand wijzigen

# XML
xml.uselimitation=gebruiksrestricties

# Validatie formulier
validate.form.title=De titel moet ingevuld zijn.
validate.form.description=De omschrijving moet ingevuld zijn.
validate.form.location=De locatie moet ingevuld zijn.
validate.form.fileid=Het nummer moet ingevuld zijn.
validate.form.fileid.warning.title=Waarschuwing: het nummer is niet goed ingevuld
validate.form.fileid.warning.body.occurrences=Let op: dit nummer bestaat al. Controleer goed, of het juiste nummer is ingevoerd, voordat je verder gaat. 
validate.form.fileid.warning.body.nonumber=Let op: het nummer wat je hebt ingevoerd dat bevat letters of andere vreemde tekens. Het is de bedoeling dat een nummer alleen uit cijfers bestaat. Controleer goed welk nummer je hebt ingevoerd, voordat je verder gaat.
validate.form.fileid.warning.body.length=Let op: het aantal cijfers van het nummer wat je hebt ingevoerd, dat is mogelijk niet correct. Gewone nummers zijn bijvoorbeeld: 160023 (in 2016), 150101 (in 2015) enz. Controleer of juiste nummer is ingevoerd, voordat je verder gaat. 
validate.form.creator=Er moet een eindverantwoordelijke geselecteerd zijn.
validate.form.creator.other=Bij het selecteren van ''andere'' bij eindverantwoordelijke moet het invoerveld ingevuld worden.
validate.form.date.create=De datum creatie moet ingevuld zijn.
validate.form.date.create.publish.check=Er is een onlogische datum ingevoerd. Datum "creatie" is later dan de datum "publicatie".
validate.form.date.valid.check=Er is een onlogische datum ingevoerd. Datum "geldig van" is later dan de datum "geldig tot".
validate.form.subject.list=Er moet minimaal één onderwerp aangevinkt zijn.
validate.form.parse.date.create=De datum creatie is niet correct ingevuld.
validate.form.parse.date.publication=De datum publicatie is niet correct ingevuld.
validate.form.parse.date.valid.start=De datum geldig, van is niet correct ingevuld.
validate.form.parse.date.valid.end=De datum geldig, tot is niet correct ingevuld.

# Zoek validatie
validate.create.date.start=De creatie datum, van is niet correct ingevuld.
validate.create.date.end=De creatie datum, tot is niet correct ingevuld.
validate.update.date.start=De update datum, van is niet correct ingevuld.
validate.update.date.end=De update datum, tot is niet correct ingevuld.
validate.search.generic=Er is iets misgegaan. Controleer of de velden correct zijn ingevuld.