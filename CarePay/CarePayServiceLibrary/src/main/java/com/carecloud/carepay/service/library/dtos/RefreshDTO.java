package com.carecloud.carepay.service.library.dtos;

/**
 * Created by cocampo on 3/10/17.
 */

public class RefreshDTO {

    private User user;

    public RefreshDTO(String refreshToken) {
        user = new User();
        user.setRefresh(refreshToken);
    }

    public User getUser() {
        return user;
    }

    private class User {
        String refresh;

        public String getRefresh() {
            return refresh;
        }

        public void setRefresh(String refresh) {
            this.refresh = refresh;
        }
    }
}
