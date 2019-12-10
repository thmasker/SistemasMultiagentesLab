package movietool.utils;

import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
}