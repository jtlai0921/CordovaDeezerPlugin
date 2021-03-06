package com.deezer.android.cordovadeezerplugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.event.DialogError;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.OAuthException;
import com.deezer.sdk.player.AbstractTrackListPlayer;
import com.deezer.sdk.player.AlbumPlayer;
import com.deezer.sdk.player.ArtistRadioPlayer;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.PlaylistPlayer;
import com.deezer.sdk.player.RadioPlayer;
import com.deezer.sdk.player.event.OnBufferProgressListener;
import com.deezer.sdk.player.event.OnPlayerProgressListener;
import com.deezer.sdk.player.event.RadioPlayerListener;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;


/**
 * 
 * @author Xavier
 * 
 */
public class DeezerSDKController implements DeezerJSListener {
    
    private final static String LOG_TAG = "DeezerSDKController";
    
    /** Permissions requested on Deezer accounts. */
    private final static String[] PERMISSIONS = new String[] {
            "basic_access",
    };
    
    private Activity mActivity;
    private DeezerConnect mConnect;
    
    private PlayerWrapper mPlayerWrapper;
    private DeezerPlugin mPlugin;
    
    /**
     * 
     * @param activity
     */
    public DeezerSDKController(final Activity activity, final DeezerPlugin plugin) {
        mActivity = activity;
        mPlugin = plugin;
    }
    
