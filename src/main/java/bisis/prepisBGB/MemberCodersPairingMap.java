package bisis.prepisBGB;

import bisis.circ.MembershipType;
import bisis.circ.UserCategory;

import java.util.HashMap;
import java.util.Map;

public class MemberCodersPairingMap {

    // Mapiranje kategorija korisnika
    static Map<String, UserCategory> userCategsBgb = new HashMap<>();
    static Map<String, String> categDescriptionMapping = new HashMap<>();

    // Mapiranje vrste clanarine
    static Map<String, MembershipType> mmbrshipTypesBgb = new HashMap<>();
    static Map<String, String> mmbrshipTypesMapping = new HashMap<>();

     static {
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

     }

    public static UserCategory getUserCategMappedByDesc(String description) {
        String desc = categDescriptionMapping.get(description);
        if (desc == null)
            return userCategsBgb.get("OSTALI");
        else
            return userCategsBgb.get(desc);
    }

}
