import java.net.*;
import java.io.*;

public class TrueRandomGenerator {
    private static String randomOrgBase = "https://www.random.org/integers/?num=1&min=%d&max=%d&col=1&base=10&format=plain&rnd=new";

    public static int getInt(int min, int max) throws IOException {
        URL randomOrg = new URL(String.format(randomOrgBase, min, max));
        URLConnection con = randomOrg.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        int num = Integer.parseInt(in.readLine());
        in.close();
        return num;
    }

    /*public static void main(String[] args) throws Exception {
        URL yahoo = new URL("http://www.yahoo.com/");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            System.out.println(inputLine);
        in.close();
    }*/
}