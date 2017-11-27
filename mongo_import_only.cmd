::Ako je kompletan eksport uradjen i ova skripta se nalazi u root direktorijumu projekta!

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

::cirkulacija
mongoimport --db bisis --collection coders.corporate_member --file circ_coders_json_output/corporateMember.json --jsonArray
mongoimport --db bisis --collection coders.education --file circ_coders_json_output/eduLvls.json --jsonArray
mongoimport --db bisis --collection coders.language --file circ_coders_json_output/languages.json --jsonArray
mongoimport --db bisis --collection coders.membership --file circ_coders_json_output/memberships.json --jsonArray
mongoimport --db bisis --collection coders.membership_type --file circ_coders_json_output/membershipTypes.json --jsonArray
mongoimport --db bisis --collection coders.organization --file circ_coders_json_output/organizations.json --jsonArray
mongoimport --db bisis --collection coders.place --file circ_coders_json_output/places.json --jsonArray
mongoimport --db bisis --collection coders.user_categ --file circ_coders_json_output/userCategories.json --jsonArray
mongoimport --db bisis --collection coders.warning_type --file circ_coders_json_output/warningTypes.json --jsonArray


mongoimport --db bisis --collection config --file clientConfig.json
mongoimport --db bisis --collection %libraryPrefix%_members --file members.json
mongoimport --db bisis --collection %libraryPrefix%_records --file records.json
mongoimport --db bisis --collection %libraryPrefix%_lendings --file lendings.json