package com.emi.systemconfiguration;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisteredCustDetail_Fragment extends Fragment {

    TextView title, MD_username, MD_registerno, MD_currentphoneno, MD_mailID, MD_emidate, MD_downpayment, MD_emiamount;
    TextView MD_emitenure, MD_financecompany, MD_deviceaname, MD_deviceamount;

    ImageView MD_custphoto;
    String Cust_detailAPI = "http://goelectronix.in/api/app/CustomerDetails";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_cust_detail_, container, false);

        title = view.findViewById(R.id.title);
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(RegisteredCustDetail_Fragment.this);
                return false;
            }
        });
        MD_username = view.findViewById(R.id.MD_username);
        MD_registerno = view.findViewById(R.id.MD_registerno);
        MD_currentphoneno = view.findViewById(R.id.MD_currentphoneno);
        MD_mailID = view.findViewById(R.id.MD_mailID);
        MD_emidate = view.findViewById(R.id.MD_emidate);
        MD_downpayment = view.findViewById(R.id.MD_downpayment);
        MD_emiamount = view.findViewById(R.id.MD_emiamount);
        MD_emitenure = view.findViewById(R.id.MD_emitenure);
        MD_financecompany = view.findViewById(R.id.MD_financecompany);
        MD_deviceaname = view.findViewById(R.id.MD_deviceaname);
        MD_custphoto = view.findViewById(R.id.MD_custphoto);
        MD_deviceamount = view.findViewById(R.id.MD_deviceamount);

        getCustDetails();

        return view;
    }

    private void getCustDetails() {
        JSONObject params = new JSONObject();

        preferences = getContext().getSharedPreferences("EMILOCKER", MODE_PRIVATE);
        editor = preferences.edit();
        String local_serialno = preferences.getString("SerialNo", "");

        //get value from local database from login API
        try {
            params.put("SerialNumber", MainActivity.getDeviceId(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Cust_detailAPI, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getBoolean("success") == true) {

                        //get and set all the values from API
                        MD_username.setText(response.getString("customerName"));
                        MD_registerno.setText(response.getString("mobileNumber"));
                        MD_mailID.setText(response.getString("emailID"));
                        MD_deviceaname.setText(response.getString("mobileBrand"));
                        MD_downpayment.setText(response.getString("downPayment"));
                        MD_emiamount.setText(response.getString("emiAmount"));
                        MD_financecompany.setText(response.getString("financiarName"));
                        MD_deviceamount.setText(response.getString("deviceAmount"));
                        MD_emidate.setText(response.getString("emiDate").split("T")[0]);
                        MD_emitenure.setText(response.getString("emiTenure"));
                        editor.putString("customerName",response.getString("customerName"));
                        editor.putString("mobileNumber",response.getString("mobileNumber"));
                        editor.putString("emailID",response.getString("emailID"));
                        editor.putString("mobileBrand",response.getString("mobileBrand"));
                        editor.putString("downPayment",response.getString("downPayment"));
                        editor.putString("emiAmount",response.getString("emiAmount"));
                        editor.putString("financiarName",response.getString("financiarName"));
                        editor.putString("deviceAmount",response.getString("deviceAmount"));
                        editor.putString("emiDate",response.getString("emiDate").split("T")[0]);
                        editor.putString("emiTenure",response.getString("emiTenure"));
                        editor.commit();
                        String photourl = response.getString("photoURL");
                        if (!photourl.equals("null") && !photourl.equals(""))
                            Glide.with(getContext()).load(photourl).into(MD_custphoto);
                    } else {
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("Data", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());
            }
        });


        Volley.newRequestQueue(getContext()).add(objectRequest);

    }
}