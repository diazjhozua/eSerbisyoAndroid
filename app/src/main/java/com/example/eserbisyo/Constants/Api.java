package com.example.eserbisyo.Constants;

public class Api {
//    public static final String URL = "https://barangay-cupang.herokuapp.com/";
    public static final String URL = "http://192.168.1.9:8001/";
    public static final String HOME = URL+"api";
    public static final String LOGIN = HOME+"/login";
    public static final String REGISTER = HOME+"/register";
    public static final String CHANGE_EMAIL = HOME+"/changeEmail";
    public static final String CHANGE_PASSWORD = HOME+"/changePassword";
    public static final String MY_PROFILE = HOME+"/myProfile";
    public static final String LOGOUT = HOME+"/logout";
    public static final String SAVE_USER_INFO = HOME+"/updateProfile";
    public static final String MY_VERIFICATION_REQUEST = HOME+"/myVerificationRequest";
    public static final String SUBMIT_VERIFICATION_REQUEST = HOME+"/submitVerificationRequest";

    public static final String NOTIFICATION_COUNT = HOME+"/getNotificationsCount";
    public static final String NOTIFICATION_LIST = HOME+"/myNotifications";
    public static final String SUBSCRIBE = HOME+"/subscribe";
    public static final String SEEN_NOTIFICATION = HOME+"/seenNotification";

    public static final String INQUIRIES = HOME+"/inquiries";

    public static final String FEEDBACKS = HOME+"/feedbacks";
    public static final String FEEDBACKS_ANALYTICS = HOME+"/feedbacks/getAnalytics";
    public static final String FEEDBACKS_CREATE = HOME+"/feedbacks/create";

    public static final String REPORTS = HOME+"/reports";
    public static final String REPORTS_ANALYTICS = HOME+"/reports/getAnalytics";
    public static final String REPORTS_CREATE = HOME+"/reports/create";

    public static final String USER_REQUIREMENTS = HOME+"/userRequirements";
    public static final String USER_REQUIREMENTS_CREATE = USER_REQUIREMENTS+"/create";

    public static final String ANNOUNCEMENTS = HOME+"/announcements";
    public static final String ANNOUNCEMENTS_LIKE = ANNOUNCEMENTS+"/like";
    public static final String ANNOUNCEMENTS_COMMENTS = ANNOUNCEMENTS+"/comment";

    public static final String COMMENTS = HOME+"/comments";

    public static final String EDIT = "/edit" ;
    public static final String CREATE = "/create";

    public static final String ORDINANCES = HOME+"/ordinances";
    public static final String DOCUMENTS = HOME+"/documents";
    public static final String PROJECTS = HOME+"/projects";
    public static final String EMPLOYEES = HOME+"/employees";

    public static final String MISSING_PERSONS = HOME+"/missingPersons";
    public static final String MISSING_PERSONS_AUTH = MISSING_PERSONS+"/authReports";
    public static final String MISSING_PERSONS_COMMENTS = MISSING_PERSONS+"/comment";
    public static final String MISSING_ITEMS = HOME+"/missingItems";

    public static final String MISSING_ITEMS_AUTH = MISSING_ITEMS+"/authReports";
    public static final String MISSING_ITEMS_COMMENTS = MISSING_ITEMS+"/comment";

    public static final String COMPLAINTS = HOME+"/complaints";
    public static final String COMPLAINTS_ANALYTICS = HOME+"/complaints/getAnalytics";
    public static final String DEFENDANTS = HOME+"/defendants";
    public static final String COMPLAINANTS = HOME+"/complainants";

    public static final String ORDERS = HOME+"/orders";
    public static final String ORDER_CHECK_REQUIREMENTS = HOME+"/orders/checkRequirements";
    public static final String ORDER_SUBMIT_REPORT = HOME+"/orders/submitReport";
    public static final String ORDER_CERTIFICATE = HOME+"/orders/certificates";

    //bikers api link
    public static final String BIKERS_LATEST_VERIFICATION = HOME+"/bikers/latestVerification";
    public static final String BIKERS_POST_VERIFICATION = HOME+"/bikers/postVerification";
    public static final String BIKERS_AUTH_ANALYTICS = HOME+"/bikers/getAuthAnalytics";
    public static final String BIKERS_AUTH_TRANSACTION = HOME+"/bikers/getAuthTransaction";
    public static final String BIKERS_GET_AVAILABLE_ORDER = HOME+"/bikers/getListOrders";
    public static final String BIKER_GET_ORDER_DETAILS = HOME+"/bikers/getOrderDetails";
    public static final String BIKER_BOOKED_ORDER = HOME+"/bikers/bookedOrder";
    public static final String BIKER_START_RIDING = HOME+"/bikers/startRiding";
    public static final String BIKER_RECEIVE_ORDER = HOME+"/bikers/confirmReceiveOrder";
    public static final String BIKER_DNR_ORDER = HOME+"/bikers/confirmDNROrder";


    // end
    public static final String VIEW_FILE = URL+"view/" ;
    public static final String DOWNLOAD_FILE = URL+"download/" ;
    public static final String STORAGE = URL+"storage/" ;

}