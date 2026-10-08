package de.gilfort.pendulumetfalcatis.item;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** The scythe ("Sense"): a melee weapon with iron-sword base stats, improved by its core. */
public class ScytheItem extends ArcaneToolItem {
    /** Iron sword: 3 damage + 2 material bonus on top of the player's base 1. */
    private static final float BASE_ATTACK_DAMAGE = 3.0F + ToolMaterial.IRON.attackDamageBonus();
    private static final float ATTACK_SPEED = -2.4F;

    private static final Map<CoreTier, ItemAttributeModifiers> MODIFIERS = new EnumMap<>(CoreTier.class);
    private static final ItemAttributeModifiers INACTIVE_MODIFIERS = buildModifiers(0.0F);

    static {
        for (CoreTier tier : CoreTier.values()) {
            MODIFIERS.put(tier, buildModifiers(BASE_ATTACK_DAMAGE + tier.damageBonus()));
        }
    }

    public ScytheItem(Properties properties) {
        // sword() provides durability, repair material, enchantability and the weapon/tool components.
        // Its attribute modifiers are cleared so getDefaultAttributeModifiers can depend on the core tier.
        super(properties.sword(ToolMaterial.IRON, 3.0F, ATTACK_SPEED).attributes(ItemAttributeModifiers.EMPTY));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return isInactive(stack) ? INACTIVE_MODIFIERS : MODIFIERS.get(getCoreTier(stack));
    }

    private static ItemAttributeModifiers buildModifiers(float attackDamage) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }
}
