package hrstal.workoutjournal.models;

public class ExerciseSet {

    private int reps;
    private int weight;

    public void ExerciseSet(int reps, int weight){
        this.reps = reps;
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
