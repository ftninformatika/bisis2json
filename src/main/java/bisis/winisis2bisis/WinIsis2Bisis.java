package bisis.winisis2bisis;

import java.util.List;

public class WinIsis2Bisis {

    public static void main(String[] args) {

        WinIsisReader winIsisReader = new WinIsisReader("/winisis.txt");
        List<WinIsisMemberTxt> listTxtMembers = winIsisReader.readFile2WIMTList();
        MembersConverter.createMemberLendings(listTxtMembers);
    }

}
