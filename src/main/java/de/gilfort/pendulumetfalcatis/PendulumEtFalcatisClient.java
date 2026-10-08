package de.gilfort.pendulumetfalcatis;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
// Key mappings and the tool screen are registered here in later milestones.
@Mod(value = PendulumEtFalcatis.MODID, dist = Dist.CLIENT)
public class PendulumEtFalcatisClient {
    public PendulumEtFalcatisClient() {
    }
}
