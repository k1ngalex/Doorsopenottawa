package com.algonquincollege.king0410.doorsopenottawa.parsers;

/**
 * Created by Alex on 2016-11-08.
 */

import com.algonquincollege.king0410.doorsopenottawa.Model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2016-11-08.
 */

public class BuildingJSONparser {
    public static List<Building> parseFeed(String content) {
        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");

            List<Building> listbuilding = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {
                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();
                building.setName(obj.getString("name"));
                building.setAddress(obj.getString("address"));
                building.setBuildingId(obj.getInt("buildingId"));
                building.setImage(obj.getString("image"));
                listbuilding.add(building);
            }
            return listbuilding;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
