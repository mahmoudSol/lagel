package com.lagel.com.utility;

/**
 * <h>ApiUrl</h>
 * <p>
 * In this clas we declare on base url and from the next time
 * we used to append api name with this base url.
 * </p>
 *
 * @since 3/31/2017
 */
public class ApiUrl {
    //private static final String BASE_URL = "http://138.197.114.213/api/";
    private static final String BASE_URL = "https://lagel.net/api/";
   private static final String BASE_URL2 = "https://lagelfoundation.org/api/";

   /// "TWILIO_ACCOUNT_SID": "AC16fad06566037a047ef1b10d14f651be",
    //"TWILIO_AUTH_TOKEN": "94ac57c83b3739b8a26536ac32ab72f6",

    /**
     * Login service
     */
    public static final String LOGIN = BASE_URL + "login";


    public static final String CHANGE_PASSWORD = BASE_URL + "changepassword-phonenumber";
    public static final String VERIFY_OTP = BASE_URL + "verify-otp";
    /**
     * USER LOCATION UPDATE
     */
    public static final String USER_LOCATION = BASE_URL + "userLocation";
    /**
     * Reset password
     */
    public static final String RESET_PASSWORD = BASE_URL + "resetPassword";

    /**
     * usernameCheck
     */
    public static final String USER_NAME_CHECK = BASE_URL + "usernameCheck";

    /**
     * emailCheck
     */
    public static final String EMAIL_ID_CHECK = BASE_URL + "emailCheck";

    /**
     * update Phone Number
     */
    public static final String OTP_PROFILE_NUMBER = BASE_URL + "profile/phoneNumber";

    /**
     * phoneNumberCheck
     */
    public static final String PHONE_NUMBER_CHECK = BASE_URL + "phoneNumberCheck";

    /**
     * generate otp
     */
    public static final String OTP = BASE_URL + "otp";

    /**
     * Device Info
     */
    public static final String LOG_DEVICE = BASE_URL + "logDevice";

    /**
     * LOG GUEST
     */
    public static final String LOG_GUEST = BASE_URL + "logGuest";

    /**
     * Sign up service
     */
    public static final String SIGN_UP = BASE_URL + "register";

    /**
     * email verification
     */
    public static final String VERIFY_FACEBOOK_LOGIN = BASE_URL + "facebook/me";

    /**
     * email verification
     */
    public static final String VERIFY_EMAIL = BASE_URL + "email/me";

    /**
     * email verification
     */
    public static final String VERIFY_GOOGLE_PLUS = BASE_URL + "google/me";

    /**
     * Link paypal
     */
    public static final String LINK_PAYPAL = BASE_URL + "paypal/me";

    /**
     * edit Profile
     */
    public static final String EDIT_PROFILE = BASE_URL + "editProfile";

    /**
     * Get Cloudinary details
     */
    public static final String GET_CLOUDINARY_DETAILS = BASE_URL + "getSignature";

    /**
     * Search Filter
     */
    public static final String SEARCH_FILTER = BASE_URL + "searchFilter/staging";
    //public static final String SEARCH_FILTER2 = BASE_URL2 + "searchFilter/m";

    /**
     * Get All posts for logged in user
     */
    public static final String GET_USER_ALL_POSTS = BASE_URL + "allPosts/users/m";

    /**
     * allPosts/guests/m
     */
    public static final String GET_GUEST_ALL_POSTS = BASE_URL + "allPosts/guests/m";

    /**
     * getPostsById/users
     */
    public static final String GET_POST_BY_ID_USER = BASE_URL + "getPostsById/users";

    /**
     * getPostsById/users
     */
    public static final String GET_POST_BY_ID_GUEST = BASE_URL + "getPostsById/guests";

    /**
     * Like the product
     */
    public static final String LIKE_PRODUCT = BASE_URL + "like";

    /**
     * unlike the product
     */
    public static final String UNLIKE_PRODUCT = BASE_URL + "unlike";

    /**
     * Follow
     */
    public static final String FOLLOW = BASE_URL + "follow/";

    /**
     * Un follow
     */
    public static final String UNFOLLOW = BASE_URL + "unfollow/";

    /**
     * get notification(following Activity)
     */
    public static final String FOLLOWING_ACTIVITY = BASE_URL + "followingActivity";

    /**
     * get notification(following Activity)
     */
    public static final String SELF_ACTIVITY = BASE_URL + "selfActivity";

    /**
     * Get all news feed datas
     */
    public static final String HOME = BASE_URL + "home";

    /**
     * user profile
     */
    public static final String USER_PROFILE = BASE_URL + "profile/users";

    /**
     * user rating
     */
    public static final String USER_RATING = BASE_URL + "userAvgRating";

    /**
     * suggested post
     */
    public static final String SUGGESTED_POSTS = BASE_URL + "suggestedPost";


