package de.gilfort.pendulumetfalcatis.client;

import com.mojang.blaze3d.platform.InputConstants;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.card.ToolKind;
import de.gilfort.pendulumetfalcatis.network.UseAbilityPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/** The ability keys of the scythe and pendulum. Both are unbound until the player assigns them. */
public final class AbilityKeys {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(PendulumEtFalcatis.id("abilities"));

    public static final KeyMapping SCYTHE_ABILITY = new KeyMapping("key.pendulumetfalcatis.scythe_ability",
            InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping PENDULUM_ABILITY = new KeyMapping("key.pendulumetfalcatis.pendulum_ability",
            InputConstants.UNKNOWN.getValue(), CATEGORY);

    private AbilityKeys() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(SCYTHE_ABILITY);
        event.register(PENDULUM_ABILITY);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean canUse = minecraft.player != null && minecraft.gui.screen() == null;
        sendWhenPressed(SCYTHE_ABILITY, ToolKind.SCYTHE, canUse);
        sendWhenPressed(PENDULUM_ABILITY, ToolKind.PENDULUM, canUse);
    }

    /** Sends at most one request per tick; the server enforces the real cooldown. */
    private static void sendWhenPressed(KeyMapping key, ToolKind tool, boolean canUse) {
        boolean pressed = false;
        while (key.consumeClick()) {
            pressed = true;
        }
        if (pressed && canUse) {
            ClientPacketDistributor.sendToServer(new UseAbilityPayload(tool));
        }
    }
}
