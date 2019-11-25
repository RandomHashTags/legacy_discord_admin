package me.randomhashtags.discordadmin;

import me.randomhashtags.discordadmin.util.Project;
import me.randomhashtags.discordadmin.util.TicketType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class iTextChannel {
    private static final HashSet<iTextChannel> channels = new HashSet<>();

    private final User creator;
    private final Guild guild;
    private final Message msg;
    private final TicketType type;
    private final String creatorMention;
    private final Project project;

    private TextChannel channel;
    private Message canBeFoundAtMsg;

    public iTextChannel(User creator, Guild guild, Message msg, TicketType type, Project project) {
        this.creator = creator;
        this.creatorMention = creator.getAsMention();
        this.guild = guild;
        this.msg = msg;
        this.type = type;
        this.project = project;
        final Category category = guild.getCategoriesByName(type.getChannelName(), false).get(0);
        final ChannelAction ca = category.createTextChannel(project.name().replace("_", ""));
        ca.queue(this::createChannel);
        channels.add(this);
    }

    private void createChannel(Channel channel) {
        final TextChannel TC = (TextChannel) channel;
        this.channel = TC;
        final ChannelManager manager = TC.getManager();
        final Member m = guild.getMember(creator);
        final Permission[] perms = new Permission[] { Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI };
        TC.createPermissionOverride(m).setAllow(perms).queue();

        final List<Role> devRoles = guild.getRolesByName("Dev - " + TC.getName(), true);
        final Role devRole = devRoles != null && !devRoles.isEmpty() ? devRoles.get(0) : null;
        final boolean isDevRole = devRole != null;
        final String mention = isDevRole ? devRole.getAsMention() : guild.getOwner().getAsMention();
        if(isDevRole) {
            TC.createPermissionOverride(devRole).setAllow(perms).queue();
        }
        final String type = this.type.name();
        TC.sendMessage("Dear " + creatorMention + ",\n\nThe developer(s) have been contacted about your " + type + ".\nPlease send any relevant info about your " + type + " in this text channel.\n\nThanks, " + mention).queue();
        sendCanBeFoundAtMessage(msg.getTextChannel(), creator, "Your " + type + " for " + project + " can be found at " + TC.getAsMention());
        manager.setTopic(TC.getCreationTime().toString()).queueAfter(1, TimeUnit.SECONDS);
    }
    public void delete() {
        channels.remove(this);
        channel.delete().reason("Resolved").queue();
        if(canBeFoundAtMsg != null) {
            canBeFoundAtMsg.delete().queueAfter(1, TimeUnit.SECONDS);
        }
    }
    private void sendCanBeFoundAtMessage(TextChannel tc, User user, String message) {
        tc.sendMessage(user.getAsMention() + ", " + message).queue(this::did);
    }
    private void did(Message message) {
        canBeFoundAtMsg = message;
    }

    public TicketType getType() { return type; }
    public Project getProject() { return project; }
    public User getCreator() { return creator; }
    public String getCreatorMention() { return creatorMention; }
    public Guild getGuild() { return guild; }
    public Message getMessage() { return msg; }

    public static iTextChannel valueOf(TextChannel channel) {
        for(iTextChannel i : channels) {
            if(i.channel == channel) {
                return i;
            }
        }
        return null;
    }
}
