package com.example.alisha_androidlabassign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DataBaseHelper mDatabase;

    Button Add;
    SwipeMenuListView listView;
    String[] favouriteLocation;

    private static final String TAG = "MainActivity";


    // boolean test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabase = new DataBaseHelper(this);
        Add = findViewById(R.id.btn_add);
        listView = (SwipeMenuListView) findViewById(R.id.list_View);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AddLocation.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddLocation.class);
                intent.putExtra("location", position);
                startActivity(intent);
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem Edit = new SwipeMenuItem(getApplicationContext());
                Edit.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                Edit.setWidth(170);
                Edit.setTitle("Edit");
                Edit.setTitleSize(18);
                Edit.setTitleColor(Color.BLACK);
                menu.addMenuItem(Edit);

                SwipeMenuItem Delete = new SwipeMenuItem(
                        getApplicationContext());
                Delete.setBackground(new ColorDrawable(Color.rgb(0, 118, 0)));
                Delete.setWidth(170);
                Delete.setTitle("delete");
                Delete.setTitleSize(18);
                Delete.setTitleColor(Color.BLACK);
                menu.addMenuItem(Delete);

            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        System.out.println("edittt");
                        Intent intent = new Intent(MainActivity.this, AddLocation.class);
//                        intent.putExtra("location",position);
                        startActivity(intent);

                        break;
                    case 1:
                        System.out.println("deletteeee");

                        boolean b = mDatabase.deleteLocation(FavPlace_Address.selected_place.get(position).getId());


                        if (b){
                            loadLocations();
                            System.out.println("if part");
                        }else {

                            System.out.println("else part ");

                        }



//
                        break;
                    default:
                        break;
                }
                return false;
            }

        });
      loadLocations();
    }


    @Override
    protected void onStart() {
        super.onStart();

        loadLocations();

    }

    private void loadLocations() {

        FavPlace_Address.selected_place.clear();

        Cursor cursor = mDatabase.getAllLocations();
        if (cursor.moveToFirst()) {
            do {
                FavPlace_Address.selected_place.add(new FavPlace_Address(
                        cursor.getInt(0),
                        cursor.getDouble(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();

        }


        favouriteLocation = new String[FavPlace_Address.selected_place.size()];


        for (int i = 0; i < FavPlace_Address.selected_place.size(); i++) {

            if (FavPlace_Address.selected_place.get(i).getAddress() != null) {
                favouriteLocation[i] = FavPlace_Address.selected_place.get(i).getAddress();
            } else {
                favouriteLocation[i] = FavPlace_Address.selected_place.get(i).getDate();
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, favouriteLocation);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

    }



}