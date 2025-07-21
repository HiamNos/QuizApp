package com.example.quizme;

public class QuizStats {
    private int totalQuizzes;
    private float averageScore;
    private int bestScore;
    private int totalCorrectAnswers;
    private int totalQuestions;

    // Constructors
    public QuizStats() {}

    // Getters and Setters
    public int getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(int totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    // Helper methods
    public double getOverallAccuracy() {
        if (totalQuestions == 0) return 0.0;
        return (double) totalCorrectAnswers / totalQuestions * 100;
    }

    public String getOverallPerformanceLevel() {
        double accuracy = getOverallAccuracy();
        if (accuracy >= 90) return "Xuất sắc";
        else if (accuracy >= 80) return "Tốt";
        else if (accuracy >= 70) return "Khá";
        else if (accuracy >= 60) return "Trung bình";
        else return "Cần cải thiện";
    }
}
