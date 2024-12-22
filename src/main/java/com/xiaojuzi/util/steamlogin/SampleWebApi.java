package com.xiaojuzi.util.steamlogin;

import in.dragonbra.javasteam.enums.EClientPersonaStateFlag;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.enums.EUniverse;
import in.dragonbra.javasteam.networking.steam3.ProtocolTypes;
import in.dragonbra.javasteam.steam.discovery.MemoryServerListProvider;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfigurationState;
import in.dragonbra.javasteam.steam.webapi.WebAPI;
import in.dragonbra.javasteam.types.KeyValue;
import in.dragonbra.javasteam.util.log.DefaultLogListener;
import in.dragonbra.javasteam.util.log.LogManager;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

//
// Sample 6: WebAPI
//
// this sample will give an example of how the WebAPI utilities can be used to
// interact with the Steam Web APIs
//
// the Steam Web APIs are structured as a set of "interfaces" with methods,
// similar to classes in OO languages.
// as such, the API for interacting with the WebAPI follows a similar methodology

/**
 * @author lngtr
 * @since 2021-10-11
 */
@SuppressWarnings("FieldCanBeLocal")
public class SampleWebApi implements Runnable {

    private SteamClient steamClient;

    private CallbackManager manager;

    private SteamUser steamUser;

    private boolean isRunning;

    private final String user;

    private final String pass;

    private OkHttpClient client;


    public SampleWebApi(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("Sample6: No username and password specified!");
//            return;
//        }

        LogManager.addListener(new DefaultLogListener());

        new SampleWebApi("mu64KKRO", "QingLiu98!").run();
    }

    @Override
    public void run() {
// 创建一个代理对象，指定代理服务器的 IP 地址和端口号
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));

        // 创建 OkHttpClient 并设置代理
        client = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
        // create our steamclient instance using default configuration
        SteamConfigurationState steamConfigurationState = new SteamConfigurationState(true,0,5000L, EnumSet.of(
                EClientPersonaStateFlag.PlayerName,
                EClientPersonaStateFlag.Presence,
                EClientPersonaStateFlag.SourceID,
                EClientPersonaStateFlag.GameExtraInfo,
                EClientPersonaStateFlag.LastSeen
        ),client, EnumSet.of(ProtocolTypes.TCP),new MemoryServerListProvider(), EUniverse.Public, WebAPI.DEFAULT_BASE_ADDRESS,null);

        SteamConfiguration steamConfiguration = new SteamConfiguration(steamConfigurationState);
        steamClient = new SteamClient(steamConfiguration);

        // create the callback manager which will route callbacks to function calls
        manager = new CallbackManager(steamClient);

        // get the steamuser handler, which is used for logging on after successfully connecting
        steamUser = steamClient.getHandler(SteamUser.class);

        // register a few callbacks we're interested in
        // these are registered upon creation to a callback manager, which will then route the callbacks
        // to the functions specified
        manager.subscribe(ConnectedCallback.class, this::onConnected);
        manager.subscribe(DisconnectedCallback.class, this::onDisconnected);

        manager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
        manager.subscribe(LoggedOffCallback.class, this::onLoggedOff);

        isRunning = true;

        System.out.println("Connecting to steam...");

        // initiate the connection
        steamClient.connect();

        // create our callback handling loop
        while (isRunning) {
            // in order for the callbacks to get routed, they need to be handled by the manager
            manager.runWaitCallbacks(1000L);
        }
    }

    private void onConnected(ConnectedCallback callback) {
        System.out.println("Connected to Steam! Logging in " + user + "...");

        LogOnDetails details = new LogOnDetails();
        details.setUsername(user);
        details.setPassword(pass);
        details.setTwoFactorCode(SampleLogonAuthentication.generateOneTimeCode("vbAREhPkibtwemEklyePZH2b73c", null));

        // Set LoginID to a non-zero value if you have another client connected using the same account,
        // the same private ip, and same public ip.
        details.setLoginID(149);

        steamUser.logOn(details);
    }

    private void onDisconnected(DisconnectedCallback callback) {
        System.out.println("Disconnected from Steam");

        isRunning = false;
    }

    private void onLoggedOn(LoggedOnCallback callback) {
        if (callback.getResult() != EResult.OK) {
            if (callback.getResult() == EResult.AccountLogonDenied) {
                // if we recieve AccountLogonDenied or one of its flavors (AccountLogonDeniedNoMailSent, etc.)
                // then the account we're logging into is SteamGuard protected
                // see sample 5 for how SteamGuard can be handled

                System.out.println("Unable to logon to Steam: This account is SteamGuard protected.");

                isRunning = false;
                return;
            }

            System.out.println("Unable to logon to Steam: " + callback.getResult() + " / " + callback.getExtendedResult());
            isRunning = false;
            return;

        }

        System.out.println("Successfully logged on!");

        // at this point, we'd be able to perform actions on Steam

        WebAPI api = steamClient.getConfiguration().getWebAPI("ISteamNotificationService");

        try {
            Map<String, String> args = new HashMap<>();
            args.put("access_token", "eyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MDAxNV8yNTZGRkM4OF84MTk0MyIsICJzdWIiOiAiNzY1NjExOTkzNTExODU0MDEiLCAiYXVkIjogWyAid2ViOmNvbW11bml0eSIgXSwgImV4cCI6IDE3MzMxNDA4MDEsICJuYmYiOiAxNzI0NDE0MTYzLCAiaWF0IjogMTczMzA1NDE2MywgImp0aSI6ICIwMDE4XzI1NkZGQzg3XzVDMzc1IiwgIm9hdCI6IDE3MzMwNTQxNjMsICJydF9leHAiOiAxNzUxMTIyNjI1LCAicGVyIjogMCwgImlwX3N1YmplY3QiOiAiMTYwLjE5MS40MS4yNTEiLCAiaXBfY29uZmlybWVyIjogIjE2MC4xOTEuNDEuMjUxIiB9.1mNFK8c08kwGSL8vjSkqS7eOGmRSrBnsnONN2hAaTnC7jOmEmwcuq2XDAZ4fvrVYMvCNd9rFjAKdS75kHxJHDw");
            args.put("origin", "https://steamcommunity.com");
            args.put("input_protobuf_encoded", "EAYYACABKAA=");

            KeyValue result = api.call("GetSteamNotifications", 1, args);

            printKeyValue(result, 1);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // for this sample we'll just log off
        steamUser.logOff();
    }

    private void onLoggedOff(LoggedOffCallback callback) {
        System.out.println("Logged off of Steam: " + callback.getResult());

        isRunning = false;
    }

    // Recursively print out child KeyValues.
    private void printKeyValue(KeyValue keyValue, int depth) {
        String spacePadding = String.join("", Collections.nCopies(depth, "    "));

        if (keyValue.getChildren().isEmpty()) {
            System.out.println(spacePadding + keyValue.getName() + ": " + keyValue.getValue());
        } else {
            System.out.println(spacePadding + keyValue.getName() + ":");
            for (KeyValue child : keyValue.getChildren()) {
                printKeyValue(child, depth + 1);
            }
        }
    }
}
