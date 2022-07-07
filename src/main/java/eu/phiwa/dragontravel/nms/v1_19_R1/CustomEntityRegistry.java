/*
This is just a copy/paste of NMS for v1_19_R1.
*/

package eu.phiwa.dragontravel.nms.v1_19_R1;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.EntityVillagerTrader;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.*;

import java.util.*;

@SuppressWarnings("rawtypes")
public class CustomEntityRegistry extends RegistryBlocks {
    private final BiMap<MinecraftKey, EntityTypes> entities = HashBiMap.create();
    private final BiMap<EntityTypes, MinecraftKey> entityClasses = this.entities.inverse();
    private final Map<EntityTypes, Integer> entityIds = Maps.newHashMap();
    private final RegistryMaterials<EntityTypes<?>> wrapped;

    public CustomEntityRegistry(RegistryBlocks<EntityTypes<?>> original) {
        //super(original.a().getNamespace(), null, null);
        //super(original.a().b(), null, null);
        super(original.a().b(), null, null, null);
        this.wrapped = original;
    }

    @Override
    public int a(Object key) {
        if (entityIds.containsKey(key)) {
            return entityIds.get(key);
        }
        //return key.hashCode();
        return wrapped.a((EntityTypes) key);
    }

    @Override
    public Optional a(RandomSource paramRandom) {
        return wrapped.a(paramRandom);
    }

    public EntityTypes findType(Class<?> search) {
        return minecraftClassMap.inverse().get(search);
        /*
        for (Object type : wrapped) {
            if (minecraftClassMap.get(type) == search) {
                return (EntityTypes) type;
            }
        }
        return null;
        */
    }

    //@Override
    public Object fromId(int var0) {
        //return this.wrapped.fromId(var0);
        return this.wrapped.a(var0);
    }

    //@Override
    public EntityTypes get(MinecraftKey key) {
        if (entities.containsKey(key)) {
            return entities.get(key);
        }

        //return wrapped.get(key);
        return wrapped.a(key);
    }

    //@Override
    public MinecraftKey getKey(Object value) {
        if (entityClasses.containsKey(value)) {
            return entityClasses.get(value);
        }

        //return wrapped.getKey((EntityTypes) value);
        return wrapped.b((EntityTypes) value);
    }

    //@Override
    public Optional getOptional(MinecraftKey var0) {
        if (entities.containsKey(var0)) {
            return Optional.of(entities.get(var0));
        }

        //return this.wrapped.getOptional(var0);
        return this.wrapped.b(var0);
    }

    public RegistryMaterials<EntityTypes<?>> getWrapped() {
        return wrapped;
    }

    @Override
    public Iterator<Object> iterator() {
        return (Iterator) wrapped.iterator();
    }

    //@Override
    public Set<Object> keySet() {
        //return (Set) wrapped.keySet();
        return (Set) wrapped.d();
    }

    public void put(int entityId, MinecraftKey key, EntityTypes entityClass) {
        entities.put(key, entityClass);
        entityIds.put(entityClass, entityId);
    }

