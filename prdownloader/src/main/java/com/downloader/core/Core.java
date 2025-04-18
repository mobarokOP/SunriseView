package com.downloader.core;

/**
 * Created by mobarok on 04/18/25.
 */

public class Core {

    private static Core instance = null;
    private final ExecutorSupplier executorSupplier;

    private Core() {
        this.executorSupplier = new DefaultExecutorSupplier();
    }

    public static Core getInstance() {
        if (instance == null) {
            synchronized (Core.class) {
                if (instance == null) {
                    instance = new Core();
                }
            }
        }
        return instance;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return executorSupplier;
    }

    public static void shutDown() {
        if (instance != null) {
            instance = null;
        }
    }
}
