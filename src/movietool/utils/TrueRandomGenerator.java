package movietool.utils;

import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TrueRandomGenerator {
    private static String randomOrgBase = "https://www.random.org/integers/?num=1&min=%d&max=%d&col=1&base=10&format=plain&rnd=new";

    /*
     * Get a True Random Number in the range [min, max] (both included)
     *
	 * min: Minimum number that can be generated
     * max: Maximum number that can be generated
	 * 
	 * Return:
	 * 		int     Random number in [min, max] range
	 */
    public static int getInt(int min, int max) throws IOException {
        URL randomOrg = new URL(String.format(randomOrgBase, min, max));
        URLConnection con = randomOrg.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        int num = Integer.parseInt(in.readLine());
        in.close();
        return num;
    }
}