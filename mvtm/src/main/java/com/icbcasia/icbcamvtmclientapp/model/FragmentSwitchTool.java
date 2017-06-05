package com.icbcasia.icbcamvtmclientapp.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chance on 17/03/28.
 */
public class FragmentSwitchTool implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private View[] currentSelectedView;
    private View[] clickableViews; //传入用于被点击的view,比如是一个LinearLayout
    private List<View[]> selectedViews; //传入用于被更改资源selected状态的view[], 比如一组View[]{TextView, ImageView}
    private Class<? extends Fragment>[] fragments;
    private Bundle[] bundles;
    private int containerId;

    public FragmentSwitchTool(FragmentManager fragmentManager, int containerId) {
        super();
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void setClickableViews(View... clickableViews) {
        this.clickableViews = clickableViews;
        for (View view : clickableViews) {
            view.setOnClickListener(this);
        }
    }

    public void setSelectedViews(List<View[]> selectedViews) {
        this.selectedViews = selectedViews;
    }

    public FragmentSwitchTool addSelectedViews(View... views) {
        if (selectedViews == null) {
            selectedViews = new ArrayList<View[]>();
        }
        selectedViews.add(views);
        return this;
    }

    public void setFragments(Class<? extends Fragment>... fragments) {
        this.fragments = fragments;
    }

    public void setBundles(Bundle... bundles) {
        this.bundles = bundles;
    }

    public void changeTag(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(v.getId()));
        for (int i = 0; i < clickableViews.length; i++) {
            if (v.getId() == clickableViews[i].getId()) {
                if (fragment == null) {
                    if (currentFragment != null) {
                        fragmentTransaction.hide(currentFragment);
                        for (View view : currentSelectedView) {
                            view.setSelected(false);
                        }
                    }
                    try {
                        fragment = fragments[i].newInstance();
                        if (bundles != null && bundles[i] != null) {
                            fragment.setArguments(bundles[i]);
                        }
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    fragmentTransaction.add(containerId, fragment, String.valueOf(clickableViews[i].getId()));
                } else if (fragment == currentFragment) {
                } else {
                    fragmentTransaction.hide(currentFragment);
                    for (View view : currentSelectedView) {
                        view.setSelected(false);
                    }
                    fragmentTransaction.show(fragment);
                }
                fragmentTransaction.commit();
                currentFragment = fragment;
                for (View view : selectedViews.get(i)) {
                    view.setSelected(true);
                }
                currentSelectedView = selectedViews.get(i);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        changeTag(v);
    }

    public void initAll() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            for (int i = clickableViews.length - 1; i > -1; i--) {
                Fragment fragment = fragments[i].newInstance();
                if (bundles != null && bundles[i] != null) {
                    fragment.setArguments(bundles[i]);
                }
                if (currentFragment != null) {
                    fragmentTransaction.hide(currentFragment);
                }
                fragmentTransaction.add(containerId, fragment, String.valueOf(clickableViews[i].getId()));
                currentFragment = fragment;
            }
            fragmentTransaction.commit();
            currentSelectedView = selectedViews.get(0);
            for (View view : currentSelectedView) {
                view.setSelected(true);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}