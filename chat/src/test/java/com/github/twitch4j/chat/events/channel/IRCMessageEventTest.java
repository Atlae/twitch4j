package com.github.twitch4j.chat.events.channel;

import com.github.twitch4j.common.enums.CommandPermission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unittest")
public class IRCMessageEventTest {

    @Test
    @DisplayName("Tests that CLEARCHAT is parsed by IRCMessageEvent")
    void parseChatClear() {
        IRCMessageEvent e = build("@room-id=12345678;tmi-sent-ts=1642715756806 :tmi.twitch.tv CLEARCHAT #dallas");

        assertEquals("CLEARCHAT", e.getCommandType());
        assertEquals("dallas", e.getChannelName().orElse(null));
        assertEquals("12345678", e.getChannelId());
    }

    @Test
    @DisplayName("Tests that CLEARMSG is parsed by IRCMessageEvent")
    void parseMessageDeletion() {
        IRCMessageEvent e = build("@login=foo;room-id=;target-msg-id=94e6c7ff-bf98-4faa-af5d-7ad633a158a9;tmi-sent-ts=1642720582342 :tmi.twitch.tv CLEARMSG #bar :what a great day");

        assertEquals("foo", e.getUserName());
        assertEquals("bar", e.getChannelName().orElse(null));
        assertEquals("CLEARMSG", e.getCommandType());
        assertEquals("what a great day", e.getMessage().orElse(null));
        assertEquals("94e6c7ff-bf98-4faa-af5d-7ad633a158a9", e.getTagValue("target-msg-id").orElse(null));
    }

    @Test
    @DisplayName("Tests that GLOBALUSERSTATE is parsed by IRCMessageEvent")
    void parseGlobalUserState() {
        IRCMessageEvent e = build("@badge-info=subscriber/8;badges=subscriber/6;color=#0D4200;display-name=dallas;emote-sets=0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239;turbo=0;user-id=12345678;user-type=admin " +
            ":tmi.twitch.tv GLOBALUSERSTATE");

        assertEquals("GLOBALUSERSTATE", e.getCommandType());
        assertEquals("12345678", e.getUserId());
        assertEquals("dallas", e.getTagValue("display-name").orElse(null));
        assertEquals("dallas", e.getUserName());
        assertEquals("0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239", e.getTagValue("emote-sets").orElse(null));
    }

    @Test
    @DisplayName("Test that normal messages are parsed by IRCMessageEvent")
    void parseMessage() {
        IRCMessageEvent e = build("@badge-info=;badges=broadcaster/1;client-nonce=459e3142897c7a22b7d275178f2259e0;color=#0000FF;display-name=lovingt3s;emote-only=1;emotes=62835:0-10;first-msg=0;flags=;" +
            "id=885196de-cb67-427a-baa8-82f9b0fcd05f;mod=0;room-id=713936733;subscriber=0;tmi-sent-ts=1643904084794;turbo=0;user-id=713936733;user-type= " +
            ":lovingt3s!lovingt3s@lovingt3s.tmi.twitch.tv PRIVMSG #lovingt3s :bleedPurple");

        assertEquals("bleedPurple", e.getMessage().orElse(null));
        assertEquals("lovingt3s", e.getChannelName().orElse(null));
        assertEquals("713936733", e.getChannelId());
        assertEquals("713936733", e.getUserId());
        assertEquals("lovingt3s", e.getUserName());
        assertEquals("PRIVMSG", e.getCommandType());
        assertTrue(e.getClientPermissions().contains(CommandPermission.BROADCASTER));
        assertEquals("885196de-cb67-427a-baa8-82f9b0fcd05f", e.getMessageId().orElse(null));
        assertEquals("459e3142897c7a22b7d275178f2259e0", e.getNonce().orElse(null));
        assertEquals("62835:0-10", e.getTagValue("emotes").orElse(null));
    }

    @Test
    @DisplayName("Tests that NOTICE is parsed by IRCMessageEvent")
    void parseNotice() {
        IRCMessageEvent e = build("@msg-id=delete_message_success :tmi.twitch.tv NOTICE #bar :The message from foo is now deleted.");

        assertEquals("NOTICE", e.getCommandType());
        assertEquals("bar", e.getChannelName().orElse(null));
        assertEquals("delete_message_success", e.getTags().get("msg-id"));
        assertEquals("The message from foo is now deleted.", e.getMessage().orElse(null));
    }

    @Test
    @DisplayName("Tests that RECONNECT is parsed by IRCMessageEvent")
    void parseReconnect() {
        IRCMessageEvent e = build(":tmi.twitch.tv RECONNECT");
        assertEquals("RECONNECT", e.getCommandType());
    }