    // /////////////////////////////////////////////////////////////////////////////
    // DeezerJSListener Implementation
    // /////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void init(final CallbackContext callbackContext, final String appId) {
        mConnect = new DeezerConnect(mActivity, appId);
        callbackContext.success();
    }
    
    @Override
    public void login(final CallbackContext callbackContext) {
        final AuthListener listener = new AuthListener(callbackContext);
        
        mActivity.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                mConnect.authorize(mActivity, PERMISSIONS, listener);
            }
        });
    }
    
    @Override
    public void onPlayTracks(final CallbackContext callbackContext, final String ids,
            final int index, final int offset, final boolean autoPlay, final boolean addToQueue) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void onPlayAlbum(final CallbackContext callbackContext, final String id,
            final int index, final int offset, final boolean autoPlay, final boolean addToQueue) {
        
        // check if a previous player exists
        if (mPlayerWrapper != null) {
            mPlayerWrapper.stop();
            mPlayerWrapper.release();
            mPlayerWrapper = null;
        }
        
        try {
            // create the album player
            mPlayerWrapper = new AlbumPlayer(mActivity.getApplication(),
                    mConnect, new WifiAndMobileNetworkStateChecker());
            
            // add a listener
            ((AlbumPlayer) mPlayerWrapper)
                    .addPlayerListener(new PlayerListener(callbackContext));
            mPlayerWrapper
                    .addOnPlayerProgressListener(new PlayerProgressListener());
            mPlayerWrapper.addOnBufferProgressListener(new PlayerBufferProgressListener());
            
            // play the given album id
            long albumId = Long.valueOf(id);
            ((AlbumPlayer) mPlayerWrapper).playAlbum(albumId, index);
            
        }
        catch (OAuthException e) {
            Log.e(LOG_TAG, "OAuthException", e);
            callbackContext.error("OAuthException");
        }
        catch (TooManyPlayersExceptions e) {
            Log.e(LOG_TAG, "TooManyPlayersExceptions", e);
            callbackContext.error("TooManyPlayersExceptions");
        }
        catch (DeezerError e) {
            Log.e(LOG_TAG, "DeezerError", e);
            callbackContext.error("DeezerError");
        }
        catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException", e);
            callbackContext.error("NumberFormatException");
        }
        
    }
    
    @Override
    public void onPlayPlaylist(final CallbackContext callbackContext, final String id,
            final int index, final int offset, final boolean autoPlay, final boolean addToQueue) {
        // check if a previous player exists
        if (mPlayerWrapper != null) {
            mPlayerWrapper.stop();
            mPlayerWrapper.release();
            mPlayerWrapper = null;
        }
        
        try {
            // create the playlist player
            mPlayerWrapper = new PlaylistPlayer(mActivity.getApplication(),
                    mConnect, new WifiAndMobileNetworkStateChecker());
            
            // add a listener
            ((PlaylistPlayer) mPlayerWrapper)
                    .addPlayerListener(new PlayerListener(callbackContext));
            mPlayerWrapper
                    .addOnPlayerProgressListener(new PlayerProgressListener());
            mPlayerWrapper.addOnBufferProgressListener(new PlayerBufferProgressListener());
            
            // play the given playlist id
            long playlistId = Long.valueOf(id);
            ((PlaylistPlayer) mPlayerWrapper).playPlaylist(playlistId, index);
            
        }
        catch (OAuthException e) {
            Log.e(LOG_TAG, "OAuthException", e);
            callbackContext.error("OAuthException");
        }
        catch (TooManyPlayersExceptions e) {
            Log.e(LOG_TAG, "TooManyPlayersExceptions", e);
            callbackContext.error("TooManyPlayersExceptions");
        }
        catch (DeezerError e) {
            Log.e(LOG_TAG, "DeezerError", e);
            callbackContext.error("DeezerError");
        }
        catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException", e);
            callbackContext.error("NumberFormatException");
        }
    }
    
    @Override
    public void onPlayRadio(final CallbackContext callbackContext, final String id,
            final int index, final int offset, final boolean autoPlay, final boolean addToQueue) {
        // check if a previous player exists
        if (mPlayerWrapper != null) {
            mPlayerWrapper.stop();
            mPlayerWrapper.release();
            mPlayerWrapper = null;
        }
        
        try {
            // create the radio player
            mPlayerWrapper = new RadioPlayer(mActivity.getApplication(),
                    mConnect, new WifiAndMobileNetworkStateChecker());
            
            // add a listener
            ((RadioPlayer) mPlayerWrapper)
                    .addPlayerListener(new PlayerListener(callbackContext));
            mPlayerWrapper
                    .addOnPlayerProgressListener(new PlayerProgressListener());
            mPlayerWrapper.addOnBufferProgressListener(new PlayerBufferProgressListener());
            
            // play the given radio id
            long radioId = Long.valueOf(id);
            ((RadioPlayer) mPlayerWrapper).playRadio(radioId);
            
        }
        catch (OAuthException e) {
            Log.e(LOG_TAG, "OAuthException", e);
            callbackContext.error("OAuthException");
        }
        catch (TooManyPlayersExceptions e) {
            Log.e(LOG_TAG, "TooManyPlayersExceptions", e);
            callbackContext.error("TooManyPlayersExceptions");
        }
        catch (DeezerError e) {
            Log.e(LOG_TAG, "DeezerError", e);
            callbackContext.error("DeezerError");
        }
        catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException", e);
            callbackContext.error("NumberFormatException");
        }
    }
    
    @Override
    public void onPlayArtistRadio(final CallbackContext callbackContext, final String id,
            final int index, final int offset, final boolean autoPlay, final boolean addToQueue) {
        // check if a previous player exists
        if (mPlayerWrapper != null) {
            mPlayerWrapper.stop();
            mPlayerWrapper.release();
            mPlayerWrapper = null;
        }
        
        try {
            // create the radio player
            mPlayerWrapper = new ArtistRadioPlayer(mActivity.getApplication(),
                    mConnect, new WifiAndMobileNetworkStateChecker());
            
            // add a listener
            ((ArtistRadioPlayer) mPlayerWrapper)
                    .addPlayerListener(new PlayerListener(callbackContext));
            mPlayerWrapper
                    .addOnPlayerProgressListener(new PlayerProgressListener());
            mPlayerWrapper.addOnBufferProgressListener(new PlayerBufferProgressListener());
            
            // play the given radio id
            long radioId = Long.valueOf(id);
            ((ArtistRadioPlayer) mPlayerWrapper).playArtistRadio(radioId);
            
        }
        catch (OAuthException e) {
            Log.e(LOG_TAG, "OAuthException", e);
            callbackContext.error("OAuthException");
        }
        catch (TooManyPlayersExceptions e) {
            Log.e(LOG_TAG, "TooManyPlayersExceptions", e);
            callbackContext.error("TooManyPlayersExceptions");
        }
        catch (DeezerError e) {
            Log.e(LOG_TAG, "DeezerError", e);
            callbackContext.error("DeezerError");
        }
        catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException", e);
            callbackContext.error("NumberFormatException");
        }
    }
    
    @Override
    public void onPlay(final CallbackContext callbackContext) {
        Log.i(LOG_TAG, "onPlay");
        
        if (mPlayerWrapper != null) {
            mPlayerWrapper.play();
            callbackContext.success();
        } else {
            callbackContext.error("No player to play");
        }
        
    }
    
    @Override
    public void onPause(final CallbackContext callbackContext) {
        Log.i(LOG_TAG, "onPause");
        
        if (mPlayerWrapper != null) {
            mPlayerWrapper.pause();
            callbackContext.success();
        } else {
            callbackContext.error("No player to pause");
        }
        
    }
    
    @Override
    public void onNext(final CallbackContext callbackContext) {
        Log.i(LOG_TAG, "onNext");
        
        if (mPlayerWrapper != null) {
            if (mPlayerWrapper.skipToNextTrack()) {
                callbackContext.success();
            } else {
                callbackContext.error(0);
            }
        } else {
            callbackContext.error("No player to next");
        }
    }
    
    @Override
    public void onPrev(final CallbackContext callbackContext) {
        Log.i(LOG_TAG, "onPrev");
        
        if (mPlayerWrapper != null) {
            if (mPlayerWrapper.skipToPreviousTrack()) {
                callbackContext.success();
            } else {
                callbackContext.error(0);
            }
        } else {
            callbackContext.error("No player to previous");
        }
    }
    
    // /////////////////////////////////////////////////////////////////////////////
    // DeezerJSListener Implementation
    // /////////////////////////////////////////////////////////////////////////////
    
    private class AuthListener implements DialogListener {
        
        private CallbackContext mContext;
        
        public AuthListener(final CallbackContext context) {
            mContext = context;
        }
        
        @Override
        public void onComplete(final Bundle bundle) {
            Log.i(LOG_TAG, "Logged In!");
            
            JSONObject dict = new JSONObject();
            for (String key : bundle.keySet()) {
                Log.d(LOG_TAG, key + " -> " + bundle.getString(key));
                
                try {
                    dict.put(key, bundle.getString(key));
                }
                catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException", e);
                }
            }
            
            mContext.success(dict);
        }
        
        @Override
        public void onCancel() {
            Log.d(LOG_TAG, "onCancel");
            mContext.error("cancel");
        }
        
        @Override
        public void onDeezerError(final DeezerError e) {
            Log.e(LOG_TAG, "onDeezerError", e);
            mContext.error("DeezerError");
        }
        
        @Override
        public void onError(final DialogError e) {
            Log.e(LOG_TAG, "onError", e);
            mContext.error("Error");
        }
        
        @Override
        public void onOAuthException(final OAuthException e) {
            Log.e(LOG_TAG, "onOAuthException", e);
            mContext.error("OAuthException");
        }
    }
    
    private class PlayerListener implements RadioPlayerListener {
        
        private boolean mTrackListSent = false;
        private final CallbackContext mContext;
        
        
        public PlayerListener(final CallbackContext context) {
            mContext = context;
        }
        
        @Override
        public void onPlayTrack(final Track track) {
            Log.i(LOG_TAG, "onPlayTrack " + track.getTitle());
            
            if (!mTrackListSent) {
                if (mPlayerWrapper instanceof AbstractTrackListPlayer) {
                    
                    JSONObject callback = new JSONObject();
                    JSONArray data = new JSONArray();
                    
                    List<Track> tracks = ((AbstractTrackListPlayer) mPlayerWrapper)
                            .getTracks();
                    for (Track t : tracks) {
                        try {
                            data.put(t.toJson());
                        }
                        catch (JSONException e) {
                            // ignore
                        }
                    }
                    
                    try {
                        callback.put("data", data);
                        mContext.success(callback);
                    }
                    catch (JSONException e) {
                        mContext.error(0);
                    }
                }
            }
            
            mPlugin.sentToJS_onCurrentTrack(-1, track);
        }
        @Override
        public void onTrackEnded(final Track track) {
            Log.i(LOG_TAG, "onTrackEnded");
        }
        
        @Override
        public void onAllTracksEnded() {
            Log.i(LOG_TAG, "onAllTracksEnded");
        }
        
        @Override
        public void onRequestDeezerError(final DeezerError e, final Object request) {
            Log.e(LOG_TAG, "onRequestDeezerError", e);
        }
        
        @Override
        public void onRequestIOException(final IOException e, final Object request) {
            Log.e(LOG_TAG, "onRequestIOException", e);
        }
        
        @Override
        public void onRequestJSONException(final JSONException e, final Object request) {
            Log.e(LOG_TAG, "onRequestJSONException", e);
        }
        
        @Override
        public void onRequestMalformedURLException(final MalformedURLException e,
                final Object request) {
            Log.e(LOG_TAG, "onRequestMalformedURLException", e);
        }
        
        @Override
        public void onRequestOAuthException(final OAuthException e, final Object request) {
            Log.e(LOG_TAG, "onRequestMalformedURLException", e);
        }
        
        @Override
        public void onTooManySkipsException() {
            Log.e(LOG_TAG, "onTooManySkipsException");
        }
    }
    
    private class PlayerProgressListener implements OnPlayerProgressListener {
        
        @Override
        public void onPlayerProgress(final long progressMS) {
            Log.i(LOG_TAG, "onPlayerProgress progressMS: " + progressMS);
            float position = (float) progressMS / 1000;
            float duration = 0f;
            if (mPlayerWrapper != null) {
                duration = mPlayerWrapper.getTrackDuration() / 1000;
                Log.i(LOG_TAG, "onPlayerProgress duration : " + duration);
            }
            
            if (mPlugin != null) {
                mPlugin.sendToJs_positionChanged(position, duration);
            }
        }
    }
    
    private class PlayerBufferProgressListener implements OnBufferProgressListener {
        
        @Override
        public void onBufferProgress(double progressMS) {
            Log.i(LOG_TAG, "onBufferProgress progressMS: " + progressMS);
            float position = (float) progressMS / 1000;
            if (mPlugin != null) {
                mPlugin.sendToJS_bufferPosition(position);
            }
        }
    }
}
