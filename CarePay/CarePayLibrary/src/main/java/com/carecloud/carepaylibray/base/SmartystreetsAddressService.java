package com.carecloud.carepaylibray.base;

import com.smartystreets.api.exceptions.SmartyException;
import com.smartystreets.api.us_street.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Rahul on 9/19/16.
 */
public class SmartystreetsAddressService {
    private static   Client client = new ClientBuilder("735638a9-0390-4037-bec4-05cf6135ed53", "T8XDbVVqO8r63KVvJTnp").build();


    public static Candidate getAddressByZipCode(String zipcode){

        Lookup lookup = null;
        Candidate  candidate=null;
        try {
            //TODO lookup = new Lookup();
            lookup.setZipCode(zipcode);
            client.send(lookup);
            ArrayList<Candidate> candidates = lookup.getResult();
            if (candidates!=null && !candidates.isEmpty())
                candidate= candidates.get(0);

        }
        catch (SmartyException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            lookup=null;

        }

        return candidate;

    }

/*    public void getAddressByAddress(String address){

        Lookup lookup = new Lookup();
        lookup.setAddressee(address);


        try {
            client.send(lookup);
        }
        catch (SmartyException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        ArrayList<Candidate> results = lookup.getResult();

        if (results.isEmpty()) {
            System.out.println("No candidates. This means the address is not valid.");
            return;
        }

        Candidate firstCandidate = results.get(0);

    }


    public void getAddressByCityName(String cityname){

        Lookup lookup = new Lookup();
        lookup.setZipCode(cityname);


        try {
            client.send(lookup);
        }
        catch (SmartyException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        ArrayList<Candidate> results = lookup.getResult();

        if (results.isEmpty()) {
            System.out.println("No candidates. This means the address is not valid.");
            return;
        }

        Candidate firstCandidate = results.get(0);

    }*/

}
