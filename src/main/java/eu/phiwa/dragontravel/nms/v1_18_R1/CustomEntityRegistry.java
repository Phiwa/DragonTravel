/*
This is just a copy/paste of NMS for v1_18_R1.
*/

package eu.phiwa.dragontravel.nms.v1_18_R1;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
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
        super(original.a().b(), null, null);
        this.wrapped = original;
    }

    public int a(Object key) {
        if (entityIds.containsKey(key)) {
            return entityIds.get(key);
        }
        return key.hashCode();
    }

    @Override
    public Object a(Random paramRandom) {
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
        minecraftClassMap.put(EntityTypes.b, EntityAreaEffectCloud.class);
        minecraftClassMap.put(EntityTypes.c, EntityArmorStand.class);
        minecraftClassMap.put(EntityTypes.d, EntityTippedArrow.class);
        minecraftClassMap.put(EntityTypes.e, Axolotl.class);
        minecraftClassMap.put(EntityTypes.f, EntityBat.class);
        minecraftClassMap.put(EntityTypes.g, EntityBee.class);
        minecraftClassMap.put(EntityTypes.h, EntityBlaze.class);
        minecraftClassMap.put(EntityTypes.i, EntityBoat.class);
        minecraftClassMap.put(EntityTypes.j, EntityCat.class);
        minecraftClassMap.put(EntityTypes.k, EntityCaveSpider.class);
        minecraftClassMap.put(EntityTypes.l, EntityChicken.class);
        minecraftClassMap.put(EntityTypes.m, EntityCod.class);
        minecraftClassMap.put(EntityTypes.n, EntityCow.class);
        minecraftClassMap.put(EntityTypes.o, EntityCreeper.class);
        minecraftClassMap.put(EntityTypes.p, EntityDolphin.class);
        minecraftClassMap.put(EntityTypes.q, EntityHorseDonkey.class);
        minecraftClassMap.put(EntityTypes.r, EntityDragonFireball.class);
        minecraftClassMap.put(EntityTypes.s, EntityDrowned.class);
        minecraftClassMap.put(EntityTypes.t, EntityGuardianElder.class);
        minecraftClassMap.put(EntityTypes.u, EntityEnderCrystal.class);
        minecraftClassMap.put(EntityTypes.v, EntityEnderDragon.class);
        minecraftClassMap.put(EntityTypes.w, EntityEnderman.class);
        minecraftClassMap.put(EntityTypes.x, EntityEndermite.class);
        minecraftClassMap.put(EntityTypes.y, EntityEvoker.class);
        minecraftClassMap.put(EntityTypes.z, EntityEvokerFangs.class);
        minecraftClassMap.put(EntityTypes.A, EntityExperienceOrb.class);
        minecraftClassMap.put(EntityTypes.B, EntityEnderSignal.class);
        minecraftClassMap.put(EntityTypes.C, EntityFallingBlock.class);
        minecraftClassMap.put(EntityTypes.D, EntityFireworks.class);
        minecraftClassMap.put(EntityTypes.E, EntityFox.class);
        minecraftClassMap.put(EntityTypes.F, EntityGhast.class);
        minecraftClassMap.put(EntityTypes.G, EntityGiantZombie.class);
        minecraftClassMap.put(EntityTypes.H, GlowItemFrame.class);
        minecraftClassMap.put(EntityTypes.I, GlowSquid.class);
        minecraftClassMap.put(EntityTypes.J, Goat.class);
        minecraftClassMap.put(EntityTypes.K, EntityGuardian.class);
        minecraftClassMap.put(EntityTypes.L, EntityHoglin.class);
        minecraftClassMap.put(EntityTypes.M, EntityHorse.class);
        minecraftClassMap.put(EntityTypes.N, EntityZombieHusk.class);
        minecraftClassMap.put(EntityTypes.O, EntityIllagerIllusioner.class);
        minecraftClassMap.put(EntityTypes.P, EntityIronGolem.class);
        minecraftClassMap.put(EntityTypes.Q, EntityItem.class);
        minecraftClassMap.put(EntityTypes.R, EntityItemFrame.class);
        minecraftClassMap.put(EntityTypes.S, EntityLargeFireball.class);
        minecraftClassMap.put(EntityTypes.T, EntityLeash.class);
        minecraftClassMap.put(EntityTypes.U, EntityLightning.class);
        minecraftClassMap.put(EntityTypes.V, EntityLlama.class);
        minecraftClassMap.put(EntityTypes.W, EntityLlamaSpit.class);
        minecraftClassMap.put(EntityTypes.X, EntityMagmaCube.class);
        minecraftClassMap.put(EntityTypes.Y, Marker.class);
        minecraftClassMap.put(EntityTypes.Z, EntityMinecartRideable.class);
        minecraftClassMap.put(EntityTypes.aa, EntityMinecartChest.class);
        minecraftClassMap.put(EntityTypes.ab, EntityMinecartCommandBlock.class);
        minecraftClassMap.put(EntityTypes.ac, EntityMinecartFurnace.class);
        minecraftClassMap.put(EntityTypes.ad, EntityMinecartHopper.class);
        minecraftClassMap.put(EntityTypes.ae, EntityMinecartMobSpawner.class);
        minecraftClassMap.put(EntityTypes.af, EntityMinecartTNT.class);
        minecraftClassMap.put(EntityTypes.ag, EntityHorseMule.class);
        minecraftClassMap.put(EntityTypes.ah, EntityMushroomCow.class);
        minecraftClassMap.put(EntityTypes.ai, EntityOcelot.class);
        minecraftClassMap.put(EntityTypes.aj, EntityPainting.class);
        minecraftClassMap.put(EntityTypes.ak, EntityPanda.class);
        minecraftClassMap.put(EntityTypes.al, EntityParrot.class);
        minecraftClassMap.put(EntityTypes.am, EntityPhantom.class);
        minecraftClassMap.put(EntityTypes.an, EntityPig.class);
        minecraftClassMap.put(EntityTypes.ao, EntityPiglin.class);
        minecraftClassMap.put(EntityTypes.ap, EntityPiglinBrute.class);
        minecraftClassMap.put(EntityTypes.aq, EntityPillager.class);
        minecraftClassMap.put(EntityTypes.ar, EntityPolarBear.class);
        minecraftClassMap.put(EntityTypes.as, EntityTNTPrimed.class);
        minecraftClassMap.put(EntityTypes.at, EntityPufferFish.class);
        minecraftClassMap.put(EntityTypes.au, EntityRabbit.class);
        minecraftClassMap.put(EntityTypes.av, EntityRavager.class);
        minecraftClassMap.put(EntityTypes.aw, EntitySalmon.class);
        minecraftClassMap.put(EntityTypes.ax, EntitySheep.class);
        minecraftClassMap.put(EntityTypes.ay, EntityShulker.class);
        minecraftClassMap.put(EntityTypes.az, EntityShulkerBullet.class);
        minecraftClassMap.put(EntityTypes.aA, EntitySilverfish.class);
        minecraftClassMap.put(EntityTypes.aB, EntitySkeleton.class);
        minecraftClassMap.put(EntityTypes.aC, EntityHorseSkeleton.class);
        minecraftClassMap.put(EntityTypes.aD, EntitySlime.class);
        minecraftClassMap.put(EntityTypes.aE, EntitySmallFireball.class);
        minecraftClassMap.put(EntityTypes.aF, EntitySnowman.class);
        minecraftClassMap.put(EntityTypes.aG, EntitySnowball.class);
        minecraftClassMap.put(EntityTypes.aH, EntitySpectralArrow.class);
        minecraftClassMap.put(EntityTypes.aI, EntitySpider.class);
        minecraftClassMap.put(EntityTypes.aJ, EntitySquid.class);
        minecraftClassMap.put(EntityTypes.aK, EntitySkeletonStray.class);
        minecraftClassMap.put(EntityTypes.aL, EntityStrider.class);
        minecraftClassMap.put(EntityTypes.aM, EntityEgg.class);
        minecraftClassMap.put(EntityTypes.aN, EntityEnderPearl.class);
        minecraftClassMap.put(EntityTypes.aO, EntityThrownExpBottle.class);
        minecraftClassMap.put(EntityTypes.aP, EntityPotion.class);
        minecraftClassMap.put(EntityTypes.aQ, EntityThrownTrident.class);
        minecraftClassMap.put(EntityTypes.aR, EntityLlamaTrader.class);
        minecraftClassMap.put(EntityTypes.aS, EntityTropicalFish.class);
        minecraftClassMap.put(EntityTypes.aT, EntityTurtle.class);
        minecraftClassMap.put(EntityTypes.aU, EntityVex.class);
        minecraftClassMap.put(EntityTypes.aV, EntityVillager.class);
        minecraftClassMap.put(EntityTypes.aW, EntityVindicator.class);
        minecraftClassMap.put(EntityTypes.aX, EntityVillagerTrader.class);
        minecraftClassMap.put(EntityTypes.aY, EntityWitch.class);
        minecraftClassMap.put(EntityTypes.aZ, EntityWither.class);
        minecraftClassMap.put(EntityTypes.ba, EntitySkeletonWither.class);
        minecraftClassMap.put(EntityTypes.bb, EntityWitherSkull.class);
        minecraftClassMap.put(EntityTypes.bc, EntityWolf.class);
        minecraftClassMap.put(EntityTypes.bd, EntityZoglin.class);
        minecraftClassMap.put(EntityTypes.be, EntityZombie.class);
        minecraftClassMap.put(EntityTypes.bf, EntityHorseZombie.class);
        minecraftClassMap.put(EntityTypes.bg, EntityZombieVillager.class);
        minecraftClassMap.put(EntityTypes.bh, EntityPigZombie.class);
        minecraftClassMap.put(EntityTypes.bi, EntityHuman.class);
        minecraftClassMap.put(EntityTypes.bj, EntityFishingHook.class);
    }
}