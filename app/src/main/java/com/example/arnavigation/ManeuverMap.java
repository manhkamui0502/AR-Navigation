package com.example.arnavigation;

import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_LEFT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_RIGHT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_SHARP_LEFT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_SHARP_RIGHT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_SLIGHT_LEFT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_STRAIGHT;
import static com.example.arnavigation.Constants.STEP_MANEUVER_MODIFIER_UTURN;

import com.mapbox.api.directions.v5.models.StepManeuver;

import java.util.HashMap;
import java.util.Map;

public class ManeuverMap {

    private final Map<String, String> maneuverMap;
    String type;
    String modifier;

    public ManeuverMap() {
        maneuverMap = new HashMap<>();
        //TURN TYPE
        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_UTURN,
                "direction_uturn");
        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_slight_left");

        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.TURN + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_slight_right");

        //END TYPE
        maneuverMap.put(StepManeuver.END_OF_ROAD + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.END_OF_ROAD + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");

        //CONTINUE TYPE
        maneuverMap.put(StepManeuver.CONTINUE + STEP_MANEUVER_MODIFIER_UTURN,
                "direction_uturn");
        maneuverMap.put(StepManeuver.CONTINUE + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_continue_left");
        maneuverMap.put(StepManeuver.CONTINUE + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_continue_right");
        maneuverMap.put(StepManeuver.CONTINUE + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_continue_straight");

        //ON RAMP TYPE
        maneuverMap.put(StepManeuver.ON_RAMP + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.ON_RAMP + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.ON_RAMP + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_slight_left");

        maneuverMap.put(StepManeuver.ON_RAMP + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.ON_RAMP + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.ON_RAMP + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_slight_right");

        //OFF RAMP TYPE
        maneuverMap.put(StepManeuver.OFF_RAMP + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_continue_left");
        maneuverMap.put(StepManeuver.OFF_RAMP + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_off_ramp_slight_left");
        maneuverMap.put(StepManeuver.OFF_RAMP + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_continue_right");
        maneuverMap.put(StepManeuver.OFF_RAMP + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_off_ramp_slight_right");

        //ARRIVE TYPE
        maneuverMap.put(StepManeuver.ARRIVE + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_arrive");
        maneuverMap.put(StepManeuver.ARRIVE + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_arrive");
        maneuverMap.put(StepManeuver.ARRIVE,
                "direction_arrive");

        //DEPART TYPE
        maneuverMap.put(StepManeuver.DEPART + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.DEPART + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.DEPART,
                "direction_continue_straight");

        //MERGE TYPE
        maneuverMap.put(StepManeuver.MERGE + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.MERGE + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_slight_left");
        maneuverMap.put(StepManeuver.MERGE + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.MERGE + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_slight_right");
        maneuverMap.put(StepManeuver.MERGE + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_continue_straight");


        //FORK TYPE
        maneuverMap.put(StepManeuver.FORK + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.FORK + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_slight_left");
        maneuverMap.put(StepManeuver.FORK + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.FORK + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_slight_right");
        maneuverMap.put(StepManeuver.FORK + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_continue_straight");
        //maneuverMap.put(StepManeuver.FORK, "direction_fork");

        //ROUNDABOUT TYPE
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_roundabout_left");
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
                "direction_roundabout_left");
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_roundabout_left");
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_roundabout_right");
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
                "direction_roundabout_right");
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_roundabout_right");
        maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_roundabout_straight");

    /*
    maneuverMap.put(StepManeuver.ROUNDABOUT, "direction_roundabout");
    maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
            "direction_roundabout_sharp_left");
    maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
            "direction_roundabout_slight_left");
    maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
            "direction_roundabout_sharp_right");
    maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
            "direction_roundabout_slight_right");
    maneuverMap.put(StepManeuver.ROUNDABOUT + STEP_MANEUVER_MODIFIER_STRAIGHT,
            "direction_roundabout_straight");
    maneuverMap.put(StepManeuver.ROUNDABOUT, "direction_roundabout");*/

        //ROUNDABOUT TURN TYPE
        maneuverMap.put(StepManeuver.ROUNDABOUT_TURN + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.ROUNDABOUT_TURN + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");

        //ROTARY TYPE
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_roundabout_left");
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
                "direction_roundabout_left");
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_roundabout_left");
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_roundabout_right");
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
                "direction_roundabout_right");
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_roundabout_right");
        maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_roundabout_straight");

    /*
    maneuverMap.put(StepManeuver.ROTARY, "direction_rotary");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_LEFT,
      "direction_rotary_left");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
      "direction_rotary_sharp_left");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
      "direction_rotary_slight_left");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_RIGHT,
      "direction_rotary_right");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
      "direction_rotary_sharp_right");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
      "direction_rotary_slight_right");
    maneuverMap.put(StepManeuver.ROTARY + STEP_MANEUVER_MODIFIER_STRAIGHT,
      "direction_rotary_straight");
    maneuverMap.put(StepManeuver.ROTARY, "direction_rotary");*/

        //NOTIFICATION TYPE
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_SHARP_LEFT,
                "direction_end_left");
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
                "direction_slight_left");
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
                "direction_end_right");
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
                "direction_slight_right");
        maneuverMap.put(StepManeuver.NOTIFICATION + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_continue_straight");

        maneuverMap.put(StepManeuver.NEW_NAME + STEP_MANEUVER_MODIFIER_STRAIGHT,
                "direction_continue_straight");
    }

    public String getManeuverResource(String maneuver) {
        if (maneuverMap.get(maneuver) != null) {
            return maneuverMap.get(maneuver);
        } else {
            return "starting";
        }
    }
}
