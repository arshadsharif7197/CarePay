
package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class PayloadIntakeFormModel {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("template_uuid")
    @Expose
    private String templateUuid;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("providers")
    @Expose
    private List<Integer> providers = new ArrayList<>();
    @SerializedName("providier_locations")
    @Expose
    private List<String> providierLocations = new ArrayList<>();
    @SerializedName("show")
    @Expose
    private Boolean show;
    @SerializedName("sections")
    @Expose
    private List<String> sections = new ArrayList<>();
    @SerializedName("xml")
    @Expose
    private String xml;

    /**
     * 
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 
     * @return
     *     The templateUuid
     */
    public String getTemplateUuid() {
        return templateUuid;
    }

    /**
     * 
     * @param templateUuid
     *     The template_uuid
     */
    public void setTemplateUuid(String templateUuid) {
        this.templateUuid = templateUuid;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The providers
     */
    public List<Integer> getProviders() {
        return providers;
    }

    /**
     * 
     * @param providers
     *     The providers
     */
    public void setProviders(List<Integer> providers) {
        this.providers = providers;
    }

    /**
     * 
     * @return
     *     The providierLocations
     */
    public List<String> getProvidierLocations() {
        return providierLocations;
    }

    /**
     * 
     * @param providierLocations
     *     The providier_locations
     */
    public void setProvidierLocations(List<String> providierLocations) {
        this.providierLocations = providierLocations;
    }

    /**
     * 
     * @return
     *     The show
     */
    public Boolean getShow() {
        return show;
    }

    /**
     * 
     * @param show
     *     The show
     */
    public void setShow(Boolean show) {
        this.show = show;
    }

    /**
     * 
     * @return
     *     The sections
     */
    public List<String> getSections() {
        return sections;
    }

    /**
     * 
     * @param sections
     *     The sections
     */
    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    /**
     * 
     * @return
     *     The xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * 
     * @param xml
     *     The xml
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

}
