package com.nice.planetlauncher;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private PlanetView planetView;
    private final List<AppEntry> allApps = new ArrayList<>();
    private EditText search;
    private TextView hint;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(5,9,20));
        getWindow().setNavigationBarColor(Color.rgb(5,9,20));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 0);
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(5,9,20));
        planetView = new PlanetView(this);
        root.addView(planetView, new FrameLayout.LayoutParams(-1,-1));

        LinearLayout overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setPadding(dp(22), dp(34), dp(22), dp(18));
        FrameLayout.LayoutParams op = new FrameLayout.LayoutParams(-1,-1);
        op.gravity = Gravity.TOP;
        root.addView(overlay, op);

        TextView title = new TextView(this);
        title.setText("P L A N E T   L A U N C H E R");
        title.setTextColor(Color.rgb(196,225,255));
        title.setTextSize(11);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(.12f);
        overlay.addView(title, new LinearLayout.LayoutParams(-1, dp(24)));

        TextView subtitle = new TextView(this);
        subtitle.setText("তোমার অ্যাপগুলো, একটি গ্রহে");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(19);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(5), 0, dp(12));
        overlay.addView(subtitle);

        search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setHint("অ্যাপ খুঁজুন…");
        search.setHintTextColor(Color.rgb(145,163,190));
        search.setTextColor(Color.WHITE);
        search.setPadding(dp(18),0,dp(18),0);
        search.setBackground(roundBg());
        overlay.addView(search, new LinearLayout.LayoutParams(-1, dp(48)));

        View spacer = new View(this);
        overlay.addView(spacer, new LinearLayout.LayoutParams(1,0,1));
        hint = new TextView(this);
        hint.setText("ঘোরাতে টেনে ধরুন  •  খুলতে আইকনে ট্যাপ করুন");
        hint.setTextColor(Color.rgb(176,195,220));
        hint.setTextSize(12);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(12), 0, dp(14));
        overlay.addView(hint);
        setContentView(root);

        loadApps("");
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            @Override public void onTextChanged(CharSequence s,int st,int before,int count){ loadApps(s.toString()); }
            @Override public void afterTextChanged(Editable e){}
        });
    }

    private android.graphics.drawable.GradientDrawable roundBg() {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(Color.argb(175,18,31,54)); d.setCornerRadius(dp(28));
        d.setStroke(dp(1), Color.argb(100,120,190,255)); return d;
    }
    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }

    private void loadApps(String query) {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        ArrayList<AppEntry> filtered = new ArrayList<>();
        String own = getPackageName();
        for (ResolveInfo ri : infos) {
            if (ri.activityInfo == null || own.equals(ri.activityInfo.packageName)) continue;
            String label = ri.loadLabel(pm).toString();
            if (!label.toLowerCase().contains(query.toLowerCase())) continue;
            filtered.add(new AppEntry(label, ri.activityInfo.packageName, ri.activityInfo.name, ri.loadIcon(pm)));
        }
        Collections.sort(filtered, Comparator.comparing(a -> a.label.toLowerCase()));
        allApps.clear(); allApps.addAll(filtered);
        if (planetView != null) planetView.setApps(filtered);
        if (hint != null) hint.setText(filtered.size()+" টি অ্যাপ  •  ঘোরাতে টেনে ধরুন  •  খুলতে ট্যাপ করুন");
    }
    @Override protected void onResume(){ super.onResume(); if(planetView!=null) loadApps(search==null?"":search.getText().toString()); }

    void launch(AppEntry app) {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setClassName(app.packageName, app.activityName);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch(Exception ignored) { }
    }
    static class AppEntry {
        final String label, packageName, activityName;
        final android.graphics.drawable.Drawable icon;
        AppEntry(String l,String p,String a,android.graphics.drawable.Drawable d){label=l;packageName=p;activityName=a;icon=d;}
    }
}
