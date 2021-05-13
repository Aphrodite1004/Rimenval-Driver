package driver.transporterimenval.com.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import driver.transporterimenval.com.Getset.paymentGetSet;
import driver.transporterimenval.com.R;


public class PaymentAdapter extends BaseAdapter {
    private final ArrayList<paymentGetSet> dat;
    private final Context context;
    private LayoutInflater inflater = null;
    private String orderAmount;

    public PaymentAdapter(ArrayList<paymentGetSet> dat, Context context) {
        this.dat = dat;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
            vi = inflater.inflate(R.layout.cell_payment, parent,false);
        }
        ImageView img_user = vi.findViewById(R.id.img_user);


        TextView txt_orderNo = vi.findViewById(R.id.txt_orderNo);
        TextView txt_paymentDates = vi.findViewById(R.id.txt_PaymentDates);
        String orderNo="";
        String itemdates;
        String time=dat.get(position).getPaymentMaxDate();

        Log.d("jaja",dat.get(position).getComplete());


        if(dat.get(position).getComplete().equals("completed")){

            orderNo="Pago Realizado #"+dat.get(position).getPaymentNo();
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_payment_completed));
            itemdates = "PAGADO";
            txt_paymentDates.setText(itemdates);
            orderAmount = "Monto pagado: "+context.getString(R.string.currency)+dat.get(position).getPaymentAmount();
        }
        if(dat.get(position).getComplete().equals("pending")){
            orderNo="Pago Pendiente #"+dat.get(position).getPaymentNo();
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_payment_pending));
            orderAmount = "Monto a Pagar: "+context.getString(R.string.currency)+dat.get(position).getPaymentAmount();
        }
        if(dat.get(position).getComplete().equals("defeated")){
            orderNo="Pago Vencido #"+dat.get(position).getPaymentNo();
            img_user.setImageDrawable(context.getDrawable(R.drawable.img_payment_defeated));
            itemdates = "VENCIDO";
            txt_paymentDates.setTextColor(Color.RED);
            txt_paymentDates.setText(itemdates);
            orderAmount = "Monto a Pagar: "+context.getString(R.string.currency)+dat.get(position).getPaymentAmount();
        }

        txt_orderNo.setText(orderNo);

        TextView txt_orderAmount = vi.findViewById(R.id.txt_orderAmount);

        txt_orderAmount.setText(orderAmount);

        TextView txt_orderQuantity = vi.findViewById(R.id.txt_orderQuantity);
        String itemNum = dat.get(position).getOrderQuantity()+" Carreras";
        txt_orderQuantity.setText(itemNum);


        TextView txt_orderDateTime = vi.findViewById(R.id.txt_orderDateTime);
        String paymentDate;
        if(dat.get(position).getPaymentDate().equals("null")){
            paymentDate="Vence: "+dat.get(position).getPaymentMaxDate();
        }
        else{
            paymentDate="Generado el: "+dat.get(position).getPaymentDate();
        }

        txt_orderDateTime.setText(paymentDate);

        return vi;
    }


    private void flipImage(Boolean ifTrue, ImageView imageView){
        if(ifTrue)
        {
            imageView.setImageDrawable(context.getDrawable(R.drawable.img_orderdelivered));
        }
        else  imageView.setImageDrawable(context.getDrawable(R.drawable.img_orderprocess));

    }
}
