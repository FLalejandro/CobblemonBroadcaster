package me.novoro.cobblemontracker.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A utility class for altering Strings in various ways.
 */
public final class StringUtil {
    /**
     * Replaces placeholders in a string with those occupied by the replacements map.
     * {@link StringUtil#n(String)} and {@link StringUtil#s(String)} are fired after replacing all other placeholders.
     * @param input The string to be parsed.
     * @param replacements The placeholders to parse.
     */
    public static String replaceReplacements(String input, @Nullable Map<String, String> replacements) {
        if (input == null) return null;
        if (replacements != null) {
            for (Map.Entry<String, String> replacer : replacements.entrySet()) {
                input = input.replace(replacer.getKey(), replacer.getValue());
            }
        }
        input = n(input);
        input = s(input);
        return input;
    }

    /**
     * Replaces {n} in a string based on what character follows it after a space.
     * I.E. "A{n} Pokémon" would turn to "A Pokémon" and "A{n} Apple" would turn to "An Apple".
     * @param string The string to be parsed.
     */
    public static String n(String string) {
        return string.replaceAll("\\{n} (&#?[a-fA-F0-9]+)?([aeiouAEIOU])", "n $1$2")
                .replace("{n}", "");
    }

    /**
     * Replaces {s} in a string based on what character is before it. Used for possession of objects.
     * I.E. "Novoro{s} Pokémon" would turn to "Novoro's Pokémon" and "James{s} Pokémon" would turn to "James' Pokémon".
     * @param string The string to be parsed.
     */
    public static String s(String string) {
        return string.replaceAll("([sS])\\{s}", "$1'").replace("{s}", "'s");
    }

    /**
     * Takes a string and parses placeholders, then attempts to turn that string into a double or integer.
     * If it's not a double or integer, this just returns a string that has parsed the replacements.
     * Mainly used to replace objects for custom NBT data on items.
     */
    public static Object replaceObject(String input, Map<String, String> replacements) {
        String parsed = StringUtil.replaceReplacements(input, replacements);
        try {
            double doubleVal = Double.parseDouble(parsed);
            try {
                return Integer.parseInt(parsed);
            } catch (NumberFormatException e) {
                return doubleVal;
            }
        } catch (NumberFormatException e) {
            return parsed;
        }
    }
}
