package com.github.dhirabayashi.mycat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

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
}