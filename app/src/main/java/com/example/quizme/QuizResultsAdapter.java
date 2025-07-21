package com.example.quizme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizResultsAdapter extends RecyclerView.Adapter<QuizResultsAdapter.ViewHolder> {

    private Context context;
    private List<QuizResult> results;

    public QuizResultsAdapter(Context context, List<QuizResult> results) {
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_quiz_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResult result = results.get(position);

        holder.categoryName.setText(result.getCategoryName());
        holder.scoreText.setText(result.getScore() + " điểm");
        holder.accuracyText.setText(result.getCorrectAnswers() + "/" + result.getTotalQuestions() + 
                                   " (" + String.format("%.1f%%", result.getAccuracy()) + ")");
        holder.timeText.setText(result.getCompletionTimeFormatted());
        holder.performanceText.setText(result.getPerformanceLevel());
        holder.dateText.setText(formatDate(result.getCompletedAt()));

        // Set category icon
        setCategoryIcon(holder.categoryIcon, result.getCategoryImage());

        // Set performance color
        setPerformanceColor(holder.performanceText, result.getAccuracy());
    }

    private void setCategoryIcon(ImageView icon, String categoryImage) {
        int resourceId = context.getResources().getIdentifier(categoryImage, "drawable", context.getPackageName());
        if (resourceId != 0) {
            icon.setImageResource(resourceId);
        } else {
            icon.setImageResource(R.drawable.avatar); // Default icon
        }
    }

    private void setPerformanceColor(TextView textView, double accuracy) {
        if (accuracy >= 90) {
            textView.setTextColor(context.getResources().getColor(R.color.white));
        } else if (accuracy >= 80) {
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
        } else if (accuracy >= 70) {
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
        } else if (accuracy >= 60) {
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
        }
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryName, scoreText, accuracyText, timeText, performanceText, dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryName = itemView.findViewById(R.id.categoryName);
            scoreText = itemView.findViewById(R.id.scoreText);
            accuracyText = itemView.findViewById(R.id.accuracyText);
            timeText = itemView.findViewById(R.id.timeText);
            performanceText = itemView.findViewById(R.id.performanceText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}
