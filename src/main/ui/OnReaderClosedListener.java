package main.ui;

/**
 * Callback to be invoked when a {@link CLIReader} closes.
 */
public interface OnReaderClosedListener {
    /**
     * Called when a {@link CLIReader} closes.
     */
    void onReaderClosed();
}
