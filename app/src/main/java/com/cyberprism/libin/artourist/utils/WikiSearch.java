/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyberprism.libin.artourist.utils;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Libin
 */
public class WikiSearch {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String search(String subject) {
        try {
            
            URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exsectionformat=plain&titles=" + subject.replace(" ", "%20"));
            String text = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
                String line = null;
                while (null != (line = br.readLine())) {
                    line = line.trim();
                    if (true) {
                        text += line;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(WikiSearch.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("text = " + text);
            JSONObject json = new JSONObject(text);
            JSONObject query = json.getJSONObject("query");
            JSONObject pages = query.getJSONObject("pages");
            Iterator iterator = pages.keys();
            
            String extract1 = "";
            
            while (iterator.hasNext())
            {
                String key = (String) iterator.next();
                System.out.println("key = " + key);
                JSONObject page = pages.getJSONObject(key);
                String extract = page.getString("extract");
//                System.out.println("extract = " + extract);
              
                extract1 += extract + "\r\n";
                
            }
            
            return extract1;
        } catch (MalformedURLException ex) {
            Logger.getLogger(WikiSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(WikiSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "No data to show";
    }
}
