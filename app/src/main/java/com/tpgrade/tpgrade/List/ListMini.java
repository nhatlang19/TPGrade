package com.tpgrade.tpgrade.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;

public abstract class ListMini {

    ArrayList<VH> vhs;
    ViewGroup vg;

    public ListMini(ViewGroup vg) {
        this.vg = vg;
        this.vhs = new ArrayList<>();
        init(vg);
        //call create to start display
    }

    public static void animationExpand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void animationCollapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void init(ViewGroup ll) {
    }

    public void create() {
        clear();
        LayoutInflater inflater = LayoutInflater.from(vg.getContext());
        for (int i = 0; i < getNumber(); i++) {
            VH vh = createItem(i, inflater);
            vg.addView(vh.item);
            vhs.add(vh);
            update(i);
        }
    }

    public abstract VH createItem(int i, LayoutInflater inflater);

    public abstract int getNumber();

    //like bindview
    public abstract void update(int i);

    public void updateAll() {
        for (int i = 0; i < getNumber(); i++) update(i);
    }

    public VH getMiniVH(int i) {
        return getVhs().get(i);
    }

    public ArrayList<VH> getVhs() {
        return vhs;
    }

    public void clear() {
        vg.removeAllViews();
        vhs.clear();
    }

    public class VH {
        public View item;

        public VH(View v) {
            item = v;
        }
    }
}