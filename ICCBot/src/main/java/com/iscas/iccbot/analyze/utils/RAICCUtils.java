package com.iscas.iccbot.analyze.utils;

import soot.Unit;

import java.util.HashSet;
import java.util.Set;

public class RAICCUtils {
    private static class WrapperMethodsHolder {
        private static final Set<String> WRAPPER_METHODS = new HashSet<String>() {{
            add(RAICCConstants.ANDROID_APP_ACTIVITYOPTIONS_REQUESTUSAGETIMEREPORT);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETALARMCLOCK);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETANDALLOWWHILEIDLE);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETEXACTANDALLOWWHILEIDLE);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETEXACT);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETINEXACTREPEATING);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SET);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETREPEATING);
            add(RAICCConstants.ANDROID_APP_ALARMMANAGER_SETWINDOW);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$ACTION$BUILDER_1);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$ACTION$BUILDER_2);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$BUBBLEMETADATA$BUILDER_SETDELETEINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$BUBBLEMETADATA$BUILDER_SETINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$BUILDER_ADDACTION);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$BUILDER_SETDELETEINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$BUILDER_SETFULLSCREENINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$CAREXTENDER$BUILDER_SETREADPENDINGINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$CAREXTENDER$BUILDER_SETREPLYACTION);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$WEARABLEEXTENDER_SETDISPLAYINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_1);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_2);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETDELETEINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_ADDACTION);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETCONTENTINTENT1);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETCONTENTINTENT2);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETDELETEINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETFULLSCREENINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$CAREXTENDER$BUILDER_SETREADPENDINGINTENT);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$CAREXTENDER$BUILDER_SETREPLYACTION);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION$WEARABLEEXTENDER_SETDISPLAYINTENT);
            add(RAICCConstants.ANDROID_APP_SLICE_SLICE$BUILDER_ADDACTION);
            add(RAICCConstants.ANDROID_BLUETOOTH_LE_BLUETOOTHLESCANNER_STARTSCAN);
            add(RAICCConstants.ANDROID_LOCATION_LOCATIONMANAGER_ADDPROXIMITYALERT);
            add(RAICCConstants.ANDROID_LOCATION_LOCATIONMANAGER_REQUESTLOCATIONUPDATES_1);
            add(RAICCConstants.ANDROID_LOCATION_LOCATIONMANAGER_REQUESTLOCATIONUPDATES_2);
            add(RAICCConstants.ANDROID_LOCATION_LOCATIONMANAGER_REQUESTSINGLEUPDATE_1);
            add(RAICCConstants.ANDROID_LOCATION_LOCATIONMANAGER_REQUESTSINGLEUPDATE_2);
            add(RAICCConstants.ANDROID_MEDIA_AUDIOMANAGER_REGISTERMEDIABUTTONEVENTRECEIVER);
            add(RAICCConstants.ANDROID_MEDIA_SESSION_MEDIASESSION_SETMEDIABUTTONRECEIVER);
            add(RAICCConstants.ANDROID_MEDIA_SESSION_MEDIASESSION_SETSESSIONACTIVITY);
            add(RAICCConstants.ANDROID_NET_CONNECTIVITYMANAGER_REGISTERNETWORKCALLBACK);
            add(RAICCConstants.ANDROID_NET_CONNECTIVITYMANAGER_REQUESTNETWORK);
            add(RAICCConstants.ANDROID_NET_SIP_SIPMANAGER_OPEN);
            add(RAICCConstants.ANDROID_NET_VPNSERVICE_BUILDER_SETCONFIGUREINTENT);
            add(RAICCConstants.ANDROID_NFC_NFCADAPTER_ENABLEFOREGROUNDDISPATCH);
            add(RAICCConstants.ANDROID_PRINT_PRINTERINFO_BUILDER_SETINFOINTENT);
            add(RAICCConstants.ANDROID_SUPPORT_V4_APP_ACTIVITYOPTIONSCOMPAT_REQUESTUSAGETIMEREPORT);
            add(RAICCConstants.ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_1);
            add(RAICCConstants.ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$ACTION_1);
            add(RAICCConstants.ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$BUILDER_ADDACTION);
            add(RAICCConstants.ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$BUILDER_SETFULLSCREENINTENT);
            add(RAICCConstants.ANDROID_SUPPORT_V4_MEDIA_APP_NOTIFICATIONCOMPAT$MEDIASTYLE_SETCANCELBUTTONINTENT);
            add(RAICCConstants.ANDROID_SUPPORT_V4_MEDIA_SESSION_MEDIASESSIONCOMPAT_SETMEDIABUTTONRECEIVER);
            add(RAICCConstants.ANDROID_SUPPORT_V4_MEDIA_SESSION_MEDIASESSIONCOMPAT_SETSESSIONACTIVITY);
            add(RAICCConstants.ANDROID_TELEPHONY_EUICC_EUICCMANAGER_DOWNLOADSUBSCRIPTION);
            add(RAICCConstants.ANDROID_TELEPHONY_EUICC_EUICCMANAGER_STARTRESOLUTIONACTIVITY);
            add(RAICCConstants.ANDROID_TELEPHONY_EUICC_EUICCMANAGER_SWITCHTOSUBSCRIPTION);
            add(RAICCConstants.ANDROID_TELEPHONY_EUICC_EUICCMANAGER_UPDATESUBSCRIPTIONNICKNAME);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_CREATEAPPSPECIFICSMSTOKENWITHPACKAGEINFO);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_DOWNLOADMULTIMEDIAMESSAGE);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_INJECTSMSPDU);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_SENDDATAMESSAGE);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_SENDMULTIMEDIAMESSAGE);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_SENDTEXTMESSAGE_1);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_SENDTEXTMESSAGE_2);
            add(RAICCConstants.ANDROID_TELEPHONY_SMSMANAGER_SENDTEXTMESSAGEWITHOUTPERSISTING);
            add(RAICCConstants.ANDROID_WIDGET_REMOTEVIEWS_SETONCLICKPENDINGINTENT);
            add(RAICCConstants.ANDROID_WIDGET_REMOTEVIEWS_SETREMOTEADAPTER);
            add(RAICCConstants.ANDROID_WIDGET_REMOTEVIEWS_SETPENDINGINTENTTEMPLATE);
            add(RAICCConstants.ANDROIDX_BROWSER_BROWSERACTIONS_BROWSERACTIONITEM);
            add(RAICCConstants.ANDROIDX_BROWSER_BROWSERACTIONS_BROWSERACTIONSINTENT$BUILDER_SETONITEMSELECTEDACTION);
            add(RAICCConstants.ANDROIDX_CORE_APP_ALARMMANAGERCOMPAT_SETEXACT);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_1);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_2);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_3);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_4);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_1);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_2);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_3);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_4);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETDELETEINTENT);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETINTENT);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA_1);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA_2);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUILDER_ADDACTION);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$CAREXTENDER$UNREADCONVERSATION);
            add(RAICCConstants.ANDROID_APP_NOTIFICATION_SETLATESTEVENTINFO);
            add(RAICCConstants.COM_GOOGLE_ANDROID_GMS_LOCATION_ACTIVITYRECOGNITIONAPI_REQUESTACTIVITYUPDATES);
            add(RAICCConstants.COM_GOOGLE_ANDROID_GMS_LOCATION_FUSEDLOCATIONPROVIDERAPI_REQUESTLOCATIONUPDATES);
            add(RAICCConstants.COM_GOOGLE_ANDROID_GMS_LOCATION_FUSEDLOCATIONPROVIDERCLIENT_REQUESTLOCATIONUPDATES);
            add(RAICCConstants.COM_GOOGLE_ANDROID_GMS_LOCATION_GEOFENCINGAPI_ADDGEOFENCES);
            add(RAICCConstants.COM_GOOGLE_ANDROID_GMS_LOCATION_LOCATIONCLIENT_ADDGEOFENCES);
            add(RAICCConstants.COM_GOOGLE_ANDROID_VENDING_EXPANSION_DOWNLOADER_DOWNLOADERCLIENTMARSHALLER_STARTDOWNLOADSERVICEIFREQUIRED);
            add(RAICCConstants.COM_GOOGLE_VR_NDK_BASE_DAYDREAMAPI_LAUNCHINVR);
            add(RAICCConstants.COM_GOOGLE_VR_NDK_BASE_DAYDREAMAPI_LAUNCHINVRFORRESULT);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUILDER_SETCONTENTINTENT);
            add(RAICCConstants.ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUILDER_SETDELETEINTENT);
            add(RAICCConstants.ANDROID_CONTENT_PM_PACKAGEINSTALLER$SESSION_COMMIT);
            add(RAICCConstants.ANDROID_CONTENT_PM_PACKAGEINSTALLER_INSTALLEXISTINGPACKAGE);
            add(RAICCConstants.ANDROID_COMPANION_COMPANIONDEVICEMANAGER$CALLBACK_ONDEVICEFOUND);
            add(RAICCConstants.ANDROID_CONTENT_INTENTSENDER$ONFINISHED_ONSENDFINISHED);
            add(RAICCConstants.ANDROID_APP_FRAGMENTHOSTCALLBACK_ONSTARTINTENTSENDERFROMFRAGMENT);
            add(RAICCConstants.ANDROID_SERVICE_AUTOFILL_SAVECALLBACK_ONSUCCESS);
            add(RAICCConstants.ANDROIDX_CORE_CONTENT_PM_SHORTCUTMANAGERCOMPAT_REQUESTPINSHORTCUT);
            add(RAICCConstants.ANDROID_SERVICE_AUTOFILL_FILLRESPONSE$BUILDER_SETAUTHENTICATION_1);
            add(RAICCConstants.ANDROID_SERVICE_AUTOFILL_FILLRESPONSE$BUILDER_SETAUTHENTICATION_2);
            add(RAICCConstants.ANDROID_CONTENT_CONTEXT_STARTINTENTSENDER_1);
            add(RAICCConstants.ANDROID_CONTENT_CONTEXT_STARTINTENTSENDER_2);
            add(RAICCConstants.ANDROID_APP_ACTIVITY_STARTINTENTSENDERFORRESULT_1);
            add(RAICCConstants.ANDROID_APP_ACTIVITY_STARTINTENTSENDERFORRESULT_2);
            add(RAICCConstants.START_INTENTSENDER_FOR_RESULT_1);
            add(RAICCConstants.START_INTENTSENDER_FOR_RESULT_2);

            add(RAICCConstants.ANDROID_APP_ACTIVITY_STARTINTENTSENDERFROMCHILD_1);
            add(RAICCConstants.ANDROID_APP_ACTIVITY_STARTINTENTSENDERFROMCHILD_2);
            add(RAICCConstants.ANDROID_CONTENT_PM_PACKAGEINSTALLER_UNINSTALL_1);
            add(RAICCConstants.ANDROID_CONTENT_PM_PACKAGEINSTALLER_UNINSTALL_2);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_1);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_2);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_3);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_4);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_5);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_6);
            add(RAICCConstants.ANDROID_APP_PENDINGINTENT_SEND_7);
            add(RAICCConstants.ANDROID_CONTENT_INTENTSENDER_SENDINTENT_1);
            add(RAICCConstants.ANDROID_CONTENT_INTENTSENDER_SENDINTENT_2);

            add(RAICCConstants.ANDROID_queryBroadcastReceivers);
            add(RAICCConstants.ANDROID_queryIntentActivities);
            add(RAICCConstants.ANDROID_queryIntentServices);
        }};
    }
    public static boolean isIntentSenderCreation(Unit unit) {
        return unit.toString().contains(RAICCConstants.ANDROID_APP_PENDING_INTENT_GET_INTENTSENDER);
    }

    public static boolean isPendingIntentCreation(Unit unit) {
        String sig = unit.toString();
        if (sig.contains(RAICCConstants.ANDROID_APP_PENDING_INTENT_GET_ACTIVITY)
                || sig.contains(RAICCConstants.ANDROID_APP_PENDING_INTENT_GET_BROADCAST)
                || sig.contains(RAICCConstants.ANDROID_APP_PENDING_INTENT_GET_SERVICE)) {
            return true;
        }
        return false;
    }

    public static boolean isWrapperMethods(Unit u) {
        for (String s : WrapperMethodsHolder.WRAPPER_METHODS) {
            if (u.toString().contains(s)) {
                return true;
            }
        }
        return false;
    }
}
