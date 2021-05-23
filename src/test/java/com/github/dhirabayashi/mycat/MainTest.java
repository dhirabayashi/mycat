package com.github.dhirabayashi.mycat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.dhirabayashi.mycat.Options.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void cat(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        var expected = "aaa";
        Files.writeString(file, expected);

        // run
        assertEquals(expected, Main.cat(file));
    }

    @Test
    void cat_multiLine(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        var expected = "aaa\nbbb";
        Files.writeString(file, expected);

        // run
        assertEquals(expected, Main.cat(file));
    }

    @Test
    void cat_numberLines(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\n\nbbb");

        var expected = "1 aaa\n2 \n3 bbb";

        // run
        assertEquals(expected, Main.cat(file, NUMBER_LINES));
    }

    @Test
    void cat_numberNonBlankLines(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\n\nbbb");

        var expected = "1 aaa\n\n2 bbb";

        // run
        assertEquals(expected, Main.cat(file, NUMBER_NON_BLANK_LINES));
    }

    @Test
    void cat_squeeze(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\n\n\n\n\n\nbbb");

        var expected = "aaa\n\nbbb";

        // run
        assertEquals(expected, Main.cat(file, SQUEEZE_EMPTY_LINES));
    }

    @Test
    void cat_squeeze_noEmptyLines(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\nbbb");

        var expected = "aaa\nbbb";

        // run
        assertEquals(expected, Main.cat(file, SQUEEZE_EMPTY_LINES));
    }

    @Test
    void cat_squeeze_numberLines(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\n\n\n\n\nbbb");

        var expected = "1 aaa\n2 \n3 bbb";

        // run
        assertEquals(expected, Main.cat(file, SQUEEZE_EMPTY_LINES, NUMBER_LINES));
    }

    @Test
    void cat_displayNonPrintingChar(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\t" + (char) 0x00);

        var expected = "aaa\t^@";

        // run
        assertEquals(expected, Main.cat(file, DISPLAY_NON_PRINTING_CHARACTERS));
    }

    @Test
    void cat_displayNonPrintingChar_multiBytes(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "あ");

        var expected = "M-^£M-^AM-^B";

        // run
        assertEquals(expected, Main.cat(file, DISPLAY_NON_PRINTING_CHARACTERS));
    }

    @Test
    void cat_displayNonPrintingCharAndTab(@TempDir Path tempDir) throws IOException {
        // setup
        var file = tempDir.resolve("test.txt");
        Files.writeString(file, "aaa\t" + (char) 0x00);

        var expected = "aaa^I^@";

        // run
        assertEquals(expected, Main.cat(file, DISPLAY_NON_PRINTING_CHARACTERS, DISPLAY_NON_PRINTING_AND_TAB));
    }

}