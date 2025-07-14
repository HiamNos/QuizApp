package com.example.quizme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdminAdapter extends BaseAdapter {

    private Context context;
    private List<Question> questions;
    private List<CategoryModel> categories;
    private int selectedPosition = -1;

    public QuestionAdminAdapter(Context context, List<Question> questions, List<CategoryModel> categories) {
        this.context = context;
        this.questions = questions;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Question getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_question_admin, parent, false);
            holder = new ViewHolder();
            holder.questionText = convertView.findViewById(R.id.questionText);
            holder.categoryText = convertView.findViewById(R.id.categoryText);
            holder.selectedIcon = convertView.findViewById(R.id.selectedIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Question question = getItem(position);
        
        // Set question text (truncate if too long)
        String questionText = question.getQuestion();
        if (questionText.length() > 60) {
            questionText = questionText.substring(0, 57) + "...";
        }
        holder.questionText.setText(questionText);

        // Set category text
        String categoryName = getCategoryNameById(question.getCategoryId());
        holder.categoryText.setText("Danh mục: " + categoryName);

        // Set selection state
        boolean isSelected = (position == selectedPosition);
        convertView.setSelected(isSelected);
        holder.selectedIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        return convertView;
    }

    private String getCategoryNameById(int categoryId) {
        for (CategoryModel category : categories) {
            if (category.getCategoryId() == categoryId) {
                return category.getCategoryName();
            }
        }
        return "Không xác định";
    }

    public void toggleSelection(int position) {
        if (position == selectedPosition) {
            // Nếu click vào item đang được chọn thì bỏ chọn
            selectedPosition = -1;
        } else {
            // Nếu click vào item khác thì chọn item mới
            selectedPosition = position;
        }
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public boolean isItemSelected(int position) {
        return position == selectedPosition;
    }

    public void clearSelection() {
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void updateData(List<Question> newQuestions) {
        this.questions = newQuestions;
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView questionText;
        TextView categoryText;
        ImageView selectedIcon;
    }
} 