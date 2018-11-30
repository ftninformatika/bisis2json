package bisis.winisis2bisis;

public class WinIsis2Bisis {

    public static void main(String[] args) {

        WinIsisReader winIsisReader = new WinIsisReader("/winisis.txt");
        winIsisReader.readFile();
    }

}
