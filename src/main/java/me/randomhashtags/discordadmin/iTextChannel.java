package me.randomhashtags.discordadmin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class iTextChannel {
    private static final List<iTextChannel> channels = new ArrayList<>();
    public final User creator;
    public final Guild guild;
    public final Message msg;
    public final String type, plugin, creatorMention;

    private final ChannelAction ca;
    private TextChannel channel;
    private ChannelManager manager;
    private Message msgPlayerChannel;

    public iTextChannel(User creator, Guild guild, Message msg, String type, String plugin) {
        this.creator = creator;
        this.creatorMention = creator.getAsMention();
        this.guild = guild;
        this.msg = msg;
        this.type = type;
        this.plugin = plugin;
        final String categoryName;
        if(type.equals("bug")) {
            categoryName = "Bugs";
        } else if(type.equals("suggestion")) {
            categoryName = "Suggestions";
        } else {
            categoryName = "Role Requests";
        }
        final Category category = guild.getCategoriesByName(categoryName, false).get(0);
        ca = category.createTextChannel(plugin);
        ca.queue(this::createChannel);
        channels.add(this);
    }

    private void createChannel(Channel channel) {
        final TextChannel TC = (TextChannel) channel;
        this.channel = TC;
        manager = TC.getManager();
        final Member m = guild.getMember(creator);
        TC.createPermissionOverride(m).setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES).queue();
        TC.sendMessage("Dear " + creatorMention + ",\n\nThe developer has been contacted about your " + type + ".\nPlease send any relevant info about your " + type + " in this text channel.\n\nThanks, " + guild.getOwner().getAsMention()).queue();
        sendMention(msg.getTextChannel(), creator, "Your " + type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase() + " " + (type.equals("bug") ? "Report" : "Request") + " for " + plugin + " can be found at " + TC.getAsMention());
        manager.setTopic(TC.getCreationTime().toString()).queueAfter(1, TimeUnit.SECONDS);
    }

    public void fixed(Message msg) {
        if(type.equals("bug")) {
            fixed(msg.getTextChannel());
            msg.delete().queue();
        }
    }
    private void fixed(TextChannel t) {
        t.sendMessage("Dear " + creatorMention + ",\n\nThis report has been fixed for next update!\n\n**Thanks for helping improve " + plugin + "!**\n\nChannel will delete upon next update for " + plugin + ".").queue();
        manager.setParent(guild.getCategoriesByName("Fixed for next update", false).get(0)).queue();
        msgPlayerChannel.delete().queue(didDelete());
        final String role = plugin.equals("RandomPackage") || plugin.equals("RandomSky") || plugin.equals("Merchants") ? plugin : "SpigotMC Free Ping";
        manager.setTopic(role).queueAfter(1, TimeUnit.SECONDS);
    }
    public void delete() {
        channels.remove(this);
        channel.delete().reason("Resolved").queue();
        if(msgPlayerChannel != null) msgPlayerChannel.delete().queue();
    }
    private Consumer<Void> didDelete() {
        msgPlayerChannel = null;
        return null;
    }

    public static iTextChannel valueOf(TextChannel channel) {
        for(iTextChannel i : channels)
            if(i.channel == channel)
                return i;
        return null;
    }
    private void sendMention(TextChannel tc, User user, String message) {
        tc.sendMessage(user.getAsMention() + ", " + message).queue(this::did);
    }
    private void did(Message message) {
        this.msgPlayerChannel = message;
    }
}
