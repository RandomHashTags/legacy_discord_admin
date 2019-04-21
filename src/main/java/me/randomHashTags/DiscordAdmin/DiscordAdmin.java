package me.randomHashTags.DiscordAdmin;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiscordAdmin extends ListenerAdapter {

    public static void main(String[] args) {
        try {
            final JDA bot = new JDABuilder(AccountType.BOT).setToken("").build();
            bot.addEventListener(new DiscordAdmin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String resourcelist = "\n\n**Resource List**\n__Minecraft Plugins__\n- RandomPackage, RandomSky, RandomPrisons, Merchants\n- CosmicVaults, CombatElite, RandomOrbsorber\n- RandomArmorSwitch, FatBuckets\n- UMaterial, UParticle, USound\n\n__iOS Apps__\n- Hopscotch Dash";
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
            final String topic = tc.getTopic(), targetName = target.getName();
            if(parent != null && parent.getName().equals("\uD83C\uDFA8 Resources \uD83C\uDFA8") && ch.equals("\uD83D\uDCE2updates") && msg.contains("**Version**")) {
                final List<Role> mentioned = message.getMentionedRoles();
                if(!mentioned.isEmpty()) {
                    final String r = mentioned.get(0).getName();
                    final List<TextChannel> list = g.getCategoriesByName("Fixed for next update", false).get(0).getTextChannels();
                    for(TextChannel tx : list) {
                        if(r.equals(tx.getTopic())) {
                            final iTextChannel it = iTextChannel.valueOf(tx);
                            if(it != null) it.delete();
                            else tx.delete().queue();
                        }
                    }
                }
            } else if(msg.startsWith("-giveaway") && u.getName().equals("RandomHashTags") && u.getDiscriminator().equals("1948")) {
                final String type = msg.split("-giveaway")[1];
                message.delete().queue();
                final long delay = 2;
                final TimeUnit unit = TimeUnit.DAYS;
                final String date = new SimpleDateFormat("MMMM dd, yyyy, 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()+unit.toMillis(delay)));

                new Giveaway(u, type, c, "@everyone\n\n**Giveaway**\n " + type + "\n\n**React with \uD83C\uDF89 to enter!**\n\n`Ends on " + date + "`", delay, unit);
            } else if(g.getMember(u).getPermissions().contains(Permission.ADMINISTRATOR) && (targetName.startsWith("bug") || targetName.startsWith("suggestion") || targetName.startsWith("role") || topic != null)) {
                if(msg.equals("-close")) {
                    final iTextChannel i = iTextChannel.valueOf(target);
                    if(i != null)
                        i.delete();
                    else
                        target.delete().reason("Resolved").queue();
                } else if(msg.equals("-fixed")) {
                    final iTextChannel i = iTextChannel.valueOf(tc);
                    if(i != null) {
                        i.fixed(message);
                    }
                }
            } else if(ch.equals("\uD83D\uDCACsupport")) {
                final String m = msg.toLowerCase().replace(" ", "").replaceAll("\\p{P}", "");
                if(m.contains("bug") && !msg.startsWith("-bug") || m.contains("error") || m.contains("doesnt w") || m.contains("doesnt d") || m.contains("dont w") || m.contains("dont d") || m.contains("fix") || m.contains("notwork") || m.contains("aintwork") || m.contains("broke")) {
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
                                : lang.equals("javascript") ? "JavaScript Developer"
                                : lang.equals("java") ? "Java Developer"
                                : lang.equals("ruby") ? "Ruby Developer"
                                : lang.equals("assembly") ? "Assembly Developer"
                                : lang.equals("mobile") ? "Mobile Developer"
                                : lang.equals("dart") ? "Dart Developer"
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
            sendMention(tc, u, "Correct usage: \"-" + T + " [role]\"\n\n**Verified Roles**\n- RandomPackage, RandomPrisons, RandomSky, Merchants\n\n**Developer Roles**\n- Assembly, Python, Java, Ruby\n- Dart, C, C++, C#\n- JavaScript, Dart, Mobile\n- SpigotMC Java, SpigotMC Skript\n\n**Other Roles**\n- Content Creator, Graphic Designer, Music Artist, Influencer");
        }
    }
    private void sendMention(TextChannel tc, User user, String message) {
        tc.sendMessage(user.getAsMention() + ", " + message).queue();
    }
    private void createTextChannel(User creator, Guild g, Message msg, String type, String plugin) {
        new iTextChannel(creator, g, msg, type, plugin);
    }
}
