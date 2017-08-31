:: paths to JARS
@echo off
SET pathToCoders=%~dp0\build\libs\bisis2json-export-coders.jar
SET pathToRecords=%~dp0\build\libs\bisis2json-export-records.jar
SET pathToLendings=%~dp0\build\libs\bisis2json-export-lendings.jar
SET pathToClientConfig=%~dp0\build\libs\bisis2json-export-client-config.jar
SET pathToUsers=%~dp0\build\libs\bisis2json-export-users.jar

::goto debug1
echo  & echo. Welcome to BISIS export tool, please make connection to both MySQLDB and MongoDB & echo. and provide the following informations in order make an export! & echo.

SET /P library=Enter library code (gbns, gbsa, tfzr....): 
SET /P address=Enter MySQLServer address: 
SET /P port=Enter port: 
SET /P db=Enter DB name: 
SET /P username=Enter username for provided DB: 
SET /P password=Enter password: 
SET /P cli=Enter path to client-config.ini: 

:::debug1
:choiceCoders
echo Select what data you want to export to json:
SET /P coders=Export coders(y/n):
if /I "%coders%" == "Y" goto exportCoders
if /I "%coders%" == "N" goto choiceClientConfig
goto choiceCoders

:exportCoders
echo Starting coders exporting...
@echo on
java -jar "%pathToCoders%" -l %library% -d %db%
@echo off

:choiceClientConfig
SET /P client=Export client-config(y/n):
if /I "%client%" == "Y" goto exportClient
if /I "%client%" == "N" goto choiceUsers
goto choiceClientConfig

:exportClient
@echo on
java -jar "%pathToClientConfig%" -i %cli% -o clientConfig.json
@echo off

:choiceUsers
SET /P users=Export users(y/n):
if /I "%users%" == "Y" goto exportUsers
if /I "%users%" == "N" goto choiceRecords
goto choiceUsers

:exportUsers
@echo on
java -jar "%pathToUsers%"  -a %address% -p %port% -d %db% -u %username% -w %password% -l %library% -o members.json
@echo off

:choiceRecords
SET /P r=Export records(y/n):
if /I "%r%" == "Y" goto exportRecords
if /I "%r%" == "N" goto choiceLendings
goto choiceRecords

:exportRecords
java -jar "%pathToRecords%"  -a %address% -p %port% -d %db% -u %username% -w %password% -o members.json

:choiceLendings
SET /P l=Export lendings(y/n):
if /I "%l%" == "Y" goto exportLendings
if /I "%l%" == "N" goto end
goto choiceLendings

:exportLendings
java -jar "%pathToLendings%"  -a %address% -p %port% -d %db% -u %username% -w %password% -o lendings.json

:end
echo Export completed!
