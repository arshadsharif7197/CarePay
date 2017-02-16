package com.carecloud.carepay.practice.library.models;

import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cocampo on 2/13/17.
 */

public class MapFilterModel {

    private Map<String, FilterDataDTO> doctors;
    private Map<String, FilterDataDTO> locations;
    private Map<String, FilterDataDTO> patients;

    private boolean filterByDoctors;
    private boolean filterByLocations;
    private boolean filterByPatients;

    /**
     * @param filterModel to transform to map
     */
    public MapFilterModel(FilterModel filterModel) {

        doctors = map(filterModel.getDoctors());
        locations = map(filterModel.getLocations());
        patients = map(filterModel.getPatients());

        filterByDoctors = !doctors.isEmpty();
        filterByLocations = !locations.isEmpty();
        filterByPatients = !patients.isEmpty();
    }

    /**
     * Default constructor
     */
    public MapFilterModel() {
        doctors = new HashMap<>();
        locations = new HashMap<>();
        patients = new HashMap<>();
    }

    private Map<String, FilterDataDTO> map(ArrayList<FilterDataDTO> list) {
        Map<String, FilterDataDTO> map = new HashMap<>();

        for(FilterDataDTO dto: list) {
            if (dto.isChecked()) {
                map.put(dto.getId(), dto);
            }
        }

        return map;
    }

    public Map<String, FilterDataDTO> getDoctors() {
        return doctors;
    }

    public Map<String, FilterDataDTO> getLocations() {
        return locations;
    }

    public Map<String, FilterDataDTO> getPatients() {
        return patients;
    }

    public boolean hasFilterByDoctors() {
        return filterByDoctors;
    }

    public boolean hasFilterByLocations() {
        return filterByLocations;
    }

    public boolean hasFilterByPatients() {
        return filterByPatients;
    }
}
