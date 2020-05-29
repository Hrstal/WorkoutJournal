package hrstal.workoutjournal.models;

import java.util.List;

public class ExercisePlan {

    private int id;
    private String name;
    private List<ExerciseSet> sets;

    public void ExercisePlan(String name){
        this.name = name;
    }

    public ExercisePlan addSet(ExerciseSet exerciseSet){
        sets.add(exerciseSet);
        return this;
    }

    public int getSetsCount(){
        return sets.size();
    }

}
