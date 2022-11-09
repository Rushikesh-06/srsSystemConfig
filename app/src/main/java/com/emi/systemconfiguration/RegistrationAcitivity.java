package com.emi.systemconfiguration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class RegistrationAcitivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private  String TAG = getClass().getSimpleName();
    String prevStarted = "yes";

    private DatePicker datePicker;
    private Calendar calendar;
    private SharedPreferences sharedPreferences;
    private TextView dateView, endDateView, spinner;
    private int year, month, day;

    private CircleImageView img_profile;
    private FloatingActionButton btn_click;
    private final int CAMERA_REQ_CODE = 101;
//    public static final int PICK_IMAGE = 1;


//    private static final int PICK_IMAGE_REQUEST =1 ;

    private static final String ROOT_URL = "http://goelectronix.in/api/app/UploadFile";


    // Firebase auth
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    // creating variables for our edit text
    private EditText policyId, device_amount, customer_uidEdit, customer_nameEdit, customer_contactEdit,
            customer_emailEdit, customer_mobile_brandEdit, customer_paymentEdit, customer_loanEdit, etdownpayment, etemitenure;

    // creating variable for button
    private Button registerBtn;

    boolean vendorFound = false;

    boolean isAllFieldsChecked = false, planValid = false;

    // creating a strings for storing
    // our values from edittext fields.
    private String plan, policy_no, customer_uid, customer_name, customer_contact, customer_email,
            customer_mobile_brand, customer_payment, customer_loan, VendorID, PolicyNo, startDate, endDate, amount, downpayment, emi_tenure, photo;

    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;

    Spinner spinnerPlan, spinner2;

    CheckBox planView, loanView;

    List<String> policyDocID = new ArrayList<String>();

    List<Map<String, Object>> userData = new ArrayList<java.util.Map<String, Object>>();

    private final String filename = "q1w2e3r4t5y6u7i8o9p0.jpg";

    Spinner selectpolicy;
    List<String> policylist=new ArrayList<>();
//    String[] policylist = {"Select Policy","ABC001", "ABC002", "ABC003", "ABC004", "ABC005", "ABC006"};

    TextView emi_date;
    private int mYear, mMonth, mDay;

    ImageView verify_icon;
    LinearLayout vendordetail_layout;
    private boolean verify_vendor = false;
    EditText et_vendorcode;

    TextView vendorName , vendorShopName, vendorContact;

    //FCM Token
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_acitivity);

        vendorName = findViewById(R.id.vendorName);
        vendorShopName = findViewById(R.id.vendorShopName);
        vendorContact = findViewById(R.id.vendorContact);

        verify_icon = findViewById(R.id.verify_icon);
        et_vendorcode = findViewById(R.id.et_vendorcode);
        vendordetail_layout = findViewById(R.id.vendordetail_layout);


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult().getToken();
                    Log.i("FCM TOKEN", token);
                }
            }
        });



        emi_date = findViewById(R.id.emi_date);
        img_profile = findViewById(R.id.img_profile);
        btn_click = findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegistrationAcitivity.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(icamera, CAMERA_REQ_CODE);
                }else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA},111);
                }

            }
        });






        // verify vendor code
        verify_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject vendorcode = new JSONObject();

                ProgressDialog dialog = new ProgressDialog(RegistrationAcitivity.this);
                dialog.setMessage("Please Wait...");
                dialog.setCancelable(false);
                dialog.show();

                String vendor_code = et_vendorcode.getText().toString();
                try {
                     vendorcode = new JSONObject().put("VendorCode",vendor_code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, "http://goelectronix.in/api/app/VendorPolicyDetails", vendorcode, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG,response.toString());
                        dialog.dismiss();

                        try {
                            if (response.getBoolean("success")==true){
                                vendordetail_layout.setVisibility(View.VISIBLE);
                                vendorName.setText(response.getString("vendorName"));
                                vendorShopName.setText(response.getString("shopName"));
                                vendorContact.setText(response.getString("vendorPhoneNumber"));

                                JSONArray policies = response.getJSONArray("policies");
                                for (int i = 0;i<policies.length();i++){
                                   Log.e(TAG,policies.getJSONObject(i).toString());

                                   JSONObject object = policies.getJSONObject(i);
                                   Log.e(TAG,"POLICY ID: " +object.getString("policyID"));
                                   Log.e(TAG,object.getString("policyNumber"));

                                    policylist.add(object.getString("policyID"));

                                }


                            }else{
                                vendordetail_layout.setVisibility(View.GONE );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       dialog.dismiss();
                        Log.e(TAG,error.toString());
                    }
                });

                Volley.newRequestQueue(RegistrationAcitivity.this).add(objectRequest);


                /*if (verify_vendor){
                    verify_vendor = false;
                    vendordetail_layout.setVisibility(View.VISIBLE);
                }else{
                    verify_vendor= true;
                    vendordetail_layout.setVisibility(View.GONE );
                }*/
            }
        });



        //select policy drop down list
        selectpolicy = findViewById(R.id.selectpolicy);
        policylist.add("Select policy");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrationAcitivity.this, android.R.layout.simple_spinner_dropdown_item, policylist);
        selectpolicy.setAdapter(adapter);


        //Emi date

        emi_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationAcitivity.this, new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                emi_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        etdownpayment = findViewById(R.id.downpayment);
        etemitenure = findViewById(R.id.emi_tenure);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Emi-Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.system_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar

        // Calender View
        dateView = (TextView) findViewById(R.id.dateTextView);
        dateView.setEnabled(false);
        endDateView = (TextView) findViewById(R.id.endDateView);
        endDateView.setVisibility(View.GONE);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        dateView.setText("Start Date : " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()));

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // getting our instance
        // from Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        // Fetch All the Policy Id
        getPolicyIdList(getApplicationContext());

        // Progress Bar
        progressbar = findViewById(R.id.progressbar);

        // Spinner brand
        spinner = findViewById(R.id.spinner1);
        spinner.setText(Build.MANUFACTURER.toUpperCase(Locale.ROOT));
        this.customer_mobile_brand = Build.MANUFACTURER.toUpperCase(Locale.ROOT);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.brands,
