package com.carecloud.carepaylibray.base.dtos;

import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileLink;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2/20/19.
 */
public class DelegatePermissionBasePayloadDto {

    @SerializedName("user_links")
    @Expose
    private UserLinks userLinks = new UserLinks();
    @SerializedName("delegate")
    @Expose
    private Profile delegate;

    public UserLinks getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(UserLinks userLinks) {
        this.userLinks = userLinks;
    }

    public Profile getDelegate() {
        return delegate;
    }

    public void setDelegate(Profile delegate) {
        this.delegate = delegate;
    }

    public boolean canSeeStatement(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewPatientStatements().isEnabled();
    }

    public boolean canViewSurveyNotifications(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }

        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }

        return profileLink.getPermissionDto().getPermissions().getViewAndSubmitSurveys().isEnabled();
    }

    public boolean canViewNotifications(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }

        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewNotifications().isEnabled();
    }

    public boolean canScheduleAppointments(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }

        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getScheduleAppointments().isEnabled()
                && canViewAppointments(practiceId);
    }

    public boolean canViewAppointments(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewAppointments().isEnabled();
    }

    public boolean canCheckInCheckOut(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getCheckInAndCheckOut().isEnabled();
    }

    public boolean canViewAndCreateVisitSummaries(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewAndCreateVisitSummaries().isEnabled();
    }

    public boolean canViewForms(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewForms().isEnabled();
    }

    public boolean canReviewForms(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getReviewForms().isEnabled();
    }

    public boolean canViewCareTeam(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewCareTeam().isEnabled();
    }

    public boolean canViewConditions(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewConditions().isEnabled();
    }

    public boolean canViewAllergies(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewAllergies().isEnabled();
    }

    public boolean canViewMedications(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewMedications().isEnabled();
    }

    public boolean canViewLabs(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewLabResults().isEnabled();
    }

    public boolean canMessageProviders(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getMessageProviders().isEnabled();
    }

    public boolean canViewBalanceAndHistoricalPayments(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewBalanceAndHistoricalPayments().isEnabled();
    }

    public boolean canViewBalanceDetails(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getViewBalanceDetails().isEnabled();
    }

    public boolean havePermissionsToMakePayments(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }
        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }
        return profileLink.getPermissionDto().getPermissions().getMakePayments().isEnabled();
    }
}
