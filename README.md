# bisis2json

## Build projekta

Za build projekta koristi se Gradle.

```
$ gradle build
```

Rezultat ce biti pet jar- ova u `build/libs` folderu:

* `bisis2json-export-records.jar` za eksport zapisa
* `bisis2json-export-users.jar` za eksport cirkulacije
* `bisis2json-export-lendings.jar` za eksport cirkulacije
* `bisis2json-export-client-config.jar` za eksport cirkulacije
* `bisis2json-export-client-coders.jar` za eksport šifarnika
# Eksport - import moguće izvršiti na 2 načina

## Korišćenjem skripte (Windows only)
Pokrenuti `bisis_export_import_tool.cmd` ispratiti korake, parametri:
* Kod biblioteke(na pr. gbns, gbsa...)
* Prefiks biblioteke(na pr. gbns_com, gbsa_rs...)
* Adresu i port MySQL servera, ime baze, kao i kredencijale za pristup
* Putanja do `clien-config.ini` fajla
* Naziv MongoDB baze u koju će biti izvrsen import

Nakon toga vršimo odabir šta eksportujemo i da li želimo automatski import u MongoDB nakon eksporta.
* Napomena elasticrecords.json je generisan za potrebe ElasticSearch- a i ne vrši se import istog u MongoDB!

## Drugi način- ručno 

### Izvršavanje eksporta

Eksport zapisa (podrazumeva se baza bisis na localhostu sa userom bisis/bisis):

```
java -jar bisis2json-export-records.jar -o records.json
```

Eksport cirkulacije (korisnici, bez zaduzenja):

```
java -jar bisis2json-export-users.jar -o users.json
```

Eksport cirkulacije (zaduzenja):

```
java -jar bisis2json-export-lendings.jar -o lendings.json
```

Eksport sifarnika:

```
java -jar bisis2json-export-coders.jar -l gbns(na primer)
```

Eksport `client-config.ini` fajla :

```
java -jar bisis2json-export-client-config.jar -i putanja-do-config-ini/client-config.ini -o clientConfig.json
```

### Import u MongoDB

Import zapisa (u primeru za biblioteku BGB):

```
mongoimport --db bisis --collection bgb.records --file records.json
```

Import cirkulacije (korisnici):
```
mongoimport --db bisis --collection bgb.users --file users.json
```

Import cirkulacije (zaduzenja):
```
mongoimport --db bisis --collection bgb.lendings --file lendings.json
```

Import konfiguracije:
```
mongoimport --db bisis --collection configs --file clientConfig.json
```

Import sifarnika:
```
mongoimport --db bisis --collection coders.accessionReg --file coders_json_output/invknj.json --jsonArray
mongoimport --db bisis --collection coders.acquisition --file coders_json_output/nacin_nabavke.json --jsonArray
mongoimport --db bisis --collection coders.availability --file coders_json_output/dostupnost.json --jsonArray
mongoimport --db bisis --collection coders.binding --file coders_json_output/povez.json --jsonArray
mongoimport --db bisis --collection coders.location --file coders_json_output/location.json --jsonArray
mongoimport --db bisis --collection coders.circ_location --file coders_json_output/location.json --jsonArray
mongoimport --db bisis --collection coders.sublocation --file coders_json_output/podlokacija.json --jsonArray
mongoimport --db bisis --collection coders.status --file coders_json_output/status_primerka.json --jsonArray
mongoimport --db bisis --collection coders.format --file coders_json_output/sigformat.json --jsonArray
mongoimport --db bisis --collection coders.internalMark --file coders_json_output/interna_oznaka.json --jsonArray


mongoimport --db bisis --collection coders.corporate_member --file circ_coders_json_output/corporateMember.json --jsonArray
mongoimport --db bisis --collection coders.education --file circ_coders_json_output/eduLvls.json --jsonArray
mongoimport --db bisis --collection coders.language --file circ_coders_json_output/languages.json --jsonArray
mongoimport --db bisis --collection coders.membership --file circ_coders_json_output/memberships.json --jsonArray
mongoimport --db bisis --collection coders.membership_type --file circ_coders_json_output/membershipTypes.json --jsonArray
mongoimport --db bisis --collection coders.organization --file circ_coders_json_output/organizations.json --jsonArray
mongoimport --db bisis --collection coders.place --file circ_coders_json_output/places.json --jsonArray
mongoimport --db bisis --collection coders.user_categ --file circ_coders_json_output/userCategories.json --jsonArray
mongoimport --db bisis --collection coders.warning_type --file circ_coders_json_output/warningTypes.json --jsonArray
```