package com.ldm.practica4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HelpPagerAdapter extends RecyclerView.Adapter<HelpPagerAdapter.HelpViewHolder> {
    private final List<String> helpTexts;
    private final List<Boolean> showTitle;

    public HelpPagerAdapter(List<String> helpTexts, List<Boolean> showTitle) {
        this.helpTexts = helpTexts;
        this.showTitle = showTitle;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_page, parent, false);
        return new HelpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {
        if (showTitle.get(position)) {
            holder.helpTitle.setVisibility(View.VISIBLE);
        } else {
            holder.helpTitle.setVisibility(View.GONE);
        }
        holder.helpTextView.setText(helpTexts.get(position));
    }

    @Override
    public int getItemCount() {
        return helpTexts.size();
    }

    public static class HelpViewHolder extends RecyclerView.ViewHolder {
        TextView helpTitle, helpTextView;

        public HelpViewHolder(@NonNull View itemView) {
            super(itemView);
            helpTitle = itemView.findViewById(R.id.helpTitle);
            helpTextView = itemView.findViewById(R.id.helpTextView);
        }
    }
}