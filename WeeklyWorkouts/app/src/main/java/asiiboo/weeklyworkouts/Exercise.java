package asiiboo.weeklyworkouts;

import java.io.Serializable;

import static asiiboo.weeklyworkouts.Status.IN_PROGRESS;

class Exercise implements Serializable {
    private String name;
    private String description;
    private int set;
    private int rep;
    private Status status;

    Exercise(String name) {
        this.name = name;
        this.status = IN_PROGRESS;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    Status getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    void setDescription(String str) {
        this.description = str;
    }

    void setSet(int n) {
        this.set = n;
    }

    void setRep(int n) {
        this.rep = n;
    }

    int getSet() {
        return this.set;
    }

    int getRep() {
        return this.rep;
    }

    @Override
    public boolean equals(Object o) {
        return name.equals(((Exercise)o).getName());
    }
}
