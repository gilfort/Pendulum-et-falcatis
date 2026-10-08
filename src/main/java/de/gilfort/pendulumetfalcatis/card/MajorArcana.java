package de.gilfort.pendulumetfalcatis.card;

import java.util.List;
import java.util.function.BiConsumer;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Effects of the Major Arcana cards. Passive values scale with the effect's strength,
 * which is 1 for a single copy of the card.
 */
public final class MajorArcana {
    private MajorArcana() {
    }

    // I – The Magician: raw power.

    public static TarotCard.Effects magicianScythe() {
        return new TarotCard.Effects(
                ActiveEffect.of(5, ctx -> {
                    // Arcane bolt: 6 magic damage to the first entity within 12 blocks.
                    LivingEntity target = CardHelpers.findTarget(ctx.player(), 12);
                    if (target == null) {
                        return false;
                    }
                    target.hurtServer(ctx.level(), ctx.level().damageSources().indirectMagic(ctx.player(), ctx.player()), 6.0F);
                    playSound(ctx.player(), SoundEvents.EVOKER_CAST_SPELL);
                    return true;
                }),
                attribute("magician_damage", Attributes.ATTACK_DAMAGE, 2.0, AttributeModifier.Operation.ADD_VALUE));
    }

    public static TarotCard.Effects magicianPendulum() {
        return new TarotCard.Effects(
                // Arcane barrier: 4 extra hearts for 5 seconds.
                ActiveEffect.of(8, ctx -> applyEffect(ctx.player(), MobEffects.ABSORPTION, 100, 1)),
                attribute("magician_armor", Attributes.ARMOR, 2.0, AttributeModifier.Operation.ADD_VALUE));
    }

    // IV – The Emperor: steadfastness.

    public static TarotCard.Effects emperorScythe() {
        return new TarotCard.Effects(
                // War cry: Strength I for 8 seconds.
                ActiveEffect.of(10, ctx -> applyEffect(ctx.player(), MobEffects.STRENGTH, 160, 0)),
                attribute("emperor_attack_speed", Attributes.ATTACK_SPEED, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    public static TarotCard.Effects emperorPendulum() {
        return new TarotCard.Effects(
                ActiveEffect.of(6, ctx -> {
                    // Shockwave: knocks back everything within 4 blocks.
                    List<LivingEntity> targets = CardHelpers.entitiesAround(ctx.player(), 4);
                    for (LivingEntity target : targets) {
                        Vec3 away = target.position().subtract(ctx.player().position());
                        target.knockback(1.5, -away.x, -away.z);
                    }
                    playSound(ctx.player(), SoundEvents.PLAYER_ATTACK_KNOCKBACK);
                    return !targets.isEmpty();
                }),
                new PassiveEffect() {
                    @Override
                    public void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
                        modifiers.accept(Attributes.ARMOR_TOUGHNESS, modifier("emperor_toughness", 2.0 * strength, AttributeModifier.Operation.ADD_VALUE));
                        modifiers.accept(Attributes.KNOCKBACK_RESISTANCE, modifier("emperor_knockback_resistance", 0.2 * strength, AttributeModifier.Operation.ADD_VALUE));
                    }
                });
    }

    // XIII – Death: reaping.

    public static TarotCard.Effects deathScythe() {
        return new TarotCard.Effects(
                ActiveEffect.of(8, ctx -> {
                    // Reaper's sweep: 5 damage and 3 seconds of Wither to everything in front within 3 blocks.
                    List<LivingEntity> targets = CardHelpers.entitiesInFront(ctx.player(), 3.5);
                    DamageSource source = ctx.level().damageSources().playerAttack(ctx.player());
                    for (LivingEntity target : targets) {
                        target.hurtServer(ctx.level(), source, 5.0F);
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0), ctx.player());
                    }
                    playSound(ctx.player(), SoundEvents.PLAYER_ATTACK_SWEEP);
                    return !targets.isEmpty();
                }),
                new PassiveEffect() {
                    @Override
                    public void onKill(Context context, LivingEntity victim) {
                        // Regeneration I for 10 seconds after a kill.
                        context.player().addEffect(new MobEffectInstance(MobEffects.REGENERATION, Math.round(200 * context.strength()), 0));
                    }
                });
    }

