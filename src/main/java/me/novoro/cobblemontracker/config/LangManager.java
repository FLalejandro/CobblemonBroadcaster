package me.novoro.cobblemontracker.config;

import me.novoro.cobblemontracker.api.configuration.Configuration;
import me.novoro.cobblemontracker.api.configuration.VersionedConfig;
import me.novoro.cobblemontracker.utils.ColorUtil;
import me.novoro.cobblemontracker.utils.StringUtil;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * CobblemonTracker's lang manager. This is used for all of CobblemonTracker's lang.
 */
public final class LangManager extends VersionedConfig {
    public static final LangManager EMPTY = new LangManager();
    private static final Map<String, String> LANG = new HashMap<>();

    @Override
    protected void reload(Configuration config) {
        super.reload(config);
        LangManager.LANG.clear();
        Configuration langSection = config.getSection("Lang");
        if (langSection == null) return;
        for (String key : langSection.getKeys()) LangManager.LANG.put(key, langSection.getString(key));
    }

    /**
     * Gets any specified lang.
     * @param langKey The key for the lang.
     */
    public static @Nullable String getLang(String langKey) {
        String lang = LangManager.LANG.get(langKey);
        if (lang == null || lang.isEmpty() || lang.isBlank()) return null;
        return lang;
    }

    /**
     * Gets any specified lang safely.
     * @param langKey The key for the lang.
     * */
    public static @NotNull String getLangSafely(String langKey) {
        return LangManager.LANG.getOrDefault(langKey, "");
    }

    /**
     * Sends any specified lang to an {@link Audience}.
     * @param key The key for the lang.
     */
    public static void sendLang(Audience audience, String key) {
        LangManager.sendLang(audience, key, null);
    }

    /**
     * Sends any specified lang to an {@link Audience} and replaces all replacements passed in.
     * @param key The key for the lang.
     * @param replacements The placeholders to parse.
     */
    public static void sendLang(Audience audience, String key, @Nullable Map<String, String> replacements) {
        String lang = LangManager.getLang(key);
        if (lang == null) return;
        lang = StringUtil.replaceReplacements(lang, replacements);
        String prefix = LangManager.getLang("Prefix");
        if (prefix != null) lang = prefix + lang;
        audience.sendMessage(ColorUtil.parseColour(lang));
    }

    public @NotNull String getOrDefault(String langKey, String def) {
        return this.LANG.getOrDefault(langKey, def);
    }

    @Override
    protected double getCurrentConfigVersion() {
        return 2.0;
    }

    @Override
    protected String getConfigFileName() {
        return "lang.yml";
    }
}

