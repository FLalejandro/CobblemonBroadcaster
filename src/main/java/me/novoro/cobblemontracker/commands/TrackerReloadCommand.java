package me.novoro.cobblemontracker.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.novoro.cobblemontracker.CobblemonTracker;
import me.novoro.cobblemontracker.utils.ColorUtil;
import net.minecraft.server.command.ServerCommandSource;

/**
 * CobblemonTracker's reload command.
 */
public final class TrackerReloadCommand extends CommandBase {
    public TrackerReloadCommand() {
        super("cobblemontracker", "cobblemontracker.reload", 4);
    }

    @Override
    public boolean bypassCommandCheck() {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return command.then(literal("reload")
                .executes(context -> {
                    CobblemonTracker.inst().reloadConfigs();
                    context.getSource().sendMessage(ColorUtil.parseColour(CobblemonTracker.MOD_PREFIX + "&aReloaded Configs!"));
                    return Command.SINGLE_SUCCESS;
                }));
    }
}
