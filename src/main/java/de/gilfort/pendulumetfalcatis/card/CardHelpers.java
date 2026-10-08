package de.gilfort.pendulumetfalcatis.card;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Shared helpers for card effects: targeting, area queries and the empowered strike. */
public final class CardHelpers {
    /** Players with an armed empowered strike. */
    private static final Map<Player, EmpoweredStrike> EMPOWERED_STRIKES = new WeakHashMap<>();

    private record EmpoweredStrike(long expiry, float multiplier) {
    }

    private CardHelpers() {
    }

    /** The first living entity on the player's line of sight within {@code range}, not hidden behind blocks. */
    public static @Nullable LivingEntity findTarget(Player player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(range));
        HitResult blockHit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        AABB searchArea = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);
        LivingEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, searchArea, e -> e != player && e.isAlive() && e.isPickable())) {
            Optional<Vec3> hit = entity.getBoundingBox().inflate(0.3).clip(eye, end);
            if (hit.isPresent()) {
                double distance = eye.distanceToSqr(hit.get());
                if (distance < closestDistance) {
                    closest = entity;
                    closestDistance = distance;
                }
            }
        }
        return closest;
    }

    /** Living entities other than the player within {@code radius}. */
    public static List<LivingEntity> entitiesAround(Player player, double radius) {
        return player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius),
                e -> e != player && e.isAlive() && player.distanceTo(e) <= radius);
    }

    /** Living entities within {@code radius} in an arc in front of the player. */
    public static List<LivingEntity> entitiesInFront(Player player, double radius) {
        Vec3 look = player.getLookAngle().multiply(1, 0, 1).normalize();
        return entitiesAround(player, radius).stream()
                .filter(e -> e.position().subtract(player.position()).multiply(1, 0, 1).normalize().dot(look) > 0.3)
                .toList();
    }

    /** Arms an empowered strike: the player's next melee hit within {@code durationTicks} deals {@code multiplier} times the damage. */
    public static void armEmpoweredStrike(Player player, int durationTicks, float multiplier) {
        EMPOWERED_STRIKES.put(player, new EmpoweredStrike(player.level().getGameTime() + durationTicks, multiplier));
    }

    /** Consumes an armed empowered strike and returns its damage multiplier, or 1 if none is armed or it expired. */
    public static float consumeEmpoweredStrike(Player player) {
        EmpoweredStrike strike = EMPOWERED_STRIKES.remove(player);
        return strike != null && player.level().getGameTime() <= strike.expiry() ? strike.multiplier() : 1.0F;
    }

    /** The up to {@code count} closest living entities other than the player within {@code radius}. */
    public static List<LivingEntity> closestAround(Player player, double radius, int count) {
        return entitiesAround(player, radius).stream()
                .sorted(Comparator.comparingDouble(e -> player.distanceToSqr(e)))
                .limit(count)
                .toList();
    }

    /** Strikes a lightning bolt at the entity's position, credited to the player. */
    public static void strikeLightning(ServerLevel level, ServerPlayer cause, Entity at) {
        LightningBolt bolt = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
        bolt.setPos(at.getX(), at.getY(), at.getZ());
        bolt.setCause(cause);
        level.addFreshEntity(bolt);
    }
}
