package com.carecloud.carepaylibray.translation;

import android.os.Bundle;

/**
 * @author pjohnson on 19/12/17.
 */
public interface TranslatableFragment {
    Bundle saveInstanceForTranslation(Bundle bundle);

    void restoreInstanceForTranslation(Bundle bundle);
}
