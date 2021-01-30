package net.trejj.rubytalksense.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import net.trejj.rubytalksense.activities.main.calls.CallsFragment;
import net.trejj.rubytalksense.activities.main.chats.FragmentChats;
import net.trejj.rubytalksense.activities.main.contacts.ContactsFragment;
import net.trejj.rubytalksense.activities.main.status.StatusFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int count = 4;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ContactsFragment();
            case 1:
                return new FragmentChats();
            case 2:
                return new StatusFragment();
            case 3:
                return new CallsFragment();
            default:
                throw new IllegalStateException("Not implemented Fragment exception");
        }

    }

    @Override
    public int getCount() {
        return count;

    }



}
