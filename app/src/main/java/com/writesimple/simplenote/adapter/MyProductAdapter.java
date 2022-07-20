package com.writesimple.simplenote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.writesimple.simplenote.BillingModels.IRecyclerClickListener;
import com.writesimple.simplenote.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {

    AppCompatActivity appCompatActivity;
    List<SkuDetails> skuDetailsList;

    public MyProductAdapter(AppCompatActivity appCompatActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.appCompatActivity = appCompatActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    BillingClient billingClient;

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext())
                .inflate(R.layout.layout_product_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyProductAdapter.MyViewHolder holder, int position) {
        holder.txt_product_name.setText(skuDetailsList.get(position).getTitle());
        holder.txt_desc.setText(skuDetailsList.get(position).getDescription());
        holder.txt_price.setText(skuDetailsList.get(position).getPrice());

        holder.setListener((view, position1) -> {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList.get(position1))
                    .build();

            int response = billingClient.launchBillingFlow(appCompatActivity,billingFlowParams)
                    .getResponseCode();
            switch (response){
                case  BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                    Toast.makeText(appCompatActivity,"ВЫСТАВЛЕНИЕ СЧЕТОВ НЕДОСТУПНО",Toast.LENGTH_SHORT).show();
                    break;
                case  BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                    Toast.makeText(appCompatActivity,"ОШИБКА РАЗРАБОТЧИКА",Toast.LENGTH_SHORT).show();
                    break;
                case  BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                    Toast.makeText(appCompatActivity,"ФУНКЦИЯ НЕ ПОДДЕРЖИВАЕТСЯ",Toast.LENGTH_SHORT).show();
                    break;
                case  BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    Toast.makeText(appCompatActivity,"ПРЕДМЕТ, КОТОРЫЙ УЖЕ ПРИНАДЛЕЖИТ",Toast.LENGTH_SHORT).show();
                    break;
                case  BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                    Toast.makeText(appCompatActivity,"СЛУЖБА ОТКЛЮЧЕНА",Toast.LENGTH_SHORT).show();
                    break;
                case  BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                    Toast.makeText(appCompatActivity,"ВРЕМЯ ОЖИДАНИЯ ОБСЛУЖИВАНИЯ",Toast.LENGTH_SHORT).show();
                    break;
                case  BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                    Toast.makeText(appCompatActivity,"ЭЛЕМЕНТ НЕДОСТУПЕН",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_product_name,txt_desc,txt_price;
        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener){
            this.listener = listener;
        }
        public MyViewHolder(@NonNull View view){
            super(view);
            txt_product_name = (TextView) view.findViewById(R.id.txt_product_name);
            txt_desc = (TextView) view.findViewById(R.id.txt_desc);
            txt_price = (TextView) view.findViewById(R.id.txt_price);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition());
        }
    }
}
