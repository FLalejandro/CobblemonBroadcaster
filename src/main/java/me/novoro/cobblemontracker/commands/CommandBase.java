package me.novoro.cobblemontracker.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.novoro.cobblemontracker.CobblemonTracker;
import me.novoro.cobblemontracker.api.permissions.PermissionProvider;
import me.novoro.cobblemontracker.config.ModuleManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * The basis for all of CobblemonTracker's commands to make registering commands easier with less repetitive code.
 */
public abstract class CommandBase {
    private final String command;
    private final String permission;
    private final int permissionLevel;
    private final String[] aliases;

    public CommandBase(String command, String permission, int permissionLevel, String... aliases) {
        this.command = command;
        this.permission = permission;
        this.permissionLevel = permissionLevel;
        this.aliases = aliases;
    }

    /**
     * Gets this command's main alias.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Gets the ID of the command group that controls this command.
     * Return null if the command has no command group.
     */
    public String getCommandGroup() {
        return null;
    }

    /**
     * Gets the ID of the module that controls this command.
     * Return null if the command has no controlling module.
     */
    public String getControllingModule() {
        return null;
    }

    /**
     * Whether this command should bypass checking if it's enabled.
     */
    public boolean bypassCommandCheck() {
        return false;
    }

    /**
     * Registers this command to the dispatcher, unless it is disabled.
     */
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!this.bypassCommandCheck() && !ModuleManager.isCommandEnabled(this)) return;
        dispatcher.register(this.getCommand(this.command));
        for (String alias : this.aliases) dispatcher.register(this.getCommand(alias));
    }

    /**
     * Gets the main logic of the command.
     * @param command A half built command that already has the command alias and permission requirement registered.
     * @return The complete logic of the command.
     */
    public abstract LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command);

    // Sets up the base command and permission node.
    private LiteralArgumentBuilder<ServerCommandSource> getCommand(String commandName) {
        return this.getCommand(literal(commandName).requires(source -> this.permission(source, this.permission, this.permissionLevel)));
    }

    /**
     * Utility method to quickly and neatly check permissions on a {@link ServerCommandSource}.
     * @see PermissionProvider#hasPermission(ServerCommandSource, String, int)
     */
    protected boolean permission(ServerCommandSource source, String permission, int level) {
        return CobblemonTracker.getPermissionProvider().hasPermission(source, permission, level);
    }

    /**
     * Utility method to quickly and neatly check permissions on a {@link ServerPlayerEntity}.
     * @see PermissionProvider#hasPermission(ServerPlayerEntity, String, int)
     */
    protected boolean permission(ServerPlayerEntity player, String permission, int level) {
        return CobblemonTracker.getPermissionProvider().hasPermission(player, permission, level);
    }

    /**
     * Utility method to quickly get a meta value on a {@link ServerCommandSource}.
     * @see PermissionProvider#getMetaValue(ServerCommandSource, String)
     */
    protected String meta(ServerCommandSource source, String metaKey) {
        return CobblemonTracker.getPermissionProvider().getMetaValue(source, metaKey);
    }

    /**
     * Utility method to quickly get a meta value on a {@link ServerPlayerEntity}.
     * @see PermissionProvider#getMetaValue(ServerPlayerEntity, String)
     */
    protected String meta(ServerPlayerEntity player, String metaKey) {
        return CobblemonTracker.getPermissionProvider().getMetaValue(player, metaKey);
    }

    /**
     * Utility method to quickly get an {@link Integer} meta value on a {@link ServerCommandSource}.
     * @see PermissionProvider#getMetaIntValue(ServerCommandSource, String)
     */
    protected Integer metaInt(ServerCommandSource source, String metaKey) {
        return CobblemonTracker.getPermissionProvider().getMetaIntValue(source, metaKey);
    }

    /**
     * Utility method to quickly get an {@link Integer} meta value on a {@link ServerPlayerEntity}.
     * @see PermissionProvider#getMetaIntValue(ServerPlayerEntity, String)
     */
    protected Integer metaInt(ServerPlayerEntity player, String metaKey) {
        return CobblemonTracker.getPermissionProvider().getMetaIntValue(player, metaKey);
    }

    /**
     * Utility method to quickly get a {@link Double} meta value on a {@link ServerCommandSource}.
     * @see PermissionProvider#getMetaDoubleValue(ServerCommandSource, String)
     */
    protected Double metaDouble(ServerCommandSource source, String metaKey) {
        return CobblemonTracker.getPermissionProvider().getMetaDoubleValue(source, metaKey);
    }

    /**
     * Utility method to quickly get a {@link Double} meta value on a {@link ServerPlayerEntity}.
     * @see PermissionProvider#getMetaDoubleValue(ServerPlayerEntity, String)
     */
    protected Double metaDouble(ServerPlayerEntity player, String metaKey) {
        return CobblemonTracker.getPermissionProvider().getMetaDoubleValue(player, metaKey);
    }

    /**
     * Utility method to make sure we never import the wrong method.
     * @see CommandManager#literal(String)
     */
    protected LiteralArgumentBuilder<ServerCommandSource> literal(String arg) {
        return CommandManager.literal(arg);
    }

    /**
     * Utility method to make sure we never import the wrong method.
     * @see CommandManager#argument(String, ArgumentType)
     */
    protected <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String arg, ArgumentType<T> type) {
        return CommandManager.argument(arg, type);
    }
}
