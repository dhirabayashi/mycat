package com.github.dhirabayashi.mycat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.dhirabayashi.mycat.util.Counter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // コマンドのパース
        var argument = new Argument();
        JCommander.newBuilder()
                .addObject(argument)
                .build()
                .parse(args);

        // オプションの設定
        List<Options> options = new ArrayList<>();
        if(argument.n) {
            options.add(Options.NUMBER_LINES);
        }
        if(argument.b) {
            options.add(Options.NUMBER_NON_BLANK_LINES);
        }

        // 実行
        for(var arg : argument.files) {
            var file = Path.of(arg);
            if(!Files.exists(file)) {
                System.err.println("mycat: " + file + ": No such file or directory");
                continue;
            }
            System.out.println(cat(file, options.toArray(Options[]::new)));
        }
    }

    static String cat(Path path, Options... options) throws IOException {
        var optionsSet = Arrays.stream(options)
                .collect(Collectors.toSet());

        try(var lines = Files.lines(path)) {
            var counter = new Counter(0);
            return lines.map(line -> {
                        if(optionsSet.contains(Options.NUMBER_LINES)) {
                            counter.increment();
                            return counter.intValue() + " " + line;
                        }

                        if(optionsSet.contains(Options.NUMBER_NON_BLANK_LINES) && !line.isEmpty()) {
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
enum Options {
    /**
     * 行番号をつける
     */
    NUMBER_LINES,
    /**
     * 空行以外に行番号をつける
     */
    NUMBER_NON_BLANK_LINES
}
