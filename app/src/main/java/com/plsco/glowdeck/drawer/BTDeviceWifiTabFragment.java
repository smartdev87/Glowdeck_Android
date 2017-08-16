package com.plsco.glowdeck.drawer;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plsco.glowdeck.R;

public class BTDeviceWifiTabFragment extends Fragment {
	//private FragmentTabHost mTabHost;
	//Mandatory Constructor
    public BTDeviceWifiTabFragment() {
    }
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_btdevice_wifi,container, false);

        /*
        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        Context context = MainActivity.getAppContext() ; 
        FragmentManager manager = getChildFragmentManager() ;  
        int containerId = R.id.realtabcontent ;
        mTabHost.setup(context, manager, containerId) ;
        //.setup(MainActivity.getAppContext(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Fragment B"),
                FragmentB.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Fragment C"),
                FragmentC.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentd").setIndicator("Fragment D"),
                FragmentD.class, null);

		*/
        return rootView;
    }
    
    
}

