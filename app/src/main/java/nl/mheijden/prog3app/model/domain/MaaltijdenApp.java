package nl.mheijden.prog3app.model.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import nl.mheijden.prog3app.controllers.callbacks.DeleteMealControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.JoinControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.LeaveControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.LoginControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.NewMealControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.RegisterControllerCallback;
import nl.mheijden.prog3app.controllers.callbacks.ReloadCallback;
import nl.mheijden.prog3app.model.Callbacks.APICallbacks;
import nl.mheijden.prog3app.model.data.DAOFactory;
import nl.mheijden.prog3app.model.data.DAOs.DAO;
import nl.mheijden.prog3app.model.data.SQLiteLocalDatabase;
import nl.mheijden.prog3app.model.services.APIServices;

/**
 * Gemaakt door Maarten van der Heijden on 9-1-2018.
 */

public class MaaltijdenApp implements APICallbacks {
    /**
     * Context of the activity
     */
    private Context context;
    /**
     * APIServices object in order to retrieve data
     */
    private APIServices api;
    /**
     * DAOfactory where the application will get its database access from
     */
    private DAOFactory daoFactory;
    /**
     * Logincallback that sends ot the activity wether the login was successfully or not
     */
    private LoginControllerCallback loginCallback;
    private ReloadCallback reloadCallback;
    private RegisterControllerCallback registerCallback;
    private JoinControllerCallback joinControllerCallback;
    private LeaveControllerCallback leaveControllerCallback;
    private NewMealControllerCallback newMealControllerCallback;
    private DeleteMealControllerCallback deleteMealControllerCallback;
    private String userID;

    /**
     * @param context of the current activity
     */
    public MaaltijdenApp(Context context) {
        this.context = context;
        this.daoFactory = new DAOFactory(new SQLiteLocalDatabase(context));
        this.api = new APIServices(context, this);
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("USERID", "");
    }

    /**
     * @return the user that is currently using the application
     */
    public Student getUser() {
        return daoFactory.getStudentDAO().getOne(Integer.parseInt(userID));
    }

    /**
     * @return all the students from the database
     */
    public ArrayList<Student> getStudents() {
        return daoFactory.getStudentDAO().getAll();
    }

    /**
     * @return all the meals from the database
     */
    public ArrayList<Meal> getMeals() {
        return daoFactory.getMealDAO().getAll();
    }

    /**
     * @param studentNumber that identifies the student
     * @param password that verifies the student
     * @param callback that is used in order to let the activity know what's up
     */
    public void login(String studentNumber, String password, LoginControllerCallback callback){
        this.loginCallback = callback;
        api.login(studentNumber, password);
        userID = studentNumber;
    }

    /**
     * @param response dictates what the response of the server was concerning the login attempt
     */
    public void loginCallback(String response){
        Log.i("API",response);
        if(response.equals("errorconn") || response.equals("errorwrong") || response.equals("errorobj")){
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

    /**
     * @param fellowEater that needs to be added
     * @param joinControllerCallback that let's the controller know what's up
     */
    public void addFellowEater(FellowEater fellowEater, JoinControllerCallback joinControllerCallback){
        this.joinControllerCallback=joinControllerCallback;
        api.addFellowEater(fellowEater);
    }

    /**
     * Called in case the token has expired
     */
    @Override
    public void invalidToken() {

    }

    /**
     * @param result dictates whether the student was added or not
     */
    @Override
    public void addedStudent(boolean result) {
        registerCallback.newStudentAdded(result);
    }

    /**
     * @param result dictates wether the felloweater was removed succesfully or not
     */
    @Override
    public void removedFellowEater(boolean result) {
        leaveControllerCallback.onLeaveComplete(result);
    }

    /**
     * @param meal  that needs to be added
     * @param newMealControllerCallback is stored in order to let the activity know what's up
     */
    public void addMeal(Meal meal, NewMealControllerCallback newMealControllerCallback){
        this.newMealControllerCallback=newMealControllerCallback;
        api.addMaaltijd(meal);
    }

    /**
     * @param result dictates wether the felloweater was added succesfully or not
     */
    @Override
    public void addedFellowEater(boolean result) {
        joinControllerCallback.onJoinComplete(result);
    }

    /**
     * @param meal that needs to be deleted
     * @param deleteMealControllerCallback that is stored in order to let the activity know what's up
     */
    public void deleteMeal(Meal meal, DeleteMealControllerCallback deleteMealControllerCallback){
        this.deleteMealControllerCallback=deleteMealControllerCallback;
        api.deleteMeal(meal);
    }

    /**
     * @param fellowEater that needs to be deleted
     * @param leaveControllerCallback that is stored to let the activity know what's up
     */
    public void deleteFellowEater(FellowEater fellowEater, LeaveControllerCallback leaveControllerCallback){
        this.leaveControllerCallback = leaveControllerCallback;
        api.deleteFellowEater(fellowEater);
    }

    /**
     * @param newStudent that needs to be added
     * @param callback that is stored to let the activity know what's up
     */
    public void register(Student newStudent, RegisterControllerCallback callback){
        this.registerCallback = callback;
        api.addStudent(newStudent);
    }

    /**
     * @param callback for the activity
     */
    public void reloadStudents(ReloadCallback callback){
        this.reloadCallback=callback;
        api.getStudents();
    }

    /**
     * @param callback for the activity
     */
    public void reloadMeals(ReloadCallback callback){
        this.reloadCallback=callback;
        api.getMeals();
        api.getFellowEaters();
    }

    /**
     * @param students that need to be added to the application
     */
    @Override
    public void loadStudents(ArrayList<Student> students) {
        DAO<Student> studentDAO = daoFactory.getStudentDAO();
        studentDAO.clear();
        studentDAO.insertData(students);
        reloadCallback.reloaded(true);
    }

    /**
     * @param meals to be inserted into the application
     */
    @Override
    public void loadMeals(ArrayList<Meal> meals) {
        DAO<Meal> mealDAO = daoFactory.getMealDAO();
        mealDAO.clear();
        mealDAO.insertData(meals);
        reloadCallback.reloaded(true);
    }

    /**
     * @param result dictates whether the meal was added succesfully or not
     */
    @Override
    public void addedMeal(boolean result) {
        newMealControllerCallback.addedMeal(result);
    }

    /**
     * @param result dictates whether meal was removed sucesfully or not
     */
    @Override
    public void removedMeal(boolean result) {
        deleteMealControllerCallback.onDeleteMealComplete(result);
    }

    /**
     * @param fellowEaters that need  to be added to the application
     */
    @Override
    public void loadFellowEaters(ArrayList<FellowEater> fellowEaters) {
        DAO<FellowEater> fellowEaterDAO = daoFactory.getFellowEaterDAO();
        fellowEaterDAO.clear();
        fellowEaterDAO.insertData(fellowEaters);
        reloadCallback.reloaded(true);
    }
}
