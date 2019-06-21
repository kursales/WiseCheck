package com.example.wisecheck;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecycleTurnAdapter extends RecyclerView.Adapter<RecycleTurnAdapter.ViewHolder>  {
    ArrayList<Employee> employees = new ArrayList<Employee>();
    private OnUserClickListener onUserClickListener;

    RecycleTurnAdapter(ArrayList<Employee> employees, OnUserClickListener onUserClickListener){
        this.employees.addAll(employees);
        this.onUserClickListener = onUserClickListener;
    }
    public  class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFio, tvBday, tvProf;
        ImageView imageView;
        ViewHolder(View itemView)
        {
            super(itemView);
            tvFio=itemView.findViewById(R.id.tv_fio);
            tvBday=itemView.findViewById(R.id.tv_birthday);
            tvProf=itemView.findViewById(R.id.tv_profession);
            imageView=itemView.findViewById(R.id.Image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Employee employee=employees.get(getLayoutPosition());
                    onUserClickListener.onUserClick(employee);
                }
            });
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ViewHolder pvh = new ViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvFio.setText(employees.get(i).getName());
        viewHolder.tvBday.setText(employees.get(i).getBirthday());
        viewHolder.tvProf.setText(employees.get(i).getProfession());
        viewHolder.imageView.setImageDrawable(Drawable.createFromPath(employees.get(i).getPathToFile()));

    }
    @Override
    public int getItemCount() {
        return employees.size();
    }

    public interface OnUserClickListener {
        void onUserClick(Employee employee);
    }
}