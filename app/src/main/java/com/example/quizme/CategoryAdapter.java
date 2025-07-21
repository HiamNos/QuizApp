package com.example.quizme;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category,null);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        final CategoryModel model = categoryModels.get(position);

        holder.textView.setText(model.getCategoryName());
        
        // Xử lý hình ảnh - kiểm tra xem có phải là URL, file path, hay tên drawable
        String imagePath = model.getCategoryImage();
        System.out.println("Loading image for category: " + model.getCategoryName() + " with path: " + imagePath);
        
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("http")) {
                // Nếu là URL, sử dụng Glide
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.quiz_icon)
                        .error(R.drawable.quiz_icon)
                        .into(holder.imageView);
                System.out.println("Loading from URL: " + imagePath);
            } else if (imagePath.startsWith("/") || imagePath.contains("/storage/") || imagePath.contains("/sdcard/")) {
                // Nếu là đường dẫn file local, sử dụng Glide để load file
                Glide.with(context)
                        .load(new java.io.File(imagePath))
                        .placeholder(R.drawable.quiz_icon)
                        .error(R.drawable.quiz_icon)
                        .into(holder.imageView);
                System.out.println("Loading from file path: " + imagePath);
            } else {
                // Nếu là tên drawable, sử dụng Resource ID
                try {
                    int resourceId = context.getResources().getIdentifier(imagePath, "drawable", context.getPackageName());
                    System.out.println("Resource ID for " + imagePath + ": " + resourceId);
                    if (resourceId != 0) {
                        holder.imageView.setImageResource(resourceId);
                        System.out.println("Successfully loaded drawable: " + imagePath);
                    } else {
                        holder.imageView.setImageResource(R.drawable.quiz_icon);
                        System.out.println("Failed to find drawable: " + imagePath + ", using default");
                    }
                } catch (Exception e) {
                    holder.imageView.setImageResource(R.drawable.quiz_icon);
                    System.out.println("Error loading drawable " + imagePath + ": " + e.getMessage());
                }
            }
        } else {
            holder.imageView.setImageResource(R.drawable.quiz_icon);
            System.out.println("No image path provided, using default");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra("catId", model.getCategoryId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.category);
        }
    }
}
