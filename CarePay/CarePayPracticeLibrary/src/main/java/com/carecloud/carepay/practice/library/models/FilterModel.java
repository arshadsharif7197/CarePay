package com.carecloud.carepay.practice.library.models;

import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cocampo on 2/13/17.
 */

public class FilterModel {

    private ArrayList<FilterDataDTO> doctors;
    private ArrayList<FilterDataDTO> locations;
    private ArrayList<FilterDataDTO> patients;

    private String practiceCheckinFilterDoctorsLabel;
    private String practiceCheckinFilterLocationsLabel;
    private String practicePaymentsFilter;
    private String practicePaymentsFilterFindPatientByName;
    private String practicePaymentsFilterClearFilters;

    private boolean filteringByPending;

    /**
     * @param doctors to filter from
     * @param locations to filter from
     * @param patients to filter from
     * @param practiceCheckinFilterDoctorsLabel on top of doctors list
     * @param practiceCheckinFilterLocationsLabel on top on locations list
     * @param practicePaymentsFilter label on top of filter dialog
     * @param practicePaymentsFilterFindPatientByName label for patient search text view
     * @param practicePaymentsFilterClearFilters label for clear filters button
     */
    public FilterModel(ArrayList<FilterDataDTO> doctors,
                       ArrayList<FilterDataDTO> locations,
                       ArrayList<FilterDataDTO> patients,
                       String practiceCheckinFilterDoctorsLabel,
                       String practiceCheckinFilterLocationsLabel,
                       String practicePaymentsFilter,
                       String practicePaymentsFilterFindPatientByName,
                       String practicePaymentsFilterClearFilters) {

        this.doctors = doctors;
        this.locations = locations;
        this.patients = patients;
        this.practiceCheckinFilterDoctorsLabel = practiceCheckinFilterDoctorsLabel;
        this.practiceCheckinFilterLocationsLabel = practiceCheckinFilterLocationsLabel;
        this.practicePaymentsFilter = practicePaymentsFilter;
        this.practicePaymentsFilterFindPatientByName = practicePaymentsFilterFindPatientByName;
        this.practicePaymentsFilterClearFilters = practicePaymentsFilterClearFilters;
    }

    /**
     * Clears filters
     */
    public void clear() {
        clear(doctors);
        clear(locations);
        clear(patients);
    }

    private void clear(ArrayList<FilterDataDTO> list) {
        for (FilterDataDTO dto : list) {
            dto.setChecked(false);
        }
    }

    public ArrayList<FilterDataDTO> getDoctors() {
        return doctors;
    }

    public ArrayList<FilterDataDTO> getLocations() {
        return locations;
    }

    public ArrayList<FilterDataDTO> getPatients() {
        return patients;
    }

    public String getPracticePaymentsFilter() {
        return practicePaymentsFilter;
    }

    public String getPracticePaymentsFilterFindPatientByName() {
        return practicePaymentsFilterFindPatientByName;
    }

    public String getPracticePaymentsFilterClearFilters() {
        return practicePaymentsFilterClearFilters;
    }

    /**
     * @return list of doctor plus locations
     */
    public List<FilterDataDTO> getDoctorsPlusLocations() {
        List<FilterDataDTO> list = new ArrayList<>();
        list.add(new FilterDataDTO(practiceCheckinFilterDoctorsLabel));
        list.addAll(doctors);
        list.add(new FilterDataDTO(practiceCheckinFilterLocationsLabel));
        list.addAll(locations);

        return list;
    }

    public void clearFilterByPatients() {
        clear(patients);
    }

    public boolean isFilteringByPending() {
        return filteringByPending;
    }

    public void setFilteringByPending(boolean newValue) {
        this.filteringByPending = newValue;
    }
}
