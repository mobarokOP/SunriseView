package com.downloader.core;

import java.util.concurrent.Executor;

/**
 * Created by mobarok on 04/18/25.
 */

public interface ExecutorSupplier {

    DownloadExecutor forDownloadTasks();

    Executor forBackgroundTasks();

    Executor forMainThreadTasks();

}
