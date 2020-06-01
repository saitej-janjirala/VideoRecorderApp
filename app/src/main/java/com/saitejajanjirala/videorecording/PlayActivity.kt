package com.saitejajanjirala.videorecording

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.custom_controller.*

class PlayActivity : AppCompatActivity() {
    lateinit var simpleExoPlayer: SimpleExoPlayer
    var flag:Boolean=false
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        val url=intent.getStringExtra("videourl")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val uri=Uri.parse(url)
        val loadcontrol:LoadControl=DefaultLoadControl()
        val bandwidthMeter=DefaultBandwidthMeter()
        val trackSelector=DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        simpleExoPlayer=ExoPlayerFactory.newSimpleInstance(
            this@PlayActivity,trackSelector,loadcontrol)
        val factory=DefaultHttpDataSourceFactory("exoplayer_video")
        val extractorFactory=DefaultExtractorsFactory()
        val mediaSource=ExtractorMediaSource(uri,factory,extractorFactory,null,null)
        playerview.player=simpleExoPlayer
        playerview.keepScreenOn=true
        simpleExoPlayer.prepare(mediaSource)
        simpleExoPlayer.addListener(object: Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }
            override fun onSeekProcessed() {
            }
            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {

            }

            override fun onPlayerError(error: ExoPlaybackException?) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState==Player.STATE_BUFFERING){
                    progress_bar.visibility= View.VISIBLE
                }
                else if(playbackState==Player.STATE_READY){
                    progress_bar.visibility=View.GONE
                }
            }

        })
        bt_fullscreen.setOnClickListener {
            if(flag){
                bt_fullscreen.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen))
                requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                flag=false
            }
            else{
                bt_fullscreen.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen_exit))
                requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                flag=true

            }
        }

    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.playWhenReady=false
        simpleExoPlayer.playbackState
    }

    override fun onRestart() {
        super.onRestart()
        simpleExoPlayer.playWhenReady=true
        simpleExoPlayer.playbackState
    }
}
