package de.gilfort.pendulumetfalcatis.card;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Effects of the Major Arcana cards, upright and reversed. Passive values scale with the effect's
 * strength, which is 1 for a single copy of the card. Reversed sides are stronger but have a drawback.
 * Readings (one-time use) are longer or stronger versions of the card's own tool effects.
 */
public final class MajorArcana {
    private MajorArcana() {
    }

    // I – The Magician: raw power.

    public static TarotCard.Side magician() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(5, ctx -> arcaneBolt(ctx, 1, 6.0F)),
                        attributes(new Mod("magician_damage", Attributes.ATTACK_DAMAGE, 2.0, Operation.ADD_VALUE))),
                new TarotCard.Effects(
                        ActiveEffect.of(8, ctx -> applyEffect(ctx.player(), MobEffects.ABSORPTION, 100, 1)),
                        attributes(new Mod("magician_armor", Attributes.ARMOR, 2.0, Operation.ADD_VALUE))),
                // Reading: the arcane barrier for a whole minute.
                ActiveEffect.of(0, ctx -> applyEffect(ctx.player(), MobEffects.ABSORPTION, 1200, 1)));
    }

    public static TarotCard.Side magicianReversed() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        // Chain bolt: hits the target and jumps to up to 2 more enemies nearby.
                        ActiveEffect.of(8, ctx -> arcaneBolt(ctx, 3, 5.0F)),
                        attributes(new Mod("magician_reversed_damage", Attributes.ATTACK_DAMAGE, 4.0, Operation.ADD_VALUE),
                                new Mod("magician_reversed_armor", Attributes.ARMOR, -2.0, Operation.ADD_VALUE))),
                new TarotCard.Effects(
                        ActiveEffect.of(10, ctx -> applyEffect(ctx.player(), MobEffects.ABSORPTION, 100, 3)
                                && applyEffect(ctx.player(), MobEffects.SLOWNESS, 100, 0)),
                        attributes(new Mod("magician_reversed_pendulum_armor", Attributes.ARMOR, 4.0, Operation.ADD_VALUE),
                                new Mod("magician_reversed_speed", Attributes.MOVEMENT_SPEED, -0.1, Operation.ADD_MULTIPLIED_TOTAL))),
                ActiveEffect.of(0, ctx -> applyEffect(ctx.player(), MobEffects.ABSORPTION, 600, 3)
                        && applyEffect(ctx.player(), MobEffects.SLOWNESS, 200, 0)));
    }

    // IV – The Emperor: steadfastness.

    public static TarotCard.Side emperor() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(10, ctx -> applyEffect(ctx.player(), MobEffects.STRENGTH, 160, 0)),
                        attributes(new Mod("emperor_attack_speed", Attributes.ATTACK_SPEED, 0.15, Operation.ADD_MULTIPLIED_BASE))),
                new TarotCard.Effects(
                        ActiveEffect.of(6, ctx -> shockwave(ctx, 4, 0.0F)),
                        attributes(new Mod("emperor_toughness", Attributes.ARMOR_TOUGHNESS, 2.0, Operation.ADD_VALUE),
                                new Mod("emperor_knockback_resistance", Attributes.KNOCKBACK_RESISTANCE, 0.2, Operation.ADD_VALUE))),
                // Reading: the war cry for 90 seconds.
                ActiveEffect.of(0, ctx -> applyEffect(ctx.player(), MobEffects.STRENGTH, 1800, 0)));
    }

    public static TarotCard.Side emperorReversed() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(10, ctx -> applyEffect(ctx.player(), MobEffects.STRENGTH, 160, 1)
                                && applyEffect(ctx.player(), MobEffects.HUNGER, 160, 0)),
                        attributes(new Mod("emperor_reversed_attack_speed", Attributes.ATTACK_SPEED, 0.3, Operation.ADD_MULTIPLIED_BASE),
                                new Mod("emperor_reversed_armor", Attributes.ARMOR, -2.0, Operation.ADD_VALUE))),
                new TarotCard.Effects(
                        // Crushing shockwave: 6 blocks and 3 damage, but costs you 1 heart.
                        ActiveEffect.of(8, ctx -> {
                            if (!shockwave(ctx, 6, 3.0F)) {
                                return false;
                            }
                            ctx.player().hurtServer(ctx.level(), ctx.level().damageSources().magic(), 2.0F);
                            return true;
                        }),
                        attributes(new Mod("emperor_reversed_toughness", Attributes.ARMOR_TOUGHNESS, 4.0, Operation.ADD_VALUE),
                                new Mod("emperor_reversed_knockback_resistance", Attributes.KNOCKBACK_RESISTANCE, 0.4, Operation.ADD_VALUE),
                                new Mod("emperor_reversed_speed", Attributes.MOVEMENT_SPEED, -0.1, Operation.ADD_MULTIPLIED_TOTAL))),
                ActiveEffect.of(0, ctx -> applyEffect(ctx.player(), MobEffects.STRENGTH, 1200, 1)
                        && applyEffect(ctx.player(), MobEffects.HUNGER, 600, 0)));
    }

    // XIII – Death: reaping.

    public static TarotCard.Side death() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(8, ctx -> reapersSweep(ctx, 0.0F)),
                        new PassiveEffect() {
                            @Override
                            public void onKill(Context context, LivingEntity victim) {
                                context.player().addEffect(new MobEffectInstance(MobEffects.REGENERATION, scaled(200, context), 0));
                            }
                        }),
                new TarotCard.Effects(
                        ActiveEffect.of(8, ctx -> curseArea(ctx, 5, 100, MobEffects.WEAKNESS, 0) | curseArea(ctx, 5, 100, MobEffects.SLOWNESS, 0)),
                        new PassiveEffect() {
                            @Override
                            public float modifyIncomingDamage(Context context, DamageSource source, float damage) {
                                return Math.max(0.0F, damage - 1.0F * context.strength());
                            }
                        }),
                // Reading: a wider, longer breath of death.
                ActiveEffect.of(0, ctx -> curseArea(ctx, 8, 300, MobEffects.WEAKNESS, 0) | curseArea(ctx, 8, 300, MobEffects.SLOWNESS, 1)));
    }

    public static TarotCard.Side deathReversed() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        // Soul harvest: the reaper's sweep heals 1 heart per enemy hit.
                        ActiveEffect.of(12, ctx -> reapersSweep(ctx, 2.0F)),
                        new PassiveEffect() {
                            @Override
                            public void onKill(Context context, LivingEntity victim) {
                                context.player().addEffect(new MobEffectInstance(MobEffects.REGENERATION, scaled(200, context), 1));
                                context.player().addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0));
                            }
                        }),
                new TarotCard.Effects(
                        ActiveEffect.of(8, ctx -> curseArea(ctx, 5, 100, MobEffects.WITHER, 1)
                                && applyEffect(ctx.player(), MobEffects.WEAKNESS, 100, 0)),
                        new PassiveEffect() {
                            @Override
                            public void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
                                modifiers.accept(Attributes.ATTACK_DAMAGE, modifier("death_reversed_damage", -0.1 * strength, Operation.ADD_MULTIPLIED_TOTAL));
                            }

                            @Override
                            public float modifyIncomingDamage(Context context, DamageSource source, float damage) {
                                return Math.max(0.0F, damage - 2.0F * context.strength());
                            }
                        }),
                ActiveEffect.of(0, ctx -> curseArea(ctx, 8, 200, MobEffects.WITHER, 1)
                        && applyEffect(ctx.player(), MobEffects.WEAKNESS, 200, 0)));
    }

    // XVI – The Tower: retribution.

    public static TarotCard.Side tower() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(6, ctx -> {
                            CardHelpers.armEmpoweredStrike(ctx.player(), 100, 2.0F);
                            playSound(ctx.player(), SoundEvents.ANVIL_LAND);
                            return true;
                        }),
                        new PassiveEffect() {
                            @Override
                            public float modifyOutgoingDamage(Context context, LivingEntity target, DamageSource source, float damage) {
                                target.igniteForSeconds(3.0F * context.strength());
                                return damage;
                            }
                        }),
                new TarotCard.Effects(
                        ActiveEffect.of(15, ctx -> {
                            LivingEntity target = CardHelpers.findTarget(ctx.player(), 16);
                            if (target == null) {
                                return false;
                            }
                            CardHelpers.strikeLightning(ctx.level(), ctx.player(), target);
                            return true;
                        }),
                        thorns("tower", 0.3F, 1.0F)),
                // Reading: lightning on up to 3 enemies within 8 blocks.
                ActiveEffect.of(0, ctx -> lightningStorm(ctx, 8, 3, false)));
    }

    public static TarotCard.Side towerReversed() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        // Ruin: the next hit within 5 seconds deals triple damage, but costs you 1 heart.
                        ActiveEffect.of(10, ctx -> {
                            CardHelpers.armEmpoweredStrike(ctx.player(), 100, 3.0F);
                            ctx.player().hurtServer(ctx.level(), ctx.level().damageSources().magic(), 2.0F);
                            playSound(ctx.player(), SoundEvents.ANVIL_LAND);
                            return true;
                        }),
                        new PassiveEffect() {
                            @Override
                            public float modifyOutgoingDamage(Context context, LivingEntity target, DamageSource source, float damage) {
                                target.igniteForSeconds(6.0F * context.strength());
                                return damage;
                            }

                            @Override
                            public float modifyIncomingDamage(Context context, DamageSource source, float damage) {
                                return source.is(DamageTypeTags.IS_FIRE) ? damage * (1.0F + 0.5F * context.strength()) : damage;
                            }
                        }),
                new TarotCard.Effects(
                        // Storm: lightning on up to 5 enemies within 8 blocks, and one on yourself.
                        ActiveEffect.of(25, ctx -> lightningStorm(ctx, 8, 5, true)),
                        thorns("tower_reversed", 0.6F, 2.0F, new Mod("tower_reversed_armor", Attributes.ARMOR, -2.0, Operation.ADD_VALUE))),
                ActiveEffect.of(0, ctx -> lightningStorm(ctx, 10, 5, true)));
    }

    // XVII – The Star: healing.

    public static TarotCard.Side star() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(10, ctx -> heal(ctx.player(), 6.0F)),
                        lifeSteal(0.1F)),
                new TarotCard.Effects(
                        ActiveEffect.of(8, ctx -> cleanse(ctx.player()) && applyEffect(ctx.player(), MobEffects.RESISTANCE, 60, 0)),
                        new PassiveEffect() {
                            @Override
                            public void tick(Context context) {
                                // Half a heart every 4 seconds; tick() runs once per second.
                                if (context.player().tickCount % 80 < 20) {
                                    context.player().heal(1.0F * context.strength());
                                }
                            }
                        }),
                // Reading: full heal and cleansing at once.
                ActiveEffect.of(0, ctx -> {
                    ctx.player().heal(ctx.player().getMaxHealth());
                    cleanse(ctx.player());
                    playSound(ctx.player(), SoundEvents.AMETHYST_BLOCK_CHIME);
                    return true;
                }));
    }

    public static TarotCard.Side starReversed() {
        return new TarotCard.Side(
                new TarotCard.Effects(
                        ActiveEffect.of(15, ctx -> heal(ctx.player(), 12.0F) && applyEffect(ctx.player(), MobEffects.HUNGER, 200, 0)),
                        new PassiveEffect() {
                            @Override
                            public void onDamageDealt(Context context, LivingEntity target, DamageSource source, float dealt) {
                                context.player().heal(dealt * 0.2F * context.strength());
                            }

                            @Override
                            public float modifyIncomingDamage(Context context, DamageSource source, float damage) {
                                return damage * (1.0F + 0.1F * context.strength());
                            }
                        }),
                new TarotCard.Effects(
                        ActiveEffect.of(12, ctx -> cleanse(ctx.player())
                                && applyEffect(ctx.player(), MobEffects.RESISTANCE, 100, 1)
                                && applyEffect(ctx.player(), MobEffects.SLOWNESS, 100, 0)),
                        new PassiveEffect() {
                            @Override
                            public void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
                                modifiers.accept(Attributes.ATTACK_DAMAGE, modifier("star_reversed_damage", -2.0 * strength, Operation.ADD_VALUE));
                            }

                            @Override
                            public void tick(Context context) {
                                // Half a heart every 2 seconds.
                                if (context.player().tickCount % 40 < 20) {
                                    context.player().heal(1.0F * context.strength());
                                }
                            }
                        }),
                ActiveEffect.of(0, ctx -> {
                    ctx.player().heal(ctx.player().getMaxHealth());
                    return applyEffect(ctx.player(), MobEffects.RESISTANCE, 600, 1)
                            && applyEffect(ctx.player(), MobEffects.HUNGER, 600, 0);
                }));
    }

    // Shared effect building blocks.

    /** Hits the first enemy on the line of sight; with {@code targets > 1} it then jumps to the closest enemies near it. */
    private static boolean arcaneBolt(ActiveEffect.Context ctx, int targets, float damage) {
        LivingEntity first = CardHelpers.findTarget(ctx.player(), 12);
        if (first == null) {
            return false;
        }
        DamageSource source = ctx.level().damageSources().indirectMagic(ctx.player(), ctx.player());
        first.hurtServer(ctx.level(), source, damage);
        if (targets > 1) {
            ctx.level().getEntitiesOfClass(LivingEntity.class, first.getBoundingBox().inflate(6.0),
                            e -> e != first && e != ctx.player() && e.isAlive())
                    .stream()
                    .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(first)))
                    .limit(targets - 1)
                    .forEach(e -> e.hurtServer(ctx.level(), source, damage));
        }
        playSound(ctx.player(), SoundEvents.EVOKER_CAST_SPELL);
        return true;
    }

    /** Knocks back everything within {@code radius}, dealing {@code damage} if positive. */
    private static boolean shockwave(ActiveEffect.Context ctx, double radius, float damage) {
        List<LivingEntity> targets = CardHelpers.entitiesAround(ctx.player(), radius);
        DamageSource source = ctx.level().damageSources().playerAttack(ctx.player());
        for (LivingEntity target : targets) {
            Vec3 away = target.position().subtract(ctx.player().position());
            if (damage > 0) {
                target.hurtServer(ctx.level(), source, damage);
            }
            target.knockback(1.5, -away.x, -away.z, source, damage);
        }
        playSound(ctx.player(), SoundEvents.PLAYER_ATTACK_KNOCKBACK);
        return !targets.isEmpty();
    }

    /** 5 damage and 3 s of Wither to everything in front; heals {@code healPerHit} per target hit. */
    private static boolean reapersSweep(ActiveEffect.Context ctx, float healPerHit) {
        List<LivingEntity> targets = CardHelpers.entitiesInFront(ctx.player(), 3.5);
        DamageSource source = ctx.level().damageSources().playerAttack(ctx.player());
        for (LivingEntity target : targets) {
            target.hurtServer(ctx.level(), source, 5.0F);
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0), ctx.player());
        }
        if (healPerHit > 0) {
            ctx.player().heal(healPerHit * targets.size());
        }
        playSound(ctx.player(), SoundEvents.PLAYER_ATTACK_SWEEP);
        return !targets.isEmpty();
    }

    /** Applies an effect to everything within {@code radius}; returns whether anything was hit. */
    private static boolean curseArea(ActiveEffect.Context ctx, double radius, int durationTicks, Holder<MobEffect> effect, int amplifier) {
        List<LivingEntity> targets = CardHelpers.entitiesAround(ctx.player(), radius);
        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(effect, durationTicks, amplifier), ctx.player());
        }
        if (!targets.isEmpty()) {
            playSound(ctx.player(), SoundEvents.WITHER_AMBIENT);
        }
        return !targets.isEmpty();
    }

    /** Lightning on up to {@code count} of the closest entities within {@code radius}, optionally on the player too. */
    private static boolean lightningStorm(ActiveEffect.Context ctx, double radius, int count, boolean hitsSelf) {
        List<LivingEntity> targets = CardHelpers.closestAround(ctx.player(), radius, count);
        if (targets.isEmpty()) {
            return false;
        }
        targets.forEach(target -> CardHelpers.strikeLightning(ctx.level(), ctx.player(), target));
        if (hitsSelf) {
            CardHelpers.strikeLightning(ctx.level(), ctx.player(), ctx.player());
        }
        return true;
    }

    /** Blocking reflects {@code fraction} of the blocked damage, at least {@code minimum}, plus optional attribute drawbacks. */
    private static PassiveEffect thorns(String name, float fraction, float minimum, Mod... drawbacks) {
        PassiveEffect drawbackModifiers = attributes(drawbacks);
        return new PassiveEffect() {
            @Override
            public void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
                drawbackModifiers.addAttributeModifiers(modifiers, strength);
            }

            @Override
            public void onBlock(Context context, DamageSource source, float blockedDamage) {
                if (source.getEntity() instanceof LivingEntity attacker && attacker != context.player()
                        && context.player().level() instanceof ServerLevel level) {
                    float reflected = Math.max(minimum, blockedDamage * fraction * context.strength());
                    attacker.hurtServer(level, level.damageSources().thorns(context.player()), reflected);
                }
            }
        };
    }

    private static PassiveEffect lifeSteal(float fraction) {
        return new PassiveEffect() {
            @Override
            public void onDamageDealt(Context context, LivingEntity target, DamageSource source, float dealt) {
                context.player().heal(dealt * fraction * context.strength());
            }
        };
    }

    /** An attribute modifier definition; its amount is scaled by the passive strength. */
    private record Mod(String name, Holder<Attribute> attribute, double amount, Operation operation) {
    }

    private static PassiveEffect attributes(Mod... mods) {
        return new PassiveEffect() {
            @Override
            public void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
                for (Mod mod : mods) {
                    modifiers.accept(mod.attribute(), modifier(mod.name(), mod.amount() * strength, mod.operation()));
                }
            }
        };
    }

    private static AttributeModifier modifier(String name, double amount, Operation operation) {
        return new AttributeModifier(PendulumEtFalcatis.id("card/" + name), amount, operation);
    }

    private static int scaled(int ticks, PassiveEffect.Context context) {
        return Math.round(ticks * context.strength());
    }

    private static boolean applyEffect(Player player, Holder<MobEffect> effect, int durationTicks, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
        playSound(player, SoundEvents.EVOKER_CAST_SPELL);
        return true;
    }

    private static boolean heal(Player player, float amount) {
        if (player.getHealth() >= player.getMaxHealth()) {
            return false;
        }
        player.heal(amount);
        playSound(player, SoundEvents.AMETHYST_BLOCK_CHIME);
        return true;
    }

    /** Removes all harmful effects. Always succeeds, so it can be chained with other effects. */
    private static boolean cleanse(Player player) {
        List<Holder<MobEffect>> harmful = player.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
                .toList();
        harmful.forEach(player::removeEffect);
        playSound(player, SoundEvents.AMETHYST_BLOCK_CHIME);
        return true;
    }

    private static void playSound(Player player, SoundEvent sound) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
