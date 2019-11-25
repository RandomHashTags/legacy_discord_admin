package me.randomhashtags.discordadmin.util;

public enum Project {
    ARCHITECT(ProjectType.MINECRAFT_PLUGIN, "https://gitlab.com/RandomHashTags/architect", true),
    MERCHANTS(ProjectType.MINECRAFT_PLUGIN, new String[] {"https://www.spigotmc.org/resources/34855"}, "https://gitlab.com/RandomHashTags/merchants", true, "elixir"),
    RANDOM_PACKAGE(ProjectType.MINECRAFT_PLUGIN, new String[] {"https://www.spigotmc.org/resources/38501"}, "https://gitlab.com/RandomHashTags/randompackage-multi", true, "randompackage", "randompackages"),
    RANDOM_PRISONS(ProjectType.MINECRAFT_PLUGIN, "https://gitlab.com/RandomHashTags/randomprisons-multi", true, "randomprison", "randomprisons"),
    RANDOM_SKY(ProjectType.MINECRAFT_PLUGIN, "https://gitlab.com/RandomHashTags/randomsky-multi", true, "randomsky", "randomskyblock"),

    COMBAT_ELITE(ProjectType.MINECRAFT_PLUGIN, new String[] {"https://www.spigotmc.org/resources/31101"}, "https://gitlab.com/RandomHashTags/combatelite", "combatelite"),
    COSMIC_VAULTS(ProjectType.MINECRAFT_PLUGIN, new String[] {"https://www.spigotmc.org/resources/34309"}, "https://gitlab.com/RandomHashTags/cosmicvaults", "cosmicvault", "cosmicvaults"),

    UMATERIAL(ProjectType.MINECRAFT_PLUGIN, new String[] {"https://www.spigotmc.org/resources/62851"}, "https://gitlab.com/RandomHashTags/umaterial"),
    UPARTICLE(ProjectType.MINECRAFT_PLUGIN,"https://gitlab.com/RandomHashTags/uparticle"),
    USOUND(ProjectType.MINECRAFT_PLUGIN, "https://gitlab.com/RandomHashTags/usound"),

    UNITED_LIVESTREAMS(ProjectType.IOS_APP, "https://gitlab.com/RandomHashTags/united-livestreams", false, "unitedlivestream", "unitedlivestreams")
    ;

    private static final Project[] enums = values();
    private final ProjectType type;
    private final String src;
    private final boolean isPremium;
    private final String[] links, aliases;
    Project(ProjectType type, String src) {
        this(type, null, src, false);
    }
    Project(ProjectType type, String src, boolean isPremium, String...aliases) {
        this(type, null, src, isPremium, aliases == null ? new String[]{} : aliases);
    }
    Project(ProjectType type, String[] links, String src, String...aliases) {
        this(type, links, src, false, aliases);
    }
    Project(ProjectType type, String[] links, String src, boolean isPremium, String...aliases) {
        this.type = type;
        this.links = links;
        this.src = src;
        this.isPremium = isPremium;
        this.aliases = aliases;
    }
    public ProjectType getType() { return type; }
    public String[] getDownloadLinks() { return links; }
    public String getSourceCode() { return src; }
    public String[] getAliases() { return aliases; }
    public boolean isPremium() { return isPremium; }
    public boolean containsAlias(String input) {
        if(input.equalsIgnoreCase(name())) {
            return true;
        }
        for(String s : aliases) {
            if(input.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static Project match(String input) {
        if(input != null) {
            final String l = input.toLowerCase();
            for(Project p : enums) {
                if(p.containsAlias(l)) {
                    return p;
                }
            }
        }
        return null;
    }
}
