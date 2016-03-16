usage: conversion [-h] -xmlDir XMLDIR [-csvDir CSVDIR] [-username USERNAME]
                  [-password PASSWORD] [-servername SERVERNAME]
                  [-port PORT] [-database DATABASE] [-schema SCHEMA]

verplicht:
  -xmlDir XMLDIR         map met XML-bestanden

optioneel:
  -h, --help             toon hulp
  -csvDir CSVDIR         map waar  CSV-bestand  met  resultaten  naar wordt geschreven (default: 'csvfiles')
  -username USERNAME     gebruikersnaam om in te loggen op de databaseserver
  -password PASSWORD     wachtwoord om in te loggen op de databaseserver
  -servername SERVERNAME server   waarop   de    database    zich   bevindt (bijvoorbeeld 'localhost')
  -port PORT             poortnummer van de server
  -database DATABASE     naam van de database
  -schema SCHEMA         database schema indien niet public

voor schrijven naar csv: CSVDIR invullen
voor inserten in database: USERNAME, PASSWORD, SERVERNAME, PORT, DATABASE en eventueel SCHEMA invullen