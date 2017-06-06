package gdm.darkskyweatherapp.ViewModel;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class Forecast {
    
    public int time;
    public String summary;
    public String icon;
    public double temperatureMin;
    public double tempreatureMax;

    
    public Forecast(int time, String summary, String icon, double temperatureMin, double temperatureMax) {
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.temperatureMin = temperatureMin;
        this.tempreatureMax = temperatureMax;

    }


}
