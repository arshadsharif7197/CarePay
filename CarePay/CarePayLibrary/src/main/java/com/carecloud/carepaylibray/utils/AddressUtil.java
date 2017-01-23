package com.carecloud.carepaylibray.utils;

import com.smartystreets.api.Credentials;
import com.smartystreets.api.SharedCredentials;
import com.smartystreets.api.exceptions.SmartyException;
import com.smartystreets.api.us_zipcode.City;
import com.smartystreets.api.us_zipcode.Client;
import com.smartystreets.api.us_zipcode.ClientBuilder;
import com.smartystreets.api.us_zipcode.Lookup;
import com.smartystreets.api.us_zipcode.Result;

import java.io.IOException;

/**
 * Created by kkannan on 9/22/16.
 */

public class AddressUtil {

    public static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };

    /**
     * This method take a US Zipcode as a param and returns the first result
     * object of type com.smartystreets.api.us_zipcode.City from a list.
     *
     *
     * The City object contains city and state information within it.
     **/
    public static City getCityAndStateByZipCode(String zipcode)
    {

        if(StringUtil.isNullOrEmpty(zipcode))
            return null;

        Credentials mobile = new SharedCredentials("6588384621595519", "CarePayMobile");
        Client client = new ClientBuilder(mobile).build();
        Lookup lookup;
        City city = null;
        lookup = new Lookup();
        lookup.setZipCode(zipcode);

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

        Result result = lookup.getResult();
        try{
         if(result != null && result.isValid() )
         {
            city = result.getCity(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return city;

    }
}
