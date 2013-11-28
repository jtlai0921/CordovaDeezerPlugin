package com.deezer.android.cordovadeezerplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;


/**
 * 
 * @author Xavier
 * 
 */
public class DeezerPlugin extends CordovaPlugin {
    
    private final static String METHOD_TAG_PLAYER_CMD = "doAction";
    private final static String METHOD_TAG_PLAYER_CONTROL = "playerControl";
    
    
    private final static String METHOD_NAME_PLAYTRACKS = "playTracks";
    private final static String METHOD_NAME_PLAYALBUM = "playAlbum";
    private final static String METHOD_NAME_PLAYPLAYLIST = "playPlaylist";
    private final static String METHOD_NAME_PLAYRADIO = "playRadio";
    private final static String METHOD_NAME_PLAYSMARTRADIO = "playSmartRadio";
    
    private CordovaInterface mInterface;
    private CordovaWebView mWebView;
    
    private DeezerJSListener mListener;
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        
        Log.i("DeezerPlugin", "initialize");
        mInterface = cordova;
        mWebView = webView;
    }
    
    @Override
    public boolean execute(String action, JSONArray args,
            CallbackContext callbackContext)
            throws JSONException {
        
        Log.i("DeezerPlugin", "execute : " + action);
        
        if (action == null) {
            callbackContext.error("action is null");
            return false;
        }
        
        Toast.makeText(mInterface.getActivity(), action, Toast.LENGTH_LONG)
                .show();
        
        if (action.equals(METHOD_TAG_PLAYER_CMD)) {
            
            JSONObject json = args.getJSONObject(0);
            String command = json.optString("command");
            if (command.equals("play")) {
                mListener.onPlay();
            } else if (command.equals("pause")) {
                mListener.onPause();
            } else if (command.equals("next")) {
                mListener.onNext();
            } else if (command.equals("prev")) {
                mListener.onPrev();
            }
            
            callbackContext.success();
            
        } else if (action.equalsIgnoreCase(METHOD_TAG_PLAYER_CONTROL)) {
            
            JSONObject json = args.getJSONObject(0);
            String method = args.getString(1);
            
            final int offset = json.optInt("offset", 0);
            final int index = json.optInt("index", 0);
            final boolean autoPlay = json.optBoolean("autoplay", true);
            final boolean addToQueue = json.optBoolean("queue", false);
            
            if (method.equals(METHOD_NAME_PLAYTRACKS)) {
                String ids = json.optString("trackList", null);
                if (ids != null && mListener != null) {
                    mListener.onPlayTracks(ids, index, offset, autoPlay, addToQueue);
                }
            } else if (method.equals(METHOD_NAME_PLAYALBUM)) {
                String id = json.optString("album_id", null);
                if (id != null) {
                    mListener.onPlayAlbum(id, index, offset, autoPlay, addToQueue);
                }
            } else if (method.equals(METHOD_NAME_PLAYPLAYLIST)) {
                String id = json.optString("playlist_id", null);
                if (id != null) {
                    mListener.onPlayPlaylist(id, index, offset, autoPlay, addToQueue);
                }
            } else if (method.equals(METHOD_NAME_PLAYRADIO)) {
                String id = json.optString("radio_id", null);
                if (id != null) {
                    mListener.onPlayRadio(id, index, offset, autoPlay, addToQueue);
                }
            } else if (method.equals(METHOD_NAME_PLAYSMARTRADIO)) {
                String id = json.optString("radio_id", null);
                if (id != null) {
                    mListener.onPlayArtistRadio(id, index, offset, autoPlay, addToQueue);
                }
            }
            
            callbackContext.success();
        }
        
        return true;
    }
}
