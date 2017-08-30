# bisis2json

## Build projekta

Za build projekta koristi se Gradle.

```
$ gradle build
```

Rezultat ce biti cetiri jara u `build/libs` folderu:

* `bisis2json-export-records.jar` za eksport zapisa
* `bisis2json-export-users.jar` za eksport cirkulacije
* `bisis2json-export-lendings.jar` za eksport cirkulacije
* `bisis2json-export-client-config.jar` za eksport cirkulacije

## Izvršavanje eksporta

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

Eksport `client-config.ini` fajla :

```
java -jar bisis2json-export-client-config.jar -i client-config.ini -o clientConfig.json
```

## Import u MongoDB

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