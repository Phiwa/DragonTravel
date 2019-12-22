package eu.phiwa.dragontravel.nms;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

public class CompatibilityUtils {

    // List of type names representing signs
    public static final List<String> signTypes = Arrays.asList(
        "SIGN",
        "WALL_SIGN",
        "SIGN_POST",
        "LEGACY_WALL_SIGN",
        "LEGACY_SIGN_POST",
        "ACACIA_SIGN",
        "ACACIA_WALL_SIGN",
        "BIRCH_SIGN",
        "BIRCH_WALL_SIGN",
        "DARK_OAK_SIGN",
        "DARK_OAK_WALL_SIGN",
        "JUNGLE_SIGN",
        "JUNGLE_WALL_SIGN",
        "OAK_SIGN",
        "OAK_WALL_SIGN",
        "SPRUCE_SIGN",
        "SPRUCE_WALL_SIGN"
    );

    public static boolean typeIsSign(Material t) {
        // Nasty trick to maintain compatibility with newer versions where the Material enum's fields might change
        //   e.g. SIGN_POST => LEGACY_SIGN_POST in CB v1_13_R1
        System.out.println( t ); // debug
        return signTypes.contains(t.name());
    }
}