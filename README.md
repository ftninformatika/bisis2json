# bisis2json

## Build projekta

Za build projekta koristi se Gradle.

```
$ gradle build
```

Rezultat ce biti sedam jar- ova u `build/libs` folderu:

* `bisis2json-export-records.jar` za eksport zapisa
* `bisis2json-export-users.jar` za eksport cirkulacije
* `bisis2json-export-lendings.jar` za eksport zaduženja
* `bisis2json-export-client-config.jar` za eksport konfiguracije
* `bisis2json-export-coders.jar` za eksport šifarnika
* `bisis2json-export-item-availabilities.jar` za eksport stanja primeraka
* `bisis-migrate.jar` za kompletnu migraciju iz MySQL u MongoDb


## Kompletan prepis
Izgenerisani `bisi-migrate.jar` je aplikacija u koju su integrisani svi eksporti + import u MongoDB.

Neophodno za rad:
* `mongoimport` u sistemskim varijablama (poziva se pri importu)

Parametri: 
* -l, --library, Library name (prefix): gbns, tfzr... MANDATORY!
* -a, --mysqladress, MySQL server address (default: localhost)
* -p, --mysqlport, MySQL server port (default: 3306)
* -n, --mysqdblname, MySQL database name (default: bisis)
* -u, --mysqlusername, MySQL server username (default: bisis)
* -w, --mysqlpassword, MySQL server password (default: bisis)
* -f, --pathtoinnis, Path to folder that conatins reports.ini and client-config.ini MADNDATORY!
* -ma, --mongoaddress, MongoDB server address (default: localhost)
* -mp, --mongoport, MongoDB server port (default: 27017)
* -mn, --mongodbname, MongoDB name (default: bisis)
* -mu, --mongousername, MongoDB server username (default: --empty--)
* -mw, --mongopassword, MongoDB server password (default: --empty--)
* -h, --help, Help


## Primer
```
java -jar bisis-migrate.jar -l gbns -f C:\Users\Korisnik\Documents\gbnsConfigs -n bisisgbns
```
