:: paths to JARS
@echo off
SET pathToCoders=%~dp0\build\libs\bisis2json-export-coders.jar
SET pathToRecords=%~dp0\build\libs\bisis2json-export-records.jar
SET pathToLendings=%~dp0\build\libs\bisis2json-export-lendings.jar
SET pathToClientConfig=%~dp0\build\libs\bisis2json-export-client-config.jar
SET pathToUsers=%~dp0\build\libs\bisis2json-export-users.jar

echo  & echo. Welcome to BISIS export tool, please make connection to both MySQLDB and MongoDB & echo. and provide the following informations in order make an export! & echo.

SET /P library=Enter library code (gbns, gbsa, tfzr....): 
SET /P libraryPrefix=Enter library code prefix (gbns_com, gbsa_rs, tfzr_uns_ac_rs....): 
SET /P address=Enter MySQLServer address: 
SET /P port=Enter port: 
SET /P db=Enter DB name: 
SET /P username=Enter username for provided DB: 
SET /P password=Enter password: 
SET /P cli=Enter path to client-config.ini: 
SET /P mDb=Enter name of MongoDB for import:
echo Default MongoDB host:port for now (localhost:27017) 

:choiceCoders
echo Select what data you want to export to json:
SET /P coders=Export coders(y/n):
if /I "%coders%" == "Y" goto choiceCodersMongo
if /I "%coders%" == "N" goto choiceClientConfig
goto choiceCoders

:choiceCodersMongo
SET /P iCoders= Import coders automatically in MongoDB after exporting?(y/n):
if /I "%iCoders%" == "Y" goto choiceClientConfig
if /I "%iCoders%" == "N" goto choiceClientConfig
goto choiceCodersMongo

:choiceClientConfig
SET /P client=Export client-config(y/n):
if /I "%client%" == "Y" goto choiceClientMongo
if /I "%client%" == "N" goto choiceUsers
goto choiceClientConfig

:choiceClientMongo
SET /P iClient=Do you want to import client config automatically in MongoDB after export?(y/n):
if /I "%iClient%" == "Y" goto choiceUsers
if /I "%iClient%" == "N" goto choiceUsers
goto choiceClientMongo

:choiceUsers
SET /P users=Export users(y/n):
if /I "%users%" == "Y" goto choiceUsersMongo
if /I "%users%" == "N" goto choiceRecords
goto choiceUsers

:choiceUsersMongo
SET /P iUsers=Import users in mongo after exporting?(y/n):
if /I "%iUsers%" == "Y" goto choiceRecords
if /I "%iUsers%" == "N" goto choiceRecords
goto choiceUsersMongo

:choiceRecords
SET /P r=Export records(y/n):
if /I "%r%" == "Y" goto choiceRecordsMongo
if /I "%r%" == "N" goto choiceLendings
goto choiceRecords

:choiceRecordsMongo
SET /P iR=Import records in mongo after exporting?(y/n):
if /I "%iR%" == "Y" goto choiceLendings
if /I "%iR%" == "N" goto choiceLendings
goto choiceRecordsMongo

:choiceLendings
SET /P l=Export lendings(y/n):
if /I "%l%" == "Y" goto choiceLendingsMongo
if /I "%l%" == "N" goto export
goto choiceLendings


:choiceLendingsMongo
SET /P iL=Import lendings in mongo after exporting?(y/n):
if /I "%iL%" == "Y" goto export
if /I "%iL%" == "N" goto export
goto choiceLendingsMongo

:export

IF /I "%coders%" == "Y" (
echo Starting coders exporting...
java -jar "%pathToCoders%" -l %library% -d %db%
)

IF /I "%client%" == "Y" (
echo Starting client-config exporting...
java -jar "%pathToClientConfig%" -i %cli% -o clientConfig.json
)

IF /I "%users%" == "Y" (
echo Starting users exporting...
java -jar "%pathToUsers%"  -a %address% -p %port% -d %db% -u %username% -w %password% -l %library% -o members.json
)

IF /I "%r%" == "Y" (
echo Starting records exporting...
java -jar "%pathToRecords%"  -a %address% -p %port% -d %db% -u %username% -w %password% -o records.json
)

IF /I "%l%" == "Y" (
echo Starting lendings exporting...
java -jar "%pathToLendings%"  -a %address% -p %port% -d %db% -u %username% -w %password% -o lendings.json
)
echo Exporting completed

:mongoImport

IF /I "%iCoders%" == "Y" (
echo Starting coders mongo import...
mongoimport --db "%mDb%" --collection coders.accessionReg --file coders_json_output/invknj.json --jsonArray
mongoimport --db "%mDb%" --collection coders.acquisition --file coders_json_output/nacin_nabavke.json --jsonArray
mongoimport --db "%mDb%" --collection coders.availability --file coders_json_output/dostupnost.json --jsonArray
mongoimport --db "%mDb%" --collection coders.binding --file coders_json_output/povez.json --jsonArray
mongoimport --db "%mDb%" --collection coders.location --file coders_json_output/location.json --jsonArray
mongoimport --db "%mDb%" --collection coders.circ_location --file coders_json_output/location.json --jsonArray
mongoimport --db "%mDb%" --collection coders.sublocation --file coders_json_output/podlokacija.json --jsonArray
mongoimport --db "%mDb%" --collection coders.status --file coders_json_output/status_primerka.json --jsonArray
mongoimport --db "%mDb%" --collection coders.format --file coders_json_output/sigformat.json --jsonArray
mongoimport --db "%mDb%" --collection coders.internalMark --file coders_json_output/interna_oznaka.json --jsonArray


mongoimport --db "%mDb%" --collection coders.corporate_member --file circ_coders_json_output/corporateMember.json --jsonArray
mongoimport --db "%mDb%" --collection coders.education --file circ_coders_json_output/eduLvls.json --jsonArray
mongoimport --db "%mDb%" --collection coders.language --file circ_coders_json_output/languages.json --jsonArray
mongoimport --db "%mDb%" --collection coders.membership --file circ_coders_json_output/memberships.json --jsonArray
mongoimport --db "%mDb%" --collection coders.membership_type --file circ_coders_json_output/membershipTypes.json --jsonArray
mongoimport --db "%mDb%" --collection coders.organization --file circ_coders_json_output/organizations.json --jsonArray
mongoimport --db "%mDb%" --collection coders.place --file circ_coders_json_output/places.json --jsonArray
mongoimport --db "%mDb%" --collection coders.user_categ --file circ_coders_json_output/userCategories.json --jsonArray
mongoimport --db "%mDb%" --collection coders.warning_type --file circ_coders_json_output/warningTypes.json --jsonArray
echo Finished importing coders
)

IF /I "%iClient%" == "Y" (
mongoimport --db "%mDb%" --collection configs --file clientConfig.json
)

IF /I "%iUsers%" == "Y" (
mongoimport --db "%mDb%" --collection %libraryPrefix%_members --file members.json
)

IF /I "%iR%" == "Y" (
mongoimport --db "%mDb%" --collection %libraryPrefix%_records --file records.json
)

IF /I "%iL%" == "Y" (
echo Starting lendings exporting...
mongoimport --db "%mDb%" --collection %libraryPrefix%_lendings --file lendings.json
)

pause

