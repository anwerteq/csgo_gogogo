package com.xiaojuzi.util.steamlogin;

import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.config.CookiesConfig;
import com.xiaojuzi.util.HttpClientUtils;
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
import in.dragonbra.javasteam.util.log.DefaultLogListener;
import in.dragonbra.javasteam.util.log.LogManager;
import okhttp3.OkHttpClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CancellationException;

/**
 * @author lossy
 * @since 2023-03-19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SampleLogonAuthentication implements Runnable {

    private SteamClient steamClient;

    private CallbackManager manager;

    private SteamUser steamUser;

    private boolean isRunning;

    private final String user;

    private final String pass;

    private String previouslyStoredGuardData; // For the sake of this sample, we do not persist guard data

    public SampleLogonAuthentication(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public static void main1(String[] args) {
//        if (args.length < 2) {
//            System.out.println("Sample1: No username and password specified!");
//            return;
//        }

        LogManager.addListener(new DefaultLogListener());

        new SampleLogonAuthentication("mu64KKRO", "QingLiu98!").run();
    }

    @Override
    public void run() {

        // // If any configuration needs to be set; such as connection protocol api key, etc., you can configure it like so.
        // var configuration = SteamConfiguration.create(config -> {
        //    config.withProtocolTypes(ProtocolTypes.WEB_SOCKET);
        // });
        // // create our steamclient instance with custom configuration.
        // steamClient = new SteamClient(configuration);


        // 创建一个代理对象，指定代理服务器的 IP 地址和端口号
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));

        // 创建 OkHttpClient 并设置代理
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
        // create our steamclient instance using default configuration
        SteamConfigurationState steamConfigurationState = new SteamConfigurationState(true,0,5000L,EnumSet.of(
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

    @SuppressWarnings("DanglingJavadoc")
    private void onConnected(ConnectedCallback callback) {
        System.out.println("Connected to Steam! Logging in " + user + "...");

        var shouldRememberPassword = false;

        AuthSessionDetails authDetails = new AuthSessionDetails();
        authDetails.username = user;
        authDetails.password = pass;
        authDetails.persistentSession = shouldRememberPassword;

        // See NewGuardData comment below.
        authDetails.guardData = previouslyStoredGuardData;

        /**
         * {@link UserConsoleAuthenticator} is the default authenticator implementation provided by JavaSteam
         * for ease of use which blocks the thread and asks for user input to enter the code.
         * However, if you require special handling (e.g. you have the TOTP secret and can generate codes on the fly),
         * you can implement your own {@link IAuthenticator}.
         */
        authDetails.authenticator = new UserConsoleAuthenticator();

        try {
            // Begin authenticating via credentials.
            var authSession = steamClient.getAuthentication().beginAuthSessionViaCredentials(authDetails);

            // Note: This is blocking, it would be up to you to make it non-blocking for Java.
            // Note: Kotlin uses should use ".pollingWaitForResult()" as its a suspending function.
            String vbAREhPkibtwemEklyePZH2b73c = generateOneTimeCode("vbAREhPkibtwemEklyePZH2b73c", null);
            String inputData = vbAREhPkibtwemEklyePZH2b73c;
            InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());

            // 设置标准输入流
            System.setIn(inputStream);

            AuthPollResult pollResponse = authSession.pollingWaitForResultCompat().get();

            if (pollResponse.getNewGuardData() != null) {
                // When using certain two factor methods (such as email 2fa), guard data may be provided by Steam
                // for use in future authentication sessions to avoid triggering 2FA again (this works similarly to the old sentry file system).
                // Do note that this guard data is also a JWT token and has an expiration date.
                previouslyStoredGuardData = pollResponse.getNewGuardData();
            }

            // Logon to Steam with the access token we have received
            // Note that we are using RefreshToken for logging on here
            LogOnDetails details = new LogOnDetails();
            details.setUsername(pollResponse.getAccountName());
            details.setAccessToken(pollResponse.getRefreshToken());

            // Set LoginID to a non-zero value if you have another client connected using the same account,
            // the same private ip, and same public ip.
            details.setLoginID(149);

            steamUser.logOn(details);

            // This is not required, but it is possible to parse the JWT access token to see the scope and expiration date.
            // parseJsonWebToken(pollResponse.accessToken, "AccessToken");
            // parseJsonWebToken(pollResponse.refreshToken, "RefreshToken");
        } catch (Exception e) {
            // List a couple of exceptions that could be important to handle.
            if (e instanceof AuthenticationException) {
                System.err.println("An Authentication error has occurred. " + e.getMessage());
            } else if (e instanceof CancellationException) {
                System.err.println("An Cancellation exception was raised. Usually means a timeout occurred. " + e.getMessage());
            } else {
                System.err.println("An error occurred:" + e.getMessage());
            }

            steamUser.logOff();
        }
    }

    private void onDisconnected(DisconnectedCallback callback) {
        System.out.println("Disconnected from Steam. User initialized: " + callback.isUserInitiated());

        // If the disconnection was not user initiated, we will retry connecting to steam again after a short delay.
        if (callback.isUserInitiated()) {
            isRunning = false;
        } else {
            try {
                Thread.sleep(2000L);
                steamClient.connect();
            } catch (InterruptedException e) {
                System.err.println("An Interrupted exception occurred. " + e.getMessage());
            }
        }
    }

    private void onLoggedOn(LoggedOnCallback callback) {
        if (callback.getResult() != EResult.OK) {
            System.out.println("Unable to logon to Steam: " + callback.getResult() + " / " + callback.getExtendedResult());

            isRunning = false;
            return;
        }

        System.out.println("Successfully logged on!");

        // at this point, we'd be able to perform actions on Steam

        // for this sample we'll just log off
        steamUser.logOff();
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

        // For brevity, we will simply output formatted json to console
        System.out.println(name + ": " + formatted);
        System.out.println();
    }


    public static void main(String[] args) {
        System.out.println(generateOneTimeCode("R3dTE2+Orb9oQgesGFqUP6cOSAs=", null));
    }
    /**
     * 生成认证码
     *
     * @param sharedSecret
     * @param timestamp
     * @return
     */
    public static String generateOneTimeCode(String sharedSecret, Long timestamp) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis() / 1000;
            timestamp += tryToGetTimeDeltaFromSteam(); // Implement this method as needed
        }
        // Pack the timestamp as Big Endian, uint64
        ByteBuffer timeBuffer = ByteBuffer.allocate(8);
        timeBuffer.order(ByteOrder.BIG_ENDIAN);
        timeBuffer.putLong(timestamp / 30);

        // Calculate HMAC using SHA-1
        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(sharedSecret), "HmacSHA1");
        try {
            hmac.init(keySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] timeHmac = hmac.doFinal(timeBuffer.array());

        // Get the 20th byte and compute the offset
        int begin = timeHmac[19] & 0xf;

        // Unpack as Big Endian uint32
        ByteBuffer codeBuffer = ByteBuffer.wrap(timeHmac, begin, 4);
        codeBuffer.order(ByteOrder.BIG_ENDIAN);
        int fullCode = codeBuffer.getInt() & 0x7fffffff;

        // Define the character set
        String chars = "23456789BCDFGHJKMNPQRTVWXY";
        StringBuilder code = new StringBuilder();

        // Generate the code
        for (int i = 0; i < 5; i++) {
            int[] divmod = divmod(fullCode, chars.length());
            fullCode = divmod[0];
            int index = divmod[1];
            code.append(chars.charAt(index));
        }

        return code.toString();
    }
    public static int[] divmod(int a, int b) {
        return new int[]{a / b, a % b};
    }

    public static long timeDelta = Long.MAX_VALUE;

    private static long tryToGetTimeDeltaFromSteam() {
        if (timeDelta == Long.MAX_VALUE) {
            for (int i = 0; i < 3; i++) {
                long serverTime = getSteamServerTime();
                if (serverTime != -1) {
                    timeDelta = serverTime - System.currentTimeMillis() / 1000; // Convert to seconds
                    return (int) timeDelta;
                }
            }
            timeDelta = 0;
        }
        return timeDelta;
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    public static long getSteamServerTime() {
        String url = "https://api.steampowered.com/ITwoFactorService/QueryTime/v1/";
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
        headerMap.remove("Content-Length");
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        String response = HttpClientUtils.sendPost(url, "", headerMap);

        JSONObject jsonObject = JSONObject.parseObject(response);
        Long server_time = jsonObject.getJSONObject("response").getLong("server_time");
        return server_time;
    }


}
