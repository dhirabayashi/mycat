package com.github.dhirabayashi.mycat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.dhirabayashi.mycat.util.Counter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
            System.out.println(cat(file, argument.n, argument.b));
        }
    }

    static String cat(Path path, boolean numberLines, boolean numberNonBlankLine) throws IOException {
        try(var lines = Files.lines(path)) {
            var counter = new Counter(0);
            return lines.map(line -> {
                        if(numberLines) {
                            counter.increment();
                            return counter.intValue() + " " + line;
                        }

                        if(numberNonBlankLine && !line.isEmpty()) {
                            counter.increment();
                            return counter.intValue() + " " + line;
                        }

                        return line;
                    })
                    .collect(Collectors.joining("\n"));
        }
    }

    public static class Argument {
        @Parameter(description = "files")
        private List<String> files;

        @Parameter(names = "-n", description = "Number the output lines, starting at 1.")
        private boolean n;

        @Parameter(names = "-b", description = "Number the non-blank output lines, starting at 1.")
        private boolean b;
    }
}
