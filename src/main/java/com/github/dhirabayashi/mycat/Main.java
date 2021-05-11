package com.github.dhirabayashi.mycat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        for(var arg : args) {
            System.out.println(cat(Path.of(arg)));
        }
    }

    static String cat(Path path) throws IOException {
        return Files.readString(path);
    }
}
