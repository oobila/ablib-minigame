package com.github.oobila.bukkit.minigame.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArenaStatus {

    //https://minecraft-heads.com/custom-heads/head/52313-dirt-path

    SETUP("35a9cfdb78ac1beb7823bcc87b7e3af82daee10a57835dc4cb632bf1cee21580"),
    READY("35a9cfdb78ac1beb7823bcc87b7e3af82daee10a57835dc4cb632bf1cee21580"),
    IN_USE("35a9cfdb78ac1beb7823bcc87b7e3af82daee10a57835dc4cb632bf1cee21580"),
    CLEAN_UP("35a9cfdb78ac1beb7823bcc87b7e3af82daee10a57835dc4cb632bf1cee21580");

    private final String texture;
}
