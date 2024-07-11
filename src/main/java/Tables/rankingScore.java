package Tables;

public class rankingScore {
    private String lrn;
    private String adviserId;
    private String name;
    private int totalScore;

    public rankingScore(String lrn, String adviserId, String name, int totalScore) {
        this.lrn = lrn;
        this.adviserId = adviserId;
        this.name = name;
        this.totalScore = totalScore;
    }

    // Getters and setters
    public String getLrn() {
        return lrn;
    }

    public void setLrn(String lrn) {
        this.lrn = lrn;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
