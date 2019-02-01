package com.lagel.com.mqttchat.Utilities;

/**
 * @since  20/06/17.
 * 
 */
public class ApiOnServer
{
    public static final String CHAT_RECEIVED_THUMBNAILS_FOLDER = "/Lagel/receivedThumbnails";
    public static final String CHAT_UPLOAD_THUMBNAILS_FOLDER = "/Lagel/upload";
    public static final String CHAT_DOODLES_FOLDER = "/Lagel/doodles";
    public static final String CHAT_DOWNLOADS_FOLDER = "/Lagel/";
    public static final String IMAGE_CAPTURE_URI = "/Lagel";
    public static final String CHAT_MULTER_UPLOAD_URL = "http://lagel.net:8009/";
    private static final String CHAT_UPLOAD_SERVER_URL = "http://lagel.net/";
    public static final String VIDEO_THUMBNAILS = "/Lagel/thumbnails";
    public static final String CHAT_UPLOAD_PATH = CHAT_UPLOAD_SERVER_URL +"chat/profilePics/";
    public static final String PROFILEPIC_UPLOAD_PATH = CHAT_UPLOAD_SERVER_URL +"chat/profilePics/";
    public static final String DELETE_DOWNLOAD = CHAT_MULTER_UPLOAD_URL +"deleteImage";
    public static final String HOST = "lagel.net";
    public static final String PORT = "1883";
    private static final String API_MAIN_LINK ="https://lagel.net/mqtt/";
    public static final String LOGIN_API = API_MAIN_LINK + "User/LoginWithEmail";
    public static final String VERIFY_API = API_MAIN_LINK + "User/verifyEmail";
    public static final String SIGNUP_API = API_MAIN_LINK + "User/SignupWithEmail";
    public static final String GET_USERS_API = API_MAIN_LINK + "User";
    public static final String TRENDING_STICKERS = "http://api.giphy.com/v1/stickers/trending?api_key=dc6zaTOxFJmzC";
    public static final String TRENDING_GIFS = "http://api.giphy.com/v1/gifs/trending?api_key=dc6zaTOxFJmzC";
    public static final String GIPHY_APIKEY = "&api_key=dc6zaTOxFJmzC";
    public static final String SEARCH_STICKERS = "http://api.giphy.com/v1/stickers/search?q=";
    public static final String SEARCH_GIFS = "http://api.giphy.com/v1/gifs/search?q=";
    public static final String USER_PROFILE = API_MAIN_LINK + "User/Profile";
    public static final String FETCH_CHATS = API_MAIN_LINK + "Chats";
    /*
     *Mqtt user name and password */
    public static final String MQTTUSER_NAME="ally";
    public static final String MQTTPASSWORD="77Gu6b9CrMfaMHtL";
    /**
     * GET api to fetch the messages into the list*/
    public static final String FETCH_MESSAGES = API_MAIN_LINK + "Messages";
    /*
    * Authorization key fro chat */
    public static final String AUTH_KEY="KMajNKHPqGt6kXwUbFN3dU46PjThSNTtrEnPZUefdasdfghsaderf1234567890ghfghsdfghjfghjkswdefrtgyhdfghj";

    public static final String PUSH_KEY="AIzaSyCCCRUAhS630RS4Dxfxw-X5mpbJ6CtfYyU";
}
