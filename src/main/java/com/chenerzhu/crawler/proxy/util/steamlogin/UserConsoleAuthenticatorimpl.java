package com.chenerzhu.crawler.proxy.util.steamlogin;

import in.dragonbra.javasteam.steam.authentication.IAuthenticator;
import in.dragonbra.javasteam.steam.authentication.UserConsoleAuthenticator;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static java.sql.DriverManager.println;

@Data
public class UserConsoleAuthenticatorimpl implements IAuthenticator {

    private String sharedSecret;
    @NotNull
    @Override
    public CompletableFuture<String> getDeviceCode(boolean b) {
        String vbAREhPkibtwemEklyePZH2b73c = SampleLogonAuthentication.generateOneTimeCode(sharedSecret, null);
        return CompletableFuture.completedFuture(vbAREhPkibtwemEklyePZH2b73c);
    }

    @NotNull
    @Override
    public CompletableFuture<String> getEmailCode(@Nullable String s, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public CompletableFuture<Boolean> acceptDeviceConfirmation() {
        println("STEAM GUARD! Use the Steam Mobile App to confirm your sign in...");

        return CompletableFuture.completedFuture(true);
    }
}
