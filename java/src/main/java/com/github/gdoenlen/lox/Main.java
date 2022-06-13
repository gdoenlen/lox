package com.github.gdoenlen.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@SuppressWarnings("java:S106") // s.out usage
public class Main {
    private static boolean hasError = false;

    private Main() {}

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Usage lox [script file]");
            System.exit(64);
        }

        if (args.length == 1) {
            runFile(Paths.get(args[0]));
        } else {
            runPrompt();
        }
    }

    private static void runFile(Path path) {
        String content;
        try {
            content = Files.readString(path);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        run(content);
        if (hasError) {
            System.exit(65);
        }
    }

    private static void run(String program) {
        var lexer = new Lexer(program);
        lexer.scan().forEach(System.out::println);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hasError = true;
    }

    private static void runPrompt() {
        var input = new InputStreamReader(System.in);
        var reader = new BufferedReader(input);

        for (;;) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }

            if (line == null) {
                break;
            }

            run(line);
        }
    }
}