    public static TarotCard.Effects deathPendulum() {
        return new TarotCard.Effects(
                ActiveEffect.of(8, ctx -> {
                    // Breath of death: Weakness and Slowness for 5 seconds within 5 blocks.
                    List<LivingEntity> targets = CardHelpers.entitiesAround(ctx.player(), 5);
                    for (LivingEntity target : targets) {
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0), ctx.player());
                        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 0), ctx.player());
                    }
                    playSound(ctx.player(), SoundEvents.WITHER_AMBIENT);
                    return !targets.isEmpty();
                }),
                new PassiveEffect() {
                    @Override
                    public float modifyIncomingDamage(Context context, DamageSource source, float damage) {
                        // Every hit taken deals 1 less damage.
                        return Math.max(0.0F, damage - 1.0F * context.strength());
                    }
                });
    }

    // XVI – The Tower: retribution.

    public static TarotCard.Effects towerScythe() {
        return new TarotCard.Effects(
                ActiveEffect.of(6, ctx -> {
                    // Shatter: the next melee hit within 5 seconds deals double damage.
                    CardHelpers.armEmpoweredStrike(ctx.player(), 100);
                    playSound(ctx.player(), SoundEvents.ANVIL_LAND);
                    return true;
                }),
                new PassiveEffect() {
                    @Override
                    public float modifyOutgoingDamage(Context context, LivingEntity target, DamageSource source, float damage) {
                        // Hits set the target on fire for 3 seconds.
                        target.igniteForSeconds(3.0F * context.strength());
                        return damage;
                    }
                });
    }

    public static TarotCard.Effects towerPendulum() {
        return new TarotCard.Effects(
                ActiveEffect.of(15, ctx -> {
                    // Lightning strike on the targeted entity within 16 blocks.
                    LivingEntity target = CardHelpers.findTarget(ctx.player(), 16);
                    if (target == null) {
                        return false;
                    }
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(ctx.level(), EntitySpawnReason.TRIGGERED);
                    if (bolt == null) {
                        return false;
                    }
                    bolt.setPos(target.getX(), target.getY(), target.getZ());
                    bolt.setCause(ctx.player());
                    ctx.level().addFreshEntity(bolt);
                    return true;
                }),
                new PassiveEffect() {
                    @Override
                    public void onBlock(Context context, DamageSource source, float blockedDamage) {
                        // Thorns: the attacker takes 30% of the blocked damage, at least 1.
                        if (source.getEntity() instanceof LivingEntity attacker && attacker != context.player()
                                && context.player().level() instanceof ServerLevel level) {
                            float reflected = Math.max(1.0F, blockedDamage * 0.3F * context.strength());
                            attacker.hurtServer(level, level.damageSources().thorns(context.player()), reflected);
                        }
                    }
                });
    }

    // XVII – The Star: healing.

    public static TarotCard.Effects starScythe() {
        return new TarotCard.Effects(
                ActiveEffect.of(10, ctx -> {
                    // Heals 3 hearts.
                    Player player = ctx.player();
                    if (player.getHealth() >= player.getMaxHealth()) {
                        return false;
                    }
                    player.heal(6.0F);
                    playSound(player, SoundEvents.AMETHYST_BLOCK_CHIME);
                    return true;
                }),
                new PassiveEffect() {
                    @Override
                    public void onDamageDealt(Context context, LivingEntity target, DamageSource source, float dealt) {
                        // Life steal: heals 10% of the damage dealt.
                        context.player().heal(dealt * 0.1F * context.strength());
                    }
                });
    }

    public static TarotCard.Effects starPendulum() {
        return new TarotCard.Effects(
                ActiveEffect.of(8, ctx -> {
                    // Cleansing: removes harmful effects and grants Resistance I for 3 seconds.
                    Player player = ctx.player();
                    List<Holder<MobEffect>> harmful = player.getActiveEffects().stream()
                            .map(MobEffectInstance::getEffect)
                            .filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
                            .toList();
                    harmful.forEach(player::removeEffect);
                    player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0));
                    playSound(player, SoundEvents.AMETHYST_BLOCK_CHIME);
                    return true;
                }),
                new PassiveEffect() {
                    @Override
                    public void tick(Context context) {
                        // Heals half a heart every 4 seconds; tick() runs once per second.
                        if (context.player().tickCount % 80 < 20) {
                            context.player().heal(1.0F * context.strength());
                        }
                    }
                });
    }

    private static PassiveEffect attribute(String name, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        return new PassiveEffect() {
            @Override
            public void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
                modifiers.accept(attribute, modifier(name, amount * strength, operation));
            }
        };
    }

    private static AttributeModifier modifier(String name, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(PendulumEtFalcatis.id("card/" + name), amount, operation);
    }

    private static boolean applyEffect(Player player, Holder<MobEffect> effect, int durationTicks, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
        playSound(player, SoundEvents.EVOKER_CAST_SPELL);
        return true;
    }

    private static void playSound(Player player, SoundEvent sound) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
