package pl.softwaremill.common.util.stringsorting;

import java.nio.charset.Charset;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SortedCharsToConsecutiveCharsAssigner {
    public static void main(String[] args) {
        String sortedChars = "!@#$%^&*()_+-={}[]:|\";'\\,./<>?~`0123456789aąäbcćdeęfghijklłmnńoóöpqrsśßtuüvwxyzżź";

        // Starting character from which we will start assigning the sorted chars consecutive characters
        byte current = 32;
        int max_c = 0;
        StringBuilder sb = new StringBuilder();
        Charset charset = Charset.forName("UTF-8");

        for (char c : sortedChars.toCharArray()) {
            if (c > max_c) max_c = c;
            sb.append("CONVERSION[")
                    .append((int) c)
                    .append("] = '")
                    .append(new String(new byte[]{current}, charset))
                    .append("'; // ")
                    .append(c)
                    .append(" = ")
                    .append(current)
                    .append("\n");
            current++;
        }

        System.out.println("char[] CONVERSION = new char[" + (max_c+1) + "];");
        System.out.println(sb.toString());
    }
}
