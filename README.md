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
Izgenerisani `bisis-migrate.jar` je aplikacija u koju su integrisani svi eksporti + import u MongoDB.

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

## MySQL restore

* mysqladmin create naziv_baze
* mysql -u root

U mysql konzoli:
* grant all privileges on naziv_baze.* to 'bisis'@'%' identified by 'bisis';
* flush privileges;

U backup fajlu promeniti use database na naziv nove baze.

* mysql -u bisis -pbisis -D naziv_baze < dump.sql

Proveriti da li je baza potpuna u odnosu na poslednju verziju Bisis4 (sifarnici, registri). Ako nije izvršiti patch_mysql_to_bisis.sql sa bisis korisnikom.

Eksport podataka:
* java -jar bisis-migrate.jar -e -l sufix_biblioteke -f ./dir_ini_files -n naziv_baze -z

Rezultat su json fajlovi u folderu koje treba probaciti na server gde je NoSQL (folder treba da bude u istom folderu gde je bisis-migrate.jar, dodati -d da obrise prethodne podatke).

java -jar bisis-migrate.jar -i -l sufix_biblioteke 


Ako fale RN brojevi (Kreirai RN brojac ako ne postoji, posle pobrisati duplo kreirane brojace):
java -jar fix-duplicate-null-rn-bgb.jar sufix_biblioteke

Indexer:
./bisis5-tools/bisis-indexer-5.0.0/bin/bisis-indexer sufix_biblioteke

Izvestaji:
./bisis5-tools/bisis-reports-5.0.0/bin/bisis-reports sufix_biblioteke

Povezivanje korica:
./bisis-books-common-merger m sufix_biblioteke

