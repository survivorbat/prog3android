package nl.mheijden.prog3app.model.domain;

/**
 * Gemaakt door Maarten van der Heijden on 9-1-2018.
 */

public class FellowEater {
    private int id;
    private Student student;
    private int guests;
    private Meal meal;

    public int getAmount(){
        return ++guests;
    }

    public FellowEater(int id, Student student, int guests, Meal meal) {
        this.id = id;
        this.student = student;
        this.guests = guests;
        this.meal = meal;
    }

    public FellowEater(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }
}