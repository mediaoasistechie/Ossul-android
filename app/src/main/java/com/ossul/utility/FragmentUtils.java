package com.ossul.utility;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ossul.R;
import com.ossul.fragments.BaseFragment;

import java.util.Set;

/**
 * *Utilities for fragment
 */
public class FragmentUtils {

    public static void replaceFragment(FragmentManager manager, String tag, boolean isPushedToBackStack, int id, BaseFragment fragment) {
        replaceFragment(manager, tag, isPushedToBackStack, id, fragment, false);
    }


    public static void replaceFragmentWithSlide(FragmentManager manager, String tag, boolean isPushedToBackStack, int id, BaseFragment fragment) {
        replaceFragment(manager, tag, isPushedToBackStack, id, fragment, false, true, R.anim.enter_from_right, 0, 0, R.anim.exit_to_right);
    }

    public static void replaceFragment(FragmentManager manager, String tag, boolean isPushedToBackStack, int id, BaseFragment fragment, boolean isAllowStateLess) {
        replaceFragment(manager, tag, isPushedToBackStack, id, fragment, isAllowStateLess, true, 0, 0, 0, 0);
    }

    public static void addFragment(FragmentManager manager, String tag, boolean isPushedToBackStack, int id, BaseFragment fragment) {
        addFragment(manager, tag, isPushedToBackStack, id, fragment, false);
    }

    public static void addFragment(FragmentManager manager, String tag, boolean isPushedToBackStack, int id, BaseFragment fragment, boolean isAllowStateLess) {
        replaceFragment(manager, tag, isPushedToBackStack, id, fragment, isAllowStateLess, false, 0, 0, 0, 0);
    }


    public static void replaceFragment(FragmentManager manager, String tag, boolean isPushedToBackStack, int id, BaseFragment fragment, boolean isAllowStateLess, boolean isReplacing, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (manager == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

        Fragment f = manager.findFragmentById(id);

        if (fragment == null || tag == null || (f != null && f.getTag().equalsIgnoreCase(tag) && equalBundle(fragment, f))) {
            return;
        }

        if (isPushedToBackStack) {
            f = manager.findFragmentByTag(tag);
            if (f != null) {
                Bundle arguments = fragment.getArguments();
                if (arguments != null) {
                    Bundle arguments1 = f.getArguments();
                    if (arguments1 != null) {
                        arguments1.putAll(arguments);
                    }
                }
            }
            if (manager.popBackStackImmediate(tag, 0)) {
                return;
            }
            fragmentTransaction.addToBackStack(tag);
        }
        if (isReplacing) {
            fragmentTransaction.replace(id, fragment, tag);
        } else {
            fragmentTransaction.add(id, fragment, tag);
        }

        if (isAllowStateLess) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }

    }


    private static boolean equalBundle(BaseFragment newFragment, Fragment oldFragment) {
        if (oldFragment == null) {
            return true;
        }
        Bundle newArguments = newFragment.getArguments();
        Bundle oldArguments = oldFragment.getArguments();
        if (newArguments == null && oldArguments == null) {
            return true;
        }

        Set<String> strings = oldArguments.keySet();
        boolean b = true;
        for (String s : strings) {
            Object o = oldArguments.get(s);
            Object o1 = newArguments.get(s);
            if (o == null && o1 == null) {
                continue;
            }

            if (o == null || o1 == null) {
                b &= false;
                continue;
            }

            b &= o.equals(o1);
            if (!b) {
                break;
            }
        }
        return b;

    }

    public static void clearStack(FragmentManager manager) {
        for (int i = 0; i < manager.getBackStackEntryCount(); ++i) {
            manager.popBackStack();
        }
    }
}
