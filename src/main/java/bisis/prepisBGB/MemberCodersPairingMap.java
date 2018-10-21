package bisis.prepisBGB;

import bisis.circ.MembershipType;
import bisis.circ.UserCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberCodersPairingMap {

    // Mapiranje kategorija korisnika
    static Map<String, UserCategory> userCategsBgb = new HashMap<>();
    static Map<String, String> categDescriptionMapping = new HashMap<>();
    static Map<String, String> memberCircMap = new HashMap<>();

    // Mapiranje vrste clanarine
    static Map<String, MembershipType> mmbrshipTypesBgb = new HashMap<>();
    static Map<String, String> mmbrshipTypesMapping = new HashMap<>();

    private static ObjectMapper mapper = new ObjectMapper();

     static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        memberCircMap.put("04","140");
        memberCircMap.put("10","260");


        userCategsBgb.put("OSTALI", new UserCategory("bgb", "OSTALI", 10, 20, 5000));
        userCategsBgb.put("UČENICI SŠ", new UserCategory("bgb", "UČENICI SŠ", 10, 20, 5000));
        userCategsBgb.put("STUDENTI", new UserCategory("bgb", "STUDENTI", 10, 20, 5000));
        userCategsBgb.put("ZAPOSLENI", new UserCategory("bgb", "ZAPOSLENI", 10, 20, 5000));
        userCategsBgb.put("PENZIONERI", new UserCategory("bgb", "PENZIONERI", 10, 20, 5000));
        userCategsBgb.put("SLUŽBENICI BGB", new UserCategory("bgb", "SLUŽBENICI BGB", 20, 20, 5000));
        userCategsBgb.put("DIREKTOR BGB", new UserCategory("bgb", "DIREKTOR BGB", 50, 20, 5000));
        userCategsBgb.put("65+", new UserCategory("bgb", "65+", 10, 20, 5000));
        userCategsBgb.put("PORTUGALCI", new UserCategory("bgb", "PORTUGALCI", 100, 365, 5000));
        userCategsBgb.put("STRANI DRŽAVLJANI", new UserCategory("bgb", "STRANI DRŽAVLJANI", 10, 20, 5000));
        userCategsBgb.put("NEZAPOSLENI", new UserCategory("bgb", "NEZAPOSLENI", 10, 20, 5000));
        userCategsBgb.put("PRVI RAZRED", new UserCategory("bgb", "PRVI RAZRED", 10, 20, 5000)); //?
        userCategsBgb.put("PREDŠKOLSKI UZRAST", new UserCategory("bgb", "PREDŠKOLSKI UZRAST", 10, 20, 5000));
        userCategsBgb.put("UČENICI OŠ", new UserCategory("bgb", "UČENICI OŠ", 10, 20, 5000));
        userCategsBgb.put("SARADNICI", new UserCategory("bgb", "SARADNICI", 10, 20, 5000));
        categDescriptionMapping.put("OSTALI", "OSTALI");
        categDescriptionMapping.put("DOMAĆICE", "OSTALI");
        categDescriptionMapping.put("UČENICI SŠ", "UČENICI SŠ");
        categDescriptionMapping.put("STUDENTI DRUŠTVENIH NAUKA", "STUDENTI");
        categDescriptionMapping.put("STUDENTI PRIRODNIH NAUKA", "STUDENTI");
        categDescriptionMapping.put("STUDENTI UMETNOSTI", "STUDENTI");
        categDescriptionMapping.put("STUDENTI BIBLIOTEKARSTVA", "STUDENTI");
        categDescriptionMapping.put("STUDENTI DIF-A", "STUDENTI");
        categDescriptionMapping.put("RADNICI", "ZAPOSLENI");
        categDescriptionMapping.put("SLUŽBENICI", "ZAPOSLENI");
        categDescriptionMapping.put("STRUČNJACI DRUŠTVENIH NAUKA", "ZAPOSLENI");
        categDescriptionMapping.put("STRUČNJACI PRIRODNIH NAUKA", "ZAPOSLENI");
        categDescriptionMapping.put("UMETNIČKE PROFESIJE", "ZAPOSLENI");
        categDescriptionMapping.put("PRIVATNI PREDUZETNICI", "ZAPOSLENI");
        categDescriptionMapping.put("BIBLIOTEKARI", "ZAPOSLENI");
        categDescriptionMapping.put("KNJIŽNIČARI", "ZAPOSLENI");
        categDescriptionMapping.put("VIŠI KNJIŽNIČAR", "ZAPOSLENI");
        categDescriptionMapping.put("PENZIONERI", "PENZIONERI");
        categDescriptionMapping.put("SLUŽBENICI BGB", "SLUŽBENICI BGB");
        categDescriptionMapping.put("DIREKTOR BGB", "DIREKTOR BGB");
        categDescriptionMapping.put("65+", "65+");
        categDescriptionMapping.put("PORTUGALCI", "PORTUGALCI");
        categDescriptionMapping.put("STRANI DRŽAVLJANI", "STRANI DRŽAVLJANI");
        categDescriptionMapping.put("NEZAPOSLENI", "NEZAPOSLENI");
        categDescriptionMapping.put("PRVI RAZRED", "PRVI RAZRED");
        categDescriptionMapping.put("PREDŠKOLSKI UZRAST", "PREDŠKOLSKI UZRAST");
        categDescriptionMapping.put("UČENICI OŠ", "UČENICI OŠ");
        categDescriptionMapping.put("SARADNICI", "SARADNICI");

        mmbrshipTypesBgb.put("REDOVAN ČLAN", new MembershipType("bgb", "REDOVAN ČLAN", 365));
        mmbrshipTypesBgb.put("SAJAM", new MembershipType("bgb", "SAJAM", 365));
        mmbrshipTypesBgb.put("KOLEKTIVNI UPIS", new MembershipType("bgb", "KOLEKTIVNI UPIS", 365));
        mmbrshipTypesBgb.put("NE PLAĆA ČLANARINU", new MembershipType("bgb", "NE PLAĆA ČLANARINU", 365));
        mmbrshipTypesBgb.put("ČLAN UDR. BIBLIOTEKA", new MembershipType("bgb", "ČLAN UDR. BIBLIOTEKA", 365));
        mmbrshipTypesBgb.put("EURO < 26", new MembershipType("bgb", "EURO < 26", 365));
        mmbrshipTypesBgb.put("ISIC", new MembershipType("bgb", "ISIC", 365));
        mmbrshipTypesBgb.put("PORODIČNO ČLANSTVO", new MembershipType("bgb", "PORODIČNO ČLANSTVO", 365));
        mmbrshipTypesBgb.put("GRUPA 10+", new MembershipType("bgb", "GRUPA 10+", 365));
        mmbrshipTypesBgb.put("SERVANTES", new MembershipType("bgb", "SERVANTES", 365));
        mmbrshipTypesBgb.put("PENZIONERI", new MembershipType("bgb", "PENZIONERI", 365));
        mmbrshipTypesBgb.put("MESEČNA ČLANARINA", new MembershipType("bgb", "MESEČNA ČLANARINA", 30));
        mmbrshipTypesBgb.put("HUMANITARNO", new MembershipType("bgb", "HUMANITARNO", 365));
        mmbrshipTypesBgb.put("DOBROVOLJNI DAVAOCI KRVI", new MembershipType("bgb", "DOBROVOLJNI DAVAOCI KRVI", 365));
        mmbrshipTypesBgb.put("65+", new MembershipType("bgb", "65+", 365));
        mmbrshipTypesBgb.put("SVETSKI DAN KNJIGE", new MembershipType("bgb", "SVETSKI DAN KNJIGE", 365));
        mmbrshipTypesBgb.put("NEZAPOSLENI", new MembershipType("bgb", "NEZAPOSLENI", 365));
        mmbrshipTypesBgb.put("UG. SRPSKO BIBLIOFILSKO DRUŠTVO", new MembershipType("bgb", "UG. SRPSKO BIBLIOFILSKO DRUŠTVO", 365));
        mmbrshipTypesBgb.put("STRANI DRŽAVLJANI", new MembershipType("bgb", "STRANI DRŽAVLJANI", 365));
        mmbrshipTypesBgb.put("UG. GRADSKA UPRAVA", new MembershipType("bgb", "UG. GRADSKA UPRAVA", 365));
        mmbrshipTypesBgb.put("NOĆ ISTRAŽIVAČA", new MembershipType("bgb", "NOĆ ISTRAŽIVAČA", 365));
        mmbrshipTypesBgb.put("UG. UNS/NUNS", new MembershipType("bgb", "UG. UNS/NUNS", 365));
        mmbrshipTypesBgb.put("INFOSTAN", new MembershipType("bgb", "INFOSTAN", 365));
        mmbrshipTypesBgb.put("VAUČER", new MembershipType("bgb", "VAUČER", 365));
        mmbrshipTypesBgb.put("ONLINE POPUSTI", new MembershipType("bgb", "ONLINE POPUSTI", 365));
        mmbrshipTypesBgb.put("PROLEĆNI POPUST", new MembershipType("bgb", "PROLEĆNI POPUST", 365));
        mmbrshipTypesBgb.put("NOĆ KNJIGE", new MembershipType("bgb", "NOĆ KNJIGE", 365));
        mmbrshipTypesBgb.put("SAJAM DECA", new MembershipType("bgb", "SAJAM DECA", 365));
        mmbrshipTypesBgb.put("PRAZNIČNI POPUST", new MembershipType("bgb", "PRAZNIČNI POPUST", 365));
        mmbrshipTypesBgb.put("UG. ŠKOLA ZA NEGU LEPOTE", new MembershipType("bgb", "UG. ŠKOLA ZA NEGU LEPOTE", 365));
        mmbrshipTypesBgb.put("UČENICI SŠ", new MembershipType("bgb", "UČENICI SŠ", 365));
        mmbrshipTypesBgb.put("GRUPA 10+DECA", new MembershipType("bgb", "GRUPA 10+DECA", 365));
        mmbrshipTypesBgb.put("MESEČNA (STRANCI)", new MembershipType("bgb", "MESEČNA (STRANCI)", 30));
        mmbrshipTypesBgb.put("EKOTEKA ODRASLI", new MembershipType("bgb", "EKOTEKA ODRASLI", 365));
        mmbrshipTypesBgb.put("SAJAM NEZAPOSLENI", new MembershipType("bgb", "SAJAM NEZAPOSLENI", 365));
        mmbrshipTypesBgb.put("UG. MATEMATIČKI FAKULTET", new MembershipType("bgb", "UG. MATEMATIČKI FAKULTET", 365));
        mmbrshipTypesBgb.put("UG. DELTA GENERALI", new MembershipType("bgb", "UG. DELTA GENERALI", 365));
        mmbrshipTypesBgb.put("UG. PRVA BEOGRADSKA GIMNAZIJA", new MembershipType("bgb", "UG. PRVA BEOGRADSKA GIMNAZIJA", 365));
        mmbrshipTypesBgb.put("SENIOR KARTICA", new MembershipType("bgb", "SENIOR KARTICA", 365));
        mmbrshipTypesBgb.put("UG. GIMNAZ. \"SV. SAVA\"", new MembershipType("bgb", "UG. GIMNAZ. “SV. SAVA”", 365));
        mmbrshipTypesBgb.put("UG. MUZEJ GRADA", new MembershipType("bgb", "UG. MUZEJ GRADA", 365));
        mmbrshipTypesBgb.put("EKOTEKA SŠ", new MembershipType("bgb", "EKOTEKA SŠ", 365));
        mmbrshipTypesBgb.put("EKOTEKA OŠ", new MembershipType("bgb", "EKOTEKA OŠ", 365));
        mmbrshipTypesBgb.put("EKOTEKA PENZIONERI", new MembershipType("bgb", "EKOTEKA PENZIONERI", 365));
        mmbrshipTypesBgb.put("ŠKOLSKA NEDELJA 5+", new MembershipType("bgb", "ŠKOLSKA NEDELJA 5+", 365));
        mmbrshipTypesBgb.put("OBRISANA VRSTA", new MembershipType("bgb", "OBRISANA VRSTA", 365));

        mmbrshipTypesMapping.put("STUD.BIBLIOTEKARSTVA", "NE PLAĆA ČLANARINU");
        mmbrshipTypesMapping.put("FILOLOŠKI F.", "OBRISANA VRSTA");
        mmbrshipTypesMapping.put("NAGRADNI UPIS", "NE PLAĆA ČLANARINU");
        mmbrshipTypesMapping.put("KOMERCIJALNA BANKA", "OBRISANA VRSTA");
        mmbrshipTypesMapping.put("SL.GLAS.2011", "OBRISANA VRSTA");
        mmbrshipTypesMapping.put("SRPSKO BIBLIOFILSKO DRUŠTVO", "UG. SRPSKO BIBLIOFILSKO DRUŠTVO");
        mmbrshipTypesMapping.put("NOĆ BIBLIOTEKA", "OBRISANA VRSTA");
        mmbrshipTypesMapping.put("GRADSKA UPRAVA", "UG. GRADSKA UPRAVA");
        mmbrshipTypesMapping.put("UNS/NUNS", "UG. UNS/NUNS");
        mmbrshipTypesMapping.put("ŠKOLA ZA NEGU LEPOTE", "UG. ŠKOLA ZA NEGU LEPOTE");
        mmbrshipTypesMapping.put("MATEMATIČKI FAKULTET", "UG. MATEMATIČKI FAKULTET");
        mmbrshipTypesMapping.put("DELTA GENERALI", "UG. DELTA GENERALI");
        mmbrshipTypesMapping.put("PRVA BEOGRADSKA GIMNAZIJA", "UG. PRVA BEOGRADSKA GIMNAZIJA");
        mmbrshipTypesMapping.put("GIMNAZ. \"SV. SAVA\"", "UG. GIMNAZ. \"SV. SAVA\"");
        mmbrshipTypesMapping.put("MUZEJ GRADA", "UG. MUZEJ GRADA");
        mmbrshipTypesMapping.put("ČLAN.UDR.BIBLIOTEKA", "ČLAN UDR. BIBLIOTEKA");
        mmbrshipTypesMapping.put("POPUSTI.RS", "ONLINE POPUSTI");
     }

    public static UserCategory getUserCategMappedByDesc(String description) {
        String desc = categDescriptionMapping.get(description);
        if (desc == null)
            return userCategsBgb.get("OSTALI");
        else
            return userCategsBgb.get(desc);
    }

    public static MembershipType getMmbrTypeByName(String name) {
         MembershipType retVal = null;

         retVal = mmbrshipTypesBgb.get(name);
         if (retVal == null) {
             retVal = mmbrshipTypesBgb.get(mmbrshipTypesMapping.get(name));
         }

         if (retVal == null) {
             retVal = mmbrshipTypesBgb.get("OBRISANA VRSTA");
         }

         return retVal;
    }

    public static String exportToJsonUserCategs() throws JsonProcessingException {
         StringBuffer sb = new StringBuffer();

         sb.append(mapper.writeValueAsString(new ArrayList<UserCategory>(userCategsBgb.values())));

         return sb.toString();
    }

    public static String exportToJsonMmbrTypes() throws JsonProcessingException {
        StringBuffer sb = new StringBuffer();

        sb.append(mapper.writeValueAsString(new ArrayList<MembershipType>(mmbrshipTypesBgb.values())));

        return sb.toString();
    }

//    public static void main(String[] args) {
//        try {
//            FileUtils.writeTextFile("userCategs.json", exportToJsonUserCategs());
//            FileUtils.writeTextFile("mmbrTypes.json", exportToJsonMmbrTypes());
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }

}
