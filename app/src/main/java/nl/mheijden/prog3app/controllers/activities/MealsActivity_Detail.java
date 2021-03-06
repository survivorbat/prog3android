package nl.mheijden.prog3app.controllers.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import nl.mheijden.prog3app.R;
import nl.mheijden.prog3app.controllers.callbacks.DeleteMealControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.InvalidTokenCallback;
import nl.mheijden.prog3app.controllers.callbacks.LeaveControllerCallback;
import nl.mheijden.prog3app.model.domain.FellowEater;
import nl.mheijden.prog3app.model.domain.MaaltijdenApp;
import nl.mheijden.prog3app.model.domain.Meal;

public class MealsActivity_Detail extends AppCompatActivity implements LeaveControllerCallback, DeleteMealControllerCallback, InvalidTokenCallback {
    private Meal meal;
    private MaaltijdenApp app;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals__detail);
        Intent i = getIntent();
        this.meal = (Meal) i.getSerializableExtra("Meal");
        if (this.meal != null) {
            TextView meal_title = findViewById(R.id.meal_title);
            TextView meal_desc = findViewById(R.id.meal_desc);
            TextView meal_chef = findViewById(R.id.meal_chef);
            TextView meal_price = findViewById(R.id.meal_price);
            TextView meal_date = findViewById(R.id.meal_date);
            TextView meal_amount = findViewById(R.id.meal_eaters);
            TextView meal_eaters = findViewById(R.id.meal_felloweaterlist);
            ImageView meal_image = findViewById(R.id.meal_image);
            Button meal_addbutton = findViewById(R.id.meal_eatwithbutton);
            app = new MaaltijdenApp(this,this);

            meal_title.setText(meal.getDish() + "");
            meal_desc.setText(meal.getInfo() + "");
            if (!meal.getChef().getInsertion().equals("null") && meal.getChef().getInsertion() != null) {
                meal_chef.setText(getText(R.string.app_meals_cheficon) + " " + meal.getChef().getFirstname() + " " + meal.getChef().getInsertion() + "" + meal.getChef().getLastname());
            } else {
                meal_chef.setText(getText(R.string.app_meals_cheficon) + " " + meal.getChef().getFirstname() + " " + meal.getChef().getLastname());
            }
            meal_price.setText(getText(R.string.app_meals_moneyicon) + " €" + String.format("%.2f", meal.getPrice()));
            meal_date.setText(getText(R.string.app_meals_timeicon) + " " + meal.getDate() + "");
            meal_amount.setText(getText(R.string.app_dashboard_button_students) + " " + meal.getAmountOfEaters() + "/" + meal.getMaxFellowEaters());

            if (meal.getChef().equals(app.getUser())) {
                meal_addbutton.setText(getText(R.string.app_meal_removemeal));
                meal_addbutton.setTextColor(getColor(R.color.colorDarkRed));
                meal_addbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteMeal();
                    }
                });
            } else if (meal.getStudents().contains(app.getUser())) {
                meal_addbutton.setText(getText(R.string.app_meal_removebutton));
                meal_addbutton.setTextColor(getColor(R.color.colorDarkRed));
                meal_addbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeEatWith();
                    }
                });
            } else if (meal.getAmountOfEaters() < meal.getMaxFellowEaters()) {
                meal_addbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        eatWith();
                    }
                });
            } else {
                meal_addbutton.setTextColor(getColor(R.color.colorDarkRed));
                meal_addbutton.setText(getText(R.string.app_joinmeal_fullalready));
            }
            StringBuilder eaters = new StringBuilder(getText(R.string.app_meals_eaters).toString() + " ");
            boolean first = true;
            for (FellowEater e : meal.getFelloweaters()) {
                if (!first) {
                    eaters.append(", ");
                }
                eaters.append(e.getStudent().getFirstname());
                if (e.getGuests() > 0) {
                    eaters.append(" (+").append(e.getGuests()).append(")");
                }
                first = false;
            }
            meal_eaters.setText(eaters + "");
            File filesDir = this.getFilesDir();
            File f = new File(filesDir, "mealPictures_" + meal.getId());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                meal_image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                meal_image.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo));
            }
        }
    }

    private void eatWith() {
        Intent i = new Intent(this, MealsActivity_Join.class);
        i.putExtra("Meal", meal);
        startActivity(i);
    }

    private void removeEatWith() {
        for (FellowEater fellowEater : meal.getFelloweaters()) {
            if (fellowEater.getStudent().equals(app.getUser())) {
                app.deleteFellowEater(fellowEater, this);
            }
        }
    }

    private void deleteMeal() {
        app.deleteMeal(meal, this);
    }

    public void onDeleteMealComplete(boolean result) {
        if (result) {
            this.finish();
        } else {
            Toast.makeText(this, getText(R.string.app_error_conn), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLeaveComplete(boolean result) {
        if (result) {
            this.finish();
        } else {
            Toast.makeText(this, getText(R.string.app_error_conn), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void invalidToken() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
