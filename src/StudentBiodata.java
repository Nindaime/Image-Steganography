/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ken8
 */

public class StudentBiodata {

    private String firstName;
    private String lastName; 
    private String faculty;
    private String department;
    private int level;
    private double mealCredit;
    private String matricNumber;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getMealCredit() {
        return mealCredit;
    }

    public void setMealCredit(double mealCredit) {
        this.mealCredit = mealCredit;
    }
    
    public void debitMealCredit( double amount){
    
        this.mealCredit -= amount; 
    
    }
    
    public void creditMealCredit( double amount){
    
        this.mealCredit += amount;
    
    
    }

    public String getMatricNumber() {
        return matricNumber;
    }

    public void setMatricNumber(String matricNumber) {
        this.matricNumber = matricNumber;
    }

    
}
