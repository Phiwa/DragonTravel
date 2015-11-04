package eu.phiwa.dragontravel.core.commands;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import eu.phiwa.dragontravel.core.DragonTravel;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;

import java.lang.reflect.Method;
import java.util.*;

/*
Class adapted from Riking's contribution
 */
public class CommandHelpTopic extends IndexHelpTopic {
    private final Map<String, HelpTopic> subCommandHelps = new HashMap<>();

    public CommandHelpTopic(String name) {
        super(name, "DragonTravel subcommands", "dt.seecommand", getTopicCollection());
        buildSubCommandHelps(this.allTopics);
    }

    private static Collection<HelpTopic> getTopicCollection() {
        List<HelpTopic> ret = Lists.newArrayList();

        Map<String, Method> subCommands = DragonTravel.getInstance().getCommands().getSubcommandMethods("dt");

        for (Map.Entry<String, Method> entry : subCommands.entrySet()) {
            Command cmd = entry.getValue().getAnnotation(Command.class);
            if (cmd.aliases()[0].equalsIgnoreCase(entry.getKey())) {
                ret.add(new SubcommandHelpTopic(entry.getValue()));
            }
        }
        return ret;
    }

    private void buildSubCommandHelps(Collection<HelpTopic> allTopics) {
        for (HelpTopic t : allTopics) {
            SubcommandHelpTopic topic = (SubcommandHelpTopic) t;
            for (String alias : topic.cmd.aliases()) {
                subCommandHelps.put(alias, topic);
            }
        }
    }

    public HelpTopic getSubcommandHelp(CommandSender sender, String subcommand) {
        HelpTopic topic = subCommandHelps.get(subcommand);
        if (topic == null) {
            topic = findPossibleMatches(subcommand);
        }
        if (topic == null || !topic.canSee(sender)) {
            return null;
        }
        return topic;
    }

    private HelpTopic findPossibleMatches(String searchString) {
        int maxDistance = (searchString.length() / 5) + 3;
        Set<HelpTopic> possibleMatches = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());

        if (searchString.startsWith("DragonTravel")) {
            searchString = searchString.substring("DragonTravel".length());
        }
        if (searchString.startsWith("/dt ")) {
            searchString = searchString.substring(4);
        }
        if (searchString.startsWith("/")) {
            searchString = searchString.substring(1);
        }

        for (HelpTopic topic : this.allTopics) {
            String trimmedTopic = topic.getName().startsWith("/dt ") ? topic.getName().substring(4) : topic.getName();

            if (trimmedTopic.length() < searchString.length()) {
                continue;
            }

            if (Character.toLowerCase(trimmedTopic.charAt(0)) != Character.toLowerCase(searchString.charAt(0))) {
                continue;
            }

            if (damerauLevenshteinDistance(searchString, trimmedTopic.substring(0, searchString.length())) < maxDistance) {
                possibleMatches.add(topic);
            }
        }

        if (possibleMatches.size() == 1) {
            return possibleMatches.iterator().next();
        } else if (possibleMatches.size() > 0) {
            return new IndexHelpTopic("Search", null, null, possibleMatches, "Search for: " + searchString);
        } else {
            return null;
        }
    }

    /**
     * Computes the Dameraur-Levenshtein Distance between two strings.
     * Adapted from the algorithm at <a
     * href="http://en.wikipedia.org/wiki/Damerau�Levenshtein_distance"
     * >Wikipedia: Damerau�Levenshtein distance</a>
     *
     * @param s1 The first string being compared.
     * @param s2 The second string being compared.
     * @return The number of substitutions, deletions, insertions, and
     * transpositions required to get from s1 to s2.
     */
    private static int damerauLevenshteinDistance(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 != null && s2 == null) {
            return s1.length();
        }
        if (s1 == null) {
            return s2.length();
        }

        int s1Len = s1.length();
        int s2Len = s2.length();
        int[][] H = new int[s1Len + 2][s2Len + 2];

        int INF = s1Len + s2Len;
        H[0][0] = INF;
        for (int i = 0; i <= s1Len; i++) {
            H[i + 1][1] = i;
            H[i + 1][0] = INF;
        }
        for (int j = 0; j <= s2Len; j++) {
            H[1][j + 1] = j;
            H[0][j + 1] = INF;
        }

        Map<Character, Integer> sd = new HashMap<>();
        for (char Letter : (s1 + s2).toCharArray()) {
            if (!sd.containsKey(Letter)) {
                sd.put(Letter, 0);
            }
        }

        for (int i = 1; i <= s1Len; i++) {
            int DB = 0;
            for (int j = 1; j <= s2Len; j++) {
                int i1 = sd.get(s2.charAt(j - 1));
                int j1 = DB;

                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    H[i + 1][j + 1] = H[i][j];
                    DB = j;
                } else {
                    H[i + 1][j + 1] = Math.min(H[i][j], Math.min(H[i + 1][j], H[i][j + 1])) + 1;
                }

                H[i + 1][j + 1] = Math.min(H[i + 1][j + 1], H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
            }
            sd.put(s1.charAt(i - 1), i);
        }

        return H[s1Len + 1][s2Len + 1];
    }

    private static class SubcommandHelpTopic extends HelpTopic {
        final Command cmd;
        final CommandPermissions perms;

        public SubcommandHelpTopic(Method method) {
            cmd = method.getAnnotation(Command.class);
            perms = method.getAnnotation(CommandPermissions.class);

            name = "/dt " + cmd.aliases()[0];

            shortText = cmd.desc();

            // Build full text
            StringBuilder sb = new StringBuilder();

            sb.append(ChatColor.GOLD);
            sb.append("Description: ");
            sb.append(ChatColor.WHITE);
            sb.append(cmd.desc());

            sb.append("\n");

            sb.append(ChatColor.GOLD);
            sb.append("Usage: ");
            sb.append(ChatColor.WHITE);
            String tmp = cmd.usage();
            tmp = tmp.replace("<command>", name.substring(1));
            tmp = tmp.replaceAll("(\\[.*?\\])", ChatColor.LIGHT_PURPLE + "$1" + ChatColor.WHITE);
            tmp = tmp.replaceAll("(<.*?>)", ChatColor.AQUA + "$1" + ChatColor.WHITE);
            sb.append(tmp);

            if (cmd.aliases().length > 0) {
                sb.append("\n");
                sb.append(ChatColor.GOLD);
                sb.append("Aliases: ");
                sb.append(ChatColor.WHITE);
                sb.append(ChatColor.WHITE).append(StringUtils.join(cmd.aliases(), ", "));
            }

            sb.append("\n");
            sb.append(cmd.help());
            fullText = sb.toString();
        }

        @Override
        public boolean canSee(CommandSender player) {
            if (perms == null)
                return true;
            for (String perm : perms.value()) {
                if (player.hasPermission(perm)) {
                    return true;
                }
            }
            return false;
        }
    }
}