//                R.layout.color_spinner_layout);
//        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);


        // Spinner Loan
        spinner2 = findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.loan,
                R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);
        spinner2.setVisibility(View.VISIBLE);

        // Spinner Plan
        spinnerPlan = findViewById(R.id.spinnerPlan);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.plans,
                R.layout.color_spinner_layout);
        adapter3.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinnerPlan.setAdapter(adapter3);
        spinnerPlan.setOnItemSelectedListener(this);
        spinnerPlan.setVisibility(View.GONE);

        loanView = (CheckBox) findViewById(R.id.loanBox);
        loanView.setVisibility(View.GONE);

        device_amount = (EditText) findViewById(R.id.amount);

        loanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CompoundButton) view).isChecked()) {
                    spinner2.setVisibility(View.VISIBLE);
                    planValid = true;
                } else {
                    spinner2.setVisibility(View.GONE);

                    device_amount.setVisibility(View.VISIBLE);
                }
            }
        });

        // initializing our edittext and buttons
        customer_nameEdit = findViewById(R.id.customerName);
        customer_emailEdit = findViewById(R.id.customerMail);
        customer_contactEdit = findViewById(R.id.customerNumber);
        registerBtn = findViewById(R.id.registerBtn);

        // adding on click listener for button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);

                isAllFieldsChecked = CheckAllFields();

                // getting data from edittext fields
                customer_name = customer_nameEdit.getText().toString();
                customer_contact = customer_contactEdit.getText().toString();
                customer_email = customer_emailEdit.getText().toString();

                if (isAllFieldsChecked) {
                    if (VendorID.length() > 0) {
                        registerNewUser();
                    } else {
                        toastMessage("Policy is empty");
                    }
                    // toastMessage("done workin");
                }
            }
        });

        policyId = (EditText) findViewById(R.id.policyId);

        policyId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PolicyNo = s.toString();
                if (s.length() >= 1) {
                    getvendorId(s.toString().toUpperCase());
                    policy_no = s.toString().toUpperCase();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // int size= s.length();
                // toastMessage(String.valueOf(s.length()));
                // PolicyNo = s.toString();
                // if(s.length() >= 1){
                // getvendorId(s.toString().toUpperCase());
                // policy_no = s.toString().toUpperCase();
                //// toastMessage(s.toString());
                // }
                // getvendorId(s.toString());
                // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });

        device_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

//    private void uploadImage(String filepath) {
//        File file = new File(filepath);
//
//        Retrofit retrofit = NetworkClient.getRetrofit();
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
//        MultipartBody.Part parts = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
//
//        RequestBody somedata = RequestBody.create(MediaType.parse("text/plain"),"this is new image");
//        RequestBody cache = RequestBody.create(MediaType.parse("text/plain"),"true");
//        FileUploadService fileUploadService = retrofit.create(FileUploadService.class);
//        Call call = fileUploadService.uplpadImage(parts ,somedata,cache);
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//
//                Log.e("TAG", "onResponse: "+response );
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                Log.e("TAG", "onFailure: "+t.getMessage() );
//            }
//        });
//    }

    public void moveToSecondary() {
        // use an intent to travel from one activity to another.
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void getvendorId(String policiesNo) {

        VendorID = "";
        TextView vendorName = (TextView) findViewById(R.id.vendorName);
        vendorName.setText("Vendor Name : ");
        TextView vendorShopName = (TextView) findViewById(R.id.vendorShopName);
        vendorShopName.setText("Shop Name : ");
//        TextView vendorShopCode = (TextView) findViewById(R.id.vendorShopCode);
//        vendorShopCode.setText("Shop Code : ");
        TextView vendorContact = (TextView) findViewById(R.id.vendorContact);
        vendorContact.setText("Vendor Contact : ");
//        TextView vendorEmail = (TextView) findViewById(R.id.vendorEmail);
//        vendorEmail.setText("Email Id : ");
//        TextView vendorAddress = (TextView) findViewById(R.id.vendorAddress);
//        vendorAddress.setText("Shop Address : ");

        db.collection("policy").whereEqualTo("policyNo", policiesNo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // toastMessage("Success found");
                            Log.d("Success", "Suucfull ofund ==========================");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // toastMessage(document.getData().toString());
                                Log.d("2Success", "Suucfull ofund ==========================");
                                if (!document.getData().containsKey("customerUid")
                                        && document.getData().containsKey("vendorID")) {

                                    String vendorID = document.getData().get("vendorID").toString();
                                    VendorID = vendorID;
                                    // toastMessage("Succ" + vendorId);
                                    db.collection("vendors").document(vendorID)
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot value,
                                                                    @Nullable FirebaseFirestoreException error) {
                                                    if (error != null) {
                                                        // this method is called when error is not null
                                                        // and we gt any error
                                                        // in this cas we are displaying an error message.
                                                        toastMessage("Error found" + error);
                                                        return;
                                                    }
                                                    // Log.d("-------->",VendorID +"===="+ value.toString() );
                                                    if (value != null && value.exists()) {
                                                        if (value.getId().equals(VendorID)) {
                                                            // toastMessage(value.getData().toString());
                                                            TextView vendorName = (TextView) findViewById(
                                                                    R.id.vendorName);
                                                            vendorName.setText("Vendor Name : "
                                                                    + value.getData().get("contactperson").toString());
                                                            vendorName.setTypeface(vendorName.getTypeface(), Typeface.BOLD);

                                                            TextView vendorShopName = (TextView) findViewById(
                                                                    R.id.vendorShopName);
                                                            vendorShopName.setText("Shop Name : "
                                                                    + value.getData().get("shopname").toString());
                                                            vendorShopName.setTypeface(vendorShopName.getTypeface(), Typeface.BOLD);
                                                            TextView vendorContact = (TextView) findViewById(
                                                                    R.id.vendorContact);
                                                            vendorContact.setText("Vendor Contact : "
                                                                    + value.getData().get("contact").toString());
                                                            vendorContact.setTypeface(vendorContact.getTypeface(), Typeface.BOLD);
                                                            // editor.commit();
                                                            TextView email = findViewById(R.id.customerMail);
                                                            email.setText(
                                                                    policiesNo.toLowerCase(Locale.ROOT) + "@gmail.com");
                                                            // Log.d("-------->1",VendorID +"===="+ value.toString() );

                                                        }
                                                    }

                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d("Policy", "Policy not found");
                            toastMessage("policy not found");

                        }
                    }
                });
    }

    private void getPolicyIdList(Context context) {
        final String cust_policy = MainActivity.getDeviceId(context);
        db.collection("policy").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        list.add(document.getId());
                        policyDocID.add(document.getId());
                        // Log.d("this id are", String.valueOf(document.getData()));
                        //
                        // toastMessage(document.getData()));
                    }
                    // toastMessage(policyDocID.toString());
                    Log.d("List are", policyDocID.toString());
                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void toastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // String text = parent.getItemAtPosition(position).toString();
