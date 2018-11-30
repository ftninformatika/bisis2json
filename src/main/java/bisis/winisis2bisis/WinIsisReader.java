package bisis.winisis2bisis;

import bisis.utils.WinIsisUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WinIsisReader {

    private String pathToFile;

    public void readFile() {

        try (Stream<String> stream = Files.lines(Paths.get(WinIsisReader.class.getResource(pathToFile).getPath()))){

            List<WinIsisMemberTxt> membersListTxt = new ArrayList<>();
            final WinIsisMemberTxt[] memberTxt = {null};
            stream.forEach(
              line -> {
                  if (line.equals("") && memberTxt[0] != null)
                      membersListTxt.add(memberTxt[0]);

                  if (line.startsWith(MembersFieldMap.ID))
                      memberTxt[0] = new WinIsisMemberTxt();

                  List<SubfieldDataPair> sfs = WinIsisUtils.getSubfieldsList(line);
                  for (SubfieldDataPair sfPair: sfs) {
                      memberTxt[0].getSubfields().put(sfPair.getKey(), sfPair.getData());
                  }
              }
            );
            System.out.println(membersListTxt.toString());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
