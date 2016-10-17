package com.carecloud.carepaylibray.customcomponents;

/**
 * Created by Jahirul Bhuiyan on 10/14/2016.
 */

public interface CustomAssetStyleable {
    interface CustomAssetFontAttribute{
        int GOTHAM_ROUNDED_BOLD=0;
        int GOTHAM_ROUNDED_BOOK=1;
        int GOTHAM_ROUNDED_MEDIUM=2;
        int PROXIMA_NOVA_EXTRA_BOLD=3;
        int PROXIMA_NOVA_LIGHT=4;
        int PROXIMA_NOVA_REGULAR=5;
        int PROXIMA_NOVA_SEMI_BOLD=6;
    }

    interface CustomAssetFont{
        String FONT_GOTHAM_ROUNDED_BOLD="fonts/proximanova_regular.otf";
        String FONT_GOTHAM_ROUNDED_BOOK="fonts/gotham_rounded_book.otf";
        String FONT_GOTHAM_ROUNDED_MEDIUM="fonts/gotham_rounded_medium.otf";
        String FONT_PROXIMA_NOVA_EXTRA_BOLD="fonts/ProximaNova-Extrabld.otf";
        String FONT_PROXIMA_NOVA_LIGHT="fonts/proximanova_light.otf";
        String FONT_PROXIMA_NOVA_REGULAR="fonts/proximanova_regular.otf";
        String FONT_PROXIMA_NOVA_SEMI_BOLD="fonts/proximanova_semibold.otf";
    }
}
