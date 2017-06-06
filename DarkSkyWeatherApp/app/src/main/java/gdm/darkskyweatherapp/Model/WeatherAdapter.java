package gdm.darkskyweatherapp.Model;



import android.content.Context;
 import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
 import gdm.darkskyweatherapp.ViewModel.Forecast;
import gdm.darkskyweatherapp.R;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Forecast> weatherList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time,summery,icon,tempmin,tempmax;

        public MyViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.time);
            summery = (TextView) view.findViewById(R.id.summary);
            icon = (TextView)view.findViewById(R.id.icon);
            tempmin = (TextView)view.findViewById(R.id.temperatureMin);
            tempmax = (TextView)view.findViewById(R.id.tempreatureMax);

        }
    }


    public WeatherAdapter(ArrayList<Forecast> weatherList, Context context) {
        this.weatherList = weatherList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weatherlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Forecast list = weatherList.get(position);
        String aa = String.valueOf(list.time);
        holder.time.setText(aa);
        holder.summery.setText(list.summary);
        holder.icon.setText(list.icon);
        String tempmini = String.valueOf(list.temperatureMin);
        String tempmaxi = String.valueOf(list.tempreatureMax);
        holder.tempmin.setText(tempmini + " Celsius");
        holder.tempmax.setText(tempmaxi + " Celsius");
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}
