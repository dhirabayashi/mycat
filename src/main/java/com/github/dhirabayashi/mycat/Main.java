package com.github.dhirabayashi.mycat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        var argument = new Argument();
        JCommander.newBuilder()
                .addObject(argument)
                .build()
                .parse(args);

        for(var arg : argument.files) {
            var file = Path.of(arg);
            if(!Files.exists(file)) {
                System.err.println("mycat: " + file + ": No such file or directory");
                continue;
            }
            System.out.println(cat(file));
        }
    }

    static String cat(Path path) throws IOException {
        var lines = Files.readAllLines(path);
        return String.join("\n", lines);
    }

    public static class Argument {
        @Parameter(description = "files")
        private List<String> files;

        @Parameter(names = "-n", description = "Number the output lines, starting at 1.")
        private boolean n;
    }
}
