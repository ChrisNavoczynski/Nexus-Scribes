package com.example.nexus_scribes.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_scribes.R;
import com.example.nexus_scribes.models.YouTubeVideos;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    List<YouTubeVideos> youTubeVideoList;

    public VideoAdapter() {
    }

    public VideoAdapter(List<YouTubeVideos> youTubeVideoList) {
        this.youTubeVideoList = youTubeVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_view, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.videoWeb.loadData(youTubeVideoList.get(position)
                .getVideoUrl(), "text/html", "utf-8");
    }

    @Override
    public int getItemCount() {
        return youTubeVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView videoWeb;

        @SuppressLint("SetJavaScriptEnabled")
        public VideoViewHolder(View itemView) {
            super(itemView);

            videoWeb = (WebView) itemView.findViewById(R.id.videoWebView);
            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.setWebChromeClient(new WebChromeClient() {

            });
        }
    }
}
