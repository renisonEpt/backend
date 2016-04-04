package com.renison.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonView;
import com.renison.jackson.View;

@Entity
@Table(name = "student")
public class Student extends BaseModel {
    public static enum Gender {
        FEMALE,
        MALE
    }

    @Column(name = "first_name")
    @JsonView(View.Public.class)
    private String firstName;
    @Column(name = "last_name")
    @JsonView(View.Public.class)
    private String lastName;
    @Column(name = "student_id")
    @JsonView(View.Public.class)
    private String studentId;
    @Column(name = "gender")
    @JsonView(View.Public.class)
    private Gender gender;
    @Column(name = "email")
    @JsonView(View.Public.class)
    private String email;
    @Column(name = "date_of_birth")
    @JsonView(View.Public.class)
    @Temporal(TemporalType.DATE) // have to put it to avoid `trailing junk on timestamp` error
    private Date dateOfBirth;
    @Column(name = "university")
    @JsonView(View.Public.class)
    private String university;
    @Column(name = "currentMajor")
    @JsonView(View.Public.class)
    private String currentMajor;

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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCurrentMajor() {
        return currentMajor;
    }

    public void setCurrentMajor(String currentMajor) {
        this.currentMajor = currentMajor;
    }

}
