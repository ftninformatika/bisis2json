package bisis.apps.winisis2bisis;

import bisis.model.jongo_circ.JoLending;
import bisis.model.jongo_circ.JoMember;

import java.util.ArrayList;
import java.util.List;

public class MembersConverter {

    public static final String ID_RAW = "!v930!";

    public static final String SURNAME_AND_NAME = "931";
    public static final String DOC_NO_AND_PLACE = "932a";
    public static final String JMBG = "932b";
    public static final String ZIP_CODE_AND_PLACE = "933a";
    public static final String ADDRESS = "933b";
    public static final String PHONE_NO = "934";
    public static final String PROFESSION = "935";
    public static final String SIGN_DATE = "936"; // DDMMYY
    public static final String PARENT_NAME = "951";
    public static final String PLACE = "952a";
    public static final String ADDRESS2 = "952b";
    public static final String CATEGORY = "953";

    public static final String LENDING_INV_NUM = "937a"; // inv. broj/broj ogranka
    public static final String LEND_DATE = "937b";
    public static final String RETURN_INV = "947a"; // inv. broj/broj ogranka//
    public static final String DATE_LENDED = "947b";
    public static final String DATE_RETURNED = "947c";

    public static final String WARNING_NOTE = "948a";
    public static final String FROM_BRANCH = "948b";
    public static final String SIGN_DATE_FROM_BRANCH = "948c";
    public static final String WARNING_ADDITIONAL = "948d";
    public static final String SIGN_DATE_FROM_BRANCH2 = "948e";

    public static List<MemberLendingsWrapper> createMemberLendings(List<WinIsisMemberTxt> mapMembers) {
        List<MemberLendingsWrapper> retVal = new ArrayList<>();
        for (WinIsisMemberTxt mTxt: mapMembers) {
            MemberLendingsWrapper mlWrapper = new MemberLendingsWrapper();
            mlWrapper.setMember(createMember(mTxt));
            mlWrapper.setLendings(createLendingsForMember(mTxt));
            System.out.println(mlWrapper.getMember());
            retVal.add(mlWrapper);
        }

        return retVal;
    }

    private static List<JoLending> createLendingsForMember(WinIsisMemberTxt member) {
        return null;
    }

    private static JoMember createMember(WinIsisMemberTxt mapMember) {
        JoMember retVal = new JoMember();
        if (mapMember.get(SURNAME_AND_NAME) != null) {
            String name = mapMember.get(SURNAME_AND_NAME).split(" ")[mapMember.get(SURNAME_AND_NAME).split(" ").length - 1];
            String surname = mapMember.get(SURNAME_AND_NAME).replace(name, "").trim();
            retVal.setFirstName(name);
            retVal.setLastName(surname);
        }

        if (mapMember.get(DOC_NO_AND_PLACE) != null) {
            String[] docTokens = mapMember.get(DOC_NO_AND_PLACE).split(" ");
            if (docTokens.length == 1)
                retVal.setDocNo(docTokens[0]);
            else if (docTokens.length > 1) {
                retVal.setDocNo(docTokens[0]);
                retVal.setDocCity(mapMember.get(DOC_NO_AND_PLACE).replace(docTokens[0], "").trim());
            }
        }

        if (mapMember.get(JMBG) != null)
            retVal.setJmbg(mapMember.get(JMBG));

        String address = "";
        if (mapMember.get(ZIP_CODE_AND_PLACE) != null) {
            address = mapMember.get(ZIP_CODE_AND_PLACE);
            String[] zipPlaceTokens = mapMember.get(ZIP_CODE_AND_PLACE).split(" ");
            retVal.setZip(zipPlaceTokens[0]);
        }
        if (mapMember.get(ADDRESS) != null) {
            address = address.equals("") ? mapMember.get(ADDRESS) : mapMember.get(ADDRESS) + ", " + address;
            retVal.setAddress(address);
        }

        if (mapMember.get(PHONE_NO) != null)
            retVal.setPhone(mapMember.get(PHONE_NO));

        if (mapMember.get(PROFESSION) != null)
            retVal.setOccupation(mapMember.get(PROFESSION)); // TODO - mapiranje neko

        //if (mapMember.get())

        return retVal;
    }

    private static JoLending createLending() {
        return null;
    }



}
