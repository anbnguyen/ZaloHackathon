package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Ulfbert on 11/28/2016.
 */

public class ExerciseItem  implements Comparable<ExerciseItem> {
    public String title;
    public String thumbnailUrl;
    public String totalViewFormat;
    public String duration;
    public String dateCreated;
    public String id;

    @Override
    public int compareTo(ExerciseItem o) {
        return this.totalViewFormat.compareTo(o.totalViewFormat);
    }

    public ExerciseItem() {
    }

}