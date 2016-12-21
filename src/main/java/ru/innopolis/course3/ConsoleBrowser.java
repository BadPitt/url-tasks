package ru.innopolis.course3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Danil Popov
 */
public class ConsoleBrowser {

    private static Logger logger = LoggerFactory.getLogger(ConsoleBrowser.class);

    public static void main(String[] args) {
        testUrl();
    }

    private static void testUrl() {
        URL url = null;
        System.out.println("please, write the url");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        byte[] b = new byte[512];
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try (InputStream is = url.openStream();) {
            while (is.read(b) > 0) ;
            System.out.println(new String(b));
        } catch (MalformedURLException e) {
            logger.error("URL creation error ", e);
        } catch (IOException e) {
            logger.error("IO error ", e);
        }
    }
}
