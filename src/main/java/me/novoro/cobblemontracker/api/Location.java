package me.novoro.cobblemontracker.api;

import me.novoro.cobblemontracker.api.configuration.Configuration;
import me.novoro.cobblemontracker.utils.LocationUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a coordinate location in a {@link ServerWorld}, with optional rotation values.
 */
public class Location {
    private ServerWorld world;
    private double x;
    private double y;
    private double z;
    private Float pitch = null;
    private Float yaw = null;

    public Location(Configuration config) {
        String worldName = config.getString("world", null);
        if (worldName == null) this.world = null;
        else this.world = LocationUtil.getWorld(worldName);
        this.x = config.getDouble("x");
        this.y = config.getDouble("y");
        this.z = config.getDouble("z");
        float pitch = config.getFloat("pitch", -1000);
        this.pitch = (pitch != -1000) ? pitch : null;
        float yaw = config.getFloat("yaw", -1000);
        this.yaw = (yaw != -1000) ? yaw : null;
    }

    public Location(ServerPlayerEntity player) {
        this.world = player.getServerWorld();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.pitch = player.getPitch();
        this.yaw = player.getYaw();
    }

    public Location(ServerWorld world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(ServerWorld world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void teleport(ServerPlayerEntity player) {
        float tpPitch = (this.pitch != null) ? this.pitch : player.getPitch();
        float tpYaw = (this.yaw != null) ? this.yaw : player.getYaw();
        player.teleport(this.world, this.x, this.y, this.z, tpYaw, tpPitch);
    }

    public boolean isEqualTo(Location location) {
        if (!this.world.equals(location.getWorld())) return false;
        if (this.x != location.getX()) return false;
        if (this.y != location.getY()) return false;
        return this.z == location.getZ();
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public @Nullable Float getPitch() {
        return this.pitch;
    }

    public @Nullable Float getYaw() {
        return this.yaw;
    }

    public BlockPos getBlockPos() {
        return new BlockPos((int) Math.round(this.x), (int) Math.round(this.y), (int) Math.round(this.z));
    }

    public WorldChunk getChunk() {
        return this.world.getWorldChunk(this.getBlockPos());
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void shift(double x, double y, double z) {
        this.shift(x, y, z, 0, 0);
    }

    public void shift(double x, double y, double z, float pitch, float yaw) {
        this.x += x;
        this.y += y;
        this.z += z;
        if (this.pitch != null) this.pitch += pitch;
        if (this.yaw != null) this.yaw += yaw;
    }

    public Location withWorld(ServerWorld world) {
        this.world = world;
        return this;
    }

    public Location shifted(double x, double y, double z) {
        return this.shifted(x, y, z, 0, 0);
    }

    public Location shifted(double x, double y, double z, float pitch, float yaw) {
        this.shift(x, y, z, pitch, yaw);
        return this;
    }

    public Location copy() {
        return new Location(this.world, this.x, this.y, this.z, this.pitch, this.yaw);
    }

    public void addReplacements(Map<String, String> replacements) {
        replacements.put("{x}", String.valueOf(this.x));
        replacements.put("{y}", String.valueOf(this.y));
        replacements.put("{z}", String.valueOf(this.z));
        replacements.put("{pitch}", (this.pitch != null) ? String.valueOf(this.pitch) : "~");
        replacements.put("{yaw}", (this.yaw != null) ? String.valueOf(this.yaw) : "~");
        replacements.put("{world}", (this.world != null) ? this.world.getRegistryKey().getValue().toString() : "~");
    }

    public Configuration toConfiguration() {
        Configuration locationConfig = new Configuration();
        if (this.world != null) locationConfig.set("world", this.world.getRegistryKey().getValue().toString());
        locationConfig.set("x", this.x);
        locationConfig.set("y", this.y);
        locationConfig.set("z", this.z);
        if (this.pitch != null) locationConfig.set("pitch", this.pitch);
        if (this.yaw != null) locationConfig.set("yaw", this.yaw);
        return locationConfig;
    }
}