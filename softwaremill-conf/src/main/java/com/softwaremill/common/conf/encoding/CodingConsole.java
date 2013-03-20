package com.softwaremill.common.conf.encoding;

import java.io.Console;
import java.io.IOException;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class CodingConsole {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the coding console!");
        MasterPasswordStore.readFromConsole();
        ConfigurationValueCoder valueCoder = new ConfigurationValueCoder();

        Console console = System.console();
        while (true) {
            System.out.println("1. encode values");
            System.out.println("2. decode values");
            System.out.println("3. exit");
            String choice = console.readLine();
            if ("1".equals(choice)) {
                String text = console.readLine("Text: ");
                System.out.println(valueCoder.encode(text));
            } else if ("2".equals(choice)) {
                String text = console.readLine("Text: ");
                System.out.println(valueCoder.decode(text));
            } if ("3".equals(choice)) {
                return;
            }
        }
    }
}
