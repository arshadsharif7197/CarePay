package com.carecloud.shamrocksdk.utils;

import android.util.Log;

import java.net.URISyntaxException;
import java.util.Properties;

import io.deepstream.ConfigOptions;
import io.deepstream.ConnectionState;
import io.deepstream.DeepstreamClient;
import io.deepstream.InvalidDeepstreamConfig;
import io.deepstream.List;
import io.deepstream.ListChangedListener;
import io.deepstream.MergeStrategy;
import io.deepstream.Record;
import io.deepstream.RecordChangedCallback;
import io.deepstream.RecordEventsListener;

/**
 * Create and Manage Singleton instance of DeepStream Client that allows closing and restarting
 * DeepStream clients to help with possible connectivity issues. This class only supports
 * connecting to a single DeepStream Url at a time.
 */

public class DeepstreamInstance {
    private static DeepstreamClient currentInstance;
    private static String lastUrl;

    /**
     * Get singleton instance of DeepStream Client
     * @param url DeepStream url
     * @return DeepstreamClient
     * @throws URISyntaxException when url is null
     */
    public static DeepstreamClient getClient(String url) throws URISyntaxException, InvalidDeepstreamConfig {
        if(url == null){
            throw new URISyntaxException(url, "must not be null");
        }

        if(lastUrl != null && !url.equals(lastUrl)){
            close();
        }

        if(currentInstance == null || currentInstance.getConnectionState() == ConnectionState.CLOSED || currentInstance.getConnectionState() == ConnectionState.ERROR) {
            Properties properties = new Properties();
            properties.setProperty(ConfigOptions.RECORD_MERGE_STRATEGY.toString(), MergeStrategy.LOCAL_WINS.toString());
            currentInstance = new DeepstreamClient(url, properties);
            lastUrl = url;
        }

        Log.d(DeepstreamInstance.class.getName(), "Current Instance: " + currentInstance.toString());
        Log.d(DeepstreamInstance.class.getName(), "Current Instance State: " + currentInstance.getConnectionState().toString());
        return currentInstance;
    }

    /**
     * Close the current Deepstream Client and invalidate it
     */
    public static void close(){
        try {
            if (currentInstance != null) {
                currentInstance.close();
                currentInstance = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Convenience method for subscribing to a DeepStream Record that will ensure that a callback
     * is not subscribed multiple times to the same record to prevent unexpected callbacks
     * @param record DeepStream record to subscribe to
     * @param recordChangedCallback record callback
     * @param recordEventsListener record event listener
     */
    public static void subscribeRecord(Record record, RecordChangedCallback recordChangedCallback, RecordEventsListener recordEventsListener){
        if(record != null){
            if(recordEventsListener != null) {
                record.removeRecordEventsListener(recordEventsListener);
                record.addRecordEventsListener(recordEventsListener);
            }

            if(recordChangedCallback != null) {
                record.unsubscribe(recordChangedCallback);
                record.subscribe(recordChangedCallback);
            }
        }
    }

    /**
     * Convinience method for un-subscribing from DeepStream Record
     * @param record DeepStream record to un-subscribe from
     * @param recordChangedCallback record callback
     * @param recordEventsListener record event listener
     */
    public static void unSubscribeRecord(Record record, RecordChangedCallback recordChangedCallback, RecordEventsListener recordEventsListener){
        if(record != null){
            if(recordEventsListener != null) {
                record.removeRecordEventsListener(recordEventsListener);
            }

            if(recordChangedCallback != null) {
                record.unsubscribe(recordChangedCallback);
            }
        }
    }

    /**
     * Convenience method for subscribing to a DeepStream List that will ensure that a callback
     * is not subscribed multiple times to the same list to prevent unexpected callbacks
     * @param list DeepStream list to subscribe to
     * @param listChangedListener list change listener
     * @param recordEventsListener record event listener
     */
    public static void subscribeList(List list, ListChangedListener listChangedListener, RecordEventsListener recordEventsListener){
        if(list != null){
            if(recordEventsListener != null) {
                list.removeRecordEventsListener(recordEventsListener);
                list.addRecordEventsListener(recordEventsListener);
            }

            if(listChangedListener != null) {
                list.unsubscribe(listChangedListener);
                list.subscribe(listChangedListener);
            }
        }
    }

    /**
     * Convinience method for un-subscribing from DeepStream List
     * @param list DeepStream list to un-subscribe from
     * @param listChangedListener list change listener
     * @param recordEventsListener record event listener
     */
    public static void unSubscribeList(List list, ListChangedListener listChangedListener, RecordEventsListener recordEventsListener){
        if(list != null){
            if(recordEventsListener != null) {
                list.removeRecordEventsListener(recordEventsListener);
            }

            if(listChangedListener != null) {
                list.unsubscribe(listChangedListener);
            }
        }
    }


}