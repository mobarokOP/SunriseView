package com.downloader;

/**
 * Created by mobarok on 04/18/25.
 */

public interface OnDownloadListener {

    void onDownloadComplete();

    void onError(Error error);

}