    @Test
    @DisplayName("Tests that ROOMSTATE is parsed by IRCMessageEvent")
    void parseRoomState() {
        IRCMessageEvent e = build("@emote-only=0;followers-only=-1;r9k=0;rituals=0;room-id=12345678;slow=0;subs-only=0 :tmi.twitch.tv ROOMSTATE #bar");
        assertEquals("ROOMSTATE", e.getCommandType());
        assertEquals("bar", e.getChannelName().orElse(null));
        assertEquals("12345678", e.getChannelId());
        assertEquals("0", e.getTags().get("emote-only"));
        assertEquals("-1", e.getTags().get("followers-only"));
    }

    @Test
    @DisplayName("Tests that USERNOTICE is parsed by IRCMessageEvent")
    void parseUserNotice() {
        IRCMessageEvent e = build("@badge-info=;badges=staff/1,premium/1;color=#0000FF;display-name=TWW2;emotes=;id=e9176cd8-5e22-4684-ad40-ce53c2561c5e;login=tww2;mod=0;msg-id=subgift;" +
            "msg-param-months=1;msg-param-recipient-display-name=Mr_Woodchuck;msg-param-recipient-id=55554444;msg-param-recipient-name=mr_woodchuck;msg-param-sub-plan-name=House\\sof\\sNyoro~n;msg-param-sub-plan=1000;" +
            "room-id=12345678;subscriber=0;system-msg=TWW2\\sgifted\\sa\\sTier\\s1\\ssub\\sto\\sMr_Woodchuck!;tmi-sent-ts=1521159445153;turbo=0;user-id=87654321;user-type=staff :tmi.twitch.tv USERNOTICE #forstycup");

        assertEquals("USERNOTICE", e.getCommandType());
        assertEquals("12345678", e.getChannelId());
        assertEquals("forstycup", e.getChannelName().orElse(null));
        assertEquals("87654321", e.getUserId());
        assertEquals("TWW2 gifted a Tier 1 sub to Mr_Woodchuck!", e.getTagValue("system-msg").orElse(null));
        assertEquals("TWW2", e.getTagValue("display-name").orElse(null));
        assertEquals("tww2", e.getUserName());
        assertEquals("subgift", e.getTagValue("msg-id").orElse(null));
    }

    @Test
    @DisplayName("Tests that USERSTATE is parsed by IRCMessageEevent")
    void parseUserState() {
        IRCMessageEvent e = build("@badge-info=;badges=staff/1;color=#0D4200;display-name=ronni;emote-sets=0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239;mod=1;subscriber=1;turbo=1;user-type=staff " +
            ":tmi.twitch.tv USERSTATE #dallas");

        assertEquals("USERSTATE", e.getCommandType());
        assertEquals("dallas", e.getChannelName().orElse(null));
        assertEquals("ronni", e.getUserName());
        assertEquals("0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239", e.getTagValue("emote-sets").orElse(null));
        assertTrue(e.getClientPermissions().contains(CommandPermission.TWITCHSTAFF));
    }

    @Test
    @DisplayName("Test that whispers are parsed by IRCMessageEvent")
    void parseWhisper() {
        IRCMessageEvent e = build("@badges=;color=;display-name=HexaFice;emotes=;message-id=103;thread-id=142621956_149223493;turbo=0;user-id=142621956;user-type= " +
            ":hexafice!hexafice@hexafice.tmi.twitch.tv WHISPER twitch4j :test 123");

        assertEquals("test 123", e.getMessage().orElse(null));
        assertEquals("WHISPER", e.getCommandType());
        assertEquals("142621956", e.getUserId());
        assertEquals("hexafice", e.getUserName());
        assertEquals("HexaFice", e.getTagValue("display-name").orElse(null));
        assertEquals("twitch4j", e.getChannelName().orElse(null));
        assertTrue(e.getBadges() == null || e.getBadges().isEmpty());
        assertTrue(e.getBadgeInfo() == null || e.getBadgeInfo().isEmpty());
    }

    @Test
    @DisplayName("Test that 353 NAMES response is parsed by IRCMessageEvent")
    void parseNamesResponse() {
        IRCMessageEvent e = build(":justinfan77645.tmi.twitch.tv 353 justinfan77645 = #pajlada :vissb ogprodigy supabridge zneix suslada beatz pajbot");

        assertEquals("353", e.getCommandType());
        assertEquals("pajlada", e.getChannelName().orElse(null));
        assertEquals("vissb ogprodigy supabridge zneix suslada beatz pajbot", e.getMessage().orElse(null));
        assertTrue(e.getClientName().isPresent());
        assertTrue(e.getBadges().isEmpty());
        assertTrue(e.getBadgeInfo().isEmpty());
    }

    private static IRCMessageEvent build(String raw) {
        return new IRCMessageEvent(raw, Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet());
    }

}
