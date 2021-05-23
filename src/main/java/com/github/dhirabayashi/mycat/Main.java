package com.github.dhirabayashi.mycat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.dhirabayashi.mycat.util.Counter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Map<Integer, String> nonPrintingTable = new HashMap<>();

    static {
        nonPrintingTable.put(0x00, "^@");
        nonPrintingTable.put(0x01, "^A");
        nonPrintingTable.put(0x02, "^B");
        nonPrintingTable.put(0x03, "^C");
        nonPrintingTable.put(0x04, "^D");
        nonPrintingTable.put(0x05, "^E");
        nonPrintingTable.put(0x06, "^F");
        nonPrintingTable.put(0x07, "^G");
        nonPrintingTable.put(0x08, "^H");
        //nonPrintingTable.put(0x09, "^I");
        //nonPrintingTable.put(0x0A, "^J");
        nonPrintingTable.put(0x0B, "^K");
        nonPrintingTable.put(0x0C, "^L");
        nonPrintingTable.put(0x0D, "^M");
        nonPrintingTable.put(0x0E, "^N");
        nonPrintingTable.put(0x0F, "^O");
        nonPrintingTable.put(0x10, "^P");
        nonPrintingTable.put(0x11, "^Q");
        nonPrintingTable.put(0x12, "^R");
        nonPrintingTable.put(0x13, "^S");
        nonPrintingTable.put(0x14, "^T");
        nonPrintingTable.put(0x15, "^U");
        nonPrintingTable.put(0x16, "^V");
        nonPrintingTable.put(0x17, "^W");
        nonPrintingTable.put(0x18, "^X");
        nonPrintingTable.put(0x19, "^Y");
        nonPrintingTable.put(0x1A, "^Z");
        nonPrintingTable.put(0x1B, "^[");
        nonPrintingTable.put(0x1C, "^\\");
        nonPrintingTable.put(0x1D, "^]");
        nonPrintingTable.put(0x1E, "^^");
        nonPrintingTable.put(0x1F, "^_");
        nonPrintingTable.put(0x7F, "^?");
    }

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
        if(argument.s) {
            options.add(Options.SQUEEZE_EMPTY_LINES);
        }
        if(argument.v) {
            options.add(Options.DISPLAY_NON_PRINTING_CHARACTERS);
        }
        if(argument.t) {
            options.add(Options.DISPLAY_NON_PRINTING_CHARACTERS);
            options.add(Options.DISPLAY_NON_PRINTING_AND_TAB);
        }

        // 終了コード
        int exitCode = 0;

        // 実行
        for(var arg : argument.files) {
            var file = Path.of(arg);
            if(!Files.exists(file)) {
                System.err.println("mycat: " + file + ": No such file or directory");
                exitCode = 1;
                continue;
            }
            System.out.println(cat(file, options.toArray(Options[]::new)));
            // バッファリングの無効化（やり方がわからないのでとりあえずフラッシュしておくｗ）
            if(argument.u) {
                System.out.flush();
            }
        }
        System.exit(exitCode);
    }

    static String cat(Path path, Options... options) throws IOException {
        var optionsSet = Arrays.stream(options)
                .collect(Collectors.toSet());

        try(var lines = Files.lines(path)) {
            List<String> tmpList = lines.collect(Collectors.toList());
            List<String> list = new ArrayList<>();

            if(optionsSet.contains(Options.SQUEEZE_EMPTY_LINES)) {
                for(int i = 0; i < tmpList.size(); i++) {
                    var line = tmpList.get(i);
                    if(line.isEmpty() && i != (tmpList.size() - 1) && tmpList.get(i + 1).isEmpty()) {
                        continue;
                    }
                    list.add(line);
                }
            } else {
                list = tmpList;
            }

            var counter = new Counter(0);
            return list.stream()
                    .map(line -> {
                        if(optionsSet.contains(Options.DISPLAY_NON_PRINTING_CHARACTERS)) {
                            // Arrays.stream()にbyte[]を引数に取るものがないので、int[]に詰め替える
                            var tmpBytes = line.getBytes(StandardCharsets.UTF_8);
                            int[] bytes = new int[tmpBytes.length];
                            for(int i = 0; i < bytes.length; i++) {
                                bytes[i] = tmpBytes[i];
                            }

                            line = Arrays.stream(bytes)
                                    .mapToObj(i -> {
                                        // マルチバイト文字
                                        if(i < 0) {
                                            return "M-^" + (char)(i + 192);
                                        }

                                        // タブ文字
                                        if(optionsSet.contains(Options.DISPLAY_NON_PRINTING_AND_TAB)
                                                && i == 0x09) {
                                            return "^I";
                                        }

                                        // それ以外
                                        return nonPrintingTable.getOrDefault(i, Character.toString((char)i));
                                    })
                                    .collect(Collectors.joining());
                        }

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

        @Parameter(names = "-s", description = "Squeeze multiple adjacent empty lines, causing the output to be single spaced.")
        private boolean s;

        @Parameter(names = "-v", description = "Display non-printing characters so they are visible.")
        private boolean v;

        @Parameter(names = "-u", description = "Disable output buffering.")
        private boolean u;

        @Parameter(names = "-t", description = "Display non-printing characters, and display tab characters as `^I'.")
        private boolean t;
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
    NUMBER_NON_BLANK_LINES,

    /**
     * 連続する空行を一行にする
     */
    SQUEEZE_EMPTY_LINES,

    /**
     * 非表示文字を表示する
     */
    DISPLAY_NON_PRINTING_CHARACTERS,

    /**
     * 非表示文字とタブ文字を表示する
     */
    DISPLAY_NON_PRINTING_AND_TAB,
}
