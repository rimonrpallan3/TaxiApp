package metrotaxi.project.in.metrotaxi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.TripDetailsModel;


public class DriverTripHistoryAdapter extends RecyclerView.Adapter<DriverTripHistoryAdapter.MyViewHolder>{
    List<TripDetailsModel> tripDetailsModelList;
    public DriverTripHistoryAdapter(List<TripDetailsModel> tripDetailsModelList) {
        this.tripDetailsModelList = tripDetailsModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_trip_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TripDetailsModel tripDetailsModel = tripDetailsModelList.get(position);
        holder.cashTextView.setText("Rs." + tripDetailsModel.getCash());
        holder.dateTextView.setText(tripDetailsModel.getDate() + " at " + tripDetailsModel.getTime());
        holder.userTextView.setText("User: " + tripDetailsModel.getUser());
       /// holder.statusTextView.setText(tripDetailsModel.getStatus());
        holder.fromTextView.setText("From: " + tripDetailsModel.getFrom());
        holder.toTextView.setText("To: " + tripDetailsModel.getTo());
    }

    @Override
    public int getItemCount() {
        return tripDetailsModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView fromTextView;
        TextView toTextView;
        TextView userTextView;
        TextView cashTextView;
        TextView statusTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.textViewDate);
            fromTextView = (TextView) itemView.findViewById(R.id.textViewFrom);
            toTextView = (TextView) itemView.findViewById(R.id.textViewTo);
            userTextView = (TextView) itemView.findViewById(R.id.textViewUser);
            cashTextView = (TextView) itemView.findViewById(R.id.textViewCash);
            statusTextView = (TextView) itemView.findViewById(R.id.textViewStatus);
        }
    }
}
