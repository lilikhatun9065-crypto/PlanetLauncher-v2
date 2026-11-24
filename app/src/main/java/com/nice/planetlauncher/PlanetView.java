
package com.nice.planetlauncher;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class PlanetView extends View {
    private final Paint p = new Paint(3);
    private final Paint iconPaint = new Paint(3);
    private final List<MainActivity.AppEntry> apps = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private float rotY = 0.25f, rotX = -0.12f, downX, downY, lastX, lastY, velocityY;
    private long lastTime;
    private float cx, cy, radius;
    private final float density;
    private final Path clip = new Path();

    public PlanetView(Context c) {
        super(c); density=getResources().getDisplayMetrics().density;
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        java.util.Random r = new java.util.Random(41);
        for(int i=0;i<150;i++) stars.add(new Star(r.nextFloat(),r.nextFloat(),.5f+r.nextFloat()*1.7f,r.nextFloat()));
    }
    public void setApps(List<MainActivity.AppEntry> list){ apps.clear(); apps.addAll(list); invalidate(); }
    private float dp(float v){return v*density;}

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c); float w=getWidth(), h=getHeight(); cx=w/2; cy=h*.53f; radius=Math.min(w*.39f,h*.285f);
        c.drawColor(Color.rgb(5,9,20));
        // soft nebula glows
        p.setShader(new RadialGradient(cx,cy,radius*2.25f,new int[]{0x303B83D5,0x10245C9C,0x00050914},null,Shader.TileMode.CLAMP));
        c.drawCircle(cx,cy,radius*2.25f,p); p.setShader(null);
        for(Star s:stars){ p.setColor(Color.argb((int)(65+s.a*150),190,218,255)); c.drawCircle(s.x*w,s.y*h,dp(s.size),p); }
        // atmospheric halo
        p.setShader(new RadialGradient(cx,cy,radius*1.23f,new int[]{0x003EBDFF,0x243EBDFF,0x553A8DFF,0x003A8DFF},null,Shader.TileMode.CLAMP));
        c.drawCircle(cx,cy,radius*1.23f,p); p.setShader(null);
        // sphere base gradient
        p.setShader(new RadialGradient(cx-radius*.32f,cy-radius*.38f,radius*1.65f,new int[]{0xFF285C83,0xFF102D50,0xFF071426,0xFF020713},new float[]{0f,.42f,.78f,1f},Shader.TileMode.CLAMP));
        c.drawCircle(cx,cy,radius,p); p.setShader(null);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(dp(1.5f)); p.setColor(0x884FCBFF); c.drawCircle(cx,cy,radius,p); p.setStyle(Paint.Style.FILL);
        // latitude/longitude-like fine curves
        p.setColor(0x334DBBEA); p.setStrokeWidth(dp(.7f)); p.setStyle(Paint.Style.STROKE);
        c.save(); c.clipPath(circlePath());
        for(int k=-2;k<=2;k++) { float yy=cy+k*radius*.31f; float rx=(float)Math.sqrt(Math.max(0,radius*radius-(yy-cy)*(yy-cy))); c.drawOval(cx-rx,yy-radius*.12f,cx+rx,yy+radius*.12f,p); }
        c.restore(); p.setStyle(Paint.Style.FILL);

        ArrayList<Projected> points = new ArrayList<>();
        int n=apps.size();
        if(n>0){
            double golden=Math.PI*(3-Math.sqrt(5));
            for(int i=0;i<n;i++){
                double yy=1-2.0*(i+.5)/n; double rr=Math.sqrt(Math.max(0,1-yy*yy)); double theta=golden*i;
                float x=(float)(Math.cos(theta)*rr), z=(float)(Math.sin(theta)*rr), y=(float)yy;
                // rotate around vertical and horizontal axes
                float x1=(float)(x*Math.cos(rotY)+z*Math.sin(rotY)); float z1=(float)(-x*Math.sin(rotY)+z*Math.cos(rotY));
                float y2=(float)(y*Math.cos(rotX)-z1*Math.sin(rotX)); float z2=(float)(y*Math.sin(rotX)+z1*Math.cos(rotX));
                float depth=(z2+1)/2f; float scale=.78f+depth*.30f;
                points.add(new Projected(apps.get(i),cx+x1*radius,cy-y2*radius,depth,scale));
            }
        }
        points.sort((a,b)->Float.compare(a.depth,b.depth));
        for(Projected q:points){
            if(q.depth<.08f) continue;
            float sz=dp(31)*q.scale;
            p.setColor(Color.argb((int)(70+q.depth*95),0,0,0)); c.drawCircle(q.x+dp(1),q.y+dp(3),sz*.57f,p);
            p.setShader(new RadialGradient(q.x-sz*.2f,q.y-sz*.25f,sz,new int[]{0xFF536F8E,0xFF172A43,0xFF081321},null,Shader.TileMode.CLAMP));
            c.drawCircle(q.x,q.y,sz*.54f,p); p.setShader(null);
            p.setColor(0x9955CFFF); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(dp(.8f)); c.drawCircle(q.x,q.y,sz*.54f,p); p.setStyle(Paint.Style.FILL);
            int save=c.save(); c.translate(q.x-sz*.37f,q.y-sz*.37f); c.scale((sz*.74f)/dp(40),(sz*.74f)/dp(40));
            q.app.icon.setBounds(0,0,(int)dp(40),(int)dp(40)); q.app.icon.setAlpha((int)(150+q.depth*105)); q.app.icon.draw(c); q.app.icon.setAlpha(255); c.restoreToCount(save);
            if(q.depth>.68f){
                p.setColor(Color.argb((int)(q.depth*235),235,244,255)); p.setTextSize(dp(9)); p.setTextAlign(Paint.Align.CENTER);
                String label=q.app.label; if(label.length()>13) label=label.substring(0,12)+"…";
                c.drawText(label,q.x,q.y+sz*.72f,p);
            }
        }
        // central glossy highlight
        p.setShader(new RadialGradient(cx-radius*.34f,cy-radius*.42f,radius*.72f,new int[]{0x1FFFFFFF,0x00FFFFFF},null,Shader.TileMode.CLAMP)); c.drawCircle(cx,cy,radius,p); p.setShader(null);
        long now=System.currentTimeMillis(); if(lastTime==0)lastTime=now; float dt=Math.min(.04f,(now-lastTime)/1000f); lastTime=now;
        if(Math.abs(velocityY)>.01f){rotY+=velocityY*dt; velocityY*=Math.pow(.94,dt*60); postInvalidateOnAnimation();}
        else postInvalidateOnAnimation();
    }
    private Path circlePath(){clip.reset();clip.addCircle(cx,cy,radius,Path.Direction.CW);return clip;}
    @Override public boolean onTouchEvent(MotionEvent e){
        float x=e.getX(),y=e.getY();
        switch(e.getActionMasked()){
            case MotionEvent.ACTION_DOWN: downX=lastX=x;downY=lastY=y;velocityY=0;return true;
            case MotionEvent.ACTION_MOVE:
                float dx=x-lastX,dy=y-lastY; rotY+=dx/(Math.max(1,radius)*1.5f); rotX+=dy/(Math.max(1,radius)*1.5f); rotX=Math.max(-1.15f,Math.min(1.15f,rotX)); velocityY=dx/(Math.max(1,radius)*1.5f)*8f; lastX=x;lastY=y;invalidate();return true;
            case MotionEvent.ACTION_UP:
                if(Math.hypot(x-downX,y-downY)<dp(12)){
                    Projected best=null;float bd=Float.MAX_VALUE;
                    for(int i=0;i<apps.size();i++){Projected q=project(i); if(q==null||q.depth<.18f)continue;float d=(q.x-x)*(q.x-x)+(q.y-y)*(q.y-y);if(d<bd&&d<dp(48)*dp(48)){bd=d;best=q;}}
                    if(best!=null && getContext() instanceof MainActivity)((MainActivity)getContext()).launch(best.app);
                } else velocityY*=.35f;
                return true;
        } return true;
    }
    private Projected project(int i){int n=apps.size();if(n==0)return null;double g=Math.PI*(3-Math.sqrt(5));double yy=1-2.0*(i+.5)/n,rr=Math.sqrt(Math.max(0,1-yy*yy)),t=g*i;float x=(float)(Math.cos(t)*rr),z=(float)(Math.sin(t)*rr),y=(float)yy;float x1=(float)(x*Math.cos(rotY)+z*Math.sin(rotY)),z1=(float)(-x*Math.sin(rotY)+z*Math.cos(rotY)),y2=(float)(y*Math.cos(rotX)-z1*Math.sin(rotX)),z2=(float)(y*Math.sin(rotX)+z1*Math.cos(rotX));return new Projected(apps.get(i),cx+x1*radius,cy-y2*radius,(z2+1)/2,.8f);}
    static class Projected{MainActivity.AppEntry app;float x,y,depth,scale;Projected(MainActivity.AppEntry a,float x,float y,float d,float s){app=a;this.x=x;this.y=y;depth=d;scale=s;}}
    static class Star{float x,y,size,a;Star(float x,float y,float s,float a){this.x=x;this.y=y;size=s;this.a=a;}}
}