    // replace regex
    // ([A-Z_]+).*?a\(E(.*?)::new.*?$
    // minecraftClassMap.put(EntityTypes.\1, E\2.class);
    private static final BiMap<EntityTypes, Class<?>> minecraftClassMap = HashBiMap.create();
    static {
        minecraftClassMap.put(EntityTypes.b, Allay.class);
        minecraftClassMap.put(EntityTypes.c, EntityAreaEffectCloud.class);
        minecraftClassMap.put(EntityTypes.d, EntityArmorStand.class);
        minecraftClassMap.put(EntityTypes.e, EntityTippedArrow.class);
        minecraftClassMap.put(EntityTypes.f, Axolotl.class);
        minecraftClassMap.put(EntityTypes.g, EntityBat.class);
        minecraftClassMap.put(EntityTypes.h, EntityBee.class);
        minecraftClassMap.put(EntityTypes.i, EntityBlaze.class);
        minecraftClassMap.put(EntityTypes.j, EntityBoat.class);
        minecraftClassMap.put(EntityTypes.k, ChestBoat.class);
        minecraftClassMap.put(EntityTypes.l, EntityCat.class);
        minecraftClassMap.put(EntityTypes.m, EntityCaveSpider.class);
        minecraftClassMap.put(EntityTypes.n, EntityChicken.class);
        minecraftClassMap.put(EntityTypes.o, EntityCod.class);
        minecraftClassMap.put(EntityTypes.p, EntityCow.class);
        minecraftClassMap.put(EntityTypes.q, EntityCreeper.class);
        minecraftClassMap.put(EntityTypes.r, EntityDolphin.class);
        minecraftClassMap.put(EntityTypes.s, EntityHorseDonkey.class);
        minecraftClassMap.put(EntityTypes.t, EntityDragonFireball.class);
        minecraftClassMap.put(EntityTypes.u, EntityDrowned.class);
        minecraftClassMap.put(EntityTypes.v, EntityGuardianElder.class);
        minecraftClassMap.put(EntityTypes.w, EntityEnderCrystal.class);
        minecraftClassMap.put(EntityTypes.x, EntityEnderDragon.class);
        minecraftClassMap.put(EntityTypes.y, EntityEnderman.class);
        minecraftClassMap.put(EntityTypes.z, EntityEndermite.class);
        minecraftClassMap.put(EntityTypes.A, EntityEvoker.class);
        minecraftClassMap.put(EntityTypes.B, EntityEvokerFangs.class);
        minecraftClassMap.put(EntityTypes.C, EntityExperienceOrb.class);
        minecraftClassMap.put(EntityTypes.D, EntityEnderSignal.class);
        minecraftClassMap.put(EntityTypes.E, EntityFallingBlock.class);
        minecraftClassMap.put(EntityTypes.F, EntityFireworks.class);
        minecraftClassMap.put(EntityTypes.G, EntityFox.class);
        minecraftClassMap.put(EntityTypes.H, Frog.class);
        minecraftClassMap.put(EntityTypes.I, EntityGhast.class);
        minecraftClassMap.put(EntityTypes.J, EntityGiantZombie.class);
        minecraftClassMap.put(EntityTypes.K, GlowItemFrame.class);
        minecraftClassMap.put(EntityTypes.L, GlowSquid.class);
        minecraftClassMap.put(EntityTypes.M, Goat.class);
        minecraftClassMap.put(EntityTypes.N, EntityGuardian.class);
        minecraftClassMap.put(EntityTypes.O, EntityHoglin.class);
        minecraftClassMap.put(EntityTypes.P, EntityHorse.class);
        minecraftClassMap.put(EntityTypes.Q, EntityZombieHusk.class);
        minecraftClassMap.put(EntityTypes.R, EntityIllagerIllusioner.class);
        minecraftClassMap.put(EntityTypes.S, EntityIronGolem.class);
        minecraftClassMap.put(EntityTypes.T, EntityItem.class);
        minecraftClassMap.put(EntityTypes.U, EntityItemFrame.class);
        minecraftClassMap.put(EntityTypes.V, EntityLargeFireball.class);
        minecraftClassMap.put(EntityTypes.W, EntityLeash.class);
        minecraftClassMap.put(EntityTypes.X, EntityLightning.class);
        minecraftClassMap.put(EntityTypes.Y, EntityLlama.class);
        minecraftClassMap.put(EntityTypes.Z, EntityLlamaSpit.class);
        minecraftClassMap.put(EntityTypes.aa, EntityMagmaCube.class);
        minecraftClassMap.put(EntityTypes.ab, Marker.class);
        minecraftClassMap.put(EntityTypes.ac, EntityMinecartRideable.class);
        minecraftClassMap.put(EntityTypes.ad, EntityMinecartChest.class);
        minecraftClassMap.put(EntityTypes.ae, EntityMinecartCommandBlock.class);
        minecraftClassMap.put(EntityTypes.af, EntityMinecartFurnace.class);
        minecraftClassMap.put(EntityTypes.ag, EntityMinecartHopper.class);
        minecraftClassMap.put(EntityTypes.ah, EntityMinecartMobSpawner.class);
        minecraftClassMap.put(EntityTypes.ai, EntityMinecartTNT.class);
        minecraftClassMap.put(EntityTypes.aj, EntityHorseMule.class);
        minecraftClassMap.put(EntityTypes.ak, EntityMushroomCow.class);
        minecraftClassMap.put(EntityTypes.al, EntityOcelot.class);
        minecraftClassMap.put(EntityTypes.am, EntityPainting.class);
        minecraftClassMap.put(EntityTypes.an, EntityPanda.class);
        minecraftClassMap.put(EntityTypes.ao, EntityParrot.class);
        minecraftClassMap.put(EntityTypes.ap, EntityPhantom.class);
        minecraftClassMap.put(EntityTypes.aq, EntityPig.class);
        minecraftClassMap.put(EntityTypes.ar, EntityPiglin.class);
        minecraftClassMap.put(EntityTypes.as, EntityPiglinBrute.class);
        minecraftClassMap.put(EntityTypes.at, EntityPillager.class);
        minecraftClassMap.put(EntityTypes.au, EntityPolarBear.class);
        minecraftClassMap.put(EntityTypes.av, EntityTNTPrimed.class);
        minecraftClassMap.put(EntityTypes.aw, EntityPufferFish.class);
        minecraftClassMap.put(EntityTypes.ax, EntityRabbit.class);
        minecraftClassMap.put(EntityTypes.ay, EntityRavager.class);
        minecraftClassMap.put(EntityTypes.az, EntitySalmon.class);
        minecraftClassMap.put(EntityTypes.aA, EntitySheep.class);
        minecraftClassMap.put(EntityTypes.aB, EntityShulker.class);
        minecraftClassMap.put(EntityTypes.aC, EntityShulkerBullet.class);
        minecraftClassMap.put(EntityTypes.aD, EntitySilverfish.class);
        minecraftClassMap.put(EntityTypes.aE, EntitySkeleton.class);
        minecraftClassMap.put(EntityTypes.aF, EntityHorseSkeleton.class);
        minecraftClassMap.put(EntityTypes.aG, EntitySlime.class);
        minecraftClassMap.put(EntityTypes.aH, EntitySmallFireball.class);
        minecraftClassMap.put(EntityTypes.aI, EntitySnowman.class);
        minecraftClassMap.put(EntityTypes.aJ, EntitySnowball.class);
        minecraftClassMap.put(EntityTypes.aK, EntitySpectralArrow.class);
        minecraftClassMap.put(EntityTypes.aL, EntitySpider.class);
        minecraftClassMap.put(EntityTypes.aM, EntitySquid.class);
        minecraftClassMap.put(EntityTypes.aN, EntitySkeletonStray.class);
        minecraftClassMap.put(EntityTypes.aO, EntityStrider.class);
        minecraftClassMap.put(EntityTypes.aP, Tadpole.class);
        minecraftClassMap.put(EntityTypes.aQ, EntityEgg.class);
        minecraftClassMap.put(EntityTypes.aR, EntityEnderPearl.class);
        minecraftClassMap.put(EntityTypes.aS, EntityThrownExpBottle.class);
        minecraftClassMap.put(EntityTypes.aT, EntityPotion.class);
        minecraftClassMap.put(EntityTypes.aU, EntityThrownTrident.class);
        minecraftClassMap.put(EntityTypes.aV, EntityLlamaTrader.class);
        minecraftClassMap.put(EntityTypes.aW, EntityTropicalFish.class);
        minecraftClassMap.put(EntityTypes.aX, EntityTurtle.class);
        minecraftClassMap.put(EntityTypes.aY, EntityVex.class);
        minecraftClassMap.put(EntityTypes.aZ, EntityVillager.class);
        minecraftClassMap.put(EntityTypes.ba, EntityVindicator.class);
        minecraftClassMap.put(EntityTypes.bb, EntityVillagerTrader.class);
        minecraftClassMap.put(EntityTypes.bc, Warden.class);
        minecraftClassMap.put(EntityTypes.bd, EntityWitch.class);
        minecraftClassMap.put(EntityTypes.be, EntityWither.class);
        minecraftClassMap.put(EntityTypes.bf, EntitySkeletonWither.class);
        minecraftClassMap.put(EntityTypes.bg, EntityWitherSkull.class);
        minecraftClassMap.put(EntityTypes.bh, EntityWolf.class);
        minecraftClassMap.put(EntityTypes.bi, EntityZoglin.class);
        minecraftClassMap.put(EntityTypes.bj, EntityZombie.class);
        minecraftClassMap.put(EntityTypes.bk, EntityHorseZombie.class);
        minecraftClassMap.put(EntityTypes.bl, EntityZombieVillager.class);
        minecraftClassMap.put(EntityTypes.bm, EntityPigZombie.class);
        minecraftClassMap.put(EntityTypes.bn, EntityHuman.class);
        minecraftClassMap.put(EntityTypes.bo, EntityFishingHook.class);
    }
}