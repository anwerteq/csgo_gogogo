package com.chenerzhu.crawler.proxy.util.steamlogin;

import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import in.dragonbra.javasteam.enums.EClientPersonaStateFlag;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.enums.EUniverse;
import in.dragonbra.javasteam.networking.steam3.ProtocolTypes;
import in.dragonbra.javasteam.steam.authentication.*;
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
import in.dragonbra.javasteam.util.Strings;
import in.dragonbra.javasteam.util.log.DefaultLogListener;
import in.dragonbra.javasteam.util.log.LogManager;
import lombok.var;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CancellationException;

/**
 * @author lossy
 * @since 2023-11-06
 */
@SuppressWarnings("FieldCanBeLocal")
public class SampleWebCookie implements Runnable {

    private SteamClient steamClient;

    private SteamAuthentication auth;

    private CallbackManager manager;

    private SteamUser steamUser;

    private boolean isRunning;

    private final String user;

    private final String pass;

    private String accessToken;

    private String refreshToken;

    private OkHttpClient client;

    private  SteamUserDate steamUserDate;


    public SampleWebCookie(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public SampleWebCookie(String user, String pass,SteamUserDate steamUserDate) {
        this.user = user;
        this.pass = pass;
        this.steamUserDate = steamUserDate;
    }

    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("SampleWebCookie: No username and password specified!");
//            return;
//        }

        LogManager.addListener(new DefaultLogListener());

        new SampleWebCookie("mj00cAux", "QingLiu98!").run();
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

        AuthSessionDetails authSessionDetails = new AuthSessionDetails();
        authSessionDetails.username = user;
        authSessionDetails.password = pass;
        authSessionDetails.persistentSession = false;
        authSessionDetails.authenticator = new UserConsoleAuthenticatorimpl();

        // get the authentication handler, which used for authenticating with Steam
        auth = new SteamAuthentication(steamClient);

        try {

            CredentialsAuthSession authSession = auth.beginAuthSessionViaCredentials(authSessionDetails);

            // Note: This is blocking, it would be up to you to make it non-blocking for Java.
            // Note: Kotlin uses should use ".pollingWaitForResult()" as its a suspending function.
            AuthPollResult pollResponse = authSession.pollingWaitForResultCompat().get();

            LogOnDetails logOnDetails = new LogOnDetails();
            logOnDetails.setUsername(pollResponse.getAccountName());
            logOnDetails.setAccessToken(pollResponse.getRefreshToken());

            // Set LoginID to a non-zero value if you have another client connected using the same account,
            // the same private ip, and same public ip.
            logOnDetails.setLoginID(149);

            steamUser.logOn(logOnDetails);
            // AccessToken can be used as the steamLogi  nSecure cookie
            // RefreshToken is required to generate new access tokens
            accessToken = pollResponse.getAccessToken();
            refreshToken = pollResponse.getRefreshToken();


        } catch (Exception e) {
            System.err.println(e.getMessage());

            // List a couple of exceptions that could be important to handle.
            if (e instanceof AuthenticationException) {
                System.out.println("An Authentication error has occurred. " + e.getMessage());
            }

            if (e instanceof CancellationException) {
                System.out.println("An Cancellation exception was raised. Usually means a timeout occurred. " + e.getMessage());
            }
        }
    }

    private void onDisconnected(DisconnectedCallback callback) {
        steamUserDate.setIsTokenExpired(true);
        steamUserDate.setAuth(null);
        steamUserDate.setCallback(null);
        steamUserDate.getSession().setRefreshToken("");
        System.out.println("Disconnected from Steam");
        new Thread(new SampleWebCookie(user,pass,steamUserDate)).start();
        isRunning = false;
    }

    private void onLoggedOn(LoggedOnCallback callback) {
        if (callback.getResult() != EResult.OK) {
            System.out.println("Unable to login to steam: " + callback.getResult() + " / " + callback.getExtendedResult());

            isRunning = false;

            return;
        }
        System.out.println("Successfully logged on!");

        // This is how you concatenate the cookie, you can set it on the Steam domains, and it should work
        // but actual usage of this will be left as an exercise for the reader
        @SuppressWarnings("unused")
        String steamLoginSecure = callback.getClientSteamID().convertToUInt64() + "||" + accessToken;

        // The access token expires in 24 hours (at the time of writing) so you will have to renew it.
        // Parse this token with a JWT library to get the expiration date and set up a timer to renew it.
        // To renew you will have to call this:
        // When allowRenewal is set to true, Steam may return new RefreshToken
        AccessTokenGenerateResult newTokens = auth.generateAccessTokenForApp(callback.getClientSteamID(), refreshToken, true);

        accessToken = newTokens.getAccessToken();
        if (!Strings.isNullOrEmpty(newTokens.getRefreshToken())) {
            refreshToken = newTokens.getRefreshToken();
        }
        steamLoginSecure = callback.getClientSteamID().convertToUInt64() + "||" + accessToken;
        //设置steam的token
        SteamUserDate.steamTokensNumberAndTokenMap.put(user,steamLoginSecure);
        // Do not forget to update steamLoginSecure with the new accessToken!
        // Begin authenticating via credentials.


        // This is not required, but it is possible to parse the JWT access token to see the scope and expiration date.
        parseJsonWebToken(accessToken, "AccessToken");
        parseJsonWebToken(refreshToken, "RefreshToken");
        steamUserDate.setAuth(auth);
        steamUserDate.setCallback(callback);
        steamUserDate.getSession().setRefreshToken(refreshToken);
        steamUserDate.setIsTokenExpired(false);
        // for this sample we'll just log off
//        steamUser.logOff();
    }

    private void onLoggedOff(LoggedOffCallback callback) {
        System.out.println("Logged off of Steam: " + callback.getResult());

        isRunning = false;
    }

    @SuppressWarnings("unused")
    private void parseJsonWebToken(String token, String name) {
        String[] tokenComponents = token.split("\\.");

        // Fix up base64url to normal base64
        String base64 = tokenComponents[1].replace('-', '+').replace('_', '/');

        if (base64.length() % 4 != 0) {
            base64 += new String(new char[4 - base64.length() % 4]).replace('\0', '=');
        }

        byte[] payloadBytes = Base64.getDecoder().decode(base64);

        // Payload can be parsed as JSON, and then fields such expiration date, scope, etc can be accessed
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement payload = JsonParser.parseString(new String(payloadBytes));
        String formatted = gson.toJson(payload);

        JSONObject jsonObject = new JSONObject(formatted);
        String exp = jsonObject.getStr("exp");
        String timeStr = convertTimestampToDate(Long.valueOf(exp));

        // For brevity, we will simply output formatted json to console
        System.out.println(name + ": " + formatted);
        System.out.println(name + ": " + formatted);

        System.out.println(name+":"+timeStr);
        System.out.println();
    }


    public static String convertTimestampToDate(long timestamp) {
        // 将时间戳转换为 Instant
        Instant instant = Instant.ofEpochSecond(timestamp);

        // 使用系统默认时区或指定时区
        ZoneId zoneId = ZoneId.systemDefault();

        // 定义格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(zoneId);

        // 格式化时间戳
        return formatter.format(instant);
    }
}
