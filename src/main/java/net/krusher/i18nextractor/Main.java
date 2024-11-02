package net.krusher.i18nextractor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {

    public static final String SRC_DIR = "C:\\dev\\repos\\datalinks-frontend\\src";
    public static final String DEST_DIR = "C:\\dev\\repos\\datalinks-frontend\\public\\locales\\en\\translation.json";
    public static final String[] FILE_EXTS = {"js", "jsx", "ts", "tsx"};
    public static final Pattern tPattern = Pattern.compile("t\\(\\s*['\"](.*?)['\"]\\s*\\)");

    public static void main(String[] args) throws IOException {

        Set<String> keys = new HashSet<>();

        try (Stream<Path> stream = Files.walk(Paths.get(SRC_DIR))) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(path -> isFileExt(path.toString()))
                    .forEach(path -> keys.addAll(extractI18n(path)));
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(DEST_DIR));
        writer.write("{\n");
        int i = 0;
        for (String key : keys) {
            writer.write("\t\"" + key + "\": \"" + key + "\"");
            if (i++ == keys.size() - 1) {
                writer.write("\n");
            } else {
                writer.write(",\n");
            }
        }
        writer.write("}");
        writer.close();

    }

    public static boolean isFileExt(String fileName) {
        for (String ext : FILE_EXTS) {
            if (fileName.toLowerCase().endsWith("." + ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> extractI18n(Path file) {
        Set<String> keys = new HashSet<>();
        try {
            String content = new String(Files.readAllBytes(file));
            Matcher m1 = tPattern.matcher(content);
            while (m1.find()) {
                String foundling = m1.group(1);
                if (foundling.length() < 3) {
                    continue;
                }
                keys.add(foundling);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keys;
    }

}
