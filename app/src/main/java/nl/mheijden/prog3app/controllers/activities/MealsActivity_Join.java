package nl.mheijden.prog3app.controllers.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nl.mheijden.prog3app.R;
import nl.mheijden.prog3app.controllers.callbacks.InvalidTokenCallback;
import nl.mheijden.prog3app.controllers.callbacks.JoinControllerCallback;
import nl.mheijden.prog3app.model.domain.FellowEater;
import nl.mheijden.prog3app.model.domain.MaaltijdenApp;
import nl.mheijden.prog3app.model.domain.Meal;

public class MealsActivity_Join extends AppCompatActivity implements JoinControllerCallback, InvalidTokenCallback {
    private Meal meal;
    private EditText amountOfPeople;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals__join);
        Intent i = getIntent();
        meal = (Meal) i.getSerializableExtra("Meal");
        TextView title = findViewById(R.id.joinscreen_title);
        title.setText(meal.getDish() + "");

        amountOfPeople = findViewById(R.id.amountOfPeople);
        TextView amountOfPeopleTitle = findViewById(R.id.joinscreen_amounttitle);

        TextView info = findViewById(R.id.joinscreen_mealinfo);
        info.setText(meal.getDate() + "\n" + meal.getAmountOfEaters() + "/" + meal.getMaxFellowEaters() + " " + getText(R.string.app_dashboard_button_students) + "\n\n" + meal.getInfo() + "");
        if (meal.getMaxFellowEaters() - meal.getAmountOfEaters() - 1 <= 0) {
            amountOfPeople.setEnabled(false);
            amountOfPeople.setVisibility(View.GONE);
            amountOfPeopleTitle.setVisibility(View.GONE);
        } else {
            amountOfPeople.setHint(getText(R.string.app_joinscreen_chooseamount) + " (max. " + (meal.getMaxFellowEaters() - meal.getAmountOfEaters() - 1) + ")");
        }
        final Button confirmButton = findViewById(R.id.joinscreen_confirmbutton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmJoin();
            }
        });
    }

    private void confirmJoin() {
        if (!amountOfPeople.getText().toString().equals("") && Integer.parseInt(amountOfPeople.getText().toString()) > (meal.getMaxFellowEaters() - meal.getAmountOfEaters() - 1)) {
            amountOfPeople.setError(getText(R.string.app_input_error_tooManyPeople) + "(max." + (meal.getMaxFellowEaters() - meal.getAmountOfEaters() - 1) + ")");
        } else if (!amountOfPeople.getText().toString().equals("") && Integer.parseInt(amountOfPeople.getText().toString()) < 0) {
            amountOfPeople.setError(getText(R.string.app_input_error_notnegative));
        } else {
            MaaltijdenApp app = new MaaltijdenApp(this,this);
            FellowEater fellowEater = new FellowEater();
            if (amountOfPeople.getText().toString().equals("")) {
                fellowEater.setGuests(0);
            } else {
                fellowEater.setGuests(Integer.parseInt(amountOfPeople.getText().toString()));
            }
            fellowEater.setMeal(meal);
            fellowEater.setStudent(app.getUser());
            app.addFellowEater(fellowEater, this);
        }
    }

    @Override
    public void onJoinComplete(boolean result) {
        if (result) {
            Toast.makeText(this, R.string.app_joinmeal_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),MealsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            Toast.makeText(this, getText(R.string.app_error_conn), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void invalidToken() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