    /**
     * get user search history
     */
    public static final String GET_USER_SEARCH_HISTORY = BASE_URL + "getUserSearchHistory";


    /**
     * add searchHistory
     */
    public static final String ADD_KEYWORD_IN_SEARCH_HISTORY = BASE_URL + "searchHistory";


    /**
     * profile guests
     */
    public static final String GUEST_PROFILE = BASE_URL + "profile/guests";

    /**
     * profile posts
     */
    public static final String PROFILE_POST = BASE_URL + "profile/posts/";

    /**
     * profile posts
     */
    public static final String GUEST_PROFILE_POST = BASE_URL + "profile/guests/posts";

    /**
     * get Follower
     */
    public static final String GET_FOLLOWER = BASE_URL + "getFollowers";


    /**
     * get Following
     */
    public static final String GET_FOLLOWING = BASE_URL + "getFollowing";

    /**
     *
     * get Member Following
     */
    public static final String GET_MEMBER_FOLLOWING = BASE_URL + "getMemberFollowing";

    /**
     * get Member Followers
     */
    public static final String GET_MEMBER_FOLLOWER = BASE_URL + "getMemberFollowers";

    /**
     * update Profile
     */
    public static final String SAVE_PROFILE = BASE_URL + "saveProfile";

    /**
     * liked Posts
     */
    public static final String LIKED_POST = BASE_URL + "likedPosts";

    /**
     * Logout
     */
    static final String LOGOUT = BASE_URL + "logout";

    /**
     * discover people
     */
    public static final String DISCOVER_PEOPLE = BASE_URL + "discover-people-website";

    /**
     * Get All Likes
     */
    public static final String GET_ALL_LIKES = BASE_URL + "getAllLikes";

    /**
     * Get Categories
     */
    public static final String GET_CATEGORIES = BASE_URL + "getCategories";

    /**
     * Get Post Comments
     */
    public static final String GET_POST_COMMENTS = BASE_URL + "getPostComments";

    /**
     * add comments
     */
    public static final String ADD_COMMENTS = BASE_URL + "comments";

    /**
     * DeleteComments From Post
     */
    static final String DELETE_COMMENTS = BASE_URL + "deleteCommentsFromPost";

    /**
     * post Report Reason
     */
    public static final String REPORT_POST_REASON = BASE_URL + "postReportReason";

    /**
     * Report Reason
     */
    public static final String REPORT_USER_REASON = BASE_URL + "reportReason";

    /**
     * report Post
     */
    public static final String REPORT_POST = BASE_URL + "reportPost";

    /**
     * report User
     */
    public static final String REPORT_USER = BASE_URL + "reportUser";

    /**
     * Post product
     */
    public static final String PRODUCT = BASE_URL + "product/v2";

    /**
     * search Users
     */
    public static final String SEARCH_USER = BASE_URL + "searchUsers";

    /**
     * search user from guest user
     */
    public static final String SEARCH_GUEST_USER = BASE_URL + "guests/search/member";

    /**
     * Search product
     */
    public static final String SEARCH_POST = BASE_URL + "search/";

    /**
     * Phone Contacts
     */
    public static final String PHONE_CONTACTS = BASE_URL + "phoneContacts";

    /**
     * Facebook Contact Sync
     */
    public static final String FACEBOOK_CONTACT_SYNC = BASE_URL + "facebookContactSync";

    /**
     * Accepted Offers
     */
    public static final String ACCEPTED_OFFER = BASE_URL + "acceptedOffers";

    /**
     * Accepted Offers
     */
    public static final String RATE_USER = BASE_URL + "rate/";

    /**
     * Accepted Offers
     */
    static final String MARK_SELLING = BASE_URL + "markSelling";


    /**
     * Sold some Where else
     */
    static final String ELSE_WHERE = BASE_URL + "sold/elseWhere";

    /**
     * mark Sold
     */
    public static final String MARK_SOLD = BASE_URL + "markSold";

    /**
     * Insights
     */
    public static final String INSIGHT = BASE_URL + "insights";

    /**
     * user campaign
     */
    static final String USER_CAMPAIGN = BASE_URL + "user/campaign";

    /**
     * unseen Notification Count
     */
    public static final String UNSEEN_NOTIFICATION_COUNT = BASE_URL + "unseenNotificationCount";

    /**
     * promotionPlans
     */
    public static final String PROMOTE_PLANS = BASE_URL + "promotionPlans";
    public static final String DELETE_POST = BASE_URL + "product";
    public static final String PURCHASED_PLAN = BASE_URL + "promotePosts";

    /**
     * getPostsById/users
     */
    public static final String MAKE_OFFER = BASE_URL + "makeOffer";

    /**
     * getPostsById/users
     */
    public static final String GET_POST_BY_ID = BASE_URL + "getPostsById/users";
}
