package nl.mheijden.prog3app.model.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import nl.mheijden.prog3app.controller.callbacks.LoginControllerCallback;
import nl.mheijden.prog3app.controller.callbacks.RegisterControllerCallback;
import nl.mheijden.prog3app.controller.callbacks.ReloadCallback;
import nl.mheijden.prog3app.model.Callbacks.APICallbacks;
import nl.mheijden.prog3app.model.data.DAOFactory;
import nl.mheijden.prog3app.model.data.DAOs.DAO;
import nl.mheijden.prog3app.model.data.SQLiteLocalDatabase;
import nl.mheijden.prog3app.model.services.APIServices;

/**
 * Gemaakt door Maarten van der Heijden on 9-1-2018.
 */

public class MaaltijdenApp implements APICallbacks {
    private Context context;
    private APIServices api;
    private DAOFactory daoFactory;
    private LoginControllerCallback loginCallback;
    private ReloadCallback reloadCallback;
    private RegisterControllerCallback registerCallback;
    private String userID;

    public MaaltijdenApp(Context context) {
        this.context = context;
        this.api = new APIServices(context, this);
        this.daoFactory = new DAOFactory(new SQLiteLocalDatabase(context));
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("USERID", "");
    }

    public ArrayList<FellowEater> getFellowEaters() {
        return daoFactory.getFellowEaterDAO().getAll();
    }

    public Student getUser() {
        return daoFactory.getStudentDAO().getOne(Integer.parseInt(userID));
    }

    public ArrayList<Student> getStudents() {
        return daoFactory.getStudentDAO().getAll();
    }

    public ArrayList<Meal> getMeals() {
        return daoFactory.getMealDAO().getAll();
    }

    public void login(Context context, String studentNumber, String password, LoginControllerCallback callback){
        this.loginCallback = callback;
        api.login(context, studentNumber, password);
        userID = studentNumber;
    }

    public void loginCallback(String response){
        Log.i("API",response);
        if(response.equals("error")){
            loginCallback.login(response);
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("userdata",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("APITOKEN",response);
            editor.putString("USERID", userID);
            loginCallback.login("success");
            editor.apply();
        }
    }

    @Override
    public void invalidToken() {

    }

    @Override
    public void addedStudent(boolean result) {
        registerCallback.newStudentAdded(result);
    }

    public void register(Student newStudent, RegisterControllerCallback callback){
        this.registerCallback = callback;
        api.addStudent(newStudent);
    }

    public void reloadData(ReloadCallback callback){
        this.reloadCallback = callback;
        api.getMeals();
        api.getFellowEaters();
        api.getStudents();
    }
    @Override
    public void loadStudents(ArrayList<Student> students) {
        DAO<Student> studentDAO = daoFactory.getStudentDAO();
        studentDAO.clear();
        studentDAO.insertData(students);
        reloadCallback.reloaded(true);
    }

    @Override
    public void loadMeals(ArrayList<Meal> meals) {
        DAO<Meal> mealDAO = daoFactory.getMealDAO();
        mealDAO.clear();
        mealDAO.insertData(meals);
        reloadCallback.reloaded(true);
    }

    @Override
    public void loadFellowEaters(ArrayList<FellowEater> fellowEaters) {
        DAO<FellowEater> fellowEaterDAO = daoFactory.getFellowEaterDAO();
        fellowEaterDAO.clear();
        fellowEaterDAO.insertData(fellowEaters);
        reloadCallback.reloaded(true);
    }
}
