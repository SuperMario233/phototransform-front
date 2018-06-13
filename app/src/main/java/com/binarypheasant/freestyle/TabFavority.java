package com.binarypheasant.freestyle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TabFavority extends Fragment {
    private List<Filter> filterList = new ArrayList<Filter>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabfavority, container, false);
        initFavority();
        FilterAdapter adapter = new FilterAdapter(super.getActivity(),R.layout.filter_item,filterList);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);/**/
        return rootView;
    }
    private void initFavority(){
    }
}