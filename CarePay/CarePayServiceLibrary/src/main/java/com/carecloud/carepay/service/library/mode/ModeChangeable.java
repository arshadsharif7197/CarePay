package com.carecloud.carepay.service.library.mode;

/**
 * Interface use to retrieve the mode of the app
 */

public interface ModeChangeable {
    Mode getMode();

    void setMode(Mode mode);
}
