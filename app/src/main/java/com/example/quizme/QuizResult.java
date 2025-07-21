package com.example.quizme;

public class QuizResult {
    private int resultId;
    private int userId;
    private int categoryId;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private long completionTime; // Thời gian làm bài (giây)
    private String completedAt; // Ngày giờ hoàn thành
    private String categoryName;
    private String categoryImage;

    // Constructors
    public QuizResult() {}

    public QuizResult(int userId, int categoryId, int score, int totalQuestions, 
                     int correctAnswers, long completionTime) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.completionTime = completionTime;
    }

    // Getters and Setters
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    // Helper methods
    public double getAccuracy() {
        if (totalQuestions == 0) return 0.0;
        return (double) correctAnswers / totalQuestions * 100;
    }

    public String getCompletionTimeFormatted() {
        long minutes = completionTime / 60;
        long seconds = completionTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getPerformanceLevel() {
        double accuracy = getAccuracy();
        if (accuracy >= 90) return "Xuất sắc";
        else if (accuracy >= 80) return "Tốt";
        else if (accuracy >= 70) return "Khá";
        else if (accuracy >= 60) return "Trung bình";
        else return "Cần cải thiện";
    }
}
