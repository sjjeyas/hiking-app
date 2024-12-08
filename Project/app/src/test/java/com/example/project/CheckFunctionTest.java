package com.example.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.project.classes.CheckFunctions;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CheckFunctionTest {
    private String v = "view";
    private String u = "user";
    @Test
    public void sameUser_false() {
        String view = "a";
        String user = "b";
        CheckFunctions cf = new CheckFunctions(view, user);
        assertFalse(cf.sameUser());
    }
    @Test
    public void sameUser_true() {
        String view = "a";
        String user = "a";
        CheckFunctions cf = new CheckFunctions(view, user);
        assertTrue(cf.sameUser());
    }
    @Test
    public void validReview_emptyText() {
        String text = "";
        Integer rating = 5;
        String displayname = "dn";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }
    @Test
    public void validReview_nullText() {
        Integer rating = 5;
        String displayname = "dn";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }
    @Test
    public void validReview_badTypeText() {
        Integer text = 1;
        Integer rating = 5;
        String displayname = "dn";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }
    @Test
    public void validReview_nullRating() {
        String text = "text";
        String displayname = "dn";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        assertFalse(cf.validReview(data));
    }
    @Test
    public void validReview_badTypeRating() {
        String text = "text";
        String rating = "5";
        String displayname = "dn";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }
    /*
    @Test
    public void validReview_emptyName() {
        String text = "text";
        Integer rating = 5;
        String displayname = "";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }
    @Test
    public void validReview_nullName() {
        String text = "text";
        Integer rating = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }

     */
    @Test
    public void validReview_badTypeName() {
        String text = "text";
        Integer rating = 5;
        Integer displayname = 1;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertFalse(cf.validReview(data));
    }
    @Test
    public void validReview() {
        String text = "text";
        Integer rating = 5;
        String displayname = "dn";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("displayname", displayname);
        data.put("rating", rating);
        assertTrue(cf.validReview(data));
    }
    @Test
    public void validGroup_emptyName() {
        String name = "";
        String trail = "Pirates Cove Trail";
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("trail", trail);
        data.put("capacity", capacity);
        assertFalse(cf.validGroup(data));
    }
    @Test
    public void validGroup_emptyTrail() {
        String name = "name";
        String trail = "";
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("capacity", capacity);
        data.put("trail", trail);
        assertFalse(cf.validGroup(data));
    }
    @Test
    public void validGroup_nullTrail() {
        String name = "name";
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("capacity", capacity);
        assertFalse(cf.validGroup(data));
    }
    @Test
    public void validGroup_nullName() {
        String trail = "Pirates Cove Trail";
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("trail", trail);
        data.put("capacity", capacity);
        assertFalse(cf.validGroup(data));
    }
    @Test
    public void validGroup_badTypeName() {
        Integer name = 111;
        String trail = "Pirates Cove Trail";
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("trail", trail);
        data.put("capacity", capacity);
        assertFalse(cf.validGroup(data));
    }
    @Test
    public void validGroup_badTypeTrail() {
        String name = "name";
        Boolean trail = true;
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("trail", trail);
        data.put("capacity", capacity);
        assertFalse(cf.validGroup(data));
    }
    @Test
    public void validGroup() {
        String name = "name";
        String trail = "Aimee's Loop";
        Integer capacity = 5;
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("trail", trail);
        data.put("capacity", capacity);
        assertTrue(cf.validGroup(data));
    }
    @Test
    public void validGroup_noCapacity() {
        String name = "name";
        String trail = "Aimee's Loop";
        CheckFunctions cf = new CheckFunctions(v, u);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("trail", trail);
        assertFalse(cf.validGroup(data));
    }
}
