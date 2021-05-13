package driver.transporterimenval.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import driver.transporterimenval.com.Getset.DeliveryGetSet;
import driver.transporterimenval.com.R;
import driver.transporterimenval.com.utils.MyCustomTimer;


public class DeliveryAdapter extends BaseAdapter {
    private final ArrayList<DeliveryGetSet> dat;
    private final Context context;
    private LayoutInflater inflater = null;
    private MyCustomTimer myTimer;

    public DeliveryAdapter(ArrayList<DeliveryGetSet> dat, Context context) {
        this.dat = dat;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myTimer= new MyCustomTimer();
    }

    @Override
    public int getCount() {
        return dat.size();
    }

    @Override
    public Object getItem(int position) {
        return dat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if(convertView==null)
        {
            vi = inflater.inflate(R.layout.celldelivery, parent,false);
        }
        ImageView img_user = vi.findViewById(R.id.img_status);
        TextView txt_order_address = vi.findViewById(R.id.txt_order_address);
        TextView txt_deliveryTime = vi.findViewById(R.id.txt_deliveryAmount);

        if (dat.get(position).getStatus().equals("0")) {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_cus_request));
            txt_deliveryTime.setText(dat.get(position).getOrderDate()+" | "+"Valor de Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery());
            txt_order_address.setText("Dir.: "+dat.get(position).getOrderAddress());
        }
        else if (dat.get(position).getStatus().equals("5")) {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_orderprocess));
            txt_deliveryTime.setText(dat.get(position).getOrderDate()+" | "+"Valor de Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery());
            txt_order_address.setText("Dir.: "+dat.get(position).getOrderAddress());
        }
        else if (dat.get(position).getStatus().equals("1")) {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_orderprepare));
            txt_deliveryTime.setText(dat.get(position).getOrderDate()+" | "+"Valor de Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery());
            txt_order_address.setText("Dir.: "+dat.get(position).getOrderAddress());
        }
        else if (dat.get(position).getStatus().equals("3"))
        {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_orderpicked));
            txt_deliveryTime.setText(dat.get(position).getOrderDate()+" | "+"Valor de Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery());
            txt_order_address.setText("Dir.: "+dat.get(position).getOrderAddress());
        }
        else if(dat.get(position).getStatus().equals("4"))
        {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_orderdelivered));
            txt_order_address.setText("Dir.: "+dat.get(position).getOrderAddress());

            if(dat.get(position).getOrderComision()=="0.00"){
                txt_deliveryTime.setText(dat.get(position).getOrderDate()+" | "+"Valor de Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery());
            }
            else{
                txt_deliveryTime.setText("Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery()+" | Producto $"+dat.get(position).getOrderAmount());

            }

        }
        else if(dat.get(position).getStatus().equals("2") || dat.get(position).getStatus().equals("7"))
        {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_res_rejeact));
            txt_order_address.setText("Dir.: "+dat.get(position).getOrderAddress());
            txt_deliveryTime.setText("Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery()+" | Producto $"+dat.get(position).getOrderAmount());
        }
        else if(dat.get(position).getStatus().equals("6"))
        {
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_res_rejeact));
            txt_deliveryTime.setText("Carrera: "+context.getString(R.string.currency)+dat.get(position).getOrderDelivery()+" | Producto $"+dat.get(position).getOrderAmount());
        }

        TextView txt_orderNo = vi.findViewById(R.id.txt_resName);
        String resName=dat.get(position).getResName();
        txt_orderNo.setText(resName);

        TextView txt_orderAmount = vi.findViewById(R.id.txt_orderAmount);

        String orderAmount = "#"+dat.get(position).getOrderNo()+" | "+"Valor de Orden: "+context.getString(R.string.currency)+dat.get(position).getOrderAmount();
        txt_orderAmount.setText(orderAmount);


        TextView txt_orderTime = vi.findViewById(R.id.txt_orderTime);
        if(dat.get(position).getOrderTime().equals("0")){
            if (dat.get(position).getStatus().equals("4")|| dat.get(position).getStatus().equals("2") || dat.get(position).getStatus().equals("6") ||dat.get(position).getStatus().equals("7"))
            {
                txt_orderAmount.setText("#"+dat.get(position).getOrderNo() +" | "+dat.get(position).getOrderDate());
            }

        }
        else{
            myTimer.setTimer(Integer.parseInt(dat.get(position).getOrderTime()),txt_orderTime);
        }

        return vi;
    }

}
