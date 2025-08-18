package com.aula.aion.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.R;
import com.aula.aion.model.CalendarDay;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final List<CalendarDay> daysOfMonth;
    private final Context context;

    public CalendarAdapter(Context context, List<CalendarDay> daysOfMonth) {
        this.context = context;
        this.daysOfMonth = daysOfMonth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_day_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = daysOfMonth.get(position);
        holder.dayTextView.setText(day.getDayText());

        // Redefinir o fundo e a cor do texto para o padrão primeiro
        holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_default);
        holder.dayTextView.setTextColor(context.getResources().getColor(R.color.black)); // Preto padrão para o mês atual

        if (!day.isCurrentMonth()) {
            // Escurecer os dias do mês anterior/seguinte
            holder.dayTextView.setTextColor(context.getResources().getColor(R.color.text_gray));
        } else {
            // Aplicar estilo específico para os dias do mês atual
            if (day.isGreenOutline()) {
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_green_outline);
            } else if (day.isRedOutline()) {
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_red_outline);
            } else if (day.isPurpleFill()) {
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_purple_fill);
                holder.dayTextView.setTextColor(context.getResources().getColor(android.R.color.white)); // Texto branco para preenchimento roxo
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}