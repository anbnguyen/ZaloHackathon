// Generated code from Butter Knife. Do not modify!
package com.example.nguye.exerciseassistant.ExerciseVideo;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class BottomFragment$$ViewInjector {
  public static void inject(Finder finder, final com.example.nguye.exerciseassistant.ExerciseVideo.BottomFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131427472);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427472' for field 'txtTitle' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.txtTitle = (android.widget.TextView) view;
    view = finder.findById(source, 2131427473);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427473' for field 'txtDetail' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.txtDetail = (android.widget.TextView) view;
    view = finder.findById(source, 2131427474);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427474' for field 'recyclerView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.recyclerView = (android.support.v7.widget.RecyclerView) view;
  }

  public static void reset(com.example.nguye.exerciseassistant.ExerciseVideo.BottomFragment target) {
    target.txtTitle = null;
    target.txtDetail = null;
    target.recyclerView = null;
  }
}
