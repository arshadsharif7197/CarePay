package com.carecloud.carepaylibray.practice;

/**
 * Created by jorge on 16/01/17.
 * Class holding info about the curent point in the checkin flow
 */
public class FlowStateInfo {

    int subflow;
    public int fragmentIndex;
    int maxFragIndex;

    /**
     * Ctor
     *
     * @param subflow       The subflow
     * @param fragmentIndex The index of the current fragment (if necessary)
     * @param maxFragIndex  The meximum number of the fragments
     */
    public FlowStateInfo(int subflow, int fragmentIndex, int maxFragIndex) {
        this.subflow = subflow;
        this.fragmentIndex = fragmentIndex;
        this.maxFragIndex = maxFragIndex;
    }

    public int getSubflow() {
        return subflow;
    }

    public void setSubflow(int subflow) {
        this.subflow = subflow;
    }

    public int getFragmentIndex() {
        return fragmentIndex;
    }

    public void setFragmentIndex(int fragmentIndex) {
        this.fragmentIndex = fragmentIndex;
    }

    public int getMaxFragIndex() {
        return maxFragIndex;
    }

    public void setMaxFragIndex(int maxFragIndex) {
        this.maxFragIndex = maxFragIndex;
    }
}