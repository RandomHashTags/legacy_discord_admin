package me.randomhashtags.discordadmin;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscordAdmin extends ListenerAdapter {

    public static void main(String[] args) {
        try {
            final JDA bot = new JDABuilder(AccountType.BOT).setToken("").build();
            bot.addEventListener(new DiscordAdmin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String resourcelist = "\n\n**Resource List**\n__Minecraft Plugins__\n- RandomPackage, RandomSky, RandomPrisons, RandomMCMMO\n- Merchants, CosmicVaults, CombatElite, RandomOrbsorber\n- RandomArmorSwitch, FatBuckets\n- UMaterial, UParticle, USound\n\n__iOS Apps__\n- Hopscotch Dash";
    public void addDevNote(Guild g, String addition) {
        final TextChannel C = g.getTextChannelsByName("\uD83D\uDCC5-dev-notes", false).get(0);
        C.sendMessage(addition).queue();
    }
    public String getCurrentDate() {
        return new SimpleDateFormat("MMMM dd, yyyy, 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()));
    }
    public void onMessageReceived(MessageReceivedEvent event) {
        final User u = event.getAuthor();
        final MessageChannel c = event.getChannel();
        final Message message = event.getMessage();
        final String msg = message.getContentRaw(), ch = c.getName();
        final Guild g = event.getGuild();
        final long id = c.getIdLong();
        if(!u.isBot() && c instanceof TextChannel) {
            final TextChannel tc = (TextChannel) c, target = g.getTextChannelById(id);
            final Category parent = tc.getParent();
            final String targetName = target.getName(), parentName = parent != null ? parent.getName() : null;
            if(g.getMember(u).getPermissions().contains(Permission.ADMINISTRATOR) && g.getRolesByName(targetName, true) != null && (parentName != null && (parentName.equals("Bugs") || parentName.equals("Suggestions") || parentName.equals("Role Requests")))) {
                final boolean bug = parentName.equals("Bugs"), suggestion = parentName.equals("Suggestions");
                if(msg.equals("-close")) {
                    target.delete().reason("Resolved").queue();
                } else if(msg.startsWith("-fixed ") && bug || msg.startsWith("-added ") && suggestion) {
                    final iTextChannel i = iTextChannel.valueOf(tc);
                    if(i != null) {
                        final String r = g.getRolesByName(ch, true).get(0).getName(), d = getCurrentDate(), m = i.creatorMention;
                        if(bug) {
                            addDevNote(g, "_(" + d + ")_\n\n**" + r + " Bug Fixed**\n" + msg.split("-fixed ")[1] + "\n\nReporter " + m);
                        } else {
                            addDevNote(g, "_(" + d + ")_\n\n**" + r + " Suggestion Added**\n" + msg.split("-added ")[1] + "\n\nRequester " + m);
                        }
                        i.delete();
                    }
                }
            } else if(ch.equals("\uD83D\uDCAC-discussion") || ch.equals("\uD83D\uDC96-supporters") || ch.equals("✨\uD83C\uDF31-random") || ch.equals("\uD83C\uDFEA\uD83D\uDC31\uD83D\uDD10-other")) {
                final String m = msg.toLowerCase().replace(" ", "").replaceAll("\\p{P}", "");
                if(m.contains("bug") && !msg.startsWith("-bug") || m.contains("error") || m.contains("doesntw") || m.contains("doesntd") || m.contains("dontw") || m.contains("dontd") || m.contains("fix") || m.contains("notwork") || m.contains("aintwork") || m.contains("broke")) {
                    sendMention(tc, u, "If you think you've found a bug/error, report it using \"-bug [resource]\"" + resourcelist);
                }
            }
            final String T = msg.startsWith("-bug") ? "bug" : msg.startsWith("-suggestion") ? "suggestion" : msg.startsWith("-role") ? "role" : null;
            if(T != null) {
                final String[] a = msg.split(T + " ");
                message.delete().queue();
                final String A = a.length > 1 ? a[1].toLowerCase() : null;
                if(T.equals("role")) {
                    if(A == null) {
                        sendCorrectUsage(tc, u, T, true);
                    } else {
                        String lang = A;
                        lang = lang.startsWith("c") ? "C/C++/C# Developer"
                                : lang.equals("python") ? "Python Developer"
                                : lang.equals("java") ? "Java Developer"
                                : lang.equals("ruby") ? "Ruby Developer"
                                : lang.equals("mobile") ? "Mobile Developer"
                                : lang.equals("spigotmc java") ? "SpigotMC Java Developer"
                                : lang.equals("spigotmc skript") ? "SpigotMC Skript Developer"

                                : lang.equals("content creator") ? "Content Creator"
                                : lang.equals("influencer") ? "Influencer"

                                : lang.equals("randompackage") ? "RandomPackage"
                                : lang.equals("randomsky") ? "RandomSky"
                                : lang.equals("randomprisons") ? "RandomPrisons"
                                : lang.equals("merchants") ? "Merchants"
                                : null;
                        if(lang == null) {
                            sendCorrectUsage(tc, u, T, true);
                        } else {
                            createTextChannel(u, g, message, "Role Request", lang);
                        }
                    }
                } else if(a.length == 1 || a.length > 1
                        && !A.equals("randompackage")
                        && !A.equals("randomsky")
                        && !A.equals("randomprisons")
                        && !A.equals("randommcmmo")
                        && !A.equals("merchants")
                        && !A.equals("randomorbsorber")
                        && !A.equals("randomarmorswitch")
                        && !A.equals("cosmicvaults")
                        && !A.equals("combatelite")
                        && !A.equals("fatbuckets")
                        && !A.equals("umaterial")
                        && !A.equals("usound")
                        && !A.equals("uparticle")
                ) {
                    sendCorrectUsage(tc, u, T, false);
                } else {
                    String plugin = a[1].toLowerCase();
                    plugin = plugin.equals("randompackage") ? "RandomPackage"
                            : plugin.equals("randomsky") ? "RandomSky"
                            : plugin.equals("randomprisons") ? "RandomPrisons"
                            : plugin.equals("randommcmmo") ? "RandomMCMMO"
                            : plugin.equals("merchants") ? "Merchants"
                            : plugin.equals("randomorbsorber") ? "RandomOrbsorber"
                            : plugin.equals("randomarmorswitch") ? "RandomArmorSwitch"
                            : plugin.equals("cosmicvaults") ? "CosmicVaults"
                            : plugin.equals("combatelite") ? "CombatElite"
                            : plugin.equals("fatbuckets") ? "FatBuckets"
                            : plugin.startsWith("u") ? plugin.substring(0, 2).toUpperCase() + plugin.substring(2).toLowerCase()
                            : plugin.substring(0, 1).toUpperCase() + plugin.substring(1).toLowerCase();
                    createTextChannel(u, g, message, T, plugin);
                }
            }
        }
    }

    private void sendCorrectUsage(TextChannel tc, User u, String T, boolean role) {
        if(!role) {
            sendMention(tc, u, "Correct usage: \"-" + T + " [resource]\"" + resourcelist);
        } else {
            sendMention(tc, u, "Correct usage: \"-" + T + " [role]\"\n\n**Verified Roles**\n- RandomPackage, RandomPrisons, RandomSky, RandomMCMMO, Merchants\n\n**Developer Roles**\n- Python, Java, Ruby\n- C, C++, C#\n- Mobile, SpigotMC Java, SpigotMC Skript\n\n**Other Roles**\n- Content Creator, Graphic Designer, Music Artist, Influencer");
        }
    }
    private void sendMention(TextChannel tc, User user, String message) {
        tc.sendMessage(user.getAsMention() + ", " + message).queue();
    }
    private void createTextChannel(User creator, Guild g, Message msg, String type, String plugin) {
        new iTextChannel(creator, g, msg, type, plugin);
    }
}
