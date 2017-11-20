package com.carecloud.carepaylibray.interfaces;

import android.os.Bundle;

/**
 * Created by lmenendez on 11/17/17
 */

public interface IcicleInterface {
    IcicleInterface pushData( Bundle icicle );

    Bundle popData();
}
