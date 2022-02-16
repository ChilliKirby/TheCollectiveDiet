package com.example.thecollectivediet.Me_Fragment_Components.Food_Editing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecollectivediet.JSON_Marshall_Objects.EditFoodObject;
import com.example.thecollectivediet.JSON_Utilities.JSONSerializer;

import java.util.List;

import com.example.thecollectivediet.R;

public class EditMealFragment extends Fragment{

    private RecyclerView mBreakfast;
    private RecyclerView mLunch;
    private RecyclerView mDinner;
    private RecyclerView mSnacks;

    RecyclerView.Adapter breakfastAdapter;
    RecyclerView.Adapter lunchAdapter;
    RecyclerView.Adapter dinnerAdapter;
    RecyclerView.Adapter snacksAdapter;

    private JSONSerializer serializer;
    private List<EditFoodObject> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance){

    View v = inflater.inflate(R.layout.fragment_edit_meal, container, false);

                serializer = new JSONSerializer("EditMealsList.json", getActivity());

        try{
            list = serializer.load();
        } catch (Exception e){
            //list = new ArrayList<FoodResult>();

        }

    //hook elements
        mBreakfast = v.findViewById(R.id.rv_breakfast);
        mBreakfast.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        breakfastAdapter = new RecyclerEditFoodAdapter(getActivity(), list);
        mBreakfast.setAdapter(breakfastAdapter);

//        mLunch = v.findViewById(R.id.rv_lunch);
//        mLunch.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//
//        mDinner = v.findViewById(R.id.rv_dinner);
//        mDinner.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//
//        mSnacks = v.findViewById(R.id.rv_snacks);
//        mSnacks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

    return v;
    }
}

//
//public class EditMealFragment extends Fragment {
//
//    private JSONSerializer serializer;
//    private List<EditFoodObject> list;
//
//    RecyclerView recyclerView;
//    RecyclerView.Adapter adapter;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//
//        View v = inflater.inflate(R.layout.fragment_edit_meal, container, false);
//
//        serializer = new JSONSerializer("EditMealsList.json", getActivity());
//
//        try{
//            list = serializer.load();
//        } catch (Exception e){
//            //list = new ArrayList<FoodResult>();
//
//        }
//
//        recyclerView = v.findViewById(R.id.rv_breakfast);
//        adapter = new RecyclerEditFoodAdapter(getActivity(), list);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//
//        //recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());///////////??????????
//
//        recyclerView.setAdapter(adapter);
//        return v;
//    }
//}
