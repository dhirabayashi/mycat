package com.github.dhirabayashi.mycat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        for(var arg : args) {
            var file = Path.of(arg);
            if(!Files.exists(file)) {
                System.err.println("mycat: " + file + ": No such file or directory");
                continue;
            }
            System.out.println(cat(file));
        }
    }

    static String cat(Path path) throws IOException {
        return Files.readString(path);
    }
}
