package me.randomhashtags.discordadmin.util;

public enum TicketType {
    BUG_REPORT("Bugs"),
    ROLE_REQUEST("Role Requests"),
    SUGGESTION_REQUEST("Suggestions"),
    ;
    private String channelName;
    TicketType(String channelName) {
        this.channelName = channelName;
    }
    public String getChannelName() { return channelName; }
}
