public class demo {
    public static void main(String[] args) {
        String string = " look@@ @ look ";
        // string = string.replaceAll(" look ", " @look@ ");
        String[] str = string.split("@");
        for (String str2 : str) {
            System.out.println("***" + str2 + "***");
        }
    }
}
