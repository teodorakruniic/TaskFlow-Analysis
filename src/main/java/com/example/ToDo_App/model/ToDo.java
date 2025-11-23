package com.example.ToDo_App.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "todo")
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nonnull
    private Long id;

    @Column
    @Nonnull
    private String title;

    @Column
    @Nonnull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Column
    @Nonnull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @Column
    @Nonnull
    private String status;

    public ToDo() {
        this.status = "ToDo";
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //reports
    @Column
    private Date doingStartTime;

    @Column
    private Date doneTime;

    public Date getDoingStartTime() {
        return doingStartTime;
    }

    public void setDoingStartTime(Date doingStartTime) {
        this.doingStartTime = doingStartTime;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }



    @Column
    private String repeatFrequency; // Options like "Daily", "Weekly", "Monthly", etc.

    @Column
    private Date nextDueDate;

    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(String repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public Date getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Date nextDueDate) {
        this.nextDueDate = nextDueDate;
    }


}
