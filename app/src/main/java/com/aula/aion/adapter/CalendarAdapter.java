package com.aula.aion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.aion.R;
import com.aula.aion.model.RelatorioPresenca;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    public interface OnDayClickListener {
        void onTodayClick();
        void onAbsentDayClick(LocalDate date, RelatorioPresenca presenca);
    }

    public static class CalendarDay {
        private String dayText;
        private boolean isCurrentMonth;
        private LocalDate dataDia;

        public CalendarDay(String dayText, boolean isCurrentMonth, LocalDate dataDia) {
            this.dayText = dayText;
            this.isCurrentMonth = isCurrentMonth;
            this.dataDia = dataDia;
        }

        public String getDayText() {
            return dayText;
        }

        public boolean isCurrentMonth() {
            return isCurrentMonth;
        }

        public LocalDate getDataDia() {
            return dataDia;
        }
    }

    private final List<CalendarDay> daysOfMonth;
    private final Context context;
    private final Map<LocalDate, RelatorioPresenca> presencaMap;
    private OnDayClickListener clickListener;

    public CalendarAdapter(Context context, List<CalendarDay> daysOfMonth, List<RelatorioPresenca> relatorioPresenca) {
        this.context = context;
        this.daysOfMonth = daysOfMonth;
        this.presencaMap = new HashMap<>();

        // Mapear RelatorioPresenca por data para acesso rápido
        if (relatorioPresenca != null) {
            for (RelatorioPresenca relatorio : relatorioPresenca) {
                presencaMap.put(relatorio.getDataDia(), relatorio);
            }
        }
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.clickListener = listener;
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

        if (!day.isCurrentMonth()) {
            // Escurecer os dias do mês anterior/seguinte
            holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_default);
            holder.dayTextView.setTextColor(context.getResources().getColor(R.color.light_gray));
            holder.itemView.setOnClickListener(null);
        } else {
            holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_default);
            holder.dayTextView.setTextColor(context.getResources().getColor(R.color.black));

            if (day.getDataDia().isEqual(LocalDate.now())) {
                // Dia de hoje - fundo roxo
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_purple_fill);
                holder.dayTextView.setTextColor(context.getResources().getColor(android.R.color.white));

                // Clique no dia de hoje - abre BottomSheet
                holder.itemView.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onTodayClick();
                    }
                });
            } else {
                RelatorioPresenca presenca = presencaMap.get(day.getDataDia());
                if (presenca != null) {
                    applyStatusStyle(holder, presenca.getStatusDia());

                    // Clique em dias com status vermelho (4) ou amarelo (3)
                    if (presenca.getStatusDia() == 3 || presenca.getStatusDia() == 4) {
                        holder.itemView.setOnClickListener(v -> {
                            if (clickListener != null) {
                                clickListener.onAbsentDayClick(day.getDataDia(), presenca);
                            }
                        });
                    } else {
                        holder.itemView.setOnClickListener(null);
                    }
                } else {
                    holder.itemView.setOnClickListener(null);
                }
            }
        }
    }

    private void applyStatusStyle(@NonNull CalendarViewHolder holder, Integer statusDia) {
        if (statusDia == null) {
            return;
        }

        switch (statusDia) {
            case 1: // Faltou
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_default);
                holder.dayTextView.setTextColor(context.getResources().getColor(R.color.text_gray));
                break;
            case 2: // Presença
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_green_outline);
                holder.dayTextView.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case 3: // Parcial (Amarelo)
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_yellow_outline);
                holder.dayTextView.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case 4: // Ausente (Vermelho)
                holder.dayTextView.setBackgroundResource(R.drawable.calendar_day_red_outline);
                holder.dayTextView.setTextColor(context.getResources().getColor(R.color.black));
                break;
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