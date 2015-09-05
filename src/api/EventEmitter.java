/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author maxim
 */
public class EventEmitter {

  private final HashMap<String, List<Callback>> events;

  public EventEmitter() {
    events = new HashMap<>();
  }

  public void on(String event, Callback cb) {
    List<Callback> arr = events.get(event);
    if (arr == null) {
      arr = new ArrayList<>();
    }
    arr.add(cb);
    events.put(event, arr);
  }

  public void emit(String event, Object... args) {
    List<Callback> cbs = events.get(event);
    if (cbs != null && cbs.size() > 0) {
      for (Callback cb : cbs) {
        cb.call(args);
      }
    }
  }
}
