package me.randomHashTags.DiscordAdmin;

import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Giveaway {
    private final Random random = new Random();
    public final User creator;
    public final String type;
    public final MessageChannel channel;
    public final String startMessage;
    public final long delay;
    public final TimeUnit unit;
    private final String emote = "\uD83C\uDF89";
    private List<MessageReaction> reactions = null;
    public Giveaway(User creator, String type, MessageChannel channel, String startMessage, long delay, TimeUnit unit) {
        this.creator = creator;
        this.type = type;
        this.channel = channel;
        this.startMessage = startMessage;
        this.delay = delay;
        this.unit = unit;
        channel.sendMessage(startMessage).queue(this::sentMessage);
    }
    private void sentMessage(Message msg) {
        msg.addReaction(emote).queue();
        final String id = msg.getId();
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable r = () -> channel.getMessageById(id).queue(this::pickWinner);
        scheduler.schedule(r, delay, unit);
    }
    private void pickwinner(List<User> users) {
        for(User user : users) if(user.isBot()) users.remove(user);

        for(MessageReaction reaction : reactions) {
            if(reaction.getReactionEmote().getName().equals(emote)) {
                final User winner = users.get(random.nextInt(users.size()));
                final String mention = winner.getAsMention();
                channel.sendMessage("Congratulations " + mention + " on winning the **" + type + "**!\n  DM " + creator.getAsMention() + " to collect your reward!").queue();
                return;
            }
        }
    }
    private void pickWinner(Message msg) {
        reactions = msg.getReactions();
        boolean did = false;
        for(MessageReaction reaction : reactions) {
            if(!did && reaction.getReactionEmote().getName().equals(emote)) {
                did = true;
                reaction.getUsers().queue(this::pickwinner);
            }
        }
    }
}