//        String Brand = spinner.getSelectedItem().toString();
        // String paymentMode = .getSelectedItem().toString();
        String loan = spinner2.getSelectedItem().toString();
        // Toast.makeText(parent.getContext(),Brand, Toast.LENGTH_SHORT).show();
//        customer_mobile_brand = Brand;
        // customer_payment = paymentMode;
        customer_loan = loan;

        plan = spinnerPlan.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void registerNewUser() {
        // show the visibility of progress bar to show loading
        // progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = customer_emailEdit.getText().toString();
        password = customer_contactEdit.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            customer_uid = MainActivity.getDeviceId(getApplicationContext());
                            amount = device_amount.getText().toString();
                            addDataToFirestore(customer_uid, customer_name, customer_contact, customer_email,
                                    customer_mobile_brand, customer_payment, customer_loan, VendorID, PolicyNo,
                                    startDate, endDate, amount, plan, downpayment, emi_tenure, photo );
                        } else {
                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Authentication failed!!" + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();
                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void addDataToFirestore(String customer_uid, String customer_name, String customer_contact,
                                    String customer_email, String customer_mobile_brand, String customer_payment, String customer_loan,
                                    String vendorId, String policyNo, String startDate, String endDate, String amount, String anti_theft_plan, String downpayment, String emi_tenure, String photo) {

        db.collection("policy").whereEqualTo("policyNo", policy_no).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                db.collection("policy").document(document.getId()).update("customerUid", customer_uid)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                CollectionReference dbRegister = db.collection("users");
                                                // adding our data to our courses object class.
                                                RegistrationDetails registration = new RegistrationDetails(customer_uid,
                                                        customer_name, customer_contact, customer_email,
                                                        customer_mobile_brand, customer_payment, customer_loan,
                                                        startDate, endDate, amount, anti_theft_plan, vendorId,downpayment,emi_tenure,photo);
                                                // below method is use to add data to Firebase Firestore.
                                                dbRegister.document(customer_uid).set(registration)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void avoid) {
                                                                progressbar.setVisibility(View.GONE);
                                                                toastMessage("Registration is done successfully");
                                                                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                                mainActivityIntent.putExtra("minimize",1);
                                                                startActivity(mainActivityIntent);
                                                                finish();

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // this method is called when the data addition process
                                                                // is failed.
                                                                // displaying a toast message when data addition is
                                                                // failed.
                                                                toastMessage(
                                                                        "Failed to register details Please try after some time \n"
                                                                                + e);
                                                            }
                                                        });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                toastMessage("Please check the vendor details and policy");
                                            }
                                        });
                            }
                        } else {
                            toastMessage("Failed to register details Please try after some time \n");
                        }
                    }
                });
    }

    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void endDate(View view) {
        showDialog(988);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        } else if (id == 988) {
            return new DatePickerDialog(this,
                    endDateListener, year, month, day);
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };
    private final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showEndDate(arg1, arg2 + 1, arg3);
        }
    };

    @SuppressLint("SetTextI18n")
    private void showDate(int year, int month, int day) {
        dateView.setText("Start Date : " + day + "/" +
                month + "/" + year);
        startDate = day + "/" + month + "/" + year;
    }

    @SuppressLint("SetTextI18n")
    private void showEndDate(int year, int month, int day) {
        endDateView.setText("End Date : " + day + "/" +
                month + "/" + year);
        endDate = day + "/" +
                month + "/" + year;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private boolean CheckAllFields() {
        if (customer_nameEdit.length() == 0) {
            customer_nameEdit.setError("Required");
            return false;
        }
        if (customer_emailEdit.length() == 0) {
            customer_emailEdit.setError("Required");
            return false;
        }
        String email = customer_emailEdit.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!customer_emailEdit.getText().toString().matches(emailPattern)) {
            customer_emailEdit.setError("Enter Valid Email");
            return false;
        }

        if (customer_contactEdit.length() == 0) {
            customer_contactEdit.setError("Required");
            return false;
        } else if (customer_contactEdit.length() <= 9 || customer_contactEdit.length() >= 11) {
            customer_contactEdit.setError("Enter Valid Number");
            return false;
        }

        if (device_amount.length() == 0) {
            device_amount.setError("Required");
            return true;
        }
//        if (endDateView.getText().toString().contains("Select end date")) {
//            toastMessage("Set the end date");
//            return true;
//        }
        if (policyId.length() == 0) {
            policyId.setError("Enter Policy");
            return false;
        }

        return true;
    }

    private void writeData(String status) {
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            String data = status;
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readData() {
        try {
            FileInputStream fin = openFileInput(filename);
            int a;
            StringBuilder temp = new StringBuilder();
            while ((a = fin.read()) != -1) {
                temp.append((char) a);
            }

            // setting text from the file.
            String data = temp.toString();
            fin.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    private void uploadBitmap(final Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            Log.e("TAG", "onResponse: "+response.data );
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("apikey", "FAC2B68A-FD0B-4224-9D99-C3309E3D810E");
                params.put("no-cache", "true");
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("files", new DataPart("test1" + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case CAMERA_REQ_CODE:
                        Bitmap img = (Bitmap) (data.getExtras().get("data"));
                        img_profile.setImageBitmap(img);
                        uploadBitmap(img);
                     /*   File f = new File(getCacheDir(), filename);
                        try {
                            f.createNewFile();
                            Bitmap bitmap = img;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 *//*ignored for PNG*//*, bos);
                            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            uploadImage(f.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
*/
//Convert bitmap to byte array

                        break;
                }
                break;

        }
    }
}
