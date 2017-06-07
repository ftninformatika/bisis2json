# bisis2json

## Build projekta

Za build projekta koristi se Gradle.

```
$ gradle build
```

Rezultat ce biti dva jara u `build/libs` folderu:

* `bisis2json-export-records.jar` za eksport zapisa
* `bisis2json-export-users.jar` za eksport cirkulacije

## Izvršavanje eksporta

Eksport zapisa (podrazumeva se baza bisis na localhostu sa userom bisis/bisis):

```
java -jar bisis2json-export-records.jar -o records.json
```

Eksport cirkulacije:

```
java -jar bisis2json-export-users.jar -o users.json
```

## Import u MongoDB

Import zapisa (u primeru za biblioteku BGB):

```
mongoimport --db bisis --collection bgb.records --file records.json
```

Import cirkulacije:
```
mongoimport --db bisis --collection bgb.users --file users.json
```