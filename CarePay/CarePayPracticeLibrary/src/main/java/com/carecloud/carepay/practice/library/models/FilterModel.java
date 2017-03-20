package com.carecloud.carepay.practice.library.models;

import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by cocampo on 2/13/17.
 */

public class FilterModel {

    private ArrayList<FilterDataDTO> doctors = new ArrayList<>();
    private ArrayList<FilterDataDTO> locations = new ArrayList<>();
    private ArrayList<FilterDataDTO> patients = new ArrayList<>();

    private boolean filteringByPending;

    /**
     * @param newList new list of doctors
     */
    public void setDoctors(ArrayList<FilterDataDTO> newList) {
        sortListByName(newList);
        checkPreviouslyChecked(this.doctors, newList);
        this.doctors = newList;
    }

    /**
     * @param newList new list of locations
     */
    public void setLocations(ArrayList<FilterDataDTO> newList) {
        sortListByName(newList);
        checkPreviouslyChecked(this.locations, newList);
        this.locations = newList;
    }

    /**
     * @param newList new list of patients
     */
    public void setPatients(ArrayList<FilterDataDTO> newList) {
        sortListByName(newList);
        checkPreviouslyChecked(this.patients, newList);
        this.patients = newList;
    }

    private void sortListByName(ArrayList<FilterDataDTO> filterableList) {
        Collections.sort(filterableList, new Comparator<FilterDataDTO>() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(FilterDataDTO lhs, FilterDataDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getDisplayText().compareTo(rhs.getDisplayText());
                }
                return -1;
            }
        });
    }

    private void checkPreviouslyChecked(ArrayList<FilterDataDTO> oldList, ArrayList<FilterDataDTO> newList) {
        int oldIndex = 0;
        int newIndex = 0;

        while (oldIndex < oldList.size() && newIndex < newList.size()) {
            FilterDataDTO oldDTO = oldList.get(oldIndex);
            FilterDataDTO newDTO = newList.get(newIndex);

            int idComparison = oldDTO.getId().compareTo(newDTO.getId());
            int displayTextComparison = oldDTO.getDisplayText().compareTo(newDTO.getDisplayText());

            if (0 == idComparison) {
                newDTO.setChecked(oldDTO.isChecked());
                oldIndex++;
                newIndex++;
            } else if (displayTextComparison < 0) {
                oldIndex++;
            } else if (displayTextComparison > 0) {
                newIndex++;
            } else if (idComparison < 0) {
                oldIndex++;
            } else {
                newIndex++;
            }
        }
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

    public boolean isFilteringByPending() {
        return filteringByPending;
    }

    public void setFilteringByPending(boolean newValue) {
        this.filteringByPending = newValue;
    }

    /**
     * @return checked patients
     */
    public ArrayList<FilterDataDTO> getCheckedPatients() {
        ArrayList<FilterDataDTO> checked = new ArrayList<>();

        for (FilterDataDTO dto: patients) {
            if (dto.isChecked()) {
                checked.add(dto);
            }
        }

        return checked;
    }
}
