package com.budgeteer.api.model;

import javax.persistence.*;

@Entity
@Table(name = "goal_types")
public class GoalType {

    @Id
    private Long id;

    private String name;

    public GoalType() {
    }

    public GoalType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
