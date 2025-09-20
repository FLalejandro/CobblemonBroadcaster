package me.novoro.cobblemontracker.config;

import me.novoro.cobblemontracker.api.configuration.Configuration;
import me.novoro.cobblemontracker.api.configuration.VersionedConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SettingsManager extends VersionedConfig {
    private static boolean feedFillsSaturation;
    private static boolean clearInventoryRequiresConfirmation;
    private static long clearInventoryConfirmationTimeoutMillis;
    private static long tpaRequestTimeoutMillis;
    private static List<String> hatBlacklist;
    private static List<String> repairBlacklist;

    @Override
    protected void reload(Configuration settingsConfig) {
        super.reload(settingsConfig);
        SettingsManager.feedFillsSaturation = settingsConfig.getBoolean("Feed-Fills-Saturation");
        SettingsManager.clearInventoryRequiresConfirmation = settingsConfig.getBoolean("ClearInventory-Requires-Confirmation");
        SettingsManager.clearInventoryConfirmationTimeoutMillis = TimeUnit.SECONDS.toMillis(settingsConfig.getLong("ClearInventory-Confirmation-Timeout"));
        SettingsManager.tpaRequestTimeoutMillis = TimeUnit.SECONDS.toMillis(settingsConfig.getLong("TPA-Request-Timeout"));
        SettingsManager.hatBlacklist = settingsConfig.getStringList("Hat.Blacklisted-Items");
        SettingsManager.repairBlacklist = settingsConfig.getStringList("Repair.Blacklisted-Items");
    }

    public static boolean feedFillsSaturation() {
        return SettingsManager.feedFillsSaturation;
    }

    public static boolean clearInventoryRequiresConfirmation() {
        return SettingsManager.clearInventoryRequiresConfirmation;
    }

    public static long getClearInventoryConfirmationTimeoutMillis() {
        return clearInventoryConfirmationTimeoutMillis;
    }

    public static long getTpaRequestTimeoutMillis() {
        return tpaRequestTimeoutMillis;
    }

    public static boolean isHatBlacklisted(ItemStack item) {
        return checkBlacklist(item, hatBlacklist);
    }

    public static boolean isRepairBlacklisted(ItemStack item) {
        return checkBlacklist(item, repairBlacklist);
    }

    private static boolean checkBlacklist(ItemStack item, List<String> blacklist) {
        if (blacklist.isEmpty()) return false;

        final String itemId = Registries.ITEM.getId(item.getItem()).toString();
        CustomModelDataComponent customModelDataComponent = item.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        int customModelData = customModelDataComponent != null ? customModelDataComponent.value() : 0;

        return blacklist.contains(itemId) || blacklist.contains(itemId + ":" + customModelData);
    }

    @Override
    public double getCurrentConfigVersion() {
        return 2.0;
    }

    @Override
    protected String getConfigFileName() {
        return "settings.yml";
    }
}