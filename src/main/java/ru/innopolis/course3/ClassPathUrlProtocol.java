package ru.innopolis.course3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * @author Danil Popov
 */
public class ClassPathUrlProtocol {

    private static Logger logger = LoggerFactory.getLogger(ClassPathUrlProtocol.class);

    public static void main(String[] args) throws MalformedURLException {
        testClassPath();
    }

    private static void testClassPath() {
        //classpath:log4j.properties
        URL url = null;
        try (Scanner scanner = new Scanner(System.in)) {
            String str = scanner.nextLine();
            URL.setURLStreamHandlerFactory(new ClasspathFactory());
            url = new URL(str);
            try (InputStream is = url.openConnection().getInputStream();
                 BufferedInputStream fis = new BufferedInputStream(is)) {

                byte[] b = new byte[512];
                StringBuilder sb = new StringBuilder();

                int i = fis.read(b);
                while (i > 0) {
                    sb.append(new String(b, 0, i, "UTF-8"));
                    i = fis.read(b);
                }
                logger.debug(sb.toString());
            } catch (IOException e) {
                logger.error("IO error", e);
            }
        } catch (MalformedURLException e) {
            logger.error("URL creation error", e);
        }
    }

    private static class ClasspathFactory implements URLStreamHandlerFactory {

        @Override
        public URLStreamHandler createURLStreamHandler(String protocol) {
            URLStreamHandler handler = new URLStreamHandler() {

                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {

                        @Override
                        public void connect() throws IOException {
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            String fileName = null;
                            String[] paths = System.getProperty("java.class.path").split(":");
                            for (String path : paths) {
                                fileName = searchFile(new File(path));
                                if (fileName != null) {
                                    break;
                                }
                            }
                            if (fileName == null) {
                                throw new FileNotFoundException("file with name \"" + u.getFile()
                                        + "\" is not found");
                            }
                            return new FileInputStream(fileName);
                        }

                        private String searchFile(File f) {
                            String name = u.getFile();
                            if (f.isDirectory()) {
                                for (File file : f.listFiles()) {
                                    if (file.isFile() && name.equals(file.getName())) {
                                        return file.getAbsolutePath();
                                    }
                                    if (file.isDirectory()) {
                                        if (searchFile(file) != null) {
                                            return searchFile(file);
                                        }
                                    }
                                }
                            } else if (f.isFile() && name.equals(f.getName())) {
                                return f.getAbsolutePath();
                            }
                            return null;
                        }
                    };
                }
            };

            if ("classpath".equals(protocol)) {
                return handler;
            } else {
                return null;
            }
        }
    }
}
