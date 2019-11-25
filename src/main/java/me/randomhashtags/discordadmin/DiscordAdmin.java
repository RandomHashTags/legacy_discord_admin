package me.randomhashtags.discordadmin;

import me.randomhashtags.discordadmin.util.Project;
import me.randomhashtags.discordadmin.util.TicketType;
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

public final class DiscordAdmin extends ListenerAdapter {
    public static void main(String[] args) {
        try {
            final JDA bot = new JDABuilder(AccountType.BOT).setToken(DataValues.DISCORD_BOT_TOKEN).build();
            bot.addEventListener(new DiscordAdmin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String commandList = "**Commands**\n`-bug <resource>`\n`-role <resource>`\n`-suggestion <resource>`";
    private String resourceList = "**Resources**\n- RandomPackage\n- RandomSky\n- RandomPrisons\n- Merchants\n- CombatElite\n- CosmicVaults\n- UMaterial";

    public void addDevNote(Guild g, String addition) {
        final TextChannel C = g.getTextChannelsByName("\uD83D\uDCC5-dev-notes", false).get(0);
        C.sendMessage(addition).queue();
    }
    public String getCurrentDate() {
        return new SimpleDateFormat("MMMM dd, yyyy, 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()));
    }

    private boolean isAdmin(Guild guild, User user) {
        return guild.getMember(user).getPermissions().contains(Permission.ADMINISTRATOR);
    }
    private Role getRole(Guild guild, String name) {
        final List<Role> roles = guild.getRolesByName(name, true);
        return roles != null && !roles.isEmpty() ? roles.get(0) : null;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        final User user = event.getAuthor();
        if(!user.isBot()) {
            final MessageChannel c = event.getChannel();
            if(c instanceof TextChannel) {
                final Message message = event.getMessage();
                final String msg = message.getContentRaw(), channelName = c.getName();
                final Guild guild = event.getGuild();
                final Role role = getRole(guild, channelName);
                final TextChannel channel = (TextChannel) c;
                final Category parent = channel.getParent();
                final String parentName = parent != null ? parent.getName() : null;
                if(isAdmin(guild, user) && role != null && (parentName != null && (parentName.equals("Bugs") || parentName.equals("Suggestions") || parentName.equals("Role Requests")))) {
                    final boolean bug = parentName.equals("Bugs"), suggestion = parentName.equals("Suggestions");
                    if(msg.equals("-close")) {
                        final iTextChannel i = iTextChannel.valueOf(channel);
                        if(i != null) {
                            i.delete();
                        } else {
                            channel.delete().reason("Resolved").queue();
                        }
                    } else if(msg.startsWith("-fixed ") && bug || msg.startsWith("-added ") && suggestion) {
                        final iTextChannel i = iTextChannel.valueOf(channel);
                        if(i != null) {
                            final String r = role.getName(), m = i.getCreatorMention();
                            final String type = bug ? "Bug Fixed" : "Suggestion Added", trigger = bug ? "-fixed" : "-added", identity = bug ? "Reporter" : "Requester";
                            addDevNote(guild, "_(" + getCurrentDate() + ")_\n\n**" + r + " " + type + "**\n" + msg.split(trigger + " ")[1] + "\n\n" + identity + " " + m);
                            i.delete();
                        }
                    }
                } else if(channelName.equals("\uD83C\uDFAB-tickets")) {
                    final String m = msg.toLowerCase();
                    if(m.startsWith("-")) {
                        final String[] values = m.split(" ");
                        final int l = values.length;
                        message.delete().queue();
                        if(l == 1) {
                            sendMention(channel, user, "Unknown command!\n\n" + commandList + "\n\n" + resourceList);
                            return;
                        }
                        final String input = values[1];
                        final Project resource = Project.match(input);
                        if(resource != null) {
                            switch (values[0].split("-")[1]) {
                                case "bug":
                                case "error":
                                case "ticket":
                                    createTextChannel(user, guild, message, TicketType.BUG_REPORT, resource);
                                    break;
                                case "suggestion":
                                case "request":
                                case "add":
                                    createTextChannel(user, guild, message, TicketType.SUGGESTION_REQUEST, resource);
                                    break;
                                case "role":
                                case "rank":
                                    createTextChannel(user, guild, message, TicketType.ROLE_REQUEST, resource);
                                    break;
                                default:
                                    sendMention(channel, user, "Unknown resource \"" + input + "\"!\n\n" + resourceList);
                                    break;
                            }
                        } else {
                            sendMention(channel, user, "Unknown resource \"" + input + "\"!\n\n" + resourceList);
                        }
                    }
                }
            }
        }
    }

    private void sendMention(TextChannel tc, User user, String message) {
        sendMention(tc, user, message, true);
    }
    private void sendMention(TextChannel tc, User user, String message, boolean delete) {
        tc.sendMessage(user.getAsMention() + ", " + message).queue(delete ? this::didSendMention : null);
    }
    private void didSendMention(Message msg) {
        msg.delete().queueAfter(30, TimeUnit.SECONDS);
    }
    private void createTextChannel(User creator, Guild guild, Message msg, TicketType type, Project project) {
        new iTextChannel(creator, guild, msg, type, project);
    }
}